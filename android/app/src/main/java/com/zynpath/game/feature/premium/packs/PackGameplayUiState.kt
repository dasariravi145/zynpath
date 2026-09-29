package com.zynpath.game.feature.premium.packs

import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

sealed class PackGameplayUiState {
    object Loading : PackGameplayUiState()

    data class Error(val message: String) : PackGameplayUiState()

    data class Ready(
        val packId: String,
        val packName: String,
        val levelIndex: Int,
        val totalLevels: Int,
        val definition: PuzzleDefinition,
        val gameState: PuzzleGameState,
        val boardState: PuzzleBoardState,
        val elapsedTimeMs: Long = 0L,
        val isUndoAvailable: Boolean = false,
        val isResetAvailable: Boolean = false,
        val isCompleted: Boolean = false,
        val completionSolveTimeMs: Long? = null,
        val personalBestTimeMs: Long? = null,
        val isNewPersonalBest: Boolean = false,
        val isUnlimitedHints: Boolean = true,
        val isHintLoading: Boolean = false,
        val hintedCoordinate: GridPosition? = null,
        val hintMessage: String? = null,
        val rejectionMessage: String? = null,
        val isTapInputMode: Boolean = false,
        val isReducedMotion: Boolean = false,
        val hasNextLevel: Boolean = false
    ) : PackGameplayUiState()
}
