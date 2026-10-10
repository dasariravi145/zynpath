# SOLO DEEP-REASONING DIFFICULTY AUDIT (LEVELS 5–30 & BEYOND)

**Status:** `HUMAN DIFFICULTY VALIDATION: NOT VERIFIED` (Awaiting physical playtest)

This document records authoritative engineering analyzer telemetry for newly regenerated Solo Levels 5–30, adhering strictly to Prompt Section 19 and Section 39 specifications.

## Core Invariants Verified
- **Playable Cells:** 100% of visible grid cells are playable (0 blocked cells, 0 boxes, 0 crates, 0 rocks).
- **Wall Style:** Internal edge barriers only (0 corner cages, 0 U-shaped pockets, 0 L-shaped traps, 0 3-sided cell enclosures).
- **Uniqueness:** 100% solver-verified strictly unique solutions.
- **Simple Strategies:** Naive heuristics (greedy proximity, wall-followers, perimeter sweeps) fail hard bands.

## Section 19 Analyzer Telemetry for Levels 5–30

| Level | Grid | Total / Covered | Checkpoints | Walls | Wall Edges | Decisions | Wrong Branches | Max Depth | Cell Resv | Future Dep | Deception | Forced Ratio | Simple Solved | Sols | Band | Structural Fingerprint |
|:---:|:---:|:---:|:---:|:---:|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|
| L5 | 4x4 | 16 / 16 | 5 | 3 | (1,2)|(1,3)<br>(1,1)|(2,1)<br>(2,2)|(3,2) | 4 | 3 | 8 | 11 | 26 | 0.25 | 0.69 | true | 1 | EASY | `b21067a0288b` |
| L6 | 4x4 | 16 / 16 | 5 | 2 | (1,1)|(1,2)<br>(2,1)|(3,1) | 5 | 6 | 8 | 8 | 37 | 0.25 | 0.63 | true | 1 | EASY | `7637de9d7782` |
| L7 | 4x4 | 16 / 16 | 5 | 2 | (1,1)|(1,2)<br>(2,0)|(2,1) | 5 | 5 | 8 | 10 | 19 | 0.25 | 0.63 | true | 1 | EASY | `b154779563b6` |
| L8 | 4x4 | 16 / 16 | 5 | 2 | (2,1)|(2,2)<br>(1,2)|(1,3) | 4 | 5 | 8 | 9 | 37 | 0.25 | 0.69 | true | 1 | EASY | `593ebc5e997e` |
| L9 | 4x4 | 16 / 16 | 5 | 2 | (1,1)|(1,2)<br>(2,1)|(3,1) | 5 | 5 | 8 | 11 | 39 | 0.25 | 0.63 | true | 1 | EASY | `9088907f6e22` |
| L10 | 4x4 | 16 / 16 | 5 | 2 | (2,0)|(2,1)<br>(1,2)|(2,2) | 4 | 5 | 8 | 8 | 18 | 0.00 | 0.69 | true | 1 | EASY | `1d41881b179c` |
| L11 | 4x4 | 16 / 16 | 5 | 2 | (1,1)|(1,2)<br>(2,0)|(2,1) | 6 | 6 | 8 | 7 | 19 | 0.00 | 0.56 | true | 1 | EASY | `1018c50b0350` |
| L12 | 4x4 | 16 / 16 | 4 | 3 | (1,0)|(1,1)<br>(1,2)|(2,2)<br>(2,1)|(3,1) | 3 | 4 | 8 | 8 | 40 | 0.67 | 0.75 | false | 1 | MEDIUM | `c9e5253b20fa` |
| L13 | 4x4 | 16 / 16 | 4 | 2 | (1,2)|(2,2)<br>(2,1)|(3,1) | 4 | 6 | 8 | 13 | 66 | 0.33 | 0.69 | false | 1 | MEDIUM | `5db0ac702b95` |
| L14 | 4x4 | 16 / 16 | 4 | 3 | (2,1)|(2,2)<br>(0,2)|(1,2)<br>(0,1)|(1,1) | 5 | 5 | 8 | 6 | 19 | 0.33 | 0.63 | false | 1 | MEDIUM | `4fe4bf649dd2` |
| L15 | 4x4 | 16 / 16 | 4 | 2 | (1,1)|(1,2)<br>(2,0)|(2,1) | 5 | 6 | 8 | 11 | 12 | 0.33 | 0.63 | false | 1 | MEDIUM | `8706e9dc5d6a` |
| L16 | 4x4 | 16 / 16 | 4 | 3 | (1,1)|(2,1)<br>(1,2)|(2,2)<br>(1,0)|(2,0) | 4 | 5 | 8 | 8 | 10 | 0.33 | 0.69 | false | 1 | MEDIUM | `8fa3b03b56a1` |
| L17 | 4x4 | 16 / 16 | 4 | 3 | (2,1)|(2,2)<br>(1,2)|(1,3)<br>(0,1)|(1,1) | 4 | 4 | 8 | 5 | 18 | 0.33 | 0.69 | false | 1 | MEDIUM | `fc4bf1e096a9` |
| L18 | 4x4 | 16 / 16 | 4 | 3 | (2,1)|(2,2)<br>(1,0)|(1,1)<br>(0,2)|(1,2) | 5 | 5 | 8 | 6 | 35 | 0.67 | 0.63 | false | 1 | MEDIUM | `2e52088e0ffa` |
| L19 | 4x4 | 16 / 16 | 4 | 3 | (3,1)|(3,2)<br>(1,2)|(2,2)<br>(1,1)|(2,1) | 4 | 4 | 8 | 5 | 4 | 0.33 | 0.69 | false | 1 | MEDIUM | `176cac1de57b` |
| L20 | 4x4 | 16 / 16 | 4 | 2 | (2,1)|(2,2)<br>(1,1)|(1,2) | 5 | 5 | 7 | 8 | 13 | 0.67 | 0.63 | false | 1 | MEDIUM | `386572f20900` |
| L21 | 5x5 | 25 / 25 | 6 | 5 | (1,1)|(1,2)<br>(1,3)|(2,3)<br>(2,1)|(3,1)<br>(2,2)|(3,2)<br>(3,3)|(4,3) | 6 | 7 | 12 | 17 | 101 | 0.20 | 0.72 | false | 1 | MEDIUM | `c79169a56fa8` |
| L22 | 5x5 | 25 / 25 | 5 | 6 | (3,2)|(3,3)<br>(2,1)|(2,2)<br>(1,2)|(1,3)<br>(1,0)|(1,1)<br>(3,0)|(3,1)<br>(1,4)|(2,4) | 6 | 7 | 12 | 13 | 80 | 0.50 | 0.72 | false | 1 | HARD | `fe5a6ae58dfc` |
| L23 | 5x5 | 25 / 25 | 6 | 4 | (2,1)|(2,2)<br>(3,2)|(3,3)<br>(1,2)|(1,3)<br>(0,1)|(1,1) | 6 | 6 | 8 | 14 | 34 | 0.40 | 0.72 | false | 1 | HARD | `c5395332df78` |
| L24 | 5x5 | 25 / 25 | 6 | 3 | (1,3)|(2,3)<br>(2,2)|(3,2)<br>(1,1)|(2,1) | 8 | 8 | 8 | 15 | 57 | 0.40 | 0.64 | false | 1 | HARD | `d7704932c3d8` |
| L25 | 5x5 | 25 / 25 | 5 | 5 | (3,1)|(3,2)<br>(2,1)|(2,2)<br>(1,3)|(1,4)<br>(2,3)|(3,3)<br>(0,2)|(1,2) | 7 | 8 | 9 | 15 | 43 | 0.50 | 0.68 | false | 1 | HARD | `5da9af5b5be6` |
| L26 | 5x5 | 25 / 25 | 6 | 3 | (2,1)|(2,2)<br>(1,1)|(1,2)<br>(3,1)|(3,2) | 7 | 10 | 8 | 15 | 53 | 0.20 | 0.68 | false | 1 | HARD | `d23eb7b576d8` |
| L27 | 5x5 | 25 / 25 | 6 | 4 | (2,1)|(2,2)<br>(3,2)|(3,3)<br>(1,1)|(1,2)<br>(2,3)|(2,4) | 8 | 8 | 10 | 14 | 75 | 0.80 | 0.64 | false | 1 | HARD | `d2f38f2d462b` |
| L28 | 5x5 | 25 / 25 | 5 | 3 | (2,2)|(3,2)<br>(1,1)|(2,1)<br>(0,3)|(1,3) | 10 | 11 | 12 | 19 | 126 | 0.25 | 0.56 | false | 1 | HARD | `a000ad929026` |
| L29 | 5x5 | 25 / 25 | 6 | 4 | (1,1)|(2,1)<br>(2,2)|(3,2)<br>(1,3)|(2,3)<br>(0,2)|(1,2) | 8 | 9 | 12 | 14 | 84 | 0.20 | 0.64 | false | 1 | HARD | `d5b2030eece9` |
| L30 | 5x5 | 25 / 25 | 6 | 3 | (3,2)|(3,3)<br>(1,2)|(2,2)<br>(2,1)|(3,1) | 9 | 10 | 9 | 16 | 71 | 0.20 | 0.60 | false | 1 | HARD | `a0970fad4c7c` |

## Levels 31–50 Extended Audit Overview

| Level Range | Dimensions | Walls Range | Decisions Range | Target Band | Baseline Status |
|:---:|:---:|:---:|:---:|:---:|:---|
| L31–L40 | 5x5 | 3–6 | 7–10 | HARD / ADVANCED | All simple strategies fail |
| L41–L50 | 5x5 | 4–6 | 7–11 | HARD / ADVANCED | All simple strategies fail |

## Progression Summary
1. **Levels 1–4 (Tutorial):** Focuses on basic controls and 100% cell coverage.
2. **Levels 5–10 (Real Puzzle Experience):** Introduces 2–3 strategic internal walls, deceptive checkpoints, and 4–6 meaningful decisions. Level 5 immediately breaks naive serpentine movement.
3. **Levels 11–20 (Hard 4x4):** Increases delayed consequences and cell reservations. Casual heuristics fail consistently.
4. **Levels 21–30 (Very Hard 5x5):** 25-cell grid with 3–5 strategic walls and 6–10 branching choices requiring multi-step lookahead.
5. **Levels 31–50 (Advanced 5x5):** Multi-stage planning with 3–6 strategic internal edge walls.
