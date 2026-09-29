package com.zynpath.backend.multiplayer.model;

/**
 * Authoritative record of a participant in a Friends Arena room.
 *
 * Implements Prompt 18 Task 2:
 * - Distinct player identity derived from authenticated session.
 * - Join timestamp preserving exact member join order.
 * - Readiness and host role flags.
 */
public class FriendsArenaMember {

    private final String playerId;
    private final String publicZynpathId;
    private final String displayName;
    private final String avatarId;
    private volatile boolean isHost;
    private volatile boolean isReady;
    private final long joinedAt;

    public FriendsArenaMember(
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            boolean isHost,
            boolean isReady,
            long joinedAt
    ) {
        this.playerId = playerId;
        this.publicZynpathId = publicZynpathId;
        this.displayName = displayName;
        this.avatarId = avatarId;
        this.isHost = isHost;
        this.isReady = isReady;
        this.joinedAt = joinedAt;
    }

    public String playerId() {
        return playerId;
    }

    public String publicZynpathId() {
        return publicZynpathId;
    }

    public String displayName() {
        return displayName;
    }

    public String avatarId() {
        return avatarId;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        this.isHost = host;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public long joinedAt() {
        return joinedAt;
    }

    public MultiplayerDto.FriendsArenaMemberDto toDto() {
        return new MultiplayerDto.FriendsArenaMemberDto(
                playerId,
                publicZynpathId,
                displayName,
                avatarId,
                isHost,
                isReady,
                joinedAt
        );
    }
}
