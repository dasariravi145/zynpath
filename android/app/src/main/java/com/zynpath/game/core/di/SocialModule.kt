package com.zynpath.game.core.di

import com.zynpath.game.core.social.api.OkHttpSocialApiService
import com.zynpath.game.core.social.api.SocialApiService
import com.zynpath.game.core.social.repository.SocialRepository
import com.zynpath.game.core.social.repository.SocialRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocialModule {

    @Binds
    @Singleton
    abstract fun bindSocialApiService(
        impl: OkHttpSocialApiService
    ): SocialApiService

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        impl: SocialRepositoryImpl
    ): SocialRepository
}
