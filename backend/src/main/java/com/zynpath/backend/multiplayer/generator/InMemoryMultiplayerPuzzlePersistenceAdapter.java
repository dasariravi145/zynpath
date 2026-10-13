package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory persistence adapter for multiplayer dynamic puzzles.
 * Active in dev and test environments when zynpath.persistence.mode=in-memory (default).
 */
@Component
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryMultiplayerPuzzlePersistenceAdapter implements MultiplayerPuzzlePersistencePort {

    private final Map<String, GeneratedPuzzle> puzzlesById = new ConcurrentHashMap<>();
    private final Map<String, GeneratedPuzzle> puzzlesByFingerprint = new ConcurrentHashMap<>();

    // Pool queues: key = "MODE:TIER" -> Deque of puzzleIds
    private final Map<String, Deque<String>> availablePools = new ConcurrentHashMap<>();

    // Player recent history: playerId -> Deque of fingerprints
    private final Map<String, Deque<String>> playerHistory = new ConcurrentHashMap<>();

    // Assignments: matchId -> assignment record
    private final Map<String, AssignmentRecord> assignmentsByMatch = new ConcurrentHashMap<>();

    public record AssignmentRecord(
            String assignmentId,
            String puzzleId,
            String matchId,
            GameMode mode,
            CompetitiveDifficultyTier tier,
            List<String> participantIds,
            long assignedAt
    ) {}

    private String poolKey(GameMode mode, CompetitiveDifficultyTier tier) {
        return mode.name() + ":" + tier.name();
    }

    @Override
    public void saveGeneratedPuzzle(GeneratedPuzzle puzzle) {
        if (puzzle == null) return;
        puzzlesById.put(puzzle.puzzleId(), puzzle);
        puzzlesByFingerprint.put(puzzle.fingerprint(), puzzle);

        String key = poolKey(puzzle.gameMode(), puzzle.difficultyTier());
        availablePools.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>()).offer(puzzle.puzzleId());
    }

    @Override
    public Optional<GeneratedPuzzle> findPuzzleById(String puzzleId) {
        return Optional.ofNullable(puzzlesById.get(puzzleId));
    }

    @Override
    public Optional<GeneratedPuzzle> findPuzzleByFingerprint(String fingerprint) {
        return Optional.ofNullable(puzzlesByFingerprint.get(fingerprint));
    }

    @Override
    public List<GeneratedPuzzle> findAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier, int limit) {
        String key = poolKey(mode, tier);
        Deque<String> queue = availablePools.get(key);
        if (queue == null || queue.isEmpty()) {
            return List.of();
        }

        List<GeneratedPuzzle> result = new ArrayList<>();
        for (String id : queue) {
            GeneratedPuzzle p = puzzlesById.get(id);
            if (p != null) {
                result.add(p);
                if (result.size() >= limit) break;
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public void recordAssignment(
            String assignmentId,
            String puzzleId,
            String matchId,
            GameMode mode,
            CompetitiveDifficultyTier tier,
            List<String> participantIds
    ) {
        long now = System.currentTimeMillis();
        assignmentsByMatch.put(matchId, new AssignmentRecord(
                assignmentId, puzzleId, matchId, mode, tier, participantIds, now
        ));

        // Consume puzzle from available pool upon assignment
        String poolKey = poolKey(mode, tier);
        Deque<String> queue = availablePools.get(poolKey);
        if (queue != null) {
            queue.remove(puzzleId);
        }

        GeneratedPuzzle puzzle = puzzlesById.get(puzzleId);
        String fingerprint = (puzzle != null) ? puzzle.fingerprint() : puzzleId;

        if (participantIds != null) {
            for (String pid : participantIds) {
                Deque<String> hist = playerHistory.computeIfAbsent(pid, k -> new ConcurrentLinkedDeque<>());
                hist.offerFirst(fingerprint);
                while (hist.size() > 50) {
                    hist.pollLast();
                }
            }
        }
    }

    @Override
    public List<String> getRecentPuzzleFingerprintsForPlayer(String playerId, int limit) {
        Deque<String> hist = playerHistory.get(playerId);
        if (hist == null || hist.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String fp : hist) {
            result.add(fp);
            if (result.size() >= limit) break;
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public long countAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier) {
        Deque<String> queue = availablePools.get(poolKey(mode, tier));
        return (queue != null) ? queue.size() : 0L;
    }
}
