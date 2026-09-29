package com.zynpath.backend.multiplayer.model;

import java.util.List;
import java.util.Map;

/**
 * Authoritative session snapshot returned upon client reconnection.
 *
 * Implements Prompt 20 Section 33:
 * Allows the client to reconcile local UI immediately without replaying missed raw stream events.
 */
public record ReconnectionSnapshot(
    String matchId,
    GameMode gameMode,
    MatchState matchState,
    PuzzleAssignment puzzleAssignment,
    List<ParticipantSummary> participants,
    Long countdownStartedAt,
    Long startedAt,
    Long endedAt,
    long currentServerTime,
    long latestEventSequence,
    boolean puzzleActive,
    List<MatchResult> results
) {
    public record ParticipantSummary(
        String playerId,
        String publicZynpathId,
        String displayName,
        String avatarId,
        boolean ready,
        boolean connected,
        int coveredCells,
        int lastCheckpoint,
        boolean completed,
        Long solveTimeMs,
        boolean winner,
        Integer finishOrder
    ) {}
}
