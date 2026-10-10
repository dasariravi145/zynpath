package com.zynpath.game.core.puzzle.curation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SoloCampaignBatchGeneratorTest {

    @Test
    fun testGenerateSampleLevelsAcrossWorlds() {
        // Test World 1 sample level
        val t0 = System.currentTimeMillis()
        val w1Asset = SoloCampaignBatchGenerator.generateUniqueLevel(1, 25)
        println("Generated W1 Lvl25 in ${System.currentTimeMillis() - t0}ms: CPs=${w1Asset.checkpoints.size}, walls=${w1Asset.blockedEdges.size}")
        assertEquals(36, w1Asset.requiredCells.size)
        assertEquals(0, w1Asset.blockedEdges.size)

        // Test World 2 sample level
        val t1 = System.currentTimeMillis()
        val w2Asset = SoloCampaignBatchGenerator.generateUniqueLevel(2, 35)
        println("Generated W2 Lvl35 in ${System.currentTimeMillis() - t1}ms: CPs=${w2Asset.checkpoints.size}, walls=${w2Asset.blockedEdges.size}")
        assertEquals(49, w2Asset.requiredCells.size)
        assertTrue(w2Asset.blockedEdges.size in 0..6)

        // Test World 3 sample level
        val t2 = System.currentTimeMillis()
        val w3Asset = SoloCampaignBatchGenerator.generateUniqueLevel(3, 55)
        println("Generated W3 Lvl55 in ${System.currentTimeMillis() - t2}ms: CPs=${w3Asset.checkpoints.size}, walls=${w3Asset.blockedEdges.size}")
        assertEquals(64, w3Asset.requiredCells.size)
        assertTrue(w3Asset.blockedEdges.size in 0..8)

        // Test World 4 sample level (9x9)
        val t3 = System.currentTimeMillis()
        val w4Asset = SoloCampaignBatchGenerator.generateUniqueLevel(4, 55)
        println("Generated W4 Lvl55 in ${System.currentTimeMillis() - t3}ms: CPs=${w4Asset.checkpoints.size}, walls=${w4Asset.blockedEdges.size}")
        assertEquals(81, w4Asset.requiredCells.size)
        assertTrue(w4Asset.blockedEdges.size in 0..10)

        // Test World 5 sample level (11x11)
        val t4 = System.currentTimeMillis()
        val w5Asset = SoloCampaignBatchGenerator.generateUniqueLevel(5, 55)
        println("Generated W5 Lvl55 in ${System.currentTimeMillis() - t4}ms: CPs=${w5Asset.checkpoints.size}, walls=${w5Asset.blockedEdges.size}")
        assertEquals(121, w5Asset.requiredCells.size)
        assertTrue(w5Asset.blockedEdges.size in 0..12)

        // Test World 6 sample level (12x12)
        val t5 = System.currentTimeMillis()
        val w6Asset = SoloCampaignBatchGenerator.generateUniqueLevel(6, 105)
        println("Generated W6 Lvl105 in ${System.currentTimeMillis() - t5}ms: CPs=${w6Asset.checkpoints.size}, walls=${w6Asset.blockedEdges.size}")
        assertEquals(144, w6Asset.requiredCells.size)
        assertTrue(w6Asset.blockedEdges.size in 0..15)
    }
}
