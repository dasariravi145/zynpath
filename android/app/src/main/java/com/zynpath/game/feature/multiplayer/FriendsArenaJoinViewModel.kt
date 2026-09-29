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
 * ViewModel managing Friends Arena room code input, invitation link validation,
 * and authoritative join verification (Prompt 16 & Prompt 18).
 *
 * Requirements fulfilled:
 * - Sanitizes and bounds room code input (alphanumeric uppercase, max 8 chars, 6 standard).
 * - Preserves pending invitations while authentication is required for guests.
 * - Enforces duplicate submission prevention during join attempts.
 * - Real authoritative server validation via MultiplayerRepository.
 * - Handles real errors (invalid code, room not found, room full, room closed).
 * - On verified membership, navigates directly to room lobby.
 */
@HiltViewModel
class FriendsArenaJoinViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository,
    private val multiplayerRepository: MultiplayerRepository
) : ViewModel() {

    private val _roomCodeInput = MutableStateFlow("")
    private val _isJoining = MutableStateFlow(false)
    private val _pendingInvitation = MutableStateFlow<FriendsArenaInvitationPayload?>(null)
    private val _joinError = MutableStateFlow<FriendsArenaJoinError?>(null)
    private val _userNoticeTitle = MutableStateFlow<String?>(null)
    private val _userNoticeDetails = MutableStateFlow<String?>(null)

    private val _transientState = combine(
        _roomCodeInput,
        _isJoining,
        _pendingInvitation,
        _joinError,
        _userNoticeTitle,
        _userNoticeDetails
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        TransientJoinState(
            roomCodeInput = args[0] as String,
            isJoining = args[1] as Boolean,
            pendingInvitation = args[2] as? FriendsArenaInvitationPayload,
            joinError = args[3] as? FriendsArenaJoinError,
            userNoticeTitle = args[4] as? String,
            userNoticeDetails = args[5] as? String
        )
    }

    val uiState: StateFlow<FriendsArenaJoinUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        playerProfileRepository.observeProfile(),
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { authState, session, profile, prefs, transient ->
        FriendsArenaJoinUiState(
            roomCodeInput = transient.roomCodeInput,
            authState = authState,
            session = session,
            playerProfile = profile,
            isJoining = transient.isJoining,
            pendingInvitation = transient.pendingInvitation,
            joinError = transient.joinError,
            userNoticeTitle = transient.userNoticeTitle,
            userNoticeDetails = transient.userNoticeDetails,
            isReducedMotion = prefs.isReducedMotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendsArenaJoinUiState()
    )

    /**
     * Handles text changes with automatic sanitization (uppercase, alphanumeric only, max 8 chars).
     */
    fun onRoomCodeInputChange(input: String) {
        val sanitized = input.uppercase().filter { it.isLetterOrDigit() }.take(8)
        _roomCodeInput.value = sanitized
        // Clear any previous error when user modifies the code
        if (_joinError.value != null) {
            _joinError.value = null
        }
    }

    /**
     * Receives and validates an initial room code from deep links or external intent payloads (Task 4).
     */
    fun setInitialInvitation(code: String?, source: String? = null) {
        if (code.isNullOrBlank()) return

        val sanitized = code.uppercase().filter { it.isLetterOrDigit() }.take(8)
        if (sanitized.length in 4..8) {
            _roomCodeInput.value = sanitized
            _pendingInvitation.value = FriendsArenaInvitationPayload(
                roomCode = sanitized,
                source = source
            )
            _joinError.value = null
        } else {
            _joinError.value = FriendsArenaJoinError(
                type = FriendsArenaJoinErrorType.INVALID_CODE,
                message = "The invitation link contains an invalid room code format: $code"
            )
        }
    }

    /**
     * Attempts authoritative room joining with real session verification (Task 3).
     *
     * Prevents duplicate submissions and does NOT fabricate fake server rooms.
     */
    fun onJoinRoom(onSuccess: (String) -> Unit) {
        if (_isJoining.value) return // Prevent duplicate submissions from rapid taps

        val currentState = uiState.value
        val code = currentState.cleanedCode

        if (code.isBlank() || !currentState.isCodeValid) {
            _joinError.value = FriendsArenaJoinError(
                type = FriendsArenaJoinErrorType.INVALID_CODE,
                message = "Please enter a valid 6-character room code."
            )
            return
        }

        if (currentState.isGuest) {
            // Preserve pending invitation while authentication is required (Task 4 & Task 6)
            _pendingInvitation.value = FriendsArenaInvitationPayload(roomCode = code)
            showNotice(
                title = "Account Required",
                details = "An authenticated account is required to join an online Friends Arena room. Your invitation code ($code) has been saved. Please sign in or link your account to continue."
            )
            return
        }

        viewModelScope.launch {
            _isJoining.value = true
            _joinError.value = null

            try {
                val joinedRoom = multiplayerRepository.joinFriendsArenaRoom(code)
                if (joinedRoom != null) {
                    multiplayerRepository.subscribeToFriendsArenaRoomEvents(joinedRoom.roomId)
                    onSuccess(joinedRoom.roomId)
                } else {
                    val err = multiplayerRepository.errorMessage.value ?: "Unable to join room. Please check your connection and try again."
                    val errorType = when {
                        err.contains("ROOM_NOT_FOUND", ignoreCase = true) || err.contains("not found", ignoreCase = true) ->
                            FriendsArenaJoinErrorType.ROOM_NOT_FOUND
                        err.contains("ROOM_FULL", ignoreCase = true) || err.contains("full", ignoreCase = true) ->
                            FriendsArenaJoinErrorType.ROOM_FULL
                        err.contains("ROOM_CLOSED", ignoreCase = true) || err.contains("closed", ignoreCase = true) ->
                            FriendsArenaJoinErrorType.ROOM_CLOSED
                        err.contains("INVALID_CODE", ignoreCase = true) || err.contains("invalid", ignoreCase = true) ->
                            FriendsArenaJoinErrorType.INVALID_CODE
                        err.contains("ALREADY_JOINED", ignoreCase = true) ->
                            FriendsArenaJoinErrorType.ALREADY_JOINED
                        else ->
                            FriendsArenaJoinErrorType.CONNECTION_FAILURE
                    }
                    _joinError.value = FriendsArenaJoinError(
                        type = errorType,
                        message = err
                    )
                }
            } catch (e: Exception) {
                _joinError.value = FriendsArenaJoinError(
                    type = FriendsArenaJoinErrorType.CONNECTION_FAILURE,
                    message = e.message ?: "Failed to connect to Friends Arena room."
                )
            } finally {
                _isJoining.value = false
            }
        }
    }

    /**
     * Shows an honest alert notice dialog.
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
    }

    /**
     * Clears current join error.
     */
    fun clearError() {
        _joinError.value = null
    }

    private data class TransientJoinState(
        val roomCodeInput: String,
        val isJoining: Boolean,
        val pendingInvitation: FriendsArenaInvitationPayload?,
        val joinError: FriendsArenaJoinError?,
        val userNoticeTitle: String?,
        val userNoticeDetails: String?
    )
}
