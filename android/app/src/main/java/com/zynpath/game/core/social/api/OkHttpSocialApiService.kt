package com.zynpath.game.core.social.api

import android.net.Uri
import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRelationshipStatus
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpSocialApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SocialApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getMyPublicProfile(sessionToken: String): NetworkResult<PublicPlayerProfile> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/me")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeProfileRequest(request)
    }

    override suspend fun searchPlayer(sessionToken: String, publicZynpathId: String): NetworkResult<PublicPlayerProfile> = withContext(ioDispatcher) {
        val encodedId = Uri.encode(publicZynpathId.trim())
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/players/search?publicId=$encodedId")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeProfileRequest(request)
    }

    override suspend fun getFriends(sessionToken: String): NetworkResult<List<FriendItem>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val array = JSONArray(bodyString)
                val list = mutableListOf<FriendItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        FriendItem(
                            playerId = obj.getString("playerId"),
                            publicZynpathId = obj.getString("publicZynpathId"),
                            displayName = obj.optString("displayName", "Player"),
                            avatarId = obj.optString("avatarId", "avatar_1"),
                            presenceState = parsePresenceState(obj.optString("presenceState", "UNKNOWN")),
                            friendsSince = obj.optLong("friendsSince", 0L)
                        )
                    )
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getIncomingRequests(sessionToken: String): NetworkResult<List<FriendRequestItem>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests/incoming")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val array = JSONArray(bodyString)
                val list = mutableListOf<FriendRequestItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        FriendRequestItem(
                            requestId = obj.getString("requestId"),
                            senderPlayerId = obj.getString("senderPlayerId"),
                            senderPublicZynpathId = obj.getString("senderPublicZynpathId"),
                            senderDisplayName = obj.optString("senderDisplayName", "Player"),
                            senderAvatarId = obj.optString("senderAvatarId", "avatar_1"),
                            createdAt = obj.optLong("createdAt", 0L)
                        )
                    )
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getOutgoingRequests(sessionToken: String): NetworkResult<List<SentFriendRequestItem>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests/outgoing")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val array = JSONArray(bodyString)
                val list = mutableListOf<SentFriendRequestItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SentFriendRequestItem(
                            requestId = obj.getString("requestId"),
                            recipientPlayerId = obj.getString("recipientPlayerId"),
                            recipientPublicZynpathId = obj.getString("recipientPublicZynpathId"),
                            recipientDisplayName = obj.optString("recipientDisplayName", "Player"),
                            createdAt = obj.optLong("createdAt", 0L)
                        )
                    )
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun sendFriendRequest(sessionToken: String, targetPublicZynpathId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val body = JSONObject().apply {
            put("targetPublicZynpathId", targetPublicZynpathId.trim())
        }.toString()

        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests")
            .post(body.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun acceptFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests/$requestId/accept")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun rejectFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests/$requestId/reject")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun cancelFriendRequest(sessionToken: String, requestId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/requests/$requestId/cancel")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun removeFriend(sessionToken: String, friendPlayerId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/friends/$friendPlayerId")
            .delete()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun blockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/blocks/$targetPlayerId")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun unblockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/blocks/$targetPlayerId")
            .delete()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun sendHeartbeat(sessionToken: String, state: PlayerPresenceState): NetworkResult<Unit> = withContext(ioDispatcher) {
        val body = JSONObject().apply {
            put("state", state.name)
        }.toString()

        val request = Request.Builder()
            .url("${getBaseUrl()}/social/presence/heartbeat")
            .post(body.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEmptyResponseRequest(request)
    }

    override suspend fun getFriendsPresence(sessionToken: String): NetworkResult<Map<String, PlayerPresenceState>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/presence")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val array = JSONArray(bodyString)
                val map = mutableMapOf<String, PlayerPresenceState>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val playerId = obj.getString("playerId")
                    val presenceStr = obj.optString("presenceState", "UNKNOWN")
                    map[playerId] = parsePresenceState(presenceStr)
                }
                NetworkResult.Success(map)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeProfileRequest(request: Request): NetworkResult<PublicPlayerProfile> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return NetworkResult.Error(
                        code = response.code,
                        message = parseErrorMessage(bodyString, response.code)
                    )
                }

                if (bodyString.isBlank()) {
                    return NetworkResult.Error(code = response.code, message = "Empty response body")
                }

                val json = JSONObject(bodyString)
                val profile = PublicPlayerProfile(
                    publicZynpathId = json.getString("publicZynpathId"),
                    displayName = json.optString("displayName", "Player"),
                    avatarId = json.optString("avatarId", "avatar_1"),
                    relationshipStatus = parseRelationshipStatus(json.optString("relationshipStatus", "NONE")),
                    presenceState = parsePresenceState(json.optString("presenceState", "UNKNOWN"))
                )
                NetworkResult.Success(profile)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeEmptyResponseRequest(request: Request): NetworkResult<Unit> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseRelationshipStatus(value: String): FriendRelationshipStatus {
        return try {
            FriendRelationshipStatus.valueOf(value.uppercase())
        } catch (e: Exception) {
            FriendRelationshipStatus.NONE
        }
    }

    private fun parsePresenceState(value: String): PlayerPresenceState {
        return try {
            PlayerPresenceState.valueOf(value.uppercase())
        } catch (e: Exception) {
            PlayerPresenceState.UNKNOWN
        }
    }

    private fun parseErrorMessage(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            when {
                json.has("errorCode") -> "[${json.getString("errorCode")}] ${json.optString("message", "")}".trim()
                json.has("message") -> json.getString("message")
                else -> "HTTP $statusCode"
            }
        } catch (e: Exception) {
            "HTTP $statusCode: $body"
        }
    }
}
