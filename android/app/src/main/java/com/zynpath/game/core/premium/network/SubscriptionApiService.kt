package com.zynpath.game.core.premium.network

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.premium.model.PremiumEntitlement

data class SubscriptionVerificationRequest(
    val purchaseToken: String,
    val productId: String,
    val basePlanId: String,
    val obfuscatedAccountId: String? = null
)

data class SubscriptionRestoreRequest(
    val purchaseTokens: List<String>
)

data class SubscriptionRestoreResponse(
    val activeEntitlement: PremiumEntitlement?,
    val restoredCount: Int,
    val message: String
)

/**
 * Network boundary for server-authoritative Google Play subscription verification and entitlement management.
 *
 * Implements Prompt 26 Sections 18, 22, 26, 29 & 30.
 */
interface SubscriptionApiService {
    fun getBaseUrl(): String
    suspend fun getEntitlement(sessionToken: String): NetworkResult<PremiumEntitlement>
    suspend fun verifyPurchase(sessionToken: String, request: SubscriptionVerificationRequest): NetworkResult<PremiumEntitlement>
    suspend fun refreshEntitlement(sessionToken: String): NetworkResult<PremiumEntitlement>
    suspend fun restoreSubscriptions(sessionToken: String, request: SubscriptionRestoreRequest): NetworkResult<SubscriptionRestoreResponse>
}
