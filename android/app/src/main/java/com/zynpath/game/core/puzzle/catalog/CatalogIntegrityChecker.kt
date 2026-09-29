package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint

/**
 * Result of catalog and asset integrity verification.
 */
data class CatalogIntegrityResult(
    val isValid: Boolean,
    val checkedWorldCount: Int,
    val checkedLevelCount: Int,
    val packagedAssetCount: Int,
    val errors: List<String>,
    val warnings: List<String>
) {
    val errorSummary: String
        get() = errors.joinToString("; ")
}

/**
 * Comprehensive integrity checker for catalog manifests and packaged puzzle assets.
 *
 * Implements Prompt 11 Sections 15, 32, and 33:
 * Verifies world definitions, level sequences, non-overlapping bounds, asset existence,
 * schema conformance, and fingerprint integrity.
 */
object CatalogIntegrityChecker {

    fun checkIntegrity(
        manifest: CatalogManifest,
        assetLoader: PuzzleAssetLoader,
        verifyAssetContents: Boolean = true
    ): CatalogIntegrityResult {
        val errors = ArrayList<String>()
        val warnings = ArrayList<String>()

        // 1. Check Worlds
        if (manifest.worlds.size != 6) {
            errors.add("Catalog must define exactly 6 worlds (got ${manifest.worlds.size})")
        }

        val worldIds = manifest.worlds.map { it.worldId }
        if (worldIds != listOf(1, 2, 3, 4, 5, 6)) {
            errors.add("World IDs must be [1, 2, 3, 4, 5, 6] (got $worldIds)")
        }

        // Check world ranges contiguity
        var expectedStartLevel = 1
        for (w in manifest.worlds.sortedBy { it.worldId }) {
            if (w.firstLevelId != expectedStartLevel) {
                errors.add("World ${w.worldId} starts at ${w.firstLevelId}, expected $expectedStartLevel")
            }
            if (w.firstLevelId > w.lastLevelId) {
                errors.add("World ${w.worldId} has invalid level bounds ${w.firstLevelId}..${w.lastLevelId}")
            }
            expectedStartLevel = w.lastLevelId + 1
        }
        if (expectedStartLevel - 1 != manifest.totalPlannedLevels) {
            errors.add("Total level span ${expectedStartLevel - 1} does not equal planned ${manifest.totalPlannedLevels}")
        }

        // 2. Check Levels in Manifest
        val seenLevelIds = HashSet<Int>()
        val seenPuzzleIds = HashSet<String>()
        var packagedCount = 0

        for (lvl in manifest.levels) {
            if (!seenLevelIds.add(lvl.levelId)) {
                errors.add("Duplicate levelId ${lvl.levelId} in catalog manifest")
            }
            if (!seenPuzzleIds.add(lvl.puzzleId)) {
                warnings.add("Reused puzzleId '${lvl.puzzleId}' across levels")
            }

            val world = manifest.getWorld(lvl.worldId)
            if (world == null) {
                errors.add("Level ${lvl.levelId} references nonexistent world ${lvl.worldId}")
            } else {
                if (lvl.levelId !in world.levelRange) {
                    errors.add("Level ${lvl.levelId} assigned to World ${lvl.worldId} whose range is ${world.levelRange}")
                }
            }

            // 3. Verify Packaged Assets
            if (lvl.hasPackagedAsset) {
                if (!assetLoader.hasAsset(lvl.assetPath)) {
                    errors.add("Level ${lvl.levelId} references missing asset '${lvl.assetPath}'")
                } else {
                    packagedCount++
                    if (verifyAssetContents) {
                        val assetJson = assetLoader.loadAsset(lvl.assetPath)
                        if (assetJson == null) {
                            errors.add("Level ${lvl.levelId} asset could not be read: '${lvl.assetPath}'")
                        } else {
                            val validation = PuzzleAssetValidator.validate(assetJson, lvl, world)
                            if (validation is PuzzleAssetValidationResult.Invalid) {
                                errors.add("Level ${lvl.levelId} asset validation failed: ${validation.errorSummary}")
                            } else if (validation is PuzzleAssetValidationResult.Valid) {
                                val computedHash = PuzzleFingerprint.computeSha256(validation.definition)
                                if (lvl.fingerprint.isNotBlank() && lvl.fingerprint != computedHash) {
                                    errors.add("Level ${lvl.levelId} fingerprint mismatch: expected ${lvl.fingerprint}, got $computedHash")
                                }
                            }
                        }
                    }
                }
            }
        }

        return CatalogIntegrityResult(
            isValid = errors.isEmpty(),
            checkedWorldCount = manifest.worlds.size,
            checkedLevelCount = manifest.levels.size,
            packagedAssetCount = packagedCount,
            errors = errors,
            warnings = warnings
        )
    }
}
