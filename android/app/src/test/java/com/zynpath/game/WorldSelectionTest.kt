package com.zynpath.game

import com.zynpath.game.feature.level.LevelSelectionViewModel
import com.zynpath.game.feature.world.WorldSelectionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldSelectionTest {

    @Test
    fun worldConfiguration_definesAllSixWorldsAccurately() {
        val worlds = WorldSelectionViewModel.getDefaultWorlds()

        assertEquals(6, worlds.size)

        // World 1
        val w1 = worlds[0]
        assertEquals(1, w1.worldId)
        assertEquals("Learn the Path", w1.name)
        assertEquals("4×4", w1.gridSizeDescription)
        assertEquals(20, w1.totalLevels)
        assertFalse("World 1 must be unlocked by default", w1.isLocked)
        assertFalse(w1.hasWalls)

        // World 2
        val w2 = worlds[1]
        assertEquals(2, w2.worldId)
        assertEquals("Longer Connections", w2.name)
        assertEquals("5×5", w2.gridSizeDescription)
        assertEquals(30, w2.totalLevels)

        // World 3
        val w3 = worlds[2]
        assertEquals(3, w3.worldId)
        assertEquals("Wall Challenge", w3.name)
        assertEquals("5×5 with walls", w3.gridSizeDescription)
        assertEquals(50, w3.totalLevels)
        assertTrue(w3.hasWalls)

        // World 4
        val w4 = worlds[3]
        assertEquals(4, w4.worldId)
        assertEquals("Complex Routes", w4.name)
        assertEquals("6×6", w4.gridSizeDescription)
        assertEquals(50, w4.totalLevels)

        // World 5
        val w5 = worlds[4]
        assertEquals(5, w5.worldId)
        assertEquals("Advanced Logic", w5.name)
        assertEquals("7×7", w5.gridSizeDescription)
        assertEquals(50, w5.totalLevels)

        // World 6
        val w6 = worlds[5]
        assertEquals(6, w6.worldId)
        assertEquals("Expert Path", w6.name)
        assertEquals("8×8", w6.gridSizeDescription)
        assertEquals(100, w6.totalLevels)

        // Total 300 base levels
        val totalLevels = worlds.sumOf { it.totalLevels }
        assertEquals(300, totalLevels)
    }

    @Test
    fun levelRanges_partitionThreeHundredLevelsWithoutGapsOrOverlaps() {
        var expectedStart = 1

        for (worldId in 1..6) {
            val range = LevelSelectionViewModel.getLevelRange(worldId)
            assertEquals("World $worldId must start at $expectedStart", expectedStart, range.first)
            expectedStart = range.last + 1
        }

        assertEquals(301, expectedStart) // All 300 levels covered
    }
}
