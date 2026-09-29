package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Solution counting, uniqueness proofs, and search limit test suite for [PuzzleSolver].
 * Adheres strictly to Prompt 8 Section 29.
 */
class PuzzleSolverCountingAndUniquenessTest {

    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
    }

    // =========================================================================
    // 1. First-Solution Mode vs Uniqueness Semantics
    // =========================================================================

    @Test
    fun findFirstSolution_stopsImmediatelyAndDoesNotClaimUniqueness() {
        // Run on puzzle known to have multiple solutions
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle3x3MultipleSolutions)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        assertEquals(1, result.solutionCount)

        // Non-exhaustive search must NOT claim uniqueness!
        assertFalse("Search with maxSolutions=1 is not exhaustive", result.isExhaustive)
        assertFalse("ONE FOUND SOLUTION DOES NOT AUTOMATICALLY MEAN UNIQUE", result.isUnique)
        assertEquals(UniquenessStatus.UNKNOWN, result.uniqueness)
    }

    @Test
    fun checkUniqueness_onUniquePuzzle_provesUniquenessExhaustively() {
        val result = solver.checkUniqueness(SamplePuzzleFixtures.puzzle3x3Unique)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        assertEquals(1, result.solutionCount)
        assertTrue("Exhaustive search completed without hitting limits", result.isExhaustive)
        assertTrue("Exhaustive search finding exactly 1 solution proves uniqueness", result.isUnique)
        assertEquals(UniquenessStatus.UNIQUE, result.uniqueness)
    }

    // =========================================================================
    // 2. Multiple-Solution Detection
    // =========================================================================

    @Test
    fun checkUniqueness_onMultipleSolutionPuzzle_discoversDistinctSolutions() {
        val result = solver.checkUniqueness(SamplePuzzleFixtures.puzzle3x3MultipleSolutions)

        assertEquals(SolverStatus.MULTIPLE_SOLUTIONS, result.status)
        assertTrue(result.isSolved)
        assertEquals(2, result.solutionCount)
        assertFalse(result.isUnique)
        assertEquals(UniquenessStatus.NON_UNIQUE, result.uniqueness)

        val sol1 = result.solutions[0]
        val sol2 = result.solutions[1]
        assertNotEquals("Discovered solutions must be distinct", sol1, sol2)
        assertEquals(9, sol1.length)
        assertEquals(9, sol2.length)
    }

    @Test
    fun countSolutions_preventsDuplicateSolutions() {
        val result = solver.countSolutions(SamplePuzzleFixtures.puzzle3x3MultipleSolutions, limit = 5)

        assertTrue(result.solutionCount >= 2)
        val uniqueSet = result.solutions.toSet()
        assertEquals("All returned solutions must be pairwise unique", uniqueSet.size, result.solutions.size)
    }

    // =========================================================================
    // 3. Search Limits
    // =========================================================================

    @Test
    fun searchLimit_beforeFindingSolution_reportsLimitReachedWithoutClaimingUnsolvable() {
        val budgetConfig = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = 3L // Extremely tight node budget
        )

        val result = solver.solve(SamplePuzzleFixtures.puzzle5x5Valid, budgetConfig)

        assertEquals(SolverStatus.SEARCH_LIMIT_REACHED, result.status)
        assertEquals(0, result.solutionCount)
        assertFalse(result.isSolved)
        assertFalse("Search limit does NOT prove unsolvability", result.isUnsolvable)
        assertFalse(result.isExhaustive)
        assertEquals(UniquenessStatus.UNKNOWN, result.uniqueness)
    }

    @Test
    fun searchLimit_afterFindingOneSolution_preservesSolutionAndReportsLimitReached() {
        // Node limit sufficient to find 1 solution on 3x3, but not to search for a 2nd solution
        val budgetConfig = SolverConfiguration(
            maxSolutions = 2,
            nodeLimit = 20L
        )

        val result = solver.solve(SamplePuzzleFixtures.puzzle3x3MultipleSolutions, budgetConfig)

        if (result.status == SolverStatus.SEARCH_LIMIT_REACHED) {
            // Finding 1 solution proves solvability even if limit reached
            assertTrue("Finding 1 solution proves solvability", result.isSolved)
            assertEquals(1, result.solutionCount)
            assertFalse("Search was cut short, uniqueness is not proven", result.isUnique)
            assertFalse(result.isExhaustive)
            assertEquals(UniquenessStatus.UNKNOWN, result.uniqueness)
        } else {
            // In case it completed within 20 nodes
            assertEquals(SolverStatus.MULTIPLE_SOLUTIONS, result.status)
        }
    }

    // =========================================================================
    // 4. Cancellation & Determinism
    // =========================================================================

    @Test
    fun cancellationSignal_abortsSearchCooperatively() {
        var callCount = 0
        val cancelConfig = SolverConfiguration(
            maxSolutions = 2,
            cancellationSignal = {
                callCount++
                true // Immediately cancel on first poll
            }
        )

        val result = solver.solve(SamplePuzzleFixtures.puzzle5x5Valid, cancelConfig)

        assertEquals(SolverStatus.CANCELLED, result.status)
        assertFalse(result.isExhaustive)
        assertEquals(UniquenessStatus.UNKNOWN, result.uniqueness)
        assertTrue(callCount > 0)
    }

    @Test
    fun deterministicRepeatedRuns_yieldIdenticalResults() {
        val run1 = solver.checkUniqueness(SamplePuzzleFixtures.puzzle3x3MultipleSolutions)
        val run2 = solver.checkUniqueness(SamplePuzzleFixtures.puzzle3x3MultipleSolutions)

        assertEquals(run1.status, run2.status)
        assertEquals(run1.solutionCount, run2.solutionCount)
        assertEquals(run1.solutions, run2.solutions)
        assertEquals(run1.statistics.nodesExplored, run2.statistics.nodesExplored)
        assertEquals(run1.statistics.backtracks, run2.statistics.backtracks)
    }
}
