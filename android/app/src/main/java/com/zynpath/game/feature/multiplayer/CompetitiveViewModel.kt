package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.achievement.AchievementRepository
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Lifecycle-aware ViewModel for Match History, Match Details, and Competitive Leaderboards.
 *
 * Implements Prompt 24 Sections 10, 14, 15, 17, 22, 29, 32, 38, 47, 50:
 * - Separates match history, match details, statistics, and leaderboard state.
 * - Handles bounded pagination without duplicate entries.
 * - Respects guest/unauthenticated state without fabricating results.
 * - Triggers idempotent competitive achievement evaluation on verified statistics arrival.
 */
@HiltViewModel
class CompetitiveViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val achievementRepository: AchievementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow(MatchHistoryUiState())
    val historyState: StateFlow<MatchHistoryUiState> = _historyState.asStateFlow()

    private val _detailsState = MutableStateFlow(MatchDetailsUiState())
    val detailsState: StateFlow<MatchDetailsUiState> = _detailsState.asStateFlow()

    private val _leaderboardState = MutableStateFlow(LeaderboardUiState())
    val leaderboardState: StateFlow<LeaderboardUiState> = _leaderboardState.asStateFlow()

    private val _statsSummaryState = MutableStateFlow(CompetitiveProfileSummaryUiState())
    val statsSummaryState: StateFlow<CompetitiveProfileSummaryUiState> = _statsSummaryState.asStateFlow()

    init {
        checkAuthAndLoadStats()
    }

    fun checkAuthAndLoadStats() {
        val isGuest = authRepository.authState.value != AuthState.AUTHENTICATED
        _statsSummaryState.update { it.copy(isGuest = isGuest) }
        if (!isGuest) {
            loadCompetitiveStats()
        }
    }

    // --- Match History Operations ---

    fun selectHistoryMode(mode: GameMode?) {
        _historyState.update { it.copy(selectedMode = mode, page = 0, items = emptyList()) }
        loadHistory(mode = mode, reset = true)
    }

    fun loadHistory(mode: GameMode? = _historyState.value.selectedMode, reset: Boolean = false) {
        viewModelScope.launch {
            val currentPage = if (reset) 0 else _historyState.value.page
            _historyState.update {
                it.copy(
                    isLoading = !reset || it.items.isEmpty(),
                    isRefreshing = reset && it.items.isNotEmpty(),
                    errorMessage = null
                )
            }

            val response = multiplayerRepository.getMatchHistory(mode = mode, page = currentPage, pageSize = 20)
            if (response != null) {
                _historyState.update { state ->
                    val combined = if (reset) response.items else (state.items + response.items).distinctBy { it.matchId }
                    state.copy(
                        items = combined,
                        page = response.page,
                        hasMore = response.hasMore,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            } else {
                _historyState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = if (it.items.isEmpty()) "Failed to load match history. Please check your connection." else null
                    )
                }
            }
        }
    }

    fun loadNextHistoryPage() {
        if (_historyState.value.isLoading || !_historyState.value.hasMore) return
        val nextPage = _historyState.value.page + 1
        _historyState.update { it.copy(page = nextPage) }
        loadHistory(reset = false)
    }

    // --- Match Details Operations ---

    fun loadMatchDetails(matchId: String) {
        viewModelScope.launch {
            _detailsState.value = MatchDetailsUiState(matchId = matchId, isLoading = true)
            val details = multiplayerRepository.getMatchDetails(matchId)
            if (details != null) {
                _detailsState.value = MatchDetailsUiState(
                    matchId = matchId,
                    details = details,
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _detailsState.value = MatchDetailsUiState(
                    matchId = matchId,
                    details = null,
                    isLoading = false,
                    errorMessage = "Unable to load match details. You may not be an authorized participant or the match was not found."
                )
            }
        }
    }

    // --- Competitive Statistics Operations ---

    fun loadCompetitiveStats() {
        viewModelScope.launch {
            _statsSummaryState.update { it.copy(isLoading = true, errorMessage = null) }
            val stats = multiplayerRepository.getCompetitiveStats()
            if (stats != null) {
                _statsSummaryState.update {
                    it.copy(stats = stats, isLoading = false, errorMessage = null)
                }
                // Prompt 24 Section 38 & 39: Evaluate verified competitive achievements idempotently
                achievementRepository.evaluateCompetitiveAchievements(stats)
            } else {
                _statsSummaryState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Competitive statistics unavailable"
                    )
                }
            }
        }
    }

    // --- Leaderboard Operations ---

    fun selectLeaderboardCategory(category: LeaderboardCategory) {
        if (_leaderboardState.value.selectedCategory == category) return
        _leaderboardState.update { it.copy(selectedCategory = category, page = 0, entries = emptyList()) }
        loadLeaderboard(category = category, reset = true)
    }

    fun selectLeaderboardPeriod(period: LeaderboardPeriod) {
        if (_leaderboardState.value.selectedPeriod == period) return
        _leaderboardState.update { it.copy(selectedPeriod = period, page = 0, entries = emptyList()) }
        loadLeaderboard(period = period, reset = true)
    }

    fun loadLeaderboard(
        category: LeaderboardCategory = _leaderboardState.value.selectedCategory,
        period: LeaderboardPeriod = _leaderboardState.value.selectedPeriod,
        reset: Boolean = false
    ) {
        viewModelScope.launch {
            val currentPage = if (reset) 0 else _leaderboardState.value.page
            _leaderboardState.update {
                it.copy(
                    isLoading = !reset || it.entries.isEmpty(),
                    isRefreshing = reset && it.entries.isNotEmpty(),
                    errorMessage = null
                )
            }

            val response = multiplayerRepository.getLeaderboard(
                category = category,
                period = period,
                page = currentPage,
                pageSize = 20
            )

            if (response != null) {
                _leaderboardState.update { state ->
                    val combined = if (reset) response.entries else (state.entries + response.entries).distinctBy { it.publicZynpathId }
                    state.copy(
                        selectedCategory = response.category,
                        selectedPeriod = response.period,
                        entries = combined,
                        myRank = response.myRank,
                        myMetricValue = response.myMetricValue,
                        page = response.page,
                        hasMore = response.hasMore,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            } else {
                _leaderboardState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = if (it.entries.isEmpty()) "Leaderboard currently unavailable." else null
                    )
                }
            }
        }
    }

    fun loadNextLeaderboardPage() {
        if (_leaderboardState.value.isLoading || !_leaderboardState.value.hasMore) return
        val nextPage = _leaderboardState.value.page + 1
        _leaderboardState.update { it.copy(page = nextPage) }
        loadLeaderboard(reset = false)
    }
}
