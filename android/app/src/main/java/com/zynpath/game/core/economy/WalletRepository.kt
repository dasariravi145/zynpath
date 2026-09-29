package com.zynpath.game.core.economy

import com.zynpath.game.core.database.entity.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    /**
     * Reactive stream of current available coin balance.
     */
    val balanceFlow: Flow<Int>

    /**
     * Synchronously fetches the current coin balance.
     */
    suspend fun getCurrentBalance(): Int

    /**
     * Checks if the player has at least [amount] coins.
     */
    suspend fun canAfford(amount: Int): Boolean

    /**
     * Ensures one-time welcome gift of +60 coins is granted upon first launch.
     */
    suspend fun ensureWelcomeGiftGranted(): Boolean

    /**
     * Atomically debits [amount] coins if balance is sufficient.
     * Fails with IllegalStateException if balance < amount, or if idempotencyKey already exists.
     */
    suspend fun debit(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String = "{}"
    ): Result<Int>

    /**
     * Idempotently credits [amount] coins. If [idempotencyKey] is already recorded,
     * returns current balance without crediting again.
     */
    suspend fun credit(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String = "{}"
    ): Result<Int>

    /**
     * Refunds previously debited coins (e.g. cancelled/drawn match).
     */
    suspend fun refund(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String = "{}"
    ): Result<Int>

    /**
     * Checks if an idempotency key was already recorded.
     */
    suspend fun isTransactionProcessed(idempotencyKey: String): Boolean

    /**
     * Reassigns all transactions from guest UUID to authenticated player ID upon sign-in.
     */
    suspend fun reassignToAuthenticatedPlayer(newPlayerId: String)

    /**
     * Observes recent ledger transactions for the Coin Details / Wallet sheet.
     */
    fun observeRecentTransactions(limit: Int = 20): Flow<List<WalletTransactionEntity>>

    // --- Solo Reward Run Lifecycle ---
    suspend fun isLevelPaidAttemptActive(levelId: Int): Boolean
    suspend fun commitSoloRewardRunEntry(levelId: Int): Result<Boolean>
    suspend fun clearActivePaidAttempt(levelId: Int)
    suspend fun isFirstClearRewardEligible(levelId: Int): Boolean
    suspend fun grantSoloFirstClearReward(levelId: Int): Result<Int>

    // --- Daily Login Rewards ---
    suspend fun isDailyLoginClaimed(dateUtc: String): Boolean
    suspend fun isDailyLoginAdBonusClaimed(dateUtc: String): Boolean
    suspend fun claimDailyLoginReward(dateUtc: String): Result<Int>
    suspend fun claimDailyLoginAdBonus(dateUtc: String): Result<Int>

    // --- Coin Rewarded Ads ---
    suspend fun getRemainingDailyCoinAds(dateUtc: String): Int
    suspend fun claimCoinAdReward(dateUtc: String): Result<Int>

    // --- Daily Challenge ---
    suspend fun isDailyChallengeClearClaimed(dateUtc: String): Boolean
    suspend fun isDailyChallengeAdBonusClaimed(dateUtc: String): Boolean
    suspend fun claimDailyChallengeReward(dateUtc: String): Result<Int>
    suspend fun claimDailyChallengeAdBonus(dateUtc: String): Result<Int>

    // --- World Completion ---
    suspend fun isWorldCompletionClaimed(worldId: Int): Boolean
    suspend fun isWorldCompletionAdBonusClaimed(worldId: Int): Boolean
    suspend fun claimWorldCompletionReward(worldId: Int): Result<Int>
    suspend fun claimWorldCompletionAdBonus(worldId: Int): Result<Int>
}
