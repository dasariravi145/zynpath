package com.zynpath.game.core.di

import com.zynpath.game.core.account.api.AccountApiService
import com.zynpath.game.core.account.api.OkHttpAccountApiService
import com.zynpath.game.core.account.repository.AccountRepository
import com.zynpath.game.core.account.repository.AccountRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountModule {

    @Binds
    @Singleton
    abstract fun bindAccountApiService(
        impl: OkHttpAccountApiService
    ): AccountApiService

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository
}
