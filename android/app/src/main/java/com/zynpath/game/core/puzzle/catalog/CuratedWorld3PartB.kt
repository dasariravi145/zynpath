package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld3PartB {

    fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    fun grid9x9Cells(): Set<GridPosition> =
        (0..8).flatMap { r -> (0..8).map { c -> GridPosition(r, c) } }.toSet()

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 76: World 3 - Level 76 (8x8) ---

    val LEVEL_76 = PuzzleDefinition(
        puzzleId = "w3_lvl76",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 7)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(1, 5)), NumberedCheckpoint(4, GridPosition(7, 1)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(2, 4)), NumberedCheckpoint(7, GridPosition(5, 0)), NumberedCheckpoint(8, GridPosition(3, 1)),
            NumberedCheckpoint(9, GridPosition(5, 3)), NumberedCheckpoint(10, GridPosition(3, 4)), NumberedCheckpoint(11, GridPosition(4, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4))
        ),
        difficultyMetadata = "World 3 - Level 76 (seed=3091)",
        seed = 3091L
    )

    val SOLUTION_76 = PuzzlePath(
        listOf(
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5)
        )
    )

    // --- Level 77: World 3 - Level 77 (8x8) ---

    val LEVEL_77 = PuzzleDefinition(
        puzzleId = "w3_lvl77",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(6, 1)), NumberedCheckpoint(3, GridPosition(3, 0)), NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(7, 3)), NumberedCheckpoint(7, GridPosition(2, 1)), NumberedCheckpoint(8, GridPosition(1, 3)),
            NumberedCheckpoint(9, GridPosition(2, 6)), NumberedCheckpoint(10, GridPosition(2, 3)), NumberedCheckpoint(11, GridPosition(3, 4)), NumberedCheckpoint(12, GridPosition(4, 6)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(4, 3)), NumberedCheckpoint(15, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 3 - Level 77 (seed=3084)",
        seed = 3084L
    )

    val SOLUTION_77 = PuzzlePath(
        listOf(
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4)
        )
    )

    // --- Level 78: World 3 - Level 78 (8x8) ---

    val LEVEL_78 = PuzzleDefinition(
        puzzleId = "w3_lvl78",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 1)), NumberedCheckpoint(2, GridPosition(0, 2)), NumberedCheckpoint(3, GridPosition(2, 7)), NumberedCheckpoint(4, GridPosition(7, 5)),
            NumberedCheckpoint(5, GridPosition(5, 0)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(6, 2)), NumberedCheckpoint(8, GridPosition(3, 6)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(5, 4)), NumberedCheckpoint(11, GridPosition(4, 5)), NumberedCheckpoint(12, GridPosition(2, 4)),
            NumberedCheckpoint(13, GridPosition(4, 4)), NumberedCheckpoint(14, GridPosition(3, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "World 3 - Level 78 (seed=3089)",
        seed = 3089L
    )

    val SOLUTION_78 = PuzzlePath(
        listOf(
            GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7),
            GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3),
            GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1),
            GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6),
            GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5),
            GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3)
        )
    )

    // --- Level 79: World 3 - Level 79 (8x8) ---

    val LEVEL_79 = PuzzleDefinition(
        puzzleId = "w3_lvl79",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 7)), NumberedCheckpoint(2, GridPosition(5, 7)), NumberedCheckpoint(3, GridPosition(6, 6)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(4, 5)), NumberedCheckpoint(6, GridPosition(7, 5)), NumberedCheckpoint(7, GridPosition(4, 4)), NumberedCheckpoint(8, GridPosition(2, 3)),
            NumberedCheckpoint(9, GridPosition(6, 3)), NumberedCheckpoint(10, GridPosition(7, 1)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(1, 0)),
            NumberedCheckpoint(13, GridPosition(4, 2)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(1, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 3 - Level 79 (seed=3122)",
        seed = 3122L
    )

    val SOLUTION_79 = PuzzlePath(
        listOf(
            GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6),
            GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5),
            GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4),
            GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1),
            GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3),
            GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7)
        )
    )

    // --- Level 80: World 3 - Level 80 (8x8) ---

    val LEVEL_80 = PuzzleDefinition(
        puzzleId = "w3_lvl80",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 6)), NumberedCheckpoint(2, GridPosition(0, 5)), NumberedCheckpoint(3, GridPosition(2, 0)), NumberedCheckpoint(4, GridPosition(4, 1)),
            NumberedCheckpoint(5, GridPosition(1, 3)), NumberedCheckpoint(6, GridPosition(4, 7)), NumberedCheckpoint(7, GridPosition(7, 7)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(5, 2)), NumberedCheckpoint(10, GridPosition(2, 3)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(4, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 3 - Level 80 (seed=3097)",
        seed = 3097L
    )

    val SOLUTION_80 = PuzzlePath(
        listOf(
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5)
        )
    )

    // --- Level 81: World 3 - Level 81 (8x8) ---

    val LEVEL_81 = PuzzleDefinition(
        puzzleId = "w3_lvl81",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(6, 7)), NumberedCheckpoint(3, GridPosition(2, 6)), NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(6, 0)), NumberedCheckpoint(6, GridPosition(7, 2)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(1, 3)),
            NumberedCheckpoint(9, GridPosition(6, 3)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(1, 1)),
            NumberedCheckpoint(13, GridPosition(3, 2)), NumberedCheckpoint(14, GridPosition(5, 2)), NumberedCheckpoint(15, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6))
        ),
        difficultyMetadata = "World 3 - Level 81 (seed=3090)",
        seed = 3090L
    )

    val SOLUTION_81 = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3)
        )
    )

    // --- Level 82: World 3 - Level 82 (8x8) ---

    val LEVEL_82 = PuzzleDefinition(
        puzzleId = "w3_lvl82",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 0)), NumberedCheckpoint(2, GridPosition(7, 7)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(5, 5)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(4, 3)), NumberedCheckpoint(7, GridPosition(4, 0)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(2, 5)), NumberedCheckpoint(11, GridPosition(2, 1)), NumberedCheckpoint(12, GridPosition(0, 0)),
            NumberedCheckpoint(13, GridPosition(1, 2)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(0, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "World 3 - Level 82 (seed=3085)",
        seed = 3085L
    )

    val SOLUTION_82 = PuzzlePath(
        listOf(
            GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7),
            GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0),
            GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7),
            GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7),
            GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3),
            GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6)
        )
    )

    // --- Level 83: World 3 - Level 83 (8x8) ---

    val LEVEL_83 = PuzzleDefinition(
        puzzleId = "w3_lvl83",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(1, 1)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(6, 3)), NumberedCheckpoint(7, GridPosition(2, 6)), NumberedCheckpoint(8, GridPosition(4, 5)),
            NumberedCheckpoint(9, GridPosition(5, 7)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(7, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4))
        ),
        difficultyMetadata = "World 3 - Level 83 (seed=3124)",
        seed = 3124L
    )

    val SOLUTION_83 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6)
        )
    )

    // --- Level 84: World 3 - Level 84 (8x8) ---

    val LEVEL_84 = PuzzleDefinition(
        puzzleId = "w3_lvl84",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 1)), NumberedCheckpoint(2, GridPosition(0, 2)), NumberedCheckpoint(3, GridPosition(2, 7)), NumberedCheckpoint(4, GridPosition(6, 6)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(3, 3)), NumberedCheckpoint(7, GridPosition(4, 2)), NumberedCheckpoint(8, GridPosition(3, 0)),
            NumberedCheckpoint(9, GridPosition(7, 0)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(5, 4)),
            NumberedCheckpoint(13, GridPosition(2, 5)), NumberedCheckpoint(14, GridPosition(6, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5))
        ),
        difficultyMetadata = "World 3 - Level 84 (seed=3095)",
        seed = 3095L
    )

    val SOLUTION_84 = PuzzlePath(
        listOf(
            GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7),
            GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6),
            GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2),
            GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2),
            GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4),
            GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4)
        )
    )

    // --- Level 85: World 3 - Level 85 (8x8) ---

    val LEVEL_85 = PuzzleDefinition(
        puzzleId = "w3_lvl85",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(0, 5)), NumberedCheckpoint(3, GridPosition(2, 7)), NumberedCheckpoint(4, GridPosition(7, 7)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(0, 1)), NumberedCheckpoint(7, GridPosition(3, 0)), NumberedCheckpoint(8, GridPosition(7, 1)),
            NumberedCheckpoint(9, GridPosition(6, 5)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(1, 1)), NumberedCheckpoint(12, GridPosition(5, 2)),
            NumberedCheckpoint(13, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 3 - Level 85 (seed=3123)",
        seed = 3123L
    )

    val SOLUTION_85 = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4)
        )
    )

    // --- Level 86: World 3 - Level 86 (8x8) ---

    val LEVEL_86 = PuzzleDefinition(
        puzzleId = "w3_lvl86",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(6, 1)), NumberedCheckpoint(3, GridPosition(5, 6)), NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(4, 0)), NumberedCheckpoint(6, GridPosition(4, 4)), NumberedCheckpoint(7, GridPosition(0, 7)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(0, 1)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(2, 4)), NumberedCheckpoint(12, GridPosition(1, 5)),
            NumberedCheckpoint(13, GridPosition(1, 4)), NumberedCheckpoint(14, GridPosition(1, 2)), NumberedCheckpoint(15, GridPosition(0, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 3 - Level 86 (seed=3092)",
        seed = 3092L
    )

    val SOLUTION_86 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3)
        )
    )

    // --- Level 87: World 3 - Level 87 (8x8) ---

    val LEVEL_87 = PuzzleDefinition(
        puzzleId = "w3_lvl87",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 2)), NumberedCheckpoint(2, GridPosition(6, 6)), NumberedCheckpoint(3, GridPosition(7, 0)), NumberedCheckpoint(4, GridPosition(5, 5)),
            NumberedCheckpoint(5, GridPosition(4, 6)), NumberedCheckpoint(6, GridPosition(4, 3)), NumberedCheckpoint(7, GridPosition(4, 0)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(2, 5)), NumberedCheckpoint(11, GridPosition(2, 1)), NumberedCheckpoint(12, GridPosition(0, 0)),
            NumberedCheckpoint(13, GridPosition(1, 2)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(0, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "World 3 - Level 87 (seed=3094)",
        seed = 3094L
    )

    val SOLUTION_87 = PuzzlePath(
        listOf(
            GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6),
            GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0),
            GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7),
            GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7),
            GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3),
            GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6)
        )
    )

    // --- Level 88: World 3 - Level 88 (8x8) ---

    val LEVEL_88 = PuzzleDefinition(
        puzzleId = "w3_lvl88",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 1)), NumberedCheckpoint(2, GridPosition(0, 4)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(3, 7)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(5, 1)), NumberedCheckpoint(8, GridPosition(4, 3)),
            NumberedCheckpoint(9, GridPosition(6, 1)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(4, 6)), NumberedCheckpoint(12, GridPosition(5, 6)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 3 - Level 88 (seed=3089)",
        seed = 3089L
    )

    val SOLUTION_88 = PuzzlePath(
        listOf(
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1),
            GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3),
            GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3),
            GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7),
            GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6)
        )
    )

    // --- Level 89: World 3 - Level 89 (8x8) ---

    val LEVEL_89 = PuzzleDefinition(
        puzzleId = "w3_lvl89",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 7)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(6, 6)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(0, 0)), NumberedCheckpoint(6, GridPosition(3, 4)), NumberedCheckpoint(7, GridPosition(6, 4)), NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(5, 2)), NumberedCheckpoint(11, GridPosition(7, 1)), NumberedCheckpoint(12, GridPosition(5, 1)),
            NumberedCheckpoint(13, GridPosition(4, 1)), NumberedCheckpoint(14, GridPosition(2, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 3 - Level 89 (seed=3096)",
        seed = 3096L
    )

    val SOLUTION_89 = PuzzlePath(
        listOf(
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1)
        )
    )

    // --- Level 90: World 3 - Level 90 (8x8) ---

    val LEVEL_90 = PuzzleDefinition(
        puzzleId = "w3_lvl90",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 0)), NumberedCheckpoint(2, GridPosition(6, 6)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(5, 5)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(4, 3)), NumberedCheckpoint(7, GridPosition(4, 0)), NumberedCheckpoint(8, GridPosition(0, 0)),
            NumberedCheckpoint(9, GridPosition(3, 2)), NumberedCheckpoint(10, GridPosition(0, 6)), NumberedCheckpoint(11, GridPosition(2, 5)), NumberedCheckpoint(12, GridPosition(0, 5)),
            NumberedCheckpoint(13, GridPosition(1, 4)), NumberedCheckpoint(14, GridPosition(2, 2)), NumberedCheckpoint(15, GridPosition(1, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2))
        ),
        difficultyMetadata = "World 3 - Level 90 (seed=3096)",
        seed = 3096L
    )

    val SOLUTION_90 = PuzzlePath(
        listOf(
            GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7),
            GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0),
            GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7),
            GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7),
            GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3)
        )
    )

    // --- Level 91: World 3 - Level 91 (9x9) ---

    val LEVEL_91 = PuzzleDefinition(
        puzzleId = "w3_lvl91",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(1, 2)), NumberedCheckpoint(4, GridPosition(0, 8)),
            NumberedCheckpoint(5, GridPosition(2, 3)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(8, 8)), NumberedCheckpoint(8, GridPosition(5, 7)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(7, 6)), NumberedCheckpoint(11, GridPosition(3, 5)), NumberedCheckpoint(12, GridPosition(7, 3)),
            NumberedCheckpoint(13, GridPosition(3, 0)), NumberedCheckpoint(14, GridPosition(4, 2)), NumberedCheckpoint(15, GridPosition(8, 2)), NumberedCheckpoint(16, GridPosition(7, 1)),
            NumberedCheckpoint(17, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5))
        ),
        difficultyMetadata = "World 3 - Level 91 (seed=3218)",
        seed = 3218L
    )

    val SOLUTION_91 = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1)
        )
    )

    // --- Level 92: World 3 - Level 92 (9x9) ---

    val LEVEL_92 = PuzzleDefinition(
        puzzleId = "w3_lvl92",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(8, 5)), NumberedCheckpoint(3, GridPosition(6, 0)), NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(4, 1)), NumberedCheckpoint(6, GridPosition(7, 2)), NumberedCheckpoint(7, GridPosition(0, 2)), NumberedCheckpoint(8, GridPosition(1, 8)),
            NumberedCheckpoint(9, GridPosition(4, 8)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(6, 3)),
            NumberedCheckpoint(13, GridPosition(7, 4)), NumberedCheckpoint(14, GridPosition(3, 4)), NumberedCheckpoint(15, GridPosition(2, 6)), NumberedCheckpoint(16, GridPosition(4, 6)),
            NumberedCheckpoint(17, GridPosition(7, 5)), NumberedCheckpoint(18, GridPosition(7, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 3 - Level 92 (seed=3098)",
        seed = 3098L
    )

    val SOLUTION_92 = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7)
        )
    )

    // --- Level 93: World 3 - Level 93 (9x9) ---

    val LEVEL_93 = PuzzleDefinition(
        puzzleId = "w3_lvl93",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(3, 0)), NumberedCheckpoint(3, GridPosition(0, 4)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(1, 6)), NumberedCheckpoint(6, GridPosition(1, 2)), NumberedCheckpoint(7, GridPosition(6, 2)), NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(3, 8)), NumberedCheckpoint(10, GridPosition(8, 6)), NumberedCheckpoint(11, GridPosition(8, 2)), NumberedCheckpoint(12, GridPosition(7, 5)),
            NumberedCheckpoint(13, GridPosition(3, 6)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(6, 6)), NumberedCheckpoint(16, GridPosition(4, 6)),
            NumberedCheckpoint(17, GridPosition(5, 5)), NumberedCheckpoint(18, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6))
        ),
        difficultyMetadata = "World 3 - Level 93 (seed=3115)",
        seed = 3115L
    )

    val SOLUTION_93 = PuzzlePath(
        listOf(
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4)
        )
    )

    // --- Level 94: World 3 - Level 94 (9x9) ---

    val LEVEL_94 = PuzzleDefinition(
        puzzleId = "w3_lvl94",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 6)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(1, 5)), NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(6, 1)), NumberedCheckpoint(7, GridPosition(3, 5)), NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(5, 5)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(6, 2)), NumberedCheckpoint(12, GridPosition(8, 2)),
            NumberedCheckpoint(13, GridPosition(7, 3)), NumberedCheckpoint(14, GridPosition(8, 4)), NumberedCheckpoint(15, GridPosition(7, 6)), NumberedCheckpoint(16, GridPosition(7, 8)),
            NumberedCheckpoint(17, GridPosition(4, 8)), NumberedCheckpoint(18, GridPosition(1, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7))
        ),
        difficultyMetadata = "World 3 - Level 94 (seed=3100)",
        seed = 3100L
    )

    val SOLUTION_94 = PuzzlePath(
        listOf(
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(1, 7)
        )
    )

    // --- Level 95: World 3 - Level 95 (9x9) ---

    val LEVEL_95 = PuzzleDefinition(
        puzzleId = "w3_lvl95",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(8, 5)), NumberedCheckpoint(3, GridPosition(4, 8)), NumberedCheckpoint(4, GridPosition(1, 8)),
            NumberedCheckpoint(5, GridPosition(2, 7)), NumberedCheckpoint(6, GridPosition(6, 7)), NumberedCheckpoint(7, GridPosition(7, 4)), NumberedCheckpoint(8, GridPosition(7, 1)),
            NumberedCheckpoint(9, GridPosition(6, 4)), NumberedCheckpoint(10, GridPosition(5, 6)), NumberedCheckpoint(11, GridPosition(5, 2)), NumberedCheckpoint(12, GridPosition(0, 0)),
            NumberedCheckpoint(13, GridPosition(0, 4)), NumberedCheckpoint(14, GridPosition(2, 6)), NumberedCheckpoint(15, GridPosition(4, 1)), NumberedCheckpoint(16, GridPosition(1, 5)),
            NumberedCheckpoint(17, GridPosition(1, 3)), NumberedCheckpoint(18, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "World 3 - Level 95 (seed=3098)",
        seed = 3098L
    )

    val SOLUTION_95 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(1, 1)
        )
    )

    // --- Level 96: World 3 - Level 96 (9x9) ---

    val LEVEL_96 = PuzzleDefinition(
        puzzleId = "w3_lvl96",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(8, 1)), NumberedCheckpoint(3, GridPosition(2, 0)), NumberedCheckpoint(4, GridPosition(1, 8)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(5, 1)), NumberedCheckpoint(7, GridPosition(7, 6)), NumberedCheckpoint(8, GridPosition(6, 4)),
            NumberedCheckpoint(9, GridPosition(2, 4)), NumberedCheckpoint(10, GridPosition(5, 8)), NumberedCheckpoint(11, GridPosition(3, 7)), NumberedCheckpoint(12, GridPosition(3, 5)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(5, 3)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(4, 6)),
            NumberedCheckpoint(17, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 3 - Level 96 (seed=3119)",
        seed = 3119L
    )

    val SOLUTION_96 = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5)
        )
    )

    // --- Level 97: World 3 - Level 97 (9x9) ---

    val LEVEL_97 = PuzzleDefinition(
        puzzleId = "w3_lvl97",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 1)), NumberedCheckpoint(2, GridPosition(8, 3)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(4, 2)), NumberedCheckpoint(8, GridPosition(3, 8)),
            NumberedCheckpoint(9, GridPosition(0, 8)), NumberedCheckpoint(10, GridPosition(3, 5)), NumberedCheckpoint(11, GridPosition(3, 3)), NumberedCheckpoint(12, GridPosition(4, 0)),
            NumberedCheckpoint(13, GridPosition(2, 5)), NumberedCheckpoint(14, GridPosition(0, 6)), NumberedCheckpoint(15, GridPosition(1, 5)), NumberedCheckpoint(16, GridPosition(0, 4)),
            NumberedCheckpoint(17, GridPosition(1, 2)), NumberedCheckpoint(18, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2))
        ),
        difficultyMetadata = "World 3 - Level 97 (seed=3106)",
        seed = 3106L
    )

    val SOLUTION_97 = PuzzlePath(
        listOf(
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1)
        )
    )

    // --- Level 98: World 3 - Level 98 (9x9) ---

    val LEVEL_98 = PuzzleDefinition(
        puzzleId = "w3_lvl98",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(0, 3)), NumberedCheckpoint(3, GridPosition(2, 8)), NumberedCheckpoint(4, GridPosition(8, 7)),
            NumberedCheckpoint(5, GridPosition(8, 0)), NumberedCheckpoint(6, GridPosition(7, 0)), NumberedCheckpoint(7, GridPosition(7, 7)), NumberedCheckpoint(8, GridPosition(6, 1)),
            NumberedCheckpoint(9, GridPosition(1, 1)), NumberedCheckpoint(10, GridPosition(4, 2)), NumberedCheckpoint(11, GridPosition(5, 5)), NumberedCheckpoint(12, GridPosition(1, 6)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(4, 4)), NumberedCheckpoint(15, GridPosition(2, 4)), NumberedCheckpoint(16, GridPosition(2, 6)),
            NumberedCheckpoint(17, GridPosition(3, 5)), NumberedCheckpoint(18, GridPosition(4, 6))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 98 (seed=3110)",
        seed = 3110L
    )

    val SOLUTION_98 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6)
        )
    )

    // --- Level 99: World 3 - Level 99 (9x9) ---

    val LEVEL_99 = PuzzleDefinition(
        puzzleId = "w3_lvl99",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 7)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(1, 6)), NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(6, 2)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(1, 5)), NumberedCheckpoint(10, GridPosition(3, 8)), NumberedCheckpoint(11, GridPosition(6, 8)), NumberedCheckpoint(12, GridPosition(6, 6)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(4, 4)), NumberedCheckpoint(15, GridPosition(5, 5)), NumberedCheckpoint(16, GridPosition(5, 7)),
            NumberedCheckpoint(17, GridPosition(4, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7))
        ),
        difficultyMetadata = "World 3 - Level 99 (seed=3125)",
        seed = 3125L
    )

    val SOLUTION_99 = PuzzlePath(
        listOf(
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(4, 6)
        )
    )

    // --- Level 100: World 3 - Level 100 (9x9) ---

    val LEVEL_100 = PuzzleDefinition(
        puzzleId = "w3_lvl100",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(9, 9),
        requiredCells = grid9x9Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(0, 5)), NumberedCheckpoint(3, GridPosition(2, 0)), NumberedCheckpoint(4, GridPosition(8, 1)),
            NumberedCheckpoint(5, GridPosition(7, 8)), NumberedCheckpoint(6, GridPosition(1, 7)), NumberedCheckpoint(7, GridPosition(1, 6)), NumberedCheckpoint(8, GridPosition(6, 7)),
            NumberedCheckpoint(9, GridPosition(7, 1)), NumberedCheckpoint(10, GridPosition(1, 3)), NumberedCheckpoint(11, GridPosition(2, 5)), NumberedCheckpoint(12, GridPosition(3, 2)),
            NumberedCheckpoint(13, GridPosition(5, 5)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(4, 2)),
            NumberedCheckpoint(17, GridPosition(5, 3)), NumberedCheckpoint(18, GridPosition(6, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 3 - Level 100 (seed=3119)",
        seed = 3119L
    )

    val SOLUTION_100 = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 2)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[76] = LEVEL_76
        map[77] = LEVEL_77
        map[78] = LEVEL_78
        map[79] = LEVEL_79
        map[80] = LEVEL_80
        map[81] = LEVEL_81
        map[82] = LEVEL_82
        map[83] = LEVEL_83
        map[84] = LEVEL_84
        map[85] = LEVEL_85
        map[86] = LEVEL_86
        map[87] = LEVEL_87
        map[88] = LEVEL_88
        map[89] = LEVEL_89
        map[90] = LEVEL_90
        map[91] = LEVEL_91
        map[92] = LEVEL_92
        map[93] = LEVEL_93
        map[94] = LEVEL_94
        map[95] = LEVEL_95
        map[96] = LEVEL_96
        map[97] = LEVEL_97
        map[98] = LEVEL_98
        map[99] = LEVEL_99
        map[100] = LEVEL_100
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[76] = SOLUTION_76
        map[77] = SOLUTION_77
        map[78] = SOLUTION_78
        map[79] = SOLUTION_79
        map[80] = SOLUTION_80
        map[81] = SOLUTION_81
        map[82] = SOLUTION_82
        map[83] = SOLUTION_83
        map[84] = SOLUTION_84
        map[85] = SOLUTION_85
        map[86] = SOLUTION_86
        map[87] = SOLUTION_87
        map[88] = SOLUTION_88
        map[89] = SOLUTION_89
        map[90] = SOLUTION_90
        map[91] = SOLUTION_91
        map[92] = SOLUTION_92
        map[93] = SOLUTION_93
        map[94] = SOLUTION_94
        map[95] = SOLUTION_95
        map[96] = SOLUTION_96
        map[97] = SOLUTION_97
        map[98] = SOLUTION_98
        map[99] = SOLUTION_99
        map[100] = SOLUTION_100
        map
    }
}
