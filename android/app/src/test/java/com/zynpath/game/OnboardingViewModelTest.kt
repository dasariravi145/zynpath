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
    fun onboarding_advancesSlidesCorrectly() = runTest {
        val fakeRepo = FakePreferencesRepository()
        val viewModel = OnboardingViewModel(fakeRepo)

        assertEquals(0, viewModel.currentSlideIndex.value)
        assertEquals("1 / 5", viewModel.slides[0].stepIndicator)

        var completed = false
        viewModel.nextSlide { completed = true }
        assertEquals(1, viewModel.currentSlideIndex.value)

        viewModel.previousSlide()
        assertEquals(0, viewModel.currentSlideIndex.value)
    }

    @Test
    fun onboarding_completesAndPersistsToDataStore_onFinalSlide() = runTest {
        val fakeRepo = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = false))
        val viewModel = OnboardingViewModel(fakeRepo)

        var completionInvoked = false
        // Advance through all 5 slides
        repeat(viewModel.slides.size) {
            viewModel.nextSlide { completionInvoked = true }
        }

        advanceUntilIdle()

        assertTrue(completionInvoked)
        val savedPreferences = fakeRepo.userPreferencesFlow.first()
        assertTrue(savedPreferences.isOnboardingCompleted)
    }

    @Test
    fun onboarding_skipImmediatelyMarksCompleted() = runTest {
        val fakeRepo = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = false))
        val viewModel = OnboardingViewModel(fakeRepo)

        var completionInvoked = false
        viewModel.skipOnboarding { completionInvoked = true }

        advanceUntilIdle()

        assertTrue(completionInvoked)
        val savedPreferences = fakeRepo.userPreferencesFlow.first()
        assertTrue(savedPreferences.isOnboardingCompleted)
    }
}
