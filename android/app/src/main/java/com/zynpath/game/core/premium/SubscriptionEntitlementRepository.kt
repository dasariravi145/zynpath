package com.zynpath.game.core.premium

import com.zynpath.game.core.premium.model.PremiumEntitlement
import kotlinx.coroutines.flow.StateFlow

/**
 * Authoritative boundary for subscription entitlement management.
 *
 * Implements Prompt 26 Sections 23, 25, 36, 37, 38 & 39.
 * - Single source of truth for Premium entitlement state.
 * - Handles offline caching with bounded validity.
 * - Isolates entitlement per account upon account switching.
 * - Synchronizes with PreferencesRepository for app-wide feature access.
 */
interface SubscriptionEntitlementRepository {
    val entitlement: StateFlow<PremiumEntitlement>

    suspend fun refreshEntitlement(): Result<PremiumEntitlement>

    suspend fun verifyPurchase(
        purchaseToken: String,
        productId: String,
        basePlanId: String
    ): Result<PremiumEntitlement>

    suspend fun restorePurchases(purchaseTokens: List<String>): Result<PremiumEntitlement>

    suspend fun onAccountSwitched(newAccountId: String?)

    suspend fun clearEntitlement()
}
