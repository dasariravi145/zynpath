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
        val currentWorldDef = WorldConfiguration.getWorldForLevel(completedLevelId)
        return resolve(currentWorldDef.worldId, completedLevelId, completedLevelIds)
    }

    /**
     * Resolves the next destination given the world ID and level (either local 1-based or canonical global ID).
     */
    fun resolve(
        worldId: Int,
        completedLevelOrLocal: Int,
        completedLevelIds: Set<Int> = emptySet()
    ): NextDestinationResolution {
        // Normalize to canonical global level ID (1..300)
        val completedGlobalLevel = WorldConfiguration.toGlobalLevel(worldId, completedLevelOrLocal)
        val currentWorldDef = WorldConfiguration.getWorldForLevel(completedGlobalLevel)
        val currentWorldId = currentWorldDef.worldId

        // 1. Final Game Level (Level 300 / World 6 Final Level)
        if (completedGlobalLevel >= WorldConfiguration.TOTAL_LEVELS) {
            val isWorldComplete = currentWorldDef.levelRange.all { it in completedLevelIds || it == completedGlobalLevel }
            return NextDestinationResolution(
                currentWorld = currentWorldId,
                completedLevel = completedGlobalLevel,
                completionState = LevelCompletionState.JOURNEY_COMPLETED,
                hasNextLevelInCurrentWorld = false,
                isCurrentWorldComplete = isWorldComplete,
                hasNextWorld = false,
                nextWorldId = null,
                nextLevelId = null,
                buttonLabel = "JOURNEY COMPLETE",
                destination = ProgressionDestination.JourneyComplete
            )
        }

        // 2. Final Level of Current World (World Boundary, e.g. 20 for W1, 50 for W2, 100 for W3, 150 for W4, 200 for W5)
        if (completedGlobalLevel == currentWorldDef.endLevel) {
            val nextWorldId = currentWorldId + 1
            val hasNextWorld = nextWorldId <= WorldConfiguration.TOTAL_WORLDS
            val isWorldComplete = currentWorldDef.levelRange.all { it in completedLevelIds || it == completedGlobalLevel }

            return NextDestinationResolution(
                currentWorld = currentWorldId,
                completedLevel = completedGlobalLevel,
                completionState = LevelCompletionState.WORLD_COMPLETED,
                hasNextLevelInCurrentWorld = false,
                isCurrentWorldComplete = isWorldComplete,
                hasNextWorld = hasNextWorld,
                nextWorldId = if (hasNextWorld) nextWorldId else null,
                nextLevelId = null,
                buttonLabel = if (hasNextWorld) "NEXT WORLD" else "JOURNEY COMPLETE",
                destination = if (hasNextWorld) {
                    ProgressionDestination.NextWorldEntry(nextWorldId)
                } else {
                    ProgressionDestination.JourneyComplete
                }
            )
        }

        // 3. Normal Sequential Level in Current World (e.g. 1..19 in W1, 21..49 in W2, etc.)
        val nextLevelId = completedGlobalLevel + 1
        val isWorldComplete = currentWorldDef.levelRange.all { it in completedLevelIds || it == completedGlobalLevel }

        return NextDestinationResolution(
            currentWorld = currentWorldId,
            completedLevel = completedGlobalLevel,
            completionState = LevelCompletionState.LEVEL_COMPLETED,
            hasNextLevelInCurrentWorld = true,
            isCurrentWorldComplete = isWorldComplete,
            hasNextWorld = currentWorldId < WorldConfiguration.TOTAL_WORLDS,
            nextWorldId = currentWorldId,
            nextLevelId = nextLevelId,
            buttonLabel = "NEXT LEVEL",
            destination = ProgressionDestination.NextLevel(currentWorldId, nextLevelId)
        )
    }
}
