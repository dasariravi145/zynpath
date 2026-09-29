package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Detailed outcome of a level curation run.
 *
 * Implements Prompt 10 Section 29:
 * Provides structured telemetry on accepted levels, candidate rejection statistics,
 * failure diagnostics, and versioned engine provenance.
 */
data class CurationResult(
    val acceptedLevels: List<CuratedLevel>,
    val totalCandidatesEvaluated: Int,
    val rejectedCandidateCount: Int,
    val rejectionReasonCounts: Map<QualityRejectionReason, Int>,
    val rejectionDiagnostics: List<String>,
    val difficultyMetrics: Map<Int, DifficultyMetrics>,
    val qualityScores: Map<Int, Double>,
    val uniquenessStatuses: Map<Int, UniquenessStatus>,
    val fingerprints: Map<Int, String>,
    val generatorVersion: String,
    val analyzerVersion: String,
    val solverVersion: String,
    val curationConfigVersion: String = CurationConfiguration.CURATION_VERSION
) {
    val acceptedCount: Int
        get() = acceptedLevels.size

    val acceptanceRate: Double
        get() = if (totalCandidatesEvaluated > 0) acceptedLevels.size.toDouble() / totalCandidatesEvaluated else 0.0
}
