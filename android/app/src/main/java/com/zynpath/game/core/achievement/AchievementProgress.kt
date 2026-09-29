package com.zynpath.game.core.achievement

/**
 * Composite model representing achievement definition with player's real unlock progress.
 *
 * Implements Prompt 17 Section 21 & 24.
 */
data class AchievementProgress(
    val definition: AchievementDefinition,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
) {
    val progressFraction: Float
        get() = if (definition.targetValue > 0) {
            (currentProgress.toFloat() / definition.targetValue.toFloat()).coerceIn(0f, 1f)
        } else {
            if (isUnlocked) 1f else 0f
        }

    val formattedProgress: String
        get() = if (isUnlocked) {
            "Completed"
        } else {
            "${currentProgress.coerceAtMost(definition.targetValue)} / ${definition.targetValue}"
        }
}
