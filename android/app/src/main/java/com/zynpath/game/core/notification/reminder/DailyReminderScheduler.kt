package com.zynpath.game.core.notification.reminder

/**
 * Interface for scheduling and managing local Daily Challenge reminders.
 */
interface DailyReminderScheduler {
    suspend fun scheduleDailyReminder(hourOfDay: Int, minute: Int)
    suspend fun cancelDailyReminder()
    suspend fun rescheduleIfEnabled()
}
