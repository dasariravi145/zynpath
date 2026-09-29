# Zynpath Competitive Gameplay Integrity & Anti-Cheat

## 1. Principles of Competitive Authority
In Zynpath competitive multiplayer (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`) and daily competitions (`DAILY_CHALLENGE`), client applications run local rendering and touch interactions, but **zero client victory claims are accepted authoritatively without independent server verification**.

Key principles:
1. **Server-Issued Puzzle Authority**:
   - The authoritative puzzle definition (grid dimensions, ascending checkpoints, blocked edge walls, puzzle fingerprint) is issued exclusively by the backend.
   - Clients cannot substitute, alter, or select competitive match puzzles.
2. **Deterministic Shared Puzzle Identity**:
   - Every participant in a given match or league round receives the identical puzzle identity, seed, and version hash.
   - Any claim referencing an unissued or mismatched fingerprint is rejected immediately (`INVALID_PUZZLE_IDENTITY`).
3. **No Hints in Competitive Modes**:
   - Hints remain strictly disabled across all competitive play modes (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`).
   - Active Premium subscription or ad-rewarded hint balances cannot bypass this restriction (`CompetitiveIntegrityGuard.assertNoHintsAllowed`).

---

## 2. Server-Side Path Validation (`ServerPuzzleValidator`)
When a player submits a victory claim (`/matches/{matchId}/claim` or `/attempt/submit`), the backend validates the exact ordered list of grid coordinates:

```mermaid
flowchart TD
    Claim[Client Path Claim] --> Ident[Verify Match & Puzzle Identity]
    Ident --> Bounds[Verify Coordinate Grid Bounds]
    Bounds --> Start[Verify Starting Cell == Checkpoint 1]
    Start --> Ortho[Verify Orthogonal Step Adjacency (No Diagonals)]
    Ortho --> Walls[Verify No Crossing of Blocked Edges (Walls)]
    Walls --> Visits[Verify Zero Cell Revisits]
    Visits --> Order[Verify Checkpoints Visited in Strict Ascending Order]
    Order --> Cover[Verify 100% Grid Cell Coverage (Every Tile Traversed)]
    Cover --> Finish[Verify Final Cell == Highest Numbered Checkpoint]
    Finish --> Time[Verify Authoritative Match Timing]
    Time --> Winner[Idempotent Match Resolution & Winner Declaration]
```

### Validation Rules
1. **Starting Point**: Path coordinate 0 must equal checkpoint 1's position `(r, c)`.
2. **Orthogonal Movement**: Every consecutive coordinate pair `(r1, c1) -> (r2, c2)` must satisfy `|r1 - r2| + |c1 - c2| == 1`. Diagonal steps are rejected.
3. **Wall Collision (Blocked Edges)**: Walls in Zynpath are blocked borders between adjacent cells, not impassable tiles. If an edge `(r1, c1) <-> (r2, c2)` is blocked, moving across it is an immediate cheat violation.
4. **No Cell Revisits**: Each cell in the path must be visited exactly once. No self-intersections are permitted.
5. **Strict Ascending Checkpoint Sequence**: Checkpoints 1 through N must be encountered in strictly monotonic numerical order: `1, 2, 3, ..., N`.
6. **Full-Grid Coverage**: The number of coordinates in the path must exactly equal `rows * cols`, ensuring every required tile in the puzzle is covered.
7. **Terminating Checkpoint**: The final coordinate must equal checkpoint N (the maximum checkpoint).

---

## 3. Authoritative Timing Policy
- **Server Match Clocks**:
  - The match begins when the server transitions state to `ACTIVE` (setting `startedAtMs = System.currentTimeMillis()`).
  - The completion timestamp is recorded when the server receives and validates the complete winning path (`completedAtMs`).
  - Client-reported elapsed times are treated as diagnostic advisory metadata and are never used to determine race winners.
- **Physical Feasibility Bounds**:
  - A minimum physical solve time is enforced based on grid size (e.g., minimum 50ms per cell move). Solves submitted faster than physically possible human finger interaction trigger `PUZZLE_INTEGRITY_VIOLATION`.
- **Reconnection Grace Periods**:
  - Players who experience transient radio disconnects may reconnect and submit as long as the match state remains playable and before the match expiration window closes.

---

## 4. Idempotency & Concurrency Control
- **First-to-Solve Race Condition Protection**:
  - Competitive claims are executed within database transactions using atomic state transitions.
  - The first valid solve claim transitions the match from `ACTIVE` to `COMPLETING` or `COMPLETED`.
  - Subsequent duplicate claims by the same player return the already-recorded outcome without duplicating ratings, statistics, or rewards.
  - Simultaneous claims from opponents are resolved based on the authoritative server timestamp of the first valid claim.

---

## 5. Leaderboard Protection
- **Direct Submission Invalidation**: There are no public APIs allowing clients to directly submit leaderboard scores or ranks.
- **Authoritative Ingestion**: The competitive leaderboard engine ingests results strictly through completed server-validated matches and official daily attempts.
- **Anti-Smurfing & Rating Integrity**: Rating calculations apply standard Elo / Glicko formulas computed entirely on the backend.

---

## 6. Verification Status
- Component implementation: IMPLEMENTED (`ServerPuzzleValidator`, `CompetitiveIntegrityGuard`, `MultiplayerService`, `DailyChallengeService`).
- Unit/Integration tests: DEFERRED TO FINAL TESTING.
