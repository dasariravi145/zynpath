package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.puzzle.model.GridPosition
import java.util.UUID

/**
 * Immutable snapshot of a persistent gameplay session.
 *
 * Implements Prompt 13 Sections 7, 11, 25:
 * - Stable UUID session identity.
 * - Exact puzzle ID, puzzle version, and catalog version.
 * - Ordered path history and monotonic elapsed active time.
 * - Revision number for write ordering and stale save rejection.
 */
data class GameplaySessionSnapshot(
    val sessionId: String,
    val levelId: Int,
    val worldId: Int,
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val catalogVersion: String = "1.0.0",
    val status: SessionStatus = SessionStatus.NOT_STARTED,
    val path: List<GridPosition> = emptyList(),
    val elapsedActiveTimeMs: Long = 0L,
    val moveCount: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val revision: Long = 1L,
    val snapshotSchemaVersion: Int = 1,
    val ownerIdentity: String? = null
) {
    val isResumable: Boolean
        get() = status == SessionStatus.ACTIVE || status == SessionStatus.PAUSED || status == SessionStatus.NOT_STARTED

    fun incrementRevision(
        path: List<GridPosition> = this.path,
        elapsedActiveTimeMs: Long = this.elapsedActiveTimeMs,
        status: SessionStatus = this.status,
        moveCount: Int = this.moveCount,
        nowMs: Long = System.currentTimeMillis()
    ): GameplaySessionSnapshot {
        return copy(
            path = path,
            elapsedActiveTimeMs = elapsedActiveTimeMs,
            status = status,
            moveCount = moveCount,
            revision = revision + 1,
            lastUpdatedAt = nowMs
        )
    }

    fun toEntity(): GameSessionEntity {
        return GameSessionEntity(
            sessionId = sessionId,
            levelId = levelId,
            worldId = worldId,
            puzzleSeed = 0L, // Kept for schema backwards compatibility
            startedAt = startedAt,
            lastUpdatedAt = lastUpdatedAt,
            elapsedActiveTimeMs = elapsedActiveTimeMs,
            status = status.name,
            pathSnapshot = SessionPathSerializer.serialize(path),
            moveCount = moveCount,
            hintCount = 0,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            catalogVersion = catalogVersion,
            revision = revision,
            snapshotSchemaVersion = snapshotSchemaVersion,
            ownerIdentity = ownerIdentity
        )
    }

    companion object {
        fun createNew(
            levelId: Int,
            worldId: Int,
            puzzleId: String,
            puzzleVersion: Int = 1,
            catalogVersion: String = "1.0.0",
            nowMs: Long = System.currentTimeMillis(),
            ownerIdentity: String? = null
        ): GameplaySessionSnapshot {
            return GameplaySessionSnapshot(
                sessionId = UUID.randomUUID().toString(),
                levelId = levelId,
                worldId = worldId,
                puzzleId = puzzleId,
                puzzleVersion = puzzleVersion,
                catalogVersion = catalogVersion,
                status = SessionStatus.NOT_STARTED,
                path = emptyList(),
                elapsedActiveTimeMs = 0L,
                moveCount = 0,
                startedAt = nowMs,
                lastUpdatedAt = nowMs,
                revision = 1L,
                snapshotSchemaVersion = 1,
                ownerIdentity = ownerIdentity
            )
        }

        fun fromEntity(entity: GameSessionEntity): GameplaySessionSnapshot {
            val deserializedPath = try {
                SessionPathSerializer.deserialize(entity.pathSnapshot)
            } catch (_: Exception) {
                emptyList()
            }
            return GameplaySessionSnapshot(
                sessionId = entity.sessionId,
                levelId = entity.levelId,
                worldId = entity.worldId,
                puzzleId = entity.puzzleId,
                puzzleVersion = entity.puzzleVersion,
                catalogVersion = entity.catalogVersion,
                status = SessionStatus.fromString(entity.status),
                path = deserializedPath,
                elapsedActiveTimeMs = entity.elapsedActiveTimeMs,
                moveCount = entity.moveCount,
                startedAt = entity.startedAt,
                lastUpdatedAt = entity.lastUpdatedAt,
                revision = entity.revision,
                snapshotSchemaVersion = entity.snapshotSchemaVersion,
                ownerIdentity = entity.ownerIdentity
            )
        }
    }
}
