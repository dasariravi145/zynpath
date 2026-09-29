package com.zynpath.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch

import com.zynpath.game.core.sync.scheduler.SyncScheduler
import javax.inject.Inject

@HiltAndroidApp
class ZynpathApp : Application() {

    @Inject lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        // Application-wide initialization without blocking cloud calls
        try {
            syncScheduler.schedulePeriodicSync()
        } catch (_: Exception) {}

        try {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    com.google.android.gms.ads.MobileAds.initialize(this@ZynpathApp) {}
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
