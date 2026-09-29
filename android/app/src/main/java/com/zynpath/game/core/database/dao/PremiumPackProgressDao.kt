package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zynpath.game.core.database.entity.PremiumPackProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for Premium pack progress records.
 *
 * Implements Prompt 27 Section 30, 31 & 32.
 */
@Dao
interface PremiumPackProgressDao {

    @Query("SELECT * FROM premium_pack_progress WHERE playerId = :playerId AND packId = :packId ORDER BY levelIndex ASC")
    fun observePackProgress(playerId: String, packId: String): Flow<List<PremiumPackProgressEntity>>

    @Query("SELECT * FROM premium_pack_progress WHERE playerId = :playerId AND packId = :packId ORDER BY levelIndex ASC")
    suspend fun getPackProgress(playerId: String, packId: String): List<PremiumPackProgressEntity>

    @Query("SELECT * FROM premium_pack_progress WHERE playerId = :playerId AND packId = :packId AND levelIndex = :levelIndex LIMIT 1")
    suspend fun getLevelProgress(playerId: String, packId: String, levelIndex: Int): PremiumPackProgressEntity?

    @Query("SELECT COUNT(*) FROM premium_pack_progress WHERE playerId = :playerId AND packId = :packId AND isCompleted = 1")
    fun observeCompletedCount(playerId: String, packId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM premium_pack_progress WHERE playerId = :playerId AND packId = :packId AND isCompleted = 1")
    suspend fun getCompletedCount(playerId: String, packId: String): Int

    @Query("SELECT COUNT(*) FROM premium_pack_progress WHERE playerId = :playerId AND isCompleted = 1")
    fun observeTotalCompletedCount(playerId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM premium_pack_progress WHERE playerId = :playerId AND isCompleted = 1")
    suspend fun getTotalCompletedPremiumPuzzles(playerId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: PremiumPackProgressEntity)

    @Query("DELETE FROM premium_pack_progress WHERE playerId = :playerId")
    suspend fun deleteProgressForPlayer(playerId: String)
}
