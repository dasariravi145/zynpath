package com.zynpath.game.feature.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.billing.BillingRepository
import com.zynpath.game.core.billing.SubscriptionConstants
import com.zynpath.game.core.billing.model.BillingConnectionState
import com.zynpath.game.core.billing.model.PurchaseEvent
import com.zynpath.game.core.billing.model.SubscriptionOffer
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()

    init {
        // Connect to Google Play Billing
        viewModelScope.launch {
            billingRepository.startConnection()
        }

        // Combine offers and entitlement
        combine(
            billingRepository.productOffers,
            entitlementRepository.entitlement,
            authRepository.currentSession
        ) { offers: List<SubscriptionOffer>, entitlement, session ->
            val monthly = offers.find { it.planType == SubscriptionPlanType.MONTHLY }
            val sixMonths = offers.find { it.planType == SubscriptionPlanType.SIX_MONTH }
            val isLinked = session != null && session.accountType != "GUEST"

            _uiState.update { current ->
                current.copy(
                    monthlyOffer = monthly,
                    sixMonthsOffer = sixMonths,
                    entitlement = entitlement,
                    isAccountLinked = isLinked,
                    accountDisplayName = session?.displayName,
                    purchaseFlowState = if (entitlement.isPremiumActive) {
                        PurchaseFlowState.ACTIVE
                    } else if (current.purchaseFlowState == PurchaseFlowState.LOADING_PRODUCTS && offers.isNotEmpty()) {
                        PurchaseFlowState.READY
                    } else {
                        current.purchaseFlowState
                    }
                )
            }
        }.launchIn(viewModelScope)

        // Observe connection state
        billingRepository.connectionState.onEach { connState ->
            when (connState) {
                BillingConnectionState.CONNECTING -> {
                    _uiState.update { it.copy(purchaseFlowState = PurchaseFlowState.LOADING_PRODUCTS) }
                }
                BillingConnectionState.CONNECTED -> {
                    _uiState.update {
                        it.copy(
                            isBillingAvailable = true,
                            purchaseFlowState = if (it.isPremiumActive) PurchaseFlowState.ACTIVE else PurchaseFlowState.READY
                        )
                    }
                }
                BillingConnectionState.UNAVAILABLE -> {
                    _uiState.update {
                        it.copy(
                            isBillingAvailable = false,
                            errorMessage = "Google Play Billing is currently unavailable on this device."
                        )
                    }
                }
                BillingConnectionState.DISCONNECTED -> {
                    // Handled gracefully, auto-reconnected on next action
                }
            }
        }.launchIn(viewModelScope)

        // Observe purchase events
        billingRepository.purchaseEvents.onEach { event ->
            handlePurchaseEvent(event)
        }.launchIn(viewModelScope)

        // Refresh authoritative entitlement from backend
        viewModelScope.launch {
            entitlementRepository.refreshEntitlement()
        }
    }

    fun selectPlan(planType: SubscriptionPlanType) {
        _uiState.update { it.copy(selectedPlanType = planType, errorMessage = null) }
    }

    fun buySelectedPlan(activity: Activity) {
        val selectedPlan = _uiState.value.selectedPlanType
        buyPlan(activity, selectedPlan)
    }

    fun buyPlan(activity: Activity, planType: SubscriptionPlanType) {
        val state = _uiState.value

        // Prompt 26 Section 21: Guest purchase policy - require authenticated account
        if (!state.isAccountLinked) {
            _uiState.update {
                it.copy(
                    errorMessage = "An authenticated Google or Facebook account is required to subscribe so your entitlement is permanently secured."
                )
            }
            return
        }

        val offer = when (planType) {
            SubscriptionPlanType.MONTHLY -> state.monthlyOffer
            SubscriptionPlanType.SIX_MONTH -> state.sixMonthsOffer
        }

        if (offer == null || !offer.isAvailable || offer.offerToken.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "The selected subscription offer is currently unavailable on Google Play."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                purchaseFlowState = PurchaseFlowState.PURCHASE_IN_PROGRESS,
                errorMessage = null,
                statusMessage = null
            )
        }

        val success = billingRepository.launchPurchaseFlow(activity, offer)
        if (!success) {
            _uiState.update {
                it.copy(
                    purchaseFlowState = PurchaseFlowState.ERROR,
                    errorMessage = "Failed to launch Google Play purchase flow"
                )
            }
        }
    }

    fun restorePurchases() {
        val session = authRepository.currentSession.value
        val isLinked = session != null && session.accountType != "GUEST"
        if (!isLinked) {
            _uiState.update {
                it.copy(
                    errorMessage = "Please sign in to your Zynpath account first to restore your subscriptions."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                purchaseFlowState = PurchaseFlowState.RESTORING,
                errorMessage = null,
                statusMessage = "Querying Google Play purchases..."
            )
        }

        viewModelScope.launch {
            val purchases = billingRepository.queryActivePurchases()
            if (purchases.isEmpty()) {
                _uiState.update {
                    it.copy(
                        purchaseFlowState = if (it.isPremiumActive) PurchaseFlowState.ACTIVE else PurchaseFlowState.READY,
                        statusMessage = "No active subscription purchases found on this Google account."
                    )
                }
                return@launch
            }

            val tokens = purchases.map { it.purchaseToken }
            val restoreResult = entitlementRepository.restorePurchases(tokens)
            restoreResult.onSuccess { freshEntitlement ->
                _uiState.update {
                    it.copy(
                        purchaseFlowState = if (freshEntitlement.isPremiumActive) PurchaseFlowState.ACTIVE else PurchaseFlowState.READY,
                        statusMessage = if (freshEntitlement.isPremiumActive) "Subscription successfully restored!" else "Restored purchases have expired or are unentitled."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        purchaseFlowState = PurchaseFlowState.ERROR,
                        errorMessage = error.message ?: "Failed to restore purchases."
                    )
                }
            }
        }
    }

    fun refreshEntitlement() {
        viewModelScope.launch {
            entitlementRepository.refreshEntitlement()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, statusMessage = null) }
    }

    private fun handlePurchaseEvent(event: PurchaseEvent) {
        when (event) {
            is PurchaseEvent.Success -> {
                _uiState.update {
                    it.copy(
                        purchaseFlowState = PurchaseFlowState.VERIFYING,
                        statusMessage = "Verifying subscription with Zynpath server..."
                    )
                }

                viewModelScope.launch {
                    val productId = event.productId.ifEmpty { SubscriptionConstants.PRODUCT_ID_PREMIUM }
                    val basePlanId = event.basePlanId ?: when (_uiState.value.selectedPlanType) {
                        SubscriptionPlanType.MONTHLY -> SubscriptionConstants.BASE_PLAN_MONTHLY
                        SubscriptionPlanType.SIX_MONTH -> SubscriptionConstants.BASE_PLAN_SIX_MONTH
                    }

                    val verifyResult = entitlementRepository.verifyPurchase(
                        purchaseToken = event.purchaseToken,
                        productId = productId,
                        basePlanId = basePlanId
                    )

                    verifyResult.onSuccess { verifiedEntitlement ->
                        // Acknowledge with Google Play
                        billingRepository.acknowledgePurchase(event.purchaseToken)
                        _uiState.update {
                            it.copy(
                                purchaseFlowState = if (verifiedEntitlement.isPremiumActive) PurchaseFlowState.ACTIVE else PurchaseFlowState.READY,
                                statusMessage = "Premium successfully activated!"
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                purchaseFlowState = PurchaseFlowState.ERROR,
                                errorMessage = error.message ?: "Verification failed."
                            )
                        }
                    }
                }
            }
            is PurchaseEvent.Pending -> {
                // Section 28: Never treat PENDING as ACTIVE
                _uiState.update {
                    it.copy(
                        purchaseFlowState = PurchaseFlowState.PENDING,
                        statusMessage = "Purchase pending Google Play payment completion. Premium will activate once payment is confirmed."
                    )
                }
            }
            is PurchaseEvent.Cancelled -> {
                _uiState.update {
                    it.copy(
                        purchaseFlowState = PurchaseFlowState.CANCELLED,
                        statusMessage = "Purchase cancelled."
                    )
                }
            }
            is PurchaseEvent.Error -> {
                _uiState.update {
                    it.copy(
                        purchaseFlowState = PurchaseFlowState.ERROR,
                        errorMessage = "Google Play Billing: ${event.message} (Code: ${event.responseCode})"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.endConnection()
    }
}
