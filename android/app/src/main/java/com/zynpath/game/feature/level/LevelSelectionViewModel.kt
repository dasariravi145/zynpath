package com.zynpath.game.feature.level

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.onboarding.FeatureDiscoveryTips
import com.zynpath.game.core.onboarding.FeatureTip
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LevelSelectionUiState(
    val worldId: Int = 1,
    val worldName: String = "World 1 • Learn the Path",
    val gridSize: String = "4×4",
    val levels: List<LevelCardData> = emptyList(),
    val totalStars: Int = 0,
    val completedCount: Int = 0,
    val activeWorldIntro: FeatureTip? = null,
    val nextPlayableLevelId: Int = 1,
    val isReducedMotion: Boolean = false,
    val currentChapter: com.zynpath.game.core.puzzle.experience.ChapterDefinition? = null,
    val isWorldLocked: Boolean = false,
    val worldUnlockRequirement: String? = null
)

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val preferencesRepository: PreferencesRepository,
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository? = null
) : ViewModel() {

    constructor(
        progressRepository: ProgressRepository,
        preferencesRepository: PreferencesRepository,
        savedStateHandle: SavedStateHandle
    ) : this(progressRepository, preferencesRepository, savedStateHandle, null)

    val initialWorldId: Int = savedStateHandle.get<Int>("worldId") ?: 0
    private val selectedWorldId = MutableStateFlow(
        if (initialWorldId in 1..WorldConfiguration.TOTAL_WORLDS) initialWorldId else 1
    )
    val worldId: Int get() = selectedWorldId.value

    private val _uiState = MutableStateFlow(
        LevelSelectionUiState(
            worldId = selectedWorldId.value,
            worldName = getWorldName(selectedWorldId.value),
            gridSize = getGridSize(selectedWorldId.value),
            levels = generateInitialLevels(selectedWorldId.value)
        )
    )
    val uiState: StateFlow<LevelSelectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            selectedWorldId.collectLatest { wId ->
                preferencesRepository.setLastSelectedWorld(wId)
                val introTip = FeatureDiscoveryTips.getWorldIntro(wId)
                if (introTip != null) {
                    val prefs = preferencesRepository.userPreferencesFlow.first()
                    if (introTip.tipId !in prefs.seenFeatureTips) {
                        _uiState.update { it.copy(activeWorldIntro = introTip) }
                    }
                }
            }
        }
        observeWorldProgress()
    }

    private fun observeWorldProgress() {
        viewModelScope.launch {
            var autoFocused = false
            kotlinx.coroutines.flow.combine(
                selectedWorldId,
                progressRepository.observeAllProgress(),
                preferencesRepository.userPreferencesFlow
            ) { currentWorldId, allProgress, prefs ->
                val progressMap = allProgress.associateBy { it.levelId }
                val completedLevelIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()

                val effectiveWorldId = if (!autoFocused && initialWorldId <= 0) {
                    autoFocused = true
                    val nextPlayable = WorldConfiguration.getNextPlayableLevel(completedLevelIds)
                    val nextWorld = WorldConfiguration.getWorldForLevel(nextPlayable).worldId
                    if (nextWorld != currentWorldId) {
                        selectedWorldId.value = nextWorld
                        nextWorld
                    } else {
                        currentWorldId
                    }
                } else {
                    currentWorldId
                }

                val worldConfig = WorldConfiguration.getWorld(effectiveWorldId)
                val levelRange = worldConfig?.levelRange ?: getLevelRange(effectiveWorldId)
                var foundFirstIncomplete = false

                val levelCards = levelRange.map { levelNum ->
                    val progress = progressMap[levelNum]
                    val isCompleted = progress?.isCompleted == true
                    val stars = progress?.stars ?: 0

                    val isUnlocked = WorldConfiguration.isLevelUnlocked(levelNum, completedLevelIds)
                    val state = when {
                        isCompleted -> LevelState.COMPLETED
                        isUnlocked -> LevelState.UNLOCKED
                        else -> LevelState.LOCKED
                    }

                    val levelDef = catalogRepository?.getLevel(levelNum)
                    val isPackaged = levelDef?.hasPackagedAsset ?: true

                    val isCurrent = if (!isCompleted && state == LevelState.UNLOCKED && isPackaged && !foundFirstIncomplete) {
                        foundFirstIncomplete = true
                        true
                    } else false

                    val experience = com.zynpath.game.core.puzzle.experience.ProgressionPlan.getLevelExperience(levelNum)

                    LevelCardData(
                        levelNumber = levelNum,
                        state = state,
                        stars = stars,
                        isCurrent = isCurrent,
                        bestTimeSeconds = progress?.let { if (it.bestTimeMs > 0) (it.bestTimeMs / 1000).toInt() else null },
                        isAvailable = isPackaged,
                        difficultyBand = levelDef?.difficultyBand?.name,
                        chapterTitle = experience.chapterTitle,
                        isMilestone = experience.isMilestone,
                        milestoneTitle = experience.milestoneIndicator?.milestoneTitle,
                        difficultyCategory = experience.difficultyCategory.displayName
                    )
                }

                val completedCount = levelCards.count { it.state == LevelState.COMPLETED }
                val totalStars = levelCards.sumOf { it.stars }
                val nextPlayableLevelId = levelCards.firstOrNull { it.isCurrent }?.levelNumber
                    ?: levelCards.firstOrNull { it.state == LevelState.UNLOCKED }?.levelNumber
                    ?: levelRange.first
                val currentChapter = com.zynpath.game.core.puzzle.experience.ProgressionPlan.getChapterForLevel(levelRange.first)

                val isWorldLocked = !WorldConfiguration.isWorldUnlocked(effectiveWorldId, completedLevelIds)
                val worldUnlockRequirement = if (isWorldLocked && effectiveWorldId > 1) {
                    val prevWorld = WorldConfiguration.getWorld(effectiveWorldId - 1)
                    val completedInPrev = completedLevelIds.count { it in (prevWorld?.levelRange ?: IntRange.EMPTY) }
                    val req = worldConfig?.minPreviousWorldCompletedToUnlock ?: 0
                    "Requires $req levels solved in World ${effectiveWorldId - 1} ($completedInPrev/$req)"
                } else null

                LevelSelectionUiState(
                    worldId = effectiveWorldId,
                    worldName = getWorldName(effectiveWorldId),
                    gridSize = getGridSize(effectiveWorldId),
                    levels = levelCards,
                    totalStars = totalStars,
                    completedCount = completedCount,
                    activeWorldIntro = _uiState.value.activeWorldIntro,
                    nextPlayableLevelId = nextPlayableLevelId,
                    isReducedMotion = prefs.isReducedMotion,
                    currentChapter = currentChapter,
                    isWorldLocked = isWorldLocked,
                    worldUnlockRequirement = worldUnlockRequirement
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectWorld(newWorldId: Int) {
        val clamped = newWorldId.coerceIn(1, WorldConfiguration.TOTAL_WORLDS)
        selectedWorldId.value = clamped
    }

    fun dismissWorldIntro() {
        val tip = _uiState.value.activeWorldIntro ?: return
        _uiState.update { it.copy(activeWorldIntro = null) }
        viewModelScope.launch {
            preferencesRepository.markFeatureTipSeen(tip.tipId)
        }
    }

    fun onLevelSelected(levelId: Int) {
        viewModelScope.launch {
            preferencesRepository.setLastSelectedLevel(levelId)
        }
    }

    companion object {
        fun getLevelRange(worldId: Int): IntRange =
            WorldConfiguration.getWorld(worldId)?.levelRange ?: (1..300)

        fun getWorldName(worldId: Int): String {
            val world = WorldConfiguration.getWorld(worldId)
            return if (world != null) "World ${world.worldId} • ${world.name}" else "World $worldId"
        }

        fun getGridSize(worldId: Int): String {
            val world = WorldConfiguration.getWorld(worldId)
            return world?.gridSizeDescription ?: "Grid"
        }

        fun generateInitialLevels(worldId: Int): List<LevelCardData> {
            val range = getLevelRange(worldId)
            return range.mapIndexed { index, levelNum ->
                LevelCardData(
                    levelNumber = levelNum,
                    state = if (index == 0 && worldId == 1) LevelState.UNLOCKED else LevelState.LOCKED,
                    isCurrent = (index == 0 && worldId == 1)
                )
            }
        }
    }
}
