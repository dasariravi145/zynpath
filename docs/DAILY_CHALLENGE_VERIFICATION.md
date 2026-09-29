# Zynpath: Daily Challenge Server Validation & Authoritative Verification

## 1. Authoritative Validation Principles
The Zynpath server never trusts client assertions such as `completed = true` or client-computed elapsed times.
All official daily challenge completions must submit the complete move sequence or cell coordinate path (`List<CellCoordinate>`) for independent server-side re-simulation.

## 2. Server-Side Path Validation Rules
The server executes `ServerPuzzleValidator.validatePath(...)` testing the 8 invariant canonical rules:

1. **Initial Checkpoint**: The path must begin precisely at checkpoint #1.
2. **Orthogonal Adjacency**: Consecutive cells in the path must be strictly orthogonal (Manhattan distance == 1). Diagonal steps are rejected.
3. **No Blocked Edges (Walls)**: Movement between cell `(r1, c1)` and `(r2, c2)` must not cross any blocked edge defined in the canonical puzzle definition.
4. **No Cell Revisits**: Each cell must be visited at most once during forward movement.
5. **Ascending Checkpoint Order**: Numbered checkpoints must be traversed in strictly ascending order: $1 \rightarrow 2 \rightarrow \dots \rightarrow N$.
6. **Premature Final Checkpoint Prevention**: Crossing or stepping into the final checkpoint $N$ before 100% of required cells have been traversed is invalid.
7. **Full Cell Coverage**: Every playable, non-excluded cell within the grid dimension must be covered by the path.
8. **Final Checkpoint Termination**: The final step in the path must land precisely on the highest-numbered checkpoint.

## 3. Authoritative Timing Policy
- Attempt start is recorded at the server when `POST /api/daily/attempt/start` is handled:
  $$t_{\text{start}} = \text{serverCurrentTimeMillis}$$
- Attempt completion is recorded when `POST /api/daily/attempt/submit` is received:
  $$t_{\text{end}} = \text{serverCurrentTimeMillis}$$
- The official competitive elapsed time is:
  $$\Delta t = t_{\text{end}} - t_{\text{start}}$$
- If $\Delta t < 0$ or $\Delta t > 7200000$ (2 hours), the attempt is deemed anomalous or expired.

## 4. Verification Badges & Status Taxonomy
To maintain trust, the UI displays clear, unambiguous verification badges:

| Status Code | Display Label | Description | Leaderboard Eligible |
|---|---|---|---|
| `SERVER_VALIDATED` | **SERVER-VALIDATED** | Official online attempt validated by backend engine. | Yes |
| `LEADERBOARD_ELIGIBLE` | **LEADERBOARD-ELIGIBLE** | Validated official online attempt ranked on daily board. | Yes |
| `PROVISIONAL` | **PROVISIONAL** | Solved locally or synced offline; path valid, timing unverified. | No |
| `LOCAL_COMPLETION` | **LOCAL COMPLETION** | Completed strictly offline or as guest without server sync. | No |
| `NOT_ELIGIBLE` | **NOT ELIGIBLE** | Path invalid, expired attempt, or mismatched puzzle fingerprint. | No |

## 5. Result Idempotency
- Submitting completion multiple times for the same attempt returns the existing finalized `DailyChallengeOnlineResult`.
- Completion statistics, streaks, and achievement evaluations are updated exactly once per daily challenge date.
- Finalized competitive records cannot be overwritten by subsequent local replay attempts.

---

## 6. Security Hardening & Rate Limiting (Prompt 36)

- **Authentication & Ownership**: Attempt initiation and completion endpoints enforce `@RequireAccess(AUTHENTICATED)` and bind attempts to the caller's verified `SecurityContext.getCurrentPlayerId()`. Attempt IDs cannot be hijacked across player identities.
- **Submission Rate Limiting**: Attempt start and result submission routes are governed by `@RateLimited(RateLimitPolicy.RESULT_SUBMISSION)` (30 req/min), preventing brute-force path submission scripts.
- **Deterministic Server-Side Verification**: Path validation is executed against the server-generated daily puzzle definition. Manipulated client definitions, modified checkpoint numbers, or altered wall boundaries are rejected with `INVALID_PUZZLE_RESULT`.
- **Security Audit Logging**: Every daily challenge solve attempt is logged via `SecurityAuditLogger` with attempt status, date key, and elapsed millisecond duration without exposing sensitive user credentials.
