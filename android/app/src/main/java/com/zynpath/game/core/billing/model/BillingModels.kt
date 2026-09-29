package com.zynpath.game.core.billing.model

/**
 * Domain models for Google Play Billing connection, subscription offers, and purchase states.
 *
 * Implements Prompt 26 Sections 10, 11, 12, 13, 16 & 28.
 */

enum class BillingConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    UNAVAILABLE
}

enum class SubscriptionPlanType {
    MONTHLY,
    SIX_MONTH
}

data class SubscriptionOffer(
    val productId: String,
    val basePlanId: String,
    val planType: SubscriptionPlanType,
    val offerToken: String,
    val formattedPrice: String,
    val billingPeriod: String,
    val currencyCode: String,
    val priceMicros: Long,
    val isAvailable: Boolean = true
)

sealed interface PurchaseEvent {
    data class Success(
        val purchaseToken: String,
        val orderId: String?,
        val productId: String,
        val basePlanId: String?
    ) : PurchaseEvent

    data class Pending(
        val purchaseToken: String,
        val orderId: String?
    ) : PurchaseEvent

    data object Cancelled : PurchaseEvent

    data class Error(
        val message: String,
        val responseCode: Int
    ) : PurchaseEvent
}
