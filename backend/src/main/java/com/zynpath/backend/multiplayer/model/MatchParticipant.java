package com.zynpath.backend.multiplayer.model;

/**
 * Mutable session state of a participant within an active multiplayer match.
 *
 * Implements Prompt 20 Section 12:
 * - Anchored to authenticated playerId from validated session.
 * - Tracks readiness, connectivity, live provisional progress, and final completion.
 */
public class MatchParticipant {

    private final String playerId;
    private final String publicZynpathId;
    private final String displayName;
    private final String avatarId;
    private final long joinedAt;

    private volatile boolean ready;
    private volatile boolean connected;
    private volatile int coveredCells;
    private volatile int lastCheckpoint;
    private volatile Long completedAt;
    private volatile Long solveTimeMs;
    private volatile boolean winner;
    private volatile boolean forfeited;
    private volatile Integer finishOrder;

    public MatchParticipant(
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            long joinedAt
    ) {
        this.playerId = playerId;
        this.publicZynpathId = publicZynpathId;
        this.displayName = displayName;
        this.avatarId = avatarId;
        this.joinedAt = joinedAt;
        this.ready = false;
        this.connected = true;
        this.coveredCells = 0;
        this.lastCheckpoint = 1;
        this.completedAt = null;
        this.solveTimeMs = null;
        this.winner = false;
        this.forfeited = false;
        this.finishOrder = null;
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

    public long getJoinedAt() {
        return joinedAt;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
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

    public void updateProgress(int coveredCells, int lastCheckpoint) {
        this.coveredCells = coveredCells;
        this.lastCheckpoint = lastCheckpoint;
    }

    public int getLastCheckpoint() {
        return lastCheckpoint;
    }

    public Long getCompletedAt() {
        return completedAt;
    }

    public void setCompleted(long completedAt, long solveTimeMs, boolean winner, int finishOrder) {
        this.completedAt = completedAt;
        this.solveTimeMs = solveTimeMs;
        this.winner = winner;
        this.finishOrder = finishOrder;
    }

    public Long getSolveTimeMs() {
        return solveTimeMs;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isForfeited() {
        return forfeited;
    }

    public void setForfeited(boolean forfeited) {
        this.forfeited = forfeited;
    }

    public Integer getFinishOrder() {
        return finishOrder;
    }

    public void setFinishOrder(Integer finishOrder) {
        this.finishOrder = finishOrder;
    }
}

