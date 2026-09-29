package com.zynpath.game.core.puzzle.model

/**
 * Solver-verified tutorial puzzles for interactive onboarding stages.
 * Every stage is governed by the authoritative [com.zynpath.game.core.puzzle.engine.PuzzleEngine]
 * and obeys the non-negotiable Zynpath puzzle rules.
 */
object TutorialPuzzles {

    const val TUTORIAL_CONTENT_VERSION = 1

    /**
     * Stage 1: "Start at Checkpoint 1"
     * 3x3 grid, checkpoints at (0,0)=1, (0,2)=2, (2,2)=3.
     * Teaches touching checkpoint 1 to initiate the path.
     */
    val Stage1_StartOne: PuzzleDefinition by lazy {
        PuzzleDefinition.standard(
            puzzleId = "tutorial_stage_1",
            dimensions = GridDimensions(3, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 2)),
                NumberedCheckpoint(3, GridPosition(2, 2))
            )
        )
    }

    /**
     * Stage 2: "Continuous Orthogonal Path"
     * 3x3 grid. Path starts at (0,0) [1].
     * Goal: extend horizontally to (0,1) and (0,2) [2].
     * Diagonal moves (e.g. to (1,1)) are rejected with NON_ADJACENT.
     */
    val Stage2_OrthogonalPath: PuzzleDefinition by lazy {
        Stage1_StartOne.copy(puzzleId = "tutorial_stage_2")
    }

    /**
     * Stage 3: "Ascending Checkpoint Order"
     * 3x3 grid. Checkpoints 1, 2, 3.
     * Attempting to jump directly to checkpoint 3 before checkpoint 2 triggers WRONG_CHECKPOINT_ORDER.
     */
    val Stage3_CheckpointOrder: PuzzleDefinition by lazy {
        Stage1_StartOne.copy(puzzleId = "tutorial_stage_3")
    }

    /**
     * Stage 4: "Full-Grid Coverage"
     * 2x3 grid (6 cells total).
     * Checkpoints: #1 at (0,0), #2 at (0,2), #3 (Final) at (1,0).
     * Solution path: (0,0) -> (0,1) -> (0,2) -> (1,2) -> (1,1) -> (1,0) (6 cells).
     * Attempting to connect directly from (0,0) or (0,2) or (1,2) into final checkpoint (1,0)
     * while (1,1) or other cells remain empty triggers PREMATURE_FINAL_CHECKPOINT.
     */
    val Stage4_FullCoverage: PuzzleDefinition by lazy {
        PuzzleDefinition.standard(
            puzzleId = "tutorial_stage_4",
            dimensions = GridDimensions(2, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 2)),
                NumberedCheckpoint(3, GridPosition(1, 0))
            )
        )
    }

    /**
     * Stage 5: "Blocked-Edge Walls"
     * 2x2 grid (4 cells total).
     * Checkpoints: #1 at (0,0), #2 (Final) at (1,0).
     * Wall edge between (0,0) and (1,0).
     * Both (0,0) and (1,0) are playable cells, but the edge between them is blocked!
     * Direct movement from (0,0) to (1,0) triggers BLOCKED_BY_WALL.
     * The player must route around: (0,0) -> (0,1) -> (1,1) -> (1,0).
     */
    val Stage5_Walls: PuzzleDefinition by lazy {
        PuzzleDefinition.standard(
            puzzleId = "tutorial_stage_5",
            dimensions = GridDimensions(2, 2),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0))
            )
        )
    }

    /**
     * Stage 6: "Mistakes & Recovery"
     * Same 2x2 grid with wall.
     * Teaches Undo and Reset buttons to recover from dead ends or mistakes.
     */
    val Stage6_Recovery: PuzzleDefinition by lazy {
        Stage5_Walls.copy(puzzleId = "tutorial_stage_6")
    }

    /**
     * Stage 7: "Complete Full Puzzle"
     * 3x3 grid (9 cells total) with 4 checkpoints and 2 walls.
     * Matches the canonical solver-verified Sample 3x3:
     * Checkpoints: (0,0)=1, (0,2)=2, (1,0)=3, (2,2)=4.
     * Walls: between (0,0) & (1,0), and between (1,1) & (2,1).
     * Verified unique solution:
     * (0,0) -> (0,1) -> (0,2) -> (1,2) -> (1,1) -> (1,0) -> (2,0) -> (2,1) -> (2,2).
     */
    val Stage7_FullPuzzle: PuzzleDefinition by lazy {
        PuzzleDefinition.standard(
            puzzleId = "tutorial_stage_7",
            dimensions = GridDimensions(3, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 2)),
                NumberedCheckpoint(3, GridPosition(1, 0)),
                NumberedCheckpoint(4, GridPosition(2, 2))
            ),
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 0)),
                BlockedEdge.between(GridPosition(1, 1), GridPosition(2, 1))
            )
        )
    }

    fun getStageDefinition(stageNumber: Int): PuzzleDefinition {
        return when (stageNumber) {
            1 -> Stage1_StartOne
            2 -> Stage2_OrthogonalPath
            3 -> Stage3_CheckpointOrder
            4 -> Stage4_FullCoverage
            5 -> Stage5_Walls
            6 -> Stage6_Recovery
            7 -> Stage7_FullPuzzle
            else -> Stage7_FullPuzzle
        }
    }
}
