# ZYNPATH WORLD 2→6 DEEP THINKING DIFFICULTY AUDIT

> **Audit Timestamp:** 2026-10-06  
> **Scope:** Complete 300-Level Campaign Audit (Worlds 1–6)  
> **World 1 Policy:** ABSOLUTELY FROZEN (100% byte & semantic identical to baseline)  
> **World 2 Policy:** Serious Strategy (planning N+1, N+2, N+3 ahead, visual proximity traps)  
> **World 3–6 Policy:** Deep Human Reasoning (cell reservation, delayed failure, competing future requirements, global planning)  
> **Multiplayer Policy:** Quick Duel & Friends Arena strictly 11×11 and 12×12 only (≤10×10 count = 0)  
> **Board Invariants:** 100% Wall placements, grid dimensions, cell sets, and Hamiltonian canonical solutions PRESERVED.  

## 1. Executive Summary & World 1 Frozen Proof

World 1 is strictly frozen and completely preserved from the authoritative baseline. No checkpoint positions, numbers, walls, or version configurations were modified.

| Level | File Path | File Size | Baseline SHA-256 Hash | Verification Status |
|:---:|:---|:---:|:---|:---:|
| 1 | `puzzles/w1/lvl1.json` | 1930 B | `d33a25d57f7a0b5f86df0ed426907e4167471b494ccd5391b96857eef3800c49` | **FROZEN / VERIFIED** |
| 2 | `puzzles/w1/lvl2.json` | 1888 B | `93ab292a55f15fc8022c6cfa2b567f0e349201c5c5b4ac881be57576edd6b5b2` | **FROZEN / VERIFIED** |
| 3 | `puzzles/w1/lvl3.json` | 1946 B | `973d3e7a6c53842276a7fa03b19eb65b269ac5cd2f297730a6568ad7581d57e6` | **FROZEN / VERIFIED** |
| 4 | `puzzles/w1/lvl4.json` | 1962 B | `dad531a2a54a564c7ddf30d593ce312e7ef552140dee889826c0432bdcab45de` | **FROZEN / VERIFIED** |
| 5 | `puzzles/w1/lvl5.json` | 2156 B | `a0b53ef85a175a96687392e33e20d9b0012fd1bb95b21357db479be8bb6f6122` | **FROZEN / VERIFIED** |
| 6 | `puzzles/w1/lvl6.json` | 2071 B | `24a1dd50f9ad58801ee9f217284a247366f0014c673f262251a732ce589a8fc6` | **FROZEN / VERIFIED** |
| 7 | `puzzles/w1/lvl7.json` | 2156 B | `568f96f21a8b5cddcb92a99fe002167b413a2a5c3a538a3c052155ff25d32f11` | **FROZEN / VERIFIED** |
| 8 | `puzzles/w1/lvl8.json` | 2071 B | `932fb4f5c0521319f2e46bfa40cfca2de9b4eb0d13bf220ad4bf80939de9e302` | **FROZEN / VERIFIED** |
| 9 | `puzzles/w1/lvl9.json` | 2071 B | `8ea404d7110842a93770a4621700feca11d0f56266f510288132c4919fce4736` | **FROZEN / VERIFIED** |
| 10 | `puzzles/w1/lvl10.json` | 2157 B | `07d35be3c93722b05ccc6cb87f1331c8a42c0ac73f114e4505bb22db84ac5f48` | **FROZEN / VERIFIED** |
| 11 | `puzzles/w1/lvl11.json` | 2114 B | `caaf5732959a61c1bed5de1848c51683b1698b5617868b50c34b4ca0e171ae23` | **FROZEN / VERIFIED** |
| 12 | `puzzles/w1/lvl12.json` | 2072 B | `78f7584f375046ceb42dfbc625f4cd70fd79a729b243fff8b89b0b67c4363ce9` | **FROZEN / VERIFIED** |
| 13 | `puzzles/w1/lvl13.json` | 2114 B | `10d114c2f753fe814647f04998e6e8e94545c513bdd9b1c2f2bb67ad8e4c5ae2` | **FROZEN / VERIFIED** |
| 14 | `puzzles/w1/lvl14.json` | 1988 B | `c0a22bb7b838caa69a8286944cb3c3ca940f97fadd0a9262c66d441f744b8801` | **FROZEN / VERIFIED** |
| 15 | `puzzles/w1/lvl15.json` | 2157 B | `4b8491dc96526b397d003293b08740faba165c729c9356adee86347e27bad213` | **FROZEN / VERIFIED** |
| 16 | `puzzles/w1/lvl16.json` | 2030 B | `38da684bdd7a68ed8bea338e801aad835708f065298099acbd9a7c0ee010d157` | **FROZEN / VERIFIED** |
| 17 | `puzzles/w1/lvl17.json` | 2072 B | `4169558d94bb1c1c2204b2a66de1c981b40e2da429069124334a0fb4ce844a05` | **FROZEN / VERIFIED** |
| 18 | `puzzles/w1/lvl18.json` | 2072 B | `6cb0f7bff20afd52c82ccb4be5edb28dcbbf66b0a97d70d451a8bd8ef51baf17` | **FROZEN / VERIFIED** |
| 19 | `puzzles/w1/lvl19.json` | 2072 B | `2d61b51c88842fe1ac28df4777fcd4c1467be0ab83e06c52f54d4b393b7324d0` | **FROZEN / VERIFIED** |
| 20 | `puzzles/w1/lvl20.json` | 2114 B | `15a5829e5af27104c4affa9272df866aaf0bb4e2be7028053cde294802091e9b` | **FROZEN / VERIFIED** |

## 2. World Summary & Difficulty Progression

| World | Name | Level Range | Grid Size | Target Thinking Depth | Achieved Thinking Depth (Min / Avg / Max) | Target Difficulty | Achieved Difficulty (Min / Avg / Max) | Tier Distribution | Anti-Greedy Rate | Solver Uniqueness |
|:---:|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---|:---:|:---:|
| **1** | The Genesis Grid | 1–20 | 6×6 | 1..2 (Tutorial) | **4 / 4.6 / 5** | Beginner → Easy (<40) | **14.6 / 30.9 / 37.1** | BEGINNER: 4, EASY: 16 | **90.0% (18/20)** | **100% UNIQUE** |
| **2** | The Forking Labyrinth | 21–50 | 7×7 | 2..3 (N+1..N+3) | **4 / 4.6 / 5** | Medium → Hard → Very Hard | **58.9 / 69.0 / 79.7** | MEDIUM: 3, HARD: 23, VERY_HARD: 4 | **86.7% (26/30)** | **100% UNIQUE** |
| **3** | The Walled Domain | 51–100 | 8×8, 9×9 | 3..4 (2-3 Look-ahead) | **4 / 4.7 / 5** | Hard baseline → Very Hard | **68.3 / 73.9 / 80.4** | HARD: 30, VERY_HARD: 20 | **100.0% (50/50)** | **100% UNIQUE** |
| **4** | The Quantum Lattice | 101–150 | 9×9, 10×10 | 3..4 (3-4 Look-ahead) | **4 / 4.7 / 5** | Very Hard baseline, sel. Expert | **81.9 / 86.8 / 91.3** | VERY_HARD: 37, EXPERT: 13 | **100.0% (50/50)** | **100% UNIQUE** |
| **5** | The Paradox Chamber | 151–200 | 10×10, 11×11 | 4..5 (4+ Look-ahead) | **4 / 4.8 / 5** | Expert baseline, sel. Extreme | **93.4 / 95.6 / 98.6** | EXPERT: 7, EXTREME: 43 | **100.0% (50/50)** | **100% UNIQUE** |
| **6** | The Infinity Expanse | 201–300 | 11×11, 12×12 | 4..5 (Deep Look-ahead) | **4 / 4.9 / 5** | Expert min, Extreme / Extreme+ | **93.1 / 96.8 / 100.0** | EXPERT: 13, EXTREME: 87 | **100.0% (100/100)** | **100% UNIQUE** |

## 3. Level 300 Grandmaster Showcase

- **Level ID:** 300 (Campaign Grandmaster Finale)
- **Grid:** 12×12 (144 playable cells)
- **Checkpoints:** 29
- **Composite Score:** **100.0 / 100.0** (Difficulty Tier: **EXTREME**)
- **Required Thinking Depth:** **5 / 5** (Global Multi-Region Look-Ahead)
- **Delayed Mistake Depth:** **7.0 steps**
- **Future Checkpoint Pressure:** **2577.0 / 100**
- **Meaningful Route Ambiguity:** **63.5 / 100**
- **Anti-Greedy Resistance:** **RESISTANT_TO_ALL_GREEDY**
- **Solver Certification:** Player-visible solution proven **UNIQUE**, 100% full-board coverage.

## 4. Competitive Multiplayer Pools Audit

| Feature | Specification | Audited Status | Small Grids (≤10×10) |
|:---|:---|:---:|:---:|
| **Quick Duel Pool** | Strictly 11×11 and 12×12 Expert/Extreme puzzles | **VERIFIED (8 puzzles: 4× 11×11, 4× 12×12)** | **0 ELIGIBLE (STRICTLY PROHIBITED)** |
| **Friends Arena Pool** | Strictly 11×11 and 12×12 Expert/Extreme puzzles | **VERIFIED (8 puzzles: 4× 11×11, 4× 12×12)** | **0 ELIGIBLE (STRICTLY PROHIBITED)** |
| **Fairness Enforcement** | Identical puzzle ID, version, walls, and start state | **VERIFIED (Deterministic canonical matching)** | N/A |

## 5. Complete Level-by-Level Audit Table (Levels 1–300)

| Lvl | W | Grid | CPs | Thinking Depth | Delayed Mistake (Steps) | Future Pressure | Route Ambiguity | Anti-Greedy Result | Uniqueness | Coverage | Tier | Score |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|:---:|:---:|:---:|:---:|
| 1 | 1 | 6×6 | 10 | 5 | 6.0 | 273.0 | 65.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | BEGINNER | 14.6 |
| 2 | 1 | 6×6 | 9 | 4 | 5.3 | 159.5 | 71.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | BEGINNER | 18.1 |
| 3 | 1 | 6×6 | 10 | 5 | 6.1 | 302.0 | 75.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | BEGINNER | 24.0 |
| 4 | 1 | 6×6 | 9 | 4 | 3.3 | 72.5 | 53.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | BEGINNER | 23.0 |
| 5 | 1 | 6×6 | 10 | 4 | 4.8 | 139.5 | 60.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 30.8 |
| 6 | 1 | 6×6 | 8 | 4 | 3.4 | 85.0 | 54.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 29.2 |
| 7 | 1 | 6×6 | 10 | 4 | 3.1 | 60.0 | 46.3 | DEFEATED_BY_ROW_SNAKE | UNIQUE | 100% | EASY | 29.3 |
| 8 | 1 | 6×6 | 8 | 4 | 3.8 | 95.5 | 55.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 29.9 |
| 9 | 1 | 6×6 | 8 | 5 | 4.8 | 109.0 | 71.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 33.7 |
| 10 | 1 | 6×6 | 10 | 5 | 6.6 | 250.5 | 67.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 34.9 |
| 11 | 1 | 6×6 | 9 | 5 | 3.6 | 91.5 | 49.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 32.2 |
| 12 | 1 | 6×6 | 8 | 5 | 4.8 | 149.0 | 72.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 34.4 |
| 13 | 1 | 6×6 | 9 | 5 | 7.3 | 187.5 | 63.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 34.3 |
| 14 | 1 | 6×6 | 6 | 4 | 6.4 | 241.0 | 76.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 36.6 |
| 15 | 1 | 6×6 | 10 | 5 | 6.4 | 228.5 | 72.7 | DEFEATED_BY_ROW_SNAKE | UNIQUE | 100% | EASY | 35.4 |
| 16 | 1 | 6×6 | 7 | 4 | 5.7 | 164.5 | 78.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 36.2 |
| 17 | 1 | 6×6 | 8 | 5 | 6.9 | 275.0 | 76.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 37.1 |
| 18 | 1 | 6×6 | 8 | 5 | 4.7 | 153.0 | 72.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 35.5 |
| 19 | 1 | 6×6 | 8 | 4 | 6.7 | 147.5 | 62.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 33.8 |
| 20 | 1 | 6×6 | 9 | 5 | 5.3 | 170.0 | 66.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EASY | 34.5 |
| 21 | 2 | 7×7 | 11 | 4 | 4.9 | 262.5 | 57.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | MEDIUM | 58.9 |
| 22 | 2 | 7×7 | 12 | 4 | 4.4 | 251.0 | 59.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | MEDIUM | 59.6 |
| 23 | 2 | 7×7 | 12 | 4 | 5.5 | 162.0 | 54.6 | DEFEATED_BY_PERIMETER_FIRST | UNIQUE | 100% | MEDIUM | 59.1 |
| 24 | 2 | 7×7 | 10 | 4 | 5.5 | 305.0 | 59.6 | DEFEATED_BY_PERIMETER_FIRST | UNIQUE | 100% | HARD | 61.0 |
| 25 | 2 | 7×7 | 12 | 4 | 7.9 | 600.5 | 72.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 65.1 |
| 26 | 2 | 7×7 | 9 | 4 | 4.3 | 262.0 | 62.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 63.2 |
| 27 | 2 | 7×7 | 11 | 4 | 3.5 | 214.5 | 56.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 61.8 |
| 28 | 2 | 7×7 | 12 | 5 | 4.9 | 225.5 | 58.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 63.9 |
| 29 | 2 | 7×7 | 12 | 5 | 10.3 | 617.0 | 65.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 68.0 |
| 30 | 2 | 7×7 | 12 | 5 | 5.5 | 252.5 | 64.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 64.7 |
| 31 | 2 | 7×7 | 10 | 4 | 4.3 | 181.5 | 55.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 62.9 |
| 32 | 2 | 7×7 | 9 | 4 | 4.4 | 227.5 | 67.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 66.1 |
| 33 | 2 | 7×7 | 12 | 5 | 5.7 | 356.0 | 60.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 66.7 |
| 34 | 2 | 7×7 | 12 | 4 | 5.2 | 354.0 | 58.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 66.6 |
| 35 | 2 | 7×7 | 11 | 5 | 9.2 | 927.5 | 76.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.7 |
| 36 | 2 | 7×7 | 8 | 5 | 6.5 | 381.5 | 73.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 70.0 |
| 37 | 2 | 7×7 | 12 | 5 | 6.5 | 386.0 | 70.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 70.8 |
| 38 | 2 | 7×7 | 8 | 4 | 8.7 | 236.0 | 75.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 69.9 |
| 39 | 2 | 7×7 | 12 | 5 | 6.8 | 475.5 | 67.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.3 |
| 40 | 2 | 7×7 | 10 | 5 | 9.0 | 646.5 | 78.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.8 |
| 41 | 2 | 7×7 | 11 | 5 | 9.9 | 847.5 | 73.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 73.9 |
| 42 | 2 | 7×7 | 12 | 5 | 8.0 | 587.0 | 74.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 75.1 |
| 43 | 2 | 7×7 | 10 | 5 | 7.3 | 506.5 | 74.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.9 |
| 44 | 2 | 7×7 | 11 | 5 | 6.1 | 306.5 | 56.9 | DEFEATED_BY_COLUMN_SNAKE | UNIQUE | 100% | HARD | 71.9 |
| 45 | 2 | 7×7 | 12 | 4 | 4.9 | 369.0 | 63.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.4 |
| 46 | 2 | 7×7 | 9 | 5 | 4.4 | 217.0 | 70.9 | DEFEATED_BY_PERIMETER_FIRST | UNIQUE | 100% | HARD | 74.9 |
| 47 | 2 | 7×7 | 10 | 4 | 5.5 | 404.0 | 64.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 73.6 |
| 48 | 2 | 7×7 | 11 | 5 | 7.3 | 432.5 | 77.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.8 |
| 49 | 2 | 7×7 | 11 | 5 | 8.1 | 620.0 | 80.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 79.7 |
| 50 | 2 | 7×7 | 10 | 5 | 9.3 | 634.5 | 76.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 79.4 |
| 51 | 3 | 8×8 | 15 | 5 | 7.1 | 647.0 | 62.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 69.5 |
| 52 | 3 | 8×8 | 14 | 4 | 4.8 | 387.5 | 60.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 68.9 |
| 53 | 3 | 8×8 | 13 | 4 | 5.5 | 417.5 | 57.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 68.3 |
| 54 | 3 | 8×8 | 15 | 4 | 5.6 | 542.5 | 61.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 69.7 |
| 55 | 3 | 8×8 | 15 | 5 | 7.2 | 610.5 | 62.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 70.1 |
| 56 | 3 | 8×8 | 15 | 5 | 5.8 | 459.5 | 58.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 69.6 |
| 57 | 3 | 8×8 | 14 | 5 | 8.2 | 835.0 | 65.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.2 |
| 58 | 3 | 8×8 | 15 | 5 | 6.5 | 743.0 | 66.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.7 |
| 59 | 3 | 8×8 | 13 | 5 | 7.8 | 756.5 | 64.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.2 |
| 60 | 3 | 8×8 | 11 | 4 | 6.3 | 469.5 | 77.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.3 |
| 61 | 3 | 8×8 | 14 | 4 | 5.4 | 516.0 | 57.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 70.2 |
| 62 | 3 | 8×8 | 14 | 4 | 6.6 | 689.0 | 60.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.0 |
| 63 | 3 | 8×8 | 15 | 5 | 5.7 | 739.0 | 62.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.1 |
| 64 | 3 | 8×8 | 15 | 4 | 6.9 | 661.5 | 65.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.9 |
| 65 | 3 | 8×8 | 15 | 4 | 6.2 | 623.5 | 65.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.3 |
| 66 | 3 | 8×8 | 13 | 4 | 5.1 | 491.5 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.0 |
| 67 | 3 | 8×8 | 15 | 5 | 6.0 | 415.0 | 58.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.6 |
| 68 | 3 | 8×8 | 13 | 5 | 6.0 | 623.5 | 63.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.3 |
| 69 | 3 | 8×8 | 14 | 5 | 5.3 | 415.0 | 56.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 71.4 |
| 70 | 3 | 8×8 | 15 | 5 | 6.5 | 644.5 | 60.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.4 |
| 71 | 3 | 8×8 | 15 | 5 | 6.3 | 627.5 | 63.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 72.8 |
| 72 | 3 | 8×8 | 15 | 5 | 6.6 | 713.5 | 64.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 73.3 |
| 73 | 3 | 8×8 | 13 | 5 | 8.4 | 929.5 | 66.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.0 |
| 74 | 3 | 8×8 | 14 | 5 | 7.5 | 809.0 | 72.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 75.0 |
| 75 | 3 | 8×8 | 14 | 5 | 7.7 | 866.5 | 63.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.2 |
| 76 | 3 | 8×8 | 12 | 5 | 10.6 | 1531.0 | 70.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 75.2 |
| 77 | 3 | 8×8 | 15 | 5 | 5.1 | 742.0 | 56.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 73.9 |
| 78 | 3 | 8×8 | 14 | 4 | 5.1 | 547.5 | 60.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.0 |
| 79 | 3 | 8×8 | 15 | 5 | 6.4 | 664.0 | 66.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 75.1 |
| 80 | 3 | 8×8 | 14 | 5 | 7.1 | 1065.0 | 69.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.7 |
| 81 | 3 | 8×8 | 15 | 5 | 5.8 | 577.0 | 58.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.3 |
| 82 | 3 | 8×8 | 15 | 4 | 5.8 | 596.5 | 61.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.1 |
| 83 | 3 | 8×8 | 11 | 5 | 6.2 | 548.5 | 79.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.6 |
| 84 | 3 | 8×8 | 14 | 5 | 8.9 | 1582.0 | 72.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.5 |
| 85 | 3 | 8×8 | 13 | 5 | 7.4 | 886.5 | 93.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 80.4 |
| 86 | 3 | 8×8 | 15 | 4 | 6.2 | 607.5 | 59.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | HARD | 74.5 |
| 87 | 3 | 8×8 | 15 | 5 | 7.8 | 901.0 | 67.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.6 |
| 88 | 3 | 8×8 | 14 | 5 | 8.3 | 876.0 | 81.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 78.7 |
| 89 | 3 | 8×8 | 14 | 5 | 7.6 | 832.5 | 63.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.2 |
| 90 | 3 | 8×8 | 15 | 5 | 7.1 | 886.0 | 62.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.3 |
| 91 | 3 | 9×9 | 17 | 5 | 7.5 | 911.5 | 60.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 75.5 |
| 92 | 3 | 9×9 | 18 | 5 | 5.2 | 687.0 | 54.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 75.7 |
| 93 | 3 | 9×9 | 18 | 5 | 8.5 | 1252.0 | 63.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.4 |
| 94 | 3 | 9×9 | 18 | 5 | 7.2 | 1032.5 | 62.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.2 |
| 95 | 3 | 9×9 | 18 | 5 | 8.5 | 1513.5 | 63.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 78.1 |
| 96 | 3 | 9×9 | 17 | 4 | 7.7 | 871.0 | 65.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.1 |
| 97 | 3 | 9×9 | 18 | 4 | 6.0 | 795.5 | 60.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.4 |
| 98 | 3 | 9×9 | 17 | 5 | 5.5 | 735.5 | 55.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.5 |
| 99 | 3 | 9×9 | 18 | 5 | 9.3 | 1049.0 | 65.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 77.6 |
| 100 | 3 | 9×9 | 17 | 5 | 5.6 | 686.5 | 55.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 76.9 |
| 101 | 4 | 9×9 | 16 | 5 | 5.3 | 550.5 | 54.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 81.9 |
| 102 | 4 | 9×9 | 18 | 4 | 7.3 | 916.5 | 60.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 82.8 |
| 103 | 4 | 9×9 | 17 | 4 | 6.3 | 989.0 | 60.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 83.0 |
| 104 | 4 | 9×9 | 16 | 5 | 7.8 | 1315.5 | 61.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 83.8 |
| 105 | 4 | 9×9 | 18 | 4 | 6.9 | 1314.0 | 65.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 84.3 |
| 106 | 4 | 9×9 | 18 | 5 | 7.7 | 1152.0 | 64.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 84.1 |
| 107 | 4 | 9×9 | 18 | 4 | 5.8 | 819.0 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 83.2 |
| 108 | 4 | 9×9 | 18 | 5 | 7.9 | 931.5 | 65.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 84.4 |
| 109 | 4 | 9×9 | 17 | 4 | 8.7 | 1439.5 | 68.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.5 |
| 110 | 4 | 9×9 | 18 | 5 | 7.6 | 1429.0 | 64.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.6 |
| 111 | 4 | 9×9 | 16 | 4 | 5.9 | 727.0 | 67.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 84.9 |
| 112 | 4 | 9×9 | 16 | 4 | 5.2 | 610.0 | 62.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 84.3 |
| 113 | 4 | 9×9 | 18 | 5 | 7.7 | 1360.5 | 64.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.9 |
| 114 | 4 | 9×9 | 16 | 5 | 6.4 | 736.5 | 69.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.3 |
| 115 | 4 | 9×9 | 18 | 5 | 8.3 | 1299.0 | 67.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.6 |
| 116 | 4 | 9×9 | 16 | 5 | 7.0 | 761.0 | 65.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.2 |
| 117 | 4 | 9×9 | 16 | 4 | 6.6 | 868.5 | 64.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 85.2 |
| 118 | 4 | 9×9 | 18 | 5 | 7.0 | 1073.5 | 70.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.9 |
| 119 | 4 | 9×9 | 17 | 5 | 6.7 | 1171.0 | 70.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.0 |
| 120 | 4 | 9×9 | 18 | 5 | 7.8 | 1285.5 | 61.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.5 |
| 121 | 4 | 9×9 | 17 | 5 | 7.7 | 1287.0 | 61.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.2 |
| 122 | 4 | 9×9 | 18 | 5 | 7.3 | 1044.5 | 63.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.0 |
| 123 | 4 | 9×9 | 17 | 5 | 7.6 | 900.5 | 71.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.2 |
| 124 | 4 | 9×9 | 18 | 5 | 8.3 | 1293.5 | 66.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.5 |
| 125 | 4 | 9×9 | 18 | 5 | 8.9 | 1456.5 | 64.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.4 |
| 126 | 4 | 9×9 | 18 | 5 | 6.8 | 1322.5 | 59.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.1 |
| 127 | 4 | 9×9 | 17 | 5 | 6.3 | 953.5 | 57.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.6 |
| 128 | 4 | 9×9 | 17 | 5 | 5.1 | 672.0 | 54.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.0 |
| 129 | 4 | 9×9 | 18 | 5 | 6.2 | 1104.5 | 57.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.2 |
| 130 | 4 | 9×9 | 17 | 5 | 10.3 | 2123.0 | 73.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 88.8 |
| 131 | 4 | 10×10 | 20 | 4 | 5.6 | 996.5 | 58.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.5 |
| 132 | 4 | 10×10 | 20 | 5 | 7.6 | 1252.5 | 59.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.1 |
| 133 | 4 | 10×10 | 20 | 4 | 6.8 | 1324.0 | 56.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 86.7 |
| 134 | 4 | 10×10 | 20 | 5 | 7.3 | 1548.0 | 58.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.6 |
| 135 | 4 | 10×10 | 20 | 4 | 7.8 | 1375.0 | 63.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.8 |
| 136 | 4 | 10×10 | 20 | 5 | 5.7 | 878.5 | 54.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.3 |
| 137 | 4 | 10×10 | 20 | 4 | 6.9 | 1167.0 | 57.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.3 |
| 138 | 4 | 10×10 | 20 | 5 | 8.1 | 1487.0 | 63.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 88.5 |
| 139 | 4 | 10×10 | 20 | 5 | 8.2 | 1478.5 | 64.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 88.9 |
| 140 | 4 | 10×10 | 19 | 5 | 8.0 | 1659.5 | 60.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 88.9 |
| 141 | 4 | 10×10 | 20 | 5 | 7.0 | 1037.5 | 65.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 88.9 |
| 142 | 4 | 10×10 | 20 | 4 | 6.5 | 1164.5 | 56.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | VERY_HARD | 87.9 |
| 143 | 4 | 10×10 | 20 | 5 | 9.8 | 2052.0 | 64.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 89.7 |
| 144 | 4 | 10×10 | 20 | 5 | 9.7 | 2619.0 | 64.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 89.8 |
| 145 | 4 | 10×10 | 20 | 5 | 7.9 | 2168.0 | 68.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 90.9 |
| 146 | 4 | 10×10 | 20 | 5 | 7.2 | 1635.0 | 58.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 89.6 |
| 147 | 4 | 10×10 | 20 | 5 | 9.2 | 2649.0 | 62.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 90.8 |
| 148 | 4 | 10×10 | 20 | 5 | 7.1 | 1590.0 | 62.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 90.3 |
| 149 | 4 | 10×10 | 20 | 5 | 8.9 | 2871.0 | 65.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 91.3 |
| 150 | 4 | 10×10 | 20 | 5 | 5.8 | 923.0 | 59.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 89.6 |
| 151 | 5 | 10×10 | 18 | 5 | 8.9 | 1613.5 | 62.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.6 |
| 152 | 5 | 10×10 | 15 | 5 | 9.6 | 2018.0 | 64.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.6 |
| 153 | 5 | 10×10 | 20 | 5 | 8.7 | 1975.5 | 65.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.3 |
| 154 | 5 | 10×10 | 19 | 5 | 6.1 | 920.5 | 66.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 155 | 5 | 10×10 | 19 | 5 | 6.8 | 1168.5 | 61.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.5 |
| 156 | 5 | 10×10 | 15 | 5 | 8.5 | 1237.5 | 68.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.1 |
| 157 | 5 | 10×10 | 19 | 4 | 4.9 | 944.0 | 57.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.6 |
| 158 | 5 | 10×10 | 18 | 5 | 6.8 | 1584.0 | 62.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.6 |
| 159 | 5 | 10×10 | 16 | 5 | 7.8 | 1955.5 | 63.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.8 |
| 160 | 5 | 10×10 | 18 | 4 | 5.6 | 762.0 | 68.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.3 |
| 161 | 5 | 10×10 | 19 | 4 | 6.4 | 918.5 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.4 |
| 162 | 5 | 10×10 | 18 | 4 | 6.0 | 1147.0 | 60.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.9 |
| 163 | 5 | 10×10 | 19 | 5 | 8.3 | 1609.0 | 60.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.4 |
| 164 | 5 | 10×10 | 18 | 5 | 8.4 | 2140.0 | 65.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.5 |
| 165 | 5 | 10×10 | 17 | 5 | 11.4 | 1912.5 | 68.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.1 |
| 166 | 5 | 10×10 | 17 | 4 | 5.6 | 1124.5 | 60.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.4 |
| 167 | 5 | 10×10 | 17 | 4 | 5.9 | 842.0 | 60.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.2 |
| 168 | 5 | 10×10 | 20 | 5 | 7.1 | 1366.0 | 57.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.9 |
| 169 | 5 | 10×10 | 16 | 5 | 6.9 | 1076.0 | 72.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 170 | 5 | 10×10 | 19 | 5 | 6.4 | 1161.0 | 63.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.2 |
| 171 | 5 | 10×10 | 17 | 5 | 6.7 | 1049.5 | 68.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.4 |
| 172 | 5 | 10×10 | 17 | 5 | 6.8 | 1719.0 | 56.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.0 |
| 173 | 5 | 10×10 | 17 | 5 | 8.1 | 1628.5 | 65.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.5 |
| 174 | 5 | 10×10 | 17 | 5 | 6.5 | 835.5 | 60.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.9 |
| 175 | 5 | 10×10 | 17 | 5 | 8.4 | 2245.0 | 64.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.3 |
| 176 | 5 | 10×10 | 20 | 5 | 6.7 | 1266.0 | 60.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.3 |
| 177 | 5 | 10×10 | 16 | 5 | 8.1 | 1327.0 | 68.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 178 | 5 | 10×10 | 18 | 5 | 8.0 | 1422.0 | 59.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.5 |
| 179 | 5 | 10×10 | 20 | 5 | 8.3 | 1725.0 | 65.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 180 | 5 | 10×10 | 19 | 5 | 6.2 | 1261.5 | 63.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 181 | 5 | 11×11 | 23 | 5 | 6.5 | 1899.0 | 60.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.3 |
| 182 | 5 | 11×11 | 22 | 5 | 5.8 | 1255.5 | 55.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.3 |
| 183 | 5 | 11×11 | 22 | 5 | 5.9 | 1067.5 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.2 |
| 184 | 5 | 11×11 | 24 | 5 | 7.6 | 2006.5 | 62.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.5 |
| 185 | 5 | 11×11 | 22 | 5 | 9.7 | 2658.0 | 64.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.8 |
| 186 | 5 | 11×11 | 24 | 5 | 6.9 | 2071.0 | 57.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.3 |
| 187 | 5 | 11×11 | 22 | 4 | 6.5 | 1387.5 | 66.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.6 |
| 188 | 5 | 11×11 | 23 | 5 | 7.8 | 2491.5 | 61.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.5 |
| 189 | 5 | 11×11 | 24 | 5 | 7.5 | 2888.0 | 57.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.1 |
| 190 | 5 | 11×11 | 22 | 5 | 8.2 | 1857.0 | 63.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.7 |
| 191 | 5 | 11×11 | 24 | 5 | 8.3 | 2441.5 | 58.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.8 |
| 192 | 5 | 11×11 | 24 | 5 | 6.4 | 1536.0 | 59.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.5 |
| 193 | 5 | 11×11 | 24 | 5 | 8.0 | 2271.5 | 62.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.2 |
| 194 | 5 | 11×11 | 22 | 5 | 8.0 | 2792.5 | 60.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.4 |
| 195 | 5 | 11×11 | 24 | 5 | 6.4 | 1539.5 | 61.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.8 |
| 196 | 5 | 11×11 | 22 | 4 | 5.6 | 1363.0 | 67.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.5 |
| 197 | 5 | 11×11 | 22 | 5 | 8.0 | 2163.0 | 58.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.1 |
| 198 | 5 | 11×11 | 24 | 5 | 11.6 | 3594.5 | 71.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.6 |
| 199 | 5 | 11×11 | 24 | 5 | 6.8 | 1827.5 | 60.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.4 |
| 200 | 5 | 11×11 | 24 | 5 | 7.5 | 2112.5 | 68.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 201 | 6 | 11×11 | 24 | 5 | 6.8 | 2298.0 | 58.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.4 |
| 202 | 6 | 11×11 | 21 | 5 | 7.8 | 2809.0 | 57.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 203 | 6 | 11×11 | 21 | 5 | 8.0 | 1711.5 | 63.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.5 |
| 204 | 6 | 11×11 | 20 | 5 | 8.3 | 1619.5 | 70.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 94.0 |
| 205 | 6 | 11×11 | 24 | 5 | 7.2 | 1915.0 | 61.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 206 | 6 | 11×11 | 21 | 5 | 6.6 | 1395.0 | 64.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 207 | 6 | 11×11 | 21 | 5 | 6.0 | 1131.5 | 53.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.1 |
| 208 | 6 | 11×11 | 24 | 5 | 7.2 | 1963.5 | 62.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.9 |
| 209 | 6 | 11×11 | 24 | 5 | 6.9 | 1305.5 | 60.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 210 | 6 | 11×11 | 22 | 5 | 7.0 | 1843.0 | 58.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.1 |
| 211 | 6 | 11×11 | 21 | 5 | 6.8 | 1695.0 | 59.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.9 |
| 212 | 6 | 11×11 | 23 | 5 | 6.7 | 1703.5 | 57.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 213 | 6 | 11×11 | 24 | 5 | 6.4 | 1854.0 | 58.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 94.0 |
| 214 | 6 | 11×11 | 18 | 5 | 7.3 | 1525.0 | 59.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXPERT | 93.8 |
| 215 | 6 | 11×11 | 22 | 5 | 10.3 | 3146.0 | 66.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.2 |
| 216 | 6 | 11×11 | 24 | 5 | 6.4 | 1678.0 | 57.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.4 |
| 217 | 6 | 11×11 | 22 | 5 | 9.4 | 1750.5 | 64.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.5 |
| 218 | 6 | 11×11 | 23 | 5 | 8.1 | 2223.5 | 68.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.1 |
| 219 | 6 | 11×11 | 22 | 5 | 9.2 | 2165.5 | 69.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.3 |
| 220 | 6 | 11×11 | 21 | 5 | 6.6 | 1282.0 | 59.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.4 |
| 221 | 6 | 11×11 | 23 | 5 | 8.0 | 2140.0 | 60.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.9 |
| 222 | 6 | 11×11 | 23 | 4 | 5.9 | 1948.0 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.8 |
| 223 | 6 | 11×11 | 22 | 5 | 6.7 | 1198.0 | 61.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.6 |
| 224 | 6 | 11×11 | 22 | 5 | 9.6 | 3712.5 | 70.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 225 | 6 | 11×11 | 19 | 5 | 9.1 | 2568.5 | 69.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 226 | 6 | 11×11 | 24 | 5 | 6.6 | 1773.0 | 65.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.3 |
| 227 | 6 | 11×11 | 20 | 4 | 6.3 | 1061.0 | 57.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.5 |
| 228 | 6 | 11×11 | 24 | 5 | 5.3 | 1167.5 | 54.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 94.6 |
| 229 | 6 | 11×11 | 24 | 5 | 10.2 | 3131.0 | 65.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 230 | 6 | 11×11 | 24 | 5 | 7.5 | 1701.5 | 57.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.0 |
| 231 | 6 | 11×11 | 23 | 5 | 7.0 | 1536.5 | 58.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.1 |
| 232 | 6 | 11×11 | 23 | 5 | 7.2 | 1685.0 | 64.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 233 | 6 | 11×11 | 24 | 5 | 9.1 | 2096.5 | 60.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 234 | 6 | 11×11 | 19 | 5 | 7.8 | 2324.0 | 57.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 235 | 6 | 11×11 | 22 | 5 | 6.7 | 1300.5 | 62.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.6 |
| 236 | 6 | 11×11 | 22 | 5 | 5.9 | 1703.0 | 62.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.9 |
| 237 | 6 | 11×11 | 24 | 5 | 7.4 | 1878.0 | 58.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.8 |
| 238 | 6 | 11×11 | 22 | 5 | 6.3 | 1449.5 | 60.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.8 |
| 239 | 6 | 11×11 | 23 | 4 | 6.0 | 1765.5 | 55.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.7 |
| 240 | 6 | 11×11 | 24 | 5 | 7.5 | 2226.0 | 57.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.0 |
| 241 | 6 | 12×12 | 25 | 5 | 8.1 | 2483.5 | 61.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.4 |
| 242 | 6 | 12×12 | 29 | 5 | 6.6 | 2016.5 | 55.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 95.8 |
| 243 | 6 | 12×12 | 28 | 5 | 8.2 | 2753.0 | 61.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.5 |
| 244 | 6 | 12×12 | 30 | 5 | 6.2 | 2000.5 | 56.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.2 |
| 245 | 6 | 12×12 | 26 | 5 | 6.8 | 2558.5 | 54.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.3 |
| 246 | 6 | 12×12 | 28 | 5 | 6.7 | 2461.5 | 56.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.5 |
| 247 | 6 | 12×12 | 30 | 5 | 8.6 | 2676.0 | 57.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.6 |
| 248 | 6 | 12×12 | 29 | 5 | 9.9 | 3505.5 | 66.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.2 |
| 249 | 6 | 12×12 | 28 | 5 | 7.1 | 1879.5 | 61.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.7 |
| 250 | 6 | 12×12 | 30 | 5 | 7.7 | 2400.0 | 58.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.9 |
| 251 | 6 | 12×12 | 29 | 5 | 6.8 | 2643.5 | 57.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.9 |
| 252 | 6 | 12×12 | 30 | 5 | 7.2 | 2919.5 | 61.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.2 |
| 253 | 6 | 12×12 | 30 | 5 | 10.8 | 4799.0 | 64.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.9 |
| 254 | 6 | 12×12 | 27 | 5 | 6.9 | 2835.0 | 54.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.0 |
| 255 | 6 | 12×12 | 30 | 5 | 6.1 | 2593.0 | 58.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.5 |
| 256 | 6 | 12×12 | 30 | 5 | 8.4 | 2548.5 | 59.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.3 |
| 257 | 6 | 12×12 | 29 | 5 | 6.0 | 1390.0 | 57.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.8 |
| 258 | 6 | 12×12 | 29 | 4 | 5.5 | 1763.5 | 51.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 96.7 |
| 259 | 6 | 12×12 | 28 | 5 | 7.0 | 2549.0 | 58.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.5 |
| 260 | 6 | 12×12 | 30 | 5 | 7.8 | 2415.5 | 58.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.4 |
| 261 | 6 | 12×12 | 29 | 5 | 5.8 | 2415.5 | 52.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.3 |
| 262 | 6 | 12×12 | 26 | 5 | 6.6 | 1923.0 | 53.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.0 |
| 263 | 6 | 12×12 | 28 | 5 | 7.0 | 2704.0 | 64.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 264 | 6 | 12×12 | 29 | 5 | 6.9 | 2348.0 | 64.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.1 |
| 265 | 6 | 12×12 | 30 | 5 | 7.4 | 2642.0 | 57.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.8 |
| 266 | 6 | 12×12 | 29 | 5 | 7.4 | 3278.5 | 55.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.1 |
| 267 | 6 | 12×12 | 29 | 5 | 7.9 | 2548.5 | 61.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.2 |
| 268 | 6 | 12×12 | 30 | 5 | 6.5 | 3245.5 | 53.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.1 |
| 269 | 6 | 12×12 | 27 | 5 | 7.3 | 2203.5 | 62.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.2 |
| 270 | 6 | 12×12 | 27 | 5 | 7.4 | 2112.5 | 57.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.8 |
| 271 | 6 | 12×12 | 30 | 5 | 6.7 | 2259.0 | 61.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 272 | 6 | 12×12 | 29 | 5 | 6.1 | 1691.5 | 56.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 97.8 |
| 273 | 6 | 12×12 | 30 | 5 | 5.9 | 1974.0 | 57.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.2 |
| 274 | 6 | 12×12 | 28 | 5 | 6.7 | 1882.5 | 57.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 275 | 6 | 12×12 | 30 | 5 | 6.2 | 2035.5 | 55.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 276 | 6 | 12×12 | 30 | 5 | 7.4 | 2467.0 | 57.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.5 |
| 277 | 6 | 12×12 | 27 | 4 | 6.0 | 2536.5 | 54.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.6 |
| 278 | 6 | 12×12 | 30 | 5 | 8.7 | 3458.0 | 60.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.0 |
| 279 | 6 | 12×12 | 30 | 5 | 8.6 | 4737.5 | 60.4 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.5 |
| 280 | 6 | 12×12 | 30 | 5 | 6.1 | 2202.5 | 56.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.6 |
| 281 | 6 | 12×12 | 30 | 5 | 6.5 | 1805.5 | 57.3 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.7 |
| 282 | 6 | 12×12 | 29 | 5 | 8.0 | 2831.0 | 60.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.5 |
| 283 | 6 | 12×12 | 30 | 5 | 6.2 | 1962.5 | 55.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.6 |
| 284 | 6 | 12×12 | 30 | 4 | 4.6 | 1166.0 | 51.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.3 |
| 285 | 6 | 12×12 | 30 | 5 | 5.9 | 1862.0 | 54.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 98.9 |
| 286 | 6 | 12×12 | 27 | 5 | 9.1 | 4150.0 | 62.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.8 |
| 287 | 6 | 12×12 | 30 | 5 | 8.5 | 3313.5 | 62.8 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.8 |
| 288 | 6 | 12×12 | 29 | 5 | 5.5 | 1223.0 | 57.9 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.0 |
| 289 | 6 | 12×12 | 28 | 5 | 7.3 | 3212.5 | 57.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.8 |
| 290 | 6 | 12×12 | 29 | 5 | 5.9 | 1752.0 | 54.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.4 |
| 291 | 6 | 12×12 | 30 | 5 | 7.0 | 2585.5 | 54.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.5 |
| 292 | 6 | 12×12 | 29 | 5 | 6.3 | 3076.0 | 58.7 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 293 | 6 | 12×12 | 28 | 5 | 6.6 | 1853.0 | 56.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.5 |
| 294 | 6 | 12×12 | 27 | 5 | 7.9 | 2269.5 | 61.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 99.9 |
| 295 | 6 | 12×12 | 29 | 5 | 7.7 | 2807.0 | 63.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 296 | 6 | 12×12 | 30 | 5 | 8.1 | 3414.0 | 60.0 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 297 | 6 | 12×12 | 30 | 5 | 8.1 | 3144.0 | 61.2 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 298 | 6 | 12×12 | 29 | 5 | 6.9 | 2572.5 | 56.6 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 299 | 6 | 12×12 | 28 | 5 | 10.8 | 6267.5 | 65.1 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |
| 300 | 6 | 12×12 | 29 | 5 | 7.0 | 2577.0 | 63.5 | RESISTANT_TO_ALL_GREEDY | UNIQUE | 100% | EXTREME | 100.0 |

## 6. Architectural Invariant Compliance Checklist

- [x] **World 1 100% Frozen:** Levels 1–20 code, JSON assets, checkpoints, and hashes completely untouched.
- [x] **Zero Board Geometry Changes:** Grid dimensions, cell counts, wall sets, and blocked edges remain 100% unchanged.
- [x] **Canonical Hamiltonian Solutions Preserved:** Every puzzle's underlying Hamiltonian path is valid, continuous, non-branching, non-diagonal, and full-coverage.
- [x] **Checkpoints Along Solution:** Checkpoints strictly follow the canonical path in ascending order.
- [x] **Solver Certified Uniqueness:** All 300 levels verified strictly UNIQUE under player rules.
- [x] **Full-Board Playability:** 100% of cells in every level are required and playable; 0 blocked cells.
- [x] **Multiplayer Eligibility Integrity:** Quick Duel and Friends Arena pool strictly 11×11 and 12×12 only; ≤10×10 count = 0.
- [x] **Backend-Frontend Alignment:** Multiplayer pool definitions in backend Java and Android catalog match.

