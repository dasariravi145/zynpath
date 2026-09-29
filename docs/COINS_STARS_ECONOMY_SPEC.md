# Zynpath Coin Economy & Star Rewards Specification
**Project Root**: `D:\Zynpath`  
**Date**: September 29, 2026  
**Status**: Authoritative Economy Specification (Phase B)

---

## 1. Principles & Boundaries

1. **Virtual Currency Only**: Coins are strictly spendable in-game virtual tokens. They cannot be converted to fiat currency, cashed out, or transferred between players. There is no real-money coin purchasing in this release.
2. **Non-Negative Balance**: A player's coin balance can never drop below zero (`balance >= 0`).
3. **Core Puzzle Accessibility**: Core puzzle gameplay (Free Practice, level progression, and star mastery) must remain 100% accessible even when a player's coin balance is zero.
4. **Stars as Permanent Mastery**: Stars represent permanent skill milestones on solo levels (maximum 300 levels × 3 stars = 900 stars). Stars can never be spent, deducted, or purchased with coins or ads.

---

## 2. Centralized Configurable Constants

All economy parameters are centralized in `com.zynpath.game.core.economy.EconomyConfig`:

```kotlin
package com.zynpath.game.core.economy

object EconomyConfig {
    // Welcome Gift
    const val WELCOME_COINS = 60

    // Solo Puzzle Economy
    const val SOLO_LEVELS_FREE_UNTIL = 3 // Levels 1..3 free entry
    const val SOLO_FIRST_CLEAR_REWARD = 5
    const val SOLO_REWARD_RUN_ENTRY_FEE = 3 // Level 4+ paid attempt
    // Net profit on successful first-clear Reward Run = 5 - 3 = +2 coins

    // Daily Login Rewards
    const val DAILY_LOGIN_BASE_REWARD = 20
    const val DAILY_LOGIN_AD_BONUS = 20

    // Rewarded Video Ad Coin Reward
    const val COIN_AD_REWARD = 15
    const val MAX_DAILY_COIN_ADS = 2

    // Daily Challenge
    const val DAILY_CHALLENGE_FIRST_CLEAR_REWARD = 15
    const val DAILY_CHALLENGE_AD_BONUS = 15

    // World Completion
    const val WORLD_COMPLETION_BASE_REWARD = 25
    const val WORLD_COMPLETION_AD_BONUS = 25

    // Interstitial Policy
    const val INTERSTITIAL_SOLO_FREQUENCY = 3 // Every 3rd distinct first-time solo level
    const val INTERSTITIAL_MIN_INTERVAL_SECONDS = 120L // 120-second cooldown
    const val INTERSTITIAL_MAX_PER_SESSION = 2
    const val INTERSTITIAL_MAX_PER_DAY = 3

    // Multiplayer Economy
    const val QUICK_DUEL_ENTRY_FEE = 15
    const val QUICK_DUEL_WINNER_PAYOUT = 25 // Net +10 for winner, net -15 for loser

    const val ARENA_ENTRY_FEE = 15
    const val ARENA_MIN_PLAYERS = 2
    const val ARENA_MAX_PLAYERS = 5

    // Friends Arena Tiered Payouts (by participating player count)
    val ARENA_PAYOUTS: Map<Int, List<Int>> = mapOf(
        2 to listOf(25, 0),          // 1st: 25, 2nd: 0
        3 to listOf(30, 10, 0),      // 1st: 30, 2nd: 10, 3rd: 0
        4 to listOf(40, 15, 0, 0),   // 1st: 40, 2nd: 15, 3rd: 0, 4th: 0
        5 to listOf(50, 20, 0, 0, 0) // 1st: 50, 2nd: 20, 3rd: 0, 4th: 0, 5th: 0
    )
}
```

---

## 3. Solo Mode: Free Practice vs Reward Run Lifecycle

For **Levels 1, 2, and 3**:
- Entry is always free (`0` coins).
- First valid completion grants `+5` coins (`SOLO_FIRST_CLEAR`).
- Subsequent completions grant `0` coins.

For **Level 4 Onward**:
1. Before starting or entering the puzzle, player selects:
   - **Free Practice**: Entry cost = `0` coins. First-clear payout = `0` coins. Full star rating and catalog level progression are still earned.
   - **Reward Run**: Entry cost = `3` coins. First-clear payout = `+5` coins (net `+2` coins).
2. **Atomic Commitment**: Entry fee is deducted when the paid run is committed.
3. **Failure & Retry Safety**: If the player resets or fails, NO additional fee is charged. Retrying the same paid attempt is completely free.
4. **App Restart Safety**: The active paid attempt is stored persistently (`active_paid_attempt_level`). If the app crashes or restarts, the paid attempt remains active and free to retry.
5. **No Duplicate Reward**: Once a level is completed, its first-clear reward is marked claimed in the database. Replaying a completed level in Reward Run will neither deduct entry nor pay first-clear coins.

---

## 4. Star Rating Rules (Phase C)

Solo levels offer up to 3 stars evaluated strictly by the authoritative validator:
- **Star 1**: Valid puzzle completion (Hamiltonian single path covering all unblocked cells in ascending sequence).
- **Star 2**: Valid completion with `hintCount == 0` (zero hints used).
- **Star 3**: Valid completion with `hintCount == 0` AND `undoCount + resetCount <= 1` (at most one Undo or Reset action).

**Persistence Invariants**:
- Stored per stable level ID (1..300) in `level_progress.stars`.
- Monotonically non-decreasing: Replaying a level updates `stars = max(existing.stars, newStars)`.
- Never downgraded or deducted for retries, failed attempts, multiplayer losses, or spending coins.
- Total stars on Home HUD = sum of saved best stars across all 300 levels (maximum 900).

---

## 5. World Progression & Boundaries (Phase D)

The 6 Worlds are strictly partitioned:
- World 1: Levels 1–20 (End: Level 20)
- World 2: Levels 21–50 (End: Level 50)
- World 3: Levels 51–100 (End: Level 100)
- World 4: Levels 101–150 (End: Level 150)
- World 5: Levels 151–200 (End: Level 200)
- World 6: Levels 201–300 (End: Level 300)

**Boundary Navigation Flow**:
- World 1 Level 1 -> Action: "NEXT LEVEL" -> World 1 Level 2
- World 1 Level 19 -> Action: "NEXT LEVEL" -> World 1 Level 20
- World 1 Level 20 completed:
  - If World 1 is fully completed -> Action: "NEXT WORLD" -> Opens World 2 Level 21
  - Triggers first-time World Completion reward (+25 coins, optional +25 rewarded ad)
  - Prioritized over interstitial ads (no full-screen ads back-to-back)

---

## 6. Multiplayer Settlement (Phases E & F)

### Quick Duel (1v1)
- Entry: 15 coins per player, deducted atomically at match start.
- Match cancelled / matchmaking aborted: Full 15-coin refund.
- Match draw: Full 15-coin refund to both players.
- Winner: Receives 25 coins (`DUEL_SETTLEMENT`). Net delta: `+10`.
- Loser: Receives 0 coins. Net delta: `-15` (entry already paid, no extra penalty).
- Settlement is idempotent (settled exactly once using match ID).

### Friends Arena (1–5 Players)
- Host can create room and wait alone without any coin deduction.
- Match requires at least 2 ready players to launch.
- Entry: 15 coins per participant, committed at match start.
- Payouts:
  - 2 Players: 1st = 25 coins, 2nd = 0 coins
  - 3 Players: 1st = 30 coins, 2nd = 10 coins, 3rd = 0 coins
  - 4 Players: 1st = 40 coins, 2nd = 15 coins, 3rd & 4th = 0 coins
  - 5 Players: 1st = 50 coins, 2nd = 20 coins, others = 0 coins
- Ties / Disconnects / Abandons: Deterministic finish order based on authoritative server timestamp; refunds issued if match aborts before active play.

---

## 7. Wallet Transaction Ledger & Invariants (Phase I)

Every balance change creates an immutable transaction in `wallet_transactions`:

| Transaction Type | Description | Idempotency Key Format |
|---|---|---|
| `WELCOME_REWARD` | One-time initial player gift (+60) | `welcome_{playerId}` |
| `SOLO_ENTRY` | Paid Reward Run entry (-3) | `entry_level_{levelId}_{attemptUuid}` |
| `SOLO_FIRST_CLEAR` | Level first valid clear (+5) | `clear_level_{levelId}` |
| `DAILY_LOGIN` | Base daily login reward (+20) | `daily_login_{dateUtc}_{playerId}` |
| `DAILY_AD_BONUS` | Optional daily login ad bonus (+20) | `daily_ad_{dateUtc}_{playerId}` |
| `COIN_AD_REWARD` | Rewarded ad video (+15) | `coin_ad_{dateUtc}_{slotIndex}_{playerId}` |
| `DAILY_CHALLENGE` | Daily challenge first clear (+15) | `dc_clear_{dateUtc}_{playerId}` |
| `DAILY_CHALLENGE_AD_BONUS` | Daily challenge ad bonus (+15) | `dc_ad_{dateUtc}_{playerId}` |
| `WORLD_COMPLETION` | World milestone clear (+25) | `world_clear_{worldId}_{playerId}` |
| `WORLD_AD_BONUS` | World completion ad bonus (+25) | `world_ad_{worldId}_{playerId}` |
| `DUEL_ENTRY` | Quick Duel entry (-15) | `duel_entry_{matchId}_{playerId}` |
| `DUEL_SETTLEMENT` | Quick Duel payout (+25) | `duel_settle_{matchId}_{playerId}` |
| `ARENA_ENTRY` | Friends Arena entry (-15) | `arena_entry_{matchId}_{playerId}` |
| `ARENA_SETTLEMENT` | Friends Arena payout (tiered) | `arena_settle_{matchId}_{playerId}` |
| `MATCH_REFUND` | Cancelled / drawn match refund (+15) | `refund_{matchId}_{playerId}` |

**Invariants**:
- Debits check `balance >= amount` before execution; fail with `INSUFFICIENT_FUNDS` if balance is inadequate.
- Credits are idempotent: inserting a transaction with an existing `idempotencyKey` is ignored or rejected without double crediting.
- Guest to authenticated migration re-keys transactions while preserving running balance.
