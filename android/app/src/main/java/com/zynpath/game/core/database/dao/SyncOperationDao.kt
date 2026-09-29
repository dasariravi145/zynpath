package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zynpath.game.core.sync.model.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for durable offline sync operations.
 */
@Dao
interface SyncOperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: SyncOperationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<SyncOperationEntity>)

    @Update
    suspend fun updateOperation(operation: SyncOperationEntity)

    @Query("SELECT * FROM sync_operations WHERE operationId = :operationId LIMIT 1")
    suspend fun getOperationById(operationId: String): SyncOperationEntity?

    @Query(
        """
        SELECT * FROM sync_operations 
        WHERE ownerIdentity = :ownerIdentity 
          AND resourceIdentity = :resourceIdentity 
          AND status IN ('PENDING', 'RETRYABLE_FAILURE') 
        LIMIT 1
        """
    )
    suspend fun getPendingOperationForResource(ownerIdentity: String, resourceIdentity: String): SyncOperationEntity?

    @Query(
        """
        SELECT * FROM sync_operations 
        WHERE ownerIdentity = :ownerIdentity 
          AND status IN ('PENDING', 'RETRYABLE_FAILURE')
        ORDER BY createdAt ASC 
        LIMIT :limit
        """
    )
    suspend fun getEligibleOperations(ownerIdentity: String, limit: Int = 50): List<SyncOperationEntity>

    @Query(
        """
        SELECT COUNT(*) FROM sync_operations 
        WHERE ownerIdentity = :ownerIdentity 
          AND status IN ('PENDING', 'RETRYABLE_FAILURE')
        """
    )
    fun observePendingCount(ownerIdentity: String): Flow<Int>

    @Query(
        """
        UPDATE sync_operations 
        SET status = 'IN_PROGRESS', 
            lastAttemptAt = :timestamp, 
            attemptCount = attemptCount + 1 
        WHERE operationId IN (:operationIds)
        """
    )
    suspend fun markInProgress(operationIds: List<String>, timestamp: Long)

    @Query(
        """
        UPDATE sync_operations 
        SET status = 'SUCCEEDED', 
            lastError = NULL 
        WHERE operationId IN (:operationIds)
        """
    )
    suspend fun markSucceeded(operationIds: List<String>)

    @Query(
        """
        UPDATE sync_operations 
        SET status = CASE WHEN :retryable = 1 THEN 'RETRYABLE_FAILURE' ELSE 'PERMANENT_FAILURE' END, 
            lastError = :error,
            lastAttemptAt = :timestamp
        WHERE operationId = :operationId
        """
    )
    suspend fun markFailed(operationId: String, error: String, retryable: Boolean, timestamp: Long)

    @Query("DELETE FROM sync_operations WHERE status = 'SUCCEEDED' AND createdAt < :olderThanTimestamp")
    suspend fun deleteOldSucceeded(olderThanTimestamp: Long): Int

    @Query("DELETE FROM sync_operations WHERE ownerIdentity = :ownerIdentity")
    suspend fun deleteOperationsForOwner(ownerIdentity: String)

    @Query("SELECT COUNT(*) FROM sync_operations WHERE ownerIdentity = :ownerIdentity")
    suspend fun getTotalOperationsCountForOwner(ownerIdentity: String): Int

    @Query(
        """
        UPDATE sync_operations 
        SET ownerIdentity = :newOwner 
        WHERE ownerIdentity = :oldOwner AND status != 'SUCCEEDED'
        """
    )
    suspend fun rebindOperationsToNewOwner(oldOwner: String, newOwner: String): Int
}
