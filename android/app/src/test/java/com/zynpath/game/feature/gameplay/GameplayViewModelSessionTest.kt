package com.zynpath.game.feature.gameplay

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.GameplaySessionRepositoryImpl
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.InMemoryAssetLoader
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.SessionPathSerializer
import com.zynpath.game.core.puzzle.session.SessionStatus
import com.zynpath.game.core.puzzle.session.TestTimeProvider
import com.zynpath.game.fake.FakeGameSessionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit and lifecycle tests for [GameplayViewModel] session management, persistence, timer, and restoration.
 *
 * Implements Prompt 13 Sections 37, 38, 40:
 * - Starting fresh session and timer start on first valid touch.
 * - Backgrounding behavior: pausing timer and flushing session to repository.
 * - Restoring existing valid saved session across launches.
 * - Pause and resume controls with monotonic time preservation.
 * - Reset behavior clearing active path, timer, and updating Room snapshot.
 * - Idempotent completion persisting personal best and clearing active session.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameplayViewModelSessionTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var fakeProgressRepo: FakeProgressRepository
    private lateinit var fakeSessionDao: FakeGameSessionDao
    private lateinit var sessionRepo: GameplaySessionRepositoryImpl
    private lateinit var testTime: TestTimeProvider

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        assetLoader = InMemoryAssetLoader()
        CatalogAssetGenerator.populateLoader(assetLoader)
        fakeProgressRepo = FakeProgressRepository()
        fakeSessionDao = FakeGameSessionDao()
        sessionRepo = GameplaySessionRepositoryImpl(fakeSessionDao)
        testTime = TestTimeProvider(currentMonotonicMs = 5000L, currentWallClockMs = 1_700_000_000_000L)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fresh level session creation and first touch starts timer`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(0L, state.elapsedTimeMs)
        assertEquals(0, state.coveredCellCount)
        assertNotNull(state.sessionId)

        // Stored session in repository is NOT_STARTED
        val storedSession = sessionRepo.getSessionById(state.sessionId)
        assertNotNull(storedSession)
        assertEquals(SessionStatus.NOT_STARTED, storedSession?.status)

        // Touch Checkpoint #1 to start path
        val accepted = viewModel.onCellEntered(GridPosition(0, 0))
        assertTrue(accepted)

        testTime.advanceTimeMs(1200L)
        testScheduler.advanceTimeBy(350L) // trigger debounced save and ticker
        testScheduler.runCurrent()

        val activeState = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(1, activeState.coveredCellCount)
        assertTrue(activeState.elapsedTimeMs >= 1200L)

        // Repository snapshot updated to ACTIVE
        val updatedSession = sessionRepo.getSessionById(state.sessionId)
        assertEquals(SessionStatus.ACTIVE, updatedSession?.status)
        assertEquals(1, updatedSession?.path?.size)
        viewModel.onPauseGame()
    }

    @Test
    fun `test app backgrounding pauses game and flushes session to Room`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        viewModel.onCellEntered(GridPosition(0, 0))
        viewModel.onCellEntered(GridPosition(0, 1))

        testTime.advanceTimeMs(3000L)
        testScheduler.advanceTimeBy(350L)
        testScheduler.runCurrent()

        // App enters background
        viewModel.onAppBackgrounded()
        testScheduler.advanceUntilIdle()

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertTrue(ready.gameState.isPaused)

        // Session in repository must be PAUSED with accumulated duration
        val stored = sessionRepo.getResumableSession(1)
        assertNotNull(stored)
        assertEquals(SessionStatus.PAUSED, stored?.status)
        assertEquals(2, stored?.path?.size)
        assertTrue(stored?.elapsedActiveTimeMs ?: 0L >= 3000L)
    }

    @Test
    fun `test safe session restoration when launching level with existing saved path`() = runTest(testDispatcher) {
        val solution = PackagedPuzzles.SOLUTION_1_ROUTE.positions
        val partialPath = solution.take(5) // 5 steps

        // Pre-populate a saved PAUSED session in Room
        val preSavedSession = GameplaySessionSnapshot(
            sessionId = "saved_sess_101",
            levelId = 1,
            worldId = 1,
            puzzleId = PackagedPuzzles.LEVEL_1_PUZZLE.puzzleId,
            puzzleVersion = PackagedPuzzles.LEVEL_1_PUZZLE.puzzleVersion,
            status = SessionStatus.PAUSED,
            path = partialPath,
            elapsedActiveTimeMs = 4500L,
            revision = 5L
        )
        sessionRepo.saveSession(preSavedSession)

        // Launch ViewModel (simulating process launch or returning to level)
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State must be Ready with restored session, got $state", state is GameplayUiState.Ready)

        val ready = state as GameplayUiState.Ready
        assertEquals("saved_sess_101", ready.sessionId)
        assertEquals(5, ready.coveredCellCount)
        assertEquals(partialPath, ready.currentOrderedPath)
        assertEquals(4500L, ready.elapsedTimeMs)
        assertTrue(ready.isRestoredSession)
        assertTrue(ready.gameState.isPaused)
    }

    @Test
    fun `test pause and resume controls preserve elapsed duration without drift`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        viewModel.onCellEntered(GridPosition(0, 0))
        testTime.advanceTimeMs(2000L) // 2s active
        testScheduler.advanceTimeBy(350L)
        testScheduler.runCurrent()

        // Pause
        viewModel.onPauseGame()
        testScheduler.advanceUntilIdle()

        // 15 seconds pass while paused
        testTime.advanceTimeMs(15_000L)
        testScheduler.advanceUntilIdle()

        var ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(2000L, ready.elapsedTimeMs)

        // Resume
        viewModel.onResumeGame()
        testTime.advanceTimeMs(1000L) // 1s active
        testScheduler.advanceTimeBy(350L)
        testScheduler.runCurrent()

        ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(3000L, ready.elapsedTimeMs)
        viewModel.onPauseGame()
    }

    @Test
    fun `test reset clears active path and resets timer in repository`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        viewModel.onCellEntered(GridPosition(0, 0))
        viewModel.onCellEntered(GridPosition(0, 1))
        testTime.advanceTimeMs(4000L)
        testScheduler.advanceTimeBy(350L)
        testScheduler.runCurrent()

        // Reset
        viewModel.onResetClicked()
        testScheduler.advanceUntilIdle()

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(0L, ready.elapsedTimeMs)
        assertEquals(0, ready.coveredCellCount)
        assertEquals(GameStatus.NOT_STARTED, ready.gameStatus)

        // Stored session must be NOT_STARTED with empty path and 0 ms
        val stored = sessionRepo.getSessionById(ready.sessionId)
        assertEquals(SessionStatus.NOT_STARTED, stored?.status)
        assertEquals(0, stored?.path?.size)
        assertEquals(0L, stored?.elapsedActiveTimeMs)
    }

    @Test
    fun `test completing puzzle marks session completed and records personal best`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, sessionRepo, testTime, savedStateHandle)

        testScheduler.advanceUntilIdle()

        val solution = PackagedPuzzles.SOLUTION_1_ROUTE.positions
        for (pos in solution) {
            viewModel.onCellEntered(pos)
            testTime.advanceTimeMs(500L)
        }
        testScheduler.advanceUntilIdle()

        val ready = viewModel.uiState.value as GameplayUiState.Ready
        assertTrue(ready.isCompleted)
        assertEquals(1, fakeProgressRepo.recordCompletionCallCount)

        // Session marked COMPLETED in Room
        val stored = sessionRepo.getSessionById(ready.sessionId)
        assertEquals(SessionStatus.COMPLETED, stored?.status)

        // Calling onCellEntered again does not re-record completion
        viewModel.onCellEntered(solution.last())
        testScheduler.advanceUntilIdle()
        assertEquals(1, fakeProgressRepo.recordCompletionCallCount)
    }

    private class FakeProgressRepository : ProgressRepository {
        private val progressList = MutableStateFlow<List<LevelProgressEntity>>(emptyList())
        var recordCompletionCallCount = 0

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
            val current = progressList.value.toMutableList()
            current.removeAll { it.levelId == result.levelId }
            val entity = LevelProgressEntity(
                levelId = result.levelId,
                worldId = result.worldId,
                isCompleted = true,
                stars = 3,
                bestTimeMs = result.elapsedTimeMs,
                movesCount = result.moveCount,
                completedAt = result.completedAt
            )
            current.add(entity)
            progressList.value = current
            return entity
        }

        override suspend fun saveGameSession(session: GameSessionEntity) {}
        override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
        override suspend fun abandonActiveSession(levelId: Int) {}
        override suspend fun clearAllProgress() { progressList.value = emptyList() }
        override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
    }
}
