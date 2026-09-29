package com.zynpath.game.feature.premium.packs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.premium.FeatureAccessPolicy
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackLevelsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val packRepository: PremiumPackRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository
) : ViewModel() {

    private val packId: String = checkNotNull(savedStateHandle.get<String>("packId")) {
        "packId argument missing in SavedStateHandle"
    }

    private val _uiState = MutableStateFlow<PackLevelsUiState>(PackLevelsUiState.Loading)
    val uiState: StateFlow<PackLevelsUiState> = _uiState.asStateFlow()

    init {
        loadPackLevels()
    }

    private fun loadPackLevels() {
        viewModelScope.launch {
            val manifest = packRepository.getPackManifest(packId)
            if (manifest == null) {
                _uiState.value = PackLevelsUiState.Error("Pack not found: $packId")
                return@launch
            }

            combine(
                packRepository.observePackLevelProgress(packId),
                entitlementRepository.entitlement
            ) { progressList, entitlement ->
                val progressMap = progressList.associateBy { it.levelIndex }
                val isFeatureUnlocked = FeatureAccessPolicy.isFeatureUnlocked(
                    featureKey = PremiumFeatureKey.PREMIUM_SOLO_PACKS,
                    gameMode = null,
                    entitlement = entitlement
                )

                var completedCount = 0
                val levelItems = manifest.puzzles.mapIndexed { index, puzzleRef ->
                    val levelIdx = puzzleRef.levelIndex
                    val prog = progressMap[levelIdx]
                    val isDone = prog?.isCompleted == true
                    if (isDone) completedCount++

                    // First level is unlocked if pack is allowed; subsequent levels unlocked if previous is completed
                    val isPrevCompleted = index == 0 || (progressMap[manifest.puzzles[index - 1].levelIndex]?.isCompleted == true)
                    val isUnlocked = isFeatureUnlocked && isPrevCompleted

                    PackLevelItem(
                        levelIndex = levelIdx,
                        puzzleId = puzzleRef.puzzleId,
                        gridSize = puzzleRef.gridSize,
                        checkpointCount = puzzleRef.checkpointCount,
                        isCompleted = isDone,
                        bestSolveTimeMs = prog?.bestSolveTimeMs ?: 0L,
                        isUnlocked = isUnlocked
                    )
                }

                PackLevelsUiState.Ready(
                    manifest = manifest,
                    levels = levelItems,
                    isAllowed = isFeatureUnlocked,
                    completedCount = completedCount,
                    totalCount = manifest.puzzleCount
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
