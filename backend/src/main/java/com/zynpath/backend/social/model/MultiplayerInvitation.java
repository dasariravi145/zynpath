package com.zynpath.backend.social.model;

/**
 * Model for multiplayer room invitations.
 *
 * Implements Prompt 19 Section 36 & 37.
 */
public record MultiplayerInvitation(
    String invitationId,
    String hostPlayerId,
    String targetPlayerId,
    MultiplayerGameMode gameMode,
    String roomCode,
    InvitationStatus status,
    long createdAt,
    long expiresAt
) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
