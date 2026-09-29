package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Room entity tracking player progress on Premium Solo puzzle packs.
 *
 * Implements Prompt 27 Section 30 & 32:
 * - Persists completed puzzles and best times independently of the free campaign.
 * - Progress is strictly preserved even if subscription expires or user cancels.
 */
@Entity(
    tableName = "premium_pack_progress",
    primaryKeys = ["playerId", "packId", "levelIndex"],
    indices = [
        Index(value = ["playerId", "packId"]),
        Index(value = ["playerId"])
    ]
)
data class PremiumPackProgressEntity(
    val playerId: String,
    val packId: String,
    val levelIndex: Int,
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val isCompleted: Boolean = false,
    val bestSolveTimeMs: Long = 0L,
    val completedAt: Long? = null
)
