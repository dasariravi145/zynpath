# Zynpath Level Completion Experience

## Overview
This document specifies the architecture, validation gates, presentation, and navigation behavior of the level completion experience in Zynpath: Number Path Puzzle (Phase 3, Prompt 15).

---

## 1. Validated Victory Condition

Victory is confirmed **exclusively** by the authoritative pure Kotlin `PuzzleEngine` and `CompletionValidator`:

1. **Cell Coverage**: Every playable cell on the grid must be covered (100% cell coverage: `coveredCellCount == totalRequiredCells`).
2. **Checkpoint Order**: All numbered checkpoints 1 through $N$ must have been visited in strictly ascending numerical order.
3. **Endpoint Requirement**: The path must end precisely at the highest-numbered checkpoint ($N$). Reaching the final checkpoint early with unvisited cells remaining is **NOT** victory.
4. **Edge Boundaries**: No blocked edge (wall) may have been crossed.
5. **No Diagonal Moves**: All steps must be strictly orthogonal.

UI layers and Compose rendering code never fabricate or prematurely trigger victory.

---

## 2. Personal Best Tracking

Upon validated victory, `GameplayViewModel` evaluates the attempt against persisted historical progress in Room:

1. **Duration Precision**: Measured via the monotonic `GameplayTimer` (based on elapsed nano/millisecond duration, unaffected by system wall-clock adjustments or time zone changes).
2. **Comparison Logic**:
   - If no prior completion exists: `isNewPersonalBest = true`.
   - If prior `bestTimeMs <= 0L`: `isNewPersonalBest = true`.
   - If `finalTimeMs < priorBestTimeMs`: `isNewPersonalBest = true`.
   - Otherwise: `isNewPersonalBest = false`.
3. **Display**:
   - When a new personal best is established, the completion dialog displays a golden celebratory badge: `★ NEW PERSONAL BEST! ★`.
   - Both current attempt time and best time are shown side-by-side using actual persisted database values.
   - Incomplete attempts are never recorded or compared as personal bests.

---

## 3. Celebratory Presentation

1. **Lightweight Animation**:
   - An entrance animation triggers once when victory is validated.
   - A celebratory particle burst renders 18 floating particles in theme colors (Cyan, Mint, Gold, White) drifting outward and fading smoothly over 900ms.
   - Non-blocking and lightweight with zero external image or video dependencies.
2. **Recomposition Safety**:
   - State is tied to `isCompletionHandled` and `LaunchedEffect(Unit)` within the dialog, preventing repeated celebration triggers during UI recompositions.
3. **Reduced Motion Adaptation**:
   - When `isReducedMotion` is active, particle movement is omitted in favor of a clean, static golden banner.

---

## 4. Navigation Actions

The modal completion overlay provides three distinct actions:

### A. Next Level
The Next Level action executes a strict 5-step sequence:
1. **Confirm Completion Persistence**: The current level's completion is written and committed to Room via `ProgressRepository.recordValidatedCompletion`.
2. **Resolve Next Level in Catalog**: Query `catalogRepository.getLevel(levelId + 1)`. If `levelId + 1 > 300`, display a congratulatory completion state.
3. **Confirm Unlocked**: Check `progressRepository.isLevelUnlocked(levelId + 1)` (automatically unlocked upon completion of the preceding level).
4. **Confirm Asset Packaging**: Verify the puzzle JSON asset exists and passes integrity checks via `catalogRepository.loadPuzzle(levelId + 1)`.
5. **Navigate**: Navigate to the next level using its stable numerical `worldId` and `levelId`.
*Error State*: If content is pending future catalog release, an honest dialog informs the player without crashing or substituting a fake puzzle.

### B. Replay
- Replays the exact same level ID, puzzle ID, and puzzle version.
- Resets the active attempt: path is cleared, timer is reset to zero, and a fresh active session snapshot is initialized.
- Preserves all historical completion records and personal bests in Room.
- Does not regenerate or alter the catalog puzzle asset.

### C. Level Selection
- Closes the completion overlay and safely returns the player to the Level Selection grid.

---

## 5. Deferred Testing

Comprehensive verification of Room completion migrations, rapid next-level traversal benchmarks, and physical device memory profiles during extended play sessions will be executed during final testing prompts.
