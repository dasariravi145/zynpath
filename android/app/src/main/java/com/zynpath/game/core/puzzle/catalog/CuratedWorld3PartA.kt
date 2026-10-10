package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld3PartA {

    fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    fun grid9x9Cells(): Set<GridPosition> =
        (0..8).flatMap { r -> (0..8).map { c -> GridPosition(r, c) } }.toSet()

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 51: World 3 - Level 51 (8x8) ---

    val LEVEL_51 = PuzzleDefinition(
        puzzleId = "w3_lvl51",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 7)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(6, 6)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(1, 0)), NumberedCheckpoint(6, GridPosition(3, 4)), NumberedCheckpoint(7, GridPosition(7, 4)), NumberedCheckpoint(8, GridPosition(3, 0)),
            NumberedCheckpoint(9, GridPosition(2, 1)), NumberedCheckpoint(10, GridPosition(3, 3)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(4, 2)),
            NumberedCheckpoint(13, GridPosition(6, 3)), NumberedCheckpoint(14, GridPosition(5, 2)), NumberedCheckpoint(15, GridPosition(6, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 51 (seed=3051)",
        seed = 3051L
    )

    val SOLUTION_51 = PuzzlePath(
        listOf(
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1)
        )
    )

    // --- Level 52: World 3 - Level 52 (8x8) ---

    val LEVEL_52 = PuzzleDefinition(
        puzzleId = "w3_lvl52",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(0, 2)), NumberedCheckpoint(6, GridPosition(2, 7)), NumberedCheckpoint(7, GridPosition(5, 7)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(5, 5)), NumberedCheckpoint(11, GridPosition(5, 3)), NumberedCheckpoint(12, GridPosition(3, 2)),
            NumberedCheckpoint(13, GridPosition(4, 3)), NumberedCheckpoint(14, GridPosition(3, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 3 - Level 52 (seed=3061)",
        seed = 3061L
    )

    val SOLUTION_52 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4)
        )
    )

    // --- Level 53: World 3 - Level 53 (8x8) ---

    val LEVEL_53 = PuzzleDefinition(
        puzzleId = "w3_lvl53",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(6, 6)),
            NumberedCheckpoint(5, GridPosition(4, 1)), NumberedCheckpoint(6, GridPosition(0, 4)), NumberedCheckpoint(7, GridPosition(5, 4)), NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(1, 3)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(4, 3)),
            NumberedCheckpoint(13, GridPosition(5, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3))
        ),
        difficultyMetadata = "World 3 - Level 53 (seed=3055)",
        seed = 3055L
    )

    val SOLUTION_53 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2)
        )
    )

    // --- Level 54: World 3 - Level 54 (8x8) ---

    val LEVEL_54 = PuzzleDefinition(
        puzzleId = "w3_lvl54",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 2)), NumberedCheckpoint(2, GridPosition(7, 4)), NumberedCheckpoint(3, GridPosition(6, 7)), NumberedCheckpoint(4, GridPosition(6, 4)),
            NumberedCheckpoint(5, GridPosition(5, 5)), NumberedCheckpoint(6, GridPosition(0, 7)), NumberedCheckpoint(7, GridPosition(3, 5)), NumberedCheckpoint(8, GridPosition(0, 1)),
            NumberedCheckpoint(9, GridPosition(6, 0)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(2, 4)),
            NumberedCheckpoint(13, GridPosition(4, 3)), NumberedCheckpoint(14, GridPosition(2, 3)), NumberedCheckpoint(15, GridPosition(3, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1))
        ),
        difficultyMetadata = "World 3 - Level 54 (seed=3055)",
        seed = 3055L
    )

    val SOLUTION_54 = PuzzlePath(
        listOf(
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2)
        )
    )

    // --- Level 55: World 3 - Level 55 (8x8) ---

    val LEVEL_55 = PuzzleDefinition(
        puzzleId = "w3_lvl55",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(1, 6)), NumberedCheckpoint(3, GridPosition(0, 0)), NumberedCheckpoint(4, GridPosition(7, 0)),
            NumberedCheckpoint(5, GridPosition(7, 7)), NumberedCheckpoint(6, GridPosition(6, 1)), NumberedCheckpoint(7, GridPosition(5, 7)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(4, 1)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(3, 3)), NumberedCheckpoint(12, GridPosition(2, 4)),
            NumberedCheckpoint(13, GridPosition(3, 5)), NumberedCheckpoint(14, GridPosition(2, 7)), NumberedCheckpoint(15, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 3 - Level 55 (seed=3090)",
        seed = 3090L
    )

    val SOLUTION_55 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6)
        )
    )

    // --- Level 56: World 3 - Level 56 (8x8) ---

    val LEVEL_56 = PuzzleDefinition(
        puzzleId = "w3_lvl56",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(3, 7)), NumberedCheckpoint(3, GridPosition(0, 2)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(5, 7)), NumberedCheckpoint(7, GridPosition(5, 4)), NumberedCheckpoint(8, GridPosition(2, 3)),
            NumberedCheckpoint(9, GridPosition(2, 0)), NumberedCheckpoint(10, GridPosition(4, 2)), NumberedCheckpoint(11, GridPosition(5, 0)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(6, 6)), NumberedCheckpoint(14, GridPosition(7, 5)), NumberedCheckpoint(15, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 3 - Level 56 (seed=3058)",
        seed = 3058L
    )

    val SOLUTION_56 = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1)
        )
    )

    // --- Level 57: World 3 - Level 57 (8x8) ---

    val LEVEL_57 = PuzzleDefinition(
        puzzleId = "w3_lvl57",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 7)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(3, 6)), NumberedCheckpoint(4, GridPosition(5, 7)),
            NumberedCheckpoint(5, GridPosition(3, 5)), NumberedCheckpoint(6, GridPosition(1, 0)), NumberedCheckpoint(7, GridPosition(2, 4)), NumberedCheckpoint(8, GridPosition(5, 4)),
            NumberedCheckpoint(9, GridPosition(2, 3)), NumberedCheckpoint(10, GridPosition(6, 0)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(7, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5))
        ),
        difficultyMetadata = "World 3 - Level 57 (seed=3106)",
        seed = 3106L
    )

    val SOLUTION_57 = PuzzlePath(
        listOf(
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6)
        )
    )

    // --- Level 58: World 3 - Level 58 (8x8) ---

    val LEVEL_58 = PuzzleDefinition(
        puzzleId = "w3_lvl58",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 7)), NumberedCheckpoint(2, GridPosition(6, 6)), NumberedCheckpoint(3, GridPosition(7, 3)), NumberedCheckpoint(4, GridPosition(7, 0)),
            NumberedCheckpoint(5, GridPosition(6, 3)), NumberedCheckpoint(6, GridPosition(5, 6)), NumberedCheckpoint(7, GridPosition(3, 4)), NumberedCheckpoint(8, GridPosition(2, 7)),
            NumberedCheckpoint(9, GridPosition(2, 4)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(1, 0)), NumberedCheckpoint(12, GridPosition(3, 3)),
            NumberedCheckpoint(13, GridPosition(5, 1)), NumberedCheckpoint(14, GridPosition(4, 1)), NumberedCheckpoint(15, GridPosition(3, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1))
        ),
        difficultyMetadata = "World 3 - Level 58 (seed=3084)",
        seed = 3084L
    )

    val SOLUTION_58 = PuzzlePath(
        listOf(
            GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3),
            GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4),
            GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4),
            GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1)
        )
    )

    // --- Level 59: World 3 - Level 59 (8x8) ---

    val LEVEL_59 = PuzzleDefinition(
        puzzleId = "w3_lvl59",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(6, 6)), NumberedCheckpoint(6, GridPosition(0, 5)), NumberedCheckpoint(7, GridPosition(6, 4)), NumberedCheckpoint(8, GridPosition(6, 1)),
            NumberedCheckpoint(9, GridPosition(4, 3)), NumberedCheckpoint(10, GridPosition(3, 1)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(1, 2)),
            NumberedCheckpoint(13, GridPosition(0, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 59 (seed=3063)",
        seed = 3063L
    )

    val SOLUTION_59 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )

    // --- Level 60: World 3 - Level 60 (8x8) ---

    val LEVEL_60 = PuzzleDefinition(
        puzzleId = "w3_lvl60",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 5)), NumberedCheckpoint(2, GridPosition(2, 7)), NumberedCheckpoint(3, GridPosition(1, 6)), NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(1, 1)), NumberedCheckpoint(7, GridPosition(3, 2)), NumberedCheckpoint(8, GridPosition(3, 7)),
            NumberedCheckpoint(9, GridPosition(4, 3)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(7, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "World 3 - Level 60 (seed=3083)",
        seed = 3083L
    )

    val SOLUTION_60 = PuzzlePath(
        listOf(
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6)
        )
    )

    // --- Level 61: World 3 - Level 61 (8x8) ---

    val LEVEL_61 = PuzzleDefinition(
        puzzleId = "w3_lvl61",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 1)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(2, 7)),
            NumberedCheckpoint(5, GridPosition(0, 6)), NumberedCheckpoint(6, GridPosition(5, 2)), NumberedCheckpoint(7, GridPosition(1, 5)), NumberedCheckpoint(8, GridPosition(1, 4)),
            NumberedCheckpoint(9, GridPosition(4, 3)), NumberedCheckpoint(10, GridPosition(3, 3)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(5, 0)),
            NumberedCheckpoint(13, GridPosition(1, 0)), NumberedCheckpoint(14, GridPosition(1, 1)), NumberedCheckpoint(15, GridPosition(1, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2))
        ),
        difficultyMetadata = "World 3 - Level 61 (seed=3062)",
        seed = 3062L
    )

    val SOLUTION_61 = PuzzlePath(
        listOf(
            GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5),
            GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7),
            GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6),
            GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5),
            GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4),
            GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2),
            GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3)
        )
    )

    // --- Level 62: World 3 - Level 62 (8x8) ---

    val LEVEL_62 = PuzzleDefinition(
        puzzleId = "w3_lvl62",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 0)), NumberedCheckpoint(2, GridPosition(0, 2)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(5, 2)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(6, 0)), NumberedCheckpoint(8, GridPosition(5, 4)),
            NumberedCheckpoint(9, GridPosition(3, 7)), NumberedCheckpoint(10, GridPosition(4, 5)), NumberedCheckpoint(11, GridPosition(5, 6)), NumberedCheckpoint(12, GridPosition(7, 5)),
            NumberedCheckpoint(13, GridPosition(6, 6)), NumberedCheckpoint(14, GridPosition(7, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4))
        ),
        difficultyMetadata = "World 3 - Level 62 (seed=3083)",
        seed = 3083L
    )

    val SOLUTION_62 = PuzzlePath(
        listOf(
            GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1),
            GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3),
            GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3),
            GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6),
            GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7),
            GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7)
        )
    )

    // --- Level 63: World 3 - Level 63 (8x8) ---

    val LEVEL_63 = PuzzleDefinition(
        puzzleId = "w3_lvl63",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 3)), NumberedCheckpoint(2, GridPosition(6, 0)), NumberedCheckpoint(3, GridPosition(0, 4)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(5, 1)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(2, 6)), NumberedCheckpoint(10, GridPosition(4, 7)), NumberedCheckpoint(11, GridPosition(7, 7)), NumberedCheckpoint(12, GridPosition(6, 6)),
            NumberedCheckpoint(13, GridPosition(4, 6)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(3, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 3 - Level 63 (seed=3102)",
        seed = 3102L
    )

    val SOLUTION_63 = PuzzlePath(
        listOf(
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4)
        )
    )

    // --- Level 64: World 3 - Level 64 (8x8) ---

    val LEVEL_64 = PuzzleDefinition(
        puzzleId = "w3_lvl64",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 7)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(6, 6)), NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(4, 0)), NumberedCheckpoint(6, GridPosition(4, 4)), NumberedCheckpoint(7, GridPosition(0, 7)), NumberedCheckpoint(8, GridPosition(0, 0)),
            NumberedCheckpoint(9, GridPosition(1, 3)), NumberedCheckpoint(10, GridPosition(1, 6)), NumberedCheckpoint(11, GridPosition(3, 5)), NumberedCheckpoint(12, GridPosition(2, 4)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(2, 2)), NumberedCheckpoint(15, GridPosition(2, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 64 (seed=3064)",
        seed = 3064L
    )

    val SOLUTION_64 = PuzzlePath(
        listOf(
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1)
        )
    )

    // --- Level 65: World 3 - Level 65 (8x8) ---

    val LEVEL_65 = PuzzleDefinition(
        puzzleId = "w3_lvl65",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(5, 1)), NumberedCheckpoint(4, GridPosition(6, 3)),
            NumberedCheckpoint(5, GridPosition(7, 4)), NumberedCheckpoint(6, GridPosition(5, 5)), NumberedCheckpoint(7, GridPosition(4, 2)), NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(2, 3)), NumberedCheckpoint(10, GridPosition(6, 6)), NumberedCheckpoint(11, GridPosition(3, 6)), NumberedCheckpoint(12, GridPosition(0, 6)),
            NumberedCheckpoint(13, GridPosition(2, 7)), NumberedCheckpoint(14, GridPosition(1, 6)), NumberedCheckpoint(15, GridPosition(2, 5))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 65 (seed=3065)",
        seed = 3065L
    )

    val SOLUTION_65 = PuzzlePath(
        listOf(
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5)
        )
    )

    // --- Level 66: World 3 - Level 66 (8x8) ---

    val LEVEL_66 = PuzzleDefinition(
        puzzleId = "w3_lvl66",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(2, 7)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(6, 0)), NumberedCheckpoint(8, GridPosition(5, 4)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 3 - Level 66 (seed=3072)",
        seed = 3072L
    )

    val SOLUTION_66 = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3)
        )
    )

    // --- Level 67: World 3 - Level 67 (8x8) ---

    val LEVEL_67 = PuzzleDefinition(
        puzzleId = "w3_lvl67",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(4, 0)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(5, 7)), NumberedCheckpoint(6, GridPosition(4, 1)), NumberedCheckpoint(7, GridPosition(0, 4)), NumberedCheckpoint(8, GridPosition(2, 7)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(1, 5)), NumberedCheckpoint(11, GridPosition(3, 5)), NumberedCheckpoint(12, GridPosition(3, 3)),
            NumberedCheckpoint(13, GridPosition(1, 2)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(2, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 3 - Level 67 (seed=3128)",
        seed = 3128L
    )

    val SOLUTION_67 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3)
        )
    )

    // --- Level 68: World 3 - Level 68 (8x8) ---

    val LEVEL_68 = PuzzleDefinition(
        puzzleId = "w3_lvl68",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 1)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(1, 5)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(7, 0)), NumberedCheckpoint(7, GridPosition(7, 7)), NumberedCheckpoint(8, GridPosition(3, 4)),
            NumberedCheckpoint(9, GridPosition(6, 2)), NumberedCheckpoint(10, GridPosition(4, 3)), NumberedCheckpoint(11, GridPosition(6, 5)), NumberedCheckpoint(12, GridPosition(4, 5)),
            NumberedCheckpoint(13, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5))
        ),
        difficultyMetadata = "World 3 - Level 68 (seed=3103)",
        seed = 3103L
    )

    val SOLUTION_68 = PuzzlePath(
        listOf(
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3),
            GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7),
            GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3),
            GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7),
            GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1),
            GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4),
            GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5)
        )
    )

    // --- Level 69: World 3 - Level 69 (8x8) ---

    val LEVEL_69 = PuzzleDefinition(
        puzzleId = "w3_lvl69",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 2)), NumberedCheckpoint(2, GridPosition(5, 7)), NumberedCheckpoint(3, GridPosition(1, 6)), NumberedCheckpoint(4, GridPosition(6, 4)),
            NumberedCheckpoint(5, GridPosition(5, 2)), NumberedCheckpoint(6, GridPosition(4, 5)), NumberedCheckpoint(7, GridPosition(7, 1)), NumberedCheckpoint(8, GridPosition(3, 2)),
            NumberedCheckpoint(9, GridPosition(3, 5)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(1, 0)), NumberedCheckpoint(12, GridPosition(1, 2)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(1, 5))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 69 (seed=3069)",
        seed = 3069L
    )

    val SOLUTION_69 = PuzzlePath(
        listOf(
            GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7),
            GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6),
            GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2),
            GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2),
            GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4),
            GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5)
        )
    )

    // --- Level 70: World 3 - Level 70 (8x8) ---

    val LEVEL_70 = PuzzleDefinition(
        puzzleId = "w3_lvl70",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(7, 6)), NumberedCheckpoint(3, GridPosition(6, 2)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)), NumberedCheckpoint(6, GridPosition(4, 1)), NumberedCheckpoint(7, GridPosition(4, 7)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(1, 2)), NumberedCheckpoint(11, GridPosition(1, 4)), NumberedCheckpoint(12, GridPosition(3, 5)),
            NumberedCheckpoint(13, GridPosition(0, 6)), NumberedCheckpoint(14, GridPosition(1, 6)), NumberedCheckpoint(15, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2))
        ),
        difficultyMetadata = "World 3 - Level 70 (seed=3071)",
        seed = 3071L
    )

    val SOLUTION_70 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6)
        )
    )

    // --- Level 71: World 3 - Level 71 (8x8) ---

    val LEVEL_71 = PuzzleDefinition(
        puzzleId = "w3_lvl71",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 3)), NumberedCheckpoint(2, GridPosition(4, 7)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(2, 6)),
            NumberedCheckpoint(5, GridPosition(6, 3)), NumberedCheckpoint(6, GridPosition(1, 5)), NumberedCheckpoint(7, GridPosition(0, 4)), NumberedCheckpoint(8, GridPosition(4, 3)),
            NumberedCheckpoint(9, GridPosition(1, 3)), NumberedCheckpoint(10, GridPosition(0, 2)), NumberedCheckpoint(11, GridPosition(6, 2)), NumberedCheckpoint(12, GridPosition(7, 0)),
            NumberedCheckpoint(13, GridPosition(5, 1)), NumberedCheckpoint(14, GridPosition(3, 1)), NumberedCheckpoint(15, GridPosition(1, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 3 - Level 71 (seed=3078)",
        seed = 3078L
    )

    val SOLUTION_71 = PuzzlePath(
        listOf(
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0)
        )
    )

    // --- Level 72: World 3 - Level 72 (8x8) ---

    val LEVEL_72 = PuzzleDefinition(
        puzzleId = "w3_lvl72",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 5)), NumberedCheckpoint(2, GridPosition(5, 7)), NumberedCheckpoint(3, GridPosition(7, 6)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(1, 5)), NumberedCheckpoint(6, GridPosition(5, 5)), NumberedCheckpoint(7, GridPosition(7, 4)), NumberedCheckpoint(8, GridPosition(3, 4)),
            NumberedCheckpoint(9, GridPosition(0, 4)), NumberedCheckpoint(10, GridPosition(3, 3)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(5, 2)),
            NumberedCheckpoint(13, GridPosition(0, 0)), NumberedCheckpoint(14, GridPosition(4, 1)), NumberedCheckpoint(15, GridPosition(6, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2))
        ),
        difficultyMetadata = "World 3 - Level 72 (seed=3110)",
        seed = 3110L
    )

    val SOLUTION_72 = PuzzlePath(
        listOf(
            GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7),
            GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6),
            GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5),
            GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3),
            GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1),
            GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0)
        )
    )

    // --- Level 73: World 3 - Level 73 (8x8) ---

    val LEVEL_73 = PuzzleDefinition(
        puzzleId = "w3_lvl73",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(1, 6)), NumberedCheckpoint(3, GridPosition(0, 0)), NumberedCheckpoint(4, GridPosition(7, 0)),
            NumberedCheckpoint(5, GridPosition(7, 7)), NumberedCheckpoint(6, GridPosition(6, 1)), NumberedCheckpoint(7, GridPosition(5, 7)), NumberedCheckpoint(8, GridPosition(2, 7)),
            NumberedCheckpoint(9, GridPosition(4, 5)), NumberedCheckpoint(10, GridPosition(2, 4)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(3, 2)),
            NumberedCheckpoint(13, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 3 - Level 73 (seed=3152)",
        seed = 3152L
    )

    val SOLUTION_73 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3)
        )
    )

    // --- Level 74: World 3 - Level 74 (8x8) ---

    val LEVEL_74 = PuzzleDefinition(
        puzzleId = "w3_lvl74",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 7)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(1, 5)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(3, 3)), NumberedCheckpoint(6, GridPosition(2, 0)), NumberedCheckpoint(7, GridPosition(4, 5)), NumberedCheckpoint(8, GridPosition(5, 7)),
            NumberedCheckpoint(9, GridPosition(5, 3)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(7, 0)), NumberedCheckpoint(12, GridPosition(6, 2)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(7, 6))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 74 (seed=3142)",
        seed = 3142L
    )

    val SOLUTION_74 = PuzzlePath(
        listOf(
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6)
        )
    )

    // --- Level 75: World 3 - Level 75 (8x8) ---

    val LEVEL_75 = PuzzleDefinition(
        puzzleId = "w3_lvl75",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 2)), NumberedCheckpoint(2, GridPosition(7, 7)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(6, 6)),
            NumberedCheckpoint(5, GridPosition(3, 5)), NumberedCheckpoint(6, GridPosition(2, 0)), NumberedCheckpoint(7, GridPosition(7, 0)), NumberedCheckpoint(8, GridPosition(1, 3)),
            NumberedCheckpoint(9, GridPosition(3, 4)), NumberedCheckpoint(10, GridPosition(5, 4)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(4, 2)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 3 - Level 75 (seed=3099)",
        seed = 3099L
    )

    val SOLUTION_75 = PuzzlePath(
        listOf(
            GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7),
            GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6),
            GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5),
            GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0),
            GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4),
            GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[51] = LEVEL_51
        map[52] = LEVEL_52
        map[53] = LEVEL_53
        map[54] = LEVEL_54
        map[55] = LEVEL_55
        map[56] = LEVEL_56
        map[57] = LEVEL_57
        map[58] = LEVEL_58
        map[59] = LEVEL_59
        map[60] = LEVEL_60
        map[61] = LEVEL_61
        map[62] = LEVEL_62
        map[63] = LEVEL_63
        map[64] = LEVEL_64
        map[65] = LEVEL_65
        map[66] = LEVEL_66
        map[67] = LEVEL_67
        map[68] = LEVEL_68
        map[69] = LEVEL_69
        map[70] = LEVEL_70
        map[71] = LEVEL_71
        map[72] = LEVEL_72
        map[73] = LEVEL_73
        map[74] = LEVEL_74
        map[75] = LEVEL_75
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[51] = SOLUTION_51
        map[52] = SOLUTION_52
        map[53] = SOLUTION_53
        map[54] = SOLUTION_54
        map[55] = SOLUTION_55
        map[56] = SOLUTION_56
        map[57] = SOLUTION_57
        map[58] = SOLUTION_58
        map[59] = SOLUTION_59
        map[60] = SOLUTION_60
        map[61] = SOLUTION_61
        map[62] = SOLUTION_62
        map[63] = SOLUTION_63
        map[64] = SOLUTION_64
        map[65] = SOLUTION_65
        map[66] = SOLUTION_66
        map[67] = SOLUTION_67
        map[68] = SOLUTION_68
        map[69] = SOLUTION_69
        map[70] = SOLUTION_70
        map[71] = SOLUTION_71
        map[72] = SOLUTION_72
        map[73] = SOLUTION_73
        map[74] = SOLUTION_74
        map[75] = SOLUTION_75
        map
    }
}
