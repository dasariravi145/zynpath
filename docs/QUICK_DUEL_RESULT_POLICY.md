# Zynpath Quick Duel — Result Determination & Validation Policy

## 1. Overview
In Zynpath Quick Duel 1v1, match results are **100% server-authoritative**. The client never declares itself the winner; rather, the client submits its completed path coordinates, and the Spring Boot backend independently validates the route, determines the finish order based on server elapsed timing, and finalizes the match.

---

## 2. Server-Side Dual-Win Validation

Before any participant can be granted a valid finish time, `ServerPuzzleValidator` verifies the submitted route against the authoritative `PuzzleAssignment`:

| # | Rule Check | Validation Criteria | Rejection Code |
|---|---|---|---|
| 1 | **Start Cell** | First coordinate must match Checkpoint #1 position exactly. | `INVALID_START_CELL` |
| 2 | **Adjacency** | Every step from index $i$ to $i+1$ must be strictly orthogonal (Manhattan distance = 1). | `NON_ORTHOGONAL_STEP` |
| 3 | **Blocked Edges** | No step can cross a registered wall barrier in `puzzle.blockedEdges`. | `BLOCKED_EDGE_CROSSED` |
| 4 | **No Revisits** | No coordinate may appear more than once in the submitted path. | `CELL_REVISITED` |
| 5 | **Checkpoint Order** | Numbered checkpoints must be encountered in ascending order ($1, 2, \dots, N$) with zero skips. | `CHECKPOINT_ORDER_VIOLATION` |
| 6 | **Full Coverage** | Total path length must equal `puzzle.requiredCells().size()`. All required cells visited. | `INCOMPLETE_COVERAGE` |
| 7 | **End Cell** | Final coordinate must match the highest numbered checkpoint position ($N$). | `INVALID_FINAL_CELL` |
| 8 | **Puzzle Match** | Submitted `puzzleId` must match the match session's assigned puzzle. | `PUZZLE_MISMATCH` |

If any check fails, the submission is rejected with `ValidationOutcome.failure(reason)`. An invalid submission cannot win under any circumstances.

---

## 3. Result Timing Policy

### 3.1 Timing Formula
Solve time is calculated strictly from server clocks:
$$\text{solveTimeMs} = \text{serverReceiptTime} - \text{session.startedAt}$$

- **Server Receipt Timestamp:** The monotonic system timestamp when the Spring Boot server receives the valid completion claim over WebSocket or REST.
- **Client Solve Time:** Client-reported elapsed time is preserved solely as metadata for audit and latency diagnostic analysis, but is **never used to determine the winner**.

### 3.2 Result Ordering
1. **First Valid Finisher:** The participant who submits the first validated solution is declared the `WINNER`.
2. **Second Finisher:** Upon validating the second player's submission, the second player is marked as `RUNNER_UP` (loss).
3. **Simultaneous Finish (Tie Window):** If both participants submit valid solutions within a bounded window of $\le 50\text{ ms}$, both participants are recorded with outcome `TIE`.
4. **Invalid Finisher:** A player who submits an invalid path or never completes before match termination is marked as `DEFEATED` or `UNFINISHED`.

---

## 4. Idempotency & Duplicate Submission Protection
- Once a player has a recorded `completedAt` timestamp in `MatchParticipant`, subsequent submission attempts return the cached outcome idempotently.
- A player cannot submit multiple solutions to overwrite their previous result.
- Submissions arriving after a match has transitioned to `COMPLETED`, `CANCELLED`, or `FORFEITED` are rejected with `INVALID_MATCH_STATE`.

---

## 5. Abandonment, Disconnection & Forfeit Policy

| Scenario | Server Action | Outcome for Leaving Player | Outcome for Remaining Player |
|---|---|---|---|
| **Explicit Forfeit** (Taps "Forfeit" or confirms exit) | Match finalizes immediately. | Marked `FORFEITED` (loss). | Marked `WINNER` (by opponent forfeit). |
| **Ready Timeout** (Fails to confirm ready within 20s) | Match cancelled before start. | Marked `CANCELLED`. | Marked `CANCELLED`. No rating or stat change. |
| **Temporary Disconnect** (Network blip $\le 30\text{s}$) | Match remains `ACTIVE`. Opponent continues playing. Reconnection restores match snapshot. | Retains current state upon reconnect. | Unaffected. |
| **Permanent Disconnect** (Drop $> 30\text{s}$ during active duel) | Match expires via background liveness sweep. | Marked `ABANDONED` (loss). | Marked `WINNER` (by opponent abandonment). |

---

## 6. Competitive Isolation
- **No Solo Overwrite:** Quick Duel results are never written to Room `gameplay_sessions` or `level_progress`.
- **No Guest Contamination:** Unauthenticated guest profiles cannot submit or receive competitive records.
- **No Solution Leaks:** Solver hints are completely deactivated during multiplayer sessions.
