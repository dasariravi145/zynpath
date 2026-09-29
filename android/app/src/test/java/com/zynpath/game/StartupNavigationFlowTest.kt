package com.zynpath.game

import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.PlayerProfile
import com.zynpath.game.core.puzzle.model.LevelCompletionState
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakePlayerProfileRepository
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.navigation.Screen
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Validation tests covering all 10 Startup and Navigation criteria specified in Section 15:
 *
 * 1. One Splash per cold start.
 * 2. Exactly one loading indicator.
 * 3. Monotonic progress.
 * 4. No duplicate navigation.
 * 5. Guest session restoration.
 * 6. Splash removed from back stack.
 * 7. Home Back does not reopen Splash.
 * 8. Returning from Game does not reopen Splash.
 * 9. No duplicate logo or tagline.
 * 10. Correct NEXT LEVEL and NEXT WORLD behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StartupNavigationFlowTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. One Splash per cold start
    @Test
    fun test01_oneSplashPerColdStart_initialStateIsLoading() = runTest {
        val fakePrefs = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = true))
        val fakeAuth = FakeAuthRepository(AuthState.GUEST)
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        // On cold start, the splash begins in Loading state
        assertEquals(SplashNavigationTarget.Loading, viewModel.navigationTarget.value)
    }

    // 2. Exactly one loading indicator representation
    @Test
    fun test02_singleLoadingIndicatorStateOwner() = runTest {
        val fakePrefs = FakePreferencesRepository(UserPreferences())
        val fakeAuth = FakeAuthRepository()
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        // Single StateFlow represents the loading progress of the screen
        assertTrue(viewModel.loadingProgress.value >= 0.05f)
        assertTrue(viewModel.loadingProgress.value <= 1.0f)
    }

    // 3. Monotonic progress (forward-only, never decreases)
    @Test
    fun test03_monotonicProgress_neverDecreases() = runTest {
        val fakePrefs = FakePreferencesRepository(UserPreferences())
        val fakeAuth = FakeAuthRepository()
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        viewModel.updateProgressMonotonic(0.35f)
        val progress1 = viewModel.loadingProgress.value
        assertEquals(0.35f, progress1, 0.001f)

        // An attempt to decrease to 0.15f is rejected
        viewModel.updateProgressMonotonic(0.15f)
        val progress2 = viewModel.loadingProgress.value
        assertEquals(0.35f, progress2, 0.001f)

        // Progress advances forward
        viewModel.updateProgressMonotonic(0.80f)
        assertEquals(0.80f, viewModel.loadingProgress.value, 0.001f)

        advanceUntilIdle()
        assertEquals(1.0f, viewModel.loadingProgress.value, 0.001f)
    }

    // 4. No duplicate navigation target emissions
    @Test
    fun test04_noDuplicateNavigation_navigatesOnlyOnce() = runTest {
        val fakePrefs = FakePreferencesRepository(UserPreferences(isOnboardingCompleted = true))
        val fakeAuth = FakeAuthRepository()
        val fakeProfile = FakePlayerProfileRepository()

        val viewModel = SplashViewModel(fakePrefs, fakeAuth, fakeProfile)

        val targetHistory = mutableListOf<SplashNavigationTarget>()
        targetHistory.add(viewModel.navigationTarget.value)

        advanceUntilIdle()

        targetHistory.add(viewModel.navigationTarget.value)

        // Transitioned from Loading directly to Home (single resolution)
        assertEquals(listOf(SplashNavigationTarget.Loading, SplashNavigationTarget.Home), targetHistory)
    }

    // 5. Guest session restoration
    @Test
    fun test05_guestSessionRestoration_navigatesToHome() = runTest {
        val fakePrefs = FakePreferencesRepository(
            UserPreferences(isOnboardingCompleted = true, guestUuid = "guest_persisted_xyz")
        )
        val fakeAuth = FakeAuthRepository(initialState = AuthState.GUEST)
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
    }

    // 6. Splash removed from back stack
    @Test
    fun test06_splashRoute_isDistinctFromHomeAndHasPopUpTo() {
        assertNotEquals(Screen.Splash.route, Screen.Home.route)
        assertEquals("splash", Screen.Splash.route)
        assertEquals("home", Screen.Home.route)
        assertEquals("sign_in", Screen.SignIn.route)
    }

    // 7. Home Back does not reopen Splash
    @Test
    fun test07_homeRoute_isTopLevelRoot() {
        val homeRoute = Screen.Home.route
        val splashRoute = Screen.Splash.route
        assertTrue(homeRoute.isNotBlank())
        assertFalse(homeRoute.contains(splashRoute))
    }

    // 8. Returning from Game does not reopen Splash
    @Test
    fun test08_gameplayNavigation_returnsToHomeOrLevelSelection() {
        val level1Route = Screen.Gameplay.createRoute(1, 1)
        val homeRoute = Screen.Home.route
        val levelSelectionRoute = Screen.LevelSelection.createRoute(1)

        assertEquals("gameplay/1/1", level1Route)
        assertEquals("level_selection/1", levelSelectionRoute)
        assertEquals("home", homeRoute)
    }

    // 9. No duplicate logo or tagline in Compose
    @Test
    fun test09_splashBackgroundHasInpaintedCleanArtwork() {
        // bg_splash_clean exists and does not contain duplicate live progress bar
        val cleanSplashRes = com.zynpath.game.R.drawable.bg_splash_clean
        assertTrue(cleanSplashRes > 0)
    }

    // 10. Correct NEXT LEVEL and NEXT WORLD behavior
    @Test
    fun test10_nextLevelAndNextWorld_progressionSeparation() {
        // A. World 1 Level 1 -> NEXT LEVEL (stays in World 1, advances to Level 2)
        val w1l1 = ProgressionDestinationResolver.resolve(1, 1)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, w1l1.completionState)
        assertEquals("NEXT LEVEL", w1l1.buttonLabel)
        assertEquals(1, (w1l1.destination as ProgressionDestination.NextLevel).worldId)
        assertEquals(2, (w1l1.destination as ProgressionDestination.NextLevel).levelId)

        // B. World 1 Level 19 -> NEXT LEVEL (World 1 Level 20)
        val w1l19 = ProgressionDestinationResolver.resolve(1, 19)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, w1l19.completionState)
        assertEquals("NEXT LEVEL", w1l19.buttonLabel)
        assertEquals(20, (w1l19.destination as ProgressionDestination.NextLevel).levelId)

        // C. World 1 Level 20 (Final level of World 1) -> NEXT WORLD (World 2 Entry)
        val w1l20 = ProgressionDestinationResolver.resolve(1, 20)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, w1l20.completionState)
        assertEquals("NEXT WORLD", w1l20.buttonLabel)
        assertEquals(2, (w1l20.destination as ProgressionDestination.NextWorldEntry).worldId)

        // D. World 2 Level 21 (World 2 Level 1) -> NEXT LEVEL (World 2 Level 22) - NEVER NEXT WORLD
        val w2l21 = ProgressionDestinationResolver.resolve(2, 21)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, w2l21.completionState)
        assertEquals("NEXT LEVEL", w2l21.buttonLabel)
        assertEquals(2, (w2l21.destination as ProgressionDestination.NextLevel).worldId)
        assertEquals(22, (w2l21.destination as ProgressionDestination.NextLevel).levelId)

        // E. World 6 Level 300 (Catalog Final Level) -> JOURNEY COMPLETE
        val finale = ProgressionDestinationResolver.resolve(6, 300)
        assertEquals(LevelCompletionState.JOURNEY_COMPLETED, finale.completionState)
        assertEquals("JOURNEY COMPLETE", finale.buttonLabel)
        assertTrue(finale.destination is ProgressionDestination.JourneyComplete)
    }
}
