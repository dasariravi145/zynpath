# Zynpath Offline-First Synchronization Architecture

**Milestone:** Phase 8 — Reliability & Data Integrity (Prompt 35)  
**Standard:** Offline-First, Durable Operations, Idempotent Reconciliation  

---

## 1. Overview & Core Philosophy

Zynpath is designed from the ground up as an **offline-first** puzzle application:
- A player must be able to launch the app, play all 300 bundled Solo levels, draw paths, undo, reset, use offline hints, achieve personal bests, and complete Daily Challenges without any network connection.
- Network connectivity is treated as an enhancement for social discovery, multiplayer duels, leaderboard competition, and cloud backup—never as a hard prerequisite for core gameplay.
- Any progress made while offline is committed durably to local Room storage before being enqueued for asynchronous cloud synchronization.

---

## 2. Synchronization Architecture

```
┌────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                   │
│         (Gameplay, Level Select, Daily, Settings)      │
└───────────────────────────┬────────────────────────────┘
                            │ Read & Write
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Domain Repositories                  │
│   (ProgressRepository, DailyChallengeRepository, etc.) │
└──────────────┬─────────────────────────┬───────────────┘
               │ Local Transaction       │ Enqueue Operation
               ▼                         ▼
┌───────────────────────────┐ ┌──────────────────────────┐
│   Room Database Storage   │ │   Sync Operation Queue   │
│ (level_progress, daily,   │ │    (sync_operations)     │
│   game_sessions, stats)   │ │                          │
└───────────────────────────┘ └────────────┬─────────────┘
                                           │
                                           ▼
┌────────────────────────────────────────────────────────┐
│              Central SyncCoordinator                   │
│   - Observes NetworkConnectivityMonitor (online/off)   │
│   - Enforces Concurrency Lock (Mutex)                  │
│   - Tracks 5 Sync States (Saved, Waiting, Syncing,     │
│       Synced, Action Required)                         │
└──────────────┬─────────────────────────┬───────────────┘
               │ Background Trigger      │ Foreground Trigger
               ▼                         ▼
┌───────────────────────────┐ ┌──────────────────────────┐
│   WorkManager SyncWorker  │ │ User / Event Sync Call   │
│  (Periodic & Expedited)   │ │  (triggerSync / Resume)  │
└──────────────┬────────────┘ └──────────┬───────────────┘
               │                         │
               └────────────┬────────────┘
                            │ REST / JSON (Batch)
                            ▼
┌────────────────────────────────────────────────────────┐
│            Spring Boot 3 Modular Monolith              │
│                 SyncController / Batch                 │
│  - Idempotent Operation Deduplication (stable UUID)    │
│  - Non-destructive Level Progress Reconciliation       │
│  - Provisional Daily Challenge Processing              │
└────────────────────────────────────────────────────────┘
```

---

## 3. Centralized Synchronization Coordinator (`SyncCoordinator`)

Rather than allowing each UI screen to trigger independent, uncoordinated network calls, all sync activities pass through a singleton `SyncCoordinator`:
- **Concurrency Control:** Utilizes a Kotlin coroutine `Mutex` to prevent race conditions or duplicate parallel batch executions.
- **Connectivity Awareness:** Reacts instantly to `NetworkConnectivityMonitor` transitions. When device gains internet access, pending eligible work is triggered automatically.
- **State Reporting:** Exposes `val syncStatus: StateFlow<SyncStatus>`, driving the UI `SyncStatusBadge` across the application.
- **Account Binding:** Automatically migrates and partitions queues upon guest linking, account sign-in, or sign-out.

---

## 4. Background Synchronization via WorkManager

- **`SyncWorker`:** A `CoroutineWorker` that injects `SyncCoordinator` via Hilt's `EntryPointAccessors`.
- **`SyncScheduler`:** Enqueues:
  1. **Periodic Work:** Scheduled every 6 hours with a 30-minute flex interval. Enforces `NetworkType.CONNECTED` and `setRequiresBatteryNotLow(true)`.
  2. **Expedited One-Time Work:** Enqueued upon network reconnection or application backgrounding.
  3. **Boot Restoration:** `BootCompletedReceiver` restores periodic WorkManager sync scheduling upon system restart.

---

## 5. Retry Policy & Exponential Backoff

To prevent battery drain and respect backend availability during outages:
- **Retryable Failures:** Network transport drops, DNS timeouts, or HTTP 5xx errors trigger bounded exponential backoff (e.g. 15s, 30s, 60s, up to 5 attempts).
- **Non-Retryable Failures:** HTTP 400 Bad Request, schema mismatch, or permanent rejection marks operations as `PERMANENT_FAILURE` and transitions UI state to `ACTION_REQUIRED`.
- **Authentication Expiration:** HTTP 401 Unauthorized pauses account synchronization and alerts the player to re-authenticate without deleting pending local gameplay progress.

---

## 6. Partial Batch Failure Resilience

When a batch of 50 operations is submitted:
- The backend evaluates each operation independently and returns an individual `SyncOperationResultDto`.
- Succeeded operations are marked `SUCCEEDED` and immediately acknowledged.
- Failed operations are marked `RETRYABLE_FAILURE` or `PERMANENT_FAILURE`.
- Subsequent sync attempts retry ONLY remaining eligible operations, completely avoiding redundant re-transmission of already processed work.

---

## 7. Security Hardening & Rate Limiting (Prompt 36)

- **Offline Solo Independence**: Backend security hardening never imposes online authentication or verification barriers on local offline Solo gameplay. The local Room database remains the immediate authoritative source for offline progression.
- **Authentication & Ownership Binding**: Batch synchronization endpoints (`POST /api/v1/sync/batch`) enforce `@RequireAccess(AUTHENTICATED)`. The backend applies sync operations exclusively to the authenticated account resolved from `SecurityContext.getCurrentPlayerId()`.
- **Sync Batch Rate Limiting**: Governed by `RateLimitPolicy.DEFAULT_AUTHENTICATED` (120 req/min per account), preventing rogue clients from flooding the synchronization endpoint.
- **Payload Sanitization & Safe Auditing**: `SecurityAuditLogger` records synchronization passes using high-level operation counts and correlation identifiers, avoiding raw JSON payload dumps to eliminate sensitive data leakage.

---

## 11. Performance, Battery Efficiency & Deduplication (Prompt 37)
- **WorkManager Battery Constraints**: Background sync triggers only when `BatteryNotLow == true` and `NetworkType.CONNECTED` is satisfied, preventing battery drain during low battery states.
- **Bounded Batch Sizing**: Sync passes bundle at most 50 pending operations per network dispatch, keeping payload sizes small and memory bounded.
- **Exponential Retry Backoff**: Sync failures use exponential backoff starting at 30 seconds up to a maximum of 5 hours, eliminating retry storms during server outages.
- **Room Index Optimization**: The `sync_operations` table utilizes compound indexes on `(ownerIdentity, status)` and `createdAt` to ensure polling for pending operations does not require full table scans.

---

## 12. Outage Handling & Durable Interruption Recovery (Prompt 38)
- **Partial Sync Failure Handling**: When a batch sync payload is partially processed by the backend, successfully acknowledged operations are immediately marked `SUCCEEDED`. Only failed operations remain in `RETRYABLE_FAILURE` state. Cursors never advance past uncommitted data.
- **Backend Outage Durability**: During server outages (HTTP 502/503/504 or network timeouts), pending sync operations remain safely persisted in Room. The local queue is never cleared upon an unknown request outcome.
- **Account-Scoped Queueing**: Operations in `sync_operations` are strictly bound to `ownerIdentity`. When a user signs out, active sync workers are halted, and operations for one account are never transmitted under a different account's session.

---

## 13. Offline Achievement Persistence & Synchronization (Prompt 41)
- **Immediate Local Persistence**: All eligible achievements earned during offline Solo play unlock instantly and are committed durably to Room (`achievements` table).
- **Idempotent Synchronization**: Achievements use permanent, immutable string identifiers (`solo_first_step`, `world_one_pioneer`, etc.). When synchronizing with the cloud, operations are strictly idempotent:
  - Unlocked achievements on the client are merged with cloud records using a union strategy (`isUnlocked = local.isUnlocked || remote.isUnlocked`).
  - Stale remote snapshots can never un-earn or erase a locally unlocked achievement.
- **Guest-to-Linked Migration**: When an offline guest links to Google or Facebook, all earned achievements remain preserved in local Room storage and are authoritatively merged into the newly linked cloud profile.

---

## 14. Production Database Schema & Idempotent Sync Processing (Prompt 44)
- **PostgreSQL Persistence Target**: Backend sync endpoints persist data into the relational PostgreSQL schema initialized via Flyway migrations (`V1`–`V6`).
- **Atomic Transaction Boundaries**: Each sync batch is executed within an atomic database transaction. If an individual operation suffers an unexpected database constraint error, the transaction fails safely without partial corruption.
- **Idempotency Safeguards**: Sync operations carry unique client operation IDs (`sync_op_id`). The backend prevents double-processing of duplicate retries by maintaining processed operation fingerprints.
- **Graceful Shutdown Resilience**: In-flight sync batches are allowed to conclude cleanly during the 30-second graceful shutdown window, avoiding incomplete sync records in the backend.

---

## 15. Offline Persistence & Isolation QA Verification (Prompt 46)

Offline operations, durable snapshot recovery, and account isolation are verified in:
- [`OfflinePersistenceAndRecoveryTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/database/OfflinePersistenceAndRecoveryTest.kt): Verifies that local level progress, best times, and session snapshots persist across simulated process restart, and that clearing local session data does not damage durable progress.
- See detailed report in [`docs/OFFLINE_PERSISTENCE_TEST_REPORT.md`](file:///d:/Zynpath/docs/OFFLINE_PERSISTENCE_TEST_REPORT.md).

---

## 16. Backend Offline Synchronization Verification (Prompt 47)

### 16.1 Test Execution Summary (`SyncIntegrationTest`)
- **Suite**: `com.zynpath.backend.sync.SyncIntegrationTest`
- **Total Tests**: 2 / 2 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `syncBatch_withSoloLevelCompletion_shouldProcessAndAcknowledge`: Confirms that offline Solo level completions and star ratings are processed idempotently and stored in backend persistence.
  2. `syncBatch_withDuplicateOperations_shouldBeIdempotent`: Verifies that sending the exact same sync operation repeatedly results in idempotent acknowledgment without duplicating records or corrupting database counters.

### 16.2 Related Test Reports
- Complete execution evidence recorded in [`docs/SYNCHRONIZATION_TEST_REPORT.md`](file:///d:/Zynpath/docs/SYNCHRONIZATION_TEST_REPORT.md).

