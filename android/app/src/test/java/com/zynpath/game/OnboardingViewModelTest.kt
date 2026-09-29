package com.zynpath.game

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.onboarding.OnboardingViewModel
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
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onboarding_onPlayClicked_navigatesToTutorial_whenTutorialNotCompleted() = runTest(testDispatcher) {
        val fakeRepo = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = false, isTutorialCompleted = false, isTutorialSkipped = false))
        val viewModel = OnboardingViewModel(fakeRepo)

        var navTutorial = false
        var navHome = false
        viewModel.onPlayClicked(
            onNavigateToTutorial = { navTutorial = true },
            onNavigateToHome = { navHome = true }
        )
        advanceUntilIdle()

        assertTrue(navTutorial)
        assertFalse(navHome)
        assertTrue(fakeRepo.userPreferencesFlow.first().isOnboardingCompleted)
    }

    @Test
    fun onboarding_onPlayClicked_navigatesToHome_whenTutorialCompleted() = runTest(testDispatcher) {
        val fakeRepo = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = false, isTutorialCompleted = true))
        val viewModel = OnboardingViewModel(fakeRepo)

        var navTutorial = false
        var navHome = false
        viewModel.onPlayClicked(
            onNavigateToTutorial = { navTutorial = true },
            onNavigateToHome = { navHome = true }
        )
        advanceUntilIdle()

        assertFalse(navTutorial)
        assertTrue(navHome)
        assertTrue(fakeRepo.userPreferencesFlow.first().isOnboardingCompleted)
    }

    @Test
    fun onboarding_onSkipClicked_marksCompletedAndTutorialSkipped() = runTest(testDispatcher) {
        val fakeRepo = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = false))
        val viewModel = OnboardingViewModel(fakeRepo)

        var navHome = false
        viewModel.onSkipClicked { navHome = true }
        advanceUntilIdle()

        assertTrue(navHome)
        val saved = fakeRepo.userPreferencesFlow.first()
        assertTrue(saved.isOnboardingCompleted)
        assertTrue(saved.isTutorialSkipped)
    }
}
