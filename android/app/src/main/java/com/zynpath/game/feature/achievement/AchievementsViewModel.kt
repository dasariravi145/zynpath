package com.zynpath.game.feature.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.achievement.AchievementCategory
import com.zynpath.game.core.achievement.AchievementRegistry
import com.zynpath.game.core.achievement.AchievementRepository
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the Achievements list and category filters.
 *
 * Implements Prompt 17 Section 24 and Prompt 09/24.
 */
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val progressRepository: ProgressRepository? = null,
    private val preferencesRepository: PreferencesRepository? = null
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(AchievementCategory.ALL)

    private val totalStarsFlow: Flow<Int> =
        progressRepository?.observeTotalStarsEarned() ?: flowOf(0)

    private val isReducedMotionFlow: Flow<Boolean> =
        preferencesRepository?.userPreferencesFlow?.map { it.isReducedMotion } ?: flowOf(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AchievementsUiState> = _selectedCategory.flatMapLatest { category ->
        combine(
            achievementRepository.observeAchievements(category),
            achievementRepository.observeUnlockedCount(),
            totalStarsFlow,
            isReducedMotionFlow
        ) { list, unlockedCount, totalStars, isReducedMotion ->
            AchievementsUiState(
                selectedCategory = category,
                achievements = list,
                unlockedCount = unlockedCount,
                totalCount = AchievementRegistry.ALL_ACHIEVEMENTS.size,
                totalStars = totalStars,
                isReducedMotion = isReducedMotion,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AchievementsUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            achievementRepository.evaluateAll()
        }
    }

    fun selectCategory(category: AchievementCategory) {
        _selectedCategory.value = category
    }

    fun retry() {
        viewModelScope.launch {
            achievementRepository.evaluateAll()
        }
    }
}
