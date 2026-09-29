# Zynpath Leaderboard Metrics Policy

## 1. Principles of Metric Design

Every leaderboard category in Zynpath is governed by an explicit metric policy defined prior to activation:

- **No Arbitrary Skill Scores**: Fabricated "skill ratings" or opaque formulas are prohibited.
- **Comparable Puzzle Requirement**: For time-based rankings, completion times are comparable only when players solve identical puzzle specifications (same fingerprint and seed). Global multiplayer leaderboards use count-based victory metrics (`QUICK_DUEL_WINS`, `MINI_LEAGUE_WINS`, `TOTAL_COMPLETIONS`) rather than comparing raw solve times across heterogeneous puzzle difficulties.
- **Verifiable Grounding**: Every metric increment maps directly to an authoritative `MatchResult` recorded in a finalized `MatchSession`.

---

## 2. Detailed Metric Policies

### Category 1: Quick Duel Victories (`QUICK_DUEL_WINS`)
- **Qualification Criteria**:
  - Match mode must be `QUICK_DUEL`.
  - Match session state must be `COMPLETED`.
  - Player's `MatchResult` must have `isWinner == true` and `finishOrder == 1`.
- **Handling of Abandonment**:
  - If Player A abandons or disconnects past the grace window, Player B receives a verified `VICTORY_BY_FORFEIT`. Player B is credited with 1 Quick Duel Win. Player A receives 1 Loss and 0 Wins.
- **Handling of Ties**:
  - If a duel completes in a verified tie, neither player is marked as winner (`isWinner == false`, `resultStatus == "TIED"`). Neither player's win metric increments.

### Category 2: Mini League 1st Places (`MINI_LEAGUE_WINS`)
- **Qualification Criteria**:
  - Match mode must be `MINI_LEAGUE`.
  - Room had at least 2 and at most 5 valid participants.
  - Player's `MatchResult` must have `finishOrder == 1` and `completed == true`.
- **Handling of Unfinished Participants**:
  - Non-finishing participants receive `UNFINISHED` or `FORFEIT` statuses and are excluded from 1st-place calculations.

### Category 3: Total Completions (`TOTAL_COMPLETIONS`)
- **Qualification Criteria**:
  - Match mode can be any multiplayer mode (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`).
  - Player submitted a valid path coordinates array solver-verified by `ServerPuzzleValidator`.
  - Player's `MatchResult` must have `completed == true`.
- **Handling of Invalid Claims**:
  - Rejected claims or syntax errors yield `completed == false` and never increment completion counts.

---

## 3. Idempotency & Reconciliation

- When `CompetitiveService.recordFinalizedMatch(session)` is called, the session is tracked in `finalizedMatchIds`.
- Repeated invocations (e.g. from network retries, reconnection broadcasts, or replayed events) are ignored safely.
- If match records require future administrative adjustments, aggregates can be fully reconciled by re-scanning the verified immutable `finalizedSessions` store.

---

## 4. Daily Challenge Metric Policy (Prompt 25)

### Category: Daily Challenge Elapsed Time (`DAILY_SOLVE_TIME`)
- **Qualification Criteria**:
  - Challenge must be the official published daily challenge for the specified UTC date (`YYYY-MM-DD`).
  - Attempt must have a valid, active server-issued `attemptId`.
  - Move path coordinates must be 100% solver-verified by `ServerPuzzleValidator` (orthogonal, no wall crossings, ascending checkpoints, complete cell coverage, ending on checkpoint $N$).
  - Elapsed solve time must be computed strictly via server authority:
    $$\Delta t = \text{serverReceiptTimestamp} - \text{serverStartTimestamp}$$
- **Disqualification / Non-Eligibility**:
  - Offline local completions and guest completions are tagged `LOCAL_COMPLETION` or `PROVISIONAL` and are excluded from the competitive leaderboard.
  - Attempts exceeding the 2-hour timeout or crossing expiration deadlines are excluded.
  - Multiple attempts for the same daily challenge are prevented; only the first official valid attempt is eligible.
- **Tie-Breaking Determinism**:
  - Equal elapsed times down to millisecond resolution receive identical ranks under Standard Competition Ranking (1, 2, 2, 4...).
  - Ties are never broken by fabricating an artificial winner.

