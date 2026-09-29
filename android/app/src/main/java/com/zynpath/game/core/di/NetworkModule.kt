package com.zynpath.game.core.di

import com.zynpath.game.core.network.api.HealthApiService
import com.zynpath.game.core.network.api.OkHttpHealthApiService
import com.zynpath.game.core.network.repository.NetworkHealthRepository
import com.zynpath.game.core.network.repository.NetworkHealthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindHealthApiService(impl: OkHttpHealthApiService): HealthApiService

    @Binds
    @Singleton
    abstract fun bindDailyChallengeApiService(
        impl: com.zynpath.game.core.puzzle.daily.OkHttpDailyChallengeApiService
    ): com.zynpath.game.core.puzzle.daily.DailyChallengeApiService

    @Binds
    @Singleton
    abstract fun bindNetworkHealthRepository(impl: NetworkHealthRepositoryImpl): NetworkHealthRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
    }
}
