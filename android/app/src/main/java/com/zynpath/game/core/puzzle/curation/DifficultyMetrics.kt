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
    val solutionCount: Int
)
