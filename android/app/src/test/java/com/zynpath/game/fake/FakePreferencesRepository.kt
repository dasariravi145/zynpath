package com.zynpath.game.fake

import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesRepository(
    initialPreferences: UserPreferences = UserPreferences()
) : PreferencesRepository {

    private val _preferences = MutableStateFlow(initialPreferences)
    override val userPreferencesFlow: Flow<UserPreferences> = _preferences.asStateFlow()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        _preferences.value = _preferences.value.copy(isOnboardingCompleted = completed)
    }

    override suspend fun setSfxEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isSfxEnabled = enabled)
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isMusicEnabled = enabled)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isHapticsEnabled = enabled)
    }

    override suspend fun setThemePreference(theme: String) {
        _preferences.value = _preferences.value.copy(themePreference = theme)
    }

    override suspend fun setReducedMotion(reduced: Boolean) {
        _preferences.value = _preferences.value.copy(isReducedMotion = reduced)
    }

    override suspend fun setPremium(isPremium: Boolean) {
        _preferences.value = _preferences.value.copy(isPremium = isPremium)
    }
}
