package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zynpath.game.core.database.entity.GameSessionEntity

data class SessionRevisionTuple(
    val revision: Long,
    val status: String
)

@Dao
interface GameSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity)

    @Update
    suspend fun updateSession(session: GameSessionEntity)

    @Query("SELECT * FROM game_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): GameSessionEntity?

    suspend fun getSession(sessionId: String): GameSessionEntity? = getSessionById(sessionId)

    @Query("SELECT * FROM game_sessions WHERE levelId = :levelId AND status IN ('ACTIVE', 'PAUSED', 'NOT_STARTED') ORDER BY revision DESC, lastUpdatedAt DESC LIMIT 1")
    suspend fun getResumableSessionForLevel(levelId: Int): GameSessionEntity?

    @Query("SELECT * FROM game_sessions WHERE levelId = :levelId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveSession(levelId: Int): GameSessionEntity?

    @Query("SELECT * FROM game_sessions WHERE status IN ('ACTIVE', 'PAUSED') ORDER BY lastUpdatedAt DESC LIMIT 1")
    suspend fun getLatestResumableSession(): GameSessionEntity?

    @Query("SELECT revision, status FROM game_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionRevisionAndStatus(sessionId: String): SessionRevisionTuple?

    @Query("UPDATE game_sessions SET status = 'PAUSED', lastUpdatedAt = :timestamp WHERE status = 'ACTIVE'")
    suspend fun pauseAllActiveSessions(timestamp: Long)

    @Query("UPDATE game_sessions SET status = 'ABANDONED', lastUpdatedAt = :timestamp WHERE levelId = :levelId AND status IN ('ACTIVE', 'PAUSED')")
    suspend fun abandonActiveSession(levelId: Int, timestamp: Long)

    @Query("DELETE FROM game_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: String)

    suspend fun deleteSession(sessionId: String) = deleteSessionById(sessionId)

    @Query("DELETE FROM game_sessions WHERE levelId = :levelId")
    suspend fun deleteSessionsForLevel(levelId: Int)

    @Query("DELETE FROM game_sessions")
    suspend fun clearAllSessions()
}
