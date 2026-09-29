package com.zynpath.game.feature.daily

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel managing official daily leaderboard state and pagination.
 *
 * Implements Prompt 25 Sections 36-40, 44 & 52.
 */
@HiltViewModel
class DailyLeaderboardViewModel @Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val clock: DailyChallengeClock,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialDateKey: String = savedStateHandle.get<String>("dateKey")
        ?.takeIf { it.isNotBlank() } ?: clock.currentUtcDateKey()

    private var currentDateKey: String = initialDateKey

    private val _uiState = MutableStateFlow<DailyLeaderboardUiState>(
        DailyLeaderboardUiState.Loading(initialDateKey)
    )
    val uiState: StateFlow<DailyLeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard(currentDateKey)
    }

    fun loadLeaderboard(dateKey: String = currentDateKey) {
        currentDateKey = dateKey
        _uiState.value = DailyLeaderboardUiState.Loading(dateKey)

        viewModelScope.launch {
            val result = dailyChallengeRepository.getDailyLeaderboard(
                dateKey = dateKey,
                page = 0,
                pageSize = 50
            )

            result.fold(
                onSuccess = { response ->
                    if (response.entries.isEmpty()) {
                        _uiState.value = DailyLeaderboardUiState.Empty(
                            dateKey = dateKey,
                            challengeId = response.challengeId
                        )
                    } else {
                        _uiState.value = DailyLeaderboardUiState.Success(
                            dateKey = dateKey,
                            response = response
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = DailyLeaderboardUiState.Error(
                        dateKey = dateKey,
                        message = error.localizedMessage ?: "Failed to load leaderboard"
                    )
                }
            )
        }
    }

    fun refresh() {
        val current = _uiState.value
        if (current is DailyLeaderboardUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            val result = dailyChallengeRepository.getDailyLeaderboard(
                dateKey = currentDateKey,
                page = 0,
                pageSize = 50
            )

            result.fold(
                onSuccess = { response ->
                    if (response.entries.isEmpty()) {
                        _uiState.value = DailyLeaderboardUiState.Empty(
                            dateKey = currentDateKey,
                            challengeId = response.challengeId
                        )
                    } else {
                        _uiState.value = DailyLeaderboardUiState.Success(
                            dateKey = currentDateKey,
                            response = response,
                            isRefreshing = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = DailyLeaderboardUiState.Error(
                        dateKey = currentDateKey,
                        message = error.localizedMessage ?: "Failed to refresh leaderboard"
                    )
                }
            )
        }
    }

    fun navigateDate(offsetDays: Long) {
        try {
            val parsed = LocalDate.parse(currentDateKey)
            val newDate = parsed.plusDays(offsetDays)
            val today = clock.currentUtcDate()

            // Do not navigate into the future
            if (newDate.isAfter(today)) return

            val newDateKey = newDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            loadLeaderboard(newDateKey)
        } catch (e: Exception) {
            // Ignore parse errors
        }
    }

    val isToday: Boolean
        get() = currentDateKey == clock.currentUtcDateKey()
}
