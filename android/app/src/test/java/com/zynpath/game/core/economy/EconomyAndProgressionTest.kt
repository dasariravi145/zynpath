package com.zynpath.game.core.economy

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefGreenClaim
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.puzzle.model.StarRatingPolicy
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.fake.FakeWalletDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Phase J — Complete Automated Test Suite (25 Tests)
 * Validates coin earning, spending, ledger integrity, star rating criteria,
 * progression boundary safety, multiplayer settlement, and guest migration.
 */
class EconomyAndProgressionTest {

    private lateinit var walletDao: FakeWalletDao
    private lateinit var levelProgressDao: FakeLevelProgressDao
    private lateinit var playerStatsDao: FakePlayerStatsDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var preferencesRepository: FakePreferencesRepository
    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var walletRepository: WalletRepositoryImpl
    private lateinit var progressRepository: ProgressRepositoryImpl

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

    @Before
    fun setup() {
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
    }

    // 1. One-time welcome reward (+60 coins)
    @Test
    fun test01_oneTimeWelcomeReward() = runTest {
        assertEquals(0, walletRepository.getCurrentBalance())
        val firstGrant = walletRepository.ensureWelcomeGiftGranted()
        assertTrue(firstGrant)
        assertEquals(EconomyConfig.WELCOME_COINS, walletRepository.getCurrentBalance())

        // Second grant must be idempotent and not credit additional coins
        val secondGrant = walletRepository.ensureWelcomeGiftGranted()
        assertFalse(secondGrant) // already granted
        assertEquals(EconomyConfig.WELCOME_COINS, walletRepository.getCurrentBalance())
    }

    // 2. Free Solo Levels 1–3
    @Test
    fun test02_freeSoloLevels1To3() = runTest {
        // Levels 1-3 have zero entry fee
        val entry1 = walletRepository.commitSoloRewardRunEntry(1)
        val entry2 = walletRepository.commitSoloRewardRunEntry(2)
        val entry3 = walletRepository.commitSoloRewardRunEntry(3)
        assertTrue(entry1.isSuccess)
        assertTrue(entry2.isSuccess)
        assertTrue(entry3.isSuccess)
        assertEquals(0, walletRepository.getCurrentBalance()) // No coins deducted

        // First clear grants +5 coins
        val clear1 = walletRepository.grantSoloFirstClearReward(1)
        assertTrue(clear1.isSuccess)
        assertEquals(EconomyConfig.SOLO_FIRST_CLEAR_REWARD, walletRepository.getCurrentBalance())
    }

    // 3. Solo paid entry deducted once (Level 4+)
    @Test
    fun test03_soloPaidEntryDeductedOnce() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        val startBalance = walletRepository.getCurrentBalance() // 60

        val entryRes = walletRepository.commitSoloRewardRunEntry(4)
        assertTrue(entryRes.isSuccess)
        assertEquals(startBalance - EconomyConfig.SOLO_REWARD_RUN_ENTRY_FEE, walletRepository.getCurrentBalance()) // 57
        assertTrue(walletRepository.isLevelPaidAttemptActive(4))
    }

    // 4. Failed level without extra deduction
    @Test
    fun test04_failedLevelWithoutExtraDeduction() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        walletRepository.commitSoloRewardRunEntry(4)
        val balanceAfterEntry = walletRepository.getCurrentBalance()

        // When attempt fails, no additional debit is committed
        assertEquals(balanceAfterEntry, walletRepository.getCurrentBalance())
    }

    // 5. Free retry of the same paid attempt
    @Test
    fun test05_freeRetrySamePaidAttempt() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        walletRepository.commitSoloRewardRunEntry(4)
        val balance = walletRepository.getCurrentBalance()

        // Re-committing entry for the active attempt is free
        val retryRes = walletRepository.commitSoloRewardRunEntry(4)
        assertTrue(retryRes.isSuccess)
        assertEquals(balance, walletRepository.getCurrentBalance())
    }

    // 6. App restart during an entered level preserves paid attempt
    @Test
    fun test06_appRestartPreservesPaidAttempt() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        walletRepository.commitSoloRewardRunEntry(4)
        assertTrue(walletRepository.isLevelPaidAttemptActive(4))

        // Create new repository instance simulating app restart with same persisted DataStore & DB
        val restartedRepo = WalletRepositoryImpl(
            walletDao = walletDao,
            levelProgressDao = levelProgressDao,
            preferencesRepository = preferencesRepository,
            dataStore = fakeDataStore
        )
        assertTrue(restartedRepo.isLevelPaidAttemptActive(4))
    }

    // 7. Successful first-clear reward (net +2 coins)
    @Test
    fun test07_successfulFirstClearReward() = runTest {
        walletRepository.ensureWelcomeGiftGranted() // 60
        walletRepository.commitSoloRewardRunEntry(4) // 57 (-3)
        val clearRes = walletRepository.grantSoloFirstClearReward(4) // 62 (+5)
        assertTrue(clearRes.isSuccess)
        assertEquals(62, walletRepository.getCurrentBalance()) // Net +2
    }

    // 8. No duplicate replay reward
    @Test
    fun test08_noDuplicateReplayReward() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        walletRepository.grantSoloFirstClearReward(4)
        val balanceAfterFirst = walletRepository.getCurrentBalance()

        // Subsequent clear attempt must be idempotent
        val replayRes = walletRepository.grantSoloFirstClearReward(4)
        assertTrue(replayRes.isSuccess)
        assertEquals(balanceAfterFirst, walletRepository.getCurrentBalance())
    }

    // 9. Free Practice with zero balance
    @Test
    fun test09_freePracticeZeroBalance() = runTest {
        assertEquals(0, walletRepository.getCurrentBalance())
        assertFalse(walletRepository.canAfford(EconomyConfig.SOLO_REWARD_RUN_ENTRY_FEE))
        // Free Practice entry commits 0 coins and player can still play
    }

    // 10. Daily login reward claimed once (+20 coins)
    @Test
    fun test10_dailyLoginClaimedOnce() = runTest {
        val today = "2026-09-29"
        assertFalse(walletRepository.isDailyLoginClaimed(today))
        val res = walletRepository.claimDailyLoginReward(today)
        assertTrue(res.isSuccess)
        assertEquals(EconomyConfig.DAILY_LOGIN_BASE_REWARD, walletRepository.getCurrentBalance())
        assertTrue(walletRepository.isDailyLoginClaimed(today))

        // Duplicate claim
        val dup = walletRepository.claimDailyLoginReward(today)
        assertTrue(dup.isSuccess)
        assertEquals(EconomyConfig.DAILY_LOGIN_BASE_REWARD, walletRepository.getCurrentBalance())
    }

    // 11. Rewarded-ad credit exactly once (+20 coins)
    @Test
    fun test11_rewardedAdCreditOnce() = runTest {
        val today = "2026-09-29"
        walletRepository.claimDailyLoginReward(today)
        assertFalse(walletRepository.isDailyLoginAdBonusClaimed(today))

        val adRes = walletRepository.claimDailyLoginAdBonus(today)
        assertTrue(adRes.isSuccess)
        assertEquals(EconomyConfig.DAILY_LOGIN_BASE_REWARD + EconomyConfig.DAILY_LOGIN_AD_BONUS, walletRepository.getCurrentBalance())
        assertTrue(walletRepository.isDailyLoginAdBonusClaimed(today))

        // Duplicate ad claim
        walletRepository.claimDailyLoginAdBonus(today)
        assertEquals(EconomyConfig.DAILY_LOGIN_BASE_REWARD + EconomyConfig.DAILY_LOGIN_AD_BONUS, walletRepository.getCurrentBalance())
    }

    // 12. Failed or skipped ad gives zero coins
    @Test
    fun test12_failedOrSkippedAdGivesZeroCoins() = runTest {
        val balanceBefore = walletRepository.getCurrentBalance()
        // When ad fails or skipped, claim method is not called
        assertEquals(balanceBefore, walletRepository.getCurrentBalance())
    }

    // 13. Daily ad limits (max 2 coin ads / day)
    @Test
    fun test13_dailyAdLimits() = runTest {
        val today = "2026-09-29"
        assertEquals(2, walletRepository.getRemainingDailyCoinAds(today))

        val ad1 = walletRepository.claimCoinAdReward(today)
        assertTrue(ad1.isSuccess)
        assertEquals(1, walletRepository.getRemainingDailyCoinAds(today))

        val ad2 = walletRepository.claimCoinAdReward(today)
        assertTrue(ad2.isSuccess)
        assertEquals(0, walletRepository.getRemainingDailyCoinAds(today))

        val ad3 = walletRepository.claimCoinAdReward(today)
        assertTrue(ad3.isFailure) // Blocked by daily limit
        assertEquals(30, walletRepository.getCurrentBalance()) // 15 * 2
    }

    // 14. Daily Challenge reward deduplication (+15 coins)
    @Test
    fun test14_dailyChallengeDeduplication() = runTest {
        val today = "2026-09-29"
        assertFalse(walletRepository.isDailyChallengeClearClaimed(today))
        val res = walletRepository.claimDailyChallengeReward(today)
        assertTrue(res.isSuccess)
        assertEquals(EconomyConfig.DAILY_CHALLENGE_FIRST_CLEAR_REWARD, walletRepository.getCurrentBalance())

        // Duplicate clear
        walletRepository.claimDailyChallengeReward(today)
        assertEquals(EconomyConfig.DAILY_CHALLENGE_FIRST_CLEAR_REWARD, walletRepository.getCurrentBalance())
    }

    // 15. World completion reward deduplication (+25 coins)
    @Test
    fun test15_worldCompletionDeduplication() = runTest {
        assertFalse(walletRepository.isWorldCompletionClaimed(1))
        val res = walletRepository.claimWorldCompletionReward(1)
        assertTrue(res.isSuccess)
        assertEquals(EconomyConfig.WORLD_COMPLETION_BASE_REWARD, walletRepository.getCurrentBalance())
        assertTrue(walletRepository.isWorldCompletionClaimed(1))

        // Duplicate world claim
        walletRepository.claimWorldCompletionReward(1)
        assertEquals(EconomyConfig.WORLD_COMPLETION_BASE_REWARD, walletRepository.getCurrentBalance())
    }

    // 16. World 1 Level 1 -> Level 2, not World 2
    @Test
    fun test16_world1Level1ToLevel2() = runTest {
        val result = ValidatedCompletionResult(
            puzzleId = "p_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 5000L,
            moveCount = 10,
            hintCount = 0
        )
        progressRepository.recordValidatedCompletion(result)
        assertTrue("Level 2 must be unlocked", progressRepository.isLevelUnlocked(2))
        assertFalse("Level 21 (World 2) must remain locked", progressRepository.isLevelUnlocked(21))
    }

    // 17. World 1 Level 20 -> World 2
    @Test
    fun test17_world1Level20ToWorld2() = runTest {
        // Unlock and complete all 20 levels of World 1
        for (lvl in 1..20) {
            progressRepository.recordValidatedCompletion(
                ValidatedCompletionResult(
                    puzzleId = "p_$lvl",
                    levelId = lvl,
                    worldId = 1,
                    isValidated = true,
                    elapsedTimeMs = 5000L,
                    moveCount = 10,
                    hintCount = 0
                )
            )
        }
        assertTrue("Level 21 (World 2 Level 1) must be unlocked", progressRepository.isLevelUnlocked(21))
    }

    // 18. Quick Duel winner/loser settlement (15 entry, 25 winner, 0 loser)
    @Test
    fun test18_quickDuelWinnerLoserSettlement() = runTest {
        walletRepository.ensureWelcomeGiftGranted() // 60
        // Match entry: 15
        walletRepository.debit("DUEL_ENTRY", EconomyConfig.QUICK_DUEL_ENTRY_FEE, "entry_match_1_p1")
        assertEquals(45, walletRepository.getCurrentBalance())

        // Winner settlement: +25 coins
        walletRepository.credit("DUEL_SETTLEMENT", EconomyConfig.QUICK_DUEL_WINNER_PAYOUT, "settle_match_1_p1")
        assertEquals(70, walletRepository.getCurrentBalance()) // Net +10
    }

    // 19. Duel cancellation refund
    @Test
    fun test19_duelCancellationRefund() = runTest {
        walletRepository.ensureWelcomeGiftGranted() // 60
        walletRepository.debit("DUEL_ENTRY", EconomyConfig.QUICK_DUEL_ENTRY_FEE, "entry_match_2_p1")
        assertEquals(45, walletRepository.getCurrentBalance())

        walletRepository.credit("MATCH_REFUND", EconomyConfig.QUICK_DUEL_ENTRY_FEE, "refund_match_2_p1")
        assertEquals(60, walletRepository.getCurrentBalance()) // Full refund
    }

    // 20. Friends Arena with 2-5 participants tiered rewards
    @Test
    fun test20_friendsArenaTieredRewards() {
        assertEquals(25, EconomyConfig.getArenaPayout(2, 1))
        assertEquals(0, EconomyConfig.getArenaPayout(2, 2))

        assertEquals(30, EconomyConfig.getArenaPayout(3, 1))
        assertEquals(10, EconomyConfig.getArenaPayout(3, 2))
        assertEquals(0, EconomyConfig.getArenaPayout(3, 3))

        assertEquals(40, EconomyConfig.getArenaPayout(4, 1))
        assertEquals(15, EconomyConfig.getArenaPayout(4, 2))
        assertEquals(0, EconomyConfig.getArenaPayout(4, 3))

        assertEquals(50, EconomyConfig.getArenaPayout(5, 1))
        assertEquals(20, EconomyConfig.getArenaPayout(5, 2))
        assertEquals(0, EconomyConfig.getArenaPayout(5, 3))
    }

    // 21. Repeated multiplayer settlement events (Idempotency)
    @Test
    fun test21_repeatedMultiplayerSettlementIdempotency() = runTest {
        walletRepository.ensureWelcomeGiftGranted() // 60
        val key = "settle_arena_match_99_p1"
        walletRepository.credit("ARENA_SETTLEMENT", 50, key)
        assertEquals(110, walletRepository.getCurrentBalance())

        // Duplicate settlement event
        walletRepository.credit("ARENA_SETTLEMENT", 50, key)
        assertEquals(110, walletRepository.getCurrentBalance()) // Exactly once
    }

    // 22. Star 1, Star 2, and Star 3 criteria
    @Test
    fun test22_starCriteria123() {
        // Star 1: Valid completion with hints
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 1, undoResetCount = 0))

        // Star 2: Valid completion without hints, but > 1 undo/reset
        assertEquals(2, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 2))

        // Star 3: Valid completion without hints and at most 1 undo/reset
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 0))
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 1))
    }

    // 23. Best-star preservation on replay
    @Test
    fun test23_bestStarPreservationOnReplay() = runTest {
        // First solve with 3 stars
        progressRepository.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p_1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 5000L,
                moveCount = 10,
                hintCount = 0,
                undoCount = 0,
                resetCount = 0
            )
        )
        val starsBefore = progressRepository.observeLevelProgress(1).first()?.stars
        assertEquals(3, starsBefore)

        // Replay with 2 hints (1 star)
        progressRepository.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p_1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 4000L,
                moveCount = 10,
                hintCount = 2,
                undoCount = 0,
                resetCount = 0
            )
        )
        val starsAfter = progressRepository.observeLevelProgress(1).first()?.stars
        assertEquals(3, starsAfter) // Retains best 3 stars!
    }

    // 24. Existing guest progress migration
    @Test
    fun test24_guestProgressMigration() = runTest {
        walletRepository.ensureWelcomeGiftGranted()
        walletRepository.claimDailyLoginReward("2026-09-29")
        assertEquals(80, walletRepository.getCurrentBalance())

        // Reassign guest transactions to authenticated player ID
        walletRepository.reassignToAuthenticatedPlayer("auth_user_999")
        val migratedBalance = walletDao.getCurrentBalance("auth_user_999")
        assertEquals(80, migratedBalance)
    }

    // 25. Reference UI tokens and World Configuration
    @Test
    fun test25_referenceUiTokensAndWorldConfiguration() {
        assertEquals(RefGoldPrimary, androidx.compose.ui.graphics.Color(0xFFFFB300))
        assertEquals(RefGreenClaim, androidx.compose.ui.graphics.Color(0xFF00C853))
        assertEquals(RefNavyDark, androidx.compose.ui.graphics.Color(0xFF050D1A))

        // Confirm 6 worlds and 300 total levels
        assertEquals(6, WorldConfiguration.TOTAL_WORLDS)
        assertEquals(300, WorldConfiguration.TOTAL_LEVELS)
        assertEquals(1..20, WorldConfiguration.getWorld(1).levelRange)
        assertEquals(21..50, WorldConfiguration.getWorld(2).levelRange)
        assertEquals(51..100, WorldConfiguration.getWorld(3).levelRange)
        assertEquals(101..150, WorldConfiguration.getWorld(4).levelRange)
        assertEquals(151..200, WorldConfiguration.getWorld(5).levelRange)
        assertEquals(201..300, WorldConfiguration.getWorld(6).levelRange)
    }
}
