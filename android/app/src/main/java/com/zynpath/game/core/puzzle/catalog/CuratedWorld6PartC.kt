package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6PartC {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 251: World 6 - Level 251 (12x12) ---

    fun buildLevel251(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl251",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(7, 1)), NumberedCheckpoint(4, GridPosition(5, 1)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(10, 1)), NumberedCheckpoint(7, GridPosition(10, 2)), NumberedCheckpoint(8, GridPosition(5, 6)),
            NumberedCheckpoint(9, GridPosition(3, 1)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(1, 1)), NumberedCheckpoint(12, GridPosition(2, 6)),
            NumberedCheckpoint(13, GridPosition(4, 7)), NumberedCheckpoint(14, GridPosition(1, 7)), NumberedCheckpoint(15, GridPosition(0, 10)), NumberedCheckpoint(16, GridPosition(2, 9)),
            NumberedCheckpoint(17, GridPosition(5, 10)), NumberedCheckpoint(18, GridPosition(2, 11)), NumberedCheckpoint(19, GridPosition(10, 10)), NumberedCheckpoint(20, GridPosition(11, 9)),
            NumberedCheckpoint(21, GridPosition(10, 8)), NumberedCheckpoint(22, GridPosition(9, 9)), NumberedCheckpoint(23, GridPosition(9, 8)), NumberedCheckpoint(24, GridPosition(8, 7)),
            NumberedCheckpoint(25, GridPosition(11, 3)), NumberedCheckpoint(26, GridPosition(9, 6)), NumberedCheckpoint(27, GridPosition(8, 5)), NumberedCheckpoint(28, GridPosition(9, 4)),
            NumberedCheckpoint(29, GridPosition(7, 7)), NumberedCheckpoint(30, GridPosition(6, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10))
        ),
        difficultyMetadata = "World 6 - Level 251 (seed=6252)",
        seed = 6252L
    )

    fun buildSolution251(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2),
            GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7)
        )
    )

    // --- Level 252: World 6 - Level 252 (12x12) ---

    fun buildLevel252(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl252",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 4)), NumberedCheckpoint(2, GridPosition(4, 2)), NumberedCheckpoint(3, GridPosition(6, 2)), NumberedCheckpoint(4, GridPosition(7, 3)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(10, 2)), NumberedCheckpoint(7, GridPosition(10, 4)), NumberedCheckpoint(8, GridPosition(11, 4)),
            NumberedCheckpoint(9, GridPosition(9, 6)), NumberedCheckpoint(10, GridPosition(10, 7)), NumberedCheckpoint(11, GridPosition(6, 8)), NumberedCheckpoint(12, GridPosition(8, 5)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(3, 2)), NumberedCheckpoint(16, GridPosition(5, 9)),
            NumberedCheckpoint(17, GridPosition(8, 10)), NumberedCheckpoint(18, GridPosition(8, 9)), NumberedCheckpoint(19, GridPosition(10, 10)), NumberedCheckpoint(20, GridPosition(9, 9)),
            NumberedCheckpoint(21, GridPosition(9, 11)), NumberedCheckpoint(22, GridPosition(5, 10)), NumberedCheckpoint(23, GridPosition(4, 8)), NumberedCheckpoint(24, GridPosition(3, 7)),
            NumberedCheckpoint(25, GridPosition(0, 11)), NumberedCheckpoint(26, GridPosition(1, 8)), NumberedCheckpoint(27, GridPosition(1, 10)), NumberedCheckpoint(28, GridPosition(1, 4)),
            NumberedCheckpoint(29, GridPosition(2, 1)), NumberedCheckpoint(30, GridPosition(0, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9))
        ),
        difficultyMetadata = "World 6 - Level 252 (seed=6253)",
        seed = 6253L
    )

    fun buildSolution252(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2),
            GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 10),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 11), GridPosition(4, 11),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )

    // --- Level 253: World 6 - Level 253 (12x12) ---

    fun buildLevel253(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl253",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(9, 3)), NumberedCheckpoint(3, GridPosition(11, 0)), NumberedCheckpoint(4, GridPosition(8, 1)),
            NumberedCheckpoint(5, GridPosition(10, 5)), NumberedCheckpoint(6, GridPosition(9, 5)), NumberedCheckpoint(7, GridPosition(11, 6)), NumberedCheckpoint(8, GridPosition(7, 8)),
            NumberedCheckpoint(9, GridPosition(2, 9)), NumberedCheckpoint(10, GridPosition(1, 8)), NumberedCheckpoint(11, GridPosition(2, 7)), NumberedCheckpoint(12, GridPosition(0, 6)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(2, 6)), NumberedCheckpoint(16, GridPosition(3, 9)),
            NumberedCheckpoint(17, GridPosition(4, 10)), NumberedCheckpoint(18, GridPosition(4, 3)), NumberedCheckpoint(19, GridPosition(0, 2)), NumberedCheckpoint(20, GridPosition(3, 0)),
            NumberedCheckpoint(21, GridPosition(3, 2)), NumberedCheckpoint(22, GridPosition(7, 1)), NumberedCheckpoint(23, GridPosition(5, 1)), NumberedCheckpoint(24, GridPosition(6, 3)),
            NumberedCheckpoint(25, GridPosition(5, 4)), NumberedCheckpoint(26, GridPosition(8, 6)), NumberedCheckpoint(27, GridPosition(5, 8)), NumberedCheckpoint(28, GridPosition(6, 10)),
            NumberedCheckpoint(29, GridPosition(9, 7)), NumberedCheckpoint(30, GridPosition(9, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(11, 7), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(0, 10)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1))
        ),
        difficultyMetadata = "World 6 - Level 253 (seed=6253)",
        seed = 6253L
    )

    fun buildSolution253(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10)
        )
    )

    // --- Level 254: World 6 - Level 254 (12x12) ---

    fun buildLevel254(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl254",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 9)), NumberedCheckpoint(2, GridPosition(5, 9)), NumberedCheckpoint(3, GridPosition(11, 10)), NumberedCheckpoint(4, GridPosition(4, 7)),
            NumberedCheckpoint(5, GridPosition(3, 11)), NumberedCheckpoint(6, GridPosition(0, 5)), NumberedCheckpoint(7, GridPosition(10, 1)), NumberedCheckpoint(8, GridPosition(5, 1)),
            NumberedCheckpoint(9, GridPosition(7, 2)), NumberedCheckpoint(10, GridPosition(11, 2)), NumberedCheckpoint(11, GridPosition(11, 5)), NumberedCheckpoint(12, GridPosition(10, 8)),
            NumberedCheckpoint(13, GridPosition(9, 6)), NumberedCheckpoint(14, GridPosition(8, 8)), NumberedCheckpoint(15, GridPosition(7, 7)), NumberedCheckpoint(16, GridPosition(6, 6)),
            NumberedCheckpoint(17, GridPosition(5, 5)), NumberedCheckpoint(18, GridPosition(8, 4)), NumberedCheckpoint(19, GridPosition(7, 3)), NumberedCheckpoint(20, GridPosition(3, 4)),
            NumberedCheckpoint(21, GridPosition(2, 5)), NumberedCheckpoint(22, GridPosition(2, 3)), NumberedCheckpoint(23, GridPosition(4, 2)), NumberedCheckpoint(24, GridPosition(1, 6)),
            NumberedCheckpoint(25, GridPosition(2, 10)), NumberedCheckpoint(26, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(10, 10), GridPosition(11, 10)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(11, 3), GridPosition(11, 4)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 6 - Level 254 (seed=6259)",
        seed = 6259L
    )

    fun buildSolution254(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6)
        )
    )

    // --- Level 255: World 6 - Level 255 (12x12) ---

    fun buildLevel255(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl255",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(10, 0)), NumberedCheckpoint(3, GridPosition(11, 7)), NumberedCheckpoint(4, GridPosition(8, 11)),
            NumberedCheckpoint(5, GridPosition(2, 10)), NumberedCheckpoint(6, GridPosition(3, 6)), NumberedCheckpoint(7, GridPosition(4, 8)), NumberedCheckpoint(8, GridPosition(4, 10)),
            NumberedCheckpoint(9, GridPosition(7, 6)), NumberedCheckpoint(10, GridPosition(8, 9)), NumberedCheckpoint(11, GridPosition(8, 8)), NumberedCheckpoint(12, GridPosition(10, 9)),
            NumberedCheckpoint(13, GridPosition(8, 6)), NumberedCheckpoint(14, GridPosition(9, 6)), NumberedCheckpoint(15, GridPosition(10, 5)), NumberedCheckpoint(16, GridPosition(9, 3)),
            NumberedCheckpoint(17, GridPosition(6, 3)), NumberedCheckpoint(18, GridPosition(6, 1)), NumberedCheckpoint(19, GridPosition(2, 0)), NumberedCheckpoint(20, GridPosition(4, 2)),
            NumberedCheckpoint(21, GridPosition(2, 3)), NumberedCheckpoint(22, GridPosition(3, 3)), NumberedCheckpoint(23, GridPosition(4, 3)), NumberedCheckpoint(24, GridPosition(8, 4)),
            NumberedCheckpoint(25, GridPosition(1, 2)), NumberedCheckpoint(26, GridPosition(0, 7)), NumberedCheckpoint(27, GridPosition(0, 11)), NumberedCheckpoint(28, GridPosition(2, 5)),
            NumberedCheckpoint(29, GridPosition(1, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(10, 1), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(9, 11), GridPosition(10, 11)),
            BlockedEdge.between(GridPosition(7, 11), GridPosition(8, 11)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0))
        ),
        difficultyMetadata = "World 6 - Level 255 (seed=6260)",
        seed = 6260L
    )

    fun buildSolution255(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6)
        )
    )

    // --- Level 256: World 6 - Level 256 (12x12) ---

    fun buildLevel256(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl256",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)), NumberedCheckpoint(2, GridPosition(1, 1)), NumberedCheckpoint(3, GridPosition(2, 3)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(0, 5)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(6, 1)), NumberedCheckpoint(8, GridPosition(8, 2)),
            NumberedCheckpoint(9, GridPosition(5, 3)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(4, 8)), NumberedCheckpoint(12, GridPosition(1, 9)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(1, 6)), NumberedCheckpoint(15, GridPosition(2, 10)), NumberedCheckpoint(16, GridPosition(9, 11)),
            NumberedCheckpoint(17, GridPosition(11, 8)), NumberedCheckpoint(18, GridPosition(9, 8)), NumberedCheckpoint(19, GridPosition(8, 4)), NumberedCheckpoint(20, GridPosition(6, 6)),
            NumberedCheckpoint(21, GridPosition(7, 8)), NumberedCheckpoint(22, GridPosition(8, 10)), NumberedCheckpoint(23, GridPosition(5, 10)), NumberedCheckpoint(24, GridPosition(5, 8)),
            NumberedCheckpoint(25, GridPosition(5, 4)), NumberedCheckpoint(26, GridPosition(7, 3)), NumberedCheckpoint(27, GridPosition(10, 5)), NumberedCheckpoint(28, GridPosition(10, 1)),
            NumberedCheckpoint(29, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(11, 6), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(10, 10), GridPosition(11, 10))
        ),
        difficultyMetadata = "World 6 - Level 256 (seed=6256)",
        seed = 6256L
    )

    fun buildSolution256(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11),
            GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(10, 2), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0)
        )
    )

    // --- Level 257: World 6 - Level 257 (12x12) ---

    fun buildLevel257(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl257",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 6)), NumberedCheckpoint(2, GridPosition(9, 0)), NumberedCheckpoint(3, GridPosition(11, 2)), NumberedCheckpoint(4, GridPosition(10, 1)),
            NumberedCheckpoint(5, GridPosition(9, 3)), NumberedCheckpoint(6, GridPosition(10, 3)), NumberedCheckpoint(7, GridPosition(9, 6)), NumberedCheckpoint(8, GridPosition(8, 8)),
            NumberedCheckpoint(9, GridPosition(11, 7)), NumberedCheckpoint(10, GridPosition(8, 9)), NumberedCheckpoint(11, GridPosition(9, 10)), NumberedCheckpoint(12, GridPosition(11, 11)),
            NumberedCheckpoint(13, GridPosition(6, 7)), NumberedCheckpoint(14, GridPosition(6, 11)), NumberedCheckpoint(15, GridPosition(3, 9)), NumberedCheckpoint(16, GridPosition(4, 11)),
            NumberedCheckpoint(17, GridPosition(1, 8)), NumberedCheckpoint(18, GridPosition(5, 8)), NumberedCheckpoint(19, GridPosition(2, 6)), NumberedCheckpoint(20, GridPosition(4, 4)),
            NumberedCheckpoint(21, GridPosition(6, 5)), NumberedCheckpoint(22, GridPosition(3, 3)), NumberedCheckpoint(23, GridPosition(4, 2)), NumberedCheckpoint(24, GridPosition(7, 4)),
            NumberedCheckpoint(25, GridPosition(7, 0)), NumberedCheckpoint(26, GridPosition(2, 3)), NumberedCheckpoint(27, GridPosition(2, 5)), NumberedCheckpoint(28, GridPosition(0, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(10, 4))
        ),
        difficultyMetadata = "World 6 - Level 257 (seed=6257)",
        seed = 6257L
    )

    fun buildSolution257(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )

    // --- Level 258: World 6 - Level 258 (12x12) ---

    fun buildLevel258(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl258",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(6, 8)), NumberedCheckpoint(3, GridPosition(4, 8)), NumberedCheckpoint(4, GridPosition(5, 10)),
            NumberedCheckpoint(5, GridPosition(1, 9)), NumberedCheckpoint(6, GridPosition(2, 8)), NumberedCheckpoint(7, GridPosition(0, 9)), NumberedCheckpoint(8, GridPosition(4, 11)),
            NumberedCheckpoint(9, GridPosition(10, 9)), NumberedCheckpoint(10, GridPosition(9, 9)), NumberedCheckpoint(11, GridPosition(7, 7)), NumberedCheckpoint(12, GridPosition(6, 6)),
            NumberedCheckpoint(13, GridPosition(9, 6)), NumberedCheckpoint(14, GridPosition(11, 6)), NumberedCheckpoint(15, GridPosition(10, 5)), NumberedCheckpoint(16, GridPosition(10, 2)),
            NumberedCheckpoint(17, GridPosition(8, 1)), NumberedCheckpoint(18, GridPosition(9, 3)), NumberedCheckpoint(19, GridPosition(7, 3)), NumberedCheckpoint(20, GridPosition(5, 4)),
            NumberedCheckpoint(21, GridPosition(9, 4)), NumberedCheckpoint(22, GridPosition(4, 2)), NumberedCheckpoint(23, GridPosition(7, 0)), NumberedCheckpoint(24, GridPosition(4, 0)),
            NumberedCheckpoint(25, GridPosition(1, 2)), NumberedCheckpoint(26, GridPosition(2, 6)), NumberedCheckpoint(27, GridPosition(5, 6)), NumberedCheckpoint(28, GridPosition(2, 5)),
            NumberedCheckpoint(29, GridPosition(2, 4)), NumberedCheckpoint(30, GridPosition(2, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(11, 7), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(9, 10), GridPosition(9, 11)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10))
        ),
        difficultyMetadata = "World 6 - Level 258 (seed=6259)",
        seed = 6259L
    )

    fun buildSolution258(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5), GridPosition(10, 4), GridPosition(11, 4),
            GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1)
        )
    )

    // --- Level 259: World 6 - Level 259 (12x12) ---

    fun buildLevel259(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl259",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(9, 2)), NumberedCheckpoint(6, GridPosition(3, 3)), NumberedCheckpoint(7, GridPosition(0, 5)), NumberedCheckpoint(8, GridPosition(2, 5)),
            NumberedCheckpoint(9, GridPosition(3, 5)), NumberedCheckpoint(10, GridPosition(6, 7)), NumberedCheckpoint(11, GridPosition(5, 10)), NumberedCheckpoint(12, GridPosition(4, 8)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(4, 7)), NumberedCheckpoint(15, GridPosition(1, 7)), NumberedCheckpoint(16, GridPosition(1, 8)),
            NumberedCheckpoint(17, GridPosition(1, 10)), NumberedCheckpoint(18, GridPosition(8, 9)), NumberedCheckpoint(19, GridPosition(8, 6)), NumberedCheckpoint(20, GridPosition(10, 7)),
            NumberedCheckpoint(21, GridPosition(8, 5)), NumberedCheckpoint(22, GridPosition(8, 3)), NumberedCheckpoint(23, GridPosition(10, 4)), NumberedCheckpoint(24, GridPosition(11, 1)),
            NumberedCheckpoint(25, GridPosition(10, 9)), NumberedCheckpoint(26, GridPosition(9, 8)), NumberedCheckpoint(27, GridPosition(10, 10)), NumberedCheckpoint(28, GridPosition(8, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8))
        ),
        difficultyMetadata = "World 6 - Level 259 (seed=6260)",
        seed = 6260L
    )

    fun buildSolution259(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(6, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11)
        )
    )

    // --- Level 260: World 6 - Level 260 (12x12) ---

    fun buildLevel260(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl260",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 0)), NumberedCheckpoint(2, GridPosition(2, 4)), NumberedCheckpoint(3, GridPosition(3, 3)), NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(10, 5)), NumberedCheckpoint(7, GridPosition(8, 2)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(8, 4)), NumberedCheckpoint(10, GridPosition(11, 6)), NumberedCheckpoint(11, GridPosition(10, 10)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(7, 5)), NumberedCheckpoint(14, GridPosition(7, 6)), NumberedCheckpoint(15, GridPosition(5, 5)), NumberedCheckpoint(16, GridPosition(4, 6)),
            NumberedCheckpoint(17, GridPosition(1, 4)), NumberedCheckpoint(18, GridPosition(1, 0)), NumberedCheckpoint(19, GridPosition(0, 8)), NumberedCheckpoint(20, GridPosition(3, 6)),
            NumberedCheckpoint(21, GridPosition(2, 8)), NumberedCheckpoint(22, GridPosition(6, 10)), NumberedCheckpoint(23, GridPosition(6, 7)), NumberedCheckpoint(24, GridPosition(9, 8)),
            NumberedCheckpoint(25, GridPosition(7, 10)), NumberedCheckpoint(26, GridPosition(8, 10)), NumberedCheckpoint(27, GridPosition(2, 9)), NumberedCheckpoint(28, GridPosition(3, 11)),
            NumberedCheckpoint(29, GridPosition(1, 9)), NumberedCheckpoint(30, GridPosition(0, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(10, 1)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(10, 10), GridPosition(10, 11)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(0, 9)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(5, 9))
        ),
        difficultyMetadata = "World 6 - Level 260 (seed=6260)",
        seed = 6260L
    )

    fun buildSolution260(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11)
        )
    )

    // --- Level 261: World 6 - Level 261 (12x12) ---

    fun buildLevel261(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl261",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 8)), NumberedCheckpoint(2, GridPosition(9, 2)), NumberedCheckpoint(3, GridPosition(11, 9)), NumberedCheckpoint(4, GridPosition(5, 7)),
            NumberedCheckpoint(5, GridPosition(8, 6)), NumberedCheckpoint(6, GridPosition(8, 5)), NumberedCheckpoint(7, GridPosition(5, 5)), NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(4, 7)), NumberedCheckpoint(10, GridPosition(2, 7)), NumberedCheckpoint(11, GridPosition(3, 3)), NumberedCheckpoint(12, GridPosition(6, 1)),
            NumberedCheckpoint(13, GridPosition(7, 2)), NumberedCheckpoint(14, GridPosition(11, 1)), NumberedCheckpoint(15, GridPosition(7, 0)), NumberedCheckpoint(16, GridPosition(4, 1)),
            NumberedCheckpoint(17, GridPosition(0, 2)), NumberedCheckpoint(18, GridPosition(1, 3)), NumberedCheckpoint(19, GridPosition(1, 4)), NumberedCheckpoint(20, GridPosition(1, 5)),
            NumberedCheckpoint(21, GridPosition(1, 7)), NumberedCheckpoint(22, GridPosition(0, 11)), NumberedCheckpoint(23, GridPosition(2, 10)), NumberedCheckpoint(24, GridPosition(1, 10)),
            NumberedCheckpoint(25, GridPosition(5, 10)), NumberedCheckpoint(26, GridPosition(4, 9)), NumberedCheckpoint(27, GridPosition(8, 9)), NumberedCheckpoint(28, GridPosition(6, 10)),
            NumberedCheckpoint(29, GridPosition(8, 10)), NumberedCheckpoint(30, GridPosition(7, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(8, 11), GridPosition(9, 11)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3))
        ),
        difficultyMetadata = "World 6 - Level 261 (seed=6261)",
        seed = 6261L
    )

    fun buildSolution261(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(7, 10)
        )
    )

    // --- Level 262: World 6 - Level 262 (12x12) ---

    fun buildLevel262(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl262",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 9)), NumberedCheckpoint(2, GridPosition(1, 6)), NumberedCheckpoint(3, GridPosition(2, 10)), NumberedCheckpoint(4, GridPosition(4, 9)),
            NumberedCheckpoint(5, GridPosition(7, 9)), NumberedCheckpoint(6, GridPosition(8, 8)), NumberedCheckpoint(7, GridPosition(9, 10)), NumberedCheckpoint(8, GridPosition(7, 11)),
            NumberedCheckpoint(9, GridPosition(7, 6)), NumberedCheckpoint(10, GridPosition(10, 6)), NumberedCheckpoint(11, GridPosition(11, 2)), NumberedCheckpoint(12, GridPosition(9, 4)),
            NumberedCheckpoint(13, GridPosition(11, 1)), NumberedCheckpoint(14, GridPosition(7, 4)), NumberedCheckpoint(15, GridPosition(5, 7)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(7, 2)), NumberedCheckpoint(18, GridPosition(5, 2)), NumberedCheckpoint(19, GridPosition(7, 0)), NumberedCheckpoint(20, GridPosition(2, 6)),
            NumberedCheckpoint(21, GridPosition(3, 6)), NumberedCheckpoint(22, GridPosition(3, 1)), NumberedCheckpoint(23, GridPosition(0, 1)), NumberedCheckpoint(24, GridPosition(2, 3)),
            NumberedCheckpoint(25, GridPosition(1, 5)), NumberedCheckpoint(26, GridPosition(1, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(5, 11)),
            BlockedEdge.between(GridPosition(3, 11), GridPosition(4, 11)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 6 - Level 262 (seed=6263)",
        seed = 6263L
    )

    fun buildSolution262(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2)
        )
    )

    // --- Level 263: World 6 - Level 263 (12x12) ---

    fun buildLevel263(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl263",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 9)), NumberedCheckpoint(2, GridPosition(10, 8)), NumberedCheckpoint(3, GridPosition(9, 7)), NumberedCheckpoint(4, GridPosition(10, 9)),
            NumberedCheckpoint(5, GridPosition(8, 10)), NumberedCheckpoint(6, GridPosition(7, 10)), NumberedCheckpoint(7, GridPosition(4, 11)), NumberedCheckpoint(8, GridPosition(0, 10)),
            NumberedCheckpoint(9, GridPosition(1, 4)), NumberedCheckpoint(10, GridPosition(4, 2)), NumberedCheckpoint(11, GridPosition(2, 2)), NumberedCheckpoint(12, GridPosition(3, 0)),
            NumberedCheckpoint(13, GridPosition(6, 2)), NumberedCheckpoint(14, GridPosition(7, 2)), NumberedCheckpoint(15, GridPosition(10, 0)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(11, 4)), NumberedCheckpoint(18, GridPosition(10, 4)), NumberedCheckpoint(19, GridPosition(6, 4)), NumberedCheckpoint(20, GridPosition(3, 4)),
            NumberedCheckpoint(21, GridPosition(7, 5)), NumberedCheckpoint(22, GridPosition(8, 6)), NumberedCheckpoint(23, GridPosition(5, 6)), NumberedCheckpoint(24, GridPosition(2, 5)),
            NumberedCheckpoint(25, GridPosition(1, 9)), NumberedCheckpoint(26, GridPosition(5, 10)), NumberedCheckpoint(27, GridPosition(2, 9)), NumberedCheckpoint(28, GridPosition(4, 8)),
            NumberedCheckpoint(29, GridPosition(6, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(11, 6), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(10, 0)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(11, 4)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2))
        ),
        difficultyMetadata = "World 6 - Level 263 (seed=6264)",
        seed = 6264L
    )

    fun buildSolution263(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9)
        )
    )

    // --- Level 264: World 6 - Level 264 (12x12) ---

    fun buildLevel264(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl264",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 10)), NumberedCheckpoint(2, GridPosition(9, 7)), NumberedCheckpoint(3, GridPosition(10, 11)), NumberedCheckpoint(4, GridPosition(8, 10)),
            NumberedCheckpoint(5, GridPosition(4, 9)), NumberedCheckpoint(6, GridPosition(6, 11)), NumberedCheckpoint(7, GridPosition(0, 8)), NumberedCheckpoint(8, GridPosition(3, 10)),
            NumberedCheckpoint(9, GridPosition(2, 6)), NumberedCheckpoint(10, GridPosition(4, 8)), NumberedCheckpoint(11, GridPosition(5, 8)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(6, 6)), NumberedCheckpoint(14, GridPosition(8, 6)), NumberedCheckpoint(15, GridPosition(11, 2)), NumberedCheckpoint(16, GridPosition(10, 3)),
            NumberedCheckpoint(17, GridPosition(7, 5)), NumberedCheckpoint(18, GridPosition(5, 4)), NumberedCheckpoint(19, GridPosition(4, 1)), NumberedCheckpoint(20, GridPosition(3, 5)),
            NumberedCheckpoint(21, GridPosition(1, 7)), NumberedCheckpoint(22, GridPosition(1, 4)), NumberedCheckpoint(23, GridPosition(3, 1)), NumberedCheckpoint(24, GridPosition(1, 3)),
            NumberedCheckpoint(25, GridPosition(0, 1)), NumberedCheckpoint(26, GridPosition(7, 1)), NumberedCheckpoint(27, GridPosition(7, 2)), NumberedCheckpoint(28, GridPosition(9, 4)),
            NumberedCheckpoint(29, GridPosition(10, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(3, 10), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(10, 1)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(10, 10)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(8, 11)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1))
        ),
        difficultyMetadata = "World 6 - Level 264 (seed=6264)",
        seed = 6264L
    )

    fun buildSolution264(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(7, 11), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1)
        )
    )

    // --- Level 265: World 6 - Level 265 (12x12) ---

    fun buildLevel265(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl265",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(1, 2)), NumberedCheckpoint(3, GridPosition(5, 4)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(0, 8)), NumberedCheckpoint(6, GridPosition(1, 6)), NumberedCheckpoint(7, GridPosition(2, 9)), NumberedCheckpoint(8, GridPosition(1, 11)),
            NumberedCheckpoint(9, GridPosition(4, 10)), NumberedCheckpoint(10, GridPosition(5, 8)), NumberedCheckpoint(11, GridPosition(7, 9)), NumberedCheckpoint(12, GridPosition(5, 7)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(5, 6)), NumberedCheckpoint(15, GridPosition(5, 3)), NumberedCheckpoint(16, GridPosition(2, 2)),
            NumberedCheckpoint(17, GridPosition(0, 1)), NumberedCheckpoint(18, GridPosition(6, 1)), NumberedCheckpoint(19, GridPosition(7, 0)), NumberedCheckpoint(20, GridPosition(8, 4)),
            NumberedCheckpoint(21, GridPosition(8, 0)), NumberedCheckpoint(22, GridPosition(10, 3)), NumberedCheckpoint(23, GridPosition(11, 1)), NumberedCheckpoint(24, GridPosition(11, 5)),
            NumberedCheckpoint(25, GridPosition(9, 10)), NumberedCheckpoint(26, GridPosition(10, 6)), NumberedCheckpoint(27, GridPosition(8, 5)), NumberedCheckpoint(28, GridPosition(9, 6)),
            NumberedCheckpoint(29, GridPosition(9, 9)), NumberedCheckpoint(30, GridPosition(6, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(7, 11)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(1, 8))
        ),
        difficultyMetadata = "World 6 - Level 265 (seed=6271)",
        seed = 6271L
    )

    fun buildSolution265(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11)
        )
    )

    // --- Level 266: World 6 - Level 266 (12x12) ---

    fun buildLevel266(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl266",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 10)), NumberedCheckpoint(2, GridPosition(4, 10)), NumberedCheckpoint(3, GridPosition(5, 8)), NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(7, 5)), NumberedCheckpoint(6, GridPosition(5, 2)), NumberedCheckpoint(7, GridPosition(6, 0)), NumberedCheckpoint(8, GridPosition(8, 1)),
            NumberedCheckpoint(9, GridPosition(9, 4)), NumberedCheckpoint(10, GridPosition(8, 5)), NumberedCheckpoint(11, GridPosition(10, 0)), NumberedCheckpoint(12, GridPosition(10, 7)),
            NumberedCheckpoint(13, GridPosition(6, 7)), NumberedCheckpoint(14, GridPosition(7, 8)), NumberedCheckpoint(15, GridPosition(8, 9)), NumberedCheckpoint(16, GridPosition(9, 8)),
            NumberedCheckpoint(17, GridPosition(10, 10)), NumberedCheckpoint(18, GridPosition(10, 8)), NumberedCheckpoint(19, GridPosition(8, 11)), NumberedCheckpoint(20, GridPosition(2, 8)),
            NumberedCheckpoint(21, GridPosition(0, 11)), NumberedCheckpoint(22, GridPosition(4, 8)), NumberedCheckpoint(23, GridPosition(4, 3)), NumberedCheckpoint(24, GridPosition(2, 2)),
            NumberedCheckpoint(25, GridPosition(3, 2)), NumberedCheckpoint(26, GridPosition(1, 1)), NumberedCheckpoint(27, GridPosition(3, 4)), NumberedCheckpoint(28, GridPosition(1, 5)),
            NumberedCheckpoint(29, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(10, 5), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(11, 6)),
            BlockedEdge.between(GridPosition(11, 7), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6))
        ),
        difficultyMetadata = "World 6 - Level 266 (seed=6266)",
        seed = 6266L
    )

    fun buildSolution266(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6)
        )
    )

    // --- Level 267: World 6 - Level 267 (12x12) ---

    fun buildLevel267(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl267",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(4, 1)), NumberedCheckpoint(4, GridPosition(7, 3)),
            NumberedCheckpoint(5, GridPosition(4, 3)), NumberedCheckpoint(6, GridPosition(6, 5)), NumberedCheckpoint(7, GridPosition(10, 4)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(10, 1)), NumberedCheckpoint(10, GridPosition(10, 0)), NumberedCheckpoint(11, GridPosition(10, 7)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(5, 7)), NumberedCheckpoint(15, GridPosition(2, 9)), NumberedCheckpoint(16, GridPosition(3, 10)),
            NumberedCheckpoint(17, GridPosition(10, 10)), NumberedCheckpoint(18, GridPosition(10, 8)), NumberedCheckpoint(19, GridPosition(11, 11)), NumberedCheckpoint(20, GridPosition(7, 9)),
            NumberedCheckpoint(21, GridPosition(6, 11)), NumberedCheckpoint(22, GridPosition(0, 10)), NumberedCheckpoint(23, GridPosition(1, 2)), NumberedCheckpoint(24, GridPosition(2, 2)),
            NumberedCheckpoint(25, GridPosition(3, 3)), NumberedCheckpoint(26, GridPosition(1, 4)), NumberedCheckpoint(27, GridPosition(1, 8)), NumberedCheckpoint(28, GridPosition(3, 6)),
            NumberedCheckpoint(29, GridPosition(2, 6)), NumberedCheckpoint(30, GridPosition(4, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(11, 7), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9))
        ),
        difficultyMetadata = "World 6 - Level 267 (seed=6268)",
        seed = 6268L
    )

    fun buildSolution267(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7),
            GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7)
        )
    )

    // --- Level 268: World 6 - Level 268 (12x12) ---

    fun buildLevel268(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl268",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 6)), NumberedCheckpoint(2, GridPosition(9, 5)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(4, 3)), NumberedCheckpoint(6, GridPosition(6, 9)), NumberedCheckpoint(7, GridPosition(9, 10)), NumberedCheckpoint(8, GridPosition(7, 11)),
            NumberedCheckpoint(9, GridPosition(4, 10)), NumberedCheckpoint(10, GridPosition(0, 11)), NumberedCheckpoint(11, GridPosition(0, 6)), NumberedCheckpoint(12, GridPosition(1, 5)),
            NumberedCheckpoint(13, GridPosition(1, 8)), NumberedCheckpoint(14, GridPosition(2, 10)), NumberedCheckpoint(15, GridPosition(2, 9)), NumberedCheckpoint(16, GridPosition(4, 8)),
            NumberedCheckpoint(17, GridPosition(2, 5)), NumberedCheckpoint(18, GridPosition(0, 0)), NumberedCheckpoint(19, GridPosition(2, 2)), NumberedCheckpoint(20, GridPosition(2, 1)),
            NumberedCheckpoint(21, GridPosition(3, 2)), NumberedCheckpoint(22, GridPosition(9, 1)), NumberedCheckpoint(23, GridPosition(5, 2)), NumberedCheckpoint(24, GridPosition(8, 4)),
            NumberedCheckpoint(25, GridPosition(10, 2)), NumberedCheckpoint(26, GridPosition(11, 11)), NumberedCheckpoint(27, GridPosition(9, 8)), NumberedCheckpoint(28, GridPosition(6, 7)),
            NumberedCheckpoint(29, GridPosition(10, 6)), NumberedCheckpoint(30, GridPosition(10, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(10, 0)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0))
        ),
        difficultyMetadata = "World 6 - Level 268 (seed=6269)",
        seed = 6269L
    )

    fun buildSolution268(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(10, 8), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(10, 3)
        )
    )

    // --- Level 269: World 6 - Level 269 (12x12) ---

    fun buildLevel269(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl269",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(2, 3)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(0, 2)), NumberedCheckpoint(6, GridPosition(1, 8)), NumberedCheckpoint(7, GridPosition(2, 6)), NumberedCheckpoint(8, GridPosition(5, 9)),
            NumberedCheckpoint(9, GridPosition(3, 8)), NumberedCheckpoint(10, GridPosition(5, 7)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(5, 3)),
            NumberedCheckpoint(13, GridPosition(5, 2)), NumberedCheckpoint(14, GridPosition(6, 7)), NumberedCheckpoint(15, GridPosition(7, 4)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(8, 2)), NumberedCheckpoint(18, GridPosition(6, 1)), NumberedCheckpoint(19, GridPosition(11, 0)), NumberedCheckpoint(20, GridPosition(9, 8)),
            NumberedCheckpoint(21, GridPosition(9, 11)), NumberedCheckpoint(22, GridPosition(10, 9)), NumberedCheckpoint(23, GridPosition(2, 10)), NumberedCheckpoint(24, GridPosition(7, 10)),
            NumberedCheckpoint(25, GridPosition(10, 6)), NumberedCheckpoint(26, GridPosition(8, 5)), NumberedCheckpoint(27, GridPosition(9, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(0, 10)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(11, 7), GridPosition(11, 8))
        ),
        difficultyMetadata = "World 6 - Level 269 (seed=6271)",
        seed = 6271L
    )

    fun buildSolution269(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4)
        )
    )

    // --- Level 270: World 6 - Level 270 (12x12) ---

    fun buildLevel270(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl270",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 9)), NumberedCheckpoint(2, GridPosition(10, 11)), NumberedCheckpoint(3, GridPosition(10, 8)), NumberedCheckpoint(4, GridPosition(8, 9)),
            NumberedCheckpoint(5, GridPosition(10, 7)), NumberedCheckpoint(6, GridPosition(7, 8)), NumberedCheckpoint(7, GridPosition(9, 4)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(11, 5)), NumberedCheckpoint(10, GridPosition(9, 2)), NumberedCheckpoint(11, GridPosition(10, 0)), NumberedCheckpoint(12, GridPosition(1, 2)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(5, 3)), NumberedCheckpoint(16, GridPosition(6, 5)),
            NumberedCheckpoint(17, GridPosition(4, 8)), NumberedCheckpoint(18, GridPosition(1, 9)), NumberedCheckpoint(19, GridPosition(4, 11)), NumberedCheckpoint(20, GridPosition(0, 6)),
            NumberedCheckpoint(21, GridPosition(0, 3)), NumberedCheckpoint(22, GridPosition(1, 4)), NumberedCheckpoint(23, GridPosition(2, 7)), NumberedCheckpoint(24, GridPosition(1, 8)),
            NumberedCheckpoint(25, GridPosition(3, 7)), NumberedCheckpoint(26, GridPosition(2, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9))
        ),
        difficultyMetadata = "World 6 - Level 270 (seed=6271)",
        seed = 6271L
    )

    fun buildSolution270(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4),
            GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5)
        )
    )

    // --- Level 271: World 6 - Level 271 (12x12) ---

    fun buildLevel271(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl271",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(0, 8)), NumberedCheckpoint(3, GridPosition(2, 5)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(4, 4)), NumberedCheckpoint(6, GridPosition(6, 5)), NumberedCheckpoint(7, GridPosition(2, 8)), NumberedCheckpoint(8, GridPosition(1, 9)),
            NumberedCheckpoint(9, GridPosition(1, 10)), NumberedCheckpoint(10, GridPosition(6, 7)), NumberedCheckpoint(11, GridPosition(6, 9)), NumberedCheckpoint(12, GridPosition(7, 10)),
            NumberedCheckpoint(13, GridPosition(10, 10)), NumberedCheckpoint(14, GridPosition(10, 9)), NumberedCheckpoint(15, GridPosition(9, 6)), NumberedCheckpoint(16, GridPosition(11, 8)),
            NumberedCheckpoint(17, GridPosition(10, 3)), NumberedCheckpoint(18, GridPosition(10, 5)), NumberedCheckpoint(19, GridPosition(9, 4)), NumberedCheckpoint(20, GridPosition(8, 8)),
            NumberedCheckpoint(21, GridPosition(7, 5)), NumberedCheckpoint(22, GridPosition(8, 1)), NumberedCheckpoint(23, GridPosition(8, 3)), NumberedCheckpoint(24, GridPosition(4, 1)),
            NumberedCheckpoint(25, GridPosition(5, 0)), NumberedCheckpoint(26, GridPosition(3, 2)), NumberedCheckpoint(27, GridPosition(2, 1)), NumberedCheckpoint(28, GridPosition(1, 2)),
            NumberedCheckpoint(29, GridPosition(1, 3)), NumberedCheckpoint(30, GridPosition(2, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(5, 11))
        ),
        difficultyMetadata = "World 6 - Level 271 (seed=6277)",
        seed = 6277L
    )

    fun buildSolution271(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3)
        )
    )

    // --- Level 272: World 6 - Level 272 (12x12) ---

    fun buildLevel272(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl272",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 7)), NumberedCheckpoint(2, GridPosition(10, 8)), NumberedCheckpoint(3, GridPosition(9, 10)), NumberedCheckpoint(4, GridPosition(7, 9)),
            NumberedCheckpoint(5, GridPosition(8, 10)), NumberedCheckpoint(6, GridPosition(8, 7)), NumberedCheckpoint(7, GridPosition(10, 7)), NumberedCheckpoint(8, GridPosition(10, 4)),
            NumberedCheckpoint(9, GridPosition(9, 3)), NumberedCheckpoint(10, GridPosition(6, 6)), NumberedCheckpoint(11, GridPosition(6, 8)), NumberedCheckpoint(12, GridPosition(5, 10)),
            NumberedCheckpoint(13, GridPosition(1, 10)), NumberedCheckpoint(14, GridPosition(4, 7)), NumberedCheckpoint(15, GridPosition(2, 9)), NumberedCheckpoint(16, GridPosition(1, 7)),
            NumberedCheckpoint(17, GridPosition(2, 8)), NumberedCheckpoint(18, GridPosition(3, 6)), NumberedCheckpoint(19, GridPosition(2, 5)), NumberedCheckpoint(20, GridPosition(4, 1)),
            NumberedCheckpoint(21, GridPosition(1, 1)), NumberedCheckpoint(22, GridPosition(3, 3)), NumberedCheckpoint(23, GridPosition(7, 3)), NumberedCheckpoint(24, GridPosition(4, 2)),
            NumberedCheckpoint(25, GridPosition(7, 1)), NumberedCheckpoint(26, GridPosition(6, 1)), NumberedCheckpoint(27, GridPosition(10, 0)), NumberedCheckpoint(28, GridPosition(8, 0)),
            NumberedCheckpoint(29, GridPosition(9, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 6 - Level 272 (seed=6275)",
        seed = 6275L
    )

    fun buildSolution272(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0)
        )
    )

    // --- Level 273: World 6 - Level 273 (12x12) ---

    fun buildLevel273(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl273",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 4)), NumberedCheckpoint(2, GridPosition(9, 3)), NumberedCheckpoint(3, GridPosition(11, 0)), NumberedCheckpoint(4, GridPosition(11, 4)),
            NumberedCheckpoint(5, GridPosition(9, 10)), NumberedCheckpoint(6, GridPosition(7, 10)), NumberedCheckpoint(7, GridPosition(10, 8)), NumberedCheckpoint(8, GridPosition(9, 5)),
            NumberedCheckpoint(9, GridPosition(9, 8)), NumberedCheckpoint(10, GridPosition(5, 7)), NumberedCheckpoint(11, GridPosition(7, 5)), NumberedCheckpoint(12, GridPosition(6, 3)),
            NumberedCheckpoint(13, GridPosition(7, 2)), NumberedCheckpoint(14, GridPosition(8, 2)), NumberedCheckpoint(15, GridPosition(5, 2)), NumberedCheckpoint(16, GridPosition(2, 2)),
            NumberedCheckpoint(17, GridPosition(4, 1)), NumberedCheckpoint(18, GridPosition(4, 7)), NumberedCheckpoint(19, GridPosition(7, 9)), NumberedCheckpoint(20, GridPosition(4, 10)),
            NumberedCheckpoint(21, GridPosition(6, 10)), NumberedCheckpoint(22, GridPosition(4, 11)), NumberedCheckpoint(23, GridPosition(3, 9)), NumberedCheckpoint(24, GridPosition(2, 8)),
            NumberedCheckpoint(25, GridPosition(2, 7)), NumberedCheckpoint(26, GridPosition(1, 8)), NumberedCheckpoint(27, GridPosition(0, 1)), NumberedCheckpoint(28, GridPosition(1, 2)),
            NumberedCheckpoint(29, GridPosition(2, 4)), NumberedCheckpoint(30, GridPosition(3, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(0, 10)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 6 - Level 273 (seed=6273)",
        seed = 6273L
    )

    fun buildSolution273(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9),
            GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6)
        )
    )

    // --- Level 274: World 6 - Level 274 (12x12) ---

    fun buildLevel274(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl274",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(9, 7)), NumberedCheckpoint(3, GridPosition(10, 5)), NumberedCheckpoint(4, GridPosition(9, 4)),
            NumberedCheckpoint(5, GridPosition(11, 3)), NumberedCheckpoint(6, GridPosition(8, 3)), NumberedCheckpoint(7, GridPosition(8, 1)), NumberedCheckpoint(8, GridPosition(10, 0)),
            NumberedCheckpoint(9, GridPosition(4, 1)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(6, 2)), NumberedCheckpoint(12, GridPosition(1, 0)),
            NumberedCheckpoint(13, GridPosition(2, 5)), NumberedCheckpoint(14, GridPosition(0, 5)), NumberedCheckpoint(15, GridPosition(1, 9)), NumberedCheckpoint(16, GridPosition(3, 6)),
            NumberedCheckpoint(17, GridPosition(7, 4)), NumberedCheckpoint(18, GridPosition(6, 6)), NumberedCheckpoint(19, GridPosition(8, 8)), NumberedCheckpoint(20, GridPosition(10, 9)),
            NumberedCheckpoint(21, GridPosition(10, 8)), NumberedCheckpoint(22, GridPosition(9, 10)), NumberedCheckpoint(23, GridPosition(7, 10)), NumberedCheckpoint(24, GridPosition(3, 10)),
            NumberedCheckpoint(25, GridPosition(6, 8)), NumberedCheckpoint(26, GridPosition(4, 7)), NumberedCheckpoint(27, GridPosition(4, 9)), NumberedCheckpoint(28, GridPosition(2, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(0, 10)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10))
        ),
        difficultyMetadata = "World 6 - Level 274 (seed=6277)",
        seed = 6277L
    )

    fun buildSolution274(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11)
        )
    )

    // --- Level 275: World 6 - Level 275 (12x12) ---

    fun buildLevel275(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl275",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(7, 7)), NumberedCheckpoint(3, GridPosition(8, 5)), NumberedCheckpoint(4, GridPosition(11, 4)),
            NumberedCheckpoint(5, GridPosition(10, 0)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(6, 6)), NumberedCheckpoint(8, GridPosition(4, 6)),
            NumberedCheckpoint(9, GridPosition(2, 9)), NumberedCheckpoint(10, GridPosition(6, 8)), NumberedCheckpoint(11, GridPosition(7, 10)), NumberedCheckpoint(12, GridPosition(7, 8)),
            NumberedCheckpoint(13, GridPosition(8, 9)), NumberedCheckpoint(14, GridPosition(10, 8)), NumberedCheckpoint(15, GridPosition(10, 6)), NumberedCheckpoint(16, GridPosition(7, 11)),
            NumberedCheckpoint(17, GridPosition(2, 11)), NumberedCheckpoint(18, GridPosition(1, 10)), NumberedCheckpoint(19, GridPosition(2, 8)), NumberedCheckpoint(20, GridPosition(0, 6)),
            NumberedCheckpoint(21, GridPosition(2, 5)), NumberedCheckpoint(22, GridPosition(1, 3)), NumberedCheckpoint(23, GridPosition(0, 0)), NumberedCheckpoint(24, GridPosition(1, 2)),
            NumberedCheckpoint(25, GridPosition(5, 3)), NumberedCheckpoint(26, GridPosition(3, 3)), NumberedCheckpoint(27, GridPosition(7, 2)), NumberedCheckpoint(28, GridPosition(9, 0)),
            NumberedCheckpoint(29, GridPosition(5, 1)), NumberedCheckpoint(30, GridPosition(4, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(9, 10), GridPosition(10, 10)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(4, 10), GridPosition(5, 10))
        ),
        difficultyMetadata = "World 6 - Level 275 (seed=6275)",
        seed = 6275L
    )

    fun buildSolution275(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[251] = buildLevel251()
        map[252] = buildLevel252()
        map[253] = buildLevel253()
        map[254] = buildLevel254()
        map[255] = buildLevel255()
        map[256] = buildLevel256()
        map[257] = buildLevel257()
        map[258] = buildLevel258()
        map[259] = buildLevel259()
        map[260] = buildLevel260()
        map[261] = buildLevel261()
        map[262] = buildLevel262()
        map[263] = buildLevel263()
        map[264] = buildLevel264()
        map[265] = buildLevel265()
        map[266] = buildLevel266()
        map[267] = buildLevel267()
        map[268] = buildLevel268()
        map[269] = buildLevel269()
        map[270] = buildLevel270()
        map[271] = buildLevel271()
        map[272] = buildLevel272()
        map[273] = buildLevel273()
        map[274] = buildLevel274()
        map[275] = buildLevel275()
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[251] = buildSolution251()
        map[252] = buildSolution252()
        map[253] = buildSolution253()
        map[254] = buildSolution254()
        map[255] = buildSolution255()
        map[256] = buildSolution256()
        map[257] = buildSolution257()
        map[258] = buildSolution258()
        map[259] = buildSolution259()
        map[260] = buildSolution260()
        map[261] = buildSolution261()
        map[262] = buildSolution262()
        map[263] = buildSolution263()
        map[264] = buildSolution264()
        map[265] = buildSolution265()
        map[266] = buildSolution266()
        map[267] = buildSolution267()
        map[268] = buildSolution268()
        map[269] = buildSolution269()
        map[270] = buildSolution270()
        map[271] = buildSolution271()
        map[272] = buildSolution272()
        map[273] = buildSolution273()
        map[274] = buildSolution274()
        map[275] = buildSolution275()
        map
    }
}
