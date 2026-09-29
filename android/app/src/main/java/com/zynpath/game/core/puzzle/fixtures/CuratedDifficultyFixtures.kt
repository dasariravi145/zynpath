package com.zynpath.game.core.puzzle.fixtures

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverResult

/**
 * Authoritative deterministic test fixtures for difficulty analysis and level curation.
 *
 * Implements Prompt 10 Section 32:
 * 1. Beginner 4×4 puzzle (uninterrupted serpentine, 4 checkpoints, 0 walls).
 * 2. More constrained 4×4 puzzle (6 checkpoints, strict intermediate guidance).
 * 3. 5×5 puzzle without walls (World 2 standard).
 * 4. 5×5 puzzle with walls (World 3 standard).
 * 5. Puzzle with multiple solutions (uniqueness = NON_UNIQUE, ambiguous routing).
 * 6. Proven unique puzzle (uniqueness = UNIQUE proven by exhaustive search).
 * 7. Puzzle with high route-turn count (winding labyrinth route).
 * 8. Puzzle with constrained graph connectivity (walls creating degree-2 bottlenecks).
 */
object CuratedDifficultyFixtures {

    private val solver = PuzzleSolver()

    // -------------------------------------------------------------------------
    // 1. Beginner 4x4 Puzzle
    // -------------------------------------------------------------------------
    val beginner4x4: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_beginner_4x4",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 3)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 0))
        ),
        difficultyMetadata = "Beginner"
    )

    val beginner4x4Solution: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
        GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
        GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
        GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
    )

    // -------------------------------------------------------------------------
    // 2. More Constrained 4x4 Puzzle (6 checkpoints)
    // -------------------------------------------------------------------------
    val constrained4x4: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_constrained_4x4",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(1, 3)),
            NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(2, 3)),
            NumberedCheckpoint(6, GridPosition(3, 0))
        ),
        difficultyMetadata = "Easy"
    )

    val constrained4x4Solution: PuzzlePath = beginner4x4Solution

    // -------------------------------------------------------------------------
    // 3. 5x5 Puzzle Without Walls (World 2)
    // -------------------------------------------------------------------------
    val clean5x5: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_clean_5x5",
        dimensions = GridDimensions(5, 5),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 4)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(4, 4))
        ),
        difficultyMetadata = "Standard"
    )

    val clean5x5Solution: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
        GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
        GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
        GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
        GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4)
    )

    // -------------------------------------------------------------------------
    // 4. 5x5 Puzzle With Walls (World 3)
    // -------------------------------------------------------------------------
    val walls5x5: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_walls_5x5",
        dimensions = GridDimensions(5, 5),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1))
        ),
        difficultyMetadata = "Medium"
    )

    // -------------------------------------------------------------------------
    // 5. Puzzle With Multiple Solutions
    // -------------------------------------------------------------------------
    val multiSolution4x4: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_multi_4x4",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(3, 0))
        ),
        difficultyMetadata = "Ambiguous"
    )

    // -------------------------------------------------------------------------
    // 6. Proven Unique Puzzle
    // -------------------------------------------------------------------------
    val provenUnique4x4: PuzzleDefinition = constrained4x4

    fun getProvenUniqueSolverResult(): SolverResult {
        return solver.solve(
            provenUnique4x4,
            SolverConfiguration(maxSolutions = 2, nodeLimit = 50_000L, timeBudgetMs = 2_000L)
        )
    }

    // -------------------------------------------------------------------------
    // 7. High Route-Turn Count Puzzle (Spiral/winding route)
    // -------------------------------------------------------------------------
    val highTurn4x4: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_high_turn_4x4",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(2, 1))
        ),
        difficultyMetadata = "HighTurn"
    )

    val highTurn4x4Solution: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
        GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
        GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
        GridPosition(2, 0), GridPosition(1, 0),
        GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1)
    )

    // -------------------------------------------------------------------------
    // 8. Constrained Graph Connectivity Puzzle (Heavy walls, narrow bottlenecks)
    // -------------------------------------------------------------------------
    val constrainedGraph4x4: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_diff_constrained_graph_4x4",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 3)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3))
        ),
        difficultyMetadata = "ConstrainedGraph"
    )
}
