package com.zynpath.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ZynpathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-wide initialization without blocking cloud calls
    }
}
