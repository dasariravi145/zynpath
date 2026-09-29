# Zynpath Master Test Plan & Quality Assurance Strategy

**Status:** Authoritative  
**Scopes:** Unit, Integration, UI / Gesture, Performance, and Security Testing  

---

## 1. Testing Philosophy & Target Coverage

Zynpath enforces strict quality gates across both client and server:
- **Core Puzzle Engine**: Minimum **95% branch coverage**. The engine must be provably mathematically correct.
- **Data & Repositories**: Minimum **85% code coverage**.
- **Backend Verification**: Minimum **90% branch coverage**. Server-side validation must catch 100% of illegal moves and edge cases.
- **Zero Flakiness**: All asynchronous tests must utilize deterministic test dispatchers (`kotlinx-coroutines-test`) and Flow verification libraries (`Turbine`).

---

## 2. Test Architecture & Framework Matrix

| Subsystem | Target Scope | Primary Frameworks | Execution Target |
|---|---|---|---|
| **Puzzle Domain Engine** | Mathematical validation, solvers, generators | JUnit 5, AssertJ, Kotlin Coroutines Test | Local JVM (< 2 seconds total) |
| **Android ViewModels** | UI State, Intent processing, MVI flows | JUnit 5, MockK, Turbine | Local JVM |
| **Android Room DAOs** | SQLite queries, batch upserts, migrations | AndroidX Test, Room In-Memory DB | Android Instrumented / Robolectric |
| **Android UI & Gestures** | Compose canvas drag, reverse undo, modals | Compose UI Test (`createComposeRule`) | Android Instrumented |
| **Spring Boot Backend** | Matchmaking, Ephemeral Rooms, WebSocket | `@SpringBootTest`, Testcontainers, JUnit 5 | Local JVM / CI Container |
| **Multiplayer Flow** | End-to-end WebSocket match lifecycle | Spring WebSocket TestClient, Awaitility | Integration Test Suite |

---

## 3. High-Priority Test Suites

### 3.1 Suite A: Pure Kotlin Engine Unit Tests
- `MovementValidatorTest`:
  - Verify rejection of diagonal coordinates ($[r+1, c+1]$).
  - Verify rejection of out-of-bounds coordinates.
  - Verify rejection of crossing defined `Wall` barriers.
  - Verify rejection of revisiting already covered cells.
- `CheckpointValidatorTest`:
  - Verify sequential progression ($1 \to 2 \to 3$).
  - Verify rejection of skipping checkpoints (e.g., $1 \to 3$).
  - Verify rejection of traversing into an unvisited checkpoint out of sequence.
- `CoverageValidatorTest`:
  - Verify `isComplete == false` if all checkpoints are visited but 1 cell remains uncovered.
  - Verify `isComplete == false` if 100% cells are covered but checkpoints were out of order.
  - Verify `isComplete == true` if and only if both conditions are strictly satisfied.
- `PuzzleSolverTest`:
  - Solve predefined 4x4, 5x5, 6x6, and 8x8 boards.
  - Assert that known unique puzzles yield exactly 1 solution.
  - Verify backtracking gracefully terminates on unsolvable boards.

### 3.2 Suite B: Undo & Drag Gesture Tests
- `UndoManagerTest`:
  - Push moves $\to$ call `undo()` $\to$ verify head retracts to previous cell.
  - Reverse drag into preceding cell $\to$ verify automatic step rollback.
  - Reset board $\to$ verify path collapses to initial checkpoint 1.

### 3.3 Suite C: Backend Headless Authoritative Verification
- `PuzzleVerificationServiceTest`:
  - Submit genuine winning path $\to$ assert `VALID_VICTORY`.
  - Submit path with skipped checkpoint $\to$ assert `REJECTED_SKIPPED_CHECKPOINT`.
  - Submit path with missing cell coverage $\to$ assert `REJECTED_INCOMPLETE_COVERAGE`.
  - Submit path with wall crossing $\to$ assert `REJECTED_WALL_COLLISION`.

### 3.4 Suite D: Reaction Rate Limiting Tests
- `ReactionRelayServiceTest`:
  - Send 3 rapid reactions $\to$ all 3 delivered.
  - Send 4th reaction immediately $\to$ rejected/dropped by rate limiter.
  - Advance time by 2.5s $\to$ token replenished $\to$ reaction delivered.

### 3.5 Suite E: Offline Persistence & Progression Tests (Prompt 04)
- `ProgressRepositoryTest`:
  - Verify new guest defaults: Level 1 unlocked, other levels locked, 0 stars, 0 completed.
  - Verify Level 1 availability immediately without requiring login.
  - Verify locked level behavior prevents unearned level access.
  - Verify validated completion updates Room progress, increments completion counts, and persists timestamps.
  - Verify sequential level unlocking: completing level $L$ deterministically unlocks level $L+1$.
  - Verify world unlocking: completing the final level of World 1 (Level 20) unlocks World 2 (Level 21).
  - Verify replay preserves personal bests: slower replay times or worse hint counts do not overwrite faster or cleaner runs.
  - Verify completion count increments upon replay while preserving `firstCompletedAt` and updating `lastCompletedAt`.
  - Verify invalid results (negative time, negative moves, negative hints, unvalidated claims) are rejected with `IllegalArgumentException`.
  - Verify game session checkpoints: saving/abandoning active sessions never marks a level completed.
- `PreferencesRepositoryTest`:
  - Verify guest preference defaults: onboarding false, tutorial false, SFX true, music true, haptics true, reduced motion false, language "en", world 1, level 1, free hints 3.
  - Verify individual and atomic settings updates reflect immediately in reactive Flow.
- `WorldSelectionTest`:
  - Verify 6 worlds partition 300 levels without gaps or overlaps.
  - Verify deterministic world and level unlock status via `WorldConfiguration`.
  - Verify reactive unlock propagation in `WorldSelectionViewModel`.
- `LevelSelectionViewModelTest`:
  - Verify initial level card generation with unlocked level 1.
  - Verify reactive updates when level completions occur.
- `HomeViewModelTest`:
  - Verify guest tag formatting, offline-ready flag, and reactive total stars / completed level count display.

### 3.6 Suite F: Backend Foundation & Android Network Tests (Prompt 05)
- `ZynpathBackendApplicationTests`:
  - Verify Spring Boot 3 context loads cleanly with all modular monolith beans.
- `HealthControllerTest`:
  - Verify `GET /api/v1/health` returns HTTP 200 with status `UP`, service name, version, timestamp, and environment.
- `GlobalExceptionHandlerTest`:
  - Verify unhandled exceptions return structured RFC 7807 responses without leaking stack traces or internal exception details.
- `NetworkHealthRepositoryTest` (Android + MockWebServer):
  - Verify successful health response JSON parsing into `BackendHealthDto`.
  - Verify HTTP error handling (e.g. 503) mapping to `NetworkResult.Error`.
  - Verify network timeout handling mapping to `NetworkResult.Exception`.
  - Verify connection refused (server down) mapping to `NetworkResult.Exception`.
  - Verify `ConnectivityDiagnosticsViewModel` cleanly sets status to `UNREACHABLE` on network error without crashing the application.

### 3.7 Suite G: Core Puzzle Domain & Validation Tests (Prompt 06)
- `GridModelTest`:
  - Verify `GridPosition` value equality, hashCode, row-major comparison.
  - Verify Manhattan distance calculation.
  - Verify orthogonal neighbor detection and diagonal rejection.
  - Verify neighbor coordinate derivation and `Direction.opposite` mapping.
  - Verify rectangular and square `GridDimensions`, bounds checking, cell counts, overflow protection (`MAX_DIMENSION = 50`).
  - Verify corner, edge, and interior neighbor enumeration in `GridGraph`.
- `CheckpointAndWallTest`:
  - Verify `NumberedCheckpoint` instantiation, `#1` start flag, sorting by number, rejection of non-positive numbers.
  - Verify `BlockedEdge` direction-independent equality (`BlockedEdge(a,b) == BlockedEdge(b,a)`).
  - Verify canonical endpoint sorting (`first <= second`) and set deduplication.
  - Verify rejection of non-adjacent, diagonal, or self-connecting walls.
  - Verify horizontal vs vertical wall boundary classification.
  - Verify graph traversal rejection across a wall and legal detours around walls.
- `PuzzleDefinitionValidationTest`:
  - Verify valid 4x4, 5x5, and 5x5 with walls fixtures pass validation.
  - Verify rejection of blank puzzle ID and non-positive version.
  - Verify rejection of non-positive dimensions.
  - Verify rejection of missing start checkpoint (#1).
  - Verify rejection of non-contiguous checkpoint numbers (e.g., 1, 2, 4).
  - Verify rejection of duplicate checkpoint numbers and positions.
  - Verify rejection of out-of-bounds checkpoints and walls.
  - Verify rejection of identical start and end checkpoint positions.
  - Verify rejection of non-adjacent blocked edges.
- `FoundationalPathValidationTest`:
  - Verify valid 4x4 and 5x5 serpentine routes achieve `ValidVictory`.
  - Verify valid 5x5 route with walls navigates around walls to achieve `ValidVictory`.
  - Verify empty path rejection.
  - Verify incorrect start cell rejection.
  - Verify diagonal step rejection.
  - Verify cell revisitation / cycle rejection.
  - Verify wall crossing rejection.
  - Verify premature final checkpoint rejection.
  - Verify immutable path operations (`plus`, `dropLast`, `retractTo`, `segments`).
  - **MANDATORY TEST 1**: Prove visiting all checkpoints without covering every required cell returns `Incomplete` and does NOT win.
  - **MANDATORY TEST 2**: Prove covering all required cells in the wrong checkpoint order returns `Violation` and does NOT win.

### 3.8 Suite H: Interactive Puzzle Engine & Backtracking Tests (Prompt 07)
- `InteractiveMovementTest`:
  - Verify `StartPath` requires checkpoint #1; rejects start on ordinary cells or checkpoint 2+.
  - Verify legal horizontal and vertical `ExtendPath` steps.
  - Verify rejection of diagonal movement and multi-cell jumps with `NON_ADJACENT`.
  - Verify rejection of out-of-bounds moves with `OUT_OF_BOUNDS`.
  - Verify rejection of steps crossing a wall with `BLOCKED_BY_WALL`, while allowing legal detours.
  - Verify rejection of revisited cells / cycles with `CELL_ALREADY_VISITED`.
  - Verify sequential progression across checkpoints advancing `nextRequiredCheckpoint`.
  - Verify rejection of skipped checkpoints with `WRONG_CHECKPOINT_ORDER`.
  - Verify `PauseGame` and `ResumeGame` freeze and unfreeze actions.
- `BacktrackingAndResetTest`:
  - Verify single-step undo (`BacktrackOne`) removes last endpoint and restores previous state.
  - Verify intuitive drag-backtracking (moving to immediately preceding cell automatically undoes 1 step).
  - Verify multi-cell retraction (`BacktrackTo`) across a checkpoint restores the previous required checkpoint.
  - Verify backtracking to checkpoint 1 preserves a 1-cell starting path in `IN_PROGRESS`.
  - Verify `ResetPath` clears path, resets counters, and restores `NOT_STARTED` state.
- `InteractiveCompletionTest`:
  - **Section 26 TEST A**: Verify all checkpoints visited with uncovered cells fails completion validation (`INCOMPLETE_CELL_COVERAGE`).
  - **Section 26 TEST B**: Verify all cells covered in wrong checkpoint order fails completion validation (`WRONG_CHECKPOINT_ORDER`).
  - **Section 26 TEST C**: Verify all cells covered crossing a wall fails completion validation (`WALL_COLLISION`).
  - **Section 26 TEST D**: Verify all cells covered with a revisited cell fails completion validation (`REVISITED_CELL`).
  - **Section 26 TEST E**: Verify attempting to enter the final checkpoint prematurely is rejected (`PREMATURE_FINAL_CHECKPOINT`) and leaves the game `IN_PROGRESS`.
  - **Section 26 TEST F**: Verify full valid route transitions game to `COMPLETED`, produces authoritative `ValidatedCompletionResult`, freezes subsequent forward moves, and maps cleanly to `PuzzleBoardState.isSolved`.
  - Verify 5x5 puzzle with walls completes with verified full-coverage route.

### 3.9 Suite I: Exact Puzzle Solver & Uniqueness Verification Tests (Prompt 08)
- `PuzzleSolverSolvabilityTest`:
  - Verify valid 4x4, 5x5, 5x5 with walls, and 6x6 puzzles discover valid full-coverage paths.
  - Verify structurally invalid puzzles are rejected before search begins (`INVALID_PUZZLE`).
  - Verify structurally valid unsolvable puzzles (parity conflict, wall isolation) return `UNSOLVABLE` after exhaustive search.
  - Verify all discovered paths satisfy all 12 game rules and pass `CompletionValidator`.
  - Verify asynchronous solving interface (`solveAsync`) off main thread via coroutines.
  - Verify `SolutionValidator` detects defective candidate paths.
- `PuzzleSolverCountingAndUniquenessTest`:
  - Verify first-solution mode stops at first valid path and does NOT claim uniqueness (`isUnique == false`, `uniqueness == UNKNOWN`).
  - Verify exhaustive uniqueness check on unique puzzle proves uniqueness (`isUnique == true`, `uniqueness == UNIQUE`).
  - Verify multiple-solution detection on 3x3 board finds 2 distinct valid paths (`MULTIPLE_SOLUTIONS`, `uniqueness == NON_UNIQUE`).
  - Verify duplicate solutions are prevented.
  - Verify search limit before finding solution reports `SEARCH_LIMIT_REACHED` without claiming unsolvability (`isUnsolvable == false`).
  - Verify search limit after finding 1 solution preserves solution and reports `isSolved == true` with `isUnique == false`.
  - Verify cooperative cancellation via cancellation callback.
  - Verify deterministic repeated runs produce identical solutions, node counts, and backtracks.
- `PuzzleSolverPruningTest`:
  - Verify independent unit tests for all 5 sound mathematical pruning rules.
  - Verify correctness comparison between pruned solver and unpruned reference solver (`enablePruning = false`): identical solution counts and identical discovered paths.
- `PuzzleSolverBenchmarkTest`:
  - Benchmark measurements of elapsed monotonic time, nodes explored, and backtracks across 4x4, 5x5, 6x6, 7x7, and 8x8 boards.
  - Verify search limit handling on bounded budgets.

### 3.8 Suite H: Solver-Validated Puzzle Generator Tests (Prompt 09)
- `PuzzleGeneratorTest`:
  - Verify deterministic generation: identical seed produces identical output and canonical fingerprint.
  - Verify different seeds produce varied candidates and distinct fingerprints.
  - Verify generated checkpoints form contiguous sequence $1 \dots N$.
  - Verify checkpoint 1 occupies route origin and checkpoint $N$ occupies route terminus.
  - Verify checkpoints appear in strictly ascending order along solution traversal.
  - Verify generated walls connect orthogonally adjacent required cells within grid dimensions.
  - Verify strict invariant: no wall ever blocks any consecutive pair in the verified solution.
  - Verify wall counts strictly adhere to configured range $[minWalls..maxWalls]$.
  - Verify grid dimensions and required cells match configuration.
  - Verify 100% cell coverage by the verified solution.
  - Verify every accepted puzzle passes independent `PuzzleDefinitionValidator`.
  - Verify every accepted puzzle passes independent `PuzzleSolver`.
  - Verify authoritative world progression contract fixtures for Worlds 1 through 6.
  - Property-based testing across multiple seeds and grid topologies.
- `PuzzleGeneratorFailureTest`:
  - Verify invalid dimensions ($<2\times 2$) return `INVALID_CONFIGURATION`.
  - Verify checkpoint count $<2$ or $>cells$ returns `INVALID_CONFIGURATION`.
  - Verify impossible wall requests return `INVALID_CONFIGURATION`.
  - Verify zero or negative candidate attempts return `INVALID_CONFIGURATION`.
  - Verify solver search limit exhaustion rejects candidate cleanly without fake completion.
  - Verify cooperative cancellation immediately halts generation and returns `CANCELLED`.
  - Verify duplicate candidate detection rejects already-seen canonical fingerprints.
- `PuzzleGeneratorUniquenessTest`:
  - Verify proven unique candidate accepted when uniqueness is required (`uniquenessStatus == UNIQUE`).
  - Verify multiple-solution candidate rejected when uniqueness is required.
  - Verify solvable candidate accepted with `UNKNOWN` uniqueness when uniqueness is optional.
  - Verify inconclusive search never mislabeled as unique or unsolvable.
- `PuzzleGeneratorBenchmarkTest`:
  - Representative benchmark measurements across 4x4, 5x5, 6x6, 7x7, and 8x8 boards recording attempts, solver nodes, elapsed times, and uniqueness classifications.

### 3.4 Difficulty Analysis & Level Curation Test Suites (`curation/`) (Prompt 10)
- `PuzzleDifficultyAnalyzerTest`:
  - Structural metrics: required cells, rows, columns, checkpoint counts.
  - Wall complexity: wall count, density, wall-constrained cells.
  - Graph topology: traversable edge count, degree distribution (average, degree=2 constrained, degree>=3 branch, dead-ends).
  - Checkpoint distribution: gap sequence, min/max/average gap, gap variance, max unnumbered stretch ratio.
  - Route complexity: turn count, path length, turn frequency, straight segment lengths, horizontal/vertical moves.
  - Solver telemetry mapping: nodes explored, backtracks, pruned branches, uniqueness status.
  - Multi-solution and inconclusive search handling.
- `PuzzleQualityEvaluatorTest`:
  - Hard rejection: structurally invalid definitions.
  - Hard rejection: unsolvable candidates.
  - Hard rejection: non-unique candidates when uniqueness required.
  - Hard rejection: mismatched grid dimensions or world bounds violations.
  - Hard rejection: exact and symmetric duplicate detection (Dihedral $D_4$).
  - Similarity scoring: partial overlap and disjoint boards.
  - Soft quality scoring: route variation, checkpoint spacing, choice richness, wall relevance, progression fit reproducibility.
- `LevelCuratorTest`:
  - World progression contract: specifications for Worlds 1 through 6.
  - World 3 introductory wall levels (gentle 1–2 wall cap for levels 51–60).
  - Periodic recovery level calculation and difficulty reduction.
  - Bounded World 1 batch curation verifying complete level and metadata generation.
  - Level metadata completeness, provenance, and solution concealment.
- `LevelCuratorBenchmarkTest`:
  - Difficulty analysis throughput across multiple boards (<20ms per puzzle).
  - Curation pipeline benchmark measuring candidate counts, acceptance rate, and rejection breakdown.

### 3.9 Suite I: Verified Level Catalog Tests (Prompt 11)
- `LevelCatalogRepositoryTest`:
  - Verify loading manifest, world progression, level availability, and puzzle definitions.
- `CatalogAdmissionPipelineTest`:
  - Verify 10-step admission filter on valid, invalid, unsolvable, and non-unique assets.
- `CatalogIntegrityCheckerTest`:
  - Verify complete catalog integrity, checksums, world coverage, and contiguity.

### 3.10 Suite J: Interactive Gameplay & Touch Input Tests (Prompt 12)
- `GridCoordinateMapperTest`:
  - Verify deterministic mapping for corners (top-left, top-right, bottom-left, bottom-right).
  - Verify cell centers, boundaries, and out-of-bounds rejection.
  - Verify grid scaling across 4x4, 5x5, 6x6, 7x7, and 8x8 boards.
  - Verify tall, wide, and square viewport aspect ratios.
  - Verify fast-finger movement path resolution: straight-line interpolation, diagonal jump rejection (returns null).
- `GameplayEngineTouchIntegrationTest`:
  - Verify touching checkpoint 1 starts the path.
  - Verify touching non-start cell first is rejected (`START_MUST_BE_CHECKPOINT_ONE`).
  - Verify orthogonal drag movement extends path.
  - Verify diagonal drag is rejected (`NON_ADJACENT`).
  - Verify wall crossing is rejected (`BLOCKED_BY_WALL`).
  - Verify checkpoint skipping is rejected (`WRONG_CHECKPOINT_ORDER`).
  - Verify premature final-checkpoint entry is rejected (`PREMATURE_FINAL_CHECKPOINT`).
  - Verify drag backtracking to predecessor retracts path by 1 step.
  - Verify multi-cell drag backtracking truncates path prefix.
  - Verify Undo control retracts last move (`BacktrackOne`).
  - Verify Reset control clears path and restores initial state (`ResetPath`).
  - Verify complete verified solution produces validated victory with full coverage.
- `GameplayViewModelInteractiveTest` (`GameplayViewModelTest`):
  - Verify level loading and progression gate verification.
  - Verify `onCellEntered` state transitions and rejection reason handling.
  - Verify drag backtracking, undo, and reset.
  - Verify full puzzle completion records progress idempotently (single Room write).

### 3.15 Suite N: Gameplay Session Management, Timer, and Restoration Tests (Prompt 13)
- `GameplayTimerTest`:
  - Verify timer starts after accepted `StartPath`.
  - Verify invalid initial touch does not start timer.
  - Verify pause freezes duration.
  - Verify resume continues duration from accumulated time.
  - Verify repeated pause is strictly idempotent.
  - Verify repeated resume is strictly idempotent.
  - Verify background time is excluded.
  - Verify reset starts fresh attempt from 0 ms.
  - Verify completion freezes final duration.
  - Verify injectable `TestTimeProvider` produces deterministic results.
- `GameplaySessionValidatorTest`:
  - Verify valid partial path replay succeeds and restores exact engine state.
  - Verify empty path for fresh session succeeds.
  - Verify level ID mismatch rejection.
  - Verify puzzle ID mismatch rejection.
  - Verify puzzle version mismatch rejection.
  - Verify unsupported schema version rejection.
  - Verify completed session is not resumable.
  - Verify start not at checkpoint 1 rejection.
  - Verify illegal move / diagonal jump rejection.
  - Verify wall crossing rejection during replay.
  - Verify cycle / self-intersection rejection during replay.
  - Verify premature final checkpoint rejection during replay.
- `GameplaySessionRepositoryTest`:
  - Verify session insertion and lookup by level and ID.
  - Verify stale write rejection with lower or equal revision.
  - Verify completed session cannot be overwritten by active/paused snapshot.
  - Verify `pauseAllActiveSessions` enforces single active session policy.
  - Verify `markRestorationFailed` and status transitions.
  - Verify `deleteSessionsForLevel`.
- `RoomMigrationTest`:
  - Verify `MIGRATION_1_2` schema evolution.
  - Verify `MIGRATION_2_3` schema evolution adding puzzle ID, version, catalog version, revision, and snapshot schema version.
- `GameplayViewModelSessionTest`:
  - Verify fresh level session creation and timer start.
  - Verify app backgrounding pauses game and flushes snapshot to Room.
  - Verify safe session restoration from saved Room snapshot across launches.
  - Verify pause and resume controls preserve elapsed duration without drift.
  - Verify reset clears active path and resets timer in repository.
  - Verify completing puzzle marks session completed and records personal best idempotently.

### 3.14 Suite N: Hint Engine, Guidance & Usage Policy Tests (Prompt 14)
- `PuzzleHintEngineTest`:
  - Verify initial valid state returns a legal next move.
  - Verify mid-path state returns a compatible next move extending current prefix.
  - Verify hinted move is orthogonally adjacent, does not cross walls, does not revisit cells, and respects checkpoint order.
  - Verify hinted move belongs to a complete full-coverage solution validated by `CompletionValidator`.
  - Verify hint engine does not mutate the gameplay state.
  - Verify multiple-solution puzzle: hint extends the player's valid prefix even if different from witness route.
  - Verify structurally legal but uncompletable path returns `RECOVERY_REQUIRED` with solver-validated rollback prefix.
  - Verify solver budget exhaustion returns `SEARCH_INCONCLUSIVE` without false dead-end claims.
  - Verify competitive game mode rejects hint with `HINT_NOT_AVAILABLE`.
  - Verify LRU solution cache hits and invalidation on divergent prefix.
- `HintUsagePolicyTest`:
  - Verify free allowance allows hint request when balance > 0 and deducts atomically on useful delivery.
  - Verify free allowance exhaustion returns `USAGE_LIMIT_REACHED`.
  - Verify verified premium entitlement grants unlimited hints without decrementing counter.
  - Verify unverified client premium claim falls back to free policy.
  - Verify repeated identical hint request for same state does not consume duplicate allowance.
  - Verify cancelled and inconclusive searches do not consume allowance.
  - Verify hint usage persists across repository re-instantiation / app restarts.
- `GameplayViewModelHintTest`:
  - Verify hint loading, success, and limit reached UI states.
  - Verify state-revision invalidation on forward movement, undo, reset, and level completion.
  - Verify duplicate concurrent clicks do not spawn duplicate solver searches.
  - Verify hint display never mutates board path or advances covered cell count.
- `PuzzleHintBenchmarkTest`:
  - Measure representative hint computation latency across $4\times4, 5\times5, 6\times6, 7\times7, 8\times8$ grids.

---


## 4. Performance & Frame-Rate Benchmarks

1. **Canvas Render Frame-Time**:
   - Must sustain $\ge 60 \text{ FPS}$ on standard devices and $\ge 120 \text{ FPS}$ on high-refresh-rate displays ($< 8.33 \text{ ms}$ per frame).
   - Monitored using Jetpack Macrobenchmark and Android Studio Frame Rendering Profiler.
2. **Puzzle Generation Latency**:
   - World 1–3 ($4\times4, 5\times5$): $< 15 \text{ ms}$.
   - World 4–5 ($6\times6, 7\times7$): $< 40 \text{ ms}$.
   - World 6 ($8\times8$): $< 85 \text{ ms}$.
3. **Memory Footprint & Leaks**:
   - Client memory usage must remain under 150 MB during active continuous play.
   - Zero memory leaks detected via LeakCanary during rapid navigation between Solo, Duels, and Settings.
