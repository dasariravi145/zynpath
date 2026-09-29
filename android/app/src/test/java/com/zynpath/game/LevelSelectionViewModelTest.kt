package com.zynpath.game

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.level.LevelSelectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LevelSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var progressRepo: ProgressRepositoryImpl
    private lateinit var prefsRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        progressRepo = ProgressRepositoryImpl(
            FakeLevelProgressDao(),
            FakePlayerStatsDao(),
            FakeGameSessionDao()
        )
        prefsRepo = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun levelSelection_initialWorld1State_hasFirstLevelUnlockedAndRestLocked() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.worldId)
        assertEquals(20, state.levels.size)
        assertEquals(0, state.completedCount)
        assertEquals(0, state.totalStars)

        assertEquals(LevelState.UNLOCKED, state.levels[0].state)
        assertTrue(state.levels[0].isCurrent)

        assertEquals(LevelState.LOCKED, state.levels[1].state)
        assertFalse(state.levels[1].isCurrent)
    }

    @Test
    fun levelSelection_completingLevel_updatesUiStateReactively() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, savedStateHandle)
        advanceUntilIdle()

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

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.completedCount)
        assertEquals(3, updatedState.totalStars)
        assertEquals(LevelState.COMPLETED, updatedState.levels[0].state)
        assertEquals(LevelState.UNLOCKED, updatedState.levels[1].state)
        assertTrue(updatedState.levels[1].isCurrent)
    }
}
