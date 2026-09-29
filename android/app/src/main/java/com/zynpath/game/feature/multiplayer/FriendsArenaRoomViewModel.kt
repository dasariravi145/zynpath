package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing Friends Arena Room Creation and 5-Player Lobby state (Prompt 15 & Prompt 18).
 *
 * Requirements fulfilled:
 * - Room occupancy: 1–5 players (including host).
 * - Minimum match start requirement: 2 connected players.
 * - Authoritative backend room creation, joining, leaving, and match starting.
 * - Real-time WebSocket membership observation.
 * - Enforces rapid-tap debouncing on room creation.
 * - Preserves leave-room confirmation to prevent accidental session drops.
 */
@HiltViewModel
class FriendsArenaRoomViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository,
    private val multiplayerRepository: MultiplayerRepository,
    private val feedbackCoordinator: com.zynpath.game.core.feedback.ZynpathFeedbackCoordinator
) : ViewModel() {

    private val _activeRoom = MutableStateFlow<FriendsArenaRoom?>(null)
    private val _lifecycle = MutableStateFlow(FriendsArenaRoomLifecycle.CONFIGURATION)
    private val _isCreatingRoom = MutableStateFlow(false)
    private val _isStartingMatch = MutableStateFlow(false)
    private val _isLeavingRoom = MutableStateFlow(false)
    private val _showLeaveDialog = MutableStateFlow(false)
    private val _userNoticeTitle = MutableStateFlow<String?>(null)
    private val _userNoticeDetails = MutableStateFlow<String?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private var lastKnownOccupancy = 0

    private val _transientState = combine(
        _activeRoom,
        _lifecycle,
        _isCreatingRoom,
        _isStartingMatch,
        _isLeavingRoom,
        _showLeaveDialog,
        _userNoticeTitle,
        _userNoticeDetails,
        _errorMessage
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        TransientRoomState(
            activeRoom = args[0] as FriendsArenaRoom?,
            lifecycle = args[1] as FriendsArenaRoomLifecycle,
            isCreatingRoom = args[2] as Boolean,
            isStartingMatch = args[3] as Boolean,
            isLeavingRoom = args[4] as Boolean,
            showLeaveDialog = args[5] as Boolean,
            userNoticeTitle = args[6] as String?,
            userNoticeDetails = args[7] as String?,
            errorMessage = args[8] as String?
        )
    }

    val uiState: StateFlow<FriendsArenaRoomUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        playerProfileRepository.observeProfile(),
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { authState, session, profile, prefs, transient ->
        FriendsArenaRoomUiState(
            authState = authState,
            session = session,
            playerProfile = profile,
            activeRoom = transient.activeRoom,
            lifecycle = transient.lifecycle,
            isCreatingRoom = transient.isCreatingRoom,
            isStartingMatch = transient.isStartingMatch,
            isLeavingRoom = transient.isLeavingRoom,
            showLeaveDialog = transient.showLeaveDialog,
            userNoticeTitle = transient.userNoticeTitle,
            userNoticeDetails = transient.userNoticeDetails,
            errorMessage = transient.errorMessage,
            isReducedMotion = prefs.isReducedMotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendsArenaRoomUiState()
    )

    val currentMatch: StateFlow<com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto?> =
        multiplayerRepository.currentFriendsArenaMatch

    init {
        // Collect authoritative room updates dispatched over WebSocket or REST
        viewModelScope.launch {
            multiplayerRepository.currentFriendsArenaRoom.collect { roomDto ->
                if (roomDto != null) {
                    val prevCount = lastKnownOccupancy
                    val domainRoom = roomDto.toDomain()
                    _activeRoom.value = domainRoom
                    _lifecycle.value = domainRoom.lifecycle
                    lastKnownOccupancy = domainRoom.occupancyCount

                    if (prevCount > 0 && domainRoom.occupancyCount > prevCount) {
                        feedbackCoordinator.onPlayerJoined()
                    } else if (prevCount > 0 && domainRoom.occupancyCount < prevCount) {
                        feedbackCoordinator.onPlayerLeft()
                    }
                } else if (_lifecycle.value != FriendsArenaRoomLifecycle.CREATING && _lifecycle.value != FriendsArenaRoomLifecycle.CONFIGURATION) {
                    _activeRoom.value = null
                    _lifecycle.value = FriendsArenaRoomLifecycle.CONFIGURATION
                    lastKnownOccupancy = 0
                }
            }
        }
    }

    /**
     * Initializes state for an existing room by internal ID (e.g. following join or deep-link).
     */
    fun initializeRoom(roomId: String) {
        if (roomId.isBlank()) return
        viewModelScope.launch {
            val roomDto = multiplayerRepository.getFriendsArenaRoom(roomId)
            if (roomDto != null) {
                val domainRoom = roomDto.toDomain()
                _activeRoom.value = domainRoom
                _lifecycle.value = domainRoom.lifecycle
                multiplayerRepository.subscribeToFriendsArenaRoomEvents(roomId)
            }
        }
    }

    /**
     * Attempts room creation with debounce and authoritative backend room creation (Prompt 18 Tasks 3 & 8).
     */
    fun onCreateRoom() {
        if (_isCreatingRoom.value) return // Prevent rapid tap duplication

        val currentState = uiState.value
        if (currentState.isGuest) {
            showNotice(
                title = "Account Required",
                details = "An authenticated Zynpath account is required to host an online multiplayer room. Please sign in or link your account to continue."
            )
            return
        }

        viewModelScope.launch {
            _isCreatingRoom.value = true
            _lifecycle.value = FriendsArenaRoomLifecycle.CREATING
            _errorMessage.value = null

            try {
                val createdRoom = multiplayerRepository.createFriendsArenaRoom()
                if (createdRoom != null) {
                    val domainRoom = createdRoom.toDomain()
                    _activeRoom.value = domainRoom
                    _lifecycle.value = domainRoom.lifecycle
                    multiplayerRepository.subscribeToFriendsArenaRoomEvents(createdRoom.roomId)
                } else {
                    val error = multiplayerRepository.errorMessage.value ?: "Unable to create Friends Arena room. Please check your connection and try again."
                    _errorMessage.value = error
                    _lifecycle.value = FriendsArenaRoomLifecycle.CONFIGURATION
                    showNotice(
                        title = "Room Creation Failed",
                        details = error
                    )
                }
            } catch (e: Exception) {
                val error = e.message ?: "An unexpected error occurred while creating the room."
                _errorMessage.value = error
                _lifecycle.value = FriendsArenaRoomLifecycle.CONFIGURATION
                showNotice(
                    title = "Error",
                    details = error
                )
            } finally {
                _isCreatingRoom.value = false
            }
        }
    }

    /**
     * Start match action enforcing all 6 conditions from Prompt 18 Task 7.
     */
    fun onStartMatch() {
        if (_isStartingMatch.value) return

        val state = uiState.value
        if (!state.isCurrentPlayerHost) {
            showNotice(
                title = "Host Control Only",
                details = "Only the room host can initiate match start."
            )
            return
        }

        val room = state.activeRoom
        if (room == null || !state.hasActiveRoom) {
            showNotice(
                title = "No Active Room",
                details = "Please create or join a verified room before starting a match."
            )
            return
        }

        if (state.currentOccupancy < state.minPlayersToStart) {
            showNotice(
                title = "Waiting for Players",
                details = "Waiting for at least one more player. Friends Arena requires 2 to 5 connected players to start a multiplayer match."
            )
            return
        }

        if (state.currentOccupancy > state.maxRoomCapacity) {
            showNotice(
                title = "Room Capacity Exceeded",
                details = "Room capacity cannot exceed 5 total players."
            )
            return
        }

        viewModelScope.launch {
            _isStartingMatch.value = true
            _lifecycle.value = FriendsArenaRoomLifecycle.STARTING_MATCH
            _errorMessage.value = null

            try {
                val updated = multiplayerRepository.startFriendsArenaMatch(room.roomId)
                if (updated != null) {
                    val domainRoom = updated.toDomain()
                    _activeRoom.value = domainRoom
                    _lifecycle.value = FriendsArenaRoomLifecycle.STARTING_MATCH
                    feedbackCoordinator.onMatchStarted()
                } else {
                    val error = multiplayerRepository.errorMessage.value ?: "Failed to start match"
                    _errorMessage.value = error
                    _lifecycle.value = room.lifecycle
                    showNotice(
                        title = "Start Match Failed",
                        details = error
                    )
                }
            } catch (e: Exception) {
                val error = e.message ?: "An unexpected error occurred starting the match."
                _errorMessage.value = error
                _lifecycle.value = room.lifecycle
                showNotice(
                    title = "Error",
                    details = error
                )
            } finally {
                _isStartingMatch.value = false
            }
        }
    }

    /**
     * Invitation entry action conforming to Prompt 15 Task 7.
     */
    fun onInvitePlayers() {
        showNotice(
            title = "Player Invitations Pending",
            details = "Direct 6-character room codes, shareable invitation links, and Facebook friend invitations will be activated in upcoming prompts. No fake invitation URLs or sent confirmations are generated."
        )
    }

    /**
     * Request leave room with confirmation check (Prompt 15 Task 8).
     */
    fun onRequestLeaveRoom(onDirectExit: () -> Unit) {
        if (_activeRoom.value != null) {
            _showLeaveDialog.value = true
        } else {
            onDirectExit()
        }
    }

    /**
     * Confirms leaving the active room with authoritative server leave operation.
     */
    fun onConfirmLeaveRoom(onExited: () -> Unit) {
        val room = _activeRoom.value
        _showLeaveDialog.value = false
        if (room != null) {
            viewModelScope.launch {
                _isLeavingRoom.value = true
                try {
                    multiplayerRepository.leaveFriendsArenaRoom(room.roomId)
                } finally {
                    multiplayerRepository.clearFriendsArenaRoom()
                    _activeRoom.value = null
                    _lifecycle.value = FriendsArenaRoomLifecycle.CONFIGURATION
                    _isLeavingRoom.value = false
                    onExited()
                }
            }
        } else {
            onExited()
        }
    }

    /**
     * Dismisses leave confirmation dialog.
     */
    fun onDismissLeaveDialog() {
        _showLeaveDialog.value = false
    }

    /**
     * Shows an honest alert notice to the user.
     */
    fun showNotice(title: String, details: String) {
        _userNoticeTitle.value = title
        _userNoticeDetails.value = details
    }

    /**
     * Clears user alert notices.
     */
    fun clearNotice() {
        _userNoticeTitle.value = null
        _userNoticeDetails.value = null
        _errorMessage.value = null
    }

    private data class TransientRoomState(
        val activeRoom: FriendsArenaRoom?,
        val lifecycle: FriendsArenaRoomLifecycle,
        val isCreatingRoom: Boolean,
        val isStartingMatch: Boolean,
        val isLeavingRoom: Boolean,
        val showLeaveDialog: Boolean,
        val userNoticeTitle: String?,
        val userNoticeDetails: String?,
        val errorMessage: String?
    )
}
