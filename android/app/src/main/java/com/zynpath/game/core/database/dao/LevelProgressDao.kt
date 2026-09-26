package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zynpath.game.core.database.entity.LevelProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress WHERE worldId = :worldId ORDER BY levelId ASC")
    fun getLevelsForWorld(worldId: Int): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    suspend fun getLevelProgress(levelId: Int): LevelProgressEntity?

    @Query("SELECT COUNT(*) FROM level_progress WHERE isCompleted = 1")
    fun getCompletedLevelCount(): Flow<Int>

    @Query("SELECT SUM(stars) FROM level_progress")
    fun getTotalStarsEarned(): Flow<Int?>

    @Query("SELECT * FROM level_progress")
    suspend fun getAllProgressList(): List<LevelProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLevelProgress(progress: LevelProgressEntity)
}
