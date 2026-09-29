# Zynpath: Daily Challenge Online & Shared Daily Puzzles

## 1. Overview & Architecture
The Daily Challenge system in Zynpath bridges offline-first puzzle play with server-authoritative competitive multiplayer.
Every player participating on the same UTC calendar date receives the identical canonical puzzle selected by the backend schedule.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Zynpath Daily Competition                       │
└────────────────────────────────────────────────────────────────────────┘
          │                                            │
   (Online Authenticated)                      (Offline / Guest)
          │                                            │
          ▼                                            ▼
 Backend /api/daily/attempt/start             Local Packaged Catalog
          │                                            │
  Server-issued Attempt ID                     Local Provisional Session
  Server Start Timestamp                       Local Timer & History
          │                                            │
          ▼                                            ▼
   Board Gameplay (No Hints)                    Board Gameplay (No Hints)
          │                                            │
          ▼                                            ▼
 Server Path & Time Validation                Local Solver Validation
          │                                            │
          ▼                                            ▼
 LEADERBOARD_ELIGIBLE / SERVER_VALIDATED      PROVISIONAL / LOCAL_COMPLETION
 Official Daily Leaderboard                   Local History & Streaks Preserved
```

## 2. UTC Date Policy
- The official challenge day is governed exclusively by **UTC calendar dates** formatted as `YYYY-MM-DD`.
- Local device clock offsets or local midnights do NOT shift the official global challenge boundary.
- Server returns canonical `serverTime` in all daily challenge API responses to prevent client clock manipulation.

## 3. Challenge Identity & Versioning
Every official daily challenge has an immutable identity model:
- `challengeId`: Deterministic composite ID `daily-{dateKey}`.
- `dateKey`: UTC date formatted as `YYYY-MM-DD`.
- `scheduleVersion`: Canonical schedule version (e.g. `1.0.0`).
- `puzzleId`: Unique identifier for the puzzle in the verified pool (e.g. `daily_curated_001`).
- `puzzleVersion`: Content version of the puzzle (`1.0.0`).
- `puzzleFingerprint`: Lowercase SHA-256 hash computed over canonical dimensions, cells, checkpoints, and wall edges.
- `publicationStatus`: `PUBLISHED`.

## 4. Shared Puzzle Assignment & Verification
- Both client (`DailyChallengePool`) and backend (`DailyChallengeService`) maintain the exact same synchronized pool of 14 solver-verified puzzles.
- Deterministic index calculation:
  ```
  index = Math.abs(Objects.hash(dateKey, scheduleVersion)) % poolSize
  ```
- All eligible daily puzzles possess an exhaustive, computer-verified unique complete solution.
- The solver's solution path is NEVER exposed in ordinary challenge responses or client network traffic.

## 5. Official Attempt Policy
- One official leaderboard-eligible attempt per authenticated player per daily challenge date.
- Start request (`POST /api/daily/attempt/start`) verifies player authentication and checks if an attempt already exists.
- If an attempt already exists:
  - If `ACTIVE` and within attempt timeout (2 hours), returns the existing attempt ID.
  - If `FINALIZED`, rejects new attempt (`ATTEMPT_ALREADY_EXISTS`).
- Attempt ID is server-issued (UUID) and cryptographically bound to:
  - Authenticated player ID.
  - Challenge ID and Date Key.
  - Puzzle canonical fingerprint.
  - Server start epoch milliseconds.

## 6. Authoritative Timing Policy
- Authoritative elapsed time is calculated strictly as:
  ```
  solveTimeMs = serverReceiptTimestamp - attemptStartedTimestamp
  ```
- Client-reported stopwatch times are never used for competitive ranking.
- Latency note: While server timing eliminates client clock speed hacks, minor network transit variance may affect submission receipt. Attempt timeout window is bounded to 2 hours.

## 7. Date Rollover Handling
- If a player starts an attempt before UTC 00:00:00 and finishes after midnight:
  - The attempt remains firmly bound to the original challenge date and puzzle identity.
  - Submission is accepted within the attempt's 2-hour validity window.
  - It does NOT mutate into the new day's puzzle or overwrite the new day's challenge.

## 8. Offline Daily Challenge Reconciliation & Sync Operation Model
- **Durable Local Records**: When offline, players can solve the canonical daily puzzle locally using the packaged pool. Local participation is recorded durably in Room (`daily_challenge_completions`).
- **Sync Operation Queue**: An offline completion automatically enqueues a `DAILY_CHALLENGE_RECONCILIATION` operation in the `sync_operations` table with idempotent payload (UTC date, solve time, path validation fingerprint).
- **Status Progression**:
  - `LOCAL_COMPLETED`: Saved on device with local solver verification.
  - `PENDING_VERIFICATION`: Queued in Room for delivery when connectivity returns.
  - `VERIFIED`: Acknowledged by backend within submission window with valid server attempt.
  - `NOT_ELIGIBLE`: Acknowledged by backend as received outside active competitive window or with expired attempt; local streak preserved, but excluded from competitive leaderboard.
  - `REJECTED`: Invalid path or duplicate incompatible submission.
- **No False Verification**: Offline client completions are NEVER marked `VERIFIED` or given leaderboard standing without authoritative backend signature.

## 9. Deferred Testing & Status
- Targeted compilation checks verified syntax and typing.
- Unit testing of conflict policies implemented (`SyncConflictPolicyTest`).
- End-to-end integration and load testing are DEFERRED TO FINAL TESTING.

