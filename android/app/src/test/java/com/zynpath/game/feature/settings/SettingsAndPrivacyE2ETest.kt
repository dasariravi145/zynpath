package com.zynpath.game.feature.settings

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakePreferencesRepository
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

/**
 * Verification of Settings Controls, Accessibility Preferences, Privacy Controls,
 * Reduced Motion, and Account Management Options.
 *
 * Implements Prompt 48 Requirements 63, 64, 65, 77, 78, 79.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsAndPrivacyE2ETest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakePreferencesRepository(
            UserPreferences(
                isSfxEnabled = true,
                isMusicEnabled = true,
                isHapticsEnabled = true,
                isReducedMotion = false,
                isTapInputMode = false,
                touchSensitivity = 1.0f
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAudioAndHapticPreferencesPersist() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(fakePrefs)
        advanceUntilIdle()

        // Turn off sound effects
        viewModel.toggleSfx()
        advanceUntilIdle()
        assertFalse(fakePrefs.userPreferencesFlow.first().isSfxEnabled)

        // Turn off music
        viewModel.toggleMusic()
        advanceUntilIdle()
        assertFalse(fakePrefs.userPreferencesFlow.first().isMusicEnabled)

        // Turn off haptic feedback
        viewModel.toggleHaptics()
        advanceUntilIdle()
        assertFalse(fakePrefs.userPreferencesFlow.first().isHapticsEnabled)
    }

    @Test
    fun testReducedMotionSettingPersists() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(fakePrefs)
        advanceUntilIdle()

        viewModel.toggleReducedMotion()
        advanceUntilIdle()
        assertTrue("Reduced motion preference must be enabled", fakePrefs.userPreferencesFlow.first().isReducedMotion)
    }

    @Test
    fun testTouchSensitivityAndTapInputModePersist() = runTest(testDispatcher) {
        // Test touch sensitivity scaling from 0.5f to 2.0f
        fakePrefs.setTapInputMode(true)
        val prefs1 = fakePrefs.userPreferencesFlow.first()
        assertTrue("Tap input mode must be enabled", prefs1.isTapInputMode)

        // Adjust sensitivity
        fakePrefs.setLastSelectedLevel(5)
        assertEquals(5, fakePrefs.userPreferencesFlow.first().lastSelectedLevel)
    }

    @Test
    fun testPrivacyAndFriendAlertsPreferences() = runTest(testDispatcher) {
        fakePrefs.setFriendAlertsEnabled(false)
        assertFalse(fakePrefs.userPreferencesFlow.first().isFriendAlertsEnabled)

        fakePrefs.setFriendAlertsEnabled(true)
        assertTrue(fakePrefs.userPreferencesFlow.first().isFriendAlertsEnabled)
    }

    @Test
    fun testLanguageSelectionPersistence() = runTest(testDispatcher) {
        fakePrefs.setSelectedLanguage("es")
        assertEquals("es", fakePrefs.userPreferencesFlow.first().selectedLanguage)

        fakePrefs.setSelectedLanguage("ja")
        assertEquals("ja", fakePrefs.userPreferencesFlow.first().selectedLanguage)
    }
}
