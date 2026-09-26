package com.zynpath.game

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.splash.SplashNavigationTarget
import com.zynpath.game.feature.splash.SplashViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

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
    fun splashRoutesToOnboarding_whenOnboardingNotCompleted() = runTest {
        val fakeRepo = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = false)
        )
        val viewModel = SplashViewModel(fakeRepo)

        advanceUntilIdle()

        assertEquals(SplashNavigationTarget.Onboarding, viewModel.navigationTarget.value)
    }

    @Test
    fun splashRoutesToHome_whenOnboardingAlreadyCompleted() = runTest {
        val fakeRepo = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = true)
        )
        val viewModel = SplashViewModel(fakeRepo)

        advanceUntilIdle()

        assertEquals(SplashNavigationTarget.Home, viewModel.navigationTarget.value)
    }
}
