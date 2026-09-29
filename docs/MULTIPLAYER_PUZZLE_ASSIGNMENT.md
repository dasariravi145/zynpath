# Zynpath Multiplayer Puzzle Assignment & Solvability

## 1. Overview
In Zynpath competitive multiplayer (Quick Duel, Friend Duel, and Mini League), competitive integrity mandates that **every participant in a match receives the exact same, solver-verified puzzle assignment**.

---

## 2. Solver-Verified Puzzle Pool
Puzzles are selected by `MultiplayerPuzzlePool` from pre-verified puzzle definitions:
- **Dimensions**: 4x4, 5x5, 6x6 square grids.
- **Continuous Path Rules**: A verified Hamiltonian continuous path covering 100% of required cells, starting at Checkpoint 1, visiting intermediate checkpoints in strictly ascending numerical order (1 -> 2 -> ... -> N), and ending at checkpoint N without revisiting cells or crossing blocked edges.
- **Unverified Puzzles Prohibited**: Unverified runtime-generated puzzles or puzzles that timed out in the solver are strictly barred from multiplayer pools.

---

## 3. Puzzle Fingerprinting
Each puzzle assignment carries a deterministic SHA-256 fingerprint computed across:
- Grid width and height
- Sorted list of required cell coordinates (e.g., `["0,0", "0,1", ...]`)
- Sorted checkpoint assignments (e.g., `["1:0,0", "2:1,2", ...]`)
- Sorted blocked edges (e.g., `["0,0-0,1", ...]`)

```
fingerprint = SHA256(width + ":" + height + ":" + sortedRequiredCells + ":" + sortedCheckpoints + ":" + sortedBlockedEdges)
```

The Android client verifies this fingerprint upon receiving the puzzle definition. If the fingerprint mismatches, the match is aborted with `PUZZLE_INVALID`.

---

## 4. Immutability Across Reconnection
- Once assigned to a match session, the puzzle assignment is permanently fixed.
- The puzzle is never regenerated during:
  - Client screen rotation
  - Process backgrounding
  - Network interruption and reconnect
  - Participant leave/rejoin
