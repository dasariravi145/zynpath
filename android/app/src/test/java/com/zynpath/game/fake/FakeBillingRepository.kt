package com.zynpath.game.fake

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.zynpath.game.core.billing.BillingRepository
import com.zynpath.game.core.billing.SubscriptionConstants
import com.zynpath.game.core.billing.model.BillingConnectionState
import com.zynpath.game.core.billing.model.PurchaseEvent
import com.zynpath.game.core.billing.model.SubscriptionOffer
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBillingRepository(
    initialOffers: List<SubscriptionOffer> = defaultOffers()
) : BillingRepository {

    private val _connectionState = MutableStateFlow(BillingConnectionState.CONNECTED)
    override val connectionState: StateFlow<BillingConnectionState> = _connectionState.asStateFlow()

    private val _productOffers = MutableStateFlow(initialOffers)
    override val productOffers: StateFlow<List<SubscriptionOffer>> = _productOffers.asStateFlow()

    private val _purchaseEvents = MutableSharedFlow<PurchaseEvent>()
    override val purchaseEvents: SharedFlow<PurchaseEvent> = _purchaseEvents.asSharedFlow()

    fun setConnectionState(state: BillingConnectionState) {
        _connectionState.value = state
    }

    fun setOffers(offers: List<SubscriptionOffer>) {
        _productOffers.value = offers
    }

    suspend fun emitPurchaseEvent(event: PurchaseEvent) {
        _purchaseEvents.emit(event)
    }

    override suspend fun startConnection() {
        _connectionState.value = BillingConnectionState.CONNECTED
    }

    override suspend fun queryProductDetails(): Result<List<SubscriptionOffer>> {
        return Result.success(_productOffers.value)
    }

    override fun launchPurchaseFlow(activity: Activity, offer: SubscriptionOffer): Boolean = true

    override suspend fun acknowledgePurchase(purchaseToken: String): Boolean = true

    override suspend fun queryActivePurchases(): List<Purchase> = emptyList()

    override fun endConnection() {
        _connectionState.value = BillingConnectionState.DISCONNECTED
    }

    companion object {
        fun defaultOffers(): List<SubscriptionOffer> = listOf(
            SubscriptionOffer(
                productId = SubscriptionConstants.PRODUCT_ID_PREMIUM,
                basePlanId = SubscriptionConstants.BASE_PLAN_MONTHLY,
                planType = SubscriptionPlanType.MONTHLY,
                offerToken = "token_monthly",
                formattedPrice = "$2.99 / month",
                billingPeriod = "P1M",
                currencyCode = "USD",
                priceMicros = 2990000L
            ),
            SubscriptionOffer(
                productId = SubscriptionConstants.PRODUCT_ID_PREMIUM,
                basePlanId = SubscriptionConstants.BASE_PLAN_SIX_MONTH,
                planType = SubscriptionPlanType.SIX_MONTH,
                offerToken = "token_six_month",
                formattedPrice = "$14.99 / 6 months",
                billingPeriod = "P6M",
                currencyCode = "USD",
                priceMicros = 14990000L
            )
        )
    }
}
