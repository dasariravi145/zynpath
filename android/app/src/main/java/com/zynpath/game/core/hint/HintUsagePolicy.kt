package com.zynpath.game.core.hint

import com.zynpath.game.core.datastore.UserPreferences

/**
 * Authoritative policy governing Solo hint allowances and entitlement boundaries.
 *
 * Implements Prompt 14 Sections 22 & 23:
 * - Free players have access to a configurable free hint allowance (default 3).
 * - Verified Premium users have unlimited Solo hints.
 * - Undo and Reset remain unconditionally free for all players.
 */
object HintUsagePolicy {

    /** Default free hint allowance granted to new players. */
    const val DEFAULT_FREE_HINT_ALLOWANCE = 3

    /** Maximum stored bonus hint credits earned from rewarded ads. */
    const val MAX_STORED_REWARD_CREDITS = 10

    /** Maximum rewarded ads that can be completed per day. */
    const val MAX_DAILY_REWARDED_ADS = 5

    /**
     * Checks if the user has unlimited Solo hint entitlements.
     */
    fun isUnlimited(preferences: UserPreferences): Boolean {
        return preferences.isPremium
    }

    /**
     * Returns the remaining usable hints (standard allowance + earned rewarded credits).
     * Returns [Int.MAX_VALUE] for premium players.
     */
    fun getRemainingHints(preferences: UserPreferences): Int {
        return if (isUnlimited(preferences)) {
            Int.MAX_VALUE
        } else {
            (preferences.freeHintsRemaining.coerceAtLeast(0) + preferences.rewardedHintCredits.coerceAtLeast(0))
        }
    }

    /**
     * True if the user is eligible to request or consume a hint.
     */
    fun canConsume(preferences: UserPreferences): Boolean {
        return isUnlimited(preferences) || getRemainingHints(preferences) > 0
    }

    /**
     * True if the user is eligible to watch a rewarded ad to earn an additional hint.
     * Premium users cannot earn credits since they already possess unlimited hints.
     */
    fun canEarnRewardedHint(preferences: UserPreferences): Boolean {
        if (isUnlimited(preferences)) return false
        return preferences.rewardedHintCredits < MAX_STORED_REWARD_CREDITS
    }
}
