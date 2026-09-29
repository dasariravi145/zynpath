package com.zynpath.game.core.hint

/**
 * Clean architectural boundary for future rewarded ad hint integrations.
 *
 * Implements Prompt 14 Section 30:
 * - Prepares the boundary without fake completions or premature SDK coupling.
 * - Ads are never mandatory to complete puzzles.
 */
interface RewardedHintProvider {
    /** True if a rewarded video ad is loaded and ready to present. */
    val isRewardedAdAvailable: Boolean

    /**
     * Prompts presentation of a rewarded ad.
     * Returns true ONLY if the ad provider confirms successful completion.
     */
    suspend fun showRewardedAd(): Boolean
}

/** Default no-op provider used when ad SDK is not present. */
class NoOpRewardedHintProvider : RewardedHintProvider {
    override val isRewardedAdAvailable: Boolean = false
    override suspend fun showRewardedAd(): Boolean = false
}
