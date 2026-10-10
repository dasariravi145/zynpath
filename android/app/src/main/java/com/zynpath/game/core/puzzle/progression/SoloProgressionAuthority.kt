package com.zynpath.game.core.puzzle.progression

import com.zynpath.game.core.puzzle.model.LevelCompletionState
import com.zynpath.game.core.puzzle.model.NextDestinationResolution
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.model.WorldDefinition

/**
 * Authoritative descriptor providing full progression context for a single stable level.
 */
data class LevelProgressionDescriptor(
    val stableLevelId: Int,
    val worldId: Int,
    val worldLocalLevel: Int,
    val previousLevelId: Int?,
    val nextLevelId: Int?,
    val isFirstOfWorld: Boolean,
    val isLastOfWorld: Boolean,
    val isFinalGameLevel: Boolean
)

/**
 * Single Authoritative Source of Truth for Zynpath Solo Campaign Progression.
 *
 * Implements Systemic Progression Engine:
 * - Authoritative catalog of 1,800 stable level IDs across 6 Worlds (Prompt Section 4).
 * - Catalog-driven progression where completing level N strictly unlocks N + 1.
 * - Clear distinction between global stable IDs, World IDs, and world-local numbering (Section 2).
 * - Monotonic forward progress preservation (Sections 14, 15).
 * - World boundary transitions and finale handling (Sections 8, 25).
 * - Unified unlock and continue resolution used by Gameplay, Victory, Level Selection, Home, and DB.
 */
object SoloProgressionAuthority {

    const val TOTAL_WORLDS: Int = WorldConfiguration.TOTAL_WORLDS // 6
    const val TOTAL_LEVELS: Int = WorldConfiguration.TOTAL_LEVELS // 1800

    /**
     * Retrieves the progression descriptor for a given stable level ID (1..1800).
     */
    fun getLevel(stableLevelId: Int): LevelProgressionDescriptor? {
        if (stableLevelId !in 1..TOTAL_LEVELS) return null
        val world = getWorld(stableLevelId)
        val local = getWorldLocalLevel(stableLevelId)
        val prev = getPreviousLevel(stableLevelId)
        val next = getNextLevel(stableLevelId)
        val isFirst = isFirstLevelOfWorld(stableLevelId)
        val isLast = isLastLevelOfWorld(stableLevelId)
        val isFinal = isFinalGameLevel(stableLevelId)

        return LevelProgressionDescriptor(
            stableLevelId = stableLevelId,
            worldId = world.worldId,
            worldLocalLevel = local,
            previousLevelId = prev,
            nextLevelId = next,
            isFirstOfWorld = isFirst,
            isLastOfWorld = isLast,
            isFinalGameLevel = isFinal
        )
    }

    /**
     * Resolves the owning [WorldDefinition] for a stable level ID.
     */
    fun getWorld(stableLevelId: Int): WorldDefinition {
        return WorldConfiguration.getWorldForLevel(stableLevelId)
    }

    /**
     * Resolves the [WorldDefinition] by world ID (1..6).
     */
    fun getWorldById(worldId: Int): WorldDefinition {
        return WorldConfiguration.getWorld(worldId)
    }

    /**
     * Returns the 1-based local level index within the level's parent World.
     * E.g., Level 1 -> 1; Level 21 -> 1; Level 151 -> 1; Level 152 -> 2.
     */
    fun getWorldLocalLevel(stableLevelId: Int): Int {
        val world = getWorld(stableLevelId)
        return (stableLevelId - world.startLevel) + 1
    }

    /**
     * Converts a 1-based world-local level number (1..totalLevels)
     * into its canonical global stable level ID.
     * This is the exact mathematical inverse of [getWorldLocalLevel].
     */
    fun localToGlobalLevel(worldId: Int, worldLocalLevel: Int): Int {
        val world = getWorldById(worldId)
        val clamped = worldLocalLevel.coerceIn(1, world.totalLevels)
        return world.startLevel + (clamped - 1)
    }

    /**
     * Normalizes a world ID and a level argument (which could be world-local or global)
     * to a canonical global stable level ID (1..300).
     */
    fun toGlobalLevel(worldId: Int, localOrGlobalLevel: Int): Int {
        return WorldConfiguration.toGlobalLevel(worldId, localOrGlobalLevel)
    }

    /**
     * Returns the immediate preceding stable level ID, or null if this is Level 1.
     */
    fun getPreviousLevel(stableLevelId: Int): Int? {
        val prev = stableLevelId - 1
        return if (prev in 1..TOTAL_LEVELS) prev else null
    }

    /**
     * Returns the immediate succeeding stable level ID, or null if this is the final Level 300.
     */
    fun getNextLevel(stableLevelId: Int): Int? {
        val next = stableLevelId + 1
        return if (next in 1..TOTAL_LEVELS) next else null
    }

    /**
     * Checks if a level is recorded as completed.
     */
    fun isCompleted(stableLevelId: Int, completedLevelIds: Set<Int>): Boolean {
        return stableLevelId in completedLevelIds
    }

    /**
     * Determines whether a World is unlocked.
     *
     * Rules:
     * 1. World 1 is always unlocked.
     * 2. If ANY level in World W is completed, World W is unlocked (preserves existing & farther progress).
     * 3. World W is unlocked if all levels of World (W - 1) are completed (or min threshold satisfied).
     * 4. In particular, if the final level of World (W - 1) is completed, World W is unlocked.
     */
    fun isWorldUnlocked(worldId: Int, completedLevelIds: Set<Int>): Boolean {
        if (worldId == 1) return true
        if (worldId !in 1..TOTAL_WORLDS) return false

        val currentWorld = getWorldById(worldId)

        // Monotonic rule: if any level in this world is already completed, the world is unlocked
        if (completedLevelIds.any { it in currentWorld.levelRange }) {
            return true
        }

        val previousWorld = getWorldById(worldId - 1)

        // If the last level of the previous world is completed, the world is unlocked
        if (previousWorld.endLevel in completedLevelIds) {
            return true
        }

        // Count completed levels in the previous world
        val completedInPrevious = completedLevelIds.count { it in previousWorld.levelRange }
        return completedInPrevious >= currentWorld.minPreviousWorldCompletedToUnlock
    }

    /**
     * Authoritative single source of truth for level unlock eligibility.
     *
     * Rules:
     * 1. Level 1 is always unlocked.
     * 2. Any level > TOTAL_LEVELS is invalid and never unlocked.
     * 3. If level L is already completed (L in completedLevelIds), it is ALWAYS unlocked.
     * 4. If level (L - 1) is completed ((L - 1) in completedLevelIds), level L is unlocked!
     *    Completing any legitimate level strictly unlocks its immediate successor.
     * 5. If level L is the first level of its World (L == world.startLevel), it is unlocked
     *    if that World is unlocked.
     * 6. Any other level whose prerequisites have not been met remains locked.
     */
    fun isUnlocked(stableLevelId: Int, completedLevelIds: Set<Int>): Boolean {
        if (stableLevelId == 1) return true
        if (stableLevelId !in 1..TOTAL_LEVELS) return false

        // Rule 3: Already completed levels are always unlocked (replay safety)
        if (stableLevelId in completedLevelIds) return true

        // Rule 4: Immediate successor of completed level is unlocked (primary progression rule)
        if ((stableLevelId - 1) in completedLevelIds) return true

        // Rule 5: First level of an unlocked world is unlocked
        val world = getWorld(stableLevelId)
        if (stableLevelId == world.startLevel && isWorldUnlocked(world.worldId, completedLevelIds)) {
            return true
        }

        return false
    }

    /**
     * Checks if this level is the first level of its parent World.
     */
    fun isFirstLevelOfWorld(stableLevelId: Int): Boolean {
        if (stableLevelId !in 1..TOTAL_LEVELS) return false
        val world = getWorld(stableLevelId)
        return stableLevelId == world.startLevel
    }

    /**
     * Checks if this level is the last level of its parent World (a World boundary).
     */
    fun isLastLevelOfWorld(stableLevelId: Int): Boolean {
        if (stableLevelId !in 1..TOTAL_LEVELS) return false
        val world = getWorld(stableLevelId)
        return stableLevelId == world.endLevel
    }

    /**
     * Checks if this is the final catalog level (Level 1800).
     */
    fun isFinalGameLevel(stableLevelId: Int): Boolean {
        return stableLevelId == TOTAL_LEVELS
    }

    /**
     * Resolves the authoritative victory destination upon completing [completedLevelId].
     *
     * Guarantees:
     * - Level 300 resolves to [ProgressionDestination.JourneyComplete].
     * - Final level of World W (e.g., 20, 50, 100, 150, 200) resolves to [ProgressionDestination.NextWorldEntry].
     * - Normal level within a World resolves to [ProgressionDestination.NextLevel].
     */
    fun resolveVictoryDestination(
        completedLevelId: Int,
        completedLevelIds: Set<Int> = emptySet()
    ): NextDestinationResolution {
        val canonicalId = completedLevelId.coerceIn(1, TOTAL_LEVELS)
        val currentWorld = getWorld(canonicalId)
        val currentWorldId = currentWorld.worldId
        val effectiveCompleted = completedLevelIds + canonicalId

        // 1. Final Game Level (Level 300)
        if (isFinalGameLevel(canonicalId)) {
            val isWorldComplete = currentWorld.levelRange.all { it in effectiveCompleted }
            return NextDestinationResolution(
                currentWorld = currentWorldId,
                completedLevel = canonicalId,
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

        // 2. World Boundary Level (Final level of current World)
        if (isLastLevelOfWorld(canonicalId)) {
            val nextWorldId = currentWorldId + 1
            val hasNextWorld = nextWorldId <= TOTAL_WORLDS
            val isWorldComplete = currentWorld.levelRange.all { it in effectiveCompleted }

            return NextDestinationResolution(
                currentWorld = currentWorldId,
                completedLevel = canonicalId,
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

        // 3. Normal Sequential Level in Current World
        val nextLevelId = canonicalId + 1
        val isWorldComplete = currentWorld.levelRange.all { it in effectiveCompleted }

        return NextDestinationResolution(
            currentWorld = currentWorldId,
            completedLevel = canonicalId,
            completionState = LevelCompletionState.LEVEL_COMPLETED,
            hasNextLevelInCurrentWorld = true,
            isCurrentWorldComplete = isWorldComplete,
            hasNextWorld = currentWorldId < TOTAL_WORLDS,
            nextWorldId = currentWorldId,
            nextLevelId = nextLevelId,
            buttonLabel = "NEXT LEVEL",
            destination = ProgressionDestination.NextLevel(currentWorldId, nextLevelId)
        )
    }

    /**
     * Resolves the authoritative continue / next playable destination for the player.
     * Returns the lowest numbered unlocked level that has not yet been completed,
     * or Level 300 if all levels are completed.
     */
    fun resolveContinueDestination(completedLevelIds: Set<Int>): Int {
        for (lvl in 1..TOTAL_LEVELS) {
            if (lvl !in completedLevelIds && isUnlocked(lvl, completedLevelIds)) {
                return lvl
            }
        }
        return TOTAL_LEVELS
    }
}
