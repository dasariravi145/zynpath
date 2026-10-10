package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Objective structural, route, wall, and solver metrics evaluating a puzzle definition.
 *
 * Implements Prompt 10 Sections 8, 9, 10, 11, 12, and 13:
 * - Structural & Planar Graph Topology metrics.
 * - Checkpoint Spacing & Distribution metrics.
 * - Route Traversal Complexity metrics.
 * - Wall Placement & Graph Constriction metrics.
 * - Exact Solver Search Behavior telemetry.
 */
data class DifficultyMetrics(
    // 1. Structural & Graph Metrics
    val rows: Int,
    val columns: Int,
    val totalRequiredCells: Int,
    val checkpointCount: Int,
    val wallCount: Int,
    val traversableEdgeCount: Int,
    val averageTraversableDegree: Double,
    val constrainedCellCount: Int, // Cells with traversable degree == 2 (mandatory pass-through)
    val branchCellCount: Int,      // Cells with traversable degree >= 3 (branching choices)
    val deadEndCellCount: Int,     // Cells with traversable degree <= 1 (excluding start/end)

    // 2. Checkpoint Distribution Metrics
    val checkpointGaps: List<Int>,
    val minCheckpointGap: Int,
    val maxCheckpointGap: Int,
    val avgCheckpointGap: Double,
    val checkpointGapVariance: Double,
    val maxUnnumberedStretchRatio: Double, // maxCheckpointGap / totalRequiredCells

    // 3. Route Complexity Metrics
    val pathLength: Int,
    val turnCount: Int,
    val turnFrequency: Double,     // turnCount / (pathLength - 2)
    val longestStraightSegment: Int,
    val straightSegmentCount: Int,
    val horizontalMoveCount: Int,
    val verticalMoveCount: Int,

    // 4. Wall Complexity Metrics
    val wallDensity: Double,       // wallCount / maxPossibleNonPathEdges
    val wallConstrainedCells: Int, // Cells whose original grid degree was reduced by walls

    // 5. Solver Telemetry Metrics
    val solverNodesExplored: Long,
    val solverBacktracks: Long,
    val solverPrunedBranches: Long,
    val uniquenessStatus: UniquenessStatus,
    val solutionCount: Int,

    // 6. Section 2 Advanced Reasoning & Look-Ahead Metrics
    val meaningfulRouteDecisions: Int = 0,
    val forcedMovesCount: Int = 0,
    val optionalMovesCount: Int = 0,
    val requiredLookAheadDepth: Int = 0,
    val plausibleIncorrectPathsCount: Int = 0,
    val remainingCellIsolationRisks: Int = 0,
    val maxSimilarityToPrevious: Double = 0.0,

    // 7. Genuine Thinking Redesign & Simple Strategy Baseline
    val simpleStrategySolved: Boolean = false,
    val simpleStrategyName: String? = null,
    val deceptiveCheckpointDistances: Int = 0,

    // 8. Final Deep-Reasoning Redesign Metrics (Section 19)
    val verifiedCoveredCells: Int = totalRequiredCells,
    val wallPositions: List<String> = emptyList(),
    val forcedMoveRatio: Double = 0.0,
    val maxWrongBranchDepth: Int = requiredLookAheadDepth,
    val averageWrongBranchDepth: Double = 0.0,
    val futureCellDependency: Int = 0,
    val cellReservationRequirement: Int = 0,
    val checkpointDeceptionScore: Double = 0.0,
    val longRangeDependencyCount: Int = 0,
    val backtrackingRequirement: Int = 0,
    val structuralFingerprint: String = "",
    val simpleStrategyDetails: String = "",

    // 9. Master Redesign Part 2 Advanced Metrics
    val meaningfulDecisionPointCount: Int = meaningfulRouteDecisions,
    val decisionDepth: Int = 1,
    val wrongBranchCheckpointDepth: Int = 0,
    val futureDependencyDistance: Double = 0.0,
    val criticalConnectorCount: Int = 0,
    val regionDependencyCount: Int = 0,
    val solverDeadEnds: Long = 0L,
    val regionPlanningScore: Double = 0.0,
    val cellReservationScore: Double = cellReservationRequirement.toDouble(),
    val futureCellDependencyScore: Double = futureCellDependency.toDouble(),

    // 10. Master Redesign Part 3 Metrics (World 5 & 6)
    val decisionChainDepth: Int = decisionDepth,
    val wrongBranchImpact: Double = 0.0,
    val multiDependencyDecisionCount: Int = 0,
    val dependencyChainDepth: Int = 0,
    val reservationDuration: Double = 0.0,
    val reservationImportance: Double = 0.0,
    val globalPlanningScore: Double = 0.0,
    val selfCutRisk: Double = 0.0
)

