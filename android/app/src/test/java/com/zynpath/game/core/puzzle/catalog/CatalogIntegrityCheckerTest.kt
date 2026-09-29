package com.zynpath.game.core.puzzle.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CatalogIntegrityChecker].
 *
 * Implements Prompt 11 Section 15, 32, and 33:
 * Exhaustively checks world definitions, level contiguity, asset presence,
 * schema conformance, and SHA-256 fingerprint verification.
 */
class CatalogIntegrityCheckerTest {

    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var manifest: CatalogManifest

    @Before
    fun setUp() {
        assetLoader = InMemoryAssetLoader()
        manifest = CatalogManifest.createDefaultManifest()
        CatalogAssetGenerator.populateLoader(assetLoader)
    }

    @Test
    fun `test default populated catalog passes complete integrity checks`() {
        val result = CatalogIntegrityChecker.checkIntegrity(
            manifest = manifest,
            assetLoader = assetLoader,
            verifyAssetContents = true
        )

        assertTrue(
            "Integrity check must succeed, but got errors: ${result.errorSummary}",
            result.isValid
        )
        assertEquals(0, result.errors.size)
        assertEquals(6, result.checkedWorldCount)
        assertEquals(300, result.checkedLevelCount)
        assertEquals(PackagedPuzzles.ALL_PACKAGED.size, result.packagedAssetCount)
    }

    @Test
    fun `test integrity checker catches missing asset file`() {
        // Remove asset for Level 1 from loader
        assetLoader.removeAsset("puzzles/w1/lvl1.json")

        val result = CatalogIntegrityChecker.checkIntegrity(
            manifest = manifest,
            assetLoader = assetLoader,
            verifyAssetContents = false
        )

        assertFalse("Integrity check must fail when asset is missing", result.isValid)
        assertTrue(result.errors.any { it.contains("missing asset") && it.contains("Level 1") })
    }

    @Test
    fun `test integrity checker catches fingerprint mismatch`() {
        // Modify manifest fingerprint for Level 1 to a bogus hash
        val modifiedLevels = manifest.levels.map { lvl ->
            if (lvl.levelId == 1) lvl.copy(fingerprint = "0000000000000000000000000000000000000000000000000000000000000000")
            else lvl
        }
        val modifiedManifest = manifest.copy(levels = modifiedLevels)

        val result = CatalogIntegrityChecker.checkIntegrity(
            manifest = modifiedManifest,
            assetLoader = assetLoader,
            verifyAssetContents = true
        )

        assertFalse("Integrity check must fail on fingerprint mismatch", result.isValid)
        assertTrue(result.errors.any { it.contains("fingerprint mismatch", ignoreCase = true) && it.contains("Level 1") })
    }

    @Test
    fun `test integrity checker catches duplicate level IDs`() {
        val dupLevel = manifest.levels.first().copy(levelId = 2) // duplicate of level 2
        val modifiedLevels = manifest.levels + dupLevel
        val modifiedManifest = manifest.copy(levels = modifiedLevels)

        val result = CatalogIntegrityChecker.checkIntegrity(
            manifest = modifiedManifest,
            assetLoader = assetLoader,
            verifyAssetContents = false
        )

        assertFalse("Integrity check must fail on duplicate levelId", result.isValid)
        assertTrue(result.errors.any { it.contains("Duplicate levelId 2") })
    }

    @Test
    fun `test integrity checker catches non-contiguous world ranges`() {
        // Change World 2 to start at 25 instead of 21 (leaving gap 21..24)
        val modifiedWorlds = manifest.worlds.map { w ->
            if (w.worldId == 2) w.copy(firstLevelId = 25) else w
        }
        val modifiedManifest = manifest.copy(worlds = modifiedWorlds)

        val result = CatalogIntegrityChecker.checkIntegrity(
            manifest = modifiedManifest,
            assetLoader = assetLoader,
            verifyAssetContents = false
        )

        assertFalse("Integrity check must fail on non-contiguous worlds", result.isValid)
        assertTrue(result.errors.any { it.contains("World 2 starts at 25, expected 21") })
    }
}
