package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Solvability and validation test suite for [PuzzleSolver] adhering to Prompt 8 Section 28.
 */
class PuzzleSolverSolvabilityTest {

    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
    }

    // =========================================================================
    // 1. Valid Puzzles Produce Solutions
    // =========================================================================

    @Test
    fun solve_valid4x4Puzzle_producesFullCoverageSolution() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle4x4Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        assertEquals(1, result.solutionCount)

        val solution = result.firstSolution
        assertNotNull("Solution path must not be null", solution)
        assertEquals(16, solution!!.length)
        assertEquals(GridPosition(0, 0), solution.positions.first())
        assertEquals(GridPosition(3, 0), solution.positions.last())

        // Authoritative independent verification
        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle4x4Valid, solution)
        assertTrue("Discovered solution must pass CompletionValidator", validation is CompletionCheckResult.Success)
    }

    @Test
    fun solve_valid5x5Puzzle_producesFullCoverageSolution() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle5x5Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        assertEquals(1, result.solutionCount)

        val solution = result.firstSolution
        assertNotNull(solution)
        assertEquals(25, solution!!.length)
        assertEquals(GridPosition(0, 0), solution.positions.first())
        assertEquals(GridPosition(4, 4), solution.positions.last())

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle5x5Valid, solution)
        assertTrue("5x5 solution must pass CompletionValidator", validation is CompletionCheckResult.Success)
    }

    @Test
    fun solve_valid5x5PuzzleWithWalls_producesValidSolutionNavigatingWalls() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle5x5WithWalls)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val solution = result.firstSolution
        assertNotNull(solution)
        assertEquals(25, solution!!.length)

        // Verify wall compliance: none of the 3 walls are crossed
        val graph = SamplePuzzleFixtures.puzzle5x5WithWalls.graph
        for (i in 0 until solution.length - 1) {
            val a = solution.positions[i]
            val b = solution.positions[i + 1]
            assertFalse("Move from $a to $b must not cross blocked wall", graph.isBlocked(a, b))
        }

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle5x5WithWalls, solution)
        assertTrue("Wall puzzle solution must pass CompletionValidator", validation is CompletionCheckResult.Success)
    }

    @Test
    fun solve_valid6x6Puzzle_producesFullCoverageSolution() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle6x6Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val solution = result.firstSolution
        assertNotNull(solution)
        assertEquals(36, solution!!.length)
        assertEquals(GridPosition(0, 0), solution.positions.first())
        assertEquals(GridPosition(5, 0), solution.positions.last())

        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle6x6Valid, solution)
        assertTrue("6x6 solution must pass CompletionValidator", validation is CompletionCheckResult.Success)
    }

    // =========================================================================
    // 2. Structurally Invalid Puzzle Rejection
    // =========================================================================

    @Test
    fun solve_structurallyInvalidPuzzle_returnsInvalidPuzzleStatus() {
        val duplicateCpPuzzle = SamplePuzzleFixtures.createDuplicateCheckpointNumberFixture()
        val result = solver.solve(duplicateCpPuzzle)

        assertEquals(SolverStatus.INVALID_PUZZLE, result.status)
        assertFalse(result.isSolved)
        assertEquals(0, result.solutionCount)
        assertNotNull(result.diagnosticMessage)
        assertTrue(result.diagnosticMessage!!.contains("DUPLICATE_CHECKPOINT_NUMBER"))
    }

    @Test
    fun solve_missingStartCheckpoint_returnsInvalidPuzzleStatus() {
        val noStartPuzzle = SamplePuzzleFixtures.createMissingStartCheckpointFixture()
        val result = solver.solve(noStartPuzzle)

        assertEquals(SolverStatus.INVALID_PUZZLE, result.status)
        assertFalse(result.isSolved)
        assertTrue(result.diagnosticMessage!!.contains("MISSING_START_CHECKPOINT"))
    }

    // =========================================================================
    // 3. Structurally Valid Unsolvable Puzzles
    // =========================================================================

    @Test
    fun solve_unsolvableParityPuzzle_returnsUnsolvableAfterExhaustiveSearch() {
        val result = solver.solve(
            SamplePuzzleFixtures.puzzle4x4UnsolvableParity,
            SolverConfiguration.CHECK_UNIQUENESS
        )

        assertEquals(SolverStatus.UNSOLVABLE, result.status)
        assertFalse(result.isSolved)
        assertTrue("Exhaustive search must be reported for completed search", result.isExhaustive)
        assertTrue(result.isUnsolvable)
        assertEquals(0, result.solutionCount)
        assertTrue(result.statistics.nodesExplored > 0)
    }

    @Test
    fun solve_unsolvableWallsPuzzle_returnsUnsolvableAfterExhaustiveSearch() {
        val result = solver.solve(
            SamplePuzzleFixtures.puzzle4x4UnsolvableWalls,
            SolverConfiguration.CHECK_UNIQUENESS
        )

        assertEquals(SolverStatus.UNSOLVABLE, result.status)
        assertFalse(result.isSolved)
        assertTrue(result.isExhaustive)
        assertTrue(result.isUnsolvable)
        assertEquals(0, result.solutionCount)
    }

    // =========================================================================
    // 4. Exact Rule Conformance
    // =========================================================================

    @Test
    fun solve_allReturnedSolutions_satisfyAllTwelveGameRules() {
        val result = solver.findFirstSolution(SamplePuzzleFixtures.puzzle4x4Valid)
        val path = result.firstSolution!!
        val def = SamplePuzzleFixtures.puzzle4x4Valid

        // Rule 1: Origin at #1
        assertEquals(def.checkpoints.first { it.number == 1 }.position, path.positions.first())

        // Rule 3: Orthogonal movement only
        for (i in 0 until path.length - 1) {
            val a = path.positions[i]
            val b = path.positions[i + 1]
            assertTrue("Step must be orthogonal", a.isOrthogonallyAdjacentTo(b))
        }

        // Rule 5: 100% full coverage
        assertEquals(def.totalRequiredCells, path.positions.toSet().size)

        // Rule 6 & 7: Simple path, no revisits
        assertEquals(path.length, path.positions.toSet().size)

        // Rule 8: No wall crossings
        for (i in 0 until path.length - 1) {
            assertFalse(def.graph.isBlocked(path.positions[i], path.positions[i + 1]))
        }

        // Rule 2 & 9 & 10: Checkpoints visited in strictly ascending order
        val visitedCpOrder = path.positions.mapNotNull { def.getCheckpointAt(it) }
        assertEquals(listOf(1, 2, 3, 4), visitedCpOrder)

        // Rule 11: Terminates at final checkpoint #4
        assertEquals(def.checkpoints.maxByOrNull { it.number }!!.position, path.positions.last())
    }

    // =========================================================================
    // 5. Asynchronous Solving Interface (Section 33)
    // =========================================================================

    @Test
    fun solveAsync_executesOffMainThreadAndReturnsValidSolution() = runTest {
        val result = solver.solveAsync(SamplePuzzleFixtures.puzzle4x4Valid)

        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        assertNotNull(result.firstSolution)
        assertEquals(16, result.firstSolution!!.length)
    }

    // =========================================================================
    // 6. Engine Defect Detection via SolutionValidator (Section 23)
    // =========================================================================

    @Test
    fun solutionValidator_detectsInvalidCandidateAsDefect() {
        // Construct an invalid path (skipping cells)
        val defectivePath = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(3, 0) // Invalid jump & incomplete
        )

        val validation = SolutionValidator.validate(SamplePuzzleFixtures.puzzle4x4Valid, defectivePath)
        assertTrue(
            "SolutionValidator must catch defective paths",
            validation is SolutionValidationResult.Defect
        )
        val defect = validation as SolutionValidationResult.Defect
        assertTrue(defect.message.isNotEmpty())
    }
}
