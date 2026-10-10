package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6PartD {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 276: World 6 - Level 276 (12x12) ---

    fun buildLevel276(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl276",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 9)), NumberedCheckpoint(2, GridPosition(1, 10)), NumberedCheckpoint(3, GridPosition(3, 10)), NumberedCheckpoint(4, GridPosition(3, 7)),
            NumberedCheckpoint(5, GridPosition(2, 8)), NumberedCheckpoint(6, GridPosition(1, 7)), NumberedCheckpoint(7, GridPosition(0, 9)), NumberedCheckpoint(8, GridPosition(0, 7)),
            NumberedCheckpoint(9, GridPosition(5, 5)), NumberedCheckpoint(10, GridPosition(8, 5)), NumberedCheckpoint(11, GridPosition(5, 7)), NumberedCheckpoint(12, GridPosition(7, 9)),
            NumberedCheckpoint(13, GridPosition(6, 11)), NumberedCheckpoint(14, GridPosition(10, 11)), NumberedCheckpoint(15, GridPosition(9, 9)), NumberedCheckpoint(16, GridPosition(7, 6)),
            NumberedCheckpoint(17, GridPosition(9, 5)), NumberedCheckpoint(18, GridPosition(10, 2)), NumberedCheckpoint(19, GridPosition(10, 7)), NumberedCheckpoint(20, GridPosition(11, 3)),
            NumberedCheckpoint(21, GridPosition(9, 1)), NumberedCheckpoint(22, GridPosition(8, 1)), NumberedCheckpoint(23, GridPosition(5, 1)), NumberedCheckpoint(24, GridPosition(0, 3)),
            NumberedCheckpoint(25, GridPosition(2, 3)), NumberedCheckpoint(26, GridPosition(3, 4)), NumberedCheckpoint(27, GridPosition(4, 1)), NumberedCheckpoint(28, GridPosition(8, 2)),
            NumberedCheckpoint(29, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(4, 10), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(9, 9)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1))
        ),
        difficultyMetadata = "World 6 - Level 276 (seed=6276)",
        seed = 6276L
    )

    fun buildSolution276(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 9), GridPosition(5, 10), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(9, 9), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 8), GridPosition(10, 8), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3)
        )
    )

    // --- Level 277: World 6 - Level 277 (12x12) ---

    fun buildLevel277(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl277",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(2, 2)), NumberedCheckpoint(3, GridPosition(5, 0)), NumberedCheckpoint(4, GridPosition(6, 10)),
            NumberedCheckpoint(5, GridPosition(10, 9)), NumberedCheckpoint(6, GridPosition(8, 8)), NumberedCheckpoint(7, GridPosition(5, 10)), NumberedCheckpoint(8, GridPosition(1, 9)),
            NumberedCheckpoint(9, GridPosition(1, 11)), NumberedCheckpoint(10, GridPosition(1, 8)), NumberedCheckpoint(11, GridPosition(1, 6)), NumberedCheckpoint(12, GridPosition(0, 2)),
            NumberedCheckpoint(13, GridPosition(2, 3)), NumberedCheckpoint(14, GridPosition(5, 5)), NumberedCheckpoint(15, GridPosition(7, 4)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(6, 3)), NumberedCheckpoint(18, GridPosition(7, 1)), NumberedCheckpoint(19, GridPosition(7, 3)), NumberedCheckpoint(20, GridPosition(9, 2)),
            NumberedCheckpoint(21, GridPosition(10, 5)), NumberedCheckpoint(22, GridPosition(8, 5)), NumberedCheckpoint(23, GridPosition(5, 6)), NumberedCheckpoint(24, GridPosition(4, 7)),
            NumberedCheckpoint(25, GridPosition(6, 7)), NumberedCheckpoint(26, GridPosition(2, 8)), NumberedCheckpoint(27, GridPosition(2, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(10, 0)),
            BlockedEdge.between(GridPosition(9, 11), GridPosition(10, 11)),
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10))
        ),
        difficultyMetadata = "World 6 - Level 277 (seed=6278)",
        seed = 6278L
    )

    fun buildSolution277(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(2, 10), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5)
        )
    )

    // --- Level 278: World 6 - Level 278 (12x12) ---

    fun buildLevel278(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl278",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 9)), NumberedCheckpoint(2, GridPosition(3, 9)), NumberedCheckpoint(3, GridPosition(7, 10)), NumberedCheckpoint(4, GridPosition(10, 9)),
            NumberedCheckpoint(5, GridPosition(10, 10)), NumberedCheckpoint(6, GridPosition(2, 11)), NumberedCheckpoint(7, GridPosition(0, 6)), NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(2, 0)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(1, 4)), NumberedCheckpoint(12, GridPosition(2, 8)),
            NumberedCheckpoint(13, GridPosition(2, 6)), NumberedCheckpoint(14, GridPosition(5, 8)), NumberedCheckpoint(15, GridPosition(10, 8)), NumberedCheckpoint(16, GridPosition(9, 6)),
            NumberedCheckpoint(17, GridPosition(10, 5)), NumberedCheckpoint(18, GridPosition(9, 3)), NumberedCheckpoint(19, GridPosition(10, 4)), NumberedCheckpoint(20, GridPosition(8, 0)),
            NumberedCheckpoint(21, GridPosition(6, 2)), NumberedCheckpoint(22, GridPosition(7, 4)), NumberedCheckpoint(23, GridPosition(6, 5)), NumberedCheckpoint(24, GridPosition(7, 6)),
            NumberedCheckpoint(25, GridPosition(8, 5)), NumberedCheckpoint(26, GridPosition(4, 4)), NumberedCheckpoint(27, GridPosition(4, 5)), NumberedCheckpoint(28, GridPosition(3, 2)),
            NumberedCheckpoint(29, GridPosition(5, 3)), NumberedCheckpoint(30, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(7, 10)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(1, 11), GridPosition(2, 11)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0))
        ),
        difficultyMetadata = "World 6 - Level 278 (seed=6281)",
        seed = 6281L
    )

    fun buildSolution278(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 11), GridPosition(0, 11),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1)
        )
    )

    // --- Level 279: World 6 - Level 279 (12x12) ---

    fun buildLevel279(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl279",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 4)), NumberedCheckpoint(2, GridPosition(5, 5)), NumberedCheckpoint(3, GridPosition(5, 8)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(4, 8)), NumberedCheckpoint(6, GridPosition(1, 6)), NumberedCheckpoint(7, GridPosition(2, 5)), NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(8, 4)), NumberedCheckpoint(10, GridPosition(10, 1)), NumberedCheckpoint(11, GridPosition(11, 2)), NumberedCheckpoint(12, GridPosition(7, 1)),
            NumberedCheckpoint(13, GridPosition(6, 1)), NumberedCheckpoint(14, GridPosition(0, 2)), NumberedCheckpoint(15, GridPosition(4, 1)), NumberedCheckpoint(16, GridPosition(2, 3)),
            NumberedCheckpoint(17, GridPosition(2, 9)), NumberedCheckpoint(18, GridPosition(3, 10)), NumberedCheckpoint(19, GridPosition(1, 11)), NumberedCheckpoint(20, GridPosition(5, 11)),
            NumberedCheckpoint(21, GridPosition(10, 11)), NumberedCheckpoint(22, GridPosition(10, 8)), NumberedCheckpoint(23, GridPosition(6, 9)), NumberedCheckpoint(24, GridPosition(8, 7)),
            NumberedCheckpoint(25, GridPosition(9, 9)), NumberedCheckpoint(26, GridPosition(9, 7)), NumberedCheckpoint(27, GridPosition(11, 4)), NumberedCheckpoint(28, GridPosition(10, 5)),
            NumberedCheckpoint(29, GridPosition(7, 5)), NumberedCheckpoint(30, GridPosition(9, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(10, 10), GridPosition(11, 10)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9))
        ),
        difficultyMetadata = "World 6 - Level 279 (seed=6280)",
        seed = 6280L
    )

    fun buildSolution279(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2)
        )
    )

    // --- Level 280: World 6 - Level 280 (12x12) ---

    fun buildLevel280(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl280",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 9)), NumberedCheckpoint(2, GridPosition(10, 10)), NumberedCheckpoint(3, GridPosition(8, 9)), NumberedCheckpoint(4, GridPosition(10, 9)),
            NumberedCheckpoint(5, GridPosition(10, 8)), NumberedCheckpoint(6, GridPosition(5, 9)), NumberedCheckpoint(7, GridPosition(7, 7)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(11, 6)), NumberedCheckpoint(10, GridPosition(9, 5)), NumberedCheckpoint(11, GridPosition(9, 3)), NumberedCheckpoint(12, GridPosition(11, 3)),
            NumberedCheckpoint(13, GridPosition(9, 2)), NumberedCheckpoint(14, GridPosition(10, 1)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(5, 0)),
            NumberedCheckpoint(17, GridPosition(4, 1)), NumberedCheckpoint(18, GridPosition(5, 5)), NumberedCheckpoint(19, GridPosition(2, 1)), NumberedCheckpoint(20, GridPosition(0, 2)),
            NumberedCheckpoint(21, GridPosition(2, 2)), NumberedCheckpoint(22, GridPosition(3, 4)), NumberedCheckpoint(23, GridPosition(1, 7)), NumberedCheckpoint(24, GridPosition(1, 5)),
            NumberedCheckpoint(25, GridPosition(4, 9)), NumberedCheckpoint(26, GridPosition(6, 10)), NumberedCheckpoint(27, GridPosition(3, 8)), NumberedCheckpoint(28, GridPosition(2, 9)),
            NumberedCheckpoint(29, GridPosition(0, 8)), NumberedCheckpoint(30, GridPosition(2, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(10, 1), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(11, 8)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9))
        ),
        difficultyMetadata = "World 6 - Level 280 (seed=6288)",
        seed = 6288L
    )

    fun buildSolution280(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(10, 3),
            GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11)
        )
    )

    // --- Level 281: World 6 - Level 281 (12x12) ---

    fun buildLevel281(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl281",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)), NumberedCheckpoint(2, GridPosition(4, 3)), NumberedCheckpoint(3, GridPosition(3, 5)), NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(0, 2)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(1, 4)), NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(0, 6)), NumberedCheckpoint(10, GridPosition(1, 8)), NumberedCheckpoint(11, GridPosition(2, 6)), NumberedCheckpoint(12, GridPosition(2, 9)),
            NumberedCheckpoint(13, GridPosition(4, 6)), NumberedCheckpoint(14, GridPosition(0, 9)), NumberedCheckpoint(15, GridPosition(5, 9)), NumberedCheckpoint(16, GridPosition(6, 5)),
            NumberedCheckpoint(17, GridPosition(7, 4)), NumberedCheckpoint(18, GridPosition(6, 2)), NumberedCheckpoint(19, GridPosition(4, 1)), NumberedCheckpoint(20, GridPosition(9, 0)),
            NumberedCheckpoint(21, GridPosition(8, 2)), NumberedCheckpoint(22, GridPosition(11, 4)), NumberedCheckpoint(23, GridPosition(9, 5)), NumberedCheckpoint(24, GridPosition(7, 7)),
            NumberedCheckpoint(25, GridPosition(7, 8)), NumberedCheckpoint(26, GridPosition(6, 11)), NumberedCheckpoint(27, GridPosition(10, 9)), NumberedCheckpoint(28, GridPosition(11, 5)),
            NumberedCheckpoint(29, GridPosition(11, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(9, 10), GridPosition(9, 11)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1))
        ),
        difficultyMetadata = "World 6 - Level 281 (seed=6284)",
        seed = 6284L
    )

    fun buildSolution281(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8)
        )
    )

    // --- Level 282: World 6 - Level 282 (12x12) ---

    fun buildLevel282(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl282",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 10)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(4, 1)), NumberedCheckpoint(4, GridPosition(1, 2)),
            NumberedCheckpoint(5, GridPosition(5, 0)), NumberedCheckpoint(6, GridPosition(9, 1)), NumberedCheckpoint(7, GridPosition(10, 2)), NumberedCheckpoint(8, GridPosition(10, 0)),
            NumberedCheckpoint(9, GridPosition(11, 2)), NumberedCheckpoint(10, GridPosition(9, 5)), NumberedCheckpoint(11, GridPosition(9, 6)), NumberedCheckpoint(12, GridPosition(6, 7)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(3, 4)), NumberedCheckpoint(16, GridPosition(2, 3)),
            NumberedCheckpoint(17, GridPosition(1, 5)), NumberedCheckpoint(18, GridPosition(1, 9)), NumberedCheckpoint(19, GridPosition(2, 10)), NumberedCheckpoint(20, GridPosition(2, 7)),
            NumberedCheckpoint(21, GridPosition(5, 7)), NumberedCheckpoint(22, GridPosition(3, 7)), NumberedCheckpoint(23, GridPosition(4, 10)), NumberedCheckpoint(24, GridPosition(4, 9)),
            NumberedCheckpoint(25, GridPosition(7, 10)), NumberedCheckpoint(26, GridPosition(8, 8)), NumberedCheckpoint(27, GridPosition(11, 6)), NumberedCheckpoint(28, GridPosition(9, 10)),
            NumberedCheckpoint(29, GridPosition(11, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(11, 4), GridPosition(11, 5)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7))
        ),
        difficultyMetadata = "World 6 - Level 282 (seed=6284)",
        seed = 6284L
    )

    fun buildSolution282(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2),
            GridPosition(11, 3), GridPosition(11, 4), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8),
            GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10)
        )
    )

    // --- Level 283: World 6 - Level 283 (12x12) ---

    fun buildLevel283(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl283",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 11)), NumberedCheckpoint(2, GridPosition(9, 8)), NumberedCheckpoint(3, GridPosition(9, 11)), NumberedCheckpoint(4, GridPosition(9, 6)),
            NumberedCheckpoint(5, GridPosition(7, 3)), NumberedCheckpoint(6, GridPosition(10, 2)), NumberedCheckpoint(7, GridPosition(9, 4)), NumberedCheckpoint(8, GridPosition(11, 3)),
            NumberedCheckpoint(9, GridPosition(9, 1)), NumberedCheckpoint(10, GridPosition(8, 1)), NumberedCheckpoint(11, GridPosition(4, 1)), NumberedCheckpoint(12, GridPosition(6, 2)),
            NumberedCheckpoint(13, GridPosition(0, 1)), NumberedCheckpoint(14, GridPosition(2, 2)), NumberedCheckpoint(15, GridPosition(0, 2)), NumberedCheckpoint(16, GridPosition(6, 3)),
            NumberedCheckpoint(17, GridPosition(5, 7)), NumberedCheckpoint(18, GridPosition(5, 5)), NumberedCheckpoint(19, GridPosition(4, 5)), NumberedCheckpoint(20, GridPosition(6, 10)),
            NumberedCheckpoint(21, GridPosition(4, 9)), NumberedCheckpoint(22, GridPosition(2, 11)), NumberedCheckpoint(23, GridPosition(0, 8)), NumberedCheckpoint(24, GridPosition(3, 10)),
            NumberedCheckpoint(25, GridPosition(2, 7)), NumberedCheckpoint(26, GridPosition(1, 6)), NumberedCheckpoint(27, GridPosition(0, 7)), NumberedCheckpoint(28, GridPosition(0, 5)),
            NumberedCheckpoint(29, GridPosition(3, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(10, 7), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(11, 8), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(11, 5), GridPosition(11, 6)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(9, 10), GridPosition(10, 10)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3))
        ),
        difficultyMetadata = "World 6 - Level 283 (seed=6283)",
        seed = 6283L
    )

    fun buildSolution283(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4)
        )
    )

    // --- Level 284: World 6 - Level 284 (12x12) ---

    fun buildLevel284(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl284",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(3, 11)), NumberedCheckpoint(3, GridPosition(1, 10)), NumberedCheckpoint(4, GridPosition(0, 9)),
            NumberedCheckpoint(5, GridPosition(1, 5)), NumberedCheckpoint(6, GridPosition(1, 7)), NumberedCheckpoint(7, GridPosition(2, 5)), NumberedCheckpoint(8, GridPosition(5, 9)),
            NumberedCheckpoint(9, GridPosition(5, 11)), NumberedCheckpoint(10, GridPosition(6, 10)), NumberedCheckpoint(11, GridPosition(9, 9)), NumberedCheckpoint(12, GridPosition(11, 8)),
            NumberedCheckpoint(13, GridPosition(8, 6)), NumberedCheckpoint(14, GridPosition(7, 8)), NumberedCheckpoint(15, GridPosition(8, 5)), NumberedCheckpoint(16, GridPosition(6, 5)),
            NumberedCheckpoint(17, GridPosition(6, 7)), NumberedCheckpoint(18, GridPosition(4, 4)), NumberedCheckpoint(19, GridPosition(2, 4)), NumberedCheckpoint(20, GridPosition(1, 3)),
            NumberedCheckpoint(21, GridPosition(0, 3)), NumberedCheckpoint(22, GridPosition(4, 2)), NumberedCheckpoint(23, GridPosition(1, 1)), NumberedCheckpoint(24, GridPosition(3, 0)),
            NumberedCheckpoint(25, GridPosition(5, 2)), NumberedCheckpoint(26, GridPosition(7, 1)), NumberedCheckpoint(27, GridPosition(11, 5)), NumberedCheckpoint(28, GridPosition(10, 0)),
            NumberedCheckpoint(29, GridPosition(8, 1)), NumberedCheckpoint(30, GridPosition(10, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(10, 6), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(10, 1), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(8, 11), GridPosition(9, 11)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(10, 7), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6))
        ),
        difficultyMetadata = "World 6 - Level 284 (seed=6287)",
        seed = 6287L
    )

    fun buildSolution284(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(3, 11), GridPosition(2, 11),
            GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(10, 11),
            GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1)
        )
    )

    // --- Level 285: World 6 - Level 285 (12x12) ---

    fun buildLevel285(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl285",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 2)), NumberedCheckpoint(2, GridPosition(6, 0)), NumberedCheckpoint(3, GridPosition(8, 0)), NumberedCheckpoint(4, GridPosition(8, 1)),
            NumberedCheckpoint(5, GridPosition(11, 0)), NumberedCheckpoint(6, GridPosition(9, 3)), NumberedCheckpoint(7, GridPosition(9, 4)), NumberedCheckpoint(8, GridPosition(7, 4)),
            NumberedCheckpoint(9, GridPosition(8, 5)), NumberedCheckpoint(10, GridPosition(6, 6)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(5, 0)),
            NumberedCheckpoint(13, GridPosition(4, 4)), NumberedCheckpoint(14, GridPosition(1, 3)), NumberedCheckpoint(15, GridPosition(3, 0)), NumberedCheckpoint(16, GridPosition(0, 1)),
            NumberedCheckpoint(17, GridPosition(2, 1)), NumberedCheckpoint(18, GridPosition(0, 2)), NumberedCheckpoint(19, GridPosition(4, 5)), NumberedCheckpoint(20, GridPosition(3, 7)),
            NumberedCheckpoint(21, GridPosition(0, 6)), NumberedCheckpoint(22, GridPosition(4, 10)), NumberedCheckpoint(23, GridPosition(6, 11)), NumberedCheckpoint(24, GridPosition(11, 9)),
            NumberedCheckpoint(25, GridPosition(9, 6)), NumberedCheckpoint(26, GridPosition(10, 9)), NumberedCheckpoint(27, GridPosition(6, 9)), NumberedCheckpoint(28, GridPosition(2, 9)),
            NumberedCheckpoint(29, GridPosition(6, 8)), NumberedCheckpoint(30, GridPosition(7, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(5, 11), GridPosition(6, 11)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(7, 11)),
            BlockedEdge.between(GridPosition(11, 6), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(9, 7))
        ),
        difficultyMetadata = "World 6 - Level 285 (seed=6288)",
        seed = 6288L
    )

    fun buildSolution285(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8)
        )
    )

    // --- Level 286: World 6 - Level 286 (12x12) ---

    fun buildLevel286(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl286",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 1)), NumberedCheckpoint(2, GridPosition(10, 4)), NumberedCheckpoint(3, GridPosition(8, 1)), NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(8, 4)), NumberedCheckpoint(6, GridPosition(6, 5)), NumberedCheckpoint(7, GridPosition(8, 7)), NumberedCheckpoint(8, GridPosition(8, 10)),
            NumberedCheckpoint(9, GridPosition(9, 9)), NumberedCheckpoint(10, GridPosition(7, 6)), NumberedCheckpoint(11, GridPosition(10, 6)), NumberedCheckpoint(12, GridPosition(10, 10)),
            NumberedCheckpoint(13, GridPosition(10, 0)), NumberedCheckpoint(14, GridPosition(6, 1)), NumberedCheckpoint(15, GridPosition(0, 0)), NumberedCheckpoint(16, GridPosition(3, 2)),
            NumberedCheckpoint(17, GridPosition(5, 4)), NumberedCheckpoint(18, GridPosition(4, 6)), NumberedCheckpoint(19, GridPosition(2, 4)), NumberedCheckpoint(20, GridPosition(3, 7)),
            NumberedCheckpoint(21, GridPosition(1, 8)), NumberedCheckpoint(22, GridPosition(5, 8)), NumberedCheckpoint(23, GridPosition(7, 11)), NumberedCheckpoint(24, GridPosition(4, 9)),
            NumberedCheckpoint(25, GridPosition(1, 10)), NumberedCheckpoint(26, GridPosition(1, 11)), NumberedCheckpoint(27, GridPosition(3, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(5, 10), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(4, 11), GridPosition(5, 11)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(0, 9)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9))
        ),
        difficultyMetadata = "World 6 - Level 286 (seed=6286)",
        seed = 6286L
    )

    fun buildSolution286(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(8, 10), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(5, 10),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(3, 10)
        )
    )

    // --- Level 287: World 6 - Level 287 (12x12) ---

    fun buildLevel287(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl287",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 5)), NumberedCheckpoint(2, GridPosition(10, 8)), NumberedCheckpoint(3, GridPosition(8, 7)), NumberedCheckpoint(4, GridPosition(7, 8)),
            NumberedCheckpoint(5, GridPosition(10, 4)), NumberedCheckpoint(6, GridPosition(11, 0)), NumberedCheckpoint(7, GridPosition(10, 3)), NumberedCheckpoint(8, GridPosition(9, 5)),
            NumberedCheckpoint(9, GridPosition(7, 3)), NumberedCheckpoint(10, GridPosition(9, 2)), NumberedCheckpoint(11, GridPosition(6, 0)), NumberedCheckpoint(12, GridPosition(1, 1)),
            NumberedCheckpoint(13, GridPosition(2, 1)), NumberedCheckpoint(14, GridPosition(6, 1)), NumberedCheckpoint(15, GridPosition(5, 5)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(1, 2)), NumberedCheckpoint(18, GridPosition(0, 6)), NumberedCheckpoint(19, GridPosition(0, 11)), NumberedCheckpoint(20, GridPosition(1, 8)),
            NumberedCheckpoint(21, GridPosition(3, 11)), NumberedCheckpoint(22, GridPosition(7, 11)), NumberedCheckpoint(23, GridPosition(3, 10)), NumberedCheckpoint(24, GridPosition(1, 7)),
            NumberedCheckpoint(25, GridPosition(3, 4)), NumberedCheckpoint(26, GridPosition(2, 5)), NumberedCheckpoint(27, GridPosition(4, 8)), NumberedCheckpoint(28, GridPosition(6, 9)),
            NumberedCheckpoint(29, GridPosition(10, 9)), NumberedCheckpoint(30, GridPosition(11, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(7, 11), GridPosition(8, 11)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(5, 10))
        ),
        difficultyMetadata = "World 6 - Level 287 (seed=6287)",
        seed = 6287L
    )

    fun buildSolution287(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10), GridPosition(1, 9), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(7, 10), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8)
        )
    )

    // --- Level 288: World 6 - Level 288 (12x12) ---

    fun buildLevel288(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl288",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)), NumberedCheckpoint(2, GridPosition(2, 0)), NumberedCheckpoint(3, GridPosition(2, 1)), NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(1, 0)), NumberedCheckpoint(6, GridPosition(1, 4)), NumberedCheckpoint(7, GridPosition(3, 4)), NumberedCheckpoint(8, GridPosition(0, 6)),
            NumberedCheckpoint(9, GridPosition(5, 9)), NumberedCheckpoint(10, GridPosition(1, 10)), NumberedCheckpoint(11, GridPosition(2, 7)), NumberedCheckpoint(12, GridPosition(4, 8)),
            NumberedCheckpoint(13, GridPosition(7, 10)), NumberedCheckpoint(14, GridPosition(6, 11)), NumberedCheckpoint(15, GridPosition(10, 9)), NumberedCheckpoint(16, GridPosition(8, 9)),
            NumberedCheckpoint(17, GridPosition(5, 7)), NumberedCheckpoint(18, GridPosition(6, 4)), NumberedCheckpoint(19, GridPosition(6, 1)), NumberedCheckpoint(20, GridPosition(7, 1)),
            NumberedCheckpoint(21, GridPosition(8, 2)), NumberedCheckpoint(22, GridPosition(9, 2)), NumberedCheckpoint(23, GridPosition(11, 0)), NumberedCheckpoint(24, GridPosition(9, 3)),
            NumberedCheckpoint(25, GridPosition(9, 6)), NumberedCheckpoint(26, GridPosition(7, 4)), NumberedCheckpoint(27, GridPosition(9, 7)), NumberedCheckpoint(28, GridPosition(11, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 10), GridPosition(5, 10)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(6, 10)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4))
        ),
        difficultyMetadata = "World 6 - Level 288 (seed=6294)",
        seed = 6294L
    )

    fun buildSolution288(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 11),
            GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5),
            GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6), GridPosition(11, 5), GridPosition(11, 4)
        )
    )

    // --- Level 289: World 6 - Level 289 (12x12) ---

    fun buildLevel289(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl289",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 6)), NumberedCheckpoint(2, GridPosition(9, 3)), NumberedCheckpoint(3, GridPosition(10, 1)), NumberedCheckpoint(4, GridPosition(10, 5)),
            NumberedCheckpoint(5, GridPosition(7, 0)), NumberedCheckpoint(6, GridPosition(1, 2)), NumberedCheckpoint(7, GridPosition(2, 1)), NumberedCheckpoint(8, GridPosition(3, 1)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(2, 7)), NumberedCheckpoint(11, GridPosition(0, 8)), NumberedCheckpoint(12, GridPosition(3, 10)),
            NumberedCheckpoint(13, GridPosition(0, 9)), NumberedCheckpoint(14, GridPosition(9, 10)), NumberedCheckpoint(15, GridPosition(8, 9)), NumberedCheckpoint(16, GridPosition(10, 10)),
            NumberedCheckpoint(17, GridPosition(11, 9)), NumberedCheckpoint(18, GridPosition(8, 7)), NumberedCheckpoint(19, GridPosition(6, 6)), NumberedCheckpoint(20, GridPosition(7, 9)),
            NumberedCheckpoint(21, GridPosition(4, 9)), NumberedCheckpoint(22, GridPosition(3, 8)), NumberedCheckpoint(23, GridPosition(4, 6)), NumberedCheckpoint(24, GridPosition(4, 4)),
            NumberedCheckpoint(25, GridPosition(8, 5)), NumberedCheckpoint(26, GridPosition(6, 4)), NumberedCheckpoint(27, GridPosition(7, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(10, 5), GridPosition(11, 5)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(0, 6)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4))
        ),
        difficultyMetadata = "World 6 - Level 289 (seed=6290)",
        seed = 6290L
    )

    fun buildSolution289(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(9, 11), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10),
            GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2)
        )
    )

    // --- Level 290: World 6 - Level 290 (12x12) ---

    fun buildLevel290(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl290",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 11)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(11, 8)), NumberedCheckpoint(4, GridPosition(9, 7)),
            NumberedCheckpoint(5, GridPosition(10, 2)), NumberedCheckpoint(6, GridPosition(9, 1)), NumberedCheckpoint(7, GridPosition(1, 3)), NumberedCheckpoint(8, GridPosition(1, 6)),
            NumberedCheckpoint(9, GridPosition(0, 4)), NumberedCheckpoint(10, GridPosition(2, 7)), NumberedCheckpoint(11, GridPosition(2, 8)), NumberedCheckpoint(12, GridPosition(3, 6)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(5, 4)), NumberedCheckpoint(15, GridPosition(3, 4)), NumberedCheckpoint(16, GridPosition(5, 2)),
            NumberedCheckpoint(17, GridPosition(3, 2)), NumberedCheckpoint(18, GridPosition(7, 2)), NumberedCheckpoint(19, GridPosition(10, 4)), NumberedCheckpoint(20, GridPosition(8, 4)),
            NumberedCheckpoint(21, GridPosition(6, 7)), NumberedCheckpoint(22, GridPosition(7, 9)), NumberedCheckpoint(23, GridPosition(6, 9)), NumberedCheckpoint(24, GridPosition(0, 9)),
            NumberedCheckpoint(25, GridPosition(2, 10)), NumberedCheckpoint(26, GridPosition(3, 9)), NumberedCheckpoint(27, GridPosition(5, 9)), NumberedCheckpoint(28, GridPosition(4, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(8, 11), GridPosition(9, 11)),
            BlockedEdge.between(GridPosition(10, 7), GridPosition(11, 7)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(11, 2)),
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(1, 7)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6))
        ),
        difficultyMetadata = "World 6 - Level 290 (seed=6293)",
        seed = 6293L
    )

    fun buildSolution290(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 11), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9)
        )
    )

    // --- Level 291: World 6 - Level 291 (12x12) ---

    fun buildLevel291(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl291",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)), NumberedCheckpoint(2, GridPosition(5, 7)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(3, 3)),
            NumberedCheckpoint(5, GridPosition(3, 6)), NumberedCheckpoint(6, GridPosition(0, 7)), NumberedCheckpoint(7, GridPosition(1, 5)), NumberedCheckpoint(8, GridPosition(0, 1)),
            NumberedCheckpoint(9, GridPosition(2, 2)), NumberedCheckpoint(10, GridPosition(4, 1)), NumberedCheckpoint(11, GridPosition(3, 1)), NumberedCheckpoint(12, GridPosition(8, 6)),
            NumberedCheckpoint(13, GridPosition(8, 3)), NumberedCheckpoint(14, GridPosition(9, 0)), NumberedCheckpoint(15, GridPosition(10, 2)), NumberedCheckpoint(16, GridPosition(10, 4)),
            NumberedCheckpoint(17, GridPosition(9, 6)), NumberedCheckpoint(18, GridPosition(10, 7)), NumberedCheckpoint(19, GridPosition(9, 7)), NumberedCheckpoint(20, GridPosition(7, 9)),
            NumberedCheckpoint(21, GridPosition(6, 9)), NumberedCheckpoint(22, GridPosition(8, 10)), NumberedCheckpoint(23, GridPosition(11, 9)), NumberedCheckpoint(24, GridPosition(10, 10)),
            NumberedCheckpoint(25, GridPosition(9, 9)), NumberedCheckpoint(26, GridPosition(1, 10)), NumberedCheckpoint(27, GridPosition(1, 8)), NumberedCheckpoint(28, GridPosition(4, 7)),
            NumberedCheckpoint(29, GridPosition(5, 9)), NumberedCheckpoint(30, GridPosition(2, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4))
        ),
        difficultyMetadata = "World 6 - Level 291 (seed=6294)",
        seed = 6294L
    )

    fun buildSolution291(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(11, 2),
            GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11),
            GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 9)
        )
    )

    // --- Level 292: World 6 - Level 292 (12x12) ---

    fun buildLevel292(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl292",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 3)), NumberedCheckpoint(2, GridPosition(9, 1)), NumberedCheckpoint(3, GridPosition(11, 0)), NumberedCheckpoint(4, GridPosition(7, 0)),
            NumberedCheckpoint(5, GridPosition(8, 2)), NumberedCheckpoint(6, GridPosition(11, 11)), NumberedCheckpoint(7, GridPosition(8, 10)), NumberedCheckpoint(8, GridPosition(9, 9)),
            NumberedCheckpoint(9, GridPosition(6, 8)), NumberedCheckpoint(10, GridPosition(7, 11)), NumberedCheckpoint(11, GridPosition(6, 9)), NumberedCheckpoint(12, GridPosition(5, 6)),
            NumberedCheckpoint(13, GridPosition(4, 4)), NumberedCheckpoint(14, GridPosition(3, 6)), NumberedCheckpoint(15, GridPosition(3, 9)), NumberedCheckpoint(16, GridPosition(4, 9)),
            NumberedCheckpoint(17, GridPosition(3, 10)), NumberedCheckpoint(18, GridPosition(1, 4)), NumberedCheckpoint(19, GridPosition(2, 5)), NumberedCheckpoint(20, GridPosition(3, 1)),
            NumberedCheckpoint(21, GridPosition(0, 3)), NumberedCheckpoint(22, GridPosition(3, 0)), NumberedCheckpoint(23, GridPosition(4, 2)), NumberedCheckpoint(24, GridPosition(7, 2)),
            NumberedCheckpoint(25, GridPosition(6, 3)), NumberedCheckpoint(26, GridPosition(5, 3)), NumberedCheckpoint(27, GridPosition(7, 4)), NumberedCheckpoint(28, GridPosition(10, 6)),
            NumberedCheckpoint(29, GridPosition(10, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(7, 10), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(11, 9), GridPosition(11, 10)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(6, 10), GridPosition(6, 11)),
            BlockedEdge.between(GridPosition(4, 11), GridPosition(5, 11)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6))
        ),
        difficultyMetadata = "World 6 - Level 292 (seed=6292)",
        seed = 6292L
    )

    fun buildSolution292(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(11, 4),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(4, 10), GridPosition(4, 11), GridPosition(3, 11), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7)
        )
    )

    // --- Level 293: World 6 - Level 293 (12x12) ---

    fun buildLevel293(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl293",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 1)), NumberedCheckpoint(2, GridPosition(3, 3)), NumberedCheckpoint(3, GridPosition(0, 3)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(4, 3)), NumberedCheckpoint(6, GridPosition(10, 0)), NumberedCheckpoint(7, GridPosition(9, 2)), NumberedCheckpoint(8, GridPosition(10, 4)),
            NumberedCheckpoint(9, GridPosition(11, 2)), NumberedCheckpoint(10, GridPosition(11, 3)), NumberedCheckpoint(11, GridPosition(9, 8)), NumberedCheckpoint(12, GridPosition(10, 9)),
            NumberedCheckpoint(13, GridPosition(9, 9)), NumberedCheckpoint(14, GridPosition(10, 11)), NumberedCheckpoint(15, GridPosition(8, 8)), NumberedCheckpoint(16, GridPosition(8, 5)),
            NumberedCheckpoint(17, GridPosition(6, 4)), NumberedCheckpoint(18, GridPosition(5, 7)), NumberedCheckpoint(19, GridPosition(4, 4)), NumberedCheckpoint(20, GridPosition(3, 7)),
            NumberedCheckpoint(21, GridPosition(3, 6)), NumberedCheckpoint(22, GridPosition(1, 7)), NumberedCheckpoint(23, GridPosition(3, 4)), NumberedCheckpoint(24, GridPosition(2, 9)),
            NumberedCheckpoint(25, GridPosition(6, 8)), NumberedCheckpoint(26, GridPosition(3, 10)), NumberedCheckpoint(27, GridPosition(0, 9)), NumberedCheckpoint(28, GridPosition(5, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(11, 5), GridPosition(11, 6)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(6, 11), GridPosition(7, 11)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(9, 9))
        ),
        difficultyMetadata = "World 6 - Level 293 (seed=6295)",
        seed = 6295L
    )

    fun buildSolution293(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(10, 1),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(9, 10), GridPosition(10, 10), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(7, 11), GridPosition(7, 10), GridPosition(8, 10), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 10), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11),
            GridPosition(1, 11), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(5, 10)
        )
    )

    // --- Level 294: World 6 - Level 294 (12x12) ---

    fun buildLevel294(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl294",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(5, 1)), NumberedCheckpoint(3, GridPosition(3, 2)), NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(9, 1)), NumberedCheckpoint(6, GridPosition(8, 2)), NumberedCheckpoint(7, GridPosition(11, 0)), NumberedCheckpoint(8, GridPosition(5, 4)),
            NumberedCheckpoint(9, GridPosition(2, 5)), NumberedCheckpoint(10, GridPosition(0, 4)), NumberedCheckpoint(11, GridPosition(1, 8)), NumberedCheckpoint(12, GridPosition(2, 7)),
            NumberedCheckpoint(13, GridPosition(1, 11)), NumberedCheckpoint(14, GridPosition(4, 9)), NumberedCheckpoint(15, GridPosition(6, 9)), NumberedCheckpoint(16, GridPosition(9, 7)),
            NumberedCheckpoint(17, GridPosition(8, 11)), NumberedCheckpoint(18, GridPosition(11, 4)), NumberedCheckpoint(19, GridPosition(10, 5)), NumberedCheckpoint(20, GridPosition(9, 4)),
            NumberedCheckpoint(21, GridPosition(6, 6)), NumberedCheckpoint(22, GridPosition(5, 7)), NumberedCheckpoint(23, GridPosition(8, 7)), NumberedCheckpoint(24, GridPosition(4, 6)),
            NumberedCheckpoint(25, GridPosition(3, 7)), NumberedCheckpoint(26, GridPosition(1, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(5, 11), GridPosition(6, 11)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7))
        ),
        difficultyMetadata = "World 6 - Level 294 (seed=6302)",
        seed = 6302L
    )

    fun buildSolution294(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2), GridPosition(11, 3), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11),
            GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(8, 11),
            GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(11, 9), GridPosition(11, 8), GridPosition(11, 7), GridPosition(11, 6),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10)
        )
    )

    // --- Level 295: World 6 - Level 295 (12x12) ---

    fun buildLevel295(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl295",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(4, 2)), NumberedCheckpoint(3, GridPosition(4, 1)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(7, 2)), NumberedCheckpoint(6, GridPosition(7, 1)), NumberedCheckpoint(7, GridPosition(11, 0)), NumberedCheckpoint(8, GridPosition(9, 2)),
            NumberedCheckpoint(9, GridPosition(11, 2)), NumberedCheckpoint(10, GridPosition(9, 3)), NumberedCheckpoint(11, GridPosition(10, 6)), NumberedCheckpoint(12, GridPosition(11, 9)),
            NumberedCheckpoint(13, GridPosition(7, 8)), NumberedCheckpoint(14, GridPosition(9, 10)), NumberedCheckpoint(15, GridPosition(9, 7)), NumberedCheckpoint(16, GridPosition(4, 3)),
            NumberedCheckpoint(17, GridPosition(2, 6)), NumberedCheckpoint(18, GridPosition(2, 3)), NumberedCheckpoint(19, GridPosition(0, 0)), NumberedCheckpoint(20, GridPosition(2, 7)),
            NumberedCheckpoint(21, GridPosition(1, 9)), NumberedCheckpoint(22, GridPosition(3, 10)), NumberedCheckpoint(23, GridPosition(2, 9)), NumberedCheckpoint(24, GridPosition(3, 8)),
            NumberedCheckpoint(25, GridPosition(5, 9)), NumberedCheckpoint(26, GridPosition(4, 11)), NumberedCheckpoint(27, GridPosition(6, 7)), NumberedCheckpoint(28, GridPosition(6, 4)),
            NumberedCheckpoint(29, GridPosition(6, 6)), NumberedCheckpoint(30, GridPosition(6, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(4, 9), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(10, 7)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(11, 3)),
            BlockedEdge.between(GridPosition(11, 4), GridPosition(11, 5)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(10, 9), GridPosition(11, 9)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5))
        ),
        difficultyMetadata = "World 6 - Level 295 (seed=6297)",
        seed = 6297L
    )

    fun buildSolution295(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 3), GridPosition(11, 4),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(2, 11),
            GridPosition(3, 11), GridPosition(3, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 11),
            GridPosition(5, 11), GridPosition(6, 11), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5)
        )
    )

    // --- Level 296: World 6 - Level 296 (12x12) ---

    fun buildLevel296(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl296",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 5)), NumberedCheckpoint(2, GridPosition(11, 3)), NumberedCheckpoint(3, GridPosition(9, 0)), NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(3, 1)), NumberedCheckpoint(6, GridPosition(6, 3)), NumberedCheckpoint(7, GridPosition(5, 6)), NumberedCheckpoint(8, GridPosition(4, 6)),
            NumberedCheckpoint(9, GridPosition(4, 4)), NumberedCheckpoint(10, GridPosition(2, 0)), NumberedCheckpoint(11, GridPosition(0, 2)), NumberedCheckpoint(12, GridPosition(1, 4)),
            NumberedCheckpoint(13, GridPosition(0, 7)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(2, 6)), NumberedCheckpoint(16, GridPosition(1, 10)),
            NumberedCheckpoint(17, GridPosition(3, 9)), NumberedCheckpoint(18, GridPosition(5, 10)), NumberedCheckpoint(19, GridPosition(7, 8)), NumberedCheckpoint(20, GridPosition(9, 9)),
            NumberedCheckpoint(21, GridPosition(7, 9)), NumberedCheckpoint(22, GridPosition(10, 10)), NumberedCheckpoint(23, GridPosition(7, 5)), NumberedCheckpoint(24, GridPosition(7, 1)),
            NumberedCheckpoint(25, GridPosition(10, 3)), NumberedCheckpoint(26, GridPosition(8, 2)), NumberedCheckpoint(27, GridPosition(10, 5)), NumberedCheckpoint(28, GridPosition(11, 8)),
            NumberedCheckpoint(29, GridPosition(5, 11)), NumberedCheckpoint(30, GridPosition(0, 11))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(10, 2)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9))
        ),
        difficultyMetadata = "World 6 - Level 296 (seed=6296)",
        seed = 6296L
    )

    fun buildSolution296(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 5), GridPosition(9, 4), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11),
            GridPosition(7, 11), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11)
        )
    )

    // --- Level 297: World 6 - Level 297 (12x12) ---

    fun buildLevel297(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl297",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(11, 9)), NumberedCheckpoint(2, GridPosition(10, 10)), NumberedCheckpoint(3, GridPosition(10, 7)), NumberedCheckpoint(4, GridPosition(7, 6)),
            NumberedCheckpoint(5, GridPosition(6, 10)), NumberedCheckpoint(6, GridPosition(9, 8)), NumberedCheckpoint(7, GridPosition(8, 10)), NumberedCheckpoint(8, GridPosition(9, 10)),
            NumberedCheckpoint(9, GridPosition(4, 10)), NumberedCheckpoint(10, GridPosition(5, 6)), NumberedCheckpoint(11, GridPosition(8, 4)), NumberedCheckpoint(12, GridPosition(8, 6)),
            NumberedCheckpoint(13, GridPosition(10, 5)), NumberedCheckpoint(14, GridPosition(11, 2)), NumberedCheckpoint(15, GridPosition(7, 3)), NumberedCheckpoint(16, GridPosition(10, 1)),
            NumberedCheckpoint(17, GridPosition(7, 1)), NumberedCheckpoint(18, GridPosition(5, 4)), NumberedCheckpoint(19, GridPosition(5, 2)), NumberedCheckpoint(20, GridPosition(1, 1)),
            NumberedCheckpoint(21, GridPosition(1, 4)), NumberedCheckpoint(22, GridPosition(3, 1)), NumberedCheckpoint(23, GridPosition(4, 4)), NumberedCheckpoint(24, GridPosition(0, 5)),
            NumberedCheckpoint(25, GridPosition(4, 6)), NumberedCheckpoint(26, GridPosition(3, 8)), NumberedCheckpoint(27, GridPosition(4, 8)), NumberedCheckpoint(28, GridPosition(2, 9)),
            NumberedCheckpoint(29, GridPosition(1, 10))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(9, 8), GridPosition(10, 8)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(2, 11), GridPosition(3, 11)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(0, 8)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9))
        ),
        difficultyMetadata = "World 6 - Level 297 (seed=6297)",
        seed = 6297L
    )

    fun buildSolution297(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(11, 8),
            GridPosition(11, 7), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(7, 10), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(10, 6), GridPosition(11, 6), GridPosition(11, 5), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 3), GridPosition(11, 2), GridPosition(10, 2), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(11, 1), GridPosition(11, 0),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 10), GridPosition(2, 11),
            GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(1, 10)
        )
    )

    // --- Level 298: World 6 - Level 298 (12x12) ---

    fun buildLevel298(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl298",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 4)), NumberedCheckpoint(2, GridPosition(9, 0)), NumberedCheckpoint(3, GridPosition(11, 0)), NumberedCheckpoint(4, GridPosition(9, 3)),
            NumberedCheckpoint(5, GridPosition(11, 10)), NumberedCheckpoint(6, GridPosition(9, 9)), NumberedCheckpoint(7, GridPosition(9, 11)), NumberedCheckpoint(8, GridPosition(6, 9)),
            NumberedCheckpoint(9, GridPosition(10, 8)), NumberedCheckpoint(10, GridPosition(10, 6)), NumberedCheckpoint(11, GridPosition(4, 5)), NumberedCheckpoint(12, GridPosition(8, 6)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(6, 11)), NumberedCheckpoint(15, GridPosition(3, 9)), NumberedCheckpoint(16, GridPosition(2, 11)),
            NumberedCheckpoint(17, GridPosition(1, 10)), NumberedCheckpoint(18, GridPosition(1, 7)), NumberedCheckpoint(19, GridPosition(4, 7)), NumberedCheckpoint(20, GridPosition(3, 5)),
            NumberedCheckpoint(21, GridPosition(2, 2)), NumberedCheckpoint(22, GridPosition(1, 4)), NumberedCheckpoint(23, GridPosition(4, 0)), NumberedCheckpoint(24, GridPosition(6, 1)),
            NumberedCheckpoint(25, GridPosition(7, 2)), NumberedCheckpoint(26, GridPosition(4, 3)), NumberedCheckpoint(27, GridPosition(3, 1)), NumberedCheckpoint(28, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(3, 10), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(10, 0)),
            BlockedEdge.between(GridPosition(9, 9), GridPosition(9, 10)),
            BlockedEdge.between(GridPosition(3, 9), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5))
        ),
        difficultyMetadata = "World 6 - Level 298 (seed=6300)",
        seed = 6300L
    )

    fun buildSolution298(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(11, 0), GridPosition(11, 1), GridPosition(11, 2),
            GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(10, 3), GridPosition(11, 3), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6),
            GridPosition(11, 7), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10), GridPosition(11, 11), GridPosition(10, 11), GridPosition(10, 10), GridPosition(10, 9),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(6, 11), GridPosition(5, 11), GridPosition(4, 11), GridPosition(4, 10), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4)
        )
    )

    // --- Level 299: World 6 - Level 299 (12x12) ---

    fun buildLevel299(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl299",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(10, 1)), NumberedCheckpoint(3, GridPosition(5, 2)), NumberedCheckpoint(4, GridPosition(8, 2)),
            NumberedCheckpoint(5, GridPosition(9, 6)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(11, 3)), NumberedCheckpoint(8, GridPosition(10, 4)),
            NumberedCheckpoint(9, GridPosition(8, 7)), NumberedCheckpoint(10, GridPosition(5, 5)), NumberedCheckpoint(11, GridPosition(6, 7)), NumberedCheckpoint(12, GridPosition(7, 8)),
            NumberedCheckpoint(13, GridPosition(10, 10)), NumberedCheckpoint(14, GridPosition(9, 8)), NumberedCheckpoint(15, GridPosition(6, 10)), NumberedCheckpoint(16, GridPosition(1, 10)),
            NumberedCheckpoint(17, GridPosition(5, 9)), NumberedCheckpoint(18, GridPosition(2, 8)), NumberedCheckpoint(19, GridPosition(0, 9)), NumberedCheckpoint(20, GridPosition(1, 5)),
            NumberedCheckpoint(21, GridPosition(0, 6)), NumberedCheckpoint(22, GridPosition(1, 3)), NumberedCheckpoint(23, GridPosition(2, 4)), NumberedCheckpoint(24, GridPosition(2, 3)),
            NumberedCheckpoint(25, GridPosition(6, 3)), NumberedCheckpoint(26, GridPosition(4, 1)), NumberedCheckpoint(27, GridPosition(0, 2)), NumberedCheckpoint(28, GridPosition(1, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9))
        ),
        difficultyMetadata = "World 6 - Level 299 (seed=6302)",
        seed = 6302L
    )

    fun buildSolution299(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(11, 0),
            GridPosition(11, 1), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(10, 2), GridPosition(11, 2),
            GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(11, 4), GridPosition(11, 5), GridPosition(11, 6), GridPosition(11, 7), GridPosition(10, 7),
            GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(10, 8), GridPosition(11, 8), GridPosition(11, 9), GridPosition(11, 10),
            GridPosition(11, 11), GridPosition(10, 11), GridPosition(9, 11), GridPosition(8, 11), GridPosition(7, 11), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 11),
            GridPosition(5, 11), GridPosition(4, 11), GridPosition(3, 11), GridPosition(2, 11), GridPosition(1, 11), GridPosition(0, 11), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0)
        )
    )

    // --- Level 300: Grandmaster Finality (12x12) ---

    fun buildLevel300(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w6_lvl300",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(12, 12),
        requiredCells = grid12x12Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 6)), NumberedCheckpoint(2, GridPosition(5, 5)), NumberedCheckpoint(3, GridPosition(6, 5)), NumberedCheckpoint(4, GridPosition(7, 8)),
            NumberedCheckpoint(5, GridPosition(6, 10)), NumberedCheckpoint(6, GridPosition(9, 10)), NumberedCheckpoint(7, GridPosition(10, 8)), NumberedCheckpoint(8, GridPosition(10, 6)),
            NumberedCheckpoint(9, GridPosition(8, 9)), NumberedCheckpoint(10, GridPosition(9, 5)), NumberedCheckpoint(11, GridPosition(11, 3)), NumberedCheckpoint(12, GridPosition(7, 4)),
            NumberedCheckpoint(13, GridPosition(9, 2)), NumberedCheckpoint(14, GridPosition(10, 2)), NumberedCheckpoint(15, GridPosition(5, 0)), NumberedCheckpoint(16, GridPosition(1, 2)),
            NumberedCheckpoint(17, GridPosition(2, 4)), NumberedCheckpoint(18, GridPosition(2, 6)), NumberedCheckpoint(19, GridPosition(0, 6)), NumberedCheckpoint(20, GridPosition(2, 10)),
            NumberedCheckpoint(21, GridPosition(4, 11)), NumberedCheckpoint(22, GridPosition(5, 10)), NumberedCheckpoint(23, GridPosition(3, 10)), NumberedCheckpoint(24, GridPosition(4, 8)),
            NumberedCheckpoint(25, GridPosition(4, 5)), NumberedCheckpoint(26, GridPosition(2, 3)), NumberedCheckpoint(27, GridPosition(4, 2)), NumberedCheckpoint(28, GridPosition(7, 1)),
            NumberedCheckpoint(29, GridPosition(7, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(2, 10), GridPosition(3, 10)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(1, 8), GridPosition(2, 8))
        ),
        difficultyMetadata = "Grandmaster Finality (seed=300300)",
        seed = 300300L
    )

    fun buildSolution300(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 11), GridPosition(7, 11), GridPosition(8, 11), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(9, 11), GridPosition(10, 11), GridPosition(11, 11), GridPosition(11, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(11, 9), GridPosition(11, 8),
            GridPosition(10, 8), GridPosition(10, 7), GridPosition(11, 7), GridPosition(11, 6), GridPosition(10, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(10, 5),
            GridPosition(11, 5), GridPosition(11, 4), GridPosition(11, 3), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 2), GridPosition(11, 2), GridPosition(11, 1), GridPosition(11, 0), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(0, 11), GridPosition(1, 11), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(2, 11), GridPosition(3, 11), GridPosition(4, 11), GridPosition(5, 11), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(4, 7),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[276] = buildLevel276()
        map[277] = buildLevel277()
        map[278] = buildLevel278()
        map[279] = buildLevel279()
        map[280] = buildLevel280()
        map[281] = buildLevel281()
        map[282] = buildLevel282()
        map[283] = buildLevel283()
        map[284] = buildLevel284()
        map[285] = buildLevel285()
        map[286] = buildLevel286()
        map[287] = buildLevel287()
        map[288] = buildLevel288()
        map[289] = buildLevel289()
        map[290] = buildLevel290()
        map[291] = buildLevel291()
        map[292] = buildLevel292()
        map[293] = buildLevel293()
        map[294] = buildLevel294()
        map[295] = buildLevel295()
        map[296] = buildLevel296()
        map[297] = buildLevel297()
        map[298] = buildLevel298()
        map[299] = buildLevel299()
        map[300] = buildLevel300()
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[276] = buildSolution276()
        map[277] = buildSolution277()
        map[278] = buildSolution278()
        map[279] = buildSolution279()
        map[280] = buildSolution280()
        map[281] = buildSolution281()
        map[282] = buildSolution282()
        map[283] = buildSolution283()
        map[284] = buildSolution284()
        map[285] = buildSolution285()
        map[286] = buildSolution286()
        map[287] = buildSolution287()
        map[288] = buildSolution288()
        map[289] = buildSolution289()
        map[290] = buildSolution290()
        map[291] = buildSolution291()
        map[292] = buildSolution292()
        map[293] = buildSolution293()
        map[294] = buildSolution294()
        map[295] = buildSolution295()
        map[296] = buildSolution296()
        map[297] = buildSolution297()
        map[298] = buildSolution298()
        map[299] = buildSolution299()
        map[300] = buildSolution300()
        map
    }
}
