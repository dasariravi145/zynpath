package com.zynpath.game.fake

import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.EntitlementStatus
import com.zynpath.game.core.premium.model.PremiumEntitlement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSubscriptionEntitlementRepository(
    initialEntitlement: PremiumEntitlement = PremiumEntitlement(status = EntitlementStatus.FREE)
) : SubscriptionEntitlementRepository {

    private val _entitlement = MutableStateFlow(initialEntitlement)
    override val entitlement: StateFlow<PremiumEntitlement> = _entitlement.asStateFlow()

    fun setEntitlement(entitlement: PremiumEntitlement) {
        _entitlement.value = entitlement
    }

    override suspend fun refreshEntitlement(): Result<PremiumEntitlement> {
        return Result.success(_entitlement.value)
    }

    override suspend fun verifyPurchase(
        purchaseToken: String,
        productId: String,
        basePlanId: String
    ): Result<PremiumEntitlement> {
        val updated = PremiumEntitlement(
            status = EntitlementStatus.ACTIVE,
            productId = productId,
            basePlanId = basePlanId,
            currentPeriodEndMs = System.currentTimeMillis() + 30L * 86400000L,
            lastVerifiedAtMs = System.currentTimeMillis()
        )
        _entitlement.value = updated
        return Result.success(updated)
    }

    override suspend fun restorePurchases(purchaseTokens: List<String>): Result<PremiumEntitlement> {
        return if (purchaseTokens.isNotEmpty()) {
            val updated = PremiumEntitlement(
                status = EntitlementStatus.ACTIVE,
                productId = "zynpath_premium",
                basePlanId = "monthly",
                currentPeriodEndMs = System.currentTimeMillis() + 30L * 86400000L
            )
            _entitlement.value = updated
            Result.success(updated)
        } else {
            Result.success(_entitlement.value)
        }
    }

    override suspend fun onAccountSwitched(newAccountId: String?) {
        _entitlement.value = _entitlement.value.copy(accountId = newAccountId)
    }

    override suspend fun clearEntitlement() {
        _entitlement.value = PremiumEntitlement(status = EntitlementStatus.FREE)
    }
}
