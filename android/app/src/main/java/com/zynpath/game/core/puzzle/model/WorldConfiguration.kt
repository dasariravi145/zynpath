package com.zynpath.game.core.puzzle.model

/**
 * Authoritative single source of truth for Zynpath world and level progression.
 * Used consistently across Room, DataStore, ViewModels, and UI cards.
 */
data class WorldDefinition(
    val worldId: Int,
    val name: String,
    val gridSize: Int,
    val hasWalls: Boolean,
    val startLevel: Int,
    val endLevel: Int,
    val minPreviousWorldCompletedToUnlock: Int,
    val accentColorHex: Long
) {
    val totalLevels: Int
        get() = (endLevel - startLevel) + 1

    val levelRange: IntRange
        get() = startLevel..endLevel

    val gridSizeDescription: String
        get() = if (hasWalls) "$gridSize×$gridSize with walls" else "$gridSize×$gridSize"

    val levelRangeDescription: String
        get() = "Levels $startLevel–$endLevel"
}

object WorldConfiguration {

    const val LEVELS_PER_WORLD: Int = 300

    val WORLDS: List<WorldDefinition> = listOf(
        WorldDefinition(
            worldId = 1,
            name = "Learn the Path",
            gridSize = 6,
            hasWalls = false,
            startLevel = 1,
            endLevel = 300,
            minPreviousWorldCompletedToUnlock = 0,
            accentColorHex = 0xFF52B788 // ForestMint
        ),
        WorldDefinition(
            worldId = 2,
            name = "Longer Connections",
            gridSize = 7,
            hasWalls = true,
            startLevel = 301,
            endLevel = 600,
            minPreviousWorldCompletedToUnlock = 20,
            accentColorHex = 0xFF4361EE // AccentBlue
        ),
        WorldDefinition(
            worldId = 3,
            name = "Wall Challenge",
            gridSize = 8,
            hasWalls = true,
            startLevel = 601,
            endLevel = 900,
            minPreviousWorldCompletedToUnlock = 30,
            accentColorHex = 0xFFE63946 // WallCrimson
        ),
        WorldDefinition(
            worldId = 4,
            name = "Complex Routes",
            gridSize = 9,
            hasWalls = true,
            startLevel = 901,
            endLevel = 1200,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFF7209B7 // AccentPurple
        ),
        WorldDefinition(
            worldId = 5,
            name = "Advanced Logic",
            gridSize = 11,
            hasWalls = true,
            startLevel = 1201,
            endLevel = 1500,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFFFFB703 // AccentGold
        ),
        WorldDefinition(
            worldId = 6,
            name = "Expert Path",
            gridSize = 12,
            hasWalls = true,
            startLevel = 1501,
            endLevel = 1800,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFF10B981 // SuccessGreen
        )
    )

    const val TOTAL_WORLDS: Int = 6
    const val TOTAL_LEVELS: Int = 1800

    fun getWorld(worldId: Int): WorldDefinition {
        return WORLDS.firstOrNull { it.worldId == worldId } ?: WORLDS.first()
    }

    fun getWorldForLevel(levelId: Int): WorldDefinition {
        return WORLDS.firstOrNull { levelId in it.levelRange } ?: WORLDS.first()
    }

    fun getLevelRange(worldId: Int): IntRange {
        return getWorld(worldId).levelRange
    }

    /**
     * Converts a canonical global level ID (1..300) into the local 1-based level index
     * within that level's parent world.
     * E.g., global level 1 -> World 1 Level 1; global level 21 -> World 2 Level 1;
     * global level 51 -> World 3 Level 1; global level 201 -> World 6 Level 1.
     */
    fun toLocalLevel(globalLevelId: Int): Int {
        val world = getWorldForLevel(globalLevelId)
        return (globalLevelId - world.startLevel) + 1
    }

    /**
     * Converts a 1-based local level number within a world into its canonical global level ID (1..300).
     * If [localOrGlobalLevel] is already in the world's global range [startLevel..endLevel],
     * it is preserved directly.
     */
    fun toGlobalLevel(worldId: Int, localOrGlobalLevel: Int): Int {
        val world = getWorld(worldId)
        if (localOrGlobalLevel in world.levelRange) {
            return localOrGlobalLevel
        }
        val calculated = world.startLevel + (localOrGlobalLevel - 1)
        return calculated.coerceIn(world.startLevel, world.endLevel)
    }

    fun formatLevelTitle(worldId: Int, globalLevelId: Int): String {
        return "World $worldId • Level ${toLocalLevel(globalLevelId)}"
    }

    fun formatLocalLevel(globalLevelId: Int): String {
        return "Level ${toLocalLevel(globalLevelId)}"
    }

    /**
     * Determines if a world is unlocked given the set of completed level IDs.
     * Delegates to authoritative [com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority].
     */
    fun isWorldUnlocked(worldId: Int, completedLevelIds: Set<Int>): Boolean {
        return com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.isWorldUnlocked(worldId, completedLevelIds)
    }

    /**
     * Determines if a level is unlocked given the set of completed level IDs.
     * Delegates to authoritative [com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority].
     */
    fun isLevelUnlocked(levelId: Int, completedLevelIds: Set<Int>): Boolean {
        return com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.isUnlocked(levelId, completedLevelIds)
    }

    /**
     * Finds the next uncompleted playable level for the player.
     * Delegates to authoritative [com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority].
     */
    fun getNextPlayableLevel(completedLevelIds: Set<Int>): Int {
        return com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.resolveContinueDestination(completedLevelIds)
    }
}
