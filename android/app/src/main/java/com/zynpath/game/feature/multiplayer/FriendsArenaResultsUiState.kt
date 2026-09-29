package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchParticipantDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto

/**
 * Authoritative item in the Friends Arena standings list.
 */
data class FriendsArenaStandingItem(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String,
    val isHost: Boolean,
    val isLocalPlayer: Boolean,
    val isConnected: Boolean,
    val finishOrder: Int,
    val solveTimeMs: Long?,
    val isCompleted: Boolean,
    val isWinner: Boolean,
    val isTie: Boolean,
    val resultStatus: String
) {
    val isDnf: Boolean
        get() = !isCompleted

    val formattedSolveTime: String
        get() = solveTimeMs?.let { "${it / 1000}.${(it % 1000) / 100}s" } ?: if (isCompleted) "--" else "DNF"
}

/**
 * UI State for the Friends Arena post-match results screen (Prompt 20).
 */
data class FriendsArenaResultsUiState(
    val matchId: String? = null,
    val roomId: String? = null,
    val match: FriendsArenaMatchDto? = null,
    val room: FriendsArenaRoomDto? = null,
    val localPlayerId: String? = null,
    val isLoading: Boolean = true,
    val isRetrying: Boolean = false,
    val errorMessage: String? = null,
    val isRematchInFlight: Boolean = false,
    val rematchRequestedByOpponent: String? = null,
    val newMatchId: String? = null,
    val isReducedMotion: Boolean = false
) {
    val participants: List<FriendsArenaMatchParticipantDto>
        get() = match?.participants ?: emptyList()

    val results: List<MatchResultDto>
        get() = match?.results ?: emptyList()

    val localParticipant: FriendsArenaMatchParticipantDto?
        get() = participants.firstOrNull { it.playerId == localPlayerId }

    val localResult: MatchResultDto?
        get() = results.firstOrNull { it.playerId == localPlayerId }

    val isHost: Boolean
        get() = localParticipant?.isHost == true || (room != null && room.hostPlayerId == localPlayerId)

    val isWinner: Boolean
        get() = localResult?.isWinner == true || localParticipant?.isWinner == true

    val isTie: Boolean
        get() = localResult?.isTie == true || (results.count { it.isWinner } > 1)

    val isFinalized: Boolean
        get() = match?.status == "COMPLETED"

    val isAwaitingResult: Boolean
        get() = match != null && match.status != "COMPLETED" && match.status != "CANCELLED"

    val isInterrupted: Boolean
        get() = match?.status == "CANCELLED"

    val canRematch: Boolean
        get() = isHost && isFinalized && (room?.members?.size ?: participants.size) in 2..5

    val canReturnToRoom: Boolean
        get() = room != null && room.status != "CLOSED"

    val winnerParticipant: FriendsArenaMatchParticipantDto?
        get() = participants.firstOrNull { it.isWinner }

    val standings: List<FriendsArenaStandingItem>
        get() {
            if (participants.isEmpty()) return emptyList()

            val resultMap = results.associateBy { it.playerId }
            return participants.map { p ->
                val res = resultMap[p.playerId]
                val completed = res?.completed ?: p.isCompleted
                val order = res?.finishOrder ?: p.finishOrder ?: 99
                val time = res?.solveTimeMs ?: p.solveTimeMs
                val win = res?.isWinner ?: p.isWinner
                val tie = res?.isTie == true
                val status = res?.resultStatus ?: if (completed) "COMPLETED" else "DNF"

                FriendsArenaStandingItem(
                    playerId = p.playerId,
                    publicZynpathId = p.publicZynpathId,
                    displayName = p.displayName,
                    avatarId = p.avatarId ?: "avatar_compass",
                    isHost = p.isHost,
                    isLocalPlayer = p.playerId == localPlayerId,
                    isConnected = p.isConnected,
                    finishOrder = order,
                    solveTimeMs = time,
                    isCompleted = completed,
                    isWinner = win,
                    isTie = tie,
                    resultStatus = status
                )
            }.sortedWith(
                compareBy<FriendsArenaStandingItem> { !it.isCompleted }
                    .thenBy { it.finishOrder }
                    .thenBy { it.solveTimeMs ?: Long.MAX_VALUE }
            )
        }
}
