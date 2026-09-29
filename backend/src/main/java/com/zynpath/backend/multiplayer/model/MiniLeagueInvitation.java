package com.zynpath.backend.multiplayer.model;

import com.zynpath.backend.social.model.InvitationStatus;

/**
 * Ephemeral invitation to join a private Mini League room.
 *
 * Implements Prompt 23 Sections 17, 18, 19:
 * - 60-second bounded lifetime.
 * - Targeted delivery to eligible accepted friends.
 * - Validated transitions.
 */
public class MiniLeagueInvitation {

    private final String invitationId;
    private final String roomId;
    private final String roomCode;
    private final String roomName;
    private final String inviterPlayerId;
    private final String inviterPublicId;
    private final String inviterDisplayName;
    private final String recipientPlayerId;
    private final String recipientPublicId;
    private final String recipientDisplayName;
    private volatile InvitationStatus status;
    private final long createdAt;
    private final long expiresAt;

    public MiniLeagueInvitation(
            String invitationId,
            String roomId,
            String roomCode,
            String roomName,
            String inviterPlayerId,
            String inviterPublicId,
            String inviterDisplayName,
            String recipientPlayerId,
            String recipientPublicId,
            String recipientDisplayName,
            long createdAt,
            long ttlMs
    ) {
        this.invitationId = invitationId;
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.inviterPlayerId = inviterPlayerId;
        this.inviterPublicId = inviterPublicId;
        this.inviterDisplayName = inviterDisplayName;
        this.recipientPlayerId = recipientPlayerId;
        this.recipientPublicId = recipientPublicId;
        this.recipientDisplayName = recipientDisplayName;
        this.status = InvitationStatus.PENDING;
        this.createdAt = createdAt;
        this.expiresAt = createdAt + ttlMs;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
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
}
