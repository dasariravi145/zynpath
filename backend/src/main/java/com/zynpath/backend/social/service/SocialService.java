package com.zynpath.backend.social.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.social.model.BlockedPlayerSummary;
import com.zynpath.backend.social.model.FriendRelationship;
import com.zynpath.backend.social.model.FriendRelationshipStatus;
import com.zynpath.backend.social.model.FriendRequest;
import com.zynpath.backend.social.model.FriendRequestStatus;
import com.zynpath.backend.social.model.FriendRequestSummary;
import com.zynpath.backend.social.model.FriendSummary;
import com.zynpath.backend.social.model.InvitationStatus;
import com.zynpath.backend.social.model.MultiplayerInvitation;
import com.zynpath.backend.social.model.PlayerBlock;
import com.zynpath.backend.social.model.PlayerPresenceState;
import com.zynpath.backend.social.model.PublicPlayerProfile;
import com.zynpath.backend.social.model.SocialDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing social relationships, player discovery, friend requests, and multiplayer invites.
 *
 * Implements Prompt 19 Sections 7-28 & 36-41.
 */
@Service
public class SocialService {

    private static final Logger log = LoggerFactory.getLogger(SocialService.class);

    private final PlayerAccountService playerAccountService;
    private final PresenceService presenceService;
    private final com.zynpath.backend.notification.service.NotificationService notificationService;

    // In-memory repositories (thread-safe, ready for JPA migration)
    private final Map<String, FriendRelationship> relationshipsByKey = new ConcurrentHashMap<>();
    private final Map<String, FriendRequest> requestsById = new ConcurrentHashMap<>();
    private final Map<String, PlayerBlock> blocksByKey = new ConcurrentHashMap<>();
    private final Map<String, MultiplayerInvitation> invitationsById = new ConcurrentHashMap<>();

    public SocialService(
            PlayerAccountService playerAccountService,
            PresenceService presenceService,
            @org.springframework.context.annotation.Lazy com.zynpath.backend.notification.service.NotificationService notificationService
    ) {
        this.playerAccountService = playerAccountService;
        this.presenceService = presenceService;
        this.notificationService = notificationService;
    }

    /**
     * Resolves the calling player's own safe public profile.
     */
    public PublicPlayerProfile getMyPublicProfile(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));
        PlayerPresenceState presence = presenceService.getPresence(playerId);
        return new PublicPlayerProfile(
                account.publicZynpathId(),
                account.displayName(),
                "avatar_compass",
                FriendRelationshipStatus.SELF,
                presence
        );
    }

    /**
     * Exact Public Zynpath ID player discovery.
     * Returns minimal safe public profile, omitting private identifiers.
     */
    public PublicPlayerProfile searchPlayer(String viewerPlayerId, String queryPublicId) {
        if (queryPublicId == null || queryPublicId.isBlank()) {
            throw new AuthException("INVALID_QUERY", "Public Zynpath ID is required");
        }

        String normalizedId = queryPublicId.trim().toUpperCase();
        PlayerAccount targetAccount = playerAccountService.findByPublicId(normalizedId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "No player found with Zynpath ID: " + normalizedId, HttpStatus.NOT_FOUND));

        FriendRelationshipStatus relStatus = determineRelationship(viewerPlayerId, targetAccount.playerId());

        // Privacy rule: Presence is only exposed to self and accepted friends
        PlayerPresenceState presence = (relStatus == FriendRelationshipStatus.SELF || relStatus == FriendRelationshipStatus.FRIENDS)
                ? presenceService.getPresence(targetAccount.playerId())
                : PlayerPresenceState.UNKNOWN;

        return new PublicPlayerProfile(
                targetAccount.publicZynpathId(),
                targetAccount.displayName(),
                "avatar_compass",
                relStatus,
                presence
        );
    }

    /**
     * Sends a friend request to a target player identified by their Public Zynpath ID.
     */
    public FriendRequest sendFriendRequest(String senderPlayerId, String targetPublicZynpathId) {
        if (targetPublicZynpathId == null || targetPublicZynpathId.isBlank()) {
            throw new AuthException("INVALID_QUERY", "Target public Zynpath ID is required");
        }

        String normalizedId = targetPublicZynpathId.trim().toUpperCase();
        PlayerAccount targetAccount = playerAccountService.findByPublicId(normalizedId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player not found: " + normalizedId, HttpStatus.NOT_FOUND));

        String recipientPlayerId = targetAccount.playerId();

        // 1. Prevent self-requests
        if (senderPlayerId.equals(recipientPlayerId)) {
            throw new AuthException("SELF_REQUEST", "You cannot send a friend request to yourself");
        }

        // 2. Check blocks
        if (isBlocked(senderPlayerId, recipientPlayerId) || isBlocked(recipientPlayerId, senderPlayerId)) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot send request to this player", HttpStatus.FORBIDDEN);
        }

        // 3. Check already friends
        if (areFriends(senderPlayerId, recipientPlayerId)) {
            throw new AuthException("ALREADY_FRIENDS", "You are already friends with this player");
        }

        // 4. Check duplicate active requests in either direction
        synchronized (this) {
            for (FriendRequest existing : requestsById.values()) {
                if (existing.status() == FriendRequestStatus.PENDING) {
                    if (existing.senderPlayerId().equals(senderPlayerId) && existing.recipientPlayerId().equals(recipientPlayerId)) {
                        throw new AuthException("REQUEST_ALREADY_PENDING", "You already have a pending request to this player");
                    }
                    if (existing.senderPlayerId().equals(recipientPlayerId) && existing.recipientPlayerId().equals(senderPlayerId)) {
                        throw new AuthException("REQUEST_ALREADY_PENDING", "This player has already sent you a request. Accept their request instead!");
                    }
                }
            }

            String requestId = "freq_" + UUID.randomUUID().toString().substring(0, 12);
            long now = System.currentTimeMillis();
            FriendRequest request = new FriendRequest(
                    requestId,
                    senderPlayerId,
                    recipientPlayerId,
                    FriendRequestStatus.PENDING,
                    now,
                    now
            );

            requestsById.put(requestId, request);
            log.info("Created friend request: id={}, from={}, to={}", requestId, senderPlayerId, recipientPlayerId);

            try {
                String senderName = playerAccountService.findById(senderPlayerId)
                        .map(PlayerAccount::displayName)
                        .orElse("A player");
                notificationService.createNotification(
                        recipientPlayerId,
                        com.zynpath.backend.notification.model.NotificationEventType.FRIEND_REQUEST,
                        "New Friend Request",
                        senderName + " sent you a friend request.",
                        requestId,
                        "friends",
                        null,
                        senderPlayerId
                );
            } catch (Exception e) {
                log.warn("Failed to dispatch friend request notification: {}", e.getMessage());
            }

            return request;
        }
    }

    /**
     * Accepts a pending friend request, establishing a mutual friendship atomically.
     */
    public FriendRelationship acceptFriendRequest(String recipientPlayerId, String requestId) {
        synchronized (this) {
            FriendRequest request = requestsById.get(requestId);
            if (request == null) {
                throw new AuthException("REQUEST_NOT_FOUND", "Friend request not found", HttpStatus.NOT_FOUND);
            }

            if (!request.recipientPlayerId().equals(recipientPlayerId)) {
                throw new AuthException("REQUEST_NOT_AUTHORIZED", "You are not authorized to accept this request", HttpStatus.FORBIDDEN);
            }

            if (request.status() != FriendRequestStatus.PENDING) {
                throw new AuthException("REQUEST_NOT_PENDING", "Request is no longer pending: " + request.status());
            }

            long now = System.currentTimeMillis();
            FriendRequest updatedRequest = new FriendRequest(
                    request.requestId(),
                    request.senderPlayerId(),
                    request.recipientPlayerId(),
                    FriendRequestStatus.ACCEPTED,
                    request.createdAt(),
                    now
            );
            requestsById.put(requestId, updatedRequest);

            String key = FriendRelationship.buildCanonicalKey(request.senderPlayerId(), request.recipientPlayerId());
            FriendRelationship relationship = new FriendRelationship(request.senderPlayerId(), request.recipientPlayerId(), now);
            relationshipsByKey.put(key, relationship);

            log.info("Established friendship between {} and {}", request.senderPlayerId(), request.recipientPlayerId());

            try {
                String recipientName = playerAccountService.findById(recipientPlayerId)
                        .map(PlayerAccount::displayName)
                        .orElse("A friend");
                notificationService.createNotification(
                        request.senderPlayerId(),
                        com.zynpath.backend.notification.model.NotificationEventType.FRIEND_REQUEST_ACCEPTED,
                        "Friend Request Accepted",
                        recipientName + " accepted your friend request!",
                        requestId,
                        "friends",
                        null,
                        recipientPlayerId
                );
            } catch (Exception e) {
                log.warn("Failed to dispatch friend request acceptance notification: {}", e.getMessage());
            }

            return relationship;
        }
    }

    /**
     * Rejects an incoming friend request.
     */
    public void rejectFriendRequest(String recipientPlayerId, String requestId) {
        synchronized (this) {
            FriendRequest request = requestsById.get(requestId);
            if (request == null) {
                throw new AuthException("REQUEST_NOT_FOUND", "Friend request not found", HttpStatus.NOT_FOUND);
            }

            if (!request.recipientPlayerId().equals(recipientPlayerId)) {
                throw new AuthException("REQUEST_NOT_AUTHORIZED", "You are not authorized to reject this request", HttpStatus.FORBIDDEN);
            }

            FriendRequest updated = new FriendRequest(
                    request.requestId(),
                    request.senderPlayerId(),
                    request.recipientPlayerId(),
                    FriendRequestStatus.REJECTED,
                    request.createdAt(),
                    System.currentTimeMillis()
            );
            requestsById.put(requestId, updated);
            log.info("Rejected friend request: id={}", requestId);
        }
    }

    /**
     * Cancels an outgoing friend request.
     */
    public void cancelFriendRequest(String senderPlayerId, String requestId) {
        synchronized (this) {
            FriendRequest request = requestsById.get(requestId);
            if (request == null) {
                throw new AuthException("REQUEST_NOT_FOUND", "Friend request not found", HttpStatus.NOT_FOUND);
            }

            if (!request.senderPlayerId().equals(senderPlayerId)) {
                throw new AuthException("REQUEST_NOT_AUTHORIZED", "You are not authorized to cancel this request", HttpStatus.FORBIDDEN);
            }

            FriendRequest updated = new FriendRequest(
                    request.requestId(),
                    request.senderPlayerId(),
                    request.recipientPlayerId(),
                    FriendRequestStatus.CANCELLED,
                    request.createdAt(),
                    System.currentTimeMillis()
            );
            requestsById.put(requestId, updated);
            log.info("Cancelled friend request: id={}", requestId);
        }
    }

    /**
     * Removes an established friendship.
     */
    public void removeFriend(String actingPlayerId, String friendPlayerId) {
        String key = FriendRelationship.buildCanonicalKey(actingPlayerId, friendPlayerId);
        FriendRelationship removed = relationshipsByKey.remove(key);
        if (removed != null) {
            log.info("Removed friendship between {} and {}", actingPlayerId, friendPlayerId);
        }
    }

    /**
     * Blocks a player, immediately severing any friendship and cancelling active requests.
     */
    public void blockPlayer(String blockerPlayerId, String targetPlayerId) {
        if (blockerPlayerId.equals(targetPlayerId)) {
            throw new AuthException("INVALID_ACTION", "You cannot block yourself");
        }

        synchronized (this) {
            removeFriend(blockerPlayerId, targetPlayerId);

            // Invalidate pending requests
            for (FriendRequest req : requestsById.values()) {
                if (req.status() == FriendRequestStatus.PENDING) {
                    if ((req.senderPlayerId().equals(blockerPlayerId) && req.recipientPlayerId().equals(targetPlayerId)) ||
                        (req.senderPlayerId().equals(targetPlayerId) && req.recipientPlayerId().equals(blockerPlayerId))) {
                        requestsById.put(req.requestId(), new FriendRequest(
                                req.requestId(), req.senderPlayerId(), req.recipientPlayerId(),
                                FriendRequestStatus.CANCELLED, req.createdAt(), System.currentTimeMillis()
                        ));
                    }
                }
            }

            String blockKey = PlayerBlock.buildBlockKey(blockerPlayerId, targetPlayerId);
            blocksByKey.put(blockKey, new PlayerBlock(blockerPlayerId, targetPlayerId, System.currentTimeMillis()));
            log.info("Player {} blocked player {}", blockerPlayerId, targetPlayerId);
        }
    }

    /**
     * Unblocks a previously blocked player.
     */
    public void unblockPlayer(String blockerPlayerId, String targetPlayerId) {
        String blockKey = PlayerBlock.buildBlockKey(blockerPlayerId, targetPlayerId);
        blocksByKey.remove(blockKey);
        log.info("Player {} unblocked player {}", blockerPlayerId, targetPlayerId);
    }

    /**
     * Lists all players currently blocked by the specified player.
     */
    public List<BlockedPlayerSummary> listBlockedPlayers(String blockerPlayerId) {
        List<BlockedPlayerSummary> blockedList = new ArrayList<>();
        for (PlayerBlock block : blocksByKey.values()) {
            if (block.blockerPlayerId().equals(blockerPlayerId)) {
                Optional<PlayerAccount> targetAccount = playerAccountService.findById(block.blockedPlayerId());
                if (targetAccount.isPresent()) {
                    PlayerAccount acc = targetAccount.get();
                    blockedList.add(new BlockedPlayerSummary(
                            acc.playerId(),
                            acc.publicZynpathId(),
                            acc.displayName(),
                            "avatar_compass",
                            block.blockedAt()
                    ));
                }
            }
        }
        return blockedList;
    }

    /**
     * Handles social graph cleanup when an account is deleted.
     */
    public void handleAccountDeletion(String playerId) {
        synchronized (this) {
            relationshipsByKey.entrySet().removeIf(entry -> entry.getValue().contains(playerId));
            requestsById.entrySet().removeIf(entry ->
                entry.getValue().senderPlayerId().equals(playerId) || entry.getValue().recipientPlayerId().equals(playerId)
            );
            blocksByKey.entrySet().removeIf(entry ->
                entry.getValue().blockerPlayerId().equals(playerId) || entry.getValue().blockedPlayerId().equals(playerId)
            );
            invitationsById.entrySet().removeIf(entry ->
                entry.getValue().hostPlayerId().equals(playerId) || entry.getValue().targetPlayerId().equals(playerId)
            );
            log.info("Cleaned up social graph and invitations for deleted account playerId={}", playerId);
        }
    }

    /**
     * Lists all accepted friends for a player with current presence states.
     */
    public List<FriendSummary> listFriends(String playerId) {
        List<FriendSummary> friends = new ArrayList<>();
        for (FriendRelationship rel : relationshipsByKey.values()) {
            if (rel.contains(playerId)) {
                String friendId = rel.getOtherPlayerId(playerId);
                Optional<PlayerAccount> friendAccount = playerAccountService.findById(friendId);
                if (friendAccount.isPresent()) {
                    PlayerAccount acc = friendAccount.get();
                    PlayerPresenceState presence = presenceService.getPresence(friendId);
                    friends.add(new FriendSummary(
                            acc.playerId(),
                            acc.publicZynpathId(),
                            acc.displayName(),
                            "avatar_compass",
                            presence,
                            rel.establishedAt()
                    ));
                }
            }
        }
        return friends;
    }

    /**
     * Lists all incoming pending friend requests.
     */
    public List<FriendRequestSummary> listIncomingRequests(String recipientPlayerId) {
        List<FriendRequestSummary> incoming = new ArrayList<>();
        for (FriendRequest req : requestsById.values()) {
            if (req.recipientPlayerId().equals(recipientPlayerId) && req.status() == FriendRequestStatus.PENDING) {
                Optional<PlayerAccount> senderAccount = playerAccountService.findById(req.senderPlayerId());
                if (senderAccount.isPresent()) {
                    PlayerAccount acc = senderAccount.get();
                    incoming.add(new FriendRequestSummary(
                            req.requestId(),
                            acc.playerId(),
                            acc.publicZynpathId(),
                            acc.displayName(),
                            "avatar_compass",
                            req.createdAt()
                    ));
                }
            }
        }
        return incoming;
    }

    /**
     * Lists all outgoing pending friend requests.
     */
    public List<FriendRequestSummary> listOutgoingRequests(String senderPlayerId) {
        List<FriendRequestSummary> outgoing = new ArrayList<>();
        for (FriendRequest req : requestsById.values()) {
            if (req.senderPlayerId().equals(senderPlayerId) && req.status() == FriendRequestStatus.PENDING) {
                Optional<PlayerAccount> recipientAccount = playerAccountService.findById(req.recipientPlayerId());
                if (recipientAccount.isPresent()) {
                    PlayerAccount acc = recipientAccount.get();
                    outgoing.add(new FriendRequestSummary(
                            req.requestId(),
                            acc.playerId(),
                            acc.publicZynpathId(),
                            acc.displayName(),
                            "avatar_compass",
                            req.createdAt()
                    ));
                }
            }
        }
        return outgoing;
    }

    /**
     * Creates a multiplayer room invitation for a friend.
     */
    public MultiplayerInvitation sendMultiplayerInvite(String hostPlayerId, SocialDto.SendMultiplayerInvitePayload payload) {
        PlayerAccount targetAccount = playerAccountService.findByPublicId(payload.targetPublicZynpathId().trim().toUpperCase())
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Invited player not found"));

        if (!areFriends(hostPlayerId, targetAccount.playerId())) {
            throw new AuthException("NOT_FRIENDS", "You can only invite accepted friends to private multiplayer rooms");
        }

        if (isBlocked(hostPlayerId, targetAccount.playerId()) || isBlocked(targetAccount.playerId(), hostPlayerId)) {
            throw new AuthException("PLAYER_BLOCKED", "Cannot invite blocked player", HttpStatus.FORBIDDEN);
        }

        String invitationId = "minv_" + UUID.randomUUID().toString().substring(0, 12);
        long now = System.currentTimeMillis();
        long expiresAt = now + 120_000L; // 2 minute invitation lease

        MultiplayerInvitation invitation = new MultiplayerInvitation(
                invitationId,
                hostPlayerId,
                targetAccount.playerId(),
                payload.gameMode(),
                payload.roomCode(),
                InvitationStatus.PENDING,
                now,
                expiresAt
        );

        invitationsById.put(invitationId, invitation);
        log.info("Created multiplayer invitation: id={}, host={}, target={}, mode={}, room={}",
                invitationId, hostPlayerId, targetAccount.playerId(), payload.gameMode(), payload.roomCode());
        return invitation;
    }

    public boolean areFriends(String idA, String idB) {
        String key = FriendRelationship.buildCanonicalKey(idA, idB);
        return relationshipsByKey.containsKey(key);
    }

    public boolean isBlocked(String blocker, String target) {
        return blocksByKey.containsKey(PlayerBlock.buildBlockKey(blocker, target));
    }

    private FriendRelationshipStatus determineRelationship(String viewerId, String targetId) {
        if (viewerId.equals(targetId)) return FriendRelationshipStatus.SELF;
        if (isBlocked(viewerId, targetId) || isBlocked(targetId, viewerId)) return FriendRelationshipStatus.BLOCKED;
        if (areFriends(viewerId, targetId)) return FriendRelationshipStatus.FRIENDS;

        for (FriendRequest req : requestsById.values()) {
            if (req.status() == FriendRequestStatus.PENDING) {
                if (req.senderPlayerId().equals(viewerId) && req.recipientPlayerId().equals(targetId)) {
                    return FriendRelationshipStatus.OUTGOING_REQUEST;
                }
                if (req.senderPlayerId().equals(targetId) && req.recipientPlayerId().equals(viewerId)) {
                    return FriendRelationshipStatus.INCOMING_REQUEST;
                }
            }
        }

        return FriendRelationshipStatus.NONE;
    }
}
