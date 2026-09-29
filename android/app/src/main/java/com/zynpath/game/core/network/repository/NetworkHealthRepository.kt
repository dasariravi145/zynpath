package com.zynpath.game.core.network.repository

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.network.api.HealthApiService
import com.zynpath.game.core.network.model.BackendHealthDto
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkHealthRepository {
    suspend fun checkHealth(): NetworkResult<BackendHealthDto>
    suspend fun checkHealth(targetUrl: String?): NetworkResult<BackendHealthDto> = checkHealth()
    fun getBaseUrl(): String
    fun setCustomBaseUrl(url: String?) {}
}

@Singleton
class NetworkHealthRepositoryImpl @Inject constructor(
    private val healthApiService: HealthApiService
) : NetworkHealthRepository {

    override suspend fun checkHealth(): NetworkResult<BackendHealthDto> {
        return healthApiService.checkHealth()
    }

    override suspend fun checkHealth(targetUrl: String?): NetworkResult<BackendHealthDto> {
        return healthApiService.checkHealth(targetUrl)
    }

    override fun getBaseUrl(): String {
        return healthApiService.getBaseUrl()
    }

    override fun setCustomBaseUrl(url: String?) {
        healthApiService.setCustomBaseUrl(url)
    }
}

