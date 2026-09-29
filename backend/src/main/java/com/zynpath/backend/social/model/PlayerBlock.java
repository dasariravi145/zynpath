package com.zynpath.backend.social.model;

/**
 * Record of a social block between two players.
 *
 * Implements Prompt 19 Section 18:
 * - Directional block: blocker blocks blockedPlayer
 * - Blocks requests and direct invites
 */
public record PlayerBlock(
    String blockerPlayerId,
    String blockedPlayerId,
    long blockedAt
) {
    public static String buildBlockKey(String blocker, String blocked) {
        return blocker + "->" + blocked;
    }
}
