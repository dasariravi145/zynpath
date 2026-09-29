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

    val WORLDS: List<WorldDefinition> = listOf(
        WorldDefinition(
            worldId = 1,
            name = "Learn the Path",
            gridSize = 4,
            hasWalls = false,
            startLevel = 1,
            endLevel = 20,
            minPreviousWorldCompletedToUnlock = 0,
            accentColorHex = 0xFF52B788 // ForestMint
        ),
        WorldDefinition(
            worldId = 2,
            name = "Longer Connections",
            gridSize = 5,
            hasWalls = false,
            startLevel = 21,
            endLevel = 50,
            minPreviousWorldCompletedToUnlock = 20,
            accentColorHex = 0xFF4361EE // AccentBlue
        ),
        WorldDefinition(
            worldId = 3,
            name = "Wall Challenge",
            gridSize = 5,
            hasWalls = true,
            startLevel = 51,
            endLevel = 100,
            minPreviousWorldCompletedToUnlock = 30,
            accentColorHex = 0xFFE63946 // WallCrimson
        ),
        WorldDefinition(
            worldId = 4,
            name = "Complex Routes",
            gridSize = 6,
            hasWalls = false,
            startLevel = 101,
            endLevel = 150,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFF7209B7 // AccentPurple
        ),
        WorldDefinition(
            worldId = 5,
            name = "Advanced Logic",
            gridSize = 7,
            hasWalls = true,
            startLevel = 151,
            endLevel = 200,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFFFFB703 // AccentGold
        ),
        WorldDefinition(
            worldId = 6,
            name = "Expert Path",
            gridSize = 8,
            hasWalls = true,
            startLevel = 201,
            endLevel = 300,
            minPreviousWorldCompletedToUnlock = 50,
            accentColorHex = 0xFF10B981 // SuccessGreen
        )
    )

    const val TOTAL_WORLDS: Int = 6
    const val TOTAL_LEVELS: Int = 300

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
     * World 1 is always unlocked.
     * World N requires all levels of World (N - 1) to be completed.
     */
    fun isWorldUnlocked(worldId: Int, completedLevelIds: Set<Int>): Boolean {
        if (worldId == 1) return true
        val previousWorld = WORLDS.firstOrNull { it.worldId == worldId - 1 } ?: return false
        val completedInPrevious = completedLevelIds.count { it in previousWorld.levelRange }
        return completedInPrevious >= getWorld(worldId).minPreviousWorldCompletedToUnlock
    }

    /**
     * Determines if a level is unlocked.
     * Level 1 is always unlocked.
     * Level L is unlocked if its world is unlocked AND:
     * - It is the first level of that world, OR
     * - Level (L - 1) is completed.
     */
    fun isLevelUnlocked(levelId: Int, completedLevelIds: Set<Int>): Boolean {
        if (levelId <= 1) return true
        if (levelId > TOTAL_LEVELS) return false

        val world = getWorldForLevel(levelId)
        if (!isWorldUnlocked(world.worldId, completedLevelIds)) return false

        // First level of world is unlocked once world is unlocked
        if (levelId == world.startLevel) return true

        // Otherwise previous level must be completed
        return (levelId - 1) in completedLevelIds
    }

    /**
     * Finds the next uncompleted playable level for the player.
     * Returns 1 if no levels are completed, or the lowest numbered unlocked uncompleted level.
     */
    fun getNextPlayableLevel(completedLevelIds: Set<Int>): Int {
        for (lvl in 1..TOTAL_LEVELS) {
            if (lvl !in completedLevelIds && isLevelUnlocked(lvl, completedLevelIds)) {
                return lvl
            }
        }
        return TOTAL_LEVELS
    }
}
