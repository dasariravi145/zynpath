package com.zynpath.backend.multiplayer.model;

/**
 * Ephemeral backend private room for 2-5 participants.
 *
 * Implements Prompt 23 Sections 6, 9, 10, 11, 13:
 * - Room ID and human-readable collision-resistant room code.
 * - Enforces minimum 2 and maximum 5 total participants (host + up to 4 invitees).
 * - Tied to authoritative MatchSession for puzzle and state lifecycle.
 */
public class MiniLeagueRoom {

    private final String roomId;
    private final String roomCode;
    private final String roomName;
    private volatile String hostPlayerId;
    private final int maxParticipants;
    private final String matchId;
    private final long createdAt;
    private final long expiresAt;

    public MiniLeagueRoom(
            String roomId,
            String roomCode,
            String roomName,
            String hostPlayerId,
            int maxParticipants,
            String matchId,
            long createdAt,
            long ttlMs
    ) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.hostPlayerId = hostPlayerId;
        this.maxParticipants = maxParticipants;
        this.matchId = matchId;
        this.createdAt = createdAt;
        this.expiresAt = createdAt + ttlMs;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(String hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public String getMatchId() {
        return matchId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
