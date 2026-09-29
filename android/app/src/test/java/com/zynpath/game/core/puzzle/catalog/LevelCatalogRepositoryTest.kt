package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [LevelCatalogRepository] and its integration with player progression.
 *
 * Implements Prompt 11 Section 19, 20, 21, 25, and 34:
 * - Clean repository API contract.
 * - Reactive [LevelAvailability] state emissions (LOCKED, UNLOCKED_AND_AVAILABLE, COMPLETED, ASSET_UNAVAILABLE).
 * - Safe puzzle loading and structured load results.
 * - Offline guest progression integration.
 */
class LevelCatalogRepositoryTest {

    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var fakeProgressRepository: FakeProgressRepository
    private lateinit var catalogRepository: LevelCatalogRepository

    @Before
    fun setUp() {
        assetLoader = InMemoryAssetLoader()
        CatalogAssetGenerator.populateLoader(assetLoader)
        fakeProgressRepository = FakeProgressRepository()
        catalogRepository = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepository)
    }

    @Test
    fun `test getWorlds and getLevelsForWorld`() {
        val worlds = catalogRepository.getWorlds()
        assertEquals(6, worlds.size)

        val w1Levels = catalogRepository.getLevelsForWorld(1)
        assertEquals(20, w1Levels.size)
        assertEquals(1, w1Levels.first().levelId)
        assertEquals(20, w1Levels.last().levelId)

        val w2Levels = catalogRepository.getLevelsForWorld(2)
        assertEquals(30, w2Levels.size)
        assertEquals(21, w2Levels.first().levelId)
        assertEquals(50, w2Levels.last().levelId)
    }

    @Test
    fun `test loadPuzzle returns Success for verified packaged level`() = runBlocking {
        val result = catalogRepository.loadPuzzle(1)
        assertTrue("Level 1 must load successfully", result is CatalogLoadResult.Success)

        val success = result as CatalogLoadResult.Success
        assertEquals(1, success.level.levelId)
        assertEquals(4, success.definition.gridDimensions.rows)
        assertEquals(4, success.definition.gridDimensions.columns)
        assertEquals(5, success.definition.checkpoints.size)
        assertEquals(0, success.definition.blockedEdges.size)
    }

    @Test
    fun `test loadPuzzle returns Success for level 6 and level 10`() = runBlocking {
        val result6 = catalogRepository.loadPuzzle(6)
        assertTrue("Level 6 must load successfully", result6 is CatalogLoadResult.Success)

        val result10 = catalogRepository.loadPuzzle(10)
        assertTrue("Level 10 must load successfully", result10 is CatalogLoadResult.Success)
    }

    @Test
    fun `test loadPuzzle returns LevelNotFound for out of bounds level`() = runBlocking {
        val result = catalogRepository.loadPuzzle(999)
        assertTrue("Level 999 is out of bounds", result is CatalogLoadResult.LevelNotFound || result is CatalogLoadResult.AssetUnavailable)
    }

    @Test
    fun `test observeLevelAvailability reflects player progression and asset packaging`() = runBlocking {
        // Initial state: Level 1 is unlocked and available; Level 2 is locked; Level 10 is locked
        val l1State = catalogRepository.observeLevelAvailability(1).first()
        assertEquals(LevelAvailability.UNLOCKED_AND_AVAILABLE, l1State)

        val l2StateInitial = catalogRepository.observeLevelAvailability(2).first()
        assertEquals(LevelAvailability.LOCKED, l2StateInitial)

        // Complete Level 1
        fakeProgressRepository.completeLevel(1)

        // Level 1 becomes COMPLETED
        val l1AfterComplete = catalogRepository.observeLevelAvailability(1).first()
        assertEquals(LevelAvailability.COMPLETED, l1AfterComplete)

        // Level 2 (which is packaged) becomes UNLOCKED_AND_AVAILABLE
        val l2StateUnlocked = catalogRepository.observeLevelAvailability(2).first()
        assertEquals(LevelAvailability.UNLOCKED_AND_AVAILABLE, l2StateUnlocked)

        // Level 10 is still locked
        val l10State = catalogRepository.observeLevelAvailability(10).first()
        assertEquals(LevelAvailability.LOCKED, l10State)
    }

    @Test
    fun `test observeLevelAvailability emits UNLOCKED_AND_AVAILABLE for level 6 when levels 1 to 5 completed`() = runBlocking {
        // Complete levels 1 through 5
        for (lvl in 1..5) {
            fakeProgressRepository.completeLevel(lvl)
        }

        // Level 6 is unlocked by progression and has packaged / verified asset
        val l6State = catalogRepository.observeLevelAvailability(6).first()
        assertEquals(LevelAvailability.UNLOCKED_AND_AVAILABLE, l6State)
    }

    @Test
    fun `test validateCatalog passes on populated catalog`() {
        val integrityResult = catalogRepository.validateCatalog()
        assertTrue("Catalog must be valid", integrityResult.isValid)
        assertEquals(0, integrityResult.errors.size)
    }

    // --- Fake in-memory ProgressRepository ---

    private class FakeProgressRepository : ProgressRepository {
        private val progressList = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

        fun completeLevel(levelId: Int, stars: Int = 3) {
            val current = progressList.value.toMutableList()
            current.removeAll { it.levelId == levelId }
            val world = WorldConfiguration.getWorldForLevel(levelId)
            current.add(
                LevelProgressEntity(
                    levelId = levelId,
                    worldId = world.worldId,
                    isCompleted = true,
                    stars = stars,
                    bestTimeMs = 15000L,
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

        override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
            completeLevel(result.levelId)
            return progressList.value.first { it.levelId == result.levelId }
        }

        override suspend fun saveGameSession(session: GameSessionEntity) {}
        override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
        override suspend fun abandonActiveSession(levelId: Int) {}
        override suspend fun clearAllProgress() { progressList.value = emptyList() }
        override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
    }
}
