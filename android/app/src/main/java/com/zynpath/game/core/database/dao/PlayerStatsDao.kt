package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStatsDao {
    @Query("SELECT * FROM player_stats WHERE id = 1 LIMIT 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayerStats(stats: PlayerStatsEntity)
}
