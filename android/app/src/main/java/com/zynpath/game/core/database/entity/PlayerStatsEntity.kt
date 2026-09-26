package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalLevelsCompleted: Int = 0,
    val totalStars: Int = 0,
    val currentStreakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val totalSolveTimeMs: Long = 0L,
    val lastPlayedTimestamp: Long = 0L
)
