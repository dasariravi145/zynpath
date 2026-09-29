package com.zynpath.game.core.puzzle.solver

/**
 * Immutable configuration controlling search parameters, bounds, and heuristics for [PuzzleSolver].
 *
 * @property maxSolutions Maximum number of distinct valid solutions to discover before terminating.
 *                        Set to 1 to find the first solution; set to 2 to check uniqueness.
 * @property nodeLimit Maximum recursive search node explorations before aborting with [SolverStatus.SEARCH_LIMIT_REACHED].
 * @property timeBudgetMs Maximum monotonic elapsed execution time in milliseconds before aborting.
 * @property enablePruning Whether to apply sound mathematical pruning rules (reachability, degree, connectivity).
 * @property cancellationSignal Cooperative cancellation callback polled periodically during search.
 */
data class SolverConfiguration(
    val maxSolutions: Int = 1,
    val nodeLimit: Long = 1_000_000L,
    val timeBudgetMs: Long = 10_000L,
    val enablePruning: Boolean = true,
    val cancellationSignal: () -> Boolean = { false }
) {
    init {
        require(maxSolutions >= 1) { "maxSolutions must be >= 1, but was $maxSolutions" }
        require(nodeLimit >= 1L) { "nodeLimit must be >= 1, but was $nodeLimit" }
        require(timeBudgetMs >= 1L) { "timeBudgetMs must be >= 1, but was $timeBudgetMs" }
    }

    companion object {
        /** Default configuration stopping at the first valid solution found. */
        val DEFAULT = SolverConfiguration()

        /** Stop as soon as the first valid solution is found. */
        val FIRST_SOLUTION = SolverConfiguration(maxSolutions = 1)

        /** Search for up to 2 solutions to prove or disprove solution uniqueness. */
        val CHECK_UNIQUENESS = SolverConfiguration(maxSolutions = 2)

        /** Fast budget for interactive or generator candidate screening. */
        val FAST_CHECK = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = 100_000L,
            timeBudgetMs = 2_000L
        )

        /** Unconstrained budget for deep benchmarking and offline proof generation. */
        val UNCONSTRAINED_BENCHMARK = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = 10_000_000L,
            timeBudgetMs = 60_000L
        )

        /** Reference unpruned configuration used for correctness comparison testing. */
        val UNPRUNED_REFERENCE = SolverConfiguration(
            maxSolutions = 2,
            enablePruning = false,
            nodeLimit = 5_000_000L,
            timeBudgetMs = 30_000L
        )
    }
}
