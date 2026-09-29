package com.zynpath.game.core.billing

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.zynpath.game.core.billing.model.BillingConnectionState
import com.zynpath.game.core.billing.model.PurchaseEvent
import com.zynpath.game.core.billing.model.SubscriptionOffer
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Lifecycle-aware repository coordinating Google Play Billing interactions.
 *
 * Implements Prompt 26 Sections 10, 11, 12, 13, 15, 27, 28 & 29.
 */
interface BillingRepository {

    /** Current connection state to Google Play services */
    val connectionState: StateFlow<BillingConnectionState>

    /** Retrieved verified subscription offers with localized pricing */
    val productOffers: StateFlow<List<SubscriptionOffer>>

    /** Stream of purchase events received from Google Play */
    val purchaseEvents: SharedFlow<PurchaseEvent>

    /** Establishes connection to Google Play Billing */
    suspend fun startConnection()

    /** Queries Google Play for current ProductDetails and subscription offers */
    suspend fun queryProductDetails(): Result<List<SubscriptionOffer>>

    /** Launches the official Google Play purchase flow for the selected offer */
    fun launchPurchaseFlow(activity: Activity, offer: SubscriptionOffer): Boolean

    /** Acknowledges a completed Google Play subscription purchase */
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean

    /** Queries existing active subscription purchases from Google Play */
    suspend fun queryActivePurchases(): List<Purchase>

    /** Cleans up the BillingClient connection */
    fun endConnection()
}
