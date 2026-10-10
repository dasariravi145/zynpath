package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld4PartA {

    fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    fun grid9x9Cells(): Set<GridPosition> =
        (0..8).flatMap { r -> (0..8).map { c -> GridPosition(r, c) } }.toSet()

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 101: World 4 - Level 101 (9x9) ---

    val LEVEL_101 = PuzzleDefinition(
        puzzleId = "w4_lvl101",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(7, 0)), NumberedCheckpoint(4, GridPosition(8, 4)),
            NumberedCheckpoint(5, GridPosition(7, 8)), NumberedCheckpoint(6, GridPosition(7, 3)), NumberedCheckpoint(7, GridPosition(3, 2)), NumberedCheckpoint(8, GridPosition(0, 3)),
            NumberedCheckpoint(9, GridPosition(6, 4)), NumberedCheckpoint(10, GridPosition(0, 5)), NumberedCheckpoint(11, GridPosition(6, 6)), NumberedCheckpoint(12, GridPosition(0, 6)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(5, 7)), NumberedCheckpoint(15, GridPosition(6, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7))
        ),
        difficultyMetadata = "World 4 - Level 101 (seed=4102)",
        seed = 4102L
    )

    val SOLUTION_101 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(6, 8)
        )
    )

    // --- Level 102: World 4 - Level 102 (9x9) ---

    val LEVEL_102 = PuzzleDefinition(
        puzzleId = "w4_lvl102",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 8)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(1, 6)),
            NumberedCheckpoint(5, GridPosition(2, 7)), NumberedCheckpoint(6, GridPosition(2, 5)), NumberedCheckpoint(7, GridPosition(2, 1)), NumberedCheckpoint(8, GridPosition(3, 5)),
            NumberedCheckpoint(9, GridPosition(8, 6)), NumberedCheckpoint(10, GridPosition(8, 2)), NumberedCheckpoint(11, GridPosition(4, 2)), NumberedCheckpoint(12, GridPosition(4, 6)),
            NumberedCheckpoint(13, GridPosition(5, 4)), NumberedCheckpoint(14, GridPosition(7, 2)), NumberedCheckpoint(15, GridPosition(6, 2)), NumberedCheckpoint(16, GridPosition(7, 4)),
            NumberedCheckpoint(17, GridPosition(7, 6)), NumberedCheckpoint(18, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5))
        ),
        difficultyMetadata = "World 4 - Level 102 (seed=4104)",
        seed = 4104L
    )

    val SOLUTION_102 = PuzzlePath(
        listOf(
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(6, 6)
        )
    )

    // --- Level 103: World 4 - Level 103 (9x9) ---

    val LEVEL_103 = PuzzleDefinition(
        puzzleId = "w4_lvl103",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(8, 4)), NumberedCheckpoint(3, GridPosition(7, 3)), NumberedCheckpoint(4, GridPosition(7, 2)),
            NumberedCheckpoint(5, GridPosition(8, 6)), NumberedCheckpoint(6, GridPosition(6, 7)), NumberedCheckpoint(7, GridPosition(5, 8)), NumberedCheckpoint(8, GridPosition(1, 7)),
            NumberedCheckpoint(9, GridPosition(2, 7)), NumberedCheckpoint(10, GridPosition(5, 3)), NumberedCheckpoint(11, GridPosition(3, 6)), NumberedCheckpoint(12, GridPosition(2, 2)),
            NumberedCheckpoint(13, GridPosition(0, 4)), NumberedCheckpoint(14, GridPosition(1, 0)), NumberedCheckpoint(15, GridPosition(4, 0)), NumberedCheckpoint(16, GridPosition(6, 0)),
            NumberedCheckpoint(17, GridPosition(7, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 4 - Level 103 (seed=4123)",
        seed = 4123L
    )

    val SOLUTION_103 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(7, 1)
        )
    )

    // --- Level 104: World 4 - Level 104 (9x9) ---

    val LEVEL_104 = PuzzleDefinition(
        puzzleId = "w4_lvl104",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(7, 6)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(0, 5)), NumberedCheckpoint(7, GridPosition(1, 1)), NumberedCheckpoint(8, GridPosition(2, 5)),
            NumberedCheckpoint(9, GridPosition(2, 3)), NumberedCheckpoint(10, GridPosition(6, 0)), NumberedCheckpoint(11, GridPosition(7, 1)), NumberedCheckpoint(12, GridPosition(3, 1)),
            NumberedCheckpoint(13, GridPosition(8, 3)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(6, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 4 - Level 104 (seed=4119)",
        seed = 4119L
    )

    val SOLUTION_104 = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4)
        )
    )

    // --- Level 105: World 4 - Level 105 (9x9) ---

    val LEVEL_105 = PuzzleDefinition(
        puzzleId = "w4_lvl105",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 2)), NumberedCheckpoint(2, GridPosition(7, 8)), NumberedCheckpoint(3, GridPosition(7, 1)), NumberedCheckpoint(4, GridPosition(6, 3)),
            NumberedCheckpoint(5, GridPosition(5, 7)), NumberedCheckpoint(6, GridPosition(5, 6)), NumberedCheckpoint(7, GridPosition(4, 0)), NumberedCheckpoint(8, GridPosition(1, 1)),
            NumberedCheckpoint(9, GridPosition(2, 1)), NumberedCheckpoint(10, GridPosition(4, 2)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(0, 2)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(4, 6)), NumberedCheckpoint(15, GridPosition(0, 7)), NumberedCheckpoint(16, GridPosition(1, 7)),
            NumberedCheckpoint(17, GridPosition(2, 6)), NumberedCheckpoint(18, GridPosition(3, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4))
        ),
        difficultyMetadata = "World 4 - Level 105 (seed=4105)",
        seed = 4105L
    )

    val SOLUTION_105 = PuzzlePath(
        listOf(
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5)
        )
    )

    // --- Level 106: World 4 - Level 106 (9x9) ---

    val LEVEL_106 = PuzzleDefinition(
        puzzleId = "w4_lvl106",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 8)), NumberedCheckpoint(2, GridPosition(8, 7)), NumberedCheckpoint(3, GridPosition(3, 7)), NumberedCheckpoint(4, GridPosition(1, 6)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(8, 5)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(0, 4)),
            NumberedCheckpoint(9, GridPosition(3, 0)), NumberedCheckpoint(10, GridPosition(8, 0)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(2, 2)),
            NumberedCheckpoint(13, GridPosition(8, 3)), NumberedCheckpoint(14, GridPosition(8, 4)), NumberedCheckpoint(15, GridPosition(6, 4)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(4, 4)), NumberedCheckpoint(18, GridPosition(2, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4))
        ),
        difficultyMetadata = "World 4 - Level 106 (seed=4113)",
        seed = 4113L
    )

    val SOLUTION_106 = PuzzlePath(
        listOf(
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4)
        )
    )

    // --- Level 107: World 4 - Level 107 (9x9) ---

    val LEVEL_107 = PuzzleDefinition(
        puzzleId = "w4_lvl107",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(0, 6)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(2, 7)), NumberedCheckpoint(7, GridPosition(6, 6)), NumberedCheckpoint(8, GridPosition(2, 3)),
            NumberedCheckpoint(9, GridPosition(6, 0)), NumberedCheckpoint(10, GridPosition(7, 4)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(6, 1)),
            NumberedCheckpoint(13, GridPosition(3, 2)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(4, 2)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(5, 4)), NumberedCheckpoint(18, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7))
        ),
        difficultyMetadata = "World 4 - Level 107 (seed=4113)",
        seed = 4113L
    )

    val SOLUTION_107 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4)
        )
    )

    // --- Level 108: World 4 - Level 108 (9x9) ---

    val LEVEL_108 = PuzzleDefinition(
        puzzleId = "w4_lvl108",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(8, 8)), NumberedCheckpoint(4, GridPosition(2, 7)),
            NumberedCheckpoint(5, GridPosition(1, 2)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(2, 6)), NumberedCheckpoint(8, GridPosition(6, 6)),
            NumberedCheckpoint(9, GridPosition(7, 5)), NumberedCheckpoint(10, GridPosition(4, 5)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(8, 4)),
            NumberedCheckpoint(13, GridPosition(8, 0)), NumberedCheckpoint(14, GridPosition(1, 0)), NumberedCheckpoint(15, GridPosition(2, 1)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(5, 2)), NumberedCheckpoint(18, GridPosition(6, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "World 4 - Level 108 (seed=4111)",
        seed = 4111L
    )

    val SOLUTION_108 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(6, 2)
        )
    )

    // --- Level 109: World 4 - Level 109 (9x9) ---

    val LEVEL_109 = PuzzleDefinition(
        puzzleId = "w4_lvl109",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(0, 8)), NumberedCheckpoint(4, GridPosition(7, 8)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(7, 5)), NumberedCheckpoint(7, GridPosition(6, 7)), NumberedCheckpoint(8, GridPosition(6, 5)),
            NumberedCheckpoint(9, GridPosition(6, 3)), NumberedCheckpoint(10, GridPosition(6, 1)), NumberedCheckpoint(11, GridPosition(1, 1)), NumberedCheckpoint(12, GridPosition(1, 7)),
            NumberedCheckpoint(13, GridPosition(2, 6)), NumberedCheckpoint(14, GridPosition(3, 4)), NumberedCheckpoint(15, GridPosition(5, 7)), NumberedCheckpoint(16, GridPosition(4, 6)),
            NumberedCheckpoint(17, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5))
        ),
        difficultyMetadata = "World 4 - Level 109 (seed=4109)",
        seed = 4109L
    )

    val SOLUTION_109 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3)
        )
    )

    // --- Level 110: World 4 - Level 110 (9x9) ---

    val LEVEL_110 = PuzzleDefinition(
        puzzleId = "w4_lvl110",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(6, 6)), NumberedCheckpoint(3, GridPosition(5, 5)), NumberedCheckpoint(4, GridPosition(5, 6)),
            NumberedCheckpoint(5, GridPosition(8, 4)), NumberedCheckpoint(6, GridPosition(5, 0)), NumberedCheckpoint(7, GridPosition(0, 3)), NumberedCheckpoint(8, GridPosition(2, 8)),
            NumberedCheckpoint(9, GridPosition(8, 7)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(3, 6)), NumberedCheckpoint(12, GridPosition(1, 3)),
            NumberedCheckpoint(13, GridPosition(2, 1)), NumberedCheckpoint(14, GridPosition(2, 2)), NumberedCheckpoint(15, GridPosition(3, 4)), NumberedCheckpoint(16, GridPosition(4, 1)),
            NumberedCheckpoint(17, GridPosition(5, 3)), NumberedCheckpoint(18, GridPosition(6, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1))
        ),
        difficultyMetadata = "World 4 - Level 110 (seed=4110)",
        seed = 4110L
    )

    val SOLUTION_110 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(6, 2)
        )
    )

    // --- Level 111: World 4 - Level 111 (9x9) ---

    val LEVEL_111 = PuzzleDefinition(
        puzzleId = "w4_lvl111",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 6)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(2, 7)), NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(4, 5)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(8, 7)), NumberedCheckpoint(8, GridPosition(1, 0)),
            NumberedCheckpoint(9, GridPosition(5, 1)), NumberedCheckpoint(10, GridPosition(7, 3)), NumberedCheckpoint(11, GridPosition(7, 7)), NumberedCheckpoint(12, GridPosition(5, 4)),
            NumberedCheckpoint(13, GridPosition(4, 2)), NumberedCheckpoint(14, GridPosition(2, 3)), NumberedCheckpoint(15, GridPosition(0, 3)), NumberedCheckpoint(16, GridPosition(0, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 4 - Level 111 (seed=4115)",
        seed = 4115L
    )

    val SOLUTION_111 = PuzzlePath(
        listOf(
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2)
        )
    )

    // --- Level 112: World 4 - Level 112 (9x9) ---

    val LEVEL_112 = PuzzleDefinition(
        puzzleId = "w4_lvl112",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(6, 7)), NumberedCheckpoint(4, GridPosition(8, 5)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(5, 1)), NumberedCheckpoint(8, GridPosition(7, 6)),
            NumberedCheckpoint(9, GridPosition(6, 5)), NumberedCheckpoint(10, GridPosition(4, 2)), NumberedCheckpoint(11, GridPosition(0, 3)), NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(2, 4)), NumberedCheckpoint(14, GridPosition(1, 6)), NumberedCheckpoint(15, GridPosition(3, 6)), NumberedCheckpoint(16, GridPosition(4, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6))
        ),
        difficultyMetadata = "World 4 - Level 112 (seed=4132)",
        seed = 4132L
    )

    val SOLUTION_112 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(4, 6)
        )
    )

    // --- Level 113: World 4 - Level 113 (9x9) ---

    val LEVEL_113 = PuzzleDefinition(
        puzzleId = "w4_lvl113",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 5)), NumberedCheckpoint(2, GridPosition(2, 4)), NumberedCheckpoint(3, GridPosition(4, 5)), NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(6, 4)), NumberedCheckpoint(6, GridPosition(8, 6)), NumberedCheckpoint(7, GridPosition(8, 3)), NumberedCheckpoint(8, GridPosition(3, 0)),
            NumberedCheckpoint(9, GridPosition(0, 4)), NumberedCheckpoint(10, GridPosition(3, 8)), NumberedCheckpoint(11, GridPosition(7, 7)), NumberedCheckpoint(12, GridPosition(1, 5)),
            NumberedCheckpoint(13, GridPosition(2, 1)), NumberedCheckpoint(14, GridPosition(2, 3)), NumberedCheckpoint(15, GridPosition(7, 1)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(3, 2)), NumberedCheckpoint(18, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4))
        ),
        difficultyMetadata = "World 4 - Level 113 (seed=4116)",
        seed = 4116L
    )

    val SOLUTION_113 = PuzzlePath(
        listOf(
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2)
        )
    )

    // --- Level 114: World 4 - Level 114 (9x9) ---

    val LEVEL_114 = PuzzleDefinition(
        puzzleId = "w4_lvl114",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(1, 7)), NumberedCheckpoint(3, GridPosition(1, 6)), NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(2, 5)), NumberedCheckpoint(6, GridPosition(3, 8)), NumberedCheckpoint(7, GridPosition(3, 4)), NumberedCheckpoint(8, GridPosition(4, 6)),
            NumberedCheckpoint(9, GridPosition(8, 7)), NumberedCheckpoint(10, GridPosition(5, 3)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(8, 6)),
            NumberedCheckpoint(13, GridPosition(7, 2)), NumberedCheckpoint(14, GridPosition(6, 0)), NumberedCheckpoint(15, GridPosition(2, 0)), NumberedCheckpoint(16, GridPosition(3, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6))
        ),
        difficultyMetadata = "World 4 - Level 114 (seed=4116)",
        seed = 4116L
    )

    val SOLUTION_114 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(3, 1)
        )
    )

    // --- Level 115: World 4 - Level 115 (9x9) ---

    val LEVEL_115 = PuzzleDefinition(
        puzzleId = "w4_lvl115",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(8, 4)), NumberedCheckpoint(4, GridPosition(5, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(7, 2)), NumberedCheckpoint(8, GridPosition(6, 2)),
            NumberedCheckpoint(9, GridPosition(0, 3)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(2, 6)), NumberedCheckpoint(12, GridPosition(3, 7)),
            NumberedCheckpoint(13, GridPosition(7, 7)), NumberedCheckpoint(14, GridPosition(7, 3)), NumberedCheckpoint(15, GridPosition(6, 5)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(1, 3)), NumberedCheckpoint(18, GridPosition(2, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5))
        ),
        difficultyMetadata = "World 4 - Level 115 (seed=4119)",
        seed = 4119L
    )

    val SOLUTION_115 = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4)
        )
    )

    // --- Level 116: World 4 - Level 116 (9x9) ---

    val LEVEL_116 = PuzzleDefinition(
        puzzleId = "w4_lvl116",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 8)), NumberedCheckpoint(2, GridPosition(2, 8)), NumberedCheckpoint(3, GridPosition(8, 8)), NumberedCheckpoint(4, GridPosition(8, 4)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(7, 7)), NumberedCheckpoint(7, GridPosition(0, 7)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(6, 5)), NumberedCheckpoint(10, GridPosition(0, 5)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(1, 3)),
            NumberedCheckpoint(13, GridPosition(4, 2)), NumberedCheckpoint(14, GridPosition(5, 0)), NumberedCheckpoint(15, GridPosition(3, 1)), NumberedCheckpoint(16, GridPosition(0, 0)),
            NumberedCheckpoint(17, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 4 - Level 116 (seed=4140)",
        seed = 4140L
    )

    val SOLUTION_116 = PuzzlePath(
        listOf(
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1)
        )
    )

    // --- Level 117: World 4 - Level 117 (9x9) ---

    val LEVEL_117 = PuzzleDefinition(
        puzzleId = "w4_lvl117",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(7, 6)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(0, 5)), NumberedCheckpoint(7, GridPosition(6, 4)), NumberedCheckpoint(8, GridPosition(7, 0)),
            NumberedCheckpoint(9, GridPosition(0, 0)), NumberedCheckpoint(10, GridPosition(7, 1)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(3, 2)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(2, 4)), NumberedCheckpoint(15, GridPosition(5, 4)), NumberedCheckpoint(16, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 4 - Level 117 (seed=4131)",
        seed = 4131L
    )

    val SOLUTION_117 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4)
        )
    )

    // --- Level 118: World 4 - Level 118 (9x9) ---

    val LEVEL_118 = PuzzleDefinition(
        puzzleId = "w4_lvl118",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 2)), NumberedCheckpoint(2, GridPosition(6, 3)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(7, 3)),
            NumberedCheckpoint(5, GridPosition(7, 8)), NumberedCheckpoint(6, GridPosition(3, 8)), NumberedCheckpoint(7, GridPosition(0, 7)), NumberedCheckpoint(8, GridPosition(0, 0)),
            NumberedCheckpoint(9, GridPosition(1, 6)), NumberedCheckpoint(10, GridPosition(7, 7)), NumberedCheckpoint(11, GridPosition(4, 6)), NumberedCheckpoint(12, GridPosition(3, 5)),
            NumberedCheckpoint(13, GridPosition(7, 4)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(2, 4)), NumberedCheckpoint(16, GridPosition(2, 0)),
            NumberedCheckpoint(17, GridPosition(4, 1)), NumberedCheckpoint(18, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2))
        ),
        difficultyMetadata = "World 4 - Level 118 (seed=4135)",
        seed = 4135L
    )

    val SOLUTION_118 = PuzzlePath(
        listOf(
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2)
        )
    )

    // --- Level 119: World 4 - Level 119 (9x9) ---

    val LEVEL_119 = PuzzleDefinition(
        puzzleId = "w4_lvl119",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 8)), NumberedCheckpoint(2, GridPosition(3, 8)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(7, 7)),
            NumberedCheckpoint(5, GridPosition(5, 6)), NumberedCheckpoint(6, GridPosition(1, 6)), NumberedCheckpoint(7, GridPosition(0, 3)), NumberedCheckpoint(8, GridPosition(4, 0)),
            NumberedCheckpoint(9, GridPosition(8, 3)), NumberedCheckpoint(10, GridPosition(7, 5)), NumberedCheckpoint(11, GridPosition(7, 1)), NumberedCheckpoint(12, GridPosition(3, 1)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(6, 5)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(2, 2)),
            NumberedCheckpoint(17, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 4 - Level 119 (seed=4121)",
        seed = 4121L
    )

    val SOLUTION_119 = PuzzlePath(
        listOf(
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2)
        )
    )

    // --- Level 120: World 4 - Level 120 (9x9) ---

    val LEVEL_120 = PuzzleDefinition(
        puzzleId = "w4_lvl120",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 6)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(0, 0)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(3, 8)), NumberedCheckpoint(6, GridPosition(7, 8)), NumberedCheckpoint(7, GridPosition(8, 8)), NumberedCheckpoint(8, GridPosition(3, 7)),
            NumberedCheckpoint(9, GridPosition(1, 2)), NumberedCheckpoint(10, GridPosition(7, 1)), NumberedCheckpoint(11, GridPosition(2, 4)), NumberedCheckpoint(12, GridPosition(3, 6)),
            NumberedCheckpoint(13, GridPosition(4, 3)), NumberedCheckpoint(14, GridPosition(5, 6)), NumberedCheckpoint(15, GridPosition(5, 3)), NumberedCheckpoint(16, GridPosition(7, 4)),
            NumberedCheckpoint(17, GridPosition(7, 6)), NumberedCheckpoint(18, GridPosition(7, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4))
        ),
        difficultyMetadata = "World 4 - Level 120 (seed=4122)",
        seed = 4122L
    )

    val SOLUTION_120 = PuzzlePath(
        listOf(
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 5)
        )
    )

    // --- Level 121: World 4 - Level 121 (9x9) ---

    val LEVEL_121 = PuzzleDefinition(
        puzzleId = "w4_lvl121",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 0)), NumberedCheckpoint(2, GridPosition(8, 7)), NumberedCheckpoint(3, GridPosition(7, 3)), NumberedCheckpoint(4, GridPosition(6, 0)),
            NumberedCheckpoint(5, GridPosition(6, 1)), NumberedCheckpoint(6, GridPosition(6, 3)), NumberedCheckpoint(7, GridPosition(3, 8)), NumberedCheckpoint(8, GridPosition(0, 4)),
            NumberedCheckpoint(9, GridPosition(1, 0)), NumberedCheckpoint(10, GridPosition(2, 6)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(4, 7)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(5, 0)), NumberedCheckpoint(18, GridPosition(4, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8))
        ),
        difficultyMetadata = "World 4 - Level 121 (seed=4123)",
        seed = 4123L
    )

    val SOLUTION_121 = PuzzlePath(
        listOf(
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0)
        )
    )

    // --- Level 122: World 4 - Level 122 (9x9) ---

    val LEVEL_122 = PuzzleDefinition(
        puzzleId = "w4_lvl122",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(6, 8)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(8, 7)),
            NumberedCheckpoint(5, GridPosition(6, 6)), NumberedCheckpoint(6, GridPosition(4, 8)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(7, 4)),
            NumberedCheckpoint(9, GridPosition(3, 4)), NumberedCheckpoint(10, GridPosition(0, 8)), NumberedCheckpoint(11, GridPosition(1, 4)), NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(8, 2)), NumberedCheckpoint(14, GridPosition(7, 1)), NumberedCheckpoint(15, GridPosition(1, 2)), NumberedCheckpoint(16, GridPosition(2, 0)),
            NumberedCheckpoint(17, GridPosition(4, 0)), NumberedCheckpoint(18, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 4 - Level 122 (seed=4126)",
        seed = 4126L
    )

    val SOLUTION_122 = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1)
        )
    )

    // --- Level 123: World 4 - Level 123 (9x9) ---

    val LEVEL_123 = PuzzleDefinition(
        puzzleId = "w4_lvl123",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(5, 7)), NumberedCheckpoint(4, GridPosition(8, 3)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(7, 4)), NumberedCheckpoint(7, GridPosition(2, 6)), NumberedCheckpoint(8, GridPosition(4, 5)),
            NumberedCheckpoint(9, GridPosition(6, 4)), NumberedCheckpoint(10, GridPosition(2, 4)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(6, 2)), NumberedCheckpoint(14, GridPosition(1, 2)), NumberedCheckpoint(15, GridPosition(2, 0)), NumberedCheckpoint(16, GridPosition(6, 0)),
            NumberedCheckpoint(17, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 4 - Level 123 (seed=4123)",
        seed = 4123L
    )

    val SOLUTION_123 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1)
        )
    )

    // --- Level 124: World 4 - Level 124 (9x9) ---

    val LEVEL_124 = PuzzleDefinition(
        puzzleId = "w4_lvl124",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(8, 1)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(6, 8)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(6, 0)), NumberedCheckpoint(7, GridPosition(3, 0)), NumberedCheckpoint(8, GridPosition(0, 4)),
            NumberedCheckpoint(9, GridPosition(3, 8)), NumberedCheckpoint(10, GridPosition(5, 8)), NumberedCheckpoint(11, GridPosition(4, 7)), NumberedCheckpoint(12, GridPosition(1, 7)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(4, 5)), NumberedCheckpoint(15, GridPosition(3, 5)), NumberedCheckpoint(16, GridPosition(2, 1)),
            NumberedCheckpoint(17, GridPosition(5, 3)), NumberedCheckpoint(18, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1))
        ),
        difficultyMetadata = "World 4 - Level 124 (seed=4127)",
        seed = 4127L
    )

    val SOLUTION_124 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2)
        )
    )

    // --- Level 125: World 4 - Level 125 (9x9) ---

    val LEVEL_125 = PuzzleDefinition(
        puzzleId = "w4_lvl125",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(7, 1)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(0, 7)), NumberedCheckpoint(6, GridPosition(7, 8)), NumberedCheckpoint(7, GridPosition(3, 7)), NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(2, 1)), NumberedCheckpoint(10, GridPosition(6, 2)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(3, 3)),
            NumberedCheckpoint(13, GridPosition(2, 4)), NumberedCheckpoint(14, GridPosition(7, 4)), NumberedCheckpoint(15, GridPosition(8, 6)), NumberedCheckpoint(16, GridPosition(6, 6)),
            NumberedCheckpoint(17, GridPosition(2, 6)), NumberedCheckpoint(18, GridPosition(3, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 4 - Level 125 (seed=4135)",
        seed = 4135L
    )

    val SOLUTION_125 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(3, 5)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[101] = LEVEL_101
        map[102] = LEVEL_102
        map[103] = LEVEL_103
        map[104] = LEVEL_104
        map[105] = LEVEL_105
        map[106] = LEVEL_106
        map[107] = LEVEL_107
        map[108] = LEVEL_108
        map[109] = LEVEL_109
        map[110] = LEVEL_110
        map[111] = LEVEL_111
        map[112] = LEVEL_112
        map[113] = LEVEL_113
        map[114] = LEVEL_114
        map[115] = LEVEL_115
        map[116] = LEVEL_116
        map[117] = LEVEL_117
        map[118] = LEVEL_118
        map[119] = LEVEL_119
        map[120] = LEVEL_120
        map[121] = LEVEL_121
        map[122] = LEVEL_122
        map[123] = LEVEL_123
        map[124] = LEVEL_124
        map[125] = LEVEL_125
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[101] = SOLUTION_101
        map[102] = SOLUTION_102
        map[103] = SOLUTION_103
        map[104] = SOLUTION_104
        map[105] = SOLUTION_105
        map[106] = SOLUTION_106
        map[107] = SOLUTION_107
        map[108] = SOLUTION_108
        map[109] = SOLUTION_109
        map[110] = SOLUTION_110
        map[111] = SOLUTION_111
        map[112] = SOLUTION_112
        map[113] = SOLUTION_113
        map[114] = SOLUTION_114
        map[115] = SOLUTION_115
        map[116] = SOLUTION_116
        map[117] = SOLUTION_117
        map[118] = SOLUTION_118
        map[119] = SOLUTION_119
        map[120] = SOLUTION_120
        map[121] = SOLUTION_121
        map[122] = SOLUTION_122
        map[123] = SOLUTION_123
        map[124] = SOLUTION_124
        map[125] = SOLUTION_125
        map
    }
}
