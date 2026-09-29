package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zynpath.game.core.database.entity.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local player profile operations.
 *
 * Implements Prompt 17 Section 7-14.
 */
@Dao
interface PlayerProfileDao {

    @Query("SELECT * FROM player_profile LIMIT 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile LIMIT 1")
    suspend fun getProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: PlayerProfileEntity)

    @Query("UPDATE player_profile SET displayName = :displayName, lastActiveAt = :timestamp WHERE playerId = :playerId")
    suspend fun updateDisplayName(playerId: String, displayName: String, timestamp: Long)

    @Query("UPDATE player_profile SET avatarId = :avatarId, lastActiveAt = :timestamp WHERE playerId = :playerId")
    suspend fun updateAvatar(playerId: String, avatarId: String, timestamp: Long)

    @Query("UPDATE player_profile SET accountType = :accountType, publicZynpathId = :publicId, lastActiveAt = :timestamp WHERE playerId = :playerId")
    suspend fun updateAccountLinking(playerId: String, accountType: String, publicId: String?, timestamp: Long)

    @Query("DELETE FROM player_profile")
    suspend fun clearProfile()
}
