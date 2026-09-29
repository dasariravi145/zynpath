# Retention Experience & Next-Goal Discovery — Zynpath

## 1. Overview

Retention in Zynpath is cultivated through clear progress visibility and empowering next-goal discovery rather than manipulative notifications or daily lock-in.

---

## 2. Home Screen Milestone & Next Objective Preview

The Home Screen incorporates `HomeMilestonePreviewCard`, a dedicated card displaying:
1. **Next Objective Discovery:** Dynamically computed next goal:
   - Evaluates all unearned achievements to find the item with the highest progress fraction (e.g., "Complete 5 unique Solo levels").
   - If all achievements in progress are locked, guides the player to the next uncompleted level in their active world (e.g., "Clear Level 14 in The Awakening").
2. **Progress Indicator:** Linear progress bar with percentage readout indicating proximity to unlocking the next milestone.
3. **Achievements Summary Badge:** Displays total unlocked achievements versus total catalog count (e.g., "🏆 12 / 25 Unlocked") with a direct tap target to open the full `AchievementsScreen`.

---

## 3. Daily Challenge Streak Calculation

### Canonical UTC Date Handling
To prevent device time manipulation, timezone discrepancies, or daylight savings bugs:
- Daily Challenge identity is bound to `LocalDate.now(ZoneOffset.UTC)`.
- The `DailyChallengeStreakCalculator` strictly iterates backwards from today (or yesterday if today's puzzle is not yet played) in UTC.
- A streak continues only if consecutive UTC dates are present in the player's completed daily records.

### Verification-Dependent Milestones
- **Local Completion:** Immediately updates the daily challenge card UI and increments local participation count.
- **Server Validated:** The `daily_server_validated` achievement is **only** awarded once the backend responds with `verificationStatus == "VERIFIED"`. Pending local submissions do not award this achievement.
- **Leaderboard Rank:** `daily_leaderboard_ranked` is awarded only when backend verification confirms `isLeaderboardEligible == true`.

---

## 4. Return-to-Play Polish

When a player launches Zynpath after an absence:
- **Instant Resume:** The primary "CONTINUE PLAYING" action points directly to their next unlocked level.
- **Progress Preservation:** Offline achievements and level stars remain intact with zero degradation.
- **Friendly Welcoming:** No negative dialogues like "You broke your streak!" Instead, today's puzzle is presented with fresh possibility.
