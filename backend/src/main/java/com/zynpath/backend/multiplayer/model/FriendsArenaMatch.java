package com.zynpath.backend.multiplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe authoritative match model for Friends Arena (2-5 players) (Prompt 19).
 *
 * Implements Prompt 19 Task 2:
 * - Match ID.
 * - Source room ID.
 * - Authoritative participant IDs (frozen roster).
 * - Host ID.
 * - Match status (COUNTDOWN, ACTIVE, COMPLETING, COMPLETED, CANCELLED).
 * - Shared puzzle identifier and solver-verified puzzle data.
 * - Server start and end timestamps.
 * - Individual player progress and independent boards.
 * - Individual completion state and authoritative finish ordering.
 * - Atomic version counter for optimistic/concurrency ordering.
 */
public class FriendsArenaMatch {

    private final String matchId;
    private final String sourceRoomId;
    private final String hostPlayerId;
    private final List<String> participantIds;
    private final PuzzleAssignment puzzleAssignment;
    private final long createdAt;

    private volatile FriendsArenaMatchStatus status;
    private volatile Long countdownStartedAt = null;
    private volatile Long startedAt = null;
    private volatile Long endedAt = null;

    private final Map<String, FriendsArenaMatchParticipant> participants = new ConcurrentHashMap<>();
    private final List<MatchResult> results = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger finishOrderCounter = new AtomicInteger(0);
    private final AtomicLong version = new AtomicLong(1);

    public FriendsArenaMatch(
            String matchId,
            String sourceRoomId,
            String hostPlayerId,
            List<String> participantIds,
            PuzzleAssignment puzzleAssignment,
            long createdAt
    ) {
        this.matchId = matchId;
        this.sourceRoomId = sourceRoomId;
        this.hostPlayerId = hostPlayerId;
        this.participantIds = Collections.unmodifiableList(new ArrayList<>(participantIds));
        this.puzzleAssignment = puzzleAssignment;
        this.createdAt = createdAt;
        this.status = FriendsArenaMatchStatus.COUNTDOWN;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getSourceRoomId() {
        return sourceRoomId;
    }

    public String getHostPlayerId() {
        return hostPlayerId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public PuzzleAssignment getPuzzleAssignment() {
        return puzzleAssignment;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public FriendsArenaMatchStatus getStatus() {
        return status;
    }

    public void setStatus(FriendsArenaMatchStatus status) {
        this.status = status;
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

    public Map<String, FriendsArenaMatchParticipant> getParticipants() {
        return participants;
    }

    public void addParticipant(FriendsArenaMatchParticipant participant) {
        participants.put(participant.getPlayerId(), participant);
    }

    public FriendsArenaMatchParticipant getParticipant(String playerId) {
        return participants.get(playerId);
    }

    public boolean hasParticipant(String playerId) {
        return participants.containsKey(playerId);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public long incrementVersion() {
        return version.incrementAndGet();
    }

    public long getVersion() {
        return version.get();
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

    public boolean isPlayable() {
        return status == FriendsArenaMatchStatus.ACTIVE || status == FriendsArenaMatchStatus.COMPLETING;
    }

    public MultiplayerDto.FriendsArenaMatchDto toDto() {
        List<MultiplayerDto.FriendsArenaMatchParticipantDto> participantDtos = new ArrayList<>();
        for (FriendsArenaMatchParticipant p : participants.values()) {
            participantDtos.add(new MultiplayerDto.FriendsArenaMatchParticipantDto(
                    p.getPlayerId(),
                    p.getPublicZynpathId(),
                    p.getDisplayName(),
                    p.getAvatarId(),
                    p.isHost(),
                    p.isConnected(),
                    p.getCoveredCells(),
                    p.getLastCheckpoint(),
                    p.getCompletedAt() != null,
                    p.getSolveTimeMs(),
                    p.isWinner(),
                    p.getFinishOrder()
            ));
        }

        return new MultiplayerDto.FriendsArenaMatchDto(
                matchId,
                sourceRoomId,
                hostPlayerId,
                status.name(),
                puzzleAssignment,
                participantDtos,
                countdownStartedAt,
                startedAt,
                endedAt,
                System.currentTimeMillis(),
                version.get(),
                getResults()
        );
    }
}
