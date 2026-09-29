package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.Direction
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import com.zynpath.game.core.puzzle.validator.IncompleteReason
import com.zynpath.game.core.puzzle.validator.PathValidationResult
import com.zynpath.game.core.puzzle.validator.ViolationReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive verification of canonical puzzle rules, wall representations,
 * orthogonal movements, grid boundary corners, and malformed input handling.
 *
 * Implements Prompt 46 Sections 7-21 & 71:
 * - 7 Canonical Rules: Start at 1, Orthogonal only, Ascending checkpoints,
 *   No cell revisit, No blocked edge crossing, Full-grid coverage, End at highest checkpoint.
 * - Wall representation (blocked edges vs tiles, wall symmetry).
 * - Boundary and corner property tests.
 * - Negative and malformed input handling without crashes.
 */
class CanonicalRulesAndBoundaryPropertyTest {

    private lateinit var puzzle4x4: PuzzleDefinition
    private lateinit var puzzle5x5WithWalls: PuzzleDefinition

    @Before
    fun setUp() {
        puzzle4x4 = SamplePuzzleFixtures.puzzle4x4Valid
        puzzle5x5WithWalls = SamplePuzzleFixtures.puzzle5x5WithWalls
    }

    // =========================================================================
    // 1. Orthogonal Movement & Direction Tests (Sections 10, 71)
    // =========================================================================

    @Test
    fun `test orthogonal moves are accepted in all four cardinal directions`() {
        val center = GridPosition(2, 2)

        val up = center.neighbor(Direction.UP)
        val down = center.neighbor(Direction.DOWN)
        val left = center.neighbor(Direction.LEFT)
        val right = center.neighbor(Direction.RIGHT)

        assertEquals(GridPosition(1, 2), up)
        assertEquals(GridPosition(3, 2), down)
        assertEquals(GridPosition(2, 1), left)
        assertEquals(GridPosition(2, 3), right)

        assertTrue(center.isOrthogonallyAdjacentTo(up))
        assertTrue(center.isOrthogonallyAdjacentTo(down))
        assertTrue(center.isOrthogonallyAdjacentTo(left))
        assertTrue(center.isOrthogonallyAdjacentTo(right))
    }

    @Test
    fun `test diagonal and non-adjacent moves are strictly rejected`() {
        val center = GridPosition(2, 2)

        val diagonals = listOf(
            GridPosition(1, 1), // UP-LEFT
            GridPosition(1, 3), // UP-RIGHT
            GridPosition(3, 1), // DOWN-LEFT
            GridPosition(3, 3)  // DOWN-RIGHT
        )

        for (diag in diagonals) {
            assertFalse("Diagonal move to $diag must not be orthogonally adjacent", center.isOrthogonallyAdjacentTo(diag))
            assertEquals("Diagonal distance must be 2", 2, center.manhattanDistanceTo(diag))
        }

        val teleports = listOf(
            GridPosition(0, 2), // 2 steps up
            GridPosition(4, 2), // 2 steps down
            GridPosition(2, 0), // 2 steps left
            GridPosition(2, 4)  // 2 steps right
        )

        for (teleport in teleports) {
            assertFalse("Teleport move to $teleport must not be orthogonally adjacent", center.isOrthogonallyAdjacentTo(teleport))
        }
    }

    @Test
    fun `test engine rejects diagonal and teleport moves`() {
        val engine = PuzzleEngine(puzzle4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Attempt diagonal move from (0,0) to (1,1)
        val diagResult = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        assertFalse("Engine must reject diagonal move", diagResult.isAccepted)
        assertEquals(1, engine.currentState.coveredCellCount)

        // Attempt teleport move to (0,2)
        val teleportResult = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        assertFalse("Engine must reject non-adjacent move", teleportResult.isAccepted)
        assertEquals(1, engine.currentState.coveredCellCount)
    }

    // =========================================================================
    // 2. Grid Boundary & Corner Property Tests (Sections 9, 11, 71)
    // =========================================================================

    @Test
    fun `test boundary corners and out-of-bounds rejection`() {
        val dims = GridDimensions(4, 4)

        val corners = listOf(
            GridPosition(0, 0), // Top-Left
            GridPosition(0, 3), // Top-Right
            GridPosition(3, 0), // Bottom-Left
            GridPosition(3, 3)  // Bottom-Right
        )

        for (corner in corners) {
            assertTrue("Corner $corner must be within bounds", dims.contains(corner))
        }

        // Out of bounds positions
        val outOfBoundsPositions = listOf(
            GridPosition(-1, 0),
            GridPosition(0, -1),
            GridPosition(-1, -1),
            GridPosition(4, 0),
            GridPosition(0, 4),
            GridPosition(4, 4),
            GridPosition(100, 100)
        )

        for (oob in outOfBoundsPositions) {
            assertFalse("Position $oob must be out of bounds for 4x4", dims.contains(oob))
        }
    }

    @Test
    fun `test coordinate conversions and cell indices`() {
        val dims = GridDimensions(5, 5)

        for (r in 0 until 5) {
            for (c in 0 until 5) {
                val pos = GridPosition(r, c)
                val index = pos.toCellIndex(dims.columns)
                assertEquals(r * 5 + c, index)
                val recovered = GridPosition.fromCellIndex(index, dims.columns)
                assertEquals(pos, recovered)
            }
        }
    }

    // =========================================================================
    // 3. Wall Representation & Symmetry (Sections 8, 16, 17, 71)
    // =========================================================================

    @Test
    fun `test walls are blocked edges between cells, not blocked tiles`() {
        val wall = BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))

        // Both cells remain part of the required cells!
        val def = puzzle5x5WithWalls
        assertTrue("Cell (0,1) must be required despite adjacent wall", def.requiredCells.contains(GridPosition(0, 1)))
        assertTrue("Cell (1,1) must be required despite adjacent wall", def.requiredCells.contains(GridPosition(1, 1)))
        assertEquals("Total required cells must equal 25 (no cells removed by walls)", 25, def.requiredCells.size)
    }

    @Test
    fun `test wall symmetry in definition and traversal`() {
        val p1 = GridPosition(2, 1)
        val p2 = GridPosition(3, 1)

        val edge1 = BlockedEdge.between(p1, p2)
        val edge2 = BlockedEdge.between(p2, p1)

        assertEquals("BlockedEdge must be equal regardless of argument order", edge1, edge2)
        assertEquals("BlockedEdge hashCodes must match regardless of argument order", edge1.hashCode(), edge2.hashCode())

        // Engine must block crossing in both directions
        val engine = PuzzleEngine(puzzle5x5WithWalls)
        val graph = puzzle5x5WithWalls.graph

        assertTrue("Graph must block p1 -> p2", graph.isBlocked(p1, p2))
        assertTrue("Graph must block p2 -> p1", graph.isBlocked(p2, p1))
    }

    @Test
    fun `test entering cell from unblocked edge is allowed while blocked edge is impassable`() {
        // In puzzle5x5WithWalls, there is a wall between (0,1) and (1,1)
        val graph = puzzle5x5WithWalls.graph
        val wallEdge = BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1))
        assertTrue("Wall must exist in puzzle", puzzle5x5WithWalls.blockedEdges.contains(wallEdge))

        // Moving (0,1) -> (1,1) is BLOCKED
        assertTrue("Direct move across wall must be blocked", graph.isBlocked(GridPosition(0, 1), GridPosition(1, 1)))

        // But moving into (1,1) from (1,0) or (1,2) or (2,1) is NOT blocked by that wall
        assertFalse("Move into (1,1) from (1,0) must not be blocked", graph.isBlocked(GridPosition(1, 0), GridPosition(1, 1)))
        assertFalse("Move into (1,1) from (1,2) must not be blocked", graph.isBlocked(GridPosition(1, 2), GridPosition(1, 1)))
        assertFalse("Move into (1,1) from (2,1) must not be blocked", graph.isBlocked(GridPosition(2, 1), GridPosition(1, 1)))
    }

    // =========================================================================
    // 4. Canonical Puzzle Rules Validation (Sections 7, 12, 13, 14, 15, 18, 19, 20)
    // =========================================================================

    @Test
    fun `Rule 1 - path must begin at checkpoint 1`() {
        val engine = PuzzleEngine(puzzle4x4)
        // Checkpoint 1 is at (0,0). Attempt to start at (0,1)
        val startResult = engine.process(PuzzleAction.StartPath(GridPosition(0, 1)))
        assertFalse("Path must not start at non-checkpoint 1 cell", startResult.isAccepted)
        assertTrue(engine.currentState.currentPath.isEmpty)

        // Independent validator check
        val invalidStartPath = PuzzlePath.of(GridPosition(0, 1), GridPosition(0, 2))
        val result = FoundationalPathValidator.validate(puzzle4x4, invalidStartPath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.INVALID_START_CELL, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun `Rule 3 - checkpoints must be visited in strict ascending order`() {
        // 4x4 Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        // Try skipping checkpoint 2: go from 1 directly to checkpoint 3 at (2,0)
        val skipPath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(1, 0),
            GridPosition(2, 0)  // Reached Checkpoint #3 before #2!
        )

        val result = FoundationalPathValidator.validate(puzzle4x4, skipPath)
        assertFalse("Skipping checkpoint 2 must be invalid", result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.WRONG_CHECKPOINT_ORDER, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun `Rule 4 - cell revisit is strictly rejected`() {
        val engine = PuzzleEngine(puzzle4x4)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)))

        // Attempt to move back into (0, 0)
        val revisitResult = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 0)))
        assertFalse("Moving into already occupied cell must be rejected", revisitResult.isAccepted)
        assertEquals(4, engine.currentState.coveredCellCount)
    }

    @Test
    fun `Rule 6 & 7 - reaching final checkpoint with uncovered cells is strictly NOT victory`() {
        val shortCutPath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(0, 1),
            GridPosition(0, 2),
            GridPosition(0, 3),
            GridPosition(1, 3), // #2
            GridPosition(1, 2),
            GridPosition(1, 1),
            GridPosition(1, 0),
            GridPosition(2, 0), // #3
            GridPosition(3, 0)  // #4 (final checkpoint reached, but only 10 cells covered!)
        )

        val foundResult = FoundationalPathValidator.validate(puzzle4x4, shortCutPath)
        assertFalse("Must not produce victory when cells remain uncovered", foundResult.isWin)
        assertTrue(foundResult is PathValidationResult.Incomplete)
        assertEquals(IncompleteReason.INCOMPLETE_COVERAGE_AT_FINAL_CHECKPOINT, (foundResult as PathValidationResult.Incomplete).reason)

        val compResult = CompletionValidator.validate(puzzle4x4, shortCutPath)
        assertFalse("CompletionValidator must reject premature final checkpoint", compResult is CompletionCheckResult.Success)
    }

    @Test
    fun `Rule Victory - complete valid route covering all cells in order achieves victory`() {
        val solution = SamplePuzzleFixtures.solution4x4Route

        val foundResult = FoundationalPathValidator.validate(puzzle4x4, solution)
        assertTrue("Complete valid solution must produce victory", foundResult.isWin)
        assertTrue(foundResult is PathValidationResult.ValidVictory)

        val compResult = CompletionValidator.validate(puzzle4x4, solution)
        assertTrue("CompletionValidator must accept complete valid solution", compResult is CompletionCheckResult.Success)
    }

    // =========================================================================
    // 5. Malformed and Boundary Inputs Safe Handling (Section 21)
    // =========================================================================

    @Test
    fun `test engine handles malformed inputs safely without crash`() {
        // Empty path validation
        val emptyPath = PuzzlePath.empty()
        val emptyResult = FoundationalPathValidator.validate(puzzle4x4, emptyPath)
        assertFalse(emptyResult.isWin)
        assertTrue(emptyResult is PathValidationResult.Violation)

        // Path extending outside board
        val oobPath = PuzzlePath.of(GridPosition(0, 0), GridPosition(-1, 0))
        val oobResult = FoundationalPathValidator.validate(puzzle4x4, oobPath)
        assertFalse(oobResult.isWin)
        assertTrue(oobResult is PathValidationResult.Violation)
    }
}
