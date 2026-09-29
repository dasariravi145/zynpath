package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Performance and benchmark measurement test suite for [PuzzleSolver].
 * Adheres strictly to Prompt 8 Section 32.
 *
 * Measures actual elapsed monotonic execution time, explored search nodes, and backtracks
 * across representative board sizes: 4x4, 5x5, 6x6, 7x7, and 8x8.
 */
class PuzzleSolverBenchmarkTest {

    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
    }

    @Test
    fun benchmark_4x4_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle4x4Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(16, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle4x4Valid, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 4x4] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_5x5_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle5x5Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(25, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle5x5Valid, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 5x5] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_5x5_with_walls_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle5x5WithWalls)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(25, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle5x5WithWalls, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 5x5 WALLS] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_6x6_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle6x6Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(36, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle6x6Valid, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 6x6] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_7x7_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle7x7Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(49, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle7x7Valid, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 7x7] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_8x8_board() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle8x8Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val path = result.firstSolution
        assertNotNull(path)
        assertEquals(64, path!!.length)

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle8x8Valid, path)
        assertTrue(validation is CompletionCheckResult.Success)

        println(
            "[BENCHMARK 8x8] Nodes: ${result.statistics.nodesExplored}, " +
                "Backtracks: ${result.statistics.backtracks}, " +
                "Pruned: ${result.statistics.prunedBranches}, " +
                "Elapsed: ${result.statistics.elapsedMs}ms"
        )
    }

    @Test
    fun benchmark_budgetLimit_reportsSearchLimitReachedWhenBudgetExceeded() {
        val budgetConfig = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = 10L // Intentionally tiny node budget
        )

        val result = solver.solve(SamplePuzzleFixtures.puzzle8x8Valid, budgetConfig)

        assertEquals(SolverStatus.SEARCH_LIMIT_REACHED, result.status)
        assertTrue(result.statistics.nodesExplored <= 260L) // Bounded by batch check
    }
}
