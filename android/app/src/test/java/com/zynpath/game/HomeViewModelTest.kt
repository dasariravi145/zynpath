package com.zynpath.game

import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefsRepo: FakePreferencesRepository
    private lateinit var progressRepo: ProgressRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefsRepo = FakePreferencesRepository(
            UserPreferences(guestUuid = "7749abcdef123456")
        )
        progressRepo = ProgressRepositoryImpl(
            FakeLevelProgressDao(),
            FakePlayerStatsDao(),
            FakeGameSessionDao()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun homeViewModel_displaysInitialGuestStateAndZeroProgress() = runTest {
        val viewModel = HomeViewModel(fakePrefsRepo, progressRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("ZYN-7749", state.guestTag)
        assertTrue(state.isGuest)
        assertEquals(0, state.completedLevelsCount)
        assertEquals(0, state.totalStars)
        assertTrue(state.isOfflineReady)
    }

    @Test
    fun homeViewModel_reflectsCompletedLevelsAndEarnedStars() = runTest {
        val viewModel = HomeViewModel(fakePrefsRepo, progressRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Complete level 1 with 3 stars
        progressRepo.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p_4x4_1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 10000L,
                moveCount = 16,
                hintCount = 0
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.completedLevelsCount)
        assertEquals(3, state.totalStars)
    }
}
