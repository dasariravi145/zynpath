package com.zynpath.backend.multiplayer.model;

import com.zynpath.backend.social.model.InvitationStatus;

/**
 * Model tracking an authoritative Rematch request between participants of a completed match.
 *
 * Implements Prompt 22 Sections 38-42.
 */
public class RematchRecord {
    private final String previousMatchId;
    private final String requesterPlayerId;
    private final String opponentPlayerId;
    private volatile InvitationStatus status;
    private final long createdAt;
    private final long expiresAt;
    private volatile String newMatchId;

    public RematchRecord(
            String previousMatchId,
            String requesterPlayerId,
            String opponentPlayerId,
            InvitationStatus status,
            long createdAt,
            long expiresAt,
            String newMatchId
    ) {
        this.previousMatchId = previousMatchId;
        this.requesterPlayerId = requesterPlayerId;
        this.opponentPlayerId = opponentPlayerId;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.newMatchId = newMatchId;
    }

    public String getPreviousMatchId() {
        return previousMatchId;
    }

    public String getRequesterPlayerId() {
        return requesterPlayerId;
    }

    public String getOpponentPlayerId() {
        return opponentPlayerId;
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

    public String getNewMatchId() {
        return newMatchId;
    }

    public void setNewMatchId(String newMatchId) {
        this.newMatchId = newMatchId;
    }
}
