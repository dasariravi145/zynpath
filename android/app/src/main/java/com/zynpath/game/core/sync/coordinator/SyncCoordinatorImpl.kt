package com.zynpath.game.core.sync.coordinator

import android.util.Log
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.database.dao.SyncOperationDao
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.sync.api.SyncApiService
import com.zynpath.game.core.sync.connectivity.NetworkConnectivityMonitor
import com.zynpath.game.core.sync.model.BatchSyncRequest
import com.zynpath.game.core.sync.model.LevelProgressDto
import com.zynpath.game.core.sync.model.SyncOperationDto
import com.zynpath.game.core.sync.model.SyncOperationEntity
import com.zynpath.game.core.sync.model.SyncState
import com.zynpath.game.core.sync.model.SyncStatus
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of the central synchronization coordinator.
 *
 * Implements Prompt 35 Sections 7, 8, 10, 11, 12, 13, 14, 16, 17, 18, 36, 40, 56 & 60:
 * - Persistent Room queue surviving process kill and restart.
 * - Idempotent deduplication with stable client-generated operation IDs.
 * - Non-destructive conflict-resolution and PB preservation.
 * - Transparent 5-state synchronization reporting.
 * - Account-isolated operation partitioning.
 */
@Singleton
class SyncCoordinatorImpl @Inject constructor(
    private val syncOperationDao: SyncOperationDao,
    private val syncApiService: SyncApiService,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor,
    private val secureTokenStorage: SecureTokenStorage,
    private val progressRepository: Lazy<ProgressRepository>,
    private val preferencesRepository: PreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SyncCoordinator {

    private val tag = "SyncCoordinator"
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val syncMutex = Mutex()

    private val activeOwnerIdFlow = MutableStateFlow("guest")

    private val _syncStatus = MutableStateFlow(SyncStatus(state = SyncState.SAVED_LOCALLY))
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override val pendingOperationsCount: Flow<Int> = activeOwnerIdFlow.flatMapLatest { owner ->
        syncOperationDao.observePendingCount(owner)
    }

    init {
        scope.launch {
            resolveInitialOwner()
            observePendingCountChanges()
            observeConnectivity()
        }
    }

    private suspend fun resolveInitialOwner() {
        val meta = secureTokenStorage.getSessionMetadata()
        if (meta != null && meta.playerId.isNotBlank()) {
            activeOwnerIdFlow.value = meta.playerId
        } else {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            activeOwnerIdFlow.value = prefs.guestUuid.ifBlank { "guest" }
        }
    }

    private fun observePendingCountChanges() {
        scope.launch {
            pendingOperationsCount.collect { count ->
                _syncStatus.update { current ->
                    val newState = when {
                        current.state == SyncState.SYNCING -> SyncState.SYNCING
                        current.state == SyncState.ACTION_REQUIRED -> SyncState.ACTION_REQUIRED
                        count > 0 -> SyncState.WAITING_TO_SYNC
                        else -> if (current.lastSyncTimestamp != null) SyncState.SYNCED else SyncState.SAVED_LOCALLY
                    }
                    current.copy(
                        state = newState,
                        pendingOperationsCount = count
                    )
                }
            }
        }
    }

    private fun observeConnectivity() {
        scope.launch {
            networkConnectivityMonitor.isConnected.collect { isConnected ->
                if (isConnected) {
                    val count = syncOperationDao.getTotalOperationsCountForOwner(activeOwnerIdFlow.value)
                    if (count > 0) {
                        Log.i(tag, "Connectivity restored; triggering sync for owner=${activeOwnerIdFlow.value}")
                        syncPendingOperations()
                    }
                }
            }
        }
    }

    override suspend fun triggerSync(): SyncStatus {
        syncPendingOperations()
        return _syncStatus.value
    }

    override suspend fun syncPendingOperations(): Boolean = syncMutex.withLock {
        val owner = activeOwnerIdFlow.value
        val isConnected = networkConnectivityMonitor.isConnected.value

        if (!isConnected) {
            _syncStatus.update {
                it.copy(
                    state = if (it.pendingOperationsCount > 0) SyncState.WAITING_TO_SYNC else SyncState.SAVED_LOCALLY,
                    message = "Waiting for network connectivity"
                )
            }
            return false
        }

        val eligibleOps = syncOperationDao.getEligibleOperations(owner, limit = 50)
        if (eligibleOps.isEmpty()) {
            _syncStatus.update {
                it.copy(
                    state = SyncState.SYNCED,
                    pendingOperationsCount = 0,
                    message = "All progress synchronized"
                )
            }
            return true
        }

        _syncStatus.update {
            it.copy(
                state = SyncState.SYNCING,
                message = "Synchronizing ${eligibleOps.size} operations..."
            )
        }

        val now = System.currentTimeMillis()
        val operationIds = eligibleOps.map { it.operationId }
        syncOperationDao.markInProgress(operationIds, now)

        val dtoList = eligibleOps.map { op ->
            SyncOperationDto(
                operationId = op.operationId,
                operationType = op.operationType,
                resourceIdentity = op.resourceIdentity,
                payloadVersion = op.payloadVersion,
                payloadJson = op.payloadJson,
                clientTimestamp = op.createdAt
            )
        }

        val batchRequest = BatchSyncRequest(
            playerId = owner,
            operations = dtoList
        )

        val sessionToken = secureTokenStorage.getSessionToken()

        return when (val networkResult = syncApiService.submitBatch(sessionToken, batchRequest)) {
            is NetworkResult.Success -> {
                val response = networkResult.data
                handleBatchSuccess(response.results, response.latestProgress)
                // Clean up old succeeded operations to keep database compact
                try {
                    syncOperationDao.deleteOldSucceeded(now - (7 * 86_400_000L))
                } catch (_: Exception) {}

                val remainingPending = syncOperationDao.getEligibleOperations(owner, limit = 1).isNotEmpty()
                _syncStatus.update {
                    it.copy(
                        state = if (remainingPending) SyncState.WAITING_TO_SYNC else SyncState.SYNCED,
                        lastSyncTimestamp = System.currentTimeMillis(),
                        message = if (remainingPending) "Batch partially synced" else "Synchronized"
                    )
                }
                true
            }
            is NetworkResult.Error -> {
                val errorMsg = networkResult.message
                val isAuthError = networkResult.code in 401..403
                val isClientPermanent = networkResult.code in 400..499 && !isAuthError

                eligibleOps.forEach { op ->
                    syncOperationDao.markFailed(
                        operationId = op.operationId,
                        error = errorMsg,
                        retryable = !isClientPermanent && !isAuthError,
                        timestamp = now
                    )
                }

                _syncStatus.update {
                    it.copy(
                        state = if (isAuthError || isClientPermanent) SyncState.ACTION_REQUIRED else SyncState.WAITING_TO_SYNC,
                        message = errorMsg
                    )
                }
                false
            }
            is NetworkResult.Exception -> {
                val exMsg = networkResult.throwable.message ?: "Network transport error"
                eligibleOps.forEach { op ->
                    syncOperationDao.markFailed(
                        operationId = op.operationId,
                        error = exMsg,
                        retryable = true,
                        timestamp = now
                    )
                }

                _syncStatus.update {
                    it.copy(
                        state = SyncState.WAITING_TO_SYNC,
                        message = exMsg
                    )
                }
                false
            }
        }
    }

    private suspend fun handleBatchSuccess(
        results: List<com.zynpath.game.core.sync.model.SyncOperationResultDto>,
        latestProgress: List<LevelProgressDto>
    ) {
        val succeededIds = mutableListOf<String>()
        val now = System.currentTimeMillis()

        for (res in results) {
            when (res.status) {
                "SUCCESS", "IGNORED_DUPLICATE" -> {
                    succeededIds.add(res.operationId)
                }
                "CONFLICT" -> {
                    syncOperationDao.markFailed(
                        res.operationId,
                        res.message ?: "Conflict detected",
                        retryable = false,
                        timestamp = now
                    )
                }
                "REJECTED" -> {
                    syncOperationDao.markFailed(
                        res.operationId,
                        res.message ?: "Operation rejected by server",
                        retryable = false,
                        timestamp = now
                    )
                }
                else -> {
                    succeededIds.add(res.operationId)
                }
            }
        }

        if (succeededIds.isNotEmpty()) {
            syncOperationDao.markSucceeded(succeededIds)
        }

        // Non-destructive level progress merge into local Room database
        if (latestProgress.isNotEmpty()) {
            val entities = latestProgress.map { p ->
                val world = WorldConfiguration.getWorldForLevel(p.levelId)
                LevelProgressEntity(
                    levelId = p.levelId,
                    worldId = if (p.worldId > 0) p.worldId else world.worldId,
                    stars = p.stars,
                    bestTimeMs = p.bestTimeMs,
                    movesCount = p.movesCount,
                    isCompleted = p.isCompleted,
                    isUnlocked = p.isCompleted,
                    bestHintCount = p.bestHintCount,
                    completionCount = if (p.isCompleted) 1 else 0,
                    firstCompletedAt = p.completedAt,
                    lastCompletedAt = p.completedAt,
                    completedAt = p.completedAt ?: 0L
                )
            }
            progressRepository.get().reconcileWithRemote(entities)
        }
    }

    override suspend fun enqueueSoloCompletion(result: ValidatedCompletionResult) {
        val owner = activeOwnerIdFlow.value
        val payload = JSONObject().apply {
            put("levelId", result.levelId)
            put("worldId", result.worldId)
            put("stars", com.zynpath.game.core.puzzle.model.StarRatingPolicy.calculateStars(result.hintCount))
            put("bestTimeMs", result.elapsedTimeMs)
            put("movesCount", result.moveCount)
            put("isCompleted", true)
            put("bestHintCount", result.hintCount)
            put("completedAt", result.completedAt)
        }

        val op = SyncOperationEntity(
            operationId = UUID.randomUUID().toString(),
            ownerIdentity = owner,
            operationType = SyncOperationEntity.TYPE_SOLO_COMPLETION,
            payloadVersion = 1,
            resourceIdentity = "solo_level_${result.levelId}",
            payloadJson = payload.toString(),
            createdAt = System.currentTimeMillis(),
            status = SyncOperationEntity.STATUS_PENDING
        )

        syncOperationDao.insertOperation(op)

        if (networkConnectivityMonitor.isConnected.value) {
            scope.launch {
                syncPendingOperations()
            }
        }
    }

    override suspend fun enqueueDailyChallenge(
        dateKey: String,
        challengeId: String,
        fingerprint: String,
        solveTimeMs: Long,
        movesCount: Int,
        completedAt: Long,
        pathCoordinates: List<String>?
    ) {
        val owner = activeOwnerIdFlow.value
        val payload = JSONObject().apply {
            put("dateKey", dateKey)
            put("challengeId", challengeId)
            put("fingerprint", fingerprint)
            put("solveTimeMs", solveTimeMs)
            put("movesCount", movesCount)
            put("completedAt", completedAt)
            if (pathCoordinates != null) {
                val coords = JSONArray()
                pathCoordinates.forEach { coords.put(it) }
                put("pathCoordinates", coords)
            }
        }

        val op = SyncOperationEntity(
            operationId = UUID.randomUUID().toString(),
            ownerIdentity = owner,
            operationType = SyncOperationEntity.TYPE_DAILY_CHALLENGE,
            payloadVersion = 1,
            resourceIdentity = "daily_$dateKey",
            payloadJson = payload.toString(),
            createdAt = System.currentTimeMillis(),
            status = SyncOperationEntity.STATUS_PENDING
        )

        syncOperationDao.insertOperation(op)

        if (networkConnectivityMonitor.isConnected.value) {
            scope.launch {
                syncPendingOperations()
            }
        }
    }

    override suspend fun onAccountSwitched(newPlayerId: String) {
        syncMutex.withLock {
            activeOwnerIdFlow.value = newPlayerId
            _syncStatus.update {
                it.copy(
                    state = SyncState.SAVED_LOCALLY,
                    message = "Account switched to $newPlayerId"
                )
            }
        }
        if (networkConnectivityMonitor.isConnected.value) {
            scope.launch {
                syncPendingOperations()
            }
        }
    }

    override suspend fun onSignOut() {
        syncMutex.withLock {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            val guestId = prefs.guestUuid.ifBlank { "guest" }
            activeOwnerIdFlow.value = guestId
            _syncStatus.update {
                it.copy(
                    state = SyncState.SAVED_LOCALLY,
                    message = "Signed out; local guest storage active"
                )
            }
        }
    }

    override suspend fun onAccountDeleted(deletedPlayerId: String) {
        syncMutex.withLock {
            // Prompt 35 Section 43: Stop sync and purge queue for deleted account to prevent recreation
            syncOperationDao.deleteOperationsForOwner(deletedPlayerId)
            val prefs = preferencesRepository.userPreferencesFlow.first()
            val guestId = prefs.guestUuid.ifBlank { "guest" }
            activeOwnerIdFlow.value = guestId
            _syncStatus.update {
                it.copy(
                    state = SyncState.SAVED_LOCALLY,
                    pendingOperationsCount = 0,
                    message = "Account deleted; sync queue purged"
                )
            }
        }
    }

    override suspend fun onGuestLinked(guestPlayerId: String, newPlayerId: String) {
        syncMutex.withLock {
            // Rebind all pending guest operations to newly linked authenticated account
            val reboundCount = syncOperationDao.rebindOperationsToNewOwner(
                oldOwner = guestPlayerId,
                newOwner = newPlayerId
            )
            Log.i(tag, "Guest linking re-bound $reboundCount pending sync operations to new account $newPlayerId")
            activeOwnerIdFlow.value = newPlayerId
        }
        if (networkConnectivityMonitor.isConnected.value) {
            scope.launch {
                syncPendingOperations()
            }
        }
    }
}
