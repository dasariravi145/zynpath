# Zynpath Quality Assurance & Regression Issue Register

## 1. Executive Summary

This register documents verified defects, regression hazards, and resolution records identified during the comprehensive Phase 11 QA verification phase of Zynpath: Number Path Puzzle.

---

## 2. Issue Tracking & Resolution Matrix

| Issue ID | Category | Severity | Description | Status | Resolution / Verification |
|---|---|---|---|---|---|
| **REG-01** | Build / Gradle | P1 (Critical) | In `android/app/build.gradle.kts`, `java.util.Properties` was referenced without import and stream loading lacked type inference, causing Gradle DSL compilation failure during test tasks. | **FIXED AND REVERIFIED** | Added explicit imports (`import java.util.Properties`, `import java.io.FileInputStream`) and wrapped keystore loading in `FileInputStream(keystorePropertiesFile).use { ... }`. Verified clean task execution in Batch 2. |
| **REG-02** | Rule Invariant | P0 (Blocker) | Premature final checkpoint navigation: if a player reached checkpoint $N$ before visiting all required cells, an incomplete implementation might mistakenly flag victory. | **VERIFIED** | Canonical rule 6 and 7 strictly tested in `FoundationalPathValidationTest` and `CanonicalRulesAndBoundaryPropertyTest`. Confirmed that reaching final checkpoint with uncovered cells produces `PathValidationResult.Incomplete(INCOMPLETE_COVERAGE_AT_FINAL_CHECKPOINT)`. |
| **REG-03** | Progression | P1 (Critical) | Replay count inflation: replaying an already completed level could mistakenly increment the unique completed levels counter, corrupting world unlock thresholds. | **VERIFIED** | Tested in `WorldProgressionAndReplayIntegrityTest`. `ProgressRepositoryImpl` increments `completionCount` on the level progress record but does NOT increment the unique count observed by `observeCompletedLevelCount()`. |
| **REG-04** | Progression | P1 (Critical) | Slower replay overwriting faster best time: replaying a level with a longer completion time could overwrite a player's established personal record. | **VERIFIED** | Tested in `WorldProgressionAndReplayIntegrityTest`. `ProgressRepositoryImpl` evaluates `Math.min(currentBest, newTime)` and strictly preserves the faster duration. |
| **REG-05** | Session / Timer | P1 (Critical) | Background timer accumulation: if the player switches apps or backgrounds the device during a game session, active playtime could erroneously inflate. | **VERIFIED** | Tested in `SoloSessionAndTimerComprehensiveTest` and `GameplayTimerTest`. Monotonic clock timestamps are captured on pause and resume, completely freezing elapsed duration during background intervals. |
| **REG-06** | Session Recovery | P1 (Critical) | Corrupted session crash: malformed or corrupted snapshot JSON (e.g. diagonal steps, coordinates out of bounds) could crash the activity during session restoration. | **VERIFIED** | Tested in `GameplaySessionValidatorTest` and `SoloSessionAndTimerComprehensiveTest`. `validateAndReplay` safely catches invalid moves and returns `SessionRestorationResult.Invalid`, triggering safe fallback to a fresh session without app crash. |
| **REG-07** | Hint Engine | P0 (Blocker) | Illegal hint moves: hint engine could theoretically suggest a diagonal or wall-crossing move if path finding had unconstrained edge traversal. | **VERIFIED** | Tested in `HintEngineSafetyAndEntitlementTest`. Verified that all hints derive strictly from solved routes and verify orthogonal adjacency, zero wall crossing, zero cell revisit, and ascending checkpoint ordering. |
| **REG-08** | Offline Autonomy | P0 (Blocker) | Core Solo requiring remote connectivity: network failure could block solo gameplay if repositories depended on remote APIs for level availability. | **VERIFIED** | Tested in `OfflinePersistenceAndRecoveryTest`. All campaign progression, asset loading, and completion recording execute completely against local Room and DataStore databases with zero network dependency. |

---

## 3. Regression Safeguards & Continuous Monitoring

1. **Automated CI Execution:** All unit tests run automatically in GitHub Actions on every pull request and push to `main`.
2. **Zero Rule Weakening Policy:** Under no circumstances are canonical puzzle rules relaxed to make a test pass. Any test failure must be investigated as either a genuine algorithm defect or a test fixture discrepancy.
3. **Database Schema Verification:** Any schema change must provide an explicit non-destructive migration script tested via `RoomMigrationTest`. Destructive fallback (`fallbackToDestructiveMigration()`) remains strictly forbidden.
