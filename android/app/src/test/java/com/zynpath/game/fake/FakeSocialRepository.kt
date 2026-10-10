package com.zynpath.game.fake

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem
import com.zynpath.game.core.social.model.SocialOperationResult
import com.zynpath.game.core.social.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSocialRepository : SocialRepository {
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

    fun setFriends(list: List<FriendItem>) {
        _friends.value = list
    }

    override suspend fun refreshAll(): SocialOperationResult = SocialOperationResult.Success("Refreshed")

    override suspend fun searchPlayer(publicZynpathId: String): NetworkResult<PublicPlayerProfile> {
        return NetworkResult.Error(404, "Not found")
    }

    override suspend fun sendFriendRequest(targetPublicZynpathId: String): SocialOperationResult =
        SocialOperationResult.Success("Sent")

    override suspend fun acceptFriendRequest(requestId: String): SocialOperationResult =
        SocialOperationResult.Success("Accepted")

    override suspend fun rejectFriendRequest(requestId: String): SocialOperationResult =
        SocialOperationResult.Success("Rejected")

    override suspend fun cancelFriendRequest(requestId: String): SocialOperationResult =
        SocialOperationResult.Success("Cancelled")

    override suspend fun removeFriend(friendPlayerId: String): SocialOperationResult =
        SocialOperationResult.Success("Removed")

    override suspend fun blockPlayer(targetPlayerId: String): SocialOperationResult =
        SocialOperationResult.Success("Blocked")

    override suspend fun unblockPlayer(targetPlayerId: String): SocialOperationResult =
        SocialOperationResult.Success("Unblocked")

    override suspend fun reportPresence(state: PlayerPresenceState) {}

    override fun clearError() {
        _lastError.value = null
    }
}
