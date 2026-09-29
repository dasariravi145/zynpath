package com.zynpath.game.core.puzzle.premium.network

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.puzzle.premium.model.PremiumPackDefinition
import com.zynpath.game.core.puzzle.premium.model.PremiumPackManifest

/**
 * Network service interface for Premium puzzle pack catalog and secure content delivery.
 *
 * Implements Prompt 27 Section 20, 22, 45, 46:
 * - Entitlement-aware content download.
 * - Catalog query and manifest delivery.
 */
interface PremiumPackApiService {
    fun getBaseUrl(): String
    suspend fun getPackCatalog(sessionToken: String?): NetworkResult<List<PremiumPackManifest>>
    suspend fun getPackManifest(sessionToken: String?, packId: String): NetworkResult<PremiumPackManifest>
    suspend fun downloadPack(sessionToken: String, packId: String): NetworkResult<PremiumPackDefinition>
}
