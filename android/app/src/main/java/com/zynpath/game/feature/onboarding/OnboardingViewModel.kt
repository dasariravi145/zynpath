package com.zynpath.game.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val detail: String,
    val stepIndicator: String
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val slides = listOf(
        OnboardingSlide(
            title = "Connect in Order",
            subtitle = "Start at checkpoint 1",
            detail = "Traverse sequentially from 1 to 2, 3, and onward to the final number. Never skip ahead or visit higher numbers out of order.",
            stepIndicator = "1 / 5"
        ),
        OnboardingSlide(
            title = "One Continuous Path",
            subtitle = "Draw without lifting or crossing",
            detail = "Move strictly between horizontally or vertically adjacent cells. The line cannot branch, split, or cross over itself.",
            stepIndicator = "2 / 5"
        ),
        OnboardingSlide(
            title = "Full Grid Coverage",
            subtitle = "Every cell must be visited",
            detail = "Connecting checkpoints is not enough! A puzzle is only solved when 100% of required grid cells are covered exactly once.",
            stepIndicator = "3 / 5"
        ),
        OnboardingSlide(
            title = "Respect the Walls",
            subtitle = "Navigate blocked connections",
            detail = "Interior wall barriers block direct orthogonal passage. Route your path around walls to reach open corridors.",
            stepIndicator = "4 / 5"
        ),
        OnboardingSlide(
            title = "Solo & Beyond",
            subtitle = "Offline-first puzzle logic",
            detail = "Enjoy 300 base levels offline as a guest. Challenge friends or compete in real-time Mini Leagues whenever you choose.",
            stepIndicator = "5 / 5"
        )
    )

    private val _currentSlideIndex = MutableStateFlow(0)
    val currentSlideIndex: StateFlow<Int> = _currentSlideIndex.asStateFlow()

    fun nextSlide(onCompleted: () -> Unit) {
        if (_currentSlideIndex.value < slides.size - 1) {
            _currentSlideIndex.value += 1
        } else {
            completeOnboarding(onCompleted)
        }
    }

    fun previousSlide() {
        if (_currentSlideIndex.value > 0) {
            _currentSlideIndex.value -= 1
        }
    }

    fun skipOnboarding(onCompleted: () -> Unit) {
        completeOnboarding(onCompleted)
    }

    private fun completeOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
            onCompleted()
        }
    }
}
