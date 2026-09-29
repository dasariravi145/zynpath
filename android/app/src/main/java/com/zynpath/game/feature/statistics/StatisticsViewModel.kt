package com.zynpath.game.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.analytics.model.PersonalAnalyticsReport
import com.zynpath.game.core.analytics.repository.PersonalAnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the personal analytics dashboard state, tab selection, and time filter.
 * Implements Prompt 30 Sections 9, 37, 53 & 59.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val analyticsRepository: PersonalAnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private var rawReport: PersonalAnalyticsReport? = null
    private var currentTab = StatisticsTab.OVERVIEW
    private var currentFilter = TimeFilter.ALL_TIME

    init {
        observeAnalytics()
    }

    fun selectTab(tab: StatisticsTab) {
        currentTab = tab
        updateSuccessState()
    }

    fun selectTimeFilter(filter: TimeFilter) {
        currentFilter = filter
        updateSuccessState()
    }

    fun refresh() {
        viewModelScope.launch {
            analyticsRepository.refreshCompetitiveAnalytics()
        }
    }

    private fun observeAnalytics() {
        analyticsRepository.observeAnalyticsReport()
            .onEach { report ->
                rawReport = report
                updateSuccessState()
            }
            .catch { error ->
                _uiState.value = StatisticsUiState.Error(
                    error.message ?: "Failed to compute personal analytics."
                )
            }
            .launchIn(viewModelScope)
    }

    private fun updateSuccessState() {
        val base = rawReport ?: return
        val filteredTrends = if (currentFilter.days > 0) {
            base.completionTrends.takeLast(currentFilter.days)
        } else {
            base.completionTrends
        }

        val effectiveReport = base.copy(completionTrends = filteredTrends)

        _uiState.value = StatisticsUiState.Success(
            report = effectiveReport,
            selectedTab = currentTab,
            timeFilter = currentFilter,
            isRefreshing = false
        )
    }
}
