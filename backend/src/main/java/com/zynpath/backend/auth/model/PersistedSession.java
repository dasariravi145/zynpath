package com.zynpath.backend.auth.model;

/**
 * Durable representation of an issued application session stored in persistence.
 */
public record PersistedSession(
        String sessionId,
        String tokenHash,
        String playerId,
        long createdAt,
        long expiresAt,
        long lastUsedAt,
        boolean isRevoked,
        Long revokedAt,
        String replacedByTokenHash
) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isValid() {
        return !isRevoked && !isExpired();
    }
}
