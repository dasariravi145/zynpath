package com.zynpath.backend.player.model;

/**
 * Summary view of a player's public profile and competitive standing.
 */
public record PlayerProfileSummary(
    String playerId,
    String zynpathTag,
    int totalStars,
    int levelsCompleted,
    int eloRating,
    boolean isPremium
) {}
