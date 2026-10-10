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
        require(levelId in 1..WorldConfiguration.TOTAL_LEVELS) { "levelId must be in 1..${WorldConfiguration.TOTAL_LEVELS} (got $levelId)" }

        val chapter = ProgressionPlan.getChapterForLevel(levelId)
        val world = WorldConfiguration.getWorldForLevel(levelId)
        val isRecovery = isRecoveryLevel(levelId)
        val seed = if (attemptIndex == 0) {
            LevelSeedGenerator.computeSeed(levelId)
        } else {
            LevelSeedGenerator.computeCandidateSeed(levelId, attemptIndex)
        }

        val policy = com.zynpath.game.core.puzzle.policy.PuzzleConstraintPolicy.forSoloLevel(levelId)
        val dimensions = policy.gridDimensions

        val checkpointCount = when {
            levelId <= 4 -> 6 // Tutorial levels: 6 checkpoints in 6x6 World 1
            isRecovery -> minOf(policy.checkpointRequirements.last, policy.checkpointRequirements.first + 3)
            else -> policy.checkpointRequirements.first + (levelId % (policy.checkpointRequirements.last - policy.checkpointRequirements.first + 1))
        }.coerceIn(policy.checkpointRequirements)

        val (minWalls, maxWalls) = when {
            levelId in 1..20 -> 0 to 0 // World 1: Learn drawing and order without wall constraints
            isRecovery -> policy.allowedWallCountRange.first to minOf(policy.allowedWallCountRange.last, policy.allowedWallCountRange.first + 2)
            else -> policy.allowedWallCountRange.first to policy.allowedWallCountRange.last
        }

        val routeStyle = when {
            isRecovery -> RouteStyle.RANDOM_WALK
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
