package com.zynpath.game.core.sync.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zynpath.game.core.sync.coordinator.SyncCoordinator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Background WorkManager worker for deferrable, persistent synchronization.
 *
 * Implements Prompt 35 Section 15:
 * - Executes deferrable offline queue synchronization in the background.
 * - Enforces CONNECTED network constraints.
 * - Avoids always-running background services.
 */
class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val tag = "SyncWorker"

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SyncWorkerEntryPoint {
        fun syncCoordinator(): SyncCoordinator
    }

    override suspend fun doWork(): Result {
        Log.i(tag, "Executing background sync worker (runAttemptCount=$runAttemptCount)")

        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                SyncWorkerEntryPoint::class.java
            )
            val coordinator = entryPoint.syncCoordinator()
            val succeeded = coordinator.syncPendingOperations()

            if (succeeded) {
                Log.i(tag, "Background sync worker completed successfully")
                Result.success()
            } else {
                Log.w(tag, "Background sync reported incomplete; scheduling retry")
                if (runAttemptCount < 5) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Background sync worker encountered unexpected exception", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME_PERIODIC = "zynpath_periodic_sync_work"
        const val WORK_NAME_EXPEDITED = "zynpath_expedited_sync_work"
    }
}
