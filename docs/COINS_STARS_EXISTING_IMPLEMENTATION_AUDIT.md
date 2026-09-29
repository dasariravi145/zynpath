# Zynpath Coins, Stars, and Economy: Existing Implementation Audit
**Project Root**: `D:\Zynpath`  
**Date**: September 29, 2026  
**Status**: Pre-Implementation Audit (Phase A)

---

## 1. Executive Summary

A comprehensive architectural and code-level audit was conducted across the Zynpath Android application (`D:\Zynpath\android`) and Spring Boot Backend service (`D:\Zynpath\backend`). 

The audit confirms that while core puzzle validation, 300-level catalog progression, Room database models, and AdMob hint integrations exist, **there is currently no persistent coin economy, wallet ledger, or transaction system**. The coin counters displayed on the Home screen and Rewards screen (`2,450`) are hardcoded placeholder strings with no click interactions. Star calculations currently check only hint counts rather than the strict 3-star criteria (valid completion, zero hints, <= 1 undo/reset). Multiplayer sessions (Quick Duel and Friends Arena) resolve winner and finish order without coin entry commitment or payout settlement.

This document details the exact existing implementations, identified gaps, bugs, and the roadmap for strict reference-exact integration.

---

## 2. Detailed Audit by Subsystem

### A. Coin Balance and Persistence
- **Existing State**:
  - `UserPreferences` (`core/datastore/UserPreferences.kt`) does not track coin balance.
  - `PlayerProfileEntity` and `PlayerStatsEntity` (`core/database/entity/`) do not track coin balance.
  - No Room DAO or entity exists for coin balances or wallet transactions.
  - `HomePlayerHeader.kt` (lines 151–154) renders:
    ```kotlin
    ZynpathMasterCurrencyPill(
        iconPainter = painterResource(id = R.drawable.ic_game_coin),
        amount = "2,450"
    )
    ```
  - `DailyChallengeScreen.kt` (lines 359–362) renders the exact same hardcoded `"2,450"`.
- **Missing Functionality**:
  - Centralized, configurable economy constants (`EconomyConfig.kt`).
  - Persistent wallet state with atomic debits and idempotent credits.
  - Immutable wallet transaction ledger (`WalletTransactionEntity`).
  - Real-time reactive flow observing wallet balance.

### B. Star Calculation and Saved Best Stars
- **Existing State**:
  - `LevelProgressEntity` (`level_progress` table) contains:
    - `stars: Int = 0`
    - `isCompleted: Boolean = false`
    - `bestHintCount: Int = 0`
  - `LevelProgressDao.getTotalStarsEarned()` provides a reactive `Flow<Int?>` summing `stars` across all levels.
  - `StarRatingPolicy.calculateStars(hintCount: Int)` in `ValidatedCompletionResult.kt` computes:
    - `hintCount <= 0 -> 3`
    - `hintCount == 1 -> 2`
    - `else -> 1`
  - In `HomePlayerHeader.kt` (line 159), if `totalStars <= 0`, it displays hardcoded `"12"`.
- **Identified Conflicts / Deviations**:
  - **Star 3 Requirement Violation**: Phase C explicitly specifies:
    - Star 1: Valid completion.
    - Star 2: Valid completion without hints (`hintCount == 0`).
    - Star 3: Valid completion without hints (`hintCount == 0`) AND no more than one Undo/Reset action (`undoCount + resetCount <= 1`).
  - Current `ValidatedCompletionResult` does not capture undo or reset actions performed during the attempt.
  - `StarRatingPolicy` ignores undo and reset actions entirely.

### C. Rewards Screen and Daily Claim Implementation
- **Existing State**:
  - `DailyChallengeScreen.kt` functions as the Rewards Screen (Panel 07 in `reference_composite.png`).
  - The 5-day streak progression row (lines 453–460) uses hardcoded mockup values (`Day 1: 100`, `Day 2: 200`, `Day 3: 1`, `Day 4: 300`, `Day 5: Gift`).
  - The green "Claim" pill button simply mutates an in-memory `var isClaimed by rememberSaveable { mutableStateOf(false) }`.
  - No connection exists to a daily login reward tracker, reward calendar, or persistence layer.
- **Missing Functionality**:
  - Base claim (+20 coins) tracked by canonical calendar reward day.
  - Optional rewarded-ad bonus (+20 coins).
  - Claim button state bound to real persisted reward status (Claimed vs Available).

### D. Coin Icon Click Behavior
- **Existing State**:
  - In `HomePlayerHeader.kt` and `DailyChallengeScreen.kt`, `ZynpathMasterCurrencyPill` is non-interactive (`Modifier` without clickable).
- **Missing Functionality**:
  - Clickable coin pill triggering a reference-styled Coin Details / Wallet sheet or modal.
  - Display of available balance, daily free claim status, rewarded video claim availability (max 2/day), and recent transaction history.

### E. AdMob Rewarded and Interstitial Integration
- **Existing State**:
  - `AdMobRewardedHintAdManager.kt` implements `RewardedHintAdContract` for hint rewards only (+1 hint credit).
  - Uses test ad unit ID `ca-app-pub-3940256099942544/5224354917` in debug.
  - **No Interstitial Ad integration exists**: `InterstitialAd` is not imported, loaded, or displayed anywhere in Android code.
  - **No Coin Rewarded Ad integration exists**: There is no rewarded ad flow dedicated to earning coins.
- **Missing Functionality**:
  - `CoinAdManager` implementing rewarded ad flow for coins (+15 coins, max 2 claims/day).
  - Strict separation: Hint ads must NEVER award coins; Coin ads must NEVER award hints.
  - `InterstitialAdManager` implementing post-level milestone interstitials (every 3rd distinct first-time solo level completion, minimum 120s cooldown, max 2/session, max 3/day).
  - Priority rule: World completion experience takes precedence over interstitial ads.

### F. Level Completion and Retry Flow
- **Existing State**:
  - `GameplayViewModel.kt` handles level completion via `recordValidatedCompletion`.
  - Replay (`onReplayLevel`) resets engine state and timer.
- **Missing Functionality**:
  - Selection between **Free Practice** (0 cost, 0 coins) and **Reward Run** (3 coins entry, +5 first-clear coins) for Level 4 onward.
  - One-time entry deduction on committed Reward Run.
  - Free retries within the same paid attempt without double charging.
  - Attempt state persistence across app process death/restart.
  - First-clear reward guard: replaying a completed level must not award repeat clear coins.

### G. World Completion and Unlock Rules (World Progression Safety)
- **Existing State**:
  - `WorldConfiguration.kt` defines 6 Worlds across 300 levels:
    - World 1: Levels 1–20 (20 levels)
    - World 2: Levels 21–50 (30 levels)
    - World 3: Levels 51–100 (50 levels)
    - World 4: Levels 101–150 (50 levels)
    - World 5: Levels 151–200 (50 levels)
    - World 6: Levels 201–300 (100 levels)
- **Identified Progression Bug**:
  - In `SoloVictoryScreen.kt` (lines 913–919), chapter unlocks were mapped to levels `25, 50, 100, 150, 200, 250, 300`, confusing World 1 with Level 25 instead of Level 20.
  - In `SoloVictoryScreen.kt` (lines 966–971), the primary button only toggled between `"NEXT LEVEL"` and `"RETURN TO WORLD MAP"`. At level 20, completing World 1 should present `"NEXT WORLD"` to advance to World 2.
  - World completion reward (+25 coins, optional +25 ad bonus) is absent.

### H. Daily Challenge
- **Existing State**:
  - Daily challenge is playable, validates locally and against backend, and stores attempt history in `DailyChallengeEntity`.
- **Missing Functionality**:
  - Valid first completion reward: +15 coins.
  - Optional rewarded-ad bonus: +15 coins.
  - Deduplication ensuring max 1 completion reward and 1 ad bonus per daily challenge date.

### I. Quick Duel & Friends Arena Economy
- **Existing State**:
  - Matchmaking, countdown, and real-time path updates function via WebSocket and REST APIs.
  - Results are displayed in `QuickDuelScreen` and `FriendsArenaResultsScreen`.
- **Missing Functionality**:
  - Quick Duel entry: 15 coins committed at match start (not on matchmaking queue).
  - Quick Duel settlement: Winner +25 coins (net +10), Loser 0 (net -15). Full refund on cancellation or draw.
  - Friends Arena entry: 15 coins per player committed at match start (minimum 2 ready players).
  - Friends Arena payouts:
    - 2 players: 1st 25 coins, 2nd 0 coins.
    - 3 players: 1st 30 coins, 2nd 10 coins.
    - 4 players: 1st 40 coins, 2nd 15 coins.
    - 5 players: 1st 50 coins, 2nd 20 coins.
  - Backend authoritative wallet debits and idempotent settlement.

### J. Guest-to-Authenticated Account Migration
- **Existing State**:
  - `UserPreferences.guestUuid` holds the local guest identity.
  - When linking Google/Facebook, `OkHttpAuthApiService` sends `guestUuid` to backend `PlayerAccountService`.
- **Missing Functionality**:
  - Local wallet ledger must re-attribute all guest transactions and current balance to the authenticated player profile upon successful account linking without resetting or overwriting progress.

---

## 3. Inventory of Files to Modify / Create

### A. New Android Files to Create
1. `core/economy/EconomyConfig.kt`: Centralized economy constants (welcome gift, entry costs, clear rewards, daily login, ad limits, multiplayer payouts).
2. `core/database/entity/WalletTransactionEntity.kt`: Room entity for immutable wallet ledger.
3. `core/database/dao/WalletDao.kt`: Room DAO for balance queries and atomic transaction insertion.
4. `core/economy/WalletRepository.kt` & `WalletRepositoryImpl.kt`: Domain repository managing balances, attempt commitments, and idempotent reward claims.
5. `core/ads/CoinRewardedAdManager.kt`: Dedicated rewarded ad manager for coin rewards.
6. `core/ads/InterstitialAdManager.kt`: Interstitial ad controller with milestone tracking, 120s cooldown, session/daily caps, and world-priority logic.
7. `feature/wallet/CoinDetailsBottomSheet.kt`: Reference-exact wallet & transaction details modal sheet.

### B. Existing Android Files to Update
1. `core/database/ZynpathDatabase.kt`: Add `WalletTransactionEntity`, bump database version to 12 with `MIGRATION_11_12`.
2. `core/puzzle/model/ValidatedCompletionResult.kt`: Update `StarRatingPolicy` and `ValidatedCompletionResult` to incorporate `undoCount` and `resetCount`.
3. `core/puzzle/engine/PuzzleEngine.kt`: Track total undo and reset actions taken during the active puzzle session.
4. `core/database/repository/ProgressRepository.kt`: Pass undo/reset counts into completion result and star evaluation.
5. `feature/gameplay/GameplayViewModel.kt`: Support Free Practice vs Reward Run, handle entry commitment, world boundary detection, and level/world coin rewards.
6. `feature/gameplay/SoloVictoryScreen.kt`: Fix World 1 boundary (level 20), show "NEXT WORLD" at world completion, display coin rewards and optional ad bonus.
7. `feature/home/components/HomePlayerHeader.kt`: Bind real coin balance and real stars to HUD pills; make coin pill clickable.
8. `feature/home/HomeScreen.kt`: Handle coin click to open `CoinDetailsBottomSheet`; show small floating animated delta (+/-) on coin changes.
9. `feature/daily/DailyChallengeScreen.kt`: Bind 5-day streak and Claim button to real persisted daily reward state; award +20 coins.
10. `feature/multiplayer/QuickDuelViewModel.kt`: Commit 15 coins entry on match start; settle rewards upon match conclusion.
11. `feature/multiplayer/FriendsArenaRoomViewModel.kt` & `FriendsArenaGameplayViewModel.kt`: Commit 15 coins entry at match start; settle tiered payouts upon completion.

### C. Backend Files to Update
1. `db/migration/V7__wallet_and_multiplayer_settlement.sql`: Schema for `player_wallets` and `wallet_transactions`.
2. `multiplayer/service/MatchSessionService.java`: Integrate backend wallet entry check and settlement trigger.
3. `multiplayer/service/WalletSettlementService.java`: Authoritative multiplayer settlement service for Quick Duel and Friends Arena.

---

## 4. Audit Conclusion & Next Steps
The existing architecture provides solid foundational contracts, but economy logic is completely unbacked by persistence. Proceeding directly to **Phase B (Economy Specification)** and **Phase C–J (Implementation & Tests)**.
