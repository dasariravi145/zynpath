package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.model.toPuzzleDefinition
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Friends Arena 2-5 player synchronized match gameplay (Prompt 19).
 *
 * Implements:
 * - Shared solver-verified puzzle loading for 2-5 participants.
 * - Independent local board progression and gesture responsiveness.
 * - Server-authoritative solution claims and progress synchronization.
 * - Real-time opponent progress reflection without solution leakage.
 * - Disconnection and reconnection recovery.
 */
@HiltViewModel
class FriendsArenaGameplayViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository,
    private val audioManager: ZynpathAudioManager,
    private val hapticManager: ZynpathHapticManager,
    val walletRepository: com.zynpath.game.core.economy.WalletRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsArenaGameplayUiState())
    val uiState: StateFlow<FriendsArenaGameplayUiState> = _uiState.asStateFlow()

    private var localEngine: PuzzleEngine? = null
    private var timerJob: Job? = null
    private var countdownJob: Job? = null
    private var lastReportedCoveredCells: Int = 0
    private var entryCommittedMatchId: String? = null

    init {
        // Collect current authenticated player ID
        viewModelScope.launch {
            authRepository.currentSession.collect { session ->
                _uiState.value = _uiState.value.copy(localPlayerId = session?.playerId)
            }
        }

        // Collect authoritative match state from repository
        viewModelScope.launch {
            multiplayerRepository.currentFriendsArenaMatch.collect { match ->
                if (match != null) {
                    onMatchUpdated(match)
                }
            }
        }
    }

    /**
     * Initializes match state, connecting to WebSocket and fetching authoritative snapshot.
     */
    fun initializeMatch(matchId: String?, roomId: String?) {
        if (matchId == null && roomId == null) return

        _uiState.value = _uiState.value.copy(
            matchId = matchId ?: _uiState.value.matchId,
            roomId = roomId ?: _uiState.value.roomId
        )

        val targetRoomId = roomId ?: _uiState.value.roomId
        if (targetRoomId != null) {
            multiplayerRepository.subscribeToFriendsArenaRoomEvents(targetRoomId)
        }

        viewModelScope.launch {
            val snapshot = if (matchId != null) {
                multiplayerRepository.getFriendsArenaMatch(matchId)
            } else if (targetRoomId != null) {
                multiplayerRepository.getFriendsArenaMatchByRoom(targetRoomId)
            } else null

            if (snapshot != null) {
                onMatchUpdated(snapshot)
            }
        }
    }

    private fun onMatchUpdated(match: FriendsArenaMatchDto) {
        val currentDef = _uiState.value.puzzleDefinition

        // Load puzzle definition once when assigned
        if (currentDef == null && match.puzzle != null) {
            val def = match.puzzle.toPuzzleDefinition()
            localEngine = PuzzleEngine(def)
            val initialState = PuzzleGameState.initial(def)
            _uiState.value = _uiState.value.copy(
                puzzleDefinition = def,
                puzzleGameState = initialState,
                boardState = initialState.toBoardState()
            )
        }

        val previousStatus = _uiState.value.matchStatus
        val previousCompletedCount = _uiState.value.participants.count { it.isCompleted }
        val newCompletedCount = match.participants.count { it.isCompleted }
        if (newCompletedCount > previousCompletedCount && _uiState.value.participants.isNotEmpty()) {
            audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
            hapticManager.triggerHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
        }

        if (match.status == "ACTIVE" && entryCommittedMatchId != match.matchId) {
            entryCommittedMatchId = match.matchId
            viewModelScope.launch {
                val myId = authRepository.currentSession.value?.playerId ?: "player"
                walletRepository?.debit(
                    type = "ARENA_ENTRY",
                    amount = com.zynpath.game.core.economy.EconomyConfig.ARENA_ENTRY_FEE,
                    idempotencyKey = "arena_entry_${match.matchId}_$myId",
                    metadataJson = "{\"matchId\":\"${match.matchId}\"}"
                )
            }
        }

        _uiState.value = _uiState.value.copy(
            matchId = match.matchId,
            roomId = match.roomId,
            matchStatus = match.status,
            participants = match.participants,
            results = match.results,
            isInputEnabled = (match.status == "ACTIVE" || match.status == "COMPLETING") && !_uiState.value.isValidatingCompletion
        )

        // Handle state transitions
        if (match.status == "COUNTDOWN" && previousStatus != "COUNTDOWN") {
            startCountdown()
        } else if ((match.status == "ACTIVE" || match.status == "COMPLETING") && timerJob == null) {
            stopCountdown()
            startMatchTimer(match.startedAt)
            if (previousStatus == "COUNTDOWN") {
                audioManager.playEvent(ZynpathAudioEvent.MATCH_STARTED)
                hapticManager.triggerHaptic(ZynpathHapticEvent.MATCH_STARTED)
            }
        } else if (match.status == "COMPLETED") {
            stopMatchTimer()
            audioManager.playEvent(ZynpathAudioEvent.MATCH_COMPLETED)
            hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(countdownSeconds = 3)
        countdownJob = viewModelScope.launch {
            audioManager.playEvent(ZynpathAudioEvent.MATCH_READY)
            for (sec in 3 downTo 1) {
                _uiState.value = _uiState.value.copy(countdownSeconds = sec)
                delay(1000L)
            }
            _uiState.value = _uiState.value.copy(countdownSeconds = 0)
        }
    }

    private fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun startMatchTimer(startedAt: Long?) {
        timerJob?.cancel()
        val baseTime = startedAt ?: System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - baseTime
                _uiState.value = _uiState.value.copy(elapsedMatchTimeMs = maxOf(0L, elapsed))
                delay(200L)
            }
        }
    }

    private fun stopMatchTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Touch drag / cell entered interaction.
     */
    fun onCellEntered(position: GridPosition): Boolean {
        if (!_uiState.value.isInputEnabled || _uiState.value.isValidatingCompletion) return false
        val engine = localEngine ?: return false
        val currentState = _uiState.value.puzzleGameState ?: return false

        if (currentState.isCompleted || currentState.isPaused) {
            return false
        }

        val elapsed = _uiState.value.elapsedMatchTimeMs

        // 1. Initial path move
        if (currentState.isNotStarted || currentState.currentPath.isEmpty) {
            val result = engine.process(PuzzleAction.StartPath(position), elapsed)
            return if (result.isAccepted) {
                _uiState.value = _uiState.value.copy(
                    puzzleGameState = result.state,
                    boardState = result.state.toBoardState()
                )
                checkAndDispatchProgress(result.state)
                audioManager.playEvent(ZynpathAudioEvent.PATH_START)
                hapticManager.triggerHaptic(ZynpathHapticEvent.PATH_START)
                true
            } else {
                audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
                hapticManager.triggerHaptic(ZynpathHapticEvent.INVALID_MOVE)
                false
            }
        }

        // 2. Already at current endpoint
        if (position == currentState.currentEndpoint) {
            return true
        }

        // 3. Backtrack
        if (currentState.currentPath.contains(position)) {
            val result = engine.process(PuzzleAction.BacktrackTo(position), elapsed)
            return if (result.isAccepted) {
                _uiState.value = _uiState.value.copy(
                    puzzleGameState = result.state,
                    boardState = result.state.toBoardState()
                )
                checkAndDispatchProgress(result.state)
                audioManager.playEvent(ZynpathAudioEvent.UNDO)
                hapticManager.triggerHaptic(ZynpathHapticEvent.UNDO)
                true
            } else {
                audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
                hapticManager.triggerHaptic(ZynpathHapticEvent.INVALID_MOVE)
                false
            }
        }

        // 4. Forward move
        val result = engine.process(PuzzleAction.ExtendPath(position), elapsed)
        return if (result.isAccepted) {
            _uiState.value = _uiState.value.copy(
                puzzleGameState = result.state,
                boardState = result.state.toBoardState()
            )
            checkAndDispatchProgress(result.state)

            if (result.state.isCompleted) {
                audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
                handleSubmitCompletion(result.state)
            } else if ((result.state.nextRequiredCheckpointNumber ?: 0) > (currentState.nextRequiredCheckpointNumber ?: 0)) {
                audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
                hapticManager.triggerHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
            } else {
                audioManager.playEvent(ZynpathAudioEvent.VALID_MOVE)
            }
            true
        } else {
            audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
            hapticManager.triggerHaptic(ZynpathHapticEvent.INVALID_MOVE)
            false
        }
    }

    fun onUndo(): Boolean {
        if (!_uiState.value.canUndo) return false
        val engine = localEngine ?: return false
        val result = engine.process(PuzzleAction.Undo, _uiState.value.elapsedMatchTimeMs)
        return if (result.isAccepted) {
            _uiState.value = _uiState.value.copy(
                puzzleGameState = result.state,
                boardState = result.state.toBoardState()
            )
            checkAndDispatchProgress(result.state)
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.triggerHaptic(ZynpathHapticEvent.UNDO)
            true
        } else false
    }

    fun onReset(): Boolean {
        if (!_uiState.value.canReset) return false
        val engine = localEngine ?: return false
        val result = engine.process(PuzzleAction.Reset, _uiState.value.elapsedMatchTimeMs)
        return if (result.isAccepted) {
            _uiState.value = _uiState.value.copy(
                puzzleGameState = result.state,
                boardState = result.state.toBoardState()
            )
            checkAndDispatchProgress(result.state)
            audioManager.playEvent(ZynpathAudioEvent.RESET)
            hapticManager.triggerHaptic(ZynpathHapticEvent.RESET)
            true
        } else false
    }

    private fun checkAndDispatchProgress(state: PuzzleGameState) {
        val covered = state.currentPath.size
        val cp = state.nextRequiredCheckpointNumber ?: 1
        val matchId = _uiState.value.matchId ?: return

        if (covered != lastReportedCoveredCells) {
            lastReportedCoveredCells = covered
            viewModelScope.launch {
                multiplayerRepository.updateFriendsArenaProgress(matchId, covered, cp)
            }
        }
    }

    private fun handleSubmitCompletion(state: PuzzleGameState) {
        val matchId = _uiState.value.matchId ?: return
        _uiState.value = _uiState.value.copy(
            isValidatingCompletion = true,
            isInputEnabled = false
        )

        viewModelScope.launch {
            val coords = state.currentPath.positions.map { "${it.row},${it.col}" }
            val outcome = multiplayerRepository.submitFriendsArenaClaim(
                matchId = matchId,
                pathCoordinates = coords,
                clientDurationMs = _uiState.value.elapsedMatchTimeMs
            )

            _uiState.value = _uiState.value.copy(isValidatingCompletion = false)
            if (outcome != null && !outcome.valid) {
                _uiState.value = _uiState.value.copy(errorMessage = outcome.rejectionReason ?: "Solution rejected")
            }
        }
    }

    fun onShowForfeitDialog() {
        _uiState.value = _uiState.value.copy(showForfeitDialog = true)
    }

    fun onDismissForfeitDialog() {
        _uiState.value = _uiState.value.copy(showForfeitDialog = false)
    }

    fun onConfirmForfeit(onExited: () -> Unit) {
        val matchId = _uiState.value.matchId
        _uiState.value = _uiState.value.copy(showForfeitDialog = false, isForfeiting = true)
        viewModelScope.launch {
            if (matchId != null) {
                multiplayerRepository.forfeitFriendsArenaMatch(matchId)
            }
            stopMatchTimer()
            stopCountdown()
            multiplayerRepository.clearFriendsArenaMatch()
            onExited()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopMatchTimer()
        stopCountdown()
    }
}
