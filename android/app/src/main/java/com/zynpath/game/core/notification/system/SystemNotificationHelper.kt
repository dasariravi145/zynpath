package com.zynpath.game.core.notification.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zynpath.game.MainActivity
import com.zynpath.game.R
import com.zynpath.game.core.notification.model.NotificationEventType
import com.zynpath.game.core.notification.model.ZynpathNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for building and displaying Android system notifications.
 *
 * Implements Prompt 31 Section 9, 13, 14, 40, 51:
 * - Proper channel routing and importance.
 * - Deep linking with intent filters.
 * - Privacy-conscious lock screen content.
 */
@Singleton
class SystemNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showSystemNotification(notification: ZynpathNotification) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) {
            return
        }

        NotificationChannels.createChannels(context)

        val channelId = when (notification.eventType) {
            NotificationEventType.FRIEND_REQUEST,
            NotificationEventType.FRIEND_REQUEST_ACCEPTED -> NotificationChannels.CHANNEL_FRIENDS

            NotificationEventType.FRIEND_DUEL_INVITATION,
            NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
            NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED,
            NotificationEventType.MINI_LEAGUE_INVITATION,
            NotificationEventType.MINI_LEAGUE_READY,
            NotificationEventType.MATCH_STARTING,
            NotificationEventType.MATCH_RESULT -> NotificationChannels.CHANNEL_MULTIPLAYER

            NotificationEventType.DAILY_CHALLENGE_REMINDER -> NotificationChannels.CHANNEL_DAILY_REMINDERS
        }

        val pendingIntent = createDeepLinkPendingIntent(notification)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
            .setPriority(
                if (channelId == NotificationChannels.CHANNEL_MULTIPLAYER)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        val notifId = notification.id.hashCode()
        try {
            manager.notify(notifId, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ permission not granted
        }
    }

    fun showDailyReminder(title: String, message: String) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) {
            return
        }

        NotificationChannels.createChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = Uri.parse("zynpath://daily")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_DAILY_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        try {
            manager.notify(9001, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }

    private fun createDeepLinkPendingIntent(notification: ZynpathNotification): PendingIntent {
        val uriString = when (notification.eventType) {
            NotificationEventType.DAILY_CHALLENGE_REMINDER -> "zynpath://daily"
            NotificationEventType.FRIEND_REQUEST,
            NotificationEventType.FRIEND_REQUEST_ACCEPTED -> {
                if (!notification.relatedResourceId.isNullOrBlank()) {
                    "zynpath://friends?invitationId=${Uri.encode(notification.relatedResourceId)}"
                } else {
                    "zynpath://friends"
                }
            }
            NotificationEventType.FRIEND_DUEL_INVITATION,
            NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
            NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED -> {
                if (!notification.relatedResourceId.isNullOrBlank()) {
                    "zynpath://friend_duel?targetId=${Uri.encode(notification.relatedResourceId)}"
                } else {
                    "zynpath://friend_duel"
                }
            }
            NotificationEventType.MINI_LEAGUE_INVITATION,
            NotificationEventType.MINI_LEAGUE_READY -> {
                if (!notification.relatedResourceId.isNullOrBlank()) {
                    "zynpath://minileague?code=${Uri.encode(notification.relatedResourceId)}"
                } else {
                    "zynpath://minileague"
                }
            }
            NotificationEventType.MATCH_STARTING,
            NotificationEventType.MATCH_RESULT -> {
                if (!notification.relatedResourceId.isNullOrBlank()) {
                    "zynpath://match/${Uri.encode(notification.relatedResourceId)}"
                } else {
                    "zynpath://notifications"
                }
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = Uri.parse(uriString)
        }

        val requestCode = notification.id.hashCode()
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
