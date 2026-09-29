package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.model.FriendsArenaMember;
import com.zynpath.backend.multiplayer.model.FriendsArenaRoom;
import com.zynpath.backend.multiplayer.model.FriendsArenaRoomStatus;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zynpath.backend.multiplayer.model.FriendsArenaMatch;
import com.zynpath.backend.multiplayer.model.FriendsArenaMatchParticipant;
import com.zynpath.backend.multiplayer.model.FriendsArenaMatchStatus;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchResult;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Authoritative service managing Friends Arena 1-5 player rooms, secure code generation,
 * atomic membership synchronization, host departure policy, and match initiation.
 *
 * Implements Prompt 18 Tasks 2-7:
 * - Authenticated room creation with cryptographic 6-char room code.
 * - Maximum 5-player capacity strictly enforced server-side.
 * - Concurrent joins handled atomically (prevents 6-player race condition).
 * - Idempotent re-join for already joined members.
 * - Deterministic host-departure policy: promotes next joined member or closes empty rooms.
 * - Server-authoritative start-match command with 6 eligibility rules.
 * - Room-scoped real-time event dispatching.
 */
@Service
public class FriendsArenaService {

    private static final Logger log = LoggerFactory.getLogger(FriendsArenaService.class);

    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 32 chars, unambiguous (no 0/O, 1/I)
    private static final int CODE_LENGTH = 6;
    private static final long ROOM_TTL_MS = 30 * 60 * 1000L; // 30 minutes room TTL
    private static final long CREATION_DEBOUNCE_MS = 3000L; // 3-second rapid-tap debounce

    private static final long COUNTDOWN_DURATION_MS = 3000L; // 3-second synchronized countdown
    private static final long COMPLETING_TIMEOUT_MS = 45000L; // 45-second finish window after first solver

    private final PlayerAccountService playerAccountService;
    private final MatchEventDispatcher eventDispatcher;
    private final MultiplayerPuzzlePool puzzlePool;
    private final ServerPuzzleValidator serverPuzzleValidator;
    private final SecureRandom secureRandom = new SecureRandom();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Primary room registries
    private final Map<String, FriendsArenaRoom> roomsById = new ConcurrentHashMap<>();
    private final Map<String, FriendsArenaRoom> roomsByCode = new ConcurrentHashMap<>();
    // Host debounce tracking: hostPlayerId -> last created room
    private final Map<String, FriendsArenaRoom> lastCreatedRoomByHost = new ConcurrentHashMap<>();
    // Active matches registry: matchId -> FriendsArenaMatch
    private final Map<String, FriendsArenaMatch> matchesById = new ConcurrentHashMap<>();

    public FriendsArenaService(
            PlayerAccountService playerAccountService,
            @Lazy MatchEventDispatcher eventDispatcher,
            @Lazy MultiplayerPuzzlePool puzzlePool,
            @Lazy ServerPuzzleValidator serverPuzzleValidator
    ) {
        this.playerAccountService = playerAccountService;
        this.eventDispatcher = eventDispatcher;
        this.puzzlePool = puzzlePool;
        this.serverPuzzleValidator = serverPuzzleValidator;
    }

    /**
     * Authenticated room creation. Host is derived strictly from verified principal.
     */
    public MultiplayerDto.FriendsArenaRoomDto createRoom(String hostPlayerId, String idempotencyKey) {
        PlayerAccount hostAccount = playerAccountService.findById(hostPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Host player account not found", HttpStatus.NOT_FOUND));

        // Rapid tap debounce check: if host already created a room in WAITING state within debounce window, return it
        FriendsArenaRoom existingRoom = lastCreatedRoomByHost.get(hostPlayerId);
        long now = System.currentTimeMillis();
        if (existingRoom != null && existingRoom.getStatus() == FriendsArenaRoomStatus.WAITING) {
            if (now - existingRoom.getCreatedAt() < CREATION_DEBOUNCE_MS) {
                log.info("Returning debounced active room {} for host {}", existingRoom.getRoomId(), hostPlayerId);
                return existingRoom.toDto();
            }
        }

        String roomId = "faroom_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String roomCode = generateUniqueRoomCode();

        FriendsArenaRoom room = new FriendsArenaRoom(roomId, roomCode, hostPlayerId, now);

        // Host is the first member (Slot 1)
        FriendsArenaMember hostMember = new FriendsArenaMember(
                hostAccount.playerId(),
                hostAccount.publicZynpathId(),
                hostAccount.displayName(),
                "avatar_compass",
                true, // isHost
                true, // isReady
                now
        );
        room.addMember(hostMember);

        roomsById.put(roomId, room);
        roomsByCode.put(roomCode, room);
        lastCreatedRoomByHost.put(hostPlayerId, room);

        log.info("Created Friends Arena room: id={}, code={}, host={}", roomId, roomCode, hostPlayerId);

        MultiplayerDto.FriendsArenaRoomDto snapshot = room.toDto();
        eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIENDS_ARENA_ROOM_CREATED,
                roomId,
                snapshot.version(),
                Map.of("room", snapshot)
        ));

        return snapshot;
    }

    /**
     * Retrieves authoritative room state by internal room ID.
     */
    public MultiplayerDto.FriendsArenaRoomDto getRoom(String roomId, String requesterPlayerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Friends Arena room not found", HttpStatus.NOT_FOUND);
        }
        return room.toDto();
    }

    /**
     * Retrieves authoritative room state by public room code.
     */
    public MultiplayerDto.FriendsArenaRoomDto getRoomByCode(String roomCode, String requesterPlayerId) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new AuthException("INVALID_CODE", "Room code is required", HttpStatus.BAD_REQUEST);
        }
        String cleanCode = roomCode.trim().toUpperCase();
        FriendsArenaRoom room = roomsByCode.get(cleanCode);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found with code: " + roomCode, HttpStatus.NOT_FOUND);
        }
        return room.toDto();
    }

    /**
     * Joins an existing room by code with atomic capacity enforcement (max 5 players).
     * Prevents race conditions and duplicate membership slots.
     */
    public MultiplayerDto.FriendsArenaRoomDto joinRoomByCode(String roomCode, String playerId) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new AuthException("INVALID_CODE", "Room code is required", HttpStatus.BAD_REQUEST);
        }
        String cleanCode = roomCode.trim().toUpperCase();
        if (cleanCode.length() < 4 || cleanCode.length() > 8) {
            throw new AuthException("INVALID_CODE", "Room code format is invalid (expected 6 characters)", HttpStatus.BAD_REQUEST);
        }

        FriendsArenaRoom room = roomsByCode.get(cleanCode);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found with code: " + roomCode, HttpStatus.NOT_FOUND);
        }

        PlayerAccount player = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        MultiplayerDto.FriendsArenaRoomDto snapshot;
        boolean newlyJoined = false;

        // Atomically synchronize join operations on the specific room instance
        synchronized (room) {
            if (room.getStatus() != FriendsArenaRoomStatus.WAITING) {
                throw new AuthException("ROOM_CLOSED", "Room is not accepting new players (state=" + room.getStatus() + ")", HttpStatus.BAD_REQUEST);
            }

            // If player is already a member, return the existing state rather than adding duplicate slot
            if (room.hasMember(playerId)) {
                log.info("Player {} already in room {}, returning existing membership snapshot", playerId, room.getRoomId());
                return room.toDto();
            }

            // Strictly enforce maximum capacity of 5 players
            if (room.getMemberCount() >= FriendsArenaRoom.MAX_CAPACITY) {
                throw new AuthException("ROOM_FULL", "Room has reached maximum capacity of 5 players", HttpStatus.BAD_REQUEST);
            }

            long now = System.currentTimeMillis();
            FriendsArenaMember newMember = new FriendsArenaMember(
                    player.playerId(),
                    player.publicZynpathId(),
                    player.displayName(),
                    "avatar_compass",
                    false, // isHost
                    false, // isReady
                    now
            );
            room.addMember(newMember);
            room.incrementVersion();
            snapshot = room.toDto();
            newlyJoined = true;
        }

        if (newlyJoined) {
            log.info("Player {} joined Friends Arena room {} (occupancy: {}/5)", playerId, room.getRoomId(), snapshot.currentOccupancy());
            eventDispatcher.dispatchToFriendsArenaRoom(room.getRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_JOINED,
                    room.getRoomId(),
                    snapshot.version(),
                    Map.of("playerId", playerId, "room", snapshot)
            ));
            eventDispatcher.dispatchToFriendsArenaRoom(room.getRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_MEMBERSHIP_CHANGED,
                    room.getRoomId(),
                    snapshot.version(),
                    Map.of("room", snapshot)
            ));
        }

        return snapshot;
    }

    /**
     * Removes player from room with deterministic host-departure policy.
     * If the host departs and members remain, promotes the next earliest joined member as host.
     * If all members leave, closes and cleans up the room.
     */
    public void leaveRoom(String roomId, String playerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) return;

        MultiplayerDto.FriendsArenaRoomDto snapshot;
        boolean roomClosed = false;
        String newHostId = null;

        synchronized (room) {
            if (!room.hasMember(playerId)) {
                return;
            }

            room.removeMember(playerId);
            room.incrementVersion();

            if (room.getMemberCount() == 0) {
                room.setStatus(FriendsArenaRoomStatus.CLOSED);
                roomsById.remove(roomId);
                roomsByCode.remove(room.getRoomCode());
                lastCreatedRoomByHost.remove(playerId);
                roomClosed = true;
                log.info("Friends Arena room {} closed (all players departed)", roomId);
            } else if (playerId.equals(room.getHostPlayerId())) {
                // Host left: Promote next member in join order (earliest joinedAt)
                FriendsArenaMember nextHost = room.getMembers().stream()
                        .min(Comparator.comparingLong(FriendsArenaMember::joinedAt))
                        .orElse(null);
                if (nextHost != null) {
                    nextHost.setHost(true);
                    room.setHostPlayerId(nextHost.playerId());
                    newHostId = nextHost.playerId();
                    log.info("Friends Arena room {} host departed: Promoted player {} as new host", roomId, newHostId);
                }
            }
            snapshot = room.toDto();
        }

        if (roomClosed) {
            eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_ROOM_CLOSED,
                    roomId,
                    snapshot.version(),
                    Map.of("roomId", roomId, "reason", "ALL_PLAYERS_LEFT")
            ));
        } else {
            eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_LEFT,
                    roomId,
                    snapshot.version(),
                    Map.of("playerId", playerId, "room", snapshot)
            ));
            if (newHostId != null) {
                eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                        MultiplayerEventType.FRIENDS_ARENA_HOST_CHANGED,
                        roomId,
                        snapshot.version(),
                        Map.of("newHostPlayerId", newHostId, "room", snapshot)
                ));
            }
            eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_MEMBERSHIP_CHANGED,
                    roomId,
                    snapshot.version(),
                    Map.of("room", snapshot)
            ));
        }
    }

    /**
     * Host start-match command with all 6 eligibility checks:
     * 1. Requester is actual host.
     * 2. Room exists and is in startable state.
     * 3. At least 2 distinct members.
     * 4. No more than 5 members.
     * 5. Readiness requirements satisfied.
     * 6. Room has not already started (repeated requests safe).
     */
    public MultiplayerDto.FriendsArenaRoomDto startMatch(String roomId, String requesterPlayerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Friends Arena room not found", HttpStatus.NOT_FOUND);
        }

        MultiplayerDto.FriendsArenaRoomDto snapshot;
        FriendsArenaMatch match;

        synchronized (room) {
            // Check 1: Requester is actual host
            if (!requesterPlayerId.equals(room.getHostPlayerId())) {
                throw new AuthException("NOT_HOST", "Only the room host can initiate match start", HttpStatus.FORBIDDEN);
            }

            // Check 6 (repeated start requests safe): if already STARTING or IN_GAME, return current state
            if (room.getStatus() == FriendsArenaRoomStatus.STARTING || room.getStatus() == FriendsArenaRoomStatus.IN_GAME) {
                return room.toDto();
            }

            // Check 2: Room is in startable state
            if (room.getStatus() != FriendsArenaRoomStatus.WAITING) {
                throw new AuthException("INVALID_STATE", "Room cannot be started from status: " + room.getStatus(), HttpStatus.BAD_REQUEST);
            }

            // Check 3: At least 2 distinct members
            if (room.getMemberCount() < FriendsArenaRoom.MIN_PLAYERS_TO_START) {
                throw new AuthException("INSUFFICIENT_PLAYERS", "Friends Arena match requires at least 2 connected players", HttpStatus.BAD_REQUEST);
            }

            // Check 4: At most 5 members
            if (room.getMemberCount() > FriendsArenaRoom.MAX_CAPACITY) {
                throw new AuthException("CAPACITY_EXCEEDED", "Friends Arena supports a maximum of 5 players", HttpStatus.BAD_REQUEST);
            }

            long now = System.currentTimeMillis();
            String matchId = "famatch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            List<String> participantIds = room.getMembers().stream().map(FriendsArenaMember::playerId).toList();

            // Sourcing one authoritative solver-verified puzzle for all participants (Prompt 19 Task 4)
            PuzzleAssignment puzzle = puzzlePool.selectPuzzleForModeExcluding(GameMode.FRIENDS_ARENA, now, null);

            match = new FriendsArenaMatch(
                    matchId,
                    roomId,
                    requesterPlayerId,
                    participantIds,
                    puzzle,
                    now
            );

            for (FriendsArenaMember m : room.getMembers()) {
                match.addParticipant(new FriendsArenaMatchParticipant(
                        m.playerId(),
                        m.publicZynpathId(),
                        m.displayName(),
                        m.avatarId(),
                        m.isHost(),
                        m.joinedAt()
                ));
            }

            match.setCountdownStartedAt(now);
            matchesById.put(matchId, match);
            room.setActiveMatchId(matchId);

            // Transition to STARTING
            room.setStatus(FriendsArenaRoomStatus.STARTING);
            room.incrementVersion();
            snapshot = room.toDto();
        }

        log.info("Host {} initiated match start for Friends Arena room {} with {} players (matchId={})",
                requesterPlayerId, roomId, snapshot.currentOccupancy(), match.getMatchId());

        MultiplayerDto.FriendsArenaMatchDto matchDto = match.toDto();

        // Broadcast MATCH_STARTING event with shared puzzle and countdown duration
        eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIENDS_ARENA_MATCH_STARTING,
                roomId,
                snapshot.version(),
                Map.of(
                        "roomId", roomId,
                        "matchId", match.getMatchId(),
                        "countdownDurationMs", COUNTDOWN_DURATION_MS,
                        "countdownStartedAt", match.getCountdownStartedAt(),
                        "puzzleAssignment", match.getPuzzleAssignment(),
                        "match", matchDto,
                        "room", snapshot
                )
        ));

        // Schedule transition from COUNTDOWN to ACTIVE after 3000ms
        scheduler.schedule(() -> {
            synchronized (match) {
                if (match.getStatus() == FriendsArenaMatchStatus.COUNTDOWN) {
                    match.setStatus(FriendsArenaMatchStatus.ACTIVE);
                    long activeStart = System.currentTimeMillis();
                    match.setStartedAt(activeStart);
                    room.setStatus(FriendsArenaRoomStatus.IN_GAME);
                    match.incrementVersion();
                    log.info("Friends Arena match {} is now ACTIVE", match.getMatchId());

                    eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                            MultiplayerEventType.FRIENDS_ARENA_MATCH_STARTED,
                            roomId,
                            match.getVersion(),
                            Map.of(
                                    "matchId", match.getMatchId(),
                                    "roomId", roomId,
                                    "startedAt", activeStart,
                                    "match", match.toDto()
                            )
                    ));
                }
            }
        }, COUNTDOWN_DURATION_MS, TimeUnit.MILLISECONDS);

        return snapshot;
    }

    /**
     * Retrieves authoritative match state by match ID with participant authorization.
     */
    public MultiplayerDto.FriendsArenaMatchDto getMatch(String matchId, String playerId) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null) {
            throw new AuthException("MATCH_NOT_FOUND", "Match session not found: " + matchId, HttpStatus.NOT_FOUND);
        }
        if (!match.hasParticipant(playerId)) {
            throw new AuthException("NOT_A_PARTICIPANT", "You are not a participant in this match", HttpStatus.FORBIDDEN);
        }
        FriendsArenaMatchParticipant p = match.getParticipant(playerId);
        if (p != null && !p.isConnected()) {
            p.setConnected(true);
            long ver = match.incrementVersion();
            eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_RECONNECTED,
                    match.getSourceRoomId(),
                    ver,
                    Map.of("matchId", matchId, "playerId", playerId, "publicZynpathId", p.getPublicZynpathId())
            ));
        }
        return match.toDto();
    }

    /**
     * Retrieves authoritative match state by source room ID.
     */
    public MultiplayerDto.FriendsArenaMatchDto getMatchByRoom(String roomId, String playerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Friends Arena room not found", HttpStatus.NOT_FOUND);
        }
        String matchId = room.getActiveMatchId();
        if (matchId == null) {
            throw new AuthException("NO_ACTIVE_MATCH", "No active match found for room: " + roomId, HttpStatus.NOT_FOUND);
        }
        return getMatch(matchId, playerId);
    }

    /**
     * Broadcasts lightweight, provisional participant progress (covered cells and checkpoints).
     * Server associates progress strictly with authenticated session identity.
     */
    public void updatePlayerProgress(String matchId, String playerId, int coveredCells, int lastCheckpoint) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null || !match.isPlayable()) return;

        FriendsArenaMatchParticipant participant = match.getParticipant(playerId);
        if (participant != null && participant.getCompletedAt() == null) {
            participant.updateProgress(coveredCells, lastCheckpoint);
            long ver = match.incrementVersion();

            eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_PROGRESS,
                    match.getSourceRoomId(),
                    ver,
                    Map.of(
                            "matchId", matchId,
                            "playerId", playerId,
                            "publicZynpathId", participant.getPublicZynpathId(),
                            "coveredCells", coveredCells,
                            "lastCheckpoint", lastCheckpoint
                    )
            ));
        }
    }

    /**
     * Authoritatively validates and processes a player's solution claim (Prompt 19 Task 6).
     */
    public ValidationOutcome submitSolutionClaim(
            String matchId,
            String playerId,
            List<String> pathCoordinates,
            long clientReportedSolveTimeMs
    ) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null) {
            return ValidationOutcome.failure("MATCH_NOT_FOUND: Match session not found: " + matchId);
        }

        if (!match.isPlayable()) {
            return ValidationOutcome.failure("INVALID_MATCH_STATE: Match is in state " + match.getStatus());
        }

        FriendsArenaMatchParticipant participant = match.getParticipant(playerId);
        if (participant == null) {
            return ValidationOutcome.failure("NOT_A_PARTICIPANT: Player does not belong to this match");
        }

        if (participant.getCompletedAt() != null) {
            return ValidationOutcome.success();
        }

        // Authoritative server-side path validation against shared puzzle
        ValidationOutcome outcome = serverPuzzleValidator.validateSolution(
                match.getPuzzleAssignment(),
                pathCoordinates
        );

        if (!outcome.valid()) {
            log.warn("Rejected invalid solution claim for Friends Arena match={}, player={}: {}",
                    matchId, playerId, outcome.rejectionReason());
            return outcome;
        }

        long now = System.currentTimeMillis();
        long solveTimeMs = match.getStartedAt() != null
                ? (now - match.getStartedAt())
                : clientReportedSolveTimeMs;

        synchronized (match) {
            if (participant.getCompletedAt() != null) {
                return ValidationOutcome.success();
            }

            int finishOrder = match.getNextFinishOrder();
            boolean isWinner = (finishOrder == 1);
            participant.markCompleted(now, solveTimeMs, isWinner, finishOrder);

            String outcomeStatus = switch (finishOrder) {
                case 1 -> "1st Place";
                case 2 -> "2nd Place";
                case 3 -> "3rd Place";
                default -> finishOrder + "th Place";
            };

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
            match.addResult(result);
            long ver = match.incrementVersion();

            log.info("Player {} completed Friends Arena match {} in position #{} ({}ms)",
                    playerId, matchId, finishOrder, solveTimeMs);

            eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_COMPLETED,
                    match.getSourceRoomId(),
                    ver,
                    Map.of(
                            "matchId", matchId,
                            "playerId", playerId,
                            "publicZynpathId", participant.getPublicZynpathId(),
                            "solveTimeMs", solveTimeMs,
                            "finishOrder", finishOrder,
                            "isWinner", isWinner,
                            "match", match.toDto()
                    )
            ));

            if (finishOrder == 1) {
                match.setStatus(FriendsArenaMatchStatus.COMPLETING);
                scheduler.schedule(() -> concludeMatch(matchId), COMPLETING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            }

            boolean allFinished = match.getParticipants().values().stream()
                    .allMatch(p -> p.getCompletedAt() != null || !p.isConnected());
            if (allFinished) {
                concludeMatch(matchId);
            }

            return ValidationOutcome.success();
        }
    }

    /**
     * Forfeits an active match for a participant.
     */
    public void forfeitMatch(String matchId, String playerId) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null || !match.isPlayable()) return;

        FriendsArenaMatchParticipant participant = match.getParticipant(playerId);
        if (participant != null && participant.getCompletedAt() == null) {
            participant.setConnected(false);
            long ver = match.incrementVersion();
            eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_PLAYER_DISCONNECTED,
                    match.getSourceRoomId(),
                    ver,
                    Map.of("matchId", matchId, "playerId", playerId, "reason", "FORFEITED")
            ));
            boolean allDone = match.getParticipants().values().stream()
                    .allMatch(p -> p.getCompletedAt() != null || !p.isConnected());
            if (allDone) {
                concludeMatch(matchId);
            }
        }
    }

    /**
     * Concludes the match and broadcasts authoritative final standings.
     */
    public void concludeMatch(String matchId) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null) return;

        synchronized (match) {
            if (match.getStatus() == FriendsArenaMatchStatus.COMPLETED || match.getStatus() == FriendsArenaMatchStatus.CANCELLED) {
                return;
            }

            match.setStatus(FriendsArenaMatchStatus.COMPLETED);
            long endedAt = System.currentTimeMillis();
            match.setEndedAt(endedAt);

            int nextFinish = match.getNextFinishOrder();
            for (FriendsArenaMatchParticipant p : match.getParticipants().values()) {
                if (p.getCompletedAt() == null) {
                    MatchResult uncompleted = new MatchResult(
                            matchId,
                            p.getPlayerId(),
                            p.getPublicZynpathId(),
                            p.getDisplayName(),
                            false,
                            null,
                            nextFinish++,
                            false,
                            p.isConnected() ? "DNF" : "DISCONNECTED"
                    );
                    match.addResult(uncompleted);
                }
            }

            // Explicit tie detection for completed participants with identical verified solve times
            List<MatchResult> completedResults = match.getResults().stream().filter(MatchResult::completed).toList();
            for (int i = 0; i < completedResults.size(); i++) {
                for (int j = i + 1; j < completedResults.size(); j++) {
                    MatchResult r1 = completedResults.get(i);
                    MatchResult r2 = completedResults.get(j);
                    if (r1.solveTimeMs() != null && r1.solveTimeMs().equals(r2.solveTimeMs())) {
                        int tiedOrder = Math.min(r1.finishOrder(), r2.finishOrder());
                        boolean tiedWinner = r1.isWinner() || r2.isWinner();
                        match.getResults().remove(r1);
                        match.getResults().remove(r2);
                        match.addResult(new MatchResult(r1.matchId(), r1.playerId(), r1.publicZynpathId(), r1.displayName(), true, r1.solveTimeMs(), tiedOrder, tiedWinner, "TIED"));
                        match.addResult(new MatchResult(r2.matchId(), r2.playerId(), r2.publicZynpathId(), r2.displayName(), true, r2.solveTimeMs(), tiedOrder, tiedWinner, "TIED"));
                    }
                }
            }

            // Restore room to WAITING state so players can return to lobby or initiate a rematch
            FriendsArenaRoom room = roomsById.get(match.getSourceRoomId());
            if (room != null && (room.getStatus() == FriendsArenaRoomStatus.IN_GAME || room.getStatus() == FriendsArenaRoomStatus.STARTING)) {
                room.setStatus(FriendsArenaRoomStatus.WAITING);
                room.incrementVersion();
            }

            match.incrementVersion();
            log.info("Friends Arena match {} CONCLUDED with {} results", matchId, match.getResults().size());

            MultiplayerDto.FriendsArenaMatchDto matchDto = match.toDto();
            eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIENDS_ARENA_MATCH_COMPLETED,
                    match.getSourceRoomId(),
                    match.getVersion(),
                    Map.of(
                            "matchId", matchId,
                            "roomId", match.getSourceRoomId(),
                            "endedAt", endedAt,
                            "match", matchDto,
                            "results", matchDto.results()
                    )
            ));
        }
    }

    /**
     * Host initiates an authoritative rematch for the room following match completion.
     * Selects a fresh puzzle excluding the previous puzzle, creates a new match identity,
     * resets participant gameplay state, transitions room and match to COUNTDOWN,
     * and broadcasts start to all room participants.
     */
    public MultiplayerDto.FriendsArenaMatchDto requestRematch(String roomId, String requesterPlayerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Friends Arena room not found: " + roomId, HttpStatus.NOT_FOUND);
        }

        if (!requesterPlayerId.equals(room.getHostPlayerId())) {
            throw new AuthException("NOT_ROOM_HOST", "Only the room host can initiate a rematch", HttpStatus.FORBIDDEN);
        }

        if (room.getMemberCount() < FriendsArenaRoom.MIN_PLAYERS_TO_START) {
            throw new AuthException("INSUFFICIENT_PLAYERS", "Rematch requires at least 2 connected players", HttpStatus.BAD_REQUEST);
        }

        if (room.getMemberCount() > FriendsArenaRoom.MAX_CAPACITY) {
            throw new AuthException("CAPACITY_EXCEEDED", "Friends Arena supports a maximum of 5 players", HttpStatus.BAD_REQUEST);
        }

        String prevMatchId = room.getActiveMatchId();
        String prevPuzzleId = null;
        if (prevMatchId != null) {
            FriendsArenaMatch prevMatch = matchesById.get(prevMatchId);
            if (prevMatch != null) {
                if (prevMatch.getStatus() != FriendsArenaMatchStatus.COMPLETED && prevMatch.getStatus() != FriendsArenaMatchStatus.CANCELLED) {
                    throw new AuthException("MATCH_IN_PROGRESS", "Previous match has not concluded yet", HttpStatus.BAD_REQUEST);
                }
                if (prevMatch.getPuzzleAssignment() != null) {
                    prevPuzzleId = prevMatch.getPuzzleAssignment().puzzleId();
                }
            }
        }

        long now = System.currentTimeMillis();
        String newMatchId = "famatch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        List<String> participantIds = room.getMembers().stream().map(FriendsArenaMember::playerId).toList();

        // Select fresh puzzle excluding previous match's puzzle
        PuzzleAssignment puzzle = puzzlePool.selectPuzzleForModeExcluding(GameMode.FRIENDS_ARENA, now, prevPuzzleId);

        FriendsArenaMatch newMatch = new FriendsArenaMatch(
                newMatchId,
                roomId,
                requesterPlayerId,
                participantIds,
                puzzle,
                now
        );

        for (FriendsArenaMember m : room.getMembers()) {
            newMatch.addParticipant(new FriendsArenaMatchParticipant(
                    m.playerId(),
                    m.publicZynpathId(),
                    m.displayName(),
                    m.avatarId(),
                    m.isHost(),
                    m.joinedAt()
            ));
        }

        newMatch.setCountdownStartedAt(now);
        matchesById.put(newMatchId, newMatch);

        synchronized (room) {
            room.setActiveMatchId(newMatchId);
            room.setStatus(FriendsArenaRoomStatus.STARTING);
            room.incrementVersion();
        }

        log.info("Host {} initiated REMATCH for Friends Arena room {} (matchId={})",
                requesterPlayerId, roomId, newMatchId);

        MultiplayerDto.FriendsArenaMatchDto matchDto = newMatch.toDto();

        // Broadcast MATCH_STARTING to room topic
        eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIENDS_ARENA_MATCH_STARTING,
                roomId,
                room.getVersion(),
                Map.of(
                        "roomId", roomId,
                        "matchId", newMatchId,
                        "countdownDurationMs", COUNTDOWN_DURATION_MS,
                        "countdownStartedAt", now,
                        "puzzleAssignment", newMatch.getPuzzleAssignment(),
                        "match", matchDto,
                        "room", room.toDto()
                )
        ));

        // Schedule transition from COUNTDOWN to ACTIVE
        scheduler.schedule(() -> {
            synchronized (newMatch) {
                if (newMatch.getStatus() == FriendsArenaMatchStatus.COUNTDOWN) {
                    newMatch.setStatus(FriendsArenaMatchStatus.ACTIVE);
                    long activeStart = System.currentTimeMillis();
                    newMatch.setStartedAt(activeStart);
                    synchronized (room) {
                        room.setStatus(FriendsArenaRoomStatus.IN_GAME);
                        room.incrementVersion();
                    }
                    newMatch.incrementVersion();
                    log.info("Friends Arena rematch {} is now ACTIVE", newMatchId);

                    eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                            MultiplayerEventType.FRIENDS_ARENA_MATCH_STARTED,
                            roomId,
                            newMatch.getVersion(),
                            Map.of(
                                    "matchId", newMatchId,
                                    "roomId", roomId,
                                    "startedAt", activeStart,
                                    "match", newMatch.toDto()
                            )
                    ));
                }
            }
        }, COUNTDOWN_DURATION_MS, TimeUnit.MILLISECONDS);

        return matchDto;
    }

    /**
     * Non-host participant requests a rematch, broadcasting notification to the room host.
     */
    public void notifyRematchRequested(String roomId, String requesterPlayerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        if (room == null) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found: " + roomId, HttpStatus.NOT_FOUND);
        }
        if (!room.hasMember(requesterPlayerId)) {
            throw new AuthException("NOT_A_MEMBER", "You are not a member of this room", HttpStatus.FORBIDDEN);
        }

        FriendsArenaMember member = room.getMember(requesterPlayerId);
        String requesterName = member != null ? member.displayName() : "A player";

        eventDispatcher.dispatchToFriendsArenaRoom(roomId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.REMATCH_REQUESTED,
                roomId,
                room.getVersion(),
                Map.of(
                        "roomId", roomId,
                        "requesterPlayerId", requesterPlayerId,
                        "requesterDisplayName", requesterName
                )
        ));
    }

    public FriendsArenaRoom getRoomEntity(String roomId) {
        return roomsById.get(roomId);
    }

    public FriendsArenaMatch getMatchEntity(String matchId) {
        return matchesById.get(matchId);
    }

    public void handlePlayerDisconnect(String matchId, String playerId) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null) return;
        synchronized (match) {
            FriendsArenaMatchParticipant participant = match.getParticipant(playerId);
            if (participant != null && participant.isConnected()) {
                participant.setConnected(false);
                match.incrementVersion();
                eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.FRIENDS_ARENA_PLAYER_DISCONNECTED,
                        match.getSourceRoomId(),
                        match.getVersion(),
                        Map.of(
                                "matchId", matchId,
                                "roomId", match.getSourceRoomId(),
                                "playerId", playerId
                        )
                ));
            }
        }
    }

    public void handlePlayerReconnect(String matchId, String playerId) {
        FriendsArenaMatch match = matchesById.get(matchId);
        if (match == null) return;
        synchronized (match) {
            FriendsArenaMatchParticipant participant = match.getParticipant(playerId);
            if (participant != null && !participant.isConnected()) {
                participant.setConnected(true);
                match.incrementVersion();
                eventDispatcher.dispatchToFriendsArenaRoom(match.getSourceRoomId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.FRIENDS_ARENA_PLAYER_RECONNECTED,
                        match.getSourceRoomId(),
                        match.getVersion(),
                        Map.of(
                                "matchId", matchId,
                                "roomId", match.getSourceRoomId(),
                                "playerId", playerId
                        )
                ));
            }
        }
    }

    /**
     * Checks whether a player is an authorized member of a room.
     * Used for WebSocket subscription authorization.
     */
    public boolean isMember(String roomId, String playerId) {
        FriendsArenaRoom room = roomsById.get(roomId);
        return room != null && room.hasMember(playerId);
    }

    /**
     * Generates a collision-resistant 6-character room code.
     */
    private String generateUniqueRoomCode() {
        for (int attempt = 0; attempt < 50; attempt++) {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                int index = secureRandom.nextInt(CODE_ALPHABET.length());
                sb.append(CODE_ALPHABET.charAt(index));
            }
            String code = sb.toString();
            if (!roomsByCode.containsKey(code)) {
                return code;
            }
        }
        // Fallback to random UUID segment if high collision
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    /**
     * Periodically cleans up expired or closed rooms.
     */
    @Scheduled(fixedDelay = 60000L)
    public void cleanupExpiredRooms() {
        long now = System.currentTimeMillis();
        roomsById.entrySet().removeIf(entry -> {
            FriendsArenaRoom room = entry.getValue();
            boolean expired = (now - room.getCreatedAt() > ROOM_TTL_MS);
            boolean closed = (room.getStatus() == FriendsArenaRoomStatus.CLOSED);
            if (expired || closed) {
                roomsByCode.remove(room.getRoomCode());
                lastCreatedRoomByHost.remove(room.getHostPlayerId());
                log.info("Cleaned up Friends Arena room id={}, code={} (expired={}, closed={})", room.getRoomId(), room.getRoomCode(), expired, closed);
                return true;
            }
            return false;
        });
    }
}
