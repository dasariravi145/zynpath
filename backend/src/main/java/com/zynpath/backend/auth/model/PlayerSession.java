package com.zynpath.backend.auth.model;

/**
 * Active application session credential for an authenticated player.
 *
 * Implements Prompt 18 Section 14:
 * - Decoupled from third-party provider tokens
 * - Expiration and validity tracking
 */
public record PlayerSession(
    String sessionToken,
    String playerId,
    long createdAt,
    long expiresAt
) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
