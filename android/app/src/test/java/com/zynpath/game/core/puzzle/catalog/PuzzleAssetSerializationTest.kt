package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Serialization round-trip and validation tests for [PuzzleAsset] and [PuzzleAssetSerializer].
 *
 * Implements Prompt 11 Section 10, 15, and 16:
 * - Deterministic JSON output.
 * - Normalized wall endpoints and coordinates.
 * - Lossless serialization round-trips.
 * - Robust error handling for corrupt or malformed JSON.
 */
class PuzzleAssetSerializationTest {

    @Test
    fun `test Level 1 serialization round-trip without walls`() {
        val def = PackagedPuzzles.LEVEL_1
        val json = CatalogAssetGenerator.generateAssetJson(1)
        assertNotNull(json)

        val deserializedAsset = PuzzleAssetSerializer.deserialize(json!!)
        val roundTripDef = deserializedAsset.toPuzzleDefinition()

        assertEquals(def.puzzleId, roundTripDef.puzzleId)
        assertEquals(def.gridDimensions, roundTripDef.gridDimensions)
        assertEquals(def.requiredCells, roundTripDef.requiredCells)
        assertEquals(def.checkpoints.size, roundTripDef.checkpoints.size)
        assertEquals(def.blockedEdges, roundTripDef.blockedEdges)

        assertTrue(
            "Puzzle definitions must be semantically identical",
            PuzzleFingerprint.areDuplicates(def, roundTripDef)
        )
    }

    @Test
    fun `test World 3 Level 51 serialization round-trip with walls`() {
        val def = PackagedPuzzles.LEVEL_51
        val json = CatalogAssetGenerator.generateAssetJson(51)
        assertNotNull(json)

        val deserializedAsset = PuzzleAssetSerializer.deserialize(json!!)
        val roundTripDef = deserializedAsset.toPuzzleDefinition()

        assertEquals(def.puzzleId, roundTripDef.puzzleId)
        assertEquals(def.gridDimensions, roundTripDef.gridDimensions)
        assertEquals(1, roundTripDef.blockedEdges.size)
        assertEquals(def.blockedEdges, roundTripDef.blockedEdges)

        assertTrue(
            "Puzzle definitions with walls must preserve identical topology",
            PuzzleFingerprint.areDuplicates(def, roundTripDef)
        )
    }

    @Test
    fun `test all packaged puzzles serialization round-trip`() {
        for ((levelId, originalDef) in PackagedPuzzles.ALL_PACKAGED) {
            val json = CatalogAssetGenerator.generateAssetJson(levelId)
            assertNotNull("JSON generation must succeed for level $levelId", json)

            val parsedAsset = PuzzleAssetSerializer.deserialize(json!!)
            val reconstructed = parsedAsset.toPuzzleDefinition()

            assertEquals("Puzzle ID must match for level $levelId", originalDef.puzzleId, reconstructed.puzzleId)
            assertEquals("Grid dimensions must match for level $levelId", originalDef.gridDimensions, reconstructed.gridDimensions)
            assertEquals("Checkpoints must match for level $levelId", originalDef.checkpoints.size, reconstructed.checkpoints.size)
            assertEquals("Blocked edges must match for level $levelId", originalDef.blockedEdges.size, reconstructed.blockedEdges.size)
            assertTrue(
                "Fingerprint must match after round trip for level $levelId",
                PuzzleFingerprint.areDuplicates(originalDef, reconstructed)
            )
        }
    }

    @Test
    fun `test blocked edge normalization on deserialization`() {
        // Construct wall where first > second to test normalization
        val reversedWallJson = """
        {
          "schemaVersion": "1.0.0",
          "puzzleId": "test_norm",
          "puzzleVersion": 1,
          "gridDimensions": {"rows": 4, "columns": 4},
          "requiredCells": [{"row": 0, "column": 0}, {"row": 0, "column": 1}],
          "checkpoints": [{"number": 1, "row": 0, "column": 0}, {"number": 2, "row": 0, "column": 1}],
          "blockedEdges": [
            {
              "first": {"row": 1, "column": 0},
              "second": {"row": 0, "column": 0}
            }
          ]
        }
        """.trimIndent()

        val asset = PuzzleAssetSerializer.deserialize(reversedWallJson)
        val edge = asset.blockedEdges.first()
        val expected = BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0))

        assertEquals(expected.first, edge.first)
        assertEquals(expected.second, edge.second)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test deserialization rejects invalid JSON missing checkpoints`() {
        val invalidJson = """
        {
          "puzzleId": "bad",
          "gridDimensions": {"rows": 4, "columns": 4}
        }
        """.trimIndent()

        PuzzleAssetSerializer.deserialize(invalidJson)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test deserialization rejects non-object root`() {
        val arrayJson = """["item1", "item2"]"""
        PuzzleAssetSerializer.deserialize(arrayJson)
    }
}
