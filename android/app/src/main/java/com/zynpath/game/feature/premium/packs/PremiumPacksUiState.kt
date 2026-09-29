package com.zynpath.game.feature.premium.packs

import com.zynpath.game.core.puzzle.premium.model.PremiumPackItem

sealed class PremiumPacksUiState {
    object Loading : PremiumPacksUiState()

    data class Ready(
        val packs: List<PremiumPackItem>,
        val isPremiumActive: Boolean,
        val totalCompletedPuzzles: Int = 0,
        val totalAvailablePuzzles: Int = 0,
        val errorMessage: String? = null
    ) : PremiumPacksUiState()
}
