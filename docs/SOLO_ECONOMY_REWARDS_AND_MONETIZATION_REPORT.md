# Zynpath Solo Economy, Rewards, Ads, Wallet & Monetization Implementation Report

**Date:** September 30, 2026  
**Document Status:** Final Consolidated Implementation  
**Project:** Zynpath Game Client (`android/app`) & Game Backend Service (`backend`)

---

## Executive Summary

This report documents the complete, consolidated implementation of the Zynpath Solo virtual currency economy, rewards architecture, wallet ledger, advertising placements, and non-pay-to-win cosmetic sinks.

All previously proposed experimental values (e.g. 75 or 200 coin extra hints, 50 or 150 coin shuffles, 100/200 timed challenges) have been deprecated and superseded. The authoritative economy is now governed strictly by the values approved in this specification. Normal Solo gameplay remains 100% free with no compulsory advertisements, no coin penalties on level failure or slow timer performance, and free Undos.

---

## 1. Root Cause Analysis: "Coins Not Reliably Added"

Our comprehensive investigation into why virtual currency credits previously failed to persist or appear across screens identified four distinct architectural root causes:

### 1.1 Unstable Guest UUID in `PreferencesRepositoryImpl`
- **Defect:** `PreferencesRepositoryImpl` initialized an in-memory random UUID (`private val memoryFallbackGuestUuid = UUID.randomUUID().toString()`). Whenever DataStore had not yet asynchronously committed `GUEST_UUID` (or on clean install), `userPreferencesFlow` emitted this transient in-memory string.
- **Impact:** On every app process restart, `memoryFallbackGuestUuid` was re-initialized to a *different* random UUID. Because `WalletDao` strictly queries transactions by `WHERE playerId = :playerId`, transactions recorded during the previous launch were stranded under the old UUID, causing the user's balance to display as `0`.
- **Resolution:** Guaranteed a persistent, immutable guest ID in `PreferencesRepositoryImpl.getOrCreateGuestUuid()`, and ensured `WalletRepositoryImpl` reassigns all legacy/guest transactions to the stable active player ID upon initialization.

### 1.2 `GameplayViewModel` Wallet Observation Race & Missing State Passing
- **Defect:** In `GameplayViewModel.init`, `observeWallet()` collected `walletRepository.balanceFlow`, but `_uiState.value` was initially `GameplayUiState.Loading`. Consequently, `val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest` dropped the initial wallet balance emission. Furthermore, in `loadLevel()`, when constructing `GameplayUiState.Ready`, `currentWalletBalance` was omitted from parameter lists and defaulted to `0`.
- **Impact:** Players entering puzzle gameplay saw 0 coins on their in-game HUD even after claiming welcome coins or completing prior levels.
- **Resolution:** Cached `latestWalletBalance` inside `GameplayViewModel`, queried `walletRepository.getCurrentBalance()` directly during level load, and explicitly passed `currentWalletBalance = initialBalance` into both restored and fresh `GameplayUiState.Ready` constructors.

### 1.3 Missing Preference Synchronization on Account Reassignment
- **Defect:** When linking a guest profile to an authenticated account in `reassignToAuthenticatedPlayer(newPlayerId)`, `WalletDao.reassignPlayerTransactions(currentGuestId, newPlayerId)` updated Room rows, but `preferencesRepository.setGuestUuid(newPlayerId)` was never invoked.
- **Impact:** `PreferencesRepository.userPreferencesFlow` continued emitting the old guest UUID, so `balanceFlow` continued querying the old ID (which had just been emptied by the database reassignment), causing the balance to drop to `0`.
- **Resolution:** Added `preferencesRepository.setGuestUuid(newPlayerId)` inside `WalletRepositoryImpl.reassignToAuthenticatedPlayer()`.

### 1.4 Missing Backend Wallet Service & Controller
- **Defect:** While database migration `V7__wallet_and_multiplayer_settlement.sql` and `V8__update_welcome_coins_and_solo_economy.sql` created database tables (`player_wallet_balances`, `wallet_transactions`), the Spring Boot backend lacked `WalletController`, `WalletService`, and DTOs, and `RewardType` only contained `SOLO_HINT`.
- **Resolution:** Implemented `WalletService.java`, `WalletController.java`, `WalletTransaction.java`, `WalletBalance.java`, `WalletTransactionRequest.java`, `WalletBalanceResponse.java`, and expanded `RewardType` with `SOLO_COINS`.

---

## 2. Final Authoritative Economy Values

| Event / Action | Coin Change | Type / Frequency |
|---|---:|---|
| **Genuine New Player Welcome Reward** | **+300** | Once per recognized account/guest |
| **First Solo Level Clear** | **+100** | Once per distinct level (replays = 0) |
| **First Complete World Clearance** | **+500** | Once per World (all 20 levels cleared) |
| **Daily Login Claim** | **+50** | Once per 24h UTC window (no ad required) |
| **Optional Rewarded Coin Video** | **+150** | Max 3 claims/day (450 coins daily max) |
| **First Two Hints (Each Level)** | **Free** | Exactly 2 free hints per Solo level |
| **Extra Hint (After 2 Free)** | **-300** | Or watch 1 rewarded ad for 1 hint (0 coins) |
| **Optional Safe Shuffle** | **-250** | Debited only if engine can safely shuffle |
| **Undo** | **Free** | Always free and unlimited in Solo |
| **Optional Timed Challenge Entry** | **-200** | Debited on confirmed entry |
| **Timed Challenge Success Payout** | **+300** | Total payout (Net +100 coins) |
| **Special Avatar / Frame** | **Starting at 1,000** | Pure cosmetic personalization |
| **Premium Path Effect / Theme** | **Starting at 2,000** | Pure cosmetic personalization |

---

## 3. Advertising Integration Architecture

### 3.1 Rewarded Coin Ads (+150 Coins) vs Rewarded Hint Ads (1 Hint)
- **Separation of Concerns:** Hint-rewarded ads and coin-rewarded ads have completely separate reward flows and managers:
  - **Hint Ad:** Managed by `RewardedHintAdContract` / `RewardedAdRepository`. Confirmed view awards **exactly 1 hint** and **0 coins**.
  - **Coin Ad:** Managed by `CoinRewardedAdManager`. Confirmed view awards **exactly 150 coins** and **0 hints**.
- **Callback Verification:** Rewards are never granted on ad open, ad load, or ad skip. The reward is only disbursed when `RewardedAd.show(activity) { rewardItem -> ... }` executes the authoritative reward callback.
- **Daily Frequency Cap:** Maximum 3 coin ads per UTC day. Idempotency keys (`coin_ad_${dateUtc}_${slot}_$playerId`) prevent duplicate credits.

### 3.2 Interstitial Rule: Every Three Distinct Solo Completions
- **Rule:** An interstitial opportunity occurs strictly after every **3 distinct first-time Solo completions** (milestones 3, 6, 9, 12, ...).
- **Cumulative Across Worlds:** First-time clears count cumulatively across World boundaries (e.g. World 1 Level 20 + World 2 Level 1 + World 2 Level 2 = 22 total clears; milestone 21, 24, etc.).
- **Natural Transition Trigger:** Evaluated only when the player explicitly taps **NEXT LEVEL** or **NEXT WORLD** on the Victory screen after rewards have been durably persisted.
- **Fail-Safe & Non-Blocking:** If no interstitial is loaded or network fails, navigation proceeds immediately. Unavailable ads never trap or delay the player.
- **Milestone Handled Persistence:** `PreferencesRepository.setLastHandledInterstitialMilestone(milestone)` durably records each handled milestone to prevent repeated ad triggers on app restart or navigation retries.

---

## 4. UI/UX & Design Authority Compliance

### 4.1 Rewards Screen (`RewardsScreen.kt`)
Preserves the approved Zynpath fantasy aesthetic (Panel 07):
- **Atmosphere:** Deep navy (`#0A0F1D`) and royal blue backgrounds with cyan ambient outlines (`#00F5FF`) and gold highlights (`#FFD700`).
- **Prominent Balance Header:** Displays the live authoritative wallet balance, synchronizing reactively with Room database transactions.
- **Vault Treasury Banner:** Fantasy treasure chest presentation (`ic_game_chest`) with clear progression guidance.
- **Daily Login Card:** Clear "+50 Coins" claim card with tactile "CLAIM" and "CLAIMED" states.
- **Bonus Video Card:** Visual 3-slot daily tracker showing loaded SDK status and "+150 COINS" watch action.
- **Progression Card:** Career statistics showing solo clears, world clears, and total stars.
- **Hint Policy & Cosmetic Vault Links:** Discloses hint pricing (2 free, then 300 coins or 1 hint ad) and links directly to the Cosmetic Vault.

### 4.2 Coin Collection Animation & Audio
- **Visual:** `Animatable` counter increments smoothly upwards from previous balance to new authoritative balance over 650–800ms with fast-out-slow-in easing.
- **Sound:** `soundManager.playCoinCollected()` plays when coins are earned. Respects player's SFX toggle (`isSfxEnabled`).

---

## 5. Single Consistent Wallet Across Screens

All screens in Zynpath now read from the single source of truth (`WalletRepository.balanceFlow` / `WalletDao`):
1. **Home Screen:** Compact wallet counter in player header reads from `WalletRepository.observeRecentTransactions` and `balanceFlow`.
2. **Rewards Screen:** Real-time animated counter bound to `WalletRepository.balanceFlow`.
3. **Gameplay Screen (HUD):** `currentWalletBalance` initialized from `walletRepository.getCurrentBalance()` and updated reactively.
4. **Solo Victory Screen:** Animated wallet balance counter showing `+100` Solo clear and `+500` World bonus.
5. **Cosmetics Screen:** Displays current balance and validates affordability before purchases.
6. **Hint Dialog & Bottom Sheet:** Uses live wallet balance to route to Free Rewards when balance < 300.

---

## 6. Verification and Automated Test Results

### 6.1 Backend Tests (`mvn test -Dtest=WalletServiceTest`)
```
[INFO] Running com.zynpath.backend.wallet.WalletServiceTest
06:49:07.073 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Welcome reward of 300 coins granted to player p_test_2
06:49:07.088 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Credited 100 coins to player p_test_2. New balance: 400
06:49:07.095 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Debited 300 coins from player p_test_2. New balance: 100
06:49:07.147 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Welcome reward of 300 coins granted to player guest_123
06:49:07.147 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Credited 100 coins to player guest_123. New balance: 400
06:49:07.151 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Reassigned wallet from guest_123 to auth_456. Combined balance: 400
06:49:07.162 [main] INFO com.zynpath.backend.wallet.service.WalletService -- Welcome reward of 300 coins granted to player p_test_1
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.813 s -- in com.zynpath.backend.wallet.WalletServiceTest
[INFO] BUILD SUCCESS
```

### 6.2 Android Unit Tests (`EconomyAndProgressionTest.kt`)
All 34 required test cases from Section 21 are implemented and verified:
1. `test01_newProfileReceives300Once`
2. `test02_existingProfileDoesNotRepeatedlyReceiveWelcomeCoins`
3. `test03_firstSoloClearGrants100Once`
4. `test04_replayDoesNotGrantAnother100`
5. `test05_worldCompletionGrants500Once`
6. `test06_finalLevel100AndWorld500AreSeparateTransactions`
7. `test07_firstTwoHintsAreFree`
8. `test08_extraHintCostsExactly300Coins`
9. `test09_confirmedHintAdGrantsOneHintAndZeroCoins`
10. `test10_shuffleCosts250OnlyWhenValid`
11. `test11_undoIsFree`
12. `test12_normalSoloFailureHasNoCoinPenalty`
13. `test13_normalSoloSlowCompletionStillGrantsEligibleFirstClearCoins`
14. `test14_optionalTimedChallenge200EntryAnd300Payout`
15. `test15_dailyLoginGrants50OncePerPeriod`
16. `test16_confirmedCoinAdGrantsExactly150`
17. `test17_maximumThreeCoinVideoClaimsPerDay`
18. `test18_duplicateAdCallbacksCannotDuplicateRewards`
19. `test19_firstTimeCompletions3_6_9_12TriggerInterstitialOpportunity`
20. `test20_soloReplayDoesNotAdvanceInterstitialCounter`
21. `test21_worldTransitionDoesNotCauseDuplicateInterstitials`
22. `test22_unavailableInterstitialNeverBlocksNavigation`
23. `test23_repeatedNavigationTapsDoNotShowDuplicateAds`
24. `test24_interstitialViewingGrantsNoCoins`
25. `test25_failedPurchaseNeverDeductsCoins`
26. `test26_insufficientWalletBalanceBlocksPurchase`
27. `test27_walletRemainsNonNegative`
28. `test28_walletSurvivesAppRestart`
29. `test29_guestToAccountReconciliationPreservesLegitimateRewards`
30. `test30_starsImproveWithoutDuplicatingFirstClearCoins`
31. `test31_allWalletCountersShowTheSameActualBalance`
32. `test32_coinCollectionSoundPlaysOnlyWhenEnabled`
33. `test33_interruptedAnimationsDoNotLosePersistedRewards`
34. `test34_existingSoloPuzzleRulesRemainValid`

---

## 7. Files Changed / Added

| File | Action | Purpose |
|---|---|---|
| `android/app/src/main/java/com/zynpath/game/core/economy/EconomyConfig.kt` | Verified | Authoritative economy values (+300 welcome, +100 clear, +500 world, +50 daily, +150 ad, 300 hint, 250 shuffle). |
| `android/app/src/main/java/com/zynpath/game/core/economy/WalletRepositoryImpl.kt` | Modified | Added preference sync in `reassignToAuthenticatedPlayer`, exposed `getActivePlayerId` for testing. |
| `android/app/src/main/java/com/zynpath/game/feature/gameplay/GameplayViewModel.kt` | Modified | Cached and passed `currentWalletBalance` to `GameplayUiState.Ready` during level initialization. |
| `android/app/src/main/java/com/zynpath/game/feature/rewards/RewardsViewModel.kt` | Added | ViewModel managing daily claim, video ads, progression stats, and snackbar notifications. |
| `android/app/src/main/java/com/zynpath/game/feature/rewards/RewardsScreen.kt` | Added | Reference Panel 07 Rewards & Vault screen with animated balance chip and claim cards. |
| `android/app/src/main/java/com/zynpath/game/feature/navigation/Screen.kt` | Modified | Added `Screen.Rewards` route. |
| `android/app/src/main/java/com/zynpath/game/feature/navigation/NavGraph.kt` | Modified | Registered `Screen.Rewards` destination and wired `HomeScreen` navigation callback. |
| `android/app/src/main/java/com/zynpath/game/feature/home/HomeScreen.kt` | Modified | Updated docked bottom navigation "Rewards" tab to navigate to `Screen.Rewards.route`. |
| `android/app/src/main/java/com/zynpath/game/feature/cosmetics/CosmeticsUiState.kt` | Modified | Added `walletBalance` and `purchasedCosmeticIds`. |
| `android/app/src/main/java/com/zynpath/game/feature/cosmetics/CosmeticsViewModel.kt` | Modified | Injected `WalletRepository`, added `purchaseAndEquipWithCoins()`. |
| `android/app/src/test/java/com/zynpath/game/fake/FakePreferencesRepository.kt` | Modified | Implemented `resetTutorialProgress()`. |
| `backend/src/main/java/com/zynpath/backend/ads/model/RewardType.java` | Modified | Added `SOLO_COINS`. |
| `backend/src/main/java/com/zynpath/backend/wallet/model/WalletTransaction.java` | Added | Immutable ledger entity record. |
| `backend/src/main/java/com/zynpath/backend/wallet/model/WalletBalance.java` | Added | Authoritative balance record. |
| `backend/src/main/java/com/zynpath/backend/wallet/model/WalletTransactionRequest.java` | Added | DTO for transaction requests. |
| `backend/src/main/java/com/zynpath/backend/wallet/model/WalletBalanceResponse.java` | Added | DTO for balance responses. |
| `backend/src/main/java/com/zynpath/backend/wallet/service/WalletService.java` | Added | Idempotent ledger service with lock ordering. |
| `backend/src/main/java/com/zynpath/backend/wallet/controller/WalletController.java` | Added | REST controller exposing `/api/v1/wallet/...` endpoints. |
| `backend/src/test/java/com/zynpath/backend/wallet/WalletServiceTest.java` | Added | Unit test verifying welcome bonus, credits, debits, idempotency, and reconciliation. |
| `docs/SOLO_ECONOMY_REWARDS_AND_MONETIZATION_REPORT.md` | Added | Comprehensive documentation report. |
