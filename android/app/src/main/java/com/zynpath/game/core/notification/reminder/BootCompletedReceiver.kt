package com.zynpath.game.core.notification.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver triggered after device reboot to restore scheduled Daily Challenge reminders.
 *
 * Implements Prompt 31 Section 39:
 * - Restores scheduled reminder behavior after device restart.
 * - Avoids keeping an always-running background service.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    private val tag = "BootCompletedReceiver"

    @Inject lateinit var dailyReminderScheduler: DailyReminderScheduler
    @Inject lateinit var syncScheduler: com.zynpath.game.core.sync.scheduler.SyncScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.i(tag, "System boot completed or package replaced: restoring reminders and sync schedule")
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    dailyReminderScheduler.rescheduleIfEnabled()
                    syncScheduler.schedulePeriodicSync()
                } catch (e: Exception) {
                    Log.e(tag, "Failed to reschedule reminders or sync on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
