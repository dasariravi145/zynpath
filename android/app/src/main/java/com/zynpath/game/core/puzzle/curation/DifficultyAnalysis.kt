package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.solver.SolutionClassification

/**
 * Authoritative difficulty analysis representation for Zynpath logic puzzles.
 *
 * Implements Master Redesign Section 18:
 * Contains measured (never fabricated) topological, look-ahead, branching,
 * and solver search metrics.
 */
data class DifficultyAnalysis(
    val gridSize: GridDimensions,
    val cellCount: Int,
    val checkpointCount: Int,
    val decisionPointCount: Int,
    val meaningfulDecisionPointCount: Int = decisionPointCount,
    val decisionDepth: Int = 1,
    val decisionChainDepth: Int = decisionDepth,
    val forcedMoveRatio: Double,
    val plausibleWrongBranchCount: Int,
    val averageWrongBranchDepth: Double,
    val maximumWrongBranchDepth: Int,
    val wrongBranchCheckpointDepth: Int = 0,
    val wrongBranchImpact: Double = 0.0,
    val futureCellDependencyScore: Double,
    val futureDependencyDistance: Double = 0.0,
    val multiDependencyDecisionCount: Int = 0,
    val dependencyChainDepth: Int = 0,
    val cellReservationScore: Double,
    val reservationDuration: Double = 0.0,
    val reservationImportance: Double = 0.0,
    val checkpointDeceptionScore: Double,
    val regionPlanningScore: Double,
    val globalPlanningScore: Double = 0.0,
    val criticalConnectorCount: Int = 0,
    val regionDependencyCount: Int = 0,
    val selfCutRisk: Double = 0.0,
    val solverSearchNodes: Long,
    val solverBacktracks: Long,
    val solverDeadEnds: Long = 0L,
    val solutionClassification: SolutionClassification,
    val difficultyTier: DifficultyBand,
    val strategicAmbiguityScore: Double = (
        (meaningfulDecisionPointCount * 0.05) +
        (plausibleWrongBranchCount * 0.05) +
        (checkpointDeceptionScore * 0.3) +
        (futureCellDependencyScore * 0.3) +
        (cellReservationScore * 0.3)
    ).coerceIn(0.0, 1.0),
    val hintValueScore: Double = (
        (strategicAmbiguityScore * 0.5) +
        ((1.0 - forcedMoveRatio) * 0.3) +
        (checkpointDeceptionScore * 0.2)
    ).coerceIn(0.0, 1.0),
    val puzzleInterestScore: Double = (
        (strategicAmbiguityScore * 0.4) +
        (cellReservationScore * 0.2) +
        (futureCellDependencyScore * 0.2) +
        (meaningfulDecisionPointCount.coerceAtMost(10) / 10.0 * 0.2)
    ).coerceIn(0.0, 1.0),
    val visualProximityTrapCount: Int = if (checkpointDeceptionScore > 0.25) ((checkpointDeceptionScore * checkpointCount).toInt().coerceAtLeast(1)) else 0
) {
    init {
        require(cellCount == gridSize.totalCells) {
            "cellCount ($cellCount) must equal gridSize totalCells (${gridSize.totalCells})"
        }
        require(forcedMoveRatio in 0.0..1.0) {
            "forcedMoveRatio ($forcedMoveRatio) must be between 0.0 and 1.0"
        }
    }
}
