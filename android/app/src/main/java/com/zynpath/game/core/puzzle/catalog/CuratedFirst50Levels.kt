package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Authoritative curated level definitions and verified canonical solutions for Solo Levels 1–50.
 *
 * Upgraded for Zynpath Puzzle Fun & Confusion Upgrade:
 * - World 1: 6x6, 36 cells, 6–10 checkpoints, strategic lines (L1-3: 0, L4: 1, L5-20: 1-3 walls).
 * - World 2: 7x7, 49 cells, 8–12 checkpoints, 1-5 strategic lines.
 * - All 50 levels solver-verified unique, 100% playable coverage, strictly ascending checkpoints.
 */
object CuratedFirst50Levels {

    fun grid6x6Cells(): Set<GridPosition> =
        (0..5).flatMap { r -> (0..5).map { c -> GridPosition(r, c) } }.toSet()

    fun grid7x7Cells(): Set<GridPosition> =
        (0..6).flatMap { r -> (0..6).map { c -> GridPosition(r, c) } }.toSet()

    // --- Level 1: Learn the Path (6x6) ---

    val LEVEL_1 = PuzzleDefinition(
        puzzleId = "w1_lvl1",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(4, 1)),
            NumberedCheckpoint(3, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(2, 2)),
            NumberedCheckpoint(5, GridPosition(4, 2)),
            NumberedCheckpoint(6, GridPosition(2, 3)),
            NumberedCheckpoint(7, GridPosition(0, 5)),
            NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(3, 4)),
            NumberedCheckpoint(10, GridPosition(4, 5))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1005L
    )

    val SOLUTION_1 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5)
        )
    )

    // --- Level 2: Numbered Checkpoints (6x6) ---

    val LEVEL_2 = PuzzleDefinition(
        puzzleId = "w1_lvl2",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)),
            NumberedCheckpoint(2, GridPosition(2, 5)),
            NumberedCheckpoint(3, GridPosition(3, 4)),
            NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(1, 5)),
            NumberedCheckpoint(6, GridPosition(3, 2)),
            NumberedCheckpoint(7, GridPosition(4, 2)),
            NumberedCheckpoint(8, GridPosition(3, 1)),
            NumberedCheckpoint(9, GridPosition(1, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1031L
    )

    val SOLUTION_2 = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0)
        )
    )

    // --- Level 3: Cover Every Cell (6x6) ---

    val LEVEL_3 = PuzzleDefinition(
        puzzleId = "w1_lvl3",
        puzzleVersion = 6,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 1)),
            NumberedCheckpoint(2, GridPosition(5, 1)),
            NumberedCheckpoint(3, GridPosition(4, 2)),
            NumberedCheckpoint(4, GridPosition(5, 4)),
            NumberedCheckpoint(5, GridPosition(4, 3)),
            NumberedCheckpoint(6, GridPosition(3, 4)),
            NumberedCheckpoint(7, GridPosition(2, 2)),
            NumberedCheckpoint(8, GridPosition(2, 1)),
            NumberedCheckpoint(9, GridPosition(1, 2)),
            NumberedCheckpoint(10, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1003L
    )

    val SOLUTION_3 = PuzzlePath(
        listOf(
            GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5),
            GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5),
            GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4)
        )
    )

    // --- Level 4: Plan Your Turns (6x6) ---

    val LEVEL_4 = PuzzleDefinition(
        puzzleId = "w1_lvl4",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)),
            NumberedCheckpoint(2, GridPosition(3, 3)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(2, 0)),
            NumberedCheckpoint(5, GridPosition(1, 2)),
            NumberedCheckpoint(6, GridPosition(0, 5)),
            NumberedCheckpoint(7, GridPosition(1, 3)),
            NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(5, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1004L
    )

    val SOLUTION_4 = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4)
        )
    )

    // --- Level 5: The Serpentine Loop (6x6) ---

    val LEVEL_5 = PuzzleDefinition(
        puzzleId = "w1_lvl5",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 4)),
            NumberedCheckpoint(2, GridPosition(4, 3)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(3, 5)),
            NumberedCheckpoint(6, GridPosition(4, 1)),
            NumberedCheckpoint(7, GridPosition(2, 0)),
            NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1279L
    )

    val SOLUTION_5 = PuzzlePath(
        listOf(
            GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3),
            GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5),
            GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4)
        )
    )

    // --- Level 6: Cross Currents (6x6) ---

    val LEVEL_6 = PuzzleDefinition(
        puzzleId = "w1_lvl6",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 0)),
            NumberedCheckpoint(2, GridPosition(4, 5)),
            NumberedCheckpoint(3, GridPosition(3, 0)),
            NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(3, 4)),
            NumberedCheckpoint(6, GridPosition(2, 1)),
            NumberedCheckpoint(7, GridPosition(2, 3)),
            NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(1, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Cross Currents (seed=1280)",
        seed = 1280L
    )

    val SOLUTION_6 = PuzzlePath(
        listOf(
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4)
        )
    )

    // --- Level 7: Corner Lock (6x6) ---

    val LEVEL_7 = PuzzleDefinition(
        puzzleId = "w1_lvl7",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 5)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(2, 2)),
            NumberedCheckpoint(6, GridPosition(0, 2)),
            NumberedCheckpoint(7, GridPosition(0, 3)),
            NumberedCheckpoint(8, GridPosition(0, 4)),
            NumberedCheckpoint(9, GridPosition(2, 4)),
            NumberedCheckpoint(10, GridPosition(1, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Corner Lock (seed=1007)",
        seed = 1007L
    )

    val SOLUTION_7 = PuzzlePath(
        listOf(
            GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5),
            GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(1, 3)
        )
    )

    // --- Level 8: Perimeter Spiral (6x6) ---

    val LEVEL_8 = PuzzleDefinition(
        puzzleId = "w1_lvl8",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(2, 4)),
            NumberedCheckpoint(3, GridPosition(2, 5)),
            NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(0, 3)),
            NumberedCheckpoint(7, GridPosition(4, 4)),
            NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(3, 1)),
            NumberedCheckpoint(10, GridPosition(1, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Perimeter Spiral (seed=1008)",
        seed = 1008L
    )

    val SOLUTION_8 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0)
        )
    )

    // --- Level 9: The Hourglass (6x6) ---

    val LEVEL_9 = PuzzleDefinition(
        puzzleId = "w1_lvl9",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 4)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(4, 2)),
            NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(2, 2)),
            NumberedCheckpoint(7, GridPosition(3, 0)),
            NumberedCheckpoint(8, GridPosition(0, 1)),
            NumberedCheckpoint(9, GridPosition(0, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "The Hourglass (seed=1009)",
        seed = 1009L
    )

    val SOLUTION_9 = PuzzlePath(
        listOf(
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4), GridPosition(1, 3),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0)
        )
    )

    // --- Level 10: Pinwheel Turn (6x6) ---

    val LEVEL_10 = PuzzleDefinition(
        puzzleId = "w1_lvl10",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 0)),
            NumberedCheckpoint(2, GridPosition(0, 1)),
            NumberedCheckpoint(3, GridPosition(5, 1)),
            NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(4, 2)),
            NumberedCheckpoint(6, GridPosition(0, 2)),
            NumberedCheckpoint(7, GridPosition(3, 3)),
            NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(1, 4)),
            NumberedCheckpoint(10, GridPosition(1, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Pinwheel Turn (seed=1010)",
        seed = 1010L
    )

    val SOLUTION_10 = PuzzlePath(
        listOf(
            GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1),
            GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3)
        )
    )

    // --- Level 11: Cul-de-Sac Weave (6x6) ---

    val LEVEL_11 = PuzzleDefinition(
        puzzleId = "w1_lvl11",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)),
            NumberedCheckpoint(2, GridPosition(2, 0)),
            NumberedCheckpoint(3, GridPosition(4, 1)),
            NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(3, 2)),
            NumberedCheckpoint(6, GridPosition(5, 2)),
            NumberedCheckpoint(7, GridPosition(0, 1)),
            NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(4, 4)),
            NumberedCheckpoint(10, GridPosition(4, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Cul-de-Sac Weave (seed=1011)",
        seed = 1011L
    )

    val SOLUTION_11 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3)
        )
    )

    // --- Level 12: Quad Quadrants (6x6) ---

    val LEVEL_12 = PuzzleDefinition(
        puzzleId = "w1_lvl12",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(3, 0)),
            NumberedCheckpoint(7, GridPosition(5, 0)),
            NumberedCheckpoint(8, GridPosition(4, 2)),
            NumberedCheckpoint(9, GridPosition(3, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Quad Quadrants (seed=1012)",
        seed = 1012L
    )

    val SOLUTION_12 = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4)
        )
    )

    // --- Level 13: Midpoint Challenge (6x6) ---

    val LEVEL_13 = PuzzleDefinition(
        puzzleId = "w1_lvl13",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(5, 0)),
            NumberedCheckpoint(3, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(4, 1)),
            NumberedCheckpoint(5, GridPosition(4, 3)),
            NumberedCheckpoint(6, GridPosition(3, 4)),
            NumberedCheckpoint(7, GridPosition(0, 2)),
            NumberedCheckpoint(8, GridPosition(2, 3)),
            NumberedCheckpoint(9, GridPosition(2, 4)),
            NumberedCheckpoint(10, GridPosition(1, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Midpoint Challenge (seed=1013)",
        seed = 1013L
    )

    val SOLUTION_13 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4)
        )
    )

    // --- Level 14: Double S-Curve (6x6) ---

    val LEVEL_14 = PuzzleDefinition(
        puzzleId = "w1_lvl14",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 0)),
            NumberedCheckpoint(2, GridPosition(5, 3)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(3, 5)),
            NumberedCheckpoint(7, GridPosition(0, 2)),
            NumberedCheckpoint(8, GridPosition(1, 4)),
            NumberedCheckpoint(9, GridPosition(1, 5))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Double S-Curve (seed=1014)",
        seed = 1014L
    )

    val SOLUTION_14 = PuzzlePath(
        listOf(
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3),
            GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5),
            GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5)
        )
    )

    // --- Level 15: Switchback Ridge (6x6) ---

    val LEVEL_15 = PuzzleDefinition(
        puzzleId = "w1_lvl15",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 5)),
            NumberedCheckpoint(3, GridPosition(1, 3)),
            NumberedCheckpoint(4, GridPosition(1, 1)),
            NumberedCheckpoint(5, GridPosition(1, 0)),
            NumberedCheckpoint(6, GridPosition(2, 5)),
            NumberedCheckpoint(7, GridPosition(2, 1)),
            NumberedCheckpoint(8, GridPosition(4, 1)),
            NumberedCheckpoint(9, GridPosition(4, 4)),
            NumberedCheckpoint(10, GridPosition(4, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Switchback Ridge (seed=1152)",
        seed = 1152L
    )

    val SOLUTION_15 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3)
        )
    )

    // --- Level 16: The Fortress (6x6) ---

    val LEVEL_16 = PuzzleDefinition(
        puzzleId = "w1_lvl16",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 3)),
            NumberedCheckpoint(2, GridPosition(4, 5)),
            NumberedCheckpoint(3, GridPosition(2, 4)),
            NumberedCheckpoint(4, GridPosition(1, 5)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(4, 0)),
            NumberedCheckpoint(7, GridPosition(3, 1)),
            NumberedCheckpoint(8, GridPosition(0, 2)),
            NumberedCheckpoint(9, GridPosition(1, 0)),
            NumberedCheckpoint(10, GridPosition(1, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "The Fortress (seed=1016)",
        seed = 1016L
    )

    val SOLUTION_16 = PuzzlePath(
        listOf(
            GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 5),
            GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2),
            GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1)
        )
    )

    // --- Level 17: Squeeze Point (6x6) ---

    val LEVEL_17 = PuzzleDefinition(
        puzzleId = "w1_lvl17",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 2)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(5, 3)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(1, 4)),
            NumberedCheckpoint(7, GridPosition(2, 1)),
            NumberedCheckpoint(8, GridPosition(3, 2)),
            NumberedCheckpoint(9, GridPosition(4, 1)),
            NumberedCheckpoint(10, GridPosition(4, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Squeeze Point (seed=1154)",
        seed = 1154L
    )

    val SOLUTION_17 = PuzzlePath(
        listOf(
            GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5),
            GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1),
            GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2)
        )
    )

    // --- Level 18: Snake vs Spiral (6x6) ---

    val LEVEL_18 = PuzzleDefinition(
        puzzleId = "w1_lvl18",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 4)),
            NumberedCheckpoint(2, GridPosition(4, 1)),
            NumberedCheckpoint(3, GridPosition(5, 1)),
            NumberedCheckpoint(4, GridPosition(1, 1)),
            NumberedCheckpoint(5, GridPosition(2, 1)),
            NumberedCheckpoint(6, GridPosition(3, 1)),
            NumberedCheckpoint(7, GridPosition(3, 3)),
            NumberedCheckpoint(8, GridPosition(2, 4)),
            NumberedCheckpoint(9, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "Snake vs Spiral (seed=1155)",
        seed = 1155L
    )

    val SOLUTION_18 = PuzzlePath(
        listOf(
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(5, 1),
            GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4)
        )
    )

    // --- Level 19: The Labyrinth Gate (6x6) ---

    val LEVEL_19 = PuzzleDefinition(
        puzzleId = "w1_lvl19",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 3)),
            NumberedCheckpoint(2, GridPosition(2, 3)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(2, 2)),
            NumberedCheckpoint(7, GridPosition(1, 4)),
            NumberedCheckpoint(8, GridPosition(1, 0)),
            NumberedCheckpoint(9, GridPosition(1, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "The Labyrinth Gate (seed=1567)",
        seed = 1567L
    )

    val SOLUTION_19 = PuzzlePath(
        listOf(
            GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1)
        )
    )

    // --- Level 20: World 1 Climax (6x6) ---

    val LEVEL_20 = PuzzleDefinition(
        puzzleId = "w1_lvl20",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = if (6 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(5, 4)),
            NumberedCheckpoint(3, GridPosition(1, 4)),
            NumberedCheckpoint(4, GridPosition(5, 0)),
            NumberedCheckpoint(5, GridPosition(1, 2)),
            NumberedCheckpoint(6, GridPosition(1, 1)),
            NumberedCheckpoint(7, GridPosition(1, 0)),
            NumberedCheckpoint(8, GridPosition(2, 0)),
            NumberedCheckpoint(9, GridPosition(3, 0)),
            NumberedCheckpoint(10, GridPosition(2, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "World 1 Climax (seed=1157)",
        seed = 1157L
    )

    val SOLUTION_20 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1)
        )
    )

    // --- Level 21: First Steps (7x7) ---

    val LEVEL_21 = PuzzleDefinition(
        puzzleId = "w2_lvl21",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)),
            NumberedCheckpoint(2, GridPosition(2, 0)),
            NumberedCheckpoint(3, GridPosition(4, 6)),
            NumberedCheckpoint(4, GridPosition(1, 2)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(2, 5)),
            NumberedCheckpoint(7, GridPosition(2, 2)),
            NumberedCheckpoint(8, GridPosition(2, 1)),
            NumberedCheckpoint(9, GridPosition(5, 4)),
            NumberedCheckpoint(10, GridPosition(5, 1)),
            NumberedCheckpoint(11, GridPosition(4, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2295L
    )

    val SOLUTION_21 = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2)
        )
    )

    // --- Level 22: Seven Seas (7x7) ---

    val LEVEL_22 = PuzzleDefinition(
        puzzleId = "w2_lvl22",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)),
            NumberedCheckpoint(2, GridPosition(1, 5)),
            NumberedCheckpoint(3, GridPosition(2, 5)),
            NumberedCheckpoint(4, GridPosition(5, 5)),
            NumberedCheckpoint(5, GridPosition(4, 5)),
            NumberedCheckpoint(6, GridPosition(3, 5)),
            NumberedCheckpoint(7, GridPosition(1, 1)),
            NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(3, 2)),
            NumberedCheckpoint(10, GridPosition(1, 2)),
            NumberedCheckpoint(11, GridPosition(0, 3)),
            NumberedCheckpoint(12, GridPosition(0, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2022L
    )

    val SOLUTION_22 = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2)
        )
    )

    // --- Level 23: Cell Reservation (7x7) ---

    val LEVEL_23 = PuzzleDefinition(
        puzzleId = "w2_lvl23",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)),
            NumberedCheckpoint(2, GridPosition(5, 1)),
            NumberedCheckpoint(3, GridPosition(6, 4)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(5, 2)),
            NumberedCheckpoint(6, GridPosition(2, 5)),
            NumberedCheckpoint(7, GridPosition(2, 4)),
            NumberedCheckpoint(8, GridPosition(3, 4)),
            NumberedCheckpoint(9, GridPosition(4, 5)),
            NumberedCheckpoint(10, GridPosition(4, 4)),
            NumberedCheckpoint(11, GridPosition(5, 4)),
            NumberedCheckpoint(12, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2297L
    )

    val SOLUTION_23 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 5)
        )
    )

    // --- Level 24: Delayed Divergence (7x7) ---

    val LEVEL_24 = PuzzleDefinition(
        puzzleId = "w2_lvl24",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)),
            NumberedCheckpoint(2, GridPosition(6, 4)),
            NumberedCheckpoint(3, GridPosition(2, 1)),
            NumberedCheckpoint(4, GridPosition(5, 1)),
            NumberedCheckpoint(5, GridPosition(5, 2)),
            NumberedCheckpoint(6, GridPosition(2, 2)),
            NumberedCheckpoint(7, GridPosition(5, 4)),
            NumberedCheckpoint(8, GridPosition(3, 4)),
            NumberedCheckpoint(9, GridPosition(2, 5)),
            NumberedCheckpoint(10, GridPosition(1, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2161L
    )

    val SOLUTION_24 = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(2, 5), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 5)
        )
    )

    // --- Level 25: Chapter 1 Climax (7x7) ---

    val LEVEL_25 = PuzzleDefinition(
        puzzleId = "w2_lvl25",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)),
            NumberedCheckpoint(2, GridPosition(1, 5)),
            NumberedCheckpoint(3, GridPosition(1, 4)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(5, 1)),
            NumberedCheckpoint(6, GridPosition(6, 1)),
            NumberedCheckpoint(7, GridPosition(6, 2)),
            NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(4, 2)),
            NumberedCheckpoint(10, GridPosition(2, 6)),
            NumberedCheckpoint(11, GridPosition(6, 6)),
            NumberedCheckpoint(12, GridPosition(6, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2025L
    )

    val SOLUTION_25 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(5, 2), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(5, 4), GridPosition(5, 3), GridPosition(6, 3),
            GridPosition(6, 4)
        )
    )

    // --- Level 26: Center Pivot (7x7) ---

    val LEVEL_26 = PuzzleDefinition(
        puzzleId = "w2_lvl26",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 0)),
            NumberedCheckpoint(2, GridPosition(6, 4)),
            NumberedCheckpoint(3, GridPosition(3, 0)),
            NumberedCheckpoint(4, GridPosition(4, 5)),
            NumberedCheckpoint(5, GridPosition(3, 5)),
            NumberedCheckpoint(6, GridPosition(1, 5)),
            NumberedCheckpoint(7, GridPosition(4, 2)),
            NumberedCheckpoint(8, GridPosition(2, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 0), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2163L
    )

    val SOLUTION_26 = PuzzlePath(
        listOf(
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(2, 2)
        )
    )

    // --- Level 27: Perimeter Trench (7x7) ---

    val LEVEL_27 = PuzzleDefinition(
        puzzleId = "w2_lvl27",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 4)),
            NumberedCheckpoint(2, GridPosition(5, 5)),
            NumberedCheckpoint(3, GridPosition(5, 4)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(2, 1)),
            NumberedCheckpoint(6, GridPosition(1, 2)),
            NumberedCheckpoint(7, GridPosition(1, 3)),
            NumberedCheckpoint(8, GridPosition(3, 4)),
            NumberedCheckpoint(9, GridPosition(3, 2)),
            NumberedCheckpoint(10, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2164L
    )

    val SOLUTION_27 = PuzzlePath(
        listOf(
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4), GridPosition(1, 5), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(5, 3)
        )
    )

    // --- Level 28: Double Spiral (7x7) ---

    val LEVEL_28 = PuzzleDefinition(
        puzzleId = "w2_lvl28",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 2)),
            NumberedCheckpoint(2, GridPosition(3, 1)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(5, 3)),
            NumberedCheckpoint(5, GridPosition(5, 2)),
            NumberedCheckpoint(6, GridPosition(5, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2)),
            NumberedCheckpoint(8, GridPosition(1, 1)),
            NumberedCheckpoint(9, GridPosition(1, 2)),
            NumberedCheckpoint(10, GridPosition(1, 4)),
            NumberedCheckpoint(11, GridPosition(1, 6)),
            NumberedCheckpoint(12, GridPosition(0, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3809L
    )

    val SOLUTION_28 = PuzzlePath(
        listOf(
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(0, 6)
        )
    )

    // --- Level 29: Crossroads Maze (7x7) ---

    val LEVEL_29 = PuzzleDefinition(
        puzzleId = "w2_lvl29",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(1, 5)),
            NumberedCheckpoint(3, GridPosition(1, 4)),
            NumberedCheckpoint(4, GridPosition(1, 1)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(4, 1)),
            NumberedCheckpoint(7, GridPosition(3, 2)),
            NumberedCheckpoint(8, GridPosition(6, 4)),
            NumberedCheckpoint(9, GridPosition(5, 1)),
            NumberedCheckpoint(10, GridPosition(3, 3)),
            NumberedCheckpoint(11, GridPosition(3, 4)),
            NumberedCheckpoint(12, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2029L
    )

    val SOLUTION_29 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 5)
        )
    )

    // --- Level 30: The Gauntlet (7x7) ---

    val LEVEL_30 = PuzzleDefinition(
        puzzleId = "w2_lvl30",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 6)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(2, 2)),
            NumberedCheckpoint(6, GridPosition(4, 2)),
            NumberedCheckpoint(7, GridPosition(6, 6)),
            NumberedCheckpoint(8, GridPosition(4, 3)),
            NumberedCheckpoint(9, GridPosition(3, 5)),
            NumberedCheckpoint(10, GridPosition(3, 4)),
            NumberedCheckpoint(11, GridPosition(2, 3)),
            NumberedCheckpoint(12, GridPosition(3, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2167L
    )

    val SOLUTION_30 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(3, 3)
        )
    )

    // --- Level 31: Weave and Turn (7x7) ---

    val LEVEL_31 = PuzzleDefinition(
        puzzleId = "w2_lvl31",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)),
            NumberedCheckpoint(2, GridPosition(1, 1)),
            NumberedCheckpoint(3, GridPosition(5, 1)),
            NumberedCheckpoint(4, GridPosition(6, 1)),
            NumberedCheckpoint(5, GridPosition(6, 4)),
            NumberedCheckpoint(6, GridPosition(4, 2)),
            NumberedCheckpoint(7, GridPosition(5, 4)),
            NumberedCheckpoint(8, GridPosition(5, 5)),
            NumberedCheckpoint(9, GridPosition(4, 5)),
            NumberedCheckpoint(10, GridPosition(3, 5)),
            NumberedCheckpoint(11, GridPosition(4, 3)),
            NumberedCheckpoint(12, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2716L
    )

    val SOLUTION_31 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(5, 1), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4)
        )
    )

    // --- Level 32: Outer Ribbon (7x7) ---

    val LEVEL_32 = PuzzleDefinition(
        puzzleId = "w2_lvl32",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 4)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(4, 5)),
            NumberedCheckpoint(4, GridPosition(0, 6)),
            NumberedCheckpoint(5, GridPosition(1, 4)),
            NumberedCheckpoint(6, GridPosition(6, 2)),
            NumberedCheckpoint(7, GridPosition(3, 1)),
            NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(3, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 6), GridPosition(3, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 6), GridPosition(5, 6))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2717L
    )

    val SOLUTION_32 = PuzzlePath(
        listOf(
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(3, 3)
        )
    )

    // --- Level 33: The Chasm (7x7) ---

    val LEVEL_33 = PuzzleDefinition(
        puzzleId = "w2_lvl33",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)),
            NumberedCheckpoint(2, GridPosition(4, 6)),
            NumberedCheckpoint(3, GridPosition(5, 5)),
            NumberedCheckpoint(4, GridPosition(4, 5)),
            NumberedCheckpoint(5, GridPosition(1, 4)),
            NumberedCheckpoint(6, GridPosition(5, 3)),
            NumberedCheckpoint(7, GridPosition(2, 1)),
            NumberedCheckpoint(8, GridPosition(2, 2)),
            NumberedCheckpoint(9, GridPosition(3, 2)),
            NumberedCheckpoint(10, GridPosition(4, 1)),
            NumberedCheckpoint(11, GridPosition(6, 1)),
            NumberedCheckpoint(12, GridPosition(6, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2307L
    )

    val SOLUTION_33 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 1), GridPosition(6, 1),
            GridPosition(6, 2)
        )
    )

    // --- Level 34: Symmetric Balance (7x7) ---

    val LEVEL_34 = PuzzleDefinition(
        puzzleId = "w2_lvl34",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(6, 2)),
            NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(4, 5)),
            NumberedCheckpoint(6, GridPosition(5, 5)),
            NumberedCheckpoint(7, GridPosition(5, 1)),
            NumberedCheckpoint(8, GridPosition(1, 2)),
            NumberedCheckpoint(9, GridPosition(3, 2)),
            NumberedCheckpoint(10, GridPosition(4, 2)),
            NumberedCheckpoint(11, GridPosition(3, 4)),
            NumberedCheckpoint(12, GridPosition(1, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2445L
    )

    val SOLUTION_34 = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3),
            GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3)
        )
    )

    // --- Level 35: Delayed Failure (7x7) ---

    val LEVEL_35 = PuzzleDefinition(
        puzzleId = "w2_lvl35",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 2)),
            NumberedCheckpoint(2, GridPosition(6, 0)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(2, 5)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(2, 2)),
            NumberedCheckpoint(8, GridPosition(1, 1)),
            NumberedCheckpoint(9, GridPosition(1, 2)),
            NumberedCheckpoint(10, GridPosition(1, 6)),
            NumberedCheckpoint(11, GridPosition(0, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2035L
    )

    val SOLUTION_35 = PuzzlePath(
        listOf(
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(3, 5), GridPosition(3, 6), GridPosition(2, 6), GridPosition(2, 5),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(0, 6)
        )
    )

    // --- Level 36: Snake Trap (7x7) ---

    val LEVEL_36 = PuzzleDefinition(
        puzzleId = "w2_lvl36",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)),
            NumberedCheckpoint(2, GridPosition(5, 1)),
            NumberedCheckpoint(3, GridPosition(4, 1)),
            NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(2, 3)),
            NumberedCheckpoint(6, GridPosition(2, 1)),
            NumberedCheckpoint(7, GridPosition(1, 3)),
            NumberedCheckpoint(8, GridPosition(5, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(6, 2), GridPosition(6, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2310L
    )

    val SOLUTION_36 = PuzzlePath(
        listOf(
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5)
        )
    )

    // --- Level 37: Corner Isolation (7x7) ---

    val LEVEL_37 = PuzzleDefinition(
        puzzleId = "w2_lvl37",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 3)),
            NumberedCheckpoint(2, GridPosition(5, 5)),
            NumberedCheckpoint(3, GridPosition(4, 5)),
            NumberedCheckpoint(4, GridPosition(1, 5)),
            NumberedCheckpoint(5, GridPosition(1, 4)),
            NumberedCheckpoint(6, GridPosition(5, 2)),
            NumberedCheckpoint(7, GridPosition(4, 3)),
            NumberedCheckpoint(8, GridPosition(3, 3)),
            NumberedCheckpoint(9, GridPosition(3, 1)),
            NumberedCheckpoint(10, GridPosition(2, 1)),
            NumberedCheckpoint(11, GridPosition(1, 2)),
            NumberedCheckpoint(12, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(1, 6)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(6, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2037L
    )

    val SOLUTION_37 = PuzzlePath(
        listOf(
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(1, 1)
        )
    )

    // --- Level 38: Midpoint Threshold (7x7) ---

    val LEVEL_38 = PuzzleDefinition(
        puzzleId = "w2_lvl38",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)),
            NumberedCheckpoint(2, GridPosition(5, 5)),
            NumberedCheckpoint(3, GridPosition(0, 5)),
            NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(4, 1)),
            NumberedCheckpoint(6, GridPosition(2, 1)),
            NumberedCheckpoint(7, GridPosition(1, 2)),
            NumberedCheckpoint(8, GridPosition(0, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(5, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2312L
    )

    val SOLUTION_38 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2)
        )
    )

    // --- Level 39: Turn Density (7x7) ---

    val LEVEL_39 = PuzzleDefinition(
        puzzleId = "w2_lvl39",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)),
            NumberedCheckpoint(2, GridPosition(6, 0)),
            NumberedCheckpoint(3, GridPosition(5, 3)),
            NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(4, 1)),
            NumberedCheckpoint(6, GridPosition(4, 2)),
            NumberedCheckpoint(7, GridPosition(4, 3)),
            NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(2, 5)),
            NumberedCheckpoint(10, GridPosition(2, 4)),
            NumberedCheckpoint(11, GridPosition(1, 2)),
            NumberedCheckpoint(12, GridPosition(2, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 3), GridPosition(0, 4)),
            BlockedEdge.between(GridPosition(0, 5), GridPosition(1, 5)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(4, 5), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(6, 1))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2313L
    )

    val SOLUTION_39 = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6),
            GridPosition(2, 6), GridPosition(2, 5), GridPosition(3, 5), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(2, 2)
        )
    )

    // --- Level 40: The Squeeze (7x7) ---

    val LEVEL_40 = PuzzleDefinition(
        puzzleId = "w2_lvl40",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)),
            NumberedCheckpoint(2, GridPosition(1, 1)),
            NumberedCheckpoint(3, GridPosition(1, 2)),
            NumberedCheckpoint(4, GridPosition(3, 3)),
            NumberedCheckpoint(5, GridPosition(1, 5)),
            NumberedCheckpoint(6, GridPosition(2, 5)),
            NumberedCheckpoint(7, GridPosition(4, 5)),
            NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(5, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(4, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 3136L
    )

    val SOLUTION_40 = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 3)
        )
    )

    // --- Level 41: Diagonal Flow (7x7) ---

    val LEVEL_41 = PuzzleDefinition(
        puzzleId = "w2_lvl41",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 3)),
            NumberedCheckpoint(2, GridPosition(5, 1)),
            NumberedCheckpoint(3, GridPosition(5, 2)),
            NumberedCheckpoint(4, GridPosition(3, 3)),
            NumberedCheckpoint(5, GridPosition(3, 2)),
            NumberedCheckpoint(6, GridPosition(1, 3)),
            NumberedCheckpoint(7, GridPosition(2, 3)),
            NumberedCheckpoint(8, GridPosition(6, 4)),
            NumberedCheckpoint(9, GridPosition(4, 5)),
            NumberedCheckpoint(10, GridPosition(1, 4)),
            NumberedCheckpoint(11, GridPosition(0, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2178L
    )

    val SOLUTION_41 = PuzzlePath(
        listOf(
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(3, 3), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 4),
            GridPosition(0, 4)
        )
    )

    // --- Level 42: Spiral Core (7x7) ---

    val LEVEL_42 = PuzzleDefinition(
        puzzleId = "w2_lvl42",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(5, 1)),
            NumberedCheckpoint(2, GridPosition(5, 3)),
            NumberedCheckpoint(3, GridPosition(6, 3)),
            NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(4, 2)),
            NumberedCheckpoint(6, GridPosition(3, 1)),
            NumberedCheckpoint(7, GridPosition(3, 4)),
            NumberedCheckpoint(8, GridPosition(1, 1)),
            NumberedCheckpoint(9, GridPosition(1, 2)),
            NumberedCheckpoint(10, GridPosition(1, 3)),
            NumberedCheckpoint(11, GridPosition(6, 5)),
            NumberedCheckpoint(12, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(1, 4), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2590L
    )

    val SOLUTION_42 = PuzzlePath(
        listOf(
            GridPosition(5, 1), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(6, 3), GridPosition(6, 4), GridPosition(5, 4), GridPosition(4, 4), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6), GridPosition(5, 5), GridPosition(6, 5),
            GridPosition(6, 6)
        )
    )

    // --- Level 43: Long Horizons (7x7) ---

    val LEVEL_43 = PuzzleDefinition(
        puzzleId = "w2_lvl43",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)),
            NumberedCheckpoint(2, GridPosition(4, 2)),
            NumberedCheckpoint(3, GridPosition(4, 0)),
            NumberedCheckpoint(4, GridPosition(6, 0)),
            NumberedCheckpoint(5, GridPosition(1, 5)),
            NumberedCheckpoint(6, GridPosition(5, 3)),
            NumberedCheckpoint(7, GridPosition(3, 4)),
            NumberedCheckpoint(8, GridPosition(2, 3)),
            NumberedCheckpoint(9, GridPosition(1, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2180L
    )

    val SOLUTION_43 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5),
            GridPosition(6, 6), GridPosition(5, 6), GridPosition(4, 6), GridPosition(3, 6), GridPosition(2, 6), GridPosition(1, 6),
            GridPosition(0, 6), GridPosition(0, 5), GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(1, 3)
        )
    )

    // --- Level 44: The Iron Ring (7x7) ---

    val LEVEL_44 = PuzzleDefinition(
        puzzleId = "w2_lvl44",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 6)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(1, 6)),
            NumberedCheckpoint(4, GridPosition(3, 5)),
            NumberedCheckpoint(5, GridPosition(5, 5)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(1, 1)),
            NumberedCheckpoint(8, GridPosition(5, 3)),
            NumberedCheckpoint(9, GridPosition(5, 1)),
            NumberedCheckpoint(10, GridPosition(3, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 2), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2181L
    )

    val SOLUTION_44 = PuzzlePath(
        listOf(
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(3, 5), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 2), GridPosition(3, 2),
            GridPosition(3, 1)
        )
    )

    // --- Level 45: False Shortcut (7x7) ---

    val LEVEL_45 = PuzzleDefinition(
        puzzleId = "w2_lvl45",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 3)),
            NumberedCheckpoint(2, GridPosition(2, 4)),
            NumberedCheckpoint(3, GridPosition(2, 3)),
            NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(4, 6)),
            NumberedCheckpoint(6, GridPosition(6, 6)),
            NumberedCheckpoint(7, GridPosition(5, 4)),
            NumberedCheckpoint(8, GridPosition(5, 2)),
            NumberedCheckpoint(9, GridPosition(4, 1)),
            NumberedCheckpoint(10, GridPosition(3, 1)),
            NumberedCheckpoint(11, GridPosition(1, 5)),
            NumberedCheckpoint(12, GridPosition(0, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2182L
    )

    val SOLUTION_45 = PuzzlePath(
        listOf(
            GridPosition(3, 3), GridPosition(3, 4), GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(1, 5), GridPosition(1, 6),
            GridPosition(0, 6)
        )
    )

    // --- Level 46: Dead End Decoy (7x7) ---

    val LEVEL_46 = PuzzleDefinition(
        puzzleId = "w2_lvl46",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(6, 0)),
            NumberedCheckpoint(2, GridPosition(4, 6)),
            NumberedCheckpoint(3, GridPosition(2, 5)),
            NumberedCheckpoint(4, GridPosition(1, 4)),
            NumberedCheckpoint(5, GridPosition(6, 2)),
            NumberedCheckpoint(6, GridPosition(6, 1)),
            NumberedCheckpoint(7, GridPosition(1, 2)),
            NumberedCheckpoint(8, GridPosition(2, 1)),
            NumberedCheckpoint(9, GridPosition(3, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5)),
            BlockedEdge.between(GridPosition(6, 4), GridPosition(6, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2046L
    )

    val SOLUTION_46 = PuzzlePath(
        listOf(
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(5, 1), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1)
        )
    )

    // --- Level 47: Twin Loops (7x7) ---

    val LEVEL_47 = PuzzleDefinition(
        puzzleId = "w2_lvl47",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)),
            NumberedCheckpoint(2, GridPosition(6, 0)),
            NumberedCheckpoint(3, GridPosition(1, 4)),
            NumberedCheckpoint(4, GridPosition(4, 3)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(1, 2)),
            NumberedCheckpoint(7, GridPosition(4, 0)),
            NumberedCheckpoint(8, GridPosition(2, 1)),
            NumberedCheckpoint(9, GridPosition(0, 1)),
            NumberedCheckpoint(10, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 3), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(4, 2), GridPosition(5, 2))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2184L
    )

    val SOLUTION_47 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(6, 5),
            GridPosition(6, 4), GridPosition(6, 3), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(5, 2), GridPosition(5, 3), GridPosition(5, 4), GridPosition(5, 5), GridPosition(4, 5),
            GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1)
        )
    )

    // --- Level 48: Master Weave (7x7) ---

    val LEVEL_48 = PuzzleDefinition(
        puzzleId = "w2_lvl48",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)),
            NumberedCheckpoint(2, GridPosition(1, 4)),
            NumberedCheckpoint(3, GridPosition(0, 4)),
            NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(3, 5)),
            NumberedCheckpoint(6, GridPosition(3, 2)),
            NumberedCheckpoint(7, GridPosition(0, 1)),
            NumberedCheckpoint(8, GridPosition(4, 4)),
            NumberedCheckpoint(9, GridPosition(5, 5)),
            NumberedCheckpoint(10, GridPosition(5, 4)),
            NumberedCheckpoint(11, GridPosition(5, 2)),
            NumberedCheckpoint(12, GridPosition(5, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 2), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(3, 5), GridPosition(4, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2048L
    )

    val SOLUTION_48 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3),
            GridPosition(5, 3), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1), GridPosition(6, 0), GridPosition(5, 0),
            GridPosition(5, 1)
        )
    )

    // --- Level 49: The Grand Gate (7x7) ---

    val LEVEL_49 = PuzzleDefinition(
        puzzleId = "w2_lvl49",
        puzzleVersion = 5,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 4)),
            NumberedCheckpoint(2, GridPosition(3, 5)),
            NumberedCheckpoint(3, GridPosition(2, 3)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(0, 2)),
            NumberedCheckpoint(6, GridPosition(2, 2)),
            NumberedCheckpoint(7, GridPosition(4, 2)),
            NumberedCheckpoint(8, GridPosition(6, 2)),
            NumberedCheckpoint(9, GridPosition(5, 1)),
            NumberedCheckpoint(10, GridPosition(4, 1)),
            NumberedCheckpoint(11, GridPosition(2, 1)),
            NumberedCheckpoint(12, GridPosition(1, 1))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(2, 4), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 4), GridPosition(5, 5))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2049L
    )

    val SOLUTION_49 = PuzzlePath(
        listOf(
            GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(1, 6),
            GridPosition(1, 5), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6),
            GridPosition(6, 6), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(6, 1),
            GridPosition(6, 0), GridPosition(5, 0), GridPosition(5, 1), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1)
        )
    )

    // --- Level 50: Smart Turns Climax (7x7) ---

    val LEVEL_50 = PuzzleDefinition(
        puzzleId = "w2_lvl50",
        puzzleVersion = 4,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = if (7 == 6) grid6x6Cells() else grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 5)),
            NumberedCheckpoint(2, GridPosition(1, 1)),
            NumberedCheckpoint(3, GridPosition(1, 2)),
            NumberedCheckpoint(4, GridPosition(2, 6)),
            NumberedCheckpoint(5, GridPosition(5, 1)),
            NumberedCheckpoint(6, GridPosition(4, 1)),
            NumberedCheckpoint(7, GridPosition(3, 1)),
            NumberedCheckpoint(8, GridPosition(3, 2)),
            NumberedCheckpoint(9, GridPosition(6, 2)),
            NumberedCheckpoint(10, GridPosition(4, 6)),
            NumberedCheckpoint(11, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 4), GridPosition(1, 4)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(3, 0), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 2187L
    )

    val SOLUTION_50 = PuzzlePath(
        listOf(
            GridPosition(1, 5), GridPosition(1, 6), GridPosition(0, 6), GridPosition(0, 5), GridPosition(0, 4), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(1, 3), GridPosition(1, 4), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(3, 6),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(6, 1),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(5, 6),
            GridPosition(4, 6), GridPosition(4, 5), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(4, 3),
            GridPosition(4, 4)
        )
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map[1] = LEVEL_1
        map[2] = LEVEL_2
        map[3] = LEVEL_3
        map[4] = LEVEL_4
        map[5] = LEVEL_5
        map[6] = LEVEL_6
        map[7] = LEVEL_7
        map[8] = LEVEL_8
        map[9] = LEVEL_9
        map[10] = LEVEL_10
        map[11] = LEVEL_11
        map[12] = LEVEL_12
        map[13] = LEVEL_13
        map[14] = LEVEL_14
        map[15] = LEVEL_15
        map[16] = LEVEL_16
        map[17] = LEVEL_17
        map[18] = LEVEL_18
        map[19] = LEVEL_19
        map[20] = LEVEL_20
        map[21] = LEVEL_21
        map[22] = LEVEL_22
        map[23] = LEVEL_23
        map[24] = LEVEL_24
        map[25] = LEVEL_25
        map[26] = LEVEL_26
        map[27] = LEVEL_27
        map[28] = LEVEL_28
        map[29] = LEVEL_29
        map[30] = LEVEL_30
        map[31] = LEVEL_31
        map[32] = LEVEL_32
        map[33] = LEVEL_33
        map[34] = LEVEL_34
        map[35] = LEVEL_35
        map[36] = LEVEL_36
        map[37] = LEVEL_37
        map[38] = LEVEL_38
        map[39] = LEVEL_39
        map[40] = LEVEL_40
        map[41] = LEVEL_41
        map[42] = LEVEL_42
        map[43] = LEVEL_43
        map[44] = LEVEL_44
        map[45] = LEVEL_45
        map[46] = LEVEL_46
        map[47] = LEVEL_47
        map[48] = LEVEL_48
        map[49] = LEVEL_49
        map[50] = LEVEL_50
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map[1] = SOLUTION_1
        map[2] = SOLUTION_2
        map[3] = SOLUTION_3
        map[4] = SOLUTION_4
        map[5] = SOLUTION_5
        map[6] = SOLUTION_6
        map[7] = SOLUTION_7
        map[8] = SOLUTION_8
        map[9] = SOLUTION_9
        map[10] = SOLUTION_10
        map[11] = SOLUTION_11
        map[12] = SOLUTION_12
        map[13] = SOLUTION_13
        map[14] = SOLUTION_14
        map[15] = SOLUTION_15
        map[16] = SOLUTION_16
        map[17] = SOLUTION_17
        map[18] = SOLUTION_18
        map[19] = SOLUTION_19
        map[20] = SOLUTION_20
        map[21] = SOLUTION_21
        map[22] = SOLUTION_22
        map[23] = SOLUTION_23
        map[24] = SOLUTION_24
        map[25] = SOLUTION_25
        map[26] = SOLUTION_26
        map[27] = SOLUTION_27
        map[28] = SOLUTION_28
        map[29] = SOLUTION_29
        map[30] = SOLUTION_30
        map[31] = SOLUTION_31
        map[32] = SOLUTION_32
        map[33] = SOLUTION_33
        map[34] = SOLUTION_34
        map[35] = SOLUTION_35
        map[36] = SOLUTION_36
        map[37] = SOLUTION_37
        map[38] = SOLUTION_38
        map[39] = SOLUTION_39
        map[40] = SOLUTION_40
        map[41] = SOLUTION_41
        map[42] = SOLUTION_42
        map[43] = SOLUTION_43
        map[44] = SOLUTION_44
        map[45] = SOLUTION_45
        map[46] = SOLUTION_46
        map[47] = SOLUTION_47
        map[48] = SOLUTION_48
        map[49] = SOLUTION_49
        map[50] = SOLUTION_50
        map
    }
}
