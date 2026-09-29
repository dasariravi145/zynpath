package com.zynpath.game.feature.world

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.designsystem.components.WorldCardData
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorldSelectionUiState(
    val worlds: List<WorldCardData> = emptyList(),
    val totalStarsEarned: Int = 0,
    val isLoading: Boolean = false,
    val nextPlayableWorldId: Int = 1,
    val nextPlayableLevelId: Int = 1,
    val isReducedMotion: Boolean = false
)

@HiltViewModel
class WorldSelectionViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val preferencesRepository: PreferencesRepository,
    private val catalogRepository: com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository? = null
) : ViewModel() {

    constructor(
        progressRepository: ProgressRepository,
        preferencesRepository: PreferencesRepository
    ) : this(progressRepository, preferencesRepository, null)

    private val _uiState = MutableStateFlow(WorldSelectionUiState(worlds = getDefaultWorlds()))
    val uiState: StateFlow<WorldSelectionUiState> = _uiState.asStateFlow()

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                progressRepository.observeAllProgress(),
                preferencesRepository.userPreferencesFlow
            ) { allProgress, prefs ->
                val completedLevelIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()
                val totalStars = allProgress.sumOf { it.stars }

                val nextPlayableLevelId = WorldConfiguration.getNextPlayableLevel(completedLevelIds)
                val nextPlayableWorld = WorldConfiguration.getWorldForLevel(nextPlayableLevelId)

                val updatedWorlds = WorldConfiguration.WORLDS.map { def ->
                    val completedInWorld = completedLevelIds.count { it in def.levelRange }
                    val isLocked = !WorldConfiguration.isWorldUnlocked(def.worldId, completedLevelIds)

                    val unlockRequirementText = if (isLocked && def.worldId > 1) {
                        val prevWorld = WorldConfiguration.getWorld(def.worldId - 1)
                        val completedInPrev = completedLevelIds.count { it in prevWorld.levelRange }
                        val req = def.minPreviousWorldCompletedToUnlock
                        "Requires $req levels solved in World ${def.worldId - 1} ($completedInPrev/$req)"
                    } else null

                    WorldCardData(
                        worldId = def.worldId,
                        name = def.name,
                        gridSizeDescription = def.gridSizeDescription,
                        levelRangeDescription = def.levelRangeDescription,
                        totalLevels = def.totalLevels,
                        completedLevels = completedInWorld,
                        isLocked = isLocked,
                        hasWalls = def.hasWalls,
                        accentColor = Color(def.accentColorHex),
                        unlockRequirementText = unlockRequirementText
                    )
                }

                WorldSelectionUiState(
                    worlds = updatedWorlds,
                    totalStarsEarned = totalStars,
                    isLoading = false,
                    nextPlayableWorldId = nextPlayableWorld.worldId,
                    nextPlayableLevelId = nextPlayableLevelId,
                    isReducedMotion = prefs.isReducedMotion
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onWorldSelected(worldId: Int) {
        viewModelScope.launch {
            preferencesRepository.setLastSelectedWorld(worldId)
        }
    }

    companion object {
        fun getDefaultWorlds(): List<WorldCardData> {
            return WorldConfiguration.WORLDS.map { def ->
                val unlockRequirementText = if (def.worldId > 1) {
                    "Requires ${def.minPreviousWorldCompletedToUnlock} levels solved in World ${def.worldId - 1}"
                } else null

                WorldCardData(
                    worldId = def.worldId,
                    name = def.name,
                    gridSizeDescription = def.gridSizeDescription,
                    levelRangeDescription = def.levelRangeDescription,
                    totalLevels = def.totalLevels,
                    completedLevels = 0,
                    isLocked = def.worldId != 1,
                    hasWalls = def.hasWalls,
                    accentColor = Color(def.accentColorHex),
                    unlockRequirementText = unlockRequirementText
                )
            }
        }
    }
}
