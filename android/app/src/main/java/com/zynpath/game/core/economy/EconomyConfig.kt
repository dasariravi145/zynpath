package com.zynpath.game.core.economy

/**
 * Authoritative single source of truth for Zynpath virtual currency and rewards economy.
 * All economy parameters are centralized here and configurable, not hardcoded in UI or ViewModels.
 *
 * Implements Prompt Final Solo Economy:
 * - Genuine new player welcome reward: +300
 * - First successful completion of a distinct Solo level: +100
 * - First complete clearance of a World: +500
 * - Daily login claim: +50
 * - One confirmed optional rewarded coin video: +150 (max 3/day = 450 max)
 * - First two hints in each Solo level: Free
 * - Extra hint after two free hints: -300
 * - Optional valid Shuffle: -250
 * - Undo: Free
 * - Optional Timed Challenge entry: -200 (payout +300 total, net +100)
 * - Special avatar/frame: starting at 1,000
 * - Premium path effect/theme: starting at 2,000
 * - Every 3rd distinct first-time solo level completion triggers interstitial opportunity
 */
object EconomyConfig {

    // --- 1. Welcome Reward ---
    const val WELCOME_COINS = 300

    // --- 2. Solo Level Economy ---
    const val SOLO_FIRST_CLEAR_REWARD = 100 // +100 coins on first valid clear of a distinct level
    const val WORLD_FIRST_CLEAR_REWARD = 500 // +500 coins on first complete clearance of a World
    const val WORLD_COMPLETION_BASE_REWARD = WORLD_FIRST_CLEAR_REWARD
    const val WORLD_COMPLETION_AD_BONUS = 0
    const val SOLO_LEVELS_FREE_UNTIL = 300 // Normal Solo level entry is free
    const val SOLO_REWARD_RUN_ENTRY_FEE = 0 // Normal Solo has no entry fee

    // --- 3. Hints & In-Level Actions ---
    const val FREE_HINTS_PER_SOLO_LEVEL = 2 // First 2 hints per level are free
    const val EXTRA_HINT_COIN_PRICE = 300 // Extra hint after 2 free hints costs 300 coins
    const val SHUFFLE_COIN_PRICE = 250 // Optional valid Shuffle (if supported): 250 coins

    // --- 4. Daily Login Rewards ---
    const val DAILY_LOGIN_CLAIM_REWARD = 50 // Explicit daily claim: +50 coins
    const val DAILY_LOGIN_BASE_REWARD = DAILY_LOGIN_CLAIM_REWARD
    const val DAILY_LOGIN_AD_BONUS = 50 // Optional daily ad bonus: +50 coins

    // --- 5. Rewarded Video Ad Coin Reward ---
    const val REWARDED_COIN_AD_REWARD = 150 // One confirmed optional rewarded coin video: +150 coins
    const val COIN_AD_REWARD = REWARDED_COIN_AD_REWARD
    const val MAX_DAILY_COIN_ADS = 3 // Maximum 3 confirmed coin-video claims per day (450 coins max)

    // --- 6. Interstitial Ad Policy ---
    const val INTERSTITIAL_SOLO_FREQUENCY = 3 // Every 3rd distinct first-time solo level completion
    const val INTERSTITIAL_MIN_INTERVAL_SECONDS = 60L
    const val INTERSTITIAL_MAX_PER_SESSION = 5
    const val INTERSTITIAL_MAX_PER_DAY = 10

    // --- 7. Optional Timed Challenge ---
    const val TIMED_CHALLENGE_ENTRY_FEE = 200 // Entry cost: 200 coins
    const val TIMED_CHALLENGE_TOTAL_PAYOUT = 300 // Successful payout: 300 coins total (net +100)

    // --- 8. Optional Cosmetics ---
    const val COSMETIC_AVATAR_FRAME_MIN_PRICE = 1000 // Special avatar/frame starting at 1,000 coins
    const val COSMETIC_THEME_EFFECT_MIN_PRICE = 2000 // Premium path effect/theme starting at 2,000 coins

    // --- 9. Daily Challenge ---
    const val DAILY_CHALLENGE_FIRST_CLEAR_REWARD = 50
    const val DAILY_CHALLENGE_AD_BONUS = 50

    // --- 10. Quick Duel (1v1) Economy ---
    const val QUICK_DUEL_ENTRY_FEE = 15
    const val QUICK_DUEL_WINNER_PAYOUT = 25

    // --- 11. Friends Arena (1-5 Players) Economy ---
    const val ARENA_ENTRY_FEE = 15
    const val ARENA_MIN_PLAYERS = 2
    const val ARENA_MAX_PLAYERS = 5

    /**
     * Tiered finish-order payouts based on participating player count (2..5).
     * [0] = 1st place, [1] = 2nd place, etc.
     */
    val ARENA_PAYOUTS: Map<Int, List<Int>> = mapOf(
        2 to listOf(25, 0),
        3 to listOf(30, 10, 0),
        4 to listOf(40, 15, 0, 0),
        5 to listOf(50, 20, 0, 0, 0)
    )

    fun getArenaPayout(playerCount: Int, finishOrder: Int): Int {
        val payouts = ARENA_PAYOUTS[playerCount.coerceIn(2, 5)] ?: return 0
        val index = finishOrder - 1
        return if (index in payouts.indices) payouts[index] else 0
    }
}

