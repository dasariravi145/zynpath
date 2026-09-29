package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.model.FriendDuelInvitation;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.multiplayer.model.RematchRecord;
import com.zynpath.backend.social.model.InvitationStatus;
import com.zynpath.backend.social.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service governing Friend Duel 1v1 invitations, acceptance lifecycle, and rematch coordination.
 *
 * Implements Prompt 22 Sections 5-23 & 38-42:
 * - Server-authoritative invitation validation (friendship, blocks, self-invitation check).
 * - Bounded 60-second expiration window and duplicate invitation suppression.
 * - Atomic private match session creation upon acceptance.
 * - Interactive rematch request & acceptance with verified puzzle rotation.
 */
@Service
public class FriendDuelService {

    private static final Logger log = LoggerFactory.getLogger(FriendDuelService.class);
    private static final long INVITATION_TTL_MS = 60_000L; // 60 seconds bounded window
    private static final long REMATCH_TTL_MS = 60_000L;

    private final PlayerAccountService playerAccountService;
    private final SocialService socialService;
    private final MatchSessionService matchSessionService;
    private final MatchEventDispatcher eventDispatcher;
    private final com.zynpath.backend.notification.service.NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, FriendDuelInvitation> invitationsById = new ConcurrentHashMap<>();
    private final Map<String, RematchRecord> rematchesByPreviousMatch = new ConcurrentHashMap<>();

    public FriendDuelService(
            PlayerAccountService playerAccountService,
            SocialService socialService,
            MatchSessionService matchSessionService,
            MatchEventDispatcher eventDispatcher,
            @org.springframework.context.annotation.Lazy com.zynpath.backend.notification.service.NotificationService notificationService
    ) {
        this.playerAccountService = playerAccountService;
        this.socialService = socialService;
        this.matchSessionService = matchSessionService;
        this.eventDispatcher = eventDispatcher;
        this.notificationService = notificationService;

        // Periodic sweep of expired invitations and rematches
        scheduler.scheduleAtFixedRate(this::sweepExpiredRecords, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * Send a Friend Duel invitation to an accepted friend by their Public Zynpath ID.
     */
    public MultiplayerDto.FriendDuelInvitationDto sendInvitation(String inviterPlayerId, String targetPublicZynpathId) {
        PlayerAccount inviter = playerAccountService.findById(inviterPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Inviter account not found"));

        PlayerAccount target = playerAccountService.findByPublicId(targetPublicZynpathId.trim().toUpperCase())
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Target player not found: " + targetPublicZynpathId));

        if (inviter.playerId().equals(target.playerId())) {
            throw new AuthException("SELF_INVITATION", "Cannot invite yourself to a Friend Duel");
        }

        if (!socialService.areFriends(inviter.playerId(), target.playerId())) {
            throw new AuthException("NOT_FRIENDS", "Friend Duel requires an established friendship");
        }

        if (socialService.isBlocked(inviter.playerId(), target.playerId()) || socialService.isBlocked(target.playerId(), inviter.playerId())) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot challenge a blocked player", HttpStatus.FORBIDDEN);
        }

        // Duplicate prevention: check for existing pending unexpired invitation
        for (FriendDuelInvitation existing : invitationsById.values()) {
            if (existing.getStatus() == InvitationStatus.PENDING && !existing.isExpired()) {
                if (existing.getInviterPlayerId().equals(inviter.playerId()) && existing.getRecipientPlayerId().equals(target.playerId())) {
                    log.info("Duplicate invitation requested; returning existing pending invitation {}", existing.getInvitationId());
                    return toDto(existing);
                }
                // Simultaneous cross-invitation: target already invited inviter!
                if (existing.getInviterPlayerId().equals(target.playerId()) && existing.getRecipientPlayerId().equals(inviter.playerId())) {
                    log.info("Simultaneous cross-invitation detected; auto-accepting existing invitation {}", existing.getInvitationId());
                    acceptInvitation(inviter.playerId(), existing.getInvitationId());
                    return toDto(existing);
                }
            }
        }

        String invitationId = "finv_" + UUID.randomUUID().toString().substring(0, 12);
        long now = System.currentTimeMillis();
        long expiresAt = now + INVITATION_TTL_MS;

        FriendDuelInvitation invitation = new FriendDuelInvitation(
                invitationId,
                inviter.playerId(),
                inviter.publicZynpathId(),
                inviter.displayName(),
                target.playerId(),
                target.publicZynpathId(),
                target.displayName(),
                GameMode.FRIEND_DUEL,
                InvitationStatus.PENDING,
                now,
                expiresAt,
                null,
                null
        );

        invitationsById.put(invitationId, invitation);
        log.info("Created Friend Duel invitation: id={}, inviter={}, target={}", invitationId, inviter.playerId(), target.playerId());

        // Dispatch WebSocket event to recipient
        eventDispatcher.dispatchToPlayer(target.playerId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIEND_DUEL_INVITED,
                null,
                1,
                Map.of(
                        "invitationId", invitationId,
                        "inviterPlayerId", inviter.playerId(),
                        "inviterPublicId", inviter.publicZynpathId(),
                        "inviterDisplayName", inviter.displayName(),
                        "gameMode", "FRIEND_DUEL",
                        "expiresAt", expiresAt
                )
        ));

        try {
            notificationService.createNotification(
                    target.playerId(),
                    com.zynpath.backend.notification.model.NotificationEventType.FRIEND_DUEL_INVITATION,
                    "Friend Duel Challenge",
                    inviter.displayName() + " challenged you to a Friend Duel!",
                    invitationId,
                    "friend_duel?targetId=" + inviter.publicZynpathId(),
                    expiresAt,
                    inviter.playerId()
            );
        } catch (Exception e) {
            log.warn("Failed to dispatch friend duel notification: {}", e.getMessage());
        }

        return toDto(invitation);
    }

    /**
     * Accept a pending Friend Duel invitation, atomically creating a private 1v1 match session.
     */
    public ReconnectionSnapshot acceptInvitation(String invitationId, String recipientPlayerId) {
        FriendDuelInvitation invitation = invitationsById.get(invitationId);
        if (invitation == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }

        if (!invitation.getRecipientPlayerId().equals(recipientPlayerId)) {
            throw new AuthException("UNAUTHORIZED_INVITATION", "Only the intended recipient may accept this invitation", HttpStatus.FORBIDDEN);
        }

        if (invitation.isExpired()) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            throw new AuthException("INVITATION_EXPIRED", "This invitation has expired");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new AuthException("INVALID_INVITATION_STATE", "Invitation is already " + invitation.getStatus());
        }

        // Recheck friendship & blocks
        if (!socialService.areFriends(invitation.getInviterPlayerId(), recipientPlayerId)) {
            invitation.setStatus(InvitationStatus.INVALIDATED);
            throw new AuthException("NOT_FRIENDS", "Friendship is no longer active");
        }

        if (socialService.isBlocked(invitation.getInviterPlayerId(), recipientPlayerId) ||
                socialService.isBlocked(recipientPlayerId, invitation.getInviterPlayerId())) {
            invitation.setStatus(InvitationStatus.INVALIDATED);
            throw new AuthException("PLAYER_BLOCKED", "Cannot duel a blocked player", HttpStatus.FORBIDDEN);
        }

        synchronized (invitation) {
            if (invitation.getStatus() != InvitationStatus.PENDING) {
                if (invitation.getStatus() == InvitationStatus.ACCEPTED && invitation.getMatchId() != null) {
                    return matchSessionService.getSession(invitation.getMatchId()).toSnapshot();
                }
                throw new AuthException("INVALID_INVITATION_STATE", "Invitation state is " + invitation.getStatus());
            }

            invitation.setStatus(InvitationStatus.ACCEPTED);

            PlayerAccount inviter = playerAccountService.findById(invitation.getInviterPlayerId())
                    .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Inviter account not found"));
            PlayerAccount recipient = playerAccountService.findById(recipientPlayerId)
                    .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Recipient account not found"));

            // Create private match session with verified puzzle
            MatchSession session = matchSessionService.createMatchSession(GameMode.FRIEND_DUEL, inviter.playerId());

            session.addParticipant(new MatchParticipant(
                    inviter.playerId(),
                    inviter.publicZynpathId(),
                    inviter.displayName(),
                    "avatar_compass",
                    System.currentTimeMillis()
            ));

            session.addParticipant(new MatchParticipant(
                    recipient.playerId(),
                    recipient.publicZynpathId(),
                    recipient.displayName(),
                    "avatar_compass",
                    System.currentTimeMillis()
            ));

            session.transitionTo(MatchState.WAITING_FOR_PLAYERS);
            invitation.setMatchId(session.getMatchId());

            log.info("Accepted Friend Duel invitation {}: created matchId={}", invitationId, session.getMatchId());

            ReconnectionSnapshot snapshot = session.toSnapshot();

            // Dispatch WebSocket notification to inviter
            eventDispatcher.dispatchToPlayer(inviter.playerId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIEND_DUEL_ACCEPTED,
                    session.getMatchId(),
                    session.nextSequenceNumber(),
                    snapshot
            ));

            // Dispatch WebSocket notification to recipient
            eventDispatcher.dispatchToPlayer(recipient.playerId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.FRIEND_DUEL_MATCH_CREATED,
                    session.getMatchId(),
                    session.nextSequenceNumber(),
                    snapshot
            ));

            try {
                notificationService.createNotification(
                        inviter.playerId(),
                        com.zynpath.backend.notification.model.NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
                        "Friend Duel Accepted",
                        recipient.displayName() + " accepted your Friend Duel challenge! Starting match...",
                        invitationId,
                        "friend_duel",
                        null,
                        recipient.playerId()
                );
            } catch (Exception e) {
                log.warn("Failed to dispatch friend duel accepted notification: {}", e.getMessage());
            }

            return snapshot;
        }
    }

    /**
     * Decline a pending Friend Duel invitation.
     */
    public MultiplayerDto.FriendDuelInvitationDto declineInvitation(String invitationId, String recipientPlayerId) {
        FriendDuelInvitation invitation = invitationsById.get(invitationId);
        if (invitation == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }

        if (!invitation.getRecipientPlayerId().equals(recipientPlayerId)) {
            throw new AuthException("UNAUTHORIZED_INVITATION", "Only the intended recipient may decline this invitation", HttpStatus.FORBIDDEN);
        }

        if (invitation.getStatus() == InvitationStatus.DECLINED) {
            return toDto(invitation);
        }

        invitation.setStatus(InvitationStatus.DECLINED);
        log.info("Declined Friend Duel invitation: id={}, recipient={}", invitationId, recipientPlayerId);

        // Notify inviter
        eventDispatcher.dispatchToPlayer(invitation.getInviterPlayerId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIEND_DUEL_DECLINED,
                null,
                1,
                Map.of("invitationId", invitationId, "message", "Friend declined duel challenge")
        ));

        try {
            notificationService.createNotification(
                    invitation.getInviterPlayerId(),
                    com.zynpath.backend.notification.model.NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED,
                    "Friend Duel Declined",
                    invitation.getRecipientDisplayName() + " declined your Friend Duel challenge.",
                    invitationId,
                    "friend_duel",
                    null,
                    invitation.getRecipientPlayerId()
            );
        } catch (Exception e) {
            log.warn("Failed to dispatch friend duel declined notification: {}", e.getMessage());
        }

        return toDto(invitation);
    }

    /**
     * Cancel an outgoing Friend Duel invitation.
     */
    public MultiplayerDto.FriendDuelInvitationDto cancelInvitation(String invitationId, String inviterPlayerId) {
        FriendDuelInvitation invitation = invitationsById.get(invitationId);
        if (invitation == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }

        if (!invitation.getInviterPlayerId().equals(inviterPlayerId)) {
            throw new AuthException("UNAUTHORIZED_INVITATION", "Only the inviter may cancel this invitation", HttpStatus.FORBIDDEN);
        }

        if (invitation.getStatus() == InvitationStatus.CANCELLED) {
            return toDto(invitation);
        }

        invitation.setStatus(InvitationStatus.CANCELLED);
        log.info("Cancelled Friend Duel invitation: id={}, inviter={}", invitationId, inviterPlayerId);

        // Notify recipient
        eventDispatcher.dispatchToPlayer(invitation.getRecipientPlayerId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.FRIEND_DUEL_CANCELLED,
                null,
                1,
                Map.of("invitationId", invitationId, "message", "Friend cancelled duel challenge")
        ));

        return toDto(invitation);
    }

    public List<MultiplayerDto.FriendDuelInvitationDto> getIncomingInvitations(String playerId) {
        List<MultiplayerDto.FriendDuelInvitationDto> result = new ArrayList<>();
        for (FriendDuelInvitation inv : invitationsById.values()) {
            if (inv.getRecipientPlayerId().equals(playerId) && inv.getStatus() == InvitationStatus.PENDING && !inv.isExpired()) {
                result.add(toDto(inv));
            }
        }
        return result;
    }

    public List<MultiplayerDto.FriendDuelInvitationDto> getOutgoingInvitations(String playerId) {
        List<MultiplayerDto.FriendDuelInvitationDto> result = new ArrayList<>();
        for (FriendDuelInvitation inv : invitationsById.values()) {
            if (inv.getInviterPlayerId().equals(playerId) && inv.getStatus() == InvitationStatus.PENDING && !inv.isExpired()) {
                result.add(toDto(inv));
            }
        }
        return result;
    }

    public MultiplayerDto.FriendDuelInvitationDto getInvitation(String invitationId, String playerId) {
        FriendDuelInvitation inv = invitationsById.get(invitationId);
        if (inv == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }
        return toDto(inv);
    }

    public MultiplayerDto.FriendDuelInvitationDto getInvitation(String invitationId) {
        FriendDuelInvitation inv = invitationsById.get(invitationId);
        if (inv == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }
        return toDto(inv);
    }

    /**
     * Request a Rematch against the opponent of a completed match session.
     * Implements Prompt 22 Sections 38-42.
     */
    public MultiplayerDto.RematchStatusDto requestRematch(String previousMatchId, String playerId) {
        MatchSession prevSession = matchSessionService.getSession(previousMatchId);
        if (prevSession.getState() != MatchState.COMPLETED) {
            throw new AuthException("INVALID_MATCH_STATE", "Rematch can only be requested after match is COMPLETED");
        }

        if (!prevSession.hasParticipant(playerId)) {
            throw new AuthException("NOT_A_PARTICIPANT", "You were not a participant in this match", HttpStatus.FORBIDDEN);
        }

        // Identify opponent
        MatchParticipant opponent = prevSession.getParticipants().values().stream()
                .filter(p -> !p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new AuthException("OPPONENT_NOT_FOUND", "No opponent found for rematch"));

        // Check friendship and blocks
        if (!socialService.areFriends(playerId, opponent.getPlayerId())) {
            throw new AuthException("NOT_FRIENDS", "Rematch requires an established friendship");
        }

        if (socialService.isBlocked(playerId, opponent.getPlayerId()) || socialService.isBlocked(opponent.getPlayerId(), playerId)) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot rematch a blocked player", HttpStatus.FORBIDDEN);
        }

        RematchRecord existing = rematchesByPreviousMatch.get(previousMatchId);
        if (existing != null && existing.getStatus() == InvitationStatus.PENDING && !existing.isExpired()) {
            // If already requested by caller, return idempotent status
            if (existing.getRequesterPlayerId().equals(playerId)) {
                return toRematchDto(existing);
            }
            // If opponent already requested it, caller's request acts as atomic acceptance!
            return toRematchDto(acceptRematchInternal(playerId, previousMatchId, existing));
        }

        long now = System.currentTimeMillis();
        long expiresAt = now + REMATCH_TTL_MS;
        RematchRecord record = new RematchRecord(
                previousMatchId,
                playerId,
                opponent.getPlayerId(),
                InvitationStatus.PENDING,
                now,
                expiresAt,
                null
        );

        rematchesByPreviousMatch.put(previousMatchId, record);
        log.info("Rematch requested: matchId={}, requester={}, opponent={}", previousMatchId, playerId, opponent.getPlayerId());

        // Dispatch WebSocket notification to opponent
        eventDispatcher.dispatchToPlayer(opponent.getPlayerId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.REMATCH_REQUESTED,
                previousMatchId,
                1,
                Map.of(
                        "previousMatchId", previousMatchId,
                        "requesterPlayerId", playerId,
                        "expiresAt", expiresAt
                )
        ));

        return toRematchDto(record);
    }

    /**
     * Respond to an incoming Rematch request (Accept or Decline).
     */
    public MultiplayerDto.RematchStatusDto respondToRematch(String previousMatchId, String playerId, boolean accept) {
        RematchRecord record = rematchesByPreviousMatch.get(previousMatchId);
        if (record == null) {
            throw new AuthException("REMATCH_NOT_FOUND", "No active rematch request for match: " + previousMatchId, HttpStatus.NOT_FOUND);
        }

        if (!record.getOpponentPlayerId().equals(playerId)) {
            throw new AuthException("UNAUTHORIZED_REMATCH", "Only the challenged player may respond to this rematch", HttpStatus.FORBIDDEN);
        }

        if (record.isExpired()) {
            record.setStatus(InvitationStatus.EXPIRED);
            throw new AuthException("REMATCH_EXPIRED", "Rematch request has expired");
        }

        if (record.getStatus() != InvitationStatus.PENDING) {
            throw new AuthException("INVALID_REMATCH_STATE", "Rematch request is already " + record.getStatus());
        }

        if (!accept) {
            record.setStatus(InvitationStatus.DECLINED);
            log.info("Rematch declined for matchId={} by playerId={}", previousMatchId, playerId);
            eventDispatcher.dispatchToPlayer(record.getRequesterPlayerId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.REMATCH_DECLINED,
                    previousMatchId,
                    1,
                    Map.of("previousMatchId", previousMatchId, "message", "Friend declined rematch")
            ));
            return toRematchDto(record);
        }

        RematchRecord accepted = acceptRematchInternal(playerId, previousMatchId, record);
        return toRematchDto(accepted);
    }

    public MultiplayerDto.RematchStatusDto getRematchStatus(String previousMatchId, String playerId) {
        RematchRecord record = rematchesByPreviousMatch.get(previousMatchId);
        if (record == null) {
            return new MultiplayerDto.RematchStatusDto(
                    previousMatchId,
                    playerId,
                    "NOT_REQUESTED",
                    0,
                    0,
                    null
            );
        }
        return toRematchDto(record);
    }

    private RematchRecord acceptRematchInternal(String acceptingPlayerId, String previousMatchId, RematchRecord record) {
        synchronized (record) {
            if (record.getStatus() == InvitationStatus.ACCEPTED && record.getNewMatchId() != null) {
                return record;
            }

            MatchSession prevSession = matchSessionService.getSession(previousMatchId);
            String excludePuzzleId = prevSession.getPuzzleAssignment() != null ? prevSession.getPuzzleAssignment().puzzleId() : null;

            // Create new match session with a fresh puzzle avoiding the previous puzzle
            MatchSession newSession = matchSessionService.createMatchSession(GameMode.FRIEND_DUEL, record.getRequesterPlayerId(), excludePuzzleId);

            for (MatchParticipant p : prevSession.getParticipants().values()) {
                newSession.addParticipant(new MatchParticipant(
                        p.getPlayerId(),
                        p.getPublicZynpathId(),
                        p.getDisplayName(),
                        p.getAvatarId(),
                        System.currentTimeMillis()
                ));
            }

            newSession.transitionTo(MatchState.WAITING_FOR_PLAYERS);
            record.setStatus(InvitationStatus.ACCEPTED);
            record.setNewMatchId(newSession.getMatchId());

            log.info("Rematch accepted for previousMatchId={}, newMatchId={}", previousMatchId, newSession.getMatchId());


            ReconnectionSnapshot snapshot = newSession.toSnapshot();

            // Notify both players of the rematch match creation
            eventDispatcher.dispatchToPlayer(record.getRequesterPlayerId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.REMATCH_ACCEPTED,
                    newSession.getMatchId(),
                    newSession.nextSequenceNumber(),
                    snapshot
            ));

            eventDispatcher.dispatchToPlayer(record.getOpponentPlayerId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.REMATCH_ACCEPTED,
                    newSession.getMatchId(),
                    newSession.nextSequenceNumber(),
                    snapshot
            ));

            return record;
        }
    }

    private void sweepExpiredRecords() {
        for (FriendDuelInvitation inv : invitationsById.values()) {
            if (inv.getStatus() == InvitationStatus.PENDING && inv.isExpired()) {
                inv.setStatus(InvitationStatus.EXPIRED);
                eventDispatcher.dispatchToPlayer(inv.getInviterPlayerId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.FRIEND_DUEL_EXPIRED,
                        null,
                        1,
                        Map.of("invitationId", inv.getInvitationId())
                ));
            }
        }
        for (RematchRecord rem : rematchesByPreviousMatch.values()) {
            if (rem.getStatus() == InvitationStatus.PENDING && rem.isExpired()) {
                rem.setStatus(InvitationStatus.EXPIRED);
                eventDispatcher.dispatchToPlayer(rem.getRequesterPlayerId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.REMATCH_EXPIRED,
                        rem.getPreviousMatchId(),
                        1,
                        Map.of("previousMatchId", rem.getPreviousMatchId())
                ));
            }
        }
    }

    private MultiplayerDto.FriendDuelInvitationDto toDto(FriendDuelInvitation inv) {
        return new MultiplayerDto.FriendDuelInvitationDto(
                inv.getInvitationId(),
                inv.getInviterPlayerId(),
                inv.getInviterPublicId(),
                inv.getInviterDisplayName(),
                inv.getRecipientPlayerId(),
                inv.getRecipientPublicId(),
                inv.getRecipientDisplayName(),
                inv.getGameMode().name(),
                inv.getStatus().name(),
                inv.getCreatedAt(),
                inv.getExpiresAt(),
                inv.getMatchId(),
                inv.getPreviousMatchId()
        );
    }

    private MultiplayerDto.RematchStatusDto toRematchDto(RematchRecord rem) {
        return new MultiplayerDto.RematchStatusDto(
                rem.getPreviousMatchId(),
                rem.getRequesterPlayerId(),
                rem.getStatus().name(),
                rem.getCreatedAt(),
                rem.getExpiresAt(),
                rem.getNewMatchId()
        );
    }
}
