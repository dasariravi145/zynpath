package com.zynpath.game.core.di

import android.content.Context
import androidx.room.Room
import com.zynpath.game.core.database.ZynpathDatabase
import com.zynpath.game.core.database.dao.AchievementDao
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.GameSessionDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerProfileDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import com.zynpath.game.core.database.dao.PremiumPackProgressDao
import com.zynpath.game.core.database.repository.GameplaySessionRepository
import com.zynpath.game.core.database.repository.GameplaySessionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "zynpath_local.db"

    @Provides
    @Singleton
    fun provideZynpathDatabase(
        @ApplicationContext context: Context
    ): ZynpathDatabase {
        return Room.databaseBuilder(
            context,
            ZynpathDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(
                ZynpathDatabase.MIGRATION_1_2,
                ZynpathDatabase.MIGRATION_2_3,
                ZynpathDatabase.MIGRATION_3_4,
                ZynpathDatabase.MIGRATION_4_5,
                ZynpathDatabase.MIGRATION_5_6,
                ZynpathDatabase.MIGRATION_6_7,
                ZynpathDatabase.MIGRATION_7_8,
                ZynpathDatabase.MIGRATION_8_9,
                ZynpathDatabase.MIGRATION_9_10,
                ZynpathDatabase.MIGRATION_10_11,
                ZynpathDatabase.MIGRATION_11_12
            )
            .build()
    }

    @Provides
    fun provideWalletDao(database: ZynpathDatabase): com.zynpath.game.core.database.dao.WalletDao {
        return database.walletDao()
    }

    @Provides
    fun provideSyncOperationDao(database: ZynpathDatabase): com.zynpath.game.core.database.dao.SyncOperationDao {
        return database.syncOperationDao()
    }

    @Provides
    fun provideNotificationDao(database: ZynpathDatabase): com.zynpath.game.core.database.dao.NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun providePremiumPackProgressDao(database: ZynpathDatabase): PremiumPackProgressDao {
        return database.premiumPackProgressDao()
    }

    @Provides
    fun provideLevelProgressDao(database: ZynpathDatabase): LevelProgressDao {
        return database.levelProgressDao()
    }

    @Provides
    fun providePlayerStatsDao(database: ZynpathDatabase): PlayerStatsDao {
        return database.playerStatsDao()
    }

    @Provides
    fun provideDailyChallengeDao(database: ZynpathDatabase): DailyChallengeDao {
        return database.dailyChallengeDao()
    }

    @Provides
    fun provideGameSessionDao(database: ZynpathDatabase): GameSessionDao {
        return database.gameSessionDao()
    }

    @Provides
    fun providePlayerProfileDao(database: ZynpathDatabase): PlayerProfileDao {
        return database.playerProfileDao()
    }

    @Provides
    fun provideAchievementDao(database: ZynpathDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun provideGameplaySessionRepository(
        gameSessionDao: GameSessionDao
    ): GameplaySessionRepository {
        return GameplaySessionRepositoryImpl(gameSessionDao)
    }
}
