package com.zynpath.game.feature.premium

import com.zynpath.game.core.billing.model.SubscriptionOffer
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import com.zynpath.game.core.premium.model.EntitlementStatus
import com.zynpath.game.core.premium.model.PremiumEntitlement

/**
 * Explicit purchase and presentation states for the Zynpath Premium screen.
 *
 * Implements Prompt 26 Sections 14, 16, 21, 24 & 49.
 */
enum class PurchaseFlowState {
    IDLE,
    LOADING_PRODUCTS,
    READY,
    PURCHASE_IN_PROGRESS,
    PENDING,
    VERIFYING,
    ACTIVE,
    CANCELLED,
    ERROR,
    RESTORING
}

data class PremiumUiState(
    val purchaseFlowState: PurchaseFlowState = PurchaseFlowState.IDLE,
    val monthlyOffer: SubscriptionOffer? = null,
    val sixMonthsOffer: SubscriptionOffer? = null,
    val entitlement: PremiumEntitlement = PremiumEntitlement.free(),
    val isAccountLinked: Boolean = false,
    val accountDisplayName: String? = null,
    val selectedPlanType: SubscriptionPlanType = SubscriptionPlanType.SIX_MONTH,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
    val isBillingAvailable: Boolean = true
) {
    val isPremiumActive: Boolean
        get() = entitlement.isPremiumActive

    val entitlementLabel: String
        get() = entitlement.status.displayLabel
}
