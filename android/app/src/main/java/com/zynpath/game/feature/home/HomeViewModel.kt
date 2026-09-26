package com.zynpath.game.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val guestTag: String = "ZYN-7749",
    val isGuest: Boolean = true,
    val completedLevelsCount: Int = 0,
    val totalStars: Int = 0,
    val isPremium: Boolean = false,
    val isOfflineReady: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository,
    levelProgressDao: LevelProgressDao
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        preferencesRepository.userPreferencesFlow,
        levelProgressDao.getCompletedLevelCount(),
        levelProgressDao.getTotalStarsEarned()
    ) { preferences, completedCount, totalStars ->
        val shortTag = if (preferences.guestUuid.length >= 8) {
            "ZYN-" + preferences.guestUuid.substring(0, 4).uppercase()
        } else {
            "ZYN-7749"
        }
        HomeUiState(
            guestTag = shortTag,
            isGuest = true,
            completedLevelsCount = completedCount,
            totalStars = totalStars ?: 0,
            isPremium = preferences.isPremium,
            isOfflineReady = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
