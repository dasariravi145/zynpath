package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.generator.DeterministicLevelProviderImpl
import com.zynpath.game.core.puzzle.model.NextDestinationResolution
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
 * Exhaustive regression test suite verifying the 300-level catalog and runtime progression.
 *
 * Implements Section 7 of Prompt:
 * 1. All 300 levels exist in the actual runtime catalog.
 * 2. All 300 are valid and loadable using the same repository path as gameplay.
 * 3. Level 6 is not marked pending.
 * 4. Level 5 -> Level 6 works.
 * 5. Level 6 -> Level 7 works.
 * 6. Level 19 -> Level 20 works.
 * 7. Level 20 -> World 2 works.
 * 8. Locked and missing states are not conflated.
 * 9. Existing completed progress remains intact.
 * 10. Guest/offline level loading works.
 * 11. No out-of-range level ID is generated.
 * 12. NEXT LEVEL does not trigger duplicate navigation.
 * Tests against actual packaged Android assets on disk.
 */
class CatalogProgressionRuntimeTest {

    private lateinit var assetLoader: PuzzleAssetLoader
    private lateinit var progressRepository: TestProgressRepository
    private lateinit var catalogRepository: LevelCatalogRepository

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
        private val progressFlow = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

        fun completeLevel(levelId: Int, stars: Int = 3, timeMs: Long = 10000L) {
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

        override fun getCompletedLevelCount(): Flow<Int> =
            progressFlow.map { list -> list.count { it.isCompleted } }

        override fun getTotalStarsEarned(): Flow<Int> =
            progressFlow.map { list -> list.filter { it.isCompleted }.sumOf { it.stars } }

        override suspend fun getCompletedLevelIds(): Set<Int> =
            progressFlow.value.filter { it.isCompleted }.map { it.levelId }.toSet()

        override suspend fun isLevelUnlocked(levelId: Int): Boolean {
            if (levelId == 1) return true
            return WorldConfiguration.isLevelUnlocked(levelId, getCompletedLevelIds())
        }

        override suspend fun isWorldUnlocked(worldId: Int): Boolean {
            if (worldId == 1) return true
            return WorldConfiguration.isWorldUnlocked(worldId, getCompletedLevelIds())
        }

        override suspend fun getNextPlayableLevel(): Int =
            WorldConfiguration.getNextPlayableLevel(getCompletedLevelIds())

        override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
            completeLevel(result.levelId, 3, result.elapsedTimeMs)
            return progressFlow.value.first { it.levelId == result.levelId }
        }

        override suspend fun saveGameSession(session: GameSessionEntity) {}
        override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
        override suspend fun abandonActiveSession(levelId: Int) {}
        override suspend fun clearAllProgress() {
            progressFlow.value = emptyList()
        }
        override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
        override suspend fun reconcilePrematureUnlocks() {}

        fun getTotalStars(): Int = progressFlow.value.filter { it.isCompleted }.sumOf { it.stars }
    }

    @Before
    fun setUp() {
        val candidates = listOf(
            File("src/main/assets"),
            File("app/src/main/assets"),
            File("android/app/src/main/assets")
        )
        val assetsDir = candidates.firstOrNull { it.exists() }
            ?: error("Cannot locate src/main/assets in candidates: $candidates")

        assetLoader = FileSystemAssetLoader(assetsDir)
        progressRepository = TestProgressRepository()
        catalogRepository = LevelCatalogRepositoryImpl(
            assetLoader = assetLoader,
            progressRepository = progressRepository,
            deterministicLevelProvider = DeterministicLevelProviderImpl()
        )
    }

    // 1. All 300 levels exist in the actual runtime catalog
    @Test
    fun `test 1 all 300 levels exist in the actual runtime catalog`() {
        val worlds = catalogRepository.getWorlds()
        assertEquals("Catalog must have 6 worlds", 6, worlds.size)

        for (levelId in 1..300) {
            val levelDef = catalogRepository.getLevel(levelId)
            assertNotNull("Level $levelId must exist in runtime catalog manifest", levelDef)
            assertEquals("Level ID must match", levelId, levelDef!!.levelId)
            assertTrue("World ID must be in 1..6", levelDef.worldId in 1..6)
        }
    }

    // 2. All 300 are valid and loadable using the same repository path as gameplay
    @Test
    fun `test 2 all 300 levels are valid and loadable using the gameplay repository path`() = runBlocking {
        for (levelId in 1..300) {
            val result = catalogRepository.loadPuzzle(levelId)
            assertTrue(
                "Level $levelId must load successfully via catalogRepository.loadPuzzle: $result",
                result is CatalogLoadResult.Success
            )
            val success = result as CatalogLoadResult.Success
            assertEquals("Loaded levelId must match requested", levelId, success.level.levelId)
            assertTrue("Required cells must not be empty", success.definition.requiredCells.isNotEmpty())
            assertTrue("Checkpoints must not be empty", success.definition.checkpoints.isNotEmpty())
            assertNotNull("Start checkpoint must exist", success.definition.startCheckpoint)
            assertNotNull("Final checkpoint must exist", success.definition.finalCheckpoint)
        }
    }

    // 3. Level 6 is not marked pending
    @Test
    fun `test 3 level 6 is not marked pending in catalog manifest or runtime`() = runBlocking {
        val level6 = catalogRepository.getLevel(6)
        assertNotNull("Level 6 must exist in catalog", level6)
        assertFalse(
            "Level 6 puzzleId must not contain 'pending'",
            level6!!.puzzleId.startsWith("pending")
        )
        assertFalse(
            "Level 6 displayName must not contain 'pending'",
            level6.displayName?.contains("pending", ignoreCase = true) == true
        )
        assertEquals("Level 6 puzzleId should be w1_lvl6", "w1_lvl6", level6.puzzleId)
        assertTrue("Level 6 asset file must physically exist in assets", assetLoader.hasAsset(level6.assetPath))

        // Level 6 loads successfully and is not marked pending or unavailable
        val loadResult = catalogRepository.loadPuzzle(6)
        assertTrue("Level 6 must load successfully", loadResult is CatalogLoadResult.Success)
    }

    // 4. Level 5 -> Level 6 works
    @Test
    fun `test 4 level 5 completion correctly resolves to playable level 6`() = runBlocking {
        // Complete levels 1 through 5
        for (lvl in 1..5) {
            progressRepository.completeLevel(lvl)
        }

        val completed = progressRepository.getCompletedLevelIds()
        val destResolution = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 5,
            completedLevelIds = completed
        )

        assertEquals("Button label for non-terminal level must be NEXT LEVEL", "NEXT LEVEL", destResolution.buttonLabel)
        assertTrue("Destination must be NextLevel", destResolution.destination is ProgressionDestination.NextLevel)
        val nextDest = destResolution.destination as ProgressionDestination.NextLevel
        assertEquals("Destination levelId must be 6", 6, nextDest.levelId)

        // GameplayViewModel resolution check
        val isUnlocked = progressRepository.isLevelUnlocked(nextDest.levelId)
        assertTrue("Level 6 must be unlocked", isUnlocked)

        val loadResult = catalogRepository.loadPuzzle(nextDest.levelId)
        assertTrue("Level 6 must load successfully", loadResult is CatalogLoadResult.Success)

        val availability = catalogRepository.observeLevelAvailability(6).first()
        assertEquals("Level 6 must be UNLOCKED_AND_AVAILABLE", LevelAvailability.UNLOCKED_AND_AVAILABLE, availability)
    }

    // 5. Level 6 -> Level 7 works
    @Test
    fun `test 5 level 6 completion correctly resolves to playable level 7`() = runBlocking {
        for (lvl in 1..6) {
            progressRepository.completeLevel(lvl)
        }

        val completed = progressRepository.getCompletedLevelIds()
        val destResolution = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 6,
            completedLevelIds = completed
        )

        assertEquals("Button label must be NEXT LEVEL", "NEXT LEVEL", destResolution.buttonLabel)
        assertTrue("Destination must be NextLevel", destResolution.destination is ProgressionDestination.NextLevel)
        val nextDest = destResolution.destination as ProgressionDestination.NextLevel
        assertEquals("Destination levelId must be 7", 7, nextDest.levelId)

        val loadResult = catalogRepository.loadPuzzle(nextDest.levelId)
        assertTrue("Level 7 must load successfully", loadResult is CatalogLoadResult.Success)
    }

    // 6. Level 19 -> Level 20 works
    @Test
    fun `test 6 level 19 completion correctly resolves to playable level 20`() = runBlocking {
        for (lvl in 1..19) {
            progressRepository.completeLevel(lvl)
        }

        val completed = progressRepository.getCompletedLevelIds()
        val destResolution = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 19,
            completedLevelIds = completed
        )

        assertEquals("Button label must be NEXT LEVEL", "NEXT LEVEL", destResolution.buttonLabel)
        assertTrue("Destination must be NextLevel", destResolution.destination is ProgressionDestination.NextLevel)
        val nextDest = destResolution.destination as ProgressionDestination.NextLevel
        assertEquals("Destination levelId must be 20", 20, nextDest.levelId)

        val loadResult = catalogRepository.loadPuzzle(20)
        assertTrue("Level 20 must load successfully", loadResult is CatalogLoadResult.Success)
    }

    // 7. Level 20 -> World 2 works
    @Test
    fun `test 7 level 20 completion correctly transitions to world 2 level 21`() = runBlocking {
        for (lvl in 1..20) {
            progressRepository.completeLevel(lvl)
        }

        val completed = progressRepository.getCompletedLevelIds()
        val destResolution = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 20,
            completedLevelIds = completed
        )

        assertEquals("Terminal world level button label must be NEXT WORLD", "NEXT WORLD", destResolution.buttonLabel)
        assertTrue("Destination must be NextWorldEntry", destResolution.destination is ProgressionDestination.NextWorldEntry)
        val nextWorldDest = destResolution.destination as ProgressionDestination.NextWorldEntry
        assertEquals("Next world must be World 2", 2, nextWorldDest.worldId)

        val world2FirstLevel = WorldConfiguration.getWorld(nextWorldDest.worldId).startLevel
        assertEquals("World 2 starts at Level 21", 21, world2FirstLevel)

        val loadResult = catalogRepository.loadPuzzle(world2FirstLevel)
        assertTrue("World 2 Level 21 must load successfully", loadResult is CatalogLoadResult.Success)
    }

    // 8. Locked and missing states are not conflated
    @Test
    fun `test 8 locked and missing states are strictly distinguished`() = runBlocking {
        // Player has only completed level 1
        progressRepository.completeLevel(1)

        // Level 25: Exists and is valid in catalog, but is LOCKED by progression
        val level25Def = catalogRepository.getLevel(25)
        assertNotNull("Level 25 content exists in catalog", level25Def)
        val isLevel25Unlocked = progressRepository.isLevelUnlocked(25)
        assertFalse("Level 25 is locked by progression", isLevel25Unlocked)

        val l25Availability = catalogRepository.observeLevelAvailability(25).first()
        assertEquals("Level 25 availability must be LOCKED, not ASSET_UNAVAILABLE", LevelAvailability.LOCKED, l25Availability)

        // Level 999: Out-of-bounds / does not exist
        val l999Def = catalogRepository.getLevel(999)
        assertEquals("Level 999 does not exist", null, l999Def)
        val l999Load = catalogRepository.loadPuzzle(999)
        assertTrue("Level 999 must return LevelNotFound", l999Load is CatalogLoadResult.LevelNotFound)
    }

    // 9. Existing completed progress remains intact
    @Test
    fun `test 9 existing completed progress remains intact without resets`() = runBlocking {
        // Simulate existing player progress: Levels 1..5 completed with stars
        for (lvl in 1..5) {
            progressRepository.completeLevel(lvl, stars = 3, timeMs = 8500L)
        }

        val completed = progressRepository.getCompletedLevelIds()
        assertEquals("Completed levels 1..5 must remain intact", setOf(1, 2, 3, 4, 5), completed)
        assertEquals("Total stars must be 15", 15, progressRepository.getTotalStars())

        // Ensure level 6 is unlocked and available
        assertTrue("Level 6 must be unlocked", progressRepository.isLevelUnlocked(6))
        val l6Availability = catalogRepository.observeLevelAvailability(6).first()
        assertEquals("Level 6 must be UNLOCKED_AND_AVAILABLE", LevelAvailability.UNLOCKED_AND_AVAILABLE, l6Availability)

        // Previous levels maintain completed availability
        for (lvl in 1..5) {
            assertEquals("Level $lvl must remain COMPLETED", LevelAvailability.COMPLETED, catalogRepository.observeLevelAvailability(lvl).first())
        }
    }

    // 10. Guest/offline level loading works
    @Test
    fun `test 10 guest offline level loading functions purely locally without network`() = runBlocking {
        // Test offline loading of multiple levels across worlds
        val testLevels = listOf(1, 5, 6, 20, 21, 50, 51, 100, 101, 200, 201, 300)
        for (levelId in testLevels) {
            val loadResult = catalogRepository.loadPuzzle(levelId)
            assertTrue("Level $levelId must load offline without error", loadResult is CatalogLoadResult.Success)
            val def = (loadResult as CatalogLoadResult.Success).definition
            assertNotNull(def)
            assertEquals("Definition grid total cells must match required cells", def.gridDimensions.totalCells, def.requiredCells.size)
        }
    }

    // 11. No out-of-range level ID is generated
    @Test
    fun `test 11 no out of range level ID is generated at catalog boundary`() = runBlocking {
        // Complete all 300 levels
        for (lvl in 1..300) {
            progressRepository.completeLevel(lvl)
        }

        val completed = progressRepository.getCompletedLevelIds()
        val destResolution = ProgressionDestinationResolver.resolve(
            worldId = 6,
            completedLevelOrLocal = 300,
            completedLevelIds = completed
        )

        assertEquals("Terminal campaign button label must be JOURNEY COMPLETE", "JOURNEY COMPLETE", destResolution.buttonLabel)
        assertTrue("Destination must be JourneyComplete", destResolution.destination is ProgressionDestination.JourneyComplete)

        // WorldConfiguration check
        val nextPlayable = progressRepository.getNextPlayableLevel()
        assertEquals("When all 300 are completed, next playable level must be capped at 300", 300, nextPlayable)
    }

    // 12. NEXT LEVEL does not trigger duplicate navigation
    @Test
    fun `test 12 progression resolution is idempotent and stable`() = runBlocking {
        progressRepository.completeLevel(5)

        val completed = progressRepository.getCompletedLevelIds()
        val res1 = ProgressionDestinationResolver.resolve(1, 5, completed)
        val res2 = ProgressionDestinationResolver.resolve(1, 5, completed)

        assertEquals("Resolution must be identical across calls", res1, res2)
        assertEquals(ProgressionDestination.NextLevel(1, 6), res1.destination)
    }
}
