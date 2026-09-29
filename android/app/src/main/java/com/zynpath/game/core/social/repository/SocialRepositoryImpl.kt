package com.zynpath.game.core.social.repository

import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.api.SocialApiService
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.InvitationLinkHelper
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem
import com.zynpath.game.core.social.model.SocialOperationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val socialApiService: SocialApiService,
    private val authRepository: AuthRepository,
    private val secureTokenStorage: SecureTokenStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SocialRepository {

    private val scope = CoroutineScope(ioDispatcher)

    private val _friends = MutableStateFlow<List<FriendItem>>(emptyList())
    override val friends: StateFlow<List<FriendItem>> = _friends.asStateFlow()

    private val _incomingRequests = MutableStateFlow<List<FriendRequestItem>>(emptyList())
    override val incomingRequests: StateFlow<List<FriendRequestItem>> = _incomingRequests.asStateFlow()

    private val _outgoingRequests = MutableStateFlow<List<SentFriendRequestItem>>(emptyList())
    override val outgoingRequests: StateFlow<List<SentFriendRequestItem>> = _outgoingRequests.asStateFlow()

    private val _myPublicProfile = MutableStateFlow<PublicPlayerProfile?>(null)
    override val myPublicProfile: StateFlow<PublicPlayerProfile?> = _myPublicProfile.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    override val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    override val lastError: StateFlow<String?> = _lastError.asStateFlow()

    init {
        scope.launch {
            authRepository.currentSession.collect { session ->
                if (session != null) {
                    refreshAll()
                } else {
                    // When unauthenticated / guest, do NOT show fake friends or fake presence
                    _friends.value = emptyList()
                    _incomingRequests.value = emptyList()
                    _outgoingRequests.value = emptyList()
                    _myPublicProfile.value = null
                }
            }
        }
    }

    override fun clearError() {
        _lastError.value = null
    }

    private suspend fun getValidToken(): String? {
        val session = authRepository.currentSession.value ?: return null
        if (session.isExpired) return null
        return secureTokenStorage.getSessionToken()
    }

    override suspend fun refreshAll(): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required to view social features"
        )

        _isRefreshing.value = true
        try {
            // 1. Fetch current public profile
            val profileRes = socialApiService.getMyPublicProfile(token)
            if (profileRes is NetworkResult.Success) {
                _myPublicProfile.value = profileRes.data
            }

            // 2. Fetch friends
            val friendsRes = socialApiService.getFriends(token)
            if (friendsRes is NetworkResult.Success) {
                // 3. Fetch latest presence states for friends
                val presenceRes = socialApiService.getFriendsPresence(token)
                val presenceMap = (presenceRes as? NetworkResult.Success)?.data ?: emptyMap()

                val updatedFriends = friendsRes.data.map { friend ->
                    val freshPresence = presenceMap[friend.playerId] ?: friend.presenceState
                    friend.copy(presenceState = freshPresence)
                }
                _friends.value = updatedFriends
            } else if (friendsRes is NetworkResult.Error) {
                _lastError.value = friendsRes.message
            }

            // 4. Fetch incoming requests
            val incomingRes = socialApiService.getIncomingRequests(token)
            if (incomingRes is NetworkResult.Success) {
                _incomingRequests.value = incomingRes.data
            }

            // 5. Fetch outgoing requests
            val outgoingRes = socialApiService.getOutgoingRequests(token)
            if (outgoingRes is NetworkResult.Success) {
                _outgoingRequests.value = outgoingRes.data
            }

            SocialOperationResult.Success("Social data refreshed")
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to refresh social data"
            _lastError.value = msg
            SocialOperationResult.Error("NETWORK_ERROR", msg)
        } finally {
            _isRefreshing.value = false
        }
    }

    override suspend fun searchPlayer(publicZynpathId: String): NetworkResult<PublicPlayerProfile> = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext NetworkResult.Error(
            401, "Sign in to search players"
        )

        if (!InvitationLinkHelper.isValidPublicId(publicZynpathId)) {
            return@withContext NetworkResult.Error(
                400, "Invalid Public Zynpath ID format. Format should be ZYN-XXXX-YYYY"
            )
        }

        socialApiService.searchPlayer(token, publicZynpathId)
    }

    override suspend fun sendFriendRequest(targetPublicZynpathId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required to send friend requests"
        )

        val trimmed = targetPublicZynpathId.trim()
        val currentProfile = _myPublicProfile.value
        if (currentProfile != null && currentProfile.publicZynpathId.equals(trimmed, ignoreCase = true)) {
            return@withContext SocialOperationResult.Error("SELF_REQUEST", "You cannot send a friend request to yourself")
        }

        when (val res = socialApiService.sendFriendRequest(token, trimmed)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Friend request sent successfully")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun acceptFriendRequest(requestId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.acceptFriendRequest(token, requestId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Friend request accepted")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.rejectFriendRequest(token, requestId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Friend request rejected")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun cancelFriendRequest(requestId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.cancelFriendRequest(token, requestId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Friend request cancelled")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun removeFriend(friendPlayerId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.removeFriend(token, friendPlayerId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Friend removed")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun blockPlayer(targetPlayerId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.blockPlayer(token, targetPlayerId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Player blocked")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun unblockPlayer(targetPlayerId: String): SocialOperationResult = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext SocialOperationResult.Error(
            "AUTH_REQUIRED", "Sign in required"
        )

        when (val res = socialApiService.unblockPlayer(token, targetPlayerId)) {
            is NetworkResult.Success -> {
                refreshAll()
                SocialOperationResult.Success("Player unblocked")
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                SocialOperationResult.Error(res.code.toString(), res.message)
            }
            is NetworkResult.Exception -> {
                val msg = res.throwable.localizedMessage ?: "Network error"
                _lastError.value = msg
                SocialOperationResult.Error("NETWORK_ERROR", msg)
            }
        }
    }

    override suspend fun reportPresence(state: PlayerPresenceState) = withContext(ioDispatcher) {
        val token = getValidToken() ?: return@withContext
        socialApiService.sendHeartbeat(token, state)
    }
}
