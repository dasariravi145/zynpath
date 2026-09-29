# Zynpath Puzzle Difficulty Analysis & Algorithmic Scoring

## Overview

The Zynpath difficulty-analysis system evaluates solver-validated number path puzzles using objective structural metrics, checkpoint distribution, route traversal characteristics, wall constriction, and exact solver search behavior.

It explicitly decouples **mathematical solvability** from **human-perceived difficulty** and treats all algorithmic difficulty ratings as provisional until calibrated against aggregate player gameplay data in future phases.

---

## 1. Difficulty Architecture

The difficulty system is implemented in pure Kotlin in `com.zynpath.game.core.puzzle.curation`:

- **`DifficultyBand`**: Coarse classification bands (`BEGINNER`, `EASY`, `MEDIUM`, `HARD`, `EXPERT`).
- **`DifficultyMetrics`**: Granular immutable container holding all objective measurements.
- **`DifficultyAnalysisConfiguration`**: Versioned weights and search limits (`CONFIG_VERSION = 1.0.0`).
- **`DifficultyAnalysisResult`**: Composite normalized score $[0.0, 1.0]$, band, component breakdowns, and provisional flags.
- **`PuzzleDifficultyAnalyzer`**: Deterministic analyzer computing metrics and weighted composite scores.

---

## 2. Objective Metric Definitions & Formulas

### A. Structural & Planar Graph Topology
1. **Grid Rows & Columns** ($R, C$): Bounding dimensions of the puzzle grid.
2. **Total Required Cells** ($N$): Count of cells that must be traversed for complete coverage.
3. **Traversable Edge Count** ($E$): $\sum_{v \in V} \text{deg}(v) / 2$, where $\text{deg}(v)$ is the count of open orthogonal neighbors respecting board boundaries and walls.
4. **Average Traversable Degree** ($D_{\text{avg}}$): $2E / N$.
5. **Constrained Cells** ($C_2$): Count of cells with traversable degree $= 2$ (mandatory pass-throughs).
6. **Branching Cells** ($C_{\ge 3}$): Count of cells with traversable degree $\ge 3$ (decision branch points).
7. **Dead-End Cells** ($C_{\le 1}$): Count of intermediate cells with degree $\le 1$ (excluding start and final checkpoints).

### B. Checkpoint Distribution & Spacing
Along the verified solution path $P = [p_0, p_1, \dots, p_{N-1}]$:
1. **Checkpoint Gaps** ($g_i$): $g_i = \text{index}(CP_{i+1}) - \text{index}(CP_i)$ for $i \in [1, K-1]$.
2. **Minimum Gap** ($\min g_i$) and **Maximum Gap** ($\max g_i$).
3. **Average Gap** ($\mu_g$): $\frac{1}{K-1} \sum g_i$.
4. **Gap Variance** ($\sigma_g^2$): $\frac{1}{K-1} \sum (g_i - \mu_g)^2$.
5. **Max Stretch Ratio**: $\frac{\max g_i}{N}$ (fraction of board traversed without number guidance).

### C. Route Traversal Complexity
1. **Path Length** ($L$): Exactly $N$ for valid full-coverage paths.
2. **Turn Count** ($T$): Number of direction changes where $(dr_{i+1}, dc_{i+1}) \neq (dr_i, dc_i)$.
3. **Turn Frequency**: $\frac{T}{L - 2}$ (normalized turn density; winding labyrinths exhibit higher frequency).
4. **Straight Segments**: Contiguous collinear runs. $\text{count} = T + 1$.
5. **Longest Straight Segment**: $\max$ segment length.
6. **Movement Ratio**: Horizontal moves vs. vertical moves.

### D. Wall Complexity
1. **Wall Count** ($W$): Number of normalized blocked edges (`BlockedEdge`).
2. **Wall Density**: $W / E_{\text{max}}$, where $E_{\text{max}} = R(C-1) + (R-1)C$.
3. **Wall-Constrained Cells**: Count of cells touching at least one blocked edge.

### E. Solver-Derived Search Behavior
1. **Explored Search Nodes**: Total recursive state transitions evaluated by `PuzzleSolver`.
2. **Backtrack Count**: Number of search dead-ends encountered and rewound.
3. **Pruned Branches**: Invariant violations intercepted by `SolverPruningRules`.
4. **Uniqueness Status**: Rigorously proven `UNIQUE`, `NON_UNIQUE`, `UNSOLVABLE`, or `UNKNOWN`.
5. **Discovered Solution Count**: Number of distinct valid Hamiltonian paths found within budget.

---

## 3. Composite Difficulty Scoring & Weights

Difficulty scores are normalized to $[0.0, 1.0]$ using versioned weights:

$$\text{Score} = w_{\text{size}} S_{\text{size}} + w_{\text{walls}} S_{\text{walls}} + w_{\text{cp}} S_{\text{cp}} + w_{\text{turns}} S_{\text{turns}} + w_{\text{gaps}} S_{\text{gaps}} + w_{\text{solver}} S_{\text{solver}}$$

### Default Normalized Weights (`DifficultyAnalysisConfiguration` v1.0.0):
| Component | Weight | Rationale |
|---|---|---|
| **Board Size** | 0.20 | Larger grids expand state space from 16 to 64 cells. |
| **Wall Constriction** | 0.20 | Walls block obvious paths and introduce deceptive detours. |
| **Checkpoint Density** | 0.15 | Fewer checkpoints mean less guidance and larger search horizon. |
| **Route Turns** | 0.15 | Highly tortuous routes require greater spatial visualization. |
| **Max Stretch Gap** | 0.15 | Long unnumbered stretches introduce high branch ambiguity. |
| **Solver Search Nodes** | 0.15 | Search tree depth and backtracks indicate branching complexity. |

---

## 4. Difficulty Bands

| Band | Score Range | Characteristics | Target Worlds |
|---|---|---|---|
| **BEGINNER** | $0.00 \dots <0.20$ | 4×4 grid, no walls, dense checkpoints, clear visual flow. | World 1 |
| **EASY** | $0.20 \dots <0.40$ | 5×5 grid, no walls, moderate spacing. | World 2 |
| **MEDIUM** | $0.40 \dots <0.60$ | 5×5/6×6 grid, 1–5 walls, interesting turns. | Worlds 3 & 4 |
| **HARD** | $0.60 \dots <0.80$ | 6×6/7×7 grid, 4–12 walls, sparse checkpoints. | Worlds 4 & 5 |
| **EXPERT** | $0.80 \dots 1.00$ | 7×7/8×8 grid, 6–18 walls, high branching ambiguity. | Worlds 5 & 6 |

---

## 5. Human Difficulty Limitation & Future Calibration

> [!WARNING]
> **Provisional Model Notice:** All difficulty ratings are generated algorithmically. Solver runtime or search node count does not directly equal human difficulty; humans use spatial pattern recognition and visual chunking, while solvers use depth-first search.
>
> In future releases (Prompt 11+ and Production Telemetry), difficulty curves will be calibrated against anonymous, privacy-preserving aggregate metrics:
> - Completion Rate (% of players finishing the level)
> - Average Completion Time
> - Restart / Reset Frequency
> - Hint Usage Frequency
> - Abandonment Rate
