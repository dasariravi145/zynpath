package com.zynpath.game.core.sync.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Durable local operation queue entity for offline-first synchronization.
 *
 * Implements Prompt 35 Sections 8-10:
 * - Persisted in Room to survive process termination and device restart.
 * - Partitioned by [ownerIdentity] to ensure strict account isolation.
 * - Uses stable [operationId] to guarantee backend idempotency.
 */
@Entity(
    tableName = "sync_operations",
    indices = [
        Index(value = ["ownerIdentity", "status"]),
        Index(value = ["createdAt"])
    ]
)
data class SyncOperationEntity(
    @PrimaryKey
    val operationId: String,
    val ownerIdentity: String,
    val operationType: String,
    val payloadVersion: Int = 1,
    val resourceIdentity: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val attemptCount: Int = 0,
    val lastAttemptAt: Long? = null,
    val lastError: String? = null,
    val status: String = STATUS_PENDING
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_SUCCEEDED = "SUCCEEDED"
        const val STATUS_RETRYABLE_FAILURE = "RETRYABLE_FAILURE"
        const val STATUS_PERMANENT_FAILURE = "PERMANENT_FAILURE"
        const val STATUS_CONFLICT_REQUIRES_ACTION = "CONFLICT_REQUIRES_ACTION"

        const val TYPE_SOLO_COMPLETION = "SOLO_LEVEL_COMPLETION"
        const val TYPE_DAILY_CHALLENGE = "DAILY_CHALLENGE_SUBMISSION"
        const val TYPE_PREFERENCE_UPDATE = "PREFERENCE_UPDATE"
    }

    val isEligibleForSync: Boolean
        get() = status == STATUS_PENDING || status == STATUS_RETRYABLE_FAILURE
}
