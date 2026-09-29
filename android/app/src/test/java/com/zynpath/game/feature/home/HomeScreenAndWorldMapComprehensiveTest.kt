package com.zynpath.game.feature.home

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.repository.GameplaySessionRepositoryImpl
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.SessionStatus
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.sync.connectivity.NetworkConnectivityMonitor
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive verification of Home Screen, Mode Discovery, Resumable Session, and World Map Progression.
 *
 * Implements Prompt 48 Requirements 18, 19, 20, 21, 22.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenAndWorldMapComprehensiveTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefsRepo: FakePreferencesRepository
    private lateinit var progressRepo: ProgressRepositoryImpl
    private lateinit var sessionDao: FakeGameSessionDao
    private lateinit var sessionRepo: GameplaySessionRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefsRepo = FakePreferencesRepository(
            UserPreferences(guestUuid = "9988aabb11223344")
        )
        sessionDao = FakeGameSessionDao()
        sessionRepo = GameplaySessionRepositoryImpl(sessionDao)
        progressRepo = ProgressRepositoryImpl(
            FakeLevelProgressDao(),
            FakePlayerStatsDao(),
            sessionDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHomeUiStateDisplaysGuestIdentityAndInitialOfflineState() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakePrefsRepo, progressRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("ZYN-9988", state.guestTag)
        assertTrue("Initial player should be guest", state.isGuest)
        assertEquals(0, state.completedLevelsCount)
        assertEquals(0, state.totalStars)
        assertTrue("Solo play must be offline ready", state.isOfflineReady)
        assertEquals(1, state.nextPlayableWorldId)
        assertEquals(1, state.nextPlayableLevelId)
    }

    @Test
    fun testResumableSessionPriorityOnHomeScreen() = runTest(testDispatcher) {
        // Insert an active unfinished session for World 1, Level 4
        sessionRepo.saveSession(
            GameplaySessionSnapshot(
                sessionId = "active_session_w1_l4",
                worldId = 1,
                levelId = 4,
                puzzleId = "p_4x4_4",
                path = listOf(GridPosition(0, 0), GridPosition(0, 1)),
                moveCount = 8,
                elapsedActiveTimeMs = 45000L,
                status = SessionStatus.PAUSED,
                startedAt = System.currentTimeMillis(),
                lastUpdatedAt = System.currentTimeMillis()
            )
        )

        val viewModel = HomeViewModel(
            preferencesRepository = fakePrefsRepo,
            progressRepository = progressRepo,
            sessionRepository = sessionRepo
        )
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull("Resumable session must be populated when active session exists", state.resumableSession)
        assertEquals(1, state.resumableSession?.worldId)
        assertEquals(4, state.resumableSession?.levelId)
        assertEquals(8, state.resumableSession?.moveCount)
        assertEquals(45000L, state.resumableSession?.elapsedActiveTimeMs)
    }

    @Test
    fun testOfflineOnlineModeGatingNotice() = runTest(testDispatcher) {
        val fakeNetwork = object : NetworkConnectivityMonitor {
            override val isConnected = MutableStateFlow(false)
        }

        val viewModel = HomeViewModel(
            preferencesRepository = fakePrefsRepo,
            progressRepository = progressRepo,
            networkMonitor = fakeNetwork
        )
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        var navigated = false
        // Try entering Quick Duel while offline
        viewModel.onOnlineModeSelected(isOnlineRequired = true) {
            navigated = true
        }
        advanceUntilIdle()

        assertFalse("Must not navigate to online mode when offline", navigated)
        assertNotNull("User notice must be set explaining offline requirement", viewModel.uiState.value.userNoticeMessage)
        assertTrue(viewModel.uiState.value.userNoticeMessage!!.contains("require an active internet connection"))

        // Dismiss notice
        viewModel.clearUserNotice()
        advanceUntilIdle()
        assertNull("Notice message must be cleared", viewModel.uiState.value.userNoticeMessage)

        // Switch to online and retry
        fakeNetwork.isConnected.value = true
        viewModel.onOnlineModeSelected(isOnlineRequired = true) {
            navigated = true
        }
        advanceUntilIdle()
        assertTrue("Must navigate to online mode when connected", navigated)
    }

    @Test
    fun testWorldConfigurationHasSixWorldsAnd300Levels() {
        assertEquals("Must configure exactly 6 worlds", 6, WorldConfiguration.TOTAL_WORLDS)
        assertEquals("Must configure exactly 300 levels total", 300, WorldConfiguration.TOTAL_LEVELS)

        val worlds = WorldConfiguration.WORLDS
        assertEquals(6, worlds.size)

        // World 1: 1..20
        assertEquals(1, worlds[0].startLevel)
        assertEquals(20, worlds[0].endLevel)
        assertEquals(0, worlds[0].minPreviousWorldCompletedToUnlock)

        // World 2: 21..50
        assertEquals(21, worlds[1].startLevel)
        assertEquals(50, worlds[1].endLevel)
        assertEquals(20, worlds[1].minPreviousWorldCompletedToUnlock)

        // World 3: 51..100
        assertEquals(51, worlds[2].startLevel)
        assertEquals(100, worlds[2].endLevel)

        // World 4: 101..150
        assertEquals(101, worlds[3].startLevel)
        assertEquals(150, worlds[3].endLevel)

        // World 5: 151..200
        assertEquals(151, worlds[4].startLevel)
        assertEquals(200, worlds[4].endLevel)

        // World 6: 201..300
        assertEquals(201, worlds[5].startLevel)
        assertEquals(300, worlds[5].endLevel)
    }

    @Test
    fun testWorldLockAndUnlockProgression() {
        val completed = mutableSetOf<Int>()

        // World 1 is always unlocked
        assertTrue("World 1 must always be unlocked", WorldConfiguration.isWorldUnlocked(1, completed))
        assertFalse("World 2 must be locked initially", WorldConfiguration.isWorldUnlocked(2, completed))

        // Complete 19 levels in World 1 -> World 2 still locked (needs 20)
        for (i in 1..19) completed.add(i)
        assertFalse("World 2 must remain locked with 19 levels completed", WorldConfiguration.isWorldUnlocked(2, completed))

        // Complete 20th level in World 1 -> World 2 unlocks
        completed.add(20)
        assertTrue("World 2 must unlock when 20 levels in World 1 are completed", WorldConfiguration.isWorldUnlocked(2, completed))
    }

    @Test
    fun testSequentialLevelProgressionDerivesCorrectNextLevel() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakePrefsRepo, progressRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.nextPlayableLevelId)

        // Complete level 1
        progressRepo.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p_4x4_1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 12000L,
                moveCount = 16,
                hintCount = 0
            )
        )
        advanceUntilIdle()

        assertEquals("Next level must advance to Level 2", 2, viewModel.uiState.value.nextPlayableLevelId)
        assertEquals(1, viewModel.uiState.value.completedLevelsCount)
        assertEquals(3, viewModel.uiState.value.totalStars)
    }
}
