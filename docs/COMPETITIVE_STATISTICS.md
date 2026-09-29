# Zynpath Competitive Player Statistics

## 1. Principles of Result Integrity

All competitive statistics in Zynpath are computed strictly from real, finalized, server-validated match records. UI counters and client-side storage are never the authoritative source of truth.

If an authoritative result finalization event is delivered multiple times or replayed, idempotent recording mechanisms in `CompetitiveService` guarantee that statistics totals do not inflate duplicate counts.

---

## 2. Documented Metrics & Formulas

### Quick Duel (1v1)
- `quickDuelMatches`: Total finalized Quick Duel matches participated in.
- `quickDuelWins`: Quick Duel matches where `isWinner == true` or `finishOrder == 1`.
- `quickDuelLosses`: Quick Duel matches where `isWinner == false` and outcome is not tied.
- `quickDuelTies`: Quick Duel matches where outcome status is explicitly `TIED`.
- **Win Rate Formula**:
  $$\text{Quick Duel Win Rate} = \frac{\text{quickDuelWins}}{\text{quickDuelMatches}}$$
  *(Evaluates to 0.0 if `quickDuelMatches == 0`)*

### Friend Duel (1v1)
- `friendDuelMatches`: Total finalized Friend Duel matches.
- `friendDuelWins`: Friend Duel matches won.
- `friendDuelLosses`: Friend Duel matches lost.
- `friendDuelTies`: Friend Duel matches tied.
- **Win Rate Formula**:
  $$\text{Friend Duel Win Rate} = \frac{\text{friendDuelWins}}{\text{friendDuelMatches}}$$
  *(Evaluates to 0.0 if `friendDuelMatches == 0`)*

### Mini League (2–5 Players)
Mini League results are never forced into a binary 1v1 win/loss model. Meaningful multi-player metrics are used:
- `miniLeagueParticipations`: Total finalized Mini League matches entered.
- `miniLeagueFirstPlaceFinishes`: Matches where `finishOrder == 1`.
- `miniLeagueTopThreeFinishes`: Matches where `finishOrder` is between 1 and 3.
- **Average Finish Position**:
  $$\text{Average Finish} = \frac{\sum \text{finishOrder}}{\text{miniLeagueParticipations}}$$
  *(Evaluates to 0.0 if `miniLeagueParticipations == 0`)*

---

## 3. Total Validated Completions & Personal Bests

- `totalValidatedCompletions`: Total multiplayer matches where the player submitted an authoritatively validated solution (`completed == true`).
- **Personal Best Solve Times**:
  Maintained per game mode (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`). Each record stores:
  - `gameMode`: Mode identifier.
  - `puzzleId`: Assigned puzzle ID.
  - `matchId`: Authoritative match session ID where the record was achieved.
  - `solveTimeMs`: Minimum solve time in milliseconds.
  - `achievedAt`: Server epoch timestamp of achievement.

Unrelated puzzle sizes and difficulties are never conflated as directly equivalent.

---

## 4. Time Periods & Server Authority

Statistics queries support three authoritative time windows:
1. `ALL_TIME`: Entire lifetime history of the account.
2. `THIS_MONTH`: Filtered from `ZonedDateTime.now(ZoneOffset.UTC).withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC)`.
3. `THIS_WEEK`: Filtered from `ZonedDateTime.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(ZoneOffset.UTC)`.

All boundary calculations use UTC server time to eliminate client timezone manipulation or clock drift.

---

## 5. Guest vs Authenticated Isolation

- Offline guests maintain their local Solo progression, star totals, Daily Challenge streaks, and local achievements.
- Online competitive statistics require an authenticated player account.
- The Profile screen displays guest status transparently and provides non-intrusive linking options to preserve offline data when connecting Google or Facebook identities.

---

## 6. Daily Competition Statistics (Prompt 25)

- **Official Daily Attempts**:
  - Tracked when an official online attempt completes and passes `ServerPuzzleValidator`.
  - Authoritative solve time is computed from server start and finish timestamps.
- **Idempotency & Replay Separation**:
  - Only the first official attempt on a given challenge date is recorded towards competitive statistics and daily leaderboard rankings.
  - Subsequent local practice replays do NOT mutate the finalized official record or increment competitive counters twice.
- **Local Daily Stats Independence**:
  - Local Room database `daily_challenge` counters, personal bests, and streak counts remain independently accessible and preserved.

---

## 7. Analytics Integration & Sample Size Requirements (Prompt 30)

- **Mandatory Sample Sizes ($N$)**: All competitive summaries displayed in the Personal Analytics dashboard (`CompetitiveModeSummaryCard`, `MiniLeagueSummaryCard`) explicitly state their supporting sample size ($N$).
- **Zero Denominator Handling**: When $N = 0$, win-rate percentages and average finish positions render an explicit empty state (`—`) rather than misleading mathematical defaults.
- **Strict Mode Separation**:
  - Quick Duel (1v1 public matchmaking) and Friend Duel (1v1 private invite) are computed and reported separately. Friend duel results never inflate Quick Duel leaderboard rankings.
  - Mini League (4-player tournament) reports 1st place finishes and top 3 podium rates, refusing to force multi-player finishes into an artificial 1v1 win/loss formula.


