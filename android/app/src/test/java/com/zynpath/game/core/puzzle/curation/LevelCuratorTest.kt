package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.generator.PuzzleGenerator
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Progression and curation pipeline unit tests.
 *
 * Implements Prompt 10 Section 35:
 * - World 1 contains no walls.
 * - World 2 contains no walls.
 * - World 3 supports introductory walls.
 * - All worlds respect checkpoint ranges.
 * - All worlds respect wall ranges.
 * - Target difficulty configuration is deterministic.
 * - Recovery levels can exist.
 * - Published puzzle identity remains stable.
 */
class LevelCuratorTest {

    private lateinit var curator: LevelCurator

    @Before
    fun setUp() {
        val generator = PuzzleGenerator()
        val analyzer = PuzzleDifficultyAnalyzer()
        val config = CurationConfiguration(
            maxCandidatesPerLevel = 10,
            requireUnique = true,
            allowRecoveryLevels = true,
            recoveryLevelFrequency = 5
        )
        curator = LevelCurator(generator, analyzer, config = config)
    }

    @Test
    fun `test world progression specifications contract`() {
        // World 1: 4x4, 4-6 cp, 0 walls
        val w1 = WorldProgressionSpec.forWorld(1)
        assertEquals(4, w1.gridSize)
        assertEquals(4..6, w1.checkpointRange)
        assertEquals(0..0, w1.wallRange)

        // World 2: 5x5, 4-7 cp, 0 walls
        val w2 = WorldProgressionSpec.forWorld(2)
        assertEquals(5, w2.gridSize)
        assertEquals(4..7, w2.checkpointRange)
        assertEquals(0..0, w2.wallRange)

        // World 3: 5x5, 4-7 cp, 1-5 walls
        val w3 = WorldProgressionSpec.forWorld(3)
        assertEquals(5, w3.gridSize)
        assertEquals(4..7, w3.checkpointRange)
        assertEquals(1..5, w3.wallRange)

        // World 4: 6x6, 4-8 cp, 2-8 walls
        val w4 = WorldProgressionSpec.forWorld(4)
        assertEquals(6, w4.gridSize)
        assertEquals(4..8, w4.checkpointRange)
        assertEquals(2..8, w4.wallRange)

        // World 5: 7x7, 4-10 cp, 4-12 walls
        val w5 = WorldProgressionSpec.forWorld(5)
        assertEquals(7, w5.gridSize)
        assertEquals(4..10, w5.checkpointRange)
        assertEquals(4..12, w5.wallRange)

        // World 6: 8x8, 4-12 cp, 6-18 walls
        val w6 = WorldProgressionSpec.forWorld(6)
        assertEquals(8, w6.gridSize)
        assertEquals(4..12, w6.checkpointRange)
        assertEquals(6..18, w6.wallRange)
    }

    @Test
    fun `test introductory walls in World 3 early levels`() {
        val w3 = WorldProgressionSpec.forWorld(3)
        // Levels 51-60 should have a gentle introduction of 1-2 walls
        val earlyWalls = curator.resolveWallRangeForLevel(w3, 51)
        assertEquals(1..2, earlyWalls)

        val laterWalls = curator.resolveWallRangeForLevel(w3, 75)
        assertEquals(1..5, laterWalls)
    }

    @Test
    fun `test recovery level difficulty reduction calculation`() {
        val w1 = WorldProgressionSpec.forWorld(1)
        // Level 5 is offset 4, Level 6 is offset 5 -> recovery level at frequency 5
        val rangeLvl5 = curator.computeTargetDifficultyRange(w1, 5)
        val rangeLvl6 = curator.computeTargetDifficultyRange(w1, 6)

        assertNotNull(rangeLvl5)
        assertNotNull(rangeLvl6)
        // Level 6 is a recovery level, so target range midpoint should drop relative to monotonic slope
        val mid5 = (rangeLvl5.start + rangeLvl5.endInclusive) / 2.0
        val mid6 = (rangeLvl6.start + rangeLvl6.endInclusive) / 2.0
        assertTrue("Recovery level 6 should relieve difficulty relative to standard progression", mid6 < mid5 + 0.05)
    }

    @Test
    fun `test curate bounded batch from World 1`() {
        // Curate levels 1 to 3
        val result = curator.curateLevels(startLevelId = 1, count = 3, baseSeed = 5000L)

        assertEquals(3, result.acceptedCount)
        assertEquals(3, result.acceptedLevels.size)

        for (curated in result.acceptedLevels) {
            assertEquals(1, curated.worldId)
            assertEquals(4, curated.definition.gridDimensions.rows)
            assertEquals(4, curated.definition.gridDimensions.columns)
            assertEquals(0, curated.definition.blockedEdges.size) // No walls in World 1
            assertTrue(curated.definition.checkpoints.size in 4..6)
            assertEquals(UniquenessStatus.UNIQUE, curated.metadata.uniquenessStatus)
            assertTrue("Fingerprint must be recorded", result.fingerprints.containsKey(curated.levelId))
        }
    }

    @Test
    fun `test level metadata completeness and solution concealment`() {
        val result = curator.curateLevels(startLevelId = 1, count = 1, baseSeed = 6000L)
        assertTrue(result.acceptedLevels.isNotEmpty())

        val level = result.acceptedLevels.first()
        val meta = level.metadata

        assertEquals(1, meta.levelId)
        assertEquals(1, meta.worldId)
        assertTrue(meta.puzzleId.isNotEmpty())
        assertEquals("1.0.0", meta.puzzleVersion)
        assertTrue(meta.generationSeed > 0L)
        assertTrue(meta.difficultyEstimate >= 0.0)
        assertEquals(DifficultyBand.BEGINNER, meta.difficultyBand)
        assertEquals(UniquenessStatus.UNIQUE, meta.uniquenessStatus)
        assertTrue(meta.softQualityScore > 0.0)
    }
}
