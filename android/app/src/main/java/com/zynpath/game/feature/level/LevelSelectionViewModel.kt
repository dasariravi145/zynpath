package com.zynpath.game.feature.level

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LevelSelectionUiState(
    val worldId: Int = 1,
    val worldName: String = "World 1: Learn the Path",
    val gridSize: String = "4×4",
    val levels: List<LevelCardData> = emptyList(),
    val totalStars: Int = 0,
    val completedCount: Int = 0
)

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val levelProgressDao: LevelProgressDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val worldId: Int = savedStateHandle.get<Int>("worldId") ?: 1

    private val _uiState = MutableStateFlow(
        LevelSelectionUiState(
            worldId = worldId,
            worldName = getWorldName(worldId),
            gridSize = getGridSize(worldId),
            levels = generateInitialLevels(worldId)
        )
    )
    val uiState: StateFlow<LevelSelectionUiState> = _uiState.asStateFlow()

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch {
            try {
                val dbLevels = levelProgressDao.getAllProgressList()
                val progressMap = dbLevels.associateBy { it.levelId }

                val levelRange = getLevelRange(worldId)
                var foundFirstIncomplete = false

                val levelCards = levelRange.map { levelNum ->
                    val progress = progressMap[levelNum]
                    val isCompleted = progress?.isCompleted == true
                    val stars = progress?.stars ?: 0

                    val state = when {
                        isCompleted -> LevelState.COMPLETED
                        levelNum == levelRange.first -> LevelState.UNLOCKED
                        progressMap[levelNum - 1]?.isCompleted == true -> LevelState.UNLOCKED
                        else -> LevelState.LOCKED
                    }

                    val isCurrent = if (!isCompleted && state == LevelState.UNLOCKED && !foundFirstIncomplete) {
                        foundFirstIncomplete = true
                        true
                    } else false

                    LevelCardData(
                        levelNumber = levelNum,
                        state = state,
                        stars = stars,
                        isCurrent = isCurrent,
                        bestTimeSeconds = progress?.let { if (it.bestTimeMs > 0) (it.bestTimeMs / 1000).toInt() else null }
                    )
                }

                val completedCount = levelCards.count { it.state == LevelState.COMPLETED }
                val totalStars = levelCards.sumOf { it.stars }

                _uiState.value = LevelSelectionUiState(
                    worldId = worldId,
                    worldName = getWorldName(worldId),
                    gridSize = getGridSize(worldId),
                    levels = levelCards,
                    totalStars = totalStars,
                    completedCount = completedCount
                )
            } catch (_: Exception) {
                // Fallback to initial
            }
        }
    }

    companion object {
        fun getLevelRange(worldId: Int): IntRange = when (worldId) {
            1 -> 1..20
            2 -> 21..50
            3 -> 51..100
            4 -> 101..150
            5 -> 151..200
            6 -> 201..300
            else -> 1..20
        }

        fun getWorldName(worldId: Int): String = when (worldId) {
            1 -> "World 1 • Learn the Path"
            2 -> "World 2 • Longer Connections"
            3 -> "World 3 • Wall Challenge"
            4 -> "World 4 • Complex Routes"
            5 -> "World 5 • Advanced Logic"
            6 -> "World 6 • Expert Path"
            else -> "World $worldId"
        }

        fun getGridSize(worldId: Int): String = when (worldId) {
            1 -> "4×4"
            2 -> "5×5"
            3 -> "5×5 (Walls)"
            4 -> "6×6"
            5 -> "7×7"
            6 -> "8×8"
            else -> "Grid"
        }

        fun generateInitialLevels(worldId: Int): List<LevelCardData> {
            val range = getLevelRange(worldId)
            return range.mapIndexed { index, levelNum ->
                LevelCardData(
                    levelNumber = levelNum,
                    state = if (index == 0) LevelState.UNLOCKED else LevelState.LOCKED,
                    isCurrent = (index == 0)
                )
            }
        }
    }
}
