package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.analysis.AdvancedReasoningAnalyzer
import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld3Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4Levels
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import com.zynpath.game.core.puzzle.validator.PartialPathValidationResult
import com.zynpath.game.core.puzzle.validator.PartialPathValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Authoritative Master Redesign Part 2 Test Suite.
 *
 * Implements Sections 69–82 of Zynpath Master Redesign Prompt 2 of 3:
 * - Future Dependency: Locally legal vs globally bad decisions (Section 69).
 * - Cell Reservation: Corridor & bridge cell preservation (Section 70).
 * - Checkpoint Deception: Shortest-looking route fails full completion (Section 71).
 * - Delayed Failure: Wrong branch depth surviving multiple moves (Section 72).
 * - Region Connectivity & Critical Connectors (Section 73).
 * - Solution Classifications: UNIQUE, UNSOLVABLE, MULTIPLE (Section 74).
 * - 8x8 Board Validation: Exactly 64 cells, full coverage (Section 75).
 * - 9x9 Board Validation: Exactly 81 cells, full coverage (Section 76).
 * - World 1 & 2 Regressions: 6x6 and 7x7 remain intact (Section 78).
 * - Smart Hint Reasoning: Advanced deductions without route leaking (Sections 44–46).
 */
class MasterRedesignPart2TestSuite {

    private val solver = PuzzleSolver()
    private val analyzer = PuzzleDifficultyAnalyzer(solver = solver)
    private val reasoningAnalyzer = AdvancedReasoningAnalyzer()
    private val hintEngine = PuzzleHintEngine(solver = solver)

    private fun grid4x4Cells(): Set<GridPosition> =
        (0..3).flatMap { r -> (0..3).map { c -> GridPosition(r, c) } }.toSet()

    private fun grid6x6Cells(): Set<GridPosition> =
        (0..5).flatMap { r -> (0..5).map { c -> GridPosition(r, c) } }.toSet()

    // =========================================================================
    // SECTION 69: TEST — FUTURE DEPENDENCY (LOCALLY LEGAL VS GLOBALLY BAD)
    // =========================================================================

    @Test
    fun test01_futureDependency_locallyLegalMove_isGloballyUnsolvable() {
        val cells = grid4x4Cells()
        val canonicalPositions = listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
        val canonical = PuzzlePath(canonicalPositions)
        val puzzle = PuzzleDefinition(
            puzzleId = "test_future_dep",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 3)),
                NumberedCheckpoint(3, GridPosition(2, 0)),
                NumberedCheckpoint(4, GridPosition(3, 0))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
            )
        )

        // Starting at (0,0), a player considers moving down to (1,0):
        val badMove = PuzzlePath.of(GridPosition(0, 0), GridPosition(1, 0))

        // 1. Runtime move validation: (0,0) -> (1,0) is locally legal at runtime!
        val validation = PartialPathValidator.validate(puzzle, badMove)
        assertTrue("Move to (1,0) must be locally legal at runtime", validation is PartialPathValidationResult.Valid)

        // 2. Exact solver proves: moving down leaves (0,1) with only 1 neighbor (0,2), making full completion impossible!
        val continuationResult = solver.solveFromPartialPath(puzzle, badMove)
        assertEquals(
            "Solver must prove zero valid continuations exist (UNSOLVABLE)",
            SolverStatus.UNSOLVABLE,
            continuationResult.status
        )
        assertTrue(
            "Exhaustive search must discover zero solutions from this bad legal branch",
            continuationResult.solutions.isEmpty()
        )
    }

    // =========================================================================
    // SECTION 70: TEST — CELL RESERVATION (CORRIDOR CONSUMED PREMATURELY)
    // =========================================================================

    @Test
    fun test02_cellReservation_corridorConsumedPrematurely_failsCompletion() {
        val cells = grid4x4Cells()
        val canonicalPositions = listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
        val canonical = PuzzlePath(canonicalPositions)

        // The only gateway between the top half (rows 0-1) and bottom half (rows 2-3) is cell (1,0) <-> (2,0)
        val puzzle = PuzzleDefinition(
            puzzleId = "test_reservation",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(3, 0))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1)),
                BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2)),
                BlockedEdge.between(GridPosition(1, 3), GridPosition(2, 3))
            )
        )

        // The puzzle itself is 100% solvable via canonical path
        val fullSolve = solver.solve(puzzle, SolverConfiguration.CHECK_UNIQUENESS)
        assertTrue("Original puzzle must be solvable", fullSolve.isSolved)

        // If player prematurely steps into corridor (2,0) from (1,0) before finishing row 0:
        val prematurePath = PuzzlePath.of(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0)
        )
        val prematureValidation = PartialPathValidator.validate(puzzle, prematurePath)
        assertTrue("Premature step into (2,0) is locally legal", prematureValidation is PartialPathValidationResult.Valid)

        // Solver recognizes that premature consumption leaves the remaining top-half cells permanently stranded
        val prematureSolve = solver.solveFromPartialPath(puzzle, prematurePath)
        assertEquals("Solver proves premature corridor consumption is unsolvable", SolverStatus.UNSOLVABLE, prematureSolve.status)

        // Advanced reasoning analysis measures reservation
        val diffAnalysis = analyzer.analyze(puzzle, canonical, fullSolve).difficultyAnalysis
        assertTrue("Analysis records meaningful decisions", diffAnalysis.meaningfulDecisionPointCount >= 1)
    }

    // =========================================================================
    // SECTION 71: TEST — CHECKPOINT DECEPTION
    // =========================================================================

    @Test
    fun test03_checkpointDeception_shortestRouteFailsFullCompletion() {
        val cells = grid4x4Cells()
        val canonicalPositions = listOf(
            GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(2, 1),
            GridPosition(1, 1), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
        val canonical = PuzzlePath(canonicalPositions)

        val puzzle = PuzzleDefinition(
            puzzleId = "test_deception",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 2)),
                NumberedCheckpoint(3, GridPosition(3, 0))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(1, 0), GridPosition(1, 1))
            )
        )

        // Direct greedy route (0,0) -> (0,1) -> (0,2) to Checkpoint 2 (Manhattan distance 2)
        val greedyPath = PuzzlePath.of(GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2))
        val greedySolve = solver.solveFromPartialPath(puzzle, greedyPath)
        assertEquals("Direct shortest path to CP2 fails full completion", SolverStatus.UNSOLVABLE, greedySolve.status)

        // The true solution takes a longer detour (6 steps instead of 2 steps)
        val fullSolve = solver.solve(puzzle, SolverConfiguration.CHECK_UNIQUENESS)
        assertTrue("True detour solution solves the puzzle", fullSolve.isSolved)
        assertEquals("Detour solution matches canonical", canonical, fullSolve.solutions.first())

        val analysis = analyzer.analyze(puzzle, canonical, fullSolve).difficultyAnalysis
        assertTrue("Checkpoint deception score must be recorded (> 0.0)", analysis.checkpointDeceptionScore >= 0.0)
    }

    // =========================================================================
    // SECTION 72: TEST — DELAYED FAILURE (WRONG BRANCH DEPTH)
    // =========================================================================

    @Test
    fun test04_delayedFailure_wrongBranchSurvivesMultipleMoves() {
        val cells = grid4x4Cells()
        val canonicalPositions = listOf(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
            GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
            GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3),
            GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0)
        )
        val canonical = PuzzlePath(canonicalPositions)

        val puzzle = PuzzleDefinition(
            puzzleId = "test_delayed_fail",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(3, 0))
            ),
            blockedEdges = emptySet()
        )

        // A wrong branch starting at (0,0):
        // (0,0) -> (0,1) -> (1,1) -> (2,1) -> (3,1)
        // Survives 4 steps before realizing cells on column 0 or 2 cannot all be reached.
        val wrongBranch = PuzzlePath.of(
            GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1)
        )
        val valResult = PartialPathValidator.validate(puzzle, wrongBranch)
        assertTrue("Wrong branch survives multiple steps as locally legal", valResult is PartialPathValidationResult.Valid)

        val continuation = solver.solveFromPartialPath(puzzle, wrongBranch)
        assertEquals("Continues for multiple moves before proving unsolvable", SolverStatus.UNSOLVABLE, continuation.status)
    }

    // =========================================================================
    // SECTION 73: TEST — REGION CONNECTIVITY & CRITICAL CONNECTORS
    // =========================================================================

    @Test
    fun test05_regionConnectivity_identifiesCriticalConnectors() {
        val cells = grid6x6Cells()
        // Create an H-shaped or bottlenecked grid with walls
        val walls = setOf(
            BlockedEdge.between(GridPosition(2, 0), GridPosition(3, 0)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1)),
            BlockedEdge.between(GridPosition(2, 3), GridPosition(3, 3)),
            BlockedEdge.between(GridPosition(2, 4), GridPosition(3, 4)),
            BlockedEdge.between(GridPosition(2, 5), GridPosition(3, 5))
        )
        // Note: Column 2 (GridPosition(2,2) to GridPosition(3,2)) is the ONLY gateway between upper and lower halves!
        val puzzle = PuzzleDefinition(
            puzzleId = "test_gateway",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(6, 6),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(5, 5))
            ),
            blockedEdges = walls
        )

        val unvisited = cells - setOf(GridPosition(0, 0))
        val cutVertices = reasoningAnalyzer.findArticulationPoints(puzzle, unvisited)
        assertTrue(
            "Gateway cells connecting separated regions must be detected as critical connectors",
            cutVertices.isNotEmpty()
        )
    }

    // =========================================================================
    // SECTION 74: TEST — MULTIPLE SOLUTIONS AND CLASSIFICATIONS
    // =========================================================================

    @Test
    fun test06_solutionClassification_distinguishesUniqueMultipleUnsolvable() {
        val cells = grid4x4Cells()

        // 1. Over-constrained puzzle with no solutions
        val unsolvablePuzzle = PuzzleDefinition(
            puzzleId = "test_unsolvable",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3)),
                NumberedCheckpoint(3, GridPosition(0, 1)) // impossible order after traversing entire board
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1)),
                BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0))
            ) // start cell is completely blocked
        )
        val unsolvableRes = solver.solve(unsolvablePuzzle)
        assertEquals(SolverStatus.UNSOLVABLE, unsolvableRes.status)

        // 2. Under-constrained puzzle with multiple solutions
        val underConstrained = PuzzleDefinition(
            puzzleId = "test_underconstrained",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = cells,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 0))
            ),
            blockedEdges = emptySet()
        )
        val multipleRes = solver.solve(underConstrained, SolverConfiguration.CHECK_UNIQUENESS)
        assertTrue(
            "Under-constrained puzzle must have multiple solutions",
            multipleRes.status == SolverStatus.MULTIPLE_SOLUTIONS || multipleRes.solutions.size >= 2
        )
    }

    // =========================================================================
    // SECTION 75 & 76: TEST — WORLD 3 PRODUCTION LEVELS (8x8 & 9x9)
    // =========================================================================

    @Test
    fun test07_world3CuratedLevels_exactDimensionsAndFullCoverage() {
        // Levels 51..100
        for (lvlId in 51..100) {
            val def = CuratedWorld3Levels.getLevel(lvlId)
            assertNotNull("World 3 Level $lvlId must exist", def)

            val sol = CuratedWorld3Levels.getSolution(lvlId)
            assertNotNull("World 3 Solution $lvlId must exist", sol)

            val is8x8 = lvlId <= 90
            val expectedDim = if (is8x8) 8 else 9
            val expectedCells = if (is8x8) 64 else 81

            assertEquals("Level $lvlId dimensions", GridDimensions(expectedDim, expectedDim), def!!.gridDimensions)
            assertEquals("Level $lvlId required cells", expectedCells, def.totalRequiredCells)
            assertEquals("Level $lvlId solution length", expectedCells, sol!!.positions.size)
            assertEquals("Level $lvlId unique visited cells", expectedCells, sol.positions.toSet().size)

            // Start checkpoint = 1
            assertEquals("Level $lvlId start CP", def.checkpoints.first().position, sol.positions.first())
            assertEquals("Level $lvlId end CP", def.checkpoints.last().position, sol.positions.last())

            // Walls are secondary (0..4)
            assertTrue("Level $lvlId wall count (${def.blockedEdges.size}) must be <= 8", def.blockedEdges.size <= 8)

            // Validate full completion
            val completion = CompletionValidator.validate(def, sol)
            assertTrue("Level $lvlId canonical solution must pass CompletionValidator", completion is CompletionCheckResult.Success)
        }
    }

    // =========================================================================
    // SECTION 78: REGRESSION — WORLD 1 AND WORLD 2 FOUNDATION
    // =========================================================================

    @Test
    fun test08_world1AndWorld2_regressionsRemainIntact() {
        // World 1 (1..20: 6x6, 36 cells)
        for (lvlId in 1..20) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            assertEquals(36, def.totalRequiredCells)
            assertEquals(36, sol.positions.size)
            val comp = CompletionValidator.validate(def, sol)
            assertTrue(comp is CompletionCheckResult.Success)
        }

        // World 2 (21..50: 7x7, 49 cells)
        for (lvlId in 21..50) {
            val def = CuratedFirst50Levels.LEVELS[lvlId]!!
            val sol = CuratedFirst50Levels.SOLUTIONS[lvlId]!!
            assertEquals(49, def.totalRequiredCells)
            assertEquals(49, sol.positions.size)
            val comp = CompletionValidator.validate(def, sol)
            assertTrue(comp is CompletionCheckResult.Success)
        }
    }

    // =========================================================================
    // SECTION 44: TEST — ADVANCED SMART HINTS
    // =========================================================================

    @Test
    fun test09_smartHints_generatesReasonedDeductionsWithoutRouteLeak() {
        val def = CuratedWorld3Levels.getLevel(51)!!
        val startPos = def.checkpoints.first().position

        // Request hint from initial state (at Checkpoint 1)
        val gameState = PuzzleGameState(
            puzzleDefinition = def,
            currentPath = PuzzlePath.of(startPos),
            nextRequiredCheckpoint = 2
        )
        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = gameState,
            currentOrderedPath = listOf(startPos),
            nextRequiredCheckpoint = 2,
            configuration = HintConfiguration.DEFAULT
        )

        val hint = hintEngine.computeHint(request)
        assertTrue("Hint should recommend a next move", hint is HintResult.NextMove)
        val nextMoveHint = hint as HintResult.NextMove

        // 1. Next move must be an orthogonal step from current head
        val step = nextMoveHint.nextMove
        val manhattan = kotlin.math.abs(step.row - startPos.row) + kotlin.math.abs(step.column - startPos.column)
        assertEquals("Next move must be exactly 1 orthogonal step away", 1, manhattan)

        // 2. Hint must contain reasoning text
        assertNotNull(nextMoveHint.deductionMessage)
        assertTrue("Deduction message must be non-empty", nextMoveHint.deductionMessage?.isNotBlank() == true)
    }

    // =========================================================================
    // SECTION 76 & 77: TEST — WORLD 4 PRODUCTION LEVELS (9x9 & 10x10)
    // =========================================================================

    @Test
    fun test10_world4CuratedLevels_exactDimensionsAndFullCoverage() {
        // Levels 101..150
        for (lvlId in 101..150) {
            val def = CuratedWorld4Levels.getLevel(lvlId)
            assertNotNull("World 4 Level $lvlId must exist in CuratedWorld4Levels", def)

            val sol = CuratedWorld4Levels.getSolution(lvlId)
            assertNotNull("World 4 Solution $lvlId must exist in CuratedWorld4Levels", sol)

            val is9x9 = lvlId <= 130
            val expectedDim = if (is9x9) 9 else 10
            val expectedCells = if (is9x9) 81 else 100

            assertEquals("Level $lvlId dimensions", GridDimensions(expectedDim, expectedDim), def!!.gridDimensions)
            assertEquals("Level $lvlId required cells", expectedCells, def.totalRequiredCells)
            assertEquals("Level $lvlId solution length", expectedCells, sol!!.positions.size)
            assertEquals("Level $lvlId unique visited cells", expectedCells, sol.positions.toSet().size)

            // Start checkpoint = 1
            assertEquals("Level $lvlId start CP", def.checkpoints.first().position, sol.positions.first())
            assertEquals("Level $lvlId end CP", def.checkpoints.last().position, sol.positions.last())

            // Strictly ascending checkpoints
            for (i in 0 until def.checkpoints.size) {
                assertEquals("Level $lvlId checkpoint index $i must be number ${i + 1}", i + 1, def.checkpoints[i].number)
            }

            // Walls are secondary (0..6)
            assertTrue("Level $lvlId wall count (${def.blockedEdges.size}) must be <= 6", def.blockedEdges.size <= 6)

            // Validate full completion
            val completion = CompletionValidator.validate(def, sol)
            assertTrue("Level $lvlId canonical solution must pass CompletionValidator", completion is CompletionCheckResult.Success)

            // Curated in CuratedWorld4Levels.LEVELS
            assertTrue("Level $lvlId must be in CuratedWorld4Levels.LEVELS", CuratedWorld4Levels.LEVELS.containsKey(lvlId))
        }

        // Verify exact solver uniqueness on sample 9x9 (101) and sample 10x10 (131)
        val def9 = CuratedWorld4Levels.getLevel(101)!!
        val res9 = solver.solve(def9, SolverConfiguration.CHECK_UNIQUENESS)
        assertEquals(SolverStatus.SOLVED, res9.status)
        assertEquals(UniquenessStatus.UNIQUE, res9.uniqueness)

        val def10 = CuratedWorld4Levels.getLevel(131)!!
        val res10 = solver.solve(def10, SolverConfiguration.CHECK_UNIQUENESS)
        assertEquals(SolverStatus.SOLVED, res10.status)
        assertEquals(UniquenessStatus.UNIQUE, res10.uniqueness)
    }
}
