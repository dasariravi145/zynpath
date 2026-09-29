# Zynpath Catalog Versioning & Migration Policy

## 1. Versioning Concerns

Zynpath maintains strict architectural separation between distinct system versions:

1. **`CATALOG_VERSION` ("1.0.0"):**
   Governs the campaign structure, world allocations, level ranges, and the complete catalog manifest. A change indicates a revised campaign structure or level re-allocation.

2. **`MANIFEST_SCHEMA_VERSION` ("1.0.0"):**
   Governs the JSON schema structure of `catalog_manifest.json`.

3. **`ASSET_SCHEMA_VERSION` ("1.0.0"):**
   Governs the JSON schema structure of individual puzzle asset files (`lvl{N}.json`).

4. **`PUZZLE_VERSION` (Integer, default 1):**
   Governs an individual puzzle definition. If a specific puzzle is revised or tweaked, its version increments, while retaining the same `levelId`.

5. **`GENERATOR_VERSION` & `CURATION_VERSION` ("1.0.0"):**
   Governs the algorithmic pipelines used to construct and curate puzzle candidates.

---

## 2. Stability & Migration Guarantees

1. **Permanent Level Identity:**
   Level IDs (1 to 300) are immutable anchors. Level 12 will forever remain Level 12.

2. **Progress Preservation:**
   Player completions, stars, and best times recorded in Room database (`LevelProgressEntity`) are indexed by `levelId`. Catalog revisions or asset updates must NEVER erase or reset user progress.

3. **Session Compatibility:**
   In-progress game sessions (`GameSessionEntity`) store `puzzleId` and `puzzleVersion`. When restoring a saved session, if the active catalog puzzle version differs from the saved session, the session is safely invalidated or reset rather than restoring an incompatible path onto a modified board.

4. **Corrupt Asset Safeguard:**
   If a packaged puzzle fails schema validation or fingerprint verification at runtime:
   - The puzzle is NOT launched.
   - User progress is preserved untouched.
   - Structured diagnostic errors are returned.
   - The user is provided a safe navigation return to Level Selection.
