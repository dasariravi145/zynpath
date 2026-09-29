package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus

/**
 * UI State for the Online Multiplayer entry, matchmaking waiting room, and session coordination.
 *
 * Implements Prompt 20 Sections 46, 47, 48.
 */
data class MultiplayerHubUiState(
    val clientMatchState: ClientMatchState = ClientMatchState.IDLE,
    val authState: AuthState = AuthState.GUEST,
    val authSession: AuthSession? = null,
    val currentSession: MatchSessionSnapshotDto? = null,
    val ticketStatus: MatchmakingTicketStatus? = null,
    val opponentProgress: Map<String, MatchParticipantDto> = emptyMap(),
    val countdownSeconds: Int? = null,
    val isConnected: Boolean = false,
    val errorMessage: String? = null,
    val friendTargetIdInput: String = "",
    val miniLeagueRoomNameInput: String = "Arena Room",
    val miniLeagueMaxParticipants: Int = 3
) {
    val isAuthenticated: Boolean
        get() = authState == AuthState.AUTHENTICATED && authSession != null && !authSession.isExpired

    val isSearching: Boolean
        get() = clientMatchState == ClientMatchState.SEARCHING

    val isInLobby: Boolean
        get() = clientMatchState in listOf(
            ClientMatchState.MATCH_FOUND,
            ClientMatchState.WAITING,
            ClientMatchState.READY
        )

    val isCountdown: Boolean
        get() = clientMatchState == ClientMatchState.COUNTDOWN

    val isActiveMatch: Boolean
        get() = clientMatchState == ClientMatchState.ACTIVE
}
