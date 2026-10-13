package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Production dynamic puzzle inventory manager for competitive multiplayer matches.
 *
 * Implements Prompt Sections 5, 6, 7, 9:
 * - Pre-generation architecture: Bounded buffer of pre-validated puzzles for instantaneous O(1) match start.
 * - Logically separated namespaces for QUICK_DUEL and FRIENDS_ARENA.
 * - Supported tiers: VERY_HARD, EXPERT, MASTER.
 * - Repeat protection: Suppresses recent fingerprints assigned to match participants.
 * - Durable persistence: Interacts with MultiplayerPuzzlePersistencePort (in-memory or JPA).
 * - Safe fallback: Falls back to the 16 handcrafted puzzles in MultiplayerPuzzlePool if dynamic inventory is depleted.
 * - Bounded asynchronous replenishment safe for Cloud Run.
 * - Configuration-controlled startup prewarming (disabled by default to prevent startup database writes).
 */
@Service
public class DynamicPuzzleInventory {

    private static final Logger log = LoggerFactory.getLogger(DynamicPuzzleInventory.class);

    public static final int TARGET_BUFFER_PER_TIER = 10;
    public static final int RECENT_HISTORY_LOOKBACK = 10;

    private final ProceduralPuzzleGenerator generator;
    private final MultiplayerPuzzlePersistencePort persistencePort;
    private final MultiplayerPuzzlePool fallbackPool;
    private final boolean startupPrewarmEnabled;
    private final ExecutorService replenishmentExecutor;
    private final AtomicBoolean isReplenishing = new AtomicBoolean(false);

    private final Object assignmentLock = new Object();

    public DynamicPuzzleInventory(
            ProceduralPuzzleGenerator generator,
            MultiplayerPuzzlePersistencePort persistencePort,
            MultiplayerPuzzlePool fallbackPool
    ) {
        this(generator, persistencePort, fallbackPool, false);
    }

    @Autowired
    public DynamicPuzzleInventory(
            ProceduralPuzzleGenerator generator,
            MultiplayerPuzzlePersistencePort persistencePort,
            MultiplayerPuzzlePool fallbackPool,
            @Value("${zynpath.multiplayer.puzzle-inventory.startup-prewarm-enabled:false}") boolean startupPrewarmEnabled
    ) {
        this(generator, persistencePort, fallbackPool, startupPrewarmEnabled, Executors.newSingleThreadExecutor());
    }

    DynamicPuzzleInventory(
            ProceduralPuzzleGenerator generator,
            MultiplayerPuzzlePersistencePort persistencePort,
            MultiplayerPuzzlePool fallbackPool,
            boolean startupPrewarmEnabled,
            ExecutorService replenishmentExecutor
    ) {
        this.generator = generator;
        this.persistencePort = persistencePort;
        this.fallbackPool = fallbackPool;
        this.startupPrewarmEnabled = startupPrewarmEnabled;
        this.replenishmentExecutor = replenishmentExecutor;
    }

    @PostConstruct
    public void init() {
        if (!startupPrewarmEnabled) {
            log.info("Multiplayer dynamic puzzle inventory startup prewarming is disabled (zynpath.multiplayer.puzzle-inventory.startup-prewarm-enabled=false).");
            return;
        }

        // Asynchronously populate baseline inventory on startup
        replenishmentExecutor.submit(() -> {
            try {
                log.info("Starting baseline multiplayer puzzle inventory pre-generation...");
                for (GameMode mode : List.of(GameMode.QUICK_DUEL, GameMode.FRIENDS_ARENA)) {
                    for (CompetitiveDifficultyTier tier : CompetitiveDifficultyTier.values()) {
                        replenishTierBuffer(mode, tier, 3);
                    }
                }
                log.info("Baseline multiplayer puzzle inventory successfully primed.");
            } catch (Exception e) {
                log.warn("Non-fatal exception during initial puzzle inventory pre-generation", e);
            }
        });
    }

    public boolean isStartupPrewarmEnabled() {
        return startupPrewarmEnabled;
    }

    /**
     * Authoritatively acquires a solver-verified puzzle for a match.
     * Guaranteed O(1) match-start latency.
     * If dynamic inventory is depleted, applies safe fallback to the 16 handcrafted puzzles.
     */
    public PuzzleAssignment acquirePuzzleForMatch(
            GameMode mode,
            CompetitiveDifficultyTier tier,
            String matchId,
            List<String> participantIds,
            String excludePuzzleId,
            long seed
    ) {
        if (mode == null) mode = GameMode.QUICK_DUEL;
        if (tier == null) tier = CompetitiveDifficultyTier.EXPERT;

        GeneratedPuzzle selected = null;
        synchronized (assignmentLock) {
            // 1. Gather recent fingerprints for participants to prevent repeats
            Set<String> suppressedFingerprints = new HashSet<>();
            if (participantIds != null) {
                for (String pid : participantIds) {
                    suppressedFingerprints.addAll(persistencePort.getRecentPuzzleFingerprintsForPlayer(pid, RECENT_HISTORY_LOOKBACK));
                }
            }

            // 2. Fetch available dynamic puzzles
            List<GeneratedPuzzle> available = persistencePort.findAvailablePuzzles(mode, tier, 20);

            for (GeneratedPuzzle candidate : available) {
                if (excludePuzzleId != null && candidate.puzzleId().equals(excludePuzzleId)) {
                    continue;
                }
                if (suppressedFingerprints.contains(candidate.fingerprint())) {
                    continue;
                }
                selected = candidate;
                break;
            }

            // If no candidate avoided recent fingerprints, relax repeat suppression but still honor excludePuzzleId
            if (selected == null && !available.isEmpty()) {
                for (GeneratedPuzzle candidate : available) {
                    if (excludePuzzleId == null || !candidate.puzzleId().equals(excludePuzzleId)) {
                        selected = candidate;
                        break;
                    }
                }
            }

            if (selected != null) {
                String assignmentId = "asgn_" + UUID.randomUUID().toString().substring(0, 12);
                persistencePort.recordAssignment(
                        assignmentId,
                        selected.puzzleId(),
                        matchId,
                        mode,
                        tier,
                        participantIds != null ? participantIds : List.of()
                );
            }
        }

        // 3. Trigger asynchronous replenishment if buffer is running low
        triggerReplenishmentIfNeeded(mode, tier);

        // 4. Return dynamic puzzle if available
        if (selected != null) {
            log.info("Assigned dynamic puzzle: matchId={}, puzzleId={}, tier={}, mode={}, fp={}",
                    matchId, selected.puzzleId(), tier, mode, selected.fingerprint());
            return selected.toPuzzleAssignment();
        }

        // 5. Safe Fallback: Fall back to verified 16 handcrafted puzzles in MultiplayerPuzzlePool
        int bracket = switch (tier) {
            case VERY_HARD -> 1;
            case EXPERT -> 2;
            case MASTER -> 2;
        };

        PuzzleAssignment fallback = fallbackPool.selectCompetitivePuzzle(mode, bracket, seed, excludePuzzleId);
        log.warn("Dynamic puzzle inventory depleted for mode={}, tier={}. Using verified handcrafted fallback: puzzleId={}",
                mode, tier, fallback.puzzleId());

        try {
            String assignmentId = "asgn_" + UUID.randomUUID().toString().substring(0, 12);
            persistencePort.recordAssignment(
                    assignmentId,
                    fallback.puzzleId(),
                    matchId,
                    mode,
                    tier,
                    participantIds != null ? participantIds : List.of()
            );
        } catch (Exception e) {
            log.warn("Failed to record fallback assignment for matchId={}, puzzleId={}", matchId, fallback.puzzleId(), e);
        }

        return fallback;
    }

    /**
     * Bounded inventory replenishment for the specified mode and tier.
     * Runs asynchronously without holding match assignment lock.
     */
    public void replenishTierBuffer(GameMode mode, CompetitiveDifficultyTier tier, int count) {
        long currentCount = persistencePort.countAvailablePuzzles(mode, tier);
        int needed = Math.max(0, count - (int) currentCount);
        if (needed <= 0) return;

        log.info("Replenishing inventory for mode={}, tier={}: generating {} puzzles...", mode, tier, needed);
        long seedBase = System.currentTimeMillis();

        for (int i = 0; i < needed; i++) {
            long seed = seedBase + (long) i * 104729L;
            try {
                GeneratedPuzzle puzzle = generator.generatePuzzle(mode, tier, seed);
                persistencePort.saveGeneratedPuzzle(puzzle);
            } catch (Exception e) {
                log.error("Failed to generate replenishment puzzle for mode={}, tier={}", mode, tier, e);
            }
        }
    }

    private void triggerReplenishmentIfNeeded(GameMode mode, CompetitiveDifficultyTier tier) {
        long count = persistencePort.countAvailablePuzzles(mode, tier);
        if (count < 3 && isReplenishing.compareAndSet(false, true)) {
            replenishmentExecutor.submit(() -> {
                try {
                    replenishTierBuffer(mode, tier, TARGET_BUFFER_PER_TIER);
                } finally {
                    isReplenishing.set(false);
                }
            });
        }
    }

    public long getAvailableCount(GameMode mode, CompetitiveDifficultyTier tier) {
        return persistencePort.countAvailablePuzzles(mode, tier);
    }
}
