# Zynpath Hint Usage Policy & Entitlements

## 1. Overview
Zynpath guarantees fair, non-coercive gameplay:
- The complete game is 100% playable offline without payment or account login.
- **Undo** and **Reset** are unconditionally free, unlimited, and instant for all players.
- Solo hints are offered as assistive guidance with fair usage boundaries.

---

## 2. Solo Hint Allowance Policies

### 2.1 Free Solo Players
- **Initial Allowance**: 3 free hints (`DEFAULT_FREE_HINT_ALLOWANCE = 3`).
- **Persistence**: Persisted locally in Jetpack DataStore through `PreferencesRepository`. Survives application restart and device reboots.
- **Consumption Gate**: A hint is consumed **only** upon successful delivery of a verified `NEXT_MOVE` or `RECOVERY_REQUIRED` result.
- **No Waste Policy**: Repeated taps on an identical state reuse the existing hint without deducting allowance. Searches that hit search limits (`SEARCH_INCONCLUSIVE`), are cancelled, or encounter invalid states do **not** consume allowance.
- **Exhaustion State**: When allowance reaches 0, a non-disruptive dialog reminds the player that Undo and Reset remain free and unlimited, with a placeholder entry point for future Premium upgrade.

### 2.2 Premium Entitlement
- Premium players receive unlimited Solo hints (`isUnlimited = true`).
- Entitlement status is managed via `HintUsageRepository` and `PreferencesRepository`.

---

## 3. Competitive Fairness (Ranked & Multiplayer)
To preserve integrity in competitive game modes:
- **Quick Duel**, **Friend Duel**, and **Mini League** strictly reject all hint requests with `HintResult.HintNotAvailable`.
- Premium users do **not** receive hints in competitive modes.
- Competitive session payloads never transmit hidden solutions or witness paths to clients.

---

## 4. Rewarded Ads Architectural Boundary
- An architectural interface `RewardedHintProvider` defines future optional rewarded ad hint grants.
- A `NoOpRewardedHintProvider` stub is provided by default.
- Watching ads is never mandatory to complete any puzzle level.
