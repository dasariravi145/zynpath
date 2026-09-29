# Zynpath Level Catalog Architecture

## 1. Overview & Objectives

The Zynpath Level Catalog provides an immutable, verified offline content foundation for the solo campaign. It decouples level progression, stable level identities, and solver-verified puzzle assets from dynamic runtime generation and cloud infrastructure.

Key tenets:
1. **Offline-First:** All campaign levels are packaged locally as application assets; zero network or remote servers are required for gameplay.
2. **Deterministic & Immutable:** Once a puzzle is published to a level, its assignment, fingerprint, and version remain stable across app updates and engine revisions.
3. **Solver-Validated Admission:** No puzzle is admitted or published without passing the 10-step verification pipeline (structural validity, exact solver execution, uniqueness check, completion validation, and serialization round-trip).
4. **Honest Content Availability:** For levels planned but not yet curated/packaged, the engine cleanly presents an honest unavailable state rather than fabricating placeholder boards.

---

## 2. World Definitions

The campaign is structured into 6 authoritative worlds comprising 300 planned levels:

| World | Name | Level Range | Grid Size | Checkpoints | Walls | Target Difficulty | Target Band |
|---|---|---|---|---|---|---|---|
| **1** | Learn the Path | 1–20 | 4×4 | 4–6 | 0 | 0.00–0.22 | BEGINNER |
| **2** | Longer Connections | 21–50 | 5×5 | 4–7 | 0 | 0.18–0.38 | EASY |
| **3** | Wall Challenge | 51–100 | 5×5 | 4–7 | 1–5 | 0.32–0.55 | MEDIUM |
| **4** | Complex Routes | 101–150 | 6×6 | 4–8 | 2–8 | 0.45–0.68 | MEDIUM |
| **5** | Advanced Logic | 151–200 | 7×7 | 4–10 | 4–12 | 0.60–0.82 | HARD |
| **6** | Expert Path | 201–300 | 8×8 | 4–12 | 6–18 | 0.75–1.00 | EXPERT |

---

## 3. Core Components

1. **`WorldDefinition` (`com.zynpath.game.core.puzzle.catalog`):**
   Pure domain representation of world topology, bounds, and level ranges, fully decoupled from Android and Compose dependencies.

2. **`LevelDefinition` (`com.zynpath.game.core.puzzle.catalog`):**
   Immutable descriptor binding `levelId`, `worldId`, `puzzleId`, `puzzleVersion`, `assetPath`, `difficultyEstimate`, `difficultyBand`, `uniquenessStatus`, `fingerprint`, and `hasPackagedAsset`.

3. **`CatalogManifest` (`com.zynpath.game.core.puzzle.catalog`):**
   Top-level manifest storing catalog metadata, world definitions, level definitions, and a root SHA-256 integrity hash computed across all levels.

4. **`PuzzleAsset` & `PuzzleAssetSerializer` (`com.zynpath.game.core.puzzle.catalog`):**
   Canonical JSON serialization and deserialization format for puzzle topology, required cells, checkpoints, normalized walls, and metadata.

5. **`PuzzleAssetLoader` & `AndroidAssetLoader`:**
   Platform abstraction allowing runtime asset loading via `AssetManager` on Android, and `InMemoryAssetLoader` in JVM test suites.

6. **`CatalogAdmissionPipeline`:**
   Rigorous 10-step admission gate verifying candidates before packaging.

7. **`CatalogIntegrityChecker`:**
   Validates world contiguity, non-overlapping level boundaries, asset presence, and SHA-256 fingerprint matches.

8. **`LevelCatalogRepository` (`com.zynpath.game.core.puzzle.catalog`):**
   Clean repository contract offering reactive Flow and suspend functions for level availability, puzzle loading, and progression observation.

---

## 4. Initial Packaged Levels

Prompt 11 packages 11 representative, solver-verified levels:
- **World 1 (Levels 1–5):** 4×4 grids, 0 walls, 4–6 checkpoints, all proven unique.
- **World 2 (Levels 21–23):** 5×5 grids, 0 walls, 6 checkpoints, all proven unique.
- **World 3 (Levels 51–53):** 5×5 grids, 1–3 walls, 6 checkpoints, all proven unique.

Unpackaged levels (e.g. 6–20, 24–50, 54–300) are mapped in the manifest with `hasPackagedAsset: false` and are gracefully handled as `LevelAvailability.ASSET_UNAVAILABLE`.

---

## 5. Next Level Resolution & Replay Gates (Prompt 15)

1. **Resolution Pipeline**:
   - `GameplayViewModel.resolveNextLevel()` queries `LevelCatalogRepository` for `levelId + 1`.
   - Checks catalog definition existence, progression unlock status via `ProgressRepository`, and asset packaging integrity before allowing navigation.
2. **Replay Invariance**:
   - Replaying a level executes against the exact same immutable asset descriptor and fingerprint.
   - Preserves historical stars and personal best durations.

---

## 6. Daily Challenge Verified Puzzle Pool Integration (Prompt 16)

The Daily Challenge scheduling system ([`DailyChallengeSchedule`](file:///d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/daily/DailyChallengeSchedule.kt)) reuses the verified puzzle assets and definitions from the catalog:
- Verified catalog fixtures (`PackagedPuzzles.LEVEL_1` through `LEVEL_53`) along with curated 6×6 challenges form the 14-puzzle curated pool.
- Each challenge assignment references the canonical `puzzleId`, `puzzleVersion`, and SHA-256 `puzzleFingerprint`.
- Guarantees that Daily Challenges share the exact same mathematical validity and completion rules as the Solo campaign without generating unverified runtime puzzles.

---

## 7. Premium Solo Puzzle Packs Catalog (Prompt 27)

### 7.1 Free World Protection
Worlds 1–6 (Levels 1–300) are strictly preserved as free content. Premium puzzle packs do not displace, renumber, or lock any campaign level.

### 7.2 Curated Pack Registry
Premium puzzle packs are organized into self-contained, theme-based collections separate from the main campaign:

| Pack ID | Display Name | Grid Sizes | Themes / Mechanics | Levels | Status | Required Entitlement |
|---|---|---|---|---|---|---|
| `pack_master_serpentine` | Serpentine Mastery | 5×5, 6×6 | Continuous curves, edge wrapping, long runs | 5 | PUBLISHED | `PREMIUM_SOLO_PACKS` |
| `pack_labyrinth_walls` | Labyrinth Walls | 5×5, 6×6 | Dense internal barriers (2–6 walls), single bottleneck routes | 5 | PUBLISHED | `PREMIUM_SOLO_PACKS` |
| `pack_grandmaster_7x7` | Grandmaster 7×7 | 7×7 | Elite 49-cell continuous paths | 0 | COMING_SOON | `PREMIUM_SOLO_PACKS` |

### 7.3 Solver Verification & Content Manifests
All published pack puzzles are solver-verified to guarantee at least one valid continuous-path solution. Each pack publishes a versioned `PremiumPackManifest` containing puzzle IDs, versions, grid dimensions, checkpoints, walls, and SHA-256 fingerprints. Puzzles are delivered securely via backend API and stored locally with atomic validation.

---

## 8. Complete 300-Level Catalog Audit & Solver Verification (Prompt 46)

During Phase 11 QA verification, all 300 campaign level descriptors in `CatalogManifest` and all shipped puzzle assets were exhaustively audited:
- **Level ID Range**: 1 to 300 contiguous and unique (0 gaps, 0 duplicates).
- **Canonical World Mapping**: 100% compliant with World 1–6 boundary specifications.
- **Shipped Level Solvability**: 100% of shipped puzzles (`PackagedPuzzles.ALL_PACKAGED`) solved by `PuzzleSolver` and independently verified by `CompletionValidator` and `FoundationalPathValidator`.
- **Zero Duplicates**: SHA-256 fingerprint deduplication verified 100% distinct topologies.
- See full audit details in [`docs/LEVEL_CATALOG_AUDIT.md`](file:///d:/Zynpath/docs/LEVEL_CATALOG_AUDIT.md).


