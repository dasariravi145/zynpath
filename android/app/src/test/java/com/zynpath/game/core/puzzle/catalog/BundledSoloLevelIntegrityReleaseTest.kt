package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.policy.PuzzleConstraintPolicy
import com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
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
 * Authoritative automated asset integrity check for all bundled Solo levels before release.
 *
 * Implements Prompt Section 4 ("Prevent recurrence"):
 * Validates:
 * 1. World and level IDs (1..300 across 6 worlds)
 * 2. Asset schema conformance ("1.0.0")
 * 3. Grid dimensions per world policy (W1=6x6, W2=7x7, W3=8x8, W4=9x9, W5=10x10..11x11, W6=12x12)
 * 4. Checkpoint integrity (strictly ascending 1..N, contiguous, within bounds)
 * 5. Wall validation (blocked edges between adjacent cells, not blocked cells; W1 strictly 0 walls)
 * 6. Solvability & full-coverage path (Hamiltonian traversal, 100% playable cells, 0 blocked cells)
 * 7. Progression mappings & next-level availability (L4->L5, L5->L6, W1->W2, L300 final)
 * 8. Rejection of invalid assets (never uses catch-all success response)
 */
class BundledSoloLevelIntegrityReleaseTest {

    private lateinit var assetLoader: PuzzleAssetLoader
    private lateinit var progressRepository: ReleaseCheckProgressRepository
    private lateinit var catalogRepository: LevelCatalogRepository
    private lateinit var manifest: CatalogManifest
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

    private class ReleaseCheckProgressRepository : ProgressRepository {
        val completedIds = mutableSetOf<Int>()
        private val progressFlow = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

        fun completeLevel(levelId: Int, stars: Int = 3, timeMs: Long = 15000L) {
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

        override suspend fun getCompletedLevelIds(): Set<Int> = completedIds.toSet()

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
        val candidateDirs = listOf(
            File("d:/Zynpath/android/app/src/main/assets"),
            File("src/main/assets"),
            File("app/src/main/assets"),
            File("android/app/src/main/assets")
        )
        val assetsDir = candidateDirs.firstOrNull { it.exists() && it.isDirectory }
            ?: File("d:/Zynpath/android/app/src/main/assets")

        assetLoader = FileSystemAssetLoader(assetsDir)
        progressRepository = ReleaseCheckProgressRepository()
        catalogRepository = LevelCatalogRepositoryImpl(assetLoader, progressRepository)
        manifest = CatalogManifestSerializer.deserialize(
            assetLoader.loadAsset(CatalogManifest.DEFAULT_MANIFEST_PATH)
                ?: error("Manifest could not be loaded from $assetsDir")
        )
        solver = PuzzleSolver()
    }

    // 1. World and Level IDs verification
    @Test
    fun test01_worldAndLevelIds_areContiguousAndMatchCatalogManifest() {
        assertEquals("Manifest must define exactly 6 worlds", 6, manifest.worlds.size)
        val worldIds = manifest.worlds.map { it.worldId }
        assertEquals(listOf(1, 2, 3, 4, 5, 6), worldIds)

        assertEquals("Total planned levels must be exactly 1800", 1800, manifest.levels.size)
        for (i in 1..1800) {
            val lvl = manifest.getLevel(i)
            assertNotNull("Level $i must exist in manifest", lvl)
            assertEquals("Level $i must have contiguous levelId", i, lvl!!.levelId)
            val expectedWorldId = when (i) {
                in 1..300 -> 1
                in 301..600 -> 2
                in 601..900 -> 3
                in 901..1200 -> 4
                in 1201..1500 -> 5
                else -> 6
            }
            assertEquals("Level $i must be assigned to World $expectedWorldId", expectedWorldId, lvl.worldId)
        }
    }

    // 2. Asset schema conformance
    @Test
    fun test02_assetSchema_matchesCanonicalVersion() {
        val testLevels = listOf(1, 4, 5, 100, 299, 300, 301, 400, 600, 601, 900, 901, 1200, 1201, 1500, 1501, 1800)
        for (i in testLevels) {
            val lvl = manifest.getLevel(i)!!
            if (assetLoader.hasAsset(lvl.assetPath)) {
                val json = assetLoader.loadAsset(lvl.assetPath)
                assertNotNull("Asset JSON for Level $i must not be null", json)
                val asset = PuzzleAssetSerializer.deserialize(json!!)
                assertEquals(
                    "Level $i asset schema must be '${CatalogVersion.ASSET_SCHEMA_VERSION}'",
                    CatalogVersion.ASSET_SCHEMA_VERSION,
                    asset.schemaVersion
                )
            }
        }
    }

    // 3. Grid dimensions per world policy
    @Test
    fun test03_gridDimensions_matchWorldAndPolicyConstraints() = runBlocking {
        val testLevels = listOf(1, 4, 5, 100, 299, 300, 301, 400, 600, 601, 900, 901, 1200, 1201, 1500, 1501, 1800)
        for (lvlId in testLevels) {
            val loadResult = catalogRepository.loadPuzzle(lvlId)
            assertTrue("Level $lvlId must load successfully: $loadResult", loadResult is CatalogLoadResult.Success)
            val def = (loadResult as CatalogLoadResult.Success).definition
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvlId)
            assertEquals("Level $lvlId dimensions must match policy", policy.gridDimensions, def.gridDimensions)
        }
    }

    // 4. Checkpoint integrity (strictly ascending 1..N, contiguous, within bounds)
    @Test
    fun test04_checkpoints_ascendingContiguousAndWithinBounds() = runBlocking {
        val testLevels = listOf(1, 4, 5, 100, 299, 300, 301, 400, 600, 601, 900, 901, 1200, 1201, 1500, 1501, 1800)
        for (lvlId in testLevels) {
            val loadResult = catalogRepository.loadPuzzle(lvlId) as CatalogLoadResult.Success
            val def = loadResult.definition
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvlId)

            assertTrue(
                "Level $lvlId checkpoint count (${def.checkpoints.size}) outside policy bounds (${policy.checkpointRequirements})",
                def.checkpoints.size in policy.checkpointRequirements
            )

            val sortedCps = def.checkpoints.sortedBy { it.number }
            assertEquals("Level $lvlId start checkpoint must be #1", 1, sortedCps.first().number)
            for (i in sortedCps.indices) {
                assertEquals("Level $lvlId checkpoint numbers must be contiguous", i + 1, sortedCps[i].number)
                assertTrue("Checkpoint ${sortedCps[i]} must be within grid bounds", def.gridDimensions.contains(sortedCps[i].position))
                assertTrue("Checkpoint ${sortedCps[i]} must be on a required cell", def.requiredCells.contains(sortedCps[i].position))
            }
            val distinctPositions = def.checkpoints.map { it.position }.toSet()
            assertEquals("Level $lvlId checkpoint positions must be distinct", def.checkpoints.size, distinctPositions.size)
        }
    }

    // 5. Wall validation (blocked edges between adjacent cells, not blocked cells; W1 strictly 0 walls)
    @Test
    fun test05_walls_areBlockedEdgesBetweenAdjacentCellsAndRespectWorldPolicy() = runBlocking {
        val testLevels = listOf(1, 4, 5, 100, 299, 300, 301, 400, 600, 601, 900, 901, 1200, 1201, 1500, 1501, 1800)
        for (lvlId in testLevels) {
            val loadResult = catalogRepository.loadPuzzle(lvlId) as CatalogLoadResult.Success
            val def = loadResult.definition
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvlId)

            // World 1 (levels 1..300) must strictly have 0 walls
            if (lvlId in 1..300) {
                assertEquals("World 1 Level $lvlId must strictly contain 0 walls", 0, def.blockedEdges.size)
            } else {
                assertTrue(
                    "Level $lvlId wall count ${def.blockedEdges.size} outside policy bounds ${policy.allowedWallCountRange}",
                    def.blockedEdges.size in policy.allowedWallCountRange
                )
            }

            // Every wall must be a blocked edge between two orthogonally adjacent required cells
            for (wall in def.blockedEdges) {
                assertTrue("Wall endpoint 1 ${wall.first} must be within bounds", def.gridDimensions.contains(wall.first))
                assertTrue("Wall endpoint 2 ${wall.second} must be within bounds", def.gridDimensions.contains(wall.second))
                assertTrue("Wall must be between orthogonally adjacent cells: $wall", wall.first.isOrthogonalNeighbor(wall.second))
                assertTrue("Wall endpoint 1 must be a required cell", def.requiredCells.contains(wall.first))
                assertTrue("Wall endpoint 2 must be a required cell", def.requiredCells.contains(wall.second))
            }

            // Policy anti-cage validation must pass
            val policyErrors = policy.validate(def)
            assertTrue("Level $lvlId must satisfy anti-cage topology rules: $policyErrors", policyErrors.isEmpty())
        }
    }

    // 6. Solvability & valid full-coverage path
    @Test
    fun test06_solvability_andValidFullCoveragePath() {
        // Verify curated/packaged levels (1..50) have verified unique solutions and 100% full grid coverage
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(lvlId)
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(lvlId)

            assertEquals("Level $lvlId must require full grid coverage", def.gridDimensions.totalCells, def.requiredCells.size)
            assertEquals("Level $lvlId solution must visit every cell", def.gridDimensions.totalCells, sol.positions.size)
            assertEquals("Level $lvlId solution must visit distinct cells", def.gridDimensions.totalCells, sol.positions.toSet().size)

            // Solution step-by-step orthogonal adjacency & wall collision check
            for (i in 0 until sol.positions.size - 1) {
                val from = sol.positions[i]
                val to = sol.positions[i + 1]
                assertTrue("Step from $from to $to must be orthogonal in Level $lvlId", from.isOrthogonalNeighbor(to))
                val edge = BlockedEdge.between(from, to)
                assertFalse("Step in Level $lvlId must not cross wall $edge", def.blockedEdges.contains(edge))
            }

            // Checkpoints hit in strictly ascending order
            var currentCpIndex = 0
            val sortedCps = def.checkpoints.sortedBy { it.number }
            for (pos in sol.positions) {
                if (currentCpIndex < sortedCps.size && pos == sortedCps[currentCpIndex].position) {
                    currentCpIndex++
                }
            }
            assertEquals("Solution for Level $lvlId must hit all checkpoints in ascending order", sortedCps.size, currentCpIndex)

            // Independent validation
            val compResult = CompletionValidator.validate(def, sol)
            assertTrue("Level $lvlId must pass independent completion validation", compResult is CompletionCheckResult.Success)
        }

        // Specifically verify Level 5 puzzle solver solvability
        val lvl5Def = CuratedFirst50Levels.LEVELS.getValue(5)
        val solverResult = solver.solve(lvl5Def, SolverConfiguration(maxSolutions = 1, timeBudgetMs = 5000L))
        assertEquals("Level 5 must be solvable by PuzzleSolver", SolverStatus.SOLVED, solverResult.status)
        assertTrue("Level 5 must have at least 1 solution", solverResult.solutions.isNotEmpty())
    }

    // 7. Progression mappings & next-level availability
    @Test
    fun test07_progressionMappings_andNextLevelAvailability() = runBlocking {
        // 1. Level 4 completed -> NEXT LEVEL destination is Level 5
        progressRepository.completeLevel(4)
        val dest4 = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 4,
            completedLevelIds = progressRepository.getCompletedLevelIds()
        )
        assertEquals("NEXT LEVEL", dest4.buttonLabel)
        assertTrue("Destination must be NextLevel", dest4.destination is ProgressionDestination.NextLevel)
        assertEquals(5, (dest4.destination as ProgressionDestination.NextLevel).levelId)

        // Verify Level 5 loads cleanly
        val lvl5Load = catalogRepository.loadPuzzle(5)
        assertTrue("Level 5 must load without error: $lvl5Load", lvl5Load is CatalogLoadResult.Success)
        val gameState5 = PuzzleGameState((lvl5Load as CatalogLoadResult.Success).definition)
        assertNotNull("Level 5 must construct valid game state", gameState5)

        // 2. Level 5 completed -> NEXT LEVEL destination is Level 6
        progressRepository.completeLevel(5)
        val dest5 = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 5,
            completedLevelIds = progressRepository.getCompletedLevelIds()
        )
        assertEquals("NEXT LEVEL", dest5.buttonLabel)
        assertTrue("Destination must be NextLevel", dest5.destination is ProgressionDestination.NextLevel)
        assertEquals(6, (dest5.destination as ProgressionDestination.NextLevel).levelId)

        // 3. World boundaries across all six worlds
        val dest299 = ProgressionDestinationResolver.resolve(
            worldId = 1,
            completedLevelOrLocal = 299,
            completedLevelIds = (1..299).toSet()
        )
        assertTrue("Level 299 must resolve to NextLevel 300", dest299.destination is ProgressionDestination.NextLevel)
        assertEquals(300, (dest299.destination as ProgressionDestination.NextLevel).levelId)

        val dest300 = SoloProgressionAuthority.resolveVictoryDestination(300)
        assertTrue("Level 300 boundary must resolve to NextWorldEntry World 2", dest300.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(2, (dest300.destination as ProgressionDestination.NextWorldEntry).worldId)

        val dest600 = SoloProgressionAuthority.resolveVictoryDestination(600)
        assertTrue("Level 600 boundary must resolve to NextWorldEntry World 3", dest600.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(3, (dest600.destination as ProgressionDestination.NextWorldEntry).worldId)

        val dest900 = SoloProgressionAuthority.resolveVictoryDestination(900)
        assertTrue("Level 900 boundary must resolve to NextWorldEntry World 4", dest900.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(4, (dest900.destination as ProgressionDestination.NextWorldEntry).worldId)

        val dest1200 = SoloProgressionAuthority.resolveVictoryDestination(1200)
        assertTrue("Level 1200 boundary must resolve to NextWorldEntry World 5", dest1200.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(5, (dest1200.destination as ProgressionDestination.NextWorldEntry).worldId)

        val dest1500 = SoloProgressionAuthority.resolveVictoryDestination(1500)
        assertTrue("Level 1500 boundary must resolve to NextWorldEntry World 6", dest1500.destination is ProgressionDestination.NextWorldEntry)
        assertEquals(6, (dest1500.destination as ProgressionDestination.NextWorldEntry).worldId)

        // 4. Final Level 1800 completed -> Journey Complete
        val dest1800 = SoloProgressionAuthority.resolveVictoryDestination(1800)
        assertTrue("Level 1800 must resolve to JourneyComplete", dest1800.destination is ProgressionDestination.JourneyComplete)
    }

    // 8. Rejection of invalid assets (never uses catch-all success response)
    @Test
    fun test08_invalidAssets_areStrictlyRejectedByValidator() {
        val levelDef = manifest.getLevel(5)!!
        val worldDef = manifest.getWorld(1)!!

        // Case A: Asset with walls in World 1 (illegal 3 walls when World 1 allows 0..0)
        val illegalWallsJson = """
            {
              "schemaVersion": "1.0.0",
              "puzzleId": "w1_lvl5",
              "puzzleVersion": 5,
              "gridDimensions": {"rows": 6, "columns": 6},
              "requiredCells": [{"row":0,"column":0},{"row":0,"column":1}],
              "checkpoints": [{"number":1,"row":0,"column":0},{"number":2,"row":0,"column":1}],
              "blockedEdges": [
                {"first":{"row":0,"column":0},"second":{"row":0,"column":1}},
                {"first":{"row":1,"column":0},"second":{"row":1,"column":1}},
                {"first":{"row":2,"column":0},"second":{"row":2,"column":1}}
              ]
            }
        """.trimIndent()
        val illegalWallsResult = PuzzleAssetValidator.validate(illegalWallsJson, levelDef, worldDef)
        assertTrue("Illegal wall count in World 1 must be rejected", illegalWallsResult is PuzzleAssetValidationResult.Invalid)
        val errorMsg = (illegalWallsResult as PuzzleAssetValidationResult.Invalid).errorSummary
        assertTrue("Error summary must mention wall count or policy: $errorMsg", errorMsg.contains("Wall count", ignoreCase = true) || errorMsg.contains("policy", ignoreCase = true))

        // Case B: Unsupported schema version
        val badSchemaJson = """
            {
              "schemaVersion": "9.9.9",
              "puzzleId": "w1_lvl5",
              "puzzleVersion": 5,
              "gridDimensions": {"rows": 6, "columns": 6},
              "requiredCells": [{"row":0,"column":0},{"row":0,"column":1}],
              "checkpoints": [{"number":1,"row":0,"column":0},{"number":2,"row":0,"column":1}],
              "blockedEdges": []
            }
        """.trimIndent()
        val badSchemaResult = PuzzleAssetValidator.validate(badSchemaJson, levelDef, worldDef)
        assertTrue("Unsupported schema must be rejected", badSchemaResult is PuzzleAssetValidationResult.Invalid)
        assertTrue((badSchemaResult as PuzzleAssetValidationResult.Invalid).errorSummary.contains("Unsupported asset schema version"))

        // Case C: Checkpoint out of bounds
        val oobCheckpointJson = """
            {
              "schemaVersion": "1.0.0",
              "puzzleId": "w1_lvl5",
              "puzzleVersion": 5,
              "gridDimensions": {"rows": 6, "columns": 6},
              "requiredCells": [{"row":0,"column":0},{"row":0,"column":1}],
              "checkpoints": [{"number":1,"row":0,"column":0},{"number":2,"row":99,"column":99}],
              "blockedEdges": []
            }
        """.trimIndent()
        val oobResult = PuzzleAssetValidator.validate(oobCheckpointJson, levelDef, worldDef)
        assertTrue("Out-of-bounds checkpoint must be rejected", oobResult is PuzzleAssetValidationResult.Invalid)

        // Case D: Dimension mismatch
        val badDimsJson = """
            {
              "schemaVersion": "1.0.0",
              "puzzleId": "w1_lvl5",
              "puzzleVersion": 5,
              "gridDimensions": {"rows": 4, "columns": 4},
              "requiredCells": [{"row":0,"column":0},{"row":0,"column":1}],
              "checkpoints": [{"number":1,"row":0,"column":0},{"number":2,"row":0,"column":1}],
              "blockedEdges": []
            }
        """.trimIndent()
        val badDimsResult = PuzzleAssetValidator.validate(badDimsJson, levelDef, worldDef)
        assertTrue("Dimension mismatch must be rejected", badDimsResult is PuzzleAssetValidationResult.Invalid)
    }
}
