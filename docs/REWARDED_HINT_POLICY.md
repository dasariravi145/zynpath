# Zynpath Rewarded Hint Policy

## 1. Core Principles

1. **Non-Competitive Isolation:**
   - Rewarded hints can **only** be earned and consumed in offline and solo puzzle modes.
   - Competitive modes (Quick Duel, Friend Duel, Mini League, and competitive Daily Challenge) have hints strictly disabled at the engine/policy layer (`HintUsagePolicy.isHintAllowed(mode)`).
   - Watching an ad never provides an advantage in any competitive leaderboard, multiplayer duel, or tournament.

2. **Clear Value Exchange:**
   - Exactly **1 completed rewarded ad grants +1 Solo Hint credit**.
   - The user is shown the exact reward amount prior to viewing the ad.
   - If an ad is dismissed early or closed before completion, no reward is granted, and the user is returned to the puzzle safely with a polite explanation ("Ad closed before completion. No hint earned.").

3. **Allowance Separation:**
   - Standard free allowance (`freeHintsRemaining`, default 3) and earned rewarded credits (`rewardedHintCredits`, default 0) are tracked distinctly in DataStore and backend schemas.
   - When a player requests a hint:
     1. Unlimited Premium entitlement is checked first.
     2. Standard free allowance is consumed first.
     3. Earned rewarded credits are consumed next.
     4. Earning a reward does not overwrite or reset the standard free allowance.

4. **Hint Delivery Integrity:**
   - Credits are only consumed when a valid, solver-calculated hint or recovery suggestion is successfully produced.
   - If a hint cannot be computed or search is inconclusive, no credit is deducted.

5. **Daily & Storage Caps:**
   - Maximum 5 rewarded ad grants per 24-hour cycle.
   - Maximum 10 stored bonus credits.
   - Prevents hoarding and bot abuse.
