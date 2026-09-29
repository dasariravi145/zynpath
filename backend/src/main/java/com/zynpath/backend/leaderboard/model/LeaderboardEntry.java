package com.zynpath.backend.leaderboard.model;

/**
 * Public leaderboard rank entry.
 */
public record LeaderboardEntry(
    int rank,
    String zynpathTag,
    int totalStars,
    int levelsCompleted,
    int eloRating
) {}
