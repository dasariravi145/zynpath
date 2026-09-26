package com.zynpath.game.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNavigationTarget {
    data object Loading : SplashNavigationTarget
    data object Onboarding : SplashNavigationTarget
    data object Home : SplashNavigationTarget
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<SplashNavigationTarget>(SplashNavigationTarget.Loading)
    val navigationTarget: StateFlow<SplashNavigationTarget> = _navigationTarget.asStateFlow()

    init {
        determineNavigation()
    }

    private fun determineNavigation() {
        viewModelScope.launch {
            val preferences = preferencesRepository.userPreferencesFlow.first()
            if (preferences.isOnboardingCompleted) {
                _navigationTarget.value = SplashNavigationTarget.Home
            } else {
                _navigationTarget.value = SplashNavigationTarget.Onboarding
            }
        }
    }
}
