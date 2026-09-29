# Zynpath: Daily Challenge Offline Participation & Synchronization

## 1. Offline-First Philosophy
Zynpath guarantees that the Daily Challenge remains 100% playable even without an internet connection or backend availability:
- Packaged verified puzzles in `DailyChallengePool` ensure immediate local availability.
- Deterministic UTC schedule calculation works offline using local UTC clocks.
- No player is blocked from playing the daily puzzle due to poor connectivity or airplane mode.

## 2. Guest & Offline Attempt Lifecycle
- **Guest Players**: Can access and complete the daily puzzle without signing in.
- **Offline Start**: If network is unreachable during start, the app launches a local session:
  - Preserves local date key, schedule version, and puzzle ID.
  - Marks session verification status as `LOCAL_COMPLETION` or `PROVISIONAL`.
  - Records local start time and local elapsed time.
  - Does NOT manufacture a fake backend attempt ID.

## 3. Safe Synchronization & Reconciliation
When connectivity returns or when the player opens the app online:
1. **Challenge Discovery**: The client requests the canonical challenge identity from `GET /api/daily/challenge/today`.
2. **Fingerprint Validation**:
   - The client compares the local puzzle fingerprint against the server's published fingerprint.
   - If fingerprints match: the local record is eligible for metadata synchronization via `POST /api/daily/sync/provisional`.
   - If fingerprints mismatch (e.g. out-of-date schedule revision): the local completion is safely preserved as a local noncompetitive record, and the server's revised puzzle is presented without clobbering history.

## 4. Leaderboard Integrity Rules
- The server CANNOT verify the historical accuracy of an offline elapsed time or ensure that the user did not pause/manipulate local clocks.
- Therefore, **offline completions are NEVER promoted to the official timed leaderboard**.
- Synced offline completions receive the `PROVISIONAL` or `LOCAL_COMPLETION` badge on the server.
- The player receives full credit for:
  - Local daily completion history.
  - Daily streak progression (7-day streak, etc.).
  - Daily completion achievements.

## 5. Account Sign-In with Prior Guest History
When a guest player signs in with Google or Zynpath Account:
- Local Room database records (`DailyChallengeRecord`) remain completely intact.
- The player's active streak and historical solve dates are preserved.
- Existing local completions remain marked as `LOCAL_COMPLETION` or `PROVISIONAL` and are not erased or falsely promoted.
