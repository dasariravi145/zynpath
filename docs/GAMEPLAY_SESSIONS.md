# Zynpath Gameplay Session Architecture & Local Restoration Specification

**Document Version:** 1.0.0  
**Phase:** Phase 3 — Interactive Gameplay  
**Milestone:** Prompt 13/50  
**Status:** Authoritative  

---

## 1. Overview & Objective

Zynpath gameplay requires reliable session management so a player can start a puzzle, pause it, leave the application, rotate the device, survive backgrounding and process termination, and safely resume the same verified puzzle with the same valid path.

Key design principles:
1. **Never restore an unverified path:** Stored paths are never blindly assigned to the ViewModel or Canvas. Every saved path is replayed step-by-step through the authoritative pure-Kotlin `PuzzleEngine`.
2. **Strict Identity Binding:** Sessions are bound to immutable tuples: `(sessionId, levelId, puzzleId, puzzleVersion, catalogVersion)`. A session cannot be resumed on a different puzzle or a modified puzzle version.
3. **One Active Solo Session:** Only the currently opened puzzle is `ACTIVE`. When switching levels or backgrounding, in-progress sessions are safely transitioned to `PAUSED`.
4. **Monotonic Revisions & Stale Save Protection:** Writes are serialized and guarded by monotonically increasing revision numbers, preventing asynchronous out-of-order saves from overwriting newer user actions.
5. **Idempotent Completion:** Genuine engine-validated victory stops active timers, marks the session `COMPLETED`, updates personal bests, and prevents duplicate completion records.

---

## 2. Session Lifecycle & Status Transitions

```
         [Enter Level]
               │
               ▼
        ┌─────────────┐
        │ NOT_STARTED │
        └──────┬──────┘
               │ First accepted StartPath at Checkpoint #1
               ▼
        ┌─────────────┐   onPause / onAppBackgrounded   ┌─────────────┐
        │   ACTIVE    │ ───────────────────────────────>│   PAUSED    │
        │             │ <────────────────────────────── │             │
        └──────┬──────┘            onResume             └─────────────┘
               │
               ├─────────────────────────────────────────┐
               │ ResetPath                               │ Dual Win Condition Met
               ▼                                         ▼
        ┌─────────────┐                           ┌─────────────┐
        │ NOT_STARTED │                           │  COMPLETED  │
        └─────────────┘                           └─────────────┘
```

### Authoritative Session Statuses (`SessionStatus`):
| Status | Meaning | Resumable? |
|---|---|---|
| `NOT_STARTED` | Session created, initial puzzle board loaded, player has not made first move. | Yes (starts at #1) |
| `ACTIVE` | Player is actively drawing; monotonic active timer is running. | Yes |
| `PAUSED` | Player paused, backgrounded, or navigated away; timer frozen; path preserved. | Yes |
| `COMPLETED` | Level completed with dual win condition verified; read-only; cannot be resumed. | No |
| `ABANDONED` | Explicitly abandoned or invalidated by player action. | No |
| `RESTORATION_FAILED` | Corrupted or version-mismatched snapshot preserved for local diagnostics. | No (fresh session started) |

---

## 3. Session Identity & Room Entity

### Table: `game_sessions` (Schema Version 3)
```sql
CREATE TABLE IF NOT EXISTS game_sessions (
    sessionId TEXT NOT NULL PRIMARY KEY,
    levelId INTEGER NOT NULL,
    worldId INTEGER NOT NULL,
    puzzleSeed INTEGER NOT NULL DEFAULT 0,
    startedAt INTEGER NOT NULL,
    lastUpdatedAt INTEGER NOT NULL,
    elapsedActiveTimeMs INTEGER NOT NULL,
    status TEXT NOT NULL,
    pathSnapshot TEXT DEFAULT NULL,
    moveCount INTEGER NOT NULL,
    hintCount INTEGER NOT NULL,
    puzzleId TEXT NOT NULL DEFAULT '',
    puzzleVersion INTEGER NOT NULL DEFAULT 1,
    catalogVersion TEXT NOT NULL DEFAULT '1.0.0',
    revision INTEGER NOT NULL DEFAULT 1,
    snapshotSchemaVersion INTEGER NOT NULL DEFAULT 1
);
```

### Fields:
- `sessionId`: UUID string (`UUID.randomUUID().toString()`).
- `levelId`: Target level integer (1..300).
- `worldId`: Target world integer (1..6).
- `puzzleId`: Stable puzzle identity string from catalog manifest (e.g. `w1_l001_s1001`).
- `puzzleVersion`: Integer schema version of the puzzle definition (default 1).
- `catalogVersion`: Semantic version string of the catalog (default `1.0.0`).
- `revision`: Monotonically increasing `Long` counter incremented on every path modification, undo, reset, pause, or status transition.
- `snapshotSchemaVersion`: Serialization format version (currently `1`).
- `pathSnapshot`: Semicolon-delimited coordinate string (`0,0;0,1;0,2`).

---

## 4. Path Serialization Format (`SessionPathSerializer`)

Coordinates are serialized deterministically without loss of row/column distinction:
- Format: `"${row},${col};${row},${col};..."`
- Example: `"0,0;0,1;0,2;1,2;1,1"`
- Empty or blank string deserializes to `emptyList<GridPosition>()`.
- Tokens are strictly validated during deserialization. Any non-integer or malformed pair throws `IllegalArgumentException`, caught cleanly by the restoration validator.

---

## 5. Restoration Validation Pipeline (`GameplaySessionValidator`)

Before restoring any saved session, the validator executes an 8-point authoritative verification check:

```
[Load Saved Snapshot]
         │
         ▼
 1. levelId matches expected level?
         │ Yes
         ▼
 2. puzzleId matches catalog definition?
         │ Yes
         ▼
 3. puzzleVersion matches definition version?
         │ Yes
         ▼
 4. snapshotSchemaVersion supported (== 1)?
         │ Yes
         ▼
 5. status is Resumable (NOT_STARTED, ACTIVE, PAUSED)?
         │ Yes
         ▼
 6. Path is empty? ───> [Success: return initial engine state]
         │ No
         ▼
 7. Path[0] == definition.startPosition (Checkpoint #1)?
         │ Yes
         ▼
 8. Replay all consecutive moves through PuzzleEngine.process(ExtendPath):
    - Orthogonal adjacency?
    - Boundary checks?
    - Wall compliance (BlockedEdge)?
    - No self-intersection (cycles)?
    - Checkpoint sequence order?
    - No premature final checkpoint entry?
         │ All Accepted
         ▼
 9. Reconstructed path == Stored path?
         │ Yes
         ▼
 [SessionRestorationResult.Success]
```

If any step fails, the validator returns `SessionRestorationResult.Invalid(snapshot, reason, detailMessage)`.  
The repository marks the existing record `RESTORATION_FAILED` (preserving diagnostics) and launches a fresh clean session at Checkpoint #1 without crashing or displaying corrupt state.

---

## 6. Persistence Strategy & Write Ordering

### Persistence Triggers:
1. **Checkpoint Reached:** Flushed immediately to Room.
2. **Continuous Drag Moves:** Conflated and debounced (300ms window).
3. **Pointer Released:** Flushed immediately to Room.
4. **Undo / Reset:** Flushed immediately to Room.
5. **Pause / Resume:** Flushed immediately to Room.
6. **App Backgrounding (`ON_PAUSE` / `ON_STOP`):** Flushed immediately to Room.
7. **Navigating Away / Popping Screen:** Flushed immediately to Room.
8. **Puzzle Completion:** Flushed immediately to Room with status `COMPLETED`.

### Potential Data Loss Window:
- **Normal Lifecycle Transitions (Background, Screen Lock, Navigation):** Zero loss. State is flushed synchronously during Android lifecycle callbacks.
- **Catastrophic Process Termination (SIGKILL / Kernel OOM during active continuous drag):** Maximum potential loss window is bounded to $\le 300\text{ ms}$ (at most a few intermediate drag cells since the last checkpoint).

### Write Ordering Guarantee:
In `GameplaySessionRepositoryImpl`:
- Mutex serializes database transactions.
- Writes inspect existing `(revision, status)`.
- Rejects any write where `existing.revision >= incoming.revision` for the same status.
- Strictly rejects overwriting a `COMPLETED` session with an active or paused snapshot.

---

## 7. Room Migration (Version 2 to 3)

`MIGRATION_2_3` safely alters `game_sessions`:
```sql
ALTER TABLE game_sessions ADD COLUMN puzzleId TEXT NOT NULL DEFAULT '';
ALTER TABLE game_sessions ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1;
ALTER TABLE game_sessions ADD COLUMN catalogVersion TEXT NOT NULL DEFAULT '1.0.0';
ALTER TABLE game_sessions ADD COLUMN revision INTEGER NOT NULL DEFAULT 1;
ALTER TABLE game_sessions ADD COLUMN snapshotSchemaVersion INTEGER NOT NULL DEFAULT 1;
```
Existing player progress, stars, completion counts, player statistics, and daily challenges are 100% preserved.

---

## 8. Hint Engine & Session Restoration Boundary (Prompt 14)

1. **Ephemeral Hint Presentations**: Visual hint highlights (`hintedCoordinate`) and hint dialog states are strictly in-memory UI transient states. They are **never** serialized to the database or restored upon resuming a session.
2. **Session Resumed State**: When a session is resumed, `hintedCoordinate` is initialized to `null`. The player must tap the Hint button again if they wish to receive guidance.
3. **Usage Accounting Isolation**: Remaining free hints are tracked authoritatively in `UserPreferences` (`DataStore`) and session-level hint counts are accumulated during the active run. Restoring a session does **not** grant additional free hints.
4. **No Solver Task Persistence**: In-progress asynchronous solver jobs are cancelled upon session backgrounding and are never persisted to disk.

---

## 9. Replay & Next Level Session Transitions (Prompt 15)

1. **Replay Session Lifecycle**:
   - Replaying a completed or in-progress level creates a fresh session snapshot for the same `(levelId, puzzleId, puzzleVersion)`.
   - The elapsed active timer is reset to zero.
   - Historical completion records (`isCompleted = true`, `bestTimeMs`, `stars`, `completionCount`) in `LevelProgressEntity` remain completely intact.
2. **Next Level Session Initialization**:
   - The completed session is committed as `SessionStatus.COMPLETED` prior to navigation.
   - Next level navigation verifies the catalog definition and progression unlock gate before launching a new independent session.
3. **Dual Input Mode Persistence**:
   - The choice of Drag vs. Tap mode is stored in `UserPreferences` and does not mutate session revision numbers or reset active paths.

---

## 10. Daily Challenge Session Isolation & Expiration (Prompt 16)

1. **Session Key Isolation**:
   - Daily Challenge sessions are explicitly keyed as `sessionId = "daily_${challengeId}"` (e.g. `daily_daily-2026-09-26-v1`).
   - Solo level progress and Daily Challenge sessions never collide or overwrite each other in `game_sessions`.
2. **Cross-Day Expiration Policy**:
   - If the player leaves an in-progress Daily Challenge and the canonical UTC date rolls over to a new calendar day, the previous day's session is marked `EXPIRED`.
   - The old partial attempt is **never** restored onto today's challenge.
3. **Restoration Verification**:
   - On resuming today's challenge, the saved snapshot is matched against `(puzzleId, puzzleVersion)`.
   - The serialized path is verified by replaying through the authoritative `PuzzleEngine` before UI presentation.
4. **Competitive Fairness**:
   - In `GameMode.DAILY_CHALLENGE`, hints are strictly disabled. Active gameplay timer records only true interaction duration.

