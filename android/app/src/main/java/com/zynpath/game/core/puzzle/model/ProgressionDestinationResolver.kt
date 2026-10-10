package com.zynpath.game.core.puzzle.model

/**
 * Distinct completion states for Zynpath solo gameplay progression.
 * Separates normal level completion from world-clearing milestones and catalog finale.
 */
enum class LevelCompletionState {
    LEVEL_COMPLETED,
    WORLD_COMPLETED,
    JOURNEY_COMPLETED
}

/**
 * Strongly typed navigation destination resolved upon level completion.
 */
sealed class ProgressionDestination {
    /**
     * Advances to the next sequential level within the SAME world.
     */
    data class NextLevel(val worldId: Int, val levelId: Int) : ProgressionDestination()

    /**
     * Advances to the level selection/entry screen of the NEXT world.
     */
    data class NextWorldEntry(val worldId: Int) : ProgressionDestination()

    /**
     * Concludes the entire 6-world, 300-level catalog journey.
     */
    data object JourneyComplete : ProgressionDestination()
}

/**
 * Comprehensive resolution result defining the exact button text and destination.
 */
data class NextDestinationResolution(
    val currentWorld: Int,
    val completedLevel: Int,
    val completionState: LevelCompletionState,
    val hasNextLevelInCurrentWorld: Boolean,
    val isCurrentWorldComplete: Boolean,
    val hasNextWorld: Boolean,
    val nextWorldId: Int?,
    val nextLevelId: Int?,
    val buttonLabel: String,
    val destination: ProgressionDestination
)

/**
 * Authoritative single source of truth for resolving next gameplay destinations.
 * Enforces strict separation between level completion and world completion.
 */
object ProgressionDestinationResolver {

    /**
     * Resolves the next destination from a canonical global level ID (1..300).
     */
    fun resolve(
        completedLevelId: Int,
        completedLevelIds: Set<Int> = emptySet()
    ): NextDestinationResolution {
        return com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.resolveVictoryDestination(completedLevelId, completedLevelIds)
    }

    /**
     * Resolves the next destination given the world ID and level (either local 1-based or canonical global ID).
     */
    fun resolve(
        worldId: Int,
        completedLevelOrLocal: Int,
        completedLevelIds: Set<Int> = emptySet()
    ): NextDestinationResolution {
        val completedGlobalLevel = WorldConfiguration.toGlobalLevel(worldId, completedLevelOrLocal)
        return com.zynpath.game.core.puzzle.progression.SoloProgressionAuthority.resolveVictoryDestination(completedGlobalLevel, completedLevelIds)
    }
}
