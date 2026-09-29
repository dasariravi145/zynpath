package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardEntry
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod
import com.zynpath.game.core.multiplayer.model.MatchDetails
import com.zynpath.game.core.multiplayer.model.MatchHistoryItem

/**
 * UI State models for Match History, Match Details, and Leaderboards.
 *
 * Implements Prompt 24 Sections 10, 14, 15, 32, 47, 50.
 */
data class MatchHistoryUiState(
    val selectedMode: GameMode? = null,
    val items: List<MatchHistoryItem> = emptyList(),
    val page: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

data class MatchDetailsUiState(
    val matchId: String = "",
    val details: MatchDetails? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class LeaderboardUiState(
    val selectedCategory: LeaderboardCategory = LeaderboardCategory.QUICK_DUEL_WINS,
    val selectedPeriod: LeaderboardPeriod = LeaderboardPeriod.ALL_TIME,
    val entries: List<LeaderboardEntry> = emptyList(),
    val myRank: Int? = null,
    val myMetricValue: Long? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

data class CompetitiveProfileSummaryUiState(
    val stats: CompetitiveStats? = null,
    val isLoading: Boolean = false,
    val isGuest: Boolean = true,
    val errorMessage: String? = null
)
