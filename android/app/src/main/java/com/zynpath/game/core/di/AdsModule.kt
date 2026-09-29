package com.zynpath.game.core.di

import com.zynpath.game.core.ads.AdMobRewardedHintAdManager
import com.zynpath.game.core.ads.consent.AdConsentManager
import com.zynpath.game.core.ads.consent.AdConsentManagerImpl
import com.zynpath.game.core.ads.network.AdRewardApiService
import com.zynpath.game.core.ads.network.OkHttpAdRewardApiService
import com.zynpath.game.core.ads.repository.RewardedAdRepository
import com.zynpath.game.core.ads.repository.RewardedAdRepositoryImpl
import com.zynpath.game.core.hint.RewardedHintProvider
import com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdsModule {

    @Binds
    @Singleton
    abstract fun bindAdConsentManager(
        impl: AdConsentManagerImpl
    ): AdConsentManager

    @Binds
    @Singleton
    abstract fun bindAdRewardApiService(
        impl: OkHttpAdRewardApiService
    ): AdRewardApiService

    @Binds
    @Singleton
    abstract fun bindRewardedAdRepository(
        impl: RewardedAdRepositoryImpl
    ): RewardedAdRepository

    @Binds
    @Singleton
    abstract fun bindRewardedHintProvider(
        impl: RewardedAdRepositoryImpl
    ): RewardedHintProvider

    @Binds
    @Singleton
    abstract fun bindRewardedHintAdContract(
        impl: AdMobRewardedHintAdManager
    ): RewardedHintAdContract
}
