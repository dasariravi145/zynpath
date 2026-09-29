package com.zynpath.game.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.zynpath.game.core.billing.model.BillingConnectionState
import com.zynpath.game.core.billing.model.PurchaseEvent
import com.zynpath.game.core.billing.model.SubscriptionOffer
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Production implementation of BillingRepository interfacing directly with Google Play Billing.
 *
 * Implements Prompt 26 Sections 9, 10, 11, 12, 13, 15, 27 & 28:
 * - Never collects raw credit card details.
 * - Queries actual localized prices and periods from Google Play.
 * - Distinguishes completed vs pending purchases.
 */
@Singleton
class BillingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BillingRepository, PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow(BillingConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<BillingConnectionState> = _connectionState.asStateFlow()

    private val _productOffers = MutableStateFlow<List<SubscriptionOffer>>(emptyList())
    override val productOffers: StateFlow<List<SubscriptionOffer>> = _productOffers.asStateFlow()

    private val _purchaseEvents = MutableSharedFlow<PurchaseEvent>(extraBufferCapacity = 10)
    override val purchaseEvents: SharedFlow<PurchaseEvent> = _purchaseEvents.asSharedFlow()

    private var cachedProductDetails: ProductDetails? = null

    private val billingClient: BillingClient by lazy {
        val pendingParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(pendingParams)
            .build()
    }

    override suspend fun startConnection() {
        if (_connectionState.value == BillingConnectionState.CONNECTED ||
            _connectionState.value == BillingConnectionState.CONNECTING
        ) {
            return
        }

        _connectionState.value = BillingConnectionState.CONNECTING

        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _connectionState.value = BillingConnectionState.CONNECTED
                        scope.launch { queryProductDetails() }
                        if (continuation.isActive) continuation.resume(Unit)
                    } else {
                        _connectionState.value = BillingConnectionState.UNAVAILABLE
                        if (continuation.isActive) continuation.resume(Unit)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    _connectionState.value = BillingConnectionState.DISCONNECTED
                }
            })
        }
    }

    override suspend fun queryProductDetails(): Result<List<SubscriptionOffer>> {
        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            startConnection()
        }

        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Billing service unavailable"))
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionConstants.PRODUCT_ID_PREMIUM)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isNullOrEmpty()) {
                    val details = productDetailsList.firstOrNull {
                        it.productId == SubscriptionConstants.PRODUCT_ID_PREMIUM
                    }

                    cachedProductDetails = details

                    val offerDetails = details?.subscriptionOfferDetails
                    if (details != null && offerDetails != null) {
                        val offers = offerDetails.mapNotNull { offerDetail ->
                            parseSubscriptionOffer(details.productId, offerDetail)
                        }
                        _productOffers.value = offers
                        if (continuation.isActive) continuation.resume(Result.success(offers))
                    } else {
                        _productOffers.value = emptyList()
                        if (continuation.isActive) continuation.resume(Result.success(emptyList()))
                    }
                } else {
                    _productOffers.value = emptyList()
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(Exception("Failed to query product details: ${billingResult.debugMessage}")))
                    }
                }
            }
        }
    }

    private fun parseSubscriptionOffer(
        productId: String,
        offerDetail: ProductDetails.SubscriptionOfferDetails
    ): SubscriptionOffer? {
        val basePlanId = offerDetail.basePlanId
        val planType = when (basePlanId) {
            SubscriptionConstants.BASE_PLAN_SIX_MONTH -> SubscriptionPlanType.SIX_MONTH
            SubscriptionConstants.BASE_PLAN_MONTHLY -> SubscriptionPlanType.MONTHLY
            else -> return null
        }

        val phase = offerDetail.pricingPhases.pricingPhaseList.firstOrNull() ?: return null

        return SubscriptionOffer(
            productId = productId,
            basePlanId = basePlanId,
            planType = planType,
            offerToken = offerDetail.offerToken,
            formattedPrice = phase.formattedPrice,
            billingPeriod = phase.billingPeriod,
            currencyCode = phase.priceCurrencyCode,
            priceMicros = phase.priceAmountMicros,
            isAvailable = true
        )
    }

    override fun launchPurchaseFlow(activity: Activity, offer: SubscriptionOffer): Boolean {
        val details = cachedProductDetails ?: return false

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offer.offerToken)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, flowParams)
        return billingResult.responseCode == BillingClient.BillingResponseCode.OK
    }

    override suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            return false
        }

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.acknowledgePurchase(params) { billingResult ->
                val success = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                if (continuation.isActive) continuation.resume(success)
            }
        }
    }

    override suspend fun queryActivePurchases(): List<Purchase> {
        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            startConnection()
        }

        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            return emptyList()
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (continuation.isActive) continuation.resume(purchasesList)
                } else {
                    if (continuation.isActive) continuation.resume(emptyList())
                }
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        scope.launch {
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.forEach { purchase ->
                        when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> {
                                val productId = purchase.products.firstOrNull() ?: SubscriptionConstants.PRODUCT_ID_PREMIUM
                                _purchaseEvents.emit(
                                    PurchaseEvent.Success(
                                        purchaseToken = purchase.purchaseToken,
                                        orderId = purchase.orderId,
                                        productId = productId,
                                        basePlanId = null
                                    )
                                )
                            }
                            Purchase.PurchaseState.PENDING -> {
                                _purchaseEvents.emit(
                                    PurchaseEvent.Pending(
                                        purchaseToken = purchase.purchaseToken,
                                        orderId = purchase.orderId
                                    )
                                )
                            }
                        }
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    _purchaseEvents.emit(PurchaseEvent.Cancelled)
                }
                else -> {
                    _purchaseEvents.emit(
                        PurchaseEvent.Error(
                            message = billingResult.debugMessage.ifBlank { "Billing error: ${billingResult.responseCode}" },
                            responseCode = billingResult.responseCode
                        )
                    )
                }
            }
        }
    }

    override fun endConnection() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
        _connectionState.value = BillingConnectionState.DISCONNECTED
    }
}
