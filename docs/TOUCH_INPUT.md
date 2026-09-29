# Zynpath Touch Input & Coordinate Mapping

## Overview
Touch input in Zynpath translates physical finger gestures on the screen into validated engine actions.

---

## 1. Grid Coordinate Mapping (`GridCoordinateMapper`)

`GridCoordinateMapper` provides deterministic geometry conversion between screen pixel offsets and 2D discrete `GridPosition(row, column)`.

### Formulas
Given canvas dimensions (`canvasWidth`, `canvasHeight`), grid size (`rowCount`, `columnCount`), and padding:
- `availableWidth = max(0, canvasWidth - 2 * padding)`
- `availableHeight = max(0, canvasHeight - 2 * padding)`
- `cellSize = min(availableWidth / columnCount, availableHeight / rowCount)`
- `boardWidth = cellSize * columnCount`
- `boardHeight = cellSize * rowCount`
- `originX = padding + (availableWidth - boardWidth) / 2`
- `originY = padding + (availableHeight - boardHeight) / 2`

### Mappings
1. **Pixel to Cell (`offsetToGridPosition(offset)`)**:
   - Bounds check: `offset.x` in `[originX, originX + boardWidth]` and `offset.y` in `[originY, originY + boardHeight]`.
   - Outside board returns `null`.
   - Inside:
     - `column = floor((offset.x - originX) / cellSize)`
     - `row = floor((offset.y - originY) / cellSize)`
2. **Cell to Center (`getCellCenter(position)`)**:
   - `x = originX + (position.column + 0.5) * cellSize`
   - `y = originY + (position.row + 0.5) * cellSize`
3. **Cell Bounds (`getCellBounds(position)`)**:
   - `Rect(originX + col * cellSize, originY + row * cellSize, ...)`

---

## 2. Touch Drawing & Drag Handling (`puzzleTouchInput`)

Using Compose pointer input with `awaitEachGesture` and `awaitFirstDown`:

1. **Touch Down**:
   - When the user touches checkpoint 1, `PuzzleAction.StartPath` is dispatched.
   - Touching any other cell before starting is rejected (`START_MUST_BE_CHECKPOINT_ONE`). The board does not start and does not teleport.
2. **Dragging**:
   - As the finger moves, new entered cells are detected.
   - If the cell is the predecessor cell in the path, intuitive single-step drag backtracking is triggered (`PuzzleAction.BacktrackTo`).
   - If the cell is an earlier path cell, multi-cell prefix truncation is dispatched.
   - If the cell is unvisited and adjacent, forward path extension is dispatched (`PuzzleAction.ExtendPath`).
   - Repeated events for the same cell are ignored.
3. **Touch Release / Cancel**:
   - Clean gesture termination without orphaned touch states.

---

## 3. Fast Finger Movement Handling

When a pointer skips intermediate pixels or cells between event frames:
- `GridCoordinateMapper.resolveIntermediatePath(from, to)` evaluates the trajectory.
- **Horizontal Straight Lines**: Resolved into ordered sequence of intermediate cells along that row.
- **Vertical Straight Lines**: Resolved into ordered sequence of intermediate cells along that column.
- **Diagonal or Ambiguous Turns**: Returns `null`. The jump is safely ignored rather than teleporting across blocked edges or skipping checkpoints.
- Each intermediate step is validated individually through `PuzzleEngine`. Traversal stops immediately at the first rejected transition.

---

## 4. Alternative Tap-to-Move Input Mode (Prompt 15 Section 26)

To provide an accessible alternative for players who find continuous dragging difficult, Zynpath includes a dedicated discrete **Tap Mode**:

1. **Tap Checkpoint #1 to Start**: Initiates the path via `PuzzleAction.StartPath`.
2. **Tap Adjacent Cell to Extend**: Tapping any orthogonally adjacent unvisited neighbor dispatches `PuzzleAction.ExtendPath`.
3. **Tap Predecessor Cell to Backtrack**: Tapping the cell immediately preceding the current endpoint retracts the path by 1 step.
4. **Tap Earlier Cell to Retract to Branch**: Tapping an earlier position on the path truncates the path back to that cell.
5. **No Rule Duplication**: Tap gestures route to the exact same [PuzzleEngine] rules pipeline as drag inputs.
6. **Persistence & Mode Switching**: Toggleable on the gameplay screen via a header button and persisted in DataStore (`tap_input_mode`). Switching modes does not alter engine state or reset the puzzle.

---

## 5. Pointer Release & Session Flushing

When the player lifts their finger from the board in either Drag or Tap mode:
- `onPointerReleased` callback is triggered.
- Any pending debounced session updates are immediately flushed to Room storage.
- Ensures the active path snapshot is durable without incurring disk I/O on every single pointer motion frame.
