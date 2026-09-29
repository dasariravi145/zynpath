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
- **Checkpoints:** 4 to 6 checkpoints
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
- **Walls:** 1 to 5 wall segments placed on interior cell edges (Levels 51–60 gently introduced with 1–2 walls)
- **Checkpoints:** 4 to 7 checkpoints
- **Objective:** Introduce impassable barriers. Forces players to route around cul-de-sacs, creating mandatory detour corridors and dead-end traps.

### World 4 — Complex Routes
- **Levels:** 101–150
- **Grid Size:** $6 \times 6$ (36 total cells)
- **Walls:** 2 to 8 wall segments
- **Checkpoints:** 4 to 8 checkpoints
- **Objective:** Demands spatial planning. Increased surface area requires careful management of parity, corner pockets, and perimeter looping.

### World 5 — Advanced Logic
- **Levels:** 151–200
- **Grid Size:** $7 \times 7$ (49 total cells)
- **Walls:** 4 to 12 wall segments
- **Checkpoints:** 4 to 10 checkpoints
- **Objective:** Severe constraint satisfaction. High checkpoint count combined with strategic walls dramatically limits viable path alternatives, punishing premature path closure.

### World 6 — Expert Path
- **Levels:** 201–300
- **Grid Size:** $8 \times 8$ (64 total cells)
- **Walls:** 6 to 18 wall segments
- **Checkpoints:** 4 to 12 checkpoints
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

1. **Authoritative Configuration (`WorldConfiguration.kt`)**:
   - `WorldConfiguration.kt` is the single source of truth for all 6 worlds, grid dimensions, level ranges, and unlock rules across the Android codebase.
   - World 1 (Levels 1–20) is unlocked immediately upon install for guest players.
   - Completing the final level of a World (or meeting the world unlock threshold) unlocks the subsequent World.
   - Sequential progression: Completing level $L$ deterministically unlocks level $L+1$.
   - Any previously unlocked or completed level remains permanently replayable.

2. **Level Stars (1–3 Stars Policy)**:
   - ⭐ **1 Star (Completion)**: Solve the puzzle meeting all rules (used 2+ hints).
   - ⭐⭐ **2 Stars (Efficiency)**: Solve the puzzle meeting all rules with only 1 hint used.
   - ⭐⭐⭐ **3 Stars (Mastery)**: Solve the puzzle meeting all rules without using any hints (0 hints).

3. **Replayability & Personal Best Records**:
   - Replaying a level never overwrites a superior record with a worse result.
   - Faster completion times, fewer moves, and higher star ratings update personal bests.
   - Slower replays still increment total `completionCount` and update `lastCompletedAt` while safely preserving `bestTimeMs`, `movesCount`, and `firstCompletedAt`.

---

---

## 6. Offline Level Catalog & Availability (Prompt 11)

The campaign level catalog binds progression to pre-verified offline puzzle assets:

1. **`LevelAvailability` Enum**:
   - `LOCKED`: Level is locked behind prerequisite progression.
   - `UNLOCKED_AND_AVAILABLE`: Unlocked and verified puzzle asset is packaged locally and ready to play.
   - `COMPLETED`: Solved and verified by the player; permanently replayable.
   - `ASSET_UNAVAILABLE`: Unlocked by progression, but content is pending in the catalog manifest (`hasPackagedAsset = false`).
   - `ASSET_INVALID`: Asset file exists but failed runtime schema or SHA-256 fingerprint verification.

2. **Integrity Guarantees**:
   - Tapping an `ASSET_UNAVAILABLE` or `ASSET_INVALID` card is non-destructive and prevents launching broken boards.
   - Level assignments ($L \leftrightarrow \text{puzzleId}$) remain permanent across app versions.

---

## 7. Progression Milestone Achievements (Prompt 17)

Validated campaign completions trigger local achievement unlocks via `AchievementRegistry`:
- **`solo_first_step`**: Unlocks upon completing level 1.
- **`solo_apprentice`**: Unlocks upon completing 5 distinct levels.
- **`solo_journeyman`**: Unlocks upon completing 15 distinct levels.
- **`world_one_pioneer`**: Unlocks upon completing all levels in World 1 (levels 1–5).
- **`pure_intellect`**: Unlocks upon achieving a 3-star hint-free solve (`bestHintCount == 0`).
- **`speed_demon`**: Unlocks upon completing any level in under 30 seconds (`bestTimeMs <= 30000`).



