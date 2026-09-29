package com.zynpath.game.core.di

import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManagerImpl
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindAudioManager(
        impl: ZynpathAudioManagerImpl
    ): ZynpathAudioManager

    @Binds
    @Singleton
    abstract fun bindHapticManager(
        impl: ZynpathHapticManagerImpl
    ): ZynpathHapticManager

    @Binds
    @Singleton
    abstract fun bindFeedbackCoordinator(
        impl: com.zynpath.game.core.feedback.ZynpathFeedbackCoordinatorImpl
    ): com.zynpath.game.core.feedback.ZynpathFeedbackCoordinator
}
