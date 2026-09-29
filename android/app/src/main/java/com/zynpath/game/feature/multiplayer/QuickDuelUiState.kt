package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Ephemeral reaction item displayed as a floating popup badge.
 */
data class ReactionPopup(
    val senderPlayerId: String,
    val isLocalPlayer: Boolean,
    val reactionCode: String,
    val emojiChar: String,
    val labelText: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor(displayName: String, reactionCode: String) : this(
        senderPlayerId = displayName,
        isLocalPlayer = false,
        reactionCode = reactionCode,
        emojiChar = "⚡",
        labelText = reactionCode
    )
}

/**
 * UI State for the complete Quick Duel 1v1 feature.
 *
 * Implements Prompt 21 Sections 5-50:
 * - Matchmaking queue state
 * - Lobby and opponent inspection
 * - Synchronized countdown
 * - Authoritative puzzle board and local movement
 * - Live opponent milestone progress
 * - Authoritative match timer
 * - Validation overlay and result screen
 */
data class QuickDuelUiState(
    val clientMatchState: ClientMatchState = ClientMatchState.IDLE,
    val authState: AuthState = AuthState.GUEST,
    val authSession: AuthSession? = null,
    val ticketStatus: MatchmakingTicketStatus? = null,
    val currentSession: MatchSessionSnapshotDto? = null,
    val puzzleDefinition: PuzzleDefinition? = null,
    val puzzleGameState: PuzzleGameState? = null,
    val boardState: PuzzleBoardState? = null,
    val countdownSeconds: Int? = null,
    val isConnected: Boolean = false,
    val errorMessage: String? = null,
    val isValidatingCompletion: Boolean = false,
    val matchResults: List<MatchResultDto> = emptyList(),
    val activeReactionPopup: ReactionPopup? = null,
    val showForfeitDialog: Boolean = false,
    val elapsedMatchTimeMs: Long = 0L,
    val isInputEnabled: Boolean = false
) {
    val isAuthenticated: Boolean
        get() = authState == AuthState.AUTHENTICATED && authSession != null

    val isSearching: Boolean
        get() = clientMatchState == ClientMatchState.SEARCHING

    val isMatchFound: Boolean
        get() = clientMatchState in listOf(
            ClientMatchState.MATCH_FOUND,
            ClientMatchState.WAITING,
            ClientMatchState.READY
        )

    val isCountdown: Boolean
        get() = clientMatchState == ClientMatchState.COUNTDOWN

    val isActiveGameplay: Boolean
        get() = clientMatchState == ClientMatchState.ACTIVE

    val isCompleted: Boolean
        get() = clientMatchState == ClientMatchState.COMPLETED

    val isCancelled: Boolean
        get() = clientMatchState == ClientMatchState.CANCELLED

    val isError: Boolean
        get() = clientMatchState == ClientMatchState.ERROR

    val localParticipant: MatchParticipantDto?
        get() = currentSession?.participants?.firstOrNull { it.playerId == authSession?.playerId }

    val opponentParticipant: MatchParticipantDto?
        get() = currentSession?.participants?.firstOrNull { it.playerId != authSession?.playerId }

    val localResult: MatchResultDto?
        get() = matchResults.firstOrNull { it.playerId == authSession?.playerId }

    val opponentResult: MatchResultDto?
        get() = matchResults.firstOrNull { it.playerId != authSession?.playerId }

    val isLocalWinner: Boolean
        get() = localResult?.isWinner == true

    val isLocalReady: Boolean
        get() = localParticipant?.isReady == true

    val isOpponentReady: Boolean
        get() = opponentParticipant?.isReady == true

    val canUndo: Boolean
        get() = isActiveGameplay && (puzzleGameState?.currentPath?.size ?: 0) > 1

    val canReset: Boolean
        get() = isActiveGameplay && (puzzleGameState?.currentPath?.isEmpty == false)
}
