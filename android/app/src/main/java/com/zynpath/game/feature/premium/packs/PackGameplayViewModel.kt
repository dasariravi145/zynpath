package com.zynpath.game.feature.premium.packs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackProgressRepository
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepository
import com.zynpath.game.core.puzzle.session.GameplayTimer
import com.zynpath.game.core.puzzle.session.SystemTimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackGameplayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val packRepository: PremiumPackRepository,
    private val progressRepository: PremiumPackProgressRepository,
    private val profileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val packId: String = checkNotNull(savedStateHandle.get<String>("packId")) {
        "packId argument missing in SavedStateHandle"
    }

    private val levelIndex: Int = run {
        val arg = savedStateHandle.get<Any>("levelIndex")
        when (arg) {
            is Int -> arg
            is String -> arg.toIntOrNull() ?: 1
            else -> 1
        }
    }

    private val _uiState = MutableStateFlow<PackGameplayUiState>(PackGameplayUiState.Loading)
    val uiState: StateFlow<PackGameplayUiState> = _uiState.asStateFlow()

    private var engine: PuzzleEngine? = null
    private val gameplayTimer = GameplayTimer(SystemTimeProvider())
    private var timerJob: Job? = null
    private val hintEngine = PuzzleHintEngine()

    init {
        loadLevel()
    }

    private fun loadLevel() {
        viewModelScope.launch {
            val manifest = packRepository.getPackManifest(packId)
            if (manifest == null) {
                _uiState.value = PackGameplayUiState.Error("Manifest not found for pack $packId")
                return@launch
            }

            val puzzleResult = packRepository.getPuzzle(packId, levelIndex)
            if (puzzleResult.isFailure) {
                _uiState.value = PackGameplayUiState.Error(
                    puzzleResult.exceptionOrNull()?.message ?: "Failed to load puzzle for level $levelIndex"
                )
                return@launch
            }

            val definition = puzzleResult.getOrThrow()
            val newEngine = PuzzleEngine(definition)
            engine = newEngine

            val profile = profileRepository.getProfile()
            val existingProgress = progressRepository.getLevelProgress(profile.playerId, packId, levelIndex)
            val personalBest = if (existingProgress?.isCompleted == true && existingProgress.bestSolveTimeMs > 0L) {
                existingProgress.bestSolveTimeMs
            } else null

            val userPrefs = preferencesRepository.userPreferencesFlow
            var tapMode = false
            var reducedMotion = false
            launch {
                userPrefs.collect { prefs ->
                    tapMode = prefs.isTapInputMode
                    reducedMotion = prefs.isReducedMotion
                    val current = _uiState.value
                    if (current is PackGameplayUiState.Ready) {
                        _uiState.value = current.copy(
                            isTapInputMode = tapMode,
                            isReducedMotion = reducedMotion
                        )
                    }
                }
            }

            gameplayTimer.start()
            startTimerPolling()

            _uiState.value = PackGameplayUiState.Ready(
                packId = packId,
                packName = manifest.displayName,
                levelIndex = levelIndex,
                totalLevels = manifest.puzzleCount,
                definition = definition,
                gameState = newEngine.currentState,
                boardState = newEngine.currentState.toBoardState(),
                elapsedTimeMs = 0L,
                isUndoAvailable = newEngine.currentState.currentPath.size > 1,
                isResetAvailable = !newEngine.currentState.currentPath.isEmpty,
                personalBestTimeMs = personalBest,
                hasNextLevel = levelIndex < manifest.puzzleCount,
                isTapInputMode = tapMode,
                isReducedMotion = reducedMotion
            )
        }
    }

    private fun startTimerPolling() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var lastEmittedSec = -1L
            while (isActive) {
                delay(200) // Bounded poll interval without excessive recompositions (Prompt 37)
                val current = _uiState.value
                if (current is PackGameplayUiState.Ready && !current.isCompleted) {
                    val elapsed = gameplayTimer.elapsedDurationMs()
                    val currentSec = elapsed / 1000L
                    if (currentSec != lastEmittedSec) {
                        lastEmittedSec = currentSec
                        _uiState.value = current.copy(elapsedTimeMs = elapsed)
                    }
                }
            }
        }
    }

    fun onCellEntered(position: GridPosition): Boolean {
        val currentReady = _uiState.value as? PackGameplayUiState.Ready ?: return false
        if (currentReady.isCompleted) return false

        val activeEngine = engine ?: return false
        val currentState = activeEngine.currentState
        if (currentState.isCompleted || currentState.isPaused) return false

        val currentElapsed = gameplayTimer.elapsedDurationMs()

        // 1. Initial move must start at checkpoint #1
        if (currentState.isNotStarted || currentState.currentPath.isEmpty) {
            val result = activeEngine.process(PuzzleAction.StartPath(position), currentElapsed)
            return if (result.isAccepted) {
                _uiState.value = currentReady.copy(
                    gameState = result.state,
                    boardState = result.state.toBoardState(),
                    isUndoAvailable = result.state.currentPath.size > 1,
                    isResetAvailable = !result.state.currentPath.isEmpty,
                    rejectionMessage = null,
                    hintedCoordinate = null
                )
                true
            } else {
                val reason = (result as? PuzzleEngineResult.Rejected)?.reason
                _uiState.value = currentReady.copy(rejectionMessage = reason?.let { formatRejectionMessage(it) })
                false
            }
        }

        // 2. Dragging on current endpoint: no-op accepted
        if (position == currentState.currentEndpoint) return true

        // 3. Backtracking to an already visited position
        if (currentState.currentPath.contains(position)) {
            val result = activeEngine.process(PuzzleAction.BacktrackTo(position), currentElapsed)
            return if (result.isAccepted) {
                _uiState.value = currentReady.copy(
                    gameState = result.state,
                    boardState = result.state.toBoardState(),
                    isUndoAvailable = result.state.currentPath.size > 1,
                    isResetAvailable = !result.state.currentPath.isEmpty,
                    rejectionMessage = null,
                    hintedCoordinate = null
                )
                true
            } else {
                false
            }
        }

        // 4. Forward extension
        val result = activeEngine.process(PuzzleAction.ExtendPath(position), currentElapsed)
        return if (result.isAccepted) {
            val newState = result.state
            val isCompleted = newState.isCompleted

            if (isCompleted) {
                gameplayTimer.pause()
                val solveTime = gameplayTimer.elapsedDurationMs()
                handleCompletion(currentReady, solveTime)
            } else {
                _uiState.value = currentReady.copy(
                    gameState = newState,
                    boardState = newState.toBoardState(),
                    isUndoAvailable = newState.currentPath.size > 1,
                    isResetAvailable = !newState.currentPath.isEmpty,
                    rejectionMessage = null,
                    hintedCoordinate = null
                )
            }
            true
        } else {
            val reason = (result as? PuzzleEngineResult.Rejected)?.reason
            _uiState.value = currentReady.copy(rejectionMessage = reason?.let { formatRejectionMessage(it) })
            false
        }
    }

    fun undo() {
        val currentReady = _uiState.value as? PackGameplayUiState.Ready ?: return
        if (currentReady.isCompleted) return

        val activeEngine = engine ?: return
        val currentElapsed = gameplayTimer.elapsedDurationMs()
        val result = activeEngine.process(PuzzleAction.BacktrackOne, currentElapsed)
        if (result.isAccepted) {
            val newState = result.state
            _uiState.value = currentReady.copy(
                gameState = newState,
                boardState = newState.toBoardState(),
                isUndoAvailable = newState.currentPath.size > 1,
                isResetAvailable = !newState.currentPath.isEmpty,
                rejectionMessage = null,
                hintedCoordinate = null
            )
        }
    }

    fun reset() {
        val currentReady = _uiState.value as? PackGameplayUiState.Ready ?: return
        if (currentReady.isCompleted) return

        val activeEngine = engine ?: return
        val currentElapsed = gameplayTimer.elapsedDurationMs()
        val result = activeEngine.process(PuzzleAction.ResetPath, currentElapsed)
        if (result.isAccepted) {
            val newState = result.state
            _uiState.value = currentReady.copy(
                gameState = newState,
                boardState = newState.toBoardState(),
                isUndoAvailable = false,
                isResetAvailable = false,
                rejectionMessage = null,
                hintedCoordinate = null
            )
        }
    }

    fun requestHint() {
        val currentReady = _uiState.value as? PackGameplayUiState.Ready ?: return
        if (currentReady.isCompleted || currentReady.isHintLoading) return

        val activeEngine = engine ?: return

        viewModelScope.launch {
            _uiState.value = currentReady.copy(isHintLoading = true, hintMessage = null)

            val request = HintRequest(
                puzzleId = currentReady.definition.puzzleId,
                puzzleVersion = currentReady.definition.puzzleVersion,
                definition = currentReady.definition,
                gameState = activeEngine.currentState,
                currentOrderedPath = activeEngine.currentState.currentPath.positions,
                nextRequiredCheckpoint = activeEngine.currentState.nextRequiredCheckpoint,
                gameMode = GameMode.SOLO
            )

            val result = hintEngine.computeHintAsync(request, Dispatchers.Default)
            val latest = _uiState.value as? PackGameplayUiState.Ready ?: return@launch

            when (result) {
                is HintResult.NextMove -> {
                    _uiState.value = latest.copy(
                        isHintLoading = false,
                        hintedCoordinate = result.nextMove,
                        boardState = latest.gameState.toBoardState(hintedCoordinate = result.nextMove),
                        hintMessage = "Follow the highlighted path forward."
                    )
                }

                is HintResult.RecoveryRequired -> {
                    _uiState.value = latest.copy(
                        isHintLoading = false,
                        hintMessage = "Dead end detected! Undo ${result.stepsToRetract} steps: ${result.explanation}"
                    )
                }

                is HintResult.AlreadyCompleted -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = "Puzzle is already completed!")
                }

                is HintResult.SearchInconclusive -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = result.message)
                }

                is HintResult.UsageLimitReached -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = result.message)
                }

                is HintResult.Cancelled -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = "Hint search was cancelled.")
                }

                is HintResult.InvalidState -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = "Hint unavailable: ${result.reason}")
                }

                is HintResult.HintNotAvailable -> {
                    _uiState.value = latest.copy(isHintLoading = false, hintMessage = "Hint unavailable: ${result.reason}")
                }
            }
        }
    }

    private fun handleCompletion(currentReady: PackGameplayUiState.Ready, solveTimeMs: Long) {
        viewModelScope.launch {
            val def = currentReady.definition
            packRepository.recordLevelCompleted(
                packId = packId,
                levelIndex = levelIndex,
                puzzleId = def.puzzleId,
                puzzleVersion = def.puzzleVersion,
                solveTimeMs = solveTimeMs
            )

            val previousBest = currentReady.personalBestTimeMs
            val isNewBest = previousBest == null || solveTimeMs < previousBest
            val activeEngine = engine ?: return@launch

            _uiState.value = currentReady.copy(
                gameState = activeEngine.currentState,
                boardState = activeEngine.currentState.toBoardState(),
                isCompleted = true,
                completionSolveTimeMs = solveTimeMs,
                personalBestTimeMs = if (isNewBest) solveTimeMs else previousBest,
                isNewPersonalBest = isNewBest,
                elapsedTimeMs = solveTimeMs,
                isUndoAvailable = false,
                isResetAvailable = false
            )
        }
    }

    private fun formatRejectionMessage(reason: MoveRejectionReason): String {
        return when (reason) {
            MoveRejectionReason.NON_ADJACENT -> "Movement must be strictly orthogonal (up, down, left, right)."
            MoveRejectionReason.CELL_ALREADY_VISITED -> "Cell already covered! Each cell must be visited exactly once."
            MoveRejectionReason.BLOCKED_BY_WALL -> "A wall blocks this edge!"
            MoveRejectionReason.OUT_OF_BOUNDS -> "Out of grid boundaries."
            MoveRejectionReason.EXCLUDED_CELL -> "Cell is excluded from the puzzle."
            MoveRejectionReason.WRONG_CHECKPOINT_ORDER -> "Visited checkpoint out of sequence!"
            MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT -> "Cannot enter the final checkpoint until all cells are covered!"
            MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE -> "Path must start at checkpoint #1."
            MoveRejectionReason.GAME_ALREADY_COMPLETED -> "Puzzle is already completed!"
            MoveRejectionReason.GAME_PAUSED -> "Game is paused."
            MoveRejectionReason.INVALID_ACTION -> "Invalid move."
        }
    }
}
