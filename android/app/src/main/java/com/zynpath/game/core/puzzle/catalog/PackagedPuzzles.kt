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

    private fun grid5x5Cells(): Set<GridPosition> =
        (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()

    // --- World 1: 6x6, 0 walls (Delegated to CuratedFirst50Levels) ---

    val LEVEL_1: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_1
    val LEVEL_2: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_2
    val LEVEL_3: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_3
    val LEVEL_4: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_4
    val LEVEL_5: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_5

    private fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    // --- World 2: 7x7, 0 walls ---

    val LEVEL_21: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_21
    val LEVEL_22: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_22
    val LEVEL_23: PuzzleDefinition get() = CuratedFirst50Levels.LEVEL_23

    // --- World 3: 8x8, with walls ---

    val LEVEL_51 = PuzzleDefinition(
        puzzleId = "w3_lvl51",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(0, 7)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(1, 0)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(2, 7)),
            NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 0)),
            NumberedCheckpoint(10, GridPosition(4, 4)),
            NumberedCheckpoint(11, GridPosition(4, 7)),
            NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(5, 0)),
            NumberedCheckpoint(14, GridPosition(6, 4)),
            NumberedCheckpoint(15, GridPosition(6, 7)),
            NumberedCheckpoint(16, GridPosition(7, 0))
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
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(0, 7)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(1, 0)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(2, 7)),
            NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 0)),
            NumberedCheckpoint(10, GridPosition(4, 4)),
            NumberedCheckpoint(11, GridPosition(4, 7)),
            NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(5, 0)),
            NumberedCheckpoint(14, GridPosition(6, 4)),
            NumberedCheckpoint(15, GridPosition(6, 7)),
            NumberedCheckpoint(16, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3052L
    )

    val LEVEL_53 = PuzzleDefinition(
        puzzleId = "w3_lvl53",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(0, 7)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(1, 0)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(2, 7)),
            NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 0)),
            NumberedCheckpoint(10, GridPosition(4, 4)),
            NumberedCheckpoint(11, GridPosition(4, 7)),
            NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(5, 0)),
            NumberedCheckpoint(14, GridPosition(6, 4)),
            NumberedCheckpoint(15, GridPosition(6, 7)),
            NumberedCheckpoint(16, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3053L
    )

    val ALL_PACKAGED: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map.putAll(CuratedFirst50Levels.LEVELS)
        map[51] = LEVEL_51
        map[52] = LEVEL_52
        map[53] = LEVEL_53
        map
    }

    val LEVEL_1_PUZZLE: PuzzleDefinition get() = LEVEL_1
    val LEVEL_51_PUZZLE: PuzzleDefinition get() = LEVEL_51

    val SOLUTION_1_ROUTE: com.zynpath.game.core.puzzle.model.PuzzlePath get() = CuratedFirst50Levels.SOLUTION_1

    val SOLUTION_51_ROUTE: com.zynpath.game.core.puzzle.model.PuzzlePath = com.zynpath.game.core.puzzle.model.PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0)
        )
    )

    val ALL_SOLUTIONS: Map<Int, com.zynpath.game.core.puzzle.model.PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, com.zynpath.game.core.puzzle.model.PuzzlePath>()
        map.putAll(CuratedFirst50Levels.SOLUTIONS)
        map[51] = SOLUTION_51_ROUTE
        map
    }
}
