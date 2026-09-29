package com.zynpath.game.core.sync.scheduler

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zynpath.game.core.sync.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for scheduling WorkManager background sync jobs.
 *
 * Implements Prompt 35 Sections 15 & 27:
 * - Persistent scheduling across device restarts.
 * - Enforces CONNECTED network constraints.
 * - Bounded backoff retry configuration.
 */
interface SyncScheduler {
    fun schedulePeriodicSync()
    fun scheduleOneTimeSync()
    fun cancelAllSyncWork()
}

@Singleton
class SyncSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncScheduler {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 30,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    override fun scheduleOneTimeSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            SyncWorker.WORK_NAME_EXPEDITED,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )
    }

    override fun cancelAllSyncWork() {
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME_PERIODIC)
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME_EXPEDITED)
    }
}
