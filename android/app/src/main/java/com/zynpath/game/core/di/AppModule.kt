package com.zynpath.game.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.PreferencesRepositoryImpl
import com.zynpath.game.core.puzzle.session.SystemTimeProvider
import com.zynpath.game.core.puzzle.session.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        impl: com.zynpath.game.core.database.repository.ProgressRepositoryImpl
    ): com.zynpath.game.core.database.repository.ProgressRepository

    @Binds
    @Singleton
    abstract fun bindHintUsageRepository(
        impl: com.zynpath.game.core.hint.HintUsageRepositoryImpl
    ): com.zynpath.game.core.hint.HintUsageRepository

    @Binds
    @Singleton
    abstract fun bindDailyChallengeClock(
        impl: com.zynpath.game.core.puzzle.daily.SystemDailyChallengeClock
    ): com.zynpath.game.core.puzzle.daily.DailyChallengeClock

    @Binds
    @Singleton
    abstract fun bindDailyChallengeRepository(
        impl: com.zynpath.game.core.puzzle.daily.DailyChallengeRepositoryImpl
    ): com.zynpath.game.core.puzzle.daily.DailyChallengeRepository

    @Binds
    @Singleton
    abstract fun bindPlayerProfileRepository(
        impl: com.zynpath.game.core.player.PlayerProfileRepositoryImpl
    ): com.zynpath.game.core.player.PlayerProfileRepository

    @Binds
    @Singleton
    abstract fun bindLevelHintRepository(
        impl: com.zynpath.game.core.puzzle.experience.hint.LevelHintRepositoryImpl
    ): com.zynpath.game.core.puzzle.experience.hint.LevelHintRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: com.zynpath.game.core.achievement.AchievementRepositoryImpl
    ): com.zynpath.game.core.achievement.AchievementRepository

    @Binds
    @Singleton
    abstract fun bindDeterministicLevelProvider(
        impl: com.zynpath.game.core.puzzle.generator.DeterministicLevelProviderImpl
    ): com.zynpath.game.core.puzzle.generator.DeterministicLevelProvider

    companion object {
        private const val USER_PREFERENCES_NAME = "zynpath_preferences"

        @Provides
        @Singleton
        fun providePreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_NAME) }
            )
        }

        @Provides
        @Singleton
        fun provideTimeProvider(): TimeProvider {
            return SystemTimeProvider()
        }

        @Provides
        @Singleton
        fun providePuzzleHintEngine(): com.zynpath.game.core.puzzle.hint.PuzzleHintEngine {
            return com.zynpath.game.core.puzzle.hint.PuzzleHintEngine()
        }

        @Provides
        @Singleton
        fun provideRewardedHintProvider(): com.zynpath.game.core.hint.RewardedHintProvider {
            return com.zynpath.game.core.hint.NoOpRewardedHintProvider()
        }

        @Provides
        @Singleton
        fun provideCoroutineDispatcher(): kotlinx.coroutines.CoroutineDispatcher {
            return Dispatchers.IO
        }
    }
}
