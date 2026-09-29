# Zynpath Gameplay UI Architecture

## Overview
The Gameplay UI connects the pure Kotlin puzzle engine (`PuzzleEngine`), verified offline level catalog (`LevelCatalogRepository`), and local progress system (`ProgressRepository`) to an interactive native Jetpack Compose screen (`GameplayShellScreen`).

```
                               ┌───────────────────────────┐
                               │  LevelCatalogRepository   │
                               │  (Verified Offline Assets)│
                               └─────────────┬─────────────┘
                                             │ loadPuzzle(levelId)
                                             ▼
┌─────────────────────────┐    ┌───────────────────────────┐    ┌───────────────────────────┐
│     PuzzleBoard         │◄───┤    GameplayViewModel      ├───►│     ProgressRepository    │
│  (Compose Canvas UI)    │    │ (StateFlow<GameplayUiState)│    │   (Room Offline Storage)  │
└────────────┬────────────┘    └───────────────────────────┘    └───────────────────────────┘
             │                               ▲
             │ pointerInput / Drag           │ onCellEntered() / onUndo()
             └───────────────────────────────┘
```

---

## 1. Unidirectional State Flow

1. **State Ownership**: `GameplayViewModel` owns the active `PuzzleEngine` instance and active `PuzzleGameState`.
2. **UI State Exposure**: The screen observes immutable `StateFlow<GameplayUiState>`.
3. **Engine Authority**: The pure Kotlin engine validates every cell transition. The UI never appends cells or mutates the path independently.

### `GameplayUiState` Contract
- `Loading`: Emitted while catalog asset and progression gate verification are processed.
- `ContentUnavailable`: Emitted when requesting a level whose asset is not yet packaged in the offline catalog.
- `Error`: Emitted on asset corruption, missing level definition, or progression lock.
- `Ready`:
  - `worldId: Int`, `levelId: Int`
  - `definition: PuzzleDefinition`
  - `gameState: PuzzleGameState`
  - `boardState: PuzzleBoardState`
  - `elapsedTimeMs: Long`
  - `isUndoAvailable: Boolean` (true when path length > 1)
  - `isResetAvailable: Boolean` (true when path length > 0)
  - `lastRejectionReason: MoveRejectionReason?`
  - `completionResult: ValidatedCompletionResult?`
  - Derived helpers: `currentOrderedPath`, `currentEndpoint`, `nextRequiredCheckpoint`, `coveredCellCount`, `totalRequiredCells`, `gameStatus`, `isCompleted`.

---

## 2. Board Rendering Pipeline (`PuzzleBoard`)

The puzzle board is rendered in a dedicated Compose `Canvas` inside a responsive `BoxWithConstraints` using geometry from `GridCoordinateMapper`:

1. **Cell Backgrounds & Grid Lines**:
   - Outlines each discrete grid cell with subtle rounded borders (`BoardCellBorder`).
2. **Covered Cell Highlights**:
   - Soft glow tint (`CellCoveredTint`) across all visited cells.
3. **Continuous Path**:
   - Connected path between centers of consecutive cells.
   - Dual-stroke rendering: wide outer cyan glow (`PathCyanSubtle`) and bright solid inner core (`PathCyanGlow`).
   - Smooth `StrokeCap.Round` and `StrokeJoin.Round` corners.
4. **Numbered Checkpoints**:
   - Distinct visual state for **Start #1** (`ForestMint` with glowing halo).
   - Distinct state for **Next Required Checkpoint** (cyan glow halo + active border).
   - Distinct state for **Final Checkpoint #N** (`AccentGold` border).
   - Visited checkpoints render in dark navy with cyan core accents.
   - Typography measured with `TextMeasurer` and centered with sub-pixel precision.
5. **Wall Obstacles (Blocked Edges)**:
   - Drawn exactly along shared cell boundaries with `WallCrimson` and outer glow.
   - Preserves cell corners and does not draw through cell centers.
6. **Path Head Marker**:
   - Pulsing cyan concentric circles indicating the current path endpoint.

---

## 3. Header, Controls & Dialogs

- **Stats Header**:
  - Live formatted elapsed active gameplay time (`00:00`).
  - Personal best comparison (`BEST: 00:00`) when a prior record exists.
  - Cell coverage fraction (`covered / total`).
  - Checkpoint progress (`visited / total`).
- **Header Action Controls**:
  - **Input Mode Toggle**: Compact toggle badge switching between continuous Drag and discrete Tap modes without resetting path or engine state.
  - **Pause Button**: Opens pause overlay, freezes timer, and guarantees Room persistence.
  - **Rules Button**: Displays clean summary of non-negotiable rules.
- **Footer Controls (Min 48dp touch targets)**:
  - **Undo**: Retracts path by 1 step (`PuzzleAction.BacktrackOne`). Clear disabled state when path length <= 1. Unconditionally free.
  - **Hint**: Requests offline, solver-verified next move or recovery guidance. Displays remaining free allowance badge or loading spinner.
  - **Reset**: Prompts confirmation dialog to clear path back to Checkpoint #1 (`PuzzleAction.ResetPath`). Unconditionally free.
- **Move Feedback Area**:
  - Subtle non-intrusive pill badge explaining rejected moves (e.g., "Blocked by wall edge", "Start at Checkpoint #1", "Visit checkpoints in ascending order") without blocking dialogs.
  - Displays hint explanations and dead-end recovery guidance.
- **Victory Overlay (`LevelCompletionDialog`)**:
  - Appears **only** upon validated engine victory (100% coverage + correct checkpoint order ending at #N).
  - Displays actual elapsed time, persisted personal best, moves count, and 100% coverage.
  - Renders a golden `★ NEW PERSONAL BEST! ★` banner when a record is broken.
  - Lightweight 18-particle celebratory burst on Canvas (respects reduced motion).
  - **Next Level**: Resolves next level in catalog, verifies unlock gate and asset availability before navigating, with honest unavailable states.
  - **Replay**: Fresh attempt of the same puzzle identity and version, resetting attempt time while preserving historical records.
  - **Level Selection**: Safely navigates back to world level grid.

---

## 4. Accessibility, Audio & Haptics

- **Alternative Tap-to-Move Mode**:
  - Discrete tap navigation enabling players with motor limitations to tap checkpoint 1 to start, tap adjacent cells to move, and tap predecessor cells to backtrack.
- **Audio Feedback (`SoundFeedbackManager`)**:
  - Native offline `ToneGenerator` playing crisp feedback for checkpoint reached, invalid move, and victory. Zero external media dependencies.
- **Haptic Feedback**:
  - Tactile confirmation on checkpoint reached (`LongPress`), invalid move (`TextHandleMove`), and victory (`LongPress`).
- **Visual & Screen Reader Semantics**:
  - Checkpoints distinguished through non-color cues: double concentric rings, directional pips, inner completion rings, and top diamond crown badges.
  - Full screen reader semantics on board canvas and controls (`Modifier.semantics`).
  - All interactive controls provide a minimum height of **48dp**.
- **Reduced Motion**:
  - Breathing glows, pulsating halos, and celebratory particles disable motion and use clean static opacity when reduced-motion preferences are set.
