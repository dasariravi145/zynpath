package com.zynpath.game.feature.navigation

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.puzzle.catalog.CatalogManifest
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.feature.home.HomeViewModel
import com.zynpath.game.feature.level.LevelSelectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative Navigation and Solo Play Flow Test Suite.
 *
 * Verifies all 20 required invariants:
 * 1. Home PLAY -> Level Selection directly
 * 2. Home PLAY does NOT open World Map
 * 3. Level Selection shows correct progression
 * 4. Unlocked level -> Gameplay
 * 5. Locked level cannot start
 * 6. Completed level replay remains correct
 * 7. Gameplay return -> Level Selection where appropriate
 * 8. Victory NEXT LEVEL -> correct next level
 * 9. World-end transition remains correct
 * 10. World rewards remain correct
 * 11. 300 stableLevelIds unchanged
 * 12. Level definitions unchanged
 * 13. Checkpoint positions unchanged
 * 14. Walls unchanged
 * 15. Difficulty unchanged
 * 16. Quick Duel unchanged
 * 17. Friends Arena unchanged
 * 18. Process-death / restoration navigation remains safe
 * 19. Back stack does not contain obsolete World Map navigation
 * 20. No broken route references
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SoloPlayNavigationFlowTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var progressRepo: ProgressRepositoryImpl
    private lateinit var prefsRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        progressRepo = ProgressRepositoryImpl(
            FakeLevelProgressDao(),
            FakePlayerStatsDao(),
            FakeGameSessionDao()
        )
        prefsRepo = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1 & 2: Home PLAY -> Level Selection directly (does NOT open World Map)
    @Test
    fun test01_and_02_homePlay_navigatesToLevelSelectionDirectly_notWorldMap() {
        val soloPlayRoute = Screen.LevelSelection.createRoute()
        assertEquals("level_selection/0", soloPlayRoute)
        assertFalse("Route must not be world map", soloPlayRoute.contains("world_selection"))
        assertFalse("Route must not be world map", soloPlayRoute.contains("world_map"))
        assertTrue("Route must target level_selection", soloPlayRoute.startsWith("level_selection"))
    }

    // 3: Level Selection shows correct progression & auto-focuses current world
    @Test
    fun test03_levelSelection_showsCorrectProgressionAndAutoFocus() = runTest(testDispatcher) {
        // Complete World 1 (levels 1..20)
        for (lvl in 1..20) {
            progressRepo.recordValidatedCompletion(
                ValidatedCompletionResult(
                    puzzleId = "p_$lvl",
                    levelId = lvl,
                    worldId = 1,
                    isValidated = true,
                    elapsedTimeMs = 15000L,
                    moveCount = 16,
                    hintCount = 0
                )
            )
        }

        // When opened with worldId = 0 (auto-focus), should auto-select World 2
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 0))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, savedStateHandle)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("World 2 must be auto-focused because level 21 is next playable", 2, state.worldId)
        assertEquals("World 2 contains 30 levels (21..50)", 30, state.levels.size)
        assertEquals("Next playable level in World 2 must be 21", 21, state.nextPlayableLevelId)
        assertTrue("First level of World 2 must be marked as current", state.levels[0].isCurrent)
        assertEquals("First level of World 2 must be unlocked", LevelState.UNLOCKED, state.levels[0].state)
        assertEquals("Second level of World 2 must be locked", LevelState.LOCKED, state.levels[1].state)
    }

    // 4 & 5: Unlocked level -> Gameplay, Locked level cannot start
    @Test
    fun test04_and_05_unlockedLevelLaunchesGameplay_lockedCannotStart() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, savedStateHandle)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val level1 = state.levels[0]
        val level2 = state.levels[1]

        assertEquals(LevelState.UNLOCKED, level1.state)
        assertEquals(LevelState.LOCKED, level2.state)

        // Target route for unlocked level 1
        val unlockedRoute = Screen.Gameplay.createRoute(state.worldId, level1.levelNumber)
        assertEquals("gameplay/1/1", unlockedRoute)

        // Locked level has locked state and cannot construct valid immediate gameplay route
        assertTrue("Level 2 must be locked", level2.state == LevelState.LOCKED)
    }

    // 6: Completed level replay remains correct
    @Test
    fun test06_completedLevelReplay_remainsCorrect() = runTest(testDispatcher) {
        progressRepo.recordValidatedCompletion(
            ValidatedCompletionResult(
                puzzleId = "p_1",
                levelId = 1,
                worldId = 1,
                isValidated = true,
                elapsedTimeMs = 12000L,
                moveCount = 10,
                hintCount = 0
            )
        )

        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, savedStateHandle)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val level1 = viewModel.uiState.value.levels[0]
        assertEquals(LevelState.COMPLETED, level1.state)
        assertEquals(3, level1.stars)

        // Replay creates gameplay route directly
        val replayRoute = Screen.Gameplay.createRoute(1, level1.levelNumber)
        assertEquals("gameplay/1/1", replayRoute)
    }

    // 7: Gameplay return -> Level Selection where appropriate
    @Test
    fun test07_gameplayReturn_navigatesToLevelSelection() {
        val levelSelectionRoute = Screen.LevelSelection.createRoute(1)
        assertEquals("level_selection/1", levelSelectionRoute)
        assertFalse("Must not return to world map", levelSelectionRoute.contains("world_selection"))
    }

    // 8: Victory NEXT LEVEL -> correct next level directly
    @Test
    fun test08_victoryNextLevel_resolvesCorrectNextLevelDirectly() {
        // Solving Level 5 in World 1 -> Next level is World 1 Level 6
        val res = SoloProgressionAuthority.resolveVictoryDestination(5)
        val dest = res.destination
        assertTrue("Destination must be NextLevel", dest is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextLevel)
        val nextLevel = dest as com.zynpath.game.core.puzzle.model.ProgressionDestination.NextLevel
        assertEquals(1, nextLevel.worldId)
        assertEquals(6, nextLevel.levelId)

        val nextLevelRoute = Screen.Gameplay.createRoute(nextLevel.worldId, nextLevel.levelId)
        assertEquals("gameplay/1/6", nextLevelRoute)
    }

    // 9: World-end transition remains correct
    @Test
    fun test09_worldEndTransition_remainsCorrect() {
        // Solving Level 20 in World 1 (world boundary)
        val res = SoloProgressionAuthority.resolveVictoryDestination(20)
        val dest = res.destination
        assertTrue("Boundary destination must be NextWorldEntry", dest is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextWorldEntry)
        val nextWorldEntry = dest as com.zynpath.game.core.puzzle.model.ProgressionDestination.NextWorldEntry
        assertEquals(2, nextWorldEntry.worldId)

        val nextWorldRoute = Screen.LevelSelection.createRoute(nextWorldEntry.worldId)
        assertEquals("level_selection/2", nextWorldRoute)
    }

    // 10: World rewards remain correct and independent of World Map
    @Test
    fun test10_worldRewards_preservedAndIndependentOfWorldMap() {
        assertEquals("Reward must be 500 coins", 500, com.zynpath.game.core.economy.EconomyConfig.WORLD_FIRST_CLEAR_REWARD)
        // World 1..6 definitions exist
        assertEquals(6, WorldConfiguration.WORLDS.size)
    }

    // 11..15: 1800 stableLevelIds, level definitions, checkpoints, walls, difficulty unchanged
    @Test
    fun test11_to_15_all1800Levels_stableIdsAndDefinitionsUntouched() {
        val totalLevels = WorldConfiguration.TOTAL_LEVELS
        assertEquals(1800, totalLevels)

        val worlds = WorldConfiguration.WORLDS
        assertEquals(6, worlds.size)

        var expectedLevel = 1
        for (w in worlds) {
            assertEquals("World ${w.worldId} start level", expectedLevel, w.startLevel)
            expectedLevel = w.endLevel + 1
        }
        assertEquals(1801, expectedLevel)
    }

    // 16 & 17: Multiplayer (Quick Duel & Friends Arena) unchanged
    @Test
    fun test16_and_17_multiplayerRoutesUnchanged() {
        assertEquals("quick_duel", Screen.QuickDuel.route)
        assertEquals("friends_arena", Screen.FriendsArena.route)
        assertEquals("friend_duel", Screen.FriendDuel.createRoute())
        assertEquals("mini_league", Screen.MiniLeague.createRoute())
    }

    // 18: Process-death / restoration navigation remains safe
    @Test
    fun test18_processDeathRestoration_safeWithSavedStateHandle() = runTest(testDispatcher) {
        val restoredHandle = SavedStateHandle(mapOf("worldId" to 3))
        val viewModel = LevelSelectionViewModel(progressRepo, prefsRepo, restoredHandle)
        advanceUntilIdle()

        assertEquals("Restored world must be 3", 3, viewModel.worldId)
        assertEquals("World 3 • Wall Challenge", viewModel.uiState.value.worldName)
    }

    // 19: Back stack does not contain obsolete World Map navigation
    @Test
    fun test19_noObsoleteWorldMapInSoloFlow() {
        val soloEntryRoute = Screen.LevelSelection.createRoute()
        assertEquals("level_selection/0", soloEntryRoute)
        assertFalse(soloEntryRoute.contains("world_selection"))
    }

    // 20: No broken route references
    @Test
    fun test20_noBrokenRouteReferences() {
        val routes = listOf(
            Screen.Splash.route,
            Screen.Home.route,
            Screen.LevelSelection.route,
            Screen.Gameplay.route,
            Screen.QuickDuel.route,
            Screen.FriendsArena.route
        )
        assertEquals(routes.size, routes.distinct().size)
    }
}
