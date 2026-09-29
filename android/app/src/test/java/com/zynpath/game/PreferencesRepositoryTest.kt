package com.zynpath.game

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesRepositoryTest {

    @Test
    fun defaultPreferences_reflectGuestDefaults() = runTest {
        val repo = FakePreferencesRepository()
        val prefs = repo.userPreferencesFlow.first()

        assertFalse("Guest should not have completed onboarding by default", prefs.isOnboardingCompleted)
        assertFalse("Tutorial should not be marked completed by default", prefs.isTutorialCompleted)
        assertTrue("SFX should be enabled by default", prefs.isSfxEnabled)
        assertTrue("Music should be enabled by default", prefs.isMusicEnabled)
        assertTrue("Haptics should be enabled by default", prefs.isHapticsEnabled)
        assertFalse("Reduced motion should be false by default", prefs.isReducedMotion)
        assertEquals("en", prefs.selectedLanguage)
        assertEquals(1, prefs.lastSelectedWorld)
        assertEquals(1, prefs.lastSelectedLevel)
        assertEquals(3, prefs.freeHintsRemaining)
    }

    @Test
    fun preferenceSetters_updatePreferencesFlowCorrectly() = runTest {
        val repo = FakePreferencesRepository()

        repo.setOnboardingCompleted(true)
        assertTrue(repo.userPreferencesFlow.first().isOnboardingCompleted)

        repo.setTutorialCompleted(true)
        assertTrue(repo.userPreferencesFlow.first().isTutorialCompleted)

        repo.setSelectedLanguage("es")
        assertEquals("es", repo.userPreferencesFlow.first().selectedLanguage)

        repo.setLastSelectedWorld(3)
        assertEquals(3, repo.userPreferencesFlow.first().lastSelectedWorld)

        repo.setLastSelectedLevel(55)
        assertEquals(55, repo.userPreferencesFlow.first().lastSelectedLevel)

        repo.setFreeHintsRemaining(2)
        assertEquals(2, repo.userPreferencesFlow.first().freeHintsRemaining)
    }
}
