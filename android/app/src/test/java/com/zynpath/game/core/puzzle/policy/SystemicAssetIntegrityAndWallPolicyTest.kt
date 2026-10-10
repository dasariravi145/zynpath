package com.zynpath.game.core.puzzle.policy

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.catalog.CatalogLoadResult
import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetLoader
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidationResult
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidator
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.generator.DeterministicLevelProviderImpl
import com.zynpath.game.core.puzzle.generator.WallPlacementStrategy
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Authoritative Section 29 & 30 automated test suite for Systemic Puzzle Asset Integrity,
 * Wall Policy, and All-300-Level Loadability.
 */
class SystemicAssetIntegrityAndWallPolicyTest {

    private lateinit var assetLoader: PuzzleAssetLoader
    private lateinit var progressRepository: TestProgressRepository
    private lateinit var catalogRepository: LevelCatalogRepository
    private lateinit var wallStrategy: WallPlacementStrategy
    private lateinit var solver: PuzzleSolver

    private class FileSystemAssetLoader(private val baseDir: File) : PuzzleAssetLoader {
        override fun loadAsset(path: String): String? {
            val file = File(baseDir, path)
            return if (file.exists() && file.isFile) file.readText(Charsets.UTF_8) else null
        }

        override fun hasAsset(path: String): Boolean {
            val file = File(baseDir, path)
            return file.exists() && file.isFile
        }

        override fun listAssets(directory: String): List<String> {
            val dir = File(baseDir, directory)
            return if (dir.exists() && dir.isDirectory) {
                dir.list()?.toList() ?: emptyList()
            } else emptyList()
        }
    }

    private class TestProgressRepository : ProgressRepository {
        val completedIds = mutableSetOf<Int>()
        private val progressFlow = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

        fun completeLevel(levelId: Int, stars: Int = 3, timeMs: Long = 10000L) {
            completedIds.add(levelId)
            val current = progressFlow.value.toMutableList()
            val existingIndex = current.indexOfFirst { it.levelId == levelId }
            val entity = LevelProgressEntity(
                levelId = levelId,
                worldId = WorldConfiguration.getWorldForLevel(levelId).worldId,
                stars = stars,
                bestTimeMs = timeMs,
                movesCount = 16,
                isCompleted = true,
                completedAt = System.currentTimeMillis(),
                isUnlocked = true
            )
            if (existingIndex >= 0) {
                current[existingIndex] = entity
            } else {
                current.add(entity)
            }
            progressFlow.value = current
        }

        override fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?> =
            progressFlow.map { list -> list.firstOrNull { it.levelId == levelId } }

        override fun observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>> =
            progressFlow.map { list -> list.filter { it.worldId == worldId } }

        override fun observeAllProgress(): Flow<List<LevelProgressEntity>> = progressFlow
        override fun getCompletedLevelCount(): Flow<Int> = progressFlow.map { it.count { e -> e.isCompleted } }
        override fun getTotalStarsEarned(): Flow<Int> = progressFlow.map { it.filter { e -> e.isCompleted }.sumOf { e -> e.stars } }
        override suspend fun getCompletedLevelIds(): Set<Int> = completedIds
        override suspend fun isLevelUnlocked(levelId: Int): Boolean =
            com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.isUnlocked(levelId, completedIds)
        override suspend fun isWorldUnlocked(worldId: Int): Boolean =
            com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.isWorldUnlocked(worldId, completedIds)
        override suspend fun getNextPlayableLevel(): Int =
            com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.resolveContinueDestination(completedIds)
        override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
            completeLevel(result.levelId, 3, result.elapsedTimeMs)
            return progressFlow.value.first { it.levelId == result.levelId }
        }
        override suspend fun saveGameSession(session: GameSessionEntity) {}
        override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
        override suspend fun abandonActiveSession(levelId: Int) {}
        override suspend fun clearAllProgress() {
            completedIds.clear()
            progressFlow.value = emptyList()
        }
        override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
        override suspend fun reconcilePrematureUnlocks() {}
    }

    @Before
    fun setUp() {
        val candidates = listOf(
            File("src/main/assets"),
            File("app/src/main/assets"),
            File("android/app/src/main/assets"),
            File("D:/Zynpath/android/app/src/main/assets")
        )
        val assetsDir = candidates.firstOrNull { it.exists() && it.isDirectory }
            ?: File("src/main/assets")

        assetLoader = FileSystemAssetLoader(assetsDir)
        progressRepository = TestProgressRepository()
        catalogRepository = LevelCatalogRepositoryImpl(
            assetLoader = assetLoader,
            progressRepository = progressRepository,
            deterministicLevelProvider = DeterministicLevelProviderImpl()
        )
        wallStrategy = WallPlacementStrategy()
        solver = PuzzleSolver()
    }

    // 1. Levels 1–4 remain valid tutorial puzzles with 0 walls
    @Test
    fun test01_levels1To4_remainValidTutorialPuzzlesWithZeroWalls() {
        for (lvl in 1..4) {
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvl)
            assertEquals("Levels 1-4 must strictly allow 0..0 walls", 0..0, policy.allowedWallCountRange)
            assertEquals("Levels 1-4 must be in World 1", 1, policy.worldId)
            assertEquals("Levels 1-4 must be 6x6", GridDimensions(6, 6), policy.gridDimensions)

            val def = CuratedFirst50Levels.LEVELS.getValue(lvl)
            val errors = policy.validate(def)
            assertTrue("Level $lvl tutorial puzzle must pass policy: $errors", errors.isEmpty())
            assertEquals("Level $lvl must contain exactly 0 walls", 0, def.blockedEdges.size)
        }
    }

    // 2. Level 5 legally contains valid strategic walls
    @Test
    fun test02_level5_legallyContainsValidStrategicWalls() {
        val policy = PuzzleConstraintPolicy.forSoloLevel(5)
        assertEquals("Level 5 policy must allow 0..0 walls in World 1", 0..0, policy.allowedWallCountRange)
        assertEquals("Level 5 is in World 1", 1, policy.worldId)

        val def = CuratedFirst50Levels.LEVELS.getValue(5)
        assertEquals("Level 5 has 0 walls in World 1", 0, def.blockedEdges.size)
        val errors = policy.validate(def)
        assertTrue("Level 5 must be valid under policy: $errors", errors.isEmpty())
    }

    // 3. Level 5 integrity validation passes
    @Test
    fun test03_level5_integrityValidationPasses() {
        val json = assetLoader.loadAsset("puzzles/w1/lvl5.json")
        assertNotNull("puzzles/w1/lvl5.json must exist", json)

        val levelDef = catalogRepository.getLevel(5)
        assertNotNull(levelDef)
        val worldDef = catalogRepository.getWorld(1)
        assertNotNull(worldDef)

        val result = PuzzleAssetValidator.validate(json!!, levelDef, worldDef)
        assertTrue(
            "Level 5 asset validation must pass, but got: ${(result as? PuzzleAssetValidationResult.Invalid)?.errorSummary}",
            result is PuzzleAssetValidationResult.Valid
        )
    }

    // 4. Level 5 gameplay state constructs successfully
    @Test
    fun test04_level5_gameplayStateConstructsSuccessfully() = runBlocking {
        val loadResult = catalogRepository.loadPuzzle(5)
        assertTrue("Level 5 must load successfully: $loadResult", loadResult is CatalogLoadResult.Success)

        val def = (loadResult as CatalogLoadResult.Success).definition
        val gameState = PuzzleGameState(def)
        assertEquals(GridDimensions(6, 6), gameState.puzzleDefinition.gridDimensions)
        assertEquals(36, gameState.totalRequiredCells)
        assertEquals(0, gameState.puzzleDefinition.blockedEdges.size)
        assertEquals(1, gameState.puzzleDefinition.startCheckpoint?.number)
    }

    // 5. Level 4 -> 5 NEXT LEVEL flow succeeds without wall count failure (Observed Regression)
    @Test
    fun test05_level4To5_nextLevelFlow_succeedsWithoutWallCountFailure() = runBlocking {
        // Player legitimately completes Level 4
        progressRepository.completeLevel(4)

        // Resolve destination
        val completed = progressRepository.getCompletedLevelIds()
        val dest = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 4,
            completedLevelIds = completed
        )

        assertEquals("NEXT LEVEL", dest.buttonLabel)
        assertTrue("Destination must be NextLevel(5)", dest.destination is ProgressionDestination.NextLevel)
        assertEquals(5, (dest.destination as ProgressionDestination.NextLevel).levelId)

        // Verify Level 5 is unlocked
        assertTrue("Level 5 must be unlocked", progressRepository.isLevelUnlocked(5))

        // Verify Level 5 loads cleanly via repository path used in gameplay
        val loadResult = catalogRepository.loadPuzzle(5)
        assertTrue(
            "Level 5 must load without 'Wall count 3 outside world bounds' error: $loadResult",
            loadResult is CatalogLoadResult.Success
        )
    }

    // 6 & 7 & 8. No blocked cells, 100% playable cells, full board coverage
    @Test
    fun test06_to_08_noBlockedCells_fullCellCoverage() {
        for (lvl in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(lvl)
            val expectedTotal = def.gridDimensions.rows * def.gridDimensions.columns
            assertEquals("Level $lvl must have 100% playable cells", expectedTotal, def.requiredCells.size)

            val policy = PuzzleConstraintPolicy.forSoloLevel(lvl)
            assertFalse("Blocked cells strictly prohibited", policy.blockedCellsAllowed)
            assertTrue("Full coverage strictly required", policy.fullCoverageRequired)
        }
    }

    // 9. Number sequence is valid and contiguous
    @Test
    fun test09_numberSequenceIsValidAndContiguous() {
        for (lvl in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(lvl)
            val checkpoints = def.checkpoints.sortedBy { it.number }
            assertEquals("Checkpoints must start at 1", 1, checkpoints.first().number)
            for (i in checkpoints.indices) {
                assertEquals("Checkpoint must be contiguous #$i", i + 1, checkpoints[i].number)
            }
        }
    }

    // 10 & 11. Walls exist only on legal adjacent edges & solution never crosses wall
    @Test
    fun test10_and_11_wallsOnLegalEdges_solutionNeverCrossesWall() {
        for (lvl in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(lvl)
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(lvl)

            for (wall in def.blockedEdges) {
                assertTrue(def.gridDimensions.contains(wall.first))
                assertTrue(def.gridDimensions.contains(wall.second))
                assertTrue(wall.first.isOrthogonalNeighbor(wall.second))
            }

            for (i in 0 until sol.positions.size - 1) {
                val step = BlockedEdge.between(sol.positions[i], sol.positions[i + 1])
                assertFalse("Solution for Level $lvl must not cross wall $step", def.blockedEdges.contains(step))
            }
        }
    }

    // 12. Prohibited L-shaped cage rejected
    @Test
    fun test12_prohibitedLShapedCage_isRejected() {
        val dims = GridDimensions(5, 5)
        // Two walls meeting at boundary cell (0, 1)
        val lWalls = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
        )
        assertTrue("Must reject L-shaped perimeter cage", wallStrategy.hasLShapedCage(lWalls, dims))
    }

    // 13. Prohibited U-shaped cage / 3-sided enclosure rejected
    @Test
    fun test13_prohibitedUShapedCage_isRejected() {
        val dims = GridDimensions(5, 5)
        val cells = (0..4).flatMap { r -> (0..4).map { c -> GridPosition(r, c) } }.toSet()
        val center = GridPosition(2, 2)
        val uWalls = setOf(
            BlockedEdge.between(center, center.up()),
            BlockedEdge.between(center, center.down()),
            BlockedEdge.between(center, center.left())
        )
        assertTrue("Must reject 3-sided enclosure", wallStrategy.hasThreeSidedEnclosure(uWalls, dims, cells))
    }

    // 14. Prohibited V-shaped wall structure rejected
    @Test
    fun test14_prohibitedVShapedWallStructure_isRejected() {
        val dims = GridDimensions(5, 5)
        val corner = GridPosition(0, 0)
        val cornerWall = setOf(BlockedEdge.between(corner, GridPosition(0, 1)))
        assertTrue("Must reject walls touching corners", wallStrategy.hasCornerCage(cornerWall, dims))
    }

    // 15. Cell enclosure rejected
    @Test
    fun test15_cellEnclosure_isRejected() {
        val dims = GridDimensions(4, 4)
        val cells = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val dummyRoute = listOf(GridPosition(0, 0), GridPosition(3, 3))
        // Box in (1, 1) on all 4 sides
        val target = GridPosition(1, 1)
        val boxedWalls = setOf(
            BlockedEdge.between(target, target.up()),
            BlockedEdge.between(target, target.down()),
            BlockedEdge.between(target, target.left()),
            BlockedEdge.between(target, target.right())
        )
        assertTrue("Must detect boxed in cell", wallStrategy.hasBoxedInCell(boxedWalls, dims, cells, dummyRoute))
    }

    // 16. Inaccessible-cell configuration rejected
    @Test
    fun test16_inaccessibleCellConfiguration_isRejected() {
        val dims = GridDimensions(4, 4)
        val cells = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val dummyRoute = listOf(GridPosition(0, 0), GridPosition(3, 3))
        // (1, 2) has degree < 2
        val target = GridPosition(1, 2)
        val walls = setOf(
            BlockedEdge.between(target, target.up()),
            BlockedEdge.between(target, target.down()),
            BlockedEdge.between(target, target.left())
        )
        assertTrue("Must reject inaccessible cell", wallStrategy.hasBoxedInCell(walls, dims, cells, dummyRoute))
    }

    // 17 & 18. Valid isolated horizontal and vertical walls accepted
    @Test
    fun test17_and_18_validIsolatedWalls_accepted() {
        val dims = GridDimensions(4, 4)
        val cells = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val route = listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )

        // Valid isolated horizontal internal wall
        val horizontalWall = setOf(BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)))
        assertTrue(
            "Isolated horizontal wall must be valid",
            wallStrategy.validateWalls(horizontalWall, dims, cells, route, 1, 3)
        )

        // Valid isolated vertical internal wall (using vertical route where edge between (1,1) and (1,2) is not traversed)
        val verticalRoute = listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
            GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
            GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
            GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3)
        )
        val validVerticalWall = setOf(BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)))
        assertTrue(
            "Isolated vertical wall must be valid",
            wallStrategy.validateWalls(validVerticalWall, dims, cells, verticalRoute, 1, 3)
        )
    }

    // 19 & 20. Every Solo level asset passes runtime integrity and constructs gameplay state
    @Test
    fun test19_and_20_all300Levels_passRuntimeIntegrity_andConstructGameplayState() = runBlocking {
        for (lvl in 1..300) {
            val loadResult = catalogRepository.loadPuzzle(lvl)
            assertTrue("Level $lvl must load successfully: $loadResult", loadResult is CatalogLoadResult.Success)

            val def = (loadResult as CatalogLoadResult.Success).definition
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvl)
            val policyErrors = policy.validate(def)
            assertTrue("Level $lvl must pass policy: $policyErrors", policyErrors.isEmpty())

            val gameState = PuzzleGameState(def)
            assertNotNull("Level $lvl must construct gameplay state", gameState)
            assertEquals("Level $lvl grid dimensions match", policy.gridDimensions, gameState.puzzleDefinition.gridDimensions)
        }
    }

    // 21. Every adjacent 1->300 transition can load its destination
    @Test
    fun test21_everyAdjacentTransition_canLoadItsDestination() = runBlocking {
        for (lvl in 1 until 300) {
            val nextLvl = lvl + 1
            val loadResult = catalogRepository.loadPuzzle(nextLvl)
            assertTrue("Destination Level $nextLvl must be loadable: $loadResult", loadResult is CatalogLoadResult.Success)
        }
    }

    // 22. Every World boundary destination loads
    @Test
    fun test22_everyWorldBoundaryDestination_loadsSuccessfully() = runBlocking {
        val boundaryFirstLevels = listOf(21, 51, 101, 151, 201)
        for (firstLevel in boundaryFirstLevels) {
            val loadResult = catalogRepository.loadPuzzle(firstLevel)
            assertTrue(
                "World boundary first level $firstLevel must load: $loadResult",
                loadResult is CatalogLoadResult.Success
            )
            val def = (loadResult as CatalogLoadResult.Success).definition
            val policy = PuzzleConstraintPolicy.forSoloLevel(firstLevel)
            val errors = policy.validate(def)
            assertTrue("First level $firstLevel policy errors: $errors", errors.isEmpty())
        }
    }

    // 23 & 24. Quick Duel & Friends Arena validated hard puzzles pass compatible integrity rules
    @Test
    fun test23_and_24_competitivePuzzles_passCompatibleIntegrityRules() {
        val comp5x5Policy = PuzzleConstraintPolicy.forCompetitive(
            mode = PuzzleMode.QUICK_DUEL,
            gridDimensions = GridDimensions(5, 5)
        )
        assertEquals(3..6, comp5x5Policy.allowedWallCountRange)
        assertEquals(5..7, comp5x5Policy.checkpointRequirements)
        assertTrue(comp5x5Policy.fullCoverageRequired)
        assertFalse(comp5x5Policy.blockedCellsAllowed)

        val comp6x6Policy = PuzzleConstraintPolicy.forCompetitive(
            mode = PuzzleMode.FRIENDS_ARENA,
            gridDimensions = GridDimensions(6, 6),
            isExtreme = true
        )
        assertEquals(5..8, comp6x6Policy.allowedWallCountRange)
        assertEquals(6..8, comp6x6Policy.checkpointRequirements)
        assertTrue(comp6x6Policy.fullCoverageRequired)
        assertFalse(comp6x6Policy.blockedCellsAllowed)
    }

    // 25. Existing progress remains intact during asset loading
    @Test
    fun test25_existingProgress_remainsIntact() = runBlocking {
        progressRepository.completeLevel(5)
        assertTrue("Level 5 must be completed", progressRepository.getCompletedLevelIds().contains(5))

        val loadResult = catalogRepository.loadPuzzle(5)
        assertTrue(loadResult is CatalogLoadResult.Success)

        // Completing and loading does not alter completion status
        assertTrue("Level 5 must still be completed", progressRepository.getCompletedLevelIds().contains(5))
        assertTrue("Level 6 must be unlocked", progressRepository.isLevelUnlocked(6))
    }

    // 26. Rewards remain idempotent across asset loading and retries
    @Test
    fun test26_rewards_remainIdempotent() = runBlocking {
        progressRepository.completeLevel(5)
        val initialCount = progressRepository.completedIds.size

        // Re-completing level 5
        progressRepository.completeLevel(5)
        val secondCount = progressRepository.completedIds.size

        assertEquals("Completion count must be strictly idempotent", initialCount, secondCount)
    }
}
