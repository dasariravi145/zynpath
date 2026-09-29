package com.zynpath.game.core.puzzle.experience.hint

/**
 * Immutable per-level hint state.
 *
 * Implements Prompt 25 Task 6 & Task 7:
 * - Each level provides exactly two free hints.
 * - After both free hints are consumed, additional hints require an optional confirmed rewarded video ad.
 * - Prevents negative balances and duplicate reward consumption.
 */
data class LevelHintState(
    val levelId: Int,
    val freeHintsTotal: Int = LOCKED_FREE_HINTS_PER_LEVEL,
    val freeHintsUsed: Int = 0,
    val rewardedHintsEarned: Int = 0,
    val rewardedHintsConsumed: Int = 0
) {
    init {
        require(levelId in 1..300) { "levelId must be in 1..300 (got $levelId)" }
        require(freeHintsTotal == LOCKED_FREE_HINTS_PER_LEVEL) {
            "Level hint economy locks exactly $LOCKED_FREE_HINTS_PER_LEVEL free hints per level"
        }
    }

    /** Remaining free hints for this level, strictly non-negative. */
    val freeHintsRemaining: Int
        get() = (freeHintsTotal - freeHintsUsed).coerceAtLeast(0)

    /** Remaining confirmed rewarded hint credits for this level, strictly non-negative. */
    val rewardedHintsRemaining: Int
        get() = (rewardedHintsEarned - rewardedHintsConsumed).coerceAtLeast(0)

    /** Total currently consumable hints (free + rewarded). */
    val totalAvailableHints: Int
        get() = freeHintsRemaining + rewardedHintsRemaining

    /** True if player can immediately consume a hint without watching an ad. */
    val canConsumeHint: Boolean
        get() = totalAvailableHints > 0

    /** True if player has exhausted free hints and must watch a rewarded ad to get another hint. */
    val requiresRewardedAd: Boolean
        get() = freeHintsRemaining == 0 && rewardedHintsRemaining == 0

    /** True if the next consumed hint will be drawn from the free allowance. */
    val isNextHintFree: Boolean
        get() = freeHintsRemaining > 0

    companion object {
        const val LOCKED_FREE_HINTS_PER_LEVEL = 2

        fun initial(levelId: Int): LevelHintState = LevelHintState(
            levelId = levelId,
            freeHintsTotal = LOCKED_FREE_HINTS_PER_LEVEL,
            freeHintsUsed = 0,
            rewardedHintsEarned = 0,
            rewardedHintsConsumed = 0
        )
    }
}

/**
 * Outcome of attempting to consume a hint on a specific level.
 */
sealed interface LevelHintConsumptionResult {
    data class ConsumedFree(val state: LevelHintState, val remainingFree: Int) : LevelHintConsumptionResult
    data class ConsumedRewarded(val state: LevelHintState, val remainingRewarded: Int) : LevelHintConsumptionResult
    data class RequiresRewardedAd(val state: LevelHintState) : LevelHintConsumptionResult
}
