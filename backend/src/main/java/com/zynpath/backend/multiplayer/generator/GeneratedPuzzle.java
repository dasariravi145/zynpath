package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;

import java.util.List;

/**
 * Immutable solver-verified dynamically generated puzzle for multiplayer matches.
 */
public record GeneratedPuzzle(
        String puzzleId,
        int generatorVersion,
        String fingerprint,
        CompetitiveDifficultyTier difficultyTier,
        GameMode gameMode,
        int gridRows,
        int gridCols,
        List<String> requiredCells,
        List<PuzzleAssignment.CheckpointSpec> checkpoints,
        List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
        List<String> canonicalSolution,
        double cognitiveScore,
        CognitiveDifficultyEvaluator.CognitiveEvaluationResult evaluationResult,
        long generationTimeMs,
        long seed
) {
    /**
     * Converts to standard authoritative client-server PuzzleAssignment DTO.
     */
    public PuzzleAssignment toPuzzleAssignment() {
        return new PuzzleAssignment(
                puzzleId,
                generatorVersion,
                fingerprint,
                gridRows,
                gridCols,
                requiredCells,
                checkpoints,
                blockedEdges
        );
    }
}
