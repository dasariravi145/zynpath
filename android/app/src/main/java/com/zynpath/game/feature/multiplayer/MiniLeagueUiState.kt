package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation
import com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoom
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.social.model.FriendItem

/**
 * Tab selection for Mini League entry hub.
 */
enum class MiniLeagueEntryTab {
    CREATE,
    JOIN_CODE,
    INVITATIONS
}

/**
 * UI State for the complete Mini League 2-5 player experience.
 *
 * Implements Prompt 23 Sections 5-63:
 * - Room creation, joining by code, and friend invitations (2-5 total participants)
 * - Real-time lobby state, host controls, participant readiness, and host transfer
 * - Solver-verified puzzle assignment and synchronized 3s countdown
 * - Live multiplayer race with compact participant progress panel
 * - Server-side path validation, finishing window (45s), and authoritative finishing order (1st-5th)
 * - Result display, reconnection state recovery, and offline safety
 */
data class MiniLeagueUiState(
    val authState: AuthState = AuthState.GUEST,
    val authSession: AuthSession? = null,
    val selectedEntryTab: MiniLeagueEntryTab = MiniLeagueEntryTab.CREATE,
    val roomCodeInput: String = "",
    val isCreatingRoom: Boolean = false,
    val isJoiningRoom: Boolean = false,
    val isStartingMatch: Boolean = false,
    val isSendingInvite: Boolean = false,
    val showInviteDialog: Boolean = false,
    val showExitDialog: Boolean = false,

    // Active Room State
    val currentRoom: MiniLeagueRoom? = null,
    val incomingInvitations: List<MiniLeagueInvitation> = emptyList(),
    val acceptedFriends: List<FriendItem> = emptyList(),

    // Live Match & Gameplay State
    val clientMatchState: ClientMatchState = ClientMatchState.IDLE,
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
    val elapsedMatchTimeMs: Long = 0L,
    val isInputEnabled: Boolean = false
) {
    val isAuthenticated: Boolean
        get() = authState == AuthState.AUTHENTICATED && authSession != null

    val isHost: Boolean
        get() = currentRoom != null && currentRoom.hostPlayerId == authSession?.playerId

    val isInRoom: Boolean
        get() = currentRoom != null && currentSession == null && clientMatchState !in listOf(
            ClientMatchState.COUNTDOWN,
            ClientMatchState.ACTIVE,
            ClientMatchState.COMPLETED
        )

    val localRoomParticipant: MiniLeagueParticipant?
        get() = currentRoom?.participants?.firstOrNull { it.playerId == authSession?.playerId }

    val isLocalReady: Boolean
        get() = localRoomParticipant?.isReady == true

    val canStartMatch: Boolean
        get() {
            val room = currentRoom ?: return false
            if (!isHost) return false
            if (room.currentParticipants < 2) return false
            if (room.currentParticipants > 5) return false
            return room.allReady && !isStartingMatch
        }

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

    val localMatchParticipant: MatchParticipantDto?
        get() = currentSession?.participants?.firstOrNull { it.playerId == authSession?.playerId }

    val otherMatchParticipants: List<MatchParticipantDto>
        get() = currentSession?.participants?.filter { it.playerId != authSession?.playerId } ?: emptyList()

    val localResult: MatchResultDto?
        get() = matchResults.firstOrNull { it.playerId == authSession?.playerId }

    val sortedResults: List<MatchResultDto>
        get() = matchResults.sortedBy { if (it.finishOrder > 0) it.finishOrder else 999 }

    val canUndo: Boolean
        get() = isActiveGameplay && (puzzleGameState?.currentPath?.size ?: 0) > 1

    val canReset: Boolean
        get() = isActiveGameplay && (puzzleGameState?.currentPath?.isEmpty == false)
}
