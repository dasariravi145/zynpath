# Zynpath Accessible Puzzle Input Architecture

## 1. Overview
Zynpath provides full accessibility for players who cannot use continuous pointer dragging. The puzzle board implements a dual-layer input architecture that enables seamless continuous dragging for touch players while simultaneously exposing a complete virtual accessibility hierarchy for TalkBack, Switch Access, physical keyboards, and D-pads.

---

## 2. Dual-Layer Input Architecture

```
┌────────────────────────────────────────────────────────┐
│               Accessible Virtual Cell Grid             │
│  (Column of Rows with Box per cell, focusable(),        │
│   semantics { onClick = { ... }; contentDescription }) │
├────────────────────────────────────────────────────────┤
│                  Custom Puzzle Canvas                  │
│       (Draws cells, walls, checkpoints, path ribbon)   │
│             + pointerInput (Raw drag gesture)          │
└────────────────────────────────────────────────────────┘
```

1. **Custom Canvas (Underlay):**
   - Renders 60fps path animations, checkpoint pulses, wall edge borders, and cosmetics.
   - Listens to pointer gestures via `pointerInput(PointerEventType.Move)` for smooth continuous dragging.
2. **Virtual Cell Grid (Overlay):**
   - Composed of transparent `Box` elements matching each cell's exact visual bounds.
   - Does NOT capture raw pointer drag events, allowing continuous dragging to pass through to the Canvas.
   - Exposes individual cells to accessibility traversal tools (TalkBack, Braille displays, Switch Access).
   - Handles `onClick` action for accessible cell activation and moves path step-by-step.

---

## 3. Cell Semantic Descriptions
Each cell generates a rich, contextual semantic description via `buildCellAccessibilityDescription()`:

```kotlin
fun buildCellAccessibilityDescription(
    cell: CellState,
    boardState: BoardState,
    currentPath: List<GridPosition>
): String
```

### Components of Description:
1. **Coordinates:** 1-indexed for human communication (e.g., `"Row 2, column 3"`).
2. **Checkpoint Status:** Identified clearly (e.g., `"Checkpoint 1, starting cell"`, `"Checkpoint 3"`, `"Final checkpoint 5"`).
3. **Path Relationship:**
   - `"Current path head (endpoint)"` — the tip of the path where the next move must connect.
   - `"Visited on path"` — cell already traversed.
   - `"Unvisited cell"` — available to be visited.
4. **Wall Edge Annotations:** Explicitly announces blocked edges:
   - `"Blocked by wall edge above, to the right"` (reflecting that walls are edges between cells, not blocked tiles).
5. **Action Guidance:**
   - `"Double tap to start path at Checkpoint 1"`
   - `"Double tap to move path here"`
   - `"Double tap to backtrack"`

---

## 4. Hardware Keyboard & D-Pad Navigation
`PuzzleBoard` attaches `Modifier.onKeyEvent` to provide full keyboard and gamepad navigation:
- **Arrow Keys / D-Pad Directions:**
  - Up (`Key.DirectionUp`), Down (`Key.DirectionDown`), Left (`Key.DirectionLeft`), Right (`Key.DirectionRight`).
  - Move the focused cell position across the grid orthogonally.
- **Action Keys (Select / Move):**
  - Spacebar (`Key.Spacebar`) or Enter (`Key.Enter`) activates the focused cell, sending `onCellEntered(focusedPosition)` to the puzzle engine.
- **Undo Shortcut:**
  - Backspace (`Key.Backspace`) triggers step undo.

---

## 5. Non-Color-Only Feedback
All state changes are conveyed through multiple sensory channels:
- **Move Rejections:**
  - Visual: Text banner with warning icon and descriptive string (e.g., `"Blocked by wall edge"`).
  - Screen Reader: `liveRegion = LiveRegionMode.Polite` automatically announces reason without interrupting critical navigation.
  - Audio: Distinct low error tone (when sound is enabled).
  - Haptics: Gentle double-buzz rejection pattern (when haptics are enabled).
- **Checkpoints:**
  - Visual: Ascending numeral badge + shape glow.
  - Semantic: Announced as `"Checkpoint N"`.
  - Audio/Haptic: Harmonized chime and tick.
- **Victory / Completion:**
  - Visual: Victory dialog with full stats breakdown (time, moves, 100% coverage).
  - Semantic: Explicit announcement `"Level complete! 100% cells covered"`.
  - Only announced after full validation by the server-authoritative engine.

---

## 6. Competitive Fairness & Invariants
- **Engine Identity:** Alternative input modes use the exact same `PuzzleEngine` validation logic as touch dragging.
- **Anti-Cheat & Validation:** No shortcuts, auto-completion, or extra hints are provided. Moves must adhere to orthogonal adjacency, ascending checkpoint order, no edge crossing, and 100% cell coverage.
- **Authoritative Results:** In multiplayer duels and daily challenges, all moves are timestamped and verified against the server-authoritative validator.

---

## 7. Verification Status
- **Virtual Accessibility Grid:** IMPLEMENTED.
- **Semantic Description Builder:** IMPLEMENTED.
- **Keyboard & D-Pad Navigation:** IMPLEMENTED.
- **Non-Color-Only Feedback & LiveRegions:** IMPLEMENTED.
- **Automated Accessibility Testing:** DEFERRED TO FINAL TESTING (Marked NOT VERIFIED).
