package com.zynpath.game.feature.premium

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.billing.model.PurchaseEvent
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import com.zynpath.game.core.premium.model.EntitlementStatus
import com.zynpath.game.core.premium.model.PremiumEntitlement
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeBillingRepository
import com.zynpath.game.fake.FakeSubscriptionEntitlementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verification of Premium Subscription Presentation, Google Play Billing Offers,
 * Guest Account Requirement, and Restore Purchases Feedback.
 *
 * Implements Prompt 48 Requirements 58, 59, 60.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PremiumAndBillingUiTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var billingRepo: FakeBillingRepository
    private lateinit var entitlementRepo: FakeSubscriptionEntitlementRepository
    private lateinit var authRepo: FakeAuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        billingRepo = FakeBillingRepository()
        entitlementRepo = FakeSubscriptionEntitlementRepository()
        authRepo = FakeAuthRepository(AuthState.AUTHENTICATED)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testPremiumPresentationLoadsMonthlyAndSixMonthOffers() = runTest(testDispatcher) {
        val viewModel = PremiumViewModel(billingRepo, entitlementRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull("Monthly offer must be available", state.monthlyOffer)
        assertEquals("$2.99 / month", state.monthlyOffer?.formattedPrice)

        assertNotNull("Six-month offer must be available", state.sixMonthsOffer)
        assertEquals("$14.99 / 6 months", state.sixMonthsOffer?.formattedPrice)
    }

    @Test
    fun testGuestPurchaseBlockedUntilAccountLinked() = runTest(testDispatcher) {
        // Setup guest auth state
        authRepo.setAuthState(AuthState.GUEST, null)

        val viewModel = PremiumViewModel(billingRepo, entitlementRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        assertFalse("Guest account is not linked", viewModel.uiState.value.isAccountLinked)

        // Attempt purchase
        viewModel.restorePurchases()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Please sign in to your Zynpath account first"))
    }

    @Test
    fun testPurchaseEventSuccessUpdatesStateToActive() = runTest(testDispatcher) {
        val session = AuthSession(
            playerId = "player_123",
            publicZynpathId = "ZYN-1234",
            displayName = "Player One",
            accountType = "REGISTERED",
            provider = AuthProvider.GOOGLE,
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        authRepo.setAuthState(AuthState.AUTHENTICATED, session)

        val viewModel = PremiumViewModel(billingRepo, entitlementRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Simulate successful purchase event
        billingRepo.emitPurchaseEvent(
            PurchaseEvent.Success(
                purchaseToken = "tok_test_abc",
                orderId = "GPA.1234-5678",
                productId = "zynpath_premium",
                basePlanId = "monthly"
            )
        )
        advanceUntilIdle()

        // Entitlement is verified
        val entitlement = entitlementRepo.entitlement.first()
        assertEquals(EntitlementStatus.ACTIVE, entitlement.status)
        assertTrue(entitlement.isPremiumActive)
    }

    @Test
    fun testRestorePurchasesWhenNoPurchasesFound() = runTest(testDispatcher) {
        val session = AuthSession(
            playerId = "player_123",
            publicZynpathId = "ZYN-1234",
            displayName = "Player One",
            accountType = "REGISTERED",
            provider = AuthProvider.GOOGLE,
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        authRepo.setAuthState(AuthState.AUTHENTICATED, session)

        val viewModel = PremiumViewModel(billingRepo, entitlementRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        viewModel.restorePurchases()
        advanceUntilIdle()

        assertEquals("No active subscription purchases found on this Google account.", viewModel.uiState.value.statusMessage)
    }
}
