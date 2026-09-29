package com.zynpath.game.core.economy

/**
 * Authoritative single source of truth for Zynpath virtual currency and rewards economy.
 * All economy parameters are centralized here and configurable, not hardcoded in UI or ViewModels.
 */
object EconomyConfig {

    // --- 1. Welcome Gift ---
    const val WELCOME_COINS = 60

    // --- 2. Solo Level Economy ---
    const val SOLO_LEVELS_FREE_UNTIL = 3 // Levels 1..3 free entry
    const val SOLO_FIRST_CLEAR_REWARD = 5 // +5 coins on first valid clear
    const val SOLO_REWARD_RUN_ENTRY_FEE = 3 // 3-coin entry on Level 4+ Reward Run
    // Net profit on successful first-time Reward Run = 5 - 3 = +2 coins

    // --- 3. Daily Login Rewards ---
    const val DAILY_LOGIN_BASE_REWARD = 20
    const val DAILY_LOGIN_AD_BONUS = 20

    // --- 4. Rewarded Video Ad Coin Reward ---
    const val COIN_AD_REWARD = 15
    const val MAX_DAILY_COIN_ADS = 2

    // --- 5. Daily Challenge ---
    const val DAILY_CHALLENGE_FIRST_CLEAR_REWARD = 15
    const val DAILY_CHALLENGE_AD_BONUS = 15

    // --- 6. World Completion ---
    const val WORLD_COMPLETION_BASE_REWARD = 25
    const val WORLD_COMPLETION_AD_BONUS = 25

    // --- 7. Interstitial Ad Policy ---
    const val INTERSTITIAL_SOLO_FREQUENCY = 3 // Every 3rd distinct first-time solo level completion
    const val INTERSTITIAL_MIN_INTERVAL_SECONDS = 120L // 120-second cooldown between interstitials
    const val INTERSTITIAL_MAX_PER_SESSION = 2
    const val INTERSTITIAL_MAX_PER_DAY = 3

    // --- 8. Quick Duel (1v1) Economy ---
    const val QUICK_DUEL_ENTRY_FEE = 15
    const val QUICK_DUEL_WINNER_PAYOUT = 25 // Net +10 for winner, net -15 for loser

    // --- 9. Friends Arena (1-5 Players) Economy ---
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
