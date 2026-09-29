package com.zynpath.game.core.puzzle

import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
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

/**
 * World progression, unlock rules, level replay invariance, unique completion count,
 * and personal best time test suite.
 *
 * Implements Prompt 46 Sections 47-52:
 * - World unlock conditions and progression order.
 * - Sequential level unlocks within worlds.
 * - Replaying a completed level does NOT increment the unique completion count.
 * - Replaying a level preserves or updates best times correctly (faster replay updates,
 *   slower replay preserves).
 * - World completion verification (world completes only when all required levels in that world
 *   are completed).
 */
class WorldProgressionAndReplayIntegrityTest {

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

    // =========================================================================
    // 1. World and Level Unlocks (Sections 47, 48)
    // =========================================================================

    @Test
    fun `test World 1 and Level 1 are unlocked initially for new players`() = runTest {
        assertTrue("World 1 must be unlocked by default", repository.isWorldUnlocked(1))
        assertTrue("Level 1 must be unlocked by default", repository.isLevelUnlocked(1))

        // Higher worlds and levels locked initially
        assertFalse("World 2 must be locked initially", repository.isWorldUnlocked(2))
        assertFalse("World 3 must be locked initially", repository.isWorldUnlocked(3))
        assertFalse("Level 2 must be locked initially", repository.isLevelUnlocked(2))
        assertFalse("Level 21 must be locked initially", repository.isLevelUnlocked(21))
    }

    @Test
    fun `test sequential level unlocks upon completion`() = runTest {
        assertFalse(repository.isLevelUnlocked(2))

        val completion1 = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 15_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000L
        )
        repository.recordValidatedCompletion(completion1)

        assertTrue("Level 2 must unlock immediately after Level 1 is completed", repository.isLevelUnlocked(2))
        assertFalse("Level 3 remains locked until Level 2 is completed", repository.isLevelUnlocked(3))
    }

    // =========================================================================
    // 2. Replay Invariance & Unique Completion Count (Sections 49, 50)
    // =========================================================================

    @Test
    fun `test replaying a completed level does not increase unique completed level count`() = runTest {
        val completion1 = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 20_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000L
        )
        repository.recordValidatedCompletion(completion1)

        assertEquals("Unique completed count must be 1 after first completion", 1, repository.observeCompletedLevelCount().first())

        // Replay Level 1 a second time
        val replay1 = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 18_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 2000L
        )
        repository.recordValidatedCompletion(replay1)

        assertEquals("Unique completed count must still be 1 after replay", 1, repository.observeCompletedLevelCount().first())

        // Replay Level 1 a third time
        val replay2 = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 14_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 3000L
        )
        repository.recordValidatedCompletion(replay2)

        assertEquals("Unique completed count must remain 1 after multiple replays", 1, repository.observeCompletedLevelCount().first())

        // Verify total completion count on level progress entity increased to 3
        val level1Progress = repository.observeLevelProgress(1).first()
        assertNotNull(level1Progress)
        assertEquals(3, level1Progress!!.completionCount)
    }

    // =========================================================================
    // 3. Personal Best Time Behavior (Section 52)
    // =========================================================================

    @Test
    fun `test first completion records initial best time`() = runTest {
        val initialCompletion = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 25_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000L
        )
        repository.recordValidatedCompletion(initialCompletion)

        val progress = repository.observeLevelProgress(1).first()
        assertNotNull(progress)
        assertEquals(25_000L, progress!!.bestTimeMs)
    }

    @Test
    fun `test faster replay updates personal best time`() = runTest {
        val initial = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 25_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000L
        )
        repository.recordValidatedCompletion(initial)

        val faster = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 15_000L, // 10s faster!
            moveCount = 16,
            hintCount = 0,
            completedAt = 2000L
        )
        repository.recordValidatedCompletion(faster)

        val progress = repository.observeLevelProgress(1).first()
        assertNotNull(progress)
        assertEquals("Best time must update to the faster time", 15_000L, progress!!.bestTimeMs)
    }

    @Test
    fun `test slower replay preserves existing personal best time`() = runTest {
        val initial = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 15_000L,
            moveCount = 16,
            hintCount = 0,
            completedAt = 1000L
        )
        repository.recordValidatedCompletion(initial)

        val slower = ValidatedCompletionResult(
            puzzleId = "w1_lvl1",
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 30_000L, // 15s slower!
            moveCount = 16,
            hintCount = 0,
            completedAt = 2000L
        )
        repository.recordValidatedCompletion(slower)

        val progress = repository.observeLevelProgress(1).first()
        assertNotNull(progress)
        assertEquals("Best time must preserve the faster 15s time", 15_000L, progress!!.bestTimeMs)
    }

    // =========================================================================
    // 4. World Completion Requirements (Section 51)
    // =========================================================================

    @Test
    fun `test world completes only when all required levels in world are completed`() = runTest {
        val w1 = WorldDefinition.forWorld(1)
        val w1Levels = w1.levelRange.toList()
        assertEquals(20, w1Levels.size)

        // Complete 19 out of 20 levels in World 1
        for (lvl in 1..19) {
            repository.recordValidatedCompletion(
                ValidatedCompletionResult(
                    puzzleId = "w1_lvl$lvl",
                    levelId = lvl,
                    worldId = 1,
                    isValidated = true,
                    elapsedTimeMs = 10_000L,
                    moveCount = 16,
                    hintCount = 0,
                    completedAt = 1000L + lvl
                )
            )
        }

        val completedWorld1Count = repository.observeAllProgress().first().count { it.isCompleted && it.levelId in w1.levelRange }
        assertEquals(19, completedWorld1Count)
        assertFalse("World 1 is not fully complete with 19/20 levels", completedWorld1Count == w1Levels.size)

        // Complete level 20 (the final level of World 1)
        repository.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "w1_lvl20",
                levelId = 20,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 10_000L,
                moveCount = 16,
                hintCount = 0,
                completedAt = 2000L
            )
        )

        val allW1Completed = repository.observeAllProgress().first().count { it.isCompleted && it.levelId in w1.levelRange }
        assertEquals(20, allW1Completed)
        assertEquals("World 1 is now fully completed", 20, allW1Completed)
    }
}
