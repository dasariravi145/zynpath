# Level Selection UX

## Overview

The Level Selection screen displays the individual level tiles for a selected World. It maps progression data directly from Room (`ProgressRepository`) and catalog definitions (`LevelCatalogRepository`), providing clear visual feedback, replay capabilities, and personal-best tracking.

---

## Level Tile Visual States

Each tile is a 76×76dp touch target with distinct shapes, borders, labels, and icons:

1. **Locked (`LevelState.LOCKED`):**
   - Translucent background with a centered lock icon.
   - Non-playable.
   - Tapping triggers an informative Snackbar feedback: `"Level X is locked. Solve Level X-1 to unlock."`
2. **Current Objective (`isCurrent == true`):**
   - Highlighted with `ForestMint` (#52B788) accent background and 2dp border.
   - Features `"NEXT"` badge.
   - Represents the player's immediate campaign milestone.
3. **Unlocked Available (`LevelState.UNLOCKED`):**
   - Clean dark elevated background with `"PLAY"` badge.
   - Immediate entry into the puzzle board.
4. **Completed (`LevelState.COMPLETED`):**
   - Gold border with 1 to 3 earned stars.
   - Personal best time formatted as `M:SS` or `Ss` (only valid, non-zero recorded times are rendered).
   - Fully replayable without overwriting the player's best record unless a faster or fewer-move completion is achieved.

---

## Grid Architecture & Responsiveness

- **Compact & Standard Phones:** 4 columns in `LazyVerticalGrid`.
- **Landscape Phones & Tablets:** 6 columns, ensuring comfortable spacing without oversized tiles.
- **Header:** Shows world title, grid dimensions, and solved level fraction (e.g., `"5×5 with walls • 18/50 Solved"`).

---

## Accessibility & Semantics

- **TalkBack Descriptions:** Fully localized semantic strings such as:
  - `"Level 14, Completed, 3 stars, Best time 35s"`
  - `"Level 15, Current objective, Available to play"`
  - `"Level 16, Locked"`
- **Non-Color-Only Indicators:** Status is conveyed through icons (Lock vs Star), text badges (`"NEXT"`, `"PLAY"`), and border thickness, ensuring full usability for colorblind players.
