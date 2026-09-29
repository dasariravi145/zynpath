# Zynpath: Personal Analytics & Progression Insights Architecture

## 1. Overview
The Zynpath Personal Analytics system provides players with reliable, transparent, and accessible insights into their puzzle-solving journey. Designed under the **Zero Fabrication Principle**, every displayed statistic, chart, and metric is derived strictly from actual recorded gameplay records.

- **Offline-First:** Solo world progression, level personal bests, and local daily challenge streaks are stored in Room and calculated locally without requiring internet access.
- **Server-Authoritative:** Multiplayer match records, tournament finishes, and official daily challenge leaderboard timings are verified and stored authoritatively by the backend.
- **Entitlement-Aware:** Free players retain all essential statistics (levels solved, worlds completed, local streaks, basic duel records). Premium players unlock advanced progression velocity, same-puzzle time improvement deltas, per-world speed analytics, and chronological completion trends.

---

## 2. Core Architecture

```mermaid
graph TD
    subgraph Client [Android Client (Compose + Room + DataStore)]
        LPD[LevelProgressDao] --> PAR[PersonalAnalyticsRepositoryImpl]
        DCD[DailyChallengeDao] --> PAR
        PPR[PremiumPackRepository] --> PAR
        SER[SubscriptionEntitlementRepository] --> PAR
        MR[MultiplayerRepository] --> PAR
        PAR --> VM[StatisticsViewModel]
        VM --> SS[StatisticsScreen]
    end

    subgraph Backend [Spring Boot Modular Monolith]
        PAS[PersonalAnalyticsService] --> CS[CompetitiveService]
        PAS --> DCS[DailyChallengeService]
        PAS --> ES[EntitlementService]
        PAC[PersonalAnalyticsController] --> PAS
    end

    PAR -.->|Sync verified stats| PAC
```

### Components
1. **`PersonalAnalyticsRepository` & `PersonalAnalyticsRepositoryImpl`**:
   - Queries `LevelProgressDao` for canonical levels 1–300.
   - Evaluates active world and completion counts against canonical world boundaries.
   - Queries `PremiumPackRepository` for separate downloaded pack progress (never mixed with the 300 free levels).
   - Queries `DailyChallengeDao` and cached server daily results for streaks and verification status.
   - Queries `MultiplayerRepository` for Quick Duel, Friend Duel, and Mini League records.
   - Evaluates `ADVANCED_PERSONAL_STATS` entitlement status via `SubscriptionEntitlementRepository`.
2. **`StatisticsViewModel`**:
   - Manages tab navigation (`OVERVIEW`, `SOLO`, `DAILY`, `COMPETITIVE`, `PREMIUM_INSIGHTS`).
   - Manages time filtering (`LAST_7_DAYS`, `LAST_30_DAYS`, `ALL_TIME`).
   - Handles asynchronous state updates and manual refresh.
3. **`StatisticsScreen` & Components**:
   - `WorldProgressChart`: Accessible horizontal progression for Worlds 1–6.
   - `CompletionTrendChart`: Bar chart distinguishing first-time solves vs replay sessions.
   - `DailyCalendarGrid`: 14-day history distinguishing verified online vs local provisional vs missed.
   - `CompetitiveModeSummaryCard`: Authoritative records for 1v1 duels with sample size ($N$).
   - `MiniLeagueSummaryCard`: Multi-player tournament metrics avoiding 1v1 win/loss distortion.
   - `PremiumAnalyticsPreviewCard`: Honest preview for free players explaining benefits without fake data.

---

## 3. Free vs. Premium Access Policy

| Feature | Free Tier | Premium Tier |
| :--- | :--- | :--- |
| **Solo Levels Completed** | Available (1–300 count & %) | Available (1–300 count & %) |
| **Active World & Completed Worlds** | Available | Available |
| **Solo Personal Bests** | Available per completed level | Available + chronological history |
| **Daily Challenge Streak** | Available (Current & Best) | Available (Current & Best) |
| **Daily Challenge Calendar** | Available (Recent 14 days) | Available (Expanded history) |
| **Competitive Win/Loss Summary** | Available (Matches, Wins, Losses) | Available + trend velocity |
| **Mini League Tournament Summary**| Available (Podiums, 1st place) | Available + rank distribution |
| **Completion Trends Chart** | Locked (Preview card shown) | Unlocked (7d/30d/All time) |
| **Same-Puzzle Time Improvements**| Locked (Preview card shown) | Unlocked (Delta & % faster) |
| **Per-World Speed Analytics** | Locked (Preview card shown) | Unlocked (Average solve times) |
| **Premium Solo Pack Analytics** | Unavailable (No packs) | Available (Per-pack tracking) |

---

## 4. Privacy and Account Isolation
1. **Private by Default:** Personal analytics reports are strictly private to the authenticated player. They are never exposed in public profiles or leaderboard payloads.
2. **Account Switching:** When a user logs out or switches accounts, cached competitive snapshots are cleared to prevent cross-account data leakage.
3. **Zero Behavioral Surveillance:** Analytics only process essential gameplay milestones (level solved, attempt finished, hint consumed, reset used). No fine-grained coordinate streams, touch gestures, or device location data are ever stored or uploaded.

---

## 5. Offline Synchronization and Analytics Reconciliation (Prompt 35)
1. **Durable Milestone Queuing**: When offline, gameplay milestones (first completions, improved personal bests) are stored locally in Room and queued in `sync_operations`.
2. **Server-Side Idempotency**: Batched sync submissions to `/api/v1/sync/batch` match on stable operation IDs. Re-transmissions across network blips never artificially inflate completion counts, replay counts, or streak days.
3. **Personal Best Reconciliation**: Merge logic strictly preserves the true mathematical minimum time (`min(local, remote)`). Incomplete remote runs or clock skew events cannot overwrite a genuine personal best.

