# Zynpath Progressive Feature Discovery & World Introductions

## 1. Contextual Discovery Strategy
To avoid overwhelming first-time players with exhaustive menus, Zynpath unlocks and introduces features **contextually** when the player encounters them for the first time.

All feature introductions are recorded in `PreferencesRepository` via `seenFeatureTips: Set<String>`. Once dismissed, tips are never shown repeatedly.

---

## 2. World Introductions (Worlds 1–6)

### World 1: Learn the Path (Levels 1–20)
- **Grid Size**: 4×4 (16 cells)
- **Checkpoints**: 4–6 numbered checkpoints
- **Walls**: None
- **Focus**: Foundational continuous path traversal, ascending checkpoint order, and 100% cell coverage.

### World 2: Longer Connections (Levels 21–50)
- **Grid Size**: 5×5 (25 cells)
- **Checkpoints**: 4–7 numbered checkpoints
- **Walls**: None
- **Introductory Tip**: *"Expanding the Grid: Larger 5×5 boards require deeper route foresight and longer continuous lines."*

### World 3: Wall Challenge (Levels 51–100)
- **Grid Size**: 5×5 (25 cells)
- **Checkpoints**: 4–7 numbered checkpoints
- **Walls**: 1–5 interior blocked edges
- **Introductory Modal**:
  - Explains that walls are **blocked edges between cells**, not blocked tiles. Both cells on either side remain playable, but direct crossing is blocked.
  - Features an explicit **Replay Wall Tutorial** action that routes the player directly into Stage 5 of the interactive tutorial without disturbing their campaign progress.

### World 4: 6×6 Labyrinth (Levels 101–150)
- **Grid Size**: 6×6 (36 cells)
- **Checkpoints**: 4–8 numbered checkpoints
- **Walls**: 2–8 interior blocked edges
- **Introductory Tip**: *"Multi-corridor routing in 36-cell grids. Watch out for isolated corners."*

### World 5: 7×7 Grand Maze (Levels 151–200)
- **Grid Size**: 7×7 (49 cells)
- **Checkpoints**: 4–10 numbered checkpoints
- **Walls**: 4–12 interior blocked edges
- **Introductory Tip**: *"Tight wall corridors and complex checkpoint sequences."*

### World 6: 8×8 Zenith (Levels 201–300)
- **Grid Size**: 8×8 (64 cells)
- **Checkpoints**: 4–12 numbered checkpoints
- **Walls**: 6–18 interior blocked edges
- **Introductory Tip**: *"The maximum difficulty path puzzle challenge with 64 playable cells."*

---

## 3. Game Mode Feature Discovery

1. **Daily Challenge**:
   - Triggered when entering Daily Challenge for the first time.
   - Explains that the puzzle is shared globally across all players for 24 hours, playable offline, and verifiable on the online daily leaderboard.
2. **Friends & Social**:
   - Triggered upon opening Friends.
   - Introduces the player's unique Zynpath ID and shareable invitations for private 1v1 duels.
3. **Quick Duel (1v1)**:
   - Explains real-time matchmaking, identical shared puzzle seeds, and that competitive hints are disabled to guarantee fair competition.
4. **Friend Duel**:
   - Explains private room codes and direct links to challenge friends on matching boards.
5. **Mini League**:
   - Explains 2–5 player party lobbies with cumulative tournament standings.
6. **Zynpath Premium**:
   - Discovered contextually in the Store or Profile.
   - Highlights honest utility: Ad-free, unlimited Solo hints, exclusive cosmetics, and personal analytics. Explicitly clarifies that Premium provides **zero competitive advantage** in duels.
7. **Rewarded Ads for Solo Hints**:
   - Offered only when a free player runs out of Solo hint allowances.
   - Completely optional with zero forced ads.
