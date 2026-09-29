package com.zynpath.game.core.puzzle.solver

/**
 * Diagnostic metrics and performance statistics collected during a puzzle solver run.
 *
 * @property nodesExplored Total number of recursive search state transitions evaluated.
 * @property backtracks Total number of times search failed at a node and stepped back.
 * @property prunedBranches Total number of branches pruned prior to full exploration by pruning rules.
 * @property elapsedMs Monotonic wall-clock execution time in milliseconds.
 */
data class SolverStatistics(
    val nodesExplored: Long,
    val backtracks: Long,
    val prunedBranches: Long,
    val elapsedMs: Long
) {
    val nodesPerMillisecond: Double
        get() = if (elapsedMs > 0) nodesExplored.toDouble() / elapsedMs else 0.0

    val elapsedTimeMs: Long
        get() = elapsedMs

    companion object {
        val EMPTY = SolverStatistics(0L, 0L, 0L, 0L)
    }
}
