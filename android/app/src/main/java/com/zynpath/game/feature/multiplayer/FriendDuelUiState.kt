package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.RematchStatusDto
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.social.model.FriendItem

/**
 * Tab selection for Friend Duel friend choice.
 */
enum class FriendDuelTab {
    FRIENDS_LIST,
    DIRECT_ID
}

/**
 * UI State for the complete Friend Duel 1v1 experience.
 *
 * Implements Prompt 22 Sections 5-55:
 * - Friend selection and invitation lifecycle (PENDING, ACCEPTED, DECLINED, CANCELLED, EXPIRED)
 * - Private 1v1 match creation with solver-verified puzzle assignment
 * - Synchronized ready window and authoritative 3s countdown
 * - Live continuous gameplay with dual progress and competitive integrity
 * - Authoritative dual-win server validation
 * - Interactive rematch experience with new puzzle selection and consent verification
 */
data class FriendDuelUiState(
    val clientMatchState: ClientMatchState = ClientMatchState.IDLE,
    val authState: AuthState = AuthState.GUEST,
    val authSession: AuthSession? = null,
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
    val isInputEnabled: Boolean = false,

    // Friend Duel specific state
    val selectedTab: FriendDuelTab = FriendDuelTab.FRIENDS_LIST,
    val acceptedFriends: List<FriendItem> = emptyList(),
    val friendTargetIdInput: String = "",
    val isSendingInvitation: Boolean = false,
    val activeOutgoingInvitation: FriendDuelInvitationDto? = null,
    val incomingInvitations: List<FriendDuelInvitationDto> = emptyList(),
    val outgoingRemainingSeconds: Int = 0,
    val rematchStatus: RematchStatusDto? = null,
    val isRematchLoading: Boolean = false
) {
    val isAuthenticated: Boolean
        get() = authState == AuthState.AUTHENTICATED && authSession != null

    val isPendingInvitation: Boolean
        get() = activeOutgoingInvitation != null && activeOutgoingInvitation.status.isActionable()

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

    val isRematchRequestedByMe: Boolean
        get() = rematchStatus?.isPending() == true && rematchStatus.requesterPlayerId == authSession?.playerId

    val isRematchRequestedByOpponent: Boolean
        get() = rematchStatus?.isPending() == true && rematchStatus.requesterPlayerId != authSession?.playerId
}
