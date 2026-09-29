package com.zynpath.game.core.social.api

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem

/**
 * Remote API contract for Zynpath social operations.
 *
 * Implements Prompt 19 Section 39:
 * - Public profile lookup and exact-ID search
 * - Friend request lifecycle (send, accept, reject, cancel)
 * - Friend list and removal
 * - Block/unblock actions
 * - Ephemeral presence heartbeat and friend presence query
 */
interface SocialApiService {
    fun getBaseUrl(): String

    suspend fun getMyPublicProfile(sessionToken: String): NetworkResult<PublicPlayerProfile>

    suspend fun searchPlayer(sessionToken: String, publicZynpathId: String): NetworkResult<PublicPlayerProfile>

    suspend fun getFriends(sessionToken: String): NetworkResult<List<FriendItem>>

    suspend fun getIncomingRequests(sessionToken: String): NetworkResult<List<FriendRequestItem>>

    suspend fun getOutgoingRequests(sessionToken: String): NetworkResult<List<SentFriendRequestItem>>

    suspend fun sendFriendRequest(sessionToken: String, targetPublicZynpathId: String): NetworkResult<Unit>

    suspend fun acceptFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit>

    suspend fun rejectFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit>

    suspend fun cancelFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit>

    suspend fun removeFriend(sessionToken: String, friendPlayerId: String): NetworkResult<Unit>

    suspend fun blockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit>

    suspend fun unblockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit>

    suspend fun sendHeartbeat(sessionToken: String, state: PlayerPresenceState): NetworkResult<Unit>

    suspend fun getFriendsPresence(sessionToken: String): NetworkResult<Map<String, PlayerPresenceState>>
}
