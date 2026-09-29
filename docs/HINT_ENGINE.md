# Zynpath Gameplay Hint Engine Specification

## 1. Overview & Mathematical Objectives
The Zynpath Gameplay Hint Engine provides solution-aware, offline guidance for Solo gameplay. Unlike naive hint systems that blindly regurgitate a generator's precomputed witness path, Zynpath's hint engine is:
1. **Current-Path Compatible**: Respects the player's actual continuous path prefix. If the puzzle has multiple valid Hamiltonian routes and the player takes an alternate branch, the hint system extends that specific branch rather than forcing the generator's original route.
2. **Proof-Backed**: Every `NEXT_MOVE` hint is mathematically guaranteed to belong to at least one valid full-coverage Hamiltonian continuation validated by `CompletionValidator`.
3. **Dead-End Aware**: If the player's path cannot be completed, exhaustive search detects this condition and provides structured `RECOVERY_REQUIRED` guidance.
4. **Offline & Zero-Cost**: Runs purely locally via `Dispatchers.Default` using deterministic DFS backtracking and Warnsdorff candidate ordering.

---

## 2. Architecture & Components

```
+-------------------------------------------------------------+
|                     GameplayViewModel                       |
|  - Debounces rapid taps                                     |
|  - Binds hint lifecycle to game-state revision              |
|  - Coordinates allowance consumption on delivery            |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                     PuzzleHintEngine                        |
|  - Input validation & competitive fairness guards           |
|  - In-process LRU verified solution cache                   |
|  - Partial-path solver orchestration                        |
|  - Dead-end detection & backward prefix recovery search      |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                       PuzzleSolver                          |
|  - solveFromPartialPath(definition, partialPath, config)     |
|  - Pruning rules: connectivity, degree consistency, reachability|
|  - Full-coverage Hamiltonian solution validation            |
+-------------------------------------------------------------+
```

### 2.1 Core Types (`com.zynpath.game.core.puzzle.hint`)
- `HintType`: Enumerates outcomes (`NEXT_MOVE`, `RECOVERY_REQUIRED`, `ALREADY_COMPLETED`, `SEARCH_INCONCLUSIVE`, `CANCELLED`, `INVALID_STATE`, `HINT_NOT_AVAILABLE`, `USAGE_LIMIT_REACHED`).
- `HintRequest`: Encapsulates puzzle ID, puzzle version, definition, `PuzzleGameState`, current path, next required checkpoint, `GameMode`, and `HintConfiguration`.
- `HintResult`: Sealed hierarchy returning rich structured data (target coordinate, full continuation, recovery rollback position, steps to retract, etc.).
- `HintConfiguration`: Bounded search parameters (`nodeLimit`, `timeBudgetMs`, `recoveryNodeLimit`, `recoveryTimeBudgetMs`, `maxPrefixesToAnalyze`).
- `HintCache`: Bounded LRU cache storing proven solutions for fast prefix matching.

---

## 3. Partial-Path Validation & Solver Integration
Before launching search, `PartialPathValidator` verifies:
- Path begins at Checkpoint #1.
- Consecutive cells are orthogonally adjacent.
- No blocked edge (wall) is crossed.
- No cell is repeated (self-avoiding continuous path).
- Checkpoints are visited in strictly ascending order ($1 \to 2 \dots \to K$).
- Endpoint matches engine state.

The solver initializes `SolverSearchState` by pre-populating the player's path into the visited bitmask. DFS exploration begins directly at the path's head, targeting the next required checkpoint and remaining unvisited required cells.

---

## 4. Dead-End Detection & Recovery Guidance
When exhaustive search from the current path produces zero solutions (`SolverStatus.UNSOLVABLE`):
1. The engine recognizes the puzzle is not globally unsolvable, but the current path prefix has trapped unvisited cells.
2. It executes bounded prefix analysis, evaluating prefixes of length $L - 1, L - 2, \dots$ down to the start position.
3. It identifies the longest earlier prefix with a verified legal continuation.
4. It delivers `HintResult.RecoveryRequired` specifying:
   - `recommendedRollbackPosition`: Grid coordinate to retract to.
   - `stepsToRetract`: Exact count of undo steps needed.
   - `explanation`: Clear message explaining the dead end.
5. The player's path is never automatically erased; confirmation is required via the Recovery Dialog.

---

## 5. UI Presentation & Lifetime
- **Cell Highlight**: Rendered on `PuzzleBoard` as a subtle golden halo and indicator ring on the hinted coordinate.
- **Lifetime**: Tied strictly to the current game state revision. The moment the player moves, undoes, resets, or pauses, the hint is cleared immediately.
- **Non-Mutation**: Requesting or displaying a hint never mutates covered cell counts, path length, timer duration, or checkpoint progression.
- **Reduced Motion Adaptation**: When reduced motion is enabled in system or app preferences, the golden guidance ring renders with static opacity rather than an oscillating breathing pulse.
