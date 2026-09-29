package com.zynpath.game.core.multiplayer.api

import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome
import com.zynpath.game.core.network.NetworkResult

/**
 * Remote API contract for Zynpath online multiplayer REST operations.
 *
 * Implements Prompt 20 Sections 14-17, 24, 32, 35:
 * - Matchmaking queue ingress and cancellation
 * - Friend Duel and Mini League session setup
 * - Authoritative state reconciliation & solution claim submission
 */
interface MultiplayerApiService {
    fun getBaseUrl(): String

    suspend fun enqueueQuickDuel(sessionToken: String): NetworkResult<MatchmakingTicketStatus>

    suspend fun cancelQuickDuel(sessionToken: String): NetworkResult<Boolean>

    suspend fun getQuickDuelStatus(sessionToken: String): NetworkResult<MatchmakingTicketStatus>

    suspend fun createFriendDuel(sessionToken: String, targetPublicZynpathId: String): NetworkResult<MatchSessionSnapshotDto>

    suspend fun createMiniLeague(sessionToken: String, roomName: String, maxParticipants: Int): NetworkResult<MatchSessionSnapshotDto>

    suspend fun getMatchSnapshot(sessionToken: String, matchId: String): NetworkResult<MatchSessionSnapshotDto>

    suspend fun markReady(sessionToken: String, matchId: String): NetworkResult<Unit>

    suspend fun submitClaim(
        sessionToken: String,
        matchId: String,
        puzzleId: String,
        pathCoordinates: List<String>,
        clientDurationMs: Long,
        movesCount: Int
    ): NetworkResult<SolutionClaimOutcome>

    suspend fun reconnect(sessionToken: String, matchId: String): NetworkResult<MatchSessionSnapshotDto>

    suspend fun forfeitMatch(sessionToken: String, matchId: String): NetworkResult<Unit>

    suspend fun getMatchResults(sessionToken: String, matchId: String): NetworkResult<List<com.zynpath.game.core.multiplayer.model.MatchResultDto>>

    // Friend Duel & Rematch operations (Prompt 22)
    suspend fun sendInvitation(sessionToken: String, targetPublicZynpathId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>

    suspend fun getIncomingInvitations(sessionToken: String): NetworkResult<List<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>>

    suspend fun getOutgoingInvitations(sessionToken: String): NetworkResult<List<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>>

    suspend fun getInvitation(sessionToken: String, invitationId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>

    suspend fun acceptInvitation(sessionToken: String, invitationId: String): NetworkResult<MatchSessionSnapshotDto>

    suspend fun declineInvitation(sessionToken: String, invitationId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>

    suspend fun cancelInvitation(sessionToken: String, invitationId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto>

    suspend fun requestRematch(sessionToken: String, matchId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.RematchStatusDto>

    suspend fun respondToRematch(sessionToken: String, matchId: String, accept: Boolean): NetworkResult<com.zynpath.game.core.multiplayer.model.RematchStatusDto>

    suspend fun getRematchStatus(sessionToken: String, matchId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.RematchStatusDto>

    // Mini League Operations (Prompt 23)
    suspend fun createMiniLeagueRoom(sessionToken: String, roomName: String, maxParticipants: Int): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom>

    suspend fun getMiniLeagueRoom(sessionToken: String, roomId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom>

    suspend fun getMiniLeagueRoomByCode(sessionToken: String, roomCode: String): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom>

    suspend fun joinMiniLeagueRoom(sessionToken: String, roomCode: String): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom>

    suspend fun leaveMiniLeagueRoom(sessionToken: String, roomId: String): NetworkResult<Unit>

    suspend fun setMiniLeagueReady(sessionToken: String, roomId: String, ready: Boolean): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom>

    suspend fun startMiniLeagueMatch(sessionToken: String, roomId: String): NetworkResult<MatchSessionSnapshotDto>

    suspend fun inviteToMiniLeague(sessionToken: String, roomId: String, targetPublicZynpathId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>

    suspend fun getMiniLeagueIncomingInvitations(sessionToken: String): NetworkResult<List<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>>

    suspend fun respondToMiniLeagueInvitation(sessionToken: String, invitationId: String, accept: Boolean): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?>

    suspend fun cancelMiniLeagueInvitation(sessionToken: String, invitationId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>

    // Friends Arena Private Rooms & Lobbies (Prompt 18)
    suspend fun createFriendsArenaRoom(sessionToken: String, idempotencyKey: String? = null): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto>

    suspend fun getFriendsArenaRoom(sessionToken: String, roomId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto>

    suspend fun getFriendsArenaRoomByCode(sessionToken: String, roomCode: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto>

    suspend fun joinFriendsArenaRoom(sessionToken: String, roomCode: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto>

    suspend fun leaveFriendsArenaRoom(sessionToken: String, roomId: String): NetworkResult<Unit>

    suspend fun startFriendsArenaMatch(sessionToken: String, roomId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto>

    // Friends Arena Match Gameplay (Prompt 19)
    suspend fun getFriendsArenaMatch(sessionToken: String, matchId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto>

    suspend fun getFriendsArenaMatchByRoom(sessionToken: String, roomId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto>

    suspend fun updateFriendsArenaProgress(sessionToken: String, matchId: String, coveredCells: Int, lastCheckpoint: Int): NetworkResult<Unit>

    suspend fun submitFriendsArenaClaim(sessionToken: String, matchId: String, pathCoordinates: List<String>, clientDurationMs: Long): NetworkResult<com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome>

    suspend fun forfeitFriendsArenaMatch(sessionToken: String, matchId: String): NetworkResult<Unit>

    suspend fun requestFriendsArenaRematch(sessionToken: String, roomId: String): NetworkResult<com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto>

    // Competitive Progression, Match History, Player Statistics & Leaderboards (Prompt 24)
    suspend fun getMatchHistory(
        sessionToken: String,
        mode: com.zynpath.game.core.multiplayer.model.GameMode?,
        page: Int = 0,
        pageSize: Int = 20
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MatchHistoryResponse>

    suspend fun getMatchDetails(
        sessionToken: String,
        matchId: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MatchDetails>

    suspend fun getCompetitiveStats(
        sessionToken: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.CompetitiveStats>

    suspend fun getPublicCompetitiveStats(
        publicZynpathId: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.PublicCompetitiveStats>

    suspend fun getLeaderboard(
        sessionToken: String?,
        category: com.zynpath.game.core.multiplayer.model.LeaderboardCategory,
        period: com.zynpath.game.core.multiplayer.model.LeaderboardPeriod,
        page: Int = 0,
        pageSize: Int = 20
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.LeaderboardResponse>
}


