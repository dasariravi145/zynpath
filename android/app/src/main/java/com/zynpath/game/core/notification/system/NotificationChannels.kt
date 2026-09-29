package com.zynpath.game.core.notification.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Android Notification Channels configuration.
 *
 * Implements Prompt 31 Section 13 & 14:
 * - FRIENDS (default importance)
 * - MULTIPLAYER (high importance)
 * - DAILY_REMINDERS (default importance)
 */
object NotificationChannels {

    const val CHANNEL_FRIENDS = "zynpath_channel_friends"
    const val CHANNEL_MULTIPLAYER = "zynpath_channel_multiplayer"
    const val CHANNEL_DAILY_REMINDERS = "zynpath_channel_daily_reminders"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val friendsChannel = NotificationChannel(
                CHANNEL_FRIENDS,
                "Friends & Social",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Friend requests, accepted friendships, and social updates."
                enableVibration(true)
            }

            val multiplayerChannel = NotificationChannel(
                CHANNEL_MULTIPLAYER,
                "Multiplayer & Duels",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Friend Duel and Mini League invitations, match start, and results."
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDERS,
                "Daily Challenge Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Challenge availability, streak alerts, and puzzle reminders."
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(
                listOf(friendsChannel, multiplayerChannel, dailyChannel)
            )
        }
    }
}
