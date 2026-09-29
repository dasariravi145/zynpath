package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Authoritative local Room persistence for player achievements.
 *
 * Implements Prompt 17 Section 17 & 21:
 * - Stable achievement identifier
 * - Actual distinct progress metrics
 * - Idempotent unlock state and timestamp
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val achievementId: String,
    val unlockedAt: Long? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val isUnlocked: Boolean = false
)
