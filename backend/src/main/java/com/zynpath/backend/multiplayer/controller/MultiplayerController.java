package com.zynpath.backend.multiplayer.controller;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.multiplayer.model.MatchResult;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.multiplayer.service.MatchmakingService;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zynpath.backend.multiplayer.model.CompetitiveDto;
import com.zynpath.backend.multiplayer.model.FriendsArenaRoom;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.service.CompetitiveService;
import org.springframework.web.bind.annotation.RequestParam;

import com.zynpath.backend.auth.service.SessionSecurityService;
import java.util.List;
import java.util.Map;

import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.authorization.ResourceAuthorizationService;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.security.integrity.CompetitiveIntegrityGuard;
import com.zynpath.backend.security.model.EndpointAccessTier;

/**
 * REST Controller for Zynpath Online Multiplayer Foundation.
 *
 * Implements Prompt 20 Sections 13, 14, 15, 16, 17, 24, 32, 35, 49 & Prompt 36 Section 7, 8, 17, 18, 22, 23, 29-41, 54:
 * - Session-authenticated endpoints for matchmaking queues and session lifecycle.
 * - Quick Duel enqueue, cancel, and status polling.
 * - Friend Duel and Mini League session creation with membership authorization.
 * - Authoritative state snapshot and solution validation claims.
 * - Reconnection snapshot retrieval.
 * - Competitive match history, player statistics, and bounded leaderboards (Prompt 24).
 */
@RestController
@RequestMapping("/api/v1/multiplayer")
@RequireAccess(EndpointAccessTier.AUTHENTICATED)
public class MultiplayerController {

    private static final Logger log = LoggerFactory.getLogger(MultiplayerController.class);

    private final MatchmakingService matchmakingService;
    private final MatchSessionService matchSessionService;
    private final SessionSecurityService sessionSecurityService;
    private final com.zynpath.backend.multiplayer.service.FriendDuelService friendDuelService;
    private final com.zynpath.backend.multiplayer.service.MiniLeagueService miniLeagueService;
    private final com.zynpath.backend.multiplayer.service.FriendsArenaService friendsArenaService;
    private final CompetitiveService competitiveService;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final CompetitiveIntegrityGuard competitiveIntegrityGuard;

    public MultiplayerController(
            MatchmakingService matchmakingService,
            MatchSessionService matchSessionService,
            SessionSecurityService sessionSecurityService,
            com.zynpath.backend.multiplayer.service.FriendDuelService friendDuelService,
            com.zynpath.backend.multiplayer.service.MiniLeagueService miniLeagueService,
            com.zynpath.backend.multiplayer.service.FriendsArenaService friendsArenaService,
            CompetitiveService competitiveService,
            ResourceAuthorizationService resourceAuthorizationService,
            CompetitiveIntegrityGuard competitiveIntegrityGuard
    ) {
        this.matchmakingService = matchmakingService;
        this.matchSessionService = matchSessionService;
        this.sessionSecurityService = sessionSecurityService;
        this.friendDuelService = friendDuelService;
        this.miniLeagueService = miniLeagueService;
        this.friendsArenaService = friendsArenaService;
        this.competitiveService = competitiveService;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.competitiveIntegrityGuard = competitiveIntegrityGuard;
    }

    /**
     * Enqueue into Quick Duel matchmaking.
     */
    @PostMapping("/matchmaking/quick-duel/enqueue")
    public ResponseEntity<MultiplayerDto.MatchmakingStatusResponse> enqueueQuickDuel(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.MatchmakingStatusResponse response = matchmakingService.enqueueQuickDuel(playerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel Quick Duel matchmaking queue ticket.
     */
    @PostMapping("/matchmaking/quick-duel/cancel")
    public ResponseEntity<Map<String, Object>> cancelQuickDuel(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        boolean cancelled = matchmakingService.cancelTicket(playerId);
        return ResponseEntity.ok(Map.of("cancelled", cancelled));
    }

    /**
     * Get Quick Duel matchmaking ticket status.
     */
    @GetMapping("/matchmaking/quick-duel/status")
    public ResponseEntity<MultiplayerDto.MatchmakingStatusResponse> getQuickDuelStatus(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.MatchmakingStatusResponse response = matchmakingService.getTicketStatus(playerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Create an invited 1v1 Friend Duel session.
     */
    @PostMapping("/matches/friend-duel")
    public ResponseEntity<ReconnectionSnapshot> createFriendDuel(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.CreateFriendDuelRequest request
    ) {
        String playerId = authenticate(authHeader);
        MatchSession session = matchmakingService.createFriendDuel(playerId, request.targetPublicZynpathId());
        return ResponseEntity.status(HttpStatus.CREATED).body(session.toSnapshot());
    }

    /**
     * Create a 2-5 participant Mini League session.
     */
    @PostMapping("/matches/mini-league")
    public ResponseEntity<ReconnectionSnapshot> createMiniLeague(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.CreateMiniLeagueRequest request
    ) {
        String playerId = authenticate(authHeader);
        MatchSession session = matchmakingService.createMiniLeague(playerId, request.roomName(), request.maxParticipants());
        return ResponseEntity.status(HttpStatus.CREATED).body(session.toSnapshot());
    }

    /**
     * Get authoritative match snapshot.
     */
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<ReconnectionSnapshot> getMatchSnapshot(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        MatchSession session = matchSessionService.getSession(matchId);

        if (session.getParticipant(playerId) == null) {
            throw new AuthException("NOT_A_PARTICIPANT", "Player does not belong to this match", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(session.toSnapshot());
    }

    /**
     * Mark participant ready.
     */
    @PostMapping("/matches/{matchId}/ready")
    public ResponseEntity<MultiplayerDto.MatchActionResponse> markReady(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        resourceAuthorizationService.assertMatchParticipant(matchId, playerId);
        matchSessionService.markPlayerReady(matchId, playerId);
        return ResponseEntity.ok(new MultiplayerDto.MatchActionResponse(matchId, "READY", "Player is ready"));
    }

    /**
     * Authoritatively validate and submit a solution claim.
     */
    @PostMapping("/matches/{matchId}/claim")
    public ResponseEntity<ValidationOutcome> submitClaim(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("matchId") String matchId,
            @Valid @RequestBody MultiplayerDto.SubmitSolutionClaimRequest request
    ) {
        String playerId = authenticate(authHeader);
        resourceAuthorizationService.assertMatchParticipant(matchId, playerId);
        MatchSession session = matchSessionService.getSession(matchId);
        competitiveIntegrityGuard.assertMatchAcceptingClaims(session, matchId, playerId);
        competitiveIntegrityGuard.assertNoHintsAllowed(session.getGameMode(), 0);

        ValidationOutcome outcome = matchSessionService.submitCompletionClaim(matchId, playerId, request);
        return ResponseEntity.ok(outcome);
    }

    /**
     * Reconnect to an ongoing match session.
     */
    @PostMapping("/matches/{matchId}/reconnect")
    public ResponseEntity<ReconnectionSnapshot> reconnect(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        resourceAuthorizationService.assertMatchParticipant(matchId, playerId);
        ReconnectionSnapshot snapshot = matchSessionService.reconnectPlayer(matchId, playerId);
        return ResponseEntity.ok(snapshot);
    }

    /**
     * Forfeit / abandon an active match explicitly (Prompt 21 Section 35).
     */
    @PostMapping("/matches/{matchId}/forfeit")
    public ResponseEntity<MultiplayerDto.MatchActionResponse> forfeit(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        resourceAuthorizationService.assertMatchParticipant(matchId, playerId);
        matchSessionService.forfeitMatch(matchId, playerId);
        return ResponseEntity.ok(new MultiplayerDto.MatchActionResponse(matchId, "FORFEITED", "Match forfeited"));
    }

    /**
     * Retrieve authoritative final match results (Prompt 21 Section 42 & 46).
     */
    @GetMapping("/matches/{matchId}/results")
    public ResponseEntity<List<MatchResult>> getMatchResults(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        resourceAuthorizationService.assertMatchParticipant(matchId, playerId);
        MatchSession session = matchSessionService.getSession(matchId);
        return ResponseEntity.ok(session.getResults());
    }

    /**
     * Send a Friend Duel invitation to an accepted friend (Prompt 22 Sections 11-14).
     */
    @PostMapping("/invitations")
    public ResponseEntity<MultiplayerDto.FriendDuelInvitationDto> sendInvitation(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.FriendDuelInvitationRequest request
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.FriendDuelInvitationDto dto = friendDuelService.sendInvitation(playerId, request.targetPublicZynpathId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Retrieve all active incoming invitations for the authenticated player.
     */
    @GetMapping("/invitations/incoming")
    public ResponseEntity<List<MultiplayerDto.FriendDuelInvitationDto>> getIncomingInvitations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendDuelService.getIncomingInvitations(playerId));
    }

    /**
     * Retrieve all active outgoing invitations sent by the authenticated player.
     */
    @GetMapping("/invitations/outgoing")
    public ResponseEntity<List<MultiplayerDto.FriendDuelInvitationDto>> getOutgoingInvitations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendDuelService.getOutgoingInvitations(playerId));
    }

    /**
     * Retrieve status of a specific invitation.
     */
    @GetMapping("/invitations/{invitationId}")
    public ResponseEntity<MultiplayerDto.FriendDuelInvitationDto> getInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendDuelService.getInvitation(invitationId, playerId));
    }

    /**
     * Accept a Friend Duel invitation (Prompt 22 Section 19).
     */
    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<ReconnectionSnapshot> acceptInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId
    ) {
        String playerId = authenticate(authHeader);
        ReconnectionSnapshot snapshot = friendDuelService.acceptInvitation(invitationId, playerId);
        return ResponseEntity.ok(snapshot);
    }

    /**
     * Decline a Friend Duel invitation (Prompt 22 Section 20).
     */
    @PostMapping("/invitations/{invitationId}/decline")
    public ResponseEntity<MultiplayerDto.FriendDuelInvitationDto> declineInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.FriendDuelInvitationDto dto = friendDuelService.declineInvitation(invitationId, playerId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Cancel an outgoing Friend Duel invitation (Prompt 22 Section 21).
     */
    @PostMapping("/invitations/{invitationId}/cancel")
    public ResponseEntity<MultiplayerDto.FriendDuelInvitationDto> cancelInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.FriendDuelInvitationDto dto = friendDuelService.cancelInvitation(invitationId, playerId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Request a rematch for a completed match (Prompt 22 Section 38).
     */
    @PostMapping("/matches/{matchId}/rematch")
    public ResponseEntity<MultiplayerDto.RematchStatusDto> requestRematch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.RematchStatusDto status = friendDuelService.requestRematch(matchId, playerId);
        return ResponseEntity.ok(status);
    }

    /**
     * Respond to a rematch request (Prompt 22 Sections 40 & 42).
     */
    @PostMapping("/matches/{matchId}/rematch/respond")
    public ResponseEntity<MultiplayerDto.RematchStatusDto> respondToRematch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId,
            @Valid @RequestBody MultiplayerDto.RematchResponseRequest request
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerDto.RematchStatusDto status = friendDuelService.respondToRematch(matchId, playerId, request.accept());
        return ResponseEntity.ok(status);
    }

    /**
     * Retrieve status of rematch request for a match.
     */
    @GetMapping("/matches/{matchId}/rematch")
    public ResponseEntity<MultiplayerDto.RematchStatusDto> getRematchStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendDuelService.getRematchStatus(matchId, playerId));
    }

    // --- Mini League Private Rooms & Lobbies (Prompt 23) ---

    @PostMapping("/mini-league/rooms")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> createMiniLeagueRoom(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.CreateMiniLeagueRequest request
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(miniLeagueService.createRoom(playerId, request.roomName(), request.maxParticipants()));
    }

    @GetMapping("/mini-league/rooms/{roomId}")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> getMiniLeagueRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.getRoom(roomId, playerId));
    }

    @GetMapping("/mini-league/rooms/code/{roomCode}")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> getMiniLeagueRoomByCode(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomCode") String roomCode
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.getRoomByCode(roomCode, playerId));
    }

    @PostMapping("/mini-league/rooms/join")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> joinMiniLeagueRoom(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.JoinRoomRequest request
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.joinRoomByCode(request.roomCode(), playerId));
    }

    @PostMapping("/mini-league/rooms/{roomId}/leave")
    public ResponseEntity<Map<String, String>> leaveMiniLeagueRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        miniLeagueService.leaveRoom(roomId, playerId);
        return ResponseEntity.ok(Map.of("roomId", roomId, "status", "LEFT"));
    }

    @PostMapping("/mini-league/rooms/{roomId}/ready")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> setMiniLeagueReady(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId,
            @RequestBody MultiplayerDto.UpdateReadyRequest request
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.setReady(roomId, playerId, request.ready()));
    }

    @PostMapping("/mini-league/rooms/{roomId}/start")
    public ResponseEntity<ReconnectionSnapshot> startMiniLeagueMatch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.startMatch(roomId, playerId));
    }

    @PostMapping("/mini-league/rooms/{roomId}/invite")
    public ResponseEntity<MultiplayerDto.MiniLeagueInvitationDto> inviteToMiniLeague(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId,
            @Valid @RequestBody MultiplayerDto.MiniLeagueInviteRequest request
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.inviteFriend(roomId, playerId, request.targetPublicZynpathId()));
    }

    @GetMapping("/mini-league/invitations/incoming")
    public ResponseEntity<List<MultiplayerDto.MiniLeagueInvitationDto>> getMiniLeagueIncomingInvitations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.getIncomingInvitations(playerId));
    }

    @PostMapping("/mini-league/invitations/{invitationId}/respond")
    public ResponseEntity<MultiplayerDto.MiniLeagueRoomDto> respondToMiniLeagueInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId,
            @RequestBody Map<String, Boolean> body
    ) {
        String playerId = authenticate(authHeader);
        boolean accept = Boolean.TRUE.equals(body.get("accept"));
        MultiplayerDto.MiniLeagueRoomDto room = miniLeagueService.respondToInvitation(invitationId, playerId, accept);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/mini-league/invitations/{invitationId}/cancel")
    public ResponseEntity<MultiplayerDto.MiniLeagueInvitationDto> cancelMiniLeagueInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("invitationId") String invitationId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(miniLeagueService.cancelInvitation(invitationId, playerId));
    }

    // --- Friends Arena Private Rooms & Lobbies (Prompt 18) ---

    @PostMapping("/friends-arena/rooms")
    public ResponseEntity<MultiplayerDto.FriendsArenaRoomDto> createFriendsArenaRoom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) MultiplayerDto.CreateFriendsArenaRoomRequest request
    ) {
        String playerId = authenticate(authHeader);
        String idempotencyKey = request != null ? request.idempotencyKey() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendsArenaService.createRoom(playerId, idempotencyKey));
    }

    @GetMapping("/friends-arena/rooms/{roomId}")
    public ResponseEntity<MultiplayerDto.FriendsArenaRoomDto> getFriendsArenaRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.getRoom(roomId, playerId));
    }

    @GetMapping("/friends-arena/rooms/code/{roomCode}")
    public ResponseEntity<MultiplayerDto.FriendsArenaRoomDto> getFriendsArenaRoomByCode(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomCode") String roomCode
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.getRoomByCode(roomCode, playerId));
    }

    @PostMapping("/friends-arena/rooms/join")
    public ResponseEntity<MultiplayerDto.FriendsArenaRoomDto> joinFriendsArenaRoom(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MultiplayerDto.JoinFriendsArenaRoomRequest request
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.joinRoomByCode(request.roomCode(), playerId));
    }

    @PostMapping("/friends-arena/rooms/{roomId}/leave")
    public ResponseEntity<Map<String, String>> leaveFriendsArenaRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        friendsArenaService.leaveRoom(roomId, playerId);
        return ResponseEntity.ok(Map.of("roomId", roomId, "status", "LEFT"));
    }

    @PostMapping("/friends-arena/rooms/{roomId}/start")
    public ResponseEntity<MultiplayerDto.FriendsArenaRoomDto> startFriendsArenaMatch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.startMatch(roomId, playerId));
    }

    @GetMapping("/friends-arena/matches/{matchId}")
    public ResponseEntity<MultiplayerDto.FriendsArenaMatchDto> getFriendsArenaMatch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.getMatch(matchId, playerId));
    }

    @GetMapping("/friends-arena/rooms/{roomId}/match")
    public ResponseEntity<MultiplayerDto.FriendsArenaMatchDto> getFriendsArenaMatchByRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(friendsArenaService.getMatchByRoom(roomId, playerId));
    }

    @PostMapping("/friends-arena/matches/{matchId}/progress")
    public ResponseEntity<Void> updateFriendsArenaProgress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId,
            @RequestBody MultiplayerDto.FriendsArenaProgressRequest request
    ) {
        String playerId = authenticate(authHeader);
        friendsArenaService.updatePlayerProgress(matchId, playerId, request.coveredCells(), request.lastCheckpoint());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/friends-arena/matches/{matchId}/claim")
    public ResponseEntity<ValidationOutcome> submitFriendsArenaSolutionClaim(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId,
            @Valid @RequestBody MultiplayerDto.FriendsArenaClaimRequest request
    ) {
        String playerId = authenticate(authHeader);
        ValidationOutcome outcome = friendsArenaService.submitSolutionClaim(
                matchId,
                playerId,
                request.pathCoordinates(),
                request.clientReportedSolveTimeMs()
        );
        return ResponseEntity.ok(outcome);
    }

    @PostMapping("/friends-arena/matches/{matchId}/forfeit")
    public ResponseEntity<Map<String, String>> forfeitFriendsArenaMatch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        friendsArenaService.forfeitMatch(matchId, playerId);
        return ResponseEntity.ok(Map.of("matchId", matchId, "status", "FORFEITED"));
    }

    @PostMapping("/friends-arena/rooms/{roomId}/rematch")
    public ResponseEntity<MultiplayerDto.FriendsArenaMatchDto> requestFriendsArenaRematch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId
    ) {
        String playerId = authenticate(authHeader);
        FriendsArenaRoom room = friendsArenaService.getRoomEntity(roomId);
        if (room != null && playerId.equals(room.getHostPlayerId())) {
            return ResponseEntity.ok(friendsArenaService.requestRematch(roomId, playerId));
        } else {
            friendsArenaService.notifyRematchRequested(roomId, playerId);
            return ResponseEntity.ok(friendsArenaService.getMatchByRoom(roomId, playerId));
        }
    }

    // --- Competitive Progression, History, Statistics & Leaderboards (Prompt 24) ---

    /**
     * Retrieve paginated match history for authenticated player (Prompt 24 Sections 7-15).
     */
    @GetMapping("/history")
    public ResponseEntity<CompetitiveDto.MatchHistoryResponse> getMatchHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "mode", required = false) String modeStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        String playerId = authenticate(authHeader);
        GameMode mode = null;
        if (modeStr != null && !modeStr.isBlank() && !"ALL".equalsIgnoreCase(modeStr)) {
            try {
                mode = GameMode.valueOf(modeStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Return all if unrecognized
            }
        }
        return ResponseEntity.ok(competitiveService.getMatchHistory(playerId, mode, page, pageSize));
    }

    /**
     * Retrieve authorized match details (Prompt 24 Sections 14, 16).
     */
    @GetMapping("/history/{matchId}")
    public ResponseEntity<CompetitiveDto.MatchDetailsDto> getMatchDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("matchId") String matchId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(competitiveService.getMatchDetails(matchId, playerId));
    }

    /**
     * Retrieve authenticated player's authoritative competitive statistics (Prompt 24 Sections 17-22).
     */
    @GetMapping("/stats")
    public ResponseEntity<CompetitiveDto.CompetitiveStatsDto> getCompetitiveStats(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(competitiveService.getCompetitiveStats(playerId));
    }

    /**
     * Retrieve public competitive profile statistics for a friend or other player (Prompt 24 Section 37).
     */
    @GetMapping("/stats/{publicZynpathId}")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    public ResponseEntity<CompetitiveDto.PublicCompetitiveStatsDto> getPublicCompetitiveStats(
            @PathVariable("publicZynpathId") String publicZynpathId
    ) {
        return ResponseEntity.ok(competitiveService.getPublicCompetitiveStats(publicZynpathId));
    }

    /**
     * Retrieve available leaderboard categories (Prompt 24 Section 24).
     */
    @GetMapping("/leaderboard/categories")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    public ResponseEntity<List<CompetitiveDto.LeaderboardCategoryInfoDto>> getLeaderboardCategories() {
        return ResponseEntity.ok(competitiveService.getLeaderboardCategories());
    }

    /**
     * Retrieve server-authoritative leaderboard rankings (Prompt 24 Sections 23-34).
     */
    @GetMapping("/leaderboard")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    public ResponseEntity<CompetitiveDto.LeaderboardResponse> getLeaderboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "category", defaultValue = "QUICK_DUEL_WINS") String categoryStr,
            @RequestParam(value = "period", defaultValue = "ALL_TIME") String periodStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(0, page);
        int safePageSize = Math.min(50, Math.max(1, pageSize)); // Bounded pagination security (Prompt 36 Section 54)

        String playerId = null;
        if (authHeader != null && !authHeader.isBlank()) {
            try {
                playerId = authenticate(authHeader);
            } catch (Exception ignored) {
                // Anonymous or guest leaderboard view allowed
            }
        }

        CompetitiveDto.LeaderboardCategory category;
        try {
            category = CompetitiveDto.LeaderboardCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            category = CompetitiveDto.LeaderboardCategory.QUICK_DUEL_WINS;
        }

        CompetitiveDto.LeaderboardPeriod period;
        try {
            period = CompetitiveDto.LeaderboardPeriod.valueOf(periodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            period = CompetitiveDto.LeaderboardPeriod.ALL_TIME;
        }

        return ResponseEntity.ok(competitiveService.getLeaderboard(category, period, safePage, safePageSize, playerId));
    }

    /**
     * Validates bearer authentication and returns the internal player ID.
     */
    private String authenticate(String authHeader) {
        if (SecurityContext.isAuthenticated()) {
            return SecurityContext.requirePlayerId();
        }
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        return session.playerId();
    }
}
