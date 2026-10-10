# ZYNPATH ADVANCED NUMBER STRATEGY AUDIT

> **Audit Date:** 2026-10-06  
> **Scope:** Solo 1–300 • Deep Route Ambiguity • Look-Ahead Depth • Anti-Greedy Resistance  
> **Preservation:** 100% Board Geometry, Cell Coordinates, Walls, and World Assignments PRESERVED.  
> **Verification:** Player-Visible Solver `UNIQUE`, 100% Full-Board Coverage.  

## 1. Executive Summary

- **Total Levels Audited:** 300 / 300
- **Levels Classified as EXCELLENT:** 277 (92.3%)
- **Levels Classified as GOOD:** 22 (7.3%)
- **Levels Classified as Weak:** 1 (0.3%)
- **Refinement Status:** 300 KEPT, 0 RETUNED, 0 REFINEMENT_TARGET_NOT_REACHED
- **Board Geometry & Wall Invariant:** 100% Unchanged
- **Player-Visible Solver Certification:** 300 / 300 Certified `UNIQUE`
- **Full Board Coverage:** 300 / 300 Exact (100%)

## 2. Look-Ahead Depth & Anti-Greedy Progression Summary

| Level Stage | Target Look-Ahead | Measured Look-Ahead Avg | Wrong Route Survival (Cells) | Anti-Greedy Resistance Rate | Strategic Identity |
|:---|:---:|:---:|:---:|:---:|:---| 
| Levels 1–4 (Tutorial) | **1** | **4.8** | **5.4 cells** | **100.0%** | Instructional / Tutorial (Greedy Allowed) |
| Levels 5–20 (World 1 Ambiguity) | **2** | **4.4** | **5.6 cells** | **87.5%** | Early Decision Points & Non-Linear Turns |
| Levels 21–50 (World 2 Deception) | **2** | **4.3** | **4.8 cells** | **80.0%** | Quadrant Reversals & Visual Proximity Traps |
| Levels 51–90 (World 3 2-CP Planning) | **3** | **4.6** | **6.4 cells** | **100.0%** | Multi-Stage Cell Reservation |
| Levels 91–130 (World 3/4 3-CP Planning) | **3** | **4.7** | **7.2 cells** | **100.0%** | Approach-Orientation Dependency |
| Levels 131–180 (World 4 3+ CP Dependency) | **4** | **4.7** | **7.2 cells** | **100.0%** | Delayed Failure Over Multiple Checkpoints |
| Levels 181–220 (World 5 Delayed Consequence) | **4** | **4.7** | **6.8 cells** | **100.0%** | Multi-Checkpoint Collisions |
| Levels 221–250 (World 6 Region Planning) | **5** | **4.9** | **7.3 cells** | **100.0%** | Whole-Region Traversal Coordination |
| Levels 251–275 (World 6 Global Planning) | **5** | **4.7** | **6.5 cells** | **100.0%** | Deep Global Ambiguity & Extensive Look-Ahead |
| Levels 276–299 (World 6 Extreme Planning) | **5** | **4.8** | **6.7 cells** | **100.0%** | Deep Global Ambiguity & Extensive Look-Ahead |
| Level 300 (Campaign Grandmaster) | **5** | **5.0** | **7.0 cells** | **100.0%** | Full-Board Grandmaster Reasoning Mastery |

## 3. Complete Level-by-Level Audit Table (Levels 1–300)

| Lvl | W | Grid | CPs | Classification | Status | Human Diff | Tier | LookAhead (Meas/Tgt) | Ambiguity | FutDep | ResPress | Decep | Approach | SurvivalDepth | MultiCP | Greedy Result |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|
| 1 | 1 | 6×6 | 10 | EXCELLENT | KEPT | 14.6 | BEGINNER | 5 / 1 | 65.0 | 273 | 45 | 0.00 | 0.88 | 6.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 2 | 1 | 6×6 | 9 | EXCELLENT | KEPT | 18.1 | BEGINNER | 4 / 1 | 71.9 | 160 | 40 | 0.33 | 1.00 | 5.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 3 | 1 | 6×6 | 10 | EXCELLENT | KEPT | 24.0 | BEGINNER | 5 / 1 | 75.3 | 302 | 41 | 0.33 | 1.00 | 6.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 4 | 1 | 6×6 | 9 | EXCELLENT | KEPT | 24.0 | BEGINNER | 5 / 1 | 63.5 | 137 | 41 | 0.17 | 0.86 | 4.4 | 4 | RESISTANT_TO_ALL_GREEDY |
| 5 | 1 | 6×6 | 10 | EXCELLENT | KEPT | 30.1 | EASY | 4 / 2 | 57.4 | 107 | 40 | 0.00 | 0.75 | 4.2 | 2 | RESISTANT_TO_ALL_GREEDY |
| 6 | 1 | 6×6 | 8 | EXCELLENT | KEPT | 28.8 | EASY | 4 / 2 | 54.1 | 85 | 38 | 0.00 | 0.67 | 3.4 | 1 | RESISTANT_TO_ALL_GREEDY |
| 7 | 1 | 6×6 | 9 | GOOD | KEPT | 30.5 | EASY | 4 / 2 | 55.5 | 133 | 48 | 0.00 | 0.86 | 3.7 | 1 | DEFEATED_BY_PERIMETER_FIRST |
| 8 | 1 | 6×6 | 6 | EXCELLENT | KEPT | 39.1 | EASY | 4 / 2 | 96.6 | 145 | 27 | 1.00 | 1.00 | 6.4 | 3 | RESISTANT_TO_ALL_GREEDY |
| 9 | 1 | 6×6 | 8 | EXCELLENT | KEPT | 33.7 | EASY | 5 / 2 | 71.9 | 109 | 32 | 0.40 | 0.83 | 4.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 10 | 1 | 6×6 | 10 | EXCELLENT | KEPT | 34.9 | EASY | 5 / 2 | 67.6 | 251 | 46 | 0.00 | 1.00 | 6.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 11 | 1 | 6×6 | 9 | EXCELLENT | KEPT | 32.2 | EASY | 5 / 2 | 49.8 | 92 | 44 | 0.17 | 1.00 | 3.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 12 | 1 | 6×6 | 8 | EXCELLENT | KEPT | 34.4 | EASY | 5 / 2 | 72.9 | 149 | 33 | 0.43 | 0.83 | 4.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 13 | 1 | 6×6 | 9 | EXCELLENT | KEPT | 32.3 | EASY | 4 / 2 | 50.8 | 142 | 61 | 0.00 | 0.86 | 5.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 14 | 1 | 6×6 | 7 | EXCELLENT | KEPT | 34.5 | EASY | 4 / 2 | 67.3 | 192 | 37 | 0.25 | 1.00 | 4.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 15 | 1 | 6×6 | 9 | GOOD | KEPT | 38.0 | EASY | 5 / 2 | 78.6 | 305 | 40 | 0.29 | 0.86 | 7.8 | 6 | DEFEATED_BY_ROW_SNAKE |
| 16 | 1 | 6×6 | 9 | EXCELLENT | KEPT | 31.6 | EASY | 4 / 2 | 58.3 | 79 | 39 | 0.17 | 1.00 | 4.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 17 | 1 | 6×6 | 7 | EXCELLENT | KEPT | 34.9 | EASY | 5 / 2 | 74.7 | 194 | 29 | 0.25 | 0.80 | 6.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 18 | 1 | 6×6 | 8 | EXCELLENT | KEPT | 37.7 | EASY | 4 / 2 | 75.2 | 300 | 36 | 0.29 | 1.00 | 9.4 | 6 | RESISTANT_TO_ALL_GREEDY |
| 19 | 1 | 6×6 | 8 | EXCELLENT | KEPT | 35.4 | EASY | 4 / 2 | 68.3 | 238 | 32 | 0.17 | 1.00 | 8.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 20 | 1 | 6×6 | 10 | EXCELLENT | KEPT | 35.6 | EASY | 5 / 2 | 69.4 | 222 | 46 | 0.13 | 1.00 | 6.2 | 7 | RESISTANT_TO_ALL_GREEDY |
| 21 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 58.6 | MEDIUM | 4 / 2 | 57.3 | 250 | 75 | 0.00 | 0.88 | 4.8 | 2 | RESISTANT_TO_ALL_GREEDY |
| 22 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 58.3 | MEDIUM | 4 / 2 | 55.3 | 247 | 67 | 0.00 | 0.80 | 4.3 | 2 | RESISTANT_TO_ALL_GREEDY |
| 23 | 2 | 7×7 | 12 | GOOD | KEPT | 58.6 | MEDIUM | 4 / 2 | 53.4 | 146 | 76 | 0.00 | 0.90 | 5.1 | 1 | DEFEATED_BY_PERIMETER_FIRST |
| 24 | 2 | 7×7 | 11 | GOOD | KEPT | 59.5 | MEDIUM | 4 / 2 | 52.9 | 198 | 88 | 0.00 | 1.00 | 3.6 | 1 | DEFEATED_BY_PERIMETER_FIRST |
| 25 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 61.0 | HARD | 4 / 2 | 59.5 | 307 | 61 | 0.00 | 0.88 | 5.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 26 | 2 | 7×7 | 9 | EXCELLENT | KEPT | 60.4 | HARD | 4 / 2 | 53.9 | 212 | 73 | 0.00 | 1.00 | 3.9 | 1 | RESISTANT_TO_ALL_GREEDY |
| 27 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 60.7 | HARD | 4 / 2 | 52.6 | 215 | 79 | 0.00 | 0.80 | 3.5 | 2 | RESISTANT_TO_ALL_GREEDY |
| 28 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 62.4 | HARD | 4 / 2 | 54.1 | 183 | 65 | 0.11 | 1.00 | 4.4 | 3 | RESISTANT_TO_ALL_GREEDY |
| 29 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 64.1 | HARD | 4 / 2 | 64.8 | 346 | 69 | 0.00 | 0.88 | 6.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 30 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 61.5 | HARD | 4 / 2 | 53.7 | 126 | 66 | 0.00 | 0.90 | 3.8 | 2 | RESISTANT_TO_ALL_GREEDY |
| 31 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 62.1 | HARD | 4 / 2 | 54.1 | 178 | 68 | 0.00 | 0.88 | 3.9 | 1 | RESISTANT_TO_ALL_GREEDY |
| 32 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 64.1 | HARD | 4 / 2 | 58.6 | 204 | 58 | 0.17 | 0.75 | 3.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 33 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 65.3 | HARD | 4 / 2 | 58.6 | 293 | 85 | 0.00 | 0.90 | 5.2 | 2 | RESISTANT_TO_ALL_GREEDY |
| 34 | 2 | 7×7 | 12 | GOOD | KEPT | 64.2 | HARD | 4 / 2 | 50.5 | 194 | 90 | 0.00 | 1.00 | 2.9 | 1 | DEFEATED_BY_PERIMETER_FIRST |
| 35 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 67.7 | HARD | 5 / 2 | 64.8 | 323 | 52 | 0.20 | 1.00 | 5.3 | 6 | RESISTANT_TO_ALL_GREEDY |
| 36 | 2 | 7×7 | 8 | EXCELLENT | KEPT | 66.4 | HARD | 4 / 2 | 63.7 | 204 | 43 | 0.20 | 0.83 | 5.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 37 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 69.0 | HARD | 5 / 2 | 65.6 | 273 | 68 | 0.22 | 0.90 | 5.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 38 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 66.9 | HARD | 4 / 2 | 55.6 | 237 | 64 | 0.00 | 0.88 | 5.1 | 3 | RESISTANT_TO_ALL_GREEDY |
| 39 | 2 | 7×7 | 11 | EXCELLENT | KEPT | 68.5 | HARD | 4 / 2 | 59.5 | 213 | 78 | 0.20 | 1.00 | 3.8 | 2 | RESISTANT_TO_ALL_GREEDY |
| 40 | 2 | 7×7 | 10 | EXCELLENT | KEPT | 69.8 | HARD | 5 / 2 | 63.9 | 267 | 52 | 0.14 | 1.00 | 5.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 41 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 68.4 | HARD | 5 / 2 | 56.9 | 194 | 61 | 0.00 | 0.90 | 4.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 42 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 72.0 | HARD | 5 / 2 | 66.5 | 369 | 58 | 0.11 | 1.00 | 6.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 43 | 2 | 7×7 | 11 | EXCELLENT | KEPT | 71.2 | HARD | 5 / 2 | 61.4 | 280 | 64 | 0.17 | 1.00 | 4.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 44 | 2 | 7×7 | 10 | GOOD | KEPT | 69.9 | HARD | 4 / 2 | 51.3 | 225 | 90 | 0.00 | 0.88 | 4.6 | 1 | DEFEATED_BY_COLUMN_SNAKE |
| 45 | 2 | 7×7 | 12 | EXCELLENT | KEPT | 71.2 | HARD | 4 / 2 | 60.3 | 254 | 67 | 0.20 | 0.90 | 4.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 46 | 2 | 7×7 | 10 | GOOD | KEPT | 72.4 | HARD | 5 / 2 | 58.7 | 162 | 75 | 0.20 | 1.00 | 4.3 | 4 | DEFEATED_BY_PERIMETER_FIRST |
| 47 | 2 | 7×7 | 10 | GOOD | KEPT | 70.5 | HARD | 4 / 2 | 57.7 | 182 | 61 | 0.00 | 0.88 | 4.9 | 1 | DEFEATED_BY_PERIMETER_FIRST |
| 48 | 2 | 7×7 | 11 | EXCELLENT | KEPT | 74.4 | HARD | 4 / 2 | 68.2 | 208 | 58 | 0.38 | 1.00 | 4.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 49 | 2 | 7×7 | 11 | EXCELLENT | KEPT | 75.7 | VERY_HARD | 5 / 2 | 69.3 | 352 | 50 | 0.25 | 1.00 | 6.1 | 8 | RESISTANT_TO_ALL_GREEDY |
| 50 | 2 | 7×7 | 11 | EXCELLENT | KEPT | 78.9 | VERY_HARD | 5 / 2 | 75.0 | 555 | 63 | 0.17 | 1.00 | 9.1 | 7 | RESISTANT_TO_ALL_GREEDY |
| 51 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 68.5 | HARD | 4 / 3 | 57.8 | 423 | 117 | 0.00 | 0.92 | 5.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 52 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 68.0 | HARD | 4 / 3 | 55.0 | 344 | 119 | 0.00 | 0.83 | 4.8 | 2 | RESISTANT_TO_ALL_GREEDY |
| 53 | 3 | 8×8 | 13 | EXCELLENT | KEPT | 68.3 | HARD | 4 / 3 | 57.1 | 418 | 117 | 0.00 | 0.82 | 5.5 | 1 | RESISTANT_TO_ALL_GREEDY |
| 54 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 69.7 | HARD | 4 / 3 | 61.3 | 543 | 114 | 0.13 | 0.92 | 5.6 | 2 | RESISTANT_TO_ALL_GREEDY |
| 55 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 70.1 | HARD | 5 / 3 | 62.5 | 611 | 120 | 0.00 | 0.77 | 7.2 | 5 | RESISTANT_TO_ALL_GREEDY |
| 56 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 69.0 | HARD | 4 / 3 | 56.9 | 392 | 100 | 0.00 | 1.00 | 5.4 | 3 | RESISTANT_TO_ALL_GREEDY |
| 57 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 70.1 | HARD | 5 / 3 | 60.6 | 631 | 97 | 0.00 | 0.92 | 6.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 58 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 71.0 | HARD | 4 / 3 | 63.6 | 649 | 102 | 0.20 | 0.92 | 5.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 59 | 3 | 8×8 | 13 | EXCELLENT | KEPT | 71.2 | HARD | 5 / 3 | 64.3 | 757 | 133 | 0.00 | 0.73 | 7.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 60 | 3 | 8×8 | 11 | EXCELLENT | KEPT | 72.3 | HARD | 4 / 3 | 77.8 | 470 | 94 | 0.60 | 0.78 | 6.3 | 2 | RESISTANT_TO_ALL_GREEDY |
| 61 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 69.8 | HARD | 4 / 3 | 55.0 | 455 | 103 | 0.00 | 0.92 | 4.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 62 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 70.3 | HARD | 4 / 3 | 57.8 | 496 | 95 | 0.00 | 1.00 | 5.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 63 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 72.1 | HARD | 5 / 3 | 62.1 | 739 | 129 | 0.14 | 0.85 | 5.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 64 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 71.9 | HARD | 4 / 3 | 65.8 | 662 | 115 | 0.14 | 0.77 | 6.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 65 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 72.3 | HARD | 4 / 3 | 65.3 | 624 | 106 | 0.20 | 1.00 | 6.2 | 3 | RESISTANT_TO_ALL_GREEDY |
| 66 | 3 | 8×8 | 12 | EXCELLENT | KEPT | 70.6 | HARD | 4 / 3 | 55.5 | 447 | 104 | 0.00 | 1.00 | 5.0 | 2 | RESISTANT_TO_ALL_GREEDY |
| 67 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 71.6 | HARD | 5 / 3 | 58.9 | 415 | 122 | 0.00 | 0.92 | 6.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 68 | 3 | 8×8 | 13 | EXCELLENT | KEPT | 72.3 | HARD | 5 / 3 | 63.7 | 624 | 102 | 0.17 | 0.73 | 6.0 | 4 | RESISTANT_TO_ALL_GREEDY |
| 69 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 71.4 | HARD | 5 / 3 | 56.6 | 415 | 105 | 0.00 | 1.00 | 5.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 70 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 72.4 | HARD | 5 / 3 | 60.3 | 645 | 101 | 0.00 | 1.00 | 6.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 71 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 72.3 | HARD | 4 / 3 | 61.5 | 538 | 94 | 0.11 | 0.92 | 5.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 72 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 73.3 | HARD | 5 / 3 | 64.9 | 714 | 89 | 0.14 | 0.92 | 6.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 73 | 3 | 8×8 | 13 | EXCELLENT | KEPT | 74.0 | HARD | 5 / 3 | 66.3 | 930 | 116 | 0.00 | 0.73 | 8.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 74 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 75.0 | HARD | 5 / 3 | 72.1 | 809 | 94 | 0.29 | 0.92 | 7.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 75 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 74.2 | HARD | 5 / 3 | 63.9 | 867 | 138 | 0.00 | 0.75 | 7.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 76 | 3 | 8×8 | 11 | EXCELLENT | KEPT | 73.7 | HARD | 4 / 3 | 63.3 | 796 | 102 | 0.00 | 1.00 | 7.5 | 2 | RESISTANT_TO_ALL_GREEDY |
| 77 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 73.9 | HARD | 5 / 3 | 56.1 | 742 | 139 | 0.00 | 1.00 | 5.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 78 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 74.0 | HARD | 4 / 3 | 60.9 | 548 | 128 | 0.17 | 1.00 | 5.1 | 3 | RESISTANT_TO_ALL_GREEDY |
| 79 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 75.1 | VERY_HARD | 5 / 3 | 66.7 | 664 | 92 | 0.22 | 1.00 | 6.4 | 8 | RESISTANT_TO_ALL_GREEDY |
| 80 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 76.7 | VERY_HARD | 5 / 3 | 69.6 | 1065 | 122 | 0.25 | 0.92 | 7.1 | 6 | RESISTANT_TO_ALL_GREEDY |
| 81 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 74.3 | HARD | 5 / 3 | 58.0 | 577 | 131 | 0.00 | 1.00 | 5.8 | 5 | RESISTANT_TO_ALL_GREEDY |
| 82 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 74.1 | HARD | 4 / 3 | 61.9 | 597 | 91 | 0.13 | 0.85 | 5.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 83 | 3 | 8×8 | 11 | EXCELLENT | KEPT | 76.6 | VERY_HARD | 5 / 3 | 79.2 | 549 | 78 | 0.67 | 0.78 | 6.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 84 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 77.5 | VERY_HARD | 5 / 3 | 72.8 | 1582 | 127 | 0.17 | 0.92 | 8.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 85 | 3 | 8×8 | 13 | EXCELLENT | KEPT | 80.4 | VERY_HARD | 5 / 3 | 93.2 | 887 | 114 | 1.00 | 0.91 | 7.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 86 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 74.5 | HARD | 4 / 3 | 59.3 | 608 | 99 | 0.00 | 0.92 | 6.2 | 3 | RESISTANT_TO_ALL_GREEDY |
| 87 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 76.6 | VERY_HARD | 5 / 3 | 67.7 | 901 | 90 | 0.11 | 0.85 | 7.8 | 8 | RESISTANT_TO_ALL_GREEDY |
| 88 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 78.7 | VERY_HARD | 5 / 3 | 81.0 | 876 | 86 | 0.50 | 0.92 | 8.3 | 6 | RESISTANT_TO_ALL_GREEDY |
| 89 | 3 | 8×8 | 14 | EXCELLENT | KEPT | 76.2 | VERY_HARD | 5 / 3 | 63.7 | 833 | 94 | 0.00 | 0.83 | 7.6 | 6 | RESISTANT_TO_ALL_GREEDY |
| 90 | 3 | 8×8 | 15 | EXCELLENT | KEPT | 77.2 | VERY_HARD | 5 / 3 | 64.9 | 1051 | 99 | 0.00 | 0.92 | 8.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 91 | 3 | 9×9 | 17 | EXCELLENT | KEPT | 75.5 | VERY_HARD | 5 / 3 | 60.9 | 912 | 126 | 0.00 | 0.87 | 7.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 92 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 75.7 | VERY_HARD | 5 / 3 | 54.5 | 687 | 208 | 0.00 | 1.00 | 5.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 93 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 77.4 | VERY_HARD | 5 / 3 | 63.7 | 1252 | 168 | 0.00 | 1.00 | 8.5 | 6 | RESISTANT_TO_ALL_GREEDY |
| 94 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 77.2 | VERY_HARD | 5 / 3 | 62.9 | 1033 | 148 | 0.10 | 1.00 | 7.2 | 7 | RESISTANT_TO_ALL_GREEDY |
| 95 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 78.1 | VERY_HARD | 5 / 3 | 63.5 | 1514 | 171 | 0.00 | 0.94 | 8.5 | 6 | RESISTANT_TO_ALL_GREEDY |
| 96 | 3 | 9×9 | 17 | EXCELLENT | KEPT | 77.1 | VERY_HARD | 4 / 3 | 65.1 | 871 | 164 | 0.13 | 1.00 | 7.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 97 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 76.4 | VERY_HARD | 4 / 3 | 60.9 | 796 | 136 | 0.14 | 0.94 | 6.0 | 2 | RESISTANT_TO_ALL_GREEDY |
| 98 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 77.7 | VERY_HARD | 5 / 3 | 60.7 | 1077 | 199 | 0.00 | 0.94 | 7.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 99 | 3 | 9×9 | 17 | EXCELLENT | KEPT | 78.9 | VERY_HARD | 5 / 3 | 73.0 | 1210 | 145 | 0.10 | 0.93 | 11.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 100 | 3 | 9×9 | 18 | EXCELLENT | KEPT | 77.8 | VERY_HARD | 5 / 3 | 58.2 | 1012 | 198 | 0.00 | 1.00 | 6.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 101 | 4 | 9×9 | 15 | EXCELLENT | KEPT | 81.5 | VERY_HARD | 4 / 3 | 53.8 | 492 | 120 | 0.00 | 1.00 | 5.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 102 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 82.3 | VERY_HARD | 4 / 3 | 57.0 | 705 | 150 | 0.00 | 1.00 | 6.1 | 3 | RESISTANT_TO_ALL_GREEDY |
| 103 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 83.0 | VERY_HARD | 4 / 3 | 60.9 | 989 | 122 | 0.11 | 0.93 | 6.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 104 | 4 | 9×9 | 16 | EXCELLENT | KEPT | 83.8 | VERY_HARD | 5 / 3 | 61.8 | 1316 | 146 | 0.00 | 1.00 | 7.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 105 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 84.3 | VERY_HARD | 4 / 3 | 65.8 | 1314 | 131 | 0.22 | 0.94 | 6.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 106 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 83.7 | VERY_HARD | 5 / 3 | 61.4 | 971 | 151 | 0.11 | 0.88 | 6.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 107 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 83.2 | VERY_HARD | 4 / 3 | 56.0 | 819 | 165 | 0.00 | 1.00 | 5.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 108 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 84.4 | VERY_HARD | 5 / 3 | 65.8 | 932 | 151 | 0.13 | 0.88 | 7.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 109 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 85.5 | VERY_HARD | 4 / 3 | 68.3 | 1440 | 196 | 0.14 | 0.93 | 8.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 110 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 85.6 | VERY_HARD | 5 / 3 | 64.5 | 1429 | 194 | 0.11 | 1.00 | 7.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 111 | 4 | 9×9 | 16 | EXCELLENT | KEPT | 84.0 | VERY_HARD | 4 / 3 | 61.0 | 674 | 139 | 0.17 | 1.00 | 5.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 112 | 4 | 9×9 | 16 | EXCELLENT | KEPT | 84.3 | VERY_HARD | 4 / 3 | 62.1 | 610 | 148 | 0.25 | 0.93 | 5.2 | 3 | RESISTANT_TO_ALL_GREEDY |
| 113 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 85.9 | VERY_HARD | 5 / 3 | 64.7 | 1361 | 190 | 0.11 | 0.94 | 7.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 114 | 4 | 9×9 | 16 | EXCELLENT | KEPT | 85.3 | VERY_HARD | 5 / 3 | 69.7 | 737 | 118 | 0.40 | 0.93 | 6.4 | 4 | RESISTANT_TO_ALL_GREEDY |
| 115 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 86.6 | VERY_HARD | 5 / 3 | 67.5 | 1299 | 204 | 0.14 | 1.00 | 8.3 | 8 | RESISTANT_TO_ALL_GREEDY |
| 116 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 84.4 | VERY_HARD | 5 / 3 | 59.2 | 743 | 129 | 0.00 | 0.87 | 6.9 | 5 | RESISTANT_TO_ALL_GREEDY |
| 117 | 4 | 9×9 | 16 | EXCELLENT | KEPT | 85.2 | VERY_HARD | 4 / 3 | 64.4 | 869 | 134 | 0.20 | 0.86 | 6.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 118 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 86.9 | VERY_HARD | 5 / 3 | 70.7 | 1074 | 171 | 0.38 | 0.88 | 7.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 119 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 87.0 | VERY_HARD | 5 / 3 | 70.7 | 1171 | 151 | 0.40 | 1.00 | 6.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 120 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 86.5 | VERY_HARD | 5 / 3 | 61.7 | 1286 | 199 | 0.00 | 0.88 | 7.8 | 6 | RESISTANT_TO_ALL_GREEDY |
| 121 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 85.3 | VERY_HARD | 5 / 3 | 59.0 | 734 | 146 | 0.00 | 1.00 | 6.8 | 5 | RESISTANT_TO_ALL_GREEDY |
| 122 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 86.0 | VERY_HARD | 5 / 3 | 63.6 | 1045 | 114 | 0.11 | 0.88 | 7.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 123 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 87.2 | VERY_HARD | 5 / 3 | 71.1 | 901 | 131 | 0.33 | 1.00 | 7.6 | 8 | RESISTANT_TO_ALL_GREEDY |
| 124 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 87.5 | VERY_HARD | 5 / 3 | 66.9 | 1294 | 168 | 0.13 | 1.00 | 8.3 | 8 | RESISTANT_TO_ALL_GREEDY |
| 125 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 87.4 | VERY_HARD | 5 / 3 | 64.7 | 1457 | 170 | 0.00 | 0.94 | 8.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 126 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 86.5 | VERY_HARD | 5 / 3 | 56.7 | 982 | 170 | 0.00 | 0.94 | 6.0 | 8 | RESISTANT_TO_ALL_GREEDY |
| 127 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 86.6 | VERY_HARD | 5 / 3 | 57.5 | 954 | 168 | 0.00 | 1.00 | 6.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 128 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 86.0 | VERY_HARD | 5 / 3 | 54.2 | 672 | 155 | 0.00 | 0.93 | 5.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 129 | 4 | 9×9 | 18 | EXCELLENT | KEPT | 87.2 | VERY_HARD | 5 / 3 | 57.3 | 1105 | 175 | 0.00 | 1.00 | 6.2 | 6 | RESISTANT_TO_ALL_GREEDY |
| 130 | 4 | 9×9 | 17 | EXCELLENT | KEPT | 88.8 | EXPERT | 5 / 3 | 73.0 | 2123 | 137 | 0.14 | 0.93 | 10.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 131 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 86.5 | VERY_HARD | 4 / 4 | 58.3 | 997 | 159 | 0.14 | 1.00 | 5.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 132 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.1 | VERY_HARD | 5 / 4 | 59.0 | 1253 | 192 | 0.00 | 1.00 | 7.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 133 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 86.7 | VERY_HARD | 4 / 4 | 56.9 | 1324 | 172 | 0.00 | 0.94 | 6.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 134 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.6 | VERY_HARD | 5 / 4 | 58.1 | 1548 | 202 | 0.00 | 1.00 | 7.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 135 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.8 | VERY_HARD | 4 / 4 | 63.8 | 1375 | 186 | 0.14 | 0.94 | 7.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 136 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.3 | VERY_HARD | 5 / 4 | 54.2 | 879 | 239 | 0.00 | 0.94 | 5.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 137 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.3 | VERY_HARD | 4 / 4 | 57.1 | 1167 | 186 | 0.00 | 0.94 | 6.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 138 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 88.5 | EXPERT | 5 / 4 | 63.4 | 1487 | 170 | 0.10 | 1.00 | 8.1 | 7 | RESISTANT_TO_ALL_GREEDY |
| 139 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 88.9 | EXPERT | 5 / 4 | 64.9 | 1479 | 226 | 0.14 | 0.94 | 8.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 140 | 4 | 10×10 | 19 | EXCELLENT | KEPT | 88.9 | EXPERT | 5 / 4 | 60.0 | 1660 | 216 | 0.00 | 1.00 | 8.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 141 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 88.9 | EXPERT | 5 / 4 | 65.7 | 1038 | 161 | 0.27 | 1.00 | 7.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 142 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 87.9 | VERY_HARD | 4 / 4 | 56.3 | 1165 | 172 | 0.00 | 1.00 | 6.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 143 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 89.7 | EXPERT | 5 / 4 | 64.4 | 2052 | 199 | 0.00 | 0.94 | 9.8 | 7 | RESISTANT_TO_ALL_GREEDY |
| 144 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 89.8 | EXPERT | 5 / 4 | 64.2 | 2619 | 159 | 0.00 | 0.94 | 9.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 145 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 90.9 | EXPERT | 5 / 4 | 68.7 | 2168 | 197 | 0.30 | 1.00 | 7.9 | 5 | RESISTANT_TO_ALL_GREEDY |
| 146 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 89.6 | EXPERT | 5 / 4 | 58.1 | 1635 | 244 | 0.00 | 0.89 | 7.2 | 5 | RESISTANT_TO_ALL_GREEDY |
| 147 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 90.8 | EXPERT | 5 / 4 | 62.9 | 2649 | 229 | 0.00 | 0.94 | 9.2 | 7 | RESISTANT_TO_ALL_GREEDY |
| 148 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 90.3 | EXPERT | 5 / 4 | 62.3 | 1590 | 204 | 0.15 | 1.00 | 7.1 | 8 | RESISTANT_TO_ALL_GREEDY |
| 149 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 91.3 | EXPERT | 5 / 4 | 65.5 | 2871 | 224 | 0.11 | 1.00 | 8.9 | 5 | RESISTANT_TO_ALL_GREEDY |
| 150 | 4 | 10×10 | 20 | EXCELLENT | KEPT | 89.6 | EXPERT | 5 / 4 | 59.5 | 923 | 197 | 0.17 | 1.00 | 5.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 151 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 93.2 | EXPERT | 5 / 4 | 58.5 | 925 | 212 | 0.09 | 0.94 | 6.3 | 6 | RESISTANT_TO_ALL_GREEDY |
| 152 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 93.5 | EXPERT | 5 / 4 | 60.1 | 1077 | 204 | 0.20 | 1.00 | 5.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 153 | 5 | 10×10 | 18 | EXCELLENT | KEPT | 93.9 | EXPERT | 5 / 4 | 62.5 | 1857 | 206 | 0.00 | 0.88 | 9.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 154 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 93.5 | EXPERT | 5 / 4 | 61.1 | 1167 | 184 | 0.13 | 0.94 | 7.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 155 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 94.0 | EXPERT | 5 / 4 | 61.7 | 1424 | 260 | 0.10 | 0.89 | 7.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 156 | 5 | 10×10 | 15 | EXCELLENT | KEPT | 94.1 | EXTREME | 5 / 4 | 68.7 | 1238 | 163 | 0.25 | 0.85 | 8.5 | 6 | RESISTANT_TO_ALL_GREEDY |
| 157 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 93.4 | EXPERT | 5 / 4 | 58.5 | 711 | 213 | 0.11 | 1.00 | 6.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 158 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 94.7 | EXTREME | 5 / 4 | 68.5 | 1429 | 221 | 0.27 | 0.94 | 8.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 159 | 5 | 10×10 | 16 | EXCELLENT | KEPT | 94.8 | EXTREME | 5 / 4 | 63.2 | 1956 | 237 | 0.13 | 0.93 | 7.8 | 6 | RESISTANT_TO_ALL_GREEDY |
| 160 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 95.1 | EXTREME | 5 / 4 | 62.9 | 2068 | 272 | 0.10 | 1.00 | 8.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 161 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 93.8 | EXPERT | 4 / 4 | 59.7 | 1028 | 173 | 0.17 | 0.94 | 5.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 162 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 93.3 | EXPERT | 4 / 4 | 53.5 | 744 | 209 | 0.00 | 0.93 | 5.4 | 2 | RESISTANT_TO_ALL_GREEDY |
| 163 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 93.9 | EXPERT | 4 / 4 | 60.4 | 843 | 196 | 0.14 | 0.94 | 6.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 164 | 5 | 10×10 | 18 | EXCELLENT | KEPT | 95.5 | EXTREME | 5 / 4 | 65.4 | 2140 | 270 | 0.14 | 0.94 | 8.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 165 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 94.6 | EXTREME | 5 / 4 | 60.9 | 1704 | 220 | 0.00 | 0.93 | 8.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 166 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 93.7 | EXPERT | 4 / 4 | 55.9 | 748 | 170 | 0.11 | 0.89 | 5.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 167 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 94.2 | EXTREME | 4 / 4 | 60.7 | 842 | 174 | 0.20 | 0.93 | 5.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 168 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 94.8 | EXTREME | 5 / 4 | 60.6 | 1931 | 179 | 0.00 | 0.82 | 8.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 169 | 5 | 10×10 | 16 | EXCELLENT | KEPT | 95.2 | EXTREME | 4 / 4 | 70.0 | 838 | 178 | 0.50 | 1.00 | 6.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 170 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 95.0 | EXTREME | 5 / 4 | 62.9 | 1246 | 165 | 0.14 | 1.00 | 7.4 | 6 | RESISTANT_TO_ALL_GREEDY |
| 171 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 95.4 | EXTREME | 5 / 4 | 68.8 | 1050 | 177 | 0.40 | 1.00 | 6.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 172 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 94.3 | EXTREME | 4 / 4 | 53.6 | 1077 | 185 | 0.00 | 0.93 | 5.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 173 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 95.0 | EXTREME | 4 / 4 | 62.3 | 1262 | 170 | 0.17 | 1.00 | 6.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 174 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 94.4 | EXTREME | 5 / 4 | 56.1 | 731 | 175 | 0.00 | 1.00 | 6.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 175 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 95.5 | EXTREME | 4 / 4 | 59.9 | 1646 | 218 | 0.11 | 1.00 | 6.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 176 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 95.3 | EXTREME | 5 / 4 | 60.0 | 1266 | 204 | 0.11 | 1.00 | 6.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 177 | 5 | 10×10 | 16 | EXCELLENT | KEPT | 95.5 | EXTREME | 5 / 4 | 64.9 | 1211 | 173 | 0.14 | 0.93 | 8.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 178 | 5 | 10×10 | 17 | EXCELLENT | KEPT | 95.0 | EXTREME | 4 / 4 | 57.1 | 1268 | 183 | 0.00 | 0.93 | 6.8 | 3 | RESISTANT_TO_ALL_GREEDY |
| 179 | 5 | 10×10 | 19 | EXCELLENT | KEPT | 95.7 | EXTREME | 4 / 4 | 64.2 | 1420 | 177 | 0.18 | 0.94 | 7.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 180 | 5 | 10×10 | 20 | EXCELLENT | KEPT | 96.9 | EXTREME | 5 / 4 | 67.3 | 2547 | 189 | 0.18 | 1.00 | 8.8 | 8 | RESISTANT_TO_ALL_GREEDY |
| 181 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 95.5 | EXTREME | 4 / 4 | 57.7 | 1344 | 284 | 0.18 | 0.95 | 5.4 | 2 | RESISTANT_TO_ALL_GREEDY |
| 182 | 5 | 11×11 | 22 | EXCELLENT | KEPT | 95.3 | EXTREME | 5 / 4 | 55.7 | 1256 | 240 | 0.08 | 1.00 | 5.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 183 | 5 | 11×11 | 24 | EXCELLENT | KEPT | 96.0 | EXTREME | 5 / 4 | 57.3 | 2361 | 280 | 0.00 | 0.86 | 7.6 | 8 | RESISTANT_TO_ALL_GREEDY |
| 184 | 5 | 11×11 | 22 | EXCELLENT | KEPT | 95.5 | EXTREME | 5 / 4 | 57.6 | 1085 | 208 | 0.18 | 0.95 | 5.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 185 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.2 | EXTREME | 5 / 4 | 60.1 | 1959 | 258 | 0.10 | 0.95 | 7.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 186 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.1 | EXTREME | 5 / 4 | 60.2 | 1459 | 245 | 0.14 | 0.90 | 7.0 | 7 | RESISTANT_TO_ALL_GREEDY |
| 187 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 95.8 | EXTREME | 4 / 4 | 56.7 | 1757 | 247 | 0.08 | 0.95 | 6.3 | 2 | RESISTANT_TO_ALL_GREEDY |
| 188 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.3 | EXTREME | 5 / 4 | 58.9 | 1754 | 284 | 0.10 | 0.90 | 7.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 189 | 5 | 11×11 | 24 | EXCELLENT | KEPT | 97.1 | EXTREME | 5 / 4 | 57.9 | 3143 | 379 | 0.00 | 0.91 | 7.9 | 5 | RESISTANT_TO_ALL_GREEDY |
| 190 | 5 | 11×11 | 22 | EXCELLENT | KEPT | 96.1 | EXTREME | 5 / 4 | 57.2 | 1372 | 235 | 0.09 | 1.00 | 6.4 | 4 | RESISTANT_TO_ALL_GREEDY |
| 191 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 97.1 | EXTREME | 5 / 4 | 65.3 | 1778 | 262 | 0.25 | 0.90 | 7.8 | 6 | RESISTANT_TO_ALL_GREEDY |
| 192 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.6 | EXTREME | 5 / 4 | 59.9 | 1668 | 243 | 0.17 | 0.95 | 6.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 193 | 5 | 11×11 | 21 | EXCELLENT | KEPT | 96.9 | EXTREME | 5 / 4 | 64.0 | 1395 | 226 | 0.30 | 1.00 | 6.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 194 | 5 | 11×11 | 24 | EXCELLENT | KEPT | 96.4 | EXTREME | 5 / 4 | 54.7 | 1419 | 258 | 0.00 | 0.95 | 6.5 | 6 | RESISTANT_TO_ALL_GREEDY |
| 195 | 5 | 11×11 | 24 | EXCELLENT | KEPT | 96.5 | EXTREME | 4 / 4 | 58.4 | 1413 | 237 | 0.13 | 0.95 | 6.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 196 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.6 | EXTREME | 4 / 4 | 57.6 | 1587 | 239 | 0.17 | 0.95 | 5.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 197 | 5 | 11×11 | 22 | EXCELLENT | KEPT | 97.0 | EXTREME | 5 / 4 | 55.8 | 1896 | 316 | 0.00 | 1.00 | 7.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 198 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.8 | EXTREME | 5 / 4 | 58.4 | 1665 | 248 | 0.08 | 0.90 | 7.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 199 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 97.0 | EXTREME | 5 / 4 | 59.5 | 1460 | 237 | 0.18 | 0.90 | 6.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 200 | 5 | 11×11 | 23 | EXCELLENT | KEPT | 96.3 | EXTREME | 4 / 4 | 52.0 | 1153 | 246 | 0.00 | 0.90 | 5.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 201 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 93.0 | EXPERT | 5 / 4 | 55.6 | 1332 | 270 | 0.09 | 0.95 | 5.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 202 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.8 | EXPERT | 5 / 4 | 58.9 | 3004 | 335 | 0.00 | 1.00 | 8.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 203 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 92.9 | EXPERT | 4 / 4 | 56.7 | 1227 | 227 | 0.09 | 1.00 | 6.1 | 3 | RESISTANT_TO_ALL_GREEDY |
| 204 | 6 | 11×11 | 20 | EXCELLENT | KEPT | 94.0 | EXPERT | 5 / 4 | 70.1 | 1620 | 235 | 0.38 | 0.89 | 8.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 205 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 93.5 | EXPERT | 5 / 4 | 58.3 | 1873 | 253 | 0.08 | 0.95 | 7.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 206 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.8 | EXPERT | 5 / 4 | 64.0 | 1395 | 224 | 0.30 | 1.00 | 6.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 207 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.1 | EXPERT | 5 / 4 | 53.6 | 1132 | 229 | 0.00 | 0.95 | 6.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 208 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 93.5 | EXPERT | 4 / 4 | 58.4 | 1413 | 237 | 0.13 | 0.95 | 6.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 209 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 93.6 | EXPERT | 5 / 4 | 58.1 | 1297 | 248 | 0.08 | 1.00 | 6.9 | 5 | RESISTANT_TO_ALL_GREEDY |
| 210 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.9 | EXPERT | 5 / 4 | 57.1 | 1620 | 297 | 0.08 | 1.00 | 6.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 211 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 93.7 | EXPERT | 5 / 4 | 57.4 | 1503 | 237 | 0.06 | 0.95 | 6.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 212 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.8 | EXPERT | 4 / 4 | 59.9 | 1478 | 216 | 0.17 | 0.95 | 6.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 213 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 93.3 | EXPERT | 4 / 4 | 52.0 | 1153 | 240 | 0.00 | 0.90 | 5.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 214 | 6 | 11×11 | 18 | EXCELLENT | KEPT | 93.5 | EXPERT | 4 / 4 | 57.0 | 1474 | 198 | 0.00 | 0.88 | 7.5 | 2 | RESISTANT_TO_ALL_GREEDY |
| 215 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 94.5 | EXTREME | 5 / 4 | 61.9 | 1984 | 281 | 0.13 | 1.00 | 8.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 216 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 94.4 | EXTREME | 5 / 4 | 57.8 | 1678 | 325 | 0.11 | 1.00 | 6.4 | 4 | RESISTANT_TO_ALL_GREEDY |
| 217 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 94.5 | EXTREME | 5 / 4 | 64.6 | 1751 | 230 | 0.11 | 0.95 | 9.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 218 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 94.7 | EXTREME | 5 / 4 | 63.2 | 1709 | 227 | 0.25 | 0.90 | 6.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 219 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 94.8 | EXTREME | 5 / 4 | 64.3 | 1823 | 266 | 0.20 | 0.95 | 8.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 220 | 6 | 11×11 | 21 | EXCELLENT | KEPT | 93.9 | EXPERT | 4 / 4 | 55.0 | 1326 | 213 | 0.00 | 0.84 | 6.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 221 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 94.9 | EXTREME | 5 / 5 | 60.8 | 2140 | 249 | 0.09 | 0.95 | 8.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 222 | 6 | 11×11 | 22 | GOOD | KEPT | 94.5 | EXTREME | 4 / 5 | 52.9 | 1755 | 338 | 0.00 | 0.95 | 5.7 | 2 | RESISTANT_TO_ALL_GREEDY |
| 223 | 6 | 11×11 | 21 | GOOD | KEPT | 94.0 | EXPERT | 4 / 5 | 52.7 | 1029 | 231 | 0.00 | 0.89 | 5.6 | 3 | RESISTANT_TO_ALL_GREEDY |
| 224 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 95.7 | EXTREME | 5 / 5 | 69.2 | 2815 | 241 | 0.33 | 0.85 | 8.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 225 | 6 | 11×11 | 19 | EXCELLENT | KEPT | 95.3 | EXTREME | 5 / 5 | 64.8 | 2499 | 207 | 0.14 | 1.00 | 9.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 226 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 95.3 | EXTREME | 5 / 5 | 65.0 | 1773 | 214 | 0.33 | 0.91 | 6.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 227 | 6 | 11×11 | 20 | GOOD | KEPT | 94.5 | EXTREME | 4 / 5 | 57.4 | 1061 | 214 | 0.10 | 0.94 | 6.3 | 3 | RESISTANT_TO_ALL_GREEDY |
| 228 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 94.6 | EXTREME | 5 / 5 | 54.2 | 1168 | 208 | 0.07 | 1.00 | 5.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 229 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 96.0 | EXTREME | 5 / 5 | 65.7 | 3131 | 311 | 0.08 | 0.95 | 10.2 | 5 | RESISTANT_TO_ALL_GREEDY |
| 230 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 95.0 | EXTREME | 5 / 5 | 57.1 | 1702 | 239 | 0.00 | 0.91 | 7.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 231 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 95.1 | EXTREME | 5 / 5 | 58.6 | 1537 | 219 | 0.09 | 0.95 | 7.0 | 4 | RESISTANT_TO_ALL_GREEDY |
| 232 | 6 | 11×11 | 23 | EXCELLENT | KEPT | 95.7 | EXTREME | 5 / 5 | 64.6 | 1685 | 215 | 0.27 | 1.00 | 7.2 | 6 | RESISTANT_TO_ALL_GREEDY |
| 233 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 95.7 | EXTREME | 5 / 5 | 60.6 | 2097 | 277 | 0.00 | 0.91 | 9.1 | 9 | RESISTANT_TO_ALL_GREEDY |
| 234 | 6 | 11×11 | 19 | EXCELLENT | KEPT | 95.7 | EXTREME | 5 / 5 | 57.7 | 2324 | 252 | 0.00 | 1.00 | 7.8 | 7 | RESISTANT_TO_ALL_GREEDY |
| 235 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 95.6 | EXTREME | 5 / 5 | 62.2 | 1301 | 215 | 0.23 | 0.90 | 6.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 236 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 95.9 | EXTREME | 5 / 5 | 62.4 | 1703 | 259 | 0.30 | 0.90 | 5.9 | 4 | RESISTANT_TO_ALL_GREEDY |
| 237 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 95.8 | EXTREME | 5 / 5 | 58.8 | 1878 | 257 | 0.07 | 0.91 | 7.4 | 8 | RESISTANT_TO_ALL_GREEDY |
| 238 | 6 | 11×11 | 22 | EXCELLENT | KEPT | 95.8 | EXTREME | 5 / 5 | 60.3 | 1450 | 230 | 0.20 | 1.00 | 6.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 239 | 6 | 11×11 | 23 | GOOD | KEPT | 95.7 | EXTREME | 4 / 5 | 55.7 | 1766 | 254 | 0.07 | 1.00 | 6.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 240 | 6 | 11×11 | 24 | EXCELLENT | KEPT | 96.0 | EXTREME | 5 / 5 | 57.0 | 2226 | 266 | 0.00 | 0.86 | 7.5 | 7 | RESISTANT_TO_ALL_GREEDY |
| 241 | 6 | 12×12 | 25 | EXCELLENT | KEPT | 96.4 | EXTREME | 5 / 5 | 61.8 | 2484 | 334 | 0.17 | 0.96 | 8.1 | 6 | RESISTANT_TO_ALL_GREEDY |
| 242 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 95.8 | EXTREME | 5 / 5 | 55.7 | 2017 | 298 | 0.07 | 0.89 | 6.6 | 5 | RESISTANT_TO_ALL_GREEDY |
| 243 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 96.5 | EXTREME | 5 / 5 | 61.3 | 2753 | 314 | 0.14 | 1.00 | 8.2 | 7 | RESISTANT_TO_ALL_GREEDY |
| 244 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 96.2 | EXTREME | 5 / 5 | 56.4 | 2001 | 350 | 0.12 | 1.00 | 6.2 | 4 | RESISTANT_TO_ALL_GREEDY |
| 245 | 6 | 12×12 | 26 | EXCELLENT | KEPT | 96.3 | EXTREME | 5 / 5 | 54.1 | 2559 | 387 | 0.00 | 1.00 | 6.8 | 5 | RESISTANT_TO_ALL_GREEDY |
| 246 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 96.5 | EXTREME | 5 / 5 | 56.4 | 2462 | 429 | 0.08 | 0.92 | 6.7 | 4 | RESISTANT_TO_ALL_GREEDY |
| 247 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 96.6 | EXTREME | 5 / 5 | 57.9 | 2676 | 354 | 0.00 | 1.00 | 8.6 | 8 | RESISTANT_TO_ALL_GREEDY |
| 248 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.2 | EXTREME | 5 / 5 | 66.5 | 3506 | 309 | 0.20 | 0.93 | 9.9 | 8 | RESISTANT_TO_ALL_GREEDY |
| 249 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 96.7 | EXTREME | 5 / 5 | 61.9 | 1880 | 294 | 0.24 | 0.92 | 7.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 250 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 96.9 | EXTREME | 5 / 5 | 58.2 | 2400 | 376 | 0.07 | 1.00 | 7.7 | 7 | RESISTANT_TO_ALL_GREEDY |
| 251 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 96.4 | EXTREME | 5 / 5 | 53.6 | 1689 | 345 | 0.06 | 0.96 | 5.6 | 4 | RESISTANT_TO_ALL_GREEDY |
| 252 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.2 | EXTREME | 5 / 5 | 62.5 | 2551 | 299 | 0.24 | 0.96 | 7.4 | 6 | RESISTANT_TO_ALL_GREEDY |
| 253 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 97.0 | EXTREME | 5 / 5 | 55.7 | 1784 | 426 | 0.13 | 0.93 | 5.7 | 7 | RESISTANT_TO_ALL_GREEDY |
| 254 | 6 | 12×12 | 26 | GOOD | KEPT | 97.0 | EXTREME | 4 / 5 | 55.0 | 2736 | 432 | 0.00 | 0.96 | 7.2 | 3 | RESISTANT_TO_ALL_GREEDY |
| 255 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 97.5 | EXTREME | 5 / 5 | 57.0 | 3348 | 418 | 0.07 | 0.93 | 7.1 | 7 | RESISTANT_TO_ALL_GREEDY |
| 256 | 6 | 12×12 | 30 | GOOD | KEPT | 97.0 | EXTREME | 4 / 5 | 57.6 | 1850 | 399 | 0.07 | 0.96 | 7.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 257 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 96.6 | EXTREME | 5 / 5 | 52.6 | 1460 | 289 | 0.00 | 0.92 | 6.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 258 | 6 | 12×12 | 28 | GOOD | KEPT | 96.8 | EXTREME | 4 / 5 | 52.2 | 2072 | 339 | 0.00 | 1.00 | 5.9 | 3 | RESISTANT_TO_ALL_GREEDY |
| 259 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 97.5 | EXTREME | 5 / 5 | 58.9 | 2549 | 309 | 0.14 | 0.96 | 7.0 | 7 | RESISTANT_TO_ALL_GREEDY |
| 260 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 97.2 | EXTREME | 5 / 5 | 55.5 | 2305 | 311 | 0.00 | 0.96 | 7.5 | 7 | RESISTANT_TO_ALL_GREEDY |
| 261 | 6 | 12×12 | 29 | TOO_SHALLOW | KEPT | 95.9 | EXTREME | 2 / 5 | 57.3 | 74 | 20 | 0.21 | 0.89 | 5.2 | 1 | RESISTANT_TO_ALL_GREEDY |
| 262 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 98.1 | EXTREME | 5 / 5 | 58.8 | 3869 | 458 | 0.09 | 0.92 | 7.7 | 5 | RESISTANT_TO_ALL_GREEDY |
| 263 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.1 | EXTREME | 5 / 5 | 59.7 | 3139 | 390 | 0.19 | 0.93 | 6.7 | 6 | RESISTANT_TO_ALL_GREEDY |
| 264 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.0 | EXTREME | 5 / 5 | 62.1 | 2008 | 314 | 0.30 | 0.96 | 6.3 | 7 | RESISTANT_TO_ALL_GREEDY |
| 265 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 97.8 | EXTREME | 5 / 5 | 61.7 | 1625 | 315 | 0.27 | 0.92 | 6.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 266 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.8 | EXTREME | 5 / 5 | 54.1 | 2484 | 396 | 0.00 | 0.96 | 6.8 | 7 | RESISTANT_TO_ALL_GREEDY |
| 267 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.9 | EXTREME | 5 / 5 | 57.5 | 1853 | 376 | 0.14 | 0.96 | 6.3 | 5 | RESISTANT_TO_ALL_GREEDY |
| 268 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.9 | EXTREME | 5 / 5 | 52.6 | 2444 | 442 | 0.00 | 0.89 | 6.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 269 | 6 | 12×12 | 28 | GOOD | KEPT | 97.9 | EXTREME | 4 / 5 | 59.6 | 1800 | 325 | 0.19 | 0.92 | 6.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 270 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.4 | EXTREME | 5 / 5 | 59.5 | 2548 | 471 | 0.17 | 0.93 | 7.0 | 4 | RESISTANT_TO_ALL_GREEDY |
| 271 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.2 | EXTREME | 5 / 5 | 58.7 | 2177 | 304 | 0.18 | 0.96 | 6.4 | 5 | RESISTANT_TO_ALL_GREEDY |
| 272 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 97.8 | EXTREME | 5 / 5 | 56.0 | 1692 | 270 | 0.11 | 1.00 | 6.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 273 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.2 | EXTREME | 5 / 5 | 57.3 | 1979 | 314 | 0.17 | 0.96 | 5.9 | 4 | RESISTANT_TO_ALL_GREEDY |
| 274 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 98.0 | EXTREME | 5 / 5 | 54.1 | 1826 | 299 | 0.07 | 1.00 | 5.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 275 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.2 | EXTREME | 5 / 5 | 54.7 | 1937 | 370 | 0.07 | 0.96 | 6.0 | 4 | RESISTANT_TO_ALL_GREEDY |
| 276 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.3 | EXTREME | 5 / 5 | 56.7 | 1981 | 312 | 0.07 | 0.96 | 7.1 | 5 | RESISTANT_TO_ALL_GREEDY |
| 277 | 6 | 12×12 | 27 | GOOD | KEPT | 98.6 | EXTREME | 4 / 5 | 54.7 | 2537 | 411 | 0.07 | 1.00 | 6.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 278 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 99.1 | EXTREME | 5 / 5 | 61.7 | 3222 | 369 | 0.13 | 0.89 | 8.5 | 5 | RESISTANT_TO_ALL_GREEDY |
| 279 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 99.3 | EXTREME | 5 / 5 | 58.5 | 4042 | 385 | 0.08 | 1.00 | 7.8 | 5 | RESISTANT_TO_ALL_GREEDY |
| 280 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 98.4 | EXTREME | 5 / 5 | 54.4 | 1616 | 296 | 0.06 | 0.96 | 6.0 | 5 | RESISTANT_TO_ALL_GREEDY |
| 281 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 98.5 | EXTREME | 5 / 5 | 57.0 | 1763 | 289 | 0.14 | 0.93 | 6.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 282 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 99.1 | EXTREME | 5 / 5 | 55.6 | 2430 | 440 | 0.00 | 1.00 | 7.5 | 6 | RESISTANT_TO_ALL_GREEDY |
| 283 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 98.4 | EXTREME | 5 / 5 | 52.7 | 1806 | 291 | 0.00 | 0.93 | 6.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 284 | 6 | 12×12 | 30 | GOOD | KEPT | 98.2 | EXTREME | 4 / 5 | 51.1 | 1069 | 277 | 0.06 | 1.00 | 4.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 285 | 6 | 12×12 | 29 | GOOD | KEPT | 98.5 | EXTREME | 4 / 5 | 53.0 | 1340 | 295 | 0.08 | 0.93 | 5.0 | 3 | RESISTANT_TO_ALL_GREEDY |
| 286 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 99.4 | EXTREME | 5 / 5 | 59.3 | 3155 | 328 | 0.10 | 0.96 | 7.8 | 4 | RESISTANT_TO_ALL_GREEDY |
| 287 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 99.6 | EXTREME | 5 / 5 | 59.5 | 3200 | 362 | 0.09 | 0.89 | 8.0 | 6 | RESISTANT_TO_ALL_GREEDY |
| 288 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 98.9 | EXTREME | 5 / 5 | 56.5 | 1091 | 279 | 0.18 | 0.92 | 5.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 289 | 6 | 12×12 | 27 | EXCELLENT | KEPT | 99.6 | EXTREME | 5 / 5 | 55.2 | 3006 | 380 | 0.00 | 1.00 | 7.3 | 6 | RESISTANT_TO_ALL_GREEDY |
| 290 | 6 | 12×12 | 28 | GOOD | KEPT | 99.1 | EXTREME | 4 / 5 | 53.4 | 1628 | 381 | 0.06 | 0.96 | 5.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 291 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 99.3 | EXTREME | 5 / 5 | 52.7 | 1993 | 363 | 0.00 | 0.96 | 6.1 | 8 | RESISTANT_TO_ALL_GREEDY |
| 292 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 99.7 | EXTREME | 5 / 5 | 56.3 | 2126 | 392 | 0.18 | 0.93 | 5.3 | 4 | RESISTANT_TO_ALL_GREEDY |
| 293 | 6 | 12×12 | 28 | GOOD | KEPT | 99.1 | EXTREME | 4 / 5 | 53.5 | 1843 | 277 | 0.00 | 1.00 | 6.5 | 3 | RESISTANT_TO_ALL_GREEDY |
| 294 | 6 | 12×12 | 26 | EXCELLENT | KEPT | 99.7 | EXTREME | 5 / 5 | 58.5 | 2055 | 346 | 0.09 | 0.96 | 7.5 | 4 | RESISTANT_TO_ALL_GREEDY |
| 295 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 100.0 | EXTREME | 5 / 5 | 59.3 | 2455 | 324 | 0.17 | 0.89 | 6.9 | 6 | RESISTANT_TO_ALL_GREEDY |
| 296 | 6 | 12×12 | 30 | EXCELLENT | KEPT | 100.0 | EXTREME | 5 / 5 | 60.0 | 3414 | 500 | 0.10 | 0.96 | 8.1 | 4 | RESISTANT_TO_ALL_GREEDY |
| 297 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 99.9 | EXTREME | 5 / 5 | 59.0 | 2313 | 281 | 0.15 | 1.00 | 6.9 | 4 | RESISTANT_TO_ALL_GREEDY |
| 298 | 6 | 12×12 | 28 | GOOD | KEPT | 99.8 | EXTREME | 4 / 5 | 56.3 | 2433 | 324 | 0.08 | 0.92 | 6.7 | 3 | RESISTANT_TO_ALL_GREEDY |
| 299 | 6 | 12×12 | 28 | EXCELLENT | KEPT | 100.0 | EXTREME | 5 / 5 | 59.6 | 3663 | 322 | 0.08 | 0.96 | 8.2 | 6 | RESISTANT_TO_ALL_GREEDY |
| 300 | 6 | 12×12 | 29 | EXCELLENT | KEPT | 100.0 | EXTREME | 5 / 5 | 63.5 | 2577 | 380 | 0.29 | 0.96 | 7.0 | 6 | RESISTANT_TO_ALL_GREEDY |
