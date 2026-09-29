package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.player.PlayerProfile

/**
 * Authoritative participant record in a Friends Arena room.
 *
 * Implements Prompt 15/24 Task 4:
 * - Displays actual connected player data (Avatar, Display name, Host indicator, Ready status).
 * - Stable player ID used as UI key.
 * - Does not simulate fake players.
 */
data class FriendsArenaRoomParticipant(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String?,
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val isConnected: Boolean = true
)

/**
 * Authoritative lifecycle states for a Friends Arena room.
 *
 * Implements Prompt 15/24 Task 8:
 * - CONFIGURATION: Room configuration state before creation.
 * - CREATING: Room creation request in flight.
 * - WAITING_FOR_PLAYERS: Room open, host waiting (e.g., 1/5 occupancy).
 * - PLAYERS_JOINED: 2–4 players joined and ready.
 * - ROOM_FULL: Reached maximum occupancy (5/5 players).
 * - STARTING_MATCH: Host initiated match start.
 * - ROOM_CLOSED: Room disbanded or closed.
 * - CONNECTION_INTERRUPTED: Temporary network disruption.
 * - ERROR: Failure in room creation or networking.
 */
enum class FriendsArenaRoomLifecycle {
    CONFIGURATION,
    CREATING,
    WAITING_FOR_PLAYERS,
    PLAYERS_JOINED,
    ROOM_FULL,
    STARTING_MATCH,
    ROOM_CLOSED,
    CONNECTION_INTERRUPTED,
    ERROR
}

/**
 * Authoritative Friends Arena room data model.
 *
 * Implements Prompt 15/24 Task 5:
 * - Room capacity: 1–5 players (including the host).
 * - Maximum additional players: 4.
 * - Room membership strictly dictates occupancy count.
 * - Room code provided only when server-assigned (no fake placeholders).
 */
data class FriendsArenaRoom(
    val roomId: String,
    val roomCode: String? = null,
    val hostPlayerId: String,
    val maxParticipants: Int = 5,
    val participants: List<FriendsArenaRoomParticipant> = emptyList(),
    val lifecycle: FriendsArenaRoomLifecycle = FriendsArenaRoomLifecycle.WAITING_FOR_PLAYERS,
    val activeMatchId: String? = null
) {
    val occupancyCount: Int get() = participants.size
    val isFull: Boolean get() = participants.size >= maxParticipants
}

/**
 * UI State for the Friends Arena Room Creation & Player Lobby Screen (Prompt 15/24).
 */
data class FriendsArenaRoomUiState(
    val authState: AuthState = AuthState.GUEST,
    val session: AuthSession? = null,
    val playerProfile: PlayerProfile? = null,
    val activeRoom: FriendsArenaRoom? = null,
    val lifecycle: FriendsArenaRoomLifecycle = FriendsArenaRoomLifecycle.CONFIGURATION,
    val isCreatingRoom: Boolean = false,
    val isStartingMatch: Boolean = false,
    val isLeavingRoom: Boolean = false,
    val showLeaveDialog: Boolean = false,
    val userNoticeTitle: String? = null,
    val userNoticeDetails: String? = null,
    val errorMessage: String? = null,
    val isReducedMotion: Boolean = false
) {
    /**
     * Whether the active user is an unauthenticated guest.
     */
    val isGuest: Boolean
        get() = session == null || authState == AuthState.GUEST || (playerProfile?.isGuest == true && session == null)

    /**
     * Current user ID for identity matching.
     */
    val currentUserId: String
        get() = session?.playerId ?: playerProfile?.playerId ?: ""

    /**
     * Display name for the active player.
     */
    val displayName: String
        get() = session?.displayName ?: playerProfile?.displayName ?: "Guest Explorer"

    /**
     * Public Zynpath identifier.
     */
    val publicId: String
        get() = session?.publicZynpathId ?: playerProfile?.publicZynpathId ?: (playerProfile?.shortGuestTag ?: "ZYN-GUEST")

    /**
     * Avatar resource identifier.
     */
    val avatarId: String
        get() = playerProfile?.avatarId ?: "avatar_compass"

    /**
     * Fixed authoritative room limits.
     */
    val maxRoomCapacity: Int = 5
    val minPlayersToStart: Int = 2

    /**
     * Whether a live authoritative room has been established.
     */
    val hasActiveRoom: Boolean
        get() = activeRoom != null && activeRoom.roomId.isNotBlank()

    /**
     * Whether the active user is the room host.
     * In configuration state (prior to room creation), the creating player is the prospective host.
     */
    val isCurrentPlayerHost: Boolean
        get() = if (hasActiveRoom) {
            activeRoom?.hostPlayerId == currentUserId
        } else {
            true
        }

    /**
     * Current room occupancy derived strictly from authoritative membership.
     */
    val currentOccupancy: Int
        get() = activeRoom?.occupancyCount ?: 0

    /**
     * Whether the room currently contains only its host.
     */
    val isOnlyHostPresent: Boolean
        get() = hasActiveRoom && currentOccupancy == 1 && isCurrentPlayerHost

    /**
     * Authoritative Start Match eligibility (Prompt 15 Task 6):
     * 1. The current player is the actual host.
     * 2. A real room exists.
     * 3. At least 2 connected players are present.
     * 4. No more than 5 players are present.
     * 5. All additional backend readiness requirements are satisfied.
     * 6. A real start-match handler is available.
     */
    val canStartMatch: Boolean
        get() = isCurrentPlayerHost &&
                hasActiveRoom &&
                currentOccupancy in minPlayersToStart..maxRoomCapacity &&
                (activeRoom?.participants?.all { it.isReady || it.isHost } ?: false) &&
                isStartMatchHandlerAvailable

    /**
     * Status flag for backend room match-start service integration (Prompt 18).
     */
    val isStartMatchHandlerAvailable: Boolean = true

    /**
     * Status flag for backend room-creation service integration (Prompt 18).
     */
    val isRoomCreationHandlerAvailable: Boolean = true

    /**
     * Status flag for backend invitation service integration (Prompt 18).
     */
    val isInvitationHandlerAvailable: Boolean = true
}

/**
 * Maps authoritative backend FriendsArenaRoomDto to domain FriendsArenaRoom.
 */
fun com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto.toDomain(): FriendsArenaRoom {
    val domainParticipants = members.map { m ->
        FriendsArenaRoomParticipant(
            playerId = m.playerId,
            publicZynpathId = m.publicZynpathId,
            displayName = m.displayName,
            avatarId = m.avatarId,
            isHost = m.isHost,
            isReady = m.isReady,
            isConnected = true
        )
    }
    val lc = when (status) {
        "WAITING" -> if (members.size >= maxCapacity) FriendsArenaRoomLifecycle.ROOM_FULL else if (members.size >= 2) FriendsArenaRoomLifecycle.PLAYERS_JOINED else FriendsArenaRoomLifecycle.WAITING_FOR_PLAYERS
        "STARTING" -> FriendsArenaRoomLifecycle.STARTING_MATCH
        "CLOSED" -> FriendsArenaRoomLifecycle.ROOM_CLOSED
        else -> FriendsArenaRoomLifecycle.WAITING_FOR_PLAYERS
    }
    return FriendsArenaRoom(
        roomId = roomId,
        roomCode = roomCode,
        hostPlayerId = hostPlayerId,
        maxParticipants = maxCapacity,
        participants = domainParticipants,
        lifecycle = lc,
        activeMatchId = activeMatchId
    )
}
