package com.zynpath.game

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.settings.SettingsViewModel
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
class SettingsViewModelTest {

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
    fun settingsToggles_updatePreferencesCorrectly() = runTest {
        val fakeRepo = FakePreferencesRepository(
            UserPreferences(
                isSfxEnabled = true,
                isMusicEnabled = true,
                isHapticsEnabled = true,
                isReducedMotion = false,
                themePreference = "FOREST_NAVY"
            )
        )
        val viewModel = SettingsViewModel(fakeRepo)
        advanceUntilIdle()

        // Toggle SFX
        viewModel.toggleSfx()
        advanceUntilIdle()
        assertFalse(fakeRepo.userPreferencesFlow.first().isSfxEnabled)

        // Toggle Music
        viewModel.toggleMusic()
        advanceUntilIdle()
        assertFalse(fakeRepo.userPreferencesFlow.first().isMusicEnabled)

        // Toggle Haptics
        viewModel.toggleHaptics()
        advanceUntilIdle()
        assertFalse(fakeRepo.userPreferencesFlow.first().isHapticsEnabled)

        // Toggle Reduced Motion
        viewModel.toggleReducedMotion()
        advanceUntilIdle()
        assertTrue(fakeRepo.userPreferencesFlow.first().isReducedMotion)

        // Change Theme
        viewModel.setThemePreference("NEON_CYAN")
        advanceUntilIdle()
        assertEquals("NEON_CYAN", fakeRepo.userPreferencesFlow.first().themePreference)
    }
}
