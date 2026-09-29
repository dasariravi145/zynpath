package com.zynpath.game.core.achievement

/**
 * Authoritative immutable achievement definition.
 *
 * Implements Prompt 17 Section 17 & 20:
 * - Stable achievement identifier
 * - Title and description
 * - Category
 * - Clear target metric requirement
 */
data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val targetValue: Int = 1,
    val iconName: String = "trophy",
    val isSecret: Boolean = false
)
