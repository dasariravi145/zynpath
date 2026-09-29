package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import com.zynpath.backend.social.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service managing matchmaking queues, opponent pairing, and match session instantiation.
 *
 * Implements Prompt 20 Sections 14-20:
 * - Quick Duel dedicated FIFO queue with duplicate tap suppression.
 * - Self-match prevention and block relationship enforcement.
 * - Bounded queue timeout (45 seconds) without bot substitution.
 * - Friend Duel and Mini League (2-5 total participants) room foundations.
 */
@Service
public class MatchmakingService {

    private static final Logger log = LoggerFactory.getLogger(MatchmakingService.class);

    private static final long QUEUE_TIMEOUT_MS = 45_000L;

    public record QueueTicket(
        String ticketId,
        String playerId,
        String publicZynpathId,
        String displayName,
        String avatarId,
        long enqueuedAt,
        String matchedSessionId
    ) {}

    private final MatchSessionService matchSessionService;
    private final PlayerAccountService playerAccountService;
    private final SocialService socialService;
    private final MatchEventDispatcher eventDispatcher;

    // Active Quick Duel tickets: ticketId -> QueueTicket
    private final Map<String, QueueTicket> ticketsById = new ConcurrentHashMap<>();
    // Player to active ticket mapping: playerId -> ticketId
    private final Map<String, String> playerActiveTicketMap = new ConcurrentHashMap<>();
    // FIFO Queue of waiting tickets
    private final ConcurrentLinkedQueue<String> quickDuelQueue = new ConcurrentLinkedQueue<>();

    public MatchmakingService(
            MatchSessionService matchSessionService,
            PlayerAccountService playerAccountService,
            SocialService socialService,
            @Lazy MatchEventDispatcher eventDispatcher
    ) {
        this.matchSessionService = matchSessionService;
        this.playerAccountService = playerAccountService;
        this.socialService = socialService;
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Enqueues an authenticated player into the Quick Duel queue or pairs with a waiting opponent.
     */
    public synchronized MultiplayerDto.MatchmakingStatusResponse enqueueQuickDuel(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        // 1. Check if player already has an active waiting ticket
        String existingTicketId = playerActiveTicketMap.get(playerId);
        if (existingTicketId != null) {
            QueueTicket existing = ticketsById.get(existingTicketId);
            if (existing != null) {
                long elapsed = System.currentTimeMillis() - existing.enqueuedAt();
                if (existing.matchedSessionId() != null) {
                    return new MultiplayerDto.MatchmakingStatusResponse(
                            existing.ticketId(), "MATCH_FOUND", existing.matchedSessionId(), existing.enqueuedAt(), elapsed
                    );
                }
                if (elapsed < QUEUE_TIMEOUT_MS) {
                    return new MultiplayerDto.MatchmakingStatusResponse(
                            existing.ticketId(), "SEARCHING", null, existing.enqueuedAt(), elapsed
                    );
                }
                // Expired ticket, clean up
                cancelTicket(playerId);
            }
        }

        // 2. Look for an eligible waiting opponent in queue
        Iterator<String> it = quickDuelQueue.iterator();
        while (it.hasNext()) {
            String candidateTicketId = it.next();
            QueueTicket candidate = ticketsById.get(candidateTicketId);

            if (candidate == null) {
                it.remove();
                continue;
            }

            // Exclude self
            if (candidate.playerId().equals(playerId)) {
                continue;
            }

            // Check if candidate expired
            if (System.currentTimeMillis() - candidate.enqueuedAt() > QUEUE_TIMEOUT_MS) {
                it.remove();
                ticketsById.remove(candidateTicketId);
                playerActiveTicketMap.remove(candidate.playerId());
                continue;
            }

            // Check block status between players
            if (socialService.isBlocked(playerId, candidate.playerId()) ||
                socialService.isBlocked(candidate.playerId(), playerId)) {
                continue;
            }

            // Eligible match found!
            it.remove();

            // 3. Create Quick Duel match session
            MatchSession session = matchSessionService.createMatchSession(GameMode.QUICK_DUEL, candidate.playerId());

            // Retain matched ticket with session ID for status polling resilience
            QueueTicket matchedCandidateTicket = new QueueTicket(
                    candidate.ticketId(),
                    candidate.playerId(),
                    candidate.publicZynpathId(),
                    candidate.displayName(),
                    candidate.avatarId(),
                    candidate.enqueuedAt(),
                    session.getMatchId()
            );
            ticketsById.put(candidateTicketId, matchedCandidateTicket);

            // Add candidate participant
            session.addParticipant(new MatchParticipant(
                    candidate.playerId(),
                    candidate.publicZynpathId(),
                    candidate.displayName(),
                    candidate.avatarId(),
                    System.currentTimeMillis()
            ));

            // Add current participant
            session.addParticipant(new MatchParticipant(
                    account.playerId(),
                    account.publicZynpathId(),
                    account.displayName(),
                    "avatar_compass",
                    System.currentTimeMillis()
            ));

            session.transitionTo(MatchState.WAITING_FOR_PLAYERS);

            // Notify both players via WebSocket
            long seq = session.nextSequenceNumber();
            MultiplayerEventEnvelope matchFoundEvent = MultiplayerEventEnvelope.create(
                    MultiplayerEventType.MATCH_FOUND,
                    session.getMatchId(),
                    seq,
                    session.toSnapshot()
            );

            eventDispatcher.dispatchToPlayer(candidate.playerId(), matchFoundEvent);
            eventDispatcher.dispatchToPlayer(playerId, matchFoundEvent);

            log.info("Quick Duel paired: matchId={}, player1={}, player2={}",
                    session.getMatchId(), candidate.playerId(), playerId);

            return new MultiplayerDto.MatchmakingStatusResponse(
                    "ticket_" + UUID.randomUUID().toString().substring(0, 8),
                    "MATCH_FOUND",
                    session.getMatchId(),
                    System.currentTimeMillis(),
                    0L
            );
        }

        // 4. No opponent found immediately: enqueue new ticket
        String ticketId = "qticket_" + UUID.randomUUID().toString().substring(0, 10);
        long now = System.currentTimeMillis();
        QueueTicket ticket = new QueueTicket(
                ticketId,
                playerId,
                account.publicZynpathId(),
                account.displayName(),
                "avatar_compass",
                now,
                null
        );

        ticketsById.put(ticketId, ticket);
        playerActiveTicketMap.put(playerId, ticketId);
        quickDuelQueue.add(ticketId);

        log.info("Enqueued Quick Duel ticket: id={}, player={}", ticketId, playerId);
        return new MultiplayerDto.MatchmakingStatusResponse(ticketId, "SEARCHING", null, now, 0L);
    }

    /**
     * Cancels an active matchmaking ticket.
     */
    public synchronized boolean cancelTicket(String playerId) {
        String ticketId = playerActiveTicketMap.remove(playerId);
        if (ticketId != null) {
            ticketsById.remove(ticketId);
            quickDuelQueue.remove(ticketId);
            log.info("Cancelled Quick Duel ticket for playerId={}", playerId);
            return true;
        }
        return false;
    }

    /**
     * Gets current matchmaking ticket status for a player.
     */
    public MultiplayerDto.MatchmakingStatusResponse getTicketStatus(String playerId) {
        String ticketId = playerActiveTicketMap.get(playerId);
        if (ticketId == null) {
            return new MultiplayerDto.MatchmakingStatusResponse(null, "IDLE", null, 0L, 0L);
        }

        QueueTicket ticket = ticketsById.get(ticketId);
        if (ticket == null) {
            playerActiveTicketMap.remove(playerId);
            return new MultiplayerDto.MatchmakingStatusResponse(null, "IDLE", null, 0L, 0L);
        }

        long elapsed = System.currentTimeMillis() - ticket.enqueuedAt();
        if (ticket.matchedSessionId() != null) {
            return new MultiplayerDto.MatchmakingStatusResponse(ticketId, "MATCH_FOUND", ticket.matchedSessionId(), ticket.enqueuedAt(), elapsed);
        }

        if (elapsed > QUEUE_TIMEOUT_MS) {
            cancelTicket(playerId);
            return new MultiplayerDto.MatchmakingStatusResponse(ticketId, "TIMEOUT", null, ticket.enqueuedAt(), elapsed);
        }

        return new MultiplayerDto.MatchmakingStatusResponse(ticketId, "SEARCHING", null, ticket.enqueuedAt(), elapsed);
    }

    /**
     * Creates an invited 1v1 Friend Duel session.
     */
    public MatchSession createFriendDuel(String hostPlayerId, String targetPublicZynpathId) {
        PlayerAccount host = playerAccountService.findById(hostPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Host player not found"));

        PlayerAccount target = playerAccountService.findByPublicId(targetPublicZynpathId.trim().toUpperCase())
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Target player not found: " + targetPublicZynpathId));

        if (host.playerId().equals(target.playerId())) {
            throw new AuthException("SELF_INVITATION", "Cannot challenge yourself to a Friend Duel");
        }

        if (!socialService.areFriends(hostPlayerId, target.playerId())) {
            throw new AuthException("NOT_FRIENDS", "Friend Duel requires an established friendship");
        }

        if (socialService.isBlocked(hostPlayerId, target.playerId()) || socialService.isBlocked(target.playerId(), hostPlayerId)) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot duel a blocked player", HttpStatus.FORBIDDEN);
        }

        MatchSession session = matchSessionService.createMatchSession(GameMode.FRIEND_DUEL, hostPlayerId);

        session.addParticipant(new MatchParticipant(
                host.playerId(),
                host.publicZynpathId(),
                host.displayName(),
                "avatar_compass",
                System.currentTimeMillis()
        ));

        session.addParticipant(new MatchParticipant(
                target.playerId(),
                target.publicZynpathId(),
                target.displayName(),
                "avatar_compass",
                System.currentTimeMillis()
        ));

        session.transitionTo(MatchState.WAITING_FOR_PLAYERS);

        log.info("Created Friend Duel session: matchId={}, host={}, target={}",
                session.getMatchId(), hostPlayerId, target.playerId());
        return session;
    }

    /**
     * Creates a 2 to 5 participant Mini League tournament room.
     */
    public MatchSession createMiniLeague(String hostPlayerId, String roomName, int maxParticipants) {
        if (maxParticipants < 2 || maxParticipants > 5) {
            throw new AuthException("INVALID_CAPACITY", "Mini League supports between 2 and 5 total participants");
        }

        PlayerAccount host = playerAccountService.findById(hostPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Host player not found"));

        MatchSession session = matchSessionService.createMatchSession(GameMode.MINI_LEAGUE, hostPlayerId);

        session.addParticipant(new MatchParticipant(
                host.playerId(),
                host.publicZynpathId(),
                host.displayName(),
                "avatar_compass",
                System.currentTimeMillis()
        ));

        session.transitionTo(MatchState.WAITING_FOR_PLAYERS);

        log.info("Created Mini League session: matchId={}, host={}, maxParticipants={}",
                session.getMatchId(), hostPlayerId, maxParticipants);
        return session;
    }
}
