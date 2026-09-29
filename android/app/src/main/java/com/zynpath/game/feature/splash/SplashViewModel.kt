package com.zynpath.game.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNavigationTarget {
    data object Loading : SplashNavigationTarget
    data object Onboarding : SplashNavigationTarget
    data object Login : SplashNavigationTarget
    data object Home : SplashNavigationTarget
}

data class SplashUiState(
    val navigationTarget: SplashNavigationTarget = SplashNavigationTarget.Loading,
    val loadingProgress: Float = 0.05f,
    val startupStatus: String = "Initializing puzzle engine...",
    val isReducedMotion: Boolean = false
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<SplashNavigationTarget>(SplashNavigationTarget.Loading)
    val navigationTarget: StateFlow<SplashNavigationTarget> = _navigationTarget.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0.10f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    private val _startupStatus = MutableStateFlow("Initializing puzzle engine...")
    val startupStatus: StateFlow<String> = _startupStatus.asStateFlow()

    val isReducedMotion: StateFlow<Boolean> = preferencesRepository.userPreferencesFlow
        .map { it.isReducedMotion }
        .catch { emit(false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        performStartupInitialization()
    }

    /**
     * Enforces strictly forward-only (monotonic) progress.
     */
    fun updateProgressMonotonic(newProgress: Float) {
        val clamped = newProgress.coerceIn(0f, 1f)
        if (clamped > _loadingProgress.value) {
            _loadingProgress.value = clamped
        }
    }

    private fun performStartupInitialization() {
        viewModelScope.launch {
            try {
                // Phase 1: Initialize local puzzle engine & storage (0% -> 25%)
                updateProgressMonotonic(0.25f)
                _startupStatus.value = "Initializing puzzle engine..."
                delay(200)

                // Phase 2: Restore session and existing guest progress (25% -> 55%)
                updateProgressMonotonic(0.55f)
                _startupStatus.value = "Restoring player progress..."
                val sessionRestored = try {
                    authRepository.restoreSession()
                } catch (_: Exception) {
                    false
                }

                // Phase 3: Check preferences and player profile (55% -> 85%)
                updateProgressMonotonic(0.85f)
                _startupStatus.value = "Verifying player progress..."
                val preferences = preferencesRepository.userPreferencesFlow.first()
                val profile = try {
                    playerProfileRepository.getProfile()
                } catch (_: Exception) {
                    null
                }

                // Phase 4: Ready (100%) and determine destination
                updateProgressMonotonic(1.0f)
                _startupStatus.value = "Entering the game world..."
                delay(150)

                val currentAuthState = authRepository.authState.value
                val hasCompletedOnboarding = preferences.isOnboardingCompleted
                val hasExistingGuest = profile != null || preferences.guestUuid.isNotBlank()

                // If user has a valid existing guest session or is authenticated, continue to Home
                if (hasCompletedOnboarding || hasExistingGuest || currentAuthState == AuthState.AUTHENTICATED) {
                    _navigationTarget.value = SplashNavigationTarget.Home
                } else {
                    _navigationTarget.value = SplashNavigationTarget.Login
                }
            } catch (_: Exception) {
                // Fail-safe recovery: always monotonic to 1.0f, safe navigation to Login or Home
                updateProgressMonotonic(1.0f)
                _navigationTarget.value = SplashNavigationTarget.Login
            }
        }
    }
}
