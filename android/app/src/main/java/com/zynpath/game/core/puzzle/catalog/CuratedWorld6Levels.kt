package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6Levels {
    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map.putAll(CuratedWorld6PartA.LEVELS)
        map.putAll(CuratedWorld6PartB.LEVELS)
        map.putAll(CuratedWorld6PartC.LEVELS)
        map.putAll(CuratedWorld6PartD.LEVELS)
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map.putAll(CuratedWorld6PartA.SOLUTIONS)
        map.putAll(CuratedWorld6PartB.SOLUTIONS)
        map.putAll(CuratedWorld6PartC.SOLUTIONS)
        map.putAll(CuratedWorld6PartD.SOLUTIONS)
        map
    }

    fun getLevel(levelId: Int): PuzzleDefinition? = LEVELS[levelId]
    fun getSolution(levelId: Int): PuzzlePath? = SOLUTIONS[levelId]
}
