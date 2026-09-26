package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenge")
data class DailyChallengeEntity(
    @PrimaryKey val dateKey: String, // Format: YYYY-MM-DD
    val seed: Long,
    val gridSize: Int,
    val isCompleted: Boolean = false,
    val solveTimeMs: Long = 0L,
    val completedAt: Long = 0L
)
