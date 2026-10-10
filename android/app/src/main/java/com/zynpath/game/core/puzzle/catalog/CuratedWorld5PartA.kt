package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld5PartA {

    fun grid10x10Cells(): Set<GridPosition> =
        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()

    fun grid11x11Cells(): Set<GridPosition> =
        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()

    fun grid12x12Cells(): Set<GridPosition> =
        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 151: World 5 - Level 151 (10x10) ---

    fun buildLevel151(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl151",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(2, 9)), NumberedCheckpoint(3, GridPosition(0, 8)), NumberedCheckpoint(4, GridPosition(1, 8)),
            NumberedCheckpoint(5, GridPosition(1, 6)), NumberedCheckpoint(6, GridPosition(7, 6)), NumberedCheckpoint(7, GridPosition(8, 5)), NumberedCheckpoint(8, GridPosition(5, 5)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(2, 0)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(3, 1)),
            NumberedCheckpoint(13, GridPosition(8, 3)), NumberedCheckpoint(14, GridPosition(8, 1)), NumberedCheckpoint(15, GridPosition(6, 1)), NumberedCheckpoint(16, GridPosition(7, 4)),
            NumberedCheckpoint(17, GridPosition(8, 7)), NumberedCheckpoint(18, GridPosition(6, 9)), NumberedCheckpoint(19, GridPosition(7, 7)), NumberedCheckpoint(20, GridPosition(4, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(4, 9)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5))
        ),
        difficultyMetadata = "World 5 - Level 151 (seed=5160)",
        seed = 5160L
    )

    fun buildSolution151(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7)
        )
    )

    // --- Level 152: World 5 - Level 152 (10x10) ---

    fun buildLevel152(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl152",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)), NumberedCheckpoint(2, GridPosition(2, 3)), NumberedCheckpoint(3, GridPosition(0, 1)), NumberedCheckpoint(4, GridPosition(1, 6)),
            NumberedCheckpoint(5, GridPosition(1, 5)), NumberedCheckpoint(6, GridPosition(0, 7)), NumberedCheckpoint(7, GridPosition(2, 8)), NumberedCheckpoint(8, GridPosition(7, 7)),
            NumberedCheckpoint(9, GridPosition(8, 8)), NumberedCheckpoint(10, GridPosition(8, 4)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(8, 2)),
            NumberedCheckpoint(13, GridPosition(3, 1)), NumberedCheckpoint(14, GridPosition(4, 1)), NumberedCheckpoint(15, GridPosition(7, 2)), NumberedCheckpoint(16, GridPosition(6, 3)),
            NumberedCheckpoint(17, GridPosition(3, 4)), NumberedCheckpoint(18, GridPosition(5, 5)), NumberedCheckpoint(19, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(3, 8), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(6, 6), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7))
        ),
        difficultyMetadata = "World 5 - Level 152 (seed=5154)",
        seed = 5154L
    )

    fun buildSolution152(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3)
        )
    )

    // --- Level 153: World 5 - Level 153 (10x10) ---

    fun buildLevel153(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl153",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 6)), NumberedCheckpoint(2, GridPosition(2, 7)), NumberedCheckpoint(3, GridPosition(4, 6)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(7, 6)), NumberedCheckpoint(6, GridPosition(4, 9)), NumberedCheckpoint(7, GridPosition(2, 4)), NumberedCheckpoint(8, GridPosition(0, 0)),
            NumberedCheckpoint(9, GridPosition(2, 1)), NumberedCheckpoint(10, GridPosition(3, 1)), NumberedCheckpoint(11, GridPosition(8, 3)), NumberedCheckpoint(12, GridPosition(9, 4)),
            NumberedCheckpoint(13, GridPosition(8, 9)), NumberedCheckpoint(14, GridPosition(5, 3)), NumberedCheckpoint(15, GridPosition(7, 2)), NumberedCheckpoint(16, GridPosition(3, 4)),
            NumberedCheckpoint(17, GridPosition(1, 3)), NumberedCheckpoint(18, GridPosition(2, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(2, 8)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7))
        ),
        difficultyMetadata = "World 5 - Level 153 (seed=5168)",
        seed = 5168L
    )

    fun buildSolution153(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(2, 3)
        )
    )

    // --- Level 154: World 5 - Level 154 (10x10) ---

    fun buildLevel154(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl154",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 3)), NumberedCheckpoint(2, GridPosition(2, 3)), NumberedCheckpoint(3, GridPosition(1, 5)), NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(0, 3)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(6, 3)), NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(6, 1)), NumberedCheckpoint(10, GridPosition(5, 0)), NumberedCheckpoint(11, GridPosition(8, 4)), NumberedCheckpoint(12, GridPosition(9, 1)),
            NumberedCheckpoint(13, GridPosition(8, 7)), NumberedCheckpoint(14, GridPosition(9, 6)), NumberedCheckpoint(15, GridPosition(7, 8)), NumberedCheckpoint(16, GridPosition(5, 6)),
            NumberedCheckpoint(17, GridPosition(6, 5)), NumberedCheckpoint(18, GridPosition(3, 6)), NumberedCheckpoint(19, GridPosition(5, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(3, 1))
        ),
        difficultyMetadata = "World 5 - Level 154 (seed=5156)",
        seed = 5156L
    )

    fun buildSolution154(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8)
        )
    )

    // --- Level 155: World 5 - Level 155 (10x10) ---

    fun buildLevel155(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl155",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 2)), NumberedCheckpoint(2, GridPosition(8, 7)), NumberedCheckpoint(3, GridPosition(8, 9)), NumberedCheckpoint(4, GridPosition(9, 4)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(3, 1)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(2, 3)), NumberedCheckpoint(10, GridPosition(1, 3)), NumberedCheckpoint(11, GridPosition(1, 6)), NumberedCheckpoint(12, GridPosition(2, 9)),
            NumberedCheckpoint(13, GridPosition(1, 7)), NumberedCheckpoint(14, GridPosition(4, 7)), NumberedCheckpoint(15, GridPosition(3, 9)), NumberedCheckpoint(16, GridPosition(2, 5)),
            NumberedCheckpoint(17, GridPosition(7, 4)), NumberedCheckpoint(18, GridPosition(6, 2)), NumberedCheckpoint(19, GridPosition(5, 1)), NumberedCheckpoint(20, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(8, 2), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(8, 6), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(7, 7)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(2, 0))
        ),
        difficultyMetadata = "World 5 - Level 155 (seed=5160)",
        seed = 5160L
    )

    fun buildSolution155(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(3, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1)
        )
    )

    // --- Level 156: World 5 - Level 156 (10x10) ---

    fun buildLevel156(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl156",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 9)), NumberedCheckpoint(2, GridPosition(9, 6)), NumberedCheckpoint(3, GridPosition(4, 7)), NumberedCheckpoint(4, GridPosition(0, 9)),
            NumberedCheckpoint(5, GridPosition(3, 8)), NumberedCheckpoint(6, GridPosition(3, 5)), NumberedCheckpoint(7, GridPosition(4, 4)), NumberedCheckpoint(8, GridPosition(6, 6)),
            NumberedCheckpoint(9, GridPosition(6, 4)), NumberedCheckpoint(10, GridPosition(7, 2)), NumberedCheckpoint(11, GridPosition(2, 2)), NumberedCheckpoint(12, GridPosition(8, 5)),
            NumberedCheckpoint(13, GridPosition(9, 0)), NumberedCheckpoint(14, GridPosition(2, 5)), NumberedCheckpoint(15, GridPosition(2, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 4), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(8, 9)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(0, 8), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7))
        ),
        difficultyMetadata = "World 5 - Level 156 (seed=5159)",
        seed = 5159L
    )

    fun buildSolution156(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(8, 6), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(5, 7), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1)
        )
    )

    // --- Level 157: World 5 - Level 157 (10x10) ---

    fun buildLevel157(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl157",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)), NumberedCheckpoint(2, GridPosition(3, 0)), NumberedCheckpoint(3, GridPosition(1, 2)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(3, 2)), NumberedCheckpoint(6, GridPosition(8, 1)), NumberedCheckpoint(7, GridPosition(8, 4)), NumberedCheckpoint(8, GridPosition(6, 6)),
            NumberedCheckpoint(9, GridPosition(4, 4)), NumberedCheckpoint(10, GridPosition(3, 8)), NumberedCheckpoint(11, GridPosition(2, 8)), NumberedCheckpoint(12, GridPosition(4, 7)),
            NumberedCheckpoint(13, GridPosition(6, 7)), NumberedCheckpoint(14, GridPosition(5, 9)), NumberedCheckpoint(15, GridPosition(8, 7)), NumberedCheckpoint(16, GridPosition(7, 8)),
            NumberedCheckpoint(17, GridPosition(7, 5)), NumberedCheckpoint(18, GridPosition(9, 6)), NumberedCheckpoint(19, GridPosition(4, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 6), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3))
        ),
        difficultyMetadata = "World 5 - Level 157 (seed=5159)",
        seed = 5159L
    )

    fun buildSolution157(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1)
        )
    )

    // --- Level 158: World 5 - Level 158 (10x10) ---

    fun buildLevel158(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl158",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(2, 5)), NumberedCheckpoint(4, GridPosition(7, 6)),
            NumberedCheckpoint(5, GridPosition(1, 3)), NumberedCheckpoint(6, GridPosition(2, 1)), NumberedCheckpoint(7, GridPosition(6, 0)), NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(6, 3)), NumberedCheckpoint(10, GridPosition(5, 3)), NumberedCheckpoint(11, GridPosition(7, 3)), NumberedCheckpoint(12, GridPosition(8, 2)),
            NumberedCheckpoint(13, GridPosition(7, 8)), NumberedCheckpoint(14, GridPosition(6, 7)), NumberedCheckpoint(15, GridPosition(6, 8)), NumberedCheckpoint(16, GridPosition(3, 8)),
            NumberedCheckpoint(17, GridPosition(0, 7)), NumberedCheckpoint(18, GridPosition(4, 9)), NumberedCheckpoint(19, GridPosition(9, 2)), NumberedCheckpoint(20, GridPosition(8, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(0, 6)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5))
        ),
        difficultyMetadata = "World 5 - Level 158 (seed=5158)",
        seed = 5158L
    )

    fun buildSolution158(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(8, 1)
        )
    )

    // --- Level 159: World 5 - Level 159 (10x10) ---

    fun buildLevel159(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl159",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)), NumberedCheckpoint(2, GridPosition(8, 0)), NumberedCheckpoint(3, GridPosition(6, 4)), NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(8, 6)), NumberedCheckpoint(6, GridPosition(6, 7)), NumberedCheckpoint(7, GridPosition(6, 6)), NumberedCheckpoint(8, GridPosition(2, 8)),
            NumberedCheckpoint(9, GridPosition(2, 4)), NumberedCheckpoint(10, GridPosition(1, 2)), NumberedCheckpoint(11, GridPosition(2, 1)), NumberedCheckpoint(12, GridPosition(5, 2)),
            NumberedCheckpoint(13, GridPosition(6, 1)), NumberedCheckpoint(14, GridPosition(4, 0)), NumberedCheckpoint(15, GridPosition(3, 6)), NumberedCheckpoint(16, GridPosition(2, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(6, 9), GridPosition(7, 9)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "World 5 - Level 159 (seed=5170)",
        seed = 5170L
    )

    fun buildSolution159(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8),
            GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(2, 5)
        )
    )

    // --- Level 160: World 5 - Level 160 (10x10) ---

    fun buildLevel160(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl160",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 6)), NumberedCheckpoint(2, GridPosition(9, 8)), NumberedCheckpoint(3, GridPosition(0, 7)), NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(1, 2)), NumberedCheckpoint(6, GridPosition(4, 1)), NumberedCheckpoint(7, GridPosition(3, 1)), NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(5, 2)), NumberedCheckpoint(10, GridPosition(5, 3)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(8, 1)),
            NumberedCheckpoint(13, GridPosition(6, 1)), NumberedCheckpoint(14, GridPosition(8, 6)), NumberedCheckpoint(15, GridPosition(9, 6)), NumberedCheckpoint(16, GridPosition(4, 7)),
            NumberedCheckpoint(17, GridPosition(5, 8)), NumberedCheckpoint(18, GridPosition(1, 6)), NumberedCheckpoint(19, GridPosition(3, 7)), NumberedCheckpoint(20, GridPosition(4, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0))
        ),
        difficultyMetadata = "World 5 - Level 160 (seed=5163)",
        seed = 5163L
    )

    fun buildSolution160(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(8, 4), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5)
        )
    )

    // --- Level 161: World 5 - Level 161 (10x10) ---

    fun buildLevel161(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl161",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)), NumberedCheckpoint(2, GridPosition(7, 0)), NumberedCheckpoint(3, GridPosition(6, 2)), NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(8, 1)), NumberedCheckpoint(6, GridPosition(8, 3)), NumberedCheckpoint(7, GridPosition(7, 4)), NumberedCheckpoint(8, GridPosition(8, 6)),
            NumberedCheckpoint(9, GridPosition(8, 7)), NumberedCheckpoint(10, GridPosition(4, 8)), NumberedCheckpoint(11, GridPosition(6, 7)), NumberedCheckpoint(12, GridPosition(5, 7)),
            NumberedCheckpoint(13, GridPosition(2, 8)), NumberedCheckpoint(14, GridPosition(4, 4)), NumberedCheckpoint(15, GridPosition(2, 5)), NumberedCheckpoint(16, GridPosition(0, 9)),
            NumberedCheckpoint(17, GridPosition(2, 1)), NumberedCheckpoint(18, GridPosition(0, 1)), NumberedCheckpoint(19, GridPosition(3, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(8, 5), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(7, 8), GridPosition(7, 9))
        ),
        difficultyMetadata = "World 5 - Level 161 (seed=5166)",
        seed = 5166L
    )

    fun buildSolution161(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8),
            GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2)
        )
    )

    // --- Level 162: World 5 - Level 162 (10x10) ---

    fun buildLevel162(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl162",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 7)), NumberedCheckpoint(2, GridPosition(1, 7)), NumberedCheckpoint(3, GridPosition(1, 8)), NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(7, 5)), NumberedCheckpoint(7, GridPosition(8, 0)), NumberedCheckpoint(8, GridPosition(8, 7)),
            NumberedCheckpoint(9, GridPosition(7, 6)), NumberedCheckpoint(10, GridPosition(8, 9)), NumberedCheckpoint(11, GridPosition(4, 8)), NumberedCheckpoint(12, GridPosition(5, 5)),
            NumberedCheckpoint(13, GridPosition(5, 6)), NumberedCheckpoint(14, GridPosition(3, 4)), NumberedCheckpoint(15, GridPosition(6, 4)), NumberedCheckpoint(16, GridPosition(4, 2)),
            NumberedCheckpoint(17, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(2, 7), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(1, 9), GridPosition(2, 9)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2))
        ),
        difficultyMetadata = "World 5 - Level 162 (seed=5162)",
        seed = 5162L
    )

    fun buildSolution162(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(8, 5),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1)
        )
    )

    // --- Level 163: World 5 - Level 163 (10x10) ---

    fun buildLevel163(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl163",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)), NumberedCheckpoint(2, GridPosition(8, 6)), NumberedCheckpoint(3, GridPosition(8, 2)), NumberedCheckpoint(4, GridPosition(9, 0)),
            NumberedCheckpoint(5, GridPosition(1, 2)), NumberedCheckpoint(6, GridPosition(0, 0)), NumberedCheckpoint(7, GridPosition(5, 2)), NumberedCheckpoint(8, GridPosition(6, 6)),
            NumberedCheckpoint(9, GridPosition(5, 8)), NumberedCheckpoint(10, GridPosition(0, 4)), NumberedCheckpoint(11, GridPosition(1, 6)), NumberedCheckpoint(12, GridPosition(2, 5)),
            NumberedCheckpoint(13, GridPosition(0, 9)), NumberedCheckpoint(14, GridPosition(2, 8)), NumberedCheckpoint(15, GridPosition(3, 8)), NumberedCheckpoint(16, GridPosition(7, 8)),
            NumberedCheckpoint(17, GridPosition(8, 7)), NumberedCheckpoint(18, GridPosition(5, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0))
        ),
        difficultyMetadata = "World 5 - Level 163 (seed=5165)",
        seed = 5165L
    )

    fun buildSolution163(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(8, 2), GridPosition(8, 1),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(7, 0), GridPosition(7, 1), GridPosition(7, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8),
            GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(8, 8), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(8, 7), GridPosition(7, 7), GridPosition(7, 6),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4)
        )
    )

    // --- Level 164: World 5 - Level 164 (10x10) ---

    fun buildLevel164(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl164",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)), NumberedCheckpoint(2, GridPosition(1, 3)), NumberedCheckpoint(3, GridPosition(2, 2)), NumberedCheckpoint(4, GridPosition(1, 2)),
            NumberedCheckpoint(5, GridPosition(3, 4)), NumberedCheckpoint(6, GridPosition(4, 1)), NumberedCheckpoint(7, GridPosition(6, 3)), NumberedCheckpoint(8, GridPosition(1, 5)),
            NumberedCheckpoint(9, GridPosition(1, 7)), NumberedCheckpoint(10, GridPosition(4, 8)), NumberedCheckpoint(11, GridPosition(4, 6)), NumberedCheckpoint(12, GridPosition(5, 5)),
            NumberedCheckpoint(13, GridPosition(7, 7)), NumberedCheckpoint(14, GridPosition(5, 8)), NumberedCheckpoint(15, GridPosition(8, 1)), NumberedCheckpoint(16, GridPosition(8, 2)),
            NumberedCheckpoint(17, GridPosition(8, 4)), NumberedCheckpoint(18, GridPosition(8, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7))
        ),
        difficultyMetadata = "World 5 - Level 164 (seed=5172)",
        seed = 5172L
    )

    fun buildSolution164(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 9), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5), GridPosition(7, 5), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6),
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9)
        )
    )

    // --- Level 165: World 5 - Level 165 (10x10) ---

    fun buildLevel165(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl165",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 6)), NumberedCheckpoint(2, GridPosition(8, 7)), NumberedCheckpoint(3, GridPosition(7, 7)), NumberedCheckpoint(4, GridPosition(5, 8)),
            NumberedCheckpoint(5, GridPosition(1, 8)), NumberedCheckpoint(6, GridPosition(0, 6)), NumberedCheckpoint(7, GridPosition(9, 1)), NumberedCheckpoint(8, GridPosition(8, 4)),
            NumberedCheckpoint(9, GridPosition(6, 3)), NumberedCheckpoint(10, GridPosition(3, 1)), NumberedCheckpoint(11, GridPosition(4, 2)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(2, 3)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(2, 7)), NumberedCheckpoint(16, GridPosition(6, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3))
        ),
        difficultyMetadata = "World 5 - Level 165 (seed=5171)",
        seed = 5171L
    )

    fun buildSolution165(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(8, 6), GridPosition(8, 7), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9),
            GridPosition(9, 9), GridPosition(9, 8), GridPosition(9, 7), GridPosition(9, 6), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(8, 3), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1)
        )
    )

    // --- Level 166: World 5 - Level 166 (10x10) ---

    fun buildLevel166(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl166",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 8)), NumberedCheckpoint(2, GridPosition(6, 9)), NumberedCheckpoint(3, GridPosition(4, 7)), NumberedCheckpoint(4, GridPosition(3, 8)),
            NumberedCheckpoint(5, GridPosition(2, 7)), NumberedCheckpoint(6, GridPosition(0, 9)), NumberedCheckpoint(7, GridPosition(1, 4)), NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(4, 6)), NumberedCheckpoint(10, GridPosition(3, 3)), NumberedCheckpoint(11, GridPosition(5, 1)), NumberedCheckpoint(12, GridPosition(8, 0)),
            NumberedCheckpoint(13, GridPosition(6, 1)), NumberedCheckpoint(14, GridPosition(8, 3)), NumberedCheckpoint(15, GridPosition(9, 2)), NumberedCheckpoint(16, GridPosition(6, 6)),
            NumberedCheckpoint(17, GridPosition(6, 5)), NumberedCheckpoint(18, GridPosition(9, 5)), NumberedCheckpoint(19, GridPosition(8, 8)), NumberedCheckpoint(20, GridPosition(8, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(3, 7)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(2, 8), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(4, 0))
        ),
        difficultyMetadata = "World 5 - Level 166 (seed=5171)",
        seed = 5171L
    )

    fun buildSolution166(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(4, 8), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 9), GridPosition(7, 9), GridPosition(7, 8),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(1, 9), GridPosition(0, 9),
            GridPosition(0, 8), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7),
            GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9)
        )
    )

    // --- Level 167: World 5 - Level 167 (10x10) ---

    fun buildLevel167(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl167",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(9, 7)), NumberedCheckpoint(2, GridPosition(6, 6)), NumberedCheckpoint(3, GridPosition(8, 5)), NumberedCheckpoint(4, GridPosition(6, 3)),
            NumberedCheckpoint(5, GridPosition(9, 2)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(0, 2)), NumberedCheckpoint(8, GridPosition(2, 1)),
            NumberedCheckpoint(9, GridPosition(2, 4)), NumberedCheckpoint(10, GridPosition(5, 3)), NumberedCheckpoint(11, GridPosition(4, 4)), NumberedCheckpoint(12, GridPosition(7, 5)),
            NumberedCheckpoint(13, GridPosition(1, 3)), NumberedCheckpoint(14, GridPosition(4, 7)), NumberedCheckpoint(15, GridPosition(1, 7)), NumberedCheckpoint(16, GridPosition(5, 6)),
            NumberedCheckpoint(17, GridPosition(2, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(8, 3), GridPosition(8, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(5, 8), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(8, 7)),
            BlockedEdge.between(GridPosition(8, 8), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(8, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3))
        ),
        difficultyMetadata = "World 5 - Level 167 (seed=5168)",
        seed = 5168L
    )

    fun buildSolution167(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(9, 7), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9), GridPosition(6, 8), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0),
            GridPosition(8, 0), GridPosition(7, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(1, 9), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(4, 8), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(5, 8),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9)
        )
    )

    // --- Level 168: World 5 - Level 168 (10x10) ---

    fun buildLevel168(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl168",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(8, 6)), NumberedCheckpoint(2, GridPosition(9, 8)), NumberedCheckpoint(3, GridPosition(4, 9)), NumberedCheckpoint(4, GridPosition(9, 4)),
            NumberedCheckpoint(5, GridPosition(5, 1)), NumberedCheckpoint(6, GridPosition(6, 2)), NumberedCheckpoint(7, GridPosition(4, 3)), NumberedCheckpoint(8, GridPosition(7, 3)),
            NumberedCheckpoint(9, GridPosition(3, 6)), NumberedCheckpoint(10, GridPosition(4, 6)), NumberedCheckpoint(11, GridPosition(5, 5)), NumberedCheckpoint(12, GridPosition(4, 4)),
            NumberedCheckpoint(13, GridPosition(1, 7)), NumberedCheckpoint(14, GridPosition(1, 4)), NumberedCheckpoint(15, GridPosition(4, 1)), NumberedCheckpoint(16, GridPosition(0, 1)),
            NumberedCheckpoint(17, GridPosition(1, 2)), NumberedCheckpoint(18, GridPosition(2, 1)), NumberedCheckpoint(19, GridPosition(1, 8))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(7, 4), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(5, 5), GridPosition(6, 5)),
            BlockedEdge.between(GridPosition(9, 7), GridPosition(9, 8)),
            BlockedEdge.between(GridPosition(2, 9), GridPosition(3, 9)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0))
        ),
        difficultyMetadata = "World 5 - Level 168 (seed=5185)",
        seed = 5185L
    )

    fun buildSolution168(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4),
            GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1),
            GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 3), GridPosition(5, 3), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(2, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9),
            GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(1, 8)
        )
    )

    // --- Level 169: World 5 - Level 169 (10x10) ---

    fun buildLevel169(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl169",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 9)), NumberedCheckpoint(2, GridPosition(0, 1)), NumberedCheckpoint(3, GridPosition(1, 4)), NumberedCheckpoint(4, GridPosition(2, 1)),
            NumberedCheckpoint(5, GridPosition(8, 2)), NumberedCheckpoint(6, GridPosition(6, 2)), NumberedCheckpoint(7, GridPosition(4, 2)), NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(3, 5)), NumberedCheckpoint(10, GridPosition(7, 5)), NumberedCheckpoint(11, GridPosition(4, 6)), NumberedCheckpoint(12, GridPosition(6, 8)),
            NumberedCheckpoint(13, GridPosition(9, 4)), NumberedCheckpoint(14, GridPosition(7, 8)), NumberedCheckpoint(15, GridPosition(1, 6)), NumberedCheckpoint(16, GridPosition(2, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(0, 8)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(7, 5)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6))
        ),
        difficultyMetadata = "World 5 - Level 169 (seed=5176)",
        seed = 5176L
    )

    fun buildSolution169(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0),
            GridPosition(9, 1), GridPosition(8, 1), GridPosition(8, 2), GridPosition(9, 2), GridPosition(9, 3), GridPosition(8, 3), GridPosition(7, 3), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 7), GridPosition(5, 7), GridPosition(5, 8), GridPosition(6, 8), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7), GridPosition(3, 7), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5)
        )
    )

    // --- Level 170: World 5 - Level 170 (10x10) ---

    fun buildLevel170(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl170",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(3, 6)), NumberedCheckpoint(3, GridPosition(1, 2)), NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(2, 2)), NumberedCheckpoint(6, GridPosition(3, 2)), NumberedCheckpoint(7, GridPosition(1, 0)), NumberedCheckpoint(8, GridPosition(9, 3)),
            NumberedCheckpoint(9, GridPosition(7, 1)), NumberedCheckpoint(10, GridPosition(9, 7)), NumberedCheckpoint(11, GridPosition(6, 4)), NumberedCheckpoint(12, GridPosition(6, 1)),
            NumberedCheckpoint(13, GridPosition(5, 4)), NumberedCheckpoint(14, GridPosition(7, 7)), NumberedCheckpoint(15, GridPosition(6, 7)), NumberedCheckpoint(16, GridPosition(2, 7)),
            NumberedCheckpoint(17, GridPosition(2, 9)), NumberedCheckpoint(18, GridPosition(6, 9))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(8, 7), GridPosition(8, 8)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(0, 8)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7))
        ),
        difficultyMetadata = "World 5 - Level 170 (seed=5180)",
        seed = 5180L
    )

    fun buildSolution170(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(9, 3), GridPosition(8, 3), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(8, 4), GridPosition(9, 4), GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(7, 6), GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8),
            GridPosition(3, 7), GridPosition(2, 7), GridPosition(2, 8), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9),
            GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9)
        )
    )

    // --- Level 171: World 5 - Level 171 (10x10) ---

    fun buildLevel171(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl171",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 1)), NumberedCheckpoint(2, GridPosition(7, 2)), NumberedCheckpoint(3, GridPosition(4, 3)), NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(4, 1)), NumberedCheckpoint(6, GridPosition(2, 2)), NumberedCheckpoint(7, GridPosition(1, 5)), NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(0, 6)), NumberedCheckpoint(10, GridPosition(1, 9)), NumberedCheckpoint(11, GridPosition(3, 5)), NumberedCheckpoint(12, GridPosition(6, 6)),
            NumberedCheckpoint(13, GridPosition(3, 8)), NumberedCheckpoint(14, GridPosition(8, 9)), NumberedCheckpoint(15, GridPosition(5, 8)), NumberedCheckpoint(16, GridPosition(9, 2)),
            NumberedCheckpoint(17, GridPosition(9, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(8, 3)),
            BlockedEdge.between(GridPosition(7, 2), GridPosition(7, 3)),
            BlockedEdge.between(GridPosition(8, 0), GridPosition(8, 1)),
            BlockedEdge.between(GridPosition(0, 7), GridPosition(0, 8)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(8, 5)),
            BlockedEdge.between(GridPosition(8, 4), GridPosition(8, 5))
        ),
        difficultyMetadata = "World 5 - Level 171 (seed=5175)",
        seed = 5175L
    )

    fun buildSolution171(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(1, 8), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(2, 8), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(7, 5), GridPosition(7, 6),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(4, 7), GridPosition(4, 8), GridPosition(3, 8), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(5, 9), GridPosition(6, 9), GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(7, 8),
            GridPosition(6, 8), GridPosition(5, 8), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(9, 3), GridPosition(9, 2), GridPosition(9, 1), GridPosition(8, 1),
            GridPosition(7, 1), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0)
        )
    )

    // --- Level 172: World 5 - Level 172 (10x10) ---

    fun buildLevel172(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl172",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 6)), NumberedCheckpoint(2, GridPosition(4, 9)), NumberedCheckpoint(3, GridPosition(2, 6)), NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(1, 4)), NumberedCheckpoint(6, GridPosition(0, 1)), NumberedCheckpoint(7, GridPosition(7, 1)), NumberedCheckpoint(8, GridPosition(9, 3)),
            NumberedCheckpoint(9, GridPosition(8, 4)), NumberedCheckpoint(10, GridPosition(8, 2)), NumberedCheckpoint(11, GridPosition(5, 9)), NumberedCheckpoint(12, GridPosition(5, 6)),
            NumberedCheckpoint(13, GridPosition(5, 7)), NumberedCheckpoint(14, GridPosition(6, 5)), NumberedCheckpoint(15, GridPosition(2, 3)), NumberedCheckpoint(16, GridPosition(5, 4)),
            NumberedCheckpoint(17, GridPosition(4, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(3, 8)),
            BlockedEdge.between(GridPosition(7, 6), GridPosition(8, 6)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(6, 0)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(7, 7), GridPosition(7, 8))
        ),
        difficultyMetadata = "World 5 - Level 172 (seed=5172)",
        seed = 5172L
    )

    fun buildSolution172(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 6), GridPosition(0, 7), GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9),
            GridPosition(4, 8), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8), GridPosition(1, 7), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(8, 1), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4),
            GridPosition(9, 5), GridPosition(8, 5), GridPosition(8, 4), GridPosition(8, 3), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(7, 6), GridPosition(7, 7), GridPosition(8, 7), GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(9, 8),
            GridPosition(9, 9), GridPosition(8, 9), GridPosition(8, 8), GridPosition(7, 8), GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 6), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3)
        )
    )

    // --- Level 173: World 5 - Level 173 (10x10) ---

    fun buildLevel173(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl173",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)), NumberedCheckpoint(2, GridPosition(0, 5)), NumberedCheckpoint(3, GridPosition(2, 2)), NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(0, 1)), NumberedCheckpoint(6, GridPosition(5, 0)), NumberedCheckpoint(7, GridPosition(4, 4)), NumberedCheckpoint(8, GridPosition(8, 8)),
            NumberedCheckpoint(9, GridPosition(5, 9)), NumberedCheckpoint(10, GridPosition(7, 7)), NumberedCheckpoint(11, GridPosition(6, 6)), NumberedCheckpoint(12, GridPosition(4, 9)),
            NumberedCheckpoint(13, GridPosition(3, 7)), NumberedCheckpoint(14, GridPosition(0, 7)), NumberedCheckpoint(15, GridPosition(6, 3)), NumberedCheckpoint(16, GridPosition(8, 3)),
            NumberedCheckpoint(17, GridPosition(7, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 7), GridPosition(1, 8)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(4, 8), GridPosition(5, 8)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6))
        ),
        difficultyMetadata = "World 5 - Level 173 (seed=5173)",
        seed = 5173L
    )

    fun buildSolution173(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2), GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2), GridPosition(9, 3), GridPosition(9, 4), GridPosition(9, 5), GridPosition(8, 5),
            GridPosition(8, 6), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9),
            GridPosition(7, 9), GridPosition(6, 9), GridPosition(5, 9), GridPosition(5, 8), GridPosition(6, 8), GridPosition(7, 8), GridPosition(7, 7), GridPosition(6, 7),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 7), GridPosition(4, 7), GridPosition(4, 8), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9),
            GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(1, 8), GridPosition(2, 8), GridPosition(3, 8), GridPosition(3, 7), GridPosition(2, 7),
            GridPosition(1, 7), GridPosition(0, 7), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6)
        )
    )

    // --- Level 174: World 5 - Level 174 (10x10) ---

    fun buildLevel174(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl174",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(7, 7)), NumberedCheckpoint(2, GridPosition(8, 9)), NumberedCheckpoint(3, GridPosition(4, 7)), NumberedCheckpoint(4, GridPosition(0, 7)),
            NumberedCheckpoint(5, GridPosition(3, 4)), NumberedCheckpoint(6, GridPosition(2, 8)), NumberedCheckpoint(7, GridPosition(6, 5)), NumberedCheckpoint(8, GridPosition(5, 1)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(1, 1)), NumberedCheckpoint(11, GridPosition(1, 3)), NumberedCheckpoint(12, GridPosition(8, 1)),
            NumberedCheckpoint(13, GridPosition(6, 2)), NumberedCheckpoint(14, GridPosition(8, 3)), NumberedCheckpoint(15, GridPosition(7, 6)), NumberedCheckpoint(16, GridPosition(9, 5)),
            NumberedCheckpoint(17, GridPosition(8, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 9), GridPosition(6, 9)),
            BlockedEdge.between(GridPosition(4, 7), GridPosition(4, 8)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(8, 1), GridPosition(9, 1))
        ),
        difficultyMetadata = "World 5 - Level 174 (seed=5174)",
        seed = 5174L
    )

    fun buildSolution174(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(7, 7), GridPosition(7, 8), GridPosition(8, 8), GridPosition(9, 8), GridPosition(9, 9), GridPosition(8, 9), GridPosition(7, 9), GridPosition(6, 9),
            GridPosition(6, 8), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(3, 8), GridPosition(4, 8), GridPosition(5, 8),
            GridPosition(5, 9), GridPosition(4, 9), GridPosition(3, 9), GridPosition(2, 9), GridPosition(1, 9), GridPosition(0, 9), GridPosition(0, 8), GridPosition(0, 7),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(1, 7), GridPosition(1, 8), GridPosition(2, 8), GridPosition(2, 7), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0), GridPosition(8, 0), GridPosition(9, 0), GridPosition(9, 1), GridPosition(9, 2),
            GridPosition(8, 2), GridPosition(8, 1), GridPosition(7, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(7, 2), GridPosition(7, 3), GridPosition(8, 3),
            GridPosition(9, 3), GridPosition(9, 4), GridPosition(8, 4), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(8, 6), GridPosition(8, 5),
            GridPosition(9, 5), GridPosition(9, 6), GridPosition(9, 7), GridPosition(8, 7)
        )
    )

    // --- Level 175: World 5 - Level 175 (10x10) ---

    fun buildLevel175(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "w5_lvl175",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(10, 10),
        requiredCells = grid10x10Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)), NumberedCheckpoint(2, GridPosition(5, 8)), NumberedCheckpoint(3, GridPosition(4, 7)), NumberedCheckpoint(4, GridPosition(6, 9)),
            NumberedCheckpoint(5, GridPosition(8, 7)), NumberedCheckpoint(6, GridPosition(3, 1)), NumberedCheckpoint(7, GridPosition(6, 1)), NumberedCheckpoint(8, GridPosition(4, 2)),
            NumberedCheckpoint(9, GridPosition(3, 3)), NumberedCheckpoint(10, GridPosition(6, 4)), NumberedCheckpoint(11, GridPosition(7, 6)), NumberedCheckpoint(12, GridPosition(1, 7)),
            NumberedCheckpoint(13, GridPosition(1, 5)), NumberedCheckpoint(14, GridPosition(4, 5)), NumberedCheckpoint(15, GridPosition(1, 3)), NumberedCheckpoint(16, GridPosition(2, 3)),
            NumberedCheckpoint(17, GridPosition(2, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(6, 7), GridPosition(6, 8)),
            BlockedEdge.between(GridPosition(9, 5), GridPosition(9, 6)),
            BlockedEdge.between(GridPosition(9, 3), GridPosition(9, 4)),
            BlockedEdge.between(GridPosition(7, 0), GridPosition(8, 0)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6))
        ),
        difficultyMetadata = "World 5 - Level 175 (seed=5177)",
        seed = 5177L
    )

    fun buildSolution175(): PuzzlePath = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(0, 8), GridPosition(0, 9), GridPosition(1, 9), GridPosition(2, 9), GridPosition(3, 9), GridPosition(4, 9), GridPosition(5, 9), GridPosition(5, 8),
            GridPosition(4, 8), GridPosition(4, 7), GridPosition(5, 7), GridPosition(6, 7), GridPosition(7, 7), GridPosition(7, 8), GridPosition(6, 8), GridPosition(6, 9),
            GridPosition(7, 9), GridPosition(8, 9), GridPosition(9, 9), GridPosition(9, 8), GridPosition(8, 8), GridPosition(8, 7), GridPosition(9, 7), GridPosition(9, 6),
            GridPosition(8, 6), GridPosition(8, 5), GridPosition(9, 5), GridPosition(9, 4), GridPosition(8, 4), GridPosition(8, 3), GridPosition(9, 3), GridPosition(9, 2),
            GridPosition(9, 1), GridPosition(9, 0), GridPosition(8, 0), GridPosition(8, 1), GridPosition(8, 2), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(7, 3), GridPosition(7, 4), GridPosition(7, 5), GridPosition(7, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 7), GridPosition(3, 8), GridPosition(2, 8), GridPosition(1, 8),
            GridPosition(1, 7), GridPosition(2, 7), GridPosition(2, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[151] = buildLevel151()
        map[152] = buildLevel152()
        map[153] = buildLevel153()
        map[154] = buildLevel154()
        map[155] = buildLevel155()
        map[156] = buildLevel156()
        map[157] = buildLevel157()
        map[158] = buildLevel158()
        map[159] = buildLevel159()
        map[160] = buildLevel160()
        map[161] = buildLevel161()
        map[162] = buildLevel162()
        map[163] = buildLevel163()
        map[164] = buildLevel164()
        map[165] = buildLevel165()
        map[166] = buildLevel166()
        map[167] = buildLevel167()
        map[168] = buildLevel168()
        map[169] = buildLevel169()
        map[170] = buildLevel170()
        map[171] = buildLevel171()
        map[172] = buildLevel172()
        map[173] = buildLevel173()
        map[174] = buildLevel174()
        map[175] = buildLevel175()
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[151] = buildSolution151()
        map[152] = buildSolution152()
        map[153] = buildSolution153()
        map[154] = buildSolution154()
        map[155] = buildSolution155()
        map[156] = buildSolution156()
        map[157] = buildSolution157()
        map[158] = buildSolution158()
        map[159] = buildSolution159()
        map[160] = buildSolution160()
        map[161] = buildSolution161()
        map[162] = buildSolution162()
        map[163] = buildSolution163()
        map[164] = buildSolution164()
        map[165] = buildSolution165()
        map[166] = buildSolution166()
        map[167] = buildSolution167()
        map[168] = buildSolution168()
        map[169] = buildSolution169()
        map[170] = buildSolution170()
        map[171] = buildSolution171()
        map[172] = buildSolution172()
        map[173] = buildSolution173()
        map[174] = buildSolution174()
        map[175] = buildSolution175()
        map
    }
}
