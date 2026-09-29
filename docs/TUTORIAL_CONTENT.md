# Zynpath Tutorial Content & Verified Layouts Specification

## 1. Content Versioning
- **Current Version**: `TUTORIAL_CONTENT_VERSION = 1`
- **Location**: `com.zynpath.game.core.puzzle.model.TutorialPuzzles`
- All tutorial puzzle definitions are bundled locally and execute with zero network requirements.

---

## 2. Mathematical Verification of Tutorial Stages

### Stage 1: "Start at Checkpoint 1"
- **Puzzle ID**: `tutorial_stage_1`
- **Grid Size**: 3×3 (9 cells)
- **Checkpoints**:
  - `#1` at `(0, 0)`
  - `#2` at `(0, 2)`
  - `#3` at `(2, 2)`
- **Walls**: None
- **Objective**: Initiate the path at checkpoint #1.
- **Verification**: Engine verifies that `currentPath.head == (0, 0)`.

---

### Stage 2: "Continuous Orthogonal Path"
- **Puzzle ID**: `tutorial_stage_2`
- **Grid Size**: 3×3 (9 cells)
- **Checkpoints**: Same as Stage 1
- **Path Preset**: Starts at `(0, 0)`.
- **Target Route**: `(0, 0)` → `(0, 1)` → `(0, 2)` (#2).
- **Rule Verification**: Moving diagonally (e.g. `(0, 0)` to `(1, 1)`) is rejected by `PuzzleEngine` with `NON_ADJACENT`.

---

### Stage 3: "Ascending Checkpoint Order"
- **Puzzle ID**: `tutorial_stage_3`
- **Grid Size**: 3×3 (9 cells)
- **Checkpoints**: `#1` at `(0, 0)`, `#2` at `(0, 2)`, `#3` at `(2, 2)`
- **Target Route**: Connect 1 → 2 → 3.
- **Rule Verification**: Attempting to move into checkpoint #3 before visiting checkpoint #2 triggers `WRONG_CHECKPOINT_ORDER`.

---

### Stage 4: "Full-Grid Coverage"
- **Puzzle ID**: `tutorial_stage_4`
- **Grid Size**: 2×3 (6 cells)
- **Checkpoints**:
  - `#1` at `(0, 0)`
  - `#2` at `(0, 2)`
  - `#3` at `(1, 0)` (Final)
- **Unique Complete Solution**:
  `(0, 0)`[#1] → `(0, 1)` → `(0, 2)`[#2] → `(1, 2)` → `(1, 1)` → `(1, 0)`[#3]
- **Rule Verification**: Attempting to connect directly from `(0, 0)` or `(1, 1)` into final checkpoint `(1, 0)` while uncovered cells remain triggers `PREMATURE_FINAL_CHECKPOINT`. Reaching checkpoint 3 alone does not grant victory.

---

### Stage 5: "Blocked-Edge Walls"
- **Puzzle ID**: `tutorial_stage_5`
- **Grid Size**: 2×2 (4 cells)
- **Checkpoints**:
  - `#1` at `(0, 0)`
  - `#2` at `(1, 0)` (Final)
- **Walls**: `BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0))`
- **Unique Complete Solution**:
  `(0, 0)`[#1] → `(0, 1)` → `(1, 1)` → `(1, 0)`[#2]
- **Rule Verification**: Direct downward step from `(0, 0)` to `(1, 0)` is blocked with `BLOCKED_BY_WALL`. The player must route around the barrier edge. Both cells are playable!

---

### Stage 6: "Mistakes & Recovery"
- **Puzzle ID**: `tutorial_stage_6`
- **Grid Size**: 2×2 with wall
- **Objective**: Use `Undo` to take back a step or `Reset` to clear the board.
- **Verification**: Handled via `PuzzleAction.Undo` and `PuzzleAction.Reset` within `InteractiveTutorialViewModel`.

---

### Stage 7: "Complete Full Puzzle"
- **Puzzle ID**: `tutorial_stage_7`
- **Grid Size**: 3×3 (9 cells)
- **Checkpoints**:
  - `#1` at `(0, 0)`
  - `#2` at `(0, 2)`
  - `#3` at `(1, 0)`
  - `#4` at `(2, 2)` (Final)
- **Walls**:
  - `(0, 0)` | `(1, 0)`
  - `(1, 1)` | `(2, 1)`
- **Unique Complete Solution**:
  `(0, 0)`[#1] → `(0, 1)` → `(0, 2)`[#2] → `(1, 2)` → `(1, 1)` → `(1, 0)`[#3] → `(2, 0)` → `(2, 1)` → `(2, 2)`[#4]
- **Solver Status**: Mathematically proven single-path solution visiting all 9/9 cells, all 4 checkpoints in order, with zero wall crossings.
