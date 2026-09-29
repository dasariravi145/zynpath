package com.zynpath.game.core.achievement

/**
 * Categories organizing player achievements.
 *
 * Implements Prompt 17 Section 18.
 */
enum class AchievementCategory(val displayName: String) {
    ALL("All"),
    SOLO("Solo Play"),
    WORLD("World Completion"),
    DAILY("Daily Challenge"),
    MASTERY("Mastery"),
    STREAK("Streaks"),
    COMPETITIVE("Competitive")
}
