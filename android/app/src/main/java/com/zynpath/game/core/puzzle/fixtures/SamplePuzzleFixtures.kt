package com.zynpath.game.core.puzzle.fixtures

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Deterministic test fixtures providing valid and invalid puzzle definitions along with
 * mathematically verified full-coverage routes for automated engine tests.
 */
object SamplePuzzleFixtures {

    // =========================================================================
    // 1. VALID 4x4 FIXTURE WITHOUT WALLS
    // =========================================================================

    /**
     * 4x4 puzzle without walls.
     * Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
     */
    val puzzle4x4Valid: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_4x4_clean",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 3)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 0))
        ),
        difficultyMetadata = "Beginner"
    )

    /**
     * Mathematically verified 16-cell serpentine Hamiltonian path for [puzzle4x4Valid].
     * (0,0)->(0,1)->(0,2)->(0,3)->(1,3)->(1,2)->(1,1)->(1,0)->(2,0)->(2,1)->(2,2)->(2,3)->(3,3)->(3,2)->(3,1)->(3,0)
     */
    val solution4x4Route: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0), // #1
        GridPosition(0, 1),
        GridPosition(0, 2),
        GridPosition(0, 3),
        GridPosition(1, 3), // #2
        GridPosition(1, 2),
        GridPosition(1, 1),
        GridPosition(1, 0),
        GridPosition(2, 0), // #3
        GridPosition(2, 1),
        GridPosition(2, 2),
        GridPosition(2, 3),
        GridPosition(3, 3),
        GridPosition(3, 2),
        GridPosition(3, 1),
        GridPosition(3, 0)  // #4
    )

    // =========================================================================
    // 2. VALID 5x5 FIXTURE WITHOUT WALLS
    // =========================================================================

    /**
     * 5x5 puzzle without walls.
     * Checkpoints: #1 at (0,0), #2 at (1,4), #3 at (2,0), #4 at (3,4), #5 at (4,4).
     */
    val puzzle5x5Valid: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_5x5_clean",
        dimensions = GridDimensions(5, 5),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 4)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(4, 4))
        ),
        difficultyMetadata = "Standard"
    )

    /**
     * Mathematically verified 25-cell serpentine Hamiltonian path for [puzzle5x5Valid].
     */
    val solution5x5Route: PuzzlePath = PuzzlePath.of(
        // Row 0 left-to-right (5 cells)
        GridPosition(0, 0), // #1
        GridPosition(0, 1),
        GridPosition(0, 2),
        GridPosition(0, 3),
        GridPosition(0, 4),
        // Row 1 right-to-left (5 cells)
        GridPosition(1, 4), // #2
        GridPosition(1, 3),
        GridPosition(1, 2),
        GridPosition(1, 1),
        GridPosition(1, 0),
        // Row 2 left-to-right (5 cells)
        GridPosition(2, 0), // #3
        GridPosition(2, 1),
        GridPosition(2, 2),
        GridPosition(2, 3),
        GridPosition(2, 4),
        // Row 3 right-to-left (5 cells)
        GridPosition(3, 4), // #4
        GridPosition(3, 3),
        GridPosition(3, 2),
        GridPosition(3, 1),
        GridPosition(3, 0),
        // Row 4 left-to-right (5 cells)
        GridPosition(4, 0),
        GridPosition(4, 1),
        GridPosition(4, 2),
        GridPosition(4, 3),
        GridPosition(4, 4)  // #5
    )

    // =========================================================================
    // 3. VALID 5x5 FIXTURE WITH BLOCKED EDGES (WALLS)
    // =========================================================================

    /**
     * 5x5 puzzle with 3 legal blocked edges.
     * Walls separate cells that are not traversed by [solution5x5Route], ensuring solvability.
     */
    val puzzle5x5WithWalls: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_5x5_walls",
        dimensions = GridDimensions(5, 5),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 4)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 4)),
            NumberedCheckpoint(5, GridPosition(4, 4))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(0, 3), GridPosition(1, 3)),
            BlockedEdge.between(GridPosition(2, 1), GridPosition(3, 1))
        ),
        difficultyMetadata = "Intermediate"
    )

    // =========================================================================
    // 4. INVALID FIXTURES FOR STRUCTURAL VALIDATION TESTING
    // =========================================================================

    /**
     * Malformed puzzle: Duplicate checkpoint number (#2 appears twice).
     */
    fun createDuplicateCheckpointNumberFixture(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "invalid_dup_num",
        gridDimensions = GridDimensions(4, 4),
        requiredCells = GridDimensions(4, 4).allPositions().toSet(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 1)),
            NumberedCheckpoint(2, GridPosition(1, 0)) // Duplicate #2
        )
    )

    /**
     * Malformed puzzle: Two checkpoints on the same cell (0, 0).
     */
    fun createDuplicateCheckpointPositionFixture(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "invalid_dup_pos",
        gridDimensions = GridDimensions(4, 4),
        requiredCells = GridDimensions(4, 4).allPositions().toSet(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 0)) // Same position
        )
    )

    /**
     * Malformed puzzle: Missing start checkpoint (#1).
     */
    fun createMissingStartCheckpointFixture(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "invalid_no_start",
        gridDimensions = GridDimensions(4, 4),
        requiredCells = GridDimensions(4, 4).allPositions().toSet(),
        checkpoints = listOf(
            NumberedCheckpoint(2, GridPosition(0, 0)),
            NumberedCheckpoint(3, GridPosition(3, 3))
        )
    )

    /**
     * Malformed puzzle: Non-contiguous checkpoint numbers (1, 2, 4 — gap at 3).
     */
    fun createNonContiguousCheckpointsFixture(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "invalid_gap",
        gridDimensions = GridDimensions(4, 4),
        requiredCells = GridDimensions(4, 4).allPositions().toSet(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 1)),
            NumberedCheckpoint(4, GridPosition(3, 3))
        )
    )

    /**
     * Malformed puzzle: Checkpoint outside of grid boundary (5, 5 on a 4x4 grid).
     */
    fun createOutOfBoundsCheckpointFixture(): PuzzleDefinition = PuzzleDefinition(
        puzzleId = "invalid_oob_cp",
        gridDimensions = GridDimensions(4, 4),
        requiredCells = GridDimensions(4, 4).allPositions().toSet(),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(5, 5)) // Out of bounds
        )
    )

    /**
     * Malformed puzzle: Wall between non-adjacent cells (0,0) and (0,2).
     */
    fun createNonAdjacentWallFixture(): PuzzleDefinition {
        val nonAdjWall = BlockedEdge(GridPosition(0, 0), GridPosition(0, 2))

        return PuzzleDefinition(
            puzzleId = "invalid_non_adj_wall",
            gridDimensions = GridDimensions(4, 4),
            requiredCells = GridDimensions(4, 4).allPositions().toSet(),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3))
            ),
            blockedEdges = setOf(nonAdjWall)
        )
    }

    /**
     * Malformed puzzle: Wall on out-of-bounds cells.
     */
    fun createOutOfBoundsWallFixture(): PuzzleDefinition {
        val oobWall = BlockedEdge(GridPosition(4, 0), GridPosition(4, 1))

        return PuzzleDefinition(
            puzzleId = "invalid_oob_wall",
            gridDimensions = GridDimensions(4, 4),
            requiredCells = GridDimensions(4, 4).allPositions().toSet(),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3))
            ),
            blockedEdges = setOf(oobWall)
        )
    }

    // =========================================================================
    // 5. SOLVER SPECIFIC TEST FIXTURES
    // =========================================================================

    /**
     * Structurally valid 3x3 puzzle without walls with at least 2 distinct valid full-coverage solutions.
     * Checkpoints: #1 at (0,0), #2 at (2,2).
     */
    val puzzle3x3MultipleSolutions: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_3x3_multiple",
        dimensions = GridDimensions(3, 3),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(2, 2))
        ),
        difficultyMetadata = "MultiSolution"
    )

    /** First verified 9-cell solution for [puzzle3x3MultipleSolutions]. */
    val solution3x3RouteA: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0),
        GridPosition(0, 1),
        GridPosition(0, 2),
        GridPosition(1, 2),
        GridPosition(1, 1),
        GridPosition(1, 0),
        GridPosition(2, 0),
        GridPosition(2, 1),
        GridPosition(2, 2)
    )

    /** Second verified distinct 9-cell solution for [puzzle3x3MultipleSolutions]. */
    val solution3x3RouteB: PuzzlePath = PuzzlePath.of(
        GridPosition(0, 0),
        GridPosition(1, 0),
        GridPosition(2, 0),
        GridPosition(2, 1),
        GridPosition(1, 1),
        GridPosition(0, 1),
        GridPosition(0, 2),
        GridPosition(1, 2),
        GridPosition(2, 2)
    )

    /**
     * Structurally valid 3x3 puzzle with walls and checkpoints configured such that
     * mathematically EXACTLY ONE unique full-coverage solution exists.
     * Checkpoints: #1 at (0,0), #2 at (0,2), #3 at (2,0), #4 at (2,2).
     * Walls: (0,1)|(1,1) and (1,1)|(2,1).
     */
    val puzzle3x3Unique: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_3x3_unique",
        dimensions = GridDimensions(3, 3),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(0, 2)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(2, 2))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1))
        ),
        difficultyMetadata = "Unique"
    )

    /**
     * Structurally valid 4x4 puzzle that is mathematically UNSOLVABLE due to checkerboard parity.
     * In any 16-cell path on a bipartite grid starting at an even cell (0,0), the final (16th) cell
     * after 15 steps must land on an odd cell. Checkpoint #2 is placed at (2,2) (even cell).
     * Therefore no Hamiltonian path can ever exist.
     */
    val puzzle4x4UnsolvableParity: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_4x4_unsolvable_parity",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(2, 2))
        ),
        difficultyMetadata = "UnsolvableParity"
    )

    /**
     * Structurally valid 4x4 puzzle that is mathematically UNSOLVABLE due to wall isolation.
     * Cell (0, 1) has walls to all 3 of its grid neighbors: (0,0), (1,1), and (0,2).
     * It has degree 0 in the traversable graph and can never be visited.
     */
    val puzzle4x4UnsolvableWalls: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_4x4_unsolvable_walls",
        dimensions = GridDimensions(4, 4),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(3, 3))
        ),
        blockedEdges = setOf(
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 0)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(1, 1)),
            BlockedEdge.between(GridPosition(0, 1), GridPosition(0, 2))
        ),
        difficultyMetadata = "UnsolvableWalls"
    )

    // =========================================================================
    // 6. BENCHMARK FIXTURES (6x6, 7x7, 8x8)
    // =========================================================================

    /**
     * 6x6 puzzle without walls (36 cells).
     */
    val puzzle6x6Valid: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_6x6_clean",
        dimensions = GridDimensions(6, 6),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 5)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 5)),
            NumberedCheckpoint(5, GridPosition(4, 0)),
            NumberedCheckpoint(6, GridPosition(5, 0))
        ),
        difficultyMetadata = "Expert_6x6"
    )

    /**
     * Mathematically verified 36-cell serpentine Hamiltonian path for [puzzle6x6Valid].
     */
    val solution6x6Route: PuzzlePath = PuzzlePath.of(
        // Row 0 (0..5)
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5),
        // Row 1 (5..0)
        GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
        // Row 2 (0..5)
        GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5),
        // Row 3 (5..0)
        GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
        // Row 4 (0..5)
        GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5),
        // Row 5 (5..0)
        GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0)
    )

    /**
     * 7x7 puzzle without walls (49 cells).
     */
    val puzzle7x7Valid: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_7x7_clean",
        dimensions = GridDimensions(7, 7),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 6)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 6)),
            NumberedCheckpoint(5, GridPosition(4, 0)),
            NumberedCheckpoint(6, GridPosition(5, 6)),
            NumberedCheckpoint(7, GridPosition(6, 6))
        ),
        difficultyMetadata = "Master_7x7"
    )

    /**
     * Mathematically verified 49-cell serpentine Hamiltonian path for [puzzle7x7Valid].
     */
    val solution7x7Route: PuzzlePath = PuzzlePath.of(
        // Row 0
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6),
        // Row 1
        GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
        // Row 2
        GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6),
        // Row 3
        GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
        // Row 4
        GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6),
        // Row 5
        GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
        // Row 6
        GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6)
    )

    /**
     * 8x8 puzzle without walls (64 cells).
     */
    val puzzle8x8Valid: PuzzleDefinition = PuzzleDefinition.standard(
        puzzleId = "fixture_8x8_clean",
        dimensions = GridDimensions(8, 8),
        checkpoints = listOf(
            NumberedCheckpoint(1, GridPosition(0, 0)),
            NumberedCheckpoint(2, GridPosition(1, 7)),
            NumberedCheckpoint(3, GridPosition(2, 0)),
            NumberedCheckpoint(4, GridPosition(3, 7)),
            NumberedCheckpoint(5, GridPosition(4, 0)),
            NumberedCheckpoint(6, GridPosition(5, 7)),
            NumberedCheckpoint(7, GridPosition(6, 0)),
            NumberedCheckpoint(8, GridPosition(7, 0))
        ),
        difficultyMetadata = "Grandmaster_8x8"
    )

    /**
     * Mathematically verified 64-cell serpentine Hamiltonian path for [puzzle8x8Valid].
     */
    val solution8x8Route: PuzzlePath = PuzzlePath.of(
        // Row 0
        GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3), GridPosition(0, 4), GridPosition(0, 5), GridPosition(0, 6), GridPosition(0, 7),
        // Row 1
        GridPosition(1, 7), GridPosition(1, 6), GridPosition(1, 5), GridPosition(1, 4), GridPosition(1, 3), GridPosition(1, 2), GridPosition(1, 1), GridPosition(1, 0),
        // Row 2
        GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4), GridPosition(2, 5), GridPosition(2, 6), GridPosition(2, 7),
        // Row 3
        GridPosition(3, 7), GridPosition(3, 6), GridPosition(3, 5), GridPosition(3, 4), GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
        // Row 4
        GridPosition(4, 0), GridPosition(4, 1), GridPosition(4, 2), GridPosition(4, 3), GridPosition(4, 4), GridPosition(4, 5), GridPosition(4, 6), GridPosition(4, 7),
        // Row 5
        GridPosition(5, 7), GridPosition(5, 6), GridPosition(5, 5), GridPosition(5, 4), GridPosition(5, 3), GridPosition(5, 2), GridPosition(5, 1), GridPosition(5, 0),
        // Row 6
        GridPosition(6, 0), GridPosition(6, 1), GridPosition(6, 2), GridPosition(6, 3), GridPosition(6, 4), GridPosition(6, 5), GridPosition(6, 6), GridPosition(6, 7),
        // Row 7
        GridPosition(7, 7), GridPosition(7, 6), GridPosition(7, 5), GridPosition(7, 4), GridPosition(7, 3), GridPosition(7, 2), GridPosition(7, 1), GridPosition(7, 0)
    )
}

