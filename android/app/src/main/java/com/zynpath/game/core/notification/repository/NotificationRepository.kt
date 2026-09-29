package com.zynpath.game.core.notification.repository

import com.zynpath.game.core.notification.model.NotificationPreferences
import com.zynpath.game.core.notification.model.ZynpathNotification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface managing in-app notifications, preferences, and offline-first persistence.
 */
interface NotificationRepository {
    val notifications: Flow<List<ZynpathNotification>>
    val unreadCount: Flow<Int>
    val preferences: Flow<NotificationPreferences>

    suspend fun refreshNotifications()
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun dismissNotification(notificationId: String)
    suspend fun updatePreferences(preferences: NotificationPreferences)
    suspend fun onSignOut()
}
