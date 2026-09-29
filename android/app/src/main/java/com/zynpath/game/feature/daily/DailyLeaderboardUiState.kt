package com.zynpath.game.feature.daily

import com.zynpath.game.core.puzzle.daily.DailyLeaderboardResponse

/**
 * UI State for Daily Leaderboard screen.
 *
 * Implements Prompt 25 Section 36, 39, 40 & 44.
 */
sealed interface DailyLeaderboardUiState {
    data class Loading(val dateKey: String) : DailyLeaderboardUiState

    data class Empty(
        val dateKey: String,
        val challengeId: String,
        val message: String = "No validated competitive completions recorded yet for this date."
    ) : DailyLeaderboardUiState

    data class Error(
        val dateKey: String,
        val message: String
    ) : DailyLeaderboardUiState

    data class Success(
        val dateKey: String,
        val response: DailyLeaderboardResponse,
        val isRefreshing: Boolean = false
    ) : DailyLeaderboardUiState
}
