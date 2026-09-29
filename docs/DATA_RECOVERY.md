# Zynpath Data Recovery & Failure Resilience

**Milestone:** Phase 8 — Reliability & Data Integrity (Prompt 35)  
**Standard:** Comprehensive Failure Recovery & Zero Progress Loss  

---

## 1. Overview

Mobile environments present extreme operating conditions: abrupt process termination by Android OS memory managers, dead zones with sudden connectivity drops, app updates, and device restarts. Zynpath provides deterministic data recovery guarantees across all critical failure modes.

---

## 2. Failure Recovery Scenarios

### 2.1 Process Termination Mid-Gameplay
- **Behavior:** The active path in progress is flushed to `game_sessions` on every path segment addition and pause.
- **Recovery:** Upon reopening the level, `GameplaySessionRepository.getResumableSession(levelId)` restores the exact orthogonal path, elapsed active time, and pause state.
- **Completed Level Protection:** Completed levels are permanently recorded in `level_progress`. A process kill can never revoke an already completed level.

### 2.2 Process Termination During Cloud Synchronization
- **Behavior:** Operations in progress in `sync_operations` have status `IN_PROGRESS` with timestamp.
- **Recovery:** Upon next app launch or WorkManager pass, `SyncOperationDao.getEligibleOperations` re-selects any operation not marked `SUCCEEDED`. Because the backend is idempotent, re-sending an operation that the server already processed returns `IGNORED_DUPLICATE` and safely completes without duplicate progress.

### 2.3 Expired Authentication Tokens
- **Behavior:** When an account token expires, calls to `/api/v1/sync/batch` return HTTP 401.
- **Recovery:** `SyncCoordinator` transitions state to `ACTION_REQUIRED` ("Authentication required").
- **Safety Guarantee:** Local Solo progress and pending operations are NEVER deleted. They remain safely queued until the player signs in again or refreshes credentials.

### 2.4 Corrupted or Incompatible Active Session Snapshot
- **Behavior:** If a persisted session snapshot contains corrupted JSON or a higher `snapshotSchemaVersion` from a future version:
- **Recovery:** `GameplaySessionSnapshot.fromEntity()` safely catches deserialization exceptions, returns an empty path, and marks the session `RESTORATION_FAILED`.
- **Integrity Guarantee:** The corrupted session is safely abandoned, and the player can restart the level cleanly without crashing the app or corrupting other levels.

### 2.5 Temporary Backend Outage (HTTP 502/503/504)
- **Behavior:** The device receives a gateway timeout or server error.
- **Recovery:** Operations are marked `RETRYABLE_FAILURE`. WorkManager and `SyncCoordinator` apply exponential backoff (15s, 30s, 60s, up to max attempts). The player continues playing offline seamlessly.

### 2.6 Guest-to-Account Linking Collision
- **Behavior:** A guest attempts to link to a Google/Facebook account that is already associated with an existing player.
- **Recovery:** Backend returns HTTP 409 `ACCOUNT_LINK_CONFLICT`. `AuthRepository` displays the collision resolution flow (Prompt 18 / 32). Guest data is preserved locally and is NOT discarded or merged with an unrelated stranger's identity.

### 2.7 Database Schema Migration Failures
- **Policy:** Destructive fallback (`fallbackToDestructiveMigration()`) is **strictly forbidden**.
- **Implementation:** Every schema update uses explicit, incremental Room `Migration` objects (`MIGRATION_1_2` through `MIGRATION_10_11`) with automated tests verifying that all existing tables, columns, and player achievements are preserved without data loss.

---

## 3. Local Storage Failure Policy

If local flash memory is completely full or SQLite encounters an I/O error (`LOCAL_STORAGE_FAILURE`):
1. **No False Success:** The application will NEVER show "Saved on this device" or "Completed" if the underlying Room write did not succeed.
2. **Clear Error Presentation:** The UI displays an actionable notification alerting the player that device storage is full.
3. **No Automatic Database Destruction:** The database file is never silently wiped or reset upon an I/O failure.

---

## 4. Non-Destructive Performance & Account Isolation Indexing
- `MIGRATION_9_10` adds compound indexes (`CREATE INDEX IF NOT EXISTS`) without altering table columns, deleting records, or dropping constraints.
- `MIGRATION_10_11` adds `ownerIdentity` column and index on `game_sessions(ownerIdentity)` for strict cross-account session isolation.
- Existing player levels, stats, daily completions, active sessions, and queued operations are 100% preserved.

---

## 5. Crash Recovery & Resilience Architecture (Prompt 38)
For detailed specifications of the unified error taxonomy, snapshot validation algorithms, home screen resumption, and backend circuit breaking, see:
- [CRASH_RECOVERY.md](file:///d:/Zynpath/docs/CRASH_RECOVERY.md)
- [ERROR_HANDLING.md](file:///d:/Zynpath/docs/ERROR_HANDLING.md)
- [RESILIENCE_MATRIX.md](file:///d:/Zynpath/docs/RESILIENCE_MATRIX.md)
- [FAILURE_RECOVERY_POLICY.md](file:///d:/Zynpath/docs/FAILURE_RECOVERY_POLICY.md)
- [ERROR_DIAGNOSTICS.md](file:///d:/Zynpath/docs/ERROR_DIAGNOSTICS.md)

---

## 6. Backend Disaster Recovery, Backups & Controlled Restores (Prompt 44)
- **Production PostgreSQL Backup Cadence**: Real-time continuous WAL streaming (Point-in-Time Recovery) with daily full logical snapshots (`pg_dump -Fc`) archived to encrypted cloud storage with 30-day retention.
- **Destructive Restore Safeguards**: Overwriting production database state requires written dual-authorization and manual confirmation (`--force-destructive-restore`). Automatic database wipes upon failure are strictly forbidden.
- **Flyway Transactional Rollback**: Relational migrations (`V1`–`V6`) execute within PostgreSQL transactions; syntax or constraint errors fail the migration and roll back cleanly without data loss.
- **Detailed Specifications**: See [BACKUP_AND_RESTORE.md](file:///d:/Zynpath/docs/BACKUP_AND_RESTORE.md) and [ROLLBACK_RUNBOOK.md](file:///d:/Zynpath/docs/ROLLBACK_RUNBOOK.md).


