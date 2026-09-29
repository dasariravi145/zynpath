# Zynpath Interactive Gameplay Tutorial

## 1. Engine Integration & Rule Validation
Unlike static slideshows, the Zynpath tutorial runs directly on the authoritative `com.zynpath.game.core.puzzle.engine.PuzzleEngine` and uses the standard `PuzzleBoard` renderer.

### Key Architectural Invariants
1. **Real Puzzle Engine**: Every drag and tap is processed through `PuzzleEngine.process(action)`.
2. **Authoritative Rule Enforcement**: Rejection reasons (`MoveRejectionReason`) directly trigger contextual learning banners (e.g. explaining why diagonal movements are blocked or why the final checkpoint cannot be reached prematurely).
3. **Sandbox Isolation**:
   - Tutorial stages use dedicated `TutorialPuzzles` definitions.
   - Mandatory tutorial guidance does **not** consume production hint allowances (`freeHintsRemaining` or `rewardedHintCredits`).
   - Finishing Stage 7 marks tutorial completion (`isTutorialCompleted = true`) but does **not** silently mark canonical Solo Level 1 as solved in `LevelProgressDao`.

---

## 2. Seven Interactive Stages

| Stage # | Title | Board Dimension | Core Concept Taught | Victory Condition |
|---|---|---|---|---|
| **1** | Start at Checkpoint 1 | 3×3 | The path must always originate at checkpoint #1. | Player touches cell `(0, 0)` (#1). |
| **2** | Continuous Orthogonal Path | 3×3 | Movement is strictly horizontal or vertical; diagonal cuts are invalid. | Player extends path from `(0, 0)` through `(0, 1)` to checkpoint 2 at `(0, 2)`. |
| **3** | Ascending Checkpoint Order | 3×3 | Checkpoints must be visited sequentially (1 → 2 → 3); skipping ahead is rejected. | Player connects checkpoints 1, 2, and 3 in numerical order. |
| **4** | Full-Grid Coverage | 2×3 (6 cells) | Reaching the final checkpoint alone is NOT a victory unless 100% of cells are filled. | Player covers all 6 cells before terminating at final checkpoint 3. |
| **5** | Blocked-Edge Walls | 2×2 (4 cells) | Walls are blocked edges between cells, not blocked tiles. Both cells are playable. | Player routes around the blocked edge between `(0, 0)` and `(1, 0)`. |
| **6** | Mistakes & Recovery | 2×2 with wall | Teaches the real `Undo` and `Reset` controls. | Player taps Undo to step back or Reset to start fresh. |
| **7** | Complete Full Puzzle | 3×3 (9 cells) | Full game integration: start at 1, walls, ascending order, and 100% coverage. | Complete solver-verified 9-cell board ending at checkpoint 4. |

---

## 3. Move Rejection Feedback Mapping

When the player attempts an illegal move, the engine rejects the move with a specific `MoveRejectionReason`. The tutorial UI translates these directly into supportive, non-punitive guidance:

- `START_MUST_BE_CHECKPOINT_ONE`: *"Start at checkpoint 1. Place your finger on 1."*
- `NON_ADJACENT`: *"Diagonal moves are not allowed! Move horizontally or vertically."*
- `WRONG_CHECKPOINT_ORDER`: *"Visit checkpoints in ascending order: 1 → 2 → 3. Do not skip numbers!"*
- `PREMATURE_FINAL_CHECKPOINT`: *"100% grid coverage required! Cover all cells before reaching the final checkpoint."*
- `BLOCKED_BY_WALL`: *"Blocked by a wall! Walls are edge barriers. Route around the barrier."*
- `CELL_ALREADY_VISITED`: *"Cannot cross your own path. Each cell can only be visited once."*

---

## 4. Accessibility & Alternative Text Guide Mode
- **Dual Display Modes**: Players can toggle between the interactive canvas board and the full text guide (`TutorialTextGuide`) at any time.
- **Screen Reader Support**: Each cell, checkpoint state, wall boundary, and status message exposes descriptive Compose semantics.
- **Large Touch Targets**: Minimum 48dp interactive touch targets for all undo, reset, next, and back controls.
- **High Contrast & Reduced Motion**: Automatically adapts to theme preferences and system motion-reduction settings.

---

## 5. Audio & Haptic Feedback in Tutorial (Prompt 34)
- **Coherent Sound Language**: The tutorial uses the exact same sound effects (`PATH_START`, `VALID_MOVE`, `CHECKPOINT_REACHED`, `INVALID_MOVE`, `UNDO`, `RESET`, `PUZZLE_COMPLETED`) as the main game, avoiding contradictory audio behaviors.
- **Supportive Tactile Cues**: Invalid moves trigger restrained double-pulse haptics and horizontal board oscillation (unless `isReducedMotion == true`), reinforcing why a move was disallowed without punitive buzzing.
- **Preference Obedience**: Respects player settings; players who prefer a silent or vibration-free learning experience can toggle audio and haptics off freely.

---

## 6. TalkBack & Alternative Input in Tutorial (Prompt 39)
- **Virtual Cell Grid Overlay**: The tutorial board exposes each cell individually to TalkBack and Switch Access with rich descriptions of row/column, checkpoint state, and blocked wall edges. Players do not need continuous drag gestures to complete tutorial stages.
- **Keyboard Navigation**: Arrows / D-pad move cell focus; Spacebar / Enter activates moves; Backspace triggers Undo.
- **Polite Live Regions**: Feedback banners attach `liveRegion = LiveRegionMode.Polite` so move rejections and stage completion guidance are read aloud clearly.


