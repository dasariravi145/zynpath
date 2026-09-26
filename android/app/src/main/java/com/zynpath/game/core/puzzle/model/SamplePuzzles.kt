package com.zynpath.game.core.puzzle.model

/**
 * Verified deterministic sample puzzles for tutorials, previews, and unit test verification.
 * Every completed board is mathematically validated:
 * - Starts at 1
 * - Checkpoints visited in strictly ascending order
 * - Exactly one continuous orthogonal path
 * - Every cell covered once (no revisits)
 * - Zero wall collisions
 */
object SamplePuzzles {

    /**
     * Verified 3×3 puzzle board with 4 checkpoints and 2 walls.
     */
    val Sample3x3_Base: PuzzleBoardState by lazy {
        val checkpoints = mapOf(
            GridCoordinate(0, 0) to 1,
            GridCoordinate(0, 2) to 2,
            GridCoordinate(1, 0) to 3,
            GridCoordinate(2, 2) to 4
        )
        val walls = setOf(
            WallEdge.between(GridCoordinate(0, 0), GridCoordinate(1, 0)),
            WallEdge.between(GridCoordinate(1, 1), GridCoordinate(2, 1))
        )
        PuzzleBoardState(
            rowCount = 3,
            columnCount = 3,
            checkpoints = checkpoints,
            walls = walls,
            path = emptyList()
        )
    }

    /**
     * Verified full solution path for Sample 3×3.
     * Path: (0,0) -> (0,1) -> (0,2) -> (1,2) -> (1,1) -> (1,0) -> (2,0) -> (2,1) -> (2,2)
     */
    val Sample3x3_FullSolutionPath: List<GridCoordinate> = listOf(
        GridCoordinate(0, 0), // 1
        GridCoordinate(0, 1),
        GridCoordinate(0, 2), // 2
        GridCoordinate(1, 2),
        GridCoordinate(1, 1),
        GridCoordinate(1, 0), // 3
        GridCoordinate(2, 0),
        GridCoordinate(2, 1),
        GridCoordinate(2, 2)  // 4
    )

    /**
     * Completed 3×3 solved state.
     */
    val Sample3x3_Solved: PuzzleBoardState by lazy {
        Sample3x3_Base.copy(path = Sample3x3_FullSolutionPath)
    }

    /**
     * Step-by-step path progression for the 6-step tutorial.
     */
    fun getTutorialStepBoard(step: Int): PuzzleBoardState {
        val path = when (step) {
            1 -> listOf(Sample3x3_FullSolutionPath[0]) // Just start at 1
            2 -> Sample3x3_FullSolutionPath.take(3)     // Reaches checkpoint 2
            3 -> Sample3x3_FullSolutionPath.take(5)     // Continuous path through cell (1,1)
            4 -> Sample3x3_FullSolutionPath.take(6)     // Around wall to checkpoint 3
            5 -> Sample3x3_FullSolutionPath.take(8)     // Almost all cells covered
            6 -> Sample3x3_FullSolutionPath            // Fully solved (9/9 cells)
            else -> emptyList()
        }
        return Sample3x3_Base.copy(path = path)
    }

    /**
     * Verified 4×4 puzzle board (World 1 Level 1 template).
     * 16 cells, 5 checkpoints, 1 wall obstacle.
     */
    val Sample4x4_Base: PuzzleBoardState by lazy {
        val checkpoints = mapOf(
            GridCoordinate(0, 0) to 1,
            GridCoordinate(0, 3) to 2,
            GridCoordinate(1, 0) to 3,
            GridCoordinate(3, 3) to 4,
            GridCoordinate(3, 0) to 5
        )
        val walls = setOf(
            WallEdge.between(GridCoordinate(0, 1), GridCoordinate(1, 1))
        )
        PuzzleBoardState(
            rowCount = 4,
            columnCount = 4,
            checkpoints = checkpoints,
            walls = walls,
            path = emptyList()
        )
    }

    val Sample4x4_FullSolutionPath: List<GridCoordinate> = listOf(
        // Row 0
        GridCoordinate(0, 0), // 1
        GridCoordinate(0, 1),
        GridCoordinate(0, 2),
        GridCoordinate(0, 3), // 2
        // Row 1
        GridCoordinate(1, 3),
        GridCoordinate(1, 2),
        GridCoordinate(1, 1),
        GridCoordinate(1, 0), // 3
        // Row 2
        GridCoordinate(2, 0),
        GridCoordinate(2, 1),
        GridCoordinate(2, 2),
        GridCoordinate(2, 3),
        // Row 3
        GridCoordinate(3, 3), // 4
        GridCoordinate(3, 2),
        GridCoordinate(3, 1),
        GridCoordinate(3, 0)  // 5
    )

    val Sample4x4_Solved: PuzzleBoardState by lazy {
        Sample4x4_Base.copy(path = Sample4x4_FullSolutionPath)
    }
}
