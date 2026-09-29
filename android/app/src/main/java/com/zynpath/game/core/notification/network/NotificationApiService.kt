package com.zynpath.game.core.notification.network

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.notification.model.NotificationPreferences
import com.zynpath.game.core.notification.model.ZynpathNotification

/**
 * Remote API service for player notifications and push token lifecycle.
 *
 * Implements Prompt 31 Section 54:
 * - List notifications, unread count, read states, preferences, push token registration.
 */
interface NotificationApiService {

    suspend fun getNotifications(
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): NetworkResult<List<ZynpathNotification>>

    suspend fun getUnreadCount(authToken: String): NetworkResult<Int>

    suspend fun markAsRead(authToken: String, notificationId: String): NetworkResult<Unit>

    suspend fun markAllAsRead(authToken: String): NetworkResult<Unit>

    suspend fun getPreferences(authToken: String): NetworkResult<NotificationPreferences>

    suspend fun updatePreferences(
        authToken: String,
        preferences: NotificationPreferences
    ): NetworkResult<NotificationPreferences>

    suspend fun registerPushToken(
        authToken: String,
        deviceToken: String,
        platform: String = "ANDROID"
    ): NetworkResult<Unit>

    suspend fun unregisterPushToken(
        authToken: String,
        deviceToken: String
    ): NetworkResult<Unit>
}
