package com.zynpath.game.core.di

import com.zynpath.game.core.puzzle.premium.network.OkHttpPremiumPackApiService
import com.zynpath.game.core.puzzle.premium.network.PremiumPackApiService
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackProgressRepository
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackProgressRepositoryImpl
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepository
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PremiumContentModule {

    @Binds
    @Singleton
    abstract fun bindPremiumPackProgressRepository(
        impl: PremiumPackProgressRepositoryImpl
    ): PremiumPackProgressRepository

    @Binds
    @Singleton
    abstract fun bindPremiumPackApiService(
        impl: OkHttpPremiumPackApiService
    ): PremiumPackApiService

    @Binds
    @Singleton
    abstract fun bindPremiumPackRepository(
        impl: PremiumPackRepositoryImpl
    ): PremiumPackRepository
}
