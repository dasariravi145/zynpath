package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Authoritative definitions for pre-packaged, verified catalog puzzles.
 *
 * Implements Prompt 11 Section 22:
 * Representative puzzles for World 1 (without walls), World 2 (without walls),
 * and World 3 (with walls). Every single puzzle has an exact, verified complete solution.
 */
object PackagedPuzzles {

    private fun grid4x4Cells(): Set<GridPosition> =
        (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid5x5Cells(): Set<GridPosition> =
        (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()

    // --- World 1: 4x4, 0 walls ---

    val LEVEL_1 = PuzzleDefinition(
        puzzleId = "w1_lvl1",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(3, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1001L
    )

    val LEVEL_2 = PuzzleDefinition(
        puzzleId = "w1_lvl2",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(3, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(3, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1002L
    )

    val LEVEL_3 = PuzzleDefinition(
        puzzleId = "w1_lvl3",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(1, 3)),
            NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(2, 3)),
            NumberedCheckpoint(6, GridPosition(3, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1003L
    )

    val LEVEL_4 = PuzzleDefinition(
        puzzleId = "w1_lvl4",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 0)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(3, 1)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(3, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1004L
    )

    val LEVEL_5 = PuzzleDefinition(
        puzzleId = "w1_lvl5",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 3)),
            NumberedCheckpoint(2, GridPosition(3, 0)),
            NumberedCheckpoint(3, GridPosition(2, 3)),
            NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(0, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1005L
    )

    // --- World 2: 5x5, 0 walls ---

    val LEVEL_21 = PuzzleDefinition(
        puzzleId = "w2_lvl21",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 4)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 2021L
    )

    val LEVEL_22 = PuzzleDefinition(
        puzzleId = "w2_lvl22",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 2022L
    )

    val LEVEL_23 = PuzzleDefinition(
        puzzleId = "w2_lvl23",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(3, 0)),
            NumberedCheckpoint(4, GridPosition(2, 4)),
            NumberedCheckpoint(5, GridPosition(1, 0)),
            NumberedCheckpoint(6, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 2023L
    )

    // --- World 3: 5x5, with walls ---

    val LEVEL_51 = PuzzleDefinition(
        puzzleId = "w3_lvl51",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 4)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3051L
    )

    val LEVEL_52 = PuzzleDefinition(
        puzzleId = "w3_lvl52",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 4)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3052L
    )

    val LEVEL_53 = PuzzleDefinition(
        puzzleId = "w3_lvl53",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 4)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3053L
    )

    val ALL_PACKAGED: Map<Int, PuzzleDefinition> by lazy {
        mapOf(
            1 to LEVEL_1,
            2 to LEVEL_2,
            3 to LEVEL_3,
            4 to LEVEL_4,
            5 to LEVEL_5,
            21 to LEVEL_21,
            22 to LEVEL_22,
            23 to LEVEL_23,
            51 to LEVEL_51,
            52 to LEVEL_52,
            53 to LEVEL_53
        )
    }

    val LEVEL_1_PUZZLE: PuzzleDefinition get() = LEVEL_1
    val LEVEL_51_PUZZLE: PuzzleDefinition get() = LEVEL_51

    val SOLUTION_1_ROUTE: com.zynpath.game.core.puzzle.model.PuzzlePath = com.zynpath.game.core.puzzle.model.PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
    )

    val ALL_SOLUTIONS: Map<Int, com.zynpath.game.core.puzzle.model.PuzzlePath> by lazy {
        mapOf(
            1 to SOLUTION_1_ROUTE
        )
    }
}
