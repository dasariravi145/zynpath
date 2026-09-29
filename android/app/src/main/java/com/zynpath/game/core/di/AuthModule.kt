package com.zynpath.game.core.di

import com.zynpath.game.core.auth.api.AuthApiService
import com.zynpath.game.core.auth.api.OkHttpAuthApiService
import com.zynpath.game.core.auth.provider.FacebookAuthClient
import com.zynpath.game.core.auth.provider.FacebookAuthClientImpl
import com.zynpath.game.core.auth.provider.GoogleAuthClient
import com.zynpath.game.core.auth.provider.GoogleAuthClientImpl
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.auth.repository.AuthRepositoryImpl
import com.zynpath.game.core.auth.storage.KeystoreEncryptedTokenStorage
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindSecureTokenStorage(
        impl: KeystoreEncryptedTokenStorage
    ): SecureTokenStorage

    @Binds
    @Singleton
    abstract fun bindGoogleAuthClient(
        impl: GoogleAuthClientImpl
    ): GoogleAuthClient

    @Binds
    @Singleton
    abstract fun bindFacebookAuthClient(
        impl: FacebookAuthClientImpl
    ): FacebookAuthClient

    @Binds
    @Singleton
    abstract fun bindAuthApiService(
        impl: OkHttpAuthApiService
    ): AuthApiService

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
