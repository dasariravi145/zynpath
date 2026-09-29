package com.zynpath.game.fake

import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod
import com.zynpath.game.core.multiplayer.model.LeaderboardResponse
import com.zynpath.game.core.multiplayer.model.MatchDetails
import com.zynpath.game.core.multiplayer.model.MatchHistoryResponse
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoom
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoomParticipant
import com.zynpath.game.core.multiplayer.model.PublicCompetitiveStats
import com.zynpath.game.core.multiplayer.model.RematchState
import com.zynpath.game.core.multiplayer.model.RematchStatusDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto
import com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMultiplayerRepository : MultiplayerRepository {

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

    private val _isConnected = MutableStateFlow(true)
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

    private val _currentMiniLeagueRoom = MutableStateFlow<MiniLeagueRoom?>(null)
    override val currentMiniLeagueRoom: StateFlow<MiniLeagueRoom?> = _currentMiniLeagueRoom.asStateFlow()

    private val _miniLeagueIncomingInvitations = MutableStateFlow<List<MiniLeagueInvitation>>(emptyList())
    override val miniLeagueIncomingInvitations: StateFlow<List<MiniLeagueInvitation>> = _miniLeagueIncomingInvitations.asStateFlow()

    fun setClientMatchState(state: ClientMatchState) {
        _clientMatchState.value = state
    }

    fun setTicketStatus(ticket: MatchmakingTicketStatus?) {
        _ticketStatus.value = ticket
    }

    fun setCurrentSession(session: MatchSessionSnapshotDto?) {
        _currentSession.value = session
    }

    fun setCountdownSeconds(seconds: Int?) {
        _countdownSeconds.value = seconds
    }

    fun setIsConnected(connected: Boolean) {
        _isConnected.value = connected
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun setMatchResults(results: List<MatchResultDto>) {
        _matchResults.value = results
    }

    fun setOpponentProgress(progress: Map<String, MatchParticipantDto>) {
        _opponentProgress.value = progress
    }

    fun setMiniLeagueRoom(room: MiniLeagueRoom?) {
        _currentMiniLeagueRoom.value = room
    }

    override suspend fun startQuickDuelSearch(): Boolean {
        _clientMatchState.value = ClientMatchState.SEARCHING
        _ticketStatus.value = MatchmakingTicketStatus(
            ticketId = "ticket_123",
            status = "QUEUED",
            matchId = null,
            enqueuedAt = System.currentTimeMillis(),
            currentWaitMs = 5000L
        )
        return true
    }

    override suspend fun cancelMatchmaking(): Boolean {
        _clientMatchState.value = ClientMatchState.IDLE
        _ticketStatus.value = null
        return true
    }

    override suspend fun createFriendDuel(targetPublicZynpathId: String): Boolean = true

    override suspend fun sendFriendDuelInvitation(targetPublicZynpathId: String): FriendDuelInvitationDto? {
        val invitation = FriendDuelInvitationDto(
            invitationId = "inv_123",
            inviterPlayerId = "player_self",
            inviterPublicZynpathId = "ZYN-1111",
            inviterDisplayName = "Self",
            inviterAvatarId = null,
            recipientPlayerId = targetPublicZynpathId,
            recipientPublicZynpathId = targetPublicZynpathId,
            recipientDisplayName = "Friend",
            recipientAvatarId = null,
            gameMode = GameMode.FRIEND_DUEL,
            status = com.zynpath.game.core.multiplayer.model.FriendDuelInvitationStatus.PENDING
        )
        _activeOutgoingInvitation.value = invitation
        return invitation
    }

    override suspend fun acceptFriendDuelInvitation(invitationId: String): Boolean = true

    override suspend fun declineFriendDuelInvitation(invitationId: String): Boolean = true

    override suspend fun cancelFriendDuelInvitation(invitationId: String): Boolean {
        _activeOutgoingInvitation.value = null
        return true
    }

    override suspend fun refreshInvitations() {}

    override suspend fun requestRematch(): Boolean {
        _rematchStatus.value = RematchStatusDto("match_curr", "player_1", RematchState.PENDING)
        return true
    }

    override suspend fun respondToRematch(accept: Boolean): Boolean {
        _rematchStatus.value = RematchStatusDto("match_curr", "player_1", if (accept) RematchState.ACCEPTED else RematchState.DECLINED)
        return true
    }

    override fun clearRematch() {
        _rematchStatus.value = null
    }

    override suspend fun createMiniLeague(roomName: String, maxParticipants: Int): Boolean {
        createMiniLeagueRoom(maxParticipants)
        return true
    }

    override suspend fun markReady(): Boolean {
        _clientMatchState.value = ClientMatchState.COUNTDOWN
        return true
    }

    override suspend fun submitCompletion(
        pathCoordinates: List<String>,
        clientDurationMs: Long,
        movesCount: Int
    ): SolutionClaimOutcome? = SolutionClaimOutcome(valid = true, rejectionReason = null, isWinner = true, solveTimeMs = clientDurationMs)

    override suspend fun forfeitMatch(): Boolean {
        _clientMatchState.value = ClientMatchState.COMPLETED
        return true
    }

    override suspend fun reconnectToMatch(matchId: String): Boolean = true

    override fun sendProgress(coveredCells: Int, lastCheckpoint: Int) {}

    override fun sendReaction(reactionCode: String) {}

    override fun leaveMatch() {
        _clientMatchState.value = ClientMatchState.IDLE
    }

    override fun clearError() {
        _errorMessage.value = null
    }

    override suspend fun getMatchHistory(mode: GameMode?, page: Int, pageSize: Int): MatchHistoryResponse? = null

    override suspend fun getMatchDetails(matchId: String): MatchDetails? = null

    override suspend fun getCompetitiveStats(): CompetitiveStats? {
        return CompetitiveStats(
            quickDuelMatches = 20,
            quickDuelWins = 15
        )
    }

    override suspend fun getPublicCompetitiveStats(publicZynpathId: String): PublicCompetitiveStats? = null

    override suspend fun getLeaderboard(
        category: LeaderboardCategory,
        period: LeaderboardPeriod,
        page: Int,
        pageSize: Int
    ): LeaderboardResponse? = null

    override suspend fun createMiniLeagueRoom(maxParticipants: Int): MiniLeagueRoom? {
        val room = MiniLeagueRoom(
            roomId = "room_mini_1",
            roomCode = "MINI55",
            roomName = "Mini League 1",
            hostPlayerId = "player_self",
            state = "WAITING_FOR_PLAYERS",
            maxParticipants = maxParticipants.coerceIn(2, 5),
            currentParticipants = 1,
            participants = listOf(
                com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant(
                    playerId = "player_self",
                    publicZynpathId = "ZYN-1111",
                    displayName = "Host Player",
                    avatarId = null,
                    isHost = true,
                    isReady = true,
                    isConnected = true
                )
            )
        )
        _currentMiniLeagueRoom.value = room
        return room
    }

    override suspend fun getMiniLeagueRoom(roomId: String): MiniLeagueRoom? = _currentMiniLeagueRoom.value

    override suspend fun getMiniLeagueRoomByCode(roomCode: String): MiniLeagueRoom? {
        return _currentMiniLeagueRoom.value?.takeIf { it.roomCode == roomCode }
    }

    override suspend fun joinMiniLeagueRoom(roomCode: String): MiniLeagueRoom? {
        val current = _currentMiniLeagueRoom.value ?: return null
        if (current.participants.size >= current.maxParticipants) return null
        val updated = current.copy(
            currentParticipants = current.participants.size + 1,
            participants = current.participants + com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant(
                playerId = "player_guest",
                publicZynpathId = "ZYN-2222",
                displayName = "Guest",
                avatarId = null,
                isHost = false,
                isReady = false,
                isConnected = true
            )
        )
        _currentMiniLeagueRoom.value = updated
        return updated
    }

    override suspend fun leaveMiniLeagueRoom(roomId: String): Boolean {
        _currentMiniLeagueRoom.value = null
        return true
    }

    override suspend fun setMiniLeagueReady(roomId: String, ready: Boolean): Boolean {
        val current = _currentMiniLeagueRoom.value ?: return false
        val updated = current.copy(
            participants = current.participants.map {
                if (it.playerId == "player_self") it.copy(isReady = ready) else it
            }
        )
        _currentMiniLeagueRoom.value = updated
        return true
    }

    override suspend fun startMiniLeagueMatch(roomId: String): MatchSessionSnapshotDto? {
        val current = _currentMiniLeagueRoom.value ?: return null
        _currentMiniLeagueRoom.value = current.copy(state = "ACTIVE")
        return _currentSession.value
    }

    override suspend fun inviteFriendToMiniLeague(roomId: String, friendPublicZynpathId: String): MiniLeagueInvitation? = null

    override suspend fun respondToMiniLeagueInvitation(invitationId: String, accept: Boolean): Boolean = true

    override suspend fun cancelMiniLeagueInvitation(invitationId: String): Boolean = true

    override suspend fun refreshMiniLeagueInvitations() {}

    override fun clearMiniLeagueRoom() {
        _currentMiniLeagueRoom.value = null
    }

    private val _currentFriendsArenaRoom = MutableStateFlow<FriendsArenaRoomDto?>(null)
    override val currentFriendsArenaRoom: StateFlow<FriendsArenaRoomDto?> = _currentFriendsArenaRoom.asStateFlow()

    private val _currentFriendsArenaMatch = MutableStateFlow<FriendsArenaMatchDto?>(null)
    override val currentFriendsArenaMatch: StateFlow<FriendsArenaMatchDto?> = _currentFriendsArenaMatch.asStateFlow()

    private val _friendsArenaRematchRequested = MutableStateFlow<String?>(null)
    override val friendsArenaRematchRequested: StateFlow<String?> = _friendsArenaRematchRequested.asStateFlow()

    override suspend fun createFriendsArenaRoom(): FriendsArenaRoomDto? {
        val room = FriendsArenaRoomDto(
            roomId = "fake_room_1",
            roomCode = "FAKECD",
            hostPlayerId = "player_self",
            status = "WAITING",
            currentOccupancy = 1,
            maxCapacity = 5,
            members = emptyList()
        )
        _currentFriendsArenaRoom.value = room
        return room
    }

    override suspend fun getFriendsArenaRoom(roomId: String): FriendsArenaRoomDto? = _currentFriendsArenaRoom.value

    override suspend fun getFriendsArenaRoomByCode(roomCode: String): FriendsArenaRoomDto? = _currentFriendsArenaRoom.value

    override suspend fun joinFriendsArenaRoom(roomCode: String): FriendsArenaRoomDto? = _currentFriendsArenaRoom.value

    override suspend fun leaveFriendsArenaRoom(roomId: String): Boolean {
        _currentFriendsArenaRoom.value = null
        return true
    }

    override suspend fun startFriendsArenaMatch(roomId: String): FriendsArenaRoomDto? = _currentFriendsArenaRoom.value

    override fun clearFriendsArenaRoom() {
        _currentFriendsArenaRoom.value = null
    }

    override fun subscribeToFriendsArenaRoomEvents(roomId: String) {}

    override suspend fun getFriendsArenaMatch(matchId: String): FriendsArenaMatchDto? = _currentFriendsArenaMatch.value

    override suspend fun getFriendsArenaMatchByRoom(roomId: String): FriendsArenaMatchDto? = _currentFriendsArenaMatch.value

    override suspend fun updateFriendsArenaProgress(matchId: String, coveredCells: Int, lastCheckpoint: Int): Boolean = true

    override suspend fun submitFriendsArenaClaim(matchId: String, pathCoordinates: List<String>, clientDurationMs: Long): SolutionClaimOutcome? = null

    override suspend fun forfeitFriendsArenaMatch(matchId: String): Boolean = true

    override suspend fun requestFriendsArenaRematch(roomId: String): FriendsArenaMatchDto? = _currentFriendsArenaMatch.value

    override fun clearFriendsArenaRematchRequested() {
        _friendsArenaRematchRequested.value = null
    }

    override fun clearFriendsArenaMatch() {
        _currentFriendsArenaMatch.value = null
    }
}
