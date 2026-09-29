package com.zynpath.game.core.notification.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clean immutable client domain model for in-app notifications.
 *
 * Implements Prompt 31 Section 8 & 47:
 * - Stable notification ID, recipient player ID, event type, timestamps, read/unread state.
 */
data class ZynpathNotification(
    val id: String,
    val recipientPlayerId: String,
    val eventType: NotificationEventType,
    val title: String,
    val message: String,
    val relatedResourceId: String?,
    val actionDestination: String?,
    val createdAt: Long,
    val expiresAt: Long?,
    val isRead: Boolean,
    val isDismissed: Boolean = false
) {
    val isExpired: Boolean
        get() = expiresAt != null && System.currentTimeMillis() > expiresAt

    val formattedTime: String
        get() {
            val now = System.currentTimeMillis()
            val diffMs = now - createdAt
            val diffSec = diffMs / 1000
            val diffMin = diffSec / 60
            val diffHours = diffMin / 60
            val diffDays = diffHours / 24

            return when {
                diffMin < 1 -> "Just now"
                diffMin < 60 -> "${diffMin}m ago"
                diffHours < 24 -> "${diffHours}h ago"
                diffDays < 7 -> "${diffDays}d ago"
                else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(createdAt))
            }
        }

    val actionLabel: String
        get() = when (eventType) {
            NotificationEventType.FRIEND_REQUEST -> "View Request"
            NotificationEventType.FRIEND_REQUEST_ACCEPTED -> "View Friends"
            NotificationEventType.FRIEND_DUEL_INVITATION -> "Accept Duel"
            NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
            NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED -> "View Duel"
            NotificationEventType.MINI_LEAGUE_INVITATION -> "Join Room"
            NotificationEventType.MINI_LEAGUE_READY,
            NotificationEventType.MATCH_STARTING -> "Open Match"
            NotificationEventType.MATCH_RESULT -> "View Results"
            NotificationEventType.DAILY_CHALLENGE_REMINDER -> "Play Daily"
        }
}

/**
 * In-app and system notification preferences.
 */
data class NotificationPreferences(
    val friendAlertsEnabled: Boolean = true,
    val multiplayerAlertsEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 9,
    val dailyReminderMinute: Int = 0,
    val isSystemPermissionGranted: Boolean = false
) {
    val formattedReminderTime: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", dailyReminderHour, dailyReminderMinute)
}
