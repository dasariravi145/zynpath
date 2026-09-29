# World Map UX & Progression Overview

## Overview

The Zynpath World Map provides an interactive, tactile representation of the campaign's 6 canonical worlds and 300 base logic levels. It adheres strictly to immutable progression invariants and provides clear feedback for unlocked, in-progress, completed, and locked regions.

---

## Canonical World Invariants

Progression parameters are hardcoded and validated across the catalog, Room database, and UI presentation:

| World | Name | Levels | Grid Dimensions | Checkpoint Range | Wall Obstacles | Unlock Requirement |
|---|---|---|---|---|---|---|
| **World 1** | Learn the Path | 1–20 | 4×4 | 4–6 | 0 walls | Unlocked by default |
| **World 2** | Longer Connections | 21–50 | 5×5 | 4–7 | 0 walls | 10 solved in World 1 |
| **World 3** | Wall Challenge | 51–100 | 5×5 | 4–7 | 1–5 walls | 15 solved in World 2 |
| **World 4** | Complex Routes | 101–150 | 6×6 | 4–8 | 2–8 walls | 25 solved in World 3 |
| **World 5** | Advanced Logic | 151–200 | 7×7 | 4–10 | 4–12 walls | 25 solved in World 4 |
| **World 6** | Expert Path | 201–300 | 8×8 | 4–12 | 6–18 walls | 25 solved in World 5 |

---

## World Card Visual States

1. **Unlocked & In Progress:**
   - World number in circular badge with world accent color.
   - World name, grid size description, and level range.
   - Progress bar indicating exact completion percentage (`X / Y Completed (Z%)`).
   - "WALLS" badge displayed on Worlds 3, 5, and 6.
   - Play action button routing to Level Selection for that world.
2. **Completed:**
   - Gold accent styling with `COMPLETE` badge and checkmark.
   - 100% progress bar in `AccentGold`.
   - Displays total stars and full completion count.
3. **Locked:**
   - Muted background styling (`BackgroundCard` alpha 0.5) with lock icon.
   - Clear unlock requirement text (e.g., `"Requires 15 levels solved in World 2 (12/15)"`).
   - Tapping a locked world presents an immediate, non-intrusive Snackbar message rather than silently dropping the touch event.

---

## Responsive Layout

- **Phone Portrait:** Single-column scrolling list (`LazyColumn`) with spaced cards.
- **Landscape & Tablets:** 2-column grid (`LazyVerticalGrid(columns = GridCells.Fixed(2))`) preventing card horizontal stretching and preserving comfortable readability.
- **Header:** Features total stars counter and back navigation conforming to the single back-stack architecture.

---

## QA Verification Status (Prompt 48)
- **Six-World Configuration & Invariants:** **PASSED** (Verified in `HomeScreenAndWorldMapComprehensiveTest.testWorldMapDisplaysAllSixWorldsWithAccurateInvariants()`).
- **Dynamic Lock/Unlock Progression Gates:** **PASSED** (World 1 unlocked initially; Worlds 2–6 locked until respective completion thresholds are reached).
- **Navigation Transitions:** **PASSED** (Level selection and gameplay transitions preserve backstack integrity).
