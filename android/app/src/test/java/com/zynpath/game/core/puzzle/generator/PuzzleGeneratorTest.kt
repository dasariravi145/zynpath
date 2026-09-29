package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.fixtures.GeneratedPuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative unit tests for [PuzzleGenerator].
 *
 * Implements Prompt 9 Sections 30 and 33:
 * 1. Same seed produces identical output.
 * 2. Different seeds can produce different candidates.
 * 3. Generated checkpoints are contiguous.
 * 4. Checkpoint 1 occupies the route start.
 * 5. Final checkpoint occupies the route end.
 * 6. Checkpoints follow route order.
 * 7. Generated walls are valid.
 * 8. No wall blocks the intended route.
 * 9. Wall counts remain within configuration.
 * 10. Grid dimensions match configuration.
 * 11. Every required cell is covered by the verified solution.
 * 12. Every accepted puzzle passes structural validation.
 * 13. Every accepted puzzle passes solver verification.
 */
class PuzzleGeneratorTest {

    private lateinit var generator: PuzzleGenerator

    @Before
    fun setUp() {
        generator = PuzzleGenerator()
    }

    // =========================================================================
    // 1. REPRODUCIBILITY (Same Seed -> Identical Output)
    // =========================================================================

    @Test
    fun `same seed produces identical puzzle output`() {
        val config1 = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            minWalls = 0,
            maxWalls = 0,
            seed = 12345L,
            routeStyle = RouteStyle.SERPENTINE
        )
        val config2 = config1.copy()

        val result1 = generator.generate(config1)
        val result2 = generator.generate(config2)

        assertTrue("Result 1 should be generated", result1.isSuccess)
        assertTrue("Result 2 should be generated", result2.isSuccess)

        val puzzle1 = result1.puzzle!!
        val puzzle2 = result2.puzzle!!

        assertEquals("Checkpoints must match exactly", puzzle1.checkpoints, puzzle2.checkpoints)
        assertEquals("Walls must match exactly", puzzle1.blockedEdges, puzzle2.blockedEdges)
        assertEquals("Fingerprints must match exactly", result1.canonicalFingerprint, result2.canonicalFingerprint)
        assertEquals("Verified solutions must match", result1.verifiedSolution, result2.verifiedSolution)
    }

    // =========================================================================
    // 2. VARIATION (Different Seeds -> Different Output)
    // =========================================================================

    @Test
    fun `different seeds can produce different candidates`() {
        val configA = GenerationConfiguration(
            dimensions = GridDimensions(5, 5),
            checkpointCount = 5,
            minWalls = 2,
            maxWalls = 4,
            seed = 111L,
            routeStyle = RouteStyle.MIXED
        )
        val configB = configA.copy(seed = 9999L)

        val resultA = generator.generate(configA)
        val resultB = generator.generate(configB)

        assertTrue(resultA.isSuccess)
        assertTrue(resultB.isSuccess)

        assertNotEquals(
            "Different seeds should produce different canonical fingerprints",
            resultA.canonicalFingerprint,
            resultB.canonicalFingerprint
        )
    }

    // =========================================================================
    // 3. CHECKPOINTS VALIDATION (Contiguity, Ordering, Origin & Terminal)
    // =========================================================================

    @Test
    fun `generated checkpoints are contiguous, ordered, and bound start to end`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(5, 5),
            checkpointCount = 6,
            minWalls = 1,
            maxWalls = 3,
            seed = 777L
        )

        val result = generator.generate(config)
        assertTrue(result.isSuccess)

        val puzzle = result.puzzle!!
        val solution = result.verifiedSolution!!

        assertEquals("Checkpoint count must match configuration", 6, puzzle.checkpoints.size)

        // Numbers must be 1..6 contiguous
        val numbers = puzzle.checkpoints.map { it.number }.sorted()
        assertEquals(listOf(1, 2, 3, 4, 5, 6), numbers)

        // Checkpoint 1 occupies route start
        val cp1 = puzzle.checkpoints.first { it.number == 1 }
        assertEquals("Checkpoint #1 must be at solution origin", solution.startPosition, cp1.position)

        // Final checkpoint occupies route end
        val finalCp = puzzle.checkpoints.first { it.number == 6 }
        assertEquals("Final checkpoint must be at solution end", solution.currentHead, finalCp.position)

        // Checkpoints appear in ascending order along traversal
        var lastRouteIndex = -1
        for (i in 1..6) {
            val cp = puzzle.checkpoints.first { it.number == i }
            val indexInRoute = solution.positions.indexOf(cp.position)
            assertTrue("Checkpoint #$i must be along route", indexInRoute >= 0)
            assertTrue("Checkpoint #$i must follow traversal order", indexInRoute > lastRouteIndex)
            lastRouteIndex = indexInRoute
        }
    }

    // =========================================================================
    // 4. WALL VALIDATION (Orthogonal, Within Bounds, No Route Block)
    // =========================================================================

    @Test
    fun `generated walls respect wall count range and never block the verified solution`() {
        val minW = 3
        val maxW = 5
        val config = GenerationConfiguration(
            dimensions = GridDimensions(6, 6),
            checkpointCount = 6,
            minWalls = minW,
            maxWalls = maxW,
            seed = 888L
        )

        val result = generator.generate(config)
        assertTrue(result.isSuccess)

        val puzzle = result.puzzle!!
        val solution = result.verifiedSolution!!

        val wallCount = puzzle.blockedEdges.size
        assertTrue("Wall count ($wallCount) must be >= $minW", wallCount >= minW)
        assertTrue("Wall count ($wallCount) must be <= $maxW", wallCount <= maxW)

        // Every wall must be orthogonal and inside grid
        for (wall in puzzle.blockedEdges) {
            assertTrue("Wall must be orthogonal", wall.first.isOrthogonalNeighbor(wall.second))
            assertTrue("Wall first cell must be in grid", puzzle.gridDimensions.contains(wall.first))
            assertTrue("Wall second cell must be in grid", puzzle.gridDimensions.contains(wall.second))
        }

        // Invariant: No wall may block any consecutive pair in the verified solution
        for (i in 0 until solution.size - 1) {
            val stepEdge = BlockedEdge.between(solution.positions[i], solution.positions[i + 1])
            assertTrue("Solution step $stepEdge must not be blocked by any wall", stepEdge !in puzzle.blockedEdges)
        }
    }

    // =========================================================================
    // 5. TOPOLOGY & COVERAGE (Full Cell Coverage)
    // =========================================================================

    @Test
    fun `every required cell is covered by the verified solution`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(5, 5),
            checkpointCount = 5,
            minWalls = 0,
            maxWalls = 2,
            seed = 555L
        )

        val result = generator.generate(config)
        assertTrue(result.isSuccess)

        val puzzle = result.puzzle!!
        val solution = result.verifiedSolution!!

        assertEquals("Puzzle must have 25 required cells", 25, puzzle.totalRequiredCells)
        assertEquals("Solution must visit 25 cells", 25, solution.size)
        assertEquals("Solution must cover all required cells", puzzle.requiredCells, solution.positions.toSet())
    }

    // =========================================================================
    // 6. STRUCTURAL AND SOLVER INTEGRITY
    // =========================================================================

    @Test
    fun `every accepted puzzle passes structural validation and solver verification`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            minWalls = 1,
            maxWalls = 2,
            seed = 444L
        )

        val result = generator.generate(config)
        assertTrue(result.isSuccess)

        val puzzle = result.puzzle!!

        // Independent structural validation
        val structuralCheck = PuzzleDefinitionValidator.validate(puzzle)
        assertTrue(
            "Puzzle must be structurally valid: ${(structuralCheck as? DefinitionValidationResult.Invalid)?.errorSummary}",
            structuralCheck.isValid
        )

        // Independent solver verification
        val solver = com.zynpath.game.core.puzzle.solver.PuzzleSolver()
        val solveResult = solver.solve(puzzle)
        assertTrue("Solver must confirm puzzle is solved", solveResult.isSolved)
        assertNotNull("Solver must provide a solution", solveResult.firstSolution)
    }

    // =========================================================================
    // 7. WORLD PROGRESSION CONTRACT FIXTURES (WORLDS 1 THROUGH 6)
    // =========================================================================

    @Test
    fun `authoritative world progression fixtures 1 through 6 generate and validate`() {
        // World 1 (4x4, 0 walls)
        val f1 = GeneratedPuzzleFixtures.fixture4x4Clean
        assertTrue("World 1 fixture must be valid", f1.isSuccess)
        assertEquals(GridDimensions(4, 4), f1.puzzle!!.gridDimensions)
        assertEquals(0, f1.puzzle!!.blockedEdges.size)

        // World 2 (5x5, 0 walls)
        val f2 = GeneratedPuzzleFixtures.fixture5x5Clean
        assertTrue("World 2 fixture must be valid", f2.isSuccess)
        assertEquals(GridDimensions(5, 5), f2.puzzle!!.gridDimensions)
        assertEquals(0, f2.puzzle!!.blockedEdges.size)

        // World 3 (5x5, 3 walls)
        val f3 = GeneratedPuzzleFixtures.fixture5x5WithWalls
        assertTrue("World 3 fixture must be valid", f3.isSuccess)
        assertEquals(GridDimensions(5, 5), f3.puzzle!!.gridDimensions)
        assertEquals(3, f3.puzzle!!.blockedEdges.size)

        // World 4 (6x6, 4 walls)
        val f4 = GeneratedPuzzleFixtures.fixture6x6WithWalls
        assertTrue("World 4 fixture must be valid", f4.isSuccess)
        assertEquals(GridDimensions(6, 6), f4.puzzle!!.gridDimensions)
        assertEquals(4, f4.puzzle!!.blockedEdges.size)

        // World 5 (7x7, 6 walls)
        val f5 = GeneratedPuzzleFixtures.fixture7x7WithWalls
        assertTrue("World 5 fixture must be valid", f5.isSuccess)
        assertEquals(GridDimensions(7, 7), f5.puzzle!!.gridDimensions)
        assertEquals(6, f5.puzzle!!.blockedEdges.size)

        // World 6 (8x8, 8 walls)
        val f6 = GeneratedPuzzleFixtures.fixture8x8WithWalls
        assertTrue("World 6 fixture must be valid", f6.isSuccess)
        assertEquals(GridDimensions(8, 8), f6.puzzle!!.gridDimensions)
        assertEquals(8, f6.puzzle!!.blockedEdges.size)
    }

    // =========================================================================
    // 8. PROPERTY-BASED RANDOMIZED TEST (Multiple Seeds)
    // =========================================================================

    @Test
    fun `property-based test across varied seeds maintains all invariants`() {
        val testSeeds = listOf(11L, 42L, 108L, 256L, 999L)

        for (seed in testSeeds) {
            val config = GenerationConfiguration(
                dimensions = GridDimensions(4, 4),
                checkpointCount = 4,
                minWalls = 0,
                maxWalls = 2,
                seed = seed
            )

            val result = generator.generate(config)
            assertTrue("Seed $seed must produce a valid puzzle", result.isSuccess)

            val puzzle = result.puzzle!!
            val solution = result.verifiedSolution!!

            // Structural check
            val validation = PuzzleDefinitionValidator.validate(puzzle)
            assertTrue("Seed $seed must be structurally valid", validation.isValid)

            // Full coverage
            assertEquals(16, solution.size)
            assertEquals(puzzle.requiredCells, solution.positions.toSet())

            // No self-intersections
            assertEquals(16, solution.positions.distinct().size)

            // Start & End
            assertEquals(puzzle.startCheckpoint?.position, solution.startPosition)
            assertEquals(puzzle.finalCheckpoint?.position, solution.currentHead)
        }
    }
}
