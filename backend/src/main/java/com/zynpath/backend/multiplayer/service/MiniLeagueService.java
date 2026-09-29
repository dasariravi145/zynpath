package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.multiplayer.model.MiniLeagueInvitation;
import com.zynpath.backend.multiplayer.model.MiniLeagueRoom;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.social.model.InvitationStatus;
import com.zynpath.backend.social.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authoritative service managing Mini League private rooms, 2-5 player lobbies,
 * invitations, host transfer, readiness, and synchronized match initiation.
 *
 * Implements Prompt 23 Sections 5, 6, 9-29, 57-60.
 */
@Service
public class MiniLeagueService {

    private static final Logger log = LoggerFactory.getLogger(MiniLeagueService.class);
    private static final long ROOM_TTL_MS = 15 * 60 * 1000L; // 15 minutes
    private static final long INVITATION_TTL_MS = 60 * 1000L; // 60 seconds
    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 32 characters, no 0/O, 1/I

    private final MatchSessionService matchSessionService;
    private final PlayerAccountService playerAccountService;
    private final SocialService socialService;
    private final MatchEventDispatcher eventDispatcher;
    private final com.zynpath.backend.notification.service.NotificationService notificationService;
    private final SecureRandom random = new SecureRandom();

    private final Map<String, MiniLeagueRoom> roomsById = new ConcurrentHashMap<>();
    private final Map<String, MiniLeagueRoom> roomsByCode = new ConcurrentHashMap<>();
    private final Map<String, MiniLeagueInvitation> invitationsById = new ConcurrentHashMap<>();

    public MiniLeagueService(
            MatchSessionService matchSessionService,
            PlayerAccountService playerAccountService,
            SocialService socialService,
            MatchEventDispatcher eventDispatcher,
            @org.springframework.context.annotation.Lazy com.zynpath.backend.notification.service.NotificationService notificationService
    ) {
        this.matchSessionService = matchSessionService;
        this.playerAccountService = playerAccountService;
        this.socialService = socialService;
        this.eventDispatcher = eventDispatcher;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new private Mini League room with 2-5 participant limit.
     * Host counts as participant 1.
     */
    public MultiplayerDto.MiniLeagueRoomDto createRoom(String hostPlayerId, String roomName, int maxParticipants) {
        if (maxParticipants < 2 || maxParticipants > 5) {
            throw new AuthException("INVALID_CAPACITY", "Mini League supports between 2 and 5 total participants", HttpStatus.BAD_REQUEST);
        }

        PlayerAccount host = playerAccountService.findById(hostPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Host account not found", HttpStatus.NOT_FOUND));
        String roomId = "room_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String roomCode = generateUniqueRoomCode();
        long now = System.currentTimeMillis();

        // 1. Create underlying match session
        MatchSession session = matchSessionService.createMatchSession(GameMode.MINI_LEAGUE, hostPlayerId);

        // 2. Add host as the first participant
        session.addParticipant(new MatchParticipant(
                host.playerId(),
                host.publicZynpathId(),
                host.displayName(),
                "avatar_compass",
                now
        ));
        session.transitionTo(MatchState.WAITING_FOR_PLAYERS);

        // 3. Store room
        MiniLeagueRoom room = new MiniLeagueRoom(
                roomId,
                roomCode,
                roomName != null && !roomName.isBlank() ? roomName.trim() : host.displayName() + "'s Room",
                hostPlayerId,
                maxParticipants,
                session.getMatchId(),
                now,
                ROOM_TTL_MS
        );

        roomsById.put(roomId, room);
        roomsByCode.put(roomCode, room);

        log.info("Created Mini League room: id={}, code={}, host={}, capacity={}", roomId, roomCode, hostPlayerId, maxParticipants);

        eventDispatcher.dispatchToPlayer(hostPlayerId, MultiplayerEventEnvelope.create(
                MultiplayerEventType.MINI_LEAGUE_ROOM_CREATED,
                session.getMatchId(),
                session.nextSequenceNumber(),
                toDto(room, session)
        ));

        return toDto(room, session);
    }

    /**
     * Retrieve room details by room ID.
     */
    public MultiplayerDto.MiniLeagueRoomDto getRoom(String roomId, String playerId) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found or expired: " + roomId, HttpStatus.NOT_FOUND);
        }
        MatchSession session = matchSessionService.getSession(room.getMatchId());
        return toDto(room, session);
    }

    /**
     * Retrieve room details by room code.
     */
    public MultiplayerDto.MiniLeagueRoomDto getRoomByCode(String roomCode, String playerId) {
        String code = roomCode != null ? roomCode.trim().toUpperCase() : "";
        MiniLeagueRoom room = roomsByCode.get(code);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found for code: " + code, HttpStatus.NOT_FOUND);
        }
        MatchSession session = matchSessionService.getSession(room.getMatchId());
        return toDto(room, session);
    }

    /**
     * Join room using a readable room code.
     */
    public MultiplayerDto.MiniLeagueRoomDto joinRoomByCode(String roomCode, String playerId) {
        String code = roomCode != null ? roomCode.trim().toUpperCase() : "";
        MiniLeagueRoom room = roomsByCode.get(code);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room code not found or expired: " + code, HttpStatus.NOT_FOUND);
        }
        return joinRoomInternal(room, playerId);
    }

    /**
     * Join room directly by room ID.
     */
    public MultiplayerDto.MiniLeagueRoomDto joinRoom(String roomId, String playerId) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found or expired: " + roomId, HttpStatus.NOT_FOUND);
        }
        return joinRoomInternal(room, playerId);
    }

    private MultiplayerDto.MiniLeagueRoomDto joinRoomInternal(MiniLeagueRoom room, String playerId) {
        MatchSession session = matchSessionService.getSession(room.getMatchId());

        synchronized (session) {
            // Check if player is already a participant (idempotent)
            if (session.hasParticipant(playerId)) {
                return toDto(room, session);
            }

            // Check match state
            if (session.getState() != MatchState.WAITING_FOR_PLAYERS && session.getState() != MatchState.CREATED) {
                throw new AuthException("MATCH_ALREADY_STARTED", "Cannot join room: match is already " + session.getState(), HttpStatus.CONFLICT);
            }

            // Check capacity (2-5 total participants)
            if (session.getParticipantCount() >= room.getMaxParticipants()) {
                throw new AuthException("ROOM_FULL", "Room has reached its maximum capacity of " + room.getMaxParticipants() + " players", HttpStatus.CONFLICT);
            }

            // Check blocks with host
            if (socialService.isBlocked(playerId, room.getHostPlayerId()) || socialService.isBlocked(room.getHostPlayerId(), playerId)) {
                throw new AuthException("PLAYER_BLOCKED", "Cannot join room due to block policy", HttpStatus.FORBIDDEN);
            }

            PlayerAccount account = playerAccountService.findById(playerId)
                    .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));
            session.addParticipant(new MatchParticipant(
                    account.playerId(),
                    account.publicZynpathId(),
                    account.displayName(),
                    "avatar_compass",
                    System.currentTimeMillis()
            ));

            log.info("Player {} joined Mini League room: id={}, currentCount={}", playerId, room.getRoomId(), session.getParticipantCount());

            // Dispatch update to all participants in the room
            eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.MINI_LEAGUE_PLAYER_JOINED,
                    session.getMatchId(),
                    session.nextSequenceNumber(),
                    toDto(room, session)
            ));

            return toDto(room, session);
        }
    }

    /**
     * Leave a room before match starts, or forfeit if match is active.
     * Enforces host transfer if the host leaves before start.
     */
    public void leaveRoom(String roomId, String playerId) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null) {
            return;
        }

        MatchSession session = matchSessionService.getSession(room.getMatchId());

        synchronized (session) {
            if (session.getState().isPlayable()) {
                // Active match: delegate to forfeit
                matchSessionService.forfeitMatch(session.getMatchId(), playerId);
                return;
            }

            if (!session.hasParticipant(playerId)) {
                return;
            }

            session.removeParticipant(playerId);
            log.info("Player {} left Mini League room {}", playerId, roomId);

            if (session.getParticipantCount() == 0) {
                // No participants left: cancel room
                session.transitionTo(MatchState.CANCELLED);
                roomsById.remove(roomId);
                roomsByCode.remove(room.getRoomCode());
                log.info("Cancelled empty Mini League room {}", roomId);

                eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.MINI_LEAGUE_ROOM_CANCELLED,
                        session.getMatchId(),
                        session.nextSequenceNumber(),
                        Map.of("roomId", roomId, "message", "Room closed because all players left")
                ));
            } else if (room.getHostPlayerId().equals(playerId)) {
                // Host left: transfer host to earliest joined remaining participant
                MatchParticipant newHost = session.getParticipants().values().stream()
                        .min(Comparator.comparingLong(MatchParticipant::getJoinedAt))
                        .orElse(null);

                if (newHost != null) {
                    room.setHostPlayerId(newHost.getPlayerId());
                    session.setHostPlayerId(newHost.getPlayerId());
                    log.info("Transferred host of room {} to player {}", roomId, newHost.getPlayerId());

                    eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                            MultiplayerEventType.MINI_LEAGUE_HOST_CHANGED,
                            session.getMatchId(),
                            session.nextSequenceNumber(),
                            Map.of(
                                    "roomId", roomId,
                                    "newHostPlayerId", newHost.getPlayerId(),
                                    "newHostDisplayName", newHost.getDisplayName()
                            )
                    ));
                }
            } else {
                // Non-host left
                eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                        MultiplayerEventType.MINI_LEAGUE_PLAYER_LEFT,
                        session.getMatchId(),
                        session.nextSequenceNumber(),
                        Map.of("roomId", roomId, "playerId", playerId)
                ));
            }
        }
    }

    /**
     * Participant confirms or revokes readiness.
     */
    public MultiplayerDto.MiniLeagueRoomDto setReady(String roomId, String playerId, boolean ready) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found or expired: " + roomId, HttpStatus.NOT_FOUND);
        }

        MatchSession session = matchSessionService.getSession(room.getMatchId());
        MatchParticipant participant = session.getParticipant(playerId);
        if (participant == null) {
            throw new AuthException("NOT_A_PARTICIPANT", "You are not a participant in this room", HttpStatus.FORBIDDEN);
        }

        participant.setReady(ready);

        eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.MINI_LEAGUE_READY_CHANGED,
                session.getMatchId(),
                session.nextSequenceNumber(),
                Map.of("roomId", roomId, "playerId", playerId, "ready", ready)
        ));

        // When all players are ready and room has at least 2 players, notify participants (Section 29)
        if (session.areAllParticipantsReady() && session.getParticipantCount() >= 2) {
            for (MatchParticipant p : session.getParticipants().values()) {
                try {
                    notificationService.createNotification(
                            p.getPlayerId(),
                            com.zynpath.backend.notification.model.NotificationEventType.MINI_LEAGUE_READY,
                            "Mini League Ready",
                            "All players in room " + room.getRoomCode() + " are ready! Match is ready to launch.",
                            roomId,
                            "mini_league?roomCode=" + room.getRoomCode(),
                            null,
                            room.getHostPlayerId()
                    );
                } catch (Exception e) {
                    log.warn("Failed to dispatch mini league ready notification: {}", e.getMessage());
                }
            }
        }

        return toDto(room, session);
    }

    /**
     * Host initiates match start after verifying 2-5 players are ready.
     */
    public ReconnectionSnapshot startMatch(String roomId, String hostPlayerId) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found or expired: " + roomId, HttpStatus.NOT_FOUND);
        }

        if (!room.getHostPlayerId().equals(hostPlayerId)) {
            throw new AuthException("HOST_ONLY", "Only the room host may start the match", HttpStatus.FORBIDDEN);
        }

        MatchSession session = matchSessionService.getSession(room.getMatchId());

        synchronized (session) {
            if (session.getState() != MatchState.WAITING_FOR_PLAYERS && session.getState() != MatchState.CREATED) {
                throw new AuthException("INVALID_STATE", "Cannot start match from state " + session.getState(), HttpStatus.CONFLICT);
            }

            if (session.getParticipantCount() < 2) {
                throw new AuthException("NOT_ENOUGH_PLAYERS", "Mini League requires at least 2 participants to start", HttpStatus.BAD_REQUEST);
            }

            if (session.getParticipantCount() > room.getMaxParticipants()) {
                throw new AuthException("TOO_MANY_PLAYERS", "Room participant count exceeds limit", HttpStatus.BAD_REQUEST);
            }

            if (!session.areAllParticipantsReady()) {
                throw new AuthException("NOT_ALL_READY", "All participants must be ready before starting", HttpStatus.BAD_REQUEST);
            }

            // Transition to READY and launch authoritative countdown
            session.transitionTo(MatchState.READY);
            matchSessionService.startCountdown(session);

            ReconnectionSnapshot snapshot = session.toSnapshot();

            eventDispatcher.dispatchToMatch(session.getMatchId(), MultiplayerEventEnvelope.create(
                    MultiplayerEventType.MINI_LEAGUE_MATCH_CREATED,
                    session.getMatchId(),
                    session.nextSequenceNumber(),
                    snapshot
            ));

            log.info("Host {} started Mini League match {} in room {}", hostPlayerId, session.getMatchId(), roomId);

            for (MatchParticipant p : session.getParticipants().values()) {
                if (!p.getPlayerId().equals(hostPlayerId)) {
                    try {
                        notificationService.createNotification(
                                p.getPlayerId(),
                                com.zynpath.backend.notification.model.NotificationEventType.MATCH_STARTING,
                                "Match Starting",
                                "Mini League room " + room.getRoomCode() + " is starting now! Get ready.",
                                session.getMatchId(),
                                "mini_league?roomCode=" + room.getRoomCode(),
                                null,
                                hostPlayerId
                        );
                    } catch (Exception e) {
                        log.warn("Failed to dispatch match starting notification: {}", e.getMessage());
                    }
                }
            }

            return snapshot;
        }
    }

    /**
     * Host invites an accepted friend to the room.
     */
    public MultiplayerDto.MiniLeagueInvitationDto inviteFriend(String roomId, String inviterPlayerId, String targetPublicZynpathId) {
        MiniLeagueRoom room = roomsById.get(roomId);
        if (room == null || room.isExpired()) {
            throw new AuthException("ROOM_NOT_FOUND", "Room not found: " + roomId, HttpStatus.NOT_FOUND);
        }

        MatchSession session = matchSessionService.getSession(room.getMatchId());
        if (session.getParticipantCount() >= room.getMaxParticipants()) {
            throw new AuthException("ROOM_FULL", "Cannot invite: room is at maximum capacity", HttpStatus.CONFLICT);
        }

        PlayerAccount inviter = playerAccountService.findById(inviterPlayerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Inviter account not found", HttpStatus.NOT_FOUND));
        PlayerAccount recipient = playerAccountService.findByPublicId(targetPublicZynpathId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Recipient account not found", HttpStatus.NOT_FOUND));

        if (inviter.playerId().equals(recipient.playerId())) {
            throw new AuthException("INVALID_TARGET", "Cannot invite yourself", HttpStatus.BAD_REQUEST);
        }

        if (!socialService.areFriends(inviter.playerId(), recipient.playerId())) {
            throw new AuthException("NOT_FRIENDS", "Mini League invitations require an accepted friendship", HttpStatus.FORBIDDEN);
        }

        if (socialService.isBlocked(inviter.playerId(), recipient.playerId()) || socialService.isBlocked(recipient.playerId(), inviter.playerId())) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot invite a blocked player", HttpStatus.FORBIDDEN);
        }

        if (session.hasParticipant(recipient.playerId())) {
            throw new AuthException("ALREADY_PARTICIPANT", "Player is already in this room", HttpStatus.CONFLICT);
        }

        String invitationId = "minv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        long now = System.currentTimeMillis();

        MiniLeagueInvitation invitation = new MiniLeagueInvitation(
                invitationId,
                roomId,
                room.getRoomCode(),
                room.getRoomName(),
                inviter.playerId(),
                inviter.publicZynpathId(),
                inviter.displayName(),
                recipient.playerId(),
                recipient.publicZynpathId(),
                recipient.displayName(),
                now,
                INVITATION_TTL_MS
        );

        invitationsById.put(invitationId, invitation);
        log.info("Sent Mini League invitation {} from {} to {} for room {}", invitationId, inviterPlayerId, recipient.playerId(), roomId);

        // Dispatch real-time WebSocket invitation to recipient
        eventDispatcher.dispatchToPlayer(recipient.playerId(), MultiplayerEventEnvelope.create(
                MultiplayerEventType.MINI_LEAGUE_INVITED,
                session.getMatchId(),
                1,
                toInvitationDto(invitation)
        ));

        try {
            notificationService.createNotification(
                    recipient.playerId(),
                    com.zynpath.backend.notification.model.NotificationEventType.MINI_LEAGUE_INVITATION,
                    "Mini League Invite",
                    inviter.displayName() + " invited you to Mini League room " + room.getRoomCode() + "!",
                    invitationId,
                    "mini_league?roomCode=" + room.getRoomCode(),
                    now + INVITATION_TTL_MS,
                    inviterPlayerId
            );
        } catch (Exception e) {
            log.warn("Failed to dispatch mini league invitation notification: {}", e.getMessage());
        }

        return toInvitationDto(invitation);
    }

    /**
     * Retrieve incoming pending invitations for caller.
     */
    public List<MultiplayerDto.MiniLeagueInvitationDto> getIncomingInvitations(String playerId) {
        List<MultiplayerDto.MiniLeagueInvitationDto> result = new ArrayList<>();
        for (MiniLeagueInvitation inv : invitationsById.values()) {
            if (inv.getRecipientPlayerId().equals(playerId) && inv.getStatus() == InvitationStatus.PENDING && !inv.isExpired()) {
                result.add(toInvitationDto(inv));
            }
        }
        return result;
    }

    /**
     * Respond to a Mini League invitation (Accept or Decline).
     */
    public MultiplayerDto.MiniLeagueRoomDto respondToInvitation(String invitationId, String recipientPlayerId, boolean accept) {
        MiniLeagueInvitation inv = invitationsById.get(invitationId);
        if (inv == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }

        if (!inv.getRecipientPlayerId().equals(recipientPlayerId)) {
            throw new AuthException("UNAUTHORIZED_INVITATION", "Only the recipient may respond to this invitation", HttpStatus.FORBIDDEN);
        }

        if (inv.isExpired()) {
            inv.setStatus(InvitationStatus.EXPIRED);
            throw new AuthException("INVITATION_EXPIRED", "Invitation has expired", HttpStatus.GONE);
        }

        if (inv.getStatus() != InvitationStatus.PENDING) {
            throw new AuthException("INVALID_INVITATION_STATE", "Invitation is already " + inv.getStatus(), HttpStatus.CONFLICT);
        }

        if (!accept) {
            inv.setStatus(InvitationStatus.DECLINED);
            log.info("Mini League invitation {} declined by {}", invitationId, recipientPlayerId);
            return null;
        }

        inv.setStatus(InvitationStatus.ACCEPTED);
        return joinRoom(inv.getRoomId(), recipientPlayerId);
    }

    /**
     * Cancel an outgoing invitation.
     */
    public MultiplayerDto.MiniLeagueInvitationDto cancelInvitation(String invitationId, String inviterPlayerId) {
        MiniLeagueInvitation inv = invitationsById.get(invitationId);
        if (inv == null) {
            throw new AuthException("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId, HttpStatus.NOT_FOUND);
        }

        if (!inv.getInviterPlayerId().equals(inviterPlayerId)) {
            throw new AuthException("UNAUTHORIZED_INVITATION", "Only the inviter may cancel this invitation", HttpStatus.FORBIDDEN);
        }

        inv.setStatus(InvitationStatus.CANCELLED);
        log.info("Mini League invitation {} cancelled by {}", invitationId, inviterPlayerId);
        return toInvitationDto(inv);
    }

    @Scheduled(fixedDelay = 30000)
    public void sweepExpiredRoomsAndInvitations() {
        long now = System.currentTimeMillis();
        for (MiniLeagueRoom room : roomsById.values()) {
            if (room.isExpired()) {
                roomsById.remove(room.getRoomId());
                roomsByCode.remove(room.getRoomCode());
            }
        }
        for (MiniLeagueInvitation inv : invitationsById.values()) {
            if (inv.getStatus() == InvitationStatus.PENDING && inv.isExpired()) {
                inv.setStatus(InvitationStatus.EXPIRED);
            }
        }
    }

    private String generateUniqueRoomCode() {
        for (int attempts = 0; attempts < 50; attempts++) {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_ALPHABET.charAt(random.nextInt(CODE_ALPHABET.length())));
            }
            String candidate = sb.toString();
            if (!roomsByCode.containsKey(candidate)) {
                return candidate;
            }
        }
        return "R" + UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
    }

    private MultiplayerDto.MiniLeagueRoomDto toDto(MiniLeagueRoom room, MatchSession session) {
        List<MultiplayerDto.MiniLeagueParticipantDto> pDtos = new ArrayList<>();
        for (MatchParticipant p : session.getParticipants().values()) {
            pDtos.add(new MultiplayerDto.MiniLeagueParticipantDto(
                    p.getPlayerId(),
                    p.getPublicZynpathId(),
                    p.getDisplayName(),
                    p.getAvatarId(),
                    p.getPlayerId().equals(room.getHostPlayerId()),
                    p.isReady(),
                    p.isConnected(),
                    p.getCoveredCells(),
                    p.getLastCheckpoint(),
                    p.getCompletedAt() != null,
                    p.getSolveTimeMs(),
                    p.getFinishOrder()
            ));
        }

        return new MultiplayerDto.MiniLeagueRoomDto(
                room.getRoomId(),
                room.getRoomCode(),
                room.getRoomName(),
                room.getHostPlayerId(),
                session.getState().name(),
                room.getMaxParticipants(),
                session.getParticipantCount(),
                pDtos,
                session.getMatchId(),
                room.getCreatedAt(),
                room.getExpiresAt()
        );
    }

    private MultiplayerDto.MiniLeagueInvitationDto toInvitationDto(MiniLeagueInvitation inv) {
        return new MultiplayerDto.MiniLeagueInvitationDto(
                inv.getInvitationId(),
                inv.getRoomId(),
                inv.getRoomCode(),
                inv.getRoomName(),
                inv.getInviterPlayerId(),
                inv.getInviterPublicId(),
                inv.getInviterDisplayName(),
                inv.getRecipientPlayerId(),
                inv.getRecipientPublicId(),
                inv.getRecipientDisplayName(),
                inv.getStatus().name(),
                inv.getCreatedAt(),
                inv.getExpiresAt()
        );
    }
}
