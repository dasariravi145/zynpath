package com.zynpath.game.core.cosmetics.network

import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.network.NetworkResult

/**
 * Network service contract for cosmetic catalog and account synchronization.
 *
 * Implements Prompt 28 Sections 31, 32 & 51.
 */
interface CosmeticsApiService {
    fun getBaseUrl(): String
    suspend fun getCatalog(): NetworkResult<List<CosmeticItem>>
    suspend fun getEquippedCosmetics(sessionToken: String): NetworkResult<EquippedCosmetics>
    suspend fun updateEquippedCosmetics(sessionToken: String, equipped: EquippedCosmetics): NetworkResult<EquippedCosmetics>
    suspend fun getPublicCosmetics(playerId: String): NetworkResult<EquippedCosmetics>
}
