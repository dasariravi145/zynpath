package com.zynpath.game.feature.gameplay

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.repository.GameplaySessionRepositoryImpl
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.SessionStatus
import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.InMemoryAssetLoader
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.fake.FakeProgressRepository
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verification of Timer Lifecycle, Pause/Resume, Backgrounding, Process Recreation,
 * Corrupted Snapshot Recovery, and Back Navigation Durability.
 *
 * Implements Prompt 48 Requirements 36, 37, 38, 39, 40, 41.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameplayLifecycleAndInterruptionTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var sessionDao: FakeGameSessionDao
    private lateinit var sessionRepo: GameplaySessionRepositoryImpl
    private lateinit var fakeProgressRepo: FakeProgressRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        assetLoader = InMemoryAssetLoader()
        CatalogAssetGenerator.populateLoader(assetLoader)
        sessionDao = FakeGameSessionDao()
        sessionRepo = GameplaySessionRepositoryImpl(sessionDao)
        fakeProgressRepo = FakeProgressRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testTimerPauseAndResumeLifecycle() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle, sessionRepo)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is GameplayUiState.Ready)

        // Initial elapsed time
        val initialTime = viewModel.elapsedTimeMs.value
        assertEquals(0L, initialTime)

        // Pause game
        viewModel.onPauseClicked()
        advanceUntilIdle()
        val pausedState = viewModel.uiState.value
        assertTrue("UI State must be Paused", pausedState is GameplayUiState.Paused)

        // Resume game
        viewModel.onResumeClicked()
        runCurrent()
        val resumedState = viewModel.uiState.value
        assertTrue("UI State must return to Ready after resume", resumedState is GameplayUiState.Ready)

        // Pause to cancel active timer coroutine before test completes
        viewModel.onPauseClicked()
    }

    @Test
    fun testBackgroundingSavesResumableSessionToRoom() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle, sessionRepo)

        advanceUntilIdle()

        // Move to (0, 1)
        viewModel.onCellEntered(GridPosition(0, 1))
        advanceUntilIdle()

        // Simulate app backgrounding
        viewModel.onAppBackgrounded()
        advanceUntilIdle()

        // Check Room database
        val savedSession = sessionRepo.getResumableSessionForLevel(1)
        assertNotNull("Resumable session must be written to Room database upon backgrounding", savedSession)
        assertEquals(1, savedSession?.worldId)
        assertEquals(1, savedSession?.levelId)
        assertTrue(savedSession?.isResumable == true)
    }

    @Test
    fun testProcessRestorationFromValidSession() = runTest(testDispatcher) {
        // Pre-seed a saved session in Room
        sessionRepo.saveSession(
            GameplaySessionSnapshot(
                sessionId = "session_w1_l1",
                worldId = 1,
                levelId = 1,
                puzzleId = "w1_lvl1",
                path = listOf(GridPosition(0, 0), GridPosition(0, 1)),
                moveCount = 2,
                elapsedActiveTimeMs = 8500L,
                status = SessionStatus.PAUSED,
                startedAt = System.currentTimeMillis(),
                lastUpdatedAt = System.currentTimeMillis()
            )
        )

        // Process recreation: new ViewModel loads
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle, sessionRepo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is GameplayUiState.Ready)
        val ready = state as GameplayUiState.Ready
        assertEquals(GridPosition(0, 1), ready.boardState.currentHead)
        assertEquals(2, ready.boardState.coveredCount)
    }

    @Test
    fun testCorruptedSnapshotRecoveryGracefullyReinitializes() = runTest(testDispatcher) {
        // Pre-seed corrupted session (diagonal jump between non-adjacent cells)
        sessionDao.insertSession(
            GameSessionEntity(
                sessionId = "session_corrupted",
                worldId = 1,
                levelId = 1,
                puzzleId = "w1_lvl1",
                pathSnapshot = "0,0;3,3", // Illegal jump
                status = "PAUSED",
                moveCount = 2,
                elapsedActiveTimeMs = 5000L,
                startedAt = System.currentTimeMillis(),
                lastUpdatedAt = System.currentTimeMillis()
            )
        )

        // Launch ViewModel
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle, sessionRepo)

        advanceUntilIdle()

        // Must recover safely to clean initial puzzle state without crashing
        val state = viewModel.uiState.value
        assertTrue("Corrupted session must recover to clean Ready state", state is GameplayUiState.Ready)
        val ready = state as GameplayUiState.Ready
        assertEquals(null, ready.boardState.currentHead)
        assertEquals(0, ready.boardState.coveredCount)
    }

    @Test
    fun testBackNavigationPreservesDurableProgressInDatabase() = runTest(testDispatcher) {
        // Complete level 1
        fakeProgressRepo.completeLevel(1)

        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to 1))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val viewModel = GameplayViewModel(catalogRepo, fakeProgressRepo, savedStateHandle, sessionRepo)

        advanceUntilIdle()

        // User navigates back
        viewModel.onNavigatedAway()
        advanceUntilIdle()

        // Verify level 1 progress in database remains intact
        val progress = fakeProgressRepo.getLevelProgress(1)
        assertNotNull(progress)
        assertTrue("Durable progress must not be discarded upon back navigation", progress?.isCompleted == true)
    }
}
