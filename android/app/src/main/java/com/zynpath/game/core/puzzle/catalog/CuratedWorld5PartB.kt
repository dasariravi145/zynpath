package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld5PartB {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 176: World 5 - Level 176 (10x10) ---

    fun buildLevel176(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl176",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)), NumberedCheckpoint(2, GridPosition(3, 3)), NumberedCheckpoint(3, GridPosition(5, 4)), NumberedCheckpoint(4, GridPosition(4, 6)),
            NumberedCheckpoint(5, GridPosition(6, 7)), NumberedCheckpoint(6, GridPosition(9, 8)), NumberedCheckpoint(7, GridPosition(8, 3)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(8, 8)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(1, 1)),
            NumberedCheckpoint(13, GridPosition(4, 1)), NumberedCheckpoint(14, GridPosition(1, 5)), NumberedCheckpoint(15, GridPosition(1, 4)), NumberedCheckpoint(16, GridPosition(2, 3)),
            NumberedCheckpoint(17, GridPosition(2, 7)), NumberedCheckpoint(18, GridPosition(0, 6)), NumberedCheckpoint(19, GridPosition(5, 8)), NumberedCheckpoint(20, GridPosition(0, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2))
        ),
        difficultyMetadata = "World 5 - Level 176 (seed=5182)",
        seed = 5182L
    )

    fun buildSolution176(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9)
        )
    )

    // --- Level 177: World 5 - Level 177 (10x10) ---

    fun buildLevel177(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl177",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(9, 0)), NumberedCheckpoint(3, GridPosition(7, 2)), NumberedCheckpoint(4, GridPosition(6, 3)),
            NumberedCheckpoint(5, GridPosition(8, 4)), NumberedCheckpoint(6, GridPosition(8, 8)), NumberedCheckpoint(7, GridPosition(4, 4)), NumberedCheckpoint(8, GridPosition(3, 5)),
            NumberedCheckpoint(9, GridPosition(5, 1)), NumberedCheckpoint(10, GridPosition(3, 1)), NumberedCheckpoint(11, GridPosition(1, 2)), NumberedCheckpoint(12, GridPosition(1, 0)),
            NumberedCheckpoint(13, GridPosition(2, 9)), NumberedCheckpoint(14, GridPosition(2, 3)), NumberedCheckpoint(15, GridPosition(4, 6)), NumberedCheckpoint(16, GridPosition(2, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 8), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(5, 9)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(7, 1)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 5 - Level 177 (seed=5177)",
        seed = 5177L
    )

    fun buildSolution177(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7)
        )
    )

    // --- Level 178: World 5 - Level 178 (10x10) ---

    fun buildLevel178(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl178",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)), NumberedCheckpoint(2, GridPosition(8, 1)), NumberedCheckpoint(3, GridPosition(7, 3)), NumberedCheckpoint(4, GridPosition(6, 7)),
            NumberedCheckpoint(5, GridPosition(4, 2)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(2, 2)), NumberedCheckpoint(8, GridPosition(2, 6)),
            NumberedCheckpoint(9, GridPosition(0, 7)), NumberedCheckpoint(10, GridPosition(2, 8)), NumberedCheckpoint(11, GridPosition(2, 3)), NumberedCheckpoint(12, GridPosition(4, 3)),
            NumberedCheckpoint(13, GridPosition(4, 9)), NumberedCheckpoint(14, GridPosition(7, 6)), NumberedCheckpoint(15, GridPosition(6, 4)), NumberedCheckpoint(16, GridPosition(5, 5)),
            NumberedCheckpoint(17, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2))
        ),
        difficultyMetadata = "World 5 - Level 178 (seed=5206)",
        seed = 5206L
    )

    fun buildSolution178(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4)
        )
    )

    // --- Level 179: World 5 - Level 179 (10x10) ---

    fun buildLevel179(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl179",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)), NumberedCheckpoint(2, GridPosition(6, 1)), NumberedCheckpoint(3, GridPosition(3, 0)), NumberedCheckpoint(4, GridPosition(8, 3)),
            NumberedCheckpoint(5, GridPosition(7, 1)), NumberedCheckpoint(6, GridPosition(7, 4)), NumberedCheckpoint(7, GridPosition(5, 3)), NumberedCheckpoint(8, GridPosition(5, 5)),
            NumberedCheckpoint(9, GridPosition(8, 6)), NumberedCheckpoint(10, GridPosition(7, 6)), NumberedCheckpoint(11, GridPosition(9, 7)), NumberedCheckpoint(12, GridPosition(0, 1)),
            NumberedCheckpoint(13, GridPosition(1, 4)), NumberedCheckpoint(14, GridPosition(0, 5)), NumberedCheckpoint(15, GridPosition(1, 7)), NumberedCheckpoint(16, GridPosition(2, 5)),
            NumberedCheckpoint(17, GridPosition(1, 8)), NumberedCheckpoint(18, GridPosition(3, 8)), NumberedCheckpoint(19, GridPosition(4, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4))
        ),
        difficultyMetadata = "World 5 - Level 179 (seed=5179)",
        seed = 5179L
    )

    fun buildSolution179(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7)
        )
    )

    // --- Level 180: World 5 - Level 180 (10x10) ---

    fun buildLevel180(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl180",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 6)), NumberedCheckpoint(2, GridPosition(7, 4)), NumberedCheckpoint(3, GridPosition(8, 3)), NumberedCheckpoint(4, GridPosition(7, 2)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(7, 1)), NumberedCheckpoint(7, GridPosition(6, 3)), NumberedCheckpoint(8, GridPosition(4, 8)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(6, 8)), NumberedCheckpoint(11, GridPosition(9, 7)), NumberedCheckpoint(12, GridPosition(3, 7)),
            NumberedCheckpoint(13, GridPosition(1, 5)), NumberedCheckpoint(14, GridPosition(0, 7)), NumberedCheckpoint(15, GridPosition(1, 2)), NumberedCheckpoint(16, GridPosition(3, 2)),
            NumberedCheckpoint(17, GridPosition(1, 3)), NumberedCheckpoint(18, GridPosition(5, 2)), NumberedCheckpoint(19, GridPosition(5, 0)), NumberedCheckpoint(20, GridPosition(0, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 5), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9))
        ),
        difficultyMetadata = "World 5 - Level 180 (seed=5180)",
        seed = 5180L
    )

    fun buildSolution180(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )

    // --- Level 181: World 5 - Level 181 (11x11) ---

    fun buildLevel181(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl181",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(2, 9)), NumberedCheckpoint(3, GridPosition(0, 9)), NumberedCheckpoint(4, GridPosition(2, 10)),
            NumberedCheckpoint(5, GridPosition(9, 10)), NumberedCheckpoint(6, GridPosition(9, 4)), NumberedCheckpoint(7, GridPosition(10, 6)), NumberedCheckpoint(8, GridPosition(9, 2)),
            NumberedCheckpoint(9, GridPosition(8, 0)), NumberedCheckpoint(10, GridPosition(2, 0)), NumberedCheckpoint(11, GridPosition(5, 3)), NumberedCheckpoint(12, GridPosition(8, 1)),
            NumberedCheckpoint(13, GridPosition(6, 5)), NumberedCheckpoint(14, GridPosition(8, 8)), NumberedCheckpoint(15, GridPosition(5, 8)), NumberedCheckpoint(16, GridPosition(3, 9)),
            NumberedCheckpoint(17, GridPosition(4, 5)), NumberedCheckpoint(18, GridPosition(2, 5)), NumberedCheckpoint(19, GridPosition(1, 7)), NumberedCheckpoint(20, GridPosition(0, 6)),
            NumberedCheckpoint(21, GridPosition(1, 3)), NumberedCheckpoint(22, GridPosition(1, 1)), NumberedCheckpoint(23, GridPosition(3, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(9, 4), GridPosition(9, 5)),
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(0, 9), GridPosition(1, 9)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(1, 10)),
            BlockedEdge.between(GridPosition(10, 8), GridPosition(10, 9)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3))
        ),
        difficultyMetadata = "World 5 - Level 181 (seed=5182)",
        seed = 5182L
    )

    fun buildSolution181(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10),
            GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3),
            GridPosition(10, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5)
        )
    )

    // --- Level 182: World 5 - Level 182 (11x11) ---

    fun buildLevel182(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl182",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)), NumberedCheckpoint(2, GridPosition(8, 2)), NumberedCheckpoint(3, GridPosition(6, 1)), NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(4, 1)), NumberedCheckpoint(6, GridPosition(7, 3)), NumberedCheckpoint(7, GridPosition(2, 4)), NumberedCheckpoint(8, GridPosition(0, 3)),
            NumberedCheckpoint(9, GridPosition(0, 6)), NumberedCheckpoint(10, GridPosition(10, 9)), NumberedCheckpoint(11, GridPosition(9, 7)), NumberedCheckpoint(12, GridPosition(9, 8)),
            NumberedCheckpoint(13, GridPosition(6, 8)), NumberedCheckpoint(14, GridPosition(2, 9)), NumberedCheckpoint(15, GridPosition(1, 5)), NumberedCheckpoint(16, GridPosition(3, 8)),
            NumberedCheckpoint(17, GridPosition(4, 6)), NumberedCheckpoint(18, GridPosition(5, 8)), NumberedCheckpoint(19, GridPosition(7, 7)), NumberedCheckpoint(20, GridPosition(6, 5)),
            NumberedCheckpoint(21, GridPosition(8, 4)), NumberedCheckpoint(22, GridPosition(9, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 1), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(9, 0), GridPosition(9, 1)),
            BlockedEdge.between(GridPosition(10, 3), GridPosition(10, 4)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3))
        ),
        difficultyMetadata = "World 5 - Level 182 (seed=5194)",
        seed = 5194L
    )

    fun buildSolution182(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(10, 2), GridPosition(10, 3),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(7, 10),
            GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(9, 6),
            GridPosition(9, 5)
        )
    )

    // --- Level 183: World 5 - Level 183 (11x11) ---

    fun buildLevel183(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl183",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 4)), NumberedCheckpoint(2, GridPosition(8, 10)), NumberedCheckpoint(3, GridPosition(10, 7)), NumberedCheckpoint(4, GridPosition(9, 4)),
            NumberedCheckpoint(5, GridPosition(10, 4)), NumberedCheckpoint(6, GridPosition(8, 2)), NumberedCheckpoint(7, GridPosition(10, 2)), NumberedCheckpoint(8, GridPosition(8, 1)),
            NumberedCheckpoint(9, GridPosition(7, 3)), NumberedCheckpoint(10, GridPosition(5, 0)), NumberedCheckpoint(11, GridPosition(1, 1)), NumberedCheckpoint(12, GridPosition(2, 3)),
            NumberedCheckpoint(13, GridPosition(1, 10)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(1, 4)), NumberedCheckpoint(16, GridPosition(5, 4)),
            NumberedCheckpoint(17, GridPosition(3, 7)), NumberedCheckpoint(18, GridPosition(2, 8)), NumberedCheckpoint(19, GridPosition(7, 4)), NumberedCheckpoint(20, GridPosition(8, 8)),
            NumberedCheckpoint(21, GridPosition(5, 10)), NumberedCheckpoint(22, GridPosition(2, 10)), NumberedCheckpoint(23, GridPosition(3, 9)), NumberedCheckpoint(24, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(8, 9), GridPosition(8, 10)),
            BlockedEdge.between(GridPosition(10, 2), GridPosition(10, 3)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
        ),
        difficultyMetadata = "World 5 - Level 183 (seed=5226)",
        seed = 5226L
    )

    fun buildSolution183(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(10, 4), GridPosition(10, 3), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(1, 9), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10),
            GridPosition(2, 10), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7),
            GridPosition(6, 6)
        )
    )

    // --- Level 184: World 5 - Level 184 (11x11) ---

    fun buildLevel184(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl184",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 3)), NumberedCheckpoint(2, GridPosition(5, 3)), NumberedCheckpoint(3, GridPosition(7, 2)), NumberedCheckpoint(4, GridPosition(10, 0)),
            NumberedCheckpoint(5, GridPosition(9, 2)), NumberedCheckpoint(6, GridPosition(10, 4)), NumberedCheckpoint(7, GridPosition(8, 3)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(7, 6)), NumberedCheckpoint(10, GridPosition(9, 9)), NumberedCheckpoint(11, GridPosition(6, 10)), NumberedCheckpoint(12, GridPosition(8, 8)),
            NumberedCheckpoint(13, GridPosition(3, 6)), NumberedCheckpoint(14, GridPosition(1, 5)), NumberedCheckpoint(15, GridPosition(3, 5)), NumberedCheckpoint(16, GridPosition(2, 2)),
            NumberedCheckpoint(17, GridPosition(3, 3)), NumberedCheckpoint(18, GridPosition(2, 1)), NumberedCheckpoint(19, GridPosition(0, 3)), NumberedCheckpoint(20, GridPosition(5, 9)),
            NumberedCheckpoint(21, GridPosition(2, 9)), NumberedCheckpoint(22, GridPosition(2, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(6, 8), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(10, 5), GridPosition(10, 6)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8))
        ),
        difficultyMetadata = "World 5 - Level 184 (seed=5185)",
        seed = 5185L
    )

    fun buildSolution184(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10),
            GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(3, 10),
            GridPosition(4, 10), GridPosition(5, 10), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8)
        )
    )

    // --- Level 185: World 5 - Level 185 (11x11) ---

    fun buildLevel185(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl185",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 9)), NumberedCheckpoint(2, GridPosition(7, 6)), NumberedCheckpoint(3, GridPosition(8, 8)), NumberedCheckpoint(4, GridPosition(6, 7)),
            NumberedCheckpoint(5, GridPosition(5, 10)), NumberedCheckpoint(6, GridPosition(3, 7)), NumberedCheckpoint(7, GridPosition(1, 4)), NumberedCheckpoint(8, GridPosition(3, 6)),
            NumberedCheckpoint(9, GridPosition(4, 4)), NumberedCheckpoint(10, GridPosition(4, 5)), NumberedCheckpoint(11, GridPosition(7, 4)), NumberedCheckpoint(12, GridPosition(10, 6)),
            NumberedCheckpoint(13, GridPosition(7, 1)), NumberedCheckpoint(14, GridPosition(8, 3)), NumberedCheckpoint(15, GridPosition(8, 2)), NumberedCheckpoint(16, GridPosition(5, 1)),
            NumberedCheckpoint(17, GridPosition(3, 0)), NumberedCheckpoint(18, GridPosition(0, 1)), NumberedCheckpoint(19, GridPosition(1, 3)), NumberedCheckpoint(20, GridPosition(0, 7)),
            NumberedCheckpoint(21, GridPosition(3, 9)), NumberedCheckpoint(22, GridPosition(0, 9)), NumberedCheckpoint(23, GridPosition(1, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(9, 6), GridPosition(9, 7)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0))
        ),
        difficultyMetadata = "World 5 - Level 185 (seed=5215)",
        seed = 5215L
    )

    fun buildSolution185(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 8), GridPosition(10, 9),
            GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(10, 6), GridPosition(10, 5),
            GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8),
            GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10), GridPosition(0, 10), GridPosition(0, 9),
            GridPosition(1, 9)
        )
    )

    // --- Level 186: World 5 - Level 186 (11x11) ---

    fun buildLevel186(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl186",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 7)), NumberedCheckpoint(2, GridPosition(10, 3)), NumberedCheckpoint(3, GridPosition(7, 3)), NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(7, 4)), NumberedCheckpoint(6, GridPosition(7, 5)), NumberedCheckpoint(7, GridPosition(8, 3)), NumberedCheckpoint(8, GridPosition(9, 5)),
            NumberedCheckpoint(9, GridPosition(8, 9)), NumberedCheckpoint(10, GridPosition(9, 8)), NumberedCheckpoint(11, GridPosition(5, 10)), NumberedCheckpoint(12, GridPosition(7, 9)),
            NumberedCheckpoint(13, GridPosition(5, 8)), NumberedCheckpoint(14, GridPosition(7, 7)), NumberedCheckpoint(15, GridPosition(4, 7)), NumberedCheckpoint(16, GridPosition(2, 10)),
            NumberedCheckpoint(17, GridPosition(3, 6)), NumberedCheckpoint(18, GridPosition(1, 2)), NumberedCheckpoint(19, GridPosition(1, 4)), NumberedCheckpoint(20, GridPosition(4, 4)),
            NumberedCheckpoint(21, GridPosition(6, 1)), NumberedCheckpoint(22, GridPosition(8, 0)), NumberedCheckpoint(23, GridPosition(7, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 1), GridPosition(9, 2)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(7, 9), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(3, 10), GridPosition(4, 10)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(0, 8)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(2, 10))
        ),
        difficultyMetadata = "World 5 - Level 186 (seed=5225)",
        seed = 5225L
    )

    fun buildSolution186(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 7), GridPosition(10, 7), GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(3, 10), GridPosition(2, 10), GridPosition(1, 10),
            GridPosition(0, 10), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1)
        )
    )

    // --- Level 187: World 5 - Level 187 (11x11) ---

    fun buildLevel187(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl187",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(1, 4)), NumberedCheckpoint(3, GridPosition(0, 2)), NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(3, 2)), NumberedCheckpoint(6, GridPosition(9, 2)), NumberedCheckpoint(7, GridPosition(10, 6)), NumberedCheckpoint(8, GridPosition(9, 8)),
            NumberedCheckpoint(9, GridPosition(8, 2)), NumberedCheckpoint(10, GridPosition(7, 2)), NumberedCheckpoint(11, GridPosition(5, 4)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(4, 3)), NumberedCheckpoint(14, GridPosition(3, 5)), NumberedCheckpoint(15, GridPosition(0, 6)), NumberedCheckpoint(16, GridPosition(3, 9)),
            NumberedCheckpoint(17, GridPosition(5, 8)), NumberedCheckpoint(18, GridPosition(9, 10)), NumberedCheckpoint(19, GridPosition(7, 9)), NumberedCheckpoint(20, GridPosition(6, 8)),
            NumberedCheckpoint(21, GridPosition(8, 7)), NumberedCheckpoint(22, GridPosition(5, 7)), NumberedCheckpoint(23, GridPosition(2, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6))
        ),
        difficultyMetadata = "World 5 - Level 187 (seed=5188)",
        seed = 5188L
    )

    fun buildSolution187(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(10, 0), GridPosition(10, 1), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7), GridPosition(10, 8), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 10), GridPosition(4, 10), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(5, 10),
            GridPosition(6, 10), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(2, 8)
        )
    )

    // --- Level 188: World 5 - Level 188 (11x11) ---

    fun buildLevel188(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl188",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 5)), NumberedCheckpoint(2, GridPosition(7, 2)), NumberedCheckpoint(3, GridPosition(9, 1)), NumberedCheckpoint(4, GridPosition(2, 2)),
            NumberedCheckpoint(5, GridPosition(0, 5)), NumberedCheckpoint(6, GridPosition(2, 8)), NumberedCheckpoint(7, GridPosition(3, 7)), NumberedCheckpoint(8, GridPosition(1, 7)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(3, 5)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(6, 5)),
            NumberedCheckpoint(13, GridPosition(4, 9)), NumberedCheckpoint(14, GridPosition(0, 10)), NumberedCheckpoint(15, GridPosition(6, 9)), NumberedCheckpoint(16, GridPosition(5, 7)),
            NumberedCheckpoint(17, GridPosition(9, 8)), NumberedCheckpoint(18, GridPosition(7, 10)), NumberedCheckpoint(19, GridPosition(9, 10)), NumberedCheckpoint(20, GridPosition(9, 2)),
            NumberedCheckpoint(21, GridPosition(9, 5)), NumberedCheckpoint(22, GridPosition(8, 7)), NumberedCheckpoint(23, GridPosition(7, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 5 - Level 188 (seed=5208)",
        seed = 5208L
    )

    fun buildSolution188(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(9, 1), GridPosition(10, 1), GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 10), GridPosition(1, 10),
            GridPosition(2, 10), GridPosition(3, 10), GridPosition(4, 10), GridPosition(5, 10), GridPosition(6, 10), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(7, 10), GridPosition(8, 10), GridPosition(9, 10), GridPosition(10, 10), GridPosition(10, 9), GridPosition(10, 8), GridPosition(10, 7),
            GridPosition(10, 6), GridPosition(10, 5), GridPosition(10, 4), GridPosition(10, 3), GridPosition(10, 2), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(7, 7)
        )
    )

    // --- Level 189: World 5 - Level 189 (11x11) ---

    fun buildLevel189(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl189",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(0, 7)), NumberedCheckpoint(3, GridPosition(5, 10)), NumberedCheckpoint(4, GridPosition(10, 10)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(8, 6)), NumberedCheckpoint(7, GridPosition(9, 4)), NumberedCheckpoint(8, GridPosition(10, 2)),
            NumberedCheckpoint(9, GridPosition(4, 0)), NumberedCheckpoint(10, GridPosition(6, 1)), NumberedCheckpoint(11, GridPosition(8, 2)), NumberedCheckpoint(12, GridPosition(6, 4)),
            NumberedCheckpoint(13, GridPosition(6, 3)), NumberedCheckpoint(14, GridPosition(3, 1)), NumberedCheckpoint(15, GridPosition(0, 1)), NumberedCheckpoint(16, GridPosition(2, 4)),
            NumberedCheckpoint(17, GridPosition(5, 4)), NumberedCheckpoint(18, GridPosition(7, 5)), NumberedCheckpoint(19, GridPosition(5, 6)), NumberedCheckpoint(20, GridPosition(0, 5)),
            NumberedCheckpoint(21, GridPosition(3, 7)), NumberedCheckpoint(22, GridPosition(7, 8)), NumberedCheckpoint(23, GridPosition(9, 9)), NumberedCheckpoint(24, GridPosition(3, 9))
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
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2))
        ),
        difficultyMetadata = "World 5 - Level 189 (seed=5199)",
        seed = 5199L
    )

    fun buildSolution189(): PuzzlePath = PuzzlePath(
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

    // --- Level 190: World 5 - Level 190 (11x11) ---

    fun buildLevel190(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl190",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 9)), NumberedCheckpoint(2, GridPosition(7, 8)), NumberedCheckpoint(3, GridPosition(9, 9)), NumberedCheckpoint(4, GridPosition(9, 10)),
            NumberedCheckpoint(5, GridPosition(9, 8)), NumberedCheckpoint(6, GridPosition(9, 1)), NumberedCheckpoint(7, GridPosition(9, 0)), NumberedCheckpoint(8, GridPosition(7, 2)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(5, 6)), NumberedCheckpoint(12, GridPosition(7, 3)),
            NumberedCheckpoint(13, GridPosition(9, 6)), NumberedCheckpoint(14, GridPosition(5, 8)), NumberedCheckpoint(15, GridPosition(3, 7)), NumberedCheckpoint(16, GridPosition(3, 4)),
            NumberedCheckpoint(17, GridPosition(1, 7)), NumberedCheckpoint(18, GridPosition(4, 9)), NumberedCheckpoint(19, GridPosition(1, 5)), NumberedCheckpoint(20, GridPosition(1, 2)),
            NumberedCheckpoint(21, GridPosition(3, 1)), NumberedCheckpoint(22, GridPosition(1, 1))
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
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3))
        ),
        difficultyMetadata = "World 5 - Level 190 (seed=5197)",
        seed = 5197L
    )

    fun buildSolution190(): PuzzlePath = PuzzlePath(
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

    // --- Level 191: World 5 - Level 191 (11x11) ---

    fun buildLevel191(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl191",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(7, 10)), NumberedCheckpoint(3, GridPosition(8, 10)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(2, 8)), NumberedCheckpoint(6, GridPosition(2, 6)), NumberedCheckpoint(7, GridPosition(3, 9)), NumberedCheckpoint(8, GridPosition(5, 6)),
            NumberedCheckpoint(9, GridPosition(7, 7)), NumberedCheckpoint(10, GridPosition(7, 5)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(6, 2)),
            NumberedCheckpoint(13, GridPosition(5, 2)), NumberedCheckpoint(14, GridPosition(2, 4)), NumberedCheckpoint(15, GridPosition(0, 1)), NumberedCheckpoint(16, GridPosition(2, 3)),
            NumberedCheckpoint(17, GridPosition(2, 2)), NumberedCheckpoint(18, GridPosition(4, 1)), NumberedCheckpoint(19, GridPosition(8, 3)), NumberedCheckpoint(20, GridPosition(8, 6)),
            NumberedCheckpoint(21, GridPosition(9, 6)), NumberedCheckpoint(22, GridPosition(10, 10)), NumberedCheckpoint(23, GridPosition(6, 8))
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
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2))
        ),
        difficultyMetadata = "World 5 - Level 191 (seed=5201)",
        seed = 5201L
    )

    fun buildSolution191(): PuzzlePath = PuzzlePath(
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

    // --- Level 192: World 5 - Level 192 (11x11) ---

    fun buildLevel192(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl192",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)), NumberedCheckpoint(2, GridPosition(2, 5)), NumberedCheckpoint(3, GridPosition(0, 4)), NumberedCheckpoint(4, GridPosition(1, 0)),
            NumberedCheckpoint(5, GridPosition(3, 3)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(2, 0)), NumberedCheckpoint(8, GridPosition(8, 0)),
            NumberedCheckpoint(9, GridPosition(10, 3)), NumberedCheckpoint(10, GridPosition(9, 1)), NumberedCheckpoint(11, GridPosition(6, 3)), NumberedCheckpoint(12, GridPosition(7, 3)),
            NumberedCheckpoint(13, GridPosition(9, 9)), NumberedCheckpoint(14, GridPosition(7, 10)), NumberedCheckpoint(15, GridPosition(8, 8)), NumberedCheckpoint(16, GridPosition(2, 8)),
            NumberedCheckpoint(17, GridPosition(5, 10)), NumberedCheckpoint(18, GridPosition(4, 7)), NumberedCheckpoint(19, GridPosition(7, 7)), NumberedCheckpoint(20, GridPosition(8, 5)),
            NumberedCheckpoint(21, GridPosition(7, 6)), NumberedCheckpoint(22, GridPosition(6, 4)), NumberedCheckpoint(23, GridPosition(3, 5))
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
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8))
        ),
        difficultyMetadata = "World 5 - Level 192 (seed=5202)",
        seed = 5202L
    )

    fun buildSolution192(): PuzzlePath = PuzzlePath(
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

    // --- Level 193: World 5 - Level 193 (11x11) ---

    fun buildLevel193(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl193",
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
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 5 - Level 193 (seed=5204)",
        seed = 5204L
    )

    fun buildSolution193(): PuzzlePath = PuzzlePath(
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

    // --- Level 194: World 5 - Level 194 (11x11) ---

    fun buildLevel194(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl194",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 8)), NumberedCheckpoint(2, GridPosition(9, 10)), NumberedCheckpoint(3, GridPosition(6, 8)), NumberedCheckpoint(4, GridPosition(4, 9)),
            NumberedCheckpoint(5, GridPosition(2, 8)), NumberedCheckpoint(6, GridPosition(1, 3)), NumberedCheckpoint(7, GridPosition(0, 6)), NumberedCheckpoint(8, GridPosition(0, 5)),
            NumberedCheckpoint(9, GridPosition(0, 0)), NumberedCheckpoint(10, GridPosition(3, 6)), NumberedCheckpoint(11, GridPosition(5, 8)), NumberedCheckpoint(12, GridPosition(5, 7)),
            NumberedCheckpoint(13, GridPosition(9, 8)), NumberedCheckpoint(14, GridPosition(8, 3)), NumberedCheckpoint(15, GridPosition(9, 3)), NumberedCheckpoint(16, GridPosition(8, 5)),
            NumberedCheckpoint(17, GridPosition(9, 6)), NumberedCheckpoint(18, GridPosition(5, 5)), NumberedCheckpoint(19, GridPosition(9, 0)), NumberedCheckpoint(20, GridPosition(2, 0)),
            NumberedCheckpoint(21, GridPosition(4, 1)), NumberedCheckpoint(22, GridPosition(6, 4)), NumberedCheckpoint(23, GridPosition(4, 2)), NumberedCheckpoint(24, GridPosition(5, 3))
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
            BlockedEdge.between(GridPosition(9, 2), GridPosition(9, 3))
        ),
        difficultyMetadata = "World 5 - Level 194 (seed=5203)",
        seed = 5203L
    )

    fun buildSolution194(): PuzzlePath = PuzzlePath(
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

    // --- Level 195: World 5 - Level 195 (11x11) ---

    fun buildLevel195(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl195",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 1)), NumberedCheckpoint(2, GridPosition(7, 2)), NumberedCheckpoint(3, GridPosition(8, 0)), NumberedCheckpoint(4, GridPosition(4, 1)),
            NumberedCheckpoint(5, GridPosition(2, 0)), NumberedCheckpoint(6, GridPosition(2, 3)), NumberedCheckpoint(7, GridPosition(1, 1)), NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(7, 4)), NumberedCheckpoint(10, GridPosition(10, 1)), NumberedCheckpoint(11, GridPosition(10, 4)), NumberedCheckpoint(12, GridPosition(8, 5)),
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
        difficultyMetadata = "World 5 - Level 195 (seed=5197)",
        seed = 5197L
    )

    fun buildSolution195(): PuzzlePath = PuzzlePath(
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

    // --- Level 196: World 5 - Level 196 (11x11) ---

    fun buildLevel196(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl196",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 2)), NumberedCheckpoint(2, GridPosition(10, 10)), NumberedCheckpoint(3, GridPosition(9, 6)), NumberedCheckpoint(4, GridPosition(6, 4)),
            NumberedCheckpoint(5, GridPosition(6, 5)), NumberedCheckpoint(6, GridPosition(8, 8)), NumberedCheckpoint(7, GridPosition(7, 10)), NumberedCheckpoint(8, GridPosition(3, 9)),
            NumberedCheckpoint(9, GridPosition(5, 8)), NumberedCheckpoint(10, GridPosition(6, 8)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(7, 2)),
            NumberedCheckpoint(13, GridPosition(9, 1)), NumberedCheckpoint(14, GridPosition(8, 1)), NumberedCheckpoint(15, GridPosition(4, 0)), NumberedCheckpoint(16, GridPosition(3, 1)),
            NumberedCheckpoint(17, GridPosition(0, 2)), NumberedCheckpoint(18, GridPosition(3, 5)), NumberedCheckpoint(19, GridPosition(3, 2)), NumberedCheckpoint(20, GridPosition(1, 3)),
            NumberedCheckpoint(21, GridPosition(1, 5)), NumberedCheckpoint(22, GridPosition(2, 7)), NumberedCheckpoint(23, GridPosition(1, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(10, 4), GridPosition(10, 5)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0))
        ),
        difficultyMetadata = "World 5 - Level 196 (seed=5206)",
        seed = 5206L
    )

    fun buildSolution196(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(10, 2), GridPosition(10, 3), GridPosition(10, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(10, 5), GridPosition(10, 6), GridPosition(10, 7),
            GridPosition(10, 8), GridPosition(10, 9), GridPosition(10, 10), GridPosition(9, 10), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(8, 9), GridPosition(8, 10), GridPosition(7, 10),
            GridPosition(6, 10), GridPosition(5, 10), GridPosition(4, 10), GridPosition(3, 10), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 8), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(10, 1),
            GridPosition(10, 0), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(0, 10), GridPosition(1, 10), GridPosition(2, 10), GridPosition(2, 9), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 8),
            GridPosition(1, 9)
        )
    )

    // --- Level 197: World 5 - Level 197 (11x11) ---

    fun buildLevel197(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl197",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(3, 0)), NumberedCheckpoint(3, GridPosition(4, 2)), NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(9, 5)), NumberedCheckpoint(6, GridPosition(6, 2)), NumberedCheckpoint(7, GridPosition(8, 1)), NumberedCheckpoint(8, GridPosition(9, 3)),
            NumberedCheckpoint(9, GridPosition(10, 1)), NumberedCheckpoint(10, GridPosition(2, 10)), NumberedCheckpoint(11, GridPosition(4, 7)), NumberedCheckpoint(12, GridPosition(3, 7)),
            NumberedCheckpoint(13, GridPosition(1, 10)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(0, 2)), NumberedCheckpoint(16, GridPosition(2, 1)),
            NumberedCheckpoint(17, GridPosition(1, 3)), NumberedCheckpoint(18, GridPosition(6, 6)), NumberedCheckpoint(19, GridPosition(9, 8)), NumberedCheckpoint(20, GridPosition(6, 8)),
            NumberedCheckpoint(21, GridPosition(7, 8)), NumberedCheckpoint(22, GridPosition(5, 9))
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
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7))
        ),
        difficultyMetadata = "World 5 - Level 197 (seed=5212)",
        seed = 5212L
    )

    fun buildSolution197(): PuzzlePath = PuzzlePath(
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

    // --- Level 198: World 5 - Level 198 (11x11) ---

    fun buildLevel198(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl198",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 6)), NumberedCheckpoint(2, GridPosition(6, 7)), NumberedCheckpoint(3, GridPosition(7, 5)), NumberedCheckpoint(4, GridPosition(10, 8)),
            NumberedCheckpoint(5, GridPosition(8, 9)), NumberedCheckpoint(6, GridPosition(9, 7)), NumberedCheckpoint(7, GridPosition(8, 5)), NumberedCheckpoint(8, GridPosition(10, 5)),
            NumberedCheckpoint(9, GridPosition(10, 2)), NumberedCheckpoint(10, GridPosition(8, 3)), NumberedCheckpoint(11, GridPosition(10, 0)), NumberedCheckpoint(12, GridPosition(7, 0)),
            NumberedCheckpoint(13, GridPosition(6, 4)), NumberedCheckpoint(14, GridPosition(5, 1)), NumberedCheckpoint(15, GridPosition(1, 2)), NumberedCheckpoint(16, GridPosition(3, 1)),
            NumberedCheckpoint(17, GridPosition(0, 4)), NumberedCheckpoint(18, GridPosition(3, 5)), NumberedCheckpoint(19, GridPosition(0, 5)), NumberedCheckpoint(20, GridPosition(3, 8)),
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
            BlockedEdge.between(GridPosition(5, 4), GridPosition(6, 4))
        ),
        difficultyMetadata = "World 5 - Level 198 (seed=5200)",
        seed = 5200L
    )

    fun buildSolution198(): PuzzlePath = PuzzlePath(
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

    // --- Level 199: World 5 - Level 199 (11x11) ---

    fun buildLevel199(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl199",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(11, 11),
        requiredCells = grid11x11Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(10, 4)), NumberedCheckpoint(2, GridPosition(6, 10)), NumberedCheckpoint(3, GridPosition(9, 9)), NumberedCheckpoint(4, GridPosition(9, 0)),
            NumberedCheckpoint(5, GridPosition(6, 2)), NumberedCheckpoint(6, GridPosition(7, 0)), NumberedCheckpoint(7, GridPosition(4, 2)), NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(1, 1)), NumberedCheckpoint(10, GridPosition(3, 2)), NumberedCheckpoint(11, GridPosition(2, 2)), NumberedCheckpoint(12, GridPosition(8, 7)),
            NumberedCheckpoint(13, GridPosition(5, 5)), NumberedCheckpoint(14, GridPosition(7, 6)), NumberedCheckpoint(15, GridPosition(7, 4)), NumberedCheckpoint(16, GridPosition(2, 4)),
            NumberedCheckpoint(17, GridPosition(4, 5)), NumberedCheckpoint(18, GridPosition(4, 7)), NumberedCheckpoint(19, GridPosition(1, 10)), NumberedCheckpoint(20, GridPosition(2, 7)),
            NumberedCheckpoint(21, GridPosition(2, 8)), NumberedCheckpoint(22, GridPosition(0, 6)), NumberedCheckpoint(23, GridPosition(1, 7))
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
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4))
        ),
        difficultyMetadata = "World 5 - Level 199 (seed=5203)",
        seed = 5203L
    )

    fun buildSolution199(): PuzzlePath = PuzzlePath(
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

    // --- Level 200: World 5 - Level 200 (11x11) ---

    fun buildLevel200(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl200",
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
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5))
        ),
        difficultyMetadata = "World 5 - Level 200 (seed=5202)",
        seed = 5202L
    )

    fun buildSolution200(): PuzzlePath = PuzzlePath(
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

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[176] = buildLevel176()
        map[177] = buildLevel177()
        map[178] = buildLevel178()
        map[179] = buildLevel179()
        map[180] = buildLevel180()
        map[181] = buildLevel181()
        map[182] = buildLevel182()
        map[183] = buildLevel183()
        map[184] = buildLevel184()
        map[185] = buildLevel185()
        map[186] = buildLevel186()
        map[187] = buildLevel187()
        map[188] = buildLevel188()
        map[189] = buildLevel189()
        map[190] = buildLevel190()
        map[191] = buildLevel191()
        map[192] = buildLevel192()
        map[193] = buildLevel193()
        map[194] = buildLevel194()
        map[195] = buildLevel195()
        map[196] = buildLevel196()
        map[197] = buildLevel197()
        map[198] = buildLevel198()
        map[199] = buildLevel199()
        map[200] = buildLevel200()
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[176] = buildSolution176()
        map[177] = buildSolution177()
        map[178] = buildSolution178()
        map[179] = buildSolution179()
        map[180] = buildSolution180()
        map[181] = buildSolution181()
        map[182] = buildSolution182()
        map[183] = buildSolution183()
        map[184] = buildSolution184()
        map[185] = buildSolution185()
        map[186] = buildSolution186()
        map[187] = buildSolution187()
        map[188] = buildSolution188()
        map[189] = buildSolution189()
        map[190] = buildSolution190()
        map[191] = buildSolution191()
        map[192] = buildSolution192()
        map[193] = buildSolution193()
        map[194] = buildSolution194()
        map[195] = buildSolution195()
        map[196] = buildSolution196()
        map[197] = buildSolution197()
        map[198] = buildSolution198()
        map[199] = buildSolution199()
        map[200] = buildSolution200()
        map
    }
}
