package com.zynpath.game.core.di

import com.zynpath.game.core.notification.network.NotificationApiService
import com.zynpath.game.core.notification.network.OkHttpNotificationApiService
import com.zynpath.game.core.notification.reminder.AlarmManagerDailyReminderScheduler
import com.zynpath.game.core.notification.reminder.DailyReminderScheduler
import com.zynpath.game.core.notification.repository.NotificationRepository
import com.zynpath.game.core.notification.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationApiService(
        impl: OkHttpNotificationApiService
    ): NotificationApiService

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindDailyReminderScheduler(
        impl: AlarmManagerDailyReminderScheduler
    ): DailyReminderScheduler
}
