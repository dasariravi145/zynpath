package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local Room entity representing an active or paused in-progress puzzle session.
 * Used for lifecycle preservation and resuming games without marking interrupted sessions as completed.
 *
 * Implements Prompt 13 Sections 7, 11, 25:
 * - Stable UUID primary key.
 * - Exact puzzle ID, puzzle version, catalog version, revision, and snapshot schema version.
 */
@Entity(
    tableName = "game_sessions",
    indices = [
        Index(value = ["levelId", "status"]),
        Index(value = ["ownerIdentity"])
    ]
)
data class GameSessionEntity(
    @PrimaryKey val sessionId: String,
    val levelId: Int,
    val worldId: Int,
    val puzzleSeed: Long = 0L,
    val startedAt: Long,
    val lastUpdatedAt: Long,
    val elapsedActiveTimeMs: Long,
    val status: String, // "NOT_STARTED", "ACTIVE", "PAUSED", "ABANDONED", "COMPLETED", "RESTORATION_FAILED"
    val pathSnapshot: String? = null, // Semicolon-delimited coordinate pairs e.g. "0,0;0,1;0,2"
    val moveCount: Int = 0,
    val hintCount: Int = 0,
    val puzzleId: String = "",
    val puzzleVersion: Int = 1,
    val catalogVersion: String = "1.0.0",
    val revision: Long = 1L,
    val snapshotSchemaVersion: Int = 1,
    val ownerIdentity: String? = null
) {
    val elapsedTimeMs: Long get() = elapsedActiveTimeMs
}
