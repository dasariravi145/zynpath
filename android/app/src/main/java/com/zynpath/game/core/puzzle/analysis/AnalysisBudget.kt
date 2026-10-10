package com.zynpath.game.core.puzzle.analysis

/**
 * Deterministic analysis budget controlling structural, lookahead, and topological reasoning complexity.
 *
 * Implements Prompt 2A Sections 3, 10, 16, and 17:
 * Limits branch simulation depth, wrong-branch sampling count, and execution time to prevent
 * combinatorially expensive analysis loops on large boards (e.g. 8x8, 9x9, 10x10).
 *
 * @property maxAnalysisBranches Maximum candidate branching points along the path to sample.
 * @property maxWrongBranchSamples Maximum wrong branches to simulate forward.
 * @property maxLookaheadDepth Maximum step depth for simulating a single wrong branch continuation.
 * @property timeBudgetMs Monotonic wall-clock safety timeout in milliseconds for the complete analysis.
 * @property cancellationSignal Cooperative cancellation callback polled during analysis.
 */
data class AnalysisBudget(
    val maxAnalysisBranches: Int = 40,
    val maxWrongBranchSamples: Int = 30,
    val maxLookaheadDepth: Int = 20,
    val timeBudgetMs: Long = 3000L,
    val cancellationSignal: () -> Boolean = { false }
) {
    init {
        require(maxAnalysisBranches >= 1) { "maxAnalysisBranches must be >= 1, but was $maxAnalysisBranches" }
        require(maxWrongBranchSamples >= 1) { "maxWrongBranchSamples must be >= 1, but was $maxWrongBranchSamples" }
        require(maxLookaheadDepth >= 1) { "maxLookaheadDepth must be >= 1, but was $maxLookaheadDepth" }
        require(timeBudgetMs >= 1L) { "timeBudgetMs must be >= 1, but was $timeBudgetMs" }
    }

    companion object {
        /** Default balanced analysis budget. */
        val DEFAULT = AnalysisBudget()

        /** Fast budget for quick screening and unit tests. */
        val FAST = AnalysisBudget(
            maxAnalysisBranches = 20,
            maxWrongBranchSamples = 15,
            maxLookaheadDepth = 10,
            timeBudgetMs = 1000L
        )

        /** Thorough budget for deep offline puzzle certification. */
        val THOROUGH = AnalysisBudget(
            maxAnalysisBranches = 60,
            maxWrongBranchSamples = 50,
            maxLookaheadDepth = 30,
            timeBudgetMs = 5000L
        )
    }
}
