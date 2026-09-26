package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val worldId: Int,
    val stars: Int,
    val bestTimeMs: Long,
    val movesCount: Int,
    val isCompleted: Boolean,
    val completedAt: Long
)
