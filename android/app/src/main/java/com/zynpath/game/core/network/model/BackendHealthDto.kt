package com.zynpath.game.core.network.model

/**
 * Data transfer object representing the response from GET /api/v1/health.
 */
data class BackendHealthDto(
    val status: String,
    val service: String,
    val version: String,
    val timestamp: Long,
    val environment: String
)
