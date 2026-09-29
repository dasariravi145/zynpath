package com.zynpath.game.core.analytics.model

/**
 * Comprehensive analytics for the canonical 300-level Solo campaign.
 *
 * Implements Prompt 30 Sections 11, 12, 13 & 20:
 * - Tracks total completed levels against the canonical 300 free levels.
 * - Details completed worlds, current active world, remaining levels, and per-world breakdown.
 * - Separates canonical free levels from optional Premium Solo puzzle packs.
 */
data class SoloProgressionAnalytics(
    val totalCompletedLevels: Int,
    val totalAvailableLevels: Int = 300,
    val completedWorldsCount: Int,
    val totalWorldsCount: Int = 6,
    val currentWorldId: Int,
    val totalStarsEarned: Int,
    val totalAvailableStars: Int = 900,
    val fastestSolveTimeMs: Long?,
    val averageSolveTimeMs: Long?,
    val worldProgressList: List<WorldProgressItem>
) {
    val completedLevels: Int
        get() = totalCompletedLevels

    val totalLevels: Int
        get() = totalAvailableLevels

    val worlds: List<WorldProgressItem>
        get() = worldProgressList

    val completionPercentage: Float
        get() = if (totalAvailableLevels > 0) totalCompletedLevels.toFloat() / totalAvailableLevels.toFloat() else 0f

    val remainingFreeLevels: Int
        get() = maxOf(0, totalAvailableLevels - totalCompletedLevels)

    val isAllWorldsCompleted: Boolean
        get() = totalCompletedLevels >= totalAvailableLevels

    val formattedOverallAverageTime: String
        get() = averageSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "No timed solves"

    val formattedFastestSolveTime: String
        get() = fastestSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"
}
