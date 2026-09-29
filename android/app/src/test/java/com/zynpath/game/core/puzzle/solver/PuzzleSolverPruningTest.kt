package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Sound mathematical pruning verification and comparative testing suite for [PuzzleSolver].
 * Adheres strictly to Prompt 8 Sections 30 and 31.
 */
class PuzzleSolverPruningTest {

    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
    }

    // =========================================================================
    // 1. Independent Unit Tests for Pruning Rules
    // =========================================================================

    @Test
    fun rule1_prematureFinalCheckpoint_pruningSoundness() {
        val finalPos = GridPosition(2, 2)

        // Entering final checkpoint with 5 remaining required cells -> MUST PRUNE
        assertTrue(SolverPruningRules.isPrematureFinalCheckpoint(finalPos, finalPos, remainingRequiredCells = 5))

        // Entering final checkpoint as the very last cell (remaining = 1) -> MUST NOT PRUNE
        assertFalse(SolverPruningRules.isPrematureFinalCheckpoint(finalPos, finalPos, remainingRequiredCells = 1))

        // Entering non-final cell -> MUST NOT PRUNE
        assertFalse(SolverPruningRules.isPrematureFinalCheckpoint(GridPosition(1, 1), finalPos, remainingRequiredCells = 5))
    }

    @Test
    fun rule2_checkpointSequence_pruningSoundness() {
        // Ordinary cell (null checkpoint) -> legal
        assertTrue(SolverPruningRules.isCheckpointSequenceValid(null, nextRequiredCheckpoint = 3))

        // Next checkpoint matches -> legal
        assertTrue(SolverPruningRules.isCheckpointSequenceValid(3, nextRequiredCheckpoint = 3))

        // Out-of-order checkpoint -> illegal (must prune)
        assertFalse(SolverPruningRules.isCheckpointSequenceValid(4, nextRequiredCheckpoint = 3))
        assertFalse(SolverPruningRules.isCheckpointSequenceValid(2, nextRequiredCheckpoint = 3))
    }

    @Test
    fun rule3_connectivityFloodFill_pruningSoundness() {
        val def = SamplePuzzleFixtures.puzzle3x3Unique
        val finalPos = GridPosition(2, 2)

        val state = SolverSearchState(3, 3, 9)
        // Mark all row 1 cells visited, cutting off row 2 from row 0
        state.visitedBitmap[3] = true // (1, 0)
        state.visitedBitmap[4] = true // (1, 1)
        state.visitedBitmap[5] = true // (1, 2)

        // From (0, 0), row 2 cannot be reached!
        val preserved = SolverPruningRules.isConnectivityPreserved(
            definition = def,
            candidate = GridPosition(0, 0),
            finalCheckpointPos = finalPos,
            remainingRequiredCells = 6, // 6 cells remain but only 3 accessible
            state = state
        )
        assertFalse("Disconnected remaining cells must trigger connectivity pruning", preserved)
    }

    @Test
    fun rule4_intermediateCellDegree_pruningSoundness() {
        val def = SamplePuzzleFixtures.puzzle3x3Unique
        val finalPos = GridPosition(2, 2)

        val state = SolverSearchState(3, 3, 9)
        // Isolate cell (0, 2): mark (0, 1) and (1, 2) visited
        state.visitedBitmap[1] = true // (0, 1)
        state.visitedBitmap[5] = true // (1, 2)

        // Cell (0, 2) now has degree 0 in the unvisited graph
        val hasDegrees = SolverPruningRules.hasSufficientDegrees(
            definition = def,
            candidate = GridPosition(0, 0),
            finalCheckpointPos = finalPos,
            remainingRequiredCells = 7,
            state = state
        )
        assertFalse("Isolated cell with degree < 2 must trigger degree pruning", hasDegrees)
    }

    @Test
    fun rule5_checkpointReachability_pruningSoundness() {
        val def = SamplePuzzleFixtures.puzzle3x3Unique
        val targetCpPos = GridPosition(0, 2) // Checkpoint #2

        val state = SolverSearchState(3, 3, 9)

        // Reachable under normal conditions
        val reachable = SolverPruningRules.isNextCheckpointReachable(
            definition = def,
            candidate = GridPosition(0, 0),
            nextRequiredCheckpoint = 2,
            targetCheckpointPos = targetCpPos,
            state = state
        )
        assertTrue("Reachable checkpoint must return true", reachable)

        // Block all paths to target
        state.visitedBitmap[1] = true // (0, 1)
        state.visitedBitmap[5] = true // (1, 2)

        val unreachable = SolverPruningRules.isNextCheckpointReachable(
            definition = def,
            candidate = GridPosition(0, 0),
            nextRequiredCheckpoint = 2,
            targetCheckpointPos = targetCpPos,
            state = state
        )
        assertFalse("Unreachable next checkpoint must trigger reachability pruning", unreachable)
    }

    // =========================================================================
    // 2. Correctness Comparison: Pruned Solver vs Unpruned Reference Solver
    // =========================================================================

    @Test
    fun comparePrunedVsUnpruned_onUniquePuzzle_producesIdenticalSolution() {
        val prunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle3x3Unique,
            SolverConfiguration.CHECK_UNIQUENESS
        )
        val unprunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle3x3Unique,
            SolverConfiguration.UNPRUNED_REFERENCE
        )

        // Both solvers must find exactly 1 solution
        assertEquals(1, prunedResult.solutionCount)
        assertEquals(1, unprunedResult.solutionCount)
        assertEquals(prunedResult.solutions.first(), unprunedResult.solutions.first())

        // Both must be valid
        val validation = CompletionValidator.validate(SamplePuzzleFixtures.puzzle3x3Unique, prunedResult.solutions.first())
        assertTrue(validation is CompletionCheckResult.Success)

        // Pruned solver must explore <= nodes than unpruned solver
        assertTrue(
            "Pruned nodes (${prunedResult.statistics.nodesExplored}) should be <= unpruned (${unprunedResult.statistics.nodesExplored})",
            prunedResult.statistics.nodesExplored <= unprunedResult.statistics.nodesExplored
        )
    }

    @Test
    fun comparePrunedVsUnpruned_onMultipleSolutionPuzzle_producesSameSolutionCount() {
        val prunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle3x3MultipleSolutions,
            SolverConfiguration.CHECK_UNIQUENESS
        )
        val unprunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle3x3MultipleSolutions,
            SolverConfiguration.UNPRUNED_REFERENCE
        )

        assertEquals(2, prunedResult.solutionCount)
        assertEquals(2, unprunedResult.solutionCount)
        assertEquals(SolverStatus.MULTIPLE_SOLUTIONS, prunedResult.status)
        assertEquals(SolverStatus.MULTIPLE_SOLUTIONS, unprunedResult.status)
    }

    @Test
    fun comparePrunedVsUnpruned_onUnsolvablePuzzle_bothAgreeOnUnsolvable() {
        val prunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle4x4UnsolvableParity,
            SolverConfiguration.CHECK_UNIQUENESS
        )
        val unprunedResult = solver.solve(
            SamplePuzzleFixtures.puzzle4x4UnsolvableParity,
            SolverConfiguration.UNPRUNED_REFERENCE
        )

        assertEquals(SolverStatus.UNSOLVABLE, prunedResult.status)
        assertEquals(SolverStatus.UNSOLVABLE, unprunedResult.status)
        assertEquals(0, prunedResult.solutionCount)
        assertEquals(0, unprunedResult.solutionCount)

        // Pruning must have pruned branches on the unsolvable board
        assertTrue(prunedResult.statistics.prunedBranches > 0)
    }
}
