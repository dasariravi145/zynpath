package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.catalog.CuratedAnchorLevels
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for stable, deterministic level retrieval across all 300 levels.
 *
 * Implements Prompt 26 Tasks 8 & 9 and Prompt 29 Tasks 9, 10, & 17:
 * - Prioritizes verified authored levels from [PackagedPuzzles] (1–50, 51–53)
 *   and chapter anchor milestones from [CuratedAnchorLevels] (100, 101, 150, 151, 200, 201, 250, 251, 300).
 * - Deterministically generates and caches un-authored levels on demand via [ChapterLevelGenerator].
 * - Guarantees identical puzzle definitions across app launches and screen recompositions.
 * - Guarantees world-accurate fallback matching grid dimensions and constraints if generation is bounded.
 */
interface DeterministicLevelProvider {
    fun getLevel(levelId: Int): PuzzleDefinition?
    fun getSolution(levelId: Int): PuzzlePath?
    fun isLevelPackaged(levelId: Int): Boolean
}

@Singleton
class DeterministicLevelProviderImpl(
    private val chapterGenerator: ChapterLevelGenerator
) : DeterministicLevelProvider {

    @Inject
    constructor() : this(ChapterLevelGenerator())

    private val puzzleCache = ConcurrentHashMap<Int, PuzzleDefinition>()
    private val solutionCache = ConcurrentHashMap<Int, PuzzlePath>()

    init {
        // Pre-populate with pre-packaged authored catalog puzzles
        for ((lvlId, def) in PackagedPuzzles.ALL_PACKAGED) {
            puzzleCache[lvlId] = def
        }
        for ((lvlId, sol) in PackagedPuzzles.ALL_SOLUTIONS) {
            solutionCache[lvlId] = sol
        }
        // Pre-populate curated first 50 levels
        for ((lvlId, def) in com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels.LEVELS) {
            puzzleCache[lvlId] = def
        }
        for ((lvlId, sol) in com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels.SOLUTIONS) {
            solutionCache[lvlId] = sol
        }
        // Pre-populate curated anchor and milestone puzzles
        for ((lvlId, def) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            puzzleCache[lvlId] = def
        }
        for ((lvlId, sol) in CuratedAnchorLevels.ANCHOR_SOLUTIONS) {
            solutionCache[lvlId] = sol
        }
    }

    override fun getLevel(levelId: Int): PuzzleDefinition? {
        if (levelId !in 1..300) return null

        puzzleCache[levelId]?.let { return it }

        // Check curated anchors
        CuratedAnchorLevels.ANCHOR_LEVELS[levelId]?.let { anchorDef ->
            puzzleCache[levelId] = anchorDef
            CuratedAnchorLevels.ANCHOR_SOLUTIONS[levelId]?.let { solutionCache[levelId] = it }
            return anchorDef
        }

        // Generate deterministically on demand
        synchronized(this) {
            puzzleCache[levelId]?.let { return it }

            val result = chapterGenerator.generateLevel(levelId)
            if (result is ChapterLevelGenerationResult.Success) {
                puzzleCache[levelId] = result.puzzle
                solutionCache[levelId] = result.solution
                return result.puzzle
            }

            // Bounded fallback to world-accurate representative puzzle matching grid dimensions
            val fallback = CuratedAnchorLevels.getFallbackForLevel(levelId)
            val fallbackSol = CuratedAnchorLevels.getFallbackSolutionForLevel(levelId)
            puzzleCache[levelId] = fallback
            solutionCache[levelId] = fallbackSol
            return fallback
        }
    }

    override fun getSolution(levelId: Int): PuzzlePath? {
        if (levelId !in 1..300) return null
        solutionCache[levelId]?.let { return it }
        CuratedAnchorLevels.ANCHOR_SOLUTIONS[levelId]?.let {
            solutionCache[levelId] = it
            return it
        }
        // Trigger load/generate if not yet cached
        getLevel(levelId)
        return solutionCache[levelId]
    }

    override fun isLevelPackaged(levelId: Int): Boolean {
        return PackagedPuzzles.ALL_PACKAGED.containsKey(levelId) ||
                CuratedAnchorLevels.ANCHOR_LEVELS.containsKey(levelId)
    }
}
