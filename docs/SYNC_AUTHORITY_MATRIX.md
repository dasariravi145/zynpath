# Zynpath Data Authority Matrix

**Milestone:** Phase 8 — Reliability & Data Integrity (Prompt 35)  
**Standard:** Explicit Distributed Authority & Single-Source-of-Truth  

---

## 1. Overview

In Zynpath's offline-first architecture, not every data category shares the same authority model. To guarantee competitive integrity, protect paid entitlement boundaries, and ensure seamless offline solo gameplay, authoritative ownership is strictly partitioned across local device storage and backend cloud services.

---

## 2. Comprehensive Authority Matrix

| Data Category | Authoritative Origin | Local Policy | Remote Reconciliation Policy | Rationale |
|---|---|---|---|---|
| **Solo Level Completion** | Local Device (Authenticated Merge) | Durable in Room `level_progress`. Playable and unlockable 100% offline. | Non-destructive union. Completed status is never revoked. Duplicate reports do not inflate total counts. | Players must be able to beat levels on a plane or subway without network access. |
| **Solo Personal Bests (Time, Moves, Hints)** | Local Device (Validated Merge) | Durable in Room `level_progress`. Validated by pure domain rules. | Strictly preserves best valid positive time (`min(local, remote)`). Zero or negative times never overwrite valid records. | Preserves player achievements without server round-trip while preventing data corruption. |
| **Active Gameplay Session** | Local Device | Durable in Room `game_sessions`. Versioned with snapshot schema and monotonically increasing revisions. | Local-only. Cleared upon level completion or reset. Never synced to competitive servers. | In-flight path and timer state belong exclusively to the active device. |
| **Daily Challenge Local Participation** | Local Device | Durable in Room `daily_challenge`. Recorded with `LOCAL_COMPLETION` status and exact UTC dateKey. | Local participation is preserved indefinitely for streaks and calendar history. | Offline daily participation is never deleted even if submitted outside the competitive window. |
| **Daily Challenge Verification & Leaderboard** | Backend Server | Displayed with local verification badges (`Local Completion`, `Provisional`, `Server-Validated`, `Leaderboard-Eligible`). | Backend verifies start token, path legality, timing window, and cryptographic fingerprint. Client queue cannot self-verify. | Leaderboard fairness requires authoritative server validation of attempts and deadlines. |
| **Competitive Multiplayer (Quick & Friend Duels, Mini Leagues)** | Backend Server | Ephemeral presentation and spectator state. Provisional local outcomes discarded on reconnect. | Authoritative server state fetched via WebSocket and REST APIs. Client queue CANNOT invent or finalize competitive outcomes. | Anti-cheat protection and synchronization of simultaneous real-time match results. |
| **Premium Subscriptions & Purchases** | Google Play / Backend Billing | Bounded offline cache with 7-day TTL (`MAX_OFFLINE_CACHE_WINDOW_MS`). Cached status allows offline access to downloaded packs. | Authoritative billing verification through Google Play Developer API. Local sync operations CANNOT create premium entitlements. | Prevents piracy and unauthorized entitlement fabrication. |
| **Premium Puzzle Pack Progress** | Local Device | Durable in Room `premium_pack_progress`. Preserved even after subscription expires. | Synced to authenticated player account. Gated content locked on expiry, but earned stars and completions remain permanent. | Players never lose the historical record of what they achieved while subscribed. |
| **Rewarded Hint Grants** | Backend / Server Verification | Stored locally in DataStore preferences upon validated callback. | Server validates reward token. Idempotent grant tracking prevents duplicate credits from repeated sync retries. | Prevents unlimited hint farming through offline replay of ad reward intents. |
| **Friends, Invites & Social Roster** | Backend Server | Cached locally for offline browsing. | Backend-authoritative. Friend requests, blocks, and presence updates reconcile against central social service. | Social relationships require mutual consent and live account resolution. |
| **Device-Local Settings (Audio, Haptics, Input Mode)** | Local Device | Stored in Jetpack DataStore Preferences. | Never overwritten by remote account profile changes. | Device hardware ergonomics belong to the physical device. |
| **Account Privacy & Public Profile Settings** | Backend Server | Cached locally for fast UI presentation. | Backend-authoritative. Update operations queued and synced idempotently. | Privacy and discovery controls must apply globally across all player interactions. |

---

## 3. Strict Prohibitions

1. **No Client-Authoritative Competitive Results:** An offline synchronization queue must never submit match outcomes or rank modifications for multiplayer duels or leagues.
2. **No False Verification:** An unverified offline daily challenge solve must never be marked `VERIFIED` or inserted into the global leaderboard without server validation.
3. **No Entitlement Creation via Sync:** Synchronization payloads can never grant, extend, or restore premium subscriptions; entitlements must flow solely through billing verification.
4. **No Destructive Reversion:** A remote record indicating 0 completions or missing level progress must NEVER overwrite a durable local record of completed levels.
