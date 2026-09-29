package com.zynpath.game.core.network.api

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.network.model.BackendHealthDto

/**
 * API service contract for backend liveness and health diagnostics.
 */
interface HealthApiService {
    suspend fun checkHealth(): NetworkResult<BackendHealthDto>
    suspend fun checkHealth(targetUrl: String?): NetworkResult<BackendHealthDto> = checkHealth()
    fun getBaseUrl(): String
    fun setCustomBaseUrl(url: String?) {}
}
