package com.zynpath.game.feature.rewards

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.zynpath.game.core.ads.CoinRewardedAdHandler
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.economy.WalletRepositoryImpl
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.fake.FakeWalletDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FreeRewardsModalAndEconomyTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var walletDao: FakeWalletDao
    private lateinit var levelProgressDao: FakeLevelProgressDao
    private lateinit var playerStatsDao: FakePlayerStatsDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var preferencesRepository: FakePreferencesRepository
    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var walletRepository: WalletRepositoryImpl
    private lateinit var progressRepository: ProgressRepositoryImpl
    private lateinit var fakeAdHandler: FakeCoinRewardedAdHandler

    private class FakeDataStore : DataStore<Preferences> {
        private val flow = MutableStateFlow<Preferences>(emptyPreferences())
        override val data: Flow<Preferences> = flow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val current = flow.value.toMutablePreferences()
            val updated = transform(current)
            flow.value = updated
            return updated
        }
    }

    private class FakeCoinRewardedAdHandler : CoinRewardedAdHandler {
        private val _isAdLoaded = MutableStateFlow(true)
        override val isAdLoaded = _isAdLoaded.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        override val isLoading = _isLoading.asStateFlow()

        var shouldSucceed = true
        var failMessage = "Ad closed before completion. No coins earned."
        var isUnavailable = false

        override fun preloadAd() {
            _isAdLoaded.value = true
        }

        override fun showCoinRewardedAd(
            activity: Activity,
            onRewarded: () -> Unit,
            onFailed: (String) -> Unit
        ) {
            if (isUnavailable) {
                onFailed("Video ad is still loading. Please try again in a moment.")
                return
            }
            if (shouldSucceed) {
                onRewarded()
            } else {
                onFailed(failMessage)
            }
        }
    }

    private class DummyActivity : Activity()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        walletDao = FakeWalletDao()
        levelProgressDao = FakeLevelProgressDao()
        playerStatsDao = FakePlayerStatsDao()
        gameSessionDao = FakeGameSessionDao()
        preferencesRepository = FakePreferencesRepository()
        fakeDataStore = FakeDataStore()

        walletRepository = WalletRepositoryImpl(
            walletDao = walletDao,
            levelProgressDao = levelProgressDao,
            preferencesRepository = preferencesRepository,
            dataStore = fakeDataStore
        )

        progressRepository = ProgressRepositoryImpl(
            levelProgressDao = levelProgressDao,
            playerStatsDao = playerStatsDao,
            gameSessionDao = gameSessionDao
        )

        fakeAdHandler = FakeCoinRewardedAdHandler()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RewardsViewModel {
        return RewardsViewModel(
            walletRepository = walletRepository,
            coinRewardedAdManager = fakeAdHandler,
            preferencesRepository = preferencesRepository,
            progressRepository = progressRepository,
            ioDispatcher = testDispatcher
        )
    }

    // 1. Modal opens with immediate local state without blocking
    @Test
    fun test01_immediateModalStateInitialization() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(0, state.walletBalance)
        assertFalse(state.isDailyClaimed)
        assertFalse(state.isClaimingDaily)
        assertEquals(EconomyConfig.MAX_DAILY_COIN_ADS, state.remainingCoinAds)
        assertEquals(3, state.remainingCoinAds)
        job.cancel()
    }

    // 2. Daily reward +50 claim succeeds and updates balance live
    @Test
    fun test02_dailyClaimSucceedsAndUpdatesBalanceLive() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.claimDailyLoginReward()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.isDailyClaimed)
        assertFalse(state.isClaimingDaily)
        assertEquals(50, state.walletBalance)
        assertEquals(50, walletRepository.getCurrentBalance())
        assertEquals(EconomyConfig.DAILY_LOGIN_CLAIM_REWARD, state.rewardAnimationCoins)
        assertTrue(state.snackbarMessage?.contains("+50 Coins") == true)
        job.cancel()
    }

    // 3. Daily reward cannot be double claimed (Idempotency)
    @Test
    fun test03_dailyRewardIdempotentAgainstRapidTaps() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        // Rapid duplicate taps
        vm.claimDailyLoginReward()
        vm.claimDailyLoginReward()
        vm.claimDailyLoginReward()
        advanceUntilIdle()

        assertEquals(50, vm.uiState.value.walletBalance)
        assertEquals(50, walletRepository.getCurrentBalance())
        assertTrue(vm.uiState.value.isDailyClaimed)
        job.cancel()
    }

    // 4. Daily claimed state persists across ViewModel recreation (app restart simulation)
    @Test
    fun test04_dailyClaimStatePersistsAcrossRestarts() = runTest(testDispatcher) {
        val vm1 = createViewModel()
        val job1 = backgroundScope.launch { vm1.uiState.collect {} }
        advanceUntilIdle()
        vm1.claimDailyLoginReward()
        advanceUntilIdle()
        assertEquals(50, vm1.uiState.value.walletBalance)
        job1.cancel()

        // Recreate ViewModel (simulate closing and reopening modal / app)
        val vm2 = createViewModel()
        val job2 = backgroundScope.launch { vm2.uiState.collect {} }
        advanceUntilIdle()

        val state2 = vm2.uiState.value
        assertEquals(50, state2.walletBalance)
        assertTrue(state2.isDailyClaimed)
        job2.cancel()
    }

    // 5. Rewarded video confirmed awards +150 coins and decrements available count live
    @Test
    fun test05_rewardedVideoConfirmedGrants150AndDecrementsSlots() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        val dummyActivity = DummyActivity()
        fakeAdHandler.shouldSucceed = true

        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(150, state.walletBalance)
        assertEquals(150, walletRepository.getCurrentBalance())
        assertEquals(2, state.remainingCoinAds)
        assertEquals(EconomyConfig.REWARDED_COIN_AD_REWARD, state.rewardAnimationCoins)
        job.cancel()
    }

    // 6. Rewarded video skipped grants 0 coins
    @Test
    fun test06_rewardedVideoSkippedGrantsZeroCoins() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        val dummyActivity = DummyActivity()
        fakeAdHandler.shouldSucceed = false
        fakeAdHandler.failMessage = "Ad closed before completion. No coins earned."

        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(0, state.walletBalance)
        assertEquals(0, walletRepository.getCurrentBalance())
        assertEquals(3, state.remainingCoinAds)
        assertEquals("Ad closed before completion. No coins earned.", state.snackbarMessage)
        job.cancel()
    }

    // 7. Rewarded video unavailable grants 0 coins and shows friendly feedback without freezing
    @Test
    fun test07_rewardedVideoUnavailableGrantsZeroCoins() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        val dummyActivity = DummyActivity()
        fakeAdHandler.isUnavailable = true

        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(0, state.walletBalance)
        assertEquals(3, state.remainingCoinAds)
        assertTrue(state.snackbarMessage?.contains("Video ad is still loading") == true)
        job.cancel()
    }

    // 8. Enforces maximum 3 coin video claims per eligible day
    @Test
    fun test08_enforcesMaxThreeCoinVideosPerDay() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        val dummyActivity = DummyActivity()
        fakeAdHandler.shouldSucceed = true

        // Claim 1
        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()
        assertEquals(150, vm.uiState.value.walletBalance)
        assertEquals(2, vm.uiState.value.remainingCoinAds)

        // Claim 2
        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()
        assertEquals(300, vm.uiState.value.walletBalance)
        assertEquals(1, vm.uiState.value.remainingCoinAds)

        // Claim 3
        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()
        assertEquals(450, vm.uiState.value.walletBalance)
        assertEquals(0, vm.uiState.value.remainingCoinAds)

        // Claim 4 (Attempt beyond limit)
        vm.watchRewardedCoinAd(dummyActivity)
        advanceUntilIdle()
        assertEquals(450, vm.uiState.value.walletBalance)
        assertEquals(0, vm.uiState.value.remainingCoinAds)
        assertTrue(vm.uiState.value.snackbarMessage?.contains("Daily limit reached") == true)
        job.cancel()
    }

    // 9. Single source of truth: balanceFlow reflects external credits live into Rewards state
    @Test
    fun test09_singleSourceOfTruthBalanceFlowUpdatesRewardsLive() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(0, vm.uiState.value.walletBalance)

        // External credit (e.g. Solo first clear of level 1)
        walletRepository.grantSoloFirstClearReward(1)
        advanceUntilIdle()

        assertEquals(EconomyConfig.SOLO_FIRST_CLEAR_REWARD, vm.uiState.value.walletBalance)
        assertEquals(100, vm.uiState.value.walletBalance)

        // External credit (e.g. World completion)
        walletRepository.credit("WORLD_COMPLETION", EconomyConfig.WORLD_FIRST_CLEAR_REWARD, "world_clear_1")
        advanceUntilIdle()

        assertEquals(600, vm.uiState.value.walletBalance)
        job.cancel()
    }

    // 10. Economy values preserved: Daily=50, Video=150, Solo=100, World=500
    @Test
    fun test10_economyConstantsPreserved() {
        assertEquals(50, EconomyConfig.DAILY_LOGIN_CLAIM_REWARD)
        assertEquals(150, EconomyConfig.REWARDED_COIN_AD_REWARD)
        assertEquals(3, EconomyConfig.MAX_DAILY_COIN_ADS)
        assertEquals(100, EconomyConfig.SOLO_FIRST_CLEAR_REWARD)
        assertEquals(500, EconomyConfig.WORLD_FIRST_CLEAR_REWARD)
    }
}
