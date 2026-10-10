package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld4Levels {
    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map.putAll(CuratedWorld4PartA.LEVELS)
        map.putAll(CuratedWorld4PartB.LEVELS)
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map.putAll(CuratedWorld4PartA.SOLUTIONS)
        map.putAll(CuratedWorld4PartB.SOLUTIONS)
        map
    }

    fun getLevel(levelId: Int): PuzzleDefinition? = LEVELS[levelId]
    fun getSolution(levelId: Int): PuzzlePath? = SOLUTIONS[levelId]
}
