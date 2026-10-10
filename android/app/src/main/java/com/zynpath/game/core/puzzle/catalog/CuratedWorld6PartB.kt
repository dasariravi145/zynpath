package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6PartB {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 226: World 6 - Level 226 (11x11) ---

    fun buildLevel226() = PuzzleDefinition(
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

    fun buildSolution226() = PuzzlePath(
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

    fun buildLevel227() = PuzzleDefinition(
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

    fun buildSolution227() = PuzzlePath(
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

    fun buildLevel228() = PuzzleDefinition(
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

    fun buildSolution228() = PuzzlePath(
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

    fun buildLevel229() = PuzzleDefinition(
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

    fun buildSolution229() = PuzzlePath(
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

    fun buildLevel230() = PuzzleDefinition(
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

    fun buildSolution230() = PuzzlePath(
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

    fun buildLevel231() = PuzzleDefinition(
        puzzleId = "w6_lvl231",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(1, 10)), NumberedCheckpoint(3, GridPosition(2, 8)), NumberedCheckpoint(4, GridPosition(5, 10)),
            NumberedCheckpoint(5, GridPosition(3, 7)), NumberedCheckpoint(6, GridPosition(4, 5)), NumberedCheckpoint(7, GridPosition(2, 1)), NumberedCheckpoint(8, GridPosition(3, 5)),
            NumberedCheckpoint(9, GridPosition(1, 4)), NumberedCheckpoint(10, GridPosition(1, 7)), NumberedCheckpoint(11, GridPosition(6, 0)), NumberedCheckpoint(12, GridPosition(5, 4)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(6, 7)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(9, 0)),
            NumberedCheckpoint(17, GridPosition(8, 2)), NumberedCheckpoint(18, GridPosition(9, 4)), NumberedCheckpoint(19, GridPosition(9, 3)), NumberedCheckpoint(20, GridPosition(9, 7)),
            NumberedCheckpoint(21, GridPosition(7, 10)), NumberedCheckpoint(22, GridPosition(9, 8)), NumberedCheckpoint(23, GridPosition(8, 6))
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

    fun buildSolution231() = PuzzlePath(
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

    fun buildLevel232() = PuzzleDefinition(
        puzzleId = "w6_lvl232",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 3)), NumberedCheckpoint(2, GridPosition(8, 3)), NumberedCheckpoint(3, GridPosition(9, 0)), NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(9, 6)), NumberedCheckpoint(7, GridPosition(9, 9)), NumberedCheckpoint(8, GridPosition(8, 9)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(5, 5)), NumberedCheckpoint(11, GridPosition(5, 3)), NumberedCheckpoint(12, GridPosition(4, 5)),
            NumberedCheckpoint(13, GridPosition(4, 6)), NumberedCheckpoint(14, GridPosition(3, 3)), NumberedCheckpoint(15, GridPosition(5, 2)), NumberedCheckpoint(16, GridPosition(2, 1)),
            NumberedCheckpoint(17, GridPosition(1, 4)), NumberedCheckpoint(18, GridPosition(0, 6)), NumberedCheckpoint(19, GridPosition(4, 9)), NumberedCheckpoint(20, GridPosition(7, 9)),
            NumberedCheckpoint(21, GridPosition(4, 7)), NumberedCheckpoint(22, GridPosition(1, 5)), NumberedCheckpoint(23, GridPosition(4, 8))
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

    fun buildSolution232() = PuzzlePath(
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

    fun buildLevel233() = PuzzleDefinition(
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
            NumberedCheckpoint(21, GridPosition(4, 4)), NumberedCheckpoint(22, GridPosition(4, 5)), NumberedCheckpoint(23, GridPosition(3, 3)), NumberedCheckpoint(24, GridPosition(5, 3))
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

    fun buildSolution233() = PuzzlePath(
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

    fun buildLevel234() = PuzzleDefinition(
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

    fun buildSolution234() = PuzzlePath(
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

    fun buildLevel235() = PuzzleDefinition(
        puzzleId = "w6_lvl235",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(1, 1)), NumberedCheckpoint(3, GridPosition(1, 2)), NumberedCheckpoint(4, GridPosition(2, 0)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(5, 3)), NumberedCheckpoint(7, GridPosition(7, 3)), NumberedCheckpoint(8, GridPosition(9, 4)),
            NumberedCheckpoint(9, GridPosition(9, 1)), NumberedCheckpoint(10, GridPosition(10, 1)), NumberedCheckpoint(11, GridPosition(6, 5)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(5, 7)), NumberedCheckpoint(15, GridPosition(8, 7)), NumberedCheckpoint(16, GridPosition(9, 6)),
            NumberedCheckpoint(17, GridPosition(2, 8)), NumberedCheckpoint(18, GridPosition(1, 4)), NumberedCheckpoint(19, GridPosition(2, 3)), NumberedCheckpoint(20, GridPosition(1, 9)),
            NumberedCheckpoint(21, GridPosition(0, 10)), NumberedCheckpoint(22, GridPosition(6, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8))
        ),
        difficultyMetadata = "World 6 - Level 235 (seed=6265)",
        seed = 6265L
    )

    fun buildSolution235() = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10)
        )
    )

    // --- Level 236: World 6 - Level 236 (11x11) ---

    fun buildLevel236() = PuzzleDefinition(
        puzzleId = "w6_lvl236",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(4, 3)), NumberedCheckpoint(4, GridPosition(5, 1)),
            NumberedCheckpoint(5, GridPosition(10, 0)), NumberedCheckpoint(6, GridPosition(7, 2)), NumberedCheckpoint(7, GridPosition(9, 3)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(8, 8)), NumberedCheckpoint(10, GridPosition(3, 10)), NumberedCheckpoint(11, GridPosition(7, 9)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(7, 6)), NumberedCheckpoint(14, GridPosition(6, 4)), NumberedCheckpoint(15, GridPosition(3, 2)), NumberedCheckpoint(16, GridPosition(0, 4)),
            NumberedCheckpoint(17, GridPosition(0, 9)), NumberedCheckpoint(18, GridPosition(1, 8)), NumberedCheckpoint(19, GridPosition(5, 7)), NumberedCheckpoint(20, GridPosition(3, 6)),
            NumberedCheckpoint(21, GridPosition(1, 5)), NumberedCheckpoint(22, GridPosition(6, 6))
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

    fun buildSolution236() = PuzzlePath(
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

    fun buildLevel237() = PuzzleDefinition(
        puzzleId = "w6_lvl237",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)), NumberedCheckpoint(2, GridPosition(3, 3)), NumberedCheckpoint(3, GridPosition(3, 4)), NumberedCheckpoint(4, GridPosition(7, 5)),
            NumberedCheckpoint(5, GridPosition(6, 7)), NumberedCheckpoint(6, GridPosition(3, 7)), NumberedCheckpoint(7, GridPosition(5, 8)), NumberedCheckpoint(8, GridPosition(3, 9)),
            NumberedCheckpoint(9, GridPosition(5, 10)), NumberedCheckpoint(10, GridPosition(2, 7)), NumberedCheckpoint(11, GridPosition(0, 0)), NumberedCheckpoint(12, GridPosition(4, 1)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(5, 2)), NumberedCheckpoint(16, GridPosition(7, 1)),
            NumberedCheckpoint(17, GridPosition(6, 3)), NumberedCheckpoint(18, GridPosition(10, 1)), NumberedCheckpoint(19, GridPosition(9, 2)), NumberedCheckpoint(20, GridPosition(9, 4)),
            NumberedCheckpoint(21, GridPosition(9, 5)), NumberedCheckpoint(22, GridPosition(7, 9)), NumberedCheckpoint(23, GridPosition(9, 8)), NumberedCheckpoint(24, GridPosition(8, 8))
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

    fun buildSolution237() = PuzzlePath(
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

    fun buildLevel238() = PuzzleDefinition(
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

    fun buildSolution238() = PuzzlePath(
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

    fun buildLevel239() = PuzzleDefinition(
        puzzleId = "w6_lvl239",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 5)), NumberedCheckpoint(2, GridPosition(4, 6)), NumberedCheckpoint(3, GridPosition(3, 1)), NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(6, 2)), NumberedCheckpoint(6, GridPosition(6, 5)), NumberedCheckpoint(7, GridPosition(7, 3)), NumberedCheckpoint(8, GridPosition(4, 0)),
            NumberedCheckpoint(9, GridPosition(1, 0)), NumberedCheckpoint(10, GridPosition(1, 3)), NumberedCheckpoint(11, GridPosition(1, 4)), NumberedCheckpoint(12, GridPosition(3, 8)),
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

    fun buildSolution239() = PuzzlePath(
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

    fun buildLevel240() = PuzzleDefinition(
        puzzleId = "w6_lvl240",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(2, 3)), NumberedCheckpoint(3, GridPosition(0, 0)), NumberedCheckpoint(4, GridPosition(5, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)), NumberedCheckpoint(6, GridPosition(3, 2)), NumberedCheckpoint(7, GridPosition(3, 3)), NumberedCheckpoint(8, GridPosition(6, 5)),
            NumberedCheckpoint(9, GridPosition(9, 5)), NumberedCheckpoint(10, GridPosition(7, 4)), NumberedCheckpoint(11, GridPosition(8, 1)), NumberedCheckpoint(12, GridPosition(10, 4)),
            NumberedCheckpoint(13, GridPosition(9, 6)), NumberedCheckpoint(14, GridPosition(10, 8)), NumberedCheckpoint(15, GridPosition(8, 10)), NumberedCheckpoint(16, GridPosition(3, 10)),
            NumberedCheckpoint(17, GridPosition(2, 5)), NumberedCheckpoint(18, GridPosition(3, 7)), NumberedCheckpoint(19, GridPosition(1, 8)), NumberedCheckpoint(20, GridPosition(5, 9)),
            NumberedCheckpoint(21, GridPosition(7, 9)), NumberedCheckpoint(22, GridPosition(6, 7)), NumberedCheckpoint(23, GridPosition(5, 5)), NumberedCheckpoint(24, GridPosition(2, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(10, 6))
        ),
        difficultyMetadata = "World 6 - Level 240 (seed=6256)",
        seed = 6256L
    )

    fun buildSolution240() = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8)
        )
    )

    // --- Level 241: World 6 - Level 241 (12x12) ---

    fun buildLevel241() = PuzzleDefinition(
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

    fun buildSolution241() = PuzzlePath(
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

    fun buildLevel242() = PuzzleDefinition(
        puzzleId = "w6_lvl242",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 2)), NumberedCheckpoint(2, GridPosition(10, 1)), NumberedCheckpoint(3, GridPosition(6, 3)), NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(5, 1)), NumberedCheckpoint(6, GridPosition(3, 2)), NumberedCheckpoint(7, GridPosition(1, 3)), NumberedCheckpoint(8, GridPosition(0, 0)),
            NumberedCheckpoint(9, GridPosition(1, 4)), NumberedCheckpoint(10, GridPosition(3, 4)), NumberedCheckpoint(11, GridPosition(5, 5)), NumberedCheckpoint(12, GridPosition(8, 2)),
            NumberedCheckpoint(13, GridPosition(10, 3)), NumberedCheckpoint(14, GridPosition(11, 4)), NumberedCheckpoint(15, GridPosition(9, 4)), NumberedCheckpoint(16, GridPosition(10, 11)),
            NumberedCheckpoint(17, GridPosition(10, 8)), NumberedCheckpoint(18, GridPosition(8, 5)), NumberedCheckpoint(19, GridPosition(7, 6)), NumberedCheckpoint(20, GridPosition(4, 6)),
            NumberedCheckpoint(21, GridPosition(0, 9)), NumberedCheckpoint(22, GridPosition(3, 11)), NumberedCheckpoint(23, GridPosition(9, 10)), NumberedCheckpoint(24, GridPosition(7, 8)),
            NumberedCheckpoint(25, GridPosition(4, 8)), NumberedCheckpoint(26, GridPosition(1, 6)), NumberedCheckpoint(27, GridPosition(2, 9)), NumberedCheckpoint(28, GridPosition(4, 10)),
            NumberedCheckpoint(29, GridPosition(3, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(5, 11)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(11, 4), GridPosition(11, 5)),
            BlockedEdge.between(GridPosition(11, 1), GridPosition(11, 2))
        ),
        difficultyMetadata = "World 6 - Level 242 (seed=6244)",
        seed = 6244L
    )

    fun buildSolution242() = PuzzlePath(
        listOf(
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8)
        )
    )

    // --- Level 243: World 6 - Level 243 (12x12) ---

    fun buildLevel243() = PuzzleDefinition(
        puzzleId = "w6_lvl243",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 5)), NumberedCheckpoint(2, GridPosition(9, 3)), NumberedCheckpoint(3, GridPosition(10, 2)), NumberedCheckpoint(4, GridPosition(9, 2)),
            NumberedCheckpoint(5, GridPosition(7, 2)), NumberedCheckpoint(6, GridPosition(11, 1)), NumberedCheckpoint(7, GridPosition(7, 0)), NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(4, 6)), NumberedCheckpoint(11, GridPosition(0, 4)), NumberedCheckpoint(12, GridPosition(0, 10)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(2, 9)), NumberedCheckpoint(15, GridPosition(3, 10)), NumberedCheckpoint(16, GridPosition(2, 10)),
            NumberedCheckpoint(17, GridPosition(9, 10)), NumberedCheckpoint(18, GridPosition(6, 9)), NumberedCheckpoint(19, GridPosition(7, 9)), NumberedCheckpoint(20, GridPosition(5, 10)),
            NumberedCheckpoint(21, GridPosition(5, 7)), NumberedCheckpoint(22, GridPosition(5, 1)), NumberedCheckpoint(23, GridPosition(8, 5)), NumberedCheckpoint(24, GridPosition(6, 5)),
            NumberedCheckpoint(25, GridPosition(8, 6)), NumberedCheckpoint(26, GridPosition(11, 10)), NumberedCheckpoint(27, GridPosition(10, 8)), NumberedCheckpoint(28, GridPosition(8, 9))
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

    fun buildSolution243() = PuzzlePath(
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

    fun buildLevel244() = PuzzleDefinition(
        puzzleId = "w6_lvl244",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 3)), NumberedCheckpoint(2, GridPosition(4, 2)), NumberedCheckpoint(3, GridPosition(1, 0)), NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(5, 1)), NumberedCheckpoint(6, GridPosition(10, 1)), NumberedCheckpoint(7, GridPosition(9, 3)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(9, 2)), NumberedCheckpoint(10, GridPosition(8, 1)), NumberedCheckpoint(11, GridPosition(6, 2)), NumberedCheckpoint(12, GridPosition(7, 3)),
            NumberedCheckpoint(13, GridPosition(8, 5)), NumberedCheckpoint(14, GridPosition(9, 8)), NumberedCheckpoint(15, GridPosition(11, 7)), NumberedCheckpoint(16, GridPosition(0, 10)),
            NumberedCheckpoint(17, GridPosition(3, 10)), NumberedCheckpoint(18, GridPosition(6, 9)), NumberedCheckpoint(19, GridPosition(7, 8)), NumberedCheckpoint(20, GridPosition(8, 10)),
            NumberedCheckpoint(21, GridPosition(9, 9)), NumberedCheckpoint(22, GridPosition(6, 7)), NumberedCheckpoint(23, GridPosition(5, 5)), NumberedCheckpoint(24, GridPosition(1, 5)),
            NumberedCheckpoint(25, GridPosition(5, 6)), NumberedCheckpoint(26, GridPosition(4, 9)), NumberedCheckpoint(27, GridPosition(1, 7)), NumberedCheckpoint(28, GridPosition(2, 9)),
            NumberedCheckpoint(29, GridPosition(2, 4)), NumberedCheckpoint(30, GridPosition(3, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(11, 5), GridPosition(11, 6))
        ),
        difficultyMetadata = "World 6 - Level 244 (seed=6249)",
        seed = 6249L
    )

    fun buildSolution244() = PuzzlePath(
        listOf(
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4)
        )
    )

    // --- Level 245: World 6 - Level 245 (12x12) ---

    fun buildLevel245() = PuzzleDefinition(
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

    fun buildSolution245() = PuzzlePath(
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

    fun buildLevel246() = PuzzleDefinition(
        puzzleId = "w6_lvl246",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 11)), NumberedCheckpoint(2, GridPosition(11, 11)), NumberedCheckpoint(3, GridPosition(9, 8)), NumberedCheckpoint(4, GridPosition(7, 10)),
            NumberedCheckpoint(5, GridPosition(6, 10)), NumberedCheckpoint(6, GridPosition(9, 7)), NumberedCheckpoint(7, GridPosition(11, 9)), NumberedCheckpoint(8, GridPosition(11, 4)),
            NumberedCheckpoint(9, GridPosition(11, 1)), NumberedCheckpoint(10, GridPosition(2, 5)), NumberedCheckpoint(11, GridPosition(1, 11)), NumberedCheckpoint(12, GridPosition(3, 10)),
            NumberedCheckpoint(13, GridPosition(1, 9)), NumberedCheckpoint(14, GridPosition(2, 7)), NumberedCheckpoint(15, GridPosition(4, 6)), NumberedCheckpoint(16, GridPosition(3, 5)),
            NumberedCheckpoint(17, GridPosition(6, 7)), NumberedCheckpoint(18, GridPosition(4, 4)), NumberedCheckpoint(19, GridPosition(6, 2)), NumberedCheckpoint(20, GridPosition(8, 5)),
            NumberedCheckpoint(21, GridPosition(10, 5)), NumberedCheckpoint(22, GridPosition(8, 3)), NumberedCheckpoint(23, GridPosition(9, 3)), NumberedCheckpoint(24, GridPosition(6, 1)),
            NumberedCheckpoint(25, GridPosition(3, 2)), NumberedCheckpoint(26, GridPosition(2, 3)), NumberedCheckpoint(27, GridPosition(2, 4)), NumberedCheckpoint(28, GridPosition(2, 1))
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

    fun buildSolution246() = PuzzlePath(
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

    fun buildLevel247() = PuzzleDefinition(
        puzzleId = "w6_lvl247",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(2, 7)), NumberedCheckpoint(3, GridPosition(7, 10)), NumberedCheckpoint(4, GridPosition(4, 9)),
            NumberedCheckpoint(5, GridPosition(1, 9)), NumberedCheckpoint(6, GridPosition(0, 8)), NumberedCheckpoint(7, GridPosition(1, 10)), NumberedCheckpoint(8, GridPosition(10, 9)),
            NumberedCheckpoint(9, GridPosition(8, 9)), NumberedCheckpoint(10, GridPosition(7, 7)), NumberedCheckpoint(11, GridPosition(4, 5)), NumberedCheckpoint(12, GridPosition(5, 4)),
            NumberedCheckpoint(13, GridPosition(1, 4)), NumberedCheckpoint(14, GridPosition(3, 3)), NumberedCheckpoint(15, GridPosition(4, 2)), NumberedCheckpoint(16, GridPosition(5, 3)),
            NumberedCheckpoint(17, GridPosition(5, 2)), NumberedCheckpoint(18, GridPosition(6, 5)), NumberedCheckpoint(19, GridPosition(7, 6)), NumberedCheckpoint(20, GridPosition(8, 6)),
            NumberedCheckpoint(21, GridPosition(10, 8)), NumberedCheckpoint(22, GridPosition(9, 6)), NumberedCheckpoint(23, GridPosition(11, 5)), NumberedCheckpoint(24, GridPosition(7, 4)),
            NumberedCheckpoint(25, GridPosition(7, 2)), NumberedCheckpoint(26, GridPosition(8, 1)), NumberedCheckpoint(27, GridPosition(1, 1)), NumberedCheckpoint(28, GridPosition(7, 0)),
            NumberedCheckpoint(29, GridPosition(9, 1)), NumberedCheckpoint(30, GridPosition(11, 0))
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

    fun buildSolution247() = PuzzlePath(
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

    fun buildLevel248() = PuzzleDefinition(
        puzzleId = "w6_lvl248",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 3)), NumberedCheckpoint(2, GridPosition(3, 5)), NumberedCheckpoint(3, GridPosition(2, 6)), NumberedCheckpoint(4, GridPosition(1, 6)),
            NumberedCheckpoint(5, GridPosition(1, 10)), NumberedCheckpoint(6, GridPosition(2, 9)), NumberedCheckpoint(7, GridPosition(3, 7)), NumberedCheckpoint(8, GridPosition(4, 3)),
            NumberedCheckpoint(9, GridPosition(1, 2)), NumberedCheckpoint(10, GridPosition(1, 0)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(0, 6)),
            NumberedCheckpoint(13, GridPosition(5, 10)), NumberedCheckpoint(14, GridPosition(4, 9)), NumberedCheckpoint(15, GridPosition(6, 7)), NumberedCheckpoint(16, GridPosition(7, 9)),
            NumberedCheckpoint(17, GridPosition(8, 8)), NumberedCheckpoint(18, GridPosition(6, 11)), NumberedCheckpoint(19, GridPosition(9, 10)), NumberedCheckpoint(20, GridPosition(8, 7)),
            NumberedCheckpoint(21, GridPosition(11, 7)), NumberedCheckpoint(22, GridPosition(5, 6)), NumberedCheckpoint(23, GridPosition(2, 1)), NumberedCheckpoint(24, GridPosition(7, 0)),
            NumberedCheckpoint(25, GridPosition(8, 2)), NumberedCheckpoint(26, GridPosition(9, 4)), NumberedCheckpoint(27, GridPosition(9, 0)), NumberedCheckpoint(28, GridPosition(10, 2)),
            NumberedCheckpoint(29, GridPosition(10, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(10, 5), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(11, 4), GridPosition(11, 5)),
            BlockedEdge.between(GridPosition(11, 8), GridPosition(11, 9))
        ),
        difficultyMetadata = "World 6 - Level 248 (seed=6249)",
        seed = 6249L
    )

    fun buildSolution248() = PuzzlePath(
        listOf(
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(10, 4), GridPosition(10, 3)
        )
    )

    // --- Level 249: World 6 - Level 249 (12x12) ---

    fun buildLevel249() = PuzzleDefinition(
        puzzleId = "w6_lvl249",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 11)), NumberedCheckpoint(2, GridPosition(9, 8)), NumberedCheckpoint(3, GridPosition(9, 6)), NumberedCheckpoint(4, GridPosition(9, 4)),
            NumberedCheckpoint(5, GridPosition(11, 5)), NumberedCheckpoint(6, GridPosition(10, 3)), NumberedCheckpoint(7, GridPosition(10, 1)), NumberedCheckpoint(8, GridPosition(6, 2)),
            NumberedCheckpoint(9, GridPosition(8, 1)), NumberedCheckpoint(10, GridPosition(5, 1)), NumberedCheckpoint(11, GridPosition(3, 2)), NumberedCheckpoint(12, GridPosition(2, 1)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(0, 5)), NumberedCheckpoint(16, GridPosition(5, 4)),
            NumberedCheckpoint(17, GridPosition(7, 5)), NumberedCheckpoint(18, GridPosition(5, 5)), NumberedCheckpoint(19, GridPosition(8, 6)), NumberedCheckpoint(20, GridPosition(6, 7)),
            NumberedCheckpoint(21, GridPosition(4, 8)), NumberedCheckpoint(22, GridPosition(1, 10)), NumberedCheckpoint(23, GridPosition(3, 10)), NumberedCheckpoint(24, GridPosition(6, 11)),
            NumberedCheckpoint(25, GridPosition(7, 10)), NumberedCheckpoint(26, GridPosition(10, 10)), NumberedCheckpoint(27, GridPosition(8, 9)), NumberedCheckpoint(28, GridPosition(10, 9))
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

    fun buildSolution249() = PuzzlePath(
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

    fun buildLevel250() = PuzzleDefinition(
        puzzleId = "w6_lvl250",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(1, 0)), NumberedCheckpoint(3, GridPosition(2, 3)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(2, 6)), NumberedCheckpoint(6, GridPosition(5, 10)), NumberedCheckpoint(7, GridPosition(6, 9)), NumberedCheckpoint(8, GridPosition(8, 10)),
            NumberedCheckpoint(9, GridPosition(6, 8)), NumberedCheckpoint(10, GridPosition(3, 6)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(2, 0)),
            NumberedCheckpoint(13, GridPosition(6, 3)), NumberedCheckpoint(14, GridPosition(4, 5)), NumberedCheckpoint(15, GridPosition(7, 7)), NumberedCheckpoint(16, GridPosition(7, 4)),
            NumberedCheckpoint(17, GridPosition(5, 2)), NumberedCheckpoint(18, GridPosition(9, 4)), NumberedCheckpoint(19, GridPosition(10, 2)), NumberedCheckpoint(20, GridPosition(11, 1)),
            NumberedCheckpoint(21, GridPosition(11, 6)), NumberedCheckpoint(22, GridPosition(9, 7)), NumberedCheckpoint(23, GridPosition(10, 8)), NumberedCheckpoint(24, GridPosition(10, 9)),
            NumberedCheckpoint(25, GridPosition(5, 11)), NumberedCheckpoint(26, GridPosition(2, 9)), NumberedCheckpoint(27, GridPosition(2, 11)), NumberedCheckpoint(28, GridPosition(0, 8)),
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

    fun buildSolution250() = PuzzlePath(
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
