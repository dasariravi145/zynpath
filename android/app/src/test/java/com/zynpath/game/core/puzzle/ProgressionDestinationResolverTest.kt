package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.model.LevelCompletionState
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression and validation test suite for [ProgressionDestinationResolver].
 * Enforces the strict separation between level completion and world completion.
 * Covers all 14 scenarios specified in Prompt Section 10.
 */
class ProgressionDestinationResolverTest {

    // 1. World 1, Level 1 -> World 1, Level 2
    @Test
    fun test01_world1_level1_resolvesTo_world1_level2() {
        val resolution = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 1)
        assertEquals(1, resolution.currentWorld)
        assertEquals(1, resolution.completedLevel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resolution.completionState)
        assertTrue(resolution.hasNextLevelInCurrentWorld)
        assertEquals(2, resolution.nextLevelId)
        assertEquals("NEXT LEVEL", resolution.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(worldId = 1, levelId = 2), resolution.destination)
    }

    // 2. World 1, Level 2 -> World 1, Level 3
    @Test
    fun test02_world1_level2_resolvesTo_world1_level3() {
        val resolution = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 2)
        assertEquals(1, resolution.currentWorld)
        assertEquals(2, resolution.completedLevel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resolution.completionState)
        assertTrue(resolution.hasNextLevelInCurrentWorld)
        assertEquals(3, resolution.nextLevelId)
        assertEquals("NEXT LEVEL", resolution.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(worldId = 1, levelId = 3), resolution.destination)
    }

    // 3. World 1, Level 19 -> World 1, Level 20
    @Test
    fun test03_world1_level19_resolvesTo_world1_level20() {
        val resolution = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 19)
        assertEquals(1, resolution.currentWorld)
        assertEquals(19, resolution.completedLevel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resolution.completionState)
        assertTrue(resolution.hasNextLevelInCurrentWorld)
        assertEquals(20, resolution.nextLevelId)
        assertEquals("NEXT LEVEL", resolution.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(worldId = 1, levelId = 20), resolution.destination)
    }

    // 4. World 1, Level 20 -> World 2
    @Test
    fun test04_world1_level20_resolvesTo_world2() {
        val completedW1 = (1..20).toSet()
        val resolution = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 20, completedLevelIds = completedW1)
        assertEquals(1, resolution.currentWorld)
        assertEquals(20, resolution.completedLevel)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resolution.completionState)
        assertFalse(resolution.hasNextLevelInCurrentWorld)
        assertTrue(resolution.isCurrentWorldComplete)
        assertTrue(resolution.hasNextWorld)
        assertEquals(2, resolution.nextWorldId)
        assertNull(resolution.nextLevelId)
        assertEquals("NEXT WORLD", resolution.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(worldId = 2), resolution.destination)
    }

    // 5. World 2, first level (21) -> World 2, second level (22)
    @Test
    fun test05_world2_firstLevel_resolvesTo_world2_secondLevel() {
        val resolution = ProgressionDestinationResolver.resolve(worldId = 2, completedLevelOrLocal = 21)
        assertEquals(2, resolution.currentWorld)
        assertEquals(21, resolution.completedLevel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resolution.completionState)
        assertTrue(resolution.hasNextLevelInCurrentWorld)
        assertEquals(22, resolution.nextLevelId)
        assertEquals("NEXT LEVEL", resolution.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(worldId = 2, levelId = 22), resolution.destination)

        // Also test with local level index 1 in World 2
        val localResolution = ProgressionDestinationResolver.resolve(worldId = 2, completedLevelOrLocal = 1)
        assertEquals(2, localResolution.currentWorld)
        assertEquals(21, localResolution.completedLevel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, localResolution.completionState)
        assertEquals(22, localResolution.nextLevelId)
        assertEquals(ProgressionDestination.NextLevel(worldId = 2, levelId = 22), localResolution.destination)
    }

    // 6. Every configured World boundary
    @Test
    fun test06_everyConfiguredWorldBoundary() {
        // World 1: 1..20
        val resW1 = ProgressionDestinationResolver.resolve(completedLevelId = 20)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resW1.completionState)
        assertEquals("NEXT WORLD", resW1.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(2), resW1.destination)

        // World 2: 21..50
        val resW2 = ProgressionDestinationResolver.resolve(completedLevelId = 50)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resW2.completionState)
        assertEquals("NEXT WORLD", resW2.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(3), resW2.destination)

        // World 3: 51..100
        val resW3 = ProgressionDestinationResolver.resolve(completedLevelId = 100)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resW3.completionState)
        assertEquals("NEXT WORLD", resW3.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(4), resW3.destination)

        // World 4: 101..150
        val resW4 = ProgressionDestinationResolver.resolve(completedLevelId = 150)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resW4.completionState)
        assertEquals("NEXT WORLD", resW4.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(5), resW4.destination)

        // World 5: 151..200
        val resW5 = ProgressionDestinationResolver.resolve(completedLevelId = 200)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resW5.completionState)
        assertEquals("NEXT WORLD", resW5.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(6), resW5.destination)

        // World 6: 201..300 (Final World)
        val resW6 = ProgressionDestinationResolver.resolve(completedLevelId = 300)
        assertEquals(LevelCompletionState.JOURNEY_COMPLETED, resW6.completionState)
        assertEquals("JOURNEY COMPLETE", resW6.buttonLabel)
        assertEquals(ProgressionDestination.JourneyComplete, resW6.destination)
    }

    // 7. Final World, final level -> Journey Complete
    @Test
    fun test07_finalWorld_finalLevel_resolvesTo_journeyComplete() {
        val completedAll = (1..300).toSet()
        val resolution = ProgressionDestinationResolver.resolve(worldId = 6, completedLevelOrLocal = 300, completedLevelIds = completedAll)
        assertEquals(6, resolution.currentWorld)
        assertEquals(300, resolution.completedLevel)
        assertEquals(LevelCompletionState.JOURNEY_COMPLETED, resolution.completionState)
        assertFalse(resolution.hasNextLevelInCurrentWorld)
        assertFalse(resolution.hasNextWorld)
        assertTrue(resolution.isCurrentWorldComplete)
        assertNull(resolution.nextWorldId)
        assertNull(resolution.nextLevelId)
        assertEquals("JOURNEY COMPLETE", resolution.buttonLabel)
        assertEquals(ProgressionDestination.JourneyComplete, resolution.destination)
    }

    // 8. Replaying a completed level
    @Test
    fun test08_replayingCompletedLevel_resolvesSequentialNextLevel() {
        val completedLevels = (1..20).toSet()
        // Replaying Level 1 when Level 1..20 are all already completed
        val resReplayL1 = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 1, completedLevelIds = completedLevels)
        assertEquals("NEXT LEVEL", resReplayL1.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(worldId = 1, levelId = 2), resReplayL1.destination)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resReplayL1.completionState)

        // Replaying Level 20 when already complete
        val resReplayL20 = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 20, completedLevelIds = completedLevels)
        assertEquals("NEXT WORLD", resReplayL20.buttonLabel)
        assertEquals(ProgressionDestination.NextWorldEntry(worldId = 2), resReplayL20.destination)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, resReplayL20.completionState)
    }

    // 9. App restart and resume progression check
    @Test
    fun test09_appRestartAndResume_progressionRules() {
        val completedLevels = setOf(1, 2, 3)

        // World 1 is always unlocked
        assertTrue(WorldConfiguration.isWorldUnlocked(1, completedLevels))
        // World 2 requires all 20 levels of World 1
        assertFalse(WorldConfiguration.isWorldUnlocked(2, completedLevels))

        // Level 4 is unlocked because Level 3 is completed
        assertTrue(WorldConfiguration.isLevelUnlocked(4, completedLevels))
        // Level 5 is locked because Level 4 is not completed
        assertFalse(WorldConfiguration.isLevelUnlocked(5, completedLevels))

        // Next playable level is 4
        assertEquals(4, WorldConfiguration.getNextPlayableLevel(completedLevels))

        // Completing level 3 points to level 4
        val resL3 = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = 3, completedLevelIds = completedLevels)
        assertEquals("NEXT LEVEL", resL3.buttonLabel)
        assertEquals(ProgressionDestination.NextLevel(1, 4), resL3.destination)
    }

    // 10. Correct button text
    @Test
    fun test10_correctButtonText() {
        // Normal level: "NEXT LEVEL"
        assertEquals("NEXT LEVEL", ProgressionDestinationResolver.resolve(1, 1).buttonLabel)
        assertEquals("NEXT LEVEL", ProgressionDestinationResolver.resolve(1, 19).buttonLabel)
        assertEquals("NEXT LEVEL", ProgressionDestinationResolver.resolve(2, 25).buttonLabel)

        // Final level of World: "NEXT WORLD"
        assertEquals("NEXT WORLD", ProgressionDestinationResolver.resolve(1, 20).buttonLabel)
        assertEquals("NEXT WORLD", ProgressionDestinationResolver.resolve(2, 50).buttonLabel)
        assertEquals("NEXT WORLD", ProgressionDestinationResolver.resolve(5, 200).buttonLabel)

        // Final level of Game: "JOURNEY COMPLETE"
        assertEquals("JOURNEY COMPLETE", ProgressionDestinationResolver.resolve(6, 300).buttonLabel)
    }

    // 11. Correct button destination
    @Test
    fun test11_correctButtonDestination() {
        val resNormal = ProgressionDestinationResolver.resolve(1, 5)
        assertTrue(resNormal.destination is ProgressionDestination.NextLevel)
        assertEquals(6, (resNormal.destination as ProgressionDestination.NextLevel).levelId)

        val resWorldFinal = ProgressionDestinationResolver.resolve(1, 20)
        assertTrue(resWorldFinal.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(2, (resWorldFinal.destination as ProgressionDestination.NextWorldEntry).worldId)

        val resGameFinal = ProgressionDestinationResolver.resolve(6, 300)
        assertTrue(resGameFinal.destination is ProgressionDestination.JourneyComplete)
    }

    // 12. No automatic World navigation on normal level completion
    @Test
    fun test12_noAutomaticWorldNavigation_onNormalLevelCompletion() {
        // For levels 1..19 in World 1, destination MUST NOT be NextWorldEntry
        for (lvl in 1..19) {
            val res = ProgressionDestinationResolver.resolve(worldId = 1, completedLevelOrLocal = lvl)
            assertFalse("Level $lvl must not navigate to next world", res.destination is ProgressionDestination.NextWorldEntry)
            assertEquals("NEXT LEVEL", res.buttonLabel)
            assertEquals(1, res.currentWorld)
            assertEquals(lvl + 1, res.nextLevelId)
        }

        // For levels 21..49 in World 2, destination MUST NOT be NextWorldEntry
        for (lvl in 21..49) {
            val res = ProgressionDestinationResolver.resolve(worldId = 2, completedLevelOrLocal = lvl)
            assertFalse("Level $lvl must not navigate to next world", res.destination is ProgressionDestination.NextWorldEntry)
            assertEquals("NEXT LEVEL", res.buttonLabel)
            assertEquals(2, res.currentWorld)
            assertEquals(lvl + 1, res.nextLevelId)
        }
    }

    // 13. Distinct completion states
    @Test
    fun test13_distinctCompletionStates() {
        val normal = ProgressionDestinationResolver.resolve(1, 10)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, normal.completionState)

        val worldEnd = ProgressionDestinationResolver.resolve(1, 20)
        assertEquals(LevelCompletionState.WORLD_COMPLETED, worldEnd.completionState)

        val journeyEnd = ProgressionDestinationResolver.resolve(6, 300)
        assertEquals(LevelCompletionState.JOURNEY_COMPLETED, journeyEnd.completionState)
    }

    // 14. Existing saved guest progress compatibility
    @Test
    fun test14_existingSavedGuestProgress_compatibility() {
        // Exactly 6 worlds configured
        assertEquals(6, WorldConfiguration.WORLDS.size)
        assertEquals(300, WorldConfiguration.TOTAL_LEVELS)

        // Verify each world's level range and total count
        val expectedRanges = listOf(
            1 to (1..20),
            2 to (21..50),
            3 to (51..100),
            4 to (101..150),
            5 to (151..200),
            6 to (201..300)
        )

        for ((worldId, expectedRange) in expectedRanges) {
            val world = WorldConfiguration.getWorld(worldId)
            assertEquals(expectedRange, world.levelRange)
            assertEquals(expectedRange.count(), world.totalLevels)
        }

        // Verify World 2 unlocks only after 20 levels of World 1
        val nineteenCompleted = (1..19).toSet()
        assertFalse("World 2 must be locked after only 19 levels", WorldConfiguration.isWorldUnlocked(2, nineteenCompleted))
        val twentyCompleted = (1..20).toSet()
        assertTrue("World 2 must unlock after all 20 levels of World 1", WorldConfiguration.isWorldUnlocked(2, twentyCompleted))
    }
}
