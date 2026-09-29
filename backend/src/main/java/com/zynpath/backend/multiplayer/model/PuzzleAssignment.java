package com.zynpath.backend.multiplayer.model;

import java.util.List;

/**
 * Immutable authoritative puzzle specification assigned to an online match.
 *
 * Implements Prompt 20 Sections 21-24:
 * - Shared identically among all participants in the match.
 * - Sourced strictly from solver-verified candidate pools.
 * - Guaranteed immutable through reconnections and state updates.
 */
public record PuzzleAssignment(
    String puzzleId,
    int puzzleVersion,
    String fingerprint,
    int gridRows,
    int gridCols,
    List<String> requiredCells,      // e.g. ["0,0", "0,1", ...]
    List<CheckpointSpec> checkpoints, // Ordered checkpoint definitions
    List<BlockedEdgeSpec> blockedEdges // Obstacle boundaries
) {
    public record CheckpointSpec(
        int number,
        int row,
        int col
    ) {}

    public record BlockedEdgeSpec(
        int row1,
        int col1,
        int row2,
        int col2
    ) {
        public boolean matches(int rA, int cA, int rB, int cB) {
            return (row1 == rA && col1 == cA && row2 == rB && col2 == cB) ||
                   (row1 == rB && col1 == cB && row2 == rA && col2 == cA);
        }
    }
}
