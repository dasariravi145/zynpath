# ZYNPATH — LEVEL EXPERIENCE ARCHITECTURE & PROGRESSION FOUNDATION
**Phase 25–32 Architecture & Handoff Document**
**Document Date:** September 28, 2026  
**Phase:** Level Experience & Progression Enhancement (Prompts 25–32)

---

## 1. ORIGINAL GAMEPLAY INVARIANTS

The core Number Path puzzle mechanics are authoritative, locked, and inviolable across all 300 levels:

1. **Strict Ascending Sequence:** Checkpoints numbered $1, 2, \dots, N$ must be visited in exact ascending numerical order. No checkpoints may be skipped, reordered, or visited out of sequence.
2. **Total Cell Coverage:** Every playable, non-excluded cell on the board must be filled by the valid path. Incomplete board coverage ($\text{visited.size} < \text{totalRequiredCells}$) is **NOT** a victory.
3. **Simple Path (No Cell Revisit):** No cell may be visited more than once. Self-intersecting loops and revisited cells are rejected.
4. **Manhattan-Orthogonal Adjacency:** Valid movement consists solely of single-step orthogonal steps ($\Delta x = \pm 1, \Delta y = 0$ or $\Delta x = 0, \Delta y = \pm 1$). Diagonal jumps, multi-axis moves, and knight-moves are strictly invalid.
5. **No Wall Crossings:** Movements across blocked edges (walls) are strictly forbidden.
6. **Authoritative Origin & Terminus:** The path must begin at Checkpoint #1 and conclude precisely at the maximum numbered checkpoint ($N$).
7. **Independence from UI/Visual Elements:** Decorative rocks, background canvas elements, or empty spaces shown in visual themes do not alter the playable graph. Only cells defined in `PuzzleDefinition.requiredCells` are valid.
8. **Backward Compatibility:** All existing level IDs ($1 \dots 300$) and stored player progress remain 100% valid and compatible.

---

## 2. EXISTING PUZZLE-ENGINE INTEGRATION

The Level Experience architecture operates strictly as a presentation and progression layer on top of the existing core engine:

- **`CompletionValidator.kt`:** Remains the single, authoritative validator executing all 8 verification checks before awarding victory. Victory is never granted based on UI state or simple path length.
- **`PuzzleEngine.kt`:** Executes pure state transitions (`StartPath`, `ExtendPath`, `BacktrackTo`, `ResetPath`) producing immutable `PuzzleGameState` snapshots.
- **`PuzzleHintEngine.kt`:** BFS-based solver computing guaranteed-valid next moves or recovery directions without revealing invalid paths.
- **`LevelExperienceMetadata.kt`:** Pure domain metadata complementing `LevelDefinition` with chapter information, difficulty tiers, and milestone definitions without duplicating or modifying the puzzle engine.

---

## 3. CURRENT 300-LEVEL DATA STRUCTURE

The 300 levels are currently organized as follows:

| World ID | Name | Level Range | Grid Size | Walls | Theme Accent |
| :---: | :--- | :---: | :---: | :---: | :---: |
| **World 1** | Learn the Path | 1–20 | 4×4 | 0 | Forest Mint (`#52B788`) |
| **World 2** | Longer Connections | 21–50 | 5×5 | 0 | Accent Blue (`#4361EE`) |
| **World 3** | Wall Challenge | 51–100 | 5×5 | 1–5 | Wall Crimson (`#E63946`) |
| **World 4** | Complex Routes | 101–150 | 6×6 | 2–8 | Accent Purple (`#7209B7`) |
| **World 5** | Advanced Logic | 151–200 | 7×7 | 4–12 | Accent Gold (`#FFB703`) |
| **World 6** | Expert Path | 201–300 | 8×8 | 6–18 | Success Green (`#10B981`) |

- **Catalog Storage:** Defined via `CatalogManifest.kt`, `LevelDefinition.kt`, and `PackagedPuzzles.kt` (representative verified level definitions), with offline puzzle asset JSON files located at `puzzles/w{worldId}/lvl{levelId}.json`.
- **Unlock Logic:** Handled by `WorldConfiguration.isLevelUnlocked(levelId, completedLevelIds)` and `WorldConfiguration.isWorldUnlocked(worldId, completedLevelIds)`.

---

## 4. SEVEN PROPOSED PROGRESSION CHAPTERS

To provide rich narrative pacing, memorable milestones, and visual variety, the 300 levels are grouped into seven presentation chapters (defined in `ProgressionPlan.kt`):

| Chapter # | Chapter Title | Level Range | Experience Focus | Primary Difficulty | Milestone Levels |
| :---: | :--- | :---: | :--- | :---: | :--- |
| **1** | **First Steps** | 1–25 | Introduction to flow, 4×4 zero-wall grids, establishing muscle memory. | INTRODUCTORY | 1 (Journey Begins), 13 (Midpoint), 25 (Chapter Climax) |
| **2** | **Smart Turns** | 26–50 | 5×5 boards, deliberate corner routing, spatial foresight. | CASUAL | 38 (Midpoint), 50 (Chapter Climax) |
| **3** | **Path Explorer** | 51–100 | Wall introduction, labyrinth navigation, detour planning. | BALANCED | 75 (Midpoint), 100 (Century Mark) |
| **4** | **Strategic Paths** | 101–150 | 6×6 topologies, multi-checkpoint spanning, forward budgeting. | STRATEGIC | 125 (Midpoint), 150 (Chapter Climax) |
| **5** | **Expert Journey** | 151–200 | 7×7 grids, dense wall chokepoints, high precision routing. | ADVANCED | 175 (Midpoint), 200 (Bicentennial Master) |
| **6** | **Master Trails** | 201–250 | 8×8 boards, deceptive shortcuts, long-horizon sequence planning. | EXPERT | 225 (Midpoint), 250 (Chapter Climax) |
| **7** | **Grand Challenge** | 251–300 | 8×8 boards, maximal wall density, ultimate test of endurance. | MASTER | 275 (Midpoint), 300 (Grand Pathmaster Finale) |

---

## 5. LEVEL METADATA MODEL

Located at `android/app/src/main/java/com/zynpath/game/core/puzzle/experience/LevelExperienceModel.kt`:

- **`LevelExperienceMetadata`:**
  - `levelId: Int` (1..300)
  - `chapterId: Int` (1..7)
  - `chapterTitle: String` ("First Steps", "Smart Turns", etc.)
  - `displayTitle: String` ("Level X")
  - `difficultyCategory: ExperienceDifficultyCategory` (7 distinct categories)
  - `puzzleId: String` (matches catalog asset reference)
  - `worldId: Int` (preserves 1..6 world progression)
  - `themeReference: ExperienceThemeReference`
  - `milestoneIndicator: MilestoneIndicator?` (milestone metadata when applicable)
  - `defaultFreeHints: Int = 2` (locked policy)
  - `completionPresentation: CompletionPresentationMetadata` (fanfare tier, sparkle particle count)

---

## 6. TWO-FREE-HINT POLICY

Locked economic requirements implemented in `LevelHintState.kt` and `LevelHintRepository.kt`:

1. **Per-Level Allocation:** Each level provides exactly **two free hints** (`freeHintsTotal = 2`).
2. **Exhaustion Boundary:** Once both free hints are consumed for a given level, any additional hint requires an optional confirmed rewarded video ad.
3. **Non-Negative Balances:** Free hints remaining is strictly bounded by `[0, 2]`. Consuming when at 0 returns `LevelHintConsumptionResult.RequiresRewardedAd`.
4. **Per-Level Independence:** Consuming free hints on Level 1 does not deplete the free hint allowance of Level 2.

---

## 7. REWARDED-AD HINT CONTRACT

Locked ad-reward contract implemented in `RewardedHintAdContract.kt`:

1. **One Completion = One Hint:** Exactly one confirmed rewarded-ad completion unlocks one additional hint credit for the active level.
2. **Completion Proof Required:** Hints are **never** awarded merely because an ad was requested, loaded, or opened.
3. **Explicit Denial Handling:** No hint credit is awarded for:
   - `RewardedHintAdResult.Skipped` (user dismissed before reward threshold)
   - `RewardedHintAdResult.Failed` (playback/network error)
   - `RewardedHintAdResult.Cancelled` (user aborted ad presentation)
   - `RewardedHintAdResult.Unavailable` (no ad inventory)
   - `RewardedHintAdResult.MissingCallback` (ad closed without provider confirmation)
4. **Idempotent Claim Processing:** `LevelHintRepository` tracks claimed transaction IDs to prevent duplicate crediting of a single ad completion.

---

## 8. GUEST PROGRESS PRESERVATION

To guarantee zero regression and zero loss of existing guest progress on test devices:

1. **No Destructive Database Migrations:** Level hint state and chapter progression metadata use DataStore key-value mappings (`lvl_hint_free_used_{id}`, etc.) rather than modifying Room tables.
2. **Stable Identifiers:** Level IDs ($1 \dots 300$), World IDs ($1 \dots 6$), and progress mappings in Room `level_progress` remain untouched.
3. **Non-Destructive Execution Protocol:** All update operations strictly prohibit `adb uninstall` or `pm clear`. Existing Guest Solo completion state is fully preserved.

---

## 9. PHASE 25–32 STATUS & ROADMAP

- **Prompt 25 (Level Experience Architecture & Progression Foundation):** COMPLETED. Implemented `LevelExperienceMetadata`, `ProgressionPlan` (7 Chapters), locked two-free-hint policy (`LevelHintRepository`), rewarded-ad hint contracts (`RewardedHintAdContract`), and UI milestone presentation components.
- **Prompt 26 (Difficulty-Controlled Puzzle Variety & Valid Level Generation):** COMPLETED. Implemented `ChapterLevelGenerator`, `ChapterLevelConfigFactory`, `LevelSeedGenerator`, `DeterministicLevelProvider`, multi-dimensional difficulty scoring, 26 recovery levels, near-duplicate sliding window rejection, and solvability validation via `CompletionValidator` and `PuzzleHintEngine`.
- **Prompt 27 (Curated Level Variety & Quality Gates):** Deep curation and asset compilation for Chapters 1–3, bespoke milestone handcrafted levels, and catalog expansion.
- **Prompt 28 (Dedicated Rewarded Ad Integration):** Production AdMob SDK adapter for `RewardedHintAdContract`, network error fallbacks, and verification.
- **Prompt 29 (Atmospheric Theming & Biome Renders):** Biome canvas shaders, chapter atmospheric lighting, and distinct visual grid treatments across Chapters 1–7.
- **Prompt 30 (Audio & Haptics Polish):** Distinct milestone fanfare audio, directional haptic feedback on turns, victory audio cues.
- **Prompt 31 (Adventure Map & World Selection Polish):** Integrating Chapter cards, milestone waypoints, and animated progress trails into `WorldSelectionScreen` and `LevelSelectionScreen`.
- **Prompt 32 (Comprehensive Audit, Build, Tests & APK Verification):** Full validation pass, automated test execution, Debug APK generation, and release-readiness verification.

---

## 10. PROMPT 26 GENERATOR ARCHITECTURE & PIPELINE

The level generation engine in `ChapterLevelGenerator.kt` functions as an authoritative, multi-stage pipeline guaranteeing solvability and gameplay fidelity:

1. **Deterministic Configuration Derivation (`ChapterLevelConfigFactory`):**
   - Resolves target dimensions (4×4 to 8×8), checkpoint count, wall budget, and route style based on level ID (1..300) and chapter assignment.
   - Computes a stable 64-bit seed using `LevelSeedGenerator`.
2. **Candidate Solution Construction (`RouteConstructor`):**
   - Builds a valid, non-self-intersecting Hamiltonian-style path that covers 100% of the playable board cells using orthogonal Manhattan steps only.
3. **Wall & Checkpoint Placement (`WallPlacementStrategy`, `CheckpointPlacementStrategy`):**
   - Places walls along non-path edges without disconnecting the solution route.
   - Distributes numbered checkpoints along the solution path in strictly ascending order ($1 \dots N$).
4. **Authoritative Completion Validation (`CompletionValidator`):**
   - Re-evaluates the candidate using the project's authoritative 8-point validator to ensure no invariant is violated.
5. **Near-Duplicate Detection (`PuzzleSimilarityCalculator`):**
   - Compares the candidate against the last 5 generated levels using normalized similarity. Candidates with $\ge 85\%$ structural and checkpoint similarity to recent neighbors are rejected.
6. **Multi-Dimensional Difficulty Profiling (`PuzzleDifficultyAnalyzer`):**
   - Evaluates topological features, turn frequency, choice points, and solver metrics, assigning a verified `DifficultyBand`.
7. **Hint Engine Compatibility Verification (`PuzzleHintEngine`):**
   - Runs `PuzzleHintEngine.computeHint` on the initial puzzle state to ensure the hint engine reliably finds the opening move without error.
8. **Final Acceptance & Fingerprint Caching:**
   - Registers the canonical D4-symmetry fingerprint in `globalFingerprints` and stores the verified puzzle and solution in `DeterministicLevelProvider`.

---

## 11. MEASURABLE DIFFICULTY & RECOVERY LEVEL PACING

Difficulty is measured objectively across five dimensions rather than relying solely on grid size:

- **Playable Cell Count:** Total cells required to be filled (16 in 4×4 up to 64 in 8×8).
- **Checkpoint Density & Spacing:** Number of numbered clues relative to total cells. Fewer clues require deeper mental route projection.
- **Path Geometry & Turn Frequency:** Count and distribution of directional turns along the valid path.
- **Topological Chokepoints (Walls):** Number and placement of blocked edges forcing serpentine detours.
- **Solver Search Tree Depth:** Exploration branch factor and node expansions required to prove the solution.

### Recovery / Breather Level Design

To avoid relentless cognitive fatigue, 26 designated recovery levels are spaced strategically after milestone and climax puzzles:
- **Designated Levels:** 7, 14, 21, 28, 35, 42, 60, 70, 85, 95, 110, 120, 135, 145, 160, 170, 185, 195, 210, 220, 235, 245, 260, 270, 285, 295.
- **Recovery Characteristics:** Higher checkpoint count (+2 to +4 clues), lower wall count, and organic flow patterns that offer a fast, satisfying, confidence-rebuilding solve.

---

## 12. DUPLICATE & NEAR-DUPLICATE REJECTION

Repetitive puzzle layouts are prevented through two complementary mechanisms:

1. **Global Canonical Fingerprinting (`PuzzleFingerprint`):**
   - Hashes checkpoint positions and wall placements normalized across all 8 dihedral symmetries (rotations and reflections).
   - Exact and rotated duplicates are permanently rejected.
2. **Neighboring Level Sliding Window:**
   - Maintains a sliding window of the last 5 accepted puzzle definitions (`NEIGHBOR_WINDOW_SIZE = 5`).
   - Uses `PuzzleSimilarityCalculator.calculateSimilarity` to assess topological overlap.
   - Candidates with $\ge 85\%$ similarity to any recent neighbor are rejected, forcing diverse path topologies across consecutive levels.

---

## 13. STABLE DETERMINISTIC LEVEL IDENTITY

Levels must remain completely identical across app restarts, configuration changes, and screen recompositions:

- **SplitMix64 Seeding (`LevelSeedGenerator`):**
  - Uses an avalanche-mixing algorithm on the level ID ($1 \dots 300$) with a catalog salt.
  - Generates the exact same puzzle on every request without persisting large generated board states in databases.
- **Deterministic Level Provider (`DeterministicLevelProvider`):**
  - Injected as a `@Singleton` via `AppModule`.
  - Caches loaded levels in memory.
  - Seamlessly returns pre-packaged authored puzzles from `PackagedPuzzles` when available, falling back to deterministic runtime generation on demand.
  - Guest progress and Room database completion records are untouched.

---

## 14. SOLVER & UNIQUENESS LIMITATIONS

- **Uniqueness Verification:**
  - Uniqueness is strictly validated for grids up to 6×6 (Chapters 1–4) within solver node limits.
  - For large 7×7 and 8×8 grids (Chapters 5–7), exhaustive uniqueness checking is bounded by `solverTimeBudgetMs` (4,000ms) and `solverNodeBudget` (100,000 nodes) to prevent UI freezes.
  - If a 7×7 or 8×8 candidate cannot be fully proven unique within the budget, its status is recorded as `UniquenessStatus.AMBIGUOUS` while still guaranteeing at least one fully verified valid solution passing `CompletionValidator`.

---

## 15. PROMPT 27 — PREMIUM INTERACTIVE PUZZLE BOARD & MULTIPLE NUMBER CONNECTIONS

### 15.1 Presentation & Board Aesthetics
- **Zynpath Color Identity:**
  - Deep navy background: `#0B132B` (`GameDeepNavy`).
  - Royal-blue board surface: `#1C2541` (`GameRoyalBlue` / `#162038`).
  - Electric-cyan active path: `#00F0FF` (`GameElectricCyan` / `PathCyanCore`).
  - Gold numbered-clue accents: `#FFD700` (`GameGoldHighlight` / `AccentGold`).
  - Subtle cyan cell borders: `#00F0FF` at 18% alpha with 8dp rounded-rect corners.
  - Cell Clue Typography: Bold numbers with contrast dropshadows and outer gold halos. Current target clue features a gentle radial breathing glow.

### 15.2 Multiple Number-to-Number Segments
- **Continuous Path Invariant:** All connections remain strictly part of **ONE single continuous path** in `PuzzleEngine`. No separate, disjointed, or branching path data structures exist.
- **Visual Segment Differentiation:**
  - `PuzzleBoard.kt` partitions the continuous path into segments delimited by numbered clues ($1 \to 2$, $2 \to 3$, etc.).
  - **Completed Segments:** Rendered with a crystallized electric cyan glow and core line.
  - **Active In-Progress Segment:** Rendered with an animated, pulsing cyan halo leading to the current path head.
  - **Directional Chevrons:** Drawn along path segment segments indicating forward flow direction.
  - **Number Connection Trail (`NumberConnectionTrail`):** Top interactive progress indicator displaying numbered clue badges connected by horizontal trail segments:
    - Visited checkpoints show a gold ring with green checkmark (`✓`).
    - Next target checkpoint displays an active pulsing gold border.
    - Future checkpoints remain muted cyan.
    - Completed segments show a solid gold line; pending segments show a dashed muted track.

### 15.3 Touch Interaction & Drag Desync Prevention
- **Corner Drag Resolution (`GridCoordinateMapper.resolveIntermediatePath`):**
  - Resolves diagonal swipes and cornering gestures into two strictly orthogonal sub-steps based on dominant touch displacement ($|\Delta x| > |\Delta y|$), preventing dropped cell entries or invalid diagonal cuts.
- **Authoritative Drag Synchronization (`PuzzleTouchInput.kt`):**
  - `lastCell` updates **only** when `onCellEntered(step)` returns `true` (accepted by `PuzzleEngine`). If an intermediate step is rejected (e.g. wall, visited cell), the pointer does not desync from the engine's true endpoint.
- **Backtracking:** Dragging back over the immediate predecessor smoothly retracts the path, decrementing coverage and restoring clue targets.

### 15.4 Progress Calculation & Incomplete Coverage Clarity
- **Authoritative Progress Indicator:**
  - Compact display showing `coveredCellCount / totalRequiredCells cells filled` and percentage coverage.
  - **Clarity Rule:** Reaching the final numbered clue does **not** grant victory if empty playable cells remain. The dynamic status guidance pill explicitly alerts:
    *"All clues reached! Cover remaining X cells to complete."*

### 15.5 Two Free Hints & Rewarded Video UI
- **Two Free Hints Display:**
  - Action button clearly displays remaining free hints: `"Hint (2 Free)"`, `"Hint (1 Free)"`, `"Hint (+X)"` (rewarded), or `"Watch Ad (+1)"`.
- **Rewarded Video Entry State:**
  - When all hints are exhausted, `showLimitReachedDialog` provides the prompt:
    **"Watch Ad — Get 1 Hint"**.
  - If ads are not loaded or network is disconnected, provides honest feedback:
    **"Ad Unavailable — Try Later"** without blocking the player or closing gameplay.
  - Idempotent claim verification: `LevelHintRepository.recordConfirmedReward` registers unique `claimId`s, preventing duplicate hint rewards.

---

## 16. PROMPT 28 — CURATED FIRST 50 LEVELS & ENGAGING EARLY PROGRESSION

### 16.1 First-50 Progression Structure
The first 50 levels of the solo campaign establish a smooth learning curve and satisfying cognitive progression across two presentation chapters:

| Level Range | Experience Sub-Band | Grid Size | Checkpoints | Progression Objective |
| :---: | :--- | :---: | :---: | :--- |
| **1–5** | Learn the path | 4×4 | 5–6 | Introduction to flow, orthogonal moves, start clue, and complete grid coverage. |
| **6–10** | Connect the numbers | 4×4 | 5–6 | Outer spirals, perimeter routing, and center-outward traversal. |
| **11–15** | Plan your turns | 4×4 | 5–6 | 4-quadrant box turns, S-curves, and corner locks; Level 13 Midpoint. |
| **16–20** | Fill every cell | 4×4 | 5–6 | Cul-de-sac traps, snake-in-box paths, and complete cell coverage mastery. |
| **21–25** | First Steps milestone | 5×5 | 6–7 | Transition to 25-cell boards; Level 25 Chapter 1 Climax Milestone. |
| **26–30** | Smarter routes | 5×5 | 6–7 | Deliberate turns, serpentine sweeps, and 5×5 spatial foresight. |
| **31–35** | Multiple clue segments | 5×5 | 7 | 7-clue multi-segment connections ($1 \to \dots \to 7$) along one continuous path. |
| **36–40** | Longer path planning | 5×5 | 6–7 | Long-horizon routing, U-turns, and perimeter-to-center sweeps; Level 38 Midpoint. |
| **41–45** | Strategic connections | 5×5 | 7 | Complex labyrinthian routing, center-start outwards; Level 42 Recovery. |
| **46–50** | Smart Turns milestone | 5×5 | 7–8 | Dense turns, intricate spatial budgeting; Level 50 Chapter 2 Grand Climax. |

### 16.2 Curated Level Architecture
- **`CuratedFirst50Levels.kt`:**
  - Implements the complete catalog of verified `PuzzleDefinition` and verified canonical `PuzzlePath` objects for Levels 1–50.
  - Linked directly into `PackagedPuzzles.ALL_PACKAGED` and `PackagedPuzzles.ALL_SOLUTIONS`.
  - Seamlessly queried by `LevelCatalogRepositoryImpl.loadPuzzle(levelId)` and cached in `DeterministicLevelProviderImpl`.
- **Existing Puzzles Retained:**
  - Levels 1, 2, 3, 4, 5 and Levels 21, 22, 23 are preserved 100% byte-for-byte from original verified catalog assets and test cases.
  - Level 1 is preserved exactly to guarantee zero disruption to existing guest progress ($1/300$).

### 16.3 Solvability & Mathematical Properties
Every curated level satisfies the core invariants:
1. **Full Playable Coverage:** Every single cell in `requiredCells` (16 cells for 4×4, 25 cells for 5×5) is visited exactly once in the canonical solution.
2. **Manhattan-Orthogonal Continuity:** Every step $(r_i, c_i) \to (r_{i+1}, c_{i+1})$ satisfies $|r_i - r_{i+1}| + |c_i - c_{i+1}| = 1$.
3. **Ascending Clue Sequence:** Clues $1, 2, \dots, N$ are visited in strictly ascending numerical order.
4. **Authoritative Validation:** Every solution passes `CompletionValidator.validate(...)` with zero violations.
5. **No Duplicate Fingerprints:** All 50 levels have unique SHA-256 canonical fingerprints.

### 16.4 Tutorial & Contextual Guidance Integration
- In `GameplayShellScreen.kt`, early levels feature friendly, non-intrusive contextual status pills:
  - Level 1: "Start at 1." $\to$ "Connect to the next number." $\to$ "Fill every cell to finish."
  - Level 2: "Keep one continuous path. No diagonals."
  - Level 3: "Connect numbers in sequence. Plan your turns."
  - Level 4: "Fill every playable cell — every square is required!"
  - Level 5: "Chapter milestone ahead — complete the path!"
- Eliminates lengthy modal popups while keeping player attention focused on the puzzle board.

### 16.5 Chapter Milestones (Levels 25 & 50)
- **Level 25:** Recognized as `MilestoneType.CHAPTER_CLIMAX`, "First Steps Mastered • Chapter 1 Completed". Highlighted in `SoloVictoryScreen.kt` with golden trophy reward badge.
- **Level 50:** Recognized as `MilestoneType.CHAPTER_CLIMAX`, "Sharp Thinker • Chapter 2 Completed". Highlighted in `SoloVictoryScreen.kt` with golden trophy reward badge.
- Triggered strictly by real authoritative completion state; no fictional rewards or progression skips.

### 16.6 Code Written vs Tested Status
- **Code Written:** `CuratedFirst50Levels.kt`, `CuratedFirst50LevelsTest.kt`, `PackagedPuzzles.kt`, `LevelCatalogRepository.kt`, `ChapterLevelConfigFactory.kt`, `GameplayShellScreen.kt`, `SoloVictoryScreen.kt`.
- **Testing Status:** 12 focused unit test scenarios created in `CuratedFirst50LevelsTest.kt`. Under the mandatory prompt execution constraints, execution of Gradle, tests, and build commands is deferred to Prompt 32.

---

## 17. PROMPT 29 — LEVELS 51–300: PROGRESSIVE CHALLENGE, PUZZLE VARIETY & CHAPTER MILESTONES

### 17.1 Progression Structure & Chapter Breakdown (51–300)
The 300-level solo campaign delivers continuous, progressive challenge across Chapters 3 through 7 while keeping the original Number Path engine strictly unmodified:

| Levels | Chapter | World | Grid Size | Checkpoints | Walls | Pacing & Milestone |
| :---: | :--- | :---: | :---: | :---: | :---: | :--- |
| **51–100** | Path Explorer | 3 | 5×5 | 4–7 | 1–5 | Varied clue placement, first wall obstacles; Recovery: 60, 70, 85, 95; **Level 100 Climax** ("Path Explorer Mastered"). |
| **101–150** | Strategic Paths | 4 | 6×6 | 4–8 | 2–8 | 36-cell planning, controlled clue density, labyrinth corridors; Recovery: 110, 120, 135, 145; **Level 150 Climax** ("Strategic Paths Mastered"). |
| **151–200** | Expert Journey | 5 | 7×7 | 4–10 | 4–12 | 49-cell expansive grids, strategic wall chokepoints; Recovery: 160, 170, 185, 195; **Level 200 Climax** ("Expert Journey Mastered"). |
| **201–250** | Master Trails | 6 | 8×8 | 4–12 | 6–18 | 64-cell grand topologies, deceptive turn options; Recovery: 210, 220, 235, 245; **Level 250 Climax** ("Master Trails Mastered"). |
| **251–300** | Grand Challenge | 6 | 8×8 | 4–12 | 6–18 | Climax of the solo campaign, deep route foresight, tight wall configurations; Recovery: 260, 270, 285, 295; **Level 300 Grand Finale** ("Grand Pathmaster"). |

### 17.2 Real Puzzle Content & Curated Anchor Architecture
- **Curated Chapter Milestones (`CuratedAnchorLevels.kt`):**
  - Authoritative, hand-crafted, mathematically verified `PuzzleDefinition` and `PuzzlePath` instances for:
    - **Level 100:** Path Explorer Climax (5×5, 4 walls, 7 clues, 25 cells).
    - **Level 101:** Strategic Paths Entry (6×6, 3 walls, 6 clues, 36 cells).
    - **Level 150:** Strategic Paths Climax (6×6, 6 walls, 8 clues, 36 cells).
    - **Level 151:** Expert Journey Entry (7×7, 5 walls, 7 clues, 49 cells).
    - **Level 200:** Expert Journey Climax (7×7, 8 walls, 9 clues, 49 cells).
    - **Level 201:** Master Trails Entry (8×8, 8 walls, 8 clues, 64 cells).
    - **Level 250:** Master Trails Climax (8×8, 12 walls, 10 clues, 64 cells).
    - **Level 251:** Grand Challenge Entry (8×8, 10 walls, 9 clues, 64 cells).
    - **Level 300:** Grand Finale (8×8, 14 walls, 12 clues, 64 cells).
- **Dimension-Accurate Fallbacks (`CuratedAnchorLevels.getFallbackForLevel`):**
  - Guaranteed fallback resolver matching the exact grid dimensions and rules of each world (World 3 $\to$ 5×5, World 4 $\to$ 6×6, World 5 $\to$ 7×7, World 6 $\to$ 8×8).
  - Prevents dimension mismatch or arbitrary replacement content if procedural attempts exhaust bounded budgets.
- **Deterministic Level Loading (`DeterministicLevelProviderImpl.kt`):**
  - Caches generated puzzles in memory.
  - Pre-populates with `CuratedAnchorLevels` and `PackagedPuzzles`.
  - Deterministically generates un-authored levels on demand via `ChapterLevelGenerator` using `LevelSeedGenerator` (SplitMix64).
  - Integrated into `LevelCatalogRepositoryImpl.loadPuzzle(levelId)` to resolve all levels in 1..300.

### 17.3 Preserved Gameplay Rules & Authoritative Validation
- Every single puzzle requires:
  1. Clues connected in strictly ascending order ($1 \to 2 \dots \to N$).
  2. Exactly ONE continuous, non-branching path through all segments.
  3. Valid Manhattan-orthogonal movements with zero diagonal steps or wall crossings.
  4. 100% full coverage of all playable cells.
  5. Completion verified by authoritative `CompletionValidator` and `FoundationalPathValidator`.
- Level 300 celebration is triggered strictly upon real completion validation (no payment, no ads required).

### 17.4 Chapter Milestone Integration & Victory UI
- **Progression Plan Updates (`ProgressionPlan.kt`):**
  - Updated milestone titles and subtitles for Levels 100, 150, 200, 250, and 300 to explicitly reflect the completed chapters and century marks.
- **Victory Screen Celebration (`SoloVictoryScreen.kt`):**
  - `TopVictoryHeader` displays golden milestone banners:
    - Level 100: `"CHAPTER 3 MILESTONE • PATH EXPLORER MASTERED"`
    - Level 150: `"CHAPTER 4 MILESTONE • STRATEGIC PATHS MASTERED"`
    - Level 200: `"CHAPTER 5 MILESTONE • EXPERT JOURNEY MASTERED"`
    - Level 250: `"CHAPTER 6 MILESTONE • MASTER TRAILS MASTERED"`
    - Level 300: `"GRAND FINALE • ALL 300 LEVELS CONQUERED!"`
  - `VictoryRewardsSection` displays corresponding gold chapter completion trophies.

### 17.5 Progress & Data Compatibility
- Guest progress (~1/300 completed on Level 1) is completely preserved.
- Room database tables, DataStore keys, and level indices 1..300 remain stable and unchanged.

### 17.6 Test Coverage Preparation
- **`ProgressiveLevels51To300Test.kt`:**
  - 15 comprehensive unit test scenarios covering:
    1. Stable IDs 51–300.
    2. Correct chapter boundaries.
    3. Valid numbered-clue sequence.
    4. Continuous solution path.
    5. Full playable-cell coverage.
    6. Valid movement.
    7. Solvability via independent validators.
    8. Duplicate detection & fingerprint uniqueness.
    9. Difficulty metadata.
    10. Stable deterministic level loading.
    11. Hint compatibility.
    12. Saved-progress compatibility.
    13. Milestones at 100, 150, 200, 250, and 300.
    14. 300 contiguous level IDs with zero duplicates or gaps.
    15. Level 300 completion verification using authoritative `CompletionValidator`.

### 17.7 Code Written vs Tested Status
- **Code Written:** `CuratedAnchorLevels.kt`, `DeterministicLevelProvider.kt`, `LevelCatalogRepository.kt`, `ProgressionPlan.kt`, `SoloVictoryScreen.kt`, `ProgressiveLevels51To300Test.kt`, `CuratedFirst50LevelsTest.kt`.
- **Testing Status:** Automated tests prepared without execution in accordance with the mandatory code-only execution constraint. Full verification deferred to Prompt 32.

---

## 19. PROMPT 30 IMPLEMENTATION: PREMIUM CHAPTER THEMES, LEVEL REWARDS & VICTORY CELEBRATIONS

### 19.1 Seven Chapter Visual Themes (`ChapterTheme.kt`, `ChapterThemes`)
- **Visual Identity Preservation:** Strictly retains Zynpath's signature aesthetic:
  - Deep Navy primary background (`0xFF07142D`, `0xFF101D3C`).
  - Royal-Blue surfaces (`0xFF154A98`).
  - Electric-Cyan puzzle connections (`0xFF21D4FD`).
  - Gold primary action buttons (`0xFFFFC247`, `0xFFFFE27A`).
- **Seven Unified Chapter Themes:**
  1. **Chapter 1 (Levels 1–25) — First Steps:** Calm blue discovery (`0xFF20D76B` emerald accent, calm stars pattern).
  2. **Chapter 2 (Levels 26–50) — Smart Turns:** Cyan pathways (`0xFF21D4FD` electric cyan, `0xFF168BFF` bright blue, cyan circuits pattern).
  3. **Chapter 3 (Levels 51–100) — Path Explorer:** Luminous exploration (`0xFFFF5252` glowing coral, luminous crystal diamond pattern).
  4. **Chapter 4 (Levels 101–150) — Strategic Paths:** Deep-blue geometric trails (`0xFF7209B7` royal amethyst, geometric trails pattern).
  5. **Chapter 5 (Levels 151–200) — Expert Journey:** Refined electric-blue atmosphere (`0xFFFF8D32` amber flame, electric aurora wave pattern).
  6. **Chapter 6 (Levels 201–250) — Master Trails:** Royal-blue and gold mastery (`0xFFFFC247` gold, royal crest pattern).
  7. **Chapter 7 (Levels 251–300) — Grand Challenge:** Premium cinematic finale (`0xFF21D4FD` electric cyan & `0xFFFFE27A` gold, cosmic finale pattern).
- **Fallback Guarantee:** Out-of-range level IDs or unknown chapter IDs gracefully fall back to `ChapterThemes.CHAPTER_1`.

### 19.2 Premium World Map Progression (`LevelAdventureMap.kt`)
- **Chapter Transition Banners:** Renders a sleek `ChapterMapBanner` at chapter entry points (e.g. crossing between Level 25/26, 50/51, 100/101, 150/151, 200/201, 250/251).
- **Thematic Trail Connection:** Cubic bezier connecting paths adopt the chapter theme's `mapPathGlow` and `nodeCompletedGlow`.
- **Chapter-Specific Environmental Details:** Replaces generic crystals with chapter pattern shapes (calm stars, cyan circuits, luminous crystals, geometric hexagons, aurora arcs, royal crests, cosmic particles).

### 19.3 Level Checkpoint Node Presentation (`LevelCheckpointNode.kt`)
- **Non-Color-Only Indicators:** Distinct shapes and icons for Locked (Lock icon), Unlocked, Current ("PLAY" badge pill), and Completed (star row 1..3).
- **Chapter Milestone Emblems:** Displays a mini trophy or crown emblem for milestones (Levels 25, 50, 100, 150, 200, 250, 300).
- **Accessible Touch Target:** Minimum 48dp clickable area with TalkBack accessibility announcements.
- **Motion Accessibility:** Automatically respects `isReducedMotion`, providing static high-contrast states instead of infinite pulsing transitions.

### 19.4 Level Entry Modal (`LevelDetailsDialog.kt`)
- Shows exact real data: Level number, Chapter title and subtitle, Difficulty category (Introductory, Casual, Balanced, Strategic, Advanced, Expert, Master), Personal Best, and earned Stars.
- Instant, lightweight transition with zero blocking animations or artificial loading screens.

### 19.5 Puzzle Board Theme Integration (`GameplayShellScreen.kt`)
- Displays chapter title in the top bar (`"${chapterTheme.title} • World $worldId • ${currentBoard.rowCount}×${currentBoard.columnCount} Grid"`).
- Ambient backdrop platform behind the board subtly glows with the chapter's `ambientGlowColor` without altering any core puzzle cell contrast or playability.
- Original Number Path rules, 100% cell fill requirement, and authoritative validator remain 100% unchanged.

### 19.6 Level Completion Celebration & Grand Finale (`SoloVictoryScreen.kt`)
- **Authoritative Trigger:** Celebration displays strictly upon `CompletionValidator` success.
- **Chapter Milestone Presentation:** Levels 25, 50, 100, 150, 200, and 250 feature custom milestone banners and badges with chapter sparkle colors.
- **Level 300 Grand Finale:**
  - Distinct radiant crown emblem and "GRAND FINALE!" headline.
  - "ALL 300 NUMBER PATHS MASTERED" with full 100% campaign completion badge.
  - Clear "RETURN TO WORLD MAP" primary action invoking `onHome()`.
  - Zero paywalls, zero ads, zero fabricated currencies.
- **Reward Idempotency:** Inspecting or reopening a completed level renders existing database state without duplicate grants.

### 19.7 Test Coverage Preparation (`ChapterThemesAndCelebrationTest.kt`)
- 12 comprehensive unit test scenarios covering:
  1. Seven chapter mappings.
  2. Correct level ranges spanning 1..300 consecutively.
  3. Theme fallback behavior.
  4. Node locked/unlocked/completed state distinction.
  5. Milestone boundaries at 25, 50, 100, 150, 200, 250, and 300.
  6. Completion fanfare tiers (Tiers 1, 2, and 3).
  7. Reward idempotency.
  8. Level 300 finale conditions and navigation.
  9. Existing star calculation boundaries.
  10. Saved guest progress compatibility (Level 1).
  11. Reduced-motion scale/alpha properties.
  12. Distinct decorative patterns across all seven chapters.

### 19.8 Code Written vs Tested Status
- **Code Written:** `ChapterTheme.kt`, `LevelCheckpointNode.kt`, `LevelAdventureMap.kt`, `LevelDetailsDialog.kt`, `GameplayShellScreen.kt`, `SoloVictoryScreen.kt`, `ChapterThemesAndCelebrationTest.kt`.
- **Testing Status:** Automated tests prepared without execution per mandatory code-only constraints. Verification deferred to Prompt 32.

---

---

## 20. PROMPT 31 — TWO FREE HINTS, REAL REWARDED ADS & DIFFICULTY BALANCE

### 20.1 Two-Free-Hint Policy & Economy (`LevelHintState.kt`, `LevelHintRepository.kt`)
- **Locked Free Hint Allowance:** Exactly two free hints per level (`LOCKED_FREE_HINTS_PER_LEVEL = 2`).
- **Level-Specific Persistence:**
  - `lvl_hint_free_used_$levelId` (0..2)
  - `lvl_hint_reward_earned_$levelId` (>= 0)
  - `lvl_hint_reward_consumed_$levelId` (0..earned)
- **Non-Negative Invariant:**
  - `freeHintsRemaining = (2 - freeHintsUsed).coerceAtLeast(0)`
  - `rewardedHintsRemaining = (rewardedHintsEarned - rewardedHintsConsumed).coerceAtLeast(0)`
  - `totalAvailableHints = freeHintsRemaining + rewardedHintsRemaining`
- **Isolation:** Hint balances are strictly keyed by `levelId`. Reopening a level retains its exact consumption state; one level's hints never leak to another.
- **Fair Consumption:** No hint is ever deducted if the solver/hint engine cannot provide valid guidance or if the puzzle is already complete.

### 20.2 Additional Hint-Credit Model & Rewarded Ad Policy
- **Transition Point:** Once both free hints are consumed (`freeHintsRemaining == 0`), the hint action transitions to:
  - If rewarded credit is available: `"Use Earned Hint"`
  - If no credit exists: `"Watch Ad — Get 1 Hint"`
- **Strict One-to-One Credit:** Exactly one confirmed rewarded video grants exactly +1 hint credit for that specific level.
- **Voluntary & Non-Blocking:** Ads are 100% optional. No player is ever forced to watch an ad to continue normal gameplay, undo, reset, or complete any level. Unlimited undo and reset remain completely free.

### 20.3 Real Google Mobile Ads Rewarded Integration (`AdMobRewardedHintAdManager.kt`)
- **Direct SDK Integration:** Implements `RewardedHintAdContract` and `Application.ActivityLifecycleCallbacks` using `libs.play.services.ads` (`com.google.android.gms.ads:play-services-ads`).
- **Core Lifecycle & Callbacks:**
  - `MobileAds.initialize(context)` with safe one-time main thread initialization.
  - `RewardedAd.load(...)` with `RewardedAdLoadCallback` using `AdConfiguration.rewardedAdUnitId`.
  - Automatic background preload on app startup, after ad dismissal, and after ad show failure.
- **Authoritative Reward Callback:**
  - Credit is granted **strictly** when the SDK's `OnUserEarnedRewardListener` (`rewardedAd.show(activity) { rewardItem -> ... }`) executes.
  - Neither button clicks, ad loads, ad impressions, ad openings, timeouts, nor ad dismissals without reward can award hints.
  - If the player closes or skips the ad before reaching the reward threshold, `RewardedHintAdResult.Skipped` is dispatched without granting credit.
- **Activity & Lifecycle Safety:**
  - WeakReference tracking of the foreground Activity via `ActivityLifecycleCallbacks` prevents memory leaks.
  - Ad display clears the cached `RewardedAd` reference immediately to prevent duplicate shows.
  - Zero auto-showing of ads upon returning to the app.

### 20.4 Exactly-Once Reward Idempotency (Task 8)
- **Unique Claim IDs:** Every ad presentation attempt generates an authoritative claim token: `lvl_${levelId}_ad_claim_${UUID.randomUUID()}`.
- **Atomic Persistence Guard:** `LevelHintRepositoryImpl.recordConfirmedReward(levelId, claimId)` atomically records `claimId` in `claimed_hint_reward_ids` set. Duplicate attempts with an identical ID are safely rejected.
- **Rapid-Click Protection:** Thread-safe `isAdShowing` atomic state machine ignores rapid consecutive button taps.

### 20.5 AdMob Configuration Safety & Missing Credentials Blocker (Task 6)
- **Debug:** Uses Google's official Rewarded Video test ad unit ID: `ca-app-pub-3940256099942544/5224354917`.
- **Release:** Reads `BuildConfig.ADMOB_REWARDED_AD_UNIT_ID`. If empty, it does **NOT** silently fall back to test ads.
- **Safety Guard:** If `AdConfiguration.isProductionBlockedByConfiguration` or the ad unit ID is blank in release:
  - Ad requests are blocked from hitting the network with invalid IDs.
  - The manager returns `RewardedHintAdResult.Unavailable` with `"No video available right now. Please try again later."`.
  - Core puzzle gameplay remains 100% functional.
- **Release Blocker:** A real, user-supplied, approved AdMob production App ID and Rewarded Ad Unit ID must be supplied in `app/build.gradle.kts` release buildConfig before production distribution.

### 20.6 Consent & Privacy Integration (Task 14)
- Integrates with `AdConsentManager.canRequestAds()`.
- If consent is required but not yet obtained, ad requests are prevented and treated as unavailable.
- Release blocker: User Messaging Platform (UMP) production consent form must be deployed in Google AdMob console prior to European/EEA app distribution.

### 20.7 Valid Hint Generation & Original Gameplay Rules (Tasks 2 & 10)
- **Mathematical Guarantee:** `PuzzleHintEngine` proves 100% full-grid coverage to checkpoint K before recommending a next move.
- **No Path Rewrites:** The engine never silently overwrites the player's path. If the current path is in a dead-end, it provides explicit `RecoveryRequired` backtracking guidance with recommended rollback position.
- **Rules Preserved:** Ascending number sequence (1 -> 2 -> ... -> K), orthogonal adjacency (no diagonals, no jumps, no wall penetrations), and mandatory 100% cell coverage remain invariant.

### 20.8 Player-Friendly Difficulty Balance & UI (`GameplayShellScreen.kt`)
- **Difficulty Badges:** Displays `LevelDifficultyIndicator` in the top bar using `ExperienceDifficultyCategory` (Introductory, Casual, Balanced, Strategic, Advanced, Expert, Master).
- **Accurate Level Progress:** Subtitle clearly shows Chapter Title, Difficulty, World ID, and Grid Size.
- **Contextual Early Guidance:** Levels 1–5 feature friendly guidance prompts in the status pill area.
- **Responsive Hint Action Button:**
  - 2 Free Hints: `"Hint — 2 Free Left"`
  - 1 Free Hint: `"Hint — 1 Free Left"`
  - 0 Free Hints, Earned Credit: `"Use Earned Hint"`
  - 0 Free Hints, No Credit: `"Watch Ad — Get 1 Hint"`
- **Clear Failure Feedback:** If no video is available, displays concise non-blocking message: `"No video available right now. Please try again later."`.

### 20.9 Test Coverage Preparation (`LevelHintEconomyTest.kt`)
- 20 comprehensive unit test scenarios created covering:
  1. Exactly two free hints per level initial state.
  2. Free-hint persistence across consumptions.
  3. Isolation between level hint balances.
  4. Proper transition to `RequiresRewardedAd`.
  5. Reward granted only from verified callback.
  6. No reward on ad opening alone.
  7. No reward on ad dismissal alone.
  8. Graceful handling of unavailable ads without deducting hints.
  9. Duplicate callback idempotency via claim IDs.
  10. Rapid duplicate button-tap protection.
  11. Reward credited strictly to originating level.
  12. Earned credit persistence across sessions.
  13. Exactly-once consumption of earned credits.
  14. Non-negative balances invariant.
  15. Valid next-move generation adjacent to path head.
  16. No hint consumption when puzzle is already completed.
  17. 100% cell coverage completion enforcement.
  18. Debug vs release configuration separation.
  19. Guest progress safety.
  20. Session hint restoration without resetting persisted used counts.

### 20.10 Code-Only Status & Release Blockers
- **Code Written:**
  - `AdConfiguration.kt` (release test ad separation)
  - `RewardedHintAdContract.kt` (preload and activity overloads)
  - `AdMobRewardedHintAdManager.kt` (real AdMob SDK implementation)
  - `AdsModule.kt` (Hilt binding)
  - `GameplayViewModel.kt` (rewarded ad integration, level hint observation)
  - `GameplayShellScreen.kt` (hint economy labels, limit dialog, difficulty badges)
  - `LevelHintEconomyTest.kt` (20 automated unit test scenarios)
  - `docs/LEVEL_EXPERIENCE_PHASE_25_32.md`
- **Testing Status:** Automated tests prepared without running per strict code-only constraint. Verification deferred to Prompt 32.
- **Remaining Release Blockers:**
  1. `ADMOB_REWARDED_AD_UNIT_ID` in `app/build.gradle.kts` release buildType is empty (`""`). Production ads require a valid approved AdMob account and unit ID.
  2. Production Google User Messaging Platform (UMP) consent form deployment required for EEA/UK privacy compliance.

---

## 21. REMAINING ROADMAP STATUS (PROMPT 32)

1. **Prompt 32 — Comprehensive Audit, Build, Tests & APK Verification:** End-to-end compilation, test suite execution, APK generation, and release-readiness verification.






