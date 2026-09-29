package com.zynpath.game.core.puzzle.experience

import com.zynpath.game.core.puzzle.model.GridDimensions

/**
 * Architectural specification and quality gates for curated level variety across the 300-level catalog.
 *
 * Implements Prompt 25 Task 5:
 * Establishes formal criteria for puzzle generation, uniqueness, visual clarity,
 * and touch accessibility while deferring bulk puzzle asset generation to later prompts.
 */
data class LevelVarietySpecification(
    val levelId: Int,
    val gridDimensions: GridDimensions,
    val targetCheckpointCount: IntRange,
    val targetWallCount: IntRange,
    val minManhattanDistanceBetweenCheckpoints: Int,
    val maxTurnRepetitionAllowance: Int,
    val touchCellTargetDp: Float,
    val requiresUniqueFingerprint: Boolean = true
) {
    companion object {

        /**
         * Resolves the variety and quality criteria for any level across the 7 chapters.
         */
        fun forLevel(levelId: Int): LevelVarietySpecification {
            val chapter = ProgressionPlan.getChapterForLevel(levelId)
            return when (chapter.chapterId) {
                1 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(4, 4),
                    targetCheckpointCount = 4..6,
                    targetWallCount = 0..0,
                    minManhattanDistanceBetweenCheckpoints = 2,
                    maxTurnRepetitionAllowance = 3,
                    touchCellTargetDp = 64f
                )
                2 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(5, 5),
                    targetCheckpointCount = 4..7,
                    targetWallCount = 0..0,
                    minManhattanDistanceBetweenCheckpoints = 2,
                    maxTurnRepetitionAllowance = 3,
                    touchCellTargetDp = 56f
                )
                3 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(5, 5),
                    targetCheckpointCount = 4..7,
                    targetWallCount = 1..5,
                    minManhattanDistanceBetweenCheckpoints = 2,
                    maxTurnRepetitionAllowance = 2,
                    touchCellTargetDp = 56f
                )
                4 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(6, 6),
                    targetCheckpointCount = 5..8,
                    targetWallCount = 2..8,
                    minManhattanDistanceBetweenCheckpoints = 3,
                    maxTurnRepetitionAllowance = 2,
                    touchCellTargetDp = 48f
                )
                5 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(7, 7),
                    targetCheckpointCount = 5..10,
                    targetWallCount = 4..12,
                    minManhattanDistanceBetweenCheckpoints = 3,
                    maxTurnRepetitionAllowance = 2,
                    touchCellTargetDp = 42f
                )
                6 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(8, 8),
                    targetCheckpointCount = 6..12,
                    targetWallCount = 6..16,
                    minManhattanDistanceBetweenCheckpoints = 3,
                    maxTurnRepetitionAllowance = 2,
                    touchCellTargetDp = 38f
                )
                7 -> LevelVarietySpecification(
                    levelId = levelId,
                    gridDimensions = GridDimensions(8, 8),
                    targetCheckpointCount = 6..14,
                    targetWallCount = 8..18,
                    minManhattanDistanceBetweenCheckpoints = 3,
                    maxTurnRepetitionAllowance = 1,
                    touchCellTargetDp = 38f
                )
                else -> forLevel(1)
            }
        }
    }
}
