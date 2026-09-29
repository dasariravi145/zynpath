package com.zynpath.game.feature.gameplay

import com.zynpath.game.core.puzzle.model.StarRatingPolicy
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused regression tests for Solo Victory & Level Completion (Prompt 07/24).
 * Verifies star calculation policy, completion data invariants, and catalog completion bounds.
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
        // Level 300 is the final solo level in World 6
        assertEquals(300, WorldConfiguration.TOTAL_LEVELS)
        assertEquals(6, WorldConfiguration.TOTAL_WORLDS)

        val worldForFinalLevel = WorldConfiguration.getWorldForLevel(300)
        assertEquals(6, worldForFinalLevel.worldId)
        assertTrue(300 in worldForFinalLevel.levelRange)
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
}
