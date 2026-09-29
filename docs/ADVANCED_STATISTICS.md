# Advanced Statistics & Progression Insights Architecture — Zynpath

## 1. Overview

Zynpath provides deep, privacy-conscious progression insights and personal analytics across Solo, Daily Challenge, and Competitive modes.

---

## 2. Free vs. Premium Statistics Boundaries (Prompt 30 & Prompt 41)

In strict accordance with the project's monetization and fair-play policies:
- **Free Tier Insights (Available to All Players):**
  - Total completed levels across the 6 canonical worlds (out of 300).
  - Star ratings and personal best solve times per completed level.
  - Total stars earned.
  - Daily Challenge completion counts, current active consecutive day streak, and best historical streak.
  - Overall competitive matches, wins, win rates, and tournament participation counts.
  - Full Achievement Gallery access with progress fractions and unlock dates.
- **Premium Tier Advanced Analytics (Subscriber Entitlement):**
  - Solve speed distributions across grid dimensions ($4\times 4$ to $8\times 8$).
  - Backtracking frequency and move efficiency curves.
  - Deep time-of-day solve trends and perfect-coverage rates.
  - Premium Solo Puzzle Pack independent telemetry.
- **Zero Competitive Advantage:**
  - Premium analytics never convey hints, move prediction, or opponent telemetry during live competitive duels.

---

## 3. Privacy-Aware Storage & Computation

- **On-Device Derivation:** All personal statistics are derived directly from verified local Room entities (`LevelProgressEntity`, `DailyChallengeRecord`, `AchievementEntity`).
- **No Unconsented Telemetry:** Personal solve statistics are never harvested or sold. Aggregated analytics events only record high-level navigation milestones without granular touch paths or board solutions.
- **Account Isolation:** Multi-user switching invalidates analytics caches and queries scoped to the active account identity.
