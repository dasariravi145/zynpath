package com.zynpath.game.core.hint

import com.zynpath.game.core.datastore.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for managing hint allowances, consumption, and entitlement states.
 *
 * Implements Prompt 14 Sections 25–27:
 * - Persists hint usage locally in DataStore through [PreferencesRepository].
 * - Survives application restart.
 * - Enforces atomic allowance checking and deduction.
 */
interface HintUsageRepository {
    val remainingHintsFlow: Flow<Int>
    val isPremiumFlow: Flow<Boolean>
    val rewardedCreditsFlow: Flow<Int>
    val freeAllowanceFlow: Flow<Int>

    suspend fun getRemainingHints(): Int
    suspend fun isPremium(): Boolean
    suspend fun canConsumeHint(): Boolean
    suspend fun consumeHint(): Boolean
    suspend fun addFreeHints(count: Int)
    suspend fun addRewardedHintCredit(count: Int = 1): Int
    suspend fun setPremium(isPremium: Boolean)
}

@Singleton
class HintUsageRepositoryImpl @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : HintUsageRepository {

    override val remainingHintsFlow: Flow<Int> = preferencesRepository.userPreferencesFlow.map { prefs ->
        HintUsagePolicy.getRemainingHints(prefs)
    }

    override val isPremiumFlow: Flow<Boolean> = preferencesRepository.userPreferencesFlow.map { prefs ->
        HintUsagePolicy.isUnlimited(prefs)
    }

    override val rewardedCreditsFlow: Flow<Int> = preferencesRepository.userPreferencesFlow.map { prefs ->
        prefs.rewardedHintCredits
    }

    override val freeAllowanceFlow: Flow<Int> = preferencesRepository.userPreferencesFlow.map { prefs ->
        prefs.freeHintsRemaining
    }

    override suspend fun getRemainingHints(): Int {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        return HintUsagePolicy.getRemainingHints(prefs)
    }

    override suspend fun isPremium(): Boolean {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        return HintUsagePolicy.isUnlimited(prefs)
    }

    override suspend fun canConsumeHint(): Boolean {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        return HintUsagePolicy.canConsume(prefs)
    }

    override suspend fun consumeHint(): Boolean {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        if (HintUsagePolicy.isUnlimited(prefs)) {
            return true
        }

        // Section 19: Consume standard free allowance first, then earned rewarded credits
        if (prefs.freeHintsRemaining > 0) {
            preferencesRepository.setFreeHintsRemaining(prefs.freeHintsRemaining - 1)
            return true
        }

        if (prefs.rewardedHintCredits > 0) {
            preferencesRepository.setRewardedHintCredits(prefs.rewardedHintCredits - 1)
            return true
        }

        return false
    }

    override suspend fun addFreeHints(count: Int) {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        val newTotal = (prefs.freeHintsRemaining + count).coerceAtLeast(0)
        preferencesRepository.setFreeHintsRemaining(newTotal)
    }

    override suspend fun addRewardedHintCredit(count: Int): Int {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        val currentCredits = prefs.rewardedHintCredits
        val newCredits = (currentCredits + count).coerceIn(0, HintUsagePolicy.MAX_STORED_REWARD_CREDITS)
        preferencesRepository.setRewardedHintCredits(newCredits)
        return HintUsagePolicy.getRemainingHints(prefs.copy(rewardedHintCredits = newCredits))
    }

    override suspend fun setPremium(isPremium: Boolean) {
        preferencesRepository.setPremium(isPremium)
    }
}
