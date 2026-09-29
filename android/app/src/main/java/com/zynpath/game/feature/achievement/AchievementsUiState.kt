package com.zynpath.game.feature.achievement

import com.zynpath.game.core.achievement.AchievementCategory
import com.zynpath.game.core.achievement.AchievementProgress

/**
 * UI State for the Achievements Screen.
 *
 * Implements Prompt 17 Section 24 and Prompt 09/24.
 */
data class AchievementsUiState(
    val selectedCategory: AchievementCategory = AchievementCategory.ALL,
    val achievements: List<AchievementProgress> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val totalStars: Int = 0,
    val isReducedMotion: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val completionPercentage: Float
        get() = if (totalCount > 0) {
            (unlockedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}
