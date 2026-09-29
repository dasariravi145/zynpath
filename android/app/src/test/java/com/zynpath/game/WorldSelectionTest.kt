package com.zynpath.game

import com.zynpath.game.feature.level.LevelSelectionViewModel
import com.zynpath.game.feature.world.WorldSelectionViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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
        assertEquals("7×7 with walls", w5.gridSizeDescription)
        assertEquals(50, w5.totalLevels)

        // World 6
        val w6 = worlds[5]
        assertEquals(6, w6.worldId)
        assertEquals("Expert Path", w6.name)
        assertEquals("8×8 with walls", w6.gridSizeDescription)
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

    @Test
    fun worldConfiguration_unlocksWorldsDeterministically() {
        val noCompletions = emptySet<Int>()
        assertTrue("World 1 is unlocked by default", com.zynpath.game.core.puzzle.model.WorldConfiguration.isWorldUnlocked(1, noCompletions))
        assertFalse("World 2 is locked without World 1 completions", com.zynpath.game.core.puzzle.model.WorldConfiguration.isWorldUnlocked(2, noCompletions))

        // Complete World 1 (levels 1..20)
        val world1Completed = (1..20).toSet()
        assertTrue("World 2 unlocked after completing World 1", com.zynpath.game.core.puzzle.model.WorldConfiguration.isWorldUnlocked(2, world1Completed))
        assertFalse("World 3 remains locked", com.zynpath.game.core.puzzle.model.WorldConfiguration.isWorldUnlocked(3, world1Completed))

        // Complete World 2 (levels 1..50)
        val world2Completed = (1..50).toSet()
        assertTrue("World 3 unlocked after completing World 2", com.zynpath.game.core.puzzle.model.WorldConfiguration.isWorldUnlocked(3, world2Completed))
    }

    @Test
    fun levelUnlocking_requiresPreviousLevelCompletion() {
        val noCompletions = emptySet<Int>()
        assertTrue("Level 1 unlocked by default", com.zynpath.game.core.puzzle.model.WorldConfiguration.isLevelUnlocked(1, noCompletions))
        assertFalse("Level 2 locked by default", com.zynpath.game.core.puzzle.model.WorldConfiguration.isLevelUnlocked(2, noCompletions))

        val level1Completed = setOf(1)
        assertTrue("Level 2 unlocked after level 1 completed", com.zynpath.game.core.puzzle.model.WorldConfiguration.isLevelUnlocked(2, level1Completed))
        assertFalse("Level 3 still locked", com.zynpath.game.core.puzzle.model.WorldConfiguration.isLevelUnlocked(3, level1Completed))
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun worldSelectionViewModel_updatesWorldLockStateReactively() = kotlinx.coroutines.test.runTest {
        val testDispatcher = kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        try {
            val progressRepo = com.zynpath.game.core.database.repository.ProgressRepositoryImpl(
                com.zynpath.game.fake.FakeLevelProgressDao(),
                com.zynpath.game.fake.FakePlayerStatsDao(),
                com.zynpath.game.fake.FakeGameSessionDao()
            )
            val prefsRepo = com.zynpath.game.fake.FakePreferencesRepository()
            val viewModel = WorldSelectionViewModel(progressRepo, prefsRepo)

            val job = backgroundScope.launch(testDispatcher) {
                viewModel.uiState.collect {}
            }
            testScheduler.advanceUntilIdle()

            // Initial state: World 1 unlocked, World 2 locked
            val initialWorlds = viewModel.uiState.value.worlds
            assertFalse(initialWorlds[0].isLocked)
            assertTrue(initialWorlds[1].isLocked)

            // Complete all 20 levels in World 1
            for (i in 1..20) {
                progressRepo.recordValidatedCompletion(
                    com.zynpath.game.core.puzzle.model.ValidatedCompletionResult(
                        puzzleId = "p_$i",
                        levelId = i,
                        worldId = 1,
                        isValidated = true,
                        elapsedTimeMs = 10000L,
                        moveCount = 16,
                        hintCount = 0
                    )
                )
            }
            testScheduler.advanceUntilIdle()

            val updatedWorlds = viewModel.uiState.value.worlds
            assertFalse("World 2 should now be unlocked", updatedWorlds[1].isLocked)
            assertEquals(20, updatedWorlds[0].completedLevels)
            assertEquals(60, viewModel.uiState.value.totalStarsEarned)
        } finally {
            kotlinx.coroutines.Dispatchers.resetMain()
        }
    }
}
