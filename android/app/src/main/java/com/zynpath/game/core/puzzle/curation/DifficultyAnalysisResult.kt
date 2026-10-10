package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Result of comprehensive puzzle difficulty analysis.
 *
 * Implements Prompt 10 Sections 6, 16, 17, and 18:
 * - Deterministic composite difficulty estimate [0.0, 1.0].
 * - Coarse [DifficultyBand] classification.
 * - Granular [DifficultyMetrics] breakdown.
 * - Explicitly labeled as provisional algorithmic estimate (not empirically player-calibrated).
 */
data class DifficultyAnalysisResult(
    val puzzleId: String,
    val estimatedScore: Double,
    val difficultyBand: DifficultyBand,
    val metrics: DifficultyMetrics,
    val componentScores: Map<String, Double>,
    val isProvisional: Boolean = true,
    val configurationVersion: String = DifficultyAnalysisConfiguration.CONFIG_VERSION,
    val analyzerVersion: String = DifficultyAnalysisConfiguration.ANALYZER_VERSION
) {
    val uniquenessStatus: UniquenessStatus
        get() = metrics.uniquenessStatus

    /**
     * Authoritative difficulty analysis representation adhering to Master Redesign Section 18.
     */
    val difficultyAnalysis: DifficultyAnalysis by lazy {
        DifficultyAnalysis(
            gridSize = com.zynpath.game.core.puzzle.model.GridDimensions(metrics.rows, metrics.columns),
            cellCount = metrics.totalRequiredCells,
            checkpointCount = metrics.checkpointCount,
            decisionPointCount = metrics.meaningfulRouteDecisions,
            meaningfulDecisionPointCount = metrics.meaningfulDecisionPointCount,
            decisionDepth = metrics.decisionDepth,
            decisionChainDepth = metrics.decisionChainDepth,
            forcedMoveRatio = metrics.forcedMoveRatio,
            plausibleWrongBranchCount = metrics.plausibleIncorrectPathsCount,
            averageWrongBranchDepth = metrics.averageWrongBranchDepth,
            maximumWrongBranchDepth = metrics.maxWrongBranchDepth,
            wrongBranchCheckpointDepth = metrics.wrongBranchCheckpointDepth,
            wrongBranchImpact = metrics.wrongBranchImpact,
            futureCellDependencyScore = metrics.futureCellDependencyScore,
            futureDependencyDistance = metrics.futureDependencyDistance,
            multiDependencyDecisionCount = metrics.multiDependencyDecisionCount,
            dependencyChainDepth = metrics.dependencyChainDepth,
            cellReservationScore = metrics.cellReservationScore,
            reservationDuration = metrics.reservationDuration,
            reservationImportance = metrics.reservationImportance,
            checkpointDeceptionScore = metrics.checkpointDeceptionScore,
            regionPlanningScore = metrics.regionPlanningScore,
            globalPlanningScore = metrics.globalPlanningScore,
            criticalConnectorCount = metrics.criticalConnectorCount,
            regionDependencyCount = metrics.regionDependencyCount,
            selfCutRisk = metrics.selfCutRisk,
            solverSearchNodes = metrics.solverNodesExplored,
            solverBacktracks = metrics.solverBacktracks,
            solverDeadEnds = metrics.solverDeadEnds,
            solutionClassification = when (metrics.uniquenessStatus) {
                UniquenessStatus.UNIQUE -> com.zynpath.game.core.puzzle.solver.SolutionClassification.UNIQUE
                UniquenessStatus.NON_UNIQUE -> com.zynpath.game.core.puzzle.solver.SolutionClassification.MULTIPLE
                UniquenessStatus.UNSOLVABLE -> com.zynpath.game.core.puzzle.solver.SolutionClassification.NONE
                UniquenessStatus.UNKNOWN -> com.zynpath.game.core.puzzle.solver.SolutionClassification.UNKNOWN_LIMIT_REACHED
            },
            difficultyTier = difficultyBand
        )
    }

    /**
     * User-facing presentation summary without exposing internal solver search internals.
     */
    fun toPresentationSummary(): String {
        return "${difficultyBand.displayName} (Rating: ${String.format(java.util.Locale.US, "%.2f", estimatedScore)})"
    }
}
