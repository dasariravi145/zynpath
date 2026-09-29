package com.zynpath.game.core.sync.api

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.sync.model.BatchSyncRequest
import com.zynpath.game.core.sync.model.BatchSyncResponse
import com.zynpath.game.core.sync.model.LevelProgressDto

/**
 * Network API contract for offline-first progress synchronization.
 */
interface SyncApiService {
    fun getBaseUrl(): String
    suspend fun submitBatch(sessionToken: String?, request: BatchSyncRequest): NetworkResult<BatchSyncResponse>
    suspend fun fetchRemoteProgress(sessionToken: String?, playerId: String): NetworkResult<List<LevelProgressDto>>
}
