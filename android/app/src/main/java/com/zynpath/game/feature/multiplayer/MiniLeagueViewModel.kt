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
import com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoom
import com.zynpath.game.core.multiplayer.model.toPuzzleDefinition
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
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
 * ViewModel governing Mini League private rooms, 2-5 player lobbies, and live racing (Prompt 23).
 */
@HiltViewModel
class MiniLeagueViewModel @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
    private val authRepository: AuthRepository,
    private val socialRepository: SocialRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _selectedEntryTab = MutableStateFlow(MiniLeagueEntryTab.CREATE)
    private val _roomCodeInput = MutableStateFlow("")
    private val _isCreatingRoom = MutableStateFlow(false)
    private val _isJoiningRoom = MutableStateFlow(false)
    private val _isStartingMatch = MutableStateFlow(false)
    private val _isSendingInvite = MutableStateFlow(false)
    private val _showInviteDialog = MutableStateFlow(false)
    private val _showExitDialog = MutableStateFlow(false)

    private val _puzzleDefinition = MutableStateFlow<PuzzleDefinition?>(null)
    private val _puzzleGameState = MutableStateFlow<PuzzleGameState?>(null)
    private val _boardState = MutableStateFlow<PuzzleBoardState?>(null)
    private val _isValidatingCompletion = MutableStateFlow(false)
    private val _elapsedMatchTimeMs = MutableStateFlow(0L)
    private val _isInputEnabled = MutableStateFlow(false)
    private val _activeReactionPopup = MutableStateFlow<ReactionPopup?>(null)

    private var localEngine: PuzzleEngine? = null
    private var timerJob: Job? = null
    private var reactionDismissJob: Job? = null
    private var lastReactionSentTimestamp: Long = 0L
    private var lastReportedCoveredCells: Int = 0
    private var lastReportedCheckpoint: Int = 0

    val uiState: StateFlow<MiniLeagueUiState> = combine(
        combine(
            authRepository.authState,
            authRepository.currentSession,
            _selectedEntryTab,
            _roomCodeInput,
            _isCreatingRoom,
            _isJoiningRoom,
            _isStartingMatch,
            _isSendingInvite,
            _showInviteDialog
        ) { args -> args },
        combine(
            _showExitDialog,
            multiplayerRepository.currentMiniLeagueRoom,
            multiplayerRepository.miniLeagueIncomingInvitations,
            socialRepository.friends,
            multiplayerRepository.clientMatchState,
            multiplayerRepository.currentSession,
            _puzzleDefinition,
            _puzzleGameState,
            _boardState
        ) { args -> args },
        combine(
            multiplayerRepository.countdownSeconds,
            multiplayerRepository.isConnected,
            multiplayerRepository.errorMessage,
            _isValidatingCompletion,
            multiplayerRepository.matchResults,
            _activeReactionPopup,
            _elapsedMatchTimeMs,
            _isInputEnabled
        ) { args -> args }
    ) { group1, group2, group3 ->
        @Suppress("UNCHECKED_CAST")
        MiniLeagueUiState(
            authState = group1[0] as AuthState,
            authSession = group1[1] as AuthSession?,
            selectedEntryTab = group1[2] as MiniLeagueEntryTab,
            roomCodeInput = group1[3] as String,
            isCreatingRoom = group1[4] as Boolean,
            isJoiningRoom = group1[5] as Boolean,
            isStartingMatch = group1[6] as Boolean,
            isSendingInvite = group1[7] as Boolean,
            showInviteDialog = group1[8] as Boolean,

            showExitDialog = group2[0] as Boolean,
            currentRoom = group2[1] as MiniLeagueRoom?,
            incomingInvitations = group2[2] as List<MiniLeagueInvitation>,
            acceptedFriends = group2[3] as List<FriendItem>,
            clientMatchState = group2[4] as ClientMatchState,
            currentSession = group2[5] as MatchSessionSnapshotDto?,
            puzzleDefinition = group2[6] as PuzzleDefinition?,
            puzzleGameState = group2[7] as PuzzleGameState?,
            boardState = group2[8] as PuzzleBoardState?,

            countdownSeconds = group3[0] as Int?,
            isConnected = group3[1] as Boolean,
            errorMessage = group3[2] as String?,
            isValidatingCompletion = group3[3] as Boolean,
            matchResults = group3[4] as List<MatchResultDto>,
            activeReactionPopup = group3[5] as ReactionPopup?,
            elapsedMatchTimeMs = group3[6] as Long,
            isInputEnabled = group3[7] as Boolean
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MiniLeagueUiState()
    )

    init {
        observeSessionForPuzzle()
        observeMatchState()
        observeIncomingReactions()
        refreshInvitationsAndFriends()
    }

    fun onTabSelected(tab: MiniLeagueEntryTab) {
        _selectedEntryTab.value = tab
    }

    fun onRoomCodeInputChange(input: String) {
        _roomCodeInput.value = input.uppercase().trim()
    }

    fun onShowInviteDialog(show: Boolean) {
        _showInviteDialog.value = show
    }

    fun onShowExitDialog(show: Boolean) {
        _showExitDialog.value = show
    }

    fun refreshInvitationsAndFriends() {
        viewModelScope.launch {
            socialRepository.refreshAll()
            multiplayerRepository.refreshMiniLeagueInvitations()
        }
    }

    fun onCreateRoom(maxParticipants: Int = 5) {
        if (_isCreatingRoom.value) return
        viewModelScope.launch {
            _isCreatingRoom.value = true
            multiplayerRepository.createMiniLeagueRoom(maxParticipants)
            _isCreatingRoom.value = false
        }
    }

    fun onJoinRoomByCode(code: String = _roomCodeInput.value) {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.length != 6 || _isJoiningRoom.value) return
        viewModelScope.launch {
            _isJoiningRoom.value = true
            multiplayerRepository.joinMiniLeagueRoom(cleanCode)
            _isJoiningRoom.value = false
        }
    }

    fun onLeaveRoom() {
        val room = multiplayerRepository.currentMiniLeagueRoom.value ?: return
        viewModelScope.launch {
            multiplayerRepository.leaveMiniLeagueRoom(room.roomId)
            resetLocalGameplay()
        }
    }

    fun onToggleReady() {
        val room = multiplayerRepository.currentMiniLeagueRoom.value ?: return
        val currentUserId = authRepository.currentSession.value?.playerId ?: return
        val myParticipant = room.participants.firstOrNull { it.playerId == currentUserId }
        val newReadyState = !(myParticipant?.isReady ?: false)

        viewModelScope.launch {
            multiplayerRepository.setMiniLeagueReady(room.roomId, newReadyState)
        }
    }

    fun onStartMatch() {
        val room = multiplayerRepository.currentMiniLeagueRoom.value ?: return
        if (_isStartingMatch.value) return
        viewModelScope.launch {
            _isStartingMatch.value = true
            multiplayerRepository.startMiniLeagueMatch(room.roomId)
            _isStartingMatch.value = false
        }
    }

    fun onInviteFriend(friendPublicZynpathId: String) {
        val room = multiplayerRepository.currentMiniLeagueRoom.value ?: return
        if (_isSendingInvite.value) return
        viewModelScope.launch {
            _isSendingInvite.value = true
            multiplayerRepository.inviteFriendToMiniLeague(room.roomId, friendPublicZynpathId)
            _isSendingInvite.value = false
            _showInviteDialog.value = false
        }
    }

    fun onRespondInvitation(invitationId: String, accept: Boolean) {
        viewModelScope.launch {
            multiplayerRepository.respondToMiniLeagueInvitation(invitationId, accept)
        }
    }

    fun onCancelInvitation(invitationId: String) {
        viewModelScope.launch {
            multiplayerRepository.cancelMiniLeagueInvitation(invitationId)
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
                    }
                    ClientMatchState.COMPLETED,
                    ClientMatchState.CANCELLED,
                    ClientMatchState.ERROR -> {
                        _isInputEnabled.value = false
                        _isValidatingCompletion.value = false
                        stopMatchTimer()
                    }
                    else -> {
                        _isInputEnabled.value = false
                        stopMatchTimer()
                    }
                }
            }
        }
    }

    private fun observeIncomingReactions() {
        viewModelScope.launch {
            multiplayerRepository.incomingReaction.collect { pair ->
                if (pair != null) {
                    val (senderPlayerId, reactionCode) = pair
                    val senderName = multiplayerRepository.currentSession.value?.participants
                        ?.firstOrNull { it.playerId == senderPlayerId }?.displayName ?: "Player"
                    showReactionPopup(senderName, reactionCode)
                }
            }
        }
    }

    private fun showReactionPopup(displayName: String, reactionCode: String) {
        reactionDismissJob?.cancel()
        _activeReactionPopup.value = ReactionPopup(displayName, reactionCode)
        reactionDismissJob = viewModelScope.launch {
            delay(2500L)
            _activeReactionPopup.value = null
        }
    }

    fun sendReaction(code: String) {
        val now = System.currentTimeMillis()
        if (now - lastReactionSentTimestamp < 1500L) return
        lastReactionSentTimestamp = now
        multiplayerRepository.sendReaction(code)
    }

    fun handleCellPointerMove(position: GridPosition): Boolean {
        if (!_isInputEnabled.value || _isValidatingCompletion.value) return false
        val engine = localEngine ?: return false
        val currentState = _puzzleGameState.value ?: return false
        val elapsed = _elapsedMatchTimeMs.value

        // 1. Initial touch
        if (currentState.currentPath.isEmpty) {
            val result = engine.process(PuzzleAction.StartPath(position), elapsed)
            return if (result.isAccepted) {
                _puzzleGameState.value = result.state
                _boardState.value = result.state.toBoardState()
                checkAndDispatchProgress(result.state)
                true
            } else {
                false
            }
        }

        // 2. Same cell
        if (currentState.currentPosition == position) return true

        // 3. Backtrack
        val path = currentState.currentPath.positions
        if (path.size >= 2 && path[path.size - 2] == position) {
            val result = engine.process(PuzzleAction.Undo, elapsed)
            return if (result.isAccepted) {
                _puzzleGameState.value = result.state
                _boardState.value = result.state.toBoardState()
                checkAndDispatchProgress(result.state)
                true
            } else {
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
                handleSubmitCompletion(result.state)
            }
            true
        } else {
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

    fun onForfeit() {
        _showExitDialog.value = false
        viewModelScope.launch {
            multiplayerRepository.forfeitMatch()
        }
    }

    private fun startMatchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startedAt = multiplayerRepository.currentSession.value?.startedAt ?: System.currentTimeMillis()
            while (isActive) {
                val now = System.currentTimeMillis()
                _elapsedMatchTimeMs.value = (now - startedAt).coerceAtLeast(0L)
                delay(100L)
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
        _elapsedMatchTimeMs.value = 0L
        _isInputEnabled.value = false
        lastReportedCoveredCells = 0
        lastReportedCheckpoint = 0
    }

    fun leaveMatch() {
        multiplayerRepository.leaveMatch()
        multiplayerRepository.clearMiniLeagueRoom()
        resetLocalGameplay()
    }

    fun clearError() {
        multiplayerRepository.clearError()
    }
}
