package com.zynpath.game.core.puzzle.generator

/**
 * Objective metrics and diagnostic telemetry captured during a puzzle generation session.
 *
 * Adheres to Prompt 9 Sections 26 and 34:
 * Records attempt counts, solver search nodes, execution times, route topology features,
 * and rejection reason tallies for performance monitoring and future difficulty calibration.
 */
data class GenerationStatistics(
    val totalAttempts: Int = 0,
    val acceptedCandidates: Int = 0,
    val rejectedCandidates: Int = 0,
    val rejectionReasons: Map<String, Int> = emptyMap(),
    val solverNodesExplored: Long = 0L,
    val solverElapsedMs: Long = 0L,
    val generationElapsedMs: Long = 0L,
    val routeTurns: Int = 0,
    val wallCount: Int = 0,
    val checkpointCount: Int = 0,
    val requiredCellCount: Int = 0
) {
    companion object {
        val EMPTY = GenerationStatistics()
    }
}
