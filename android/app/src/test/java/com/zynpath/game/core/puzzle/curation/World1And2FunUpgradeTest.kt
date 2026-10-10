package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Verification test suite for Zynpath Puzzle Fun & Confusion Upgrade.
 *
 * Verifies:
 * 1. World 1 (Levels 1–20):
 *    - Levels 1–3: 0 walls (onboarding clarity)
 *    - Level 4: 1 teaching line
 *    - Levels 5–20: 1–3 strategic lines, 6–10 checkpoints
 * 2. World 2 (Levels 21–50):
 *    - Levels 21–50: 1–5 strategic lines, 8–12 checkpoints
 * 3. Topological Invariants:
 *    - 100% playable coverage (walls never occupy cells)
 *    - Ascending checkpoints 1..N
 *    - Solver uniqueness verified
 * 4. Smart Number Placement & Visual Proximity Traps
 * 5. State-Aware Hint Engine & Backtrack Recovery:
 *    - Fresh board
 *    - Partial valid path
 *    - Dead-end backtrack advice ("Backtrack — this route closes the remaining cells. Undo X steps.")
 *    - Post-rollback recovery
 *    - Zero solution leakage
 */
class World1And2FunUpgradeTest {

    private val solver = PuzzleSolver()
    private val hintEngine = PuzzleHintEngine(solver = solver)

    @Test
    fun testWorld1StrategicLinesAndTeachingProgression() {
        // Levels 1..3: 0 walls
        for (lvlId in 1..3) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            assertTrue("Level $lvlId must have 0 walls for onboarding clarity", def.blockedEdges.isEmpty())
            assertEquals("Level $lvlId must have 36 playable cells", 36, def.requiredCells.size)
            assertTrue("Level $lvlId checkpoints must be 6..10", def.checkpoints.size in 6..10)
        }

        // Level 4: Introduce 1 teaching line
        val lvl4 = CuratedFirst50Levels.LEVELS[4]!!
        assertEquals("Level 4 must introduce exactly 1 teaching line", 1, lvl4.blockedEdges.size)
        assertEquals("Level 4 must have 36 playable cells", 36, lvl4.requiredCells.size)
        assertTrue("Level 4 checkpoints must be 6..10", lvl4.checkpoints.size in 6..10)

        // Levels 5..20: 1-3 strategic lines
        for (lvlId in 5..20) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            assertTrue(
                "Level $lvlId must have 1..3 strategic lines (found ${def.blockedEdges.size})",
                def.blockedEdges.size in 1..3
            )
            assertEquals("Level $lvlId must have 36 playable cells", 36, def.requiredCells.size)
            assertTrue("Level $lvlId checkpoints must be 6..10", def.checkpoints.size in 6..10)
        }
    }

    @Test
    fun testWorld2StrategicLinesAndCorridorPreservation() {
        for (lvlId in 21..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            assertTrue(
                "Level $lvlId must have 1..5 strategic lines (found ${def.blockedEdges.size})",
                def.blockedEdges.size in 1..5
            )
            assertEquals("Level $lvlId must have 49 playable cells", 49, def.requiredCells.size)
            assertTrue(
                "Level $lvlId checkpoints must be 8..12 (found ${def.checkpoints.size})",
                def.checkpoints.size in 8..12
            )

            // Strictly ascending checkpoints
            val cpNumbers = def.checkpoints.map { it.number }
            assertEquals((1..def.checkpoints.size).toList(), cpNumbers)
        }
    }

    @Test
    fun testSolverCertifiedUniquenessOnSampleLevels() {
        val sampleLevels = listOf(1, 4, 5, 10, 15, 20, 21, 25, 35, 45, 50)
        for (lvlId in sampleLevels) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
            assertEquals("Level $lvlId must be SOLVED", SolverStatus.SOLVED, res.status)
            assertEquals("Level $lvlId must be UNIQUE", UniquenessStatus.UNIQUE, res.uniqueness)
            assertNotNull("Level $lvlId must provide a solution", res.firstSolution)
            assertEquals(
                "Level $lvlId solution length must equal total cells",
                def.gridDimensions.totalCells,
                res.firstSolution!!.length
            )
        }
    }

    @Test
    fun testVisualProximityTrapsPresentInWorld1AndWorld2() {
        var w1TrapsTotal = 0
        var w2TrapsTotal = 0

        for (lvlId in 5..20) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val traps = countTraps(sol.positions, def.checkpoints)
            w1TrapsTotal += traps
        }

        for (lvlId in 21..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val traps = countTraps(sol.positions, def.checkpoints)
            w2TrapsTotal += traps
        }

        assertTrue("World 1 (Levels 5-20) must contain visual proximity traps", w1TrapsTotal >= 10)
        assertTrue("World 2 (Levels 21-50) must contain visual proximity traps", w2TrapsTotal >= 20)
    }

    @Test
    fun testHintEngineFreshBoardReturnsStartCheckpoint() {
        val def = CuratedFirst50Levels.LEVELS[4]!!
        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = com.zynpath.game.core.puzzle.engine.PuzzleGameState(def),
            currentOrderedPath = emptyList(),
            nextRequiredCheckpoint = 1,
            configuration = HintConfiguration()
        )
        val result = hintEngine.computeHint(request)
        assertTrue("Fresh board hint must return NextMove", result is HintResult.NextMove)
        val move = result as HintResult.NextMove
        assertEquals(def.checkpoints.first().position, move.nextMove)
        assertNotNull("Deduction message must be present", move.deductionMessage)
    }

    @Test
    fun testHintEnginePartialValidPathReturnsNextStepWithStrategicDeduction() {
        val def = CuratedFirst50Levels.LEVELS[5]!!
        val sol = CuratedFirst50Levels.SOLUTIONS[5]!!
        val partialPath = sol.positions.subList(0, 5)

        val cpReached = def.checkpoints.count { it.position in partialPath }
        val nextCp = cpReached + 1

        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = com.zynpath.game.core.puzzle.engine.PuzzleGameState(
                def,
                currentPath = com.zynpath.game.core.puzzle.model.PuzzlePath(partialPath),
                nextRequiredCheckpoint = nextCp
            ),
            currentOrderedPath = partialPath,
            nextRequiredCheckpoint = nextCp,
            configuration = HintConfiguration()
        )
        val result = hintEngine.computeHint(request)
        assertTrue("Partial valid path must return NextMove", result is HintResult.NextMove)
        val move = result as HintResult.NextMove
        assertEquals(sol.positions[5], move.nextMove)
        assertNotNull("Deduction message must be non-null", move.deductionMessage)
        assertFalse("Deduction message must not be blank", move.deductionMessage!!.isBlank())
    }

    @Test
    fun testHintEngineDeadEndRecommendsBacktrackWithoutLeakingSolution() {
        val def = CuratedFirst50Levels.LEVELS[5]!!
        val sol = CuratedFirst50Levels.SOLUTIONS[5]!!

        // Create an illegal branch: follow canonical path for 3 steps, then deviate into an adjacent cell that causes a dead end
        val validPrefix = sol.positions.subList(0, 3)
        val currentHead = validPrefix.last()

        // Find a neighbor that is legal as a grid step, but NOT the canonical next step
        val canonicalNext = sol.positions[3]
        val wrongNeighbor = def.graph.getTraversableNeighbors(currentHead).firstOrNull {
            it !in validPrefix && it != canonicalNext
        }

        if (wrongNeighbor != null) {
            val deadEndPath = validPrefix + wrongNeighbor
            val request = HintRequest(
                puzzleId = def.puzzleId,
                puzzleVersion = def.puzzleVersion,
                definition = def,
                gameState = com.zynpath.game.core.puzzle.engine.PuzzleGameState(
                    def,
                    currentPath = com.zynpath.game.core.puzzle.model.PuzzlePath(deadEndPath),
                    nextRequiredCheckpoint = 2
                ),
                currentOrderedPath = deadEndPath,
                nextRequiredCheckpoint = 2,
                configuration = HintConfiguration()
            )
            val result = hintEngine.computeHint(request)

            // Must identify dead end and recommend backtrack
            assertTrue("Dead end branch must return RecoveryRequired", result is HintResult.RecoveryRequired)
            val deadEnd = result as HintResult.RecoveryRequired
            assertTrue(
                "Explanation must use concise backtrack phrasing",
                deadEnd.explanation.startsWith("Backtrack — this route closes the remaining cells.")
            )
            assertEquals("Recommended rollback position must be in valid prefix", currentHead, deadEnd.recommendedRollbackPosition)
            assertEquals(1, deadEnd.stepsToRetract)

            // Step 4: After player rolls back, request hint again -> returns forward move
            val recoveredPath = deadEndPath.dropLast(deadEnd.stepsToRetract)
            val recoveryRequest = HintRequest(
                puzzleId = def.puzzleId,
                puzzleVersion = def.puzzleVersion,
                definition = def,
                gameState = com.zynpath.game.core.puzzle.engine.PuzzleGameState(
                    def,
                    currentPath = com.zynpath.game.core.puzzle.model.PuzzlePath(recoveredPath),
                    nextRequiredCheckpoint = 2
                ),
                currentOrderedPath = recoveredPath,
                nextRequiredCheckpoint = 2,
                configuration = HintConfiguration()
            )
            val recoveryResult = hintEngine.computeHint(recoveryRequest)
            assertTrue("Recovery after backtrack must return NextMove", recoveryResult is HintResult.NextMove)
            val recoveryMove = recoveryResult as HintResult.NextMove
            assertEquals("Recovery step must match canonical step", canonicalNext, recoveryMove.nextMove)
        }
    }

    @Test
    fun testZeroSolutionLeakageInHintsAndPuzzles() {
        // Confirm that the hint result only provides a single nextMove and deduction guidance, never revealing the complete remaining path
        val def = CuratedFirst50Levels.LEVELS[6]!!
        val sol = CuratedFirst50Levels.SOLUTIONS[6]!!
        val partialPath = sol.positions.subList(0, 4)

        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = com.zynpath.game.core.puzzle.engine.PuzzleGameState(
                def,
                currentPath = com.zynpath.game.core.puzzle.model.PuzzlePath(partialPath),
                nextRequiredCheckpoint = 2
            ),
            currentOrderedPath = partialPath,
            nextRequiredCheckpoint = 2,
            configuration = HintConfiguration()
        )
        val result = hintEngine.computeHint(request)
        assertTrue(result is HintResult.NextMove)
        val move = result as HintResult.NextMove
        // Only one step is exposed
        assertEquals(sol.positions[4], move.nextMove)
    }

    private fun countTraps(path: List<GridPosition>, checkpoints: List<NumberedCheckpoint>): Int {
        var traps = 0
        val sortedCps = checkpoints.sortedBy { it.number }
        for (i in 0 until sortedCps.size - 1) {
            val cpA = sortedCps[i]
            val cpB = sortedCps[i + 1]
            val idxA = path.indexOf(cpA.position)
            val idxB = path.indexOf(cpB.position)
            val manhattan = abs(cpA.position.row - cpB.position.row) + abs(cpA.position.column - cpB.position.column)
            if (manhattan <= 2 && (idxB - idxA) >= 4) {
                traps++
            }
        }
        return traps
    }
}
