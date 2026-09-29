package com.zynpath.game.core.database.repository

import com.zynpath.game.core.database.dao.GameSessionDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import com.zynpath.game.core.puzzle.model.StarRatingPolicy
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.database.dao.SyncOperationDao
import com.zynpath.game.core.sync.conflict.SyncConflictPolicy
import com.zynpath.game.core.sync.model.SyncOperationEntity
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface ProgressRepository {
    fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?>
    fun observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>>
    fun observeAllProgress(): Flow<List<LevelProgressEntity>>
    fun getCompletedLevelCount(): Flow<Int>
    fun getTotalStarsEarned(): Flow<Int>
    fun observeCompletedLevelCount(): Flow<Int> = getCompletedLevelCount()
    fun observeTotalStarsEarned(): Flow<Int> = getTotalStarsEarned()

    suspend fun getCompletedLevelIds(): Set<Int>
    suspend fun isLevelUnlocked(levelId: Int): Boolean
    suspend fun isWorldUnlocked(worldId: Int): Boolean
    suspend fun getNextPlayableLevel(): Int

    suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity
    suspend fun saveGameSession(session: GameSessionEntity)
    suspend fun getActiveSession(levelId: Int): GameSessionEntity?
    suspend fun abandonActiveSession(levelId: Int)
    suspend fun clearAllProgress()
    suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>)
    suspend fun reconcilePrematureUnlocks() {}
}

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val levelProgressDao: LevelProgressDao,
    private val playerStatsDao: PlayerStatsDao,
    private val gameSessionDao: GameSessionDao,
    private val syncOperationDao: SyncOperationDao? = null,
    private val secureTokenStorage: SecureTokenStorage? = null,
    private val achievementRepositoryProvider: javax.inject.Provider<com.zynpath.game.core.achievement.AchievementRepository>? = null
) : ProgressRepository {

    override fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?> {
        return levelProgressDao.observeLevelProgress(levelId)
    }

    override fun observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>> {
        return levelProgressDao.getLevelsForWorld(worldId)
    }

    override fun observeAllProgress(): Flow<List<LevelProgressEntity>> {
        return levelProgressDao.observeAllProgress()
    }

    override fun getCompletedLevelCount(): Flow<Int> {
        return levelProgressDao.getCompletedLevelCount()
    }

    override fun getTotalStarsEarned(): Flow<Int> {
        return levelProgressDao.getTotalStarsEarned().map { it ?: 0 }
    }

    override fun observeCompletedLevelCount(): Flow<Int> {
        return getCompletedLevelCount()
    }

    override fun observeTotalStarsEarned(): Flow<Int> {
        return getTotalStarsEarned()
    }

    override suspend fun getCompletedLevelIds(): Set<Int> {
        return levelProgressDao.getAllProgressList()
            .filter { it.isCompleted }
            .map { it.levelId }
            .toSet()
    }

    override suspend fun isLevelUnlocked(levelId: Int): Boolean {
        if (levelId == 1) return true
        val completed = getCompletedLevelIds()
        return WorldConfiguration.isLevelUnlocked(levelId, completed)
    }

    override suspend fun isWorldUnlocked(worldId: Int): Boolean {
        if (worldId == 1) return true
        val completed = getCompletedLevelIds()
        return WorldConfiguration.isWorldUnlocked(worldId, completed)
    }

    override suspend fun getNextPlayableLevel(): Int {
        val completed = getCompletedLevelIds()
        return WorldConfiguration.getNextPlayableLevel(completed)
    }

    override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
        val calculatedStars = StarRatingPolicy.calculateStars(result.hintCount, result.undoResetCount)
        val existing = levelProgressDao.getLevelProgress(result.levelId)

        val updatedEntity = if (existing == null) {
            LevelProgressEntity(
                levelId = result.levelId,
                worldId = result.worldId,
                stars = calculatedStars,
                bestTimeMs = result.elapsedTimeMs,
                movesCount = result.moveCount,
                isCompleted = true,
                completedAt = result.completedAt,
                isUnlocked = true,
                bestHintCount = result.hintCount,
                completionCount = 1,
                firstCompletedAt = result.completedAt,
                lastCompletedAt = result.completedAt
            )
        } else {
            val isFaster = existing.bestTimeMs <= 0L || result.elapsedTimeMs < existing.bestTimeMs
            val hasFewerMoves = existing.movesCount <= 0 || result.moveCount < existing.movesCount
            val hasFewerHints = result.hintCount < existing.bestHintCount

            existing.copy(
                stars = maxOf(existing.stars, calculatedStars),
                bestTimeMs = if (isFaster) result.elapsedTimeMs else existing.bestTimeMs,
                movesCount = if (hasFewerMoves) result.moveCount else existing.movesCount,
                bestHintCount = if (hasFewerHints) result.hintCount else existing.bestHintCount,
                isCompleted = true,
                isUnlocked = true,
                completionCount = existing.completionCount + 1,
                firstCompletedAt = existing.firstCompletedAt ?: existing.completedAt,
                lastCompletedAt = result.completedAt,
                completedAt = result.completedAt
            )
        }

        // Save current level progress
        levelProgressDao.atomicUpsertProgress(updatedEntity)

        // Automatically unlock the next sequential level if in range
        val nextLevelId = result.levelId + 1
        if (nextLevelId <= WorldConfiguration.TOTAL_LEVELS) {
            val currentWorld = WorldConfiguration.getWorldForLevel(result.levelId)
            val nextWorld = WorldConfiguration.getWorldForLevel(nextLevelId)

            val canUnlockNext = if (nextWorld.worldId == currentWorld.worldId) {
                true
            } else {
                val allCompleted = getCompletedLevelIds() + result.levelId
                WorldConfiguration.isWorldUnlocked(nextWorld.worldId, allCompleted)
            }

            if (canUnlockNext) {
                val nextExisting = levelProgressDao.getLevelProgress(nextLevelId)
                if (nextExisting == null) {
                    levelProgressDao.upsertLevelProgress(
                        LevelProgressEntity(
                            levelId = nextLevelId,
                            worldId = nextWorld.worldId,
                            isUnlocked = true
                        )
                    )
                } else if (!nextExisting.isUnlocked) {
                    levelProgressDao.upsertLevelProgress(nextExisting.copy(isUnlocked = true))
                }
            }
        }

        // Update player stats
        val allProgress = levelProgressDao.getAllProgressList()
        val totalCompleted = allProgress.count { it.isCompleted }
        val totalStars = allProgress.sumOf { it.stars }
        playerStatsDao.upsertPlayerStats(
            PlayerStatsEntity(
                id = 1,
                totalLevelsCompleted = totalCompleted,
                totalStars = totalStars,
                lastPlayedTimestamp = result.completedAt
            )
        )

        // Clear active session for this completed level
        gameSessionDao.deleteSessionsForLevel(result.levelId)

        // Prompt 35 & 38: Persist durable pending sync operation with deduplication for offline-first reconciliation
        try {
            syncOperationDao?.let { dao ->
                val owner = secureTokenStorage?.getSessionMetadata()?.playerId ?: "guest"
                val resourceId = "solo_level_${updatedEntity.levelId}"
                val payload = JSONObject().apply {
                    put("levelId", updatedEntity.levelId)
                    put("worldId", updatedEntity.worldId)
                    put("stars", updatedEntity.stars)
                    put("bestTimeMs", updatedEntity.bestTimeMs)
                    put("movesCount", updatedEntity.movesCount)
                    put("isCompleted", true)
                    put("bestHintCount", updatedEntity.bestHintCount)
                    put("completedAt", updatedEntity.completedAt)
                }

                val existingPending = dao.getPendingOperationForResource(owner, resourceId)
                if (existingPending != null) {
                    dao.updateOperation(
                        existingPending.copy(
                            payloadJson = payload.toString(),
                            createdAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    val op = SyncOperationEntity(
                        operationId = UUID.randomUUID().toString(),
                        ownerIdentity = owner,
                        operationType = SyncOperationEntity.TYPE_SOLO_COMPLETION,
                        payloadVersion = 1,
                        resourceIdentity = resourceId,
                        payloadJson = payload.toString(),
                        createdAt = System.currentTimeMillis(),
                        status = SyncOperationEntity.STATUS_PENDING
                    )
                    dao.insertOperation(op)
                }
            }
        } catch (_: Exception) {}

        try {
            achievementRepositoryProvider?.get()?.evaluateAll()
        } catch (_: Exception) {}

        return updatedEntity
    }

    override suspend fun saveGameSession(session: GameSessionEntity) {
        gameSessionDao.insertSession(session)
    }

    override suspend fun getActiveSession(levelId: Int): GameSessionEntity? {
        return gameSessionDao.getActiveSession(levelId)
    }

    override suspend fun abandonActiveSession(levelId: Int) {
        gameSessionDao.abandonActiveSession(levelId, System.currentTimeMillis())
    }

    override suspend fun clearAllProgress() {
        levelProgressDao.clearAllProgress()
        gameSessionDao.clearAllSessions()
        playerStatsDao.clearStats()
    }

    override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {
        // Reconciliation contract: merge remote records non-destructively using SyncConflictPolicy
        for (remote in remoteProgress) {
            val local = levelProgressDao.getLevelProgress(remote.levelId)
            val merged = SyncConflictPolicy.mergeLevelProgress(local, remote)
            levelProgressDao.upsertLevelProgress(merged)
        }
        reconcilePrematureUnlocks()
    }

    override suspend fun reconcilePrematureUnlocks() {
        val allProgress = levelProgressDao.getAllProgressList()
        val completedIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()
        for (entity in allProgress) {
            if (!entity.isCompleted) {
                val shouldBeUnlocked = WorldConfiguration.isLevelUnlocked(entity.levelId, completedIds)
                if (entity.isUnlocked != shouldBeUnlocked) {
                    levelProgressDao.upsertLevelProgress(entity.copy(isUnlocked = shouldBeUnlocked))
                }
            }
        }
    }
}
