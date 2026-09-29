package com.zynpath.game.core.di

import com.zynpath.game.core.sync.api.OkHttpSyncApiService
import com.zynpath.game.core.sync.api.SyncApiService
import com.zynpath.game.core.sync.connectivity.NetworkConnectivityMonitor
import com.zynpath.game.core.sync.connectivity.NetworkConnectivityMonitorImpl
import com.zynpath.game.core.sync.coordinator.SyncCoordinator
import com.zynpath.game.core.sync.coordinator.SyncCoordinatorImpl
import com.zynpath.game.core.sync.scheduler.SyncScheduler
import com.zynpath.game.core.sync.scheduler.SyncSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncApiService(
        impl: OkHttpSyncApiService
    ): SyncApiService

    @Binds
    @Singleton
    abstract fun bindNetworkConnectivityMonitor(
        impl: NetworkConnectivityMonitorImpl
    ): NetworkConnectivityMonitor

    @Binds
    @Singleton
    abstract fun bindSyncCoordinator(
        impl: SyncCoordinatorImpl
    ): SyncCoordinator

    @Binds
    @Singleton
    abstract fun bindSyncScheduler(
        impl: SyncSchedulerImpl
    ): SyncScheduler
}
