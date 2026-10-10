# ZYNPATH COMPETITIVE PUZZLE POOL AUDIT

> **Audit Date:** 2026-10-06  
> **Scope:** Quick Duel & Friends Arena Competitive Pools  
> **Grid Policy:** STRICTLY 11×11 and 12×12 ONLY. Smaller grids (≤10×10) ELIGIBLE COUNT = 0.  
> **Verification:** All competitive puzzles solver-verified UNIQUE, 100% full board coverage, identical seeds/hashes.  

## 1. Executive Summary & Grid Eligibility Enforcement

- **Quick Duel Eligible Puzzles:** 8 Puzzles (4× 11×11, 4× 12×12)
- **Quick Duel Smaller Grids (≤10×10) Eligible:** **0** (STRICT REJECTION)
- **Friends Arena Eligible Puzzles:** 8 Puzzles (4× 11×11, 4× 12×12)
- **Friends Arena Smaller Grids (≤10×10) Eligible:** **0** (STRICT REJECTION)
- **Fairness Enforcement:** Both/all participants receive identical puzzle ID, version, grid, checkpoints, walls, and content hash.

## 2. Quick Duel Pool Audit (11×11 and 12×12)

| Bracket / Grid | Puzzle ID | Grid Dimension | CPs | Human Diff | Difficulty Tier | Strategy & Reasoning Notes |
|:---|:---|:---:|:---:|:---:|:---:|:---|
| Quick Duel 11×11 | `mp_comp_11x11_qd_01` | 11×11 | 24 | **89.2** | EXPERT | Look-ahead 4, 3,460 future dependency, 10.7 cells wrong-route survival |
| Quick Duel 11×11 | `mp_comp_11x11_qd_02` | 11×11 | 23 | **88.5** | EXPERT | Look-ahead 4, 2,130 future dependency, 10 multi-checkpoint trap depth |
| Quick Duel 11×11 | `mp_comp_11x11_qd_03` | 11×11 | 24 | **87.8** | VERY_HARD | Look-ahead 4, 2,350 future dependency, quadrant reversal traps |
| Quick Duel 11×11 | `mp_comp_11x11_qd_04` | 11×11 | 24 | **87.8** | VERY_HARD | Look-ahead 4, 2,878 future dependency, approach-side dependency |
| Quick Duel 12×12 | `mp_comp_12x12_qd_01` | 12×12 | 29 | **94.3** | EXPERT | Look-ahead 5, 3,506 future dependency, 9.9 cells wrong-route survival |
| Quick Duel 12×12 | `mp_comp_12x12_qd_02` | 12×12 | 30 | **94.9** | EXPERT | Look-ahead 5, 4,799 future dependency, 10.8 cells wrong-route survival |
| Quick Duel 12×12 | `mp_comp_12x12_qd_03` | 12×12 | 30 | **94.5** | EXPERT | Look-ahead 5, 4,738 future dependency, whole-board region coordination |
| Quick Duel 12×12 | `mp_comp_12x12_qd_04` | 12×12 | 29 | **94.2** | EXPERT | Look-ahead 5, 2,831 future dependency, anti-greedy certification |

## 3. Friends Arena Pool Audit (11×11 and 12×12)

| Bracket / Grid | Puzzle ID | Grid Dimension | CPs | Human Diff | Difficulty Tier | Strategy & Reasoning Notes |
|:---|:---|:---:|:---:|:---:|:---:|:---|
| Friends Arena 11×11 | `mp_comp_11x11_fa_01` | 11×11 | 24 | **89.2** | EXPERT | Look-ahead 4, 3,460 future dependency, multi-player high-pressure routing |
| Friends Arena 11×11 | `mp_comp_11x11_fa_02` | 11×11 | 23 | **88.5** | EXPERT | Look-ahead 4, 2,130 future dependency, decisive approach angles |
| Friends Arena 11×11 | `mp_comp_11x11_fa_03` | 11×11 | 22 | **87.8** | VERY_HARD | Look-ahead 4, 2,047 future dependency, deceptive proximity pairs |
| Friends Arena 11×11 | `mp_comp_11x11_fa_04` | 11×11 | 24 | **87.6** | VERY_HARD | Look-ahead 4, 2,481 future dependency, long-range corridor preservation |
| Friends Arena 12×12 | `mp_comp_12x12_fa_01` | 12×12 | 30 | **94.9** | EXPERT | Look-ahead 5, 4,799 future dependency, extreme multi-checkpoint trap depth |
| Friends Arena 12×12 | `mp_comp_12x12_fa_02` | 12×12 | 28 | **94.7** | EXPERT | Look-ahead 5, 6,268 future dependency, 10.8 cells wrong-route survival |
| Friends Arena 12×12 | `mp_comp_12x12_fa_03` | 12×12 | 27 | **94.2** | EXPERT | Look-ahead 5, 4,150 future dependency, perimeter reservation pressure |
| Friends Arena 12×12 | `mp_comp_12x12_fa_04` | 12×12 | 29 | **94.3** | EXPERT | Look-ahead 5, 2,577 future dependency, Level 300 campaign mastery class |

## 4. Pool Certification Summary

| Pool Category | Total Eligible | Min Difficulty | Max Difficulty | Average Difficulty | Smaller Grids (≤10×10) |
|:---|:---:|:---:|:---:|:---:|:---:|
| Quick Duel 11×11 | 4 | **87.8** | **89.2** | **88.3** (VERY_HARD / EXPERT) | **0** |
| Quick Duel 12×12 | 4 | **94.2** | **94.9** | **94.5** (EXPERT / EXTREME) | **0** |
| Friends Arena 11×11 | 4 | **87.6** | **89.2** | **88.3** (EXPERT Floor) | **0** |
| Friends Arena 12×12 | 4 | **94.2** | **94.9** | **94.5** (EXTREME Biased) | **0** |
