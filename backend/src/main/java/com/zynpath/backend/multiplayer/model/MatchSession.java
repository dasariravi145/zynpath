package com.zynpath.backend.multiplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe authoritative in-memory representation of an active multiplayer match session.
 *
 * Implements Prompt 20 Sections 9, 10, 11, 21, 25, 33, 44 & 45:
 * - Backed by backend-issued stable matchId.
 * - Controls legal state transitions through the match state machine.
 * - Holds immutable assigned puzzle.
 * - Manages participant readiness, countdown, server timing, and sequence numbering.
 */
public class MatchSession {

    private final String matchId;
    private final GameMode gameMode;
    private volatile String hostPlayerId;
    private final PuzzleAssignment puzzleAssignment;
    private final long createdAt;
    private final long expiresAt;

    private volatile MatchState state;
    private volatile Long countdownStartedAt;
    private volatile Long startedAt;
    private volatile Long endedAt;

    private final Map<String, MatchParticipant> participants = new ConcurrentHashMap<>();
    private final List<MatchResult> results = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong eventSequence = new AtomicLong(0);
    private final AtomicInteger finishOrderCounter = new AtomicInteger(0);

    public MatchSession(
            String matchId,
            GameMode gameMode,
            String hostPlayerId,
            PuzzleAssignment puzzleAssignment,
            long createdAt,
            long ttlMs
    ) {
        this.matchId = matchId;
        this.gameMode = gameMode;
        this.hostPlayerId = hostPlayerId;
        this.puzzleAssignment = puzzleAssignment;
        this.createdAt = createdAt;
        this.expiresAt = createdAt + ttlMs;
        this.state = MatchState.CREATED;
    }

    public String getMatchId() {
        return matchId;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public String getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(String hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public PuzzleAssignment getPuzzleAssignment() {
        return puzzleAssignment;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public MatchState getState() {
        return state;
    }

    public synchronized boolean transitionTo(MatchState nextState) {
        if (!this.state.canTransitionTo(nextState)) {
            return false;
        }
        this.state = nextState;
        return true;
    }

    public Long getCountdownStartedAt() {
        return countdownStartedAt;
    }

    public void setCountdownStartedAt(Long countdownStartedAt) {
        this.countdownStartedAt = countdownStartedAt;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Long endedAt) {
        this.endedAt = endedAt;
    }

    public Map<String, MatchParticipant> getParticipants() {
        return participants;
    }

    public void addParticipant(MatchParticipant participant) {
        participants.put(participant.getPlayerId(), participant);
    }

    public MatchParticipant removeParticipant(String playerId) {
        return participants.remove(playerId);
    }

    public MatchParticipant getParticipant(String playerId) {
        return participants.get(playerId);
    }

    public boolean hasParticipant(String playerId) {
        return participants.containsKey(playerId);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public boolean areAllParticipantsReady() {
        if (participants.size() < gameMode.getMinParticipants()) {
            return false;
        }
        for (MatchParticipant p : participants.values()) {
            if (!p.isReady()) {
                return false;
            }
        }
        return true;
    }

    public long nextSequenceNumber() {
        return eventSequence.incrementAndGet();
    }

    public long getCurrentSequenceNumber() {
        return eventSequence.get();
    }

    public synchronized int getNextFinishOrder() {
        return finishOrderCounter.incrementAndGet();
    }

    public List<MatchResult> getResults() {
        synchronized (results) {
            return new ArrayList<>(results);
        }
    }

    public void addResult(MatchResult result) {
        results.add(result);
    }

    public ReconnectionSnapshot toSnapshot() {
        List<ReconnectionSnapshot.ParticipantSummary> pSummaries = new ArrayList<>();
        for (MatchParticipant p : participants.values()) {
            pSummaries.add(new ReconnectionSnapshot.ParticipantSummary(
                    p.getPlayerId(),
                    p.getPublicZynpathId(),
                    p.getDisplayName(),
                    p.getAvatarId(),
                    p.isReady(),
                    p.isConnected(),
                    p.getCoveredCells(),
                    p.getLastCheckpoint(),
                    p.getCompletedAt() != null,
                    p.getSolveTimeMs(),
                    p.isWinner(),
                    p.getFinishOrder()
            ));
        }

        return new ReconnectionSnapshot(
                matchId,
                gameMode,
                state,
                puzzleAssignment,
                pSummaries,
                countdownStartedAt,
                startedAt,
                endedAt,
                System.currentTimeMillis(),
                eventSequence.get(),
                state.isPlayable(),
                getResults()
        );
    }
}
