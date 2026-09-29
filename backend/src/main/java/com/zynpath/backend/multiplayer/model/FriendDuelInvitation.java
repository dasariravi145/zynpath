package com.zynpath.backend.multiplayer.model;

import com.zynpath.backend.social.model.InvitationStatus;

/**
 * Model representing an authoritative Friend Duel 1v1 invitation.
 *
 * Implements Prompt 22 Sections 11 & 12:
 * - Unique invitation ID, inviter and recipient identities.
 * - Explicit states: PENDING, ACCEPTED, DECLINED, CANCELLED, EXPIRED, INVALIDATED.
 * - Bounded expiration window and associated match ID upon acceptance.
 */
public class FriendDuelInvitation {
    private final String invitationId;
    private final String inviterPlayerId;
    private final String inviterPublicId;
    private final String inviterDisplayName;
    private final String recipientPlayerId;
    private final String recipientPublicId;
    private final String recipientDisplayName;
    private final GameMode gameMode;
    private volatile InvitationStatus status;
    private final long createdAt;
    private final long expiresAt;
    private volatile String matchId;
    private final String previousMatchId;

    public FriendDuelInvitation(
            String invitationId,
            String inviterPlayerId,
            String inviterPublicId,
            String inviterDisplayName,
            String recipientPlayerId,
            String recipientPublicId,
            String recipientDisplayName,
            GameMode gameMode,
            InvitationStatus status,
            long createdAt,
            long expiresAt,
            String matchId,
            String previousMatchId
    ) {
        this.invitationId = invitationId;
        this.inviterPlayerId = inviterPlayerId;
        this.inviterPublicId = inviterPublicId;
        this.inviterDisplayName = inviterDisplayName;
        this.recipientPlayerId = recipientPlayerId;
        this.recipientPublicId = recipientPublicId;
        this.recipientDisplayName = recipientDisplayName;
        this.gameMode = gameMode;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.matchId = matchId;
        this.previousMatchId = previousMatchId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public String getInviterPlayerId() {
        return inviterPlayerId;
    }

    public String getInviterPublicId() {
        return inviterPublicId;
    }

    public String getInviterDisplayName() {
        return inviterDisplayName;
    }

    public String getRecipientPlayerId() {
        return recipientPlayerId;
    }

    public String getRecipientPublicId() {
        return recipientPublicId;
    }

    public String getRecipientDisplayName() {
        return recipientDisplayName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getPreviousMatchId() {
        return previousMatchId;
    }
}
