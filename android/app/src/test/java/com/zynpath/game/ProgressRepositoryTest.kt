package com.zynpath.game

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.puzzle.model.StarRatingPolicy
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProgressRepositoryTest {

    private lateinit var levelProgressDao: FakeLevelProgressDao
    private lateinit var playerStatsDao: FakePlayerStatsDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var repository: ProgressRepositoryImpl

    @Before
    fun setUp() {
        levelProgressDao = FakeLevelProgressDao()
        playerStatsDao = FakePlayerStatsDao()
        gameSessionDao = FakeGameSessionDao()
        repository = ProgressRepositoryImpl(levelProgressDao, playerStatsDao, gameSessionDao)
    }

    @Test
    fun defaultProgress_level1IsAccessibleAndOthersFollowRules() = runTest {
        assertTrue("Level 1 must be unlocked by default for new guest", repository.isLevelUnlocked(1))
        assertFalse("Level 2 must be locked initially", repository.isLevelUnlocked(2))
        assertFalse("Level 50 must be locked initially", repository.isLevelUnlocked(50))

        assertEquals(0, repository.observeCompletedLevelCount().first())
        assertEquals(0, repository.observeTotalStarsEarned().first())
        assertEquals(1, repository.getNextPlayableLevel())
    }

    @Test
    fun recordValidatedCompletion_firstPlay_unlocksNextLevelAndRecordsStats() = runTest {
        val result = ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 12500L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1758850000L
        )

        repository.recordValidatedCompletion(result)

        // Verify Level 1 progress
        val level1 = repository.observeLevelProgress(1).first()
        assertNotNull(level1)
        assertTrue(level1!!.isCompleted)
        assertTrue(level1.isUnlocked)
        assertEquals(3, level1.stars)
        assertEquals(12500L, level1.bestTimeMs)
        assertEquals(16, level1.movesCount)
        assertEquals(0, level1.bestHintCount)
        assertEquals(1, level1.completionCount)
        assertEquals(1758850000L, level1.firstCompletedAt)
        assertEquals(1758850000L, level1.lastCompletedAt)

        // Verify Level 2 is unlocked
        assertTrue("Completing level 1 must unlock level 2", repository.isLevelUnlocked(2))

        // Verify completed level count and total stars
        assertEquals(1, repository.observeCompletedLevelCount().first())
        assertEquals(3, repository.observeTotalStarsEarned().first())
        assertEquals(2, repository.getNextPlayableLevel())
    }

    @Test
    fun recordValidatedCompletion_completingWorldFinalLevel_unlocksNextWorld() = runTest {
        // Complete first 19 levels in World 1
        for (lvl in 1..19) {
            val result = ValidatedCompletionResult(
                puzzleId = "p_4x4_$lvl",
                levelId = lvl,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 30000L,
                moveCount = 25,
                hintCount = 1,
                completedAt = 1758851000L + lvl
            )
            repository.recordValidatedCompletion(result)
        }

        // Level 21 and World 2 should still be locked
        assertFalse("Completing 19 levels must keep Level 21 locked", repository.isLevelUnlocked(21))
        assertFalse("World 2 must remain locked until level 20 is completed", repository.isWorldUnlocked(2))

        // Complete level 20 (final level of World 1)
        val finalResult = ValidatedCompletionResult(
            puzzleId = "p_4x4_20",
            levelId = 20,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 30000L,
            moveCount = 25,
            hintCount = 1,
            completedAt = 1758852000L
        )
        repository.recordValidatedCompletion(finalResult)

        // Level 21 (World 2 start) should now be unlocked
        assertTrue("Completing all 20 levels must unlock Level 21", repository.isLevelUnlocked(21))
        assertTrue("World 2 must be unlocked after completing World 1", repository.isWorldUnlocked(2))
    }

    @Test
    fun recordValidatedCompletion_replayWithSlowerTime_preservesBestTimeAndIncrementsCount() = runTest {
        // First run: 15s, 0 hints
        val run1 = ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 15000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1758850000L
        )
        repository.recordValidatedCompletion(run1)

        // Replay run: 25s, 2 hints (slower, worse)
        val run2 = ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 25000L,
            moveCount = 20,
            hintCount = 2,
            completedAt = 1758860000L
        )
        repository.recordValidatedCompletion(run2)

        val progress = repository.observeLevelProgress(1).first()!!
        assertEquals("Best time must remain 15000ms from faster run", 15000L, progress.bestTimeMs)
        assertEquals("Best moves must remain 16 from better run", 16, progress.movesCount)
        assertEquals("Best hints must remain 0 from better run", 0, progress.bestHintCount)
        assertEquals("Stars must remain 3 from better run", 3, progress.stars)
        assertEquals("Completion count must increment to 2", 2, progress.completionCount)
        assertEquals("First completion timestamp must be preserved", 1758850000L, progress.firstCompletedAt)
        assertEquals("Last completion timestamp must be updated", 1758860000L, progress.lastCompletedAt)
    }

    @Test
    fun recordValidatedCompletion_replayWithBetterTimeAndZeroHints_updatesPersonalBest() = runTest {
        // First run: 20s, 0 hints, 2 undos (2 stars)
        val run1 = ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 20000L,
            moveCount = 18,
            hintCount = 0,
            undoCount = 2,
            completedAt = 1758850000L
        )
        repository.recordValidatedCompletion(run1)

        val intermediate = repository.observeLevelProgress(1).first()!!
        assertEquals(2, intermediate.stars)
        assertEquals(20000L, intermediate.bestTimeMs)

        // Replay run: 12s, 0 hints (3 stars)
        val run2 = ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 12000L,
            moveCount = 16,
            hintCount = 0,
            undoCount = 0,
            completedAt = 1758860000L
        )
        repository.recordValidatedCompletion(run2)

        val updated = repository.observeLevelProgress(1).first()!!
        assertEquals("Stars must upgrade to 3", 3, updated.stars)
        assertEquals("Best time must upgrade to 12000ms", 12000L, updated.bestTimeMs)
        assertEquals("Best hint count must upgrade to 0", 0, updated.bestHintCount)
        assertEquals("Best moves must upgrade to 16", 16, updated.movesCount)
        assertEquals(2, updated.completionCount)
    }

    @Test
    fun starRatingPolicy_calculatesStarsAccuratelyBasedOnHints() {
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 0, timeTakenMs = 10000L))
        assertEquals(3, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 1, timeTakenMs = 10000L))
        assertEquals(2, StarRatingPolicy.calculateStars(hintCount = 0, undoResetCount = 2, timeTakenMs = 10000L))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 1, undoResetCount = 0, timeTakenMs = 10000L))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 2, undoResetCount = 0, timeTakenMs = 10000L))
        assertEquals(1, StarRatingPolicy.calculateStars(hintCount = 5, undoResetCount = 0, timeTakenMs = 50000L))
    }

    @Test(expected = IllegalArgumentException::class)
    fun recordValidatedCompletion_rejectsUnvalidatedResult() {
        ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = false,
            elapsedTimeMs = 10000L,
            moveCount = 10,
            hintCount = 0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun recordValidatedCompletion_rejectsNegativeTime() {
        ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = -500L,
            moveCount = 10,
            hintCount = 0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun recordValidatedCompletion_rejectsNegativeMoves() {
        ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 5000L,
            moveCount = -1,
            hintCount = 0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun recordValidatedCompletion_rejectsNegativeHints() {
        ValidatedCompletionResult(
            puzzleId = "p_4x4_1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 5000L,
            moveCount = 10,
            hintCount = -1
        )
    }

    @Test
    fun sessionManagement_savingAndAbandoningSession_doesNotMarkLevelCompleted() = runTest {
        // Save session
        repository.saveGameSession(
            GameSessionEntity(
                sessionId = "sess_1",
                levelId = 2,
                worldId = 1,
                puzzleSeed = 1000L,
                startedAt = 1000L,
                lastUpdatedAt = 2000L,
                elapsedActiveTimeMs = 8000L,
                status = "ACTIVE",
                pathSnapshot = "0,0;0,1;0,2"
            )
        )

        val active = repository.getActiveSession(2)
        assertNotNull(active)
        assertEquals("ACTIVE", active?.status)
        assertEquals(8000L, active?.elapsedActiveTimeMs)

        // Abandon session
        repository.abandonActiveSession(2)

        val abandoned = repository.getActiveSession(2)
        assertNull("Active session should no longer be returned after abandonment", abandoned)

        // Verify level 2 was NOT marked completed
        val level2 = repository.observeLevelProgress(2).first()
        assertFalse("Interrupted session must NOT mark level completed", level2?.isCompleted == true)
    }

    @Test
    fun getNextPlayableLevel_returnsFirstUncompletedLevel() = runTest {
        // Initially, next playable is 1
        assertEquals(1, repository.getNextPlayableLevel())

        // Complete level 1
        repository.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 10000L,
                moveCount = 16,
                hintCount = 0
            )
        )

        // Next playable is now 2
        assertEquals(2, repository.getNextPlayableLevel())
    }
}
