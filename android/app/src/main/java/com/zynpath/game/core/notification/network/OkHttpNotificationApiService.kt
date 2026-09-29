package com.zynpath.game.core.notification.network

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.notification.model.NotificationEventType
import com.zynpath.game.core.notification.model.NotificationPreferences
import com.zynpath.game.core.notification.model.ZynpathNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp implementation of the Notification REST client.
 */
@Singleton
class OkHttpNotificationApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getNotifications(
        authToken: String,
        limit: Int,
        offset: Int
    ): NetworkResult<List<ZynpathNotification>> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications?limit=$limit&offset=$offset"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to load notifications: ${response.message}")
            }

            val json = JSONObject(body)
            val notifsArray = json.optJSONArray("notifications") ?: JSONArray()
            val list = mutableListOf<ZynpathNotification>()

            for (i in 0 until notifsArray.length()) {
                val item = notifsArray.getJSONObject(i)
                val type = try {
                    NotificationEventType.valueOf(item.getString("eventType"))
                } catch (e: Exception) {
                    NotificationEventType.FRIEND_REQUEST
                }

                list.add(
                    ZynpathNotification(
                        id = item.getString("id"),
                        recipientPlayerId = item.optString("recipientPlayerId", ""),
                        eventType = type,
                        title = item.getString("title"),
                        message = item.getString("message"),
                        relatedResourceId = if (item.has("relatedResourceId") && !item.isNull("relatedResourceId")) item.getString("relatedResourceId") else null,
                        actionDestination = if (item.has("actionDestination") && !item.isNull("actionDestination")) item.getString("actionDestination") else null,
                        createdAt = item.getLong("createdAt"),
                        expiresAt = if (item.has("expiresAt") && !item.isNull("expiresAt")) item.getLong("expiresAt") else null,
                        isRead = item.getBoolean("isRead")
                    )
                )
            }
            NetworkResult.Success(list)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getUnreadCount(authToken: String): NetworkResult<Int> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/unread-count"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to get unread count: ${response.message}")
            }
            val json = JSONObject(body)
            val count = json.optInt("unreadCount", 0)
            NetworkResult.Success(count)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun markAsRead(authToken: String, notificationId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/$notificationId/read"
        val request = Request.Builder()
            .url(url)
            .put("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to mark notification read: ${response.message}")
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun markAllAsRead(authToken: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/read-all"
        val request = Request.Builder()
            .url(url)
            .put("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to mark all read: ${response.message}")
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getPreferences(authToken: String): NetworkResult<NotificationPreferences> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/preferences"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to get notification preferences: ${response.message}")
            }
            val json = JSONObject(body)
            NetworkResult.Success(
                NotificationPreferences(
                    friendAlertsEnabled = json.optBoolean("friendAlertsEnabled", true),
                    multiplayerAlertsEnabled = json.optBoolean("multiplayerAlertsEnabled", true),
                    dailyReminderEnabled = json.optBoolean("dailyReminderEnabled", false),
                    dailyReminderHour = json.optInt("dailyReminderHour", 9),
                    dailyReminderMinute = json.optInt("dailyReminderMinute", 0)
                )
            )
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun updatePreferences(
        authToken: String,
        preferences: NotificationPreferences
    ): NetworkResult<NotificationPreferences> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/preferences"
        val jsonPayload = JSONObject().apply {
            put("friendAlertsEnabled", preferences.friendAlertsEnabled)
            put("multiplayerAlertsEnabled", preferences.multiplayerAlertsEnabled)
            put("dailyReminderEnabled", preferences.dailyReminderEnabled)
            put("dailyReminderHour", preferences.dailyReminderHour)
            put("dailyReminderMinute", preferences.dailyReminderMinute)
        }

        val request = Request.Builder()
            .url(url)
            .put(jsonPayload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to update notification preferences: ${response.message}")
            }
            val json = JSONObject(body)
            NetworkResult.Success(
                NotificationPreferences(
                    friendAlertsEnabled = json.optBoolean("friendAlertsEnabled", true),
                    multiplayerAlertsEnabled = json.optBoolean("multiplayerAlertsEnabled", true),
                    dailyReminderEnabled = json.optBoolean("dailyReminderEnabled", false),
                    dailyReminderHour = json.optInt("dailyReminderHour", 9),
                    dailyReminderMinute = json.optInt("dailyReminderMinute", 0)
                )
            )
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun registerPushToken(
        authToken: String,
        deviceToken: String,
        platform: String
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/push-token"
        val jsonPayload = JSONObject().apply {
            put("deviceToken", deviceToken)
            put("platform", platform)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to register push token: ${response.message}")
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun unregisterPushToken(
        authToken: String,
        deviceToken: String
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/notifications/push-token"
        val jsonPayload = JSONObject().apply {
            put("deviceToken", deviceToken)
        }

        val request = Request.Builder()
            .url(url)
            .delete(jsonPayload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to unregister push token: ${response.message}")
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }
}
