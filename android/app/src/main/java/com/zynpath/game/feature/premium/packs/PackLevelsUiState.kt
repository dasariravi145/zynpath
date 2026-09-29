package com.zynpath.game.feature.premium.packs

import com.zynpath.game.core.puzzle.premium.model.PremiumPackManifest

data class PackLevelItem(
    val levelIndex: Int,
    val puzzleId: String,
    val gridSize: String,
    val checkpointCount: Int,
    val isCompleted: Boolean,
    val bestSolveTimeMs: Long,
    val isUnlocked: Boolean
)

sealed class PackLevelsUiState {
    object Loading : PackLevelsUiState()

    data class Ready(
        val manifest: PremiumPackManifest,
        val levels: List<PackLevelItem>,
        val isAllowed: Boolean,
        val completedCount: Int,
        val totalCount: Int
    ) : PackLevelsUiState()

    data class Error(val message: String) : PackLevelsUiState()
}
