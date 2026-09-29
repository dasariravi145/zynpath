package com.zynpath.game.core.notification.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android background alarm scheduler for Daily Challenge reminders.
 *
 * Implements Prompt 31 Section 34, 35, 36, 38:
 * - Uses device local timezone for trigger calculations.
 * - Inexact scheduling avoiding exact-alarm privileges.
 * - Cancels cleanly when disabled.
 */
@Singleton
class AlarmManagerDailyReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) : DailyReminderScheduler {

    private val tag = "DailyReminderScheduler"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override suspend fun scheduleDailyReminder(hourOfDay: Int, minute: Int) {
        if (alarmManager == null) return

        val intent = Intent(context, DailyChallengeReminderReceiver::class.java).apply {
            action = DailyChallengeReminderReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate next occurrence in local device timezone
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has already passed today, advance to tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val triggerAtMillis = calendar.timeInMillis
        Log.i(tag, "Scheduling daily challenge reminder for: ${calendar.time} (inexact repeat)")

        // Inexact repeating alarm (deferrable, battery friendly, no exact alarm permission needed)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            triggerAtMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override suspend fun cancelDailyReminder() {
        if (alarmManager == null) return

        val intent = Intent(context, DailyChallengeReminderReceiver::class.java).apply {
            action = DailyChallengeReminderReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.i(tag, "Cancelled future daily challenge reminders")
        }
    }

    override suspend fun rescheduleIfEnabled() {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        if (prefs.isDailyReminderEnabled) {
            scheduleDailyReminder(prefs.dailyReminderHour, prefs.dailyReminderMinute)
        } else {
            cancelDailyReminder()
        }
    }

    companion object {
        const val REMINDER_REQUEST_CODE = 8801
    }
}
