package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CuratedWorld5Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld6Levels
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.generator.DeterministicLevelProviderImpl
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver
import com.zynpath.game.core.puzzle.policy.PuzzleConstraintPolicy
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Authoritative Master Redesign Part 3 Test Suite.
 *
 * Implements Prompt 3 of 3 Certification:
 * - World 5 (151–200): 10x10 (151–180) and 11x11 (181–200).
 * - World 6 (201–300): 11x11 (201–240) and 12x12 (241–300).
 * - Level 300 Grandmaster Finality certification.
 * - 100% full board coverage across all 150 levels.
 * - Strict 1..N ascending checkpoints and Manhattan-orthogonal edge walls.
 * - Canonical solution validation via CompletionValidator and FoundationalPathValidator.
 * - Exact solver uniqueness verification on anchor milestones.
 * - Full 300-level progression continuity (all 299 transitions + terminal journey completion).
 */
class MasterRedesignPart3TestSuite {

    private val solver = PuzzleSolver()
    private val levelProvider = DeterministicLevelProviderImpl()

    // =========================================================================
    // 1. WORLD 5 PRODUCTION CERTIFICATION (Levels 151–200)
    // =========================================================================

    @Test
    fun test01_world5CuratedLevels_exactDimensionsAndFullCoverage() {
        for (lvlId in 151..200) {
            val def = CuratedWorld5Levels.getLevel(lvlId)
            assertNotNull("World 5 Level $lvlId must exist in CuratedWorld5Levels", def)

            val sol = CuratedWorld5Levels.getSolution(lvlId)
            assertNotNull("World 5 Solution $lvlId must exist in CuratedWorld5Levels", sol)

            val is10x10 = lvlId <= 180
            val expectedDim = if (is10x10) 10 else 11
            val expectedCells = if (is10x10) 100 else 121

            assertEquals("Level $lvlId dimensions", GridDimensions(expectedDim, expectedDim), def!!.gridDimensions)
            assertEquals("Level $lvlId required cells", expectedCells, def.totalRequiredCells)
            assertEquals("Level $lvlId solution length", expectedCells, sol!!.positions.size)
            assertEquals("Level $lvlId unique visited cells", expectedCells, sol.positions.toSet().size)

            // Start checkpoint = 1, End checkpoint = N
            assertEquals("Level $lvlId start CP", def.checkpoints.first().position, sol.positions.first())
            assertEquals("Level $lvlId end CP", def.checkpoints.last().position, sol.positions.last())

            // Strictly ascending checkpoints 1..N
            val cpCount = def.checkpoints.size
            for (i in 0 until cpCount) {
                assertEquals("Level $lvlId checkpoint index $i must be number ${i + 1}", i + 1, def.checkpoints[i].number)
            }

            // Checkpoint range compliance
            val minCp = if (is10x10) 14 else 16
            val maxCp = if (is10x10) 20 else 24
            assertTrue(
                "Level $lvlId checkpoint count $cpCount must be in $minCp..$maxCp",
                cpCount in minCp..maxCp
            )

            // Wall count compliance
            val maxWalls = if (is10x10) 8 else 11
            assertTrue(
                "Level $lvlId wall count ${def.blockedEdges.size} must be <= $maxWalls",
                def.blockedEdges.size <= maxWalls
            )

            // Walls must be Manhattan orthogonal
            for (wall in def.blockedEdges) {
                assertTrue("Level $lvlId wall within bounds", def.gridDimensions.contains(wall.first) && def.gridDimensions.contains(wall.second))
                assertTrue("Level $lvlId wall orthogonal", wall.first.isOrthogonallyAdjacentTo(wall.second))
            }

            // Authoritative policy validation
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvlId)
            val policyErrors = policy.validate(def)
            assertTrue("Level $lvlId policy errors: $policyErrors", policyErrors.isEmpty())

            // Anti-cage wall topology validation
            val topologyErrors = policy.validateWallTopology(def)
            assertTrue("Level $lvlId topology errors: $topologyErrors", topologyErrors.isEmpty())

            // Dual independent solution validation
            val completion = CompletionValidator.validate(def, sol)
            assertTrue("Level $lvlId CompletionValidator: $completion", completion is CompletionCheckResult.Success)

            val foundational = FoundationalPathValidator.validate(def, sol)
            assertTrue("Level $lvlId FoundationalPathValidator: $foundational", foundational.isWin)

            // Catalog map membership
            assertTrue("Level $lvlId in CuratedWorld5Levels.LEVELS", CuratedWorld5Levels.LEVELS.containsKey(lvlId))
            assertTrue("Level $lvlId in CuratedWorld5Levels.SOLUTIONS", CuratedWorld5Levels.SOLUTIONS.containsKey(lvlId))
        }
    }

    // =========================================================================
    // 2. WORLD 6 PRODUCTION CERTIFICATION (Levels 201–300)
    // =========================================================================

    @Test
    fun test02_world6CuratedLevels_exactDimensionsAndFullCoverage() {
        for (lvlId in 201..300) {
            val def = CuratedWorld6Levels.getLevel(lvlId)
            assertNotNull("World 6 Level $lvlId must exist in CuratedWorld6Levels", def)

            val sol = CuratedWorld6Levels.getSolution(lvlId)
            assertNotNull("World 6 Solution $lvlId must exist in CuratedWorld6Levels", sol)

            val is11x11 = lvlId <= 240
            val expectedDim = if (is11x11) 11 else 12
            val expectedCells = if (is11x11) 121 else 144

            assertEquals("Level $lvlId dimensions", GridDimensions(expectedDim, expectedDim), def!!.gridDimensions)
            assertEquals("Level $lvlId required cells", expectedCells, def.totalRequiredCells)
            assertEquals("Level $lvlId solution length", expectedCells, sol!!.positions.size)
            assertEquals("Level $lvlId unique visited cells", expectedCells, sol.positions.toSet().size)

            // Start checkpoint = 1, End checkpoint = N
            assertEquals("Level $lvlId start CP", def.checkpoints.first().position, sol.positions.first())
            assertEquals("Level $lvlId end CP", def.checkpoints.last().position, sol.positions.last())

            // Strictly ascending checkpoints 1..N
            val cpCount = def.checkpoints.size
            for (i in 0 until cpCount) {
                assertEquals("Level $lvlId checkpoint index $i must be number ${i + 1}", i + 1, def.checkpoints[i].number)
            }

            // Checkpoint range compliance
            val minCp = if (is11x11) 16 else 18
            val maxCp = if (is11x11) 24 else 30
            assertTrue(
                "Level $lvlId checkpoint count $cpCount must be in $minCp..$maxCp",
                cpCount in minCp..maxCp
            )

            // Wall count compliance
            val maxWalls = if (is11x11) 12 else 15
            assertTrue(
                "Level $lvlId wall count ${def.blockedEdges.size} must be <= $maxWalls",
                def.blockedEdges.size <= maxWalls
            )

            // Walls must be Manhattan orthogonal
            for (wall in def.blockedEdges) {
                assertTrue("Level $lvlId wall within bounds", def.gridDimensions.contains(wall.first) && def.gridDimensions.contains(wall.second))
                assertTrue("Level $lvlId wall orthogonal", wall.first.isOrthogonallyAdjacentTo(wall.second))
            }

            // Authoritative policy validation
            val policy = PuzzleConstraintPolicy.forSoloLevel(lvlId)
            val policyErrors = policy.validate(def)
            assertTrue("Level $lvlId policy errors: $policyErrors", policyErrors.isEmpty())

            // Anti-cage wall topology validation
            val topologyErrors = policy.validateWallTopology(def)
            assertTrue("Level $lvlId topology errors: $topologyErrors", topologyErrors.isEmpty())

            // Dual independent solution validation
            val completion = CompletionValidator.validate(def, sol)
            assertTrue("Level $lvlId CompletionValidator: $completion", completion is CompletionCheckResult.Success)

            val foundational = FoundationalPathValidator.validate(def, sol)
            assertTrue("Level $lvlId FoundationalPathValidator: $foundational", foundational.isWin)

            // Catalog map membership
            assertTrue("Level $lvlId in CuratedWorld6Levels.LEVELS", CuratedWorld6Levels.LEVELS.containsKey(lvlId))
            assertTrue("Level $lvlId in CuratedWorld6Levels.SOLUTIONS", CuratedWorld6Levels.SOLUTIONS.containsKey(lvlId))
        }
    }

    // =========================================================================
    // 3. LEVEL 300 GRANDMASTER FINALITY CERTIFICATION
    // =========================================================================

    @Test
    fun test03_level300GrandmasterFinality_completeVerification() {
        val def300 = CuratedWorld6Levels.getLevel(300)
        assertNotNull("Level 300 must exist", def300)

        val sol300 = CuratedWorld6Levels.getSolution(300)
        assertNotNull("Level 300 solution must exist", sol300)

        // 12x12 grid, 144 cells, full coverage
        assertEquals(GridDimensions(12, 12), def300!!.gridDimensions)
        assertEquals(144, def300.totalRequiredCells)
        assertEquals(144, sol300!!.positions.size)
        assertEquals(144, sol300.positions.toSet().size)

        // Master checkpoints (strictly 1..N within 18..30)
        val cpCount = def300.checkpoints.size
        assertTrue("Level 300 checkpoint count $cpCount must be in 18..30", cpCount in 18..30)
        for (i in 0 until cpCount) {
            assertEquals(i + 1, def300.checkpoints[i].number)
        }

        // Exact solver uniqueness check
        val solverResult = solver.solve(def300, SolverConfiguration.CHECK_UNIQUENESS)
        assertEquals("Level 300 must be SOLVED", SolverStatus.SOLVED, solverResult.status)
        assertEquals("Level 300 must have exactly ONE unique solution", UniquenessStatus.UNIQUE, solverResult.uniqueness)
        assertEquals("Level 300 solution must match canonical", sol300, solverResult.firstSolution)

        // Policy and topology
        val policy = PuzzleConstraintPolicy.forSoloLevel(300)
        assertTrue(policy.validate(def300).isEmpty())
        assertTrue(policy.validateWallTopology(def300).isEmpty())
    }

    // =========================================================================
    // 4. EXACT SOLVER UNIQUENESS ON KEY ANCHORS
    // =========================================================================

    @Test
    fun test04_exactSolverUniquenessOnAnchorMilestones() {
        val anchors = listOf(151, 180, 181, 200, 201, 240, 241, 300)
        for (levelId in anchors) {
            val def = levelProvider.getLevel(levelId)
            assertNotNull("Anchor level $levelId must exist", def)

            val result = solver.solve(def!!, SolverConfiguration.CHECK_UNIQUENESS)
            assertEquals("Anchor level $levelId must be solved", SolverStatus.SOLVED, result.status)
            assertEquals("Anchor level $levelId must have unique solution", UniquenessStatus.UNIQUE, result.uniqueness)

            val canonicalSol = levelProvider.getSolution(levelId)
            assertNotNull("Canonical solution for anchor $levelId must exist", canonicalSol)
            assertEquals("Anchor level $levelId first solution matches canonical", canonicalSol, result.firstSolution)
        }
    }

    // =========================================================================
    // 5. FULL 300-LEVEL PROGRESSION CONTINUITY & COLD STARTS
    // =========================================================================

    @Test
    fun test05_progressionContinuity_all299TransitionsAndTerminalCompletion() {
        val completedIds = mutableSetOf<Int>()

        for (lvl in 1..299) {
            completedIds.add(lvl)
            val world = WorldDefinition.forLevel(lvl)
            val resolution = ProgressionDestinationResolver.resolve(
                worldId = world.worldId,
                completedLevelOrLocal = lvl,
                completedLevelIds = completedIds
            )

            val isWorldEnd = lvl == world.lastLevelId
            if (isWorldEnd) {
                assertEquals("Level $lvl transition button", "NEXT WORLD", resolution.buttonLabel)
                assertTrue("Level $lvl destination NextWorldEntry", resolution.destination is ProgressionDestination.NextWorldEntry)
                val nextWorldDest = resolution.destination as ProgressionDestination.NextWorldEntry
                assertEquals("Next world id", world.worldId + 1, nextWorldDest.worldId)
            } else {
                assertEquals("Level $lvl transition button", "NEXT LEVEL", resolution.buttonLabel)
                assertTrue("Level $lvl destination NextLevel", resolution.destination is ProgressionDestination.NextLevel)
                val nextLevelDest = resolution.destination as ProgressionDestination.NextLevel
                assertEquals("Next level id", lvl + 1, nextLevelDest.levelId)
            }
        }

        // Terminal level 300 completion
        completedIds.add(300)
        val finalResolution = ProgressionDestinationResolver.resolve(
            worldId = 6,
            completedLevelOrLocal = 300,
            completedLevelIds = completedIds
        )
        assertEquals("Terminal completion button", "JOURNEY COMPLETE", finalResolution.buttonLabel)
        assertTrue("Terminal destination JourneyComplete", finalResolution.destination is ProgressionDestination.JourneyComplete)
    }

    // =========================================================================
    // 6. TOTAL 300-LEVEL CATALOG PURITY & DETERMINISTIC PROVIDER INTEGRITY
    // =========================================================================

    @Test
    fun test06_all300Levels_fullyPackagedAndValidInProvider() {
        for (lvlId in 1..300) {
            assertTrue("Level $lvlId must be marked as packaged", levelProvider.isLevelPackaged(lvlId))
            val def = levelProvider.getLevel(lvlId)
            assertNotNull("Level $lvlId must be non-null in levelProvider", def)
            val sol = levelProvider.getSolution(lvlId)
            assertNotNull("Solution $lvlId must be non-null in levelProvider", sol)

            val expectedDim = WorldDefinition.getExpectedGridDimensions(lvlId)
            assertEquals("Level $lvlId expected dimensions", expectedDim, def!!.gridDimensions)
            assertEquals("Level $lvlId 100% full coverage", expectedDim.totalCells, def.totalRequiredCells)
            assertEquals("Level $lvlId solution length", expectedDim.totalCells, sol!!.positions.size)
        }
    }
}
