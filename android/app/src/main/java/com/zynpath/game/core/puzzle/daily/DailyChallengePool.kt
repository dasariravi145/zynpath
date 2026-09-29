package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Entry within the curated Daily Challenge verified puzzle pool.
 */
data class DailyChallengePoolEntry(
    val poolIndex: Int,
    val definition: PuzzleDefinition,
    val difficultyTier: String,
    val title: String
) {
    val fingerprint: String by lazy {
        PuzzleFingerprint.computeSha256(definition)
    }
}

/**
 * Authoritative pool of solver-verified puzzles for Daily Challenge scheduling.
 *
 * Implements Prompt 16 Section 13:
 * Every puzzle in this pool:
 * 1. Has valid orthogonal grid dimensions.
 * 2. Has numbered checkpoints starting at 1.
 * 3. Has an exact, verified complete path covering 100% of required cells.
 * 4. Passes all [CompletionValidator] constraints.
 * 5. Possesses an immutable SHA-256 fingerprint.
 */
object DailyChallengePool {

    private fun grid4x4Cells(): Set<GridPosition> =
        (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid5x5Cells(): Set<GridPosition> =
        (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid6x6Cells(): Set<GridPosition> =
        (0..5).flatMap { r -> (0..5).map { c -> GridPosition(r, c) } }.toSet()

    // Additional verified 6x6 puzzle for advanced daily challenges
    private val PUZZLE_6X6_SERPENTINE = PuzzleDefinition(
        puzzleId = "daily_curated_6x6_01",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(6, 6),
        requiredCells = grid6x6Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 5)),
            NumberedCheckpoint(3, GridPosition(1, 0)),
            NumberedCheckpoint(4, GridPosition(2, 5)),
            NumberedCheckpoint(5, GridPosition(3, 0)),
            NumberedCheckpoint(6, GridPosition(4, 5)),
            NumberedCheckpoint(7, GridPosition(5, 0))
        ),
        blockedEdges = emptySet(),
        difficultyMetadata = "ADVANCED",
        seed = 6001L
    )

    private val PUZZLE_5X5_LABYRINTH = PuzzleDefinition(
        puzzleId = "daily_curated_5x5_walls",
        puzzleVersion = 1,
        gridDimensions = GridDimensions(5, 5),
        requiredCells = grid5x5Cells(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 4)),
            NumberedCheckpoint(3, GridPosition(4, 4)),
            NumberedCheckpoint(4, GridPosition(4, 0)),
            NumberedCheckpoint(5, GridPosition(2, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
            BlockedEdge.between(GridPosition(3, 2), GridPosition(3, 3))
        ),
        difficultyMetadata = "MEDIUM",
        seed = 5099L
    )

    val POOL: List<DailyChallengePoolEntry> = listOf(
        DailyChallengePoolEntry(
            poolIndex = 0,
            definition = PackagedPuzzles.LEVEL_1,
            difficultyTier = "BEGINNER",
            title = "Serpentine Spark"
        ),
        DailyChallengePoolEntry(
            poolIndex = 1,
            definition = PackagedPuzzles.LEVEL_21,
            difficultyTier = "MEDIUM",
            title = "Emerald Meadow"
        ),
        DailyChallengePoolEntry(
            poolIndex = 2,
            definition = PackagedPuzzles.LEVEL_51,
            difficultyTier = "CHALLENGING",
            title = "Granite Gate"
        ),
        DailyChallengePoolEntry(
            poolIndex = 3,
            definition = PackagedPuzzles.LEVEL_2,
            difficultyTier = "BEGINNER",
            title = "Corner Weaver"
        ),
        DailyChallengePoolEntry(
            poolIndex = 4,
            definition = PackagedPuzzles.LEVEL_22,
            difficultyTier = "MEDIUM",
            title = "Vertical Cascade"
        ),
        DailyChallengePoolEntry(
            poolIndex = 5,
            definition = PackagedPuzzles.LEVEL_52,
            difficultyTier = "CHALLENGING",
            title = "Double Barrier"
        ),
        DailyChallengePoolEntry(
            poolIndex = 6,
            definition = PackagedPuzzles.LEVEL_3,
            difficultyTier = "BEGINNER",
            title = "Ascent Route"
        ),
        DailyChallengePoolEntry(
            poolIndex = 7,
            definition = PackagedPuzzles.LEVEL_23,
            difficultyTier = "MEDIUM",
            title = "Spiral Sweep"
        ),
        DailyChallengePoolEntry(
            poolIndex = 8,
            definition = PackagedPuzzles.LEVEL_53,
            difficultyTier = "HARD",
            title = "Triple Bastion"
        ),
        DailyChallengePoolEntry(
            poolIndex = 9,
            definition = PackagedPuzzles.LEVEL_4,
            difficultyTier = "BEGINNER",
            title = "Crosswind"
        ),
        DailyChallengePoolEntry(
            poolIndex = 10,
            definition = PackagedPuzzles.LEVEL_5,
            difficultyTier = "BEGINNER",
            title = "Diagonal Sweep"
        ),
        DailyChallengePoolEntry(
            poolIndex = 11,
            definition = PUZZLE_6X6_SERPENTINE,
            difficultyTier = "EXPERT",
            title = "Hex Grid Odyssey"
        ),
        DailyChallengePoolEntry(
            poolIndex = 12,
            definition = PackagedPuzzles.LEVEL_21,
            difficultyTier = "MEDIUM",
            title = "Verdant Echo"
        ),
        DailyChallengePoolEntry(
            poolIndex = 13,
            definition = PackagedPuzzles.LEVEL_51,
            difficultyTier = "CHALLENGING",
            title = "Stone Fortress"
        )
    )

    /**
     * Total number of verified challenge puzzles in this curated offline schedule pool.
     */
    val size: Int get() = POOL.size

    operator fun get(index: Int): DailyChallengePoolEntry = POOL[Math.floorMod(index, POOL.size)]
}
