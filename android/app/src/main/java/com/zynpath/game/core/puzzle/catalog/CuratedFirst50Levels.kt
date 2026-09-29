package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Authoritative curated level definitions and verified canonical solutions for Solo Levels 1–50.
 *
 * Implements Prompt 28:
 * - Levels 1–5: Learn the path (4x4 introductory serpentine & column sweeps)
 * - Levels 6–10: Connect the numbers (4x4 spirals & perimeter routing)
 * - Levels 11–15: Plan your turns (4x4 quadrant boxes & S-curves; Level 13 Midpoint)
 * - Levels 16–20: Fill every cell (4x4 cul-de-sac & coverage mastery)
 * - Levels 21–25: First Steps milestone (5x5 expansion; Level 25 Chapter 1 Climax)
 * - Levels 26–30: Smarter routes (5x5 deliberate turns & perimeter sweeps)
 * - Levels 31–35: Multiple clue segments (5x5 7-clue multi-segment connections)
 * - Levels 36–40: Longer path planning (5x5 long-horizon routes; Level 38 Midpoint)
 * - Levels 41–45: Strategic connections (5x5 complex routing; Level 42 Recovery)
 * - Levels 46–50: Smart Turns milestone (5x5 dense turns; Level 50 Chapter 2 Climax)
 *
 * Invariants:
 * 1. Strict ascending checkpoint sequence (1 -> 2 -> ... -> N).
 * 2. Exactly one continuous, non-branching path covering 100% of playable cells.
 * 3. Strictly Manhattan-orthogonal movements with zero diagonal steps or wall crossings.
 */
object CuratedFirst50Levels {

    private fun grid4x4Cells(): Set<GridPosition> =
        (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid5x5Cells(): Set<GridPosition> =
        (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()

    // --- Levels 1–5: Learn the path (Preserved from PackagedPuzzles) ---

    val LEVEL_1 = PackagedPuzzles.LEVEL_1
    val SOLUTION_1 = PackagedPuzzles.SOLUTION_1_ROUTE

    val LEVEL_2 = PackagedPuzzles.LEVEL_2
    val SOLUTION_2 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3)
        )
    )

    val LEVEL_3 = PackagedPuzzles.LEVEL_3
    val SOLUTION_3 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
    )

    val LEVEL_4 = PackagedPuzzles.LEVEL_4
    val SOLUTION_4 = PuzzlePath(
        listOf(
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3)
        )
    )

    val LEVEL_5 = PackagedPuzzles.LEVEL_5
    val SOLUTION_5 = PuzzlePath(
        listOf(
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3)
        )
    )

    // --- Levels 6–10: Connect the numbers (Spirals & Perimeter flow) ---

    val SOLUTION_6 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1)
        )
    )
    val LEVEL_6 = PuzzleDefinition(
        puzzleId = "w1_lvl6",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(2, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1006L
    )

    val SOLUTION_7 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2)
        )
    )
    val LEVEL_7 = PuzzleDefinition(
        puzzleId = "w1_lvl7",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(3, 0)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(0, 3)),
            NumberedCheckpoint(5, GridPosition(1, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1007L
    )

    val SOLUTION_8 = PuzzlePath(
        listOf(
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0)
        )
    )
    val LEVEL_8 = PuzzleDefinition(
        puzzleId = "w1_lvl8",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 3)),
            NumberedCheckpoint(2, GridPosition(3, 3)),
            NumberedCheckpoint(3, GridPosition(0, 2)),
            NumberedCheckpoint(4, GridPosition(3, 1)),
            NumberedCheckpoint(5, GridPosition(0, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1008L
    )

    val SOLUTION_9 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0)
        )
    )
    val LEVEL_9 = PuzzleDefinition(
        puzzleId = "w1_lvl9",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 3)),
            NumberedCheckpoint(3, GridPosition(2, 3)),
            NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(1, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1009L
    )

    val SOLUTION_10 = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0)
        )
    )
    val LEVEL_10 = PuzzleDefinition(
        puzzleId = "w1_lvl10",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)),
            NumberedCheckpoint(2, GridPosition(2, 1)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(0, 3)),
            NumberedCheckpoint(5, GridPosition(0, 0)),
            NumberedCheckpoint(6, GridPosition(3, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "BEGINNER",
        seed = 1010L
    )

    // --- Levels 11–15: Plan your turns (4-Quadrant boxes & S-turns) ---

    val SOLUTION_11 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2)
        )
    )
    val LEVEL_11 = PuzzleDefinition(
        puzzleId = "w1_lvl11",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 0)),
            NumberedCheckpoint(3, GridPosition(2, 1)),
            NumberedCheckpoint(4, GridPosition(2, 3)),
            NumberedCheckpoint(5, GridPosition(1, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1011L
    )

    val SOLUTION_12 = PuzzlePath(
        listOf(
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(2, 2),
            GridPosition(1, 2), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1)
        )
    )
    val LEVEL_12 = PuzzleDefinition(
        puzzleId = "w1_lvl12",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 0)),
            NumberedCheckpoint(2, GridPosition(3, 1)),
            NumberedCheckpoint(3, GridPosition(2, 2)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(1, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1012L
    )

    val SOLUTION_13 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3)
        )
    )
    val LEVEL_13 = PuzzleDefinition(
        puzzleId = "w1_lvl13",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(3, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(3, 2)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(0, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1013L
    )

    val SOLUTION_14 = PuzzlePath(
        listOf(
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0)
        )
    )
    val LEVEL_14 = PuzzleDefinition(
        puzzleId = "w1_lvl14",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 0)),
            NumberedCheckpoint(2, GridPosition(3, 3)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(1, 3)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(0, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1014L
    )

    val SOLUTION_15 = PuzzlePath(
        listOf(
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0),
            GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_15 = PuzzleDefinition(
        puzzleId = "w1_lvl15",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 3)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(3, 0)),
            NumberedCheckpoint(4, GridPosition(3, 3)),
            NumberedCheckpoint(5, GridPosition(1, 2)),
            NumberedCheckpoint(6, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1015L
    )

    // --- Levels 16–20: Fill every cell (Cul-de-sac & complete coverage mastery) ---

    val SOLUTION_16 = PuzzlePath(
        listOf(
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2),
            GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1), GridPosition(1, 1)
        )
    )
    val LEVEL_16 = PuzzleDefinition(
        puzzleId = "w1_lvl16",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 1)),
            NumberedCheckpoint(2, GridPosition(3, 0)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(1, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1016L
    )

    val SOLUTION_17 = PuzzlePath(
        listOf(
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2)
        )
    )
    val LEVEL_17 = PuzzleDefinition(
        puzzleId = "w1_lvl17",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 1)),
            NumberedCheckpoint(2, GridPosition(0, 3)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(1, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1017L
    )

    val SOLUTION_18 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
            GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
        )
    )
    val LEVEL_18 = PuzzleDefinition(
        puzzleId = "w1_lvl18",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)),
            NumberedCheckpoint(2, GridPosition(1, 3)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(2, 1)),
            NumberedCheckpoint(5, GridPosition(0, 1))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1018L
    )

    val SOLUTION_19 = PuzzlePath(
        listOf(
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0)
        )
    )
    val LEVEL_19 = PuzzleDefinition(
        puzzleId = "w1_lvl19",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(3, 1)),
            NumberedCheckpoint(2, GridPosition(1, 2)),
            NumberedCheckpoint(3, GridPosition(3, 3)),
            NumberedCheckpoint(4, GridPosition(0, 3)),
            NumberedCheckpoint(5, GridPosition(0, 0)),
            NumberedCheckpoint(6, GridPosition(3, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1019L
    )

    val SOLUTION_20 = PuzzlePath(
        listOf(
            GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2),
            GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3)
        )
    )
    val LEVEL_20 = PuzzleDefinition(
        puzzleId = "w1_lvl20",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(4, 4),
        requiredCells = grid4x4Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(1, 2)),
            NumberedCheckpoint(2, GridPosition(2, 2)),
            NumberedCheckpoint(3, GridPosition(0, 3)),
            NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(3, 3))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 1020L
    )

    // --- Levels 21–25: First Steps milestone (5x5 expansion; Level 25 Chapter 1 Climax) ---

    val LEVEL_21 = PackagedPuzzles.LEVEL_21
    val SOLUTION_21 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4)
        )
    )

    val LEVEL_22 = PackagedPuzzles.LEVEL_22
    val SOLUTION_22 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4)
        )
    )

    val LEVEL_23 = PackagedPuzzles.LEVEL_23
    val SOLUTION_23 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4)
        )
    )

    val SOLUTION_24 = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0)
        )
    )
    val LEVEL_24 = PuzzleDefinition(
        puzzleId = "w2_lvl24",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 3)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(4, 1)),
            NumberedCheckpoint(6, GridPosition(0, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 2024L
    )

    // Level 25: Chapter 1 Climax Milestone
    val SOLUTION_25 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(3, 1),
            GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_25 = PuzzleDefinition(
        puzzleId = "w2_lvl25",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(3, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "EASY",
        seed = 2025L
    )

    // --- Levels 26–30: Smarter routes (5x5 deliberate turns) ---

    val SOLUTION_26 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(1, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_26 = PuzzleDefinition(
        puzzleId = "w2_lvl26",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2026L
    )

    val SOLUTION_27 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0)
        )
    )
    val LEVEL_27 = PuzzleDefinition(
        puzzleId = "w2_lvl27",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(0, 3)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(0, 1)),
            NumberedCheckpoint(6, GridPosition(4, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2027L
    )

    val SOLUTION_28 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4)
        )
    )
    val LEVEL_28 = PuzzleDefinition(
        puzzleId = "w2_lvl28",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 2)),
            NumberedCheckpoint(4, GridPosition(2, 0)),
            NumberedCheckpoint(5, GridPosition(2, 4)),
            NumberedCheckpoint(6, GridPosition(3, 1)),
            NumberedCheckpoint(7, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2028L
    )

    val SOLUTION_29 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_29 = PuzzleDefinition(
        puzzleId = "w2_lvl29",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(0, 4)),
            NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2029L
    )

    val SOLUTION_30 = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1),
            GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(2, 2)
        )
    )
    val LEVEL_30 = PuzzleDefinition(
        puzzleId = "w2_lvl30",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(0, 0)),
            NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2030L
    )

    // --- Levels 31–35: Multiple clue segments (7 checkpoints) ---

    val SOLUTION_31 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4)
        )
    )
    val LEVEL_31 = PuzzleDefinition(
        puzzleId = "w2_lvl31",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(3, 4)),
            NumberedCheckpoint(7, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2031L
    )

    val SOLUTION_32 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4)
        )
    )
    val LEVEL_32 = PuzzleDefinition(
        puzzleId = "w2_lvl32",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(3, 2)),
            NumberedCheckpoint(4, GridPosition(2, 0)),
            NumberedCheckpoint(5, GridPosition(2, 4)),
            NumberedCheckpoint(6, GridPosition(1, 1)),
            NumberedCheckpoint(7, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2032L
    )

    val SOLUTION_33 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0),
            GridPosition(1, 0), GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3), GridPosition(1, 4),
            GridPosition(2, 4), GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1), GridPosition(2, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(3, 4),
            GridPosition(4, 4), GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0)
        )
    )
    val LEVEL_33 = PuzzleDefinition(
        puzzleId = "w2_lvl33",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(1, 4)),
            NumberedCheckpoint(4, GridPosition(2, 0)),
            NumberedCheckpoint(5, GridPosition(3, 4)),
            NumberedCheckpoint(6, GridPosition(4, 3)),
            NumberedCheckpoint(7, GridPosition(4, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2033L
    )

    val SOLUTION_34 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(3, 1),
            GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_34 = PuzzleDefinition(
        puzzleId = "w2_lvl34",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(3, 2)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2034L
    )

    val SOLUTION_35 = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0)
        )
    )
    val LEVEL_35 = PuzzleDefinition(
        puzzleId = "w2_lvl35",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(2, 3)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(4, 0)),
            NumberedCheckpoint(7, GridPosition(0, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2035L
    )

    // --- Levels 36–40: Longer path planning (Level 38 Midpoint) ---

    val SOLUTION_36 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4)
        )
    )
    val LEVEL_36 = PuzzleDefinition(
        puzzleId = "w2_lvl36",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(4, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2036L
    )

    val SOLUTION_37 = PuzzlePath(
        listOf(
            GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(4, 3), GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4)
        )
    )
    val LEVEL_37 = PuzzleDefinition(
        puzzleId = "w2_lvl37",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 2)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(3, 1)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(4, 3)),
            NumberedCheckpoint(6, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2037L
    )

    // Level 38: Chapter 2 Midpoint Milestone
    val SOLUTION_38 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1),
            GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3),
            GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4)
        )
    )
    val LEVEL_38 = PuzzleDefinition(
        puzzleId = "w2_lvl38",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(2, 2)),
            NumberedCheckpoint(6, GridPosition(2, 3)),
            NumberedCheckpoint(7, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2038L
    )

    val SOLUTION_39 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(4, 1),
            GridPosition(4, 2), GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
            GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3),
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4)
        )
    )
    val LEVEL_39 = PuzzleDefinition(
        puzzleId = "w2_lvl39",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(4, 1)),
            NumberedCheckpoint(4, GridPosition(0, 2)),
            NumberedCheckpoint(5, GridPosition(4, 3)),
            NumberedCheckpoint(6, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2039L
    )

    val SOLUTION_40 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2)
        )
    )
    val LEVEL_40 = PuzzleDefinition(
        puzzleId = "w2_lvl40",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(0, 3)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(4, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2040L
    )

    // --- Levels 41–45: Strategic connections (Level 42 Recovery) ---

    val SOLUTION_41 = PuzzlePath(
        listOf(
            GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1),
            GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_41 = PuzzleDefinition(
        puzzleId = "w2_lvl41",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(1, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2041L
    )

    val SOLUTION_42 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4),
            GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4)
        )
    )
    val LEVEL_42 = PuzzleDefinition(
        puzzleId = "w2_lvl42",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 2)),
            NumberedCheckpoint(5, GridPosition(2, 4)),
            NumberedCheckpoint(6, GridPosition(3, 0)),
            NumberedCheckpoint(7, GridPosition(4, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2042L
    )

    val SOLUTION_43 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0),
            GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1),
            GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_43 = PuzzleDefinition(
        puzzleId = "w2_lvl43",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(0, 0)),
            NumberedCheckpoint(4, GridPosition(3, 0)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(1, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2043L
    )

    val SOLUTION_44 = PuzzlePath(
        listOf(
            GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(1, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_44 = PuzzleDefinition(
        puzzleId = "w2_lvl44",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 2)),
            NumberedCheckpoint(2, GridPosition(4, 4)),
            NumberedCheckpoint(3, GridPosition(4, 0)),
            NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(3, 1)),
            NumberedCheckpoint(6, GridPosition(1, 3)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2044L
    )

    val SOLUTION_45 = PuzzlePath(
        listOf(
            GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4)
        )
    )
    val LEVEL_45 = PuzzleDefinition(
        puzzleId = "w2_lvl45",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(2, 2)),
            NumberedCheckpoint(2, GridPosition(3, 1)),
            NumberedCheckpoint(3, GridPosition(0, 3)),
            NumberedCheckpoint(4, GridPosition(0, 0)),
            NumberedCheckpoint(5, GridPosition(4, 0)),
            NumberedCheckpoint(6, GridPosition(4, 4)),
            NumberedCheckpoint(7, GridPosition(0, 4))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2045L
    )

    // --- Levels 46–50: Smart Turns milestone (Level 50 Chapter 2 Climax) ---

    val SOLUTION_46 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
            GridPosition(1, 1), GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(3, 1),
            GridPosition(2, 1), GridPosition(2, 2)
        )
    )
    val LEVEL_46 = PuzzleDefinition(
        puzzleId = "w2_lvl46",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(4, 0)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(0, 4)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(3, 3)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2046L
    )

    val SOLUTION_47 = PuzzlePath(
        listOf(
            GridPosition(4, 4), GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4), GridPosition(0, 4),
            GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0),
            GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(1, 2), GridPosition(1, 1),
            GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_47 = PuzzleDefinition(
        puzzleId = "w2_lvl47",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 4)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(0, 0)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(1, 3)),
            NumberedCheckpoint(6, GridPosition(3, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2047L
    )

    val SOLUTION_48 = PuzzlePath(
        listOf(
            GridPosition(4, 0), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
            GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(1, 3),
            GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_48 = PuzzleDefinition(
        puzzleId = "w2_lvl48",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(4, 0)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(0, 4)),
            NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(3, 3)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2048L
    )

    val SOLUTION_49 = PuzzlePath(
        listOf(
            GridPosition(0, 4), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1), GridPosition(0, 0),
            GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4),
            GridPosition(3, 4), GridPosition(2, 4), GridPosition(1, 4),
            GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
            GridPosition(3, 2), GridPosition(3, 1),
            GridPosition(2, 1), GridPosition(1, 1),
            GridPosition(1, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_49 = PuzzleDefinition(
        puzzleId = "w2_lvl49",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 4)),
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(4, 0)),
            NumberedCheckpoint(4, GridPosition(4, 4)),
            NumberedCheckpoint(5, GridPosition(3, 3)),
            NumberedCheckpoint(6, GridPosition(1, 1)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2049L
    )

    // Level 50: Chapter 2 Climax Milestone (8 checkpoints, grand finish)
    val SOLUTION_50 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4),
            GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4),
            GridPosition(4, 3), GridPosition(4, 2), GridPosition(4, 1), GridPosition(4, 0),
            GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
            GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
            GridPosition(3, 2), GridPosition(3, 3),
            GridPosition(2, 3), GridPosition(1, 3),
            GridPosition(1, 2), GridPosition(2, 2)
        )
    )
    val LEVEL_50 = PuzzleDefinition(
        puzzleId = "w2_lvl50",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(1, 1)),
            NumberedCheckpoint(6, GridPosition(1, 3)),
            NumberedCheckpoint(7, GridPosition(2, 2))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "CASUAL",
        seed = 2050L
    )

    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        mapOf(
            1 to LEVEL_1, 2 to LEVEL_2, 3 to LEVEL_3, 4 to LEVEL_4, 5 to LEVEL_5,
            6 to LEVEL_6, 7 to LEVEL_7, 8 to LEVEL_8, 9 to LEVEL_9, 10 to LEVEL_10,
            11 to LEVEL_11, 12 to LEVEL_12, 13 to LEVEL_13, 14 to LEVEL_14, 15 to LEVEL_15,
            16 to LEVEL_16, 17 to LEVEL_17, 18 to LEVEL_18, 19 to LEVEL_19, 20 to LEVEL_20,
            21 to LEVEL_21, 22 to LEVEL_22, 23 to LEVEL_23, 24 to LEVEL_24, 25 to LEVEL_25,
            26 to LEVEL_26, 27 to LEVEL_27, 28 to LEVEL_28, 29 to LEVEL_29, 30 to LEVEL_30,
            31 to LEVEL_31, 32 to LEVEL_32, 33 to LEVEL_33, 34 to LEVEL_34, 35 to LEVEL_35,
            36 to LEVEL_36, 37 to LEVEL_37, 38 to LEVEL_38, 39 to LEVEL_39, 40 to LEVEL_40,
            41 to LEVEL_41, 42 to LEVEL_42, 43 to LEVEL_43, 44 to LEVEL_44, 45 to LEVEL_45,
            46 to LEVEL_46, 47 to LEVEL_47, 48 to LEVEL_48, 49 to LEVEL_49, 50 to LEVEL_50
        )
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        mapOf(
            1 to SOLUTION_1, 2 to SOLUTION_2, 3 to SOLUTION_3, 4 to SOLUTION_4, 5 to SOLUTION_5,
            6 to SOLUTION_6, 7 to SOLUTION_7, 8 to SOLUTION_8, 9 to SOLUTION_9, 10 to SOLUTION_10,
            11 to SOLUTION_11, 12 to SOLUTION_12, 13 to SOLUTION_13, 14 to SOLUTION_14, 15 to SOLUTION_15,
            16 to SOLUTION_16, 17 to SOLUTION_17, 18 to SOLUTION_18, 19 to SOLUTION_19, 20 to SOLUTION_20,
            21 to SOLUTION_21, 22 to SOLUTION_22, 23 to SOLUTION_23, 24 to SOLUTION_24, 25 to SOLUTION_25,
            26 to SOLUTION_26, 27 to SOLUTION_27, 28 to SOLUTION_28, 29 to SOLUTION_29, 30 to SOLUTION_30,
            31 to SOLUTION_31, 32 to SOLUTION_32, 33 to SOLUTION_33, 34 to SOLUTION_34, 35 to SOLUTION_35,
            36 to SOLUTION_36, 37 to SOLUTION_37, 38 to SOLUTION_38, 39 to SOLUTION_39, 40 to SOLUTION_40,
            41 to SOLUTION_41, 42 to SOLUTION_42, 43 to SOLUTION_43, 44 to SOLUTION_44, 45 to SOLUTION_45,
            46 to SOLUTION_46, 47 to SOLUTION_47, 48 to SOLUTION_48, 49 to SOLUTION_49, 50 to SOLUTION_50
        )
    }
}
