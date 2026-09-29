package com.zynpath.backend.social.model;

/**
 * Summary record of a blocked player.
 *
 * Implements Prompt 32 Section 21:
 * - Minimal safe public identity representation for block list management
 */
public record BlockedPlayerSummary(
    String playerId,
    String publicZynpathId,
    String displayName,
    String avatarId,
    long blockedAt
) {}
