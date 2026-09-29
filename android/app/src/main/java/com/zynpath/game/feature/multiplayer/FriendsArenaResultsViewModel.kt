package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Friends Arena post-match results, standings & rematch flow (Prompt 20).
 *
 * Implements:
 * - Server-authoritative match results presentation for 2-5 players.
 * - Explicit tie, win, loss, and DNF handling without device-side fabrication.
 * - Idempotent recovery when reopened.
 * - Host-controlled real rematch execution and non-host rematch request notifications.
 * - Auto-navigation when a new match begins.
 */
@HiltViewModel
class FriendsArenaResultsViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val audioManager: ZynpathAudioManager,
    private val hapticManager: ZynpathHapticManager,
    val walletRepository: com.zynpath.game.core.economy.WalletRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsArenaResultsUiState())
    val uiState: StateFlow<FriendsArenaResultsUiState> = _uiState.asStateFlow()

    private var initialMatchId: String? = null
    private var hasPlayedOutcomeAudio = false
    private var settledMatchId: String? = null

    init {
        // Collect current authenticated user ID
        viewModelScope.launch {
            authRepository.currentSession.collect { session ->
                _uiState.value = _uiState.value.copy(localPlayerId = session?.playerId)
            }
        }

        // Collect preferences for reduced motion
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.value = _uiState.value.copy(isReducedMotion = prefs.isReducedMotion)
            }
        }

        // Collect authoritative match updates
        viewModelScope.launch {
            multiplayerRepository.currentFriendsArenaMatch.collect { match ->
                if (match != null) {
                    onMatchReceived(match)
                }
            }
        }

        // Collect authoritative room updates
        viewModelScope.launch {
            multiplayerRepository.currentFriendsArenaRoom.collect { room ->
                if (room != null) {
                    _uiState.value = _uiState.value.copy(room = room)
                }
            }
        }

        // Collect rematch request notifications from opponents
        viewModelScope.launch {
            multiplayerRepository.friendsArenaRematchRequested.collect { requester ->
                if (!requester.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(rematchRequestedByOpponent = requester)
                }
            }
        }
    }

    /**
     * Initializes the results screen with match and room identifiers.
     */
    fun initialize(matchId: String?, roomId: String?) {
        initialMatchId = matchId
        _uiState.value = _uiState.value.copy(
            matchId = matchId ?: _uiState.value.matchId,
            roomId = roomId ?: _uiState.value.roomId,
            isLoading = true,
            errorMessage = null
        )

        val targetRoomId = roomId ?: _uiState.value.roomId
        if (!targetRoomId.isNullOrBlank()) {
            multiplayerRepository.subscribeToFriendsArenaRoomEvents(targetRoomId)
        }

        loadAuthoritativeResults()
    }

    /**
     * Fetches the authoritative match snapshot from the backend.
     */
    fun loadAuthoritativeResults() {
        val matchId = _uiState.value.matchId
        val roomId = _uiState.value.roomId

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRetrying = true)
            try {
                val matchSnapshot = if (!matchId.isNullOrBlank()) {
                    multiplayerRepository.getFriendsArenaMatch(matchId)
                } else if (!roomId.isNullOrBlank()) {
                    multiplayerRepository.getFriendsArenaMatchByRoom(roomId)
                } else null

                if (!roomId.isNullOrBlank()) {
                    val roomDto = multiplayerRepository.getFriendsArenaRoom(roomId)
                    if (roomDto != null) {
                        _uiState.value = _uiState.value.copy(room = roomDto)
                    }
                }

                if (matchSnapshot != null) {
                    onMatchReceived(matchSnapshot)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRetrying = false,
                        errorMessage = "Unable to load verified match results. Please check your connection."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRetrying = false,
                    errorMessage = e.message ?: "An unexpected error occurred loading match results."
                )
            }
        }
    }

    private fun onMatchReceived(match: FriendsArenaMatchDto) {
        // Detect if a brand new match has started (Rematch transition)
        if (initialMatchId != null && match.matchId != initialMatchId && match.status == "COUNTDOWN") {
            _uiState.value = _uiState.value.copy(
                newMatchId = match.matchId,
                match = match,
                isLoading = false,
                isRetrying = false
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            matchId = match.matchId,
            roomId = match.roomId,
            match = match,
            isLoading = false,
            isRetrying = false,
            errorMessage = null
        )

        if (match.status == "COMPLETED" && !hasPlayedOutcomeAudio) {
            hasPlayedOutcomeAudio = true
            val isWin = match.results.firstOrNull { it.playerId == _uiState.value.localPlayerId }?.isWinner == true
            if (isWin) {
                audioManager.playEvent(ZynpathAudioEvent.VICTORY)
                hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
            } else {
                audioManager.playEvent(ZynpathAudioEvent.DEFEAT)
            }
        }

        // Economy: Settle tiered rewards for 2-5 players
        if (match.status == "COMPLETED" && settledMatchId != match.matchId) {
            settledMatchId = match.matchId
            viewModelScope.launch {
                val myId = _uiState.value.localPlayerId ?: authRepository.currentSession.value?.playerId ?: "player"
                val sortedResults = match.results.sortedBy { it.finishOrder }
                val myResult = sortedResults.firstOrNull { it.playerId == myId }
                val finishOrder = myResult?.finishOrder ?: (sortedResults.indexOf(myResult) + 1)
                val playerCount = match.results.size.coerceIn(2, 5)
                val reward = com.zynpath.game.core.economy.EconomyConfig.getArenaPayout(playerCount, finishOrder)
                if (reward > 0) {
                    walletRepository?.credit(
                        type = "ARENA_SETTLEMENT",
                        amount = reward,
                        idempotencyKey = "arena_settle_${match.matchId}_$myId",
                        metadataJson = "{\"matchId\":\"${match.matchId}\",\"finishOrder\":$finishOrder,\"playerCount\":$playerCount}"
                    )
                }
            }
        } else if (match.status == "CANCELLED" && settledMatchId != match.matchId) {
            settledMatchId = match.matchId
            viewModelScope.launch {
                val myId = _uiState.value.localPlayerId ?: authRepository.currentSession.value?.playerId ?: "player"
                walletRepository?.credit(
                    type = "MATCH_REFUND",
                    amount = com.zynpath.game.core.economy.EconomyConfig.ARENA_ENTRY_FEE,
                    idempotencyKey = "arena_refund_${match.matchId}_$myId",
                    metadataJson = "{\"matchId\":\"${match.matchId}\",\"reason\":\"CANCELLED\"}"
                )
            }
        }
    }

    /**
     * Initiates rematch request (host creates new match, non-host sends notification).
     */
    fun onRequestRematch() {
        if (_uiState.value.isRematchInFlight) return
        val roomId = _uiState.value.roomId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRematchInFlight = true, errorMessage = null)
            try {
                val newMatch = multiplayerRepository.requestFriendsArenaRematch(roomId)
                if (newMatch != null) {
                    _uiState.value = _uiState.value.copy(
                        newMatchId = newMatch.matchId,
                        isRematchInFlight = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isRematchInFlight = false,
                        errorMessage = "Rematch could not be initiated. Please ensure at least 2 players are present."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRematchInFlight = false,
                    errorMessage = e.message ?: "Failed to initiate rematch."
                )
            }
        }
    }

    /**
     * Dismisses the opponent rematch notification banner.
     */
    fun onDismissRematchNotification() {
        _uiState.value = _uiState.value.copy(rematchRequestedByOpponent = null)
        multiplayerRepository.clearFriendsArenaRematchRequested()
    }

    /**
     * Clears error notice.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Resets newMatchId after consumption by navigation.
     */
    fun onNewMatchNavigated() {
        _uiState.value = _uiState.value.copy(newMatchId = null)
    }
}
