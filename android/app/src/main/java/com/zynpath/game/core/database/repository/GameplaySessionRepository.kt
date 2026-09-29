package com.zynpath.game.core.database.repository

import com.zynpath.game.core.database.dao.GameSessionDao
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.SessionStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for durable gameplay session persistence and lifecycle state restoration.
 *
 * Implements Prompt 13 Sections 6, 7, 10, 24, 25:
 * - Room-backed session storage.
 * - Single active session enforcement across levels.
 * - Stale write prevention and write ordering via monotonically increasing revision numbers.
 * - Protection against overwriting completed puzzle sessions.
 */
interface GameplaySessionRepository {
    suspend fun getResumableSession(levelId: Int): GameplaySessionSnapshot?
    suspend fun getResumableSessionForLevel(levelId: Int): GameplaySessionSnapshot? = getResumableSession(levelId)
    suspend fun getLatestResumableSession(): GameplaySessionSnapshot?
    suspend fun getSessionById(sessionId: String): GameplaySessionSnapshot?
    suspend fun saveSession(snapshot: GameplaySessionSnapshot): Boolean
    suspend fun pauseAllActiveSessions(timestamp: Long = System.currentTimeMillis())
    suspend fun markCompleted(sessionId: String, elapsedActiveTimeMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean
    suspend fun markRestorationFailed(sessionId: String, errorDetail: String, nowMs: Long = System.currentTimeMillis()): Boolean
    suspend fun abandonSession(sessionId: String, nowMs: Long = System.currentTimeMillis()): Boolean
    suspend fun deleteSessionsForLevel(levelId: Int)
    suspend fun deleteSessionById(sessionId: String)
    suspend fun clearAllSessions()
}

@Singleton
class GameplaySessionRepositoryImpl @Inject constructor(
    private val gameSessionDao: GameSessionDao
) : GameplaySessionRepository {

    private val writeMutex = Mutex()

    override suspend fun getResumableSession(levelId: Int): GameplaySessionSnapshot? {
        val entity = gameSessionDao.getResumableSessionForLevel(levelId) ?: return null
        return GameplaySessionSnapshot.fromEntity(entity)
    }

    override suspend fun getLatestResumableSession(): GameplaySessionSnapshot? {
        val entity = gameSessionDao.getLatestResumableSession() ?: return null
        return GameplaySessionSnapshot.fromEntity(entity)
    }

    override suspend fun getSessionById(sessionId: String): GameplaySessionSnapshot? {
        val entity = gameSessionDao.getSessionById(sessionId) ?: return null
        return GameplaySessionSnapshot.fromEntity(entity)
    }

    override suspend fun saveSession(snapshot: GameplaySessionSnapshot): Boolean = writeMutex.withLock {
        val existingTuple = gameSessionDao.getSessionRevisionAndStatus(snapshot.sessionId)
        if (existingTuple != null) {
            // Guard 1: Never overwrite a COMPLETED session with an active/paused snapshot
            if (existingTuple.status == SessionStatus.COMPLETED.name && snapshot.status != SessionStatus.COMPLETED) {
                return@withLock false
            }
            // Guard 2: Stale write rejection: Never overwrite a newer revision with an older or equal revision
            if (existingTuple.revision >= snapshot.revision && existingTuple.status == snapshot.status.name) {
                return@withLock false
            }
        }
        gameSessionDao.insertSession(snapshot.toEntity())
        return@withLock true
    }

    override suspend fun pauseAllActiveSessions(timestamp: Long) = writeMutex.withLock {
        gameSessionDao.pauseAllActiveSessions(timestamp)
    }

    override suspend fun markCompleted(sessionId: String, elapsedActiveTimeMs: Long, nowMs: Long): Boolean = writeMutex.withLock {
        val existing = gameSessionDao.getSessionById(sessionId) ?: return@withLock false
        if (existing.status == SessionStatus.COMPLETED.name) return@withLock true
        val updated = existing.copy(
            status = SessionStatus.COMPLETED.name,
            elapsedActiveTimeMs = elapsedActiveTimeMs,
            lastUpdatedAt = nowMs,
            revision = existing.revision + 1
        )
        gameSessionDao.insertSession(updated)
        return@withLock true
    }

    override suspend fun markRestorationFailed(sessionId: String, errorDetail: String, nowMs: Long): Boolean = writeMutex.withLock {
        val existing = gameSessionDao.getSessionById(sessionId) ?: return@withLock false
        val updated = existing.copy(
            status = SessionStatus.RESTORATION_FAILED.name,
            lastUpdatedAt = nowMs,
            revision = existing.revision + 1
        )
        gameSessionDao.insertSession(updated)
        return@withLock true
    }

    override suspend fun abandonSession(sessionId: String, nowMs: Long): Boolean = writeMutex.withLock {
        val existing = gameSessionDao.getSessionById(sessionId) ?: return@withLock false
        val updated = existing.copy(
            status = SessionStatus.ABANDONED.name,
            lastUpdatedAt = nowMs,
            revision = existing.revision + 1
        )
        gameSessionDao.insertSession(updated)
        return@withLock true
    }

    override suspend fun deleteSessionsForLevel(levelId: Int) = writeMutex.withLock {
        gameSessionDao.deleteSessionsForLevel(levelId)
    }

    override suspend fun deleteSessionById(sessionId: String) = writeMutex.withLock {
        gameSessionDao.deleteSessionById(sessionId)
    }

    override suspend fun clearAllSessions() = writeMutex.withLock {
        gameSessionDao.clearAllSessions()
    }
}
