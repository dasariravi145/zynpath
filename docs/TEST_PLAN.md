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
