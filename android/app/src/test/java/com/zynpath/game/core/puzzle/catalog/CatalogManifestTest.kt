package com.zynpath.game.core.puzzle.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Manifest parsing, serialization, and integrity hashing tests.
 *
 * Implements Prompt 11 Section 11, 18, and 32:
 * - Versioned manifest structure.
 * - Authoritative 6 worlds and 300 level entries.
 * - Non-overlapping bounds and contiguity.
 * - SHA-256 integrity hash verification.
 */
class CatalogManifestTest {

    @Test
    fun `test default manifest configuration adheres to world specification`() {
        val manifest = CatalogManifest.createDefaultManifest()

        assertEquals("zynpath_solo_campaign", manifest.catalogId)
        assertEquals(CatalogVersion.CATALOG_VERSION, manifest.catalogVersion)
        assertEquals(CatalogVersion.MANIFEST_SCHEMA_VERSION, manifest.schemaVersion)
        assertEquals(6, manifest.worlds.size)
        assertEquals(300, manifest.levels.size)
        assertTrue(manifest.integrityHash.isNotBlank())

        // Check each world's level bounds
        var expectedStart = 1
        for (world in manifest.worlds) {
            assertEquals(expectedStart, world.firstLevelId)
            assertTrue(world.lastLevelId >= world.firstLevelId)
            expectedStart = world.lastLevelId + 1
        }
        assertEquals(301, expectedStart)
    }

    @Test
    fun `test manifest serialization round-trip`() {
        val original = CatalogManifest.createDefaultManifest()
        val json = CatalogManifestSerializer.serialize(original)
        assertNotNull(json)

        val deserialized = CatalogManifestSerializer.deserialize(json)

        assertEquals(original.catalogId, deserialized.catalogId)
        assertEquals(original.catalogVersion, deserialized.catalogVersion)
        assertEquals(original.schemaVersion, deserialized.schemaVersion)
        assertEquals(original.totalPlannedLevels, deserialized.totalPlannedLevels)
        assertEquals(original.worlds.size, deserialized.worlds.size)
        assertEquals(original.levels.size, deserialized.levels.size)
        assertEquals(original.integrityHash, deserialized.integrityHash)

        // Compare worlds
        for (i in original.worlds.indices) {
            val w1 = original.worlds[i]
            val w2 = deserialized.worlds[i]
            assertEquals(w1.worldId, w2.worldId)
            assertEquals(w1.displayName, w2.displayName)
            assertEquals(w1.firstLevelId, w2.firstLevelId)
            assertEquals(w1.lastLevelId, w2.lastLevelId)
            assertEquals(w1.gridDimensions, w2.gridDimensions)
            assertEquals(w1.minimumCheckpointCount, w2.minimumCheckpointCount)
            assertEquals(w1.maximumCheckpointCount, w2.maximumCheckpointCount)
            assertEquals(w1.minimumWallCount, w2.minimumWallCount)
            assertEquals(w1.maximumWallCount, w2.maximumWallCount)
        }

        // Compare sample level entries
        val l1Original = original.getLevel(1)!!
        val l1Parsed = deserialized.getLevel(1)!!
        assertEquals(l1Original.levelId, l1Parsed.levelId)
        assertEquals(l1Original.puzzleId, l1Parsed.puzzleId)
        assertEquals(l1Original.assetPath, l1Parsed.assetPath)
        assertEquals(l1Original.fingerprint, l1Parsed.fingerprint)
        assertEquals(l1Original.hasPackagedAsset, l1Parsed.hasPackagedAsset)
    }

    @Test
    fun `test integrity hash changes if level metadata altered`() {
        val original = CatalogManifest.createDefaultManifest()
        val originalHash = original.integrityHash

        val modifiedLevels = original.levels.map { lvl ->
            if (lvl.levelId == 1) lvl.copy(puzzleId = "tampered_id") else lvl
        }
        val tamperedHash = CatalogManifest.computeIntegrityHash(modifiedLevels)

        assertTrue(
            "Integrity hash must change if any level puzzleId is modified",
            originalHash != tamperedHash
        )
    }
}
