# Zynpath: Mini League Result Validation, Finishing Order & Tie Policy

## Authoritative Validation & Timing

### Server Route Verification
- When a client completes the puzzle locally, it freezes input, captures the ordered coordinate sequence, and submits a solution claim to the backend.
- The server independently runs `MultiplayerPathValidator.validatePath(...)`:
  1. Starts at checkpoint 1.
  2. Traverses orthogonally without crossing blocked edge walls.
  3. Covers every required grid cell exactly once without illegal revisits.
  4. Visits checkpoints in ascending numbered order (1, 2, 3, ...).
  5. Terminates at the highest-numbered checkpoint.
- If invalid, the claim is rejected and the client may continue playing.

### Finishing Order Determination
1. **1st Place**: The first participant whose path is authoritatively validated is assigned `finishOrder = 1`, `isWinner = true`.
2. **Finishing Window (45s)**:
   - Receipt of the 1st place validation immediately switches the match state from `ACTIVE` to `COMPLETING`.
   - A server-side 45-second timer starts.
   - Subsequent valid completions received within the window are awarded positions `2nd`, `3rd`, `4th`, and `5th` in receipt order.
3. **Match Finalization (`concludeMiniLeague`)**:
   - Fires when either:
     a) All participants have successfully completed and validated their paths, OR
     b) The 45-second finishing window expires.
   - Any participants who did not complete within the window are marked with `completed = false`, `resultStatus = "UNFINISHED"` (or `"FORFEIT"` if disconnected/abandoned).
   - Standings are permanently saved to `MatchResultDto` records.

### Tie Policy
- If two valid completions arrive within the same server timestamp granularity (millisecond resolution) with identical verified solve durations, they are assigned the same rank (e.g. Tied 2nd), with subsequent places adjusted accordingly.
