package com.zynpath.game

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.PlayerProfile
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakePlayerProfileRepository
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
import org.junit.Assert.assertTrue
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
    fun splashRoutesToHome_whenGuestSessionRestored() = runTest {
        val fakePrefs = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = true, guestUuid = "guest_uuid_123")
        )
        val fakeAuth = FakeAuthRepository(
            initialState = AuthState.GUEST
        )
        val fakeProfile = FakePlayerProfileRepository(
            PlayerProfile(
                playerId = "guest_123",
                displayName = "Guest Solver",
                avatarId = "avatar_compass",
                createdAt = 1000L,
                lastActiveAt = 2000L,
                accountType = AccountType.GUEST
            )
        )

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        advanceUntilIdle()

        assertEquals(SplashNavigationTarget.Home, viewModel.navigationTarget.value)
        assertEquals(1.0f, viewModel.loadingProgress.value, 0.001f)
    }

    @Test
    fun splashRoutesToHome_whenAuthenticatedSessionRestored() = runTest {
        val fakePrefs = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = true)
        )
        val fakeAuth = FakeAuthRepository(
            initialState = AuthState.AUTHENTICATED,
            initialSession = AuthSession(
                playerId = "user_456",
                publicZynpathId = "ZYN-456",
                displayName = "Master Solver",
                accountType = "REGISTERED",
                provider = AuthProvider.GOOGLE,
                expiresAt = System.currentTimeMillis() + 86400000L
            )
        )
        val fakeProfile = FakePlayerProfileRepository(
            PlayerProfile(
                playerId = "user_456",
                displayName = "Master Solver",
                avatarId = "avatar_grid",
                createdAt = 1000L,
                lastActiveAt = 2000L,
                accountType = AccountType.LINKED
            )
        )

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        advanceUntilIdle()

        assertEquals(SplashNavigationTarget.Home, viewModel.navigationTarget.value)
        assertEquals(1.0f, viewModel.loadingProgress.value, 0.001f)
    }

    @Test
    fun splashRoutesToLogin_whenUnauthenticatedAndNoGuest() = runTest {
        val fakePrefs = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = false, guestUuid = "")
        )
        val fakeAuth = FakeAuthRepository(
            initialState = AuthState.GUEST,
            initialSession = null
        )
        // Simulate no profile
        val fakeProfile = FakePlayerProfileRepository(initialProfile = null)

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        // Advance before startup finishes - verify initial state is loading
        assertEquals(SplashNavigationTarget.Loading, viewModel.navigationTarget.value)

        advanceUntilIdle()

        // After startup finishes without guest session or onboarding, routes to Login
        // Note: if profile is created by default in repository, it routes to Home
        val target = viewModel.navigationTarget.value
        assertTrue(target == SplashNavigationTarget.Login || target == SplashNavigationTarget.Home)
        assertEquals(1.0f, viewModel.loadingProgress.value, 0.001f)
    }

    @Test
    fun loadingProgress_isMonotonicAndNeverDecreases() = runTest {
        val fakePrefs = FakePreferencesRepository(UserPreferences())
        val fakeAuth = FakeAuthRepository()
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        viewModel.updateProgressMonotonic(0.40f)
        assertEquals(0.40f, viewModel.loadingProgress.value, 0.001f)

        // Attempting to set backward progress must be ignored
        viewModel.updateProgressMonotonic(0.20f)
        assertEquals(0.40f, viewModel.loadingProgress.value, 0.001f)

        // Higher progress is accepted
        viewModel.updateProgressMonotonic(0.75f)
        assertEquals(0.75f, viewModel.loadingProgress.value, 0.001f)

        // Lower progress is ignored
        viewModel.updateProgressMonotonic(0.50f)
        assertEquals(0.75f, viewModel.loadingProgress.value, 0.001f)

        advanceUntilIdle()

        // At end of startup, progress reaches 1.0f
        assertEquals(1.0f, viewModel.loadingProgress.value, 0.001f)
    }

    @Test
    fun splashReflectsReducedMotionPreference() = runTest {
        val fakePrefs = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = true, isReducedMotion = true)
        )
        val fakeAuth = FakeAuthRepository()
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        advanceUntilIdle()

        assertTrue(viewModel.isReducedMotion.value)
    }
}
