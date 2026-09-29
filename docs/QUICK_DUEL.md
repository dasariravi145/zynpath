# Zynpath Quick Duel 1v1 — Architecture & Technical Specification

## 1. Overview
Quick Duel 1v1 is Zynpath's real-time, competitive online multiplayer mode. Two real, authenticated players are matched automatically through a server-authoritative matchmaking queue. Both participants receive the exact same solver-verified continuous puzzle, independently trace their solutions locally via the responsive touch engine, and submit their complete routes to the Spring Boot game server for rigorous 8-point dual-win validation. The server authoritatively validates solution legality, determines the finish order based on server elapsed timestamps, and persists immutable match results.

**Core Principles:**
- **Zero Bots / Fake Opponents:** Matches only occur between two real authenticated human players.
- **Server Authoritative:** Victory is never determined by client claims alone. Every completion requires full route re-validation by `ServerPuzzleValidator`.
- **Identical Verified Puzzle:** Both participants receive the exact same puzzle ID, grid dimensions, checkpoints, blocked edges, and SHA-256 fingerprint.
- **Competitive Integrity:** Solution-revealing hints are strictly disabled. Undo and Reset remain free.
- **Responsive Local Touch Drawing:** Moves are processed locally through `PuzzleEngine` for zero touch lag. No raw finger coordinate streaming.
- **Offline Isolation:** Solo Play and Daily Challenge operate completely independently from Quick Duel.

---

## 2. Matchmaking Queue Lifecycle

```
[IDLE Entry]
     │ (Find Opponent Tapped - Must be Authenticated)
     ▼
[POST /api/v1/multiplayer/matchmaking/quick-duel]
     │
     ▼
[SEARCHING / QUEUED (Atomic Ticket)]
     │
     ├── (Cancel Tapped) ──> [DELETE /matchmaking/tickets/{ticketId}] ──> [IDLE]
     │
     ▼ (Atomic Candidate Pairing: FIFO Queue, Distinct Players, No Self-Match)
[MATCH_FOUND] (Ticket status updated; Match ID & Snapshot issued)
     │
     ▼
[WEBSOCKET /ws/multiplayer Connected & Subscribed]
```

### Matchmaking Invariants:
1. **Authenticated Only:** Requires valid bearer token from Google/Facebook authentication. Guest accounts are prompted with an account sign-in gate.
2. **Single Active Ticket:** A player can only have one active matchmaking ticket at any time. Repeated requests return the active ticket idempotently.
3. **No Self-Matching:** A player cannot match with themselves across devices or concurrent sessions.
4. **Ticket Polling & Push:** Clients poll `GET /matchmaking/quick-duel/status` every 2 seconds as a fallback, while WebSocket events push `MATCH_FOUND` instantly.

---

## 3. Match Lifecycle & Ready Confirmation

```
[MATCH_FOUND]
     │
     ▼
[LOBBY / WAITING] ──── (20-Second Bounded Ready Window)
     │
     ├── Player marks Ready ──> [POST /matches/{id}/ready] or [WS: PLAYER_READY]
     │
     ▼ (Both Players Ready)
[COUNTDOWN] ── (Authoritative 3-Second Synchronized Countdown)
     │
     ▼ (Server transitions session to ACTIVE)
[ACTIVE GAMEPLAY]
```

### Ready Window Policy:
- Both players have up to 20 seconds to confirm readiness.
- Repeated ready events are idempotent.
- One player cannot mark another player ready.
- If a player fails to confirm readiness within the 20-second timeout, the match is cancelled with `OPPONENT_NOT_READY` or `READY_TIMEOUT`. No simulated substitute opponent is ever inserted.

---

## 4. Shared Verified Puzzle Assignment
- The backend selects or generates a solver-verified puzzle with unique solution guarantee.
- Serialized to clients with:
  - `puzzleId`: Unique puzzle UUID.
  - `fingerprint`: Authoritative SHA-256 hash.
  - `gridRows` & `gridCols`: Board dimensions.
  - `checkpoints`: Sorted array of `(number, row, col)` specs.
  - `blockedEdges`: Array of `(row1, col1, row2, col2)` specs.
- The Android client parses the DTO into a domain `PuzzleDefinition` and runs `PuzzleDefinitionValidator` before gameplay begins. If validation fails, the match aborts safely with `PUZZLE_INVALID`.

---

## 5. Synchronized Countdown & Move Gating
- When both players are ready, the backend issues `MATCH_COUNTDOWN` with `countdownSeconds: 3`.
- During countdown, the board renderer is visible, but `isInputEnabled` is strictly `false`.
- Moves submitted before the server transitions the match to `ACTIVE` are rejected immediately.

---

## 6. Live Gameplay & Dual-Win Path Tracing
- **Local Engine:** Touches are dispatched to `PuzzleEngine.process(PuzzleAction)`.
- **Milestone Progress Updates:**
  - Progress updates are throttled over WebSocket: sent only when a checkpoint is reached, when the puzzle is completed, or when covered cells advance by $\ge 2$ cells.
  - Raw pointer movement is never transmitted over the network.
- **Opponent HUD:** Displays opponent display name, covered cell count, and last checkpoint reached.
- **Match Timer:** Elapsed time is calculated authoritative from `System.currentTimeMillis() - session.startedAt`.

---

## 7. Authoritative Result Validation & Timing Policy
When the local `PuzzleEngine` confirms completion:
1. Local board freezes immediately (`isInputEnabled = false`, `isValidatingCompletion = true`).
2. Client submits the complete ordered path: `pathCoordinates = ["0,0", "0,1", ...]`.
3. Server executes `ServerPuzzleValidator.validateSolution(puzzle, pathCoordinates)` against the 8 core rules:
   1. Start cell is Checkpoint 1.
   2. Orthogonal adjacency for all steps.
   3. No blocked edges crossed.
   4. No cell revisits on forward moves.
   5. Numbered checkpoints visited in strict ascending order without skips.
   6. 100% cell coverage of all required board cells.
   7. Endpoint is the highest numbered checkpoint.
   8. Unique solution matching authoritative puzzle assignment.
4. **Timing Calculation:**
   - Solve duration is calculated server-side: $\text{durationMs} = \text{receiptTime} - \text{session.startedAt}$.
   - The first validated submission wins the duel.
   - If both players submit within a 50ms window with valid routes, the duel is recorded as a `TIE`.

---

## 8. Forfeit & Abandonment Handling
- Tapping "Forfeit" or the hardware back button prompts a confirmation dialog.
- Confirming sends `POST /matches/{id}/forfeit` and WebSocket `FORFEIT`.
- The server finalizes the match immediately, marking the forfeiting player as `FORFEITED` (loss) and the remaining opponent as `WINNER`.

---

## 9. Ephemeral Preset Reactions
- Players can transmit lightweight emoji reactions: `THUMBS_UP` (👍), `LIGHTNING` (⚡), `FIRE` (🔥), `MIND_BLOWN` (🤯).
- Reactions are rate-limited to 1 per 2 seconds.
- Reactions appear as floating badges and auto-dismiss after 3 seconds. Free-text chat is strictly prohibited.

---

## 10. Competitive History & Statistics Integration (Prompt 24)
- **Match Ingestion:** Every finalized Quick Duel is ingested into `CompetitiveService`.
- **Match History:** Accessible via `GET /api/v1/multiplayer/history?mode=QUICK_DUEL`. Shows opponent's public profile, outcome (WIN / LOSS / TIE), solve time, and puzzle fingerprint.
- **Dedicated Metrics:** Quick Duel statistics track:
  - `quickDuelMatches`: Total completed or concluded Quick Duel matches.
  - `quickDuelWins`: Total victories.
  - `quickDuelLosses`: Total defeats.
  - `quickDuelTies`: Total draws.
  - `quickDuelWinRate`: Expressed as $\text{quickDuelWins} / \text{quickDuelMatches}$.
  - Personal best solve times tagged with `QUICK_DUEL` and puzzle identity.
- **Leaderboards:** Quick Duel wins power the `QUICK_DUEL_WINS` leaderboard across `ALL_TIME`, `THIS_MONTH`, and `THIS_WEEK`.
- **Verified Achievements:** Unlocks `comp_quick_duel_complete` on first completion and `comp_quick_duel_win` on first win.

---

## 11. Security Hardening & Integrity Enforcement (Prompt 36)

- **Participant Authorization**: Match endpoints enforce `@RequireAccess(MATCH_PARTICIPANT_ONLY)`. Only verified participants bound to the active match record may view live snapshots, confirm readiness, forfeit, or submit solutions.
- **Competitive Integrity Guard (`CompetitiveIntegrityGuard`)**:
  - Validates that the submitted puzzle matches the server-issued puzzle ID and fingerprint.
  - Rejects result submissions for matches that are already in a terminal state (`COMPLETED`, `CANCELLED`, `FORFEITED`).
  - Re-verifies that hints were not utilized during the competitive session.
- **Authoritative Timing & Solver Validation**: Victory claims are re-verified by `ServerPuzzleValidator` executing the 8-point rule check. Client-reported solve times are discarded in favor of server elapsed time.
- **Multiplayer Rate Limiting**: Matchmaking requests are limited by `RateLimitPolicy.MATCHMAKING` (15 req/min), and result submissions by `RateLimitPolicy.RESULT_SUBMISSION` (30 req/min). WebSockets enforce a 20 msg/sec per-connection ceiling.
- **Idempotency Guarantee**: Repeated valid submissions return the established match outcome without duplicating rating changes, match history records, or leaderboard credits.

---

## 12. Performance & Lifecycle Optimization (Prompt 37)
- **Lifecycle-Aware State Collection**: `QuickDuelScreen` observes `QuickDuelViewModel.uiState` using `collectAsStateWithLifecycle()`, stopping unnecessary background state collection when the activity pauses.
- **Draw-Phase Canvas Animations**: Reuses `PuzzleBoard` with draw-phase animation reads and text layout caching, keeping competitive board rendering at steady 60/120 FPS.
- **Bounded WebSocket Backoff**: Unexpected network drops during a duel initiate automatic reconnection with exponential backoff (1s, 2s, 4s, max 8s, 3 attempts), auto-subscribing back to the active match.
- **In-Memory Leaderboard Slicing**: Quick Duel wins are aggregated in `CompetitiveService.leaderboardCache`, eliminating full table scans on leaderboard requests.

---

## 13. Multiplayer Reconnection & Authoritative State Recovery (Prompt 38)
- **Authoritative Snapshot Reconciliation**: When WebSocket connection drops and recovers during an active Quick Duel, `MultiplayerRepositoryImpl` immediately retrieves `getMatchSnapshot(matchId)` from the server REST API. Stale local client state is never trusted over authoritative server match status.
- **Server Match Timers**: Competitive timers continue running on the backend during player disconnections. Client reconnection cannot reset, pause, or extend match timers.
- **Submission Recovery & Idempotency**: If the client disconnects immediately after submitting a solution, the server validates the route idempotently using the match ID. Upon reconnecting, the client receives the authoritative completed match result with verified solve duration and winner status.

---

## 14. Accessibility, TalkBack Semantics & Fair Input (Prompt 39)
- **Accessible Duel Interaction**: The duel board utilizes the dual-layer `PuzzleBoard` architecture. Players using TalkBack or Switch Access can explore individual cells via virtual accessibility overlays and activate moves via cell clicks without drag requirements.
- **Hardware Keyboard & D-Pad**: Arrow keys / D-pad navigate cells, Space/Enter selects, and Backspace undos.
- **Fair Play & Anti-Cheat**: Accessible alternative input uses the exact same `PuzzleEngine` validation and generates the identical coordinate array submitted to the backend `ServerPuzzleValidator`. No extra time, solution reveals, or hints are provided.
- **TalkBack Match State**: Opponent progress updates, match countdown, and final result are announced through semantic live regions without disclosing private opponent path steps.

## 15. Production WebSocket & Database Migrations (Prompt 44)
- **Persistent Match Results (`V3__multiplayer_sessions_and_results.sql`)**: Concluded Quick Duel matches are durably recorded in `match_results` and `match_participants` upon server-authoritative validation. Rows are append-only and immutable.
- **In-Memory Active Session Architecture**: Active match queues and duel state remain in-memory for zero database write latency during live path draws. The initial production deployment is a lean single-instance Spring Boot container.
- **Production WebSocket Upgrade**: Edge proxies must forward `Upgrade: websocket` and `Connection: Upgrade` headers to `/ws/multiplayer` with extended read timeouts (3600s). See [WEBSOCKET_DEPLOYMENT.md](file:///d:/Zynpath/docs/WEBSOCKET_DEPLOYMENT.md).

---

## 16. Status & Verification
- Backend Integration: **PASSED (100%)** (Prompt 47)
- Solver Verification: **PASSED** (Prompt 47)
- Authoritative Results: **PASSED** (Prompt 47)
- Full End-to-End Android UI: Scheduled for Prompt 48

---

## 17. Backend Quick Duel Integration & Verification (Prompt 47)

### 17.1 Test Execution Summary (`MultiplayerIntegrationTest`)
- **Suite**: `com.zynpath.backend.multiplayer.MultiplayerIntegrationTest`
- **Total Tests in Suite**: 6 / 6 PASSED (100% Pass Rate).
- **Verified Quick Duel Behaviors**:
  1. `quickDuelMatchmaking_shouldPairEligiblePlayers`: Verifies two players entering matchmaking queue are paired into an active match session with identical solver-verified puzzle assignment.
  2. `quickDuelForfeit_shouldFinalizeMatch`: Verifies that a player forfeiting updates the match state immediately to finalized outcome, crediting the opponent.
  3. `quickDuelPathValidation_shouldRejectInvalidRoutes`: Rejects diagonal steps, wall collisions, and incomplete routes via `ServerPuzzleValidator`.
  4. `quickDuelDuplicateQueue_shouldPreventMultipleEntries`: Prevents duplicate queue entries for the same active player.

### 17.2 Solver and Puzzle Integrity
- All competitive match assignments draw from `MultiplayerPuzzlePool` where each puzzle is solver-verified for unique or solvable Hamiltonian paths.
- Competitive hints remain disabled (`allowsHints == false`).
- Full report available in [`docs/MULTIPLAYER_TEST_REPORT.md`](file:///d:/Zynpath/docs/MULTIPLAYER_TEST_REPORT.md).





