# Zynpath Sync Operation Model

**Milestone:** Phase 8 — Reliability & Data Integrity (Prompt 35)  
**Standard:** Durable Local Operation Queue & Distributed Idempotency  

---

## 1. Overview

In Zynpath, offline mutations are captured as persistent, durable operation records within the Room database (`sync_operations` table). Rather than relying on in-memory buffers or ephemeral job parameters, every syncable game event is written within a transactional local boundary.

---

## 2. Database Schema (`sync_operations`)

```sql
CREATE TABLE IF NOT EXISTS sync_operations (
    operationId TEXT NOT NULL PRIMARY KEY,
    ownerIdentity TEXT NOT NULL,
    operationType TEXT NOT NULL,
    payloadVersion INTEGER NOT NULL DEFAULT 1,
    resourceIdentity TEXT NOT NULL,
    payloadJson TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    attemptCount INTEGER NOT NULL DEFAULT 0,
    lastAttemptAt INTEGER DEFAULT NULL,
    lastError TEXT DEFAULT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX IF NOT EXISTS index_sync_operations_ownerIdentity_status 
ON sync_operations (ownerIdentity, status);

CREATE INDEX IF NOT EXISTS index_sync_operations_createdAt 
ON sync_operations (createdAt);
```

---

## 3. Operation Lifecycle & State Machine

```
              ┌──────────────┐
              │   PENDING    │◄─────────────────┐
              └──────┬───────┘                  │
                     │ (Eligible for sync)      │
                     ▼                          │
              ┌──────────────┐                  │ (Transient Error /
              │ IN_PROGRESS  │                  │  Network Offline)
              └──────┬───────┘                  │
                     │                          │
        ┌────────────┼──────────────────────────┼───────────────┐
        ▼            ▼                          ▼               ▼
 ┌──────────┐ ┌──────────────┐       ┌──────────────────┐ ┌──────────────┐
 │SUCCEEDED │ │  RETRYABLE   │       │    PERMANENT     │ │   CONFLICT   │
 │          │ │   FAILURE    ├───────┤     FAILURE      │ │   REQUIRES   │
 └──────────┘ └──────────────┘       │ (Malformed/Auth) │ │    ACTION    │
                                     └──────────────────┘ └──────────────┘
```

### State Definitions

1. **`PENDING`**: Newly enqueued operation awaiting connectivity or scheduled sync worker execution.
2. **`IN_PROGRESS`**: Operation currently included in an active batch submission. Increments `attemptCount` and records `lastAttemptAt`.
3. **`SUCCEEDED`**: Server authoritatively acknowledged the operation (`SUCCESS` or `IGNORED_DUPLICATE`). Eligible for automatic pruning after 7 days.
4. **`RETRYABLE_FAILURE`**: Network transport error, socket timeout, or HTTP 5xx server error. Re-eligible for subsequent background and foreground retry passes.
5. **`PERMANENT_FAILURE`**: Unrecoverable error (e.g., HTTP 400 Bad Request, unsupported schema version, invalid payload). Will not be retried automatically.
6. **`CONFLICT_REQUIRES_ACTION`**: Irreconcilable conflict requiring player decision (e.g. account collision during linking).

---

## 4. Operation Types & Payloads

### 4.1 `SOLO_LEVEL_COMPLETION`
- **Resource Identity:** `solo_level_{levelId}`
- **Payload Schema:**
```json
{
  "levelId": 42,
  "worldId": 2,
  "stars": 3,
  "bestTimeMs": 14250,
  "movesCount": 38,
  "isCompleted": true,
  "bestHintCount": 0,
  "completedAt": 1727419200000
}
```

### 4.2 `DAILY_CHALLENGE_SUBMISSION`
- **Resource Identity:** `daily_{dateKey}`
- **Payload Schema:**
```json
{
  "dateKey": "2026-09-27",
  "challengeId": "dc_20260927_seed12345",
  "fingerprint": "fp_a1b2c3d4e5",
  "solveTimeMs": 28400,
  "movesCount": 46,
  "completedAt": 1727419200000,
  "pathCoordinates": ["0,0", "0,1", "1,1", "1,2"]
}
```

### 4.3 `PREFERENCE_UPDATE`
- **Resource Identity:** `pref_{settingKey}`
- **Payload Schema:** Key-value map of syncable cloud settings.

---

## 5. Account Isolation & Migration

- **Strict Partitioning:** Every operation is tagged with `ownerIdentity` (either a stable guest UUID or authenticated player account ID).
- **Guest-to-Account Linking:** When a guest links to an authenticated account, pending operations are migrated atomically via:
  ```sql
  UPDATE sync_operations 
  SET ownerIdentity = :newOwner 
  WHERE ownerIdentity = :oldOwner AND status != 'SUCCEEDED'
  ```
- **Account Sign-Out:** Active queue owner reverts to guest UUID. Authenticated operations remain sealed under their account identity.
- **Account Deletion:** All sync operations belonging to the deleted account are purged immediately to prevent recreation or stale writes.

---

## 6. Retention and Pruning

To prevent unbounded SQLite table growth, `SyncCoordinator` executes a non-blocking cleanup pass following each successful batch synchronization:
```sql
DELETE FROM sync_operations 
WHERE status = 'SUCCEEDED' AND createdAt < :sevenDaysAgoTimestamp;
```
Failed operations are retained for diagnostics and manual inspection in developer/settings menus.
