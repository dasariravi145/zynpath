package com.zynpath.game.core.puzzle.experience.hint

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.zynpath.game.core.error.ErrorClassifier
import com.zynpath.game.core.error.ZynpathDiagnostics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative contract for per-level hint economy and persistence.
 *
 * Implements Prompt 25 Task 6 & Task 7:
 * - Exactly two free hints per level.
 * - Confirmed rewarded-ad completion yields +1 hint credit for that level.
 * - Prevents duplicate reward consumption and negative balances.
 */
interface LevelHintRepository {
    fun observeLevelHintState(levelId: Int): Flow<LevelHintState>
    suspend fun getLevelHintState(levelId: Int): LevelHintState
    suspend fun consumeHint(levelId: Int): LevelHintConsumptionResult
    suspend fun recordConfirmedReward(levelId: Int, rewardClaimId: String): Boolean
    suspend fun restoreSessionHints(levelId: Int, hintsUsedInSession: Int): LevelHintState
}

@Singleton
class LevelHintRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LevelHintRepository {

    private fun freeUsedKey(levelId: Int) = intPreferencesKey("lvl_hint_free_used_$levelId")
    private fun rewardEarnedKey(levelId: Int) = intPreferencesKey("lvl_hint_reward_earned_$levelId")
    private fun rewardConsumedKey(levelId: Int) = intPreferencesKey("lvl_hint_reward_consumed_$levelId")
    private val claimedRewardIdsKey = stringSetPreferencesKey("claimed_hint_reward_ids")

    override fun observeLevelHintState(levelId: Int): Flow<LevelHintState> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                mapPreferencesToState(levelId, prefs)
            }
    }

    override suspend fun getLevelHintState(levelId: Int): LevelHintState {
        val prefs = dataStore.data.first()
        return mapPreferencesToState(levelId, prefs)
    }

    override suspend fun consumeHint(levelId: Int): LevelHintConsumptionResult {
        var result: LevelHintConsumptionResult = LevelHintConsumptionResult.RequiresRewardedAd(LevelHintState.initial(levelId))

        dataStore.edit { prefs ->
            val currentState = mapPreferencesToState(levelId, prefs)
            when {
                currentState.freeHintsRemaining > 0 -> {
                    val updatedUsed = (currentState.freeHintsUsed + 1).coerceAtMost(LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
                    prefs[freeUsedKey(levelId)] = updatedUsed
                    val newState = currentState.copy(freeHintsUsed = updatedUsed)
                    result = LevelHintConsumptionResult.ConsumedFree(newState, newState.freeHintsRemaining)
                }
                currentState.rewardedHintsRemaining > 0 -> {
                    val updatedConsumed = (currentState.rewardedHintsConsumed + 1).coerceAtMost(currentState.rewardedHintsEarned)
                    prefs[rewardConsumedKey(levelId)] = updatedConsumed
                    val newState = currentState.copy(rewardedHintsConsumed = updatedConsumed)
                    result = LevelHintConsumptionResult.ConsumedRewarded(newState, newState.rewardedHintsRemaining)
                }
                else -> {
                    result = LevelHintConsumptionResult.RequiresRewardedAd(currentState)
                }
            }
        }

        return result
    }

    override suspend fun recordConfirmedReward(levelId: Int, rewardClaimId: String): Boolean {
        if (rewardClaimId.isBlank()) return false
        var awarded = false

        dataStore.edit { prefs ->
            val claimedIds = prefs[claimedRewardIdsKey] ?: emptySet()
            if (rewardClaimId in claimedIds) {
                // Idempotent rejection: reward was already credited
                awarded = false
                return@edit
            }

            val currentEarned = (prefs[rewardEarnedKey(levelId)] ?: 0).coerceAtLeast(0)
            prefs[rewardEarnedKey(levelId)] = currentEarned + 1
            prefs[claimedRewardIdsKey] = claimedIds + rewardClaimId
            awarded = true
        }

        return awarded
    }

    override suspend fun restoreSessionHints(levelId: Int, hintsUsedInSession: Int): LevelHintState {
        var restoredState = LevelHintState.initial(levelId)

        dataStore.edit { prefs ->
            val current = mapPreferencesToState(levelId, prefs)
            // Ensure persisted used count accounts for restored in-flight session
            val adjustedFreeUsed = minOf(hintsUsedInSession, LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
            val adjustedRewardUsed = maxOf(0, hintsUsedInSession - LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)

            val finalFreeUsed = maxOf(current.freeHintsUsed, adjustedFreeUsed)
            val finalRewardUsed = maxOf(current.rewardedHintsConsumed, adjustedRewardUsed)
            val finalRewardEarned = maxOf(current.rewardedHintsEarned, finalRewardUsed)

            prefs[freeUsedKey(levelId)] = finalFreeUsed
            prefs[rewardEarnedKey(levelId)] = finalRewardEarned
            prefs[rewardConsumedKey(levelId)] = finalRewardUsed

            restoredState = LevelHintState(
                levelId = levelId,
                freeHintsTotal = LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL,
                freeHintsUsed = finalFreeUsed,
                rewardedHintsEarned = finalRewardEarned,
                rewardedHintsConsumed = finalRewardUsed
            )
        }

        return restoredState
    }

    private fun mapPreferencesToState(levelId: Int, prefs: Preferences): LevelHintState {
        val freeUsed = (prefs[freeUsedKey(levelId)] ?: 0).coerceIn(0, LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
        val rewardEarned = (prefs[rewardEarnedKey(levelId)] ?: 0).coerceAtLeast(0)
        val rewardConsumed = (prefs[rewardConsumedKey(levelId)] ?: 0).coerceIn(0, rewardEarned)

        return LevelHintState(
            levelId = levelId,
            freeHintsTotal = LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL,
            freeHintsUsed = freeUsed,
            rewardedHintsEarned = rewardEarned,
            rewardedHintsConsumed = rewardConsumed
        )
    }
}
