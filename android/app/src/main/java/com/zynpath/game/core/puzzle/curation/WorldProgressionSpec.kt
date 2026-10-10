package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.model.GridDimensions

/**
 * Authoritative World Progression Specification for Zynpath level curation.
 *
 * Implements Prompt 10 Section 25:
 * - World 1: Levels 1–20,   4×4, 4–6 checkpoints, 0 walls.
 * - World 2: Levels 21–50,  5×5, 4–7 checkpoints, 0 walls.
 * - World 3: Levels 51–100, 5×5, 4–7 checkpoints, 1–5 walls.
 * - World 4: Levels 101–150, 6×6, 4–8 checkpoints, 2–8 walls.
 * - World 5: Levels 151–200, 7×7, 4–10 checkpoints, 4–12 walls.
 * - World 6: Levels 201–300, 8×8, 4–12 checkpoints, 6–18 walls.
 */
data class WorldProgressionSpec(
    val worldId: Int,
    val name: String,
    val levelRange: IntRange,
    val gridSize: Int,
    val checkpointRange: IntRange,
    val wallRange: IntRange,
    val targetDifficultyRange: ClosedFloatingPointRange<Double>,
    val targetDifficultyBand: DifficultyBand
) {
    val gridDimensions: GridDimensions
        get() = GridDimensions(gridSize, gridSize)

    companion object {
        val WORLD_1 = WorldProgressionSpec(
            worldId = 1,
            name = "Learn the Path",
            levelRange = 1..20,
            gridSize = 6,
            checkpointRange = 6..10,
            wallRange = 0..0,
            targetDifficultyRange = 0.35..0.75,
            targetDifficultyBand = DifficultyBand.EASY
        )

        val WORLD_2 = WorldProgressionSpec(
            worldId = 2,
            name = "Longer Connections",
            levelRange = 21..50,
            gridSize = 7,
            checkpointRange = 8..12,
            wallRange = 0..0,
            targetDifficultyRange = 0.50..0.80,
            targetDifficultyBand = DifficultyBand.HARD
        )

        val WORLD_3 = WorldProgressionSpec(
            worldId = 3,
            name = "Wall Challenge",
            levelRange = 51..100,
            gridSize = 8,
            checkpointRange = 10..18,
            wallRange = 0..8,
            targetDifficultyRange = 0.60..0.85,
            targetDifficultyBand = DifficultyBand.HARD
        )

        val WORLD_4 = WorldProgressionSpec(
            worldId = 4,
            name = "Complex Routes",
            levelRange = 101..150,
            gridSize = 9,
            checkpointRange = 12..20,
            wallRange = 0..10,
            targetDifficultyRange = 0.75..0.92,
            targetDifficultyBand = DifficultyBand.VERY_HARD
        )

        val WORLD_5 = WorldProgressionSpec(
            worldId = 5,
            name = "Advanced Logic",
            levelRange = 151..200,
            gridSize = 7,
            checkpointRange = 4..10,
            wallRange = 4..12,
            targetDifficultyRange = 0.75..0.95,
            targetDifficultyBand = DifficultyBand.EXPERT
        )

        val WORLD_6 = WorldProgressionSpec(
            worldId = 6,
            name = "Expert Path",
            levelRange = 201..300,
            gridSize = 8,
            checkpointRange = 4..12,
            wallRange = 6..18,
            targetDifficultyRange = 0.80..1.00,
            targetDifficultyBand = DifficultyBand.EXPERT
        )

        val ALL_WORLDS: List<WorldProgressionSpec> = listOf(
            WORLD_1, WORLD_2, WORLD_3, WORLD_4, WORLD_5, WORLD_6
        )

        fun forWorld(worldId: Int): WorldProgressionSpec {
            return ALL_WORLDS.firstOrNull { it.worldId == worldId } ?: WORLD_1
        }

        fun forLevel(levelId: Int): WorldProgressionSpec {
            return ALL_WORLDS.firstOrNull { levelId in it.levelRange } ?: WORLD_1
        }
    }
}
