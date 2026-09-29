package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiplayerHubViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val friendTargetIdInput = MutableStateFlow("")
    private val miniLeagueRoomNameInput = MutableStateFlow("Arena Room")
    private val miniLeagueMaxParticipants = MutableStateFlow(3)

    val uiState: StateFlow<MultiplayerHubUiState> = combine(
        multiplayerRepository.clientMatchState,
        authRepository.authState,
        authRepository.currentSession,
        multiplayerRepository.currentSession,
        multiplayerRepository.ticketStatus,
        multiplayerRepository.opponentProgress,
        multiplayerRepository.countdownSeconds,
        multiplayerRepository.isConnected,
        multiplayerRepository.errorMessage,
        friendTargetIdInput,
        miniLeagueRoomNameInput,
        miniLeagueMaxParticipants
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        MultiplayerHubUiState(
            clientMatchState = args[0] as com.zynpath.game.core.multiplayer.model.ClientMatchState,
            authState = args[1] as com.zynpath.game.core.auth.model.AuthState,
            authSession = args[2] as com.zynpath.game.core.auth.model.AuthSession?,
            currentSession = args[3] as com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto?,
            ticketStatus = args[4] as com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus?,
            opponentProgress = args[5] as Map<String, com.zynpath.game.core.multiplayer.model.MatchParticipantDto>,
            countdownSeconds = args[6] as Int?,
            isConnected = args[7] as Boolean,
            errorMessage = args[8] as String?,
            friendTargetIdInput = args[9] as String,
            miniLeagueRoomNameInput = args[10] as String,
            miniLeagueMaxParticipants = args[11] as Int
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MultiplayerHubUiState()
    )

    fun onFriendTargetIdChanged(id: String) {
        friendTargetIdInput.value = id
    }

    fun onMiniLeagueRoomNameChanged(name: String) {
        miniLeagueRoomNameInput.value = name
    }

    fun onMiniLeagueMaxParticipantsChanged(count: Int) {
        if (count in 2..5) {
            miniLeagueMaxParticipants.value = count
        }
    }

    fun startQuickDuel() {
        viewModelScope.launch {
            multiplayerRepository.startQuickDuelSearch()
        }
    }

    fun cancelMatchmaking() {
        viewModelScope.launch {
            multiplayerRepository.cancelMatchmaking()
        }
    }

    fun createFriendDuel() {
        val targetId = friendTargetIdInput.value.trim()
        if (targetId.isNotEmpty()) {
            viewModelScope.launch {
                multiplayerRepository.createFriendDuel(targetId)
            }
        }
    }

    fun createMiniLeague() {
        val roomName = miniLeagueRoomNameInput.value.trim()
        val capacity = miniLeagueMaxParticipants.value
        viewModelScope.launch {
            multiplayerRepository.createMiniLeague(roomName, capacity)
        }
    }

    fun markReady() {
        viewModelScope.launch {
            multiplayerRepository.markReady()
        }
    }

    fun leaveMatch() {
        multiplayerRepository.leaveMatch()
    }

    fun clearError() {
        multiplayerRepository.clearError()
    }
}
