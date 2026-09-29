package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * UI state for the Friends Arena 2-5 player synchronized match gameplay (Prompt 19).
 */
data class FriendsArenaGameplayUiState(
    val matchId: String? = null,
    val roomId: String? = null,
    val roomCode: String? = null,
    val matchStatus: String = "COUNTDOWN",
    val puzzleDefinition: PuzzleDefinition? = null,
    val puzzleGameState: PuzzleGameState? = null,
    val boardState: PuzzleBoardState? = null,
    val participants: List<FriendsArenaMatchParticipantDto> = emptyList(),
    val results: List<MatchResultDto> = emptyList(),
    val localPlayerId: String? = null,
    val countdownSeconds: Int = 3,
    val elapsedMatchTimeMs: Long = 0L,
    val isInputEnabled: Boolean = false,
    val isValidatingCompletion: Boolean = false,
    val isForfeiting: Boolean = false,
    val showForfeitDialog: Boolean = false,
    val isConnected: Boolean = true,
    val errorMessage: String? = null
) {
    val isCountdown: Boolean
        get() = matchStatus == "COUNTDOWN"

    val isActive: Boolean
        get() = matchStatus == "ACTIVE" || matchStatus == "COMPLETING"

    val isCompleted: Boolean
        get() = matchStatus == "COMPLETED"

    val localParticipant: FriendsArenaMatchParticipantDto?
        get() = participants.firstOrNull { it.playerId == localPlayerId }

    val otherParticipants: List<FriendsArenaMatchParticipantDto>
        get() = participants.filter { it.playerId != localPlayerId }

    val canUndo: Boolean
        get() = isActive && isInputEnabled && !isValidatingCompletion && (puzzleGameState?.currentPath?.size ?: 0) > 1

    val canReset: Boolean
        get() = isActive && isInputEnabled && !isValidatingCompletion && (puzzleGameState?.currentPath?.isNotEmpty == true)

    val localResult: MatchResultDto?
        get() = results.firstOrNull { it.playerId == localPlayerId }

    val sortedResults: List<MatchResultDto>
        get() = results.sortedBy { if (it.finishOrder > 0) it.finishOrder else 999 }
}
