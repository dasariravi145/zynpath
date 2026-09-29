package com.zynpath.game.core.sync.model

/**
 * Data Transfer Objects for offline-first synchronization and conflict resolution.
 *
 * Implements Prompt 35 Sections 9, 11, 18, 53, 54:
 * - Versioned synchronization payloads.
 * - Idempotent operation status.
 * - Non-destructive level progress exchange.
 */

data class SyncOperationDto(
    val operationId: String,
    val operationType: String,
    val resourceIdentity: String,
    val payloadVersion: Int = 1,
    val payloadJson: String,
    val clientTimestamp: Long
)

data class BatchSyncRequest(
    val playerId: String,
    val operations: List<SyncOperationDto>
)

data class SyncOperationResultDto(
    val operationId: String,
    val status: String, // "SUCCESS", "CONFLICT", "REJECTED", "IGNORED_DUPLICATE"
    val message: String? = null,
    val serverTimestamp: Long = System.currentTimeMillis()
)

data class LevelProgressDto(
    val levelId: Int,
    val worldId: Int,
    val stars: Int,
    val bestTimeMs: Long,
    val movesCount: Int,
    val isCompleted: Boolean,
    val bestHintCount: Int = 0,
    val completedAt: Long? = null
)

data class BatchSyncResponse(
    val results: List<SyncOperationResultDto>,
    val latestProgress: List<LevelProgressDto>,
    val serverTimestamp: Long = System.currentTimeMillis()
)

/**
 * Transparent user-facing synchronization status.
 *
 * Implements Prompt 35 Section 18:
 * - SAVED_LOCALLY: Changes stored durably in Room on this device.
 * - WAITING_TO_SYNC: Network unavailable or offline queue pending.
 * - SYNCING: Background or foreground sync operation active.
 * - SYNCED: All eligible operations acknowledged by authoritative server.
 * - ACTION_REQUIRED: Manual conflict or authentication action needed.
 */
enum class SyncState {
    SAVED_LOCALLY,
    WAITING_TO_SYNC,
    SYNCING,
    SYNCED,
    ACTION_REQUIRED
}

data class SyncStatus(
    val state: SyncState = SyncState.SAVED_LOCALLY,
    val pendingOperationsCount: Int = 0,
    val lastSyncTimestamp: Long? = null,
    val message: String? = null
)
