package com.zynpath.game.core.di

import com.zynpath.game.core.cosmetics.network.CosmeticsApiService
import com.zynpath.game.core.cosmetics.network.OkHttpCosmeticsApiService
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepository
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CosmeticsModule {

    @Binds
    @Singleton
    abstract fun bindCosmeticsRepository(
        impl: CosmeticsRepositoryImpl
    ): CosmeticsRepository

    @Binds
    @Singleton
    abstract fun bindCosmeticsApiService(
        impl: OkHttpCosmeticsApiService
    ): CosmeticsApiService
}
