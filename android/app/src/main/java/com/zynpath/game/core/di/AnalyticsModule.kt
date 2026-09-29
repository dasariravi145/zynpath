package com.zynpath.game.core.di

import com.zynpath.game.core.analytics.repository.PersonalAnalyticsRepository
import com.zynpath.game.core.analytics.repository.PersonalAnalyticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindPersonalAnalyticsRepository(
        impl: PersonalAnalyticsRepositoryImpl
    ): PersonalAnalyticsRepository
}
