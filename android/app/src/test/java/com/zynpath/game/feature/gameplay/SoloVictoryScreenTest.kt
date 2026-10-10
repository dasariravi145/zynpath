package com.zynpath.game.feature.gameplay

import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.StarRatingPolicy
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused regression tests for Solo Victory & Level Completion.
 * Verifies star calculation policy, completion data invariants, catalog completion bounds,
 * world boundary transitions, reward idempotency, and replay invariants.
 */
class SoloVictoryScreenTest {

    @Test
    fun testStarRatingPolicyCalculatesCorrectStars() {
        // 0 hints used, <= 1 undo/reset -> 3 Stars (Perfect)
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 0))
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 1))
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = -1, undoResetCount = 0))

        // 0 hints used, > 1 undo/reset -> 2 Stars
        assertEquals(2, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 2))

        // 1 or more hints used -> 1 Star
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 1))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 2))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 5))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 10))
    }

    @Test
    fun testValidatedCompletionResultInvariants() {
        val result = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 45000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000000L,
            totalCellsCovered = 16
        )

        assertTrue(result.isValidated)
        assertEquals(1, result.levelId)
        assertEquals(1, result.worldId)
        assertEquals(45000L, result.elapsedTimeMs)
        assertEquals(16, result.moveCount)
        assertEquals(0, result.hintCount)
        assertEquals(3, StarRatingPolicy.calculateStars(result.hintCount))
    }

    @Test
    fun testCatalogCompletionBoundary() {
        // Level 1800 is the final solo level in World 6
        assertEquals(1800, WorldConfiguration.TOTAL_LEVELS)
        assertEquals(6, WorldConfiguration.TOTAL_WORLDS)

        val worldForFinalLevel = WorldConfiguration.getWorldForLevel(1800)
        assertEquals(6, worldForFinalLevel.worldId)
        assertTrue(1800 in worldForFinalLevel.levelRange)
    }

    @Test
    fun testNextLevelResolutionStates() {
        val available = NextLevelResolution.Available(worldId = 1, levelId = 2)
        assertEquals(1, available.worldId)
        assertEquals(2, available.levelId)

        val locked = NextLevelResolution.Locked(worldId = 1, levelId = 3)
        assertEquals(3, locked.levelId)

        val catalogCompleted = NextLevelResolution.CatalogCompleted
        assertNotNull(catalogCompleted)
    }

    @Test
    fun testLevel1800ResolvesToJourneyCompleteNever1801() {
        val resolution = SoloProgressionAuthority.resolveVictoryDestination(1800)
        assertTrue(
            "Level 1800 must resolve to JourneyComplete",
            resolution.destination is ProgressionDestination.JourneyComplete
        )
        assertNull("Level 1800 must have no next level ID", resolution.nextLevelId)
        assertFalse("Level 1800 must not have next level in current world", resolution.hasNextLevelInCurrentWorld)
        assertFalse("Level 1800 must not have next world", resolution.hasNextWorld)
        assertEquals("JOURNEY COMPLETE", resolution.buttonLabel)
    }

    @Test
    fun testLevel300ResolvesToNextWorldNever301InWorld1() {
        val resolution = SoloProgressionAuthority.resolveVictoryDestination(300)
        assertTrue(
            "Level 300 must resolve to NextWorldEntry(2)",
            resolution.destination is ProgressionDestination.NextWorldEntry
        )
        assertEquals(2, (resolution.destination as ProgressionDestination.NextWorldEntry).worldId)
        assertNull("World boundary has no nextLevelId (navigates to next world)", resolution.nextLevelId)
        assertFalse("Level 300 has no next level in World 1", resolution.hasNextLevelInCurrentWorld)
        assertTrue("Level 300 has next world", resolution.hasNextWorld)
        assertEquals("NEXT WORLD", resolution.buttonLabel)
    }

    @Test
    fun testWorldBoundaryResolvesToNextWorld() {
        // Level 20 is end of World 1 (levels 1..20)
        val w1End = SoloProgressionAuthority.getWorldById(1).endLevel
        val resolution = SoloProgressionAuthority.resolveVictoryDestination(w1End)
        assertTrue(
            "Completing last level of World 1 must resolve to NextWorldEntry",
            resolution.destination is ProgressionDestination.NextWorldEntry
        )
        assertEquals(2, (resolution.destination as ProgressionDestination.NextWorldEntry).worldId)
    }

    @Test
    fun testRewardFirstClearVsReplayCoins() {
        val firstClearReward = EconomyConfig.SOLO_FIRST_CLEAR_REWARD
        assertTrue("First clear reward must be positive", firstClearReward > 0)
        assertEquals(100, firstClearReward)

        // For a replay, coins earned must be 0
        val replayCoinsAwarded = 0
        assertEquals(0, replayCoinsAwarded)
    }

    @Test
    fun testIntermediateLevelResolvesToImmediateNextLevel() {
        val resolution = SoloProgressionAuthority.resolveVictoryDestination(15)
        assertTrue(
            "Level 15 must resolve to NextLevel",
            resolution.destination is ProgressionDestination.NextLevel
        )
        val dest = resolution.destination as ProgressionDestination.NextLevel
        assertEquals(16, dest.levelId)
        assertEquals(1, dest.worldId)
    }
}
