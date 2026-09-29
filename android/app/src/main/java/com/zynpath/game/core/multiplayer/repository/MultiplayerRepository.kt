package com.zynpath.game.core.multiplayer.repository

import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface governing online multiplayer session lifecycle, matchmaking queues,
 * real-time progress synchronization, and authoritative result verification.
 *
 * Implements Prompt 20 Sections 14, 15, 16, 17, 26, 32, 46.
 */
interface MultiplayerRepository {

    val clientMatchState: StateFlow<ClientMatchState>

    val currentSession: StateFlow<MatchSessionSnapshotDto?>

    val ticketStatus: StateFlow<MatchmakingTicketStatus?>

    val opponentProgress: StateFlow<Map<String, MatchParticipantDto>>

    val countdownSeconds: StateFlow<Int?>

    val isConnected: StateFlow<Boolean>

    val errorMessage: StateFlow<String?>
    val matchResults: StateFlow<List<com.zynpath.game.core.multiplayer.model.MatchResultDto>>
    val incomingReaction: StateFlow<Pair<String, String>?>

    // Friend Duel & Rematch reactive state flows (Prompt 22)
    val incomingInvitations: StateFlow<List<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>>
    val activeOutgoingInvitation: StateFlow<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto?>
    val rematchStatus: StateFlow<com.zynpath.game.core.multiplayer.model.RematchStatusDto?>

    // Mini League reactive state flows & operations (Prompt 23)
    val currentMiniLeagueRoom: StateFlow<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?>
    val miniLeagueIncomingInvitations: StateFlow<List<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>>

    suspend fun createMiniLeagueRoom(maxParticipants: Int = 5): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?

    suspend fun getMiniLeagueRoom(roomId: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?

    suspend fun getMiniLeagueRoomByCode(roomCode: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?

    suspend fun joinMiniLeagueRoom(roomCode: String): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?

    suspend fun leaveMiniLeagueRoom(roomId: String): Boolean

    suspend fun setMiniLeagueReady(roomId: String, ready: Boolean): Boolean

    suspend fun startMiniLeagueMatch(roomId: String): MatchSessionSnapshotDto?

    suspend fun inviteFriendToMiniLeague(roomId: String, friendPublicZynpathId: String): com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation?

    suspend fun respondToMiniLeagueInvitation(invitationId: String, accept: Boolean): Boolean

    suspend fun cancelMiniLeagueInvitation(invitationId: String): Boolean

    suspend fun refreshMiniLeagueInvitations()

    fun clearMiniLeagueRoom()

    // Friends Arena reactive state flow & operations (Prompt 18)
    val currentFriendsArenaRoom: StateFlow<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?>

    suspend fun createFriendsArenaRoom(): com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?

    suspend fun getFriendsArenaRoom(roomId: String): com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?

    suspend fun getFriendsArenaRoomByCode(roomCode: String): com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?

    suspend fun joinFriendsArenaRoom(roomCode: String): com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?

    suspend fun leaveFriendsArenaRoom(roomId: String): Boolean

    suspend fun startFriendsArenaMatch(roomId: String): com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto?

    fun clearFriendsArenaRoom()

    fun subscribeToFriendsArenaRoomEvents(roomId: String)

    // Friends Arena Match Gameplay (Prompt 19)
    val currentFriendsArenaMatch: StateFlow<com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto?>

    suspend fun getFriendsArenaMatch(matchId: String): com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto?

    suspend fun getFriendsArenaMatchByRoom(roomId: String): com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto?

    suspend fun updateFriendsArenaProgress(matchId: String, coveredCells: Int, lastCheckpoint: Int): Boolean

    suspend fun submitFriendsArenaClaim(matchId: String, pathCoordinates: List<String>, clientDurationMs: Long): com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome?

    suspend fun forfeitFriendsArenaMatch(matchId: String): Boolean

    suspend fun requestFriendsArenaRematch(roomId: String): com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto?

    val friendsArenaRematchRequested: StateFlow<String?>

    fun clearFriendsArenaRematchRequested()

    fun clearFriendsArenaMatch()

    suspend fun startQuickDuelSearch(): Boolean

    suspend fun cancelMatchmaking(): Boolean

    suspend fun createFriendDuel(targetPublicZynpathId: String): Boolean

    suspend fun sendFriendDuelInvitation(targetPublicZynpathId: String): com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto?

    suspend fun acceptFriendDuelInvitation(invitationId: String): Boolean

    suspend fun declineFriendDuelInvitation(invitationId: String): Boolean

    suspend fun cancelFriendDuelInvitation(invitationId: String): Boolean

    suspend fun refreshInvitations()

    suspend fun requestRematch(): Boolean

    suspend fun respondToRematch(accept: Boolean): Boolean

    fun clearRematch()

    suspend fun createMiniLeague(roomName: String, maxParticipants: Int): Boolean

    suspend fun markReady(): Boolean

    suspend fun submitCompletion(
        pathCoordinates: List<String>,
        clientDurationMs: Long,
        movesCount: Int
    ): SolutionClaimOutcome?

    suspend fun forfeitMatch(): Boolean

    suspend fun reconnectToMatch(matchId: String): Boolean

    fun sendProgress(coveredCells: Int, lastCheckpoint: Int)

    fun sendReaction(reactionCode: String)

    fun leaveMatch()

    fun clearError()

    // Competitive Progression, Match History, Player Statistics & Leaderboards (Prompt 24)
    suspend fun getMatchHistory(
        mode: com.zynpath.game.core.multiplayer.model.GameMode?,
        page: Int = 0,
        pageSize: Int = 20
    ): com.zynpath.game.core.multiplayer.model.MatchHistoryResponse?

    suspend fun getMatchDetails(
        matchId: String
    ): com.zynpath.game.core.multiplayer.model.MatchDetails?

    suspend fun getCompetitiveStats(): com.zynpath.game.core.multiplayer.model.CompetitiveStats?

    suspend fun getPublicCompetitiveStats(
        publicZynpathId: String
    ): com.zynpath.game.core.multiplayer.model.PublicCompetitiveStats?

    suspend fun getLeaderboard(
        category: com.zynpath.game.core.multiplayer.model.LeaderboardCategory,
        period: com.zynpath.game.core.multiplayer.model.LeaderboardPeriod,
        page: Int = 0,
        pageSize: Int = 20
    ): com.zynpath.game.core.multiplayer.model.LeaderboardResponse?
}
