package com.zynpath.game.feature.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.RematchStatusDto
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
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.repository.SocialRepository
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
 * ViewModel for the Friend Duel experience (Prompt 22).
 *
 * Implements:
 * - Real accepted friend selection & direct ID challenge
 * - Atomic invitation dispatch, acceptance, decline, and cancellation
 * - Real-time incoming duel notifications
 * - 20s ready window and authoritative 3s countdown
 * - Shared solver-verified continuous puzzle gameplay
 * - Dual progress tracking without pointer streaming
 * - Server-authoritative dual-win completion claims
 * - Interactive rematch experience with new puzzle selection
 */
@HiltViewModel
class FriendDuelViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository,
    private val socialRepository: SocialRepository,
    private val preferencesRepository: PreferencesRepository,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager()
) : ViewModel() {

    private val _puzzleDefinition = MutableStateFlow<PuzzleDefinition?>(null)
    private val _puzzleGameState = MutableStateFlow<PuzzleGameState?>(null)
    private val _boardState = MutableStateFlow<PuzzleBoardState?>(null)
    private val _isValidatingCompletion = MutableStateFlow(false)
    private val _showForfeitDialog = MutableStateFlow(false)
    private val _elapsedMatchTimeMs = MutableStateFlow(0L)
    private val _isInputEnabled = MutableStateFlow(false)
    private val _activeReactionPopup = MutableStateFlow<ReactionPopup?>(null)

    private val _selectedTab = MutableStateFlow(FriendDuelTab.FRIENDS_LIST)
    private val _friendTargetIdInput = MutableStateFlow("")
    private val _isSendingInvitation = MutableStateFlow(false)
    private val _outgoingRemainingSeconds = MutableStateFlow(0)
    private val _isRematchLoading = MutableStateFlow(false)

    private var localEngine: PuzzleEngine? = null
    private var timerJob: Job? = null
    private var invitationTimerJob: Job? = null
    private var reactionDismissJob: Job? = null
    private var lastReactionSentTimestamp: Long = 0L
    private var lastReportedCoveredCells: Int = 0
    private var lastReportedCheckpoint: Int = 0

    val uiState: StateFlow<FriendDuelUiState> = combine(
        combine(
            multiplayerRepository.clientMatchState,
            authRepository.authState,
            authRepository.currentSession,
            multiplayerRepository.currentSession,
            multiplayerRepository.countdownSeconds,
            multiplayerRepository.isConnected,
            multiplayerRepository.errorMessage,
            multiplayerRepository.matchResults,
            socialRepository.friends
        ) { args -> args },
        combine(
            multiplayerRepository.incomingInvitations,
            multiplayerRepository.activeOutgoingInvitation,
            multiplayerRepository.rematchStatus,
            _puzzleDefinition,
            _puzzleGameState,
            _boardState,
            _isValidatingCompletion,
            _showForfeitDialog,
            _elapsedMatchTimeMs
        ) { args -> args },
        combine(
            _isInputEnabled,
            _activeReactionPopup,
            _selectedTab,
            _friendTargetIdInput,
            _isSendingInvitation,
            _outgoingRemainingSeconds,
            _isRematchLoading
        ) { args -> args }
    ) { group1, group2, group3 ->
        @Suppress("UNCHECKED_CAST")
        FriendDuelUiState(
            clientMatchState = group1[0] as ClientMatchState,
            authState = group1[1] as AuthState,
            authSession = group1[2] as AuthSession?,
            currentSession = group1[3] as MatchSessionSnapshotDto?,
            countdownSeconds = group1[4] as Int?,
            isConnected = group1[5] as Boolean,
            errorMessage = group1[6] as String?,
            matchResults = group1[7] as List<MatchResultDto>,
            acceptedFriends = group1[8] as List<FriendItem>,

            incomingInvitations = group2[0] as List<FriendDuelInvitationDto>,
            activeOutgoingInvitation = group2[1] as FriendDuelInvitationDto?,
            rematchStatus = group2[2] as RematchStatusDto?,
            puzzleDefinition = group2[3] as PuzzleDefinition?,
            puzzleGameState = group2[4] as PuzzleGameState?,
            boardState = group2[5] as PuzzleBoardState?,
            isValidatingCompletion = group2[6] as Boolean,
            showForfeitDialog = group2[7] as Boolean,
            elapsedMatchTimeMs = group2[8] as Long,

            isInputEnabled = group3[0] as Boolean,
            activeReactionPopup = group3[1] as ReactionPopup?,
            selectedTab = group3[2] as FriendDuelTab,
            friendTargetIdInput = group3[3] as String,
            isSendingInvitation = group3[4] as Boolean,
            outgoingRemainingSeconds = group3[5] as Int,
            isRematchLoading = group3[6] as Boolean
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendDuelUiState()
    )

    init {
        observeSessionForPuzzle()
        observeMatchState()
        observeOutgoingInvitation()
        observeIncomingReactions()
        observeIncomingInvitations()
        refreshFriendsAndInvitations()
    }

    private fun observeIncomingInvitations() {
        viewModelScope.launch {
            var knownInvitationIds = emptySet<String>()
            multiplayerRepository.incomingInvitations.collect { invitations ->
                val currentIds = invitations.map { it.invitationId }.toSet()
                val newInvitations = currentIds - knownInvitationIds
                if (knownInvitationIds.isNotEmpty() && newInvitations.isNotEmpty()) {
                    audioManager.playEvent(ZynpathAudioEvent.INVITATION_RECEIVED)
                }
                knownInvitationIds = currentIds
            }
        }
    }

    private fun observeSessionForPuzzle() {
        viewModelScope.launch {
            multiplayerRepository.currentSession.collect { session ->
                if (session == null) {
                    resetLocalGameplay()
                    return@collect
                }
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
                    }
                    ClientMatchState.COUNTDOWN -> {
                        _isInputEnabled.value = false
                        stopMatchTimer()
                        audioManager.playEvent(ZynpathAudioEvent.MATCH_READY)
                    }
                    ClientMatchState.COMPLETED -> {
                        _isInputEnabled.value = false
                        _isValidatingCompletion.value = false
                        stopMatchTimer()
                        audioManager.playEvent(ZynpathAudioEvent.MATCH_COMPLETED)
                        hapticManager.triggerHaptic(ZynpathHapticEvent.COMPLETION)
                    }
                    ClientMatchState.CANCELLED,
                    ClientMatchState.ERROR -> {
                        _isInputEnabled.value = false
                        _isValidatingCompletion.value = false
                        stopMatchTimer()
                    }
                    else -> {
                        _isInputEnabled.value = false
                    }
                }
            }
        }
    }

    private fun observeOutgoingInvitation() {
        viewModelScope.launch {
            multiplayerRepository.activeOutgoingInvitation.collect { invitation ->
                invitationTimerJob?.cancel()
                if (invitation != null && invitation.status.isActionable()) {
                    invitationTimerJob = launch {
                        while (isActive) {
                            val remaining = invitation.remainingSeconds()
                            _outgoingRemainingSeconds.value = remaining
                            if (remaining <= 0) break
                            delay(1000L)
                        }
                    }
                } else {
                    _outgoingRemainingSeconds.value = 0
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

                    reactionDismissJob?.cancel()
                    reactionDismissJob = viewModelScope.launch {
                        delay(2500)
                        _activeReactionPopup.value = null
                    }
                }
            }
        }
    }

    fun onTabSelected(tab: FriendDuelTab) {
        _selectedTab.value = tab
    }

    fun onTargetIdInputChanged(input: String) {
        _friendTargetIdInput.value = input.trim().uppercase()
    }

    fun refreshFriendsAndInvitations() {
        viewModelScope.launch {
            socialRepository.refreshAll()
            multiplayerRepository.refreshInvitations()
        }
    }

    fun onInviteFriendClicked(friend: FriendItem) {
        sendInvitation(friend.publicZynpathId)
    }

    fun onDirectChallengeClicked() {
        val targetId = _friendTargetIdInput.value.trim().uppercase()
        if (targetId.isNotEmpty()) {
            sendInvitation(targetId)
        }
    }

    private fun sendInvitation(targetPublicZynpathId: String) {
        viewModelScope.launch {
            _isSendingInvitation.value = true
            multiplayerRepository.sendFriendDuelInvitation(targetPublicZynpathId)
            _isSendingInvitation.value = false
        }
    }

    fun onAcceptInvitationClicked(invitationId: String) {
        viewModelScope.launch {
            multiplayerRepository.acceptFriendDuelInvitation(invitationId)
        }
    }

    fun onDeclineInvitationClicked(invitationId: String) {
        viewModelScope.launch {
            multiplayerRepository.declineFriendDuelInvitation(invitationId)
        }
    }

    fun onCancelInvitationClicked(invitationId: String) {
        viewModelScope.launch {
            multiplayerRepository.cancelFriendDuelInvitation(invitationId)
        }
    }

    fun onReadyClicked() {
        viewModelScope.launch {
            multiplayerRepository.markReady()
        }
    }

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
            } else if (result.state.nextRequiredCheckpointNumber ?: 0 > currentState.nextRequiredCheckpointNumber ?: 0) {
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
        val checkpoint = (state.nextRequiredCheckpointNumber ?: 2) - 1

        if (covered != lastReportedCoveredCells || checkpoint != lastReportedCheckpoint) {
            lastReportedCoveredCells = covered
            lastReportedCheckpoint = checkpoint
            multiplayerRepository.sendProgress(covered, checkpoint)
        }
    }

    private fun handleSubmitCompletion(state: PuzzleGameState) {
        _isInputEnabled.value = false
        _isValidatingCompletion.value = true

        val startedAt = multiplayerRepository.currentSession.value?.startedAt ?: System.currentTimeMillis()
        val durationMs = (System.currentTimeMillis() - startedAt).coerceAtLeast(100L)
        val pathCoords = state.currentPath.positions.map { "${it.row},${it.column}" }
        val movesCount = state.moveCount

        viewModelScope.launch {
            val outcome = multiplayerRepository.submitCompletion(
                pathCoordinates = pathCoords,
                clientDurationMs = durationMs,
                movesCount = movesCount
            )
            _isValidatingCompletion.value = false
            if (outcome != null && !outcome.valid) {
                _isInputEnabled.value = true
            }
        }
    }

    fun onRequestRematch() {
        viewModelScope.launch {
            _isRematchLoading.value = true
            multiplayerRepository.requestRematch()
            _isRematchLoading.value = false
        }
    }

    fun onAcceptRematch() {
        viewModelScope.launch {
            _isRematchLoading.value = true
            multiplayerRepository.respondToRematch(accept = true)
            _isRematchLoading.value = false
        }
    }

    fun onDeclineRematch() {
        viewModelScope.launch {
            _isRematchLoading.value = true
            multiplayerRepository.respondToRematch(accept = false)
            _isRematchLoading.value = false
        }
    }

    fun sendReaction(code: String) {
        val now = System.currentTimeMillis()
        if (now - lastReactionSentTimestamp < 2000L) return
        lastReactionSentTimestamp = now
        multiplayerRepository.sendReaction(code)

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
            delay(2500)
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

    fun leaveMatch() {
        invitationTimerJob?.cancel()
        stopMatchTimer()
        multiplayerRepository.leaveMatch()
        resetLocalGameplay()
    }

    fun clearError() {
        multiplayerRepository.clearError()
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
        _outgoingRemainingSeconds.value = 0
        lastReportedCoveredCells = 0
        lastReportedCheckpoint = 0
    }

    private fun startMatchTimer() {
        val startedAt = multiplayerRepository.currentSession.value?.startedAt ?: System.currentTimeMillis()
        timerJob?.cancel()
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

    private fun parseReaction(code: String): Pair<String, String> {
        return when (code.uppercase()) {
            "THUMBS_UP" -> Pair("👍", "Well Played!")
            "LIGHTNING", "SPEED" -> Pair("⚡", "Speed Run!")
            "FIRE" -> Pair("🔥", "On Fire!")
            "MIND_BLOWN", "MINDBLOWN" -> Pair("🤯", "Incredible!")
            "CLAP" -> Pair("👏", "Great Move!")
            else -> Pair("🤝", "Good Game!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        invitationTimerJob?.cancel()
        stopMatchTimer()
        reactionDismissJob?.cancel()
    }
}
