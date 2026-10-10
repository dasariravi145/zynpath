package com.zynpath.game.feature.multiplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing Friends Arena entry flow, authoritative Google authentication gate,
 * compact Create Room modal, compact Join Room modal, and real friend selection.
 */
@HiltViewModel
class FriendsArenaViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository,
    private val multiplayerRepository: MultiplayerRepository? = null,
    private val socialRepository: SocialRepository? = null
) : ViewModel() {

    private data class TransientState(
        val isLoading: Boolean = false,
        val userNotice: String? = null,
        val showGoogleConnectModal: Boolean = false,
        val isGoogleConnecting: Boolean = false,
        val googleConnectError: String? = null,
        val showCreateRoomModal: Boolean = false,
        val isCreatingRoom: Boolean = false,
        val createRoomError: String? = null,
        val selectedPlayerCount: Int = 3,
        val selectedFriendIds: Set<String> = emptySet(),
        val selectedPuzzleDescription: String = "Random Expert",
        val showFriendPickerModal: Boolean = false,
        val showJoinRoomModal: Boolean = false,
        val isJoiningRoom: Boolean = false,
        val joinRoomCode: String = "",
        val joinRoomError: String? = null,
        val eligibleFriends: List<FriendItem> = emptyList()
    )

    private val _transientState = MutableStateFlow(TransientState())
    private var pendingPostAuthAction: (() -> Unit)? = null

    init {
        // Observe real eligible friends from SocialRepository if provided
        socialRepository?.let { repo ->
            viewModelScope.launch {
                repo.friends.collect { friendList ->
                    _transientState.update { it.copy(eligibleFriends = friendList) }
                }
            }
        }
    }

    val uiState: StateFlow<FriendsArenaUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        playerProfileRepository.observeProfile(),
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { authState, session, profile, prefs, transient ->
        val isFacebookConfigured = authRepository.isProviderConfigured(AuthProvider.FACEBOOK)
        val isGoogleConfigured = authRepository.isProviderConfigured(AuthProvider.GOOGLE)

        FriendsArenaUiState(
            authState = authState,
            session = session,
            playerProfile = profile,
            isFacebookConfigured = isFacebookConfigured,
            isGoogleConfigured = isGoogleConfigured,
            isReducedMotion = prefs.isReducedMotion,
            isLoading = transient.isLoading,
            userNotice = transient.userNotice,
            showGoogleConnectModal = transient.showGoogleConnectModal,
            isGoogleConnecting = transient.isGoogleConnecting,
            googleConnectError = transient.googleConnectError,
            showCreateRoomModal = transient.showCreateRoomModal,
            isCreatingRoom = transient.isCreatingRoom,
            createRoomError = transient.createRoomError,
            selectedPlayerCount = transient.selectedPlayerCount,
            selectedFriendIds = transient.selectedFriendIds,
            selectedPuzzleDescription = transient.selectedPuzzleDescription,
            showFriendPickerModal = transient.showFriendPickerModal,
            showJoinRoomModal = transient.showJoinRoomModal,
            isJoiningRoom = transient.isJoiningRoom,
            joinRoomCode = transient.joinRoomCode,
            joinRoomError = transient.joinRoomError,
            eligibleFriends = transient.eligibleFriends
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendsArenaUiState(
            isFacebookConfigured = authRepository.isProviderConfigured(AuthProvider.FACEBOOK),
            isGoogleConfigured = authRepository.isProviderConfigured(AuthProvider.GOOGLE)
        )
    )

    // ========================================================================
    // 1. Google Authentication Gate
    // ========================================================================

    /**
     * Authoritative Google Authentication check:
     * When Friends Arena is tapped:
     * - Read authoritative Google session (not a stale flag).
     * - If Google connected: open Create Room popup immediately.
     * - If not connected: open Connect Google popup.
     */
    fun onFriendsArenaTapped() {
        val session = authRepository.currentSession.value
        val isGoogleConnected = session != null &&
            session.provider == AuthProvider.GOOGLE &&
            !session.isExpired

        _transientState.update {
            if (isGoogleConnected) {
                it.copy(showCreateRoomModal = true, showGoogleConnectModal = false)
            } else {
                it.copy(showGoogleConnectModal = true, showCreateRoomModal = false)
            }
        }
    }

    fun openGoogleConnectModal(onSuccess: (() -> Unit)? = null) {
        pendingPostAuthAction = onSuccess
        _transientState.update {
            it.copy(
                showGoogleConnectModal = true,
                googleConnectError = null
            )
        }
    }

    fun dismissGoogleConnectModal() {
        pendingPostAuthAction = null
        _transientState.update {
            it.copy(
                showGoogleConnectModal = false,
                googleConnectError = null
            )
        }
    }

    fun connectWithGoogle(context: Context) {
        viewModelScope.launch {
            _transientState.update {
                it.copy(isGoogleConnecting = true, googleConnectError = null)
            }
            when (val result = authRepository.connectWithGoogle(context)) {
                is AuthResult.Success -> {
                    _transientState.update {
                        it.copy(
                            isGoogleConnecting = false,
                            showGoogleConnectModal = false,
                            // Automatically open Create Room popup upon successful Google authentication
                            showCreateRoomModal = true
                        )
                    }
                    val action = pendingPostAuthAction
                    pendingPostAuthAction = null
                    action?.invoke()
                }
                is AuthResult.Cancelled -> {
                    _transientState.update {
                        it.copy(isGoogleConnecting = false)
                    }
                    // Remain on original screen; do not open Create Room
                }
                is AuthResult.ProviderNotConfigured -> {
                    _transientState.update {
                        it.copy(
                            isGoogleConnecting = false,
                            googleConnectError = "Google Sign-In is not configured on this environment."
                        )
                    }
                }
                is AuthResult.Error,
                is AuthResult.Conflict -> {
                    _transientState.update {
                        it.copy(
                            isGoogleConnecting = false,
                            googleConnectError = "Couldn't connect. Try again."
                        )
                    }
                }
            }
        }
    }

    // ========================================================================
    // 2. Create Room Actions
    // ========================================================================

    fun openCreateRoomModal() {
        _transientState.update {
            it.copy(
                showCreateRoomModal = true,
                createRoomError = null
            )
        }
    }

    fun dismissCreateRoomModal() {
        _transientState.update {
            it.copy(
                showCreateRoomModal = false,
                createRoomError = null
            )
        }
    }

    fun onPlayerCountChanged(count: Int) {
        val validCount = count.coerceIn(1, 5)
        val maxInvitees = (validCount - 1).coerceAtLeast(0)
        _transientState.update { current ->
            current.copy(
                selectedPlayerCount = validCount,
                selectedFriendIds = if (current.selectedFriendIds.size > maxInvitees) {
                    current.selectedFriendIds.take(maxInvitees).toSet()
                } else {
                    current.selectedFriendIds
                }
            )
        }
    }

    fun createRoom(onSuccess: (roomId: String) -> Unit) {
        if (_transientState.value.isCreatingRoom) return // Idempotent debounce: prevent rapid duplicate creation

        val session = authRepository.currentSession.value
        val isGoogleConnected = session != null &&
            session.provider == AuthProvider.GOOGLE &&
            !session.isExpired

        if (!isGoogleConnected) {
            openGoogleConnectModal { createRoom(onSuccess) }
            return
        }

        _transientState.update {
            it.copy(isCreatingRoom = true, createRoomError = null)
        }

        viewModelScope.launch {
            try {
                val createdRoom = multiplayerRepository?.createFriendsArenaRoom()
                if (createdRoom != null) {
                    _transientState.update {
                        it.copy(isCreatingRoom = false, showCreateRoomModal = false)
                    }
                    multiplayerRepository.subscribeToFriendsArenaRoomEvents(createdRoom.roomId)
                    onSuccess(createdRoom.roomId)
                } else {
                    val rawError = multiplayerRepository?.errorMessage?.value
                    try {
                        android.util.Log.e("FriendsArenaVM", "Room creation failed on server: $rawError")
                    } catch (_: Throwable) {}
                    _transientState.update {
                        it.copy(
                            isCreatingRoom = false,
                            createRoomError = mapCreateRoomError(rawError)
                        )
                    }
                }
            } catch (e: Exception) {
                try {
                    android.util.Log.e("FriendsArenaVM", "Room creation exception: ${e.message}", e)
                } catch (_: Throwable) {}
                _transientState.update {
                    it.copy(
                        isCreatingRoom = false,
                        createRoomError = mapCreateRoomError(e.message)
                    )
                }
            }
        }
    }

    private fun mapCreateRoomError(raw: String?): String {
        if (raw.isNullOrBlank()) return "Unable to create room. Please retry."
        val lower = raw.lowercase()
        return when {
            lower.contains("session") || lower.contains("expired") || lower.contains("unauthorized") || lower.contains("401") || lower.contains("sign in") ->
                "Session expired. Connect Google again."
            lower.contains("connect") || lower.contains("network") || lower.contains("timeout") || lower.contains("unreachable") || lower.contains("econnrefused") ->
                "Unable to connect to server."
            lower.contains("unavailable") || lower.contains("503") || lower.contains("circuit") ->
                "Room service temporarily unavailable."
            else ->
                "Unable to create room. Please retry."
        }
    }

    // ========================================================================
    // 3. Friend Picker Actions
    // ========================================================================

    fun openFriendPicker() {
        _transientState.update { it.copy(showFriendPickerModal = true) }
        viewModelScope.launch {
            socialRepository?.refreshAll()
        }
    }

    fun dismissFriendPicker() {
        _transientState.update { it.copy(showFriendPickerModal = false) }
    }

    fun toggleFriendSelected(friendId: String) {
        _transientState.update { current ->
            val set = current.selectedFriendIds
            if (set.contains(friendId)) {
                current.copy(selectedFriendIds = set - friendId)
            } else {
                val maxInvitees = (current.selectedPlayerCount - 1).coerceAtLeast(0)
                if (set.size < maxInvitees) {
                    current.copy(selectedFriendIds = set + friendId)
                } else {
                    current
                }
            }
        }
    }

    // ========================================================================
    // 4. Join Room Actions
    // ========================================================================

    fun openJoinRoomModal() {
        _transientState.update {
            it.copy(
                showCreateRoomModal = false,
                showJoinRoomModal = true,
                joinRoomCode = "",
                joinRoomError = null
            )
        }
    }

    fun dismissJoinRoomModal() {
        _transientState.update {
            it.copy(
                showJoinRoomModal = false,
                joinRoomError = null
            )
        }
    }

    fun onJoinRoomCodeChanged(input: String) {
        val sanitized = input.uppercase().filter { it.isLetterOrDigit() }.take(8)
        _transientState.update {
            it.copy(
                joinRoomCode = sanitized,
                joinRoomError = null
            )
        }
    }

    fun joinRoom(code: String, onSuccess: (roomId: String) -> Unit) {
        if (_transientState.value.isJoiningRoom) return // Prevent duplicate taps

        val sanitized = code.uppercase().filter { it.isLetterOrDigit() }.take(8)
        if (sanitized.length < 4) {
            _transientState.update {
                it.copy(joinRoomError = "Please enter a valid 6-character room code.")
            }
            return
        }

        val session = authRepository.currentSession.value
        val isGoogleConnected = session != null &&
            session.provider == AuthProvider.GOOGLE &&
            !session.isExpired

        if (!isGoogleConnected) {
            openGoogleConnectModal { joinRoom(sanitized, onSuccess) }
            return
        }

        _transientState.update {
            it.copy(isJoiningRoom = true, joinRoomError = null)
        }

        viewModelScope.launch {
            try {
                val joinedRoom = multiplayerRepository?.joinFriendsArenaRoom(sanitized)
                if (joinedRoom != null) {
                    _transientState.update {
                        it.copy(isJoiningRoom = false, showJoinRoomModal = false)
                    }
                    multiplayerRepository.subscribeToFriendsArenaRoomEvents(joinedRoom.roomId)
                    onSuccess(joinedRoom.roomId)
                } else {
                    _transientState.update {
                        it.copy(
                            isJoiningRoom = false,
                            joinRoomError = multiplayerRepository?.errorMessage?.value
                                ?: "Room not found or cannot be joined."
                        )
                    }
                }
            } catch (e: Exception) {
                _transientState.update {
                    it.copy(
                        isJoiningRoom = false,
                        joinRoomError = e.message ?: "Failed to connect to room service."
                    )
                }
            }
        }
    }

    fun showActionNotice(actionTitle: String, details: String) {
        _transientState.update {
            it.copy(userNotice = "$actionTitle\n\n$details")
        }
    }

    fun clearUserNotice() {
        _transientState.update { it.copy(userNotice = null) }
    }
}
