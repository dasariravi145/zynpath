package com.zynpath.game.feature.gameplay

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.InMemoryAssetLoader
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GameplayViewModel].
 *
 * Implements Prompt 11 Section 28 and Section 31:
 * - Tests level loading, catalog verification, and gate checks prior to gameplay.
 * - Confirms structured error handling when attempting to launch locked levels.
 * - Confirms honest ContentUnavailable state when opening unpackaged levels.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameplayViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var fakeProgressRepo: FakeProgressRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        assetLoader = InMemoryAssetLoader()
        CatalogAssetGenerator.populateLoader(assetLoader)
        fakeProgressRepo = FakeProgressRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loading unlocked and packaged Level 1 produces Ready state`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State must be Ready for Level 1, got $state", state is GameplayUiState.Ready)

        val ready = state as GameplayUiState.Ready
        assertEquals(1, ready.levelId)
        assertEquals(1, ready.worldId)
        assertEquals(4, ready.definition.gridDimensions.rows)
        assertEquals(4, ready.definition.gridDimensions.columns)
        assertEquals(5, ready.definition.checkpoints.size)
        assertEquals(0, ready.definition.blockedEdges.size)
    }

    @Test
    fun `test loading locked Level 2 produces Error state`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 2))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State must be Error for locked level 2, got $state", state is GameplayUiState.Error)
        val error = state as GameplayUiState.Error
        assertTrue(error.message.contains("locked"))
    }

    @Test
    fun `test loading unlocked unpackaged Level 6 produces ContentUnavailable`() = runTest(testDispatcher) {
        // Unlock levels 1..5
        for (i in 1..5) {
            fakeProgressRepo.completeLevel(i)
        }

        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 6))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State must be ContentUnavailable for unpackaged level 6, got $state", state is GameplayUiState.ContentUnavailable)
        val unavailable = state as GameplayUiState.ContentUnavailable
        assertEquals(6, unavailable.levelId)
    }

    @Test
    fun `test onCellEntered starting on checkpoint 1 transitions state to in progress`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val accepted = viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 0))
        assertTrue("Touching checkpoint 1 must be accepted", accepted)

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(com.zynpath.game.core.puzzle.engine.GameStatus.IN_PROGRESS, ready.gameStatus)
        assertEquals(1, ready.coveredCellCount)
        assertTrue(ready.isResetAvailable)
        assertFalse(ready.isUndoAvailable) // 1 cell path cannot undo
    }

    @Test
    fun `test onCellEntered starting on invalid cell returns false and preserves empty path`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val accepted = viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 1))
        assertFalse("Touching non-start cell must be rejected", accepted)

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(com.zynpath.game.core.puzzle.engine.GameStatus.NOT_STARTED, ready.gameStatus)
        assertEquals(com.zynpath.game.core.puzzle.engine.MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE, ready.lastRejectionReason)
        assertEquals(0, ready.coveredCellCount)
    }

    @Test
    fun `test onCellEntered forward movement and drag backtracking`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        // (0,0) -> (0,1) -> (0,2)
        viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 0))
        viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 1))
        viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 2))

        var ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(3, ready.coveredCellCount)
        assertTrue(ready.isUndoAvailable)

        // Drag backtrack to (0, 1)
        val backtrackAccepted = viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 1))
        assertTrue("Drag backtracking to predecessor must be accepted", backtrackAccepted)

        ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(2, ready.coveredCellCount)
        assertEquals(com.zynpath.game.core.puzzle.model.GridPosition(0, 1), ready.currentEndpoint)
    }

    @Test
    fun `test onUndoClicked and onResetClicked`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 0))
        viewModel.onCellEntered(com.zynpath.game.core.puzzle.model.GridPosition(0, 1))

        // Undo
        viewModel.onUndoClicked()
        var ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(1, ready.coveredCellCount)
        assertEquals(com.zynpath.game.core.puzzle.model.GridPosition(0, 0), ready.currentEndpoint)

        // Reset
        viewModel.onResetClicked()
        ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(0, ready.coveredCellCount)
        assertEquals(com.zynpath.game.core.puzzle.engine.GameStatus.NOT_STARTED, ready.gameStatus)
        assertFalse(ready.isUndoAvailable)
        assertFalse(ready.isResetAvailable)
    }

    @Test
    fun `test completing full puzzle records completion idempotently`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val solution = com.zynpath.game.core.puzzle.catalog.PackagedPuzzles.ALL_SOLUTIONS[1]!!
        for (pos in solution.positions) {
            viewModel.onCellEntered(pos)
        }

        testScheduler.advanceUntilIdle()

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertTrue("Level 1 must be completed", ready.isCompleted)
        assertEquals(1, fakeProgressRepo.recordCompletionCallCount)

        // Attempting further moves on completed board must not trigger duplicate recording
        viewModel.onCellEntered(solution.positions.last())
        testScheduler.advanceUntilIdle()
        assertEquals(1, fakeProgressRepo.recordCompletionCallCount)
    }

    private class FakeProgressRepository : ProgressRepository {
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
    }
}
