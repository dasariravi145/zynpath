package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Factory deriving strict, chapter-aligned [GenerationConfiguration] instances for any level in 1..300.
 *
 * Implements Prompt 26 Tasks 5, 6, and 8:
 * - Direct mapping from 7 progression chapters to board dimensions, checkpoint bounds, and wall budgets.
 * - Identifies and tailors recovery levels to avoid continuous cognitive player frustration.
 * - Seeds configurations deterministically via [LevelSeedGenerator].
 */
object ChapterLevelConfigFactory {

    /**
     * Set of designated recovery levels across the 300-level solo campaign.
     * Recovery levels provide slightly more frequent clues and lower wall density
     * to offer a satisfying breather following intense climax or milestone levels.
     */
    val RECOVERY_LEVELS: Set<Int> = setOf(
        7, 14, 21, 28, 35, 42, 60, 70, 85, 95,
        110, 120, 135, 145, 160, 170, 185, 195,
        210, 220, 235, 245, 260, 270, 285, 295
    )

    fun isRecoveryLevel(levelId: Int): Boolean = levelId in RECOVERY_LEVELS

    /**
     * Constructs the authoritative [GenerationConfiguration] for [levelId].
     */
    fun createConfig(levelId: Int, attemptIndex: Int = 0): GenerationConfiguration {
        require(levelId in 1..300) { "levelId must be in 1..300 (got $levelId)" }

        val chapter = ProgressionPlan.getChapterForLevel(levelId)
        val world = WorldConfiguration.getWorldForLevel(levelId)
        val isRecovery = isRecoveryLevel(levelId)
        val seed = if (attemptIndex == 0) {
            LevelSeedGenerator.computeSeed(levelId)
        } else {
            LevelSeedGenerator.computeCandidateSeed(levelId, attemptIndex)
        }

        val dimensions = when (world.worldId) {
            1 -> GridDimensions(4, 4)
            2 -> GridDimensions(5, 5)
            3 -> GridDimensions(5, 5)
            4 -> GridDimensions(6, 6)
            5 -> GridDimensions(7, 7)
            6 -> GridDimensions(8, 8)
            else -> GridDimensions(4, 4)
        }

        val checkpointCount = when (chapter.chapterId) {
            1 -> if (isRecovery) 6 else 5 + ((levelId - 1) % 2)
            2 -> if (isRecovery) 7 else 4 + ((levelId - 26) % 4)
            3 -> if (isRecovery) 7 else 4 + ((levelId - 51) % 4)
            4 -> if (isRecovery) 8 else 5 + ((levelId - 101) % 4)
            5 -> if (isRecovery) 10 else 5 + ((levelId - 151) % 6)
            6 -> if (isRecovery) 12 else 6 + ((levelId - 201) % 7)
            7 -> if (isRecovery) 14 else 6 + ((levelId - 251) % 9)
            else -> 4
        }

        val (minWalls, maxWalls) = when (chapter.chapterId) {
            1 -> 0 to 0
            2 -> 0 to 0
            3 -> if (isRecovery) 1 to 2 else 2 to 5
            4 -> if (isRecovery) 2 to 4 else 3 to 8
            5 -> if (isRecovery) 4 to 6 else 5 to 12
            6 -> if (isRecovery) 6 to 8 else 7 to 16
            7 -> if (isRecovery) 8 to 10 else 9 to 18
            else -> 0 to 0
        }

        val routeStyle = when {
            isRecovery -> RouteStyle.RANDOM_WALK
            chapter.chapterId <= 2 -> RouteStyle.SERPENTINE
            chapter.chapterId in 3..4 -> RouteStyle.SPIRAL_LIKE
            else -> RouteStyle.MIXED
        }

        // Uniqueness is strictly required for grids up to 6x6.
        // For massive 7x7 and 8x8 grids, solver time budgets are adjusted to balance proof depth.
        val requireUniqueness = dimensions.rows <= 6

        return GenerationConfiguration(
            dimensions = dimensions,
            checkpointCount = checkpointCount,
            minWalls = minWalls,
            maxWalls = maxWalls,
            seed = seed,
            maxCandidateAttempts = 50,
            solverNodeBudget = when (dimensions.rows) {
                4 -> 20_000L
                5 -> 40_000L
                6 -> 60_000L
                7 -> 80_000L
                else -> 100_000L
            },
            solverTimeBudgetMs = when (dimensions.rows) {
                4 -> 1_000L
                5 -> 2_000L
                6 -> 3_000L
                else -> 4_000L
            },
            requireUniqueness = requireUniqueness,
            routeStyle = routeStyle,
            puzzleIdPrefix = "w${world.worldId}_lvl$levelId",
            worldId = world.worldId,
            levelId = levelId
        )
    }

    /**
     * Resolves the expected target difficulty band for a level.
     */
    fun targetDifficultyBand(levelId: Int): DifficultyBand {
        val chapter = ProgressionPlan.getChapterForLevel(levelId)
        return when (chapter.chapterId) {
            1 -> DifficultyBand.BEGINNER
            2 -> DifficultyBand.EASY
            3 -> DifficultyBand.MEDIUM
            4 -> DifficultyBand.MEDIUM
            5 -> DifficultyBand.HARD
            6 -> DifficultyBand.EXPERT
            7 -> DifficultyBand.EXPERT
            else -> DifficultyBand.BEGINNER
        }
    }
}
