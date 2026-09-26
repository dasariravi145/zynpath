package com.zynpath.game.core.di

import android.content.Context
import androidx.room.Room
import com.zynpath.game.core.database.ZynpathDatabase
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "zynpath_database.db"

    @Provides
    @Singleton
    fun provideZynpathDatabase(
        @ApplicationContext context: Context
    ): ZynpathDatabase {
        return Room.databaseBuilder(
            context,
            ZynpathDatabase::class.java,
            DATABASE_NAME
        ).build()
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
}
