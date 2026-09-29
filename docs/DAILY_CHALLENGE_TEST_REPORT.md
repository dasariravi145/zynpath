# Daily Challenge Integration Test Report

## Overview
Verification of canonical UTC daily puzzle publication, deterministic schedule generation, official competitive attempt tracking, dual-win server-side validation, leaderboard calculation, and offline provisional sync isolation.

- **Suite**: `com.zynpath.backend.daily.DailyChallengeIntegrationTest`
- **Tests Executed**: 6
- **Passed**: 6
- **Failed**: 0
- **Status**: **PASSED**

---

## Detailed Results

| Test Method | Category | Verified Behavior | Status |
| :--- | :--- | :--- | :--- |
| `getChallenge_returnsCanonicalDefinition` | Schedule Determinism | Requesting today's daily challenge returns canonical puzzle spec with grid dimensions, checkpoints, and puzzle fingerprint. | **PASSED** |
| `getChallenge_sameDate_returnsDeterministicPuzzle` | Restart Integrity | Two independent queries for the same date return identical `puzzleId` and `puzzleFingerprint`, ensuring backend restarts do not change the daily puzzle. | **PASSED** |
| `startAttempt_andGetActiveAttempt` | Attempt Tracking | Starting an official competitive attempt sets attempt status to `ACTIVE`, records server timestamp, and allows retrieval via `/attempt/active`. | **PASSED** |
| `submitCompletion_validPath_recordsResult` | Authoritative Solve | Valid Hamiltonian solution path submitted by solver passes `ServerPuzzleValidator`, transitions attempt to `FINALIZED`, records solve time, and marks result `SERVER_VALIDATED`. | **PASSED** |
| `submitCompletion_invalidPath_isRejected` | Anti-Cheat | Invalid path submission (e.g. truncated or skipped cells) is rejected with `400 Bad Request` and `INVALID_COMPLETION`. | **PASSED** |
| `syncProvisional_doesNotPolluteVerifiedLeaderboard` | Offline Isolation | Provisional offline completion records are accepted for personal streaks and history, marked `PROVISIONAL`, but excluded from verified timed competitive leaderboards (`isLeaderboardEligible = false`). | **PASSED** |

---

## Daily Schedule & UTC Guarantee

- Daily puzzle selection is derived via SHA-256 hash over `scheduleVersion + ":" + dateKey` modulo pool size.
- Cutover occurs authoritatively at `00:00:00 UTC`.
- Official competitive attempts are bounded by a 2-hour lease from start time, preventing delayed replay or solver assistance.
