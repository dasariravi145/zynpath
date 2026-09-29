package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local Room entity representing persistent player progress on a specific puzzle level.
 */
@Entity(
    tableName = "level_progress",
    indices = [
        Index(value = ["worldId", "isCompleted"]),
        Index(value = ["isCompleted"])
    ]
)
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val worldId: Int,
    val stars: Int = 0,
    val bestTimeMs: Long = 0L,
    val movesCount: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L,
    val isUnlocked: Boolean = false,
    val bestHintCount: Int = 0,
    val completionCount: Int = 0,
    val firstCompletedAt: Long? = null,
    val lastCompletedAt: Long? = null
)
