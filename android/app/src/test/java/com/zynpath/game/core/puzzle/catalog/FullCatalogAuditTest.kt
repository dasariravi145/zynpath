package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative 300-level catalog audit and shipped puzzle verification test suite.
 *
 * Implements Prompt 46 Sections 36-46:
 * - Complete 300-level catalog audit across all 6 canonical worlds.
 * - Complete, unique, and contiguous Level IDs from 1 to 300.
 * - Exact world boundary and assignment verification.
 * - Grid dimensions, checkpoint count, and wall count bounds verification per world.
 * - Full solver solvability verification for every shipped puzzle.
 * - Independent canonical solution validation (CompletionValidator + FoundationalPathValidator).
 * - Duplicate layout and fingerprint detection.
 * - Performance tracking for solver execution across shipped levels.
 */
class FullCatalogAuditTest {

    private lateinit var manifest: CatalogManifest
    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        manifest = CatalogManifest.createDefaultManifest()
        solver = PuzzleSolver()
    }

    // =========================================================================
    // 1. World Structure & Boundary Verification (Sections 36, 39, 40)
    // =========================================================================

    @Test
    fun `test canonical world count and world IDs`() {
        assertEquals("Catalog must define exactly 6 worlds", 6, manifest.worlds.size)
        val worldIds = manifest.worlds.map { it.worldId }
        assertEquals(listOf(1, 2, 3, 4, 5, 6), worldIds)
    }

    @Test
    fun `test canonical world specifications match Section 36 precisely`() {
        // World 1: Levels 1–20, 4x4, 4–6 checkpoints, 0 walls
        val w1 = manifest.getWorld(1)
        assertNotNull(w1)
        assertEquals(1, w1!!.firstLevelId)
        assertEquals(20, w1.lastLevelId)
        assertEquals(GridDimensions(4, 4), w1.gridDimensions)
        assertEquals(4, w1.minimumCheckpointCount)
        assertEquals(6, w1.maximumCheckpointCount)
        assertEquals(0, w1.minimumWallCount)
        assertEquals(0, w1.maximumWallCount)

        // World 2: Levels 21–50, 5x5, 4–7 checkpoints, 0 walls
        val w2 = manifest.getWorld(2)
        assertNotNull(w2)
        assertEquals(21, w2!!.firstLevelId)
        assertEquals(50, w2.lastLevelId)
        assertEquals(GridDimensions(5, 5), w2.gridDimensions)
        assertEquals(4, w2.minimumCheckpointCount)
        assertEquals(7, w2.maximumCheckpointCount)
        assertEquals(0, w2.minimumWallCount)
        assertEquals(0, w2.maximumWallCount)

        // World 3: Levels 51–100, 5x5, 4–7 checkpoints, 1–5 walls
        val w3 = manifest.getWorld(3)
        assertNotNull(w3)
        assertEquals(51, w3!!.firstLevelId)
        assertEquals(100, w3.lastLevelId)
        assertEquals(GridDimensions(5, 5), w3.gridDimensions)
        assertEquals(4, w3.minimumCheckpointCount)
        assertEquals(7, w3.maximumCheckpointCount)
        assertEquals(1, w3.minimumWallCount)
        assertEquals(5, w3.maximumWallCount)

        // World 4: Levels 101–150, 6x6, 4–8 checkpoints, 2–8 walls
        val w4 = manifest.getWorld(4)
        assertNotNull(w4)
        assertEquals(101, w4!!.firstLevelId)
        assertEquals(150, w4.lastLevelId)
        assertEquals(GridDimensions(6, 6), w4.gridDimensions)
        assertEquals(4, w4.minimumCheckpointCount)
        assertEquals(8, w4.maximumCheckpointCount)
        assertEquals(2, w4.minimumWallCount)
        assertEquals(8, w4.maximumWallCount)

        // World 5: Levels 151–200, 7x7, 4–10 checkpoints, 4–12 walls
        val w5 = manifest.getWorld(5)
        assertNotNull(w5)
        assertEquals(151, w5!!.firstLevelId)
        assertEquals(200, w5.lastLevelId)
        assertEquals(GridDimensions(7, 7), w5.gridDimensions)
        assertEquals(4, w5.minimumCheckpointCount)
        assertEquals(10, w5.maximumCheckpointCount)
        assertEquals(4, w5.minimumWallCount)
        assertEquals(12, w5.maximumWallCount)

        // World 6: Levels 201–300, 8x8, 4–12 checkpoints, 6–18 walls
        val w6 = manifest.getWorld(6)
        assertNotNull(w6)
        assertEquals(201, w6!!.firstLevelId)
        assertEquals(300, w6.lastLevelId)
        assertEquals(GridDimensions(8, 8), w6.gridDimensions)
        assertEquals(4, w6.minimumCheckpointCount)
        assertEquals(12, w6.maximumCheckpointCount)
        assertEquals(6, w6.minimumWallCount)
        assertEquals(18, w6.maximumWallCount)
    }

    // =========================================================================
    // 2. Complete 300-Level Catalog Audit (Sections 37, 38, 39)
    // =========================================================================

    @Test
    fun `test all 300 level IDs are unique contiguous and present`() {
        assertEquals("Manifest must contain exactly 300 levels", 300, manifest.levels.size)

        val levelIds = manifest.levels.map { it.levelId }
        assertEquals("First level must be 1", 1, levelIds.first())
        assertEquals("Last level must be 300", 300, levelIds.last())

        // Check contiguity and uniqueness
        val uniqueIds = levelIds.toSet()
        assertEquals("All 300 level IDs must be unique", 300, uniqueIds.size)
        for (expectedId in 1..300) {
            assertTrue("Level $expectedId must exist in catalog manifest", uniqueIds.contains(expectedId))
        }
    }

    @Test
    fun `test every level in manifest belongs to its correct canonical world`() {
        for (lvl in manifest.levels) {
            val expectedWorldId = when (lvl.levelId) {
                in 1..20 -> 1
                in 21..50 -> 2
                in 51..100 -> 3
                in 101..150 -> 4
                in 151..200 -> 5
                in 201..300 -> 6
                else -> -1
            }
            assertEquals(
                "Level ${lvl.levelId} must belong to World $expectedWorldId",
                expectedWorldId,
                lvl.worldId
            )
        }
    }

    // =========================================================================
    // 3. Shipped Puzzles Verification (Sections 41, 42, 43, 44, 45, 46)
    // =========================================================================

    @Test
    fun `test shipped puzzle definitions respect world checkpoint and wall bounds`() {
        for ((levelId, definition) in PackagedPuzzles.ALL_PACKAGED) {
            val world = WorldDefinition.forLevel(levelId)

            // Grid dimension check
            assertEquals(
                "Level $levelId grid rows must match world",
                world.gridDimensions.rows,
                definition.gridDimensions.rows
            )
            assertEquals(
                "Level $levelId grid cols must match world",
                world.gridDimensions.columns,
                definition.gridDimensions.columns
            )

            // Required cells must equal total cells
            assertEquals(
                "Level $levelId must require full grid coverage",
                world.gridDimensions.totalCells,
                definition.requiredCells.size
            )

            // Checkpoint range check
            val cpCount = definition.checkpoints.size
            assertTrue(
                "Level $levelId checkpoint count $cpCount must be within [${world.minimumCheckpointCount}..${world.maximumCheckpointCount}]",
                cpCount in world.checkpointRange
            )

            // Wall count check
            val wallCount = definition.blockedEdges.size
            assertTrue(
                "Level $levelId wall count $wallCount must be within [${world.minimumWallCount}..${world.maximumWallCount}]",
                wallCount in world.wallRange
            )

            // Checkpoints must be ordered 1..N
            val numbers = definition.checkpoints.map { it.number }
            assertEquals(
                "Level $levelId checkpoints must be numbered 1..$cpCount",
                (1..cpCount).toList(),
                numbers
            )

            // Checkpoint positions must be within bounds and distinct
            val cpPositions = definition.checkpoints.map { it.position }
            assertEquals(
                "Level $levelId checkpoint positions must all be distinct",
                cpPositions.size,
                cpPositions.toSet().size
            )
            for (cp in definition.checkpoints) {
                assertTrue(
                    "Checkpoint ${cp.number} position ${cp.position} must be within bounds",
                    definition.gridDimensions.contains(cp.position)
                )
            }

            // Walls must connect orthogonally adjacent cells within bounds
            for (wall in definition.blockedEdges) {
                assertTrue(
                    "Wall position1 ${wall.first} must be within bounds",
                    definition.gridDimensions.contains(wall.first)
                )
                assertTrue(
                    "Wall position2 ${wall.second} must be within bounds",
                    definition.gridDimensions.contains(wall.second)
                )
                assertTrue(
                    "Wall must connect orthogonally adjacent cells: ${wall.first} <-> ${wall.second}",
                    wall.first.isOrthogonallyAdjacentTo(wall.second)
                )
            }
        }
    }

    @Test
    fun `test all shipped puzzles are solvable and solutions pass independent validation`() {
        val solverFailures = mutableListOf<String>()
        val validatorFailures = mutableListOf<String>()
        val timings = mutableMapOf<Int, Long>()

        val config = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = 250_000,
            timeBudgetMs = 5000L
        )

        for ((levelId, definition) in PackagedPuzzles.ALL_PACKAGED) {
            val startTime = System.currentTimeMillis()
            val solverResult = solver.solve(definition, config)
            val elapsed = System.currentTimeMillis() - startTime
            timings[levelId] = elapsed

            if (solverResult.status != SolverStatus.SOLVED || !solverResult.isSolved) {
                solverFailures.add("Level $levelId (${definition.puzzleId}) failed to solve: ${solverResult.status}")
                continue
            }

            val solution = solverResult.firstSolution
            if (solution == null) {
                solverFailures.add("Level $levelId returned null solution path")
                continue
            }

            // Solution must cover all cells
            val expectedLength = definition.gridDimensions.totalCells
            if (solution.length != expectedLength) {
                solverFailures.add("Level $levelId solution length ${solution.length} != expected $expectedLength")
            }

            // Independent validation 1: CompletionValidator
            val compResult = CompletionValidator.validate(definition, solution)
            if (compResult !is CompletionCheckResult.Success) {
                validatorFailures.add("Level $levelId failed CompletionValidator: $compResult")
            }

            // Independent validation 2: FoundationalPathValidator
            val foundResult = FoundationalPathValidator.validate(definition, solution)
            if (!foundResult.isWin) {
                validatorFailures.add("Level $levelId failed FoundationalPathValidator: $foundResult")
            }
        }

        assertTrue("Solver failures detected: $solverFailures", solverFailures.isEmpty())
        assertTrue("Validator failures detected: $validatorFailures", validatorFailures.isEmpty())
    }

    @Test
    fun `test pre-stored reference solutions pass independent validation`() {
        for ((levelId, solution) in PackagedPuzzles.ALL_SOLUTIONS) {
            val definition = PackagedPuzzles.ALL_PACKAGED[levelId]
            assertNotNull("Definition for Level $levelId must exist", definition)

            val compResult = CompletionValidator.validate(definition!!, solution)
            assertTrue("Pre-stored solution for Level $levelId must pass CompletionValidator", compResult is CompletionCheckResult.Success)

            val foundResult = FoundationalPathValidator.validate(definition, solution)
            assertTrue("Pre-stored solution for Level $levelId must pass FoundationalPathValidator", foundResult.isWin)
        }
    }

    @Test
    fun `test duplicate puzzle layouts and fingerprints are identified`() {
        val seenFingerprints = mutableMapOf<String, Int>()
        val duplicates = mutableListOf<String>()

        for ((levelId, definition) in PackagedPuzzles.ALL_PACKAGED) {
            val fp = PuzzleFingerprint.computeSha256(definition)
            val existing = seenFingerprints[fp]
            if (existing != null) {
                duplicates.add("Level $levelId is a duplicate of Level $existing (fingerprint $fp)")
            } else {
                seenFingerprints[fp] = levelId
            }
        }

        assertTrue("No duplicate puzzle definitions permitted among shipped levels: $duplicates", duplicates.isEmpty())
    }
}
