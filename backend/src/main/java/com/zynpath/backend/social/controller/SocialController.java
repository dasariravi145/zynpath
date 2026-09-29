package com.zynpath.backend.social.controller;

import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.social.model.BlockedPlayerSummary;
import com.zynpath.backend.social.model.FriendRelationship;
import com.zynpath.backend.social.model.FriendRequest;
import com.zynpath.backend.social.model.FriendRequestSummary;
import com.zynpath.backend.social.model.FriendSummary;
import com.zynpath.backend.social.model.MultiplayerInvitation;
import com.zynpath.backend.social.model.PlayerPresenceState;
import com.zynpath.backend.social.model.PublicPlayerProfile;
import com.zynpath.backend.social.model.SocialDto;
import com.zynpath.backend.social.service.PresenceService;
import com.zynpath.backend.social.service.SocialService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zynpath.backend.security.abuse.SocialAbuseGuard;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.authorization.ResourceAuthorizationService;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for Zynpath social foundation: player discovery, friend relationships,
 * presence heartbeats, and future multiplayer room invites.
 *
 * Implements Prompt 19 Sections 5-23, 29-41 and Prompt 36 Sections 18, 20, 21, 50:
 * - Derives acting player identity strictly from validated session token.
 * - Enforces privacy: exact-ID lookup only, no bulk user scraping.
 * - Enforces anti-abuse guards on friend requests (caps, blocks, self-request prevention).
 * - Manages friend requests with transactional uniqueness & relationship validation.
 * - Provides ephemeral presence heartbeats and friend presence resolution.
 */
@RestController
@RequestMapping("/api/v1/social")
public class SocialController {

    private static final Logger log = LoggerFactory.getLogger(SocialController.class);

    private final SocialService socialService;
    private final PresenceService presenceService;
    private final SessionSecurityService sessionSecurityService;
    private final SocialAbuseGuard socialAbuseGuard;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final SecurityAuditLogger auditLogger;

    public SocialController(
            SocialService socialService,
            PresenceService presenceService,
            SessionSecurityService sessionSecurityService,
            SocialAbuseGuard socialAbuseGuard,
            ResourceAuthorizationService resourceAuthorizationService,
            SecurityAuditLogger auditLogger
    ) {
        this.socialService = socialService;
        this.presenceService = presenceService;
        this.sessionSecurityService = sessionSecurityService;
        this.socialAbuseGuard = socialAbuseGuard;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.auditLogger = auditLogger;
    }

    /**
     * Resolves the current player's own public profile.
     */
    @GetMapping("/me")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PublicPlayerProfile> getMyPublicProfile(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(socialService.getMyPublicProfile(playerId));
    }

    /**
     * Privacy-preserving exact Public Zynpath ID search.
     * Prevents account enumeration; returns minimal public info.
     */
    @GetMapping("/players/search")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PublicPlayerProfile> searchPlayer(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("publicId") String publicZynpathId
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(socialService.searchPlayer(playerId, publicZynpathId));
    }

    /**
     * Lists current accepted friends for the authenticated player.
     */
    @GetMapping("/friends")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<FriendSummary>> getFriends(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(socialService.listFriends(playerId));
    }

    /**
     * Lists incoming pending friend requests awaiting response.
     */
    @GetMapping("/friends/requests/incoming")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<FriendRequestSummary>> getIncomingRequests(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(socialService.listIncomingRequests(playerId));
    }

    /**
     * Lists outgoing pending friend requests sent by the authenticated player.
     */
    @GetMapping("/friends/requests/outgoing")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<FriendRequestSummary>> getOutgoingRequests(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        return ResponseEntity.ok(socialService.listOutgoingRequests(playerId));
    }

    /**
     * Sends a friend request to a target player via their Public Zynpath ID.
     */
    @PostMapping("/friends/requests")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.INVITATIONS_AND_ROOMS)
    public ResponseEntity<FriendRequest> sendFriendRequest(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SocialDto.SendFriendRequestPayload dto
    ) {
        String playerId = authenticate(authHeader);
        socialAbuseGuard.assertFriendRequestPermitted(playerId, dto.targetPublicZynpathId());

        FriendRequest request = socialService.sendFriendRequest(playerId, dto.targetPublicZynpathId());
        auditLogger.logEvent(SecurityEventType.FRIEND_REQUEST_SENT, playerId,
                Map.of("targetPublicId", dto.targetPublicZynpathId(), "requestId", request.requestId()));
        return ResponseEntity.ok(request);
    }

    /**
     * Accepts a pending incoming friend request.
     */
    @PostMapping("/friends/requests/{requestId}/accept")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<FriendRelationship> acceptFriendRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("requestId") String requestId
    ) {
        String playerId = authenticate(authHeader);
        FriendRelationship rel = socialService.acceptFriendRequest(playerId, requestId);
        auditLogger.logEvent(SecurityEventType.FRIEND_REQUEST_ACCEPTED, playerId,
                Map.of("requestId", requestId, "otherPlayerId", rel.getOtherPlayerId(playerId)));
        return ResponseEntity.ok(rel);
    }

    /**
     * Rejects a pending incoming friend request.
     */
    @PostMapping("/friends/requests/{requestId}/reject")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> rejectFriendRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("requestId") String requestId
    ) {
        String playerId = authenticate(authHeader);
        socialService.rejectFriendRequest(playerId, requestId);
        return ResponseEntity.ok(Map.of("status", "REJECTED"));
    }

    /**
     * Cancels an outgoing pending friend request.
     */
    @PostMapping("/friends/requests/{requestId}/cancel")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> cancelFriendRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("requestId") String requestId
    ) {
        String playerId = authenticate(authHeader);
        socialService.cancelFriendRequest(playerId, requestId);
        return ResponseEntity.ok(Map.of("status", "CANCELLED"));
    }

    /**
     * Removes an established mutual friendship.
     */
    @DeleteMapping("/friends/{friendPlayerId}")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> removeFriend(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("friendPlayerId") String friendPlayerId
    ) {
        String playerId = authenticate(authHeader);
        socialService.removeFriend(playerId, friendPlayerId);
        return ResponseEntity.ok(Map.of("status", "REMOVED"));
    }

    /**
     * Lists all players currently blocked by the calling player.
     */
    @GetMapping("/blocks")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<BlockedPlayerSummary>> listBlockedPlayers(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        List<BlockedPlayerSummary> blocked = socialService.listBlockedPlayers(playerId);
        return ResponseEntity.ok(blocked);
    }

    /**
     * Directionally blocks a player from future social interactions.
     */
    @PostMapping("/blocks/{targetPlayerId}")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> blockPlayer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("targetPlayerId") String targetPlayerId
    ) {
        String playerId = authenticate(authHeader);
        socialService.blockPlayer(playerId, targetPlayerId);
        auditLogger.logEvent(SecurityEventType.PLAYER_BLOCKED, playerId, Map.of("blockedTargetId", targetPlayerId));
        return ResponseEntity.ok(Map.of("status", "BLOCKED"));
    }

    /**
     * Unblocks a previously blocked player.
     */
    @DeleteMapping("/blocks/{targetPlayerId}")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> unblockPlayer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("targetPlayerId") String targetPlayerId
    ) {
        String playerId = authenticate(authHeader);
        socialService.unblockPlayer(playerId, targetPlayerId);
        return ResponseEntity.ok(Map.of("status", "UNBLOCKED"));
    }

    /**
     * Reports client application foreground/presence heartbeat.
     */
    @PostMapping("/presence/heartbeat")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, Object>> heartbeat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) SocialDto.HeartbeatDto dto
    ) {
        String playerId = authenticate(authHeader);
        PlayerPresenceState state = (dto != null && dto.state() != null)
                ? dto.state()
                : PlayerPresenceState.ONLINE;

        presenceService.recordHeartbeat(playerId, state);
        return ResponseEntity.ok(Map.of(
                "playerId", playerId,
                "recordedState", state,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Returns the current presence states for all accepted friends of the calling player.
     */
    @GetMapping("/presence")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<SocialDto.FriendPresenceDto>> getFriendsPresence(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        List<FriendSummary> friends = socialService.listFriends(playerId);

        List<SocialDto.FriendPresenceDto> list = friends.stream()
                .map(friend -> new SocialDto.FriendPresenceDto(
                        friend.playerId(),
                        friend.publicZynpathId(),
                        presenceService.getPresence(friend.playerId())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    /**
     * Creates a future multiplayer invitation for an accepted friend.
     */
    @PostMapping("/invitations/multiplayer")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.INVITATIONS_AND_ROOMS)
    public ResponseEntity<MultiplayerInvitation> createMultiplayerInvitation(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SocialDto.SendMultiplayerInvitePayload dto
    ) {
        String playerId = authenticate(authHeader);
        MultiplayerInvitation invite = socialService.sendMultiplayerInvite(playerId, dto);
        auditLogger.logEvent(SecurityEventType.INVITATION_SENT, playerId,
                Map.of("invitationId", invite.invitationId(), "gameMode", dto.gameMode().name()));
        return ResponseEntity.ok(invite);
    }

    /**
     * Helper to authenticate the bearer token and return the trusted playerId.
     */
    private String authenticate(String authHeader) {
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        return session.playerId();
    }
}
