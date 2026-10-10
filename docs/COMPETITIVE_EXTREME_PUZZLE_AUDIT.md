# COMPETITIVE VERY-HARD & EXTREME PUZZLE POOL AUDIT

**Status:** `HUMAN DIFFICULTY VALIDATION: NOT VERIFIED` (Awaiting physical playtest)

## 1. Quick Duel & Friends Arena Pool Design
In accordance with Prompt Sections 22, 26, 28, and 29, competitive matches strictly reject ordinary Solo tutorial and beginner boards (no 4x4 boards).
Competitive difficulty is partitioned into three solver-verified tiers:
- **COMPETITIVE_HARD:** 5x5 boards, 25 playable cells, 3–5 strategic internal edge walls, deceptive checkpoints, multi-stage path reservations.
- **COMPETITIVE_EXPERT:** 6x6 boards, 36 playable cells, 4–6 strategic internal edge walls, deceptive checkpoints, extensive spatial branching.
- **COMPETITIVE_EXTREME:** 6x6 advanced boards, 36 playable cells, 5–7 strategic internal edge walls, 8 deceptive checkpoints, deep wrong branches.
- **FRIENDS_ARENA:** Selected exclusively from the Hard, Expert, and Extreme pools with identical authoritative board instances.

## 2. Competitive Acceptance Gate (Section 29)
| Invariant | Status | Verification Mechanism |
|---|:---:|---|
| 100% Cells Playable | PASS | Every grid cell in (rows × cols) is required (0 boxes, 0 crates, 0 rocks) |
| 100% Cells Covered by Solution | PASS | Verified continuous Hamiltonian path covers exactly rows × cols |
| Only Valid Edge Walls | PASS | Walls exist strictly as BlockedEdges between orthogonal adjacent cells |
| No Corner / U / L Cages | PASS | Verified by WallPlacementStrategy anti-cage predicates |
| Strictly Unique Solution | PASS | Verified by exact DFS Hamiltonian solver (stop on 2nd solution) |
| Simple Strategies Fail | PASS | Verified against 6 baseline heuristics (Greedy, Wall-Followers, Perimeter, Serpentine) |
| Checkpoint Sequence | PASS | Starts at #1, visits 1..N strictly ascending, terminates at #N |

## 3. Verified Competitive Entries

| Pool | Puzzle ID | Grid | Walls | Checkpoints | Decisions | Wrong Branches | Simple Solved | Fingerprint |
|---|---|:---:|:---:|:---:|:---:|:---:|:---:|---|
| HARD | `mp_comp_5x5_hard` | 5x5 | 5 | 6 | 8 | 8 | false | `0c6d75203c35` |
| HARD | `mp_comp_5x5_hard` | 5x5 | 3 | 6 | 11 | 11 | false | `2e633d015a36` |
| EXPERT | `mp_comp_6x6_expert` | 6x6 | 6 | 7 | 14 | 14 | false | `960c4eba5263` |
| EXPERT | `mp_comp_6x6_expert` | 6x6 | 6 | 7 | 15 | 15 | false | `6718c551986e` |
| EXTREME | `mp_comp_6x6_extreme` | 6x6 | 7 | 8 | 13 | 13 | false | `d77d975f1e31` |
| EXTREME | `mp_comp_6x6_extreme` | 6x6 | 6 | 8 | 13 | 15 | false | `b97f17635186` |

## 4. Competitive Fairness & Authority
- **Same Puzzle Guarantee:** The server picks the puzzle deterministically via `seed` and transmits identical `PuzzleAssignment` (puzzleId, version, fingerprint, dimensions, checkpoints, walls) to all match participants.
- **Authoritative Validation:** `ServerPuzzleValidator` validates submitted path on server (coverage, sequence, walls, no revisit). Clients cannot simply report `completed = true`.
- **Competitive Ads & Hints Policy:** No interstitial or rewarded ads are shown during active Quick Duel or Friends Arena matches (Section 32). Competitive hints remain disabled to preserve equal competitive fairness (Section 25).
