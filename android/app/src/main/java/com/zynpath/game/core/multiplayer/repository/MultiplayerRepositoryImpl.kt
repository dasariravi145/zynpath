package com.zynpath.game.core.multiplayer.repository

import android.util.Log
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.multiplayer.api.MultiplayerApiService
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationStatus
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.FriendsArenaMemberDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchParticipantDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.model.PuzzleAssignmentDto
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchState
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.RematchState
import com.zynpath.game.core.multiplayer.model.RematchStatusDto
import com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod
import com.zynpath.game.core.multiplayer.model.LeaderboardResponse
import com.zynpath.game.core.multiplayer.model.MatchHistoryResponse
import com.zynpath.game.core.multiplayer.model.MatchDetails
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.model.PublicCompetitiveStats
import com.zynpath.game.core.multiplayer.websocket.IncomingMultiplayerEvent
import com.zynpath.game.core.multiplayer.websocket.MultiplayerWebSocketClient
import com.zynpath.game.core.network.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiplayerRepositoryImpl @Inject constructor(
    private val multiplayerApiService: MultiplayerApiService,
    private val webSocketClient: MultiplayerWebSocketClient,
    private val secureTokenStorage: SecureTokenStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MultiplayerRepository {

    companion object {
        private const val TAG = "MultiplayerRepo"
        private const val POLL_INTERVAL_MS = 2000L
        private const val QUEUE_TIMEOUT_MS = 45000L
    }

    private val scope = CoroutineScope(ioDispatcher)

    private val _clientMatchState = MutableStateFlow(ClientMatchState.IDLE)
    override val clientMatchState: StateFlow<ClientMatchState> = _clientMatchState.asStateFlow()

    private val _currentSession = MutableStateFlow<MatchSessionSnapshotDto?>(null)
    override val currentSession: StateFlow<MatchSessionSnapshotDto?> = _currentSession.asStateFlow()

    private val _ticketStatus = MutableStateFlow<MatchmakingTicketStatus?>(null)
    override val ticketStatus: StateFlow<MatchmakingTicketStatus?> = _ticketStatus.asStateFlow()

    private val _opponentProgress = MutableStateFlow<Map<String, MatchParticipantDto>>(emptyMap())
    override val opponentProgress: StateFlow<Map<String, MatchParticipantDto>> = _opponentProgress.asStateFlow()

    private val _countdownSeconds = MutableStateFlow<Int?>(null)
    override val countdownSeconds: StateFlow<Int?> = _countdownSeconds.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _matchResults = MutableStateFlow<List<MatchResultDto>>(emptyList())
    override val matchResults: StateFlow<List<MatchResultDto>> = _matchResults.asStateFlow()

    private val _incomingReaction = MutableStateFlow<Pair<String, String>?>(null)
    override val incomingReaction: StateFlow<Pair<String, String>?> = _incomingReaction.asStateFlow()

    private val _incomingInvitations = MutableStateFlow<List<FriendDuelInvitationDto>>(emptyList())
    override val incomingInvitations: StateFlow<List<FriendDuelInvitationDto>> = _incomingInvitations.asStateFlow()

    private val _activeOutgoingInvitation = MutableStateFlow<FriendDuelInvitationDto?>(null)
    override val activeOutgoingInvitation: StateFlow<FriendDuelInvitationDto?> = _activeOutgoingInvitation.asStateFlow()

    private val _rematchStatus = MutableStateFlow<RematchStatusDto?>(null)
    override val rematchStatus: StateFlow<RematchStatusDto?> = _rematchStatus.asStateFlow()

    private val _currentMiniLeagueRoom = MutableStateFlow<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?>(null)
    override val currentMiniLeagueRoom: StateFlow<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?> = _currentMiniLeagueRoom.asStateFlow()

    private val _miniLeagueIncomingInvitations = MutableStateFlow<List<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>>(emptyList())
    override val miniLeagueIncomingInvitations: StateFlow<List<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>> = _miniLeagueIncomingInvitations.asStateFlow()

    private val _currentFriendsArenaRoom = MutableStateFlow<FriendsArenaRoomDto?>(null)
    override val currentFriendsArenaRoom: StateFlow<FriendsArenaRoomDto?> = _currentFriendsArenaRoom.asStateFlow()

    private val _currentFriendsArenaMatch = MutableStateFlow<FriendsArenaMatchDto?>(null)
    override val currentFriendsArenaMatch: StateFlow<FriendsArenaMatchDto?> = _currentFriendsArenaMatch.asStateFlow()

    private val _friendsArenaRematchRequested = MutableStateFlow<String?>(null)
    override val friendsArenaRematchRequested: StateFlow<String?> = _friendsArenaRematchRequested.asStateFlow()

    private var pollingJob: Job? = null
    private var countdownJob: Job? = null

    init {
        // Collect WebSocket incoming events
        scope.launch {
            webSocketClient.incomingEvents.collect { event ->
                handleIncomingEvent(event)
            }
        }

        // Collect WebSocket connection state with authoritative state reconciliation on reconnect
        scope.launch {
            var wasConnected = false
            webSocketClient.isConnected.collect { connected ->
                _isConnected.value = connected
                if (connected && !wasConnected) {
                    val activeSession = _currentSession.value
                    if (activeSession != null && (
                        _clientMatchState.value == ClientMatchState.ACTIVE ||
                        _clientMatchState.value == ClientMatchState.READY ||
                        _clientMatchState.value == ClientMatchState.RECONNECTING
                    )) {
                        // Prompt 38 Sections 36 & 37: Reconcile authoritative match state upon reconnect
                        val token = secureTokenStorage.getSessionToken()
                        if (token != null) {
                            try {
                                when (val res = multiplayerApiService.getMatchSnapshot(token, activeSession.matchId)) {
                                    is NetworkResult.Success -> {
                                        val snapshot = res.data
                                        _currentSession.value = snapshot
                                        if (snapshot.results.isNotEmpty()) {
                                            _matchResults.value = snapshot.results
                                        }
                                        mapMatchStateToClientState(snapshot.matchState)
                                    }
                                    else -> {}
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }
                wasConnected = connected
            }
        }
    }

    override suspend fun startQuickDuelSearch(): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
        if (token.isNullOrBlank()) {
            _errorMessage.value = "Sign in to your Zynpath account to play online multiplayer"
            _clientMatchState.value = ClientMatchState.ERROR
            return@withContext false
        }

        _errorMessage.value = null
        _clientMatchState.value = ClientMatchState.SEARCHING

        // Connect WebSocket early for instant match event notifications
        webSocketClient.connect(token, null)

        when (val res = multiplayerApiService.enqueueQuickDuel(token)) {
            is NetworkResult.Success -> {
                val ticket = res.data
                _ticketStatus.value = ticket
                if (ticket.status == "MATCH_FOUND" && ticket.matchId != null) {
                    onMatchFound(ticket.matchId)
                } else {
                    startStatusPolling(token)
                }
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                _clientMatchState.value = ClientMatchState.ERROR
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to connect to matchmaking"
                _clientMatchState.value = ClientMatchState.ERROR
                false
            }
        }
    }

    override suspend fun cancelMatchmaking(): Boolean = withContext(ioDispatcher) {
        stopStatusPolling()
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val result = multiplayerApiService.cancelQuickDuel(token)
        _ticketStatus.value = null
        _clientMatchState.value = ClientMatchState.IDLE
        webSocketClient.disconnect()
        result is NetworkResult.Success
    }

    override suspend fun createFriendDuel(targetPublicZynpathId: String): Boolean = withContext(ioDispatcher) {
        val invitation = sendFriendDuelInvitation(targetPublicZynpathId)
        invitation != null
    }

    override suspend fun sendFriendDuelInvitation(targetPublicZynpathId: String): FriendDuelInvitationDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
        if (token.isNullOrBlank()) {
            _errorMessage.value = "Sign in to your Zynpath account to challenge friends"
            _clientMatchState.value = ClientMatchState.ERROR
            return@withContext null
        }

        _errorMessage.value = null
        // Connect WS early for invitation signaling
        webSocketClient.connect(token, null)

        when (val res = multiplayerApiService.sendInvitation(token, targetPublicZynpathId)) {
            is NetworkResult.Success -> {
                val invitation = res.data
                _activeOutgoingInvitation.value = invitation
                // If auto-accepted (e.g. cross-invitation race resolution)
                if (invitation.matchId != null) {
                    onMatchFound(invitation.matchId)
                }
                invitation
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to send invitation"
                null
            }
        }
    }

    override suspend fun acceptFriendDuelInvitation(invitationId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        _errorMessage.value = null

        when (val res = multiplayerApiService.acceptInvitation(token, invitationId)) {
            is NetworkResult.Success -> {
                val snapshot = res.data
                _currentSession.value = snapshot
                _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invitationId }
                webSocketClient.connect(token, snapshot.matchId)
                mapMatchStateToClientState(snapshot.matchState)
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to accept invitation"
                false
            }
        }
    }

    override suspend fun declineFriendDuelInvitation(invitationId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.declineInvitation(token, invitationId)) {
            is NetworkResult.Success -> {
                _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invitationId }
                true
            }
            else -> false
        }
    }

    override suspend fun cancelFriendDuelInvitation(invitationId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.cancelInvitation(token, invitationId)) {
            is NetworkResult.Success -> {
                _activeOutgoingInvitation.value = null
                true
            }
            else -> false
        }
    }

    override suspend fun refreshInvitations() = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext
        when (val incomingRes = multiplayerApiService.getIncomingInvitations(token)) {
            is NetworkResult.Success -> {
                _incomingInvitations.value = incomingRes.data
            }
            else -> {}
        }
        when (val outgoingRes = multiplayerApiService.getOutgoingInvitations(token)) {
            is NetworkResult.Success -> {
                _activeOutgoingInvitation.value = outgoingRes.data.firstOrNull { it.status == FriendDuelInvitationStatus.PENDING }
            }
            else -> {}
        }
    }

    override suspend fun requestRematch(): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val session = _currentSession.value ?: return@withContext false
        _errorMessage.value = null

        when (val res = multiplayerApiService.requestRematch(token, session.matchId)) {
            is NetworkResult.Success -> {
                _rematchStatus.value = res.data
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to request rematch"
                false
            }
        }
    }

    override suspend fun respondToRematch(accept: Boolean): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val session = _currentSession.value ?: return@withContext false
        _errorMessage.value = null

        when (val res = multiplayerApiService.respondToRematch(token, session.matchId, accept)) {
            is NetworkResult.Success -> {
                val status = res.data
                _rematchStatus.value = status
                if (status.status == RematchState.ACCEPTED && status.newMatchId != null) {
                    onMatchFound(status.newMatchId)
                }
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to respond to rematch"
                false
            }
        }
    }

    override fun clearRematch() {
        _rematchStatus.value = null
    }

    override suspend fun createMiniLeagueRoom(maxParticipants: Int): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
        if (token.isNullOrBlank()) {
            _errorMessage.value = "Sign in to your Zynpath account to create a Mini League room"
            return@withContext null
        }
        _errorMessage.value = null
        when (val res = multiplayerApiService.createMiniLeagueRoom(token, "Mini League", maxParticipants)) {
            is NetworkResult.Success -> {
                val room = res.data
                _currentMiniLeagueRoom.value = room
                webSocketClient.connect(token, null)
                room
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to create Mini League room"
                null
            }
        }
    }

    override suspend fun getMiniLeagueRoom(roomId: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getMiniLeagueRoom(token, roomId)) {
            is NetworkResult.Success -> {
                _currentMiniLeagueRoom.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun getMiniLeagueRoomByCode(roomCode: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getMiniLeagueRoomByCode(token, roomCode)) {
            is NetworkResult.Success -> {
                _currentMiniLeagueRoom.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun joinMiniLeagueRoom(roomCode: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
        if (token.isNullOrBlank()) {
            _errorMessage.value = "Sign in to your Zynpath account to join a Mini League room"
            return@withContext null
        }
        _errorMessage.value = null
        when (val res = multiplayerApiService.joinMiniLeagueRoom(token, roomCode)) {
            is NetworkResult.Success -> {
                val room = res.data
                _currentMiniLeagueRoom.value = room
                webSocketClient.connect(token, null)
                room
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to join Mini League room"
                null
            }
        }
    }

    override suspend fun leaveMiniLeagueRoom(roomId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.leaveMiniLeagueRoom(token, roomId)) {
            is NetworkResult.Success -> {
                _currentMiniLeagueRoom.value = null
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun setMiniLeagueReady(roomId: String, ready: Boolean): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.setMiniLeagueReady(token, roomId, ready)) {
            is NetworkResult.Success -> {
                _currentMiniLeagueRoom.value = res.data
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun startMiniLeagueMatch(roomId: String): MatchSessionSnapshotDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.startMiniLeagueMatch(token, roomId)) {
            is NetworkResult.Success -> {
                val snapshot = res.data
                _currentSession.value = snapshot
                webSocketClient.subscribeMatch(snapshot.matchId)
                mapMatchStateToClientState(snapshot.matchState)
                snapshot
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to start Mini League match"
                null
            }
        }
    }

    override suspend fun inviteFriendToMiniLeague(roomId: String, friendPublicZynpathId: String): com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.inviteToMiniLeague(token, roomId, friendPublicZynpathId)) {
            is NetworkResult.Success -> res.data
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun respondToMiniLeagueInvitation(invitationId: String, accept: Boolean): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.respondToMiniLeagueInvitation(token, invitationId, accept)) {
            is NetworkResult.Success -> {
                _miniLeagueIncomingInvitations.value = _miniLeagueIncomingInvitations.value.filter { it.invitationId != invitationId }
                if (accept) {
                    _currentMiniLeagueRoom.value = res.data
                    webSocketClient.connect(token, null)
                }
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun cancelMiniLeagueInvitation(invitationId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = multiplayerApiService.cancelMiniLeagueInvitation(token, invitationId)) {
            is NetworkResult.Success -> true
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun refreshMiniLeagueInvitations() = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext
        when (val res = multiplayerApiService.getMiniLeagueIncomingInvitations(token)) {
            is NetworkResult.Success -> {
                _miniLeagueIncomingInvitations.value = res.data
            }
            else -> {}
        }
    }

    override fun clearMiniLeagueRoom() {
        _currentMiniLeagueRoom.value = null
    }

    // --- Friends Arena Operations (Prompt 18) ---

    override suspend fun createFriendsArenaRoom(): FriendsArenaRoomDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: run {
            _errorMessage.value = "Sign in to create a Friends Arena room"
            return@withContext null
        }
        when (val res = multiplayerApiService.createFriendsArenaRoom(token)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaRoom.value = res.data
                webSocketClient.connect(token, null)
                webSocketClient.subscribeFriendsArenaRoom(res.data.roomId)
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to connect to room service"
                null
            }
        }
    }

    override suspend fun getFriendsArenaRoom(roomId: String): FriendsArenaRoomDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getFriendsArenaRoom(token, roomId)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaRoom.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun getFriendsArenaRoomByCode(roomCode: String): FriendsArenaRoomDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getFriendsArenaRoomByCode(token, roomCode)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaRoom.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun joinFriendsArenaRoom(roomCode: String): FriendsArenaRoomDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: run {
            _errorMessage.value = "Sign in to join an online Friends Arena room"
            return@withContext null
        }
        when (val res = multiplayerApiService.joinFriendsArenaRoom(token, roomCode)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaRoom.value = res.data
                webSocketClient.connect(token, null)
                webSocketClient.subscribeFriendsArenaRoom(res.data.roomId)
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to connect to room service"
                null
            }
        }
    }

    override suspend fun leaveFriendsArenaRoom(roomId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val result = multiplayerApiService.leaveFriendsArenaRoom(token, roomId)
        _currentFriendsArenaRoom.value = null
        result is NetworkResult.Success
    }

    override suspend fun startFriendsArenaMatch(roomId: String): FriendsArenaRoomDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: run {
            _errorMessage.value = "Authentication required to start match"
            return@withContext null
        }
        when (val res = multiplayerApiService.startFriendsArenaMatch(token, roomId)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaRoom.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to start match"
                null
            }
        }
    }

    override fun clearFriendsArenaRoom() {
        _currentFriendsArenaRoom.value = null
    }

    override fun subscribeToFriendsArenaRoomEvents(roomId: String) {
        scope.launch {
            val token = secureTokenStorage.getSessionToken()
            if (token != null) {
                webSocketClient.connect(token, null)
                webSocketClient.subscribeFriendsArenaRoom(roomId)
            }
        }
    }

    // --- Friends Arena Match Gameplay (Prompt 19) ---

    override suspend fun getFriendsArenaMatch(matchId: String): FriendsArenaMatchDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getFriendsArenaMatch(token, matchId)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaMatch.value = res.data
                res.data
            }
            else -> null
        }
    }

    override suspend fun getFriendsArenaMatchByRoom(roomId: String): FriendsArenaMatchDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.getFriendsArenaMatchByRoom(token, roomId)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaMatch.value = res.data
                res.data
            }
            else -> null
        }
    }

    override suspend fun updateFriendsArenaProgress(matchId: String, coveredCells: Int, lastCheckpoint: Int): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val res = multiplayerApiService.updateFriendsArenaProgress(token, matchId, coveredCells, lastCheckpoint)
        res is NetworkResult.Success
    }

    override suspend fun submitFriendsArenaClaim(matchId: String, pathCoordinates: List<String>, clientDurationMs: Long): SolutionClaimOutcome? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        when (val res = multiplayerApiService.submitFriendsArenaClaim(token, matchId, pathCoordinates, clientDurationMs)) {
            is NetworkResult.Success -> res.data
            is NetworkResult.Error -> SolutionClaimOutcome(valid = false, rejectionReason = res.message)
            is NetworkResult.Exception -> SolutionClaimOutcome(valid = false, rejectionReason = res.throwable.message)
        }
    }

    override suspend fun forfeitFriendsArenaMatch(matchId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        val res = multiplayerApiService.forfeitFriendsArenaMatch(token, matchId)
        res is NetworkResult.Success
    }

    override fun clearFriendsArenaMatch() {
        _currentFriendsArenaMatch.value = null
    }

    override suspend fun requestFriendsArenaRematch(roomId: String): FriendsArenaMatchDto? = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: run {
            _errorMessage.value = "Authentication required for rematch"
            return@withContext null
        }
        when (val res = multiplayerApiService.requestFriendsArenaRematch(token, roomId)) {
            is NetworkResult.Success -> {
                _currentFriendsArenaMatch.value = res.data
                res.data
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Failed to request rematch"
                null
            }
        }
    }

    override fun clearFriendsArenaRematchRequested() {
        _friendsArenaRematchRequested.value = null
    }

    override suspend fun createMiniLeague(roomName: String, maxParticipants: Int): Boolean = withContext(ioDispatcher) {
        createMiniLeagueRoom(maxParticipants) != null
    }

    override suspend fun markReady(): Boolean = withContext(ioDispatcher) {
        val session = _currentSession.value ?: return@withContext false
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false

        // Send via WebSocket first
        webSocketClient.sendReady(session.matchId)
        // Also call REST as fallback
        val res = multiplayerApiService.markReady(token, session.matchId)
        if (res is NetworkResult.Success) {
            _clientMatchState.value = ClientMatchState.READY
            true
        } else {
            false
        }
    }

    override suspend fun submitCompletion(
        pathCoordinates: List<String>,
        clientDurationMs: Long,
        movesCount: Int
    ): SolutionClaimOutcome? = withContext(ioDispatcher) {
        val session = _currentSession.value ?: return@withContext null
        val token = secureTokenStorage.getSessionToken() ?: return@withContext null
        val puzzle = session.puzzle ?: return@withContext null
        // Low-latency WebSocket claim dispatch
        webSocketClient.sendClaim(
            matchId = session.matchId,
            puzzleId = puzzle.puzzleId,
            pathCoordinates = pathCoordinates,
            clientReportedSolveTimeMs = clientDurationMs,
            movesCount = movesCount
        )

        when (val res = multiplayerApiService.submitClaim(
            token,
            session.matchId,
            puzzle.puzzleId,
            pathCoordinates,
            clientDurationMs,
            movesCount
        )) {
            is NetworkResult.Success -> {
                val outcome = res.data
                if (outcome.valid) {
                    _clientMatchState.value = ClientMatchState.COMPLETED
                } else {
                    _errorMessage.value = outcome.rejectionReason ?: "Solution rejected by server"
                }
                outcome
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                null
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                null
            }
        }
    }

    override suspend fun forfeitMatch(): Boolean = withContext(ioDispatcher) {
        val session = _currentSession.value ?: return@withContext false
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false

        // Low-latency WebSocket forfeit dispatch
        webSocketClient.sendForfeit(session.matchId)

        when (val res = multiplayerApiService.forfeitMatch(token, session.matchId)) {
            is NetworkResult.Success -> {
                _clientMatchState.value = ClientMatchState.CANCELLED
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun reconnectToMatch(matchId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        _clientMatchState.value = ClientMatchState.RECONNECTING

        when (val res = multiplayerApiService.reconnect(token, matchId)) {
            is NetworkResult.Success -> {
                val snapshot = res.data
                _currentSession.value = snapshot
                webSocketClient.connect(token, snapshot.matchId)
                if (snapshot.results.isNotEmpty()) {
                    _matchResults.value = snapshot.results
                }
                mapMatchStateToClientState(snapshot.matchState)
                true
            }
            is NetworkResult.Error -> {
                _errorMessage.value = "Failed to reconnect to match: ${res.message}"
                _clientMatchState.value = ClientMatchState.ERROR
                false
            }
            is NetworkResult.Exception -> {
                _errorMessage.value = res.throwable.message ?: "Reconnect connection error"
                _clientMatchState.value = ClientMatchState.ERROR
                false
            }
        }
    }

    override fun sendProgress(coveredCells: Int, lastCheckpoint: Int) {
        val session = _currentSession.value ?: return
        webSocketClient.sendProgress(session.matchId, coveredCells, lastCheckpoint)
    }

    override fun sendReaction(reactionCode: String) {
        val session = _currentSession.value ?: return
        webSocketClient.sendReaction(session.matchId, reactionCode)
    }

    override fun leaveMatch() {
        stopStatusPolling()
        countdownJob?.cancel()
        countdownJob = null
        webSocketClient.disconnect()
        _currentSession.value = null
        _ticketStatus.value = null
        _opponentProgress.value = emptyMap()
        _countdownSeconds.value = null
        _matchResults.value = emptyList()
        _incomingReaction.value = null
        _rematchStatus.value = null
        _clientMatchState.value = ClientMatchState.IDLE
    }

    override fun clearError() {
        _errorMessage.value = null
        if (_clientMatchState.value == ClientMatchState.ERROR) {
            _clientMatchState.value = ClientMatchState.IDLE
        }
    }

    private fun startStatusPolling(token: String) {
        stopStatusPolling()
        pollingJob = scope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                if (System.currentTimeMillis() - startTime > QUEUE_TIMEOUT_MS) {
                    _ticketStatus.value = MatchmakingTicketStatus(null, "TIMEOUT", null, startTime, QUEUE_TIMEOUT_MS)
                    _errorMessage.value = "No opponents found. Tap retry to search again."
                    _clientMatchState.value = ClientMatchState.ERROR
                    break
                }

                when (val res = multiplayerApiService.getQuickDuelStatus(token)) {
                    is NetworkResult.Success -> {
                        val ticket = res.data
                        _ticketStatus.value = ticket
                        if (ticket.status == "MATCH_FOUND" && ticket.matchId != null) {
                            onMatchFound(ticket.matchId)
                            break
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun stopStatusPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun onMatchFound(matchId: String) {
        stopStatusPolling()
        _clientMatchState.value = ClientMatchState.MATCH_FOUND
        val token = secureTokenStorage.getSessionToken() ?: return
        when (val res = multiplayerApiService.getMatchSnapshot(token, matchId)) {
            is NetworkResult.Success -> {
                _currentSession.value = res.data
                if (res.data.results.isNotEmpty()) {
                    _matchResults.value = res.data.results
                }
                webSocketClient.subscribeMatch(matchId)
                mapMatchStateToClientState(res.data.matchState)
            }
            else -> {}
        }
    }

    private fun handleIncomingEvent(event: IncomingMultiplayerEvent) {
        Log.d(TAG, "Incoming WS event: ${event.eventType} for match=${event.matchId}")
        when (event.eventType) {
            "MATCH_FOUND" -> {
                event.matchId?.let { matchId ->
                    scope.launch { onMatchFound(matchId) }
                }
            }
            "PLAYER_READY" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val readyPlayerId = obj.optString("playerId")
                    updateParticipantReady(readyPlayerId)
                } catch (_: Exception) {}
            }
            "MATCH_COUNTDOWN" -> {
                _clientMatchState.value = ClientMatchState.COUNTDOWN
                startLocalCountdown(3)
            }
            "MATCH_STARTED" -> {
                countdownJob?.cancel()
                _countdownSeconds.value = null
                _clientMatchState.value = ClientMatchState.ACTIVE
            }
            "PLAYER_PROGRESS" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val covered = obj.optInt("coveredCells", 0)
                    val checkpoint = obj.optInt("lastCheckpoint", 1)
                    updateOpponentProgress(pId, covered, checkpoint)
                } catch (_: Exception) {}
            }
            "PLAYER_COMPLETED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val finishOrder = obj.optInt("finishOrder", 1)
                    val isWinner = obj.optBoolean("isWinner", false)
                    val solveTime = obj.optLong("solveTimeMs", 0L)
                    markParticipantCompleted(pId, finishOrder, isWinner, solveTime)
                } catch (_: Exception) {}
            }
            "MATCH_COMPLETED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val resArr = obj.optJSONArray("results")
                    if (resArr != null) {
                        val list = mutableListOf<MatchResultDto>()
                        for (i in 0 until resArr.length()) {
                            val r = resArr.getJSONObject(i)
                            list.add(
                                MatchResultDto(
                                    matchId = r.optString("matchId", event.matchId ?: ""),
                                    playerId = r.optString("playerId", ""),
                                    publicZynpathId = r.optString("publicZynpathId", ""),
                                    displayName = r.optString("displayName", "Player"),
                                    completed = r.optBoolean("completed", true),
                                    solveTimeMs = if (r.has("solveTimeMs") && !r.isNull("solveTimeMs")) r.getLong("solveTimeMs") else null,
                                    finishOrder = r.optInt("finishOrder", 1),
                                    isWinner = r.optBoolean("isWinner", r.optBoolean("winner", false)),
                                    resultStatus = r.optString("resultStatus", if (r.optBoolean("isWinner", false)) "VICTORY" else "DEFEAT")
                                )
                            )
                        }
                        _matchResults.value = list
                    }
                } catch (_: Exception) {}
                _clientMatchState.value = ClientMatchState.COMPLETED
            }
            "REACTION_EMITTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val code = obj.optString("reactionCode")
                    if (pId.isNotEmpty() && code.isNotEmpty()) {
                        _incomingReaction.value = Pair(pId, code)
                    }
                } catch (_: Exception) {}
            }
            "MATCH_CANCELLED" -> {
                _clientMatchState.value = ClientMatchState.CANCELLED
            }
            "RECONNECT_STATE" -> {
                // Parse full snapshot from payload
                try {
                    event.matchId?.let { mId ->
                        scope.launch {
                            val token = secureTokenStorage.getSessionToken() ?: return@launch
                            val res = multiplayerApiService.getMatchSnapshot(token, mId)
                            if (res is NetworkResult.Success) {
                                _currentSession.value = res.data
                                mapMatchStateToClientState(res.data.matchState)
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_INVITED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val invitation = parseInvitationJson(obj)
                    val currentList = _incomingInvitations.value.filter { it.invitationId != invitation.invitationId }
                    _incomingInvitations.value = currentList + invitation
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_ACCEPTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val mId = obj.optString("matchId").takeIf { it.isNotEmpty() } ?: event.matchId
                    val invId = obj.optString("invitationId")
                    if (_activeOutgoingInvitation.value?.invitationId == invId) {
                        _activeOutgoingInvitation.value = _activeOutgoingInvitation.value?.copy(status = FriendDuelInvitationStatus.ACCEPTED)
                    }
                    _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invId }
                    if (mId != null) {
                        scope.launch { onMatchFound(mId) }
                    }
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_DECLINED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val invId = obj.optString("invitationId")
                    if (_activeOutgoingInvitation.value?.invitationId == invId) {
                        _activeOutgoingInvitation.value = _activeOutgoingInvitation.value?.copy(status = FriendDuelInvitationStatus.DECLINED)
                    }
                    _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invId }
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_CANCELLED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val invId = obj.optString("invitationId")
                    _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invId }
                    if (_activeOutgoingInvitation.value?.invitationId == invId) {
                        _activeOutgoingInvitation.value = null
                    }
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_EXPIRED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val invId = obj.optString("invitationId")
                    _incomingInvitations.value = _incomingInvitations.value.filter { it.invitationId != invId }
                    if (_activeOutgoingInvitation.value?.invitationId == invId) {
                        _activeOutgoingInvitation.value = _activeOutgoingInvitation.value?.copy(status = FriendDuelInvitationStatus.EXPIRED)
                    }
                } catch (_: Exception) {}
            }
            "FRIEND_DUEL_MATCH_CREATED" -> {
                val mId = event.matchId
                if (mId != null) {
                    scope.launch { onMatchFound(mId) }
                }
            }
            "REMATCH_REQUESTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val mId = obj.optString("matchId", event.matchId ?: "")
                    val requester = obj.optString("requesterPlayerId")
                    val expires = obj.optLong("expiresAt")
                    _rematchStatus.value = RematchStatusDto(
                        matchId = mId,
                        requesterPlayerId = requester,
                        status = RematchState.PENDING,
                        requestedAt = System.currentTimeMillis(),
                        expiresAt = expires
                    )
                } catch (_: Exception) {}
            }
            "REMATCH_ACCEPTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val newMatchId = obj.optString("newMatchId")
                    _rematchStatus.value = _rematchStatus.value?.copy(
                        status = RematchState.ACCEPTED,
                        newMatchId = newMatchId
                    )
                    if (newMatchId.isNotEmpty()) {
                        scope.launch { onMatchFound(newMatchId) }
                    }
                } catch (_: Exception) {}
            }
            "REMATCH_DECLINED" -> {
                _rematchStatus.value = _rematchStatus.value?.copy(status = RematchState.DECLINED)
            }
            "REMATCH_EXPIRED" -> {
                _rematchStatus.value = _rematchStatus.value?.copy(status = RematchState.EXPIRED)
            }
            "MINI_LEAGUE_INVITED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val invitation = parseMiniLeagueInvitationJson(obj)
                    val currentList = _miniLeagueIncomingInvitations.value.filter { it.invitationId != invitation.invitationId }
                    _miniLeagueIncomingInvitations.value = currentList + invitation
                } catch (_: Exception) {}
            }
            "MINI_LEAGUE_PLAYER_JOINED" -> {
                try {
                    val rId = event.matchId
                    if (rId != null) {
                        scope.launch {
                            val token = secureTokenStorage.getSessionToken()
                            if (token != null) {
                                val res = multiplayerApiService.getMiniLeagueRoom(token, rId)
                                if (res is NetworkResult.Success) {
                                    _currentMiniLeagueRoom.value = res.data
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            "MINI_LEAGUE_PLAYER_LEFT" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val room = _currentMiniLeagueRoom.value
                    if (room != null && pId.isNotEmpty()) {
                        val updated = room.participants.filter { it.playerId != pId }
                        _currentMiniLeagueRoom.value = room.copy(
                            participants = updated,
                            currentParticipants = updated.size
                        )
                    }
                } catch (_: Exception) {}
            }
            "MINI_LEAGUE_HOST_CHANGED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val newHostId = obj.optString("newHostPlayerId")
                    val room = _currentMiniLeagueRoom.value
                    if (room != null && newHostId.isNotEmpty()) {
                        val updated = room.participants.map {
                            if (it.playerId == newHostId) it.copy(isHost = true) else it.copy(isHost = false)
                        }
                        _currentMiniLeagueRoom.value = room.copy(
                            hostPlayerId = newHostId,
                            participants = updated
                        )
                    }
                } catch (_: Exception) {}
            }
            "MINI_LEAGUE_READY_CHANGED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val isReady = obj.optBoolean("isReady", false)
                    val room = _currentMiniLeagueRoom.value
                    if (room != null && pId.isNotEmpty()) {
                        val updated = room.participants.map {
                            if (it.playerId == pId) it.copy(isReady = isReady) else it
                        }
                        _currentMiniLeagueRoom.value = room.copy(participants = updated)
                    }
                } catch (_: Exception) {}
            }
            "MINI_LEAGUE_ROOM_CANCELLED" -> {
                val room = _currentMiniLeagueRoom.value
                if (room != null) {
                    _currentMiniLeagueRoom.value = room.copy(state = "CANCELLED")
                }
                _clientMatchState.value = ClientMatchState.CANCELLED
            }
            "MINI_LEAGUE_ROOM_EXPIRED" -> {
                val room = _currentMiniLeagueRoom.value
                if (room != null) {
                    _currentMiniLeagueRoom.value = room.copy(state = "EXPIRED")
                }
            }
            "MINI_LEAGUE_MATCH_CREATED" -> {
                val mId = event.matchId
                if (mId != null) {
                    scope.launch { onMatchFound(mId) }
                }
            }
            "FRIENDS_ARENA_ROOM_CREATED", "FRIENDS_ARENA_MEMBERSHIP_CHANGED", "FRIENDS_ARENA_PLAYER_JOINED", "FRIENDS_ARENA_PLAYER_LEFT", "FRIENDS_ARENA_HOST_CHANGED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val roomObj = if (obj.has("room")) obj.getJSONObject("room") else obj
                    if (roomObj.has("roomId")) {
                        _currentFriendsArenaRoom.value = parseFriendsArenaRoomJson(roomObj)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_ROOM_CLOSED" -> {
                _currentFriendsArenaRoom.value = _currentFriendsArenaRoom.value?.copy(status = "CLOSED")
            }
            "FRIENDS_ARENA_MATCH_STARTING" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val roomObj = if (obj.has("room")) obj.getJSONObject("room") else null
                    if (roomObj != null && roomObj.has("roomId")) {
                        _currentFriendsArenaRoom.value = parseFriendsArenaRoomJson(roomObj)
                    }
                    if (obj.has("match")) {
                        val matchObj = obj.getJSONObject("match")
                        _currentFriendsArenaMatch.value = parseFriendsArenaMatchJson(matchObj)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_MATCH_STARTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val matchObj = if (obj.has("match")) obj.getJSONObject("match") else null
                    if (matchObj != null) {
                        _currentFriendsArenaMatch.value = parseFriendsArenaMatchJson(matchObj)
                    } else {
                        _currentFriendsArenaMatch.value = _currentFriendsArenaMatch.value?.copy(status = "ACTIVE")
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_PLAYER_PROGRESS" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val covered = obj.optInt("coveredCells", 0)
                    val cp = obj.optInt("lastCheckpoint", 1)
                    val match = _currentFriendsArenaMatch.value
                    if (match != null && pId.isNotEmpty()) {
                        val updated = match.participants.map {
                            if (it.playerId == pId) it.copy(coveredCells = covered, lastCheckpoint = cp) else it
                        }
                        _currentFriendsArenaMatch.value = match.copy(participants = updated)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_PLAYER_COMPLETED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val finishOrder = obj.optInt("finishOrder", 1)
                    val isWinner = obj.optBoolean("isWinner", false)
                    val solveTime = obj.optLong("solveTimeMs", 0L)
                    val match = _currentFriendsArenaMatch.value
                    if (match != null && pId.isNotEmpty()) {
                        val updated = match.participants.map {
                            if (it.playerId == pId) it.copy(isCompleted = true, finishOrder = finishOrder, isWinner = isWinner, solveTimeMs = solveTime) else it
                        }
                        _currentFriendsArenaMatch.value = match.copy(participants = updated)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_PLAYER_DISCONNECTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val match = _currentFriendsArenaMatch.value
                    if (match != null && pId.isNotEmpty()) {
                        val updated = match.participants.map {
                            if (it.playerId == pId) it.copy(isConnected = false) else it
                        }
                        _currentFriendsArenaMatch.value = match.copy(participants = updated)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_PLAYER_RECONNECTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val pId = obj.optString("playerId")
                    val match = _currentFriendsArenaMatch.value
                    if (match != null && pId.isNotEmpty()) {
                        val updated = match.participants.map {
                            if (it.playerId == pId) it.copy(isConnected = true) else it
                        }
                        _currentFriendsArenaMatch.value = match.copy(participants = updated)
                    }
                } catch (_: Exception) {}
            }
            "FRIENDS_ARENA_MATCH_COMPLETED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    if (obj.has("match")) {
                        val matchObj = obj.getJSONObject("match")
                        _currentFriendsArenaMatch.value = parseFriendsArenaMatchJson(matchObj)
                    } else {
                        _currentFriendsArenaMatch.value = _currentFriendsArenaMatch.value?.copy(status = "COMPLETED")
                    }
                } catch (_: Exception) {}
            }
            "REMATCH_REQUESTED", "FRIENDS_ARENA_REMATCH_REQUESTED" -> {
                try {
                    val obj = JSONObject(event.payloadJson)
                    val requester = obj.optString("requesterDisplayName", "An opponent")
                    _friendsArenaRematchRequested.value = requester
                } catch (_: Exception) {}
            }
        }
    }

    private fun parseFriendsArenaRoomJson(obj: JSONObject): FriendsArenaRoomDto {
        val membersArr = obj.optJSONArray("members") ?: org.json.JSONArray()
        val members = mutableListOf<FriendsArenaMemberDto>()
        for (i in 0 until membersArr.length()) {
            val m = membersArr.getJSONObject(i)
            members.add(
                FriendsArenaMemberDto(
                    playerId = m.getString("playerId"),
                    publicZynpathId = m.optString("publicZynpathId", ""),
                    displayName = m.optString("displayName", "Player"),
                    avatarId = m.optString("avatarId", "avatar_compass"),
                    isHost = m.optBoolean("isHost", false),
                    isReady = m.optBoolean("isReady", false),
                    joinedAt = m.optLong("joinedAt", 0L)
                )
            )
        }
        return FriendsArenaRoomDto(
            roomId = obj.getString("roomId"),
            roomCode = obj.getString("roomCode"),
            hostPlayerId = obj.getString("hostPlayerId"),
            status = obj.optString("status", "WAITING"),
            currentOccupancy = obj.optInt("currentOccupancy", members.size),
            maxCapacity = obj.optInt("maxCapacity", 5),
            members = members,
            activeMatchId = if (obj.has("activeMatchId") && !obj.isNull("activeMatchId")) obj.optString("activeMatchId") else null,
            createdAt = obj.optLong("createdAt", 0L),
            version = obj.optLong("version", 1L)
        )
    }

    private fun parseFriendsArenaMatchJson(obj: JSONObject): FriendsArenaMatchDto {
        val puzzleObj = obj.optJSONObject("puzzleAssignment") ?: obj.optJSONObject("puzzle")
        val puzzle = puzzleObj?.let { pz ->
            val reqArr = pz.optJSONArray("requiredCells") ?: org.json.JSONArray()
            val reqList = mutableListOf<String>()
            for (i in 0 until reqArr.length()) reqList.add(reqArr.getString(i))

            val cpArr = pz.optJSONArray("checkpoints") ?: org.json.JSONArray()
            val cpMap = mutableMapOf<Int, String>()
            for (i in 0 until cpArr.length()) {
                val cp = cpArr.getJSONObject(i)
                cpMap[cp.getInt("number")] = "${cp.getInt("row")},${cp.getInt("col")}"
            }

            val edgeArr = pz.optJSONArray("blockedEdges") ?: org.json.JSONArray()
            val edgeList = mutableListOf<String>()
            for (i in 0 until edgeArr.length()) {
                val e = edgeArr.getJSONObject(i)
                edgeList.add("${e.getInt("row1")},${e.getInt("col1")}-${e.getInt("row2")},${e.getInt("col2")}")
            }

            PuzzleAssignmentDto(
                puzzleId = pz.getString("puzzleId"),
                gameMode = GameMode.FRIENDS_ARENA,
                width = pz.optInt("gridCols", 4),
                height = pz.optInt("gridRows", 4),
                requiredCells = reqList,
                checkpoints = cpMap,
                blockedEdges = edgeList,
                fingerprint = pz.optString("fingerprint", "")
            )
        }

        val pArr = obj.optJSONArray("participants") ?: org.json.JSONArray()
        val participants = mutableListOf<FriendsArenaMatchParticipantDto>()
        for (i in 0 until pArr.length()) {
            val p = pArr.getJSONObject(i)
            participants.add(
                FriendsArenaMatchParticipantDto(
                    playerId = p.getString("playerId"),
                    publicZynpathId = p.optString("publicZynpathId", ""),
                    displayName = p.optString("displayName", "Player"),
                    avatarId = p.optString("avatarId", "avatar_compass"),
                    isHost = p.optBoolean("isHost", false),
                    isConnected = p.optBoolean("isConnected", true),
                    coveredCells = p.optInt("coveredCells", 0),
                    lastCheckpoint = p.optInt("lastCheckpoint", 1),
                    isCompleted = p.optBoolean("isCompleted", false),
                    solveTimeMs = if (p.has("solveTimeMs") && !p.isNull("solveTimeMs")) p.optLong("solveTimeMs") else null,
                    isWinner = p.optBoolean("isWinner", false),
                    finishOrder = if (p.has("finishOrder") && !p.isNull("finishOrder")) p.optInt("finishOrder") else null
                )
            )
        }

        val rArr = obj.optJSONArray("results") ?: org.json.JSONArray()
        val results = mutableListOf<MatchResultDto>()
        for (i in 0 until rArr.length()) {
            val r = rArr.getJSONObject(i)
            results.add(
                MatchResultDto(
                    matchId = r.optString("matchId", obj.optString("matchId", "")),
                    playerId = r.optString("playerId", ""),
                    publicZynpathId = r.optString("publicZynpathId", ""),
                    displayName = r.optString("displayName", "Player"),
                    completed = r.optBoolean("completed", true),
                    solveTimeMs = if (r.has("solveTimeMs") && !r.isNull("solveTimeMs")) r.getLong("solveTimeMs") else null,
                    finishOrder = r.optInt("finishOrder", 1),
                    isWinner = r.optBoolean("isWinner", r.optBoolean("winner", false)),
                    resultStatus = r.optString("resultStatus", if (r.optBoolean("isWinner", false)) "VICTORY" else "DEFEAT")
                )
            )
        }

        return FriendsArenaMatchDto(
            matchId = obj.getString("matchId"),
            roomId = obj.optString("roomId", ""),
            hostPlayerId = obj.optString("hostPlayerId", ""),
            status = obj.optString("status", "COUNTDOWN"),
            puzzle = puzzle,
            participants = participants,
            countdownStartedAt = if (obj.has("countdownStartedAt") && !obj.isNull("countdownStartedAt")) obj.optLong("countdownStartedAt") else null,
            startedAt = if (obj.has("startedAt") && !obj.isNull("startedAt")) obj.optLong("startedAt") else null,
            endedAt = if (obj.has("endedAt") && !obj.isNull("endedAt")) obj.optLong("endedAt") else null,
            serverTime = obj.optLong("serverTime", System.currentTimeMillis()),
            version = obj.optLong("version", 1L),
            results = results
        )
    }

    private fun parseMiniLeagueInvitationJson(obj: JSONObject): com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation {
        return com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation(
            invitationId = obj.getString("invitationId"),
            roomId = obj.getString("roomId"),
            roomCode = obj.optString("roomCode", ""),
            hostPlayerId = obj.getString("hostPlayerId"),
            hostDisplayName = obj.optString("hostDisplayName", "Host"),
            hostAvatarId = obj.optString("hostAvatarId").takeIf { it.isNotEmpty() },
            recipientPlayerId = obj.getString("recipientPlayerId"),
            recipientDisplayName = obj.optString("recipientDisplayName", "Player"),
            status = obj.optString("status", "PENDING"),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            expiresAt = obj.optLong("expiresAt", System.currentTimeMillis() + 60000L)
        )
    }

    private fun parseInvitationJson(obj: JSONObject): FriendDuelInvitationDto {
        return FriendDuelInvitationDto(
            invitationId = obj.getString("invitationId"),
            inviterPlayerId = obj.getString("inviterPlayerId"),
            inviterPublicZynpathId = obj.optString("inviterPublicZynpathId", ""),
            inviterDisplayName = obj.optString("inviterDisplayName", "Player"),
            inviterAvatarId = obj.optString("inviterAvatarId").takeIf { it.isNotEmpty() },
            recipientPlayerId = obj.getString("recipientPlayerId"),
            recipientPublicZynpathId = obj.optString("recipientPublicZynpathId", ""),
            recipientDisplayName = obj.optString("recipientDisplayName", "Friend"),
            recipientAvatarId = obj.optString("recipientAvatarId").takeIf { it.isNotEmpty() },
            gameMode = GameMode.fromString(obj.optString("gameMode", "FRIEND_DUEL")),
            status = FriendDuelInvitationStatus.fromString(obj.optString("status", "PENDING")),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            expiresAt = obj.optLong("expiresAt", System.currentTimeMillis() + 60000L),
            matchId = obj.optString("matchId").takeIf { it.isNotEmpty() }
        )
    }

    private fun startLocalCountdown(seconds: Int) {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            for (sec in seconds downTo 1) {
                _countdownSeconds.value = sec
                delay(1000L)
            }
            _countdownSeconds.value = 0
            delay(300L)
            _countdownSeconds.value = null
            _clientMatchState.value = ClientMatchState.ACTIVE
        }
    }

    private fun mapMatchStateToClientState(state: MatchState) {
        _clientMatchState.value = when (state) {
            MatchState.CREATED, MatchState.WAITING_FOR_PLAYERS -> ClientMatchState.WAITING
            MatchState.READY -> ClientMatchState.READY
            MatchState.COUNTDOWN -> ClientMatchState.COUNTDOWN
            MatchState.ACTIVE, MatchState.COMPLETING -> ClientMatchState.ACTIVE
            MatchState.COMPLETED -> ClientMatchState.COMPLETED
            MatchState.CANCELLED -> ClientMatchState.CANCELLED
            MatchState.EXPIRED -> ClientMatchState.ERROR
        }
    }

    private fun updateParticipantReady(playerId: String) {
        val session = _currentSession.value ?: return
        val updated = session.participants.map {
            if (it.playerId == playerId) it.copy(isReady = true) else it
        }
        _currentSession.value = session.copy(participants = updated)
    }

    private fun updateOpponentProgress(playerId: String, covered: Int, checkpoint: Int) {
        val current = _opponentProgress.value.toMutableMap()
        val existing = current[playerId]
        if (existing != null) {
            current[playerId] = existing.copy(coveredCellsCount = covered, lastCheckpoint = checkpoint)
        } else {
            val session = _currentSession.value
            val p = session?.participants?.firstOrNull { it.playerId == playerId }
            if (p != null) {
                current[playerId] = p.copy(coveredCellsCount = covered, lastCheckpoint = checkpoint)
            }
        }
        _opponentProgress.value = current
    }

    private fun markParticipantCompleted(playerId: String, finishOrder: Int, isWinner: Boolean, solveTimeMs: Long) {
        val session = _currentSession.value ?: return
        val updated = session.participants.map {
            if (it.playerId == playerId) {
                it.copy(
                    isCompleted = true,
                    isWinner = isWinner,
                    finishOrder = finishOrder,
                    solveTimeMs = solveTimeMs
                )
            } else it
        }
        _currentSession.value = session.copy(participants = updated)
    }

    // --- Competitive Progression, History, Statistics & Leaderboards (Prompt 24) ---

    override suspend fun getMatchHistory(
        mode: GameMode?,
        page: Int,
        pageSize: Int
    ): MatchHistoryResponse? {
        val token = secureTokenStorage.getSessionToken() ?: return null
        return when (val res = multiplayerApiService.getMatchHistory(token, mode, page, pageSize)) {
            is NetworkResult.Success -> res.data
            else -> null
        }
    }

    override suspend fun getMatchDetails(matchId: String): MatchDetails? {
        val token = secureTokenStorage.getSessionToken() ?: return null
        return when (val res = multiplayerApiService.getMatchDetails(token, matchId)) {
            is NetworkResult.Success -> res.data
            else -> null
        }
    }

    override suspend fun getCompetitiveStats(): CompetitiveStats? {
        val token = secureTokenStorage.getSessionToken() ?: return null
        return when (val res = multiplayerApiService.getCompetitiveStats(token)) {
            is NetworkResult.Success -> res.data
            else -> null
        }
    }

    override suspend fun getPublicCompetitiveStats(publicZynpathId: String): PublicCompetitiveStats? {
        return when (val res = multiplayerApiService.getPublicCompetitiveStats(publicZynpathId)) {
            is NetworkResult.Success -> res.data
            else -> null
        }
    }

    override suspend fun getLeaderboard(
        category: LeaderboardCategory,
        period: LeaderboardPeriod,
        page: Int,
        pageSize: Int
    ): LeaderboardResponse? {
        val token = secureTokenStorage.getSessionToken()
        return when (val res = multiplayerApiService.getLeaderboard(token, category, period, page, pageSize)) {
            is NetworkResult.Success -> res.data
            else -> null
        }
    }
}
