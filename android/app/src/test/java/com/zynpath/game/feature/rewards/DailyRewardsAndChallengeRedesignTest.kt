package com.zynpath.game.feature.rewards

import android.app.Activity
import com.zynpath.game.core.ads.CoinRewardedAdManager
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.economy.WalletRepositoryImpl
import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeSchedule
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.fake.FakeDailyChallengeRepository
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.fake.FakeWalletDao
import com.zynpath.game.feature.daily.DailyLeaderboardUiState
import com.zynpath.game.feature.daily.DailyLeaderboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DailyRewardsAndChallengeRedesignTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var walletDao: FakeWalletDao
    private lateinit var levelProgressDao: FakeLevelProgressDao
    private lateinit var playerStatsDao: FakePlayerStatsDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var preferencesRepository: FakePreferencesRepository
    private lateinit var walletRepository: WalletRepositoryImpl
    private lateinit var progressRepository: ProgressRepositoryImpl
    private lateinit var dailyChallengeRepository: FakeDailyChallengeRepository
    private lateinit var testClock: DailyChallengeClock
    private lateinit var testSchedule: DailyChallengeSchedule
    private lateinit var rewardsViewModel: RewardsViewModel

    private class DummyActivity : Activity()

    private class TestAdHandler : com.zynpath.game.core.ads.CoinRewardedAdHandler {
        val loadedFlow = kotlinx.coroutines.flow.MutableStateFlow(true)
        val loadingFlow = kotlinx.coroutines.flow.MutableStateFlow(false)
        override val isAdLoaded = loadedFlow
        override val isLoading = loadingFlow
        var shouldSucceed = true
        var failureMessage = "Ad closed before completion. No coins earned."
        override fun preloadAd() { loadedFlow.value = true }
        override fun showCoinRewardedAd(activity: Activity, onRewarded: () -> Unit, onFailed: (String) -> Unit) {
            if (shouldSucceed) onRewarded() else onFailed(failureMessage)
        }
    }

    private lateinit var fakeAdHandler: TestAdHandler

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        walletDao = FakeWalletDao()
        levelProgressDao = FakeLevelProgressDao()
        playerStatsDao = FakePlayerStatsDao()
        gameSessionDao = FakeGameSessionDao()
        preferencesRepository = FakePreferencesRepository()

        testClock = object : DailyChallengeClock {
            override fun currentUtcInstant(): Instant = Instant.ofEpochMilli(1790467200000L)
            override fun currentUtcDate(): LocalDate = LocalDate.parse("2026-10-08")
            override fun currentUtcDateKey(): String = "2026-10-08"
            override fun millisUntilNextReset(): Long = 3600000L
            override fun challengeWindowStartMs(date: LocalDate): Long = 1790467200000L
            override fun challengeWindowEndMs(date: LocalDate): Long = 1790553599999L
            override fun isToday(dateKey: String): Boolean = dateKey == "2026-10-08"
        }
        testSchedule = DailyChallengeSchedule()
        dailyChallengeRepository = FakeDailyChallengeRepository(testClock, testSchedule)

        val fakeDataStore = object : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
            private val flow = kotlinx.coroutines.flow.MutableStateFlow(androidx.datastore.preferences.core.emptyPreferences())
            override val data: kotlinx.coroutines.flow.Flow<androidx.datastore.preferences.core.Preferences> = flow
            override suspend fun updateData(transform: suspend (t: androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences): androidx.datastore.preferences.core.Preferences {
                val updated = transform(flow.value)
                flow.value = updated
                return updated
            }
        }

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

        fakeAdHandler = TestAdHandler()

        rewardsViewModel = RewardsViewModel(
            walletRepository = walletRepository,
            coinRewardedAdManager = fakeAdHandler,
            preferencesRepository = preferencesRepository,
            progressRepository = progressRepository,
            dailyChallengeRepository = dailyChallengeRepository,
            clock = testClock,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- REWARDS MODAL & AUTHORITATIVE DAILY CLAIM ---

    @Test
    fun testAuthoritativeDailyLoginRewardIsFiftyCoins() {
        assertEquals("Authoritative daily login claim reward must be 50", 50, EconomyConfig.DAILY_LOGIN_CLAIM_REWARD)
        assertEquals("UI state daily reward amount must be 50", 50, rewardsViewModel.uiState.value.dailyRewardAmount)
    }

    @Test
    fun testClaimDailyLoginRewardCreditsFiftyCoinsAndUpdatesLive() = runTest(testDispatcher) {
        advanceUntilIdle()

        assertFalse("Initially daily reward must not be claimed", rewardsViewModel.uiState.value.isDailyClaimed)
        assertEquals(0, rewardsViewModel.uiState.value.walletBalance)

        // Claim daily login
        rewardsViewModel.claimDailyLoginReward()
        advanceUntilIdle()

        assertTrue("Daily claim flag must be updated to true", rewardsViewModel.uiState.value.isDailyClaimed)
        assertEquals("Balance must increase by 50 immediately", 50, rewardsViewModel.uiState.value.walletBalance)
        assertEquals("Wallet repository balance must match", 50, walletRepository.getCurrentBalance())
    }

    @Test
    fun testRepeatedTapsDoNotDuplicateDailyRewardCoins() = runTest(testDispatcher) {
        advanceUntilIdle()

        rewardsViewModel.claimDailyLoginReward()
        advanceUntilIdle()
        assertEquals(50, rewardsViewModel.uiState.value.walletBalance)

        // Repeated tap
        rewardsViewModel.claimDailyLoginReward()
        advanceUntilIdle()
        assertEquals("Balance must remain exactly 50 after duplicate claim attempt", 50, rewardsViewModel.uiState.value.walletBalance)
        assertEquals("Wallet repository balance must remain 50", 50, walletRepository.getCurrentBalance())
    }

    // --- FAST DETERMINISTIC DAILY CHALLENGE & PUZZLE VALIDITY ---

    @Test
    fun testDailyChallengeResolvesInstantlyDeterministic() {
        val startNano = System.nanoTime()
        val challenge1 = dailyChallengeRepository.getTodayChallenge()
        val durationMs = (System.nanoTime() - startNano) / 1_000_000.0

        assertTrue("Daily puzzle must resolve in < 10ms (was ${durationMs}ms)", durationMs < 10.0)
        assertEquals("2026-10-08", challenge1.dateKey)
        assertNotNull(challenge1.puzzleDefinition)
        assertTrue(challenge1.puzzleDefinition.gridDimensions.rows >= 4)
        assertTrue(challenge1.puzzleDefinition.gridDimensions.columns >= 4)

        // Determinism test
        val challenge2 = dailyChallengeRepository.getTodayChallenge()
        assertEquals(challenge1.challengeId, challenge2.challengeId)
        assertEquals(challenge1.puzzleId, challenge2.puzzleId)
        assertEquals(challenge1.puzzleFingerprint, challenge2.puzzleFingerprint)
    }

    @Test
    fun testDailyChallengeCompletionFirstClearAwardsFiftyCoins() = runTest(testDispatcher) {
        val today = testClock.currentUtcDateKey()
        assertFalse(walletRepository.isDailyChallengeClearClaimed(today))

        val result = walletRepository.claimDailyChallengeReward(today)
        assertTrue(result.isSuccess)
        assertEquals(50, result.getOrNull())
        assertEquals(50, walletRepository.getCurrentBalance())
        assertTrue(walletRepository.isDailyChallengeClearClaimed(today))

        // Replay attempt must not award duplicate coins
        val preReplayBalance = walletRepository.getCurrentBalance()
        val replayResult = walletRepository.claimDailyChallengeReward(today)
        assertTrue(replayResult.isSuccess)
        assertEquals("Balance must remain exactly 50 on replay attempt", preReplayBalance, walletRepository.getCurrentBalance())
        assertEquals(50, walletRepository.getCurrentBalance())
    }

    @Test
    fun testDailyChallengeStatusUpdatesInRewardsViewModel() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertEquals(DailyChallengeAvailability.AVAILABLE, rewardsViewModel.uiState.value.challengeAvailability)

        // Solve today's challenge
        val todayChallenge = dailyChallengeRepository.getTodayChallenge()
        dailyChallengeRepository.recordCompletion(
            challenge = todayChallenge,
            solveTimeMs = 35000L,
            movesCount = 20,
            verificationStatus = DailyVerificationStatus.LOCAL_COMPLETION,
            serverAttemptId = null,
            isLeaderboardEligible = false
        )
        advanceUntilIdle()

        assertEquals(DailyChallengeAvailability.COMPLETED, rewardsViewModel.uiState.value.challengeAvailability)
        assertTrue("Streak must be at least 1", rewardsViewModel.uiState.value.streakDays >= 1)
    }

    // --- LEADERBOARD MODAL STATE & DATA POLICY ---

    @Test
    fun testLeaderboardViewModelHandlesEmptyRankingsCleanly() = runTest(testDispatcher) {
        val vm = DailyLeaderboardViewModel(dailyChallengeRepository, testClock)
        advanceUntilIdle()

        // Empty response from fake repository
        val state = vm.uiState.value
        assertTrue("State must be Empty when entries list is empty", state is DailyLeaderboardUiState.Empty)
    }

    @Test
    fun testLeaderboardViewModelRendersVerifiedEntriesWithoutFictionalScores() = runTest(testDispatcher) {
        val entry1 = com.zynpath.game.core.puzzle.daily.DailyLeaderboardEntry(
            rank = 1,
            publicZynpathId = "ZYN-1",
            displayName = "TopPlayer",
            avatarId = "av_1",
            solveTimeMs = 15200L,
            completedAt = 1000L,
            verificationStatus = DailyVerificationStatus.SERVER_VERIFIED
        )
        val entry2 = com.zynpath.game.core.puzzle.daily.DailyLeaderboardEntry(
            rank = 2,
            publicZynpathId = "ZYN-2",
            displayName = "Solver99",
            avatarId = "av_2",
            solveTimeMs = 18900L,
            completedAt = 1050L,
            verificationStatus = DailyVerificationStatus.SERVER_VERIFIED
        )
        dailyChallengeRepository.setMockLeaderboardResponse(
            com.zynpath.game.core.puzzle.daily.DailyLeaderboardResponse(
                challengeId = "c_1",
                dateKey = "2026-10-08",
                puzzleFingerprint = "fp_1",
                entries = listOf(entry1, entry2),
                playerEntry = entry2,
                totalEntries = 2,
                page = 0,
                pageSize = 50
            )
        )

        val vm = DailyLeaderboardViewModel(dailyChallengeRepository, testClock)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is DailyLeaderboardUiState.Success)
        val successState = state as DailyLeaderboardUiState.Success
        assertEquals(2, successState.response.entries.size)
        assertEquals("TopPlayer", successState.response.entries[0].displayName)
        assertEquals(1, successState.response.entries[0].rank)
        assertEquals(2, successState.response.playerEntry?.rank)
    }

    @Test
    fun testLeaderboardErrorStateProvidesRetryMechanism() = runTest(testDispatcher) {
        dailyChallengeRepository.setSimulateError(true)

        val vm = DailyLeaderboardViewModel(dailyChallengeRepository, testClock)
        advanceUntilIdle()

        assertTrue(vm.uiState.value is DailyLeaderboardUiState.Error)

        // Reset error and retry
        dailyChallengeRepository.setSimulateError(false)
        vm.retry()
        advanceUntilIdle()

        assertTrue("After retry without error, state must be Success or Empty", vm.uiState.value is DailyLeaderboardUiState.Empty || vm.uiState.value is DailyLeaderboardUiState.Success)
    }

    // --- REWARDED AD IN DAILY REWARDS & ECONOMY CONSISTENCY ---

    @Test
    fun testAuthoritativeDailyAdBonusConfigMatchesDisplayedFiftyCoins() {
        assertEquals("Authoritative DAILY_LOGIN_AD_BONUS must be 50", 50, EconomyConfig.DAILY_LOGIN_AD_BONUS)
        assertEquals("UI state daily ad reward amount must be 50", 50, rewardsViewModel.uiState.value.dailyAdRewardAmount)
    }

    @Test
    fun testWatchDailyAdCreditsFiftyCoinsAndUpdatesLive() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertFalse(rewardsViewModel.uiState.value.isDailyAdClaimed)
        assertEquals(0, rewardsViewModel.uiState.value.walletBalance)

        fakeAdHandler.shouldSucceed = true
        rewardsViewModel.watchDailyAd(DummyActivity())
        advanceUntilIdle()

        assertTrue("Daily ad claim flag must be updated to true", rewardsViewModel.uiState.value.isDailyAdClaimed)
        assertFalse("Watching flag must be reset", rewardsViewModel.uiState.value.isWatchingDailyAd)
        assertEquals("Balance must increase by 50 immediately", 50, rewardsViewModel.uiState.value.walletBalance)
        assertEquals("Wallet repository balance must match", 50, walletRepository.getCurrentBalance())
        assertEquals(50, rewardsViewModel.uiState.value.rewardAnimationCoins)
        assertTrue(rewardsViewModel.uiState.value.snackbarMessage?.contains("+50 Coins Earned") == true)
    }

    @Test
    fun testFailedOrSkippedAdDoesNotCreditCoins() = runTest(testDispatcher) {
        advanceUntilIdle()
        fakeAdHandler.shouldSucceed = false
        fakeAdHandler.failureMessage = "Ad closed before completion. No coins earned."

        rewardsViewModel.watchDailyAd(DummyActivity())
        advanceUntilIdle()

        assertFalse("Daily ad claim flag must remain false", rewardsViewModel.uiState.value.isDailyAdClaimed)
        assertFalse("Watching flag must be reset", rewardsViewModel.uiState.value.isWatchingDailyAd)
        assertEquals("Balance must remain 0 on failed ad", 0, rewardsViewModel.uiState.value.walletBalance)
        assertEquals("Wallet repository balance must remain 0", 0, walletRepository.getCurrentBalance())
        assertEquals("Ad closed before completion. No coins earned.", rewardsViewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun testAdUnavailableShowsFeedbackAndDoesNotCreditCoins() = runTest(testDispatcher) {
        advanceUntilIdle()
        fakeAdHandler.loadedFlow.value = false
        advanceUntilIdle()

        rewardsViewModel.watchDailyAd(DummyActivity())
        advanceUntilIdle()

        assertFalse(rewardsViewModel.uiState.value.isDailyAdClaimed)
        assertFalse(rewardsViewModel.uiState.value.isWatchingDailyAd)
        assertEquals(0, rewardsViewModel.uiState.value.walletBalance)
        assertTrue(rewardsViewModel.uiState.value.snackbarMessage?.contains("Video ad is still loading") == true)
    }

    @Test
    fun testWatchDailyAdCannotBeDoubleClaimed() = runTest(testDispatcher) {
        advanceUntilIdle()
        fakeAdHandler.shouldSucceed = true

        rewardsViewModel.watchDailyAd(DummyActivity())
        advanceUntilIdle()
        assertEquals(50, rewardsViewModel.uiState.value.walletBalance)

        // Duplicate attempt
        rewardsViewModel.watchDailyAd(DummyActivity())
        advanceUntilIdle()
        assertEquals("Balance must remain exactly 50 after second ad attempt", 50, rewardsViewModel.uiState.value.walletBalance)
        assertEquals("Wallet repository balance must remain 50", 50, walletRepository.getCurrentBalance())
    }
}
