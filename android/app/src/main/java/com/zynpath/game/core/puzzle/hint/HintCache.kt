package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Thread-safe, bounded memory in-process cache of verified full solutions.
 *
 * Implements Prompt 14 Section 21:
 * - Cache keys incorporate puzzle ID and puzzle version.
 * - Queries strictly verify that candidate cached solutions contain the exact current path prefix.
 * - Incompatible solutions are never reused.
 * - Bounded LRU memory eviction.
 */
class HintCache(private val maxEntries: Int = 50) {

    private val lock = Any()
    // LinkedHashMap with accessOrder = true for true LRU eviction
    private val cache = object : LinkedHashMap<String, ArrayList<PuzzlePath>>(maxEntries, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ArrayList<PuzzlePath>>?): Boolean {
            return size > maxEntries
        }
    }

    private fun buildKey(puzzleId: String, puzzleVersion: Int): String = "$puzzleId:$puzzleVersion"

    /**
     * Stores a verified full solution for the given puzzle ID and version.
     */
    fun put(puzzleId: String, puzzleVersion: Int, solution: PuzzlePath) {
        val key = buildKey(puzzleId, puzzleVersion)
        synchronized(lock) {
            val list = cache.getOrPut(key) { ArrayList(2) }
            if (!list.contains(solution)) {
                list.add(solution)
            }
        }
    }

    /**
     * Finds a verified full solution compatible with the player's [currentPath].
     * Returns null if no cached solution matches the exact path prefix.
     */
    fun findCompatibleSolution(
        puzzleId: String,
        puzzleVersion: Int,
        currentPath: List<GridPosition>
    ): PuzzlePath? {
        val key = buildKey(puzzleId, puzzleVersion)
        synchronized(lock) {
            val solutions = cache[key] ?: return null
            for (sol in solutions) {
                if (isPrefixMatch(currentPath, sol.positions)) {
                    return sol
                }
            }
        }
        return null
    }

    /**
     * Clears all cached solutions.
     */
    fun clear() {
        synchronized(lock) {
            cache.clear()
        }
    }

    private fun isPrefixMatch(prefix: List<GridPosition>, full: List<GridPosition>): Boolean {
        if (prefix.size > full.size) return false
        for (i in prefix.indices) {
            if (prefix[i] != full[i]) return false
        }
        return true
    }
}
