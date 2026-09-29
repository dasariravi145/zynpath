package com.zynpath.game.core.notification.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.NotificationDao
import com.zynpath.game.core.database.entity.NotificationEntity
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.notification.model.NotificationEventType
import com.zynpath.game.core.notification.system.SystemNotificationHelper
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * BroadcastReceiver triggered by AlarmManager to post Daily Challenge reminders.
 *
 * Implements Prompt 31 Section 32, 36, 37:
 * - Checks local completion state: suppresses reminder if challenge was completed today.
 * - Posts system notification with deep link to Daily Challenge.
 * - Stores local in-app notification record.
 */
@AndroidEntryPoint
class DailyChallengeReminderReceiver : BroadcastReceiver() {

    private val tag = "DailyReminderReceiver"

    @Inject lateinit var dailyChallengeDao: DailyChallengeDao
    @Inject lateinit var clock: DailyChallengeClock
    @Inject lateinit var notificationHelper: SystemNotificationHelper
    @Inject lateinit var notificationDao: NotificationDao
    @Inject lateinit var preferencesRepository: PreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_DAILY_REMINDER) return

        Log.i(tag, "Daily challenge reminder alarm triggered")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = preferencesRepository.userPreferencesFlow.first()
                if (!prefs.isDailyReminderEnabled) {
                    Log.i(tag, "Daily reminders disabled in preferences, skipping notification")
                    return@launch
                }

                // Check if today's challenge is already completed locally
                val todayKey = clock.currentUtcDateKey()
                val todayEntity = dailyChallengeDao.getDailyChallenge(todayKey).first()

                if (todayEntity?.isCompleted == true) {
                    Log.i(tag, "Today's daily challenge ($todayKey) is already completed, suppressing reminder")
                    return@launch
                }

                val title = "Daily Challenge Ready"
                val message = "Today's number path puzzle is waiting! Keep your daily streak alive."

                // 1. Post Android system notification
                notificationHelper.showDailyReminder(title, message)

                // 2. Persist in-app notification record
                val notificationId = "notif_daily_" + UUID.randomUUID().toString().substring(0, 8)
                val notifEntity = NotificationEntity(
                    notificationId = notificationId,
                    recipientPlayerId = prefs.guestUuid.ifBlank { "local_player" },
                    eventType = NotificationEventType.DAILY_CHALLENGE_REMINDER.name,
                    title = title,
                    message = message,
                    relatedResourceId = todayKey,
                    actionDestination = "daily",
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                    isRead = false,
                    isDismissed = false
                )
                notificationDao.insertNotification(notifEntity)
                Log.i(tag, "Posted Daily Challenge reminder successfully")
            } catch (e: Exception) {
                Log.e(tag, "Error handling daily reminder broadcast", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER = "com.zynpath.game.ACTION_DAILY_CHALLENGE_REMINDER"
    }
}
