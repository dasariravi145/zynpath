package com.zynpath.game.core.player

/**
 * Derived player statistics model aggregated strictly from verified records.
 *
 * Implements Prompt 17 Section 15 & 16:
 * - Derived from validated Solo completion records
 * - Verified Daily Challenge records
 * - Authoritative catalog metadata
 * - No fabricated totals or unearned achievements
 */
data class PlayerStatistics(
    val completedSoloLevels: Int = 0,
    val totalAvailableSoloLevels: Int = 0,
    val totalStarsEarned: Int = 0,
    val completedDailyChallenges: Int = 0,
    val currentDailyStreak: Int = 0,
    val bestDailyStreak: Int = 0,
    val totalCompletedSolves: Int = 0,
    val unlockedAchievementsCount: Int = 0,
    val totalAchievementsCount: Int = 0
) {
    val soloCompletionPercentage: Float
        get() = if (totalAvailableSoloLevels > 0) {
            (completedSoloLevels.toFloat() / totalAvailableSoloLevels.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val achievementPercentage: Float
        get() = if (totalAchievementsCount > 0) {
            (unlockedAchievementsCount.toFloat() / totalAchievementsCount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}
