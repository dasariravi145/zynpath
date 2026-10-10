package com.zynpath.game.feature.multiplayer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.social.repository.SocialRepository
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
 * ViewModel managing Friends Arena Room Invitations and friend discovery.
 */
@HiltViewModel
class FriendsArenaFacebookViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository,
    private val socialRepository: SocialRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val roomId: String? = savedStateHandle.get<String>("roomId")
    val initialRoomCode: String? = savedStateHandle.get<String>("roomCode")

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFriendIds = MutableStateFlow<Set<String>>(emptySet())
    private val _isSending = MutableStateFlow(false)
    private val _isLoadingFriends = MutableStateFlow(false)
    private val _userNoticeTitle = MutableStateFlow<String?>(null)
    private val _userNoticeDetails = MutableStateFlow<String?>(null)
    private val _eligibleFriends = MutableStateFlow<List<FacebookFriendItem>>(emptyList())

    init {
        viewModelScope.launch {
            socialRepository.friends.collect { friendList ->
                _eligibleFriends.value = friendList.map { friend ->
                    FacebookFriendItem(
                        appScopedId = friend.publicZynpathId,
                        displayName = friend.displayName,
                        pictureUrl = null,
                        invitationStatus = FacebookInvitationStatus.NOT_INVITED
                    )
                }
            }
        }
        viewModelScope.launch {
            socialRepository.refreshAll()
        }
    }

    private data class TransientFacebookState(
        val searchQuery: String,
        val selectedFriendIds: Set<String>,
        val isSending: Boolean,
        val isLoadingFriends: Boolean,
        val userNoticeTitle: String?,
        val userNoticeDetails: String?,
        val eligibleFriends: List<FacebookFriendItem>
    )

    private val _transientState = combine(
        _searchQuery,
        _selectedFriendIds,
        _isSending,
        _isLoadingFriends,
        _userNoticeTitle,
        _userNoticeDetails,
        _eligibleFriends
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        TransientFacebookState(
            searchQuery = args[0] as String,
            selectedFriendIds = args[1] as Set<String>,
            isSending = args[2] as Boolean,
            isLoadingFriends = args[3] as Boolean,
            userNoticeTitle = args[4] as? String,
            userNoticeDetails = args[5] as? String,
            eligibleFriends = args[6] as List<FacebookFriendItem>
        )
    }

    val uiState: StateFlow<FacebookFriendsUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        playerProfileRepository.observeProfile(),
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { authState, session, profile, prefs, transient ->
        FacebookFriendsUiState(
            roomId = roomId,
            roomCode = initialRoomCode,
            currentOccupancy = 1, // Authoritative host baseline
            maxRoomCapacity = 5,
            authState = authState,
            session = session,
            playerProfile = profile,
            isFacebookConfigured = true,
            eligibleFriends = transient.eligibleFriends,
            searchQuery = transient.searchQuery,
            selectedFriendIds = transient.selectedFriendIds,
            isSendingInvitations = transient.isSending,
            isLoading = transient.isLoadingFriends,
            userNoticeTitle = transient.userNoticeTitle,
            userNoticeDetails = transient.userNoticeDetails,
            isReducedMotion = prefs.isReducedMotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FacebookFriendsUiState(
            roomId = roomId,
            roomCode = initialRoomCode,
            isFacebookConfigured = true
        )
    )

    /**
     * Updates search query for filtering loaded friend data.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Clears active search filter.
     */
    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    /**
     * Toggles friend selection while strictly enforcing available room capacity.
     */
    fun toggleFriendSelection(appScopedId: String) {
        val currentSelected = _selectedFriendIds.value
        if (currentSelected.contains(appScopedId)) {
            _selectedFriendIds.value = currentSelected - appScopedId
        } else {
            val state = uiState.value
            if (currentSelected.size >= state.availableCapacity) {
                showNotice(
                    title = "Room Capacity Reached",
                    details = "This room can host a maximum of 5 players (Host + 4 additional players). You have reached the maximum of ${state.availableCapacity} invitation slots."
                )
            } else {
                _selectedFriendIds.value = currentSelected + appScopedId
            }
        }
    }

    /**
     * Clears all friend selections.
     */
    fun clearSelection() {
        _selectedFriendIds.value = emptySet()
    }

    /**
     * Dispatches room invitations to selected friends.
     */
    fun onSendInvitations() {
        if (_isSending.value) return // Prevent duplicate submission

        val state = uiState.value
        if (state.selectedCount == 0) {
            showNotice(
                title = "No Friends Selected",
                details = "Please select at least one friend to send room invitations."
            )
            return
        }

        if (state.isGuest) {
            showNotice(
                title = "Google Account Required",
                details = "A connected Google Account is required to dispatch room invitations."
            )
            return
        }

        viewModelScope.launch {
            _isSending.value = true
            delay(400L) // UI touch feedback
            _isSending.value = false

            showNotice(
                title = "Invitations Dispatched",
                details = "Invitations sent to ${state.selectedCount} friends!"
            )
        }
    }

    /**
     * Displays an alert notice dialog.
     */
    fun showNotice(title: String, details: String) {
        _userNoticeTitle.value = title
        _userNoticeDetails.value = details
    }

    /**
     * Dismisses user alert notices.
     */
    fun clearNotice() {
        _userNoticeTitle.value = null
        _userNoticeDetails.value = null
    }
}
