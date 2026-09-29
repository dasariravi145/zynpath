package com.zynpath.game.feature.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.R
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.audio.NoOpAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.NoOpHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.TutorialPuzzles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TutorialStageConfig(
    val stageNumber: Int,
    val titleRes: Int,
    val descRes: Int,
    val tipRes: Int,
    val initialHighlight: GridPosition? = null
)

data class TutorialUiState(
    val currentStage: Int = 1,
    val totalStages: Int = 7,
    val boardState: PuzzleBoardState = PuzzleBoardState(3, 3, emptyMap(), emptySet(), emptyList()),
    val isStageCompleted: Boolean = false,
    val isAllTutorialFinished: Boolean = false,
    val activeFeedbackMessage: String? = null,
    val isTextMode: Boolean = false,
    val canUndo: Boolean = false,
    val canReset: Boolean = false,
    val highlightPosition: GridPosition? = null,
    val stageConfig: TutorialStageConfig = TutorialStageConfigs[0]
)

val TutorialStageConfigs = listOf(
    TutorialStageConfig(
        stageNumber = 1,
        titleRes = R.string.tutorial_s1_title,
        descRes = R.string.tutorial_s1_desc,
        tipRes = R.string.tutorial_s1_tip,
        initialHighlight = GridPosition(0, 0)
    ),
    TutorialStageConfig(
        stageNumber = 2,
        titleRes = R.string.tutorial_s2_title,
        descRes = R.string.tutorial_s2_desc,
        tipRes = R.string.tutorial_s2_tip,
        initialHighlight = GridPosition(0, 1)
    ),
    TutorialStageConfig(
        stageNumber = 3,
        titleRes = R.string.tutorial_s3_title,
        descRes = R.string.tutorial_s3_desc,
        tipRes = R.string.tutorial_s3_tip,
        initialHighlight = GridPosition(0, 2)
    ),
    TutorialStageConfig(
        stageNumber = 4,
        titleRes = R.string.tutorial_s4_title,
        descRes = R.string.tutorial_s4_desc,
        tipRes = R.string.tutorial_s4_tip,
        initialHighlight = null
    ),
    TutorialStageConfig(
        stageNumber = 5,
        titleRes = R.string.tutorial_s5_title,
        descRes = R.string.tutorial_s5_desc,
        tipRes = R.string.tutorial_s5_tip,
        initialHighlight = GridPosition(0, 1)
    ),
    TutorialStageConfig(
        stageNumber = 6,
        titleRes = R.string.tutorial_s6_title,
        descRes = R.string.tutorial_s6_desc,
        tipRes = R.string.tutorial_s6_tip,
        initialHighlight = null
    ),
    TutorialStageConfig(
        stageNumber = 7,
        titleRes = R.string.tutorial_s7_title,
        descRes = R.string.tutorial_s7_desc,
        tipRes = R.string.tutorial_s7_tip,
        initialHighlight = GridPosition(0, 0)
    )
)

@HiltViewModel
class InteractiveTutorialViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager()
) : ViewModel() {

    private var engine: PuzzleEngine = PuzzleEngine(TutorialPuzzles.Stage1_StartOne)
    private var hasUsedRecoveryInStage6 = false

    private val _uiState = MutableStateFlow(
        TutorialUiState(
            currentStage = 1,
            totalStages = TutorialStageConfigs.size,
            boardState = engine.currentState.toBoardState(),
            stageConfig = TutorialStageConfigs[0],
            highlightPosition = TutorialStageConfigs[0].initialHighlight
        )
    )
    val uiState: StateFlow<TutorialUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            val savedStage = prefs.tutorialStage.coerceIn(1, TutorialStageConfigs.size)
            // If already completed, start at stage 1 on replay, otherwise resume saved stage
            val initialStage = if (prefs.isTutorialCompleted) 1 else savedStage
            loadStage(initialStage)
        }
    }

    fun loadStage(stageNumber: Int) {
        val stageIndex = (stageNumber - 1).coerceIn(0, TutorialStageConfigs.size - 1)
        val config = TutorialStageConfigs[stageIndex]
        val definition = TutorialPuzzles.getStageDefinition(stageNumber)

        engine = PuzzleEngine(definition)
        hasUsedRecoveryInStage6 = false

        // For stage 2 and stage 6, pre-initiate path to demonstrate interaction
        if (stageNumber == 2) {
            engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        } else if (stageNumber == 6) {
            engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
            engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        }

        _uiState.update { current ->
            current.copy(
                currentStage = stageNumber,
                boardState = engine.currentState.toBoardState(),
                stageConfig = config,
                isStageCompleted = false,
                isAllTutorialFinished = false,
                activeFeedbackMessage = null,
                canUndo = engine.currentState.currentPath.length > 1,
                canReset = !engine.currentState.currentPath.isEmpty,
                highlightPosition = config.initialHighlight
            )
        }

        viewModelScope.launch {
            preferencesRepository.setTutorialStage(stageNumber)
        }
    }

    fun onCellEntered(position: GridPosition): Boolean {
        if (_uiState.value.isStageCompleted && _uiState.value.currentStage != 7) {
            return false
        }

        val state = engine.currentState
        val action = if (state.currentPath.isEmpty) {
            PuzzleAction.StartPath(position)
        } else {
            // Check if user is backtracking
            val currentPosList = state.currentPath.positions
            if (currentPosList.size >= 2 && position == currentPosList[currentPosList.size - 2]) {
                PuzzleAction.BacktrackOne
            } else {
                PuzzleAction.ExtendPath(position)
            }
        }

        val result = engine.process(action)
        return handleEngineResult(result, position)
    }

    fun onPointerReleased() {
        // Clear transient error message if any
        if (_uiState.value.activeFeedbackMessage != null && !_uiState.value.isStageCompleted) {
            _uiState.update { it.copy(activeFeedbackMessage = null) }
        }
    }

    private fun handleEngineResult(result: PuzzleEngineResult, targetPosition: GridPosition): Boolean {
        when (result) {
            is PuzzleEngineResult.Accepted -> {
                val newState = result.state
                val currentStage = _uiState.value.currentStage
                var stageSuccess = false

                when (currentStage) {
                    1 -> {
                        // Success when path is initiated at checkpoint 1
                        if (!newState.currentPath.isEmpty) {
                            stageSuccess = true
                        }
                    }
                    2 -> {
                        // Success when path reaches (0, 2) [Checkpoint 2]
                        if (newState.currentPath.contains(GridPosition(0, 2))) {
                            stageSuccess = true
                        }
                    }
                    3 -> {
                        // Success when checkpoints 1, 2, 3 are all visited
                        if (newState.visitedCheckpointCount >= 3) {
                            stageSuccess = true
                        }
                    }
                    4 -> {
                        // Full grid coverage in 2x3 (6 cells)
                        if (newState.gameStatus == GameStatus.COMPLETED) {
                            stageSuccess = true
                        }
                    }
                    5 -> {
                        // 2x2 grid with wall routed around to (1,0)
                        if (newState.gameStatus == GameStatus.COMPLETED) {
                            stageSuccess = true
                        }
                    }
                    6 -> {
                        // Stage 6 teaches recovery (Undo / Reset). Evaluated in onUndoClick / onResetClick.
                        if (hasUsedRecoveryInStage6) {
                            stageSuccess = true
                        }
                    }
                    7 -> {
                        // Full 3x3 puzzle completed
                        if (newState.gameStatus == GameStatus.COMPLETED) {
                            stageSuccess = true
                            onTutorialCompleted()
                        }
                    }
                }

                if (stageSuccess) {
                    audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                    hapticManager.performHaptic(ZynpathHapticEvent.COMPLETION)
                } else if (engine.definition.getCheckpointAt(targetPosition) != null) {
                    audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
                    hapticManager.performHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
                } else if (newState.currentPath.length == 1) {
                    audioManager.playEvent(ZynpathAudioEvent.PATH_START)
                    hapticManager.performHaptic(ZynpathHapticEvent.PATH_START)
                } else {
                    audioManager.playEvent(ZynpathAudioEvent.VALID_MOVE)
                    hapticManager.performHaptic(ZynpathHapticEvent.VALID_MOVE)
                }

                _uiState.update { current ->
                    current.copy(
                        boardState = newState.toBoardState(),
                        isStageCompleted = stageSuccess,
                        activeFeedbackMessage = null,
                        canUndo = newState.currentPath.length > 1,
                        canReset = !newState.currentPath.isEmpty,
                        highlightPosition = if (stageSuccess) null else current.highlightPosition
                    )
                }
                return true
            }
            is PuzzleEngineResult.Rejected -> {
                audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.INVALID_MOVE)
                val feedback = getFeedbackForRejection(result.reason)
                _uiState.update { current ->
                    current.copy(
                        activeFeedbackMessage = feedback
                    )
                }
                return false
            }
        }
    }

    private fun getFeedbackForRejection(reason: MoveRejectionReason): String {
        return when (reason) {
            MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE ->
                "Start at checkpoint 1. Place your finger on 1."
            MoveRejectionReason.NON_ADJACENT ->
                "Diagonal moves are not allowed! Move horizontally or vertically."
            MoveRejectionReason.WRONG_CHECKPOINT_ORDER ->
                "Visit checkpoints in ascending order: 1 → 2 → 3. Do not skip numbers!"
            MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT ->
                "100% grid coverage required! Cover all cells before reaching the final checkpoint."
            MoveRejectionReason.BLOCKED_BY_WALL ->
                "Blocked by a wall! Walls are edge barriers. Route around the barrier."
            MoveRejectionReason.CELL_ALREADY_VISITED ->
                "Cannot cross your own path. Each cell can only be visited once."
            MoveRejectionReason.OUT_OF_BOUNDS ->
                "Movement is outside the grid."
            MoveRejectionReason.GAME_ALREADY_COMPLETED ->
                "Stage already completed!"
            else ->
                "Invalid move. Follow orthogonal adjacent cells."
        }
    }

    fun onUndoClick() {
        val result = engine.process(PuzzleAction.Undo)
        if (result.isAccepted) {
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            if (_uiState.value.currentStage == 6) {
                hasUsedRecoveryInStage6 = true
            }
            _uiState.update { current ->
                current.copy(
                    boardState = result.state.toBoardState(),
                    isStageCompleted = if (current.currentStage == 6) true else current.isStageCompleted,
                    activeFeedbackMessage = if (current.currentStage == 6) "Great! You stepped back with Undo." else null,
                    canUndo = result.state.currentPath.length > 1,
                    canReset = !result.state.currentPath.isEmpty
                )
            }
        }
    }

    fun onResetClick() {
        val result = engine.process(PuzzleAction.Reset)
        if (result.isAccepted) {
            audioManager.playEvent(ZynpathAudioEvent.RESET)
            hapticManager.performHaptic(ZynpathHapticEvent.RESET)
            if (_uiState.value.currentStage == 6) {
                hasUsedRecoveryInStage6 = true
            }
            _uiState.update { current ->
                current.copy(
                    boardState = result.state.toBoardState(),
                    isStageCompleted = if (current.currentStage == 6) true else current.isStageCompleted,
                    activeFeedbackMessage = if (current.currentStage == 6) "Board reset! You can start fresh anytime." else null,
                    canUndo = false,
                    canReset = false
                )
            }
        }
    }

    fun nextStage() {
        val next = _uiState.value.currentStage + 1
        if (next <= TutorialStageConfigs.size) {
            loadStage(next)
        } else {
            onTutorialCompleted()
        }
    }

    fun previousStage() {
        val prev = _uiState.value.currentStage - 1
        if (prev >= 1) {
            loadStage(prev)
        }
    }

    fun toggleTextMode() {
        _uiState.update { it.copy(isTextMode = !it.isTextMode) }
    }

    fun skipTutorial(onFinish: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setTutorialSkipped(true)
            preferencesRepository.setTutorialCompleted(true)
            preferencesRepository.setOnboardingCompleted(true)
            onFinish()
        }
    }

    private fun onTutorialCompleted() {
        _uiState.update { it.copy(isAllTutorialFinished = true, isStageCompleted = true) }
        viewModelScope.launch {
            preferencesRepository.setTutorialCompleted(true)
            preferencesRepository.setOnboardingCompleted(true)
            preferencesRepository.setTutorialStage(7)
        }
    }

    fun replayTutorial() {
        viewModelScope.launch {
            preferencesRepository.resetTutorialProgress()
            loadStage(1)
        }
    }
}
