package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchResult;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service managing the authoritative lifecycle of active multiplayer match sessions.
 *
 * Implements Prompt 20 Sections 7, 9, 10, 11, 21, 25, 26, 32, 35-39:
 * - Creates match sessions with verified puzzle assignments.
 * - Coordinates synchronized countdown and server-issued start signals.
 * - Enforces server-authoritative dual-win solution validation and timing.
 * - Prevents duplicate result submissions (idempotent result tracking).
 * - Issues full authoritative snapshots upon participant reconnection.
 */
@Service
public class MatchSessionService {

    private static final Logger log = LoggerFactory.getLogger(MatchSessionService.class);

    private static final long MATCH_TTL_MS = 600_000L; // 10 minutes session TTL
    private static final long COUNTDOWN_DURATION_MS = 3_000L; // 3 seconds synchronized countdown

    private final MultiplayerPuzzlePool puzzlePool;
    private final ServerPuzzleValidator serverPuzzleValidator;
    private final MatchEventDispatcher eventDispatcher;
    private final CompetitiveService competitiveService;
    private final com.zynpath.backend.notification.service.NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, MatchSession> activeSessions = new ConcurrentHashMap<>();

    public MatchSessionService(
            MultiplayerPuzzlePool puzzlePool,
            ServerPuzzleValidator serverPuzzleValidator,
            @Lazy MatchEventDispatcher eventDispatcher,
            @Lazy CompetitiveService competitiveService,
            @Lazy com.zynpath.backend.notification.service.NotificationService notificationService
    ) {
        this.puzzlePool = puzzlePool;
        this.serverPuzzleValidator = serverPuzzleValidator;
        this.eventDispatcher = eventDispatcher;
        this.competitiveService = competitiveService;
        this.notificationService = notificationService;
    }

    /**
     * Initializes a new match session with an authoritative solver-verified puzzle.
     */
    public MatchSession createMatchSession(GameMode mode, String hostPlayerId) {
        return createMatchSession(mode, hostPlayerId, null);
    }

    /**
     * Initializes a new match session with an authoritative solver-verified puzzle,
     * avoiding an excluded puzzle ID when alternatives are available.
     * Implements Prompt 22 Section 40 & 41.
     */
    public MatchSession createMatchSession(GameMode mode, String hostPlayerId, String excludePuzzleId) {
        String matchId = "match_" + UUID.randomUUID().toString().substring(0, 12);
        long seed = System.currentTimeMillis();
        PuzzleAssignment puzzle = puzzlePool.selectPuzzleForModeExcluding(mode, seed, excludePuzzleId);

        MatchSession session = new MatchSession(
                matchId,
                mode,
                hostPlayerId,
                puzzle,
                System.currentTimeMillis(),
                MATCH_TTL_MS
        );

        activeSessions.put(matchId, session);
        log.info("Created match session: id={}, mode={}, host={}, excludePuzzleId={}", matchId, mode, hostPlayerId, excludePuzzleId);

        // Bounded ready window (20 seconds) - Prompt 21 Section 15
        scheduler.schedule(() -> {
            MatchSession s = activeSessions.get(matchId);
            if (s != null && s.getState() == MatchState.WAITING_FOR_PLAYERS) {
                log.info("Match {} timed out waiting for players to become ready", matchId);
                if (s.transitionTo(MatchState.CANCELLED)) {
                    s.setEndedAt(System.currentTimeMillis());
                    long seq = s.nextSequenceNumber();
                    eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                            MultiplayerEventType.MATCH_CANCELLED,
                            matchId,
                            seq,
                            Map.of("reason", "READY_TIMEOUT", "message", "A player failed to become ready in time")
                    ));
                }
            }
        }, 20, TimeUnit.SECONDS);

        return session;
    }

    public MatchSession getSession(String matchId) {
        MatchSession session = activeSessions.get(matchId);
        if (session == null) {
            throw new AuthException("MATCH_NOT_FOUND", "Match session not found: " + matchId, HttpStatus.NOT_FOUND);
        }
        return session;
    }

    /**
     * Records a player's ready state. When all participants are ready, triggers countdown.
     */
    public void markPlayerReady(String matchId, String playerId) {
        MatchSession session = getSession(matchId);
        MatchParticipant participant = session.getParticipant(playerId);
        if (participant == null) {
            throw new AuthException("PLAYER_NOT_IN_MATCH", "Player does not belong to this match", HttpStatus.FORBIDDEN);
        }

        participant.setReady(true);
        long seq = session.nextSequenceNumber();
        eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.PLAYER_READY,
                matchId,
                seq,
                Map.of("playerId", playerId, "publicZynpathId", participant.getPublicZynpathId())
        ));

        // Check if all players are ready
        synchronized (session) {
            if (session.areAllParticipantsReady() && session.getState() == MatchState.WAITING_FOR_PLAYERS) {
                if (session.getGameMode() != GameMode.MINI_LEAGUE) {
                    if (session.transitionTo(MatchState.READY)) {
                        startCountdown(session);
                    }
                }
            }
        }
    }

    /**
     * Synchronizes and broadcasts match countdown, followed by match start.
     */
    public void startCountdown(MatchSession session) {
        String matchId = session.getMatchId();
        if (!session.transitionTo(MatchState.COUNTDOWN)) {
            return;
        }

        long countdownStart = System.currentTimeMillis();
        session.setCountdownStartedAt(countdownStart);

        long seq = session.nextSequenceNumber();
        eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.MATCH_COUNTDOWN,
                matchId,
                seq,
                Map.of(
                        "countdownDurationMs", COUNTDOWN_DURATION_MS,
                        "countdownStartedAt", countdownStart
                )
        ));

        scheduler.schedule(() -> {
            synchronized (session) {
                if (session.getState() == MatchState.COUNTDOWN && session.transitionTo(MatchState.ACTIVE)) {
                    long activeStartTime = System.currentTimeMillis();
                    session.setStartedAt(activeStartTime);

                    long startSeq = session.nextSequenceNumber();
                    eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                            MultiplayerEventType.MATCH_STARTED,
                            matchId,
                            startSeq,
                            Map.of(
                                    "startedAt", activeStartTime,
                                    "puzzleId", session.getPuzzleAssignment().puzzleId(),
                                    "puzzleFingerprint", session.getPuzzleAssignment().fingerprint()
                            )
                    ));
                    log.info("Match started: id={}, mode={}", matchId, session.getGameMode());
                }
            }
        }, COUNTDOWN_DURATION_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Broadcasts lightweight, provisional participant progress (covered cells and checkpoints).
     * Does NOT transmit finger gestures or save to permanent DB.
     */
    public void updateProgress(String matchId, String playerId, int coveredCells, int lastCheckpoint) {
        MatchSession session = getSession(matchId);
        if (!session.getState().isPlayable()) {
            return;
        }

        MatchParticipant participant = session.getParticipant(playerId);
        if (participant != null && participant.getCompletedAt() == null) {
            participant.updateProgress(coveredCells, lastCheckpoint);

            long seq = session.nextSequenceNumber();
            eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.PLAYER_PROGRESS,
                    matchId,
                    seq,
                    Map.of(
                            "playerId", playerId,
                            "publicZynpathId", participant.getPublicZynpathId(),
                            "coveredCells", coveredCells,
                            "lastCheckpoint", lastCheckpoint
                    )
            ));
        }
    }

    /**
     * Submits and validates a competitive solution claim authoritatively.
     */
    public ValidationOutcome submitCompletionClaim(
            String matchId,
            String playerId,
            MultiplayerDto.SubmitSolutionClaimRequest request
    ) {
        MatchSession session = getSession(matchId);

        // 1. Verify match state
        if (!session.getState().isPlayable()) {
            return ValidationOutcome.failure("INVALID_MATCH_STATE: Match is in state " + session.getState());
        }

        // 2. Verify participant
        MatchParticipant participant = session.getParticipant(playerId);
        if (participant == null) {
            return ValidationOutcome.failure("NOT_A_PARTICIPANT: Player does not belong to this match");
        }

        // 3. Idempotency: if player already completed, return cached victory/result
        if (participant.getCompletedAt() != null) {
            return ValidationOutcome.success();
        }

        // 4. Validate solution authoritatively
        ValidationOutcome outcome = serverPuzzleValidator.validateSolution(
                session.getPuzzleAssignment(),
                request.pathCoordinates()
        );

        if (!outcome.valid()) {
            log.warn("Rejected invalid solution claim for match={}, player={}: {}", matchId, playerId, outcome.rejectionReason());
            return outcome;
        }

        // 5. Authoritative timing & finish order
        long now = System.currentTimeMillis();
        long solveTimeMs = (session.getStartedAt() != null)
                ? (now - session.getStartedAt())
                : request.clientReportedSolveTimeMs();

        synchronized (session) {
            if (participant.getCompletedAt() != null) {
                return ValidationOutcome.success();
            }

            int finishOrder = session.getNextFinishOrder();
            boolean isWinner = (finishOrder == 1);
            participant.setCompleted(now, solveTimeMs, isWinner, finishOrder);

            String outcomeStatus;
            if (session.getGameMode() == GameMode.MINI_LEAGUE) {
                outcomeStatus = switch (finishOrder) {
                    case 1 -> "1st Place";
                    case 2 -> "2nd Place";
                    case 3 -> "3rd Place";
                    default -> finishOrder + "th Place";
                };
            } else {
                outcomeStatus = isWinner ? "VICTORY" : "COMPLETED";
            }

            MatchResult result = new MatchResult(
                    matchId,
                    playerId,
                    participant.getPublicZynpathId(),
                    participant.getDisplayName(),
                    true,
                    solveTimeMs,
                    finishOrder,
                    isWinner,
                    outcomeStatus
            );
            session.addResult(result);

            // Broadcast completion
            long seq = session.nextSequenceNumber();
            eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.PLAYER_COMPLETED,
                    matchId,
                    seq,
                    Map.of(
                            "playerId", playerId,
                            "publicZynpathId", participant.getPublicZynpathId(),
                            "solveTimeMs", solveTimeMs,
                            "finishOrder", finishOrder,
                            "isWinner", isWinner
                    )
            ));

            // Check if match should conclude
            if (session.getGameMode() == GameMode.QUICK_DUEL || session.getGameMode() == GameMode.FRIEND_DUEL) {
                // Duel completes on first solver: assign defeat result to opponent
                for (MatchParticipant other : session.getParticipants().values()) {
                    if (!other.getPlayerId().equals(playerId) && other.getCompletedAt() == null) {
                        session.addResult(new MatchResult(
                                matchId,
                                other.getPlayerId(),
                                other.getPublicZynpathId(),
                                other.getDisplayName(),
                                false,
                                solveTimeMs,
                                2,
                                false,
                                "DEFEAT"
                        ));
                    }
                }
                concludeMatch(session);
            } else {
                // Mini league: transition to COMPLETING on first finish, schedule bounded 45s window
                if (finishOrder == 1) {
                    session.transitionTo(MatchState.COMPLETING);
                    scheduler.schedule(() -> {
                        synchronized (session) {
                            if (session.getState() == MatchState.COMPLETING || session.getState() == MatchState.ACTIVE) {
                                concludeMiniLeague(session);
                            }
                        }
                    }, 45, TimeUnit.SECONDS);
                }

                // If all participants finished or forfeited, conclude immediately
                boolean allDone = true;
                for (MatchParticipant p : session.getParticipants().values()) {
                    if (p.getCompletedAt() == null && !p.isForfeited()) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    concludeMiniLeague(session);
                }
            }
        }

        return outcome;
    }

    public void concludeMiniLeague(MatchSession session) {
        synchronized (session) {
            if (session.getState().isTerminal()) {
                return;
            }
            String matchId = session.getMatchId();
            for (MatchParticipant p : session.getParticipants().values()) {
                if (p.getCompletedAt() == null) {
                    int order = session.getNextFinishOrder();
                    p.setFinishOrder(order);
                    session.addResult(new MatchResult(
                            matchId,
                            p.getPlayerId(),
                            p.getPublicZynpathId(),
                            p.getDisplayName(),
                            false,
                            null,
                            order,
                            false,
                            p.isForfeited() ? "FORFEIT" : "UNFINISHED"
                    ));
                }
            }
            concludeMatch(session);
        }
    }

    private void concludeMatch(MatchSession session) {
        if (session.transitionTo(MatchState.COMPLETED)) {
            long now = System.currentTimeMillis();
            session.setEndedAt(now);

            long seq = session.nextSequenceNumber();
            eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.MATCH_COMPLETED,
                    session.getMatchId(),
                    seq,
                    Map.of(
                            "matchId", session.getMatchId(),
                            "endedAt", now,
                            "results", session.getResults()
                    )
            ));
            log.info("Match completed: id={}, resultsCount={}", session.getMatchId(), session.getResults().size());
            try {
                competitiveService.recordFinalizedMatch(session);
            } catch (Exception e) {
                log.error("Failed to record finalized match in competitive history: matchId={}", session.getMatchId(), e);
            }

            try {
                for (String participantId : session.getParticipants().keySet()) {
                    notificationService.createNotification(
                            participantId,
                            com.zynpath.backend.notification.model.NotificationEventType.MATCH_RESULT,
                            "Match Concluded",
                            "Your " + session.getGameMode().name() + " match has ended. Tap to view finalized results.",
                            session.getMatchId(),
                            "match_details/" + session.getMatchId(),
                            null
                    );
                }
            } catch (Exception e) {
                log.warn("Failed to dispatch match result notifications: {}", e.getMessage());
            }
        }
    }

    /**
     * Handles explicit abandonment/forfeit during an active match (Prompt 21 Sections 35 & 36).
     */
    public void forfeitMatch(String matchId, String playerId) {
        MatchSession session = getSession(matchId);
        if (!session.hasParticipant(playerId)) {
            throw new AuthException("NOT_A_PARTICIPANT", "Player does not belong to this match", HttpStatus.FORBIDDEN);
        }

        synchronized (session) {
            if (session.getState().isTerminal()) {
                return;
            }

            MatchParticipant forfeiter = session.getParticipant(playerId);
            if (forfeiter != null) {
                forfeiter.setForfeited(true);
                forfeiter.setConnected(false);
            }

            long now = System.currentTimeMillis();
            long solveTime = session.getStartedAt() != null ? (now - session.getStartedAt()) : 0L;

            session.addResult(new MatchResult(
                    matchId,
                    playerId,
                    forfeiter != null ? forfeiter.getPublicZynpathId() : "",
                    forfeiter != null ? forfeiter.getDisplayName() : "Player",
                    false,
                    solveTime,
                    2,
                    false,
                    "ABANDONED"
            ));

            if (session.getGameMode() == GameMode.QUICK_DUEL || session.getGameMode() == GameMode.FRIEND_DUEL) {
                for (MatchParticipant other : session.getParticipants().values()) {
                    if (!other.getPlayerId().equals(playerId)) {
                        other.setWinner(true);
                        other.setFinishOrder(1);
                        session.addResult(new MatchResult(
                                matchId,
                                other.getPlayerId(),
                                other.getPublicZynpathId(),
                                other.getDisplayName(),
                                true,
                                solveTime,
                                1,
                                true,
                                "VICTORY_BY_FORFEIT"
                        ));
                    }
                }
                concludeMatch(session);
            } else {
                // Mini league: check if all active players are done
                boolean allDone = true;
                for (MatchParticipant p : session.getParticipants().values()) {
                    if (p.getCompletedAt() == null && !p.isForfeited()) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    concludeMiniLeague(session);
                }
            }
        }
    }

    /**
     * Issues an authoritative snapshot for a reconnecting participant.
     */
    public ReconnectionSnapshot reconnectPlayer(String matchId, String playerId) {
        MatchSession session = getSession(matchId);
        MatchParticipant participant = session.getParticipant(playerId);
        if (participant == null) {
            throw new AuthException("NOT_A_PARTICIPANT", "Player does not belong to this match", HttpStatus.FORBIDDEN);
        }

        participant.setConnected(true);
        ReconnectionSnapshot snapshot = session.toSnapshot();

        long seq = session.nextSequenceNumber();
        eventDispatcher.dispatchToPlayer(playerId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.RECONNECT_STATE,
                matchId,
                seq,
                snapshot
        ));

        // Notify opponents of rejoin
        eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.PLAYER_JOINED,
                matchId,
                seq,
                Map.of("playerId", playerId, "publicZynpathId", participant.getPublicZynpathId())
        ));

        return snapshot;
    }

    public void handlePlayerDisconnect(String matchId, String playerId) {
        MatchSession session = activeSessions.get(matchId);
        if (session == null) return;

        MatchParticipant participant = session.getParticipant(playerId);
        if (participant != null) {
            participant.setConnected(false);

            long seq = session.nextSequenceNumber();
            eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.PLAYER_LEFT,
                    matchId,
                    seq,
                    Map.of("playerId", playerId, "publicZynpathId", participant.getPublicZynpathId())
            ));
        }
    }

    public void cancelMatch(String matchId, String actingPlayerId) {
        MatchSession session = getSession(matchId);
        if (!session.hasParticipant(actingPlayerId)) {
            throw new AuthException("NOT_AUTHORIZED", "Only a participant can cancel the match", HttpStatus.FORBIDDEN);
        }

        if (session.transitionTo(MatchState.CANCELLED)) {
            session.setEndedAt(System.currentTimeMillis());
            long seq = session.nextSequenceNumber();
            eventDispatcher.dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.MATCH_CANCELLED,
                    matchId,
                    seq,
                    Map.of("matchId", matchId, "cancelledBy", actingPlayerId)
            ));
        }
    }
}
