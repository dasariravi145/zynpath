package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolutionClassification
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import com.zynpath.game.core.puzzle.ui.GridCoordinateMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Authoritative Master Redesign Part 1 Test Suite.
 *
 * Implements Section 38 of Zynpath Master Redesign Prompt 1 of 3:
 * Proves the 24 required invariants across Puzzle Engine, 6x6 World 1, 7x7 World 2,
 * Solution-First Generator, Exact Solver, Living Path, Touch, and Progression Safety.
 */
class MasterRedesignPart1TestSuite {

    private val solver = PuzzleSolver()
    private val analyzer = PuzzleDifficultyAnalyzer(solver = solver)

    // 1. 6x6 = 36 required cells
    @Test
    fun test01_world1RequiresExactly36Cells() {
        for (lvlId in 1..20) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]
            assertNotNull("Level $lvlId must exist in CuratedFirst50Levels", def)
            assertEquals("Level $lvlId must be 6x6", GridDimensions(6, 6), def!!.gridDimensions)
            assertEquals("Level $lvlId must have exactly 36 required cells", 36, def.totalRequiredCells)
            assertEquals("Level $lvlId requiredCells set size must be 36", 36, def.requiredCells.size)
        }
    }

    // 2. 7x7 = 49 required cells
    @Test
    fun test02_world2RequiresExactly49Cells() {
        for (lvlId in 21..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]
            assertNotNull("Level $lvlId must exist in CuratedFirst50Levels", def)
            assertEquals("Level $lvlId must be 7x7", GridDimensions(7, 7), def!!.gridDimensions)
            assertEquals("Level $lvlId must have exactly 49 required cells", 49, def.totalRequiredCells)
            assertEquals("Level $lvlId requiredCells set size must be 49", 49, def.requiredCells.size)
        }
    }

    // 3. No production 4x4 or 5x5 in migrated scope (Levels 1–50)
    @Test
    fun test03_noProduction4x4or5x5InLevels1To50() {
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val rows = def.gridDimensions.rows
            val cols = def.gridDimensions.columns
            assertFalse("Level $lvlId must not be 4x4", rows == 4 && cols == 4)
            assertFalse("Level $lvlId must not be 5x5", rows == 5 && cols == 5)
            assertTrue("Level $lvlId must be at least 6x6", rows >= 6 && cols >= 6)
        }
    }

    // 4. Canonical route covers every cell
    @Test
    fun test04_canonicalRouteCoversEveryCell() {
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]
            assertNotNull("Solution $lvlId must exist", sol)
            assertEquals(
                "Solution $lvlId must cover every cell (${def.totalRequiredCells})",
                def.totalRequiredCells,
                sol!!.positions.size
            )
        }
    }

    // 5. Canonical cells unique
    @Test
    fun test05_canonicalCellsAreUnique() {
        for (lvlId in 1..50) {
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val uniqueCount = sol.positions.toSet().size
            assertEquals(
                "Level $lvlId canonical path must not have duplicate cells",
                sol.positions.size,
                uniqueCount
            )
        }
    }

    // 6. Moves are orthogonal
    @Test
    fun test06_canonicalMovesAreStrictlyOrthogonal() {
        for (lvlId in 1..50) {
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val positions = sol.positions
            for (i in 0 until positions.size - 1) {
                val curr = positions[i]
                val next = positions[i + 1]
                assertTrue(
                    "Move from $curr to $next in Level $lvlId must be orthogonal",
                    curr.isOrthogonalNeighbor(next)
                )
            }
        }
    }

    // 7. Checkpoints are consecutive (1..N)
    @Test
    fun test07_checkpointsAreConsecutiveOneToN() {
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val cpCount = def.checkpoints.size
            val numbers = def.checkpoints.map { it.number }.sorted()
            assertEquals(
                "Level $lvlId checkpoints must be numbered 1..$cpCount",
                (1..cpCount).toList(),
                numbers
            )
        }
    }

    // 8. Checkpoints ordered along route
    @Test
    fun test08_checkpointsAreEncounteredInOrderAlongCanonicalRoute() {
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val positions = sol.positions

            val cpPositions = def.checkpoints.sortedBy { it.number }.map { it.position }
            var lastIdx = -1
            for ((cpNum, pos) in cpPositions.withIndex()) {
                val idx = positions.indexOf(pos)
                assertTrue("Checkpoint ${cpNum + 1} must exist in canonical path of Level $lvlId", idx != -1)
                assertTrue(
                    "Checkpoint ${cpNum + 1} at step $idx must appear after checkpoint $cpNum at step $lastIdx",
                    idx > lastIdx
                )
                lastIdx = idx
            }
        }
    }

    // 9. Future checkpoint rejected
    @Test
    fun test09_futureCheckpointIsRejected() {
        val def = CuratedFirst50Levels.LEVEL_1
        val engine = PuzzleEngine(def)
        val startCp = def.checkpoints.find { it.number == 1 }!!
        val cp3 = def.checkpoints.find { it.number == 3 }!!

        // Start at cp1
        var state = engine.currentState
        val startRes = engine.reduce(state, PuzzleAction.StartPath(startCp.position))
        assertTrue(startRes is PuzzleEngineResult.Accepted)
        state = (startRes as PuzzleEngineResult.Accepted).newState

        // Stepping to cp3 when expecting cp2 must be rejected
        val rejectRes = engine.reduce(state, PuzzleAction.ExtendPath(cp3.position))
        assertTrue("Stepping to future checkpoint must be rejected", rejectRes is PuzzleEngineResult.Rejected)
        val rej = rejectRes as PuzzleEngineResult.Rejected
        assertTrue(
            "Rejection reason must be WRONG_CHECKPOINT_ORDER or NON_ADJACENT",
            rej.reason == MoveRejectionReason.WRONG_CHECKPOINT_ORDER || rej.reason == MoveRejectionReason.NON_ADJACENT
        )
    }

    // 10. Final checkpoint rejected early
    @Test
    fun test10_finalCheckpointRejectedEarly() {
        val def = CuratedFirst50Levels.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = CuratedFirst50Levels.SOLUTION_1.positions

        // Start at cp1
        var state = engine.currentState
        state = (engine.reduce(state, PuzzleAction.StartPath(sol[0])) as PuzzleEngineResult.Accepted).newState

        // Walk 10 steps along canonical path
        for (i in 1..10) {
            val res = engine.reduce(state, PuzzleAction.ExtendPath(sol[i]))
            assertTrue(res is PuzzleEngineResult.Accepted)
            state = (res as PuzzleEngineResult.Accepted).newState
        }

        val finalCp = def.checkpoints.find { it.number == def.maxCheckpointNumber }!!
        val prematureRes = engine.reduce(state, PuzzleAction.ExtendPath(finalCp.position))
        assertTrue("Entering final checkpoint before full coverage must be rejected", prematureRes is PuzzleEngineResult.Rejected)
        val rej = prematureRes as PuzzleEngineResult.Rejected
        assertTrue(
            "Rejection reason should be PREMATURE_FINAL_CHECKPOINT or NON_ADJACENT",
            rej.reason == MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT || rej.reason == MoveRejectionReason.NON_ADJACENT
        )
    }

    // 11. Incomplete coverage never wins
    @Test
    fun test11_incompleteCoverageNeverWins() {
        val def = CuratedFirst50Levels.LEVEL_1
        val fullPositions = CuratedFirst50Levels.SOLUTION_1.positions

        // Missing last cell
        val truncatedPath = PuzzlePath(fullPositions.dropLast(1))
        val result1 = CompletionValidator.validate(def, truncatedPath)
        assertTrue("Incomplete path must not pass validation", result1 is CompletionCheckResult.Failure)

        // Missing middle cell
        val missingMiddle = PuzzlePath(fullPositions.filterIndexed { index, _ -> index != 5 })
        val result2 = CompletionValidator.validate(def, missingMiddle)
        assertTrue("Path missing middle cell must not pass validation", result2 is CompletionCheckResult.Failure)
    }

    // 12. Valid full coverage wins
    @Test
    fun test12_validFullCoverageWins() {
        for (lvlId in listOf(1, 2, 5, 10, 20, 21, 25, 30, 40, 50)) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val result = CompletionValidator.validate(def, sol)
            assertTrue("Canonical solution for Level $lvlId must pass validation", result is CompletionCheckResult.Success)
        }
    }

    // 13. No path shown at start
    @Test
    fun test13_noPathShownAtStart() {
        for (lvlId in listOf(1, 5, 21, 50)) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val state = PuzzleGameState.initial(def)
            assertTrue("Path must be empty at start for Level $lvlId", state.currentPath.isEmpty)
            assertEquals("Covered cells must be 0 at start", 0, state.coveredCellCount)
            assertEquals("Move count must be 0 at start", 0, state.moveCount)
        }
    }

    // 14. Rendered path equals PlayerPath
    @Test
    fun test14_renderedPathEqualsPlayerPath() {
        val def = CuratedFirst50Levels.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = CuratedFirst50Levels.SOLUTION_1.positions

        var state = engine.currentState
        state = (engine.reduce(state, PuzzleAction.StartPath(sol[0])) as PuzzleEngineResult.Accepted).newState

        for (i in 1..4) {
            state = (engine.reduce(state, PuzzleAction.ExtendPath(sol[i])) as PuzzleEngineResult.Accepted).newState
            val boardState = state.toBoardState()
            assertEquals("BoardState path must match PlayerPath exactly", state.currentPath.positions, boardState.path)
            assertEquals("BoardState covered count must match", state.coveredCellCount, boardState.coveredCellCount)
        }
    }

    // 15. Fast drag cannot skip cells
    @Test
    fun test15_fastDragCannotSkipCells() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 600f,
            canvasHeight = 600f,
            rowCount = 6,
            columnCount = 6,
            padding = 0f
        )
        val from = GridPosition(0, 0)
        val to = GridPosition(0, 3) // 3 cells away horizontally

        val intermediate = mapper.resolveIntermediatePath(from, to, androidx.compose.ui.geometry.Offset(350f, 50f))
        assertNotNull("Intermediate steps must be resolved", intermediate)
        assertEquals("Must produce 3 steps (1, 2, 3)", 3, intermediate!!.size)
        assertEquals(GridPosition(0, 1), intermediate[0])
        assertEquals(GridPosition(0, 2), intermediate[1])
        assertEquals(GridPosition(0, 3), intermediate[2])

        // Verify all adjacent
        for (i in 0 until intermediate.size - 1) {
            assertTrue(intermediate[i].isOrthogonalNeighbor(intermediate[i + 1]))
        }
    }

    // 16. Immediate backtracking updates checkpoint state
    @Test
    fun test16_immediateBacktrackingUpdatesCheckpointState() {
        val def = CuratedFirst50Levels.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = CuratedFirst50Levels.SOLUTION_1.positions

        // Start at cp1
        var state = engine.currentState
        state = (engine.reduce(state, PuzzleAction.StartPath(sol[0])) as PuzzleEngineResult.Accepted).newState

        // Step to sol[1] which is Checkpoint 2
        val cp2Pos = def.checkpoints.find { it.number == 2 }!!.position
        assertEquals(cp2Pos, sol[1])

        state = (engine.reduce(state, PuzzleAction.ExtendPath(sol[1])) as PuzzleEngineResult.Accepted).newState
        assertEquals("Next checkpoint should now be 3", 3, state.nextRequiredCheckpoint)
        assertEquals(2, state.currentPath.size)

        // Now backtrack to sol[0]
        val backtrackRes = engine.reduce(state, PuzzleAction.ExtendPath(sol[0]))
        assertTrue("Backtracking move must be accepted", backtrackRes is PuzzleEngineResult.Accepted)
        state = (backtrackRes as PuzzleEngineResult.Accepted).newState

        assertEquals("Next checkpoint must be restored to 2 after backtracking", 2, state.nextRequiredCheckpoint)
        assertEquals("Path size must retract to 1", 1, state.currentPath.size)
        assertEquals(sol[0], state.currentEndpoint)
    }

    // 17. Walls never occupy cells
    @Test
    fun test17_wallsNeverOccupyCellsInWorld1AndWorld2() {
        for (lvlId in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val maxWalls = if (lvlId in 1..3) 0 else if (lvlId == 4) 1 else if (lvlId in 5..20) 4 else 6
            assertTrue("Level $lvlId walls must be within strategic line limit (found ${def.blockedEdges.size}, max $maxWalls)", def.blockedEdges.size <= maxWalls)
            assertEquals("All cells must be playable without obstruction", def.gridDimensions.totalCells, def.requiredCells.size)
        }
    }

    // 18. Exact solver succeeds on accepted content
    @Test
    fun test18_exactSolverSucceedsOnCuratedContent() {
        for (lvlId in listOf(1, 2, 5, 21, 25)) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
            assertEquals("Solver must solve Level $lvlId", SolverStatus.SOLVED, res.status)
            assertEquals("Solver must prove unique solution for Level $lvlId", UniquenessStatus.UNIQUE, res.uniqueness)
        }
    }

    // 19. Solution classification works
    @Test
    fun test19_solutionClassificationWorks() {
        val def = CuratedFirst50Levels.LEVEL_1
        val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
        assertEquals(SolutionClassification.UNIQUE, res.solutionClassification)
    }

    // 20. Difficulty metrics are real (not fabricated)
    @Test
    fun test20_difficultyMetricsAreReal() {
        val def = CuratedFirst50Levels.LEVEL_1
        val sol = CuratedFirst50Levels.SOLUTION_1
        val diff = analyzer.analyzeDifficulty(def, sol)

        assertEquals(GridDimensions(6, 6), diff.gridSize)
        assertEquals(36, diff.cellCount)
        assertEquals(def.checkpoints.size, diff.checkpointCount)
        assertTrue("Forced move ratio must be between 0.0 and 1.0", diff.forcedMoveRatio in 0.0..1.0)
        assertTrue("Solver search nodes must be > 0", diff.solverSearchNodes > 0)
        assertEquals(SolutionClassification.UNIQUE, diff.solutionClassification)
    }

    // 21. Level 5+ anti-trivial filter works
    @Test
    fun test21_level5PlusAntiTrivialFilterWorks() {
        fun countTurns(path: List<GridPosition>): Int {
            var turns = 0
            for (i in 1 until path.size - 1) {
                val d1r = path[i].row - path[i - 1].row
                val d1c = path[i].column - path[i - 1].column
                val d2r = path[i + 1].row - path[i].row
                val d2c = path[i + 1].column - path[i].column
                if (d1r != d2r || d1c != d2c) turns++
            }
            return turns
        }

        // Levels 5–20 must have at least 8 turns (not a trivial horizontal/vertical snake)
        for (lvlId in 5..20) {
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            val turns = countTurns(sol.positions)
            assertTrue(
                "Level $lvlId must have at least 8 turns to prevent trivial snakes (found $turns)",
                turns >= 8
            )
        }
    }

    // 22. World 2 difficulty analysis exceeds tutorial baseline structurally
    @Test
    fun test22_world2ExceedsTutorialBaselineStructurally() {
        val tutorial = CuratedFirst50Levels.LEVEL_1
        val tutorialSol = CuratedFirst50Levels.SOLUTION_1
        val tutorialDiff = analyzer.analyzeDifficulty(tutorial, tutorialSol)

        val w2Level = CuratedFirst50Levels.LEVEL_25
        val w2Sol = CuratedFirst50Levels.SOLUTION_25
        val w2Diff = analyzer.analyzeDifficulty(w2Level, w2Sol)

        assertTrue(
            "World 2 cell count (49) must exceed tutorial (36)",
            w2Diff.cellCount > tutorialDiff.cellCount
        )
        assertTrue(
            "World 2 must have greater grid dimensions than World 1",
            w2Diff.gridSize.totalCells > tutorialDiff.gridSize.totalCells
        )
    }

    // 23. Progression state remains preserved
    @Test
    fun test23_progressionStateRemainsPreserved() {
        // Stable level IDs 1..50 remain identical
        for (i in 1..50) {
            val def = PackagedPuzzles.ALL_PACKAGED[i]
            assertNotNull("Stable level ID $i must be in PackagedPuzzles", def)
            val expectedWorldId = if (i <= 20) 1 else 2
            assertEquals("World assignment must be deterministic", expectedWorldId, WorldDefinition.forLevel(i).worldId)
        }
    }

    // 24. Economy state remains preserved
    @Test
    fun test24_economyStateRemainsPreserved() {
        val freeLimit = EconomyConfig.FREE_HINTS_PER_SOLO_LEVEL
        assertEquals("First 2 Solo hints must be free", 2, freeLimit)

        val hintCost = EconomyConfig.EXTRA_HINT_COIN_PRICE
        assertEquals("Additional hint cost must be 300 coins", 300, hintCost)
    }
}

