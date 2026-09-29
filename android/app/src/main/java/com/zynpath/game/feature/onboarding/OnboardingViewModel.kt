package com.zynpath.game.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    fun onPlayClicked(onNavigateToTutorial: () -> Unit, onNavigateToHome: () -> Unit) {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            preferencesRepository.setOnboardingCompleted(true)
            if (!prefs.isTutorialCompleted && !prefs.isTutorialSkipped) {
                onNavigateToTutorial()
            } else {
                onNavigateToHome()
            }
        }
    }

    fun onHowToPlayClicked(onNavigateToTutorial: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
            onNavigateToTutorial()
        }
    }

    fun onSkipClicked(onNavigateToHome: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
            preferencesRepository.setTutorialSkipped(true)
            onNavigateToHome()
        }
    }
}
