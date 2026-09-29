package com.zynpath.game.feature.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.model.InvitationLinkHelper
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.SocialOperationResult
import com.zynpath.game.core.social.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing social state, player discovery, friend requests, and presence.
 *
 * Implements Prompt 19 Sections 5-23, 29-35:
 * - Real backend identity and presence integration.
 * - Guest mode shows sign-in explanation without fake friends.
 * - Public ID exact lookup and invitation link processing.
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        // Observe authentication state
        viewModelScope.launch {
            authRepository.currentSession.collect { session ->
                val isAuth = session != null && !session.isExpired
                _uiState.update { current ->
                    current.copy(
                        isAuthenticated = isAuth,
                        myPublicId = session?.publicZynpathId,
                        myDisplayName = session?.displayName
                    )
                }
                if (isAuth) {
                    socialRepository.reportPresence(PlayerPresenceState.ONLINE)
                    socialRepository.refreshAll()
                }
            }
        }

        // Observe friends list
        viewModelScope.launch {
            socialRepository.friends.collect { list ->
                _uiState.update { it.copy(friends = list) }
            }
        }

        // Observe incoming requests
        viewModelScope.launch {
            socialRepository.incomingRequests.collect { list ->
                _uiState.update { it.copy(incomingRequests = list) }
            }
        }

        // Observe outgoing requests
        viewModelScope.launch {
            socialRepository.outgoingRequests.collect { list ->
                _uiState.update { it.copy(outgoingRequests = list) }
            }
        }

        // Observe profile details
        viewModelScope.launch {
            socialRepository.myPublicProfile.collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            myPublicId = profile.publicZynpathId,
                            myDisplayName = profile.displayName,
                            myAvatarId = profile.avatarId
                        )
                    }
                }
            }
        }

        // Observe refresh status
        viewModelScope.launch {
            socialRepository.isRefreshing.collect { refreshing ->
                _uiState.update { it.copy(isLoading = refreshing) }
            }
        }
    }

    fun selectTab(tab: FriendsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, searchError = null) }
    }

    fun searchPlayer() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchError = "Enter a Public Zynpath ID (e.g. ZYN-XXXX-YYYY)") }
            return
        }

        if (!InvitationLinkHelper.isValidPublicId(query)) {
            _uiState.update { it.copy(searchError = "Invalid format. Public ID must start with ZYN-") }
            return
        }

        _uiState.update { it.copy(isSearching = true, searchError = null, searchResult = null) }
        viewModelScope.launch {
            when (val res = socialRepository.searchPlayer(query)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResult = res.data,
                            searchError = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    val msg = if (res.code == 404) "No player found with ID '$query'" else res.message
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResult = null,
                            searchError = msg
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResult = null,
                            searchError = res.throwable.localizedMessage ?: "Network error"
                        )
                    }
                }
            }
        }
    }

    fun sendFriendRequest(targetPublicZynpathId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.sendFriendRequest(targetPublicZynpathId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = "Friend request sent to $targetPublicZynpathId",
                            searchResult = null,
                            searchQuery = ""
                        )
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = res.message
                        )
                    }
                }
            }
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.acceptFriendRequest(requestId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Friend request accepted!")
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = res.message)
                    }
                }
            }
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.rejectFriendRequest(requestId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Friend request rejected")
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = res.message)
                    }
                }
            }
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.cancelFriendRequest(requestId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Friend request cancelled")
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = res.message)
                    }
                }
            }
        }
    }

    fun removeFriend(friendPlayerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.removeFriend(friendPlayerId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Friend removed")
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = res.message)
                    }
                }
            }
        }
    }

    fun blockPlayer(targetPlayerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val res = socialRepository.blockPlayer(targetPlayerId)) {
                is SocialOperationResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Player blocked")
                    }
                }
                is SocialOperationResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = res.message)
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            socialRepository.refreshAll()
        }
    }

    fun handleIncomingInvitation(publicZynpathId: String) {
        if (InvitationLinkHelper.isValidPublicId(publicZynpathId)) {
            _uiState.update {
                it.copy(
                    selectedTab = FriendsTab.FIND_PLAYER,
                    searchQuery = publicZynpathId,
                    pendingDeepLinkInviterId = publicZynpathId
                )
            }
            searchPlayer()
        }
    }

    fun onLifecycleStateChanged(isForeground: Boolean) {
        viewModelScope.launch {
            if (_uiState.value.isAuthenticated) {
                val state = if (isForeground) PlayerPresenceState.ONLINE else PlayerPresenceState.AWAY
                socialRepository.reportPresence(state)
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
