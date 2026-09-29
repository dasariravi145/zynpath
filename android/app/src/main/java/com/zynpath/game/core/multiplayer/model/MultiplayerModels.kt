package com.zynpath.game.core.multiplayer.model

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator

/**
 * Game modes supported by Zynpath online multiplayer foundation.
 *
 * Implements Prompt 20 Section 8:
 * - QUICK_DUEL: 2 automatically matched players.
 * - FRIEND_DUEL: 2 players connected via direct invitation.
 * - MINI_LEAGUE: 2-5 total participants room tournament.
 */
enum class GameMode(val displayName: String, val minParticipants: Int, val maxParticipants: Int) {
    QUICK_DUEL("Quick Duel", 2, 2),
    FRIEND_DUEL("Friend Duel", 2, 2),
    MINI_LEAGUE("Mini League", 2, 5),
    FRIENDS_ARENA("Friends Arena", 2, 5);

    companion object {
        fun fromString(value: String): GameMode {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: QUICK_DUEL
        }
    }
}

/**
 * Server-authoritative match finite state machine.
 *
 * Implements Prompt 20 Sections 10 & 11:
 * Validated states with unidirectional progression and terminal states.
 */
enum class MatchState {
    CREATED,
    WAITING_FOR_PLAYERS,
    READY,
    COUNTDOWN,
    ACTIVE,
    COMPLETING,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    fun isTerminal(): Boolean = this in listOf(COMPLETED, CANCELLED, EXPIRED)
    fun isPlayable(): Boolean = this in listOf(COUNTDOWN, ACTIVE, COMPLETING)

    companion object {
        fun fromString(value: String): MatchState {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CREATED
        }
    }
}

/**
 * Client presentation and lifecycle state.
 *
 * Implements Prompt 20 Section 46:
 * Exposes explicit lifecycle states to ViewModel and UI components.
 */
enum class ClientMatchState {
    IDLE,
    SEARCHING,
    MATCH_FOUND,
    WAITING,
    READY,
    COUNTDOWN,
    ACTIVE,
    RECONNECTING,
    COMPLETED,
    CANCELLED,
    ERROR
}

/**
 * Server-assigned solver-verified immutable puzzle assignment.
 *
 * Implements Prompt 20 Sections 21-24:
 * Shared identically across all match participants and immutable across reconnection.
 */
data class PuzzleAssignmentDto(
    val puzzleId: String,
    val gameMode: GameMode,
    val width: Int,
    val height: Int,
    val requiredCells: List<String>,
    val checkpoints: Map<Int, String>,
    val blockedEdges: List<String>,
    val fingerprint: String
)

/**
 * Snapshot of a match participant.
 *
 * Implements Prompt 20 Section 12.
 */
data class MatchParticipantDto(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String?,
    val isReady: Boolean = false,
    val isConnected: Boolean = true,
    val isCompleted: Boolean = false,
    val isWinner: Boolean = false,
    val solveTimeMs: Long? = null,
    val finishOrder: Int? = null,
    val coveredCellsCount: Int = 0,
    val lastCheckpoint: Int = 1
) {
    val coveredCells: Int get() = coveredCellsCount
}

/**
 * Authoritative record of a participant's outcome in a completed match.
 *
 * Implements Prompt 20 Section 39 & Prompt 21 Section 31, 33, 46.
 */
data class MatchResultDto(
    val matchId: String,
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val completed: Boolean,
    val solveTimeMs: Long? = null,
    val finishOrder: Int = 1,
    val isWinner: Boolean = false,
    val resultStatus: String = "COMPLETED" // "VICTORY", "DEFEAT", "TIED", "FORFEIT", "DNF", "ABANDONED"
) {
    val isValidated: Boolean get() = completed
    val isTie: Boolean get() = resultStatus == "TIED"
    fun formattedTime(): String = solveTimeMs?.let { "${it / 1000}.${(it % 1000) / 100}s" } ?: "--"
}

/**
 * Complete authoritative session snapshot for state reconciliation & reconnection.
 *
 * Implements Prompt 20 Sections 9 & 33 & Prompt 21 Section 37.
 */
data class MatchSessionSnapshotDto(
    val matchId: String,
    val gameMode: GameMode,
    val matchState: MatchState,
    val hostPlayerId: String,
    val puzzle: PuzzleAssignmentDto?,
    val participants: List<MatchParticipantDto>,
    val createdAt: Long,
    val startedAt: Long?,
    val endedAt: Long?,
    val countdownDurationMs: Long = 3000L,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val results: List<MatchResultDto> = emptyList()
) {
    val hostDisplayName: String
        get() = participants.firstOrNull { it.playerId == hostPlayerId }?.displayName ?: "Host"
    val allReady: Boolean
        get() = participants.isNotEmpty() && participants.all { it.isReady || it.playerId == hostPlayerId }
}

/**
 * Quick Duel queue status ticket.
 *
 * Implements Prompt 20 Sections 14, 15, 20.
 */
data class MatchmakingTicketStatus(
    val ticketId: String?,
    val status: String, // "IDLE", "SEARCHING", "MATCH_FOUND", "CANCELLED", "TIMEOUT"
    val matchId: String?,
    val enqueuedAt: Long,
    val currentWaitMs: Long
)

/**
 * Validated outcome from authoritative server-side completion claim.
 *
 * Implements Prompt 20 Sections 35 & 36 & Prompt 21 Section 27.
 */
data class SolutionClaimOutcome(
    val valid: Boolean,
    val rejectionReason: String?,
    val finishOrder: Int? = null,
    val isWinner: Boolean = false,
    val solveTimeMs: Long? = null
)

/**
 * Converts a server-assigned [PuzzleAssignmentDto] into an authoritative domain [PuzzleDefinition].
 * Validates topology and structural invariants before gameplay (Prompt 21 Section 17).
 */
fun PuzzleAssignmentDto.toPuzzleDefinition(): PuzzleDefinition {
    val dimensions = GridDimensions(height, width)

    val parsedRequiredCells = if (requiredCells.isNotEmpty()) {
        requiredCells.mapNotNull { coord ->
            val parts = coord.split(",")
            if (parts.size == 2) {
                val r = parts[0].trim().toIntOrNull()
                val c = parts[1].trim().toIntOrNull()
                if (r != null && c != null) GridPosition(r, c) else null
            } else null
        }.toSet()
    } else {
        dimensions.allPositions().toSet()
    }

    val parsedCheckpoints = checkpoints.mapNotNull { (num, coord) ->
        val parts = coord.split(",")
        if (parts.size == 2) {
            val r = parts[0].trim().toIntOrNull()
            val c = parts[1].trim().toIntOrNull()
            if (r != null && c != null) NumberedCheckpoint(num, GridPosition(r, c)) else null
        } else null
    }.sorted()

    val parsedBlockedEdges = blockedEdges.mapNotNull { edgeStr ->
        val sep = if (edgeStr.contains("|")) "|" else if (edgeStr.contains("-")) "-" else null
        if (sep != null) {
            val parts = edgeStr.split(sep)
            if (parts.size == 2) {
                val p1 = parts[0].split(",")
                val p2 = parts[1].split(",")
                if (p1.size == 2 && p2.size == 2) {
                    val r1 = p1[0].trim().toIntOrNull()
                    val c1 = p1[1].trim().toIntOrNull()
                    val r2 = p2[0].trim().toIntOrNull()
                    val c2 = p2[1].trim().toIntOrNull()
                    if (r1 != null && c1 != null && r2 != null && c2 != null) {
                        val pos1 = GridPosition(r1, c1)
                        val pos2 = GridPosition(r2, c2)
                        if (pos1.isOrthogonalNeighbor(pos2)) {
                            BlockedEdge.between(pos1, pos2)
                        } else null
                    } else null
                } else null
            } else null
        } else null
    }.toSet()

    val definition = PuzzleDefinition(
        puzzleId = puzzleId,
        puzzleVersion = 1,
        gridDimensions = dimensions,
        requiredCells = parsedRequiredCells,
        checkpoints = parsedCheckpoints,
        blockedEdges = parsedBlockedEdges,
        difficultyMetadata = "MULTIPLAYER_DUEL",
        seed = null
    )

    val validationResult = PuzzleDefinitionValidator.validate(definition)
    if (validationResult is com.zynpath.game.core.puzzle.validator.DefinitionValidationResult.Invalid) {
        throw IllegalStateException("Invalid multiplayer puzzle structure from server: ${validationResult.errorSummary}")
    }

    return definition
}

/**
 * Friend Duel invitation state.
 *
 * Implements Prompt 22 Sections 11 & 12.
 */
enum class FriendDuelInvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    EXPIRED,
    INVALIDATED;

    fun isActionable(): Boolean = this == PENDING

    companion object {
        fun fromString(value: String): FriendDuelInvitationStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

/**
 * Data representation of a 1v1 Friend Duel invitation between accepted friends.
 *
 * Implements Prompt 22 Section 11.
 */
data class FriendDuelInvitationDto(
    val invitationId: String,
    val inviterPlayerId: String,
    val inviterPublicZynpathId: String,
    val inviterDisplayName: String,
    val inviterAvatarId: String?,
    val recipientPlayerId: String,
    val recipientPublicZynpathId: String,
    val recipientDisplayName: String,
    val recipientAvatarId: String?,
    val gameMode: GameMode = GameMode.FRIEND_DUEL,
    val status: FriendDuelInvitationStatus = FriendDuelInvitationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 60000L,
    val matchId: String? = null
) {
    fun isExpired(): Boolean = System.currentTimeMillis() >= expiresAt
    fun remainingSeconds(): Int = ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0L) / 1000L).toInt()
}

/**
 * Rematch state machine status.
 *
 * Implements Prompt 22 Section 39.
 */
enum class RematchState {
    NOT_REQUESTED,
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    EXPIRED;

    companion object {
        fun fromString(value: String): RematchState {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NOT_REQUESTED
        }
    }
}

/**
 * Authoritative rematch state for a completed match.
 *
 * Implements Prompt 22 Sections 38-42.
 */
data class RematchStatusDto(
    val matchId: String,
    val requesterPlayerId: String?,
    val status: RematchState = RematchState.NOT_REQUESTED,
    val requestedAt: Long? = null,
    val expiresAt: Long? = null,
    val newMatchId: String? = null
) {
    fun isPending(): Boolean = status == RematchState.PENDING
    fun isAccepted(): Boolean = status == RematchState.ACCEPTED
    fun remainingSeconds(): Int {
        if (expiresAt == null) return 0
        return ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0L) / 1000L).toInt()
    }
}

/**
 * Participant within a Mini League private room lobby.
 *
 * Implements Prompt 23 Sections 6, 21, 22.
 */
data class MiniLeagueParticipant(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String?,
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val isConnected: Boolean = true,
    val coveredCells: Int = 0,
    val lastCheckpoint: Int = 1,
    val completed: Boolean = false,
    val solveTimeMs: Long? = null,
    val finishOrder: Int? = null
)

/**
 * Authoritative Mini League private room state.
 *
 * Implements Prompt 23 Sections 6, 9, 10, 11, 13, 21.
 */
data class MiniLeagueRoom(
    val roomId: String,
    val roomCode: String,
    val roomName: String,
    val hostPlayerId: String,
    val state: String = "WAITING_FOR_PLAYERS",
    val maxParticipants: Int = 5,
    val currentParticipants: Int = 1,
    val participants: List<MiniLeagueParticipant> = emptyList(),
    val matchId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 900000L
) {
    val isFull: Boolean get() = participants.size >= maxParticipants
    val canStart: Boolean get() = participants.size in 2..maxParticipants && participants.all { it.isReady }
    val allReady: Boolean get() = participants.isNotEmpty() && participants.all { it.isReady }
}

typealias MiniLeagueRoomParticipant = MiniLeagueParticipant

/**
 * Invitation to a Mini League private room.
 *
 * Implements Prompt 23 Sections 17, 18, 19.
 */
data class MiniLeagueInvitation(
    val invitationId: String,
    val roomId: String,
    val roomCode: String,
    val roomName: String,
    val inviterPlayerId: String,
    val inviterPublicZynpathId: String,
    val inviterDisplayName: String,
    val recipientPlayerId: String,
    val recipientPublicZynpathId: String,
    val recipientDisplayName: String,
    val status: FriendDuelInvitationStatus = FriendDuelInvitationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 60000L
) {
    constructor(
        invitationId: String,
        roomId: String,
        roomCode: String,
        hostPlayerId: String,
        hostDisplayName: String,
        hostAvatarId: String?,
        recipientPlayerId: String,
        recipientDisplayName: String,
        status: String,
        createdAt: Long,
        expiresAt: Long
    ) : this(
        invitationId = invitationId,
        roomId = roomId,
        roomCode = roomCode,
        roomName = "Mini League",
        inviterPlayerId = hostPlayerId,
        inviterPublicZynpathId = "",
        inviterDisplayName = hostDisplayName,
        recipientPlayerId = recipientPlayerId,
        recipientPublicZynpathId = "",
        recipientDisplayName = recipientDisplayName,
        status = try { FriendDuelInvitationStatus.valueOf(status) } catch (e: Exception) { FriendDuelInvitationStatus.PENDING },
        createdAt = createdAt,
        expiresAt = expiresAt
    )

    val hostDisplayName: String get() = inviterDisplayName
    fun isExpired(): Boolean = System.currentTimeMillis() >= expiresAt
    fun remainingSeconds(): Int = ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0L) / 1000L).toInt()
}

/**
 * Competitive Match History and Statistics models.
 *
 * Implements Prompt 24 Sections 6, 7, 10, 14, 15, 17, 21, 24, 29, 30.
 */
enum class LeaderboardCategory(val id: String, val title: String, val description: String) {
    QUICK_DUEL_WINS("QUICK_DUEL_WINS", "Quick Duel Victories", "Most 1v1 Quick Duel wins"),
    MINI_LEAGUE_WINS("MINI_LEAGUE_WINS", "Mini League 1st Places", "Most 1st place finishes in Mini League"),
    TOTAL_COMPLETIONS("TOTAL_COMPLETIONS", "Total Completions", "Most multiplayer puzzles solved")
}

enum class LeaderboardPeriod(val id: String, val displayName: String) {
    ALL_TIME("ALL_TIME", "All Time"),
    THIS_MONTH("THIS_MONTH", "This Month"),
    THIS_WEEK("THIS_WEEK", "This Week")
}

data class MatchParticipantSummary(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String? = null,
    val completed: Boolean = false,
    val solveTimeMs: Long? = null,
    val finishOrder: Int = 0,
    val isWinner: Boolean = false,
    val outcomeStatus: String = ""
)

data class MatchHistoryItem(
    val matchId: String,
    val gameMode: GameMode,
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val puzzleFingerprint: String = "",
    val gridRows: Int = 5,
    val gridCols: Int = 5,
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    val matchStatus: String = "COMPLETED",
    val participantCount: Int = 2,
    val participants: List<MatchParticipantSummary> = emptyList(),
    val myResult: MatchParticipantSummary? = null
)

data class MatchHistoryResponse(
    val items: List<MatchHistoryItem> = emptyList(),
    val page: Int = 0,
    val pageSize: Int = 20,
    val totalItems: Int = 0,
    val hasMore: Boolean = false
)

data class MatchDetails(
    val matchId: String,
    val gameMode: GameMode,
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val puzzleFingerprint: String = "",
    val gridRows: Int = 5,
    val gridCols: Int = 5,
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    val durationMs: Long = 0L,
    val matchStatus: String = "COMPLETED",
    val participantCount: Int = 2,
    val participants: List<MatchParticipantSummary> = emptyList(),
    val myResult: MatchParticipantSummary? = null
)

data class PersonalBestRecord(
    val gameMode: GameMode,
    val puzzleId: String,
    val matchId: String,
    val solveTimeMs: Long,
    val achievedAt: Long
)

data class CompetitiveStats(
    val playerId: String = "",
    val publicZynpathId: String = "",
    val displayName: String = "",
    val totalFinalizedMatches: Int = 0,
    val quickDuelMatches: Int = 0,
    val quickDuelWins: Int = 0,
    val quickDuelLosses: Int = 0,
    val quickDuelTies: Int = 0,
    val quickDuelWinRate: Double = 0.0,
    val friendDuelMatches: Int = 0,
    val friendDuelWins: Int = 0,
    val friendDuelLosses: Int = 0,
    val friendDuelTies: Int = 0,
    val friendDuelWinRate: Double = 0.0,
    val miniLeagueParticipations: Int = 0,
    val miniLeagueFirstPlaceFinishes: Int = 0,
    val miniLeagueTopThreeFinishes: Int = 0,
    val miniLeagueAverageFinishPosition: Double = 0.0,
    val totalValidatedCompletions: Int = 0,
    val personalBests: Map<String, PersonalBestRecord> = emptyMap()
)

data class PublicCompetitiveStats(
    val publicZynpathId: String = "",
    val displayName: String = "",
    val avatarId: String = "avatar_default",
    val totalFinalizedMatches: Int = 0,
    val quickDuelWins: Int = 0,
    val friendDuelWins: Int = 0,
    val miniLeagueWins: Int = 0,
    val totalCompletions: Int = 0
)

data class LeaderboardEntry(
    val rank: Int,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String = "avatar_default",
    val metricValue: Long = 0L,
    val formattedValue: String = ""
)

data class LeaderboardResponse(
    val category: LeaderboardCategory,
    val period: LeaderboardPeriod,
    val entries: List<LeaderboardEntry> = emptyList(),
    val myRank: Int? = null,
    val myMetricValue: Long? = null,
    val page: Int = 0,
    val pageSize: Int = 20,
    val totalEntries: Int = 0,
    val hasMore: Boolean = false
)

// Friends Arena DTOs (Prompt 18)
data class FriendsArenaMemberDto(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String? = "avatar_compass",
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val joinedAt: Long = 0L
)

data class FriendsArenaRoomDto(
    val roomId: String,
    val roomCode: String,
    val hostPlayerId: String,
    val status: String = "WAITING",
    val currentOccupancy: Int = 1,
    val maxCapacity: Int = 5,
    val members: List<FriendsArenaMemberDto> = emptyList(),
    val activeMatchId: String? = null,
    val createdAt: Long = 0L,
    val version: Long = 1L
)

// Friends Arena Match DTOs (Prompt 19)
data class FriendsArenaMatchParticipantDto(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String? = "avatar_compass",
    val isHost: Boolean = false,
    val isConnected: Boolean = true,
    val coveredCells: Int = 0,
    val lastCheckpoint: Int = 1,
    val isCompleted: Boolean = false,
    val solveTimeMs: Long? = null,
    val isWinner: Boolean = false,
    val finishOrder: Int? = null
)

data class FriendsArenaMatchDto(
    val matchId: String,
    val roomId: String,
    val hostPlayerId: String,
    val status: String = "COUNTDOWN",
    val puzzle: PuzzleAssignmentDto? = null,
    val participants: List<FriendsArenaMatchParticipantDto> = emptyList(),
    val countdownStartedAt: Long? = null,
    val startedAt: Long? = null,
    val endedAt: Long? = null,
    val serverTime: Long = 0L,
    val version: Long = 1L,
    val results: List<MatchResultDto> = emptyList()
)




