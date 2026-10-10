package com.zynpath.game.core.puzzle.solver

/**
 * Deterministic search budget parameters for puzzle solving and uniqueness verification.
 *
 * Implements Prompt 2A Sections 2, 3, 4, and 5:
 * Enforces strict deterministic node and backtrack limits to eliminate unbounded DFS exploration.
 *
 * @property maxSearchNodes Maximum number of search node expansions before search limit is reached.
 * @property maxBacktracks Maximum number of backtracks allowed before aborting search.
 * @property maxSolutionsToFind Maximum distinct solutions to discover (1 for solvability, 2 for uniqueness).
 * @property timeBudgetMs Monotonic wall-clock safety timeout in milliseconds.
 * @property enablePruning Whether sound mathematical pruning (connectivity, degrees, reachability) is active.
 */
data class SearchBudget(
    val maxSearchNodes: Long = 1_000_000L,
    val maxBacktracks: Long = Long.MAX_VALUE,
    val maxSolutionsToFind: Int = 1,
    val timeBudgetMs: Long = 10_000L,
    val enablePruning: Boolean = true
) {
    init {
        require(maxSearchNodes >= 1L) { "maxSearchNodes must be >= 1, but was $maxSearchNodes" }
        require(maxBacktracks >= 1L) { "maxBacktracks must be >= 1, but was $maxBacktracks" }
        require(maxSolutionsToFind >= 1) { "maxSolutionsToFind must be >= 1, but was $maxSolutionsToFind" }
        require(timeBudgetMs >= 1L) { "timeBudgetMs must be >= 1, but was $timeBudgetMs" }
    }

    /**
     * Converts this search budget into a [SolverConfiguration].
     */
    fun toSolverConfiguration(cancellationSignal: () -> Boolean = { false }): SolverConfiguration =
        SolverConfiguration(
            maxSolutions = maxSolutionsToFind,
            nodeLimit = maxSearchNodes,
            maxBacktracks = maxBacktracks,
            timeBudgetMs = timeBudgetMs,
            enablePruning = enablePruning,
            cancellationSignal = cancellationSignal
        )

    companion object {
        /** Default search budget stopping at the first valid solution found. */
        val DEFAULT = SearchBudget()

        /** Stop as soon as the first valid solution is found. */
        val FIRST_SOLUTION = SearchBudget(maxSolutionsToFind = 1)

        /** Search for up to 2 solutions to prove or disprove uniqueness with standard bounds. */
        val CHECK_UNIQUENESS = SearchBudget(
            maxSolutionsToFind = 2,
            maxSearchNodes = 1_000_000L,
            maxBacktracks = 500_000L,
            timeBudgetMs = 10_000L
        )

        /** Fast budget for interactive or generator candidate screening. */
        val FAST_CHECK = SearchBudget(
            maxSolutionsToFind = 1,
            maxSearchNodes = 100_000L,
            maxBacktracks = 50_000L,
            timeBudgetMs = 2_000L
        )

        /** Strict bounded candidate screening budget for generator loops. */
        val CANDIDATE_SCREENING = SearchBudget(
            maxSolutionsToFind = 2,
            maxSearchNodes = 50_000L,
            maxBacktracks = 25_000L,
            timeBudgetMs = 1_500L
        )

        /** Highly constrained budget for fast deterministic diagnostic checks. */
        val STRICT_BOUNDED = SearchBudget(
            maxSolutionsToFind = 2,
            maxSearchNodes = 25_000L,
            maxBacktracks = 10_000L,
            timeBudgetMs = 1_000L
        )
    }
}
