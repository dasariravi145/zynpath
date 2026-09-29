package com.zynpath.backend.puzzle.model;

import java.util.List;

/**
 * Competitive puzzle solution claim submitted by an online player.
 * The server is authoritative: all coordinates and checkpoints must be validated against the puzzle seed.
 */
public record CompetitiveValidationClaim(
    String matchId,
    String playerId,
    int levelId,
    int worldId,
    int puzzleVersion,
    long puzzleSeed,
    long solveTimeMs,
    int movesCount,
    List<String> pathCoordinates
) {}
