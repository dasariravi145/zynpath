package com.zynpath.game.core.di

import com.zynpath.game.core.multiplayer.api.MultiplayerApiService
import com.zynpath.game.core.multiplayer.api.OkHttpMultiplayerApiService
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MultiplayerModule {

    @Binds
    @Singleton
    abstract fun bindMultiplayerApiService(
        impl: OkHttpMultiplayerApiService
    ): MultiplayerApiService

    @Binds
    @Singleton
    abstract fun bindMultiplayerRepository(
        impl: MultiplayerRepositoryImpl
    ): MultiplayerRepository
}
