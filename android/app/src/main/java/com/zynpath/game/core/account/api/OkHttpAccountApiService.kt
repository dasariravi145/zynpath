package com.zynpath.game.core.account.api

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.account.model.AccountDeletionResult
import com.zynpath.game.core.account.model.AccountSettings
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.DataExportResult
import com.zynpath.game.core.account.model.PlayerPrivacySettings
import com.zynpath.game.core.account.model.ProfileVisibility
import com.zynpath.game.core.network.NetworkResult
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
class OkHttpAccountApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AccountApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getAccountSettings(sessionToken: String): NetworkResult<AccountSettings> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/account/settings")
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
                val json = JSONObject(bodyString)
                val privacyJson = json.optJSONObject("privacySettings") ?: JSONObject()
                val providersJson = json.optJSONArray("linkedProviders") ?: JSONArray()
                val providersList = mutableListOf<String>()
                for (i in 0 until providersJson.length()) {
                    providersList.add(providersJson.getString(i))
                }

                val settings = AccountSettings(
                    playerId = json.getString("playerId"),
                    publicZynpathId = json.getString("publicZynpathId"),
                    displayName = json.getString("displayName"),
                    email = if (json.has("email") && !json.isNull("email")) json.getString("email") else null,
                    avatarUrl = if (json.has("avatarUrl") && !json.isNull("avatarUrl")) json.getString("avatarUrl") else null,
                    linkedProviders = providersList,
                    privacySettings = PlayerPrivacySettings(
                        profileVisibility = ProfileVisibility.fromString(privacyJson.optString("profileVisibility", "PUBLIC")),
                        allowZynpathIdSearch = privacyJson.optBoolean("allowZynpathIdSearch", true),
                        allowFriendRequests = privacyJson.optBoolean("allowFriendRequests", true)
                    ),
                    isPremium = json.optBoolean("isPremium", false),
                    createdAt = json.optLong("createdAt", System.currentTimeMillis())
                )
                NetworkResult.Success(settings)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun updatePrivacySettings(
        sessionToken: String,
        settings: PlayerPrivacySettings
    ): NetworkResult<PlayerPrivacySettings> = withContext(ioDispatcher) {
        val jsonPayload = JSONObject().apply {
            put("profileVisibility", settings.profileVisibility.name)
            put("allowZynpathIdSearch", settings.allowZynpathIdSearch)
            put("allowFriendRequests", settings.allowFriendRequests)
        }.toString()

        val request = Request.Builder()
            .url("${getBaseUrl()}/account/privacy")
            .put(jsonPayload.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val json = JSONObject(bodyString)
                NetworkResult.Success(
                    PlayerPrivacySettings(
                        profileVisibility = ProfileVisibility.fromString(json.optString("profileVisibility", "PUBLIC")),
                        allowZynpathIdSearch = json.optBoolean("allowZynpathIdSearch", true),
                        allowFriendRequests = json.optBoolean("allowFriendRequests", true)
                    )
                )
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getLinkedProviders(sessionToken: String): NetworkResult<List<String>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/account/providers")
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
                val jsonArr = JSONArray(bodyString)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArr.length()) {
                    list.add(jsonArr.getString(i))
                }
                NetworkResult.Success(list)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun unlinkProvider(
        sessionToken: String,
        provider: String
    ): NetworkResult<List<String>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/account/providers/$provider")
            .delete()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val jsonArr = JSONArray(bodyString)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArr.length()) {
                    list.add(jsonArr.getString(i))
                }
                NetworkResult.Success(list)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun requestDataExport(sessionToken: String): NetworkResult<DataExportResult> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/account/export")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val json = JSONObject(bodyString)
                NetworkResult.Success(
                    DataExportResult(
                        schemaVersion = json.optInt("schemaVersion", 1),
                        exportedAt = json.optLong("exportedAt", System.currentTimeMillis()),
                        jsonContent = bodyString
                    )
                )
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun deleteAccount(sessionToken: String): NetworkResult<AccountDeletionResult> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/account/delete")
            .delete()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val json = JSONObject(bodyString)
                NetworkResult.Success(
                    AccountDeletionResult(
                        status = json.optString("status", "SUCCESS"),
                        playerId = json.optString("playerId", ""),
                        deletedAt = json.optLong("deletedAt", System.currentTimeMillis()),
                        googlePlaySubscriptionNotice = json.optString(
                            "googlePlaySubscriptionNotice",
                            "Google Play subscriptions must be cancelled separately in the Google Play Store."
                        )
                    )
                )
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getBlockedPlayers(sessionToken: String): NetworkResult<List<BlockedPlayerItem>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/blocks")
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
                val jsonArr = JSONArray(bodyString)
                val list = mutableListOf<BlockedPlayerItem>()
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    list.add(
                        BlockedPlayerItem(
                            playerId = obj.getString("playerId"),
                            publicZynpathId = obj.getString("publicZynpathId"),
                            displayName = obj.getString("displayName"),
                            avatarUrl = if (obj.has("avatarUrl") && !obj.isNull("avatarUrl")) obj.getString("avatarUrl") else null,
                            blockedAt = obj.optLong("blockedAt", System.currentTimeMillis())
                        )
                    )
                }
                NetworkResult.Success(list)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun unblockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/social/blocks/$targetPlayerId")
            .delete()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
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
