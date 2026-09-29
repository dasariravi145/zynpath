package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local Room entity representing daily challenge participation, progress, and completion history.
 *
 * Implements Prompt 16 Sections 8, 29 & 33:
 * Preserves stable challenge and puzzle identity, validated best time, and completion timestamps.
 */
@Entity(
    tableName = "daily_challenge",
    indices = [
        Index(value = ["isCompleted", "dateKey"])
    ]
)
data class DailyChallengeEntity(
    @PrimaryKey val dateKey: String, // Format: YYYY-MM-DD
    val challengeId: String = "",
    val challengeVersion: Int = 1,
    val puzzleId: String = "",
    val puzzleVersion: Int = 1,
    val puzzleFingerprint: String = "",
    val scheduleVersion: String = "1.0.0",
    val seed: Long = 0L,
    val gridSize: Int = 5,
    val isCompleted: Boolean = false,
    val solveTimeMs: Long = 0L,
    val completedAt: Long = 0L,
    val attemptCount: Int = 0,
    val bestTimeMs: Long = 0L,
    val firstAttemptAt: Long = 0L,
    val lastAttemptAt: Long = 0L,
    val movesCount: Int = 0,
    val verificationStatus: String = "LOCAL_COMPLETION",
    val serverAttemptId: String? = null,
    val isLeaderboardEligible: Boolean = false
) {
    val moveCount: Int get() = movesCount
}

