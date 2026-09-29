package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.GridDimensions

/**
 * Immutable domain definition of a campaign World in Zynpath.
 *
 * Implements Prompt 11 Section 8:
 * Holds pure game rules, bounds, and level ranges without Android framework or UI dependencies.
 */
data class WorldDefinition(
    val worldId: Int,
    val displayName: String,
    val firstLevelId: Int,
    val lastLevelId: Int,
    val gridDimensions: GridDimensions,
    val minimumCheckpointCount: Int,
    val maximumCheckpointCount: Int,
    val minimumWallCount: Int,
    val maximumWallCount: Int,
    val themeIdentifier: String? = null,
    val catalogVersion: String = CatalogVersion.CATALOG_VERSION
) {
    val totalLevels: Int
        get() = (lastLevelId - firstLevelId) + 1

    val levelRange: IntRange
        get() = firstLevelId..lastLevelId

    val hasWalls: Boolean
        get() = maximumWallCount > 0

    val gridSizeDescription: String
        get() = if (hasWalls) {
            "${gridDimensions.rows}×${gridDimensions.columns} with walls"
        } else {
            "${gridDimensions.rows}×${gridDimensions.columns}"
        }

    val checkpointRange: IntRange
        get() = minimumCheckpointCount..maximumCheckpointCount

    val wallRange: IntRange
        get() = minimumWallCount..maximumWallCount

    init {
        require(worldId in 1..6) { "worldId must be between 1 and 6 (got $worldId)" }
        require(firstLevelId <= lastLevelId) { "firstLevelId ($firstLevelId) must be <= lastLevelId ($lastLevelId)" }
        require(minimumCheckpointCount >= 2) { "minimumCheckpointCount must be >= 2" }
        require(minimumCheckpointCount <= maximumCheckpointCount) { "checkpoint bounds invalid" }
        require(minimumWallCount >= 0) { "minimumWallCount must be >= 0" }
        require(minimumWallCount <= maximumWallCount) { "wall bounds invalid" }
    }

    companion object {
        val WORLD_1 = WorldDefinition(
            worldId = 1,
            displayName = "Learn the Path",
            firstLevelId = 1,
            lastLevelId = 20,
            gridDimensions = GridDimensions(4, 4),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 6,
            minimumWallCount = 0,
            maximumWallCount = 0,
            themeIdentifier = "forest_mint"
        )

        val WORLD_2 = WorldDefinition(
            worldId = 2,
            displayName = "Longer Connections",
            firstLevelId = 21,
            lastLevelId = 50,
            gridDimensions = GridDimensions(5, 5),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 7,
            minimumWallCount = 0,
            maximumWallCount = 0,
            themeIdentifier = "accent_blue"
        )

        val WORLD_3 = WorldDefinition(
            worldId = 3,
            displayName = "Wall Challenge",
            firstLevelId = 51,
            lastLevelId = 100,
            gridDimensions = GridDimensions(5, 5),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 7,
            minimumWallCount = 1,
            maximumWallCount = 5,
            themeIdentifier = "wall_crimson"
        )

        val WORLD_4 = WorldDefinition(
            worldId = 4,
            displayName = "Complex Routes",
            firstLevelId = 101,
            lastLevelId = 150,
            gridDimensions = GridDimensions(6, 6),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 8,
            minimumWallCount = 2,
            maximumWallCount = 8,
            themeIdentifier = "accent_purple"
        )

        val WORLD_5 = WorldDefinition(
            worldId = 5,
            displayName = "Advanced Logic",
            firstLevelId = 151,
            lastLevelId = 200,
            gridDimensions = GridDimensions(7, 7),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 10,
            minimumWallCount = 4,
            maximumWallCount = 12,
            themeIdentifier = "accent_gold"
        )

        val WORLD_6 = WorldDefinition(
            worldId = 6,
            displayName = "Expert Path",
            firstLevelId = 201,
            lastLevelId = 300,
            gridDimensions = GridDimensions(8, 8),
            minimumCheckpointCount = 4,
            maximumCheckpointCount = 12,
            minimumWallCount = 6,
            maximumWallCount = 18,
            themeIdentifier = "success_green"
        )

        val ALL_WORLDS: List<WorldDefinition> = listOf(
            WORLD_1, WORLD_2, WORLD_3, WORLD_4, WORLD_5, WORLD_6
        )

        fun forWorld(worldId: Int): WorldDefinition {
            return ALL_WORLDS.firstOrNull { it.worldId == worldId } ?: WORLD_1
        }

        fun forLevel(levelId: Int): WorldDefinition {
            return ALL_WORLDS.firstOrNull { levelId in it.levelRange } ?: WORLD_1
        }
    }
}
