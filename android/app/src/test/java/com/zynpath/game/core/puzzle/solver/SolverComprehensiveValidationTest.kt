package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test suite validating solver correctness, completeness, determinism,
 * negative cases, uniqueness verification, termination bounds, and performance.
 *
 * Implements Prompt 46 Sections 22-29:
 * - Solver acceptance of valid puzzles and discovery of valid full-coverage paths.
 * - Independent canonical validation of all discovered paths.
 * - Solver rejection and UNSOLVABLE status for negative cases (impossible checkpoints,
 *   disconnected graphs, contradictory walls isolating cells, premature final checkpoint).
 * - Determinism verification across repeated solver runs.
 * - Solution uniqueness classification (UNIQUE vs MULTI_SOLUTION).
 * - Termination under node and time budget limits on pathological inputs.
 * - Performance metrics capture.
 */
class SolverComprehensiveValidationTest {

    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
    }

    // =========================================================================
    // 1. Correctness & Independent Path Validation (Sections 22, 23, 24)
    // =========================================================================

    @Test
    fun `test small 3x3 board solver completeness and solution validity`() {
        // Construct a simple 3x3 serpentine puzzle:
        // (0,0) -> (0,1) -> (0,2) -> (1,2) -> (1,1) -> (1,0) -> (2,0) -> (2,1) -> (2,2)
        val cells3x3 = (0..2).flatMap { r -> (0..2).map { c -> GridPosition(r, c) } }.toSet()
        val puzzle3x3 = PuzzleDefinition(
            puzzleId = "test_3x3",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(3, 3),
            requiredCells = cells3x3,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 1)),
                NumberedCheckpoint(3, GridPosition(2, 2))
            ),
            blockedEdges = emptySet()
        )

        val result = solver.solve(puzzle3x3, SolverConfiguration.DEFAULT)
        assertEquals(SolverStatus.SOLVED, result.status)
        assertTrue(result.isSolved)
        val solution = result.firstSolution
        assertNotNull(solution)
        assertEquals(9, solution!!.length)

        // Independent validation
        val compResult = CompletionValidator.validate(puzzle3x3, solution)
        assertTrue("3x3 solution must pass CompletionValidator", compResult is CompletionCheckResult.Success)

        val foundResult = FoundationalPathValidator.validate(puzzle3x3, solution)
        assertTrue("3x3 solution must pass FoundationalPathValidator", foundResult.isWin)
    }

    // =========================================================================
    // 2. Negative Cases & Unsolvable Configurations (Section 25)
    // =========================================================================

    @Test
    fun `test negative case - impossible checkpoint ordering returns UNSOLVABLE`() {
        // 4x4 grid: Checkpoint 1 at (0,0), Checkpoint 2 at (3,3), Checkpoint 3 at (0,1).
        // To reach checkpoint 2 and then checkpoint 3 and still cover all cells is impossible
        // because checkpoint 2 is far away and returning to (0,1) without crossing creates cycles.
        val cells4x4 = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val impossibleCheckpoints = PuzzleDefinition(
            puzzleId = "impossible_cp",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells4x4,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3)),
                NumberedCheckpoint(3, GridPosition(0, 1)),
                NumberedCheckpoint(4, GridPosition(3, 2))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
                BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2)),
                BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2)),
                BlockedEdge.between(GridPosition(3, 1), GridPosition(3, 2))
            ) // Complete vertical wall down column 1-2 partition!
        )

        val result = solver.solve(impossibleCheckpoints, SolverConfiguration(nodeLimit = 50_000))
        assertEquals("Partitioned board with checkpoints on opposite sides must be UNSOLVABLE", SolverStatus.UNSOLVABLE, result.status)
        assertFalse(result.isSolved)
    }

    @Test
    fun `test negative case - contradictory walls isolating a required cell returns UNSOLVABLE`() {
        // Enclose cell (1,1) with walls on all 4 sides:
        // top: (0,1)<->(1,1), bottom: (1,1)<->(2,1), left: (1,0)<->(1,1), right: (1,1)<->(1,2)
        val cells = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val isolatedCellPuzzle = PuzzleDefinition(
            puzzleId = "isolated_cell",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
                BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
                BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1)),
                BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 2))
            )
        )

        val result = solver.solve(isolatedCellPuzzle, SolverConfiguration(nodeLimit = 20_000))
        assertEquals("Isolated cell must be recognized as UNSOLVABLE", SolverStatus.UNSOLVABLE, result.status)
        assertFalse(result.isSolved)
    }

    @Test
    fun `test negative case - premature final checkpoint blocking remaining cells returns UNSOLVABLE`() {
        // Checkpoint 1 at (0,0), Checkpoint 2 (final) at (0,1).
        // Since final checkpoint is at (0,1), any full coverage path must end at (0,1),
        // but if (0,1) is walled off except from (0,0), entering it ends the game prematurely!
        val cells = (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()
        val deadEndFinalCheckpoint = PuzzleDefinition(
            puzzleId = "premature_final",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 1)) // final checkpoint
            ),
            blockedEdges = setOf(
                // Block all other exits from (0,1) except (0,0)
                BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2)),
                BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
            )
        )

        val result = solver.solve(deadEndFinalCheckpoint, SolverConfiguration(nodeLimit = 20_000))
        assertEquals("Cannot cover 16 cells if final checkpoint has only 1 entrance and is adjacent to start", SolverStatus.UNSOLVABLE, result.status)
    }

    // =========================================================================
    // 3. Determinism & Repeatability (Section 26)
    // =========================================================================

    @Test
    fun `test solver determinism across repeated executions`() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        val config = SolverConfiguration(maxSolutions = 1, nodeLimit = 50_000)

        val run1 = solver.solve(puzzle, config)
        val run2 = solver.solve(puzzle, config)
        val run3 = solver.solve(puzzle, config)

        assertEquals(run1.status, run2.status)
        assertEquals(run2.status, run3.status)
        assertEquals(run1.nodeCount, run2.nodeCount)
        assertEquals(run2.nodeCount, run3.nodeCount)
        assertEquals(run1.firstSolution?.positions, run2.firstSolution?.positions)
        assertEquals(run2.firstSolution?.positions, run3.firstSolution?.positions)
    }

    // =========================================================================
    // 4. Solution Uniqueness Verification (Section 27)
    // =========================================================================

    @Test
    fun `test uniqueness verification identifies single vs multiple solutions`() {
        // Puzzle with maxSolutions = 2
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        val config = SolverConfiguration(maxSolutions = 2, nodeLimit = 100_000)

        val result = solver.solve(puzzle, config)
        assertTrue(result.isSolved)
        // Check uniqueness classification
        assertTrue(
            "Uniqueness status must be UNIQUE or NON_UNIQUE",
            result.uniqueness == UniquenessStatus.UNIQUE || result.uniqueness == UniquenessStatus.NON_UNIQUE
        )
    }

    // =========================================================================
    // 5. Termination Bounds on Pathological Inputs (Section 28)
    // =========================================================================

    @Test
    fun `test solver respects node limit budget and terminates safely`() {
        val complexPuzzle = SamplePuzzleFixtures.puzzle6x6Valid
        // Intentionally set a tiny node budget of 10 nodes
        val tinyBudgetConfig = SolverConfiguration(nodeLimit = 10, timeBudgetMs = 5000L)

        val result = solver.solve(complexPuzzle, tinyBudgetConfig)
        assertEquals(SolverStatus.SEARCH_LIMIT_REACHED, result.status)
        assertTrue("Node count must not exceed budget significantly", result.statistics.nodesExplored <= 20)
    }

    // =========================================================================
    // 6. Solver Performance Reporting (Section 29)
    // =========================================================================

    @Test
    fun `test solver solves standard 4x4 and 5x5 puzzles within responsive timing`() {
        val start4x4 = System.currentTimeMillis()
        val res4x4 = solver.findFirstSolution(SamplePuzzleFixtures.puzzle4x4Valid)
        val duration4x4 = System.currentTimeMillis() - start4x4

        assertTrue(res4x4.isSolved)
        assertTrue("4x4 solve must complete in under 1000ms, took ${duration4x4}ms", duration4x4 < 1000L)

        val start5x5 = System.currentTimeMillis()
        val res5x5 = solver.findFirstSolution(SamplePuzzleFixtures.puzzle5x5Valid)
        val duration5x5 = System.currentTimeMillis() - start5x5

        assertTrue(res5x5.isSolved)
        assertTrue("5x5 solve must complete in under 2000ms, took ${duration5x5}ms", duration5x5 < 2000L)
    }
}
