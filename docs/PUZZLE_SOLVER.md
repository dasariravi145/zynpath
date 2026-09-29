# Zynpath Continuous-Path Puzzle Solver Specification

**Specification Status:** Authoritative & Multiplatform-Ready  
**Phase:** Phase 2 — Core Puzzle Engine (Prompt 8/50)  
**Package:** `com.zynpath.game.core.puzzle.solver`  

---

## 1. Problem Definition & Objectives

Zynpath puzzles require discovering a **checkpoint-constrained Hamiltonian path** across a planar grid graph $G = (V, E)$:
- **Start Constraint**: The path must originate at numbered checkpoint $1$.
- **Ascending Checkpoint Order**: Checkpoints $1, 2, \dots, N$ must be visited in strictly ascending numerical order ($1 \to 2 \to \dots \to N$).
- **Full Cell Coverage ($100\%$)**: The path must visit every required vertex $v \in V_{\text{req}}$ exactly once.
- **Planar Edge Obstacles**: Movement is strictly orthogonal and must not cross any blocked wall edge $(u, v) \in E_{\text{blocked}}$.
- **Terminal Constraint**: The path must terminate at the highest-numbered checkpoint ($N$). Entering checkpoint $N$ with uncovered cells remaining is strictly illegal.

The objective of `PuzzleSolver` is to provide an exact, deterministic search engine that:
1. Determines whether a validated `PuzzleDefinition` is solvable.
2. Finds valid full-coverage ordered solution paths (`PuzzlePath`).
3. Determines solution uniqueness when computationally feasible (`maxSolutions = 2`).
4. Rejects structurally invalid or provably unsolvable puzzle boards.
5. Operates with bounded resource limits (nodes, monotonic elapsed time, cooperative cancellation).
6. Ensures zero engine defects by independently verifying every discovered candidate against `CompletionValidator`.

---

## 2. Solver Architecture

```
com.zynpath.game.core.puzzle.solver
├── PuzzleSolver.kt               # Main solver entrypoint, recursive DFS, Warnsdorff ordering, solveAsync
├── SolverConfiguration.kt        # Search limits, pruning flags, cancellation callbacks
├── SolverResult.kt               # Immutable outcome: status, solutions, statistics, uniqueness
├── SolverStatus.kt               # SOLVED, UNSOLVABLE, MULTIPLE_SOLUTIONS, SEARCH_LIMIT_REACHED, CANCELLED, INVALID_PUZZLE
├── UniquenessStatus.kt           # UNIQUE, NON_UNIQUE, UNSOLVABLE, UNKNOWN
├── SolverStatistics.kt           # Nodes explored, backtracks, pruned branches, elapsed ms
├── SolverSearchState.kt          # Reversible state: visited bitmask, path stack, BFS generation buffers
├── SolverPruningRules.kt         # 5 sound mathematical pruning rules
└── SolutionValidator.kt          # Authoritative validation gate & engine defect detection
```

### 2.1 Reversible Search State (`SolverSearchState`)
To ensure high-performance execution without object allocation overhead during recursive exploration:
- **Visited Bitmask**: A flat primitive `BooleanArray` indexed by `row * colCount + col` provides $O(1)$ cell status queries.
- **Ordered Path Stack**: An `ArrayList<GridPosition>` maintains the exact chronological step sequence. Positions are pushed on forward traversal and popped on backtrack, avoiding graph copying.
- **$O(1)$ Generation-Stamped BFS Buffers**: Breadth-first search scratch buffers (`bfsQueue: IntArray` and `bfsVisitedToken: IntArray`) utilize an integer generation token (`bfsGeneration`). Advancing the token eliminates the need to zero-fill arrays between pruning checks.
- **Metrics Telemetry**: Tracks `nodesExplored`, `backtracks`, `prunedBranches`, and execution termination flags.

---

## 3. Exact Search Algorithm & Pruning Rules

The solver implements depth-first search with backtracking, enhanced by candidate ordering and 5 conservative pruning rules.

```
dfs(currentHead, nextRequiredCheckpoint, remainingRequired):
  1. Check node budget, monotonic time deadline, and cancellation signal every 256 nodes.
  2. Query traversable neighbors from planar GridGraph (orthogonal, unblocked).
  3. Filter illegal candidates:
     - Already visited in current path.
     - Out of checkpoint sequence (Rule 2).
     - Premature final checkpoint entry (Rule 1).
  4. Candidate Ordering:
     - Primary: Minimum onward unvisited degree (Warnsdorff's heuristic).
     - Secondary: Manhattan distance to target checkpoint (Proximity heuristic).
     - Tertiary: Deterministic coordinate order (row, column).
  5. For each candidate:
     - Sound Pruning (if remainingRequired > 1):
       - Connectivity flood-fill: all remaining cells reachable without routing through final checkpoint (Rule 3).
       - Intermediate cell degrees: all unvisited intermediate cells have degree >= 2 (Rule 4).
       - Checkpoint reachability: next checkpoint reachable without traversing higher checkpoints (Rule 5).
     - Step forward: mark visited, push to path, update nextRequiredCheckpoint.
     - If remainingRequired == 1 and cand == finalPos:
       - Validate candidate using SolutionValidator (raises engine defect on failure).
       - Record unique solution; backtrack if maxSolutions reached.
     - Else:
       - Recurse dfs(cand, ...).
     - Backtrack: pop path, unmark visited, increment backtrack counter.
```

### 3.1 Pruning Rules Specification

| Rule # | Name | Invariant Checked | Pruning Condition | Soundness Guarantee |
|---|---|---|---|---|
| **Rule 1** | Premature Final Checkpoint | Final checkpoint must be the exact final cell | `candidate == finalPos && remainingRequired > 1` | Mandatory win condition; cannot visit end early. |
| **Rule 2** | Checkpoint Sequence | Checkpoints must be visited $1 \to 2 \dots \to N$ | `cpNumber != null && cpNumber != nextCp` | Skipping or out-of-order checkpoints is strictly illegal. |
| **Rule 3** | Connectivity Flood-Fill | All unvisited required cells must form a connected component | Reached unvisited cells $< remainingRequired$ (treating `finalPos` as barrier) | Path is continuous and cannot exit `finalPos` to cover cut-off cells. |
| **Rule 4** | Intermediate Cell Degree | Intermediate cells must have degree $\ge 2$ | Any unvisited non-final cell has $< 2$ available edges, or final cell has $< 1$ | In a simple path, every intermediate cell requires 1 entry and 1 exit edge. |
| **Rule 5** | Checkpoint Reachability | Next checkpoint must be reachable legally | Target checkpoint unreachable through cells with checkpoint $\le \text{nextCp}$ | Traversal through higher checkpoints before the current checkpoint is strictly forbidden. |

All 5 rules are **sound necessary conditions**: they only prune branches where a valid solution is mathematically impossible. No valid Hamiltonian route is ever pruned.

---

## 4. Solution Counting & Uniqueness Semantics

Adheres strictly to Prompt 8 Section 19:

| Scenario | `status` | `solutionCount` | `isExhaustive` | `isSolved` | `isUnique` | `uniqueness` |
|---|---|---|---|---|---|---|
| **Exhaustive Unique Proof** (`maxSolutions=2`) | `SOLVED` | 1 | `true` | `true` | `true` | `UNIQUE` |
| **Multiple Solutions Discovered** | `MULTIPLE_SOLUTIONS` | $\ge 2$ | `false` | `true` | `false` | `NON_UNIQUE` |
| **First-Solution Mode** (`maxSolutions=1`) | `SOLVED` | 1 | `false` | `true` | `false` | `UNKNOWN` |
| **Exhaustive Unsolvable Proof** | `UNSOLVABLE` | 0 | `true` | `false` | `false` | `UNSOLVABLE` |
| **Node / Time Limit (With 1 Solution)** | `SEARCH_LIMIT_REACHED` | 1 | `false` | `true` | `false` | `UNKNOWN` |
| **Node / Time Limit (No Solutions)** | `SEARCH_LIMIT_REACHED` | 0 | `false` | `false` | `false` | `UNKNOWN` |
| **Cancelled Search** | `CANCELLED` | 0 or 1 | `false` | `solutions.isNotEmpty()` | `false` | `UNKNOWN` |
| **Malformed Definition** | `INVALID_PUZZLE` | 0 | `true` | `false` | `false` | `UNSOLVABLE` |

**Critical Invariants**:
- **Finding one solution proves solvability**, but does NOT automatically prove uniqueness.
- **Reaching a search limit does NOT prove unsolvability**. Unsolvability requires exhaustive proof.
- **Two distinct solutions prove non-uniqueness** regardless of whether the rest of the tree was searched.

---

## 5. Verified Deterministic Test Fixtures

| Fixture Name | Grid Size | Checkpoints | Walls | Expected Solutions | Classification |
|---|---|---|---|---|---|
| `puzzle4x4Valid` | 4×4 | #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0) | None | $\ge 1$ (Serpentine verified) | Solvable |
| `puzzle5x5Valid` | 5×5 | #1 at (0,0), #2 at (1,4), #3 at (2,0), #4 at (3,4), #5 at (4,4) | None | $\ge 1$ (Serpentine verified) | Solvable |
| `puzzle5x5WithWalls` | 5×5 | Same as 5x5 clean | 3 walls | $\ge 1$ (Walls bypassed) | Solvable |
| `puzzle6x6Valid` | 6×6 | 6 checkpoints along serpentine route | None | $\ge 1$ (36-cell verified) | Solvable |
| `puzzle7x7Valid` | 7×7 | 7 checkpoints along serpentine route | None | $\ge 1$ (49-cell verified) | Solvable |
| `puzzle8x8Valid` | 8×8 | 8 checkpoints along serpentine route | None | $\ge 1$ (64-cell verified) | Solvable |
| `puzzle3x3Unique` | 3×3 | #1 (0,0), #2 (0,2), #3 (2,0), #4 (2,2) | 2 walls | Exactly 1 | Proven Unique |
| `puzzle3x3MultipleSolutions` | 3×3 | #1 (0,0), #2 (2,2) | None | $\ge 2$ (Routes A & B verified) | Proven Non-Unique |
| `puzzle4x4UnsolvableParity` | 4×4 | #1 (0,0), #2 (2,2) | None | 0 | Proven Unsolvable (Parity) |
| `puzzle4x4UnsolvableWalls` | 4×4 | #1 (0,0), #2 (3,3) | (0,1) boxed | 0 | Proven Unsolvable (Isolated Cell) |

---

## 6. Android Integration Boundary

- **Off-Main-Thread Execution**: The solver provides `suspend fun solveAsync(definition, config, dispatcher = Dispatchers.Default)`.
- **Cooperative Coroutine Cancellation**: Polling checks `!coroutineContext.isActive` every 256 nodes, guaranteeing responsive cancellation when a user navigates away or starts a new action.
- **Zero UI Interruption**: The solver does not touch Android UI classes, Compose states, or modify the active path during background calculation.

---

## 7. Future Engine Contracts

### 7.1 Puzzle Generator Integration (Implemented in Prompt 9)
The solver acts as the authoritative verification engine for `PuzzleGenerator`:
1. Constructs candidate planar grid and Hamiltonian walk.
2. Places checkpoint #1 at origin, checkpoint $N$ at terminus, and intermediate checkpoints along the walk.
3. Places direction-independent `BlockedEdge` walls exclusively on non-route edges.
4. Validates candidate structure with `PuzzleDefinitionValidator`.
5. Runs `PuzzleSolver.solve(candidate)`:
   - If `UNSOLVABLE` or `SEARCH_LIMIT_REACHED`, rejects candidate.
   - If `MULTIPLE_SOLUTIONS` and uniqueness is required, rejects candidate.
   - If proven valid and unique (when configured), accepts puzzle definition.
6. Solution is independently confirmed with `CompletionValidator`.
See `docs/PUZZLE_GENERATOR.md` for full implementation details.

### 7.2 Hint Engine Integration (Implemented in Prompt 14)
1. `PartialPathValidator` validates player's actual continuous path prefix.
2. `PuzzleSolver.solveFromPartialPath(definition, partialPath, config)` pre-populates `SolverSearchState` with visited cells and begins forward exploration directly from the player's path endpoint.
3. If an exhaustive search proves no full continuation exists (`UNSOLVABLE`), `PuzzleHintEngine` executes backward prefix analysis to discover the longest recoverable prefix with a proven solution.
4. If a continuation exists, the solver returns the complete Hamiltonian continuation and recommends the immediate next step.
5. All operations run asynchronously off the main thread with cooperative cancellation polling.

### 7.3 Backend Headless Validation Contract (Spring Boot 3)
1. Competitive multiplayer submissions submit ordered cell arrays.
2. Backend runs independent `CompletionValidator` in $O(L)$ time.
3. Backend does not trust client victory flags or coverage percentages.

### 7.4 Difficulty Analyzer & Curation Integration (Prompt 10)
`PuzzleDifficultyAnalyzer` consumes `SolverStatistics` (`nodesExplored`, `backtracks`, `prunedBranches`) and `UniquenessStatus` to objectively gauge search tree complexity and enforce hard uniqueness constraints during level curation.

