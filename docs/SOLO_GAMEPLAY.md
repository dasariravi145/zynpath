# Zynpath Solo Gameplay Engine & Premium Solo Packs Specification

## 1. Overview
The Zynpath Solo Gameplay Engine provides the core single-player experience for both the free campaign (Worlds 1–6) and optional Premium Solo puzzle packs. It is built upon pure Kotlin domain models (`android.*`-free), a high-performance Jetpack Compose Canvas renderer (`PuzzleBoard`), and monotonic active-session timing (`GameplayTimer`).

---

## 2. Canonical Puzzle Rules
All puzzles—free campaign, Daily Challenge, and Premium packs—strictly adhere to the canonical rules of Zynpath:
1. **Start Checkpoint**: The path must begin at checkpoint #1.
2. **Sequential Checkpoints**: Numbered checkpoints must be reached in strictly ascending order (1 → 2 → 3...).
3. **Orthogonal Movement**: Traversal is strictly orthogonal (up, down, left, right). Diagonal moves and multi-cell jumps are rejected.
4. **100% Cell Coverage**: Every required playable cell on the board must be visited exactly once.
5. **No Self-Intersection**: Cells cannot be revisited during forward movement. Dragging backward over the path triggers intuitive backtracking.
6. **Edge Walls**: Movement across blocked edges (walls) is strictly obstructed.
7. **Final Checkpoint Victory Condition**: Entering the final checkpoint early is invalid. Victory requires 100% board coverage upon arrival at the final checkpoint.

---

## 3. Solo Hint Policy & Rewarded Ads (Prompt 29)
- **Solver-Aware Hints**: Hints are computed asynchronously off the main thread using `PuzzleHintEngine`. Hints never fabricate guidance when the solver cannot establish a valid continuation.
- **Next Move Guidance**: Highlights the next verified orthogonal move toward complete coverage.
- **Dead-End Detection**: When a path cannot lead to a valid solution, `PuzzleHintEngine` calculates the exact number of rewind steps required to recover a solvable state.
- **Consumption Hierarchy**:
  1. Active Premium subscribers: Unlimited Solo hints with zero ads.
  2. Standard free daily allowance: Consumed first when available (default 3 hints).
  3. Rewarded hint credits: Consumed next when standard free allowance is exhausted.
- **Optional Rewarded Videos**: Free players can optionally watch a short video to earn +1 Solo hint (up to 5 rewarded ads per calendar day, max 10 stored credits).
- **Non-Forced Experience**: Ad viewing is 100% voluntary; Undo and Reset remain completely free and unlimited for all players.
- **Competitive Proscription**: In competitive multiplayer modes (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`, competitive `DAILY_CHALLENGE`), hints are strictly prohibited for all players at the policy layer.

---

## 4. Premium Solo Puzzle Packs Integration (Prompt 27)
- **Engine Reuse**: Premium packs run on the exact same `PuzzleEngine`, `PuzzleHintEngine`, `PuzzleBoard`, and `GameplayTimer`. No secondary or altered engine is introduced.
- **Separate Progression**: Pack progress is tracked independently in Room (`PremiumPackProgressEntity`) with level indices, completion status, and personal best solve times.
- **Offline Playability**: Once downloaded and authorized, packs remain playable offline for up to 30 days under the bounded entitlement cache policy.
- **Zero Pay-to-Win**: Premium packs provide additional single-player puzzles and have zero effect on competitive matchmaking, duel puzzle difficulty, or leaderboard rankings.

---

## 5. Solo Progression Analytics & World Tracking (Prompt 30)
- **Canonical 300 Levels**: Free campaign progression strictly enforces the 300-level catalog structure:
  - World 1: Levels 1–20 (4x4 grids, 0 walls)
  - World 2: Levels 21–50 (5x5 grids, 0 walls)
  - World 3: Levels 51–100 (5x5 grids, 1–3 walls)
  - World 4: Levels 101–150 (6x6 grids, 2–4 walls)
  - World 5: Levels 151–200 (6x6 grids, 3–6 walls)
  - World 6: Levels 201–300 (8x8 grids, 4–8 walls)
- **Denominators**: Solo campaign completion percentages strictly use a fixed denominator of 300. Premium packs are tracked and reported in isolated models (`PremiumPackAnalytics`).
- **Personal Bests & Time Averages**: Calculated solely from non-zero completion times (`bestTimeMs > 0`). Historical attempts without recorded times are preserved as completed without corrupting average solve metrics.

---

## 6. Onboarding, Tutorial and Solo Campaign Separation (Prompt 33)
- **Zero Level 1 Auto-Completion**: Finishing the 7-stage interactive tutorial sets `isTutorialCompleted = true` in DataStore preferences, but does **not** insert or update Level 1 progress in `LevelProgressDao`. Solo Level 1 must be legitimately played and solved in the campaign.
- **Hint Independence**: Mandatory tutorial guidance is sandboxed and never deducts from the player's 3 free daily Solo hints (`freeHintsRemaining`) or stored rewarded credits (`rewardedHintCredits`).
- **World 3 Wall Introduction**: Upon navigating into World 3 (Levels 51–100) for the first time, a contextual modal introduces interior wall edge mechanics and offers a "Replay Wall Tutorial" shortcut directly into Stage 5 of the tutorial.
- **Replayability**: The tutorial can be replayed at any time via Settings or Home without resetting Solo campaign level completion, stars, or personal best times.

---

## 7. Audio, Haptic & Interaction Polish (Prompt 34)
- **Gameplay Audio Cues**:
  - `PATH_START`: Resonant light chime when beginning from checkpoint 1.
  - `VALID_MOVE`: Soft wooden click on every accepted cell transition (throttled to 40ms).
  - `CHECKPOINT_REACHED`: Resonant bell chime when sequentially advancing to the next required checkpoint.
  - `INVALID_MOVE`: Low-frequency thud on wall collision, diagonal attempt, or cell revisit (throttled to 200ms).
  - `UNDO` & `RESET`: Subtle backtrack clicks on single step or full board clear.
  - `HINT_USED`: Gentle shimmer upon rendering a solver recommendation.
  - `PUZZLE_COMPLETED`: Triumphant 3-note celebration chime upon engine-verified full grid coverage.
- **Haptic Tactile Feedback**:
  - Throttled discrete pulses accompany path start, checkpoint arrival, invalid moves, undo, and completion.
- **Board Rejection Shake**:
  - Invalid moves trigger horizontal damped oscillation (`rejectionShake`), bypassed when `isReducedMotion == true`.

---

---

## 9. Performance Optimization & Smooth Rendering (Prompt 37)
- **Zero Heap Churn Canvas**: Preallocated `Path` and `sharedDiamondPath` eliminate garbage collection pauses during continuous path drag interactions.
- **Draw-Phase Animation Deferral**: Pulse animations are read exclusively inside the Compose draw pass (`Canvas { ... }`), preventing the entire puzzle screen from recomposing at 60/120Hz.
- **Whole-Second UI Tickers**: HUD elapsed-time updates emit state copies strictly on whole-second transitions, reducing recompositions by 90% while keeping solve completion millisecond-accurate.
- **Intermediate Move Interpolation**: Fast drag gestures decompose multi-axis swipes into bounded Manhattan orthogonal steps, preventing missed intermediate cells during rapid play.
- **In-Memory Catalog Caching**: Asset validity checks and loaded puzzle definitions are memoized to avoid repeated disk reads and JSON parsing.

---

## 10. Crash Recovery, Session Resumption & Durability (Prompt 38)
- **Commit-Before-Celebration**: When a puzzle is solved, `GameplayViewModel` commits the level completion to Room (`level_progress`) before transitioning UI state to completed/celebration. If the app is killed or crashes during celebration animations, the completion is already durably saved.
- **Durable Snapshot Resumption**: In-flight moves and elapsed timer states are periodically stored in `game_sessions`. Before resuming, snapshots undergo full validation (puzzle identity, version, ascending checkpoints, orthogonal steps, no revisits, no wall crossings).
- **Corrupted Snapshot Safety**: If a snapshot fails validation, the corrupted snapshot is deleted and the user is provided with a fresh level start. Existing completed level records and personal bests are never affected.
- **Home Screen "Continue Your Path"**: Players can directly resume their latest unfinished level from a dedicated card on the Home screen.
- **100% Offline Solo Continuity**: Solo gameplay has zero dependence on network connectivity or backend reachability. An active backend outage or flight mode has zero impact on Solo play.

---

## 11. Accessible Puzzle Input, TalkBack Semantics & Responsive Layouts (Prompt 39)
- **Dual-Layer Board**: Seamless continuous dragging for touch players, combined with a transparent virtual cell grid overlay exposing individual cells to TalkBack and Switch Access with rich 1-indexed coordinate, checkpoint, path head, and wall edge descriptions.
- **Hardware Keyboard & D-Pad Controls**: Physical arrow keys move focus orthogonally across the board; Spacebar/Enter executes the move; Backspace undos.
- **Alternative Tap-to-Move**: Single-tap step-by-step navigation without requiring continuous dragging gestures.
- **Polite Live Regions**: Move rejections and hints announce politely via `LiveRegionMode.Polite` without interrupting navigation.
- **Two-Region Landscape**: Switches to a side-by-side layout (Board left, stats & controls right) preserving board square aspect ratio and eliminating control clipping.
- **Tablet & Compact Scaling**: Constrains portrait width on tablets (560dp) to prevent button stretching; compresses vertical padding to 4dp on compact phones (<640dp height).

---

## 12. Milestone Celebrations, Personal Bests & Achievement Evaluation (Prompt 41)
- **Automatic Achievement Evaluation**: `ProgressRepositoryImpl.recordValidatedCompletion` automatically triggers asynchronous evaluation of `AchievementRepository.evaluateAll()` upon every verified level completion.
- **Personal Best Recognition**:
  - `LevelCompletionDialog` displays `★ NEW PERSONAL BEST! ★` if and only if a previous valid record existed (`existingBest > 0`) and the new solve time is strictly faster (`finalTimeMs < existingBest`).
  - Times are compared strictly against identical level IDs.
- **World Completion First-Time Celebrations**:
  - Completing the final level of any of the 6 canonical worlds triggers `WorldCompletionCelebrationDialog`.
  - DataStore deduplication (`world_celebrated_{worldId}`) ensures the celebration dialog is shown only once and does not repeat upon level replays or re-navigation.

---

## 13. Solo Session & Timer QA Verification (Prompt 46)

Solo session creation, restoration, timer accuracy, and hint safety are verified in:
- [`SoloSessionAndTimerComprehensiveTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/puzzle/session/SoloSessionAndTimerComprehensiveTest.kt): Verifies session snapshot persistence, corrupted/diagonal snapshot recovery, and monotonic duration freezing during app backgrounding.
- [`HintEngineSafetyAndEntitlementTest`](file:///d:/Zynpath/android/app/src/test/java/com/zynpath/game/core/puzzle/hint/HintEngineSafetyAndEntitlementTest.kt): Verifies that hints derive exclusively from valid solutions and never suggest diagonal or wall-crossing moves.
- See detailed reports in [`docs/PUZZLE_ENGINE_TEST_REPORT.md`](file:///d:/Zynpath/docs/PUZZLE_ENGINE_TEST_REPORT.md) and [`docs/OFFLINE_PERSISTENCE_TEST_REPORT.md`](file:///d:/Zynpath/docs/OFFLINE_PERSISTENCE_TEST_REPORT.md).

---

## 14. Comprehensive End-to-End Gameplay QA (Prompt 48)
- **Continuous Gesture & Precision Hit-Testing**: Verified in `SoloGameplayE2EInteractionTest.kt`. Continuous finger drag maps to discrete coordinates without jitter or phantom steps.
- **Rule Enforcement & Dual-Win Requirement**: Rejection of diagonal moves, wall collisions, out-of-order checkpoints, and self-intersections verified. Victory triggers only upon 100% cell coverage and sequential checkpoints.
- **Undo and Reset**: Verified that undo pops the last move and rolls back both visual path and engine state cleanly.
- **Session Continuity**: Verified timer freezing and atomic persistence during backgrounding and configuration changes (`GameplayLifecycleAndInterruptionTest.kt`).
- **Comprehensive Documentation**: See [docs/END_TO_END_GAMEPLAY_REPORT.md](file:///d:/Zynpath/docs/END_TO_END_GAMEPLAY_REPORT.md).

