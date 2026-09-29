# Zynpath Conflict Resolution Policies

**Milestone:** Phase 8 — Reliability & Data Integrity (Prompt 35)  
**Standard:** Domain-Specific Reconciliations Over Generic Last-Write-Wins  

---

## 1. Principle: Rejection of Unconditional Last-Write-Wins (LWW)

A naive "last-write-wins" approach in distributed mobile games causes devastating data loss:
- A player solves 50 levels on an offline airplane. Upon reconnecting, if an outdated cloud record with 0 completed levels has a clock timestamp newer due to clock skew, the player's hard-earned progress is permanently erased.
- A player achieves a blazing 5-second personal best offline. Reconnecting to a server with a 15-second time could overwrite the record if simple timestamp comparison is used.

**Zynpath Rule:** Every domain category has an explicit, mathematically sound, non-destructive reconciliation policy implemented in `SyncConflictPolicy.kt`.

---

## 2. Comprehensive Conflict Resolution Matrix

| Domain Category | Conflict Scenario | Reconciliation Algorithm | Outcome Guarantee |
|---|---|---|---|
| **Solo Level Completion** | Local says Completed; Remote says Incomplete | `mergedCompleted = local.isCompleted \|\| remote.isCompleted` | Completed state is strictly monotonic. Once completed, a level is NEVER marked incomplete. |
| **Solo Unlock State** | Local has Level unlocked; Remote has Level locked | `mergedUnlocked = local.isUnlocked \|\| remote.isUnlocked` | Unlocked levels remain playable. Never relocks accessible worlds. |
| **Solo Stars Earned** | Local has 3 stars (0 hints); Remote has 1 star (2 hints) | `mergedStars = maxOf(local.stars, remote.stars)` | Player retains highest earned star achievement. |
| **Solo Solve Time (PB)** | Local: 4,200 ms; Remote: 3,800 ms | `mergedTime = minOf(valid local > 0, valid remote > 0)` | Fastest positive solve time is preserved. Zero or negative times never overwrite valid records. |
| **Solo Move Count (PB)** | Local: 18 moves; Remote: 22 moves | `mergedMoves = minOf(valid local > 0, valid remote > 0)` | Lowest valid move count is preserved. |
| **Solo Hint Count (PB)** | Local: 0 hints; Remote: 1 hint | `mergedHints = minOf(local.bestHintCount, remote.bestHintCount)` | Minimal hint usage preserved. |
| **First Completion Date** | Local: timestamp T1; Remote: timestamp T2 | `mergedFirstAt = minOf(non-null T1, non-null T2)` | Earliest historical completion date is permanently preserved. |
| **Replay Sessions** | Active replay session in progress vs completed level | Active session is tracked independently in `game_sessions`. Completed level record is untouched until validated victory is achieved. | Replays cannot degrade or invalidate existing personal bests. |
| **Daily Challenge Completion** | Local solved offline; Remote attempt incomplete | Local completion preserved in Room `daily_challenge`. Server marks provisional sync without polluting timed competitive board. | Player keeps calendar streak and badge; leaderboard remains protected. |
| **Daily Challenge Verification** | Local: `LOCAL_COMPLETION`; Remote: `LEADERBOARD_ELIGIBLE` | Promoted to `LEADERBOARD_ELIGIBLE` only upon explicit cryptographic server acknowledgement. | Server verification authority strictly enforced. |
| **Competitive Multiplayer** | Local disconnected during duel; Server recorded loss | Server outcome is authoritative. Client adopts finalized match record from server. | No offline fabrication of competitive wins. |
| **Premium Entitlements** | Local cached entitlement vs expired Google Play response | Local bounded cache respected up to 7-day TTL (`MAX_OFFLINE_CACHE_WINDOW_MS`). Once expired, gated features locked. Completed pack progress in `premium_pack_progress` retained permanently. | Zero entitlement piracy; 100% progress preservation. |
| **Account Privacy Controls** | Local toggle changed offline vs remote privacy settings | Remote backend settings are authoritative. Local change is queued as an idempotent operation with server confirmation. | Privacy and discovery rules remain globally consistent. |
| **Cosmetic Selection** | Device A equipped Theme A; Device B equipped Theme B | Last-applied preference with server acknowledgement. | Safe non-destructive preference sync across devices. |
| **Notification Preferences** | Local toggle modified offline | Synced to backend notification service upon connectivity. | Push notifications respect player consent. |

---

## 3. Clock Skew Resilience

1. **Device Clocks are Untrusted for Competition:** Device system time can be manipulated (e.g. changing phone clock backwards). Competitive match ordering, daily attempt submission windows, and leaderboard rankings use **UTC server timestamps exclusively**.
2. **Local Solo Progression is Tolerant:** Valid offline solo puzzle solutions are checked against puzzle geometry and checkpoint rules, NOT clock validity. A player with an inaccurate device clock is never penalized in Solo mode.
