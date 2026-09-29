package com.zynpath.game.core.puzzle

import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative Regression Test Suite for World Progression and Level Unlocks.
 *
 * Covers all 10 required real-device regression test scenarios:
 * 1. World 1 Level 1 → World 1 Level 2
 * 2. World 1 Level 2 → World 1 Level 3
 * 3. World 1 Level 19 → World 1 Level 20
 * 4. World 1 Level 20 → World 2 Level 1
 * 5. World 2 Level 1 → World 2 Level 2
 * 6. Every configured world boundary (W1:20->W2:21, W2:50->W3:51, W3:100->W4:101, W4:150->W5:151, W5:200->W6:201)
 * 7. Replaying completed levels does not incorrectly unlock worlds
 * 8. App restart preserves the correct world and level
 * 9. Next Level and World Map show consistent progression
 * 10. Existing guest progress remains intact and premature unlock reconciliation works non-destructively
 */
class WorldProgressionRegressionTest {

    private lateinit var levelProgressDao: FakeLevelProgressDao
    private lateinit var playerStatsDao: FakePlayerStatsDao
    private lateinit var gameSessionDao: FakeGameSessionDao
    private lateinit var repository: ProgressRepositoryImpl
    private lateinit var preferencesRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        levelProgressDao = FakeLevelProgressDao()
        playerStatsDao = FakePlayerStatsDao()
        gameSessionDao = FakeGameSessionDao()
        repository = ProgressRepositoryImpl(levelProgressDao, playerStatsDao, gameSessionDao)
        preferencesRepo = FakePreferencesRepository()
    }

    private fun createCompletion(
        levelId: Int,
        worldId: Int = WorldConfiguration.getWorldForLevel(levelId).worldId,
        timeMs: Long = 12_000L,
        hints: Int = 0
    ) = ValidatedCompletionResult(
        puzzleId = "puzzle_w${worldId}_lvl$levelId",
        levelId = levelId,
        worldId = worldId,
        isValidated = true,
        elapsedTimeMs = timeMs,
        moveCount = 16,
        hintCount = hints,
        completedAt = 1_000_000L + levelId
    )

    // =========================================================================
    // Scenario 1: World 1 Level 1 → World 1 Level 2
    // =========================================================================
    @Test
    fun testScenario1_world1Level1Completes_unlocksLevel2WithinSameWorld() = runTest {
        assertTrue("W1 L1 must be unlocked initially", repository.isLevelUnlocked(1))
        assertFalse("W1 L2 must be locked initially", repository.isLevelUnlocked(2))
        assertFalse("World 2 must be locked initially", repository.isWorldUnlocked(2))

        repository.recordValidatedCompletion(createCompletion(levelId = 1, worldId = 1))

        assertTrue("W1 L2 must unlock after W1 L1 completion", repository.isLevelUnlocked(2))
        assertFalse("W1 L3 must remain locked", repository.isLevelUnlocked(3))
        assertFalse("World 2 must remain locked", repository.isWorldUnlocked(2))

        val nextPlayable = repository.getNextPlayableLevel()
        assertEquals("Next playable level must be 2", 2, nextPlayable)
        val nextWorld = WorldConfiguration.getWorldForLevel(nextPlayable)
        assertEquals("Next level must stay in World 1", 1, nextWorld.worldId)
        assertEquals("Local level for 2 in World 1 must be 2", 2, WorldConfiguration.toLocalLevel(2))
    }

    // =========================================================================
    // Scenario 2: World 1 Level 2 → World 1 Level 3
    // =========================================================================
    @Test
    fun testScenario2_world1Level2Completes_unlocksLevel3WithinSameWorld() = runTest {
        repository.recordValidatedCompletion(createCompletion(levelId = 1, worldId = 1))
        repository.recordValidatedCompletion(createCompletion(levelId = 2, worldId = 1))

        assertTrue("W1 L3 must unlock after W1 L2 completion", repository.isLevelUnlocked(3))
        assertFalse("W1 L4 must remain locked", repository.isLevelUnlocked(4))
        assertFalse("World 2 must remain locked", repository.isWorldUnlocked(2))

        val nextPlayable = repository.getNextPlayableLevel()
        assertEquals("Next playable level must be 3", 3, nextPlayable)
        assertEquals("Next level must stay in World 1", 1, WorldConfiguration.getWorldForLevel(nextPlayable).worldId)
        assertEquals("Local level for 3 in World 1 must be 3", 3, WorldConfiguration.toLocalLevel(3))
    }

    // =========================================================================
    // Scenario 3: World 1 Level 19 → World 1 Level 20
    // =========================================================================
    @Test
    fun testScenario3_world1Level19Completes_unlocksLevel20_world2RemainsLocked() = runTest {
        for (lvl in 1..19) {
            repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = 1))
        }

        assertTrue("W1 L20 must be unlocked after completing level 19", repository.isLevelUnlocked(20))
        assertFalse("World 2 must remain locked with only 19 levels completed", repository.isWorldUnlocked(2))
        assertFalse("Level 21 (World 2 Level 1) must remain locked", repository.isLevelUnlocked(21))

        val nextPlayable = repository.getNextPlayableLevel()
        assertEquals("Next playable level must be 20", 20, nextPlayable)
        assertEquals("Next level must stay in World 1", 1, WorldConfiguration.getWorldForLevel(nextPlayable).worldId)
        assertEquals("Local level for 20 in World 1 must be 20", 20, WorldConfiguration.toLocalLevel(20))
    }

    // =========================================================================
    // Scenario 4: World 1 Level 20 → World 2 Level 1
    // =========================================================================
    @Test
    fun testScenario4_world1Level20Completes_unlocksWorld2AndLevel21() = runTest {
        for (lvl in 1..20) {
            repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = 1))
        }

        assertTrue("World 2 must unlock only after all 20 levels in World 1 are completed", repository.isWorldUnlocked(2))
        assertTrue("Level 21 (World 2 start) must be unlocked", repository.isLevelUnlocked(21))
        assertFalse("Level 22 must remain locked", repository.isLevelUnlocked(22))
        assertFalse("World 3 must remain locked", repository.isWorldUnlocked(3))

        val nextPlayable = repository.getNextPlayableLevel()
        assertEquals("Next playable level must advance to global level 21", 21, nextPlayable)
        val nextWorld = WorldConfiguration.getWorldForLevel(nextPlayable)
        assertEquals("Next world must be World 2", 2, nextWorld.worldId)
        assertEquals("Local level for global level 21 in World 2 must be 1", 1, WorldConfiguration.toLocalLevel(21))
        assertEquals("Formatted title must be World 2 • Level 1", "World 2 • Level 1", WorldConfiguration.formatLevelTitle(2, 21))
    }

    // =========================================================================
    // Scenario 5: World 2 Level 1 → World 2 Level 2
    // =========================================================================
    @Test
    fun testScenario5_world2Level1Completes_unlocksWorld2Level2() = runTest {
        // Complete World 1 (1..20)
        for (lvl in 1..20) {
            repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = 1))
        }

        // Complete World 2 Level 1 (global level 21)
        repository.recordValidatedCompletion(createCompletion(levelId = 21, worldId = 2))

        assertTrue("Global level 22 (World 2 Level 2) must be unlocked", repository.isLevelUnlocked(22))
        assertFalse("Global level 23 must remain locked", repository.isLevelUnlocked(23))
        assertFalse("World 3 must remain locked", repository.isWorldUnlocked(3))

        val nextPlayable = repository.getNextPlayableLevel()
        assertEquals("Next playable level must be global 22", 22, nextPlayable)
        assertEquals("Next world must stay in World 2", 2, WorldConfiguration.getWorldForLevel(nextPlayable).worldId)
        assertEquals("Local level for global 22 must be 2", 2, WorldConfiguration.toLocalLevel(22))
        assertEquals("Formatted title must be World 2 • Level 2", "World 2 • Level 2", WorldConfiguration.formatLevelTitle(2, 22))
    }

    // =========================================================================
    // Scenario 6: Every Configured World Boundary
    // =========================================================================
    @Test
    fun testScenario6_everyConfiguredWorldBoundaryProgression() = runTest {
        val worlds = WorldConfiguration.WORLDS
        assertEquals("Total configured worlds must be 6", 6, worlds.size)

        var completedSoFar = 0
        for (wIndex in 0 until worlds.size - 1) {
            val currentWorld = worlds[wIndex]
            val nextWorld = worlds[wIndex + 1]

            // Complete all levels except the last level of the current world
            for (lvl in currentWorld.startLevel until currentWorld.endLevel) {
                repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = currentWorld.worldId))
                completedSoFar++
            }

            // Next world must still be locked
            assertFalse(
                "World ${nextWorld.worldId} must remain locked before final level of World ${currentWorld.worldId} is solved",
                repository.isWorldUnlocked(nextWorld.worldId)
            )
            assertFalse(
                "Level ${nextWorld.startLevel} must remain locked",
                repository.isLevelUnlocked(nextWorld.startLevel)
            )

            // Now complete the final level of current world
            repository.recordValidatedCompletion(
                createCompletion(levelId = currentWorld.endLevel, worldId = currentWorld.worldId)
            )
            completedSoFar++

            // Next world and its first level must now be unlocked
            assertTrue(
                "World ${nextWorld.worldId} must unlock after completing all ${currentWorld.totalLevels} levels of World ${currentWorld.worldId}",
                repository.isWorldUnlocked(nextWorld.worldId)
            )
            assertTrue(
                "First level of World ${nextWorld.worldId} (level ${nextWorld.startLevel}) must be unlocked",
                repository.isLevelUnlocked(nextWorld.startLevel)
            )
            assertEquals(
                "Local level for first level of World ${nextWorld.worldId} must be 1",
                1,
                WorldConfiguration.toLocalLevel(nextWorld.startLevel)
            )
        }
    }

    // =========================================================================
    // Scenario 7: Replaying Completed Levels Does Not Unlock Worlds
    // =========================================================================
    @Test
    fun testScenario7_replayingCompletedLevelsDoesNotUnlockWorldsPrematurely() = runTest {
        // Replay Level 1 twenty times with different times
        for (i in 1..20) {
            repository.recordValidatedCompletion(
                createCompletion(levelId = 1, worldId = 1, timeMs = 10_000L + i * 100)
            )
        }

        assertEquals("Unique completed count must still be 1", 1, repository.observeCompletedLevelCount().first())
        assertFalse("World 2 must remain locked after replaying Level 1", repository.isWorldUnlocked(2))
        assertFalse("Level 21 must remain locked", repository.isLevelUnlocked(21))
        assertTrue("Level 2 must be unlocked", repository.isLevelUnlocked(2))
        assertFalse("Level 3 must remain locked", repository.isLevelUnlocked(3))
    }

    // =========================================================================
    // Scenario 8: App Restart Preserves Correct World and Level
    // =========================================================================
    @Test
    fun testScenario8_appRestartPreservesCorrectWorldAndLevel() = runTest {
        // Complete levels 1..5 in World 1
        for (lvl in 1..5) {
            repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = 1))
        }

        // Persist preferences as gameplay does
        preferencesRepo.setLastSelectedWorld(1)
        preferencesRepo.setLastSelectedLevel(5)

        // Simulate app restart by creating a new repository referencing the same underlying DAOs
        val restartedRepo = ProgressRepositoryImpl(levelProgressDao, playerStatsDao, gameSessionDao)

        assertEquals("Completed count must be preserved across restart", 5, restartedRepo.observeCompletedLevelCount().first())
        assertEquals("Total stars must be preserved across restart", 15, restartedRepo.observeTotalStarsEarned().first())
        assertTrue("Levels 1..5 must remain completed", (1..5).all { restartedRepo.observeLevelProgress(it).first()?.isCompleted == true })
        assertTrue("Level 6 must be unlocked as the next playable", restartedRepo.isLevelUnlocked(6))
        assertFalse("Level 7 must remain locked", restartedRepo.isLevelUnlocked(7))
        assertFalse("World 2 must remain locked", restartedRepo.isWorldUnlocked(2))

        val prefs = preferencesRepo.userPreferencesFlow.first()
        assertEquals("Last selected world must be 1", 1, prefs.lastSelectedWorld)
        assertEquals("Last selected level must be 5", 5, prefs.lastSelectedLevel)
    }

    // =========================================================================
    // Scenario 9: Next Level and World Map Show Consistent Progression
    // =========================================================================
    @Test
    fun testScenario9_nextLevelAndWorldMapShowConsistentProgression() = runTest {
        // Complete World 1 (1..20)
        for (lvl in 1..20) {
            repository.recordValidatedCompletion(createCompletion(levelId = lvl, worldId = 1))
        }

        val allProgress = repository.observeAllProgress().first()
        val completedIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()

        val nextLevelId = WorldConfiguration.getNextPlayableLevel(completedIds)
        val nextWorld = WorldConfiguration.getWorldForLevel(nextLevelId)

        assertEquals("Next level across all progress must be 21", 21, nextLevelId)
        assertEquals("Next world must be 2", 2, nextWorld.worldId)
        assertEquals("Local level for Next Level in World 2 must be 1", 1, WorldConfiguration.toLocalLevel(nextLevelId))

        // World Map selection check
        assertTrue("World 1 is unlocked", WorldConfiguration.isWorldUnlocked(1, completedIds))
        assertTrue("World 2 is unlocked", WorldConfiguration.isWorldUnlocked(2, completedIds))
        assertFalse("World 3 is locked", WorldConfiguration.isWorldUnlocked(3, completedIds))

        val w1CompletedCount = completedIds.count { it in WorldConfiguration.getWorld(1)!!.levelRange }
        assertEquals("World 1 map must show 20/20 solved", 20, w1CompletedCount)

        val w2CompletedCount = completedIds.count { it in WorldConfiguration.getWorld(2)!!.levelRange }
        assertEquals("World 2 map must show 0/30 solved", 0, w2CompletedCount)
    }

    // =========================================================================
    // Scenario 10: Existing Guest Progress Remains Intact & Non-Destructive Reconciliation
    // =========================================================================
    @Test
    fun testScenario10_existingGuestProgressRemainsIntactAndReconcilesNonDestructively() = runTest {
        // Simulate existing legitimate guest progress: levels 1..5 completed with stars and best times
        for (lvl in 1..5) {
            repository.recordValidatedCompletion(
                createCompletion(levelId = lvl, worldId = 1, timeMs = 15_000L, hints = 0)
            )
        }

        // Simulate a premature unlock record that was incorrectly created by the old progression bug:
        // (Level 21 uncompleted was inserted into Room)
        levelProgressDao.upsertLevelProgress(
            LevelProgressEntity(
                levelId = 21,
                worldId = 2,
                isCompleted = false,
                isUnlocked = true,
                stars = 0,
                bestTimeMs = 0L,
                completionCount = 0,
                firstCompletedAt = null,
                lastCompletedAt = null
            )
        )

        // Verify state prior to reconciliation
        assertEquals(5, repository.observeCompletedLevelCount().first())
        assertEquals(15, repository.observeTotalStarsEarned().first())

        // Run non-destructive reconciliation
        repository.reconcilePrematureUnlocks()

        // 1. Verify legitimate completed levels are untouched
        for (lvl in 1..5) {
            val progress = repository.observeLevelProgress(lvl).first()
            assertNotNull("Level $lvl progress must be intact", progress)
            assertTrue("Level $lvl must remain completed", progress!!.isCompleted)
            assertEquals("Level $lvl stars must remain 3", 3, progress.stars)
            assertEquals("Level $lvl best time must remain 15000", 15_000L, progress.bestTimeMs)
        }

        // 2. Total stars and completed count must not have changed
        assertEquals("Completed level count must remain 5", 5, repository.observeCompletedLevelCount().first())
        assertEquals("Total stars earned must remain 15", 15, repository.observeTotalStarsEarned().first())

        // 3. Level 21 must be safely reconciled back to locked since World 1 is not yet complete
        assertFalse("Prematurely unlocked Level 21 must be relocked by reconciliation", repository.isLevelUnlocked(21))
        assertFalse("World 2 must remain locked until World 1 has all 20 levels complete", repository.isWorldUnlocked(2))

        // 4. Next playable level should correctly be 6 in World 1
        assertEquals(6, repository.getNextPlayableLevel())
    }
}
