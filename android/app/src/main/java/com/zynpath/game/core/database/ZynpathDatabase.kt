package com.zynpath.game.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity

@Database(
    entities = [
        LevelProgressEntity::class,
        PlayerStatsEntity::class,
        DailyChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZynpathDatabase : RoomDatabase() {
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun playerStatsDao(): PlayerStatsDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
}
