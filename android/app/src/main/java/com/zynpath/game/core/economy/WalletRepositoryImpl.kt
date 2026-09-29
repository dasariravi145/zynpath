package com.zynpath.game.core.economy

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.WalletDao
import com.zynpath.game.core.database.entity.WalletTransactionEntity
import com.zynpath.game.core.datastore.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao,
    private val preferencesRepository: PreferencesRepository,
    private val levelProgressDao: LevelProgressDao,
    private val dataStore: DataStore<Preferences>
) : WalletRepository {

    private val transactionMutex = Mutex()

    private object PreferencesKeys {
        val ACTIVE_PAID_LEVEL_ID = intPreferencesKey("active_paid_attempt_level_id")
        val ACTIVE_PAID_ATTEMPT_UUID = stringPreferencesKey("active_paid_attempt_uuid")
    }

    private suspend fun getActivePlayerId(): String {
        val prefs = preferencesRepository.userPreferencesFlow.firstOrNull()
        return prefs?.guestUuid?.ifBlank { "guest_player" } ?: "guest_player"
    }

    override val balanceFlow: Flow<Int> = preferencesRepository.userPreferencesFlow
        .map { it.guestUuid.ifBlank { "guest_player" } }
        .flatMapLatest { playerId ->
            walletDao.observeBalance(playerId).map { it ?: 0 }
        }

    override suspend fun getCurrentBalance(): Int {
        val playerId = getActivePlayerId()
        return walletDao.getCurrentBalance(playerId) ?: 0
    }

    override suspend fun canAfford(amount: Int): Boolean {
        require(amount >= 0) { "Amount cannot be negative" }
        return getCurrentBalance() >= amount
    }

    override suspend fun ensureWelcomeGiftGranted(): Boolean {
        val playerId = getActivePlayerId()
        val idempotencyKey = "welcome_$playerId"
        return transactionMutex.withLock {
            if (walletDao.hasTransaction(idempotencyKey)) {
                false
            } else {
                val current = walletDao.getCurrentBalance(playerId) ?: 0
                val newBalance = current + EconomyConfig.WELCOME_COINS
                val tx = WalletTransactionEntity(
                    transactionId = "tx_" + UUID.randomUUID().toString().replace("-", ""),
                    playerId = playerId,
                    type = "WELCOME_REWARD",
                    amount = EconomyConfig.WELCOME_COINS,
                    balanceAfter = newBalance,
                    idempotencyKey = idempotencyKey,
                    metadataJson = "{\"note\":\"One-time welcome gift\"}"
                )
                walletDao.insertTransaction(tx)
                true
            }
        }
    }

    override suspend fun debit(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String
    ): Result<Int> = transactionMutex.withLock {
        require(amount >= 0) { "Debit amount must be non-negative" }
        val playerId = getActivePlayerId()

        // Check if transaction already processed
        val existing = walletDao.getTransactionByIdempotencyKey(idempotencyKey)
        if (existing != null) {
            return@withLock Result.success(existing.balanceAfter)
        }

        val currentBalance = walletDao.getCurrentBalance(playerId) ?: 0
        if (currentBalance < amount) {
            return@withLock Result.failure(
                IllegalStateException("INSUFFICIENT_FUNDS: Current balance $currentBalance is less than required $amount")
            )
        }

        val newBalance = currentBalance - amount
        val tx = WalletTransactionEntity(
            transactionId = "tx_" + UUID.randomUUID().toString().replace("-", ""),
            playerId = playerId,
            type = type,
            amount = -amount,
            balanceAfter = newBalance,
            idempotencyKey = idempotencyKey,
            metadataJson = metadataJson
        )
        try {
            walletDao.insertTransaction(tx)
            Result.success(newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun credit(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String
    ): Result<Int> = transactionMutex.withLock {
        require(amount >= 0) { "Credit amount must be non-negative" }
        val playerId = getActivePlayerId()

        // Idempotency check: if already processed, return current balance
        val existing = walletDao.getTransactionByIdempotencyKey(idempotencyKey)
        if (existing != null) {
            val current = walletDao.getCurrentBalance(playerId) ?: existing.balanceAfter
            return@withLock Result.success(current)
        }

        val currentBalance = walletDao.getCurrentBalance(playerId) ?: 0
        val newBalance = currentBalance + amount
        val tx = WalletTransactionEntity(
            transactionId = "tx_" + UUID.randomUUID().toString().replace("-", ""),
            playerId = playerId,
            type = type,
            amount = amount,
            balanceAfter = newBalance,
            idempotencyKey = idempotencyKey,
            metadataJson = metadataJson
        )
        try {
            walletDao.insertTransaction(tx)
            Result.success(newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refund(
        type: String,
        amount: Int,
        idempotencyKey: String,
        metadataJson: String
    ): Result<Int> {
        return credit(
            type = type.ifBlank { "MATCH_REFUND" },
            amount = amount,
            idempotencyKey = idempotencyKey,
            metadataJson = metadataJson
        )
    }

    override suspend fun isTransactionProcessed(idempotencyKey: String): Boolean {
        return walletDao.hasTransaction(idempotencyKey)
    }

    override suspend fun reassignToAuthenticatedPlayer(newPlayerId: String) = transactionMutex.withLock {
        val currentGuestId = getActivePlayerId()
        if (currentGuestId != newPlayerId) {
            walletDao.reassignPlayerTransactions(currentGuestId, newPlayerId)
        }
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<WalletTransactionEntity>> {
        return preferencesRepository.userPreferencesFlow
            .map { it.guestUuid.ifBlank { "guest_player" } }
            .flatMapLatest { playerId ->
                walletDao.observeRecentTransactions(playerId, limit)
            }
    }

    // --- Solo Reward Run Lifecycle ---

    override suspend fun isLevelPaidAttemptActive(levelId: Int): Boolean {
        val prefs = dataStore.data.first()
        val activeLevel = prefs[PreferencesKeys.ACTIVE_PAID_LEVEL_ID] ?: 0
        return activeLevel == levelId
    }

    override suspend fun commitSoloRewardRunEntry(levelId: Int): Result<Boolean> {
        // Levels 1..3 are always free
        if (levelId <= EconomyConfig.SOLO_LEVELS_FREE_UNTIL) {
            return Result.success(true)
        }

        // If level already completed, no fee charged and no first-clear reward
        val isCompleted = levelProgressDao.getLevelProgress(levelId)?.isCompleted == true
        if (isCompleted) {
            return Result.success(true)
        }

        // If paid attempt already committed for this level, retry is free
        if (isLevelPaidAttemptActive(levelId)) {
            return Result.success(true)
        }

        // Deduct entry fee atomically
        val attemptUuid = UUID.randomUUID().toString()
        val debitResult = debit(
            type = "SOLO_ENTRY",
            amount = EconomyConfig.SOLO_REWARD_RUN_ENTRY_FEE,
            idempotencyKey = "entry_level_${levelId}_$attemptUuid",
            metadataJson = "{\"levelId\":$levelId,\"attemptUuid\":\"$attemptUuid\"}"
        )

        return if (debitResult.isSuccess) {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.ACTIVE_PAID_LEVEL_ID] = levelId
                prefs[PreferencesKeys.ACTIVE_PAID_ATTEMPT_UUID] = attemptUuid
            }
            Result.success(true)
        } else {
            Result.failure(debitResult.exceptionOrNull() ?: IllegalStateException("Payment failed"))
        }
    }

    override suspend fun clearActivePaidAttempt(levelId: Int) {
        dataStore.edit { prefs ->
            if (prefs[PreferencesKeys.ACTIVE_PAID_LEVEL_ID] == levelId) {
                prefs.remove(PreferencesKeys.ACTIVE_PAID_LEVEL_ID)
                prefs.remove(PreferencesKeys.ACTIVE_PAID_ATTEMPT_UUID)
            }
        }
    }

    override suspend fun isFirstClearRewardEligible(levelId: Int): Boolean {
        return !walletDao.hasTransaction("clear_level_$levelId")
    }

    override suspend fun grantSoloFirstClearReward(levelId: Int): Result<Int> {
        val key = "clear_level_$levelId"
        return credit(
            type = "SOLO_FIRST_CLEAR",
            amount = EconomyConfig.SOLO_FIRST_CLEAR_REWARD,
            idempotencyKey = key,
            metadataJson = "{\"levelId\":$levelId}"
        )
    }

    // --- Daily Login Rewards ---

    override suspend fun isDailyLoginClaimed(dateUtc: String): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("daily_login_${dateUtc}_$playerId")
    }

    override suspend fun isDailyLoginAdBonusClaimed(dateUtc: String): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("daily_ad_${dateUtc}_$playerId")
    }

    override suspend fun claimDailyLoginReward(dateUtc: String): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "daily_login_${dateUtc}_$playerId"
        return credit(
            type = "DAILY_LOGIN",
            amount = EconomyConfig.DAILY_LOGIN_BASE_REWARD,
            idempotencyKey = key,
            metadataJson = "{\"dateUtc\":\"$dateUtc\"}"
        )
    }

    override suspend fun claimDailyLoginAdBonus(dateUtc: String): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "daily_ad_${dateUtc}_$playerId"
        return credit(
            type = "DAILY_AD_BONUS",
            amount = EconomyConfig.DAILY_LOGIN_AD_BONUS,
            idempotencyKey = key,
            metadataJson = "{\"dateUtc\":\"$dateUtc\"}"
        )
    }

    // --- Coin Rewarded Ads ---

    override suspend fun getRemainingDailyCoinAds(dateUtc: String): Int {
        val playerId = getActivePlayerId()
        var used = 0
        for (i in 1..EconomyConfig.MAX_DAILY_COIN_ADS) {
            if (walletDao.hasTransaction("coin_ad_${dateUtc}_${i}_$playerId")) {
                used++
            }
        }
        return (EconomyConfig.MAX_DAILY_COIN_ADS - used).coerceAtLeast(0)
    }

    override suspend fun claimCoinAdReward(dateUtc: String): Result<Int> {
        val playerId = getActivePlayerId()
        var chosenSlot = 0
        for (i in 1..EconomyConfig.MAX_DAILY_COIN_ADS) {
            if (!walletDao.hasTransaction("coin_ad_${dateUtc}_${i}_$playerId")) {
                chosenSlot = i
                break
            }
        }
        if (chosenSlot == 0) {
            return Result.failure(IllegalStateException("DAILY_LIMIT_REACHED: Maximum 2 coin ads claimed for $dateUtc"))
        }

        val key = "coin_ad_${dateUtc}_${chosenSlot}_$playerId"
        return credit(
            type = "COIN_AD_REWARD",
            amount = EconomyConfig.COIN_AD_REWARD,
            idempotencyKey = key,
            metadataJson = "{\"dateUtc\":\"$dateUtc\",\"slot\":$chosenSlot}"
        )
    }

    // --- Daily Challenge ---

    override suspend fun isDailyChallengeClearClaimed(dateUtc: String): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("dc_clear_${dateUtc}_$playerId")
    }

    override suspend fun isDailyChallengeAdBonusClaimed(dateUtc: String): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("dc_ad_${dateUtc}_$playerId")
    }

    override suspend fun claimDailyChallengeReward(dateUtc: String): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "dc_clear_${dateUtc}_$playerId"
        return credit(
            type = "DAILY_CHALLENGE",
            amount = EconomyConfig.DAILY_CHALLENGE_FIRST_CLEAR_REWARD,
            idempotencyKey = key,
            metadataJson = "{\"dateUtc\":\"$dateUtc\"}"
        )
    }

    override suspend fun claimDailyChallengeAdBonus(dateUtc: String): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "dc_ad_${dateUtc}_$playerId"
        return credit(
            type = "DAILY_CHALLENGE_AD_BONUS",
            amount = EconomyConfig.DAILY_CHALLENGE_AD_BONUS,
            idempotencyKey = key,
            metadataJson = "{\"dateUtc\":\"$dateUtc\"}"
        )
    }

    // --- World Completion ---

    override suspend fun isWorldCompletionClaimed(worldId: Int): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("world_clear_${worldId}_$playerId")
    }

    override suspend fun isWorldCompletionAdBonusClaimed(worldId: Int): Boolean {
        val playerId = getActivePlayerId()
        return walletDao.hasTransaction("world_ad_${worldId}_$playerId")
    }

    override suspend fun claimWorldCompletionReward(worldId: Int): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "world_clear_${worldId}_$playerId"
        return credit(
            type = "WORLD_COMPLETION",
            amount = EconomyConfig.WORLD_COMPLETION_BASE_REWARD,
            idempotencyKey = key,
            metadataJson = "{\"worldId\":$worldId}"
        )
    }

    override suspend fun claimWorldCompletionAdBonus(worldId: Int): Result<Int> {
        val playerId = getActivePlayerId()
        val key = "world_ad_${worldId}_$playerId"
        return credit(
            type = "WORLD_AD_BONUS",
            amount = EconomyConfig.WORLD_COMPLETION_AD_BONUS,
            idempotencyKey = key,
            metadataJson = "{\"worldId\":$worldId}"
        )
    }
}
