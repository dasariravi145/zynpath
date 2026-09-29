package com.zynpath.game.core.puzzle.premium.repository

import com.zynpath.game.core.database.dao.PremiumPackProgressDao
import com.zynpath.game.core.database.entity.PremiumPackProgressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for managing player progress on Premium Solo puzzle packs.
 *
 * Implements Prompt 27 Section 30, 31, 32:
 * - Progress is strictly preserved across app restarts, pack updates, and subscription status changes.
 * - Records best solve times and completion timestamps.
 */
interface PremiumPackProgressRepository {
    fun observePackProgress(playerId: String, packId: String): Flow<List<PremiumPackProgressEntity>>
    fun observeCompletedCount(playerId: String, packId: String): Flow<Int>
    fun observeTotalCompletedCount(playerId: String): Flow<Int>
    suspend fun getLevelProgress(playerId: String, packId: String, levelIndex: Int): PremiumPackProgressEntity?
    suspend fun recordCompletion(
        playerId: String,
        packId: String,
        levelIndex: Int,
        puzzleId: String,
        puzzleVersion: Int,
        solveTimeMs: Long
    )
}

@Singleton
class PremiumPackProgressRepositoryImpl @Inject constructor(
    private val dao: PremiumPackProgressDao
) : PremiumPackProgressRepository {

    override fun observePackProgress(playerId: String, packId: String): Flow<List<PremiumPackProgressEntity>> {
        return dao.observePackProgress(playerId, packId)
    }

    override fun observeCompletedCount(playerId: String, packId: String): Flow<Int> {
        return dao.observeCompletedCount(playerId, packId)
    }

    override fun observeTotalCompletedCount(playerId: String): Flow<Int> {
        return dao.observeTotalCompletedCount(playerId)
    }

    override suspend fun getLevelProgress(
        playerId: String,
        packId: String,
        levelIndex: Int
    ): PremiumPackProgressEntity? {
        return dao.getLevelProgress(playerId, packId, levelIndex)
    }

    override suspend fun recordCompletion(
        playerId: String,
        packId: String,
        levelIndex: Int,
        puzzleId: String,
        puzzleVersion: Int,
        solveTimeMs: Long
    ) {
        val existing = dao.getLevelProgress(playerId, packId, levelIndex)
        val now = System.currentTimeMillis()
        val bestTime = if (existing != null && existing.isCompleted && existing.bestSolveTimeMs > 0L) {
            minOf(existing.bestSolveTimeMs, solveTimeMs)
        } else {
            solveTimeMs
        }

        val updated = PremiumPackProgressEntity(
            playerId = playerId,
            packId = packId,
            levelIndex = levelIndex,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            isCompleted = true,
            bestSolveTimeMs = bestTime,
            completedAt = existing?.completedAt ?: now
        )
        dao.upsertProgress(updated)
    }
}
