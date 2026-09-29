package com.zynpath.game.core.sync.coordinator

import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.sync.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Unified synchronization coordinator contract.
 *
 * Implements Prompt 35 Sections 7, 8, 14, 16, 17, 18, 40 & 41:
 * - Detects eligible pending operations in Room.
 * - Coordinates connectivity-aware background & foreground synchronization.
 * - Applies bounded backoff retries and non-destructive reconciliation.
 * - Prevents duplicate concurrent operations.
 * - Provides transparent synchronization status and account isolation.
 */
interface SyncCoordinator {

    val syncStatus: StateFlow<SyncStatus>

    val pendingOperationsCount: Flow<Int>

    suspend fun triggerSync(): SyncStatus

    suspend fun syncPendingOperations(): Boolean

    suspend fun enqueueSoloCompletion(result: ValidatedCompletionResult)

    suspend fun enqueueDailyChallenge(
        dateKey: String,
        challengeId: String,
        fingerprint: String,
        solveTimeMs: Long,
        movesCount: Int,
        completedAt: Long,
        pathCoordinates: List<String>?
    )

    suspend fun onAccountSwitched(newPlayerId: String)

    suspend fun onSignOut()

    suspend fun onAccountDeleted(deletedPlayerId: String)

    suspend fun onGuestLinked(guestPlayerId: String, newPlayerId: String)
}
