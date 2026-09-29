package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zynpath.game.core.database.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local achievement persistence.
 *
 * Implements Prompt 17 Section 17-22:
 * - Prevents duplicate unlock events
 * - Tracks distinct progress values
 */
@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements")
    fun observeAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    fun observeUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun observeUnlockedCount(): Flow<Int>

    @Query("SELECT * FROM achievements WHERE achievementId = :achievementId LIMIT 1")
    suspend fun getAchievement(achievementId: String): AchievementEntity?

    @Query("SELECT * FROM achievements")
    suspend fun getAllAchievements(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET currentProgress = :progress WHERE achievementId = :id AND isUnlocked = 0")
    suspend fun updateProgress(id: String, progress: Int)

    @Query("UPDATE achievements SET targetProgress = :target WHERE achievementId = :id AND isUnlocked = 0")
    suspend fun updateTargetProgress(id: String, target: Int)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt, currentProgress = targetProgress WHERE achievementId = :id AND isUnlocked = 0")
    suspend fun unlockAchievement(id: String, unlockedAt: Long): Int

    @Query("DELETE FROM achievements")
    suspend fun clearAchievements()
}
