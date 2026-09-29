# World Progression & Canonical Boundaries

## Canonical 6-World Structure

Zynpath enforces strict, immutable progression rules across 6 canonical worlds totaling 300 base logic levels:

1. **World 1: Learn the Path**
   - Levels: 1–20 (20 levels)
   - Grid Size: 4×4
   - Checkpoints: 4–6
   - Obstacles: 0 walls
   - Unlock Requirement: Unlocked by default (0 previous levels required)

2. **World 2: Longer Connections**
   - Levels: 21–50 (30 levels)
   - Grid Size: 5×5
   - Checkpoints: 4–7
   - Obstacles: 0 walls
   - Unlock Requirement: 10 completed levels in World 1

3. **World 3: Wall Challenge**
   - Levels: 51–100 (50 levels)
   - Grid Size: 5×5
   - Checkpoints: 4–7
   - Obstacles: 1–5 walls
   - Unlock Requirement: 15 completed levels in World 2

4. **World 4: Complex Routes**
   - Levels: 101–150 (50 levels)
   - Grid Size: 6×6
   - Checkpoints: 4–8
   - Obstacles: 2–8 walls
   - Unlock Requirement: 25 completed levels in World 3

5. **World 5: Advanced Logic**
   - Levels: 151–200 (50 levels)
   - Grid Size: 7×7
   - Checkpoints: 4–10
   - Obstacles: 4–12 walls
   - Unlock Requirement: 25 completed levels in World 4

6. **World 6: Expert Path**
   - Levels: 201–300 (100 levels)
   - Grid Size: 8×8
   - Checkpoints: 4–12
   - Obstacles: 6–18 walls
   - Unlock Requirement: 25 completed levels in World 5

---

## Progression Invariants

- **Sequential Unlocking**: Level $L$ within an unlocked world is unlocked if $L = \text{startLevel}$ or if Level $L - 1$ is completed in Room.
- **Replayability**: Completed levels remain 100% playable for replay; star ratings and best times are non-destructively preserved. Replaying already-solved levels does not increment distinct completed counts.
- **Authoritative Source**: Room `ProgressRepository` and domain `WorldConfiguration` maintain single source of truth.

---

## World Completion Achievements & First-Time Celebrations (Prompt 41)

Each of the 6 canonical worlds has an associated full-completion achievement awarded when all levels in that world are solved:
- **World 1**: `world_one_pioneer` (20 levels)
- **World 2**: `world_two_explorer` (30 levels)
- **World 3**: `world_three_wall_breaker` (50 levels)
- **World 4**: `world_four_navigator` (50 levels)
- **World 5**: `world_five_mastermind` (50 levels)
- **World 6**: `world_six_grandmaster` (100 levels)

### First-Time Celebration Deduplication
When all levels in a world are completed, `GameplayViewModel` presents `WorldCompletionCelebrationDialog`. To prevent repetitive celebration spam, acknowledgment writes `world_celebrated_{worldId}` into DataStore `UserPreferences.seenFeatureTips`. Subsequent level replays or navigations will not replay the celebration modal.

---

## Progression Integrity & Automated Verification (Prompt 46)

The progression rules, sequential unlocks, replay invariance, and personal best time behavior are verified in:
- [`WorldProgressionAndReplayIntegrityTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/puzzle/WorldProgressionAndReplayIntegrityTest.kt): Verifies that replaying a solved level never inflates the distinct completed level count, and that faster replays update personal best times while slower replays preserve them.
- See detailed report in [`docs/PROGRESSION_TEST_REPORT.md`](file:///d:/Zynpath/docs/PROGRESSION_TEST_REPORT.md).
