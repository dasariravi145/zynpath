package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld4PartB {

    fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    fun grid9x9Cells(): Set<GridPosition> =
        (0..8).flatMap { r -> (0..8).map { c -> GridPosition(r, c) } }.toSet()

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()



    // --- Level 126: World 4 - Level 126 (9x9) ---

    val LEVEL_126 = PuzzleDefinition(
        puzzleId = "w4_lvl126",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(8, 1)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(5, 2)), NumberedCheckpoint(6, GridPosition(8, 7)), NumberedCheckpoint(7, GridPosition(2, 8)), NumberedCheckpoint(8, GridPosition(1, 7)),
            NumberedCheckpoint(9, GridPosition(4, 7)), NumberedCheckpoint(10, GridPosition(7, 6)), NumberedCheckpoint(11, GridPosition(6, 6)), NumberedCheckpoint(12, GridPosition(4, 6)),
            NumberedCheckpoint(13, GridPosition(1, 6)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(6, 3)), NumberedCheckpoint(16, GridPosition(4, 3)),
            NumberedCheckpoint(17, GridPosition(3, 3)), NumberedCheckpoint(18, GridPosition(0, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5))
        ),
        difficultyMetadata = "World 4 - Level 126 (seed=4140)",
        seed = 4140L
    )

    val SOLUTION_126 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(0, 2)
        )
    )

    // --- Level 127: World 4 - Level 127 (9x9) ---

    val LEVEL_127 = PuzzleDefinition(
        puzzleId = "w4_lvl127",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(6, 8)), NumberedCheckpoint(4, GridPosition(7, 7)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(2, 7)), NumberedCheckpoint(7, GridPosition(1, 6)), NumberedCheckpoint(8, GridPosition(5, 6)),
            NumberedCheckpoint(9, GridPosition(8, 2)), NumberedCheckpoint(10, GridPosition(3, 0)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(7, 1)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(6, 3)), NumberedCheckpoint(15, GridPosition(3, 5)), NumberedCheckpoint(16, GridPosition(3, 2)),
            NumberedCheckpoint(17, GridPosition(2, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2))
        ),
        difficultyMetadata = "World 4 - Level 127 (seed=4131)",
        seed = 4131L
    )

    val SOLUTION_127 = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 2)
        )
    )

    // --- Level 128: World 4 - Level 128 (9x9) ---

    val LEVEL_128 = PuzzleDefinition(
        puzzleId = "w4_lvl128",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(1, 7)), NumberedCheckpoint(3, GridPosition(2, 7)), NumberedCheckpoint(4, GridPosition(3, 7)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(0, 6)), NumberedCheckpoint(7, GridPosition(1, 0)), NumberedCheckpoint(8, GridPosition(5, 0)),
            NumberedCheckpoint(9, GridPosition(8, 1)), NumberedCheckpoint(10, GridPosition(8, 8)), NumberedCheckpoint(11, GridPosition(7, 2)), NumberedCheckpoint(12, GridPosition(1, 1)),
            NumberedCheckpoint(13, GridPosition(4, 2)), NumberedCheckpoint(14, GridPosition(5, 3)), NumberedCheckpoint(15, GridPosition(2, 5)), NumberedCheckpoint(16, GridPosition(6, 4)),
            NumberedCheckpoint(17, GridPosition(5, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 4 - Level 128 (seed=4128)",
        seed = 4128L
    )

    val SOLUTION_128 = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 7)
        )
    )

    // --- Level 129: World 4 - Level 129 (9x9) ---

    val LEVEL_129 = PuzzleDefinition(
        puzzleId = "w4_lvl129",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(0, 6)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(2, 7)), NumberedCheckpoint(6, GridPosition(2, 0)), NumberedCheckpoint(7, GridPosition(6, 1)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(4, 7)), NumberedCheckpoint(10, GridPosition(4, 6)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(5, 2)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(7, 6)), NumberedCheckpoint(15, GridPosition(6, 5)), NumberedCheckpoint(16, GridPosition(7, 5)),
            NumberedCheckpoint(17, GridPosition(7, 2)), NumberedCheckpoint(18, GridPosition(8, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "World 4 - Level 129 (seed=4142)",
        seed = 4142L
    )

    val SOLUTION_129 = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 2)
        )
    )

    // --- Level 130: World 4 - Level 130 (9x9) ---

    val LEVEL_130 = PuzzleDefinition(
        puzzleId = "w4_lvl130",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(0, 8)), NumberedCheckpoint(3, GridPosition(1, 8)), NumberedCheckpoint(4, GridPosition(1, 1)),
            NumberedCheckpoint(5, GridPosition(2, 3)), NumberedCheckpoint(6, GridPosition(3, 7)), NumberedCheckpoint(7, GridPosition(3, 6)), NumberedCheckpoint(8, GridPosition(4, 0)),
            NumberedCheckpoint(9, GridPosition(8, 3)), NumberedCheckpoint(10, GridPosition(7, 7)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(4, 2)),
            NumberedCheckpoint(13, GridPosition(6, 3)), NumberedCheckpoint(14, GridPosition(4, 3)), NumberedCheckpoint(15, GridPosition(6, 8)), NumberedCheckpoint(16, GridPosition(6, 4)),
            NumberedCheckpoint(17, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 4 - Level 130 (seed=4131)",
        seed = 4131L
    )

    val SOLUTION_130 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 5)
        )
    )

    // --- Level 131: World 4 - Level 131 (10x10) ---

    val LEVEL_131 = PuzzleDefinition(
        puzzleId = "w4_lvl131",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 7)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(8, 7)), NumberedCheckpoint(4, GridPosition(9, 5)),
            NumberedCheckpoint(5, GridPosition(9, 1)), NumberedCheckpoint(6, GridPosition(8, 5)), NumberedCheckpoint(7, GridPosition(7, 0)), NumberedCheckpoint(8, GridPosition(6, 3)),
            NumberedCheckpoint(9, GridPosition(5, 6)), NumberedCheckpoint(10, GridPosition(4, 0)), NumberedCheckpoint(11, GridPosition(1, 1)), NumberedCheckpoint(12, GridPosition(3, 1)),
            NumberedCheckpoint(13, GridPosition(4, 7)), NumberedCheckpoint(14, GridPosition(5, 9)), NumberedCheckpoint(15, GridPosition(2, 8)), NumberedCheckpoint(16, GridPosition(0, 7)),
            NumberedCheckpoint(17, GridPosition(3, 6)), NumberedCheckpoint(18, GridPosition(0, 6)), NumberedCheckpoint(19, GridPosition(2, 2)), NumberedCheckpoint(20, GridPosition(0, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8))
        ),
        difficultyMetadata = "World 4 - Level 131 (seed=4138)",
        seed = 4138L
    )

    val SOLUTION_131 = PuzzlePath(
        listOf(
            GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8),
            GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1),
            GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6),
            GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0),
            GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5),
            GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8),
            GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8),
            GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7),
            GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2)
        )
    )

    // --- Level 132: World 4 - Level 132 (10x10) ---

    val LEVEL_132 = PuzzleDefinition(
        puzzleId = "w4_lvl132",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 5)), NumberedCheckpoint(2, GridPosition(1, 1)), NumberedCheckpoint(3, GridPosition(0, 8)), NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(2, 9)), NumberedCheckpoint(7, GridPosition(4, 4)), NumberedCheckpoint(8, GridPosition(4, 0)),
            NumberedCheckpoint(9, GridPosition(8, 0)), NumberedCheckpoint(10, GridPosition(9, 6)), NumberedCheckpoint(11, GridPosition(8, 9)), NumberedCheckpoint(12, GridPosition(8, 5)),
            NumberedCheckpoint(13, GridPosition(8, 1)), NumberedCheckpoint(14, GridPosition(7, 4)), NumberedCheckpoint(15, GridPosition(5, 9)), NumberedCheckpoint(16, GridPosition(5, 7)),
            NumberedCheckpoint(17, GridPosition(5, 5)), NumberedCheckpoint(18, GridPosition(5, 3)), NumberedCheckpoint(19, GridPosition(6, 1)), NumberedCheckpoint(20, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2))
        ),
        difficultyMetadata = "World 4 - Level 132 (seed=4164)",
        seed = 4164L
    )

    val SOLUTION_132 = PuzzlePath(
        listOf(
            GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8),
            GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4),
            GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9),
            GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2),
            GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1),
            GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9),
            GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2),
            GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7),
            GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7),
            GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3),
            GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1)
        )
    )

    // --- Level 133: World 4 - Level 133 (10x10) ---

    val LEVEL_133 = PuzzleDefinition(
        puzzleId = "w4_lvl133",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(1, 9)), NumberedCheckpoint(3, GridPosition(3, 8)), NumberedCheckpoint(4, GridPosition(9, 7)),
            NumberedCheckpoint(5, GridPosition(5, 7)), NumberedCheckpoint(6, GridPosition(5, 5)), NumberedCheckpoint(7, GridPosition(9, 1)), NumberedCheckpoint(8, GridPosition(7, 4)),
            NumberedCheckpoint(9, GridPosition(7, 0)), NumberedCheckpoint(10, GridPosition(0, 0)), NumberedCheckpoint(11, GridPosition(6, 2)), NumberedCheckpoint(12, GridPosition(0, 2)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(4, 3)), NumberedCheckpoint(15, GridPosition(2, 4)), NumberedCheckpoint(16, GridPosition(1, 4)),
            NumberedCheckpoint(17, GridPosition(4, 7)), NumberedCheckpoint(18, GridPosition(2, 6)), NumberedCheckpoint(19, GridPosition(0, 6)), NumberedCheckpoint(20, GridPosition(0, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 4 - Level 133 (seed=4156)",
        seed = 4156L
    )

    val SOLUTION_133 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7)
        )
    )

    // --- Level 134: World 4 - Level 134 (10x10) ---

    val LEVEL_134 = PuzzleDefinition(
        puzzleId = "w4_lvl134",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(0, 8)), NumberedCheckpoint(3, GridPosition(8, 0)), NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(6, 3)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(7, 9)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(2, 4)), NumberedCheckpoint(10, GridPosition(3, 5)), NumberedCheckpoint(11, GridPosition(4, 8)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(7, 7)), NumberedCheckpoint(14, GridPosition(6, 7)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(5, 5)),
            NumberedCheckpoint(17, GridPosition(5, 6)), NumberedCheckpoint(18, GridPosition(6, 4)), NumberedCheckpoint(19, GridPosition(8, 6)), NumberedCheckpoint(20, GridPosition(8, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4))
        ),
        difficultyMetadata = "World 4 - Level 134 (seed=4137)",
        seed = 4137L
    )

    val SOLUTION_134 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5)
        )
    )

    // --- Level 135: World 4 - Level 135 (10x10) ---

    val LEVEL_135 = PuzzleDefinition(
        puzzleId = "w4_lvl135",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 8)), NumberedCheckpoint(2, GridPosition(1, 6)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(3, 9)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(4, 0)), NumberedCheckpoint(7, GridPosition(8, 1)), NumberedCheckpoint(8, GridPosition(3, 1)),
            NumberedCheckpoint(9, GridPosition(5, 2)), NumberedCheckpoint(10, GridPosition(9, 5)), NumberedCheckpoint(11, GridPosition(9, 9)), NumberedCheckpoint(12, GridPosition(5, 9)),
            NumberedCheckpoint(13, GridPosition(4, 6)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(7, 8)), NumberedCheckpoint(16, GridPosition(6, 3)),
            NumberedCheckpoint(17, GridPosition(8, 3)), NumberedCheckpoint(18, GridPosition(7, 5)), NumberedCheckpoint(19, GridPosition(8, 5)), NumberedCheckpoint(20, GridPosition(7, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7))
        ),
        difficultyMetadata = "World 4 - Level 135 (seed=4136)",
        seed = 4136L
    )

    val SOLUTION_135 = PuzzlePath(
        listOf(
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6)
        )
    )

    // --- Level 136: World 4 - Level 136 (10x10) ---

    val LEVEL_136 = PuzzleDefinition(
        puzzleId = "w4_lvl136",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(9, 9)), NumberedCheckpoint(3, GridPosition(7, 1)), NumberedCheckpoint(4, GridPosition(7, 7)),
            NumberedCheckpoint(5, GridPosition(6, 8)), NumberedCheckpoint(6, GridPosition(6, 5)), NumberedCheckpoint(7, GridPosition(5, 1)), NumberedCheckpoint(8, GridPosition(5, 6)),
            NumberedCheckpoint(9, GridPosition(3, 9)), NumberedCheckpoint(10, GridPosition(1, 5)), NumberedCheckpoint(11, GridPosition(2, 8)), NumberedCheckpoint(12, GridPosition(2, 4)),
            NumberedCheckpoint(13, GridPosition(1, 2)), NumberedCheckpoint(14, GridPosition(2, 1)), NumberedCheckpoint(15, GridPosition(4, 1)), NumberedCheckpoint(16, GridPosition(4, 3)),
            NumberedCheckpoint(17, GridPosition(3, 4)), NumberedCheckpoint(18, GridPosition(4, 5)), NumberedCheckpoint(19, GridPosition(4, 8)), NumberedCheckpoint(20, GridPosition(4, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 4 - Level 136 (seed=4167)",
        seed = 4167L
    )

    val SOLUTION_136 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7)
        )
    )

    // --- Level 137: World 4 - Level 137 (10x10) ---

    val LEVEL_137 = PuzzleDefinition(
        puzzleId = "w4_lvl137",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 0)), NumberedCheckpoint(2, GridPosition(1, 9)), NumberedCheckpoint(3, GridPosition(5, 2)), NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(5, 4)), NumberedCheckpoint(6, GridPosition(2, 5)), NumberedCheckpoint(7, GridPosition(5, 6)), NumberedCheckpoint(8, GridPosition(4, 6)),
            NumberedCheckpoint(9, GridPosition(2, 7)), NumberedCheckpoint(10, GridPosition(3, 7)), NumberedCheckpoint(11, GridPosition(6, 6)), NumberedCheckpoint(12, GridPosition(7, 0)),
            NumberedCheckpoint(13, GridPosition(7, 4)), NumberedCheckpoint(14, GridPosition(8, 6)), NumberedCheckpoint(15, GridPosition(8, 3)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(9, 7)), NumberedCheckpoint(18, GridPosition(6, 8)), NumberedCheckpoint(19, GridPosition(2, 9)), NumberedCheckpoint(20, GridPosition(3, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8))
        ),
        difficultyMetadata = "World 4 - Level 137 (seed=4149)",
        seed = 4149L
    )

    val SOLUTION_137 = PuzzlePath(
        listOf(
            GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9),
            GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1),
            GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2),
            GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4),
            GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6),
            GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4),
            GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1),
            GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7),
            GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3),
            GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8),
            GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8),
            GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9)
        )
    )

    // --- Level 138: World 4 - Level 138 (10x10) ---

    val LEVEL_138 = PuzzleDefinition(
        puzzleId = "w4_lvl138",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(3, 9)), NumberedCheckpoint(4, GridPosition(2, 6)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(3, 5)), NumberedCheckpoint(8, GridPosition(5, 8)),
            NumberedCheckpoint(9, GridPosition(6, 6)), NumberedCheckpoint(10, GridPosition(7, 8)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(4, 0)),
            NumberedCheckpoint(13, GridPosition(8, 0)), NumberedCheckpoint(14, GridPosition(7, 1)), NumberedCheckpoint(15, GridPosition(6, 2)), NumberedCheckpoint(16, GridPosition(6, 3)),
            NumberedCheckpoint(17, GridPosition(7, 4)), NumberedCheckpoint(18, GridPosition(8, 5)), NumberedCheckpoint(19, GridPosition(8, 8)), NumberedCheckpoint(20, GridPosition(8, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 4 - Level 138 (seed=4148)",
        seed = 4148L
    )

    val SOLUTION_138 = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9)
        )
    )

    // --- Level 139: World 4 - Level 139 (10x10) ---

    val LEVEL_139 = PuzzleDefinition(
        puzzleId = "w4_lvl139",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(7, 7)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(9, 1)), NumberedCheckpoint(7, GridPosition(8, 5)), NumberedCheckpoint(8, GridPosition(7, 6)),
            NumberedCheckpoint(9, GridPosition(7, 0)), NumberedCheckpoint(10, GridPosition(0, 0)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(2, 6)),
            NumberedCheckpoint(13, GridPosition(3, 4)), NumberedCheckpoint(14, GridPosition(4, 6)), NumberedCheckpoint(15, GridPosition(6, 6)), NumberedCheckpoint(16, GridPosition(4, 3)),
            NumberedCheckpoint(17, GridPosition(1, 1)), NumberedCheckpoint(18, GridPosition(4, 1)), NumberedCheckpoint(19, GridPosition(6, 2)), NumberedCheckpoint(20, GridPosition(6, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 4 - Level 139 (seed=4140)",
        seed = 4140L
    )

    val SOLUTION_139 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3)
        )
    )

    // --- Level 140: World 4 - Level 140 (10x10) ---

    val LEVEL_140 = PuzzleDefinition(
        puzzleId = "w4_lvl140",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(1, 9)), NumberedCheckpoint(3, GridPosition(1, 1)), NumberedCheckpoint(4, GridPosition(8, 2)),
            NumberedCheckpoint(5, GridPosition(9, 8)), NumberedCheckpoint(6, GridPosition(8, 3)), NumberedCheckpoint(7, GridPosition(7, 9)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(3, 5)), NumberedCheckpoint(11, GridPosition(3, 8)), NumberedCheckpoint(12, GridPosition(4, 6)),
            NumberedCheckpoint(13, GridPosition(4, 5)), NumberedCheckpoint(14, GridPosition(4, 2)), NumberedCheckpoint(15, GridPosition(6, 3)), NumberedCheckpoint(16, GridPosition(6, 5)),
            NumberedCheckpoint(17, GridPosition(5, 7)), NumberedCheckpoint(18, GridPosition(6, 7)), NumberedCheckpoint(19, GridPosition(5, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5))
        ),
        difficultyMetadata = "World 4 - Level 140 (seed=4151)",
        seed = 4151L
    )

    val SOLUTION_140 = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8)
        )
    )

    // --- Level 141: World 4 - Level 141 (10x10) ---

    val LEVEL_141 = PuzzleDefinition(
        puzzleId = "w4_lvl141",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(1, 4)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(3, 1)), NumberedCheckpoint(6, GridPosition(4, 6)), NumberedCheckpoint(7, GridPosition(5, 0)), NumberedCheckpoint(8, GridPosition(2, 9)),
            NumberedCheckpoint(9, GridPosition(4, 8)), NumberedCheckpoint(10, GridPosition(6, 8)), NumberedCheckpoint(11, GridPosition(7, 7)), NumberedCheckpoint(12, GridPosition(6, 6)),
            NumberedCheckpoint(13, GridPosition(9, 7)), NumberedCheckpoint(14, GridPosition(9, 5)), NumberedCheckpoint(15, GridPosition(8, 1)), NumberedCheckpoint(16, GridPosition(8, 5)),
            NumberedCheckpoint(17, GridPosition(6, 4)), NumberedCheckpoint(18, GridPosition(7, 4)), NumberedCheckpoint(19, GridPosition(6, 2)), NumberedCheckpoint(20, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6))
        ),
        difficultyMetadata = "World 4 - Level 141 (seed=4150)",
        seed = 4150L
    )

    val SOLUTION_141 = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1)
        )
    )

    // --- Level 142: World 4 - Level 142 (10x10) ---

    val LEVEL_142 = PuzzleDefinition(
        puzzleId = "w4_lvl142",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 0)), NumberedCheckpoint(2, GridPosition(8, 8)), NumberedCheckpoint(3, GridPosition(6, 0)), NumberedCheckpoint(4, GridPosition(7, 3)),
            NumberedCheckpoint(5, GridPosition(7, 7)), NumberedCheckpoint(6, GridPosition(5, 9)), NumberedCheckpoint(7, GridPosition(1, 9)), NumberedCheckpoint(8, GridPosition(5, 8)),
            NumberedCheckpoint(9, GridPosition(6, 7)), NumberedCheckpoint(10, GridPosition(0, 7)), NumberedCheckpoint(11, GridPosition(6, 6)), NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(3, 5)), NumberedCheckpoint(14, GridPosition(0, 4)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(1, 2)),
            NumberedCheckpoint(17, GridPosition(5, 2)), NumberedCheckpoint(18, GridPosition(4, 1)), NumberedCheckpoint(19, GridPosition(2, 1)), NumberedCheckpoint(20, GridPosition(0, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5))
        ),
        difficultyMetadata = "World 4 - Level 142 (seed=4180)",
        seed = 4180L
    )

    val SOLUTION_142 = PuzzlePath(
        listOf(
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )

    // --- Level 143: World 4 - Level 143 (10x10) ---

    val LEVEL_143 = PuzzleDefinition(
        puzzleId = "w4_lvl143",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(2, 8)), NumberedCheckpoint(4, GridPosition(1, 7)),
            NumberedCheckpoint(5, GridPosition(3, 7)), NumberedCheckpoint(6, GridPosition(7, 7)), NumberedCheckpoint(7, GridPosition(8, 6)), NumberedCheckpoint(8, GridPosition(1, 6)),
            NumberedCheckpoint(9, GridPosition(2, 3)), NumberedCheckpoint(10, GridPosition(4, 5)), NumberedCheckpoint(11, GridPosition(8, 4)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(3, 1)), NumberedCheckpoint(14, GridPosition(0, 0)), NumberedCheckpoint(15, GridPosition(4, 0)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(9, 3)), NumberedCheckpoint(18, GridPosition(4, 1)), NumberedCheckpoint(19, GridPosition(5, 2)), NumberedCheckpoint(20, GridPosition(6, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 4 - Level 143 (seed=4161)",
        seed = 4161L
    )

    val SOLUTION_143 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3)
        )
    )

    // --- Level 144: World 4 - Level 144 (10x10) ---

    val LEVEL_144 = PuzzleDefinition(
        puzzleId = "w4_lvl144",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(2, 1)), NumberedCheckpoint(4, GridPosition(3, 9)),
            NumberedCheckpoint(5, GridPosition(3, 5)), NumberedCheckpoint(6, GridPosition(3, 2)), NumberedCheckpoint(7, GridPosition(9, 0)), NumberedCheckpoint(8, GridPosition(4, 2)),
            NumberedCheckpoint(9, GridPosition(5, 2)), NumberedCheckpoint(10, GridPosition(9, 2)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(4, 8)), NumberedCheckpoint(14, GridPosition(5, 7)), NumberedCheckpoint(15, GridPosition(6, 4)), NumberedCheckpoint(16, GridPosition(9, 8)),
            NumberedCheckpoint(17, GridPosition(6, 5)), NumberedCheckpoint(18, GridPosition(7, 7)), NumberedCheckpoint(19, GridPosition(6, 8)), NumberedCheckpoint(20, GridPosition(6, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9))
        ),
        difficultyMetadata = "World 4 - Level 144 (seed=4178)",
        seed = 4178L
    )

    val SOLUTION_144 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9)
        )
    )

    // --- Level 145: World 4 - Level 145 (10x10) ---

    val LEVEL_145 = PuzzleDefinition(
        puzzleId = "w4_lvl145",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 0)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(8, 5)), NumberedCheckpoint(4, GridPosition(8, 2)),
            NumberedCheckpoint(5, GridPosition(7, 3)), NumberedCheckpoint(6, GridPosition(7, 7)), NumberedCheckpoint(7, GridPosition(6, 9)), NumberedCheckpoint(8, GridPosition(6, 4)),
            NumberedCheckpoint(9, GridPosition(3, 0)), NumberedCheckpoint(10, GridPosition(0, 5)), NumberedCheckpoint(11, GridPosition(3, 9)), NumberedCheckpoint(12, GridPosition(5, 8)),
            NumberedCheckpoint(13, GridPosition(1, 7)), NumberedCheckpoint(14, GridPosition(3, 1)), NumberedCheckpoint(15, GridPosition(4, 3)), NumberedCheckpoint(16, GridPosition(5, 5)),
            NumberedCheckpoint(17, GridPosition(5, 7)), NumberedCheckpoint(18, GridPosition(2, 7)), NumberedCheckpoint(19, GridPosition(2, 5)), NumberedCheckpoint(20, GridPosition(3, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 4 - Level 145 (seed=4153)",
        seed = 4153L
    )

    val SOLUTION_145 = PuzzlePath(
        listOf(
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4)
        )
    )

    // --- Level 146: World 4 - Level 146 (10x10) ---

    val LEVEL_146 = PuzzleDefinition(
        puzzleId = "w4_lvl146",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(9, 0)), NumberedCheckpoint(3, GridPosition(8, 3)), NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(0, 0)), NumberedCheckpoint(6, GridPosition(7, 0)), NumberedCheckpoint(7, GridPosition(4, 1)), NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(7, 3)), NumberedCheckpoint(10, GridPosition(3, 3)), NumberedCheckpoint(11, GridPosition(1, 5)), NumberedCheckpoint(12, GridPosition(2, 8)),
            NumberedCheckpoint(13, GridPosition(2, 4)), NumberedCheckpoint(14, GridPosition(4, 8)), NumberedCheckpoint(15, GridPosition(5, 8)), NumberedCheckpoint(16, GridPosition(6, 7)),
            NumberedCheckpoint(17, GridPosition(5, 6)), NumberedCheckpoint(18, GridPosition(7, 4)), NumberedCheckpoint(19, GridPosition(5, 4)), NumberedCheckpoint(20, GridPosition(4, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 4 - Level 146 (seed=4189)",
        seed = 4189L
    )

    val SOLUTION_146 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5)
        )
    )

    // --- Level 147: World 4 - Level 147 (10x10) ---

    val LEVEL_147 = PuzzleDefinition(
        puzzleId = "w4_lvl147",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 9)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(1, 8)), NumberedCheckpoint(4, GridPosition(2, 1)),
            NumberedCheckpoint(5, GridPosition(5, 0)), NumberedCheckpoint(6, GridPosition(9, 0)), NumberedCheckpoint(7, GridPosition(9, 7)), NumberedCheckpoint(8, GridPosition(8, 8)),
            NumberedCheckpoint(9, GridPosition(8, 4)), NumberedCheckpoint(10, GridPosition(8, 1)), NumberedCheckpoint(11, GridPosition(4, 1)), NumberedCheckpoint(12, GridPosition(3, 4)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(4, 5)), NumberedCheckpoint(15, GridPosition(5, 2)), NumberedCheckpoint(16, GridPosition(7, 7)),
            NumberedCheckpoint(17, GridPosition(5, 9)), NumberedCheckpoint(18, GridPosition(5, 7)), NumberedCheckpoint(19, GridPosition(5, 5)), NumberedCheckpoint(20, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2))
        ),
        difficultyMetadata = "World 4 - Level 147 (seed=4152)",
        seed = 4152L
    )

    val SOLUTION_147 = PuzzlePath(
        listOf(
            GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6),
            GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8),
            GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1),
            GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9),
            GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2),
            GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6),
            GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3),
            GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9),
            GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5),
            GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3)
        )
    )

    // --- Level 148: World 4 - Level 148 (10x10) ---

    val LEVEL_148 = PuzzleDefinition(
        puzzleId = "w4_lvl148",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(6, 4)), NumberedCheckpoint(3, GridPosition(6, 3)), NumberedCheckpoint(4, GridPosition(6, 5)),
            NumberedCheckpoint(5, GridPosition(8, 5)), NumberedCheckpoint(6, GridPosition(6, 2)), NumberedCheckpoint(7, GridPosition(9, 1)), NumberedCheckpoint(8, GridPosition(7, 1)),
            NumberedCheckpoint(9, GridPosition(5, 1)), NumberedCheckpoint(10, GridPosition(2, 1)), NumberedCheckpoint(11, GridPosition(3, 9)), NumberedCheckpoint(12, GridPosition(9, 7)),
            NumberedCheckpoint(13, GridPosition(7, 7)), NumberedCheckpoint(14, GridPosition(5, 8)), NumberedCheckpoint(15, GridPosition(5, 7)), NumberedCheckpoint(16, GridPosition(1, 6)),
            NumberedCheckpoint(17, GridPosition(3, 3)), NumberedCheckpoint(18, GridPosition(2, 3)), NumberedCheckpoint(19, GridPosition(2, 5)), NumberedCheckpoint(20, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9))
        ),
        difficultyMetadata = "World 4 - Level 148 (seed=4155)",
        seed = 4155L
    )

    val SOLUTION_148 = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6)
        )
    )

    // --- Level 149: World 4 - Level 149 (10x10) ---

    val LEVEL_149 = PuzzleDefinition(
        puzzleId = "w4_lvl149",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 9)), NumberedCheckpoint(2, GridPosition(8, 1)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(0, 3)),
            NumberedCheckpoint(5, GridPosition(1, 0)), NumberedCheckpoint(6, GridPosition(1, 7)), NumberedCheckpoint(7, GridPosition(2, 8)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(3, 4)), NumberedCheckpoint(11, GridPosition(3, 8)), NumberedCheckpoint(12, GridPosition(6, 7)),
            NumberedCheckpoint(13, GridPosition(7, 6)), NumberedCheckpoint(14, GridPosition(5, 6)), NumberedCheckpoint(15, GridPosition(4, 5)), NumberedCheckpoint(16, GridPosition(5, 1)),
            NumberedCheckpoint(17, GridPosition(6, 4)), NumberedCheckpoint(18, GridPosition(7, 2)), NumberedCheckpoint(19, GridPosition(7, 0)), NumberedCheckpoint(20, GridPosition(6, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2))
        ),
        difficultyMetadata = "World 4 - Level 149 (seed=4158)",
        seed = 4158L
    )

    val SOLUTION_149 = PuzzlePath(
        listOf(
            GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3),
            GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4),
            GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9),
            GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6),
            GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8),
            GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7),
            GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6),
            GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2),
            GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5),
            GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2),
            GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0)
        )
    )

    // --- Level 150: World 4 - Level 150 (10x10) ---

    val LEVEL_150 = PuzzleDefinition(
        puzzleId = "w4_lvl150",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 8)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(4, 8)), NumberedCheckpoint(4, GridPosition(5, 6)),
            NumberedCheckpoint(5, GridPosition(6, 8)), NumberedCheckpoint(6, GridPosition(7, 8)), NumberedCheckpoint(7, GridPosition(8, 5)), NumberedCheckpoint(8, GridPosition(8, 0)),
            NumberedCheckpoint(9, GridPosition(7, 4)), NumberedCheckpoint(10, GridPosition(7, 0)), NumberedCheckpoint(11, GridPosition(2, 2)), NumberedCheckpoint(12, GridPosition(2, 5)),
            NumberedCheckpoint(13, GridPosition(3, 4)), NumberedCheckpoint(14, GridPosition(3, 2)), NumberedCheckpoint(15, GridPosition(6, 2)), NumberedCheckpoint(16, GridPosition(4, 2)),
            NumberedCheckpoint(17, GridPosition(4, 3)), NumberedCheckpoint(18, GridPosition(6, 3)), NumberedCheckpoint(19, GridPosition(5, 3)), NumberedCheckpoint(20, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4))
        ),
        difficultyMetadata = "World 4 - Level 150 (seed=4152)",
        seed = 4152L
    )

    val SOLUTION_150 = PuzzlePath(
        listOf(
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[126] = LEVEL_126
        map[127] = LEVEL_127
        map[128] = LEVEL_128
        map[129] = LEVEL_129
        map[130] = LEVEL_130
        map[131] = LEVEL_131
        map[132] = LEVEL_132
        map[133] = LEVEL_133
        map[134] = LEVEL_134
        map[135] = LEVEL_135
        map[136] = LEVEL_136
        map[137] = LEVEL_137
        map[138] = LEVEL_138
        map[139] = LEVEL_139
        map[140] = LEVEL_140
        map[141] = LEVEL_141
        map[142] = LEVEL_142
        map[143] = LEVEL_143
        map[144] = LEVEL_144
        map[145] = LEVEL_145
        map[146] = LEVEL_146
        map[147] = LEVEL_147
        map[148] = LEVEL_148
        map[149] = LEVEL_149
        map[150] = LEVEL_150
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[126] = SOLUTION_126
        map[127] = SOLUTION_127
        map[128] = SOLUTION_128
        map[129] = SOLUTION_129
        map[130] = SOLUTION_130
        map[131] = SOLUTION_131
        map[132] = SOLUTION_132
        map[133] = SOLUTION_133
        map[134] = SOLUTION_134
        map[135] = SOLUTION_135
        map[136] = SOLUTION_136
        map[137] = SOLUTION_137
        map[138] = SOLUTION_138
        map[139] = SOLUTION_139
        map[140] = SOLUTION_140
        map[141] = SOLUTION_141
        map[142] = SOLUTION_142
        map[143] = SOLUTION_143
        map[144] = SOLUTION_144
        map[145] = SOLUTION_145
        map[146] = SOLUTION_146
        map[147] = SOLUTION_147
        map[148] = SOLUTION_148
        map[149] = SOLUTION_149
        map[150] = SOLUTION_150
        map
    }
}
