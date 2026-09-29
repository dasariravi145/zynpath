package com.zynpath.backend.social.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SocialDto {

    public record SendFriendRequestPayload(
        @NotBlank(message = "Target public Zynpath ID is required")
        String targetPublicZynpathId
    ) {}

    public record FriendActionResponse(
        String message,
        String status
    ) {}

    public record PresenceHeartbeatPayload(
        @NotNull(message = "Presence state is required")
        PlayerPresenceState presenceState
    ) {}

    public record HeartbeatDto(
        PlayerPresenceState state
    ) {}

    public record FriendPresenceDto(
        String playerId,
        String publicZynpathId,
        PlayerPresenceState presenceState
    ) {}

    public record PlayerPresenceUpdate(
        String playerId,
        String publicZynpathId,
        PlayerPresenceState presenceState,
        long timestamp
    ) {}

    public record SendMultiplayerInvitePayload(
        @NotBlank(message = "Target public Zynpath ID is required")
        String targetPublicZynpathId,

        @NotNull(message = "Game mode is required")
        MultiplayerGameMode gameMode,

        @NotBlank(message = "Room code is required")
        String roomCode
    ) {}
}
