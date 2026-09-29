# Zynpath Authoritative Game Rules & Mechanics

**Specification Status:** Strictly Non-Negotiable & Authoritative  
**Domain:** Puzzle Logic, State Validation, and Win Conditions  

---

## 1. Core Concept

**Zynpath: Number Path Puzzle** is a deterministic, continuous number-path logic puzzle played on a rectangular grid. 

The objective is to draw **one unbroken orthogonal path** that begins at checkpoint `1`, visits all numbered checkpoints in strictly ascending order ($1 \to 2 \to 3 \to \dots \to N$), and covers **every required grid cell exactly once**.

---

## 2. The 12 Non-Negotiable Rules

1. **Origin Rule**: The path must originate at numbered checkpoint `1`.
2. **Ascending Order Rule**: Checkpoints must be connected in strictly ascending sequential order ($1 \to 2 \to 3 \dots \to N$).
3. **Orthogonal Movement Only**: Movement is strictly allowed between horizontally or vertically adjacent cells. **Diagonal movement is strictly illegal.**
4. **Single Continuous Path**: The solution must consist of exactly one unbroken, continuous polyline from start to finish.
5. **Full Grid Coverage**: Every required cell on the grid board must be covered **exactly once**.
6. **No Revisit Rule**: Once a cell is covered by the path, it cannot be traversed or entered again.
7. **No Self-Intersection Rule**: The path can never cross over itself, loop across existing lines, or occupy the same cell coordinate twice.
8. **No Wall Crossing Rule**: The path cannot traverse across a blocked edge or wall between adjacent cells.
9. **No Checkpoint Skipping**: Every numbered checkpoint present on the board must be incorporated into the path.
10. **Strict Checkpoint Gate Rule**: A higher numbered checkpoint (e.g., `3`) cannot be visited or passed through before the preceding checkpoint (e.g., `2`) has been successfully reached.
11. **Terminal Condition**: The path must terminate at the highest numbered checkpoint ($N$).
12. **Complete Coverage Completion Requirement**: The puzzle is completed if and only if **both** the checkpoint order is satisfied **and** 100% of required grid cells have been traversed.

---

## 3. Independent Dual Validation

```
                        PUZZLE COMPLETION EVALUATION
                                     │
           ┌─────────────────────────┴─────────────────────────┐
           ▼                                                   ▼
   CHECKPOINT SEQUENCE                                GRID CELL COVERAGE
  (Are 1..N visited in order?)                       (Are 100% cells covered?)
           │                                                   │
     YES   │   NO                                        YES   │   NO
      ┌────┴────┐                                         ┌────┴────┐
      ▼         ▼                                         ▼         ▼
   VALID     INVALID                                   VALID     INVALID
      │                                                   │
      └─────────────────────────┬─────────────────────────┘
                                ▼
                       DO BOTH EQUAL VALID?
                                │
                        YES ────┴──── NO
                         │             │
                         ▼             ▼
                    LEVEL WON     NO WIN YET
```

### Critical Validation Distinctions
- **Condition A (Checkpoint Order)**: If a player draws a direct line connecting $1 \to 2 \to 3 \to \dots \to N$ while leaving empty cells on the board, **THIS IS NOT A WIN**.
- **Condition B (Full Board Coverage)**: If a player covers every cell on the board but enters checkpoint `3` before checkpoint `2`, **THIS IS NOT A WIN**.
- Both conditions are validated independently and must both be `true` for a victory event to be dispatched.
- **Solver Conformance**: The automated exact search solver (`PuzzleSolver`) strictly adheres to these identical 12 rules, pruning branches that violate checkpoint order or wall constraints and ensuring 100% full board coverage. Every candidate route discovered by the solver is validated through `CompletionValidator`.

---

## 4. Visual Path & Coordinate Integrity

The visual path is **not a freehand decorative line** drawn arbitrarily across the screen.

1. **Center-Snapped Orthogonal Geometry**:
   - Each grid cell possesses an exact center coordinate $(x_c, y_c)$.
   - Movement from cell $(r_1, c_1)$ to adjacent cell $(r_2, c_2)$ generates an exact orthogonal segment connecting $(x_{c1}, y_{c1})$ to $(x_{c2}, y_{c2})$.
   - Corner turns are rendered with smooth fillet arcs snapped directly to the cell geometry.
2. **Head of Path Tracking & Backtracking**:
   - The path always has an active "Head" (the latest valid cell).
   - Touch drag gestures are evaluated against the current Head:
     - Dragging to an adjacent legal cell advances the Head and extends the path.
     - Dragging back into the immediately preceding cell retracts the Head (intuitive undo by reverse drawing).
     - Dragging into non-adjacent, already visited, or wall-blocked cells is rejected without corrupting the path.
   - **Premature Final Checkpoint Rejection**: If the player attempts to enter the final checkpoint before 100% of all other required cells have been covered, the move is rejected (`PREMATURE_FINAL_CHECKPOINT`). The path does not enter the final checkpoint and victory is not triggered.
   - **Start-Cell Backtracking Policy**: Retracting the path back to checkpoint 1 preserves a 1-cell starting path at checkpoint 1 (`IN_PROGRESS`). Resetting the board returns the session to `NOT_STARTED`.

---

## 5. Board Elements & Geometry (Domain Models)

| Element | Domain Class | Description | Visual Characteristics |
|---|---|---|---|
| **Grid Cell** | `PuzzleCell` / `GridPosition` | A discrete coordinate `(row, column)` on the board. Zero-indexed. | Light slate background with subtle borders. |
| **Numbered Checkpoint** | `NumberedCheckpoint` | Fixed anchor point containing a number $k \in [1, N]$. | Circular badge with high-contrast numeral. |
| **Active Path** | `PuzzlePath` / `PathSegment` | Ordered sequence of contiguous orthogonal cell steps. | Glowing electric cyan/mint ribbon. |
| **Wall / Edge Barrier** | `BlockedEdge` | An impassable obstacle between two adjacent cells (direction-independent). | Thick crimson bar separating cell borders. |
| **Grid Dimensions** | `GridDimensions` | Board geometry bounds (`rows`, `columns`), max 50x50. | Board layout boundaries. |
| **Void / Inactive Cell** | `PuzzleCell(isRequired=false)` | Non-playable cell for irregular boards (advanced levels). | Darkened or transparent space outside board bounds. |

---

## 6. Prohibited Mechanics & Anti-Patterns

To preserve the cognitive integrity of Zynpath as a pure logic puzzle, the following mechanics are **expressly prohibited**:

- ❌ **No Tile Matching**: No matching 3 identical colors or symbols.
- ❌ **No Tile Clearing / Popping**: Cells do not vanish or explode upon connection.
- ❌ **No Falling Tiles / Gravity**: Elements do not drop downwards when lower cells are emptied.
- ❌ **No Number Merging**: Numbers do not sum, multiply, or merge like "2048".
- ❌ **No Random Tile Refill**: The board configuration is deterministic and fixed per level.
- ❌ **No Combos / Cascade Chains**: No score multiplier cascading chains.

---

## 7. Interactive Tutorial & Onboarding Rule Alignment

To ensure players build accurate mental models, the interactive onboarding tutorial strictly adheres to production puzzle validation:
1. **Identical Engine**: The tutorial runs directly on `PuzzleEngine`. No tutorial-only "fake" rules or contradictory win conditions exist.
2. **Blocked-Edge Wall Clarification**: Walls are explicitly rendered and validated as **blocked boundaries between cells**, never as filled or missing grid tiles. Both cells flanking a wall are fully playable.
3. **Dual Win Condition Enforcement**: Tutorial Stage 4 explicitly trains the full-grid coverage rule, rejecting premature final checkpoint entries with `PREMATURE_FINAL_CHECKPOINT`.
4. **Ascending Order Requirement**: Tutorial Stage 3 enforces numerical sequence ($1 \to 2 \to 3$), rejecting out-of-order jumps with `WRONG_CHECKPOINT_ORDER`.
5. **No Penalty Recovery**: Recovery actions (`Undo` and `Reset`) are taught in Stage 6 with zero score or star penalties.

---

## 8. Automated Verification & QA Test Harness

All 12 canonical rules and dual-validation invariants are exhaustively verified in automated unit test suites:
- [`CanonicalRulesAndBoundaryPropertyTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/puzzle/CanonicalRulesAndBoundaryPropertyTest.kt): Exhaustive combinatorial verification of origin, orthogonal movement, boundary corners, wall symmetry, and zero-revisit constraints.
- [`FoundationalPathValidationTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/puzzle/FoundationalPathValidationTest.kt): Full coverage vs. premature checkpoint order dual-condition validation.
- See detailed QA report in [`docs/PUZZLE_ENGINE_TEST_REPORT.md`](file:///d:/Zynpath/docs/PUZZLE_ENGINE_TEST_REPORT.md).

