package com.zynpath.game.feature.statistics

import com.zynpath.game.core.analytics.model.PersonalAnalyticsReport

/**
 * Tab definitions for the Personal Statistics screen.
 * Implements Prompt 30 Section 37.
 */
enum class StatisticsTab(val title: String) {
    OVERVIEW("Overview"),
    SOLO("Solo"),
    DAILY("Daily"),
    COMPETITIVE("Competitive"),
    PREMIUM_INSIGHTS("Premium")
}

/**
 * Time boundary filters for trend calculations.
 * Implements Prompt 30 Section 53.
 */
enum class TimeFilter(val title: String, val days: Int) {
    LAST_7_DAYS("7 Days", 7),
    LAST_30_DAYS("30 Days", 30),
    ALL_TIME("All Time", 0)
}

/**
 * UI State for the Personal Statistics dashboard.
 */
sealed interface StatisticsUiState {
    data object Loading : StatisticsUiState

    data class Success(
        val report: PersonalAnalyticsReport,
        val selectedTab: StatisticsTab = StatisticsTab.OVERVIEW,
        val timeFilter: TimeFilter = TimeFilter.ALL_TIME,
        val isRefreshing: Boolean = false
    ) : StatisticsUiState

    data class Error(val message: String) : StatisticsUiState
}
