# Zynpath Comprehensive Test Strategy & QA Architecture

## 1. Overview & Quality Objectives

Zynpath is a deterministic number path puzzle game engineered for high reliability, mathematically sound gameplay, and resilient offline progression. The core philosophy is that **no player should ever encounter an unsolvable puzzle, an illegal hint, a corrupted session, or a lost personal best**.

This document outlines the testing architecture, methodology, test pyramid, and verification invariants governing Zynpath across its native Android Kotlin implementation and offline gameplay stack.

---

## 2. Test Architecture & The Verification Pyramid

```
                ▲
               / \
              /   \
             / E2E \       (UI Automator, Manual Play Console)
            /-------\
           /  Integ  \     (Room Migrations, DataStore, Repo Integration)
          /-----------\
         / Cat. Audits \   (Complete 300-Level Manifest & Shipped Asset Audits)
        /---------------\
       / Property Tests  \ (Boundary conditions, Wall symmetry, Orthogonality)
      /-------------------\
     /     Unit Tests      \ (Pure Kotlin PuzzleEngine, Solver, Generator, Validator)
    /-----------------------\
```

### 2.1 Pure Kotlin Headless Domain Layer
All core gameplay mathematical models, puzzle rules, solver algorithms, generator routines, catalog manifests, and session validators reside in the decoupled pure Kotlin package `com.zynpath.game.core.puzzle`.
- **Zero Android Framework Dependencies:** Runs entirely on standard JVM, executing thousands of iterations per second without mocking android.jar or requiring an emulator.
- **Fast Feedback Loop:** Full unit test execution runs under 60 seconds on developer machines and CI pipelines.

### 2.2 Property-Based & Combinatorial Testing
- Exhaustive verification of grid coordinate boundaries, 4-corner boundary transitions, and illegal movement vectors (diagonals, teleports).
- Bidirectional wall symmetry (`BlockedEdge.between(a, b) == BlockedEdge.between(b, a)`).
- Canonical rule enforcement across all valid and invalid permutations.

### 2.3 Shipped Asset & Catalog Verification
- Every shipped puzzle is subject to a 10-step admission and audit pipeline:
  1. Valid dimensions and coordinates.
  2. Total required cells match grid dimensions.
  3. Contiguous ascending checkpoint numbering (1..N).
  4. Orthogonally adjacent wall placements.
  5. Deterministic SHA-256 fingerprint verification.
  6. Independent solver execution (`PuzzleSolver`).
  7. Discovery of full-coverage valid solution path.
  8. Validation through `CompletionValidator`.
  9. Validation through `FoundationalPathValidator`.
  10. Duplicate detection against all other catalog levels.

---

## 3. The 7 Canonical Puzzle Invariants

Every puzzle validation suite enforces the 7 canonical rules without compromise:

1. **Rule 1 (Start Point):** The path must begin at Checkpoint 1 (`GridPosition` matching `NumberedCheckpoint(1)`).
2. **Rule 2 (Orthogonal Movement):** Every movement between consecutive steps must be strictly orthogonal (Manhattan distance = 1: UP, DOWN, LEFT, RIGHT). Diagonals and teleports are strictly invalid.
3. **Rule 3 (Checkpoint Sequence):** Numbered checkpoints must be visited in strict ascending order (1, 2, 3, ..., N). Skipped, reversed, or out-of-order visits immediately violate game rules.
4. **Rule 4 (No Cell Revisit):** The path must never cross or revisit an already occupied cell.
5. **Rule 5 (Blocked Edges / Walls):** Walls are blocked edges between adjacent cells. Movement across a blocked edge is strictly impassable from either direction. Walls do not remove cells from the grid; all cells remain required.
6. **Rule 6 (Full-Grid Coverage):** The path must visit every single required cell on the board exactly once. A path reaching the highest checkpoint with uncovered cells is strictly an incomplete state, never a win.
7. **Rule 7 (Final Checkpoint Victory):** Reaching the highest checkpoint is necessary but not sufficient on its own. Victory triggers only when Rule 6 and all preceding rules are satisfied.

---

## 4. Solver Testing Strategy

The `PuzzleSolver` utilizes recursive depth-first backtracking with reachability pruning, articulation point detection, and checkpoint ordering constraints.

### Test Categories:
- **Completeness:** Solves representative boards (3x3, 4x4, 5x5, 6x6) and proves existence of valid solutions.
- **Negative Cases (Unsolvability):**
  - Confirms `SolverStatus.UNSOLVABLE` on partitioned boards, unreachable cells, contradictory walls enclosing cells, and premature final checkpoint bottlenecks.
- **Determinism:** Verifies that multiple solver runs on identical puzzle definitions explore identical node counts and return identical solution paths.
- **Uniqueness Proof:** Configured with `maxSolutions = 2` to distinguish `UniquenessStatus.UNIQUE` from `MULTI_SOLUTION`.
- **Termination Bounds:** Ensures safe timeout and node budget exhaustion (`SolverStatus.NODE_LIMIT_EXCEEDED`, `TIME_LIMIT_EXCEEDED`) without hanging or throwing unhandled exceptions.

---

## 5. Generator Testing Strategy

The `PuzzleGenerator` creates playable number path puzzles from deterministic seeds:
- **Seed Determinism:** Identical `(seed, dimensions, checkpointCount, walls)` inputs must produce bit-for-bit identical puzzle definitions.
- **Solver In-the-Loop:** Every generated candidate is solved before admission; unverified candidates are discarded.
- **Failure Resilience:** Invalid configurations (e.g. grid < 2x2, checkpoints < 2) fail safely with structured diagnostics (`GenerationStatus.INVALID_CONFIGURATION`) without crashing or producing corrupted boards.

---

## 6. Complete 300-Level Catalog Audit Specification

The campaign spans 6 authoritative worlds:

| World | Name | Level Range | Dimensions | Checkpoints | Walls | Expected Band |
|---|---|---|---|---|---|---|
| **World 1** | Learn the Path | 1–20 | 4×4 | 4–6 | 0 | BEGINNER |
| **World 2** | Longer Connections | 21–50 | 5×5 | 4–7 | 0 | EASY |
| **World 3** | Wall Challenge | 51–100 | 5×5 | 4–7 | 1–5 | MEDIUM |
| **World 4** | Complex Routes | 101–150 | 6×6 | 4–8 | 2–8 | MEDIUM |
| **World 5** | Advanced Logic | 151–200 | 7×7 | 4–10 | 4–12 | HARD |
| **World 6** | Expert Path | 201–300 | 8×8 | 4–12 | 6–18 | EXPERT |

### Audit Assertions:
1. All 300 level IDs (1..300) are contiguous and unique.
2. Every level is assigned to its exact canonical world.
3. Every world matches its canonical grid dimensions, checkpoint range, and wall range.
4. For all shipped packaged levels (`PackagedPuzzles.ALL_PACKAGED`):
   - All levels are solved by `PuzzleSolver`.
   - All solutions pass `CompletionValidator` and `FoundationalPathValidator`.
   - Zero duplicate puzzle fingerprints exist across the shipped catalog.

---

## 7. Progression, Replay & Persistence Testing

- **Unlock Hierarchy:** World 1 and Level 1 are unlocked by default. Subsequent levels unlock strictly upon completion of the preceding level.
- **Replay Invariance:** Replaying an already completed level increments `completionCount` on the record, but never increases the unique completed level count.
- **Personal Best Times:** Faster completions update `bestTimeMs`; slower replays preserve the existing faster record.
- **Session Snapshots:** Room `game_sessions` and `level_progress` persist state durably across app restarts.
- **Timer Backgrounding:** `GameplayTimer` calculates active duration via monotonic clock deltas, freezing time while paused or backgrounded.

---

## 8. Defect Classification & Triage Policy

| Severity | Definition | Action Required |
|---|---|---|
| **P0 - Blocker** | Solvable puzzle rejected by validator, or invalid path accepted as win; data loss on upgrade. | Immediate fix before release. Rules cannot be weakened. |
| **P1 - Critical** | Solver infinite loop / OOM on bounded puzzle; crash on malformed session snapshot. | Hard bounds enforced; safe fallback handler required. |
| **P2 - Major** | Slower replay overwriting faster best time; duplicate unique level completion count. | Correct state accumulation in repository. |
| **P3 - Minor** | Formatting anomaly in diagnostics or non-blocking metadata mismatch. | Track in regression register. |
