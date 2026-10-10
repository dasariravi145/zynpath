package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.analysis.AdvancedReasoningAnalysisResult
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.SimpleStrategyEvaluation
import com.zynpath.game.core.puzzle.solver.SolverResult
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe, bounded memory cache for solver and difficulty analysis results.
 *
 * Implements Prompt 2A Section 7:
 * Memoizes results for identical immutable puzzle definitions during one generation/audit execution.
 * Cache key includes puzzleId, puzzleVersion, and structural content fingerprint.
 *
 * @param maxEntries Maximum total entries allowed across all cache buckets to prevent unbounded memory growth.
 */
class PuzzleAnalysisCache(
    private val maxEntries: Int = 1000
) {
    private val solverCache = ConcurrentHashMap<String, SolverResult>()
    private val difficultyCache = ConcurrentHashMap<String, DifficultyAnalysisResult>()
    private val simpleStrategyCache = ConcurrentHashMap<String, SimpleStrategyEvaluation>()
    private val advancedReasoningCache = ConcurrentHashMap<String, AdvancedReasoningAnalysisResult>()

    /**
     * Computes a canonical, versioned cache key for [definition].
     */
    fun cacheKey(definition: PuzzleDefinition, suffix: String = ""): String {
        val fp = PuzzleFingerprint.canonicalRepresentation(definition)
        val hash = fp.hashCode().toString(16)
        return "${definition.puzzleId}_v${definition.puzzleVersion}_${hash}_$suffix"
    }

    fun getSolverResult(key: String): SolverResult? = solverCache[key]
    fun putSolverResult(key: String, result: SolverResult) {
        if (solverCache.size < maxEntries) solverCache[key] = result
    }

    fun getDifficultyResult(key: String): DifficultyAnalysisResult? = difficultyCache[key]
    fun putDifficultyResult(key: String, result: DifficultyAnalysisResult) {
        if (difficultyCache.size < maxEntries) difficultyCache[key] = result
    }

    fun getSimpleStrategyResult(key: String): SimpleStrategyEvaluation? = simpleStrategyCache[key]
    fun putSimpleStrategyResult(key: String, result: SimpleStrategyEvaluation) {
        if (simpleStrategyCache.size < maxEntries) simpleStrategyCache[key] = result
    }

    fun getAdvancedReasoningResult(key: String): AdvancedReasoningAnalysisResult? = advancedReasoningCache[key]
    fun putAdvancedReasoningResult(key: String, result: AdvancedReasoningAnalysisResult) {
        if (advancedReasoningCache.size < maxEntries) advancedReasoningCache[key] = result
    }

    fun clear() {
        solverCache.clear()
        difficultyCache.clear()
        simpleStrategyCache.clear()
        advancedReasoningCache.clear()
    }

    val totalEntries: Int
        get() = solverCache.size + difficultyCache.size + simpleStrategyCache.size + advancedReasoningCache.size

    companion object {
        /** Global shared analysis cache. */
        val SHARED = PuzzleAnalysisCache()
    }
}
