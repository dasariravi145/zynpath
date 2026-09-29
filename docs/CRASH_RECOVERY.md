# Zynpath: Crash Recovery and Gameplay State Restoration

## 1. Principles of Gameplay Durability

Zynpath enforces durable persistence of player progress and active puzzle sessions:
1. **Never rely on uncaught exception handlers at crash time:** Application state must be persisted as meaningful domain events occur rather than trying to perform emergency database writes during a crash or SIGKILL.
2. **Commit before celebration:** Level completion records must be durably committed in Room before transitioning the UI into celebratory or success states. This guarantees that an animation crash or low-memory killer event immediately upon completion never costs the player their victory.
3. **Isolate snapshot failures from completed records:** If an active session snapshot is corrupted or incompatible, existing completed level records and personal bests are strictly preserved.

---

## 2. Gameplay Session Snapshot Model

Snapshots are stored in Room (`game_sessions` table) and capture:
- `sessionId`: Unique UUID for the session.
- `ownerIdentity`: Guest identifier or authenticated account ID (enforces account isolation).
- `puzzleId` & `puzzleVersion`: Identity and schema version of the active puzzle.
- `currentPath`: Valid sequence of grid cell coordinates visited so far.
- `elapsedTimeMs`: Total active playing duration excluding pauses.
- `isPaused`: Boolean indicating pause state.
- `updatedAt`: Canonical UTC timestamp.

### Storage Throttle
Raw pointer touch drag samples are buffered in memory and only committed to Room when the path step changes or upon lifecycle events (`onPause`, `onStop`).

---

## 3. Snapshot Validation Architecture

Before a snapshot can be restored into active gameplay, `GameplaySessionValidator.validate()` validates the following rules:

1. **Puzzle Identity Match:** The saved `puzzleId` must match the puzzle being launched.
2. **Version Compatibility:** Schema version must match current engine rules.
3. **Session Ownership:** `ownerIdentity` in the snapshot must match the active player ID (or both be guest). Prevents session leakage across users on a shared device.
4. **Path Structural Integrity:**
   - Path must start at checkpoint 1.
   - All steps must be orthogonally adjacent (no diagonals, no teleports).
   - No duplicate visited cells (no revisits).
   - No blocked-edge (wall) crossings.
   - Checkpoints visited must strictly follow ascending numerical order.

If any check fails, restoration returns `SessionRestorationResult.Incompatible` with a specific `RestorationFailureReason` (e.g., `CORRUPTED_PATH_DATA`, `SESSION_OWNERSHIP_MISMATCH`).

---

## 4. Incompatible Snapshot Recovery Flow

When a snapshot fails validation:
1. The corrupted session row is removed from `game_sessions`.
2. A non-destructive diagnostic event is logged with `ZynpathDiagnostics`.
3. The UI presents an informative recovery message explaining that the previous unfinished attempt could not be resumed.
4. The player is provided with a **Start Fresh** option to begin the level with a clean board.
5. All previously completed level progress in `level_progress` remains intact.

---

## 5. Process Death and Device Restart

- **Process Death:** Android may kill background processes at any time. When returning, `SavedStateHandle` restores the level ID, and `GameplayViewModel` queries `GameplaySessionRepository` to restore the validated snapshot.
- **Device Restart:** All completed records and active snapshots are stored in Room on local flash storage, surviving reboots. Pending sync operations are managed by WorkManager with `ExistingWorkPolicy.KEEP` and device restart constraints.

---

## 6. Backend Crash Recovery & Graceful Shutdown (Prompt 44)
- **Graceful Shutdown Phase**: Backend is configured with `server.shutdown: graceful` and a 30-second shutdown phase timeout (`spring.lifecycle.timeout-per-shutdown-phase: 30s`). In-flight HTTP transactions and database operations are allowed to complete before termination.
- **In-Memory Match Restart Recovery**: Active multiplayer duel matches and matchmaking queues are ephemeral in-memory collections. If the backend process crashes or restarts:
  - Active WebSocket connections receive close code `1001` (Going Away).
  - Unfinalized in-memory matches are marked as aborted; the server never fabricates or estimates match results.
  - Clients reconnect with exponential backoff and resume gameplay cleanly.
- **Automated Health Probes**: Container orchestrators monitor `/actuator/health/liveness` and `/readiness` to detect deadlocks or database connectivity loss and automate process recovery without manual intervention.
