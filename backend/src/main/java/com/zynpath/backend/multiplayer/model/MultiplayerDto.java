package com.zynpath.backend.multiplayer.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request and response DTOs for multiplayer REST and WebSocket communication.
 *
 * Implements Prompt 20 Sections 14, 15, 16, 17, 35, 41, 43.
 */
public class MultiplayerDto {

    public record MatchmakingStatusResponse(
        String ticketId,
        String status, // "SEARCHING", "MATCH_FOUND", "CANCELLED", "TIMEOUT"
        String matchId,
        long enqueuedAt,
        long currentWaitMs
    ) {}

    public record CreateFriendDuelRequest(
        @NotBlank(message = "Target public Zynpath ID is required")
        String targetPublicZynpathId
    ) {}

    public record CreateMiniLeagueRequest(
        @NotBlank(message = "Room name is required")
        String roomName,

        @Min(value = 2, message = "Mini League requires at least 2 participants")
        @Max(value = 5, message = "Mini League supports a maximum of 5 participants")
        int maxParticipants
    ) {}

    public record SubmitSolutionClaimRequest(
        @NotBlank(message = "Match ID is required")
        String matchId,

        @NotBlank(message = "Puzzle ID is required")
        String puzzleId,

        @NotEmpty(message = "Solution path coordinates are required")
        List<String> pathCoordinates,

        long clientReportedSolveTimeMs,
        int movesCount
    ) {}

    public record ProgressUpdatePayload(
        int coveredCells,
        int lastCheckpoint
    ) {}

    public record PresetReactionPayload(
        @NotBlank(message = "Reaction code is required")
        String reactionCode
    ) {}

    public record MatchActionResponse(
        String matchId,
        String status,
        String message
    ) {}

    public record FriendDuelInvitationRequest(
        @NotBlank(message = "Target public Zynpath ID is required")
        String targetPublicZynpathId
    ) {}

    public record FriendDuelInvitationDto(
        String invitationId,
        String inviterPlayerId,
        String inviterPublicId,
        String inviterDisplayName,
        String recipientPlayerId,
        String recipientPublicId,
        String recipientDisplayName,
        String gameMode,
        String status,
        long createdAt,
        long expiresAt,
        String matchId,
        String previousMatchId
    ) {}

    public record RematchRequest(
        @NotBlank(message = "Previous match ID is required")
        String previousMatchId
    ) {}

    public record RematchResponseRequest(
        @NotBlank(message = "Previous match ID is required")
        String previousMatchId,
        boolean accept
    ) {}

    public record RematchStatusDto(
        String previousMatchId,
        String requesterPlayerId,
        String status,
        long createdAt,
        long expiresAt,
        String newMatchId
    ) {}

    // Mini League DTOs (Prompt 23)
    public record JoinRoomRequest(
        @NotBlank(message = "Room code is required")
        String roomCode
    ) {}

    public record MiniLeagueParticipantDto(
        String playerId,
        String publicZynpathId,
        String displayName,
        String avatarId,
        boolean isHost,
        boolean isReady,
        boolean isConnected,
        int coveredCells,
        int lastCheckpoint,
        boolean completed,
        Long solveTimeMs,
        Integer finishOrder
    ) {}

    public record MiniLeagueRoomDto(
        String roomId,
        String roomCode,
        String roomName,
        String hostPlayerId,
        String state,
        int maxParticipants,
        int currentParticipants,
        List<MiniLeagueParticipantDto> participants,
        String matchId,
        long createdAt,
        long expiresAt
    ) {}

    public record MiniLeagueInviteRequest(
        @NotBlank(message = "Target public Zynpath ID is required")
        String targetPublicZynpathId
    ) {}

    public record MiniLeagueInvitationDto(
        String invitationId,
        String roomId,
        String roomCode,
        String roomName,
        String inviterPlayerId,
        String inviterPublicId,
        String inviterDisplayName,
        String recipientPlayerId,
        String recipientPublicId,
        String recipientDisplayName,
        String status,
        long createdAt,
        long expiresAt
    ) {}

    public record UpdateReadyRequest(
        boolean ready
    ) {}

    // Friends Arena DTOs (Prompt 18)
    public record CreateFriendsArenaRoomRequest(
        String idempotencyKey
    ) {}

    public record JoinFriendsArenaRoomRequest(
        @NotBlank(message = "Room code is required")
        String roomCode
    ) {}

    public record FriendsArenaMemberDto(
        String playerId,
        String publicZynpathId,
        String displayName,
        String avatarId,
        boolean isHost,
        boolean isReady,
        long joinedAt
    ) {}

    public record FriendsArenaRoomDto(
        String roomId,
        String roomCode,
        String hostPlayerId,
        String status,
        int currentOccupancy,
        int maxCapacity,
        List<FriendsArenaMemberDto> members,
        String activeMatchId,
        long createdAt,
        long version
    ) {
        public FriendsArenaRoomDto(String roomId, String roomCode, String hostPlayerId, String status, int currentOccupancy, int maxCapacity, List<FriendsArenaMemberDto> members, long createdAt, long version) {
            this(roomId, roomCode, hostPlayerId, status, currentOccupancy, maxCapacity, members, null, createdAt, version);
        }
    }

    // Friends Arena Match DTOs (Prompt 19)
    public record FriendsArenaMatchParticipantDto(
        String playerId,
        String publicZynpathId,
        String displayName,
        String avatarId,
        boolean isHost,
        boolean isConnected,
        int coveredCells,
        int lastCheckpoint,
        boolean isCompleted,
        Long solveTimeMs,
        boolean isWinner,
        Integer finishOrder
    ) {}

    public record FriendsArenaMatchDto(
        String matchId,
        String roomId,
        String hostPlayerId,
        String status,
        PuzzleAssignment puzzleAssignment,
        List<FriendsArenaMatchParticipantDto> participants,
        Long countdownStartedAt,
        Long startedAt,
        Long endedAt,
        long serverTime,
        long version,
        List<MatchResult> results
    ) {}

    public record FriendsArenaProgressRequest(
        int coveredCells,
        int lastCheckpoint
    ) {}

    public record FriendsArenaClaimRequest(
        @NotEmpty(message = "Path coordinates are required")
        List<String> pathCoordinates,
        long clientReportedSolveTimeMs
    ) {}
}

