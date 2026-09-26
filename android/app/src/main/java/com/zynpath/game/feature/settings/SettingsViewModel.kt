package com.zynpath.game.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun toggleSfx() {
        viewModelScope.launch {
            val current = userPreferences.value.isSfxEnabled
            preferencesRepository.setSfxEnabled(!current)
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            val current = userPreferences.value.isMusicEnabled
            preferencesRepository.setMusicEnabled(!current)
        }
    }

    fun toggleHaptics() {
        viewModelScope.launch {
            val current = userPreferences.value.isHapticsEnabled
            preferencesRepository.setHapticsEnabled(!current)
        }
    }

    fun toggleReducedMotion() {
        viewModelScope.launch {
            val current = userPreferences.value.isReducedMotion
            preferencesRepository.setReducedMotion(!current)
        }
    }

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            preferencesRepository.setThemePreference(theme)
        }
    }
}
