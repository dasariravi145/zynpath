package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative unit tests for [PuzzleHintEngine].
 *
 * Implements Prompt 14:
 * - Section 34: 8 Mandatory Next-Move Tests.
 * - Section 35: Multiple-Solution Current-Path Compatibility.
 * - Section 36: Dead-End Detection & Recovery Guidance.
 * - Section 24: Competitive Fairness.
 * - Section 21: Verified Solution Caching.
 */
class PuzzleHintEngineTest {

    private lateinit var hintEngine: PuzzleHintEngine
    private lateinit var definition4x4: PuzzleDefinition

    @Before
    fun setUp() {
        hintEngine = PuzzleHintEngine()
        definition4x4 = CuratedDifficultyFixtures.beginner4x4
    }

    // =========================================================================
    // Prompt 14 Section 34: Next-Move Unit Tests
    // =========================================================================

    @Test
    fun `1 Initial valid state returns a legal next move`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Expected NextMove, got $result", result is HintResult.NextMove)

        val nextMove = (result as HintResult.NextMove).nextMove
        assertEquals(GridPosition(0, 1), nextMove)
        assertTrue(definition4x4.gridDimensions.contains(nextMove))
    }

    @Test
    fun `2 Mid-path state returns a compatible next move`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)), 200L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)), 300L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3)), 400L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Expected NextMove, got $result", result is HintResult.NextMove)

        val nextMove = (result as HintResult.NextMove).nextMove
        assertEquals(GridPosition(1, 2), nextMove)
    }

    @Test
    fun `3 Hinted move is orthogonally adjacent`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)

        val currentHead = engine.currentState.currentEndpoint!!
        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.NextMove
        assertTrue(
            "Hinted move ${result.nextMove} must be orthogonally adjacent to $currentHead",
            currentHead.isOrthogonalNeighbor(result.nextMove)
        )
    }

    @Test
    fun `4 Hinted move does not cross a wall`() {
        val defWithWall = PuzzleDefinition.standard(
            puzzleId = "fixture_walls_test",
            dimensions = GridDimensions(4, 4),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 0))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0))
            )
        )

        val engine = PuzzleEngine(defWithWall)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val request = HintRequest(
            puzzleId = defWithWall.puzzleId,
            puzzleVersion = defWithWall.puzzleVersion,
            definition = defWithWall,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.NextMove
        // Wall blocks (0,0) to (1,0); hint must recommend (0,1)
        assertEquals(GridPosition(0, 1), result.nextMove)
        assertFalse(
            "Hint must never cross wall",
            defWithWall.graph.isBlocked(GridPosition(0, 0), result.nextMove)
        )
    }

    @Test
    fun `5 Hinted move does not revisit a cell`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)), 200L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.NextMove
        assertFalse(
            "Hinted cell ${result.nextMove} must not be already visited",
            engine.currentState.currentPath.contains(result.nextMove)
        )
    }

    @Test
    fun `6 Hinted move respects checkpoint order`() {
        // In beginner4x4, checkpoint #2 is at (1,3). The path must reach checkpoint #2 before checkpoint #3 (2,0).
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)), 200L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)), 300L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.NextMove
        // Next move into (1,3) visits checkpoint #2, strictly in ascending sequence
        assertEquals(GridPosition(1, 3), result.nextMove)
        assertEquals(2, definition4x4.getCheckpointAt(result.nextMove))
    }

    @Test
    fun `7 Hinted move belongs to a complete solution`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.NextMove
        val fullSolution = result.fullSolution

        // CompletionValidator proof
        val completion = CompletionValidator.validate(definition4x4, fullSolution)
        assertTrue("Full continuation must be completely valid", completion.isSuccess)
        assertEquals(definition4x4.totalRequiredCells, fullSolution.length)
        assertEquals(result.nextMove, fullSolution.positions[2])
    }

    @Test
    fun `8 Hint does not modify the gameplay state`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)

        val pathBefore = engine.currentState.currentPath.positions
        val movesBefore = engine.currentState.moveCount
        val coveredBefore = engine.currentState.coveredCellCount

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        hintEngine.computeHint(request)

        assertEquals(pathBefore, engine.currentState.currentPath.positions)
        assertEquals(movesBefore, engine.currentState.moveCount)
        assertEquals(coveredBefore, engine.currentState.coveredCellCount)
    }

    // =========================================================================
    // Prompt 14 Section 35: Multiple Solutions Compatibility
    // =========================================================================

    @Test
    fun `test multiple solutions compatibility with player partial prefix`() {
        val multiDef = CuratedDifficultyFixtures.multiSolution4x4
        val solver = PuzzleSolver()
        val allSolutions = solver.countSolutions(multiDef, limit = 5).solutions
        assertTrue("Fixture must have at least 2 distinct solutions", allSolutions.size >= 2)

        val solA = allSolutions[0]
        val solB = allSolutions[1]

        // Find the first index where solution A and B diverge
        var divergeIndex = -1
        for (i in 0 until minOf(solA.length, solB.length)) {
            if (solA.positions[i] != solB.positions[i]) {
                divergeIndex = i
                break
            }
        }
        assertTrue("Solutions must diverge", divergeIndex > 0)

        // Construct player path along Solution B at the divergence point
        val engine = PuzzleEngine(multiDef)
        engine.process(PuzzleAction.StartPath(solB.positions[0]), 0L)
        for (i in 1..divergeIndex) {
            engine.process(PuzzleAction.ExtendPath(solB.positions[i]), i * 100L)
        }

        val request = HintRequest(
            puzzleId = multiDef.puzzleId,
            puzzleVersion = multiDef.puzzleVersion,
            definition = multiDef,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Expected NextMove, got $result", result is HintResult.NextMove)

        val hintNext = (result as HintResult.NextMove).nextMove
        val expectedNextFromB = solB.positions[divergeIndex + 1]

        // Hint MUST extend along a valid completion compatible with solution B, NOT force solution A
        assertEquals(
            "Hint must follow a valid continuation of player's actual path",
            expectedNextFromB,
            hintNext
        )
    }

    // =========================================================================
    // Prompt 14 Section 36: Dead-End Detection & Recovery Guidance
    // =========================================================================

    @Test
    fun `test dead end detection proves recovery required`() {
        // Construct a dead-end partial path on 4x4
        // Path (0,0) -> (1,0) -> (2,0) -> (3,0) (visits checkpoint 4 prematurely, leaving remainder unvisited)
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)), 100L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(2, 0)), 200L)

        // Even without reaching final checkpoint, going down (0,0)->(1,0)->(2,0)->(2,1)->(2,2)->(2,3)->(1,3)
        // traps the board so cells (0,1),(0,2),(0,3) cannot be visited and completed.
        val deadEndDef = PuzzleDefinition.standard(
            puzzleId = "fixture_dead_end_test",
            dimensions = GridDimensions(3, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(2, 2))
            )
        )

        val deadEngine = PuzzleEngine(deadEndDef)
        deadEngine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)), 200L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)), 300L)
        // Now step into (2,2) which is final checkpoint prematurely (covered only 5 of 9 cells)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 1)), 400L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)), 500L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)), 600L)
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 0)), 700L)
        // Remaining unvisited cell is (2,2), which is adjacent!
        deadEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 2)), 800L)

        // All 9 cells covered! Wait, that's a solution.
        // Let's create an actual trapped dead end:
        // (0,0) -> (0,1) -> (1,1) -> (1,0) (now (2,0) has no escape to (0,2), etc.)
        val trappedEngine = PuzzleEngine(deadEndDef)
        trappedEngine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)), 200L)
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)), 300L)
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 0)), 400L)
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 1)), 500L)
        // At (2,1), only unvisited neighbors are (2,2). Stepping into (2,2) leaves (0,2) and (1,2) trapped!
        trappedEngine.process(PuzzleAction.ExtendPath(GridPosition(2, 2)), 600L)

        val request = HintRequest(
            puzzleId = deadEndDef.puzzleId,
            puzzleVersion = deadEndDef.puzzleVersion,
            definition = deadEndDef,
            gameState = trappedEngine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Expected RecoveryRequired for dead end, got $result", result is HintResult.RecoveryRequired)

        val recovery = result as HintResult.RecoveryRequired
        assertTrue("Recommended retract steps must be >= 1", recovery.stepsToRetract >= 1)
        assertNotNull(recovery.recommendedRollbackPosition)
    }

    @Test
    fun `test recovery rollback recommendations identify a proven continuation prefix`() {
        val deadEndDef = PuzzleDefinition.standard(
            puzzleId = "fixture_recovery_proof",
            dimensions = GridDimensions(3, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(2, 2))
            )
        )

        val engine = PuzzleEngine(deadEndDef)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)), 100L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)), 200L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)), 300L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(2, 0)), 400L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(2, 1)), 500L)
        engine.process(PuzzleAction.ExtendPath(GridPosition(2, 2)), 600L) // Trapped premature final checkpoint

        val request = HintRequest(
            puzzleId = deadEndDef.puzzleId,
            puzzleVersion = deadEndDef.puzzleVersion,
            definition = deadEndDef,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request) as HintResult.RecoveryRequired

        // Validate that rolling back to the recommended position produces a solvable prefix
        val solver = PuzzleSolver()
        val rollbackPrefix = PuzzlePath(result.verifiedPrefix)
        val continuationResult = solver.solveFromPartialPath(deadEndDef, rollbackPrefix)
        assertTrue("Recommended prefix must have a proven complete solution", continuationResult.isSolved)
    }

    // =========================================================================
    // Prompt 14 Section 24: Competitive Mode Fairness
    // =========================================================================

    @Test
    fun `test competitive modes strictly reject hints`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val competitiveModes = listOf(
            GameMode.QUICK_DUEL,
            GameMode.FRIEND_DUEL,
            GameMode.MINI_LEAGUE
        )

        for (mode in competitiveModes) {
            val request = HintRequest(
                puzzleId = definition4x4.puzzleId,
                puzzleVersion = definition4x4.puzzleVersion,
                definition = definition4x4,
                gameState = engine.currentState,
                gameMode = mode
            )

            val result = hintEngine.computeHint(request)
            assertTrue(
                "Mode $mode must reject hint with HintNotAvailable, got $result",
                result is HintResult.HintNotAvailable
            )
        }
    }

    // =========================================================================
    // Edge Cases, Invalidation, and Caching
    // =========================================================================

    @Test
    fun `test already completed puzzle returns AlreadyCompleted`() {
        val fullRoute = CuratedDifficultyFixtures.beginner4x4Solution
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(fullRoute.positions[0]), 0L)
        for (i in 1 until fullRoute.length) {
            engine.process(PuzzleAction.ExtendPath(fullRoute.positions[i]), i * 10L)
        }
        assertTrue(engine.currentState.isCompleted)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Completed game must return AlreadyCompleted, got $result", result is HintResult.AlreadyCompleted)
    }

    @Test
    fun `test invalid definition or state mismatch returns InvalidState`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val request = HintRequest(
            puzzleId = "wrong_id",
            puzzleVersion = 999,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Mismatch must return InvalidState, got $result", result is HintResult.InvalidState)
    }

    @Test
    fun `test cooperative cancellation aborts search`() {
        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val request = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState,
            configuration = HintConfiguration(
                cancellationSignal = { true }
            )
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Cancelled signal must return Cancelled, got $result", result is HintResult.Cancelled)
    }

    @Test
    fun `test verified solution caching reuses solution on prefix match`() {
        val cache = HintCache()
        val engineWithCache = PuzzleHintEngine(cache = cache)

        val engine = PuzzleEngine(definition4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)), 0L)

        val request1 = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result1 = engineWithCache.computeHint(request1) as HintResult.NextMove
        assertFalse("First query is computed, not cached", result1.isCached)

        // Advance engine along recommended step
        engine.process(PuzzleAction.ExtendPath(result1.nextMove), 100L)

        val request2 = HintRequest(
            puzzleId = definition4x4.puzzleId,
            puzzleVersion = definition4x4.puzzleVersion,
            definition = definition4x4,
            gameState = engine.currentState
        )

        val result2 = engineWithCache.computeHint(request2) as HintResult.NextMove
        assertTrue("Second query along same path must hit cache", result2.isCached)
    }
}
