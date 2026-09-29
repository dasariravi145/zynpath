package com.zynpath.backend.multiplayer.model;

/**
 * Authoritative record of a participant's outcome in a completed match.
 *
 * Implements Prompt 20 Section 39.
 */
public record MatchResult(
    String matchId,
    String playerId,
    String publicZynpathId,
    String displayName,
    boolean completed,
    Long solveTimeMs,
    int finishOrder,
    boolean isWinner,
    String resultStatus // "VICTORY", "DEFEAT", "TIED", "FORFEIT", "DNF"
) {}
