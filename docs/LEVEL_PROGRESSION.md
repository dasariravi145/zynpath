# Zynpath Level Progression & Curriculum Design

**Document Status:** Approved & Authoritative  
**Domain:** Game Design, World Tiers, Difficulty Modeling  

---

## 1. Curriculum Overview

Zynpath structures puzzle progression across six distinct **Worlds**, comprising 300 curated levels in the base campaign, complemented by procedurally generated Daily Challenges.

Progression is designed with a calibrated cognitive curve: teaching the fundamental mechanic of orthogonal continuous coverage before introducing walls, expanding board dimensions, and requiring complex multi-step foresight.

```
World 1 (4×4)   ──>  World 2 (5×5)   ──>  World 3 (5×5 + Walls)
[Basics: 1-20]       [Length: 21-50]      [Barriers: 51-100]
      │
      ▼
World 4 (6×6)   ──>  World 5 (7×7)   ──>  World 6 (8×8)
[Routes: 101-150]    [Logic: 151-200]     [Expert: 201-300]
```

---

## 2. World Breakdown

### World 1 — Learn the Path
- **Levels:** 1–20
- **Grid Size:** $4 \times 4$ (16 total cells)
- **Walls:** None (0 walls)
- **Checkpoints:** 3 to 5 checkpoints
- **Objective:** Teach the dual win condition: players must realize that connecting checkpoints directly without weaving through remaining empty cells will fail. Promotes understanding of corners, edges, and full-cell coverage.

### World 2 — Longer Connections
- **Levels:** 21–50
- **Grid Size:** $5 \times 5$ (25 total cells)
- **Walls:** None (0 walls)
- **Checkpoints:** 4 to 7 checkpoints
- **Objective:** Introduce longer distances between numbers. Requires players to deliberately snake through open board sections to absorb cell space between checkpoint pairs.

### World 3 — Wall Challenge
- **Levels:** 51–100
- **Grid Size:** $5 \times 5$ (25 total cells)
- **Walls:** 2 to 6 wall segments placed on interior cell edges
- **Checkpoints:** 4 to 8 checkpoints
- **Objective:** Introduce impassable barriers. Forces players to route around cul-de-sacs, creating mandatory detour corridors and dead-end traps.

### World 4 — Complex Routes
- **Levels:** 101–150
- **Grid Size:** $6 \times 6$ (36 total cells)
- **Walls:** 4 to 10 wall segments
- **Checkpoints:** 5 to 10 checkpoints
- **Objective:** Demands spatial planning. Increased surface area requires careful management of parity, corner pockets, and perimeter looping.

### World 5 — Advanced Logic
- **Levels:** 151–200
- **Grid Size:** $7 \times 7$ (49 total cells)
- **Walls:** 6 to 14 wall segments
- **Checkpoints:** 6 to 12 checkpoints
- **Objective:** Severe constraint satisfaction. High checkpoint count combined with strategic walls dramatically limits viable path alternatives, punishing premature path closure.

### World 6 — Expert Path
- **Levels:** 201–300
- **Grid Size:** $8 \times 8$ (64 total cells)
- **Walls:** 8 to 20 wall segments
- **Checkpoints:** 8 to 16 checkpoints
- **Objective:** Grandmaster logic challenges. Demands full visual forward projection, advanced topological deduction, and disciplined spatial routing.

---

## 3. Multi-Dimensional Difficulty Algorithm

Difficulty is **not a naive function of grid dimensions**. The engine calculates a composite **Difficulty Score ($D \in [0, 100]$)** based on six weighted metrics:

$$D = w_g \cdot G + w_t \cdot T + w_s \cdot S + w_w \cdot W + w_b \cdot B + w_u \cdot U$$

| Metric | Factor Name | Description | Weight ($w$) |
|---|---|---|---|
| **$G$** | Grid Cell Count | Total playable cells on the board ($\text{rows} \times \text{cols}$). | 0.20 |
| **$T$** | Turn Complexity | Number of $90^\circ$ direction changes in the optimal path. Higher turns require more routing foresight. | 0.20 |
| **$S$** | Checkpoint Spacing Variance | Variance in path length between consecutive checkpoint pairs. High variance increases deceptive dead-ends. | 0.15 |
| **$W$** | Wall Constraint Factor | Ratio of interior walls to available internal edges, creating choke points. | 0.15 |
| **$B$** | Branching Entropy | Average number of unvisited orthogonal neighbors per cell along failed search paths. | 0.15 |
| **$U$** | Solution Uniqueness Gap | Step distance in backtracking before alternative paths fail (forcing a unique deduction). | 0.15 |

---

## 4. Unlock Mechanics & Star Rating

1. **World Unlocking**:
   - World 1 is unlocked immediately upon install.
   - Subsequent Worlds unlock upon completing 75% of levels in the immediately preceding World (e.g., World 2 unlocks after completing 15 levels of World 1).
2. **Level Stars (1–3 Stars)**:
   - ⭐ **1 Star (Completion)**: Solve the puzzle meeting all rules.
   - ⭐⭐ **2 Stars (Efficiency)**: Solve without resetting the board and within $\le 3$ manual undo steps.
   - ⭐⭐⭐ **3 Stars (Mastery)**: Solve without any hints, without resetting, and in under the target par time (e.g., 45 seconds on 5×5).
3. **Replayability**:
   - Players can replay any solved level at any time to upgrade their star score or beat their personal best time.
   - Star totals are displayed on World select screens and player profile cards.
