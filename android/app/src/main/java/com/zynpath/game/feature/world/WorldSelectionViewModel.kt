package com.zynpath.game.feature.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.designsystem.components.WorldCardData
import com.zynpath.game.core.designsystem.theme.AccentBlue
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.ForestMint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorldSelectionUiState(
    val worlds: List<WorldCardData> = emptyList(),
    val totalStarsEarned: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class WorldSelectionViewModel @Inject constructor(
    private val levelProgressDao: LevelProgressDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldSelectionUiState(worlds = getDefaultWorlds()))
    val uiState: StateFlow<WorldSelectionUiState> = _uiState.asStateFlow()

    init {
        loadWorldProgress()
    }

    private fun loadWorldProgress() {
        viewModelScope.launch {
            try {
                val allProgress = levelProgressDao.getAllProgressList()
                val totalStars = allProgress.sumOf { it.stars }
                val completedLevelIds: Set<Int> = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()

                val updatedWorlds = getDefaultWorlds().map { world ->
                    val range: IntRange = when (world.worldId) {
                        1 -> 1..20
                        2 -> 21..50
                        3 -> 51..100
                        4 -> 101..150
                        5 -> 151..200
                        6 -> 201..300
                        else -> 1..20
                    }
                    val completedInWorld = completedLevelIds.count { id -> id in range }
                    // World 1 unlocked by default; World N unlocked if previous world threshold reached
                    val isLocked = when (world.worldId) {
                        1 -> false
                        2 -> completedLevelIds.count { id -> id in 1..20 } < 10
                        3 -> completedLevelIds.count { id -> id in 21..50 } < 15
                        4 -> completedLevelIds.count { id -> id in 51..100 } < 25
                        5 -> completedLevelIds.count { id -> id in 101..150 } < 25
                        6 -> completedLevelIds.count { id -> id in 151..200 } < 25
                        else -> true
                    }
                    world.copy(
                        completedLevels = completedInWorld,
                        isLocked = isLocked
                    )
                }

                _uiState.value = WorldSelectionUiState(
                    worlds = updatedWorlds,
                    totalStarsEarned = totalStars,
                    isLoading = false
                )
            } catch (_: Exception) {
                // Keep default state on error
            }
        }
    }

    companion object {
        fun getDefaultWorlds(): List<WorldCardData> = listOf(
            WorldCardData(
                worldId = 1,
                name = "Learn the Path",
                gridSizeDescription = "4×4",
                levelRangeDescription = "Levels 1–20",
                totalLevels = 20,
                completedLevels = 0,
                isLocked = false,
                hasWalls = false,
                accentColor = ForestMint
            ),
            WorldCardData(
                worldId = 2,
                name = "Longer Connections",
                gridSizeDescription = "5×5",
                levelRangeDescription = "Levels 21–50",
                totalLevels = 30,
                completedLevels = 0,
                isLocked = true,
                hasWalls = false,
                accentColor = AccentBlue
            ),
            WorldCardData(
                worldId = 3,
                name = "Wall Challenge",
                gridSizeDescription = "5×5 with walls",
                levelRangeDescription = "Levels 51–100",
                totalLevels = 50,
                completedLevels = 0,
                isLocked = true,
                hasWalls = true,
                accentColor = com.zynpath.game.core.designsystem.theme.WallCrimson
            ),
            WorldCardData(
                worldId = 4,
                name = "Complex Routes",
                gridSizeDescription = "6×6",
                levelRangeDescription = "Levels 101–150",
                totalLevels = 50,
                completedLevels = 0,
                isLocked = true,
                hasWalls = false,
                accentColor = AccentPurple
            ),
            WorldCardData(
                worldId = 5,
                name = "Advanced Logic",
                gridSizeDescription = "7×7",
                levelRangeDescription = "Levels 151–200",
                totalLevels = 50,
                completedLevels = 0,
                isLocked = true,
                hasWalls = true,
                accentColor = AccentGold
            ),
            WorldCardData(
                worldId = 6,
                name = "Expert Path",
                gridSizeDescription = "8×8",
                levelRangeDescription = "Levels 201–300",
                totalLevels = 100,
                completedLevels = 0,
                isLocked = true,
                hasWalls = true,
                accentColor = ForestMint
            )
        )
    }
}
