package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenge WHERE dateKey = :dateKey LIMIT 1")
    fun getDailyChallenge(dateKey: String): Flow<DailyChallengeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyChallenge(challenge: DailyChallengeEntity)
}
