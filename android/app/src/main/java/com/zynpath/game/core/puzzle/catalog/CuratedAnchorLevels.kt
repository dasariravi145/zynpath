package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Authoritative curated anchor definitions and verified canonical solutions for Solo Levels 51–300.
 *
 * Implements Prompt 29:
 * - Chapter 3 (Path Explorer 51–100): World 3 (5x5 with walls). Level 100 Chapter 3 Climax.
 * - Chapter 4 (Strategic Paths 101–150): World 4 (6x6 with walls). Level 101 Entry, Level 150 Chapter 4 Climax.
 * - Chapter 5 (Expert Journey 151–200): World 5 (7x7 with walls). Level 151 Entry, Level 200 Chapter 5 Climax.
 * - Chapter 6 (Master Trails 201–250): World 6 (8x8 with walls). Level 201 Entry, Level 250 Chapter 6 Climax.
 * - Chapter 7 (Grand Challenge 251–300): World 6 (8x8 with walls). Level 251 Entry, Level 300 Grand Finale.
 *
 * Invariants:
 * 1. Strict ascending checkpoint sequence (1 -> 2 -> ... -> N).
 * 2. Exactly one continuous, non-branching path covering 100% of playable cells.
 * 3. Strictly Manhattan-orthogonal movements with zero diagonal steps or wall crossings.
 * 4. Deterministic, immutable level definitions guaranteed across launches.
 */
object CuratedAnchorLevels {

    private fun grid5x5Cells(): Set<GridPosition> =
        (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid6x6Cells(): Set<GridPosition> =
        (0..5).flatMap { r -> (0..5).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid7x7Cells(): Set<GridPosition> =
        (0..6).flatMap { r -> (0..6).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid8x8Cells(): Set<GridPosition> =
        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()

    // =========================================================================
    // LEVEL 100 — CHAPTER 3 CLIMAX (Path Explorer Complete, 5x5, 4 walls, 7 clues)
    // =========================================================================

    val LEVEL_100 = PuzzleDefinition(
        puzzleId = "w3_lvl100",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 1)),
            NumberedCheckpoint(3, GridPosition(4, 2)),
            NumberedCheckpoint(4, GridPosition(0, 3)),
            NumberedCheckpoint(5, GridPosition(0, 4)),
            NumberedCheckpoint(6, GridPosition(2, 4)),
            NumberedCheckpoint(7, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
            BlockedEdge.between(GridPosition(4, 1), GridPosition(4, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4))
        ),
        difficultyMetadata = "BALANCED",
        seed = 3100L
    )

    val SOLUTION_100 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0),
            GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2),
            GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4)
        )
    )

    // =========================================================================
    // LEVEL 101 — CHAPTER 4 ENTRY (Strategic Paths Start, 6x6, 3 walls, 6 clues)
    // =========================================================================

    val LEVEL_101 = PuzzleDefinition(
        puzzleId = "w4_lvl101",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = grid6x6Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 5)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(3, 5)),
            NumberedCheckpoint(5, GridPosition(4, 5)),
            NumberedCheckpoint(6, GridPosition(5, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
            BlockedEdge.between(GridPosition(1, 5), GridPosition(2, 5)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0))
        ),
        difficultyMetadata = "STRATEGIC",
        seed = 4101L
    )

    val SOLUTION_101 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
            GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
            GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
            GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0)
        )
    )

    // =========================================================================
    // LEVEL 150 — CHAPTER 4 CLIMAX (Strategic Paths Complete, 6x6, 6 walls, 8 clues)
    // =========================================================================

    val LEVEL_150 = PuzzleDefinition(
        puzzleId = "w4_lvl150",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = grid6x6Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(5, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(5, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(5, 4)),
            NumberedCheckpoint(7, GridPosition(5, 5)),
            NumberedCheckpoint(8, GridPosition(0, 5))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
            BlockedEdge.between(GridPosition(5, 1), GridPosition(5, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(5, 3), GridPosition(5, 4)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(2, 2), GridPosition(2, 3))
        ),
        difficultyMetadata = "STRATEGIC",
        seed = 4150L
    )

    val SOLUTION_150 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0),
            GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2),
            GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4),
            GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5)
        )
    )

    // =========================================================================
    // LEVEL 151 — CHAPTER 5 ENTRY (Expert Journey Start, 7x7, 5 walls, 7 clues)
    // =========================================================================

    val LEVEL_151 = PuzzleDefinition(
        puzzleId = "w5_lvl151",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 6)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(4, 6)),
            NumberedCheckpoint(6, GridPosition(5, 0)),
            NumberedCheckpoint(7, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
            BlockedEdge.between(GridPosition(1, 6), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 6), GridPosition(4, 6)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0))
        ),
        difficultyMetadata = "ADVANCED",
        seed = 5151L
    )

    val SOLUTION_151 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6),
            GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6),
            GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6),
            GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6)
        )
    )

    // =========================================================================
    // LEVEL 200 — CHAPTER 5 CLIMAX (Expert Journey Complete, 7x7, 8 walls, 9 clues)
    // =========================================================================

    val LEVEL_200 = PuzzleDefinition(
        puzzleId = "w5_lvl200",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(7, 7),
        requiredCells = grid7x7Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(6, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(6, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(6, 4)),
            NumberedCheckpoint(7, GridPosition(0, 5)),
            NumberedCheckpoint(8, GridPosition(3, 6)),
            NumberedCheckpoint(9, GridPosition(6, 6))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
            BlockedEdge.between(GridPosition(6, 1), GridPosition(6, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(6, 3), GridPosition(6, 4)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(6, 5), GridPosition(6, 6)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(3, 5))
        ),
        difficultyMetadata = "ADVANCED",
        seed = 5200L
    )

    val SOLUTION_200 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0),
            GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2),
            GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4),
            GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6)
        )
    )

    // =========================================================================
    // LEVEL 201 — CHAPTER 6 ENTRY (Master Trails Start, 8x8, 8 walls, 8 clues)
    // =========================================================================

    val LEVEL_201 = PuzzleDefinition(
        puzzleId = "w6_lvl201",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 7)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(3, 7)),
            NumberedCheckpoint(5, GridPosition(4, 7)),
            NumberedCheckpoint(6, GridPosition(5, 0)),
            NumberedCheckpoint(7, GridPosition(6, 7)),
            NumberedCheckpoint(8, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(4, 3))
        ),
        difficultyMetadata = "EXPERT",
        seed = 6201L
    )

    val SOLUTION_201 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0)
        )
    )

    // =========================================================================
    // LEVEL 250 — CHAPTER 6 CLIMAX (Master Trails Complete, 8x8, 12 walls, 10 clues)
    // =========================================================================

    val LEVEL_250 = PuzzleDefinition(
        puzzleId = "w6_lvl250",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(7, 0)),
            NumberedCheckpoint(3, GridPosition(0, 1)),
            NumberedCheckpoint(4, GridPosition(7, 2)),
            NumberedCheckpoint(5, GridPosition(0, 3)),
            NumberedCheckpoint(6, GridPosition(7, 4)),
            NumberedCheckpoint(7, GridPosition(0, 5)),
            NumberedCheckpoint(8, GridPosition(7, 6)),
            NumberedCheckpoint(9, GridPosition(4, 7)),
            NumberedCheckpoint(10, GridPosition(0, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2)),
            BlockedEdge.between(GridPosition(4, 3), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(5, 0), GridPosition(5, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(2, 4))
        ),
        difficultyMetadata = "EXPERT",
        seed = 6250L
    )

    val SOLUTION_250 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7)
        )
    )

    // =========================================================================
    // LEVEL 251 — CHAPTER 7 ENTRY (Grand Challenge Start, 8x8, 10 walls, 9 clues)
    // =========================================================================

    val LEVEL_251 = PuzzleDefinition(
        puzzleId = "w6_lvl251",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 7)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 7)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 7)),
            NumberedCheckpoint(7, GridPosition(5, 0)),
            NumberedCheckpoint(8, GridPosition(6, 7)),
            NumberedCheckpoint(9, GridPosition(7, 0))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
            BlockedEdge.between(GridPosition(1, 7), GridPosition(2, 7)),
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(3, 7), GridPosition(4, 7)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(5, 0)),
            BlockedEdge.between(GridPosition(5, 7), GridPosition(6, 7)),
            BlockedEdge.between(GridPosition(6, 0), GridPosition(7, 0)),
            BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3)),
            BlockedEdge.between(GridPosition(3, 4), GridPosition(4, 4)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(6, 2))
        ),
        difficultyMetadata = "MASTER",
        seed = 6251L
    )

    val SOLUTION_251 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
            GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
            GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
            GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
            GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
            GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
            GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0)
        )
    )

    // =========================================================================
    // LEVEL 300 — GRAND FINALE (Solo Campaign Climax, 8x8, 14 walls, 12 clues)
    // =========================================================================

    val LEVEL_300 = PuzzleDefinition(
        puzzleId = "w6_lvl300",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(8, 8),
        requiredCells = grid8x8Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(7, 0)),
            NumberedCheckpoint(3, GridPosition(4, 1)),
            NumberedCheckpoint(4, GridPosition(0, 1)),
            NumberedCheckpoint(5, GridPosition(4, 2)),
            NumberedCheckpoint(6, GridPosition(7, 2)),
            NumberedCheckpoint(7, GridPosition(0, 3)),
            NumberedCheckpoint(8, GridPosition(7, 4)),
            NumberedCheckpoint(9, GridPosition(0, 5)),
            NumberedCheckpoint(10, GridPosition(3, 6)),
            NumberedCheckpoint(11, GridPosition(7, 6)),
            NumberedCheckpoint(12, GridPosition(0, 7))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
            BlockedEdge.between(GridPosition(7, 1), GridPosition(7, 2)),
            BlockedEdge.between(GridPosition(0, 2), GridPosition(0, 3)),
            BlockedEdge.between(GridPosition(7, 3), GridPosition(7, 4)),
            BlockedEdge.between(GridPosition(0, 4), GridPosition(0, 5)),
            BlockedEdge.between(GridPosition(7, 5), GridPosition(7, 6)),
            BlockedEdge.between(GridPosition(0, 6), GridPosition(0, 7)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
            BlockedEdge.between(GridPosition(4, 0), GridPosition(4, 1)),
            BlockedEdge.between(GridPosition(5, 2), GridPosition(5, 3)),
            BlockedEdge.between(GridPosition(3, 3), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(4, 4), GridPosition(4, 5)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(2, 6)),
            BlockedEdge.between(GridPosition(5, 6), GridPosition(5, 7))
        ),
        difficultyMetadata = "MASTER",
        seed = 6300L
    )

    val SOLUTION_300 = PuzzlePath(
        listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0), GridPosition(4, 0), GridPosition(5, 0), GridPosition(6, 0), GridPosition(7, 0),
            GridPosition(7, 1), GridPosition(6, 1), GridPosition(5, 1), GridPosition(4, 1), GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2), GridPosition(4, 2), GridPosition(5, 2), GridPosition(6, 2), GridPosition(7, 2),
            GridPosition(7, 3), GridPosition(6, 3), GridPosition(5, 3), GridPosition(4, 3), GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3),
            GridPosition(0, 4), GridPosition(1, 4), GridPosition(2, 4), GridPosition(3, 4), GridPosition(4, 4), GridPosition(5, 4), GridPosition(6, 4), GridPosition(7, 4),
            GridPosition(7, 5), GridPosition(6, 5), GridPosition(5, 5), GridPosition(4, 5), GridPosition(3, 5), GridPosition(2, 5), GridPosition(1, 5), GridPosition(0, 5),
            GridPosition(0, 6), GridPosition(1, 6), GridPosition(2, 6), GridPosition(3, 6), GridPosition(4, 6), GridPosition(5, 6), GridPosition(6, 6), GridPosition(7, 6),
            GridPosition(7, 7), GridPosition(6, 7), GridPosition(5, 7), GridPosition(4, 7), GridPosition(3, 7), GridPosition(2, 7), GridPosition(1, 7), GridPosition(0, 7)
        )
    )

    val ANCHOR_LEVELS: Map<Int, PuzzleDefinition> = mapOf(
        100 to LEVEL_100,
        101 to LEVEL_101,
        150 to LEVEL_150,
        151 to LEVEL_151,
        200 to LEVEL_200,
        201 to LEVEL_201,
        250 to LEVEL_250,
        251 to LEVEL_251,
        300 to LEVEL_300
    )

    val ANCHOR_SOLUTIONS: Map<Int, PuzzlePath> = mapOf(
        100 to SOLUTION_100,
        101 to SOLUTION_101,
        150 to SOLUTION_150,
        151 to SOLUTION_151,
        200 to SOLUTION_200,
        201 to SOLUTION_201,
        250 to SOLUTION_250,
        251 to SOLUTION_251,
        300 to SOLUTION_300
    )

    /**
     * Resolves the world-accurate representative fallback puzzle when generation fails or is offline.
     * Guarantees 100% grid dimension and rule compliance for any level in 1..300.
     */
    fun getFallbackForLevel(levelId: Int): PuzzleDefinition {
        val world = WorldConfiguration.getWorldForLevel(levelId)
        return when (world.worldId) {
            1 -> PackagedPuzzles.LEVEL_1
            2 -> PackagedPuzzles.LEVEL_21
            3 -> LEVEL_100
            4 -> LEVEL_101
            5 -> LEVEL_151
            6 -> LEVEL_201
            else -> PackagedPuzzles.LEVEL_1
        }
    }

    /**
     * Resolves the verified canonical solution for the fallback puzzle.
     */
    fun getFallbackSolutionForLevel(levelId: Int): PuzzlePath {
        val world = WorldConfiguration.getWorldForLevel(levelId)
        return when (world.worldId) {
            1 -> PackagedPuzzles.SOLUTION_1_ROUTE
            2 -> CuratedFirst50Levels.SOLUTION_21
            3 -> SOLUTION_100
            4 -> SOLUTION_101
            5 -> SOLUTION_151
            6 -> SOLUTION_201
            else -> PackagedPuzzles.SOLUTION_1_ROUTE
        }
    }
}
