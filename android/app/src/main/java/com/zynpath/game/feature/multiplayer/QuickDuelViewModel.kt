package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.toPuzzleDefinition
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.audio.NoOpAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.NoOpHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel governing Quick Duel 1v1 online multiplayer experience.
 *
 * Implements Prompt 21 Sections 5-50:
 * - Authenticated matchmaking queue with cancellation.
 * - Server-issued puzzle verification and local engine loading.
 * - Synchronized countdown and strict move rejection before ACTIVE.
 * - Responsive local touch drawing with milestone progress throttling.
 * - Solution-revealing hints strictly disabled for competitive integrity.
 * - Server dual-win completion claim submission and validating overlay.
 * - Authoritative result presentation and safe forfeit dialog.
 * - Ephemeral rate-limited preset reactions.
 * - Complete isolation from Solo and Daily Challenge progression.
 */
@HiltViewModel
class QuickDuelViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager(),
    val walletRepository: com.zynpath.game.core.economy.WalletRepository? = null
) : ViewModel() {

    private val _puzzleDefinition = MutableStateFlow<PuzzleDefinition?>(null)
    private val _puzzleGameState = MutableStateFlow<PuzzleGameState?>(null)
    private val _boardState = MutableStateFlow<PuzzleBoardState?>(null)
    private val _isValidatingCompletion = MutableStateFlow(false)
    private val _showForfeitDialog = MutableStateFlow(false)
    private val _elapsedMatchTimeMs = MutableStateFlow(0L)
    private val _isInputEnabled = MutableStateFlow(false)
    private val _activeReactionPopup = MutableStateFlow<ReactionPopup?>(null)

    private var localEngine: PuzzleEngine? = null
    private var timerJob: Job? = null
    private var reactionDismissJob: Job? = null
    private var lastReactionSentTimestamp: Long = 0L
    private var lastReportedCoveredCells: Int = 0
    private var lastReportedCheckpoint: Int = 0
    private var entryCommittedMatchId: String? = null
    private var settledMatchId: String? = null

    val uiState: StateFlow<QuickDuelUiState> = combine(
        multiplayerRepository.clientMatchState,
        authRepository.authState,
        authRepository.currentSession,
        multiplayerRepository.currentSession,
        multiplayerRepository.ticketStatus,
        multiplayerRepository.countdownSeconds,
        multiplayerRepository.isConnected,
        multiplayerRepository.errorMessage,
        multiplayerRepository.matchResults,
        _puzzleDefinition,
        _puzzleGameState,
        _boardState,
        _isValidatingCompletion,
        _showForfeitDialog,
        _elapsedMatchTimeMs,
        _isInputEnabled,
        _activeReactionPopup
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        QuickDuelUiState(
            clientMatchState = args[0] as ClientMatchState,
            authState = args[1] as AuthState,
            authSession = args[2] as AuthSession?,
            currentSession = args[3] as MatchSessionSnapshotDto?,
            ticketStatus = args[4] as MatchmakingTicketStatus?,
            countdownSeconds = args[5] as Int?,
            isConnected = args[6] as Boolean,
            errorMessage = args[7] as String?,
            matchResults = args[8] as List<MatchResultDto>,
            puzzleDefinition = args[9] as PuzzleDefinition?,
            puzzleGameState = args[10] as PuzzleGameState?,
            boardState = args[11] as PuzzleBoardState?,
            isValidatingCompletion = args[12] as Boolean,
            showForfeitDialog = args[13] as Boolean,
            elapsedMatchTimeMs = args[14] as Long,
            isInputEnabled = args[15] as Boolean,
            activeReactionPopup = args[16] as ReactionPopup?
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuickDuelUiState()
    )

    private var hasPlayedDuelOutcomeAudio = false

    init {
        observeMatchSession()
        observeMatchState()
        observeIncomingReactions()
        observeMatchResults()
    }

    private fun observeMatchResults() {
        viewModelScope.launch {
            multiplayerRepository.matchResults.collect { results ->
                if (results.isNotEmpty() && !hasPlayedDuelOutcomeAudio) {
                    hasPlayedDuelOutcomeAudio = true
                    val myId = authRepository.currentSession.value?.playerId
                    val myResult = results.firstOrNull { it.playerId == myId }
                    if (myResult?.isWinner == true) {
                        audioManager.playEvent(ZynpathAudioEvent.VICTORY)
                        hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
                    } else if (myResult != null) {
                        audioManager.playEvent(ZynpathAudioEvent.DEFEAT)
                    }
                } else if (results.isEmpty()) {
                    hasPlayedDuelOutcomeAudio = false
                }
            }
        }
    }

    private fun observeMatchSession() {
        viewModelScope.launch {
            multiplayerRepository.currentSession.collect { session ->
                if (session == null) {
                    resetLocalGameplay()
                    return@collect
                }

                // If puzzle is assigned and not yet loaded into local engine
                val assignedPuzzle = session.puzzle
                if (assignedPuzzle != null && _puzzleDefinition.value?.fingerprint != assignedPuzzle.fingerprint) {
                    val parsedDef = assignedPuzzle.toPuzzleDefinition()
                    if (parsedDef != null) {
                        _puzzleDefinition.value = parsedDef
                        val engine = PuzzleEngine(parsedDef)
                        localEngine = engine
                        val initialState = engine.currentState
                        _puzzleGameState.value = initialState
                        _boardState.value = initialState.toBoardState()
                        lastReportedCoveredCells = 0
                        lastReportedCheckpoint = 0
                    }
                }
            }
        }
    }

    private fun observeMatchState() {
        viewModelScope.launch {
            multiplayerRepository.clientMatchState.collect { state ->
                when (state) {
                    ClientMatchState.ACTIVE -> {
                        _isInputEnabled.value = !_isValidatingCompletion.value
                        startMatchTimer()
                        audioManager.playEvent(ZynpathAudioEvent.MATCH_STARTED)
                        hapticManager.triggerHaptic(ZynpathHapticEvent.MATCH_STARTED)

                        // Economy: Deduct 15 coins entry fee exactly once at match start
                        val matchId = multiplayerRepository.currentSession.value?.matchId
                        if (matchId != null && entryCommittedMatchId != matchId) {
                            entryCommittedMatchId = matchId
                            viewModelScope.launch {
                                val myId = authRepository.currentSession.value?.playerId ?: "player"
                                walletRepository?.debit(
                                    type = "DUEL_ENTRY",
                                    amount = com.zynpath.game.core.economy.EconomyConfig.QUICK_DUEL_ENTRY_FEE,
                                    idempotencyKey = "duel_entry_${matchId}_$myId",
                                    metadataJson = "{\"matchId\":\"$matchId\"}"
                                )
                            }
                        }
                    }
                    ClientMatchState.COMPLETED -> {
                        _isInputEnabled.value = false
                        stopMatchTimer()
                        _isValidatingCompletion.value = false
                        audioManager.playEvent(ZynpathAudioEvent.MATCH_COMPLETED)
                        hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)

                        // Economy: Settle winner +25 coins, or refund draw +15 coins
                        val matchId = multiplayerRepository.currentSession.value?.matchId
                        if (matchId != null && settledMatchId != matchId) {
                            settledMatchId = matchId
                            viewModelScope.launch {
                                val myId = authRepository.currentSession.value?.playerId ?: "player"
                                val results = multiplayerRepository.matchResults.value
                                val myResult = results.firstOrNull { it.playerId == myId }
                                val hasWinner = results.any { it.isWinner }
                                if (myResult?.isWinner == true) {
                                    walletRepository?.credit(
                                        type = "DUEL_SETTLEMENT",
                                        amount = com.zynpath.game.core.economy.EconomyConfig.QUICK_DUEL_WINNER_PAYOUT,
                                        idempotencyKey = "duel_settlement_${matchId}_$myId",
                                        metadataJson = "{\"matchId\":\"$matchId\",\"result\":\"WIN\"}"
                                    )
                                } else if (!hasWinner && results.size >= 2) {
                                    // Valid draw: refund entry
                                    walletRepository?.credit(
                                        type = "MATCH_REFUND",
                                        amount = com.zynpath.game.core.economy.EconomyConfig.QUICK_DUEL_ENTRY_FEE,
                                        idempotencyKey = "duel_draw_refund_${matchId}_$myId",
                                        metadataJson = "{\"matchId\":\"$matchId\",\"result\":\"DRAW\"}"
                                    )
                                }
                            }
                        }
                    }
                    ClientMatchState.CANCELLED,
                    ClientMatchState.ERROR -> {
                        _isInputEnabled.value = false
                        stopMatchTimer()
                        _isValidatingCompletion.value = false
                        if (state == ClientMatchState.ERROR) {
                            audioManager.playEvent(ZynpathAudioEvent.ERROR)
                            hapticManager.triggerHaptic(ZynpathHapticEvent.ERROR)
                        }

                        // Economy: Refund entry if cancelled or failed after entry was committed
                        val matchId = multiplayerRepository.currentSession.value?.matchId
                        if (matchId != null && entryCommittedMatchId == matchId && settledMatchId != matchId) {
                            settledMatchId = matchId
                            viewModelScope.launch {
                                val myId = authRepository.currentSession.value?.playerId ?: "player"
                                walletRepository?.credit(
                                    type = "MATCH_REFUND",
                                    amount = com.zynpath.game.core.economy.EconomyConfig.QUICK_DUEL_ENTRY_FEE,
                                    idempotencyKey = "duel_cancel_refund_${matchId}_$myId",
                                    metadataJson = "{\"matchId\":\"$matchId\",\"status\":\"${state.name}\"}"
                                )
                            }
                        }
                    }
                    ClientMatchState.COUNTDOWN -> {
                        _isInputEnabled.value = false
                        stopMatchTimer()
                        audioManager.playEvent(ZynpathAudioEvent.MATCH_READY)
                    }
                    else -> {
                        _isInputEnabled.value = false
                    }
                }
            }
        }
    }

    private fun observeIncomingReactions() {
        viewModelScope.launch {
            multiplayerRepository.incomingReaction.collect { pair ->
                if (pair != null) {
                    val (senderId, reactionCode) = pair
                    val myId = authRepository.currentSession.value?.playerId
                    val isLocal = senderId == myId
                    val (emoji, label) = parseReaction(reactionCode)

                    _activeReactionPopup.value = ReactionPopup(
                        senderPlayerId = senderId,
                        isLocalPlayer = isLocal,
                        reactionCode = reactionCode,
                        emojiChar = emoji,
                        labelText = label
                    )

                    // Auto-dismiss reaction after 3 seconds
                    reactionDismissJob?.cancel()
                    reactionDismissJob = viewModelScope.launch {
                        delay(3000)
                        _activeReactionPopup.value = null
                    }
                }
            }
        }
    }

    /**
     * Start Quick Duel matchmaking queue.
     */
    fun startSearching() {
        val auth = authRepository.currentSession.value
        if (auth == null) {
            return
        }
        resetLocalGameplay()
        viewModelScope.launch {
            multiplayerRepository.startQuickDuelSearch()
        }
    }

    fun onStartMatchmaking() = startSearching()

    /**
     * Cancel active matchmaking queue ticket.
     */
    fun cancelSearching() {
        viewModelScope.launch {
            multiplayerRepository.cancelMatchmaking()
        }
    }

    fun onCancelMatchmaking() = cancelSearching()

    /**
     * Confirm ready status in lobby.
     */
    fun markReady() {
        viewModelScope.launch {
            multiplayerRepository.markReady()
        }
    }

    /**
     * Touch drag / tap cell entered on the puzzle board.
     * Processed 100% locally through PuzzleEngine for zero-latency drawing.
     */
    fun onCellEntered(position: GridPosition): Boolean {
        if (!_isInputEnabled.value || _isValidatingCompletion.value) {
            return false
        }
        val engine = localEngine ?: return false
        val currentState = engine.currentState
        if (currentState.isCompleted || currentState.isPaused) {
            return false
        }

        val elapsed = _elapsedMatchTimeMs.value

        // 1. Initial path move
        if (currentState.isNotStarted || currentState.currentPath.isEmpty) {
            val result = engine.process(PuzzleAction.StartPath(position), elapsed)
            return if (result.isAccepted) {
                _puzzleGameState.value = result.state
                _boardState.value = result.state.toBoardState()
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
                _puzzleGameState.value = result.state
                _boardState.value = result.state.toBoardState()
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
            _puzzleGameState.value = result.state
            _boardState.value = result.state.toBoardState()
            checkAndDispatchProgress(result.state)

            if (result.state.isCompleted) {
                audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
                handleSubmitCompletion(result.state)
            } else if (result.state.nextRequiredCheckpoint > currentState.nextRequiredCheckpoint) {
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

    /**
     * Revert the most recent move step.
     */
    fun undo(): Boolean {
        if (!_isInputEnabled.value || _isValidatingCompletion.value) return false
        val engine = localEngine ?: return false
        val result = engine.process(PuzzleAction.Undo, _elapsedMatchTimeMs.value)
        return if (result.isAccepted) {
            _puzzleGameState.value = result.state
            _boardState.value = result.state.toBoardState()
            checkAndDispatchProgress(result.state)
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.triggerHaptic(ZynpathHapticEvent.UNDO)
            true
        } else {
            false
        }
    }

    /**
     * Clear active path and reset back to empty board.
     */
    fun reset(): Boolean {
        if (!_isInputEnabled.value || _isValidatingCompletion.value) return false
        val engine = localEngine ?: return false
        val result = engine.process(PuzzleAction.Reset, _elapsedMatchTimeMs.value)
        return if (result.isAccepted) {
            _puzzleGameState.value = result.state
            _boardState.value = result.state.toBoardState()
            checkAndDispatchProgress(result.state)
            audioManager.playEvent(ZynpathAudioEvent.RESET)
            hapticManager.triggerHaptic(ZynpathHapticEvent.RESET)
            true
        } else {
            false
        }
    }

    private fun checkAndDispatchProgress(state: PuzzleGameState) {
        val covered = state.coveredCellCount
        val checkpoint = (state.nextRequiredCheckpoint - 1).coerceAtLeast(0)

        // Throttle WebSocket updates: send on checkpoint reach, completion, or every 2 cells
        if (checkpoint > lastReportedCheckpoint || state.isCompleted || (covered - lastReportedCoveredCells >= 2)) {
            lastReportedCoveredCells = covered
            lastReportedCheckpoint = checkpoint
            multiplayerRepository.sendProgress(covered, checkpoint)
        }
    }

    private fun handleSubmitCompletion(state: PuzzleGameState) {
        _isValidatingCompletion.value = true
        _isInputEnabled.value = false

        val session = multiplayerRepository.currentSession.value
        val startedAt = session?.startedAt ?: System.currentTimeMillis()
        val durationMs = (System.currentTimeMillis() - startedAt).coerceAtLeast(100L)
        val pathCoords = state.currentPath.positions.map { "${it.row},${it.column}" }
        val movesCount = state.moveCount

        viewModelScope.launch {
            multiplayerRepository.submitCompletion(
                pathCoordinates = pathCoords,
                clientDurationMs = durationMs,
                movesCount = movesCount
            )
        }
    }

    /**
     * Transmit rate-limited preset reaction (👍, ⚡, 🔥, 🤯).
     */
    fun sendReaction(code: String) {
        val now = System.currentTimeMillis()
        if (now - lastReactionSentTimestamp < 2000L) {
            // Rate limit: 1 per 2 seconds
            return
        }
        lastReactionSentTimestamp = now
        multiplayerRepository.sendReaction(code)

        // Show local preview immediately
        val myId = authRepository.currentSession.value?.playerId ?: "me"
        val (emoji, label) = parseReaction(code)
        _activeReactionPopup.value = ReactionPopup(
            senderPlayerId = myId,
            isLocalPlayer = true,
            reactionCode = code,
            emojiChar = emoji,
            labelText = label
        )

        reactionDismissJob?.cancel()
        reactionDismissJob = viewModelScope.launch {
            delay(3000)
            _activeReactionPopup.value = null
        }
    }

    fun requestForfeit() {
        _showForfeitDialog.value = true
    }

    fun dismissForfeitDialog() {
        _showForfeitDialog.value = false
    }

    fun confirmForfeit() {
        _showForfeitDialog.value = false
        _isInputEnabled.value = false
        viewModelScope.launch {
            multiplayerRepository.forfeitMatch()
        }
    }

    fun playAgain() {
        resetLocalGameplay()
        startSearching()
    }

    fun leaveMatch() {
        stopMatchTimer()
        resetLocalGameplay()
        multiplayerRepository.leaveMatch()
    }

    fun clearError() {
        multiplayerRepository.clearError()
    }

    private fun startMatchTimer() {
        timerJob?.cancel()
        val session = multiplayerRepository.currentSession.value
        val startedAt = session?.startedAt ?: System.currentTimeMillis()

        timerJob = viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _elapsedMatchTimeMs.value = (now - startedAt).coerceAtLeast(0L)
                delay(100)
            }
        }
    }

    private fun stopMatchTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun resetLocalGameplay() {
        stopMatchTimer()
        localEngine = null
        _puzzleDefinition.value = null
        _puzzleGameState.value = null
        _boardState.value = null
        _isValidatingCompletion.value = false
        _showForfeitDialog.value = false
        _elapsedMatchTimeMs.value = 0L
        _isInputEnabled.value = false
        _activeReactionPopup.value = null
        lastReportedCoveredCells = 0
        lastReportedCheckpoint = 0
    }

    private fun parseReaction(code: String): Pair<String, String> {
        return when (code.uppercase()) {
            "THUMBS_UP" -> Pair("👍", "Well Played!")
            "LIGHTNING" -> Pair("⚡", "Speed Run!")
            "FIRE" -> Pair("🔥", "On Fire!")
            "MIND_BLOWN" -> Pair("🤯", "Incredible!")
            else -> Pair("🎮", "Good Game!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMatchTimer()
        reactionDismissJob?.cancel()
    }
}
