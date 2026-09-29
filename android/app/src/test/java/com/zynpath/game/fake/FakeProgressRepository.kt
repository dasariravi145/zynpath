package com.zynpath.game.fake

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProgressRepository : ProgressRepository {
    private val progressList = MutableStateFlow<List<LevelProgressEntity>>(emptyList())
    var recordCompletionCallCount = 0

    fun completeLevel(levelId: Int) {
        val current = progressList.value.toMutableList()
        current.removeAll { it.levelId == levelId }
        val world = WorldConfiguration.getWorldForLevel(levelId)
        current.add(
            LevelProgressEntity(
                levelId = levelId,
                worldId = world.worldId,
                isCompleted = true,
                stars = 3,
                bestTimeMs = 12000L,
                movesCount = 16,
                completedAt = System.currentTimeMillis()
            )
        )
        progressList.value = current
    }

    override fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?> =
        progressList.map { list -> list.firstOrNull { it.levelId == levelId } }

    override fun observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>> =
        progressList.map { list -> list.filter { it.worldId == worldId } }

    override fun observeAllProgress(): Flow<List<LevelProgressEntity>> = progressList.asStateFlow()

    override fun getCompletedLevelCount(): Flow<Int> =
        progressList.map { list -> list.count { it.isCompleted } }

    override fun getTotalStarsEarned(): Flow<Int> =
        progressList.map { list -> list.sumOf { it.stars } }

    override suspend fun getCompletedLevelIds(): Set<Int> =
        progressList.value.filter { it.isCompleted }.map { it.levelId }.toSet()

    override suspend fun isLevelUnlocked(levelId: Int): Boolean =
        WorldConfiguration.isLevelUnlocked(levelId, getCompletedLevelIds())

    override suspend fun isWorldUnlocked(worldId: Int): Boolean =
        WorldConfiguration.isWorldUnlocked(worldId, getCompletedLevelIds())

    override suspend fun getNextPlayableLevel(): Int =
        WorldConfiguration.getNextPlayableLevel(getCompletedLevelIds())

    suspend fun getLevelProgress(levelId: Int): LevelProgressEntity? =
        progressList.value.firstOrNull { it.levelId == levelId }

    override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
        recordCompletionCallCount++
        completeLevel(result.levelId)
        return progressList.value.first { it.levelId == result.levelId }
    }

    override suspend fun saveGameSession(session: GameSessionEntity) {}
    override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
    override suspend fun abandonActiveSession(levelId: Int) {}
    override suspend fun clearAllProgress() { progressList.value = emptyList() }
    override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
    override suspend fun reconcilePrematureUnlocks() {}
}
