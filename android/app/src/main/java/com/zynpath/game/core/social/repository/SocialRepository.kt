package com.zynpath.game.core.social.repository

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem
import com.zynpath.game.core.social.model.SocialOperationResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Authoritative client repository for social state, discovery, relationships, and presence.
 *
 * Implements Prompt 19 Sections 5-23, 29-35, 43-45:
 * - Reactive state flows for friends, incoming requests, and outgoing requests.
 * - Exact-ID player search.
 * - Friend request lifecycle.
 * - Presence heartbeats.
 * - Guest-first safety: No fake data when unauthenticated.
 */
interface SocialRepository {
    val friends: StateFlow<List<FriendItem>>
    val incomingRequests: StateFlow<List<FriendRequestItem>>
    val outgoingRequests: StateFlow<List<SentFriendRequestItem>>
    val myPublicProfile: StateFlow<PublicPlayerProfile?>
    val isRefreshing: StateFlow<Boolean>
    val lastError: StateFlow<String?>

    suspend fun refreshAll(): SocialOperationResult

    suspend fun searchPlayer(publicZynpathId: String): NetworkResult<PublicPlayerProfile>

    suspend fun sendFriendRequest(targetPublicZynpathId: String): SocialOperationResult

    suspend fun acceptFriendRequest(requestId: String): SocialOperationResult

    suspend fun rejectFriendRequest(requestId: String): SocialOperationResult

    suspend fun cancelFriendRequest(requestId: String): SocialOperationResult

    suspend fun removeFriend(friendPlayerId: String): SocialOperationResult

    suspend fun blockPlayer(targetPlayerId: String): SocialOperationResult

    suspend fun unblockPlayer(targetPlayerId: String): SocialOperationResult

    suspend fun reportPresence(state: PlayerPresenceState)

    fun clearError()
}
