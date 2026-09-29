package com.zynpath.backend.multiplayer.model;

/**
 * Mutable state of a participant within an active Friends Arena match (Prompt 19).
 * Tracks independent provisional progress, live connectivity, and verified completion.
 */
public class FriendsArenaMatchParticipant {

    private final String playerId;
    private final String publicZynpathId;
    private final String displayName;
    private final String avatarId;
    private final boolean isHost;
    private final long joinedAt;

    private volatile boolean connected = true;
    private volatile int coveredCells = 0;
    private volatile int lastCheckpoint = 1;
    private volatile Long completedAt = null;
    private volatile Long solveTimeMs = null;
    private volatile boolean isWinner = false;
    private volatile Integer finishOrder = null;

    public FriendsArenaMatchParticipant(
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            boolean isHost,
            long joinedAt
    ) {
        this.playerId = playerId;
        this.publicZynpathId = publicZynpathId;
        this.displayName = displayName;
        this.avatarId = avatarId != null ? avatarId : "avatar_compass";
        this.isHost = isHost;
        this.joinedAt = joinedAt;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPublicZynpathId() {
        return publicZynpathId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public boolean isHost() {
        return isHost;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getCoveredCells() {
        return coveredCells;
    }

    public void setCoveredCells(int coveredCells) {
        this.coveredCells = coveredCells;
    }

    public int getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(int lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public void updateProgress(int coveredCells, int lastCheckpoint) {
        this.coveredCells = coveredCells;
        this.lastCheckpoint = lastCheckpoint;
    }

    public Long getCompletedAt() {
        return completedAt;
    }

    public Long getSolveTimeMs() {
        return solveTimeMs;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public Integer getFinishOrder() {
        return finishOrder;
    }

    public void markCompleted(long completedAt, long solveTimeMs, boolean isWinner, int finishOrder) {
        this.completedAt = completedAt;
        this.solveTimeMs = solveTimeMs;
        this.isWinner = isWinner;
        this.finishOrder = finishOrder;
    }
}
