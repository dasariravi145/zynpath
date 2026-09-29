package com.zynpath.game.core.puzzle.hint

/**
 * Configuration parameters and search budgets for [PuzzleHintEngine].
 *
 * Implements Prompt 14 Section 20: Configurable solver budgets and cancellation.
 *
 * @property nodeLimit Maximum DFS recursive steps before terminating hint search.
 * @property timeBudgetMs Monotonic elapsed time limit in milliseconds for forward hint search.
 * @property recoveryNodeLimit Node budget per prefix during dead-end recovery analysis.
 * @property recoveryTimeBudgetMs Time limit in milliseconds per prefix during dead-end recovery analysis.
 * @property maxPrefixesToAnalyze Maximum number of earlier path prefixes to test during recovery guidance.
 * @property enableCache Whether to utilize the verified solution cache.
 * @property cancellationSignal Periodic callback polled to abort long-running searches cooperatively.
 */
data class HintConfiguration(
    val nodeLimit: Long = 100_000L,
    val timeBudgetMs: Long = 3_000L,
    val recoveryNodeLimit: Long = 25_000L,
    val recoveryTimeBudgetMs: Long = 1_500L,
    val maxPrefixesToAnalyze: Int = 15,
    val enableCache: Boolean = true,
    val cancellationSignal: () -> Boolean = { false }
) {
    init {
        require(nodeLimit >= 1L) { "nodeLimit must be >= 1" }
        require(timeBudgetMs >= 1L) { "timeBudgetMs must be >= 1" }
        require(recoveryNodeLimit >= 1L) { "recoveryNodeLimit must be >= 1" }
        require(recoveryTimeBudgetMs >= 1L) { "recoveryTimeBudgetMs must be >= 1" }
        require(maxPrefixesToAnalyze >= 1) { "maxPrefixesToAnalyze must be >= 1" }
    }

    companion object {
        val DEFAULT = HintConfiguration()

        /** Fast budget for responsive interactive tests. */
        val FAST = HintConfiguration(
            nodeLimit = 25_000L,
            timeBudgetMs = 1_000L,
            recoveryNodeLimit = 10_000L,
            recoveryTimeBudgetMs = 500L,
            maxPrefixesToAnalyze = 5
        )
    }
}
