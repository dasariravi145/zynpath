package com.zynpath.game.core.premium

import com.zynpath.game.core.premium.model.PremiumEntitlement
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import com.zynpath.game.core.puzzle.hint.GameMode

/**
 * Centralized, authoritative policy governing feature access and competitive fairness boundaries.
 *
 * Implements Prompt 26:
 * - Section 5: Free players retain full core gameplay, daily challenges, and multiplayer modes.
 * - Section 6 & 42: Competitive Fairness — Premium NEVER grants hints or advantages in competitive modes.
 * - Section 39: Reusable entitlement interface avoiding scattered Boolean checks.
 * - Section 41: Solo hint integration (unlimited hints for premium, limited for free).
 */
object FeatureAccessPolicy {

    /**
     * Checks if a specific premium feature is unlocked for the user given their entitlement.
     *
     * @param featureKey The feature to evaluate.
     * @param gameMode The context game mode (if applicable).
     * @param entitlement The authoritative entitlement for the current player.
     * @return True if access is permitted, false otherwise.
     */
    fun isFeatureUnlocked(
        featureKey: PremiumFeatureKey,
        gameMode: GameMode? = null,
        entitlement: PremiumEntitlement
    ): Boolean {
        // Core game is unconditionally free and accessible for all players
        if (!entitlement.isPremiumActive) {
            return false
        }

        return when (featureKey) {
            PremiumFeatureKey.UNLIMITED_SOLO_HINTS -> {
                // Section 6 & 42: Competitive Hint Restriction
                // Hints are STRICTLY DISABLED in all competitive modes regardless of subscription status!
                if (gameMode != null && !gameMode.allowsHints) {
                    false
                } else {
                    true
                }
            }

            PremiumFeatureKey.AD_FREE -> true
            PremiumFeatureKey.PREMIUM_SOLO_PACKS -> true
            PremiumFeatureKey.PREMIUM_THEMES -> true
            PremiumFeatureKey.PREMIUM_PATH_EFFECTS -> true
            PremiumFeatureKey.PREMIUM_AVATAR_FRAMES -> true
            PremiumFeatureKey.ADVANCED_PERSONAL_STATS -> true
        }
    }

    /**
     * Authoritative hint evaluation logic combining game mode rules, premium status, and free allowances.
     */
    fun canConsumeHint(
        gameMode: GameMode,
        entitlement: PremiumEntitlement,
        freeHintsRemaining: Int
    ): Boolean {
        // Hard competitive gating: Quick Duel, Friend Duel, Mini League, and Daily Challenge reject hints unconditionally
        if (!gameMode.allowsHints) {
            return false
        }

        // In Solo mode: unlimited for active premium subscribers
        if (entitlement.isPremiumActive) {
            return true
        }

        // Free players can consume if they have remaining hint quota
        return freeHintsRemaining > 0
    }
}
