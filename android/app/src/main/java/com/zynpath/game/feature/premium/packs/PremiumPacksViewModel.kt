package com.zynpath.game.feature.premium.packs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumPacksViewModel @Inject constructor(
    private val packRepository: PremiumPackRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository
) : ViewModel() {

    private val _errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PremiumPacksUiState> = combine(
        packRepository.observePacks(),
        entitlementRepository.entitlement,
        _errorFlow
    ) { packs, entitlement, error ->
        val totalCompleted = packs.sumOf { it.completedPuzzlesCount }
        val totalAvailable = packs.sumOf { it.puzzleCount }

        PremiumPacksUiState.Ready(
            packs = packs,
            isPremiumActive = entitlement.isPremiumActive,
            totalCompletedPuzzles = totalCompleted,
            totalAvailablePuzzles = totalAvailable,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PremiumPacksUiState.Loading
    )

    fun downloadPack(packId: String) {
        viewModelScope.launch {
            _errorFlow.value = null
            val result = packRepository.downloadPack(packId)
            if (result.isFailure) {
                _errorFlow.value = result.exceptionOrNull()?.message ?: "Failed to install pack"
            }
        }
    }

    fun clearError() {
        _errorFlow.value = null
    }
}
