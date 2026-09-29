# Zynpath Authoritative Multiplayer Result Validation

## 1. Overview
In Zynpath competitive multiplayer, client devices are treated as untrusted presentation engines. A client cannot claim victory merely by transmitting a boolean `completed=true` flag. The backend Spring Boot service performs **independent server-side dual-win path validation** using `ServerPuzzleValidator`.

---

## 2. Server-Side Path Validation Rules
When a client submits a completion claim via `POST /api/v1/multiplayer/matches/{matchId}/claim`, the backend verifies:
1. **Starting Position**: Path coordinate `[0]` must match Checkpoint 1.
2. **Orthogonal Adjacency**: Consecutive coordinates `[i]` and `[i+1]` must be horizontally or vertically adjacent (|dr| + |dc| == 1). Diagonal steps are strictly rejected.
3. **Blocked Edge Crossing**: The step between `[i]` and `[i+1]` must not cross any blocked edge defined in `puzzle.blockedEdges()`.
4. **No Cell Revisits**: Each cell coordinate in the path must appear exactly once. Forward movement self-intersection is invalid.
5. **Checkpoint Ascending Order**: Intermediate checkpoints must be visited in strictly ascending numerical order (1 -> 2 -> ... -> N). Skipping a checkpoint or visiting out of order causes immediate rejection.
6. **100% Cell Coverage**: The unique cells visited in the path must exactly equal the set of all required cells defined in the puzzle assignment. Reaching the final checkpoint without full board coverage is NOT a victory.
7. **Final Checkpoint**: The last coordinate of the path must match Checkpoint N.

---

## 3. Server-Authoritative Timing
- **Start Reference**: The match start time (`startedAt`) is stamped authoritatively on the backend when the countdown concludes and the state enters `ACTIVE`.
- **Completion Receipt**: When a valid solution is received, solve duration is computed as:
  ```
  authoritativeSolveTimeMs = System.currentTimeMillis() - session.getStartedAt();
  ```
- Client-reported elapsed times are logged for latency discrepancy telemetry, but are **never** used as the authoritative finish time.

---

## 4. Result Idempotency & Concurrency Safety
- Multiple result submissions for the same participant are idempotent. If a player has already submitted a verified solution, subsequent submissions return the cached success outcome.
- Concurrent claim submissions are synchronized using the session lock (`synchronized (session)`). Finish order (`finishOrder`) is allocated monotonically (1st place, 2nd place, etc.).
- The first player to complete a valid dual-win path in a duel is declared the winner (`isWinner = true`), transitioning the match into `COMPLETED`.

---

## 5. Result Delivery & REST Endpoint (Prompt 21)
- **Instant WebSocket Broadcast**: When the first player validates or the match completes, `MATCH_COMPLETED` is broadcast with the complete list of `MatchResult` objects.
- **REST Results Endpoint**: `GET /api/v1/multiplayer/matches/{matchId}/results` provides authoritative results retrieval upon reconnection or screen resume.
- **Tie Resolution**: Submissions arriving within a 50ms window with valid routes receive outcome `TIE`.
- **Forfeit / Abandonment**: Explicit forfeits or expired disconnects award the remaining participant an immediate authoritative `WINNER` outcome.

---

## 6. Friend Duel Result Validation Parity & History Persistence (Prompt 22)
- **100% Validation Parity**: Friend Duel reuses the exact same 8-point `ServerPuzzleValidator` and server-authoritative elapsed timing ($\text{receipt} - \text{startedAt}$) as Quick Duel.
- **No Client Win Declarations**: Local victory claims in Friend Duel remain strictly provisional until the server acknowledges dual-win path validity.
- **History Isolation**: Friend Duel results are stored with `gameMode = FRIEND_DUEL` and match session history. They are strictly decoupled from Solo level stars, Solo personal best records, and Daily Challenge streaks.

---

## 7. Mini League Result Validation, Finishing Order & Finishing Window (Prompt 23)
- **Validation Parity**: Mini League runs the exact same authoritative `ServerPuzzleValidator` checks on all 2–5 players' paths.
- **1st Place & Finishing Window**: The first player to validate their route receives `finishOrder = 1` and `isWinner = true`. This transitions match state to `COMPLETING` and opens a 45-second finishing window for remaining participants.
- **Rankings 2nd Through 5th**: Remaining participants who finish within the window receive `finishOrder = 2..5` in order of valid completion receipt.
- **Unfinished Participants**: When the 45-second window expires or all players finish, `concludeMiniLeague` sets uncompleted players to `UNFINISHED` or `FORFEIT`. Unfinished players are not assigned artificial finish times.

---

## 8. Competitive Ingestion Boundary & History (Prompt 24)
Once match resolution completes across any mode (Quick Duel, Friend Duel, Mini League):
- **Authoritative Ingestion:** `MatchSessionService.concludeMatch(...)` passes the finalized `MatchSession` to `CompetitiveService.recordFinalizedMatch(session)`.
- **Idempotency Guard:** `CompetitiveService` deduplicates via internal match ID tracking (`finalizedMatchIds`). If a match finalization event re-fires or a client re-submits a claim, statistical aggregates and match history items are not duplicated.
- **Strict Distinction:** Quick Duel wins, Friend Duel wins, and Mini League finishes are tracked in separate mode buckets. Unfinished or abandoned matches count towards match totals but not towards wins or completions.
- **Immutable Historical Records:** Once written, historical match entries and participant results cannot be altered by client action or friendship removals.




