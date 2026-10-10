package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6PartA {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 201: World 6 - Level 201 (11x11) ---

    fun buildLevel201(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl201",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(9, 10)), NumberedCheckpoint(3, GridPosition(10, 6)), NumberedCheckpoint(4, GridPosition(8, 7)),
            NumberedCheckpoint(5, GridPosition(7, 7)), NumberedCheckpoint(6, GridPosition(2, 8)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(5, 3)), NumberedCheckpoint(10, GridPosition(8, 4)), NumberedCheckpoint(11, GridPosition(7, 5)), NumberedCheckpoint(12, GridPosition(9, 4)),
            NumberedCheckpoint(13, GridPosition(6, 1)), NumberedCheckpoint(14, GridPosition(9, 0)), NumberedCheckpoint(15, GridPosition(3, 1)), NumberedCheckpoint(16, GridPosition(1, 0)),
            NumberedCheckpoint(17, GridPosition(0, 4)), NumberedCheckpoint(18, GridPosition(2, 1)), NumberedCheckpoint(19, GridPosition(1, 6)), NumberedCheckpoint(20, GridPosition(0, 5)),
            NumberedCheckpoint(21, GridPosition(0, 8)), NumberedCheckpoint(22, GridPosition(4, 7)), NumberedCheckpoint(23, GridPosition(4, 6)), NumberedCheckpoint(24, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(10, 5), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5))
        ),
        difficultyMetadata = "World 6 - Level 201 (seed=6205)",
        seed = 6205L
    )

    fun buildSolution201(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2)
        )
    )

    // --- Level 202: World 6 - Level 202 (11x11) ---

    fun buildLevel202(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl202",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(5, 10)), NumberedCheckpoint(4, GridPosition(9, 5)),
            NumberedCheckpoint(5, GridPosition(8, 6)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(8, 1)), NumberedCheckpoint(8, GridPosition(4, 0)),
            NumberedCheckpoint(9, GridPosition(6, 1)), NumberedCheckpoint(10, GridPosition(8, 2)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(3, 1)),
            NumberedCheckpoint(13, GridPosition(2, 4)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(7, 5)), NumberedCheckpoint(16, GridPosition(5, 6)),
            NumberedCheckpoint(17, GridPosition(0, 5)), NumberedCheckpoint(18, GridPosition(3, 7)), NumberedCheckpoint(19, GridPosition(7, 8)), NumberedCheckpoint(20, GridPosition(9, 9)),
            NumberedCheckpoint(21, GridPosition(3, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 6 - Level 202 (seed=6211)",
        seed = 6211L
    )

    fun buildSolution202(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9)
        )
    )

    // --- Level 203: World 6 - Level 203 (11x11) ---

    fun buildLevel203(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl203",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 9)), NumberedCheckpoint(2, GridPosition(7, 8)), NumberedCheckpoint(3, GridPosition(9, 9)), NumberedCheckpoint(4, GridPosition(9, 10)),
            NumberedCheckpoint(5, GridPosition(9, 8)), NumberedCheckpoint(6, GridPosition(9, 1)), NumberedCheckpoint(7, GridPosition(9, 0)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(6, 4)), NumberedCheckpoint(10, GridPosition(5, 6)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(9, 6)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(3, 7)), NumberedCheckpoint(15, GridPosition(3, 4)), NumberedCheckpoint(16, GridPosition(1, 7)),
            NumberedCheckpoint(17, GridPosition(4, 9)), NumberedCheckpoint(18, GridPosition(1, 5)), NumberedCheckpoint(19, GridPosition(1, 2)), NumberedCheckpoint(20, GridPosition(3, 1)),
            NumberedCheckpoint(21, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1))
        ),
        difficultyMetadata = "World 6 - Level 203 (seed=6209)",
        seed = 6209L
    )

    fun buildSolution203(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1)
        )
    )

    // --- Level 204: World 6 - Level 204 (11x11) ---

    fun buildLevel204(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl204",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(8, 10)), NumberedCheckpoint(3, GridPosition(3, 6)), NumberedCheckpoint(4, GridPosition(2, 8)),
            NumberedCheckpoint(5, GridPosition(2, 6)), NumberedCheckpoint(6, GridPosition(3, 9)), NumberedCheckpoint(7, GridPosition(1, 5)), NumberedCheckpoint(8, GridPosition(5, 6)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(6, 2)), NumberedCheckpoint(11, GridPosition(5, 2)), NumberedCheckpoint(12, GridPosition(0, 1)),
            NumberedCheckpoint(13, GridPosition(2, 3)), NumberedCheckpoint(14, GridPosition(2, 2)), NumberedCheckpoint(15, GridPosition(4, 1)), NumberedCheckpoint(16, GridPosition(8, 3)),
            NumberedCheckpoint(17, GridPosition(8, 6)), NumberedCheckpoint(18, GridPosition(9, 6)), NumberedCheckpoint(19, GridPosition(10, 10)), NumberedCheckpoint(20, GridPosition(6, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(9, 0)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4))
        ),
        difficultyMetadata = "World 6 - Level 204 (seed=6213)",
        seed = 6213L
    )

    fun buildSolution204(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(6, 8)
        )
    )

    // --- Level 205: World 6 - Level 205 (11x11) ---

    fun buildLevel205(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl205",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(2, 5)), NumberedCheckpoint(3, GridPosition(0, 4)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(3, 3)), NumberedCheckpoint(6, GridPosition(5, 2)), NumberedCheckpoint(7, GridPosition(3, 1)), NumberedCheckpoint(8, GridPosition(2, 0)),
            NumberedCheckpoint(9, GridPosition(5, 0)), NumberedCheckpoint(10, GridPosition(10, 3)), NumberedCheckpoint(11, GridPosition(9, 1)), NumberedCheckpoint(12, GridPosition(6, 3)),
            NumberedCheckpoint(13, GridPosition(7, 3)), NumberedCheckpoint(14, GridPosition(10, 5)), NumberedCheckpoint(15, GridPosition(7, 10)), NumberedCheckpoint(16, GridPosition(8, 8)),
            NumberedCheckpoint(17, GridPosition(2, 8)), NumberedCheckpoint(18, GridPosition(5, 10)), NumberedCheckpoint(19, GridPosition(0, 7)), NumberedCheckpoint(20, GridPosition(4, 7)),
            NumberedCheckpoint(21, GridPosition(7, 7)), NumberedCheckpoint(22, GridPosition(8, 5)), NumberedCheckpoint(23, GridPosition(6, 4)), NumberedCheckpoint(24, GridPosition(3, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7))
        ),
        difficultyMetadata = "World 6 - Level 205 (seed=6214)",
        seed = 6214L
    )

    fun buildSolution205(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(3, 5)
        )
    )

    // --- Level 206: World 6 - Level 206 (11x11) ---

    fun buildLevel206(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl206",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 7)), NumberedCheckpoint(2, GridPosition(1, 8)), NumberedCheckpoint(3, GridPosition(3, 8)), NumberedCheckpoint(4, GridPosition(8, 10)),
            NumberedCheckpoint(5, GridPosition(5, 9)), NumberedCheckpoint(6, GridPosition(7, 8)), NumberedCheckpoint(7, GridPosition(6, 6)), NumberedCheckpoint(8, GridPosition(1, 5)),
            NumberedCheckpoint(9, GridPosition(3, 5)), NumberedCheckpoint(10, GridPosition(0, 1)), NumberedCheckpoint(11, GridPosition(2, 1)), NumberedCheckpoint(12, GridPosition(3, 2)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(8, 4)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(3, 0)), NumberedCheckpoint(18, GridPosition(6, 1)), NumberedCheckpoint(19, GridPosition(10, 2)), NumberedCheckpoint(20, GridPosition(9, 9)),
            NumberedCheckpoint(21, GridPosition(9, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 6 - Level 206 (seed=6216)",
        seed = 6216L
    )

    fun buildSolution206(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3)
        )
    )

    // --- Level 207: World 6 - Level 207 (11x11) ---

    fun buildLevel207(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl207",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(9, 10)), NumberedCheckpoint(3, GridPosition(6, 8)), NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(0, 6)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(3, 6)), NumberedCheckpoint(8, GridPosition(5, 8)),
            NumberedCheckpoint(9, GridPosition(5, 7)), NumberedCheckpoint(10, GridPosition(9, 8)), NumberedCheckpoint(11, GridPosition(8, 5)), NumberedCheckpoint(12, GridPosition(9, 6)),
            NumberedCheckpoint(13, GridPosition(5, 5)), NumberedCheckpoint(14, GridPosition(7, 2)), NumberedCheckpoint(15, GridPosition(9, 0)), NumberedCheckpoint(16, GridPosition(4, 0)),
            NumberedCheckpoint(17, GridPosition(2, 0)), NumberedCheckpoint(18, GridPosition(4, 1)), NumberedCheckpoint(19, GridPosition(6, 4)), NumberedCheckpoint(20, GridPosition(4, 2)),
            NumberedCheckpoint(21, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(4, 10), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(10, 3))
        ),
        difficultyMetadata = "World 6 - Level 207 (seed=6215)",
        seed = 6215L
    )

    fun buildSolution207(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3)
        )
    )

    // --- Level 208: World 6 - Level 208 (11x11) ---

    fun buildLevel208(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl208",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(7, 2)), NumberedCheckpoint(3, GridPosition(8, 0)), NumberedCheckpoint(4, GridPosition(4, 1)),
            NumberedCheckpoint(5, GridPosition(2, 0)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(1, 1)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(7, 4)), NumberedCheckpoint(10, GridPosition(9, 1)), NumberedCheckpoint(11, GridPosition(10, 4)), NumberedCheckpoint(12, GridPosition(8, 5)),
            NumberedCheckpoint(13, GridPosition(7, 9)), NumberedCheckpoint(14, GridPosition(9, 8)), NumberedCheckpoint(15, GridPosition(6, 6)), NumberedCheckpoint(16, GridPosition(4, 5)),
            NumberedCheckpoint(17, GridPosition(1, 5)), NumberedCheckpoint(18, GridPosition(1, 8)), NumberedCheckpoint(19, GridPosition(3, 7)), NumberedCheckpoint(20, GridPosition(5, 6)),
            NumberedCheckpoint(21, GridPosition(2, 8)), NumberedCheckpoint(22, GridPosition(1, 10)), NumberedCheckpoint(23, GridPosition(5, 9)), NumberedCheckpoint(24, GridPosition(4, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1))
        ),
        difficultyMetadata = "World 6 - Level 208 (seed=6209)",
        seed = 6209L
    )

    fun buildSolution208(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10)
        )
    )

    // --- Level 209: World 6 - Level 209 (11x11) ---

    fun buildLevel209(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl209",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 5)), NumberedCheckpoint(2, GridPosition(9, 7)), NumberedCheckpoint(3, GridPosition(6, 8)), NumberedCheckpoint(4, GridPosition(7, 6)),
            NumberedCheckpoint(5, GridPosition(7, 8)), NumberedCheckpoint(6, GridPosition(8, 6)), NumberedCheckpoint(7, GridPosition(9, 5)), NumberedCheckpoint(8, GridPosition(10, 5)),
            NumberedCheckpoint(9, GridPosition(9, 1)), NumberedCheckpoint(10, GridPosition(7, 1)), NumberedCheckpoint(11, GridPosition(9, 4)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(7, 2)), NumberedCheckpoint(14, GridPosition(4, 1)), NumberedCheckpoint(15, GridPosition(2, 2)), NumberedCheckpoint(16, GridPosition(3, 0)),
            NumberedCheckpoint(17, GridPosition(2, 3)), NumberedCheckpoint(18, GridPosition(3, 4)), NumberedCheckpoint(19, GridPosition(3, 6)), NumberedCheckpoint(20, GridPosition(0, 5)),
            NumberedCheckpoint(21, GridPosition(4, 7)), NumberedCheckpoint(22, GridPosition(1, 9)), NumberedCheckpoint(23, GridPosition(2, 9)), NumberedCheckpoint(24, GridPosition(0, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3))
        ),
        difficultyMetadata = "World 6 - Level 209 (seed=6211)",
        seed = 6211L
    )

    fun buildSolution209(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8)
        )
    )

    // --- Level 210: World 6 - Level 210 (11x11) ---

    fun buildLevel210(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl210",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(3, 0)), NumberedCheckpoint(3, GridPosition(4, 2)), NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(7, 4)), NumberedCheckpoint(7, GridPosition(6, 2)), NumberedCheckpoint(8, GridPosition(8, 1)),
            NumberedCheckpoint(9, GridPosition(9, 3)), NumberedCheckpoint(10, GridPosition(10, 1)), NumberedCheckpoint(11, GridPosition(2, 10)), NumberedCheckpoint(12, GridPosition(4, 7)),
            NumberedCheckpoint(13, GridPosition(1, 10)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(2, 1)), NumberedCheckpoint(16, GridPosition(1, 3)),
            NumberedCheckpoint(17, GridPosition(3, 4)), NumberedCheckpoint(18, GridPosition(6, 6)), NumberedCheckpoint(19, GridPosition(9, 8)), NumberedCheckpoint(20, GridPosition(6, 8)),
            NumberedCheckpoint(21, GridPosition(5, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(3, 10), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9))
        ),
        difficultyMetadata = "World 6 - Level 210 (seed=6224)",
        seed = 6224L
    )

    fun buildSolution210(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(5, 9)
        )
    )

    // --- Level 211: World 6 - Level 211 (11x11) ---

    fun buildLevel211(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl211",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 6)), NumberedCheckpoint(2, GridPosition(6, 7)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(10, 8)),
            NumberedCheckpoint(5, GridPosition(8, 9)), NumberedCheckpoint(6, GridPosition(9, 7)), NumberedCheckpoint(7, GridPosition(8, 5)), NumberedCheckpoint(8, GridPosition(10, 5)),
            NumberedCheckpoint(9, GridPosition(10, 2)), NumberedCheckpoint(10, GridPosition(8, 3)), NumberedCheckpoint(11, GridPosition(10, 0)), NumberedCheckpoint(12, GridPosition(7, 0)),
            NumberedCheckpoint(13, GridPosition(7, 3)), NumberedCheckpoint(14, GridPosition(1, 2)), NumberedCheckpoint(15, GridPosition(3, 1)), NumberedCheckpoint(16, GridPosition(0, 4)),
            NumberedCheckpoint(17, GridPosition(3, 5)), NumberedCheckpoint(18, GridPosition(0, 5)), NumberedCheckpoint(19, GridPosition(2, 6)), NumberedCheckpoint(20, GridPosition(3, 8)),
            NumberedCheckpoint(21, GridPosition(1, 7)), NumberedCheckpoint(22, GridPosition(6, 10)), NumberedCheckpoint(23, GridPosition(6, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5))
        ),
        difficultyMetadata = "World 6 - Level 211 (seed=6212)",
        seed = 6212L
    )

    fun buildSolution211(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8)
        )
    )

    // --- Level 212: World 6 - Level 212 (11x11) ---

    fun buildLevel212(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl212",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 4)), NumberedCheckpoint(2, GridPosition(6, 10)), NumberedCheckpoint(3, GridPosition(9, 9)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(4, 2)), NumberedCheckpoint(7, GridPosition(1, 2)), NumberedCheckpoint(8, GridPosition(3, 2)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(8, 7)), NumberedCheckpoint(11, GridPosition(5, 5)), NumberedCheckpoint(12, GridPosition(7, 6)),
            NumberedCheckpoint(13, GridPosition(7, 4)), NumberedCheckpoint(14, GridPosition(2, 4)), NumberedCheckpoint(15, GridPosition(4, 5)), NumberedCheckpoint(16, GridPosition(4, 7)),
            NumberedCheckpoint(17, GridPosition(1, 10)), NumberedCheckpoint(18, GridPosition(3, 9)), NumberedCheckpoint(19, GridPosition(2, 7)), NumberedCheckpoint(20, GridPosition(0, 6)),
            NumberedCheckpoint(21, GridPosition(1, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(9, 0)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8))
        ),
        difficultyMetadata = "World 6 - Level 212 (seed=6215)",
        seed = 6215L
    )

    fun buildSolution212(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7)
        )
    )

    // --- Level 213: World 6 - Level 213 (11x11) ---

    fun buildLevel213(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl213",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(7, 6)), NumberedCheckpoint(3, GridPosition(10, 6)), NumberedCheckpoint(4, GridPosition(8, 7)),
            NumberedCheckpoint(5, GridPosition(5, 10)), NumberedCheckpoint(6, GridPosition(0, 10)), NumberedCheckpoint(7, GridPosition(4, 9)), NumberedCheckpoint(8, GridPosition(4, 6)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(1, 8)), NumberedCheckpoint(11, GridPosition(2, 6)), NumberedCheckpoint(12, GridPosition(0, 3)),
            NumberedCheckpoint(13, GridPosition(2, 1)), NumberedCheckpoint(14, GridPosition(1, 5)), NumberedCheckpoint(15, GridPosition(3, 2)), NumberedCheckpoint(16, GridPosition(6, 0)),
            NumberedCheckpoint(17, GridPosition(4, 2)), NumberedCheckpoint(18, GridPosition(5, 2)), NumberedCheckpoint(19, GridPosition(5, 4)), NumberedCheckpoint(20, GridPosition(8, 2)),
            NumberedCheckpoint(21, GridPosition(7, 0)), NumberedCheckpoint(22, GridPosition(7, 5)), NumberedCheckpoint(23, GridPosition(9, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "World 6 - Level 213 (seed=6214)",
        seed = 6214L
    )

    fun buildSolution213(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1)
        )
    )

    // --- Level 214: World 6 - Level 214 (11x11) ---

    fun buildLevel214(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl214",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)), NumberedCheckpoint(2, GridPosition(7, 1)), NumberedCheckpoint(3, GridPosition(4, 1)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(1, 1)), NumberedCheckpoint(6, GridPosition(1, 4)), NumberedCheckpoint(7, GridPosition(0, 8)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(9, 4)), NumberedCheckpoint(10, GridPosition(8, 0)), NumberedCheckpoint(11, GridPosition(8, 7)), NumberedCheckpoint(12, GridPosition(10, 6)),
            NumberedCheckpoint(13, GridPosition(4, 8)), NumberedCheckpoint(14, GridPosition(2, 7)), NumberedCheckpoint(15, GridPosition(4, 6)), NumberedCheckpoint(16, GridPosition(6, 6)),
            NumberedCheckpoint(17, GridPosition(5, 9)), NumberedCheckpoint(18, GridPosition(9, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(0, 9)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7))
        ),
        difficultyMetadata = "World 6 - Level 214 (seed=6215)",
        seed = 6215L
    )

    fun buildSolution214(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9)
        )
    )

    // --- Level 215: World 6 - Level 215 (11x11) ---

    fun buildLevel215(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl215",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(8, 4)), NumberedCheckpoint(3, GridPosition(10, 2)), NumberedCheckpoint(4, GridPosition(10, 3)),
            NumberedCheckpoint(5, GridPosition(1, 10)), NumberedCheckpoint(6, GridPosition(3, 9)), NumberedCheckpoint(7, GridPosition(0, 7)), NumberedCheckpoint(8, GridPosition(1, 1)),
            NumberedCheckpoint(9, GridPosition(1, 3)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(5, 8)), NumberedCheckpoint(12, GridPosition(7, 8)),
            NumberedCheckpoint(13, GridPosition(9, 9)), NumberedCheckpoint(14, GridPosition(8, 5)), NumberedCheckpoint(15, GridPosition(8, 7)), NumberedCheckpoint(16, GridPosition(7, 2)),
            NumberedCheckpoint(17, GridPosition(5, 6)), NumberedCheckpoint(18, GridPosition(4, 2)), NumberedCheckpoint(19, GridPosition(3, 1)), NumberedCheckpoint(20, GridPosition(3, 4)),
            NumberedCheckpoint(21, GridPosition(6, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4))
        ),
        difficultyMetadata = "World 6 - Level 215 (seed=6218)",
        seed = 6218L
    )

    fun buildSolution215(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 0)
        )
    )

    // --- Level 216: World 6 - Level 216 (11x11) ---

    fun buildLevel216(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl216",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 3)), NumberedCheckpoint(2, GridPosition(6, 1)), NumberedCheckpoint(3, GridPosition(5, 3)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(3, 1)), NumberedCheckpoint(6, GridPosition(0, 4)), NumberedCheckpoint(7, GridPosition(2, 5)), NumberedCheckpoint(8, GridPosition(3, 9)),
            NumberedCheckpoint(9, GridPosition(0, 7)), NumberedCheckpoint(10, GridPosition(4, 8)), NumberedCheckpoint(11, GridPosition(5, 6)), NumberedCheckpoint(12, GridPosition(6, 8)),
            NumberedCheckpoint(13, GridPosition(7, 10)), NumberedCheckpoint(14, GridPosition(9, 8)), NumberedCheckpoint(15, GridPosition(9, 9)), NumberedCheckpoint(16, GridPosition(9, 7)),
            NumberedCheckpoint(17, GridPosition(8, 5)), NumberedCheckpoint(18, GridPosition(3, 5)), NumberedCheckpoint(19, GridPosition(7, 4)), NumberedCheckpoint(20, GridPosition(10, 6)),
            NumberedCheckpoint(21, GridPosition(8, 2)), NumberedCheckpoint(22, GridPosition(10, 1)), NumberedCheckpoint(23, GridPosition(6, 0)), NumberedCheckpoint(24, GridPosition(0, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9))
        ),
        difficultyMetadata = "World 6 - Level 216 (seed=6231)",
        seed = 6231L
    )

    fun buildSolution216(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0)
        )
    )

    // --- Level 217: World 6 - Level 217 (11x11) ---

    fun buildLevel217(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl217",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 10)), NumberedCheckpoint(2, GridPosition(3, 8)), NumberedCheckpoint(3, GridPosition(1, 7)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(0, 0)), NumberedCheckpoint(6, GridPosition(1, 2)), NumberedCheckpoint(7, GridPosition(1, 6)), NumberedCheckpoint(8, GridPosition(5, 7)),
            NumberedCheckpoint(9, GridPosition(1, 10)), NumberedCheckpoint(10, GridPosition(3, 5)), NumberedCheckpoint(11, GridPosition(3, 0)), NumberedCheckpoint(12, GridPosition(9, 3)),
            NumberedCheckpoint(13, GridPosition(4, 1)), NumberedCheckpoint(14, GridPosition(6, 2)), NumberedCheckpoint(15, GridPosition(7, 2)), NumberedCheckpoint(16, GridPosition(9, 5)),
            NumberedCheckpoint(17, GridPosition(9, 4)), NumberedCheckpoint(18, GridPosition(10, 8)), NumberedCheckpoint(19, GridPosition(8, 9)), NumberedCheckpoint(20, GridPosition(8, 6)),
            NumberedCheckpoint(21, GridPosition(8, 8)), NumberedCheckpoint(22, GridPosition(7, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(10, 1)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2))
        ),
        difficultyMetadata = "World 6 - Level 217 (seed=6223)",
        seed = 6223L
    )

    fun buildSolution217(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5)
        )
    )

    // --- Level 218: World 6 - Level 218 (11x11) ---

    fun buildLevel218(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl218",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(1, 4)), NumberedCheckpoint(3, GridPosition(1, 1)), NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(9, 1)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(9, 5)), NumberedCheckpoint(8, GridPosition(8, 1)),
            NumberedCheckpoint(9, GridPosition(6, 2)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(5, 5)),
            NumberedCheckpoint(13, GridPosition(0, 5)), NumberedCheckpoint(14, GridPosition(2, 7)), NumberedCheckpoint(15, GridPosition(2, 8)), NumberedCheckpoint(16, GridPosition(4, 9)),
            NumberedCheckpoint(17, GridPosition(0, 10)), NumberedCheckpoint(18, GridPosition(7, 10)), NumberedCheckpoint(19, GridPosition(6, 7)), NumberedCheckpoint(20, GridPosition(5, 6)),
            NumberedCheckpoint(21, GridPosition(8, 10)), NumberedCheckpoint(22, GridPosition(9, 8)), NumberedCheckpoint(23, GridPosition(7, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7))
        ),
        difficultyMetadata = "World 6 - Level 218 (seed=6224)",
        seed = 6224L
    )

    fun buildSolution218(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7)
        )
    )

    // --- Level 219: World 6 - Level 219 (11x11) ---

    fun buildLevel219(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl219",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(9, 1)), NumberedCheckpoint(3, GridPosition(10, 1)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(4, 2)), NumberedCheckpoint(6, GridPosition(4, 3)), NumberedCheckpoint(7, GridPosition(2, 2)), NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(5, 6)), NumberedCheckpoint(10, GridPosition(2, 6)), NumberedCheckpoint(11, GridPosition(1, 5)), NumberedCheckpoint(12, GridPosition(2, 1)),
            NumberedCheckpoint(13, GridPosition(0, 5)), NumberedCheckpoint(14, GridPosition(3, 8)), NumberedCheckpoint(15, GridPosition(5, 9)), NumberedCheckpoint(16, GridPosition(0, 9)),
            NumberedCheckpoint(17, GridPosition(6, 7)), NumberedCheckpoint(18, GridPosition(9, 10)), NumberedCheckpoint(19, GridPosition(8, 8)), NumberedCheckpoint(20, GridPosition(9, 4)),
            NumberedCheckpoint(21, GridPosition(9, 7)), NumberedCheckpoint(22, GridPosition(8, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 8), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(10, 1)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7))
        ),
        difficultyMetadata = "World 6 - Level 219 (seed=6232)",
        seed = 6232L
    )

    fun buildSolution219(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2)
        )
    )

    // --- Level 220: World 6 - Level 220 (11x11) ---

    fun buildLevel220(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl220",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(6, 1)), NumberedCheckpoint(3, GridPosition(10, 0)), NumberedCheckpoint(4, GridPosition(7, 3)),
            NumberedCheckpoint(5, GridPosition(6, 7)), NumberedCheckpoint(6, GridPosition(8, 5)), NumberedCheckpoint(7, GridPosition(7, 10)), NumberedCheckpoint(8, GridPosition(8, 8)),
            NumberedCheckpoint(9, GridPosition(2, 8)), NumberedCheckpoint(10, GridPosition(4, 9)), NumberedCheckpoint(11, GridPosition(0, 9)), NumberedCheckpoint(12, GridPosition(3, 6)),
            NumberedCheckpoint(13, GridPosition(5, 7)), NumberedCheckpoint(14, GridPosition(6, 2)), NumberedCheckpoint(15, GridPosition(4, 4)), NumberedCheckpoint(16, GridPosition(5, 1)),
            NumberedCheckpoint(17, GridPosition(2, 2)), NumberedCheckpoint(18, GridPosition(0, 0)), NumberedCheckpoint(19, GridPosition(1, 4)), NumberedCheckpoint(20, GridPosition(2, 6)),
            NumberedCheckpoint(21, GridPosition(1, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2))
        ),
        difficultyMetadata = "World 6 - Level 220 (seed=6222)",
        seed = 6222L
    )

    fun buildSolution220(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3)
        )
    )

    // --- Level 221: World 6 - Level 221 (11x11) ---

    fun buildLevel221(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl221",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(2, 8)), NumberedCheckpoint(3, GridPosition(2, 9)), NumberedCheckpoint(4, GridPosition(1, 5)),
            NumberedCheckpoint(5, GridPosition(2, 7)), NumberedCheckpoint(6, GridPosition(4, 4)), NumberedCheckpoint(7, GridPosition(2, 3)), NumberedCheckpoint(8, GridPosition(1, 3)),
            NumberedCheckpoint(9, GridPosition(2, 10)), NumberedCheckpoint(10, GridPosition(5, 8)), NumberedCheckpoint(11, GridPosition(7, 9)), NumberedCheckpoint(12, GridPosition(5, 5)),
            NumberedCheckpoint(13, GridPosition(5, 2)), NumberedCheckpoint(14, GridPosition(3, 2)), NumberedCheckpoint(15, GridPosition(1, 2)), NumberedCheckpoint(16, GridPosition(3, 0)),
            NumberedCheckpoint(17, GridPosition(8, 0)), NumberedCheckpoint(18, GridPosition(8, 3)), NumberedCheckpoint(19, GridPosition(10, 0)), NumberedCheckpoint(20, GridPosition(8, 10)),
            NumberedCheckpoint(21, GridPosition(8, 8)), NumberedCheckpoint(22, GridPosition(6, 4)), NumberedCheckpoint(23, GridPosition(8, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7))
        ),
        difficultyMetadata = "World 6 - Level 221 (seed=6223)",
        seed = 6223L
    )

    fun buildSolution221(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 6)
        )
    )

    // --- Level 222: World 6 - Level 222 (11x11) ---

    fun buildLevel222(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl222",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 8)), NumberedCheckpoint(2, GridPosition(6, 10)), NumberedCheckpoint(3, GridPosition(3, 8)), NumberedCheckpoint(4, GridPosition(4, 10)),
            NumberedCheckpoint(5, GridPosition(1, 9)), NumberedCheckpoint(6, GridPosition(1, 3)), NumberedCheckpoint(7, GridPosition(2, 1)), NumberedCheckpoint(8, GridPosition(0, 2)),
            NumberedCheckpoint(9, GridPosition(7, 1)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(10, 1)),
            NumberedCheckpoint(13, GridPosition(9, 2)), NumberedCheckpoint(14, GridPosition(9, 3)), NumberedCheckpoint(15, GridPosition(8, 4)), NumberedCheckpoint(16, GridPosition(9, 9)),
            NumberedCheckpoint(17, GridPosition(6, 7)), NumberedCheckpoint(18, GridPosition(9, 6)), NumberedCheckpoint(19, GridPosition(5, 5)), NumberedCheckpoint(20, GridPosition(4, 7)),
            NumberedCheckpoint(21, GridPosition(1, 7)), NumberedCheckpoint(22, GridPosition(3, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7))
        ),
        difficultyMetadata = "World 6 - Level 222 (seed=6230)",
        seed = 6230L
    )

    fun buildSolution222(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(3, 3)
        )
    )

    // --- Level 223: World 6 - Level 223 (11x11) ---

    fun buildLevel223(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl223",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 8)), NumberedCheckpoint(2, GridPosition(5, 6)), NumberedCheckpoint(3, GridPosition(0, 2)), NumberedCheckpoint(4, GridPosition(3, 2)),
            NumberedCheckpoint(5, GridPosition(3, 8)), NumberedCheckpoint(6, GridPosition(1, 5)), NumberedCheckpoint(7, GridPosition(0, 4)), NumberedCheckpoint(8, GridPosition(1, 7)),
            NumberedCheckpoint(9, GridPosition(3, 9)), NumberedCheckpoint(10, GridPosition(7, 10)), NumberedCheckpoint(11, GridPosition(6, 7)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(9, 7)), NumberedCheckpoint(14, GridPosition(8, 8)), NumberedCheckpoint(15, GridPosition(10, 9)), NumberedCheckpoint(16, GridPosition(9, 4)),
            NumberedCheckpoint(17, GridPosition(10, 0)), NumberedCheckpoint(18, GridPosition(8, 3)), NumberedCheckpoint(19, GridPosition(7, 1)), NumberedCheckpoint(20, GridPosition(6, 2)),
            NumberedCheckpoint(21, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4))
        ),
        difficultyMetadata = "World 6 - Level 223 (seed=6224)",
        seed = 6224L
    )

    fun buildSolution223(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1)
        )
    )

    // --- Level 224: World 6 - Level 224 (11x11) ---

    fun buildLevel224(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl224",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 5)), NumberedCheckpoint(2, GridPosition(7, 4)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(2, 0)), NumberedCheckpoint(7, GridPosition(4, 3)), NumberedCheckpoint(8, GridPosition(6, 0)),
            NumberedCheckpoint(9, GridPosition(10, 1)), NumberedCheckpoint(10, GridPosition(8, 2)), NumberedCheckpoint(11, GridPosition(10, 7)), NumberedCheckpoint(12, GridPosition(7, 10)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(8, 6)), NumberedCheckpoint(15, GridPosition(4, 7)), NumberedCheckpoint(16, GridPosition(2, 10)),
            NumberedCheckpoint(17, GridPosition(3, 6)), NumberedCheckpoint(18, GridPosition(2, 5)), NumberedCheckpoint(19, GridPosition(1, 7)), NumberedCheckpoint(20, GridPosition(1, 2)),
            NumberedCheckpoint(21, GridPosition(0, 6)), NumberedCheckpoint(22, GridPosition(1, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10))
        ),
        difficultyMetadata = "World 6 - Level 224 (seed=6224)",
        seed = 6224L
    )

    fun buildSolution224(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(1, 9)
        )
    )

    // --- Level 225: World 6 - Level 225 (11x11) ---

    fun buildLevel225(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl225",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 0)), NumberedCheckpoint(2, GridPosition(7, 1)), NumberedCheckpoint(3, GridPosition(10, 4)), NumberedCheckpoint(4, GridPosition(10, 5)),
            NumberedCheckpoint(5, GridPosition(9, 2)), NumberedCheckpoint(6, GridPosition(8, 4)), NumberedCheckpoint(7, GridPosition(7, 3)), NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(6, 1)), NumberedCheckpoint(10, GridPosition(2, 0)), NumberedCheckpoint(11, GridPosition(1, 2)), NumberedCheckpoint(12, GridPosition(8, 5)),
            NumberedCheckpoint(13, GridPosition(3, 4)), NumberedCheckpoint(14, GridPosition(6, 6)), NumberedCheckpoint(15, GridPosition(9, 8)), NumberedCheckpoint(16, GridPosition(1, 7)),
            NumberedCheckpoint(17, GridPosition(2, 5)), NumberedCheckpoint(18, GridPosition(3, 7)), NumberedCheckpoint(19, GridPosition(5, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(1, 10), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5))
        ),
        difficultyMetadata = "World 6 - Level 225 (seed=6233)",
        seed = 6233L
    )

    fun buildSolution225(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(5, 7)
        )
    )

    // --- Level 226: World 6 - Level 226 (11x11) ---

    fun buildLevel226(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl226",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)), NumberedCheckpoint(2, GridPosition(5, 0)), NumberedCheckpoint(3, GridPosition(0, 0)), NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(1, 5)), NumberedCheckpoint(6, GridPosition(3, 3)), NumberedCheckpoint(7, GridPosition(6, 1)), NumberedCheckpoint(8, GridPosition(10, 0)),
            NumberedCheckpoint(9, GridPosition(5, 4)), NumberedCheckpoint(10, GridPosition(10, 2)), NumberedCheckpoint(11, GridPosition(9, 4)), NumberedCheckpoint(12, GridPosition(8, 6)),
            NumberedCheckpoint(13, GridPosition(9, 9)), NumberedCheckpoint(14, GridPosition(9, 8)), NumberedCheckpoint(15, GridPosition(9, 10)), NumberedCheckpoint(16, GridPosition(7, 7)),
            NumberedCheckpoint(17, GridPosition(4, 5)), NumberedCheckpoint(18, GridPosition(5, 8)), NumberedCheckpoint(19, GridPosition(5, 6)), NumberedCheckpoint(20, GridPosition(3, 6)),
            NumberedCheckpoint(21, GridPosition(0, 6)), NumberedCheckpoint(22, GridPosition(2, 8)), NumberedCheckpoint(23, GridPosition(1, 8)), NumberedCheckpoint(24, GridPosition(6, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7))
        ),
        difficultyMetadata = "World 6 - Level 226 (seed=6227)",
        seed = 6227L
    )

    fun buildSolution226(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10)
        )
    )

    // --- Level 227: World 6 - Level 227 (11x11) ---

    fun buildLevel227(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl227",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(1, 6)), NumberedCheckpoint(3, GridPosition(2, 6)), NumberedCheckpoint(4, GridPosition(0, 9)),
            NumberedCheckpoint(5, GridPosition(2, 8)), NumberedCheckpoint(6, GridPosition(3, 8)), NumberedCheckpoint(7, GridPosition(5, 9)), NumberedCheckpoint(8, GridPosition(5, 5)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(5, 2)), NumberedCheckpoint(12, GridPosition(9, 3)),
            NumberedCheckpoint(13, GridPosition(8, 6)), NumberedCheckpoint(14, GridPosition(9, 5)), NumberedCheckpoint(15, GridPosition(9, 2)), NumberedCheckpoint(16, GridPosition(6, 2)),
            NumberedCheckpoint(17, GridPosition(6, 6)), NumberedCheckpoint(18, GridPosition(9, 8)), NumberedCheckpoint(19, GridPosition(6, 10)), NumberedCheckpoint(20, GridPosition(7, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 8), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8))
        ),
        difficultyMetadata = "World 6 - Level 227 (seed=6246)",
        seed = 6246L
    )

    fun buildSolution227(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9),
            GridPosition(7, 9)
        )
    )

    // --- Level 228: World 6 - Level 228 (11x11) ---

    fun buildLevel228(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl228",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(2, 1)), NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(6, 1)), NumberedCheckpoint(6, GridPosition(1, 3)), NumberedCheckpoint(7, GridPosition(0, 8)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(5, 3)), NumberedCheckpoint(10, GridPosition(9, 2)), NumberedCheckpoint(11, GridPosition(8, 0)), NumberedCheckpoint(12, GridPosition(10, 3)),
            NumberedCheckpoint(13, GridPosition(9, 4)), NumberedCheckpoint(14, GridPosition(10, 7)), NumberedCheckpoint(15, GridPosition(8, 4)), NumberedCheckpoint(16, GridPosition(6, 5)),
            NumberedCheckpoint(17, GridPosition(5, 7)), NumberedCheckpoint(18, GridPosition(8, 7)), NumberedCheckpoint(19, GridPosition(4, 7)), NumberedCheckpoint(20, GridPosition(5, 9)),
            NumberedCheckpoint(21, GridPosition(6, 9)), NumberedCheckpoint(22, GridPosition(9, 9)), NumberedCheckpoint(23, GridPosition(10, 8)), NumberedCheckpoint(24, GridPosition(8, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 6 - Level 228 (seed=6241)",
        seed = 6241L
    )

    fun buildSolution228(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(8, 8)
        )
    )

    // --- Level 229: World 6 - Level 229 (11x11) ---

    fun buildLevel229(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl229",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 7)), NumberedCheckpoint(2, GridPosition(6, 9)), NumberedCheckpoint(3, GridPosition(5, 7)), NumberedCheckpoint(4, GridPosition(7, 5)),
            NumberedCheckpoint(5, GridPosition(2, 4)), NumberedCheckpoint(6, GridPosition(7, 4)), NumberedCheckpoint(7, GridPosition(5, 3)), NumberedCheckpoint(8, GridPosition(4, 2)),
            NumberedCheckpoint(9, GridPosition(2, 0)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(5, 0)), NumberedCheckpoint(12, GridPosition(8, 2)),
            NumberedCheckpoint(13, GridPosition(10, 1)), NumberedCheckpoint(14, GridPosition(9, 6)), NumberedCheckpoint(15, GridPosition(8, 8)), NumberedCheckpoint(16, GridPosition(9, 10)),
            NumberedCheckpoint(17, GridPosition(8, 9)), NumberedCheckpoint(18, GridPosition(6, 10)), NumberedCheckpoint(19, GridPosition(3, 8)), NumberedCheckpoint(20, GridPosition(1, 7)),
            NumberedCheckpoint(21, GridPosition(2, 10)), NumberedCheckpoint(22, GridPosition(0, 5)), NumberedCheckpoint(23, GridPosition(0, 0)), NumberedCheckpoint(24, GridPosition(1, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 6 - Level 229 (seed=6232)",
        seed = 6232L
    )

    fun buildSolution229(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5)
        )
    )

    // --- Level 230: World 6 - Level 230 (11x11) ---

    fun buildLevel230(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl230",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(0, 4)), NumberedCheckpoint(4, GridPosition(0, 9)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(3, 2)), NumberedCheckpoint(8, GridPosition(2, 8)),
            NumberedCheckpoint(9, GridPosition(3, 8)), NumberedCheckpoint(10, GridPosition(2, 10)), NumberedCheckpoint(11, GridPosition(8, 8)), NumberedCheckpoint(12, GridPosition(10, 9)),
            NumberedCheckpoint(13, GridPosition(7, 7)), NumberedCheckpoint(14, GridPosition(4, 9)), NumberedCheckpoint(15, GridPosition(6, 7)), NumberedCheckpoint(16, GridPosition(5, 6)),
            NumberedCheckpoint(17, GridPosition(9, 6)), NumberedCheckpoint(18, GridPosition(7, 3)), NumberedCheckpoint(19, GridPosition(2, 1)), NumberedCheckpoint(20, GridPosition(5, 0)),
            NumberedCheckpoint(21, GridPosition(7, 1)), NumberedCheckpoint(22, GridPosition(10, 1)), NumberedCheckpoint(23, GridPosition(9, 3)), NumberedCheckpoint(24, GridPosition(10, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(10, 1), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 6 - Level 230 (seed=6232)",
        seed = 6232L
    )

    fun buildSolution230(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6)
        )
    )

    // --- Level 231: World 6 - Level 231 (11x11) ---

    fun buildLevel231(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl231",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(1, 10)), NumberedCheckpoint(3, GridPosition(2, 8)), NumberedCheckpoint(4, GridPosition(5, 10)),
            NumberedCheckpoint(5, GridPosition(3, 7)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(3, 5)), NumberedCheckpoint(8, GridPosition(1, 4)),
            NumberedCheckpoint(9, GridPosition(1, 7)), NumberedCheckpoint(10, GridPosition(0, 2)), NumberedCheckpoint(11, GridPosition(6, 0)), NumberedCheckpoint(12, GridPosition(5, 4)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(6, 7)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(9, 0)),
            NumberedCheckpoint(17, GridPosition(8, 2)), NumberedCheckpoint(18, GridPosition(9, 4)), NumberedCheckpoint(19, GridPosition(9, 3)), NumberedCheckpoint(20, GridPosition(10, 5)),
            NumberedCheckpoint(21, GridPosition(9, 7)), NumberedCheckpoint(22, GridPosition(7, 9)), NumberedCheckpoint(23, GridPosition(9, 8)), NumberedCheckpoint(24, GridPosition(8, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(1, 10)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3))
        ),
        difficultyMetadata = "World 6 - Level 231 (seed=6237)",
        seed = 6237L
    )

    fun buildSolution231(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6)
        )
    )

    // --- Level 232: World 6 - Level 232 (11x11) ---

    fun buildLevel232(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl232",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 3)), NumberedCheckpoint(2, GridPosition(8, 3)), NumberedCheckpoint(3, GridPosition(9, 0)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(9, 6)), NumberedCheckpoint(7, GridPosition(9, 9)), NumberedCheckpoint(8, GridPosition(8, 9)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(5, 5)), NumberedCheckpoint(11, GridPosition(5, 3)), NumberedCheckpoint(12, GridPosition(4, 6)),
            NumberedCheckpoint(13, GridPosition(3, 3)), NumberedCheckpoint(14, GridPosition(5, 2)), NumberedCheckpoint(15, GridPosition(2, 1)), NumberedCheckpoint(16, GridPosition(1, 4)),
            NumberedCheckpoint(17, GridPosition(0, 6)), NumberedCheckpoint(18, GridPosition(4, 9)), NumberedCheckpoint(19, GridPosition(7, 9)), NumberedCheckpoint(20, GridPosition(4, 7)),
            NumberedCheckpoint(21, GridPosition(1, 5)), NumberedCheckpoint(22, GridPosition(4, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2))
        ),
        difficultyMetadata = "World 6 - Level 232 (seed=6246)",
        seed = 6246L
    )

    fun buildSolution232(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8)
        )
    )

    // --- Level 233: World 6 - Level 233 (11x11) ---

    fun buildLevel233(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl233",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(0, 6)), NumberedCheckpoint(3, GridPosition(1, 10)), NumberedCheckpoint(4, GridPosition(5, 9)),
            NumberedCheckpoint(5, GridPosition(7, 9)), NumberedCheckpoint(6, GridPosition(8, 7)), NumberedCheckpoint(7, GridPosition(4, 8)), NumberedCheckpoint(8, GridPosition(3, 8)),
            NumberedCheckpoint(9, GridPosition(4, 7)), NumberedCheckpoint(10, GridPosition(3, 6)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(2, 0)),
            NumberedCheckpoint(13, GridPosition(1, 2)), NumberedCheckpoint(14, GridPosition(10, 3)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(8, 1)),
            NumberedCheckpoint(17, GridPosition(6, 1)), NumberedCheckpoint(18, GridPosition(10, 9)), NumberedCheckpoint(19, GridPosition(9, 7)), NumberedCheckpoint(20, GridPosition(6, 7)),
            NumberedCheckpoint(21, GridPosition(4, 5)), NumberedCheckpoint(22, GridPosition(3, 3)), NumberedCheckpoint(23, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 6 - Level 233 (seed=6240)",
        seed = 6240L
    )

    fun buildSolution233(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(5, 3)
        )
    )

    // --- Level 234: World 6 - Level 234 (11x11) ---

    fun buildLevel234(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl234",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 0)), NumberedCheckpoint(2, GridPosition(10, 4)), NumberedCheckpoint(3, GridPosition(8, 6)), NumberedCheckpoint(4, GridPosition(5, 5)),
            NumberedCheckpoint(5, GridPosition(5, 3)), NumberedCheckpoint(6, GridPosition(4, 5)), NumberedCheckpoint(7, GridPosition(3, 7)), NumberedCheckpoint(8, GridPosition(5, 7)),
            NumberedCheckpoint(9, GridPosition(2, 9)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(5, 2)), NumberedCheckpoint(12, GridPosition(7, 1)),
            NumberedCheckpoint(13, GridPosition(2, 2)), NumberedCheckpoint(14, GridPosition(1, 0)), NumberedCheckpoint(15, GridPosition(1, 6)), NumberedCheckpoint(16, GridPosition(9, 8)),
            NumberedCheckpoint(17, GridPosition(7, 7)), NumberedCheckpoint(18, GridPosition(10, 5)), NumberedCheckpoint(19, GridPosition(4, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(9, 0)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 6 - Level 234 (seed=6238)",
        seed = 6238L
    )

    fun buildSolution234(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10)
        )
    )

    // --- Level 235: World 6 - Level 235 (11x11) ---

    fun buildLevel235(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl235",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(8, 1)), NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(2, 3)), NumberedCheckpoint(6, GridPosition(1, 1)), NumberedCheckpoint(7, GridPosition(5, 2)), NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(4, 3)), NumberedCheckpoint(10, GridPosition(7, 5)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(9, 2)),
            NumberedCheckpoint(13, GridPosition(10, 5)), NumberedCheckpoint(14, GridPosition(8, 6)), NumberedCheckpoint(15, GridPosition(3, 6)), NumberedCheckpoint(16, GridPosition(0, 5)),
            NumberedCheckpoint(17, GridPosition(3, 7)), NumberedCheckpoint(18, GridPosition(5, 8)), NumberedCheckpoint(19, GridPosition(9, 6)), NumberedCheckpoint(20, GridPosition(9, 9)),
            NumberedCheckpoint(21, GridPosition(4, 9)), NumberedCheckpoint(22, GridPosition(0, 8)), NumberedCheckpoint(23, GridPosition(2, 9)), NumberedCheckpoint(24, GridPosition(1, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10))
        ),
        difficultyMetadata = "World 6 - Level 235 (seed=6261)",
        seed = 6261L
    )

    fun buildSolution235(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(1, 9)
        )
    )

    // --- Level 236: World 6 - Level 236 (11x11) ---

    fun buildLevel236(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl236",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(4, 3)), NumberedCheckpoint(4, GridPosition(5, 1)),
            NumberedCheckpoint(5, GridPosition(10, 0)), NumberedCheckpoint(6, GridPosition(7, 2)), NumberedCheckpoint(7, GridPosition(9, 3)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(8, 8)), NumberedCheckpoint(10, GridPosition(3, 10)), NumberedCheckpoint(11, GridPosition(7, 9)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(7, 6)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(3, 2)), NumberedCheckpoint(16, GridPosition(0, 9)),
            NumberedCheckpoint(17, GridPosition(1, 8)), NumberedCheckpoint(18, GridPosition(5, 7)), NumberedCheckpoint(19, GridPosition(3, 6)), NumberedCheckpoint(20, GridPosition(1, 5)),
            NumberedCheckpoint(21, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5))
        ),
        difficultyMetadata = "World 6 - Level 236 (seed=6236)",
        seed = 6236L
    )

    fun buildSolution236(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6)
        )
    )

    // --- Level 237: World 6 - Level 237 (11x11) ---

    fun buildLevel237(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl237",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)), NumberedCheckpoint(2, GridPosition(3, 3)), NumberedCheckpoint(3, GridPosition(3, 4)), NumberedCheckpoint(4, GridPosition(7, 5)),
            NumberedCheckpoint(5, GridPosition(3, 7)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(3, 9)), NumberedCheckpoint(8, GridPosition(5, 10)),
            NumberedCheckpoint(9, GridPosition(2, 7)), NumberedCheckpoint(10, GridPosition(0, 0)), NumberedCheckpoint(11, GridPosition(4, 1)), NumberedCheckpoint(12, GridPosition(1, 3)),
            NumberedCheckpoint(13, GridPosition(2, 5)), NumberedCheckpoint(14, GridPosition(5, 2)), NumberedCheckpoint(15, GridPosition(7, 1)), NumberedCheckpoint(16, GridPosition(6, 3)),
            NumberedCheckpoint(17, GridPosition(10, 1)), NumberedCheckpoint(18, GridPosition(9, 2)), NumberedCheckpoint(19, GridPosition(9, 4)), NumberedCheckpoint(20, GridPosition(9, 5)),
            NumberedCheckpoint(21, GridPosition(7, 9)), NumberedCheckpoint(22, GridPosition(9, 8)), NumberedCheckpoint(23, GridPosition(8, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(0, 9)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5))
        ),
        difficultyMetadata = "World 6 - Level 237 (seed=6257)",
        seed = 6257L
    )

    fun buildSolution237(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(8, 8)
        )
    )

    // --- Level 238: World 6 - Level 238 (11x11) ---

    fun buildLevel238(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl238",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 7)), NumberedCheckpoint(2, GridPosition(1, 9)), NumberedCheckpoint(3, GridPosition(1, 8)), NumberedCheckpoint(4, GridPosition(3, 3)),
            NumberedCheckpoint(5, GridPosition(4, 5)), NumberedCheckpoint(6, GridPosition(4, 8)), NumberedCheckpoint(7, GridPosition(6, 9)), NumberedCheckpoint(8, GridPosition(8, 8)),
            NumberedCheckpoint(9, GridPosition(7, 9)), NumberedCheckpoint(10, GridPosition(7, 6)), NumberedCheckpoint(11, GridPosition(10, 4)), NumberedCheckpoint(12, GridPosition(10, 2)),
            NumberedCheckpoint(13, GridPosition(8, 1)), NumberedCheckpoint(14, GridPosition(10, 1)), NumberedCheckpoint(15, GridPosition(6, 1)), NumberedCheckpoint(16, GridPosition(7, 2)),
            NumberedCheckpoint(17, GridPosition(4, 1)), NumberedCheckpoint(18, GridPosition(1, 1)), NumberedCheckpoint(19, GridPosition(0, 3)), NumberedCheckpoint(20, GridPosition(2, 2)),
            NumberedCheckpoint(21, GridPosition(5, 4)), NumberedCheckpoint(22, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4))
        ),
        difficultyMetadata = "World 6 - Level 238 (seed=6239)",
        seed = 6239L
    )

    fun buildSolution238(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(4, 4)
        )
    )

    // --- Level 239: World 6 - Level 239 (11x11) ---

    fun buildLevel239(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl239",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 5)), NumberedCheckpoint(2, GridPosition(3, 1)), NumberedCheckpoint(3, GridPosition(3, 4)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(7, 3)), NumberedCheckpoint(7, GridPosition(4, 0)), NumberedCheckpoint(8, GridPosition(1, 0)),
            NumberedCheckpoint(9, GridPosition(1, 3)), NumberedCheckpoint(10, GridPosition(1, 4)), NumberedCheckpoint(11, GridPosition(0, 6)), NumberedCheckpoint(12, GridPosition(3, 8)),
            NumberedCheckpoint(13, GridPosition(0, 8)), NumberedCheckpoint(14, GridPosition(2, 9)), NumberedCheckpoint(15, GridPosition(10, 7)), NumberedCheckpoint(16, GridPosition(7, 9)),
            NumberedCheckpoint(17, GridPosition(6, 7)), NumberedCheckpoint(18, GridPosition(4, 8)), NumberedCheckpoint(19, GridPosition(5, 6)), NumberedCheckpoint(20, GridPosition(10, 6)),
            NumberedCheckpoint(21, GridPosition(9, 4)), NumberedCheckpoint(22, GridPosition(9, 1)), NumberedCheckpoint(23, GridPosition(10, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1))
        ),
        difficultyMetadata = "World 6 - Level 239 (seed=6247)",
        seed = 6247L
    )

    fun buildSolution239(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2)
        )
    )

    // --- Level 240: World 6 - Level 240 (11x11) ---

    fun buildLevel240(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl240",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(3, 2)), NumberedCheckpoint(3, GridPosition(6, 0)), NumberedCheckpoint(4, GridPosition(10, 2)),
            NumberedCheckpoint(5, GridPosition(10, 10)), NumberedCheckpoint(6, GridPosition(8, 9)), NumberedCheckpoint(7, GridPosition(5, 7)), NumberedCheckpoint(8, GridPosition(6, 10)),
            NumberedCheckpoint(9, GridPosition(3, 8)), NumberedCheckpoint(10, GridPosition(2, 7)), NumberedCheckpoint(11, GridPosition(1, 9)), NumberedCheckpoint(12, GridPosition(2, 10)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(2, 6)), NumberedCheckpoint(15, GridPosition(5, 1)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(8, 3)), NumberedCheckpoint(18, GridPosition(7, 2)), NumberedCheckpoint(19, GridPosition(7, 4)), NumberedCheckpoint(20, GridPosition(9, 4)),
            NumberedCheckpoint(21, GridPosition(9, 7)), NumberedCheckpoint(22, GridPosition(7, 6)), NumberedCheckpoint(23, GridPosition(5, 5)), NumberedCheckpoint(24, GridPosition(4, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 6 - Level 240 (seed=6247)",
        seed = 6247L
    )

    fun buildSolution240(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6)
        )
    )

    // --- Level 241: World 6 - Level 241 (12x12) ---

    fun buildLevel241(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl241",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 8)), NumberedCheckpoint(2, GridPosition(3, 10)), NumberedCheckpoint(3, GridPosition(1, 3)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(1, 1)), NumberedCheckpoint(6, GridPosition(4, 2)), NumberedCheckpoint(7, GridPosition(3, 4)), NumberedCheckpoint(8, GridPosition(3, 6)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(5, 6)), NumberedCheckpoint(11, GridPosition(9, 6)), NumberedCheckpoint(12, GridPosition(10, 2)),
            NumberedCheckpoint(13, GridPosition(8, 5)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(5, 4)), NumberedCheckpoint(16, GridPosition(6, 1)),
            NumberedCheckpoint(17, GridPosition(2, 0)), NumberedCheckpoint(18, GridPosition(8, 1)), NumberedCheckpoint(19, GridPosition(10, 7)), NumberedCheckpoint(20, GridPosition(9, 10)),
            NumberedCheckpoint(21, GridPosition(11, 11)), NumberedCheckpoint(22, GridPosition(8, 9)), NumberedCheckpoint(23, GridPosition(4, 9)), NumberedCheckpoint(24, GridPosition(7, 11)),
            NumberedCheckpoint(25, GridPosition(2, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(0, 9)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(10, 7), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(11, 8), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "World 6 - Level 241 (seed=6241)",
        seed = 6241L
    )

    fun buildSolution241(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11)
        )
    )

    // --- Level 242: World 6 - Level 242 (12x12) ---

    fun buildLevel242(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl242",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)), NumberedCheckpoint(2, GridPosition(3, 3)), NumberedCheckpoint(3, GridPosition(1, 6)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(4, 6)), NumberedCheckpoint(6, GridPosition(5, 7)), NumberedCheckpoint(7, GridPosition(7, 6)), NumberedCheckpoint(8, GridPosition(7, 8)),
            NumberedCheckpoint(9, GridPosition(4, 8)), NumberedCheckpoint(10, GridPosition(1, 8)), NumberedCheckpoint(11, GridPosition(2, 10)), NumberedCheckpoint(12, GridPosition(1, 11)),
            NumberedCheckpoint(13, GridPosition(4, 10)), NumberedCheckpoint(14, GridPosition(11, 11)), NumberedCheckpoint(15, GridPosition(9, 10)), NumberedCheckpoint(16, GridPosition(7, 2)),
            NumberedCheckpoint(17, GridPosition(6, 1)), NumberedCheckpoint(18, GridPosition(7, 3)), NumberedCheckpoint(19, GridPosition(9, 3)), NumberedCheckpoint(20, GridPosition(10, 1)),
            NumberedCheckpoint(21, GridPosition(10, 6)), NumberedCheckpoint(22, GridPosition(8, 6)), NumberedCheckpoint(23, GridPosition(6, 4)), NumberedCheckpoint(24, GridPosition(6, 5)),
            NumberedCheckpoint(25, GridPosition(5, 4)), NumberedCheckpoint(26, GridPosition(4, 2)), NumberedCheckpoint(27, GridPosition(5, 1)), NumberedCheckpoint(28, GridPosition(1, 4)),
            NumberedCheckpoint(29, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(10, 10), GridPosition(11, 10)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(10, 5), GridPosition(10, 6))
        ),
        difficultyMetadata = "World 6 - Level 242 (seed=6243)",
        seed = 6243L
    )

    fun buildSolution242(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11),
            GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0)
        )
    )

    // --- Level 243: World 6 - Level 243 (12x12) ---

    fun buildLevel243(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl243",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 5)), NumberedCheckpoint(2, GridPosition(10, 2)), NumberedCheckpoint(3, GridPosition(7, 2)), NumberedCheckpoint(4, GridPosition(11, 1)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(2, 2)), NumberedCheckpoint(7, GridPosition(3, 3)), NumberedCheckpoint(8, GridPosition(1, 6)),
            NumberedCheckpoint(9, GridPosition(0, 4)), NumberedCheckpoint(10, GridPosition(0, 5)), NumberedCheckpoint(11, GridPosition(0, 10)), NumberedCheckpoint(12, GridPosition(3, 8)),
            NumberedCheckpoint(13, GridPosition(2, 9)), NumberedCheckpoint(14, GridPosition(3, 10)), NumberedCheckpoint(15, GridPosition(6, 11)), NumberedCheckpoint(16, GridPosition(9, 10)),
            NumberedCheckpoint(17, GridPosition(6, 9)), NumberedCheckpoint(18, GridPosition(7, 7)), NumberedCheckpoint(19, GridPosition(5, 10)), NumberedCheckpoint(20, GridPosition(4, 7)),
            NumberedCheckpoint(21, GridPosition(5, 1)), NumberedCheckpoint(22, GridPosition(8, 5)), NumberedCheckpoint(23, GridPosition(6, 5)), NumberedCheckpoint(24, GridPosition(8, 6)),
            NumberedCheckpoint(25, GridPosition(11, 6)), NumberedCheckpoint(26, GridPosition(11, 10)), NumberedCheckpoint(27, GridPosition(10, 8)), NumberedCheckpoint(28, GridPosition(8, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(1, 10), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(9, 10), GridPosition(10, 10)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(11, 3), GridPosition(11, 4)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7))
        ),
        difficultyMetadata = "World 6 - Level 243 (seed=6246)",
        seed = 6246L
    )

    fun buildSolution243(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 11),
            GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9)
        )
    )

    // --- Level 244: World 6 - Level 244 (12x12) ---

    fun buildLevel244(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl244",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(5, 1)), NumberedCheckpoint(3, GridPosition(4, 3)), NumberedCheckpoint(4, GridPosition(1, 1)),
            NumberedCheckpoint(5, GridPosition(0, 3)), NumberedCheckpoint(6, GridPosition(0, 7)), NumberedCheckpoint(7, GridPosition(3, 11)), NumberedCheckpoint(8, GridPosition(2, 9)),
            NumberedCheckpoint(9, GridPosition(4, 6)), NumberedCheckpoint(10, GridPosition(2, 6)), NumberedCheckpoint(11, GridPosition(1, 7)), NumberedCheckpoint(12, GridPosition(1, 2)),
            NumberedCheckpoint(13, GridPosition(3, 5)), NumberedCheckpoint(14, GridPosition(10, 5)), NumberedCheckpoint(15, GridPosition(8, 2)), NumberedCheckpoint(16, GridPosition(9, 5)),
            NumberedCheckpoint(17, GridPosition(8, 6)), NumberedCheckpoint(18, GridPosition(10, 7)), NumberedCheckpoint(19, GridPosition(9, 7)), NumberedCheckpoint(20, GridPosition(7, 8)),
            NumberedCheckpoint(21, GridPosition(10, 9)), NumberedCheckpoint(22, GridPosition(9, 9)), NumberedCheckpoint(23, GridPosition(11, 10)), NumberedCheckpoint(24, GridPosition(7, 11)),
            NumberedCheckpoint(25, GridPosition(4, 9)), NumberedCheckpoint(26, GridPosition(8, 9)), NumberedCheckpoint(27, GridPosition(6, 8)), NumberedCheckpoint(28, GridPosition(6, 5)),
            NumberedCheckpoint(29, GridPosition(7, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(0, 10)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(9, 0)),
            BlockedEdge.between(GridPosition(11, 5), GridPosition(11, 6))
        ),
        difficultyMetadata = "World 6 - Level 244 (seed=6248)",
        seed = 6248L
    )

    fun buildSolution244(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4)
        )
    )

    // --- Level 245: World 6 - Level 245 (12x12) ---

    fun buildLevel245(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl245",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(3, 10)), NumberedCheckpoint(3, GridPosition(10, 7)), NumberedCheckpoint(4, GridPosition(8, 4)),
            NumberedCheckpoint(5, GridPosition(11, 6)), NumberedCheckpoint(6, GridPosition(11, 1)), NumberedCheckpoint(7, GridPosition(9, 3)), NumberedCheckpoint(8, GridPosition(9, 0)),
            NumberedCheckpoint(9, GridPosition(0, 5)), NumberedCheckpoint(10, GridPosition(2, 2)), NumberedCheckpoint(11, GridPosition(1, 6)), NumberedCheckpoint(12, GridPosition(2, 7)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(5, 6)), NumberedCheckpoint(15, GridPosition(3, 4)), NumberedCheckpoint(16, GridPosition(3, 2)),
            NumberedCheckpoint(17, GridPosition(5, 1)), NumberedCheckpoint(18, GridPosition(7, 2)), NumberedCheckpoint(19, GridPosition(7, 1)), NumberedCheckpoint(20, GridPosition(7, 5)),
            NumberedCheckpoint(21, GridPosition(6, 5)), NumberedCheckpoint(22, GridPosition(7, 7)), NumberedCheckpoint(23, GridPosition(5, 8)), NumberedCheckpoint(24, GridPosition(4, 7)),
            NumberedCheckpoint(25, GridPosition(1, 11)), NumberedCheckpoint(26, GridPosition(1, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(3, 10), GridPosition(3, 11)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0))
        ),
        difficultyMetadata = "World 6 - Level 245 (seed=6247)",
        seed = 6247L
    )

    fun buildSolution245(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(1, 9), GridPosition(1, 10), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8)
        )
    )

    // --- Level 246: World 6 - Level 246 (12x12) ---

    fun buildLevel246(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl246",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 11)), NumberedCheckpoint(2, GridPosition(11, 11)), NumberedCheckpoint(3, GridPosition(9, 8)), NumberedCheckpoint(4, GridPosition(7, 10)),
            NumberedCheckpoint(5, GridPosition(6, 10)), NumberedCheckpoint(6, GridPosition(9, 7)), NumberedCheckpoint(7, GridPosition(11, 9)), NumberedCheckpoint(8, GridPosition(2, 5)),
            NumberedCheckpoint(9, GridPosition(1, 11)), NumberedCheckpoint(10, GridPosition(3, 10)), NumberedCheckpoint(11, GridPosition(1, 9)), NumberedCheckpoint(12, GridPosition(2, 7)),
            NumberedCheckpoint(13, GridPosition(4, 6)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(6, 7)), NumberedCheckpoint(16, GridPosition(4, 4)),
            NumberedCheckpoint(17, GridPosition(6, 2)), NumberedCheckpoint(18, GridPosition(8, 5)), NumberedCheckpoint(19, GridPosition(10, 5)), NumberedCheckpoint(20, GridPosition(8, 3)),
            NumberedCheckpoint(21, GridPosition(9, 3)), NumberedCheckpoint(22, GridPosition(6, 1)), NumberedCheckpoint(23, GridPosition(2, 3)), NumberedCheckpoint(24, GridPosition(2, 4)),
            NumberedCheckpoint(25, GridPosition(2, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(11, 2), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(0, 6)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(1, 10))
        ),
        difficultyMetadata = "World 6 - Level 246 (seed=6247)",
        seed = 6247L
    )

    fun buildSolution246(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1)
        )
    )

    // --- Level 247: World 6 - Level 247 (12x12) ---

    fun buildLevel247(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl247",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(2, 7)), NumberedCheckpoint(3, GridPosition(4, 8)), NumberedCheckpoint(4, GridPosition(7, 10)),
            NumberedCheckpoint(5, GridPosition(4, 9)), NumberedCheckpoint(6, GridPosition(0, 8)), NumberedCheckpoint(7, GridPosition(1, 10)), NumberedCheckpoint(8, GridPosition(10, 9)),
            NumberedCheckpoint(9, GridPosition(8, 9)), NumberedCheckpoint(10, GridPosition(4, 5)), NumberedCheckpoint(11, GridPosition(5, 4)), NumberedCheckpoint(12, GridPosition(3, 4)),
            NumberedCheckpoint(13, GridPosition(1, 4)), NumberedCheckpoint(14, GridPosition(4, 2)), NumberedCheckpoint(15, GridPosition(5, 3)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(6, 5)), NumberedCheckpoint(18, GridPosition(7, 6)), NumberedCheckpoint(19, GridPosition(8, 6)), NumberedCheckpoint(20, GridPosition(10, 8)),
            NumberedCheckpoint(21, GridPosition(9, 6)), NumberedCheckpoint(22, GridPosition(11, 5)), NumberedCheckpoint(23, GridPosition(7, 4)), NumberedCheckpoint(24, GridPosition(8, 1)),
            NumberedCheckpoint(25, GridPosition(6, 1)), NumberedCheckpoint(26, GridPosition(1, 1)), NumberedCheckpoint(27, GridPosition(7, 0)), NumberedCheckpoint(28, GridPosition(9, 1)),
            NumberedCheckpoint(29, GridPosition(11, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1))
        ),
        difficultyMetadata = "World 6 - Level 247 (seed=6254)",
        seed = 6254L
    )

    fun buildSolution247(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0)
        )
    )

    // --- Level 248: World 6 - Level 248 (12x12) ---

    fun buildLevel248(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl248",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(7, 5)), NumberedCheckpoint(3, GridPosition(5, 5)), NumberedCheckpoint(4, GridPosition(1, 10)),
            NumberedCheckpoint(5, GridPosition(4, 7)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(7, 9)), NumberedCheckpoint(8, GridPosition(7, 11)),
            NumberedCheckpoint(9, GridPosition(4, 11)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(1, 5)), NumberedCheckpoint(12, GridPosition(2, 3)),
            NumberedCheckpoint(13, GridPosition(1, 1)), NumberedCheckpoint(14, GridPosition(2, 1)), NumberedCheckpoint(15, GridPosition(7, 1)), NumberedCheckpoint(16, GridPosition(11, 0)),
            NumberedCheckpoint(17, GridPosition(8, 1)), NumberedCheckpoint(18, GridPosition(11, 3)), NumberedCheckpoint(19, GridPosition(7, 3)), NumberedCheckpoint(20, GridPosition(6, 2)),
            NumberedCheckpoint(21, GridPosition(4, 1)), NumberedCheckpoint(22, GridPosition(4, 4)), NumberedCheckpoint(23, GridPosition(6, 4)), NumberedCheckpoint(24, GridPosition(10, 6)),
            NumberedCheckpoint(25, GridPosition(10, 8)), NumberedCheckpoint(26, GridPosition(11, 9)), NumberedCheckpoint(27, GridPosition(9, 6)), NumberedCheckpoint(28, GridPosition(8, 7)),
            NumberedCheckpoint(29, GridPosition(10, 10)), NumberedCheckpoint(30, GridPosition(11, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 6), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3))
        ),
        difficultyMetadata = "World 6 - Level 248 (seed=6248)",
        seed = 6248L
    )

    fun buildSolution248(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(8, 10), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10)
        )
    )

    // --- Level 249: World 6 - Level 249 (12x12) ---

    fun buildLevel249(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl249",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 11)), NumberedCheckpoint(2, GridPosition(9, 8)), NumberedCheckpoint(3, GridPosition(9, 4)), NumberedCheckpoint(4, GridPosition(11, 5)),
            NumberedCheckpoint(5, GridPosition(10, 3)), NumberedCheckpoint(6, GridPosition(10, 1)), NumberedCheckpoint(7, GridPosition(6, 2)), NumberedCheckpoint(8, GridPosition(8, 1)),
            NumberedCheckpoint(9, GridPosition(5, 1)), NumberedCheckpoint(10, GridPosition(3, 2)), NumberedCheckpoint(11, GridPosition(2, 1)), NumberedCheckpoint(12, GridPosition(1, 3)),
            NumberedCheckpoint(13, GridPosition(2, 5)), NumberedCheckpoint(14, GridPosition(0, 5)), NumberedCheckpoint(15, GridPosition(5, 4)), NumberedCheckpoint(16, GridPosition(7, 5)),
            NumberedCheckpoint(17, GridPosition(5, 5)), NumberedCheckpoint(18, GridPosition(8, 6)), NumberedCheckpoint(19, GridPosition(6, 7)), NumberedCheckpoint(20, GridPosition(4, 8)),
            NumberedCheckpoint(21, GridPosition(1, 10)), NumberedCheckpoint(22, GridPosition(3, 10)), NumberedCheckpoint(23, GridPosition(6, 11)), NumberedCheckpoint(24, GridPosition(7, 10)),
            NumberedCheckpoint(25, GridPosition(10, 10)), NumberedCheckpoint(26, GridPosition(8, 9)), NumberedCheckpoint(27, GridPosition(10, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(11, 6), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(8, 11))
        ),
        difficultyMetadata = "World 6 - Level 249 (seed=6249)",
        seed = 6249L
    )

    fun buildSolution249(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(5, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9)
        )
    )

    // --- Level 250: World 6 - Level 250 (12x12) ---

    fun buildLevel250(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl250",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(2, 3)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(2, 6)), NumberedCheckpoint(6, GridPosition(5, 8)), NumberedCheckpoint(7, GridPosition(5, 10)), NumberedCheckpoint(8, GridPosition(8, 10)),
            NumberedCheckpoint(9, GridPosition(6, 8)), NumberedCheckpoint(10, GridPosition(3, 6)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(2, 0)),
            NumberedCheckpoint(13, GridPosition(6, 3)), NumberedCheckpoint(14, GridPosition(4, 5)), NumberedCheckpoint(15, GridPosition(7, 7)), NumberedCheckpoint(16, GridPosition(7, 4)),
            NumberedCheckpoint(17, GridPosition(5, 2)), NumberedCheckpoint(18, GridPosition(7, 1)), NumberedCheckpoint(19, GridPosition(9, 4)), NumberedCheckpoint(20, GridPosition(10, 2)),
            NumberedCheckpoint(21, GridPosition(11, 1)), NumberedCheckpoint(22, GridPosition(11, 6)), NumberedCheckpoint(23, GridPosition(9, 7)), NumberedCheckpoint(24, GridPosition(10, 8)),
            NumberedCheckpoint(25, GridPosition(10, 9)), NumberedCheckpoint(26, GridPosition(2, 9)), NumberedCheckpoint(27, GridPosition(2, 11)), NumberedCheckpoint(28, GridPosition(0, 8)),
            NumberedCheckpoint(29, GridPosition(1, 7)), NumberedCheckpoint(30, GridPosition(3, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(8, 10), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(5, 10))
        ),
        difficultyMetadata = "World 6 - Level 250 (seed=6251)",
        seed = 6251L
    )

    fun buildSolution250(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 8), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[201] = buildLevel201()
        map[202] = buildLevel202()
        map[203] = buildLevel203()
        map[204] = buildLevel204()
        map[205] = buildLevel205()
        map[206] = buildLevel206()
        map[207] = buildLevel207()
        map[208] = buildLevel208()
        map[209] = buildLevel209()
        map[210] = buildLevel210()
        map[211] = buildLevel211()
        map[212] = buildLevel212()
        map[213] = buildLevel213()
        map[214] = buildLevel214()
        map[215] = buildLevel215()
        map[216] = buildLevel216()
        map[217] = buildLevel217()
        map[218] = buildLevel218()
        map[219] = buildLevel219()
        map[220] = buildLevel220()
        map[221] = buildLevel221()
        map[222] = buildLevel222()
        map[223] = buildLevel223()
        map[224] = buildLevel224()
        map[225] = buildLevel225()
        map[226] = buildLevel226()
        map[227] = buildLevel227()
        map[228] = buildLevel228()
        map[229] = buildLevel229()
        map[230] = buildLevel230()
        map[231] = buildLevel231()
        map[232] = buildLevel232()
        map[233] = buildLevel233()
        map[234] = buildLevel234()
        map[235] = buildLevel235()
        map[236] = buildLevel236()
        map[237] = buildLevel237()
        map[238] = buildLevel238()
        map[239] = buildLevel239()
        map[240] = buildLevel240()
        map[241] = buildLevel241()
        map[242] = buildLevel242()
        map[243] = buildLevel243()
        map[244] = buildLevel244()
        map[245] = buildLevel245()
        map[246] = buildLevel246()
        map[247] = buildLevel247()
        map[248] = buildLevel248()
        map[249] = buildLevel249()
        map[250] = buildLevel250()
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[201] = buildSolution201()
        map[202] = buildSolution202()
        map[203] = buildSolution203()
        map[204] = buildSolution204()
        map[205] = buildSolution205()
        map[206] = buildSolution206()
        map[207] = buildSolution207()
        map[208] = buildSolution208()
        map[209] = buildSolution209()
        map[210] = buildSolution210()
        map[211] = buildSolution211()
        map[212] = buildSolution212()
        map[213] = buildSolution213()
        map[214] = buildSolution214()
        map[215] = buildSolution215()
        map[216] = buildSolution216()
        map[217] = buildSolution217()
        map[218] = buildSolution218()
        map[219] = buildSolution219()
        map[220] = buildSolution220()
        map[221] = buildSolution221()
        map[222] = buildSolution222()
        map[223] = buildSolution223()
        map[224] = buildSolution224()
        map[225] = buildSolution225()
        map[226] = buildSolution226()
        map[227] = buildSolution227()
        map[228] = buildSolution228()
        map[229] = buildSolution229()
        map[230] = buildSolution230()
        map[231] = buildSolution231()
        map[232] = buildSolution232()
        map[233] = buildSolution233()
        map[234] = buildSolution234()
        map[235] = buildSolution235()
        map[236] = buildSolution236()
        map[237] = buildSolution237()
        map[238] = buildSolution238()
        map[239] = buildSolution239()
        map[240] = buildSolution240()
        map[241] = buildSolution241()
        map[242] = buildSolution242()
        map[243] = buildSolution243()
        map[244] = buildSolution244()
        map[245] = buildSolution245()
        map[246] = buildSolution246()
        map[247] = buildSolution247()
        map[248] = buildSolution248()
        map[249] = buildSolution249()
        map[250] = buildSolution250()
        map
    }
}
