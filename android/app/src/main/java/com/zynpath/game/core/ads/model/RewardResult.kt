package com.zynpath.game.core.ads.model

/**
 * Result outcome of attempting to show a rewarded ad.
 *
 * Implements Prompt 29 Sections 11, 24, 25:
 * - Ad dismissal alone never produces Success.
 * - Success is only produced when the SDK reports reward completion.
 */
sealed interface RewardResult {
    data class Success(
        val rewardEventId: String,
        val amount: Int,
        val rewardType: String = "SOLO_HINT"
    ) : RewardResult

    data object DismissedWithoutReward : RewardResult
    data class Failed(val error: String) : RewardResult
    data object Unavailable : RewardResult
    data object LimitReached : RewardResult
    data object BlockedByPolicy : RewardResult
}
