package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicPuzzleInventoryStressBenchmarkTest {

    private ProceduralPuzzleGenerator generator;
    private InMemoryMultiplayerPuzzlePersistenceAdapter persistencePort;
    private MultiplayerPuzzlePool fallbackPool;
    private DynamicPuzzleInventory inventory;

    @BeforeEach
    void setUp() {
        generator = new ProceduralPuzzleGenerator();
        persistencePort = new InMemoryMultiplayerPuzzlePersistenceAdapter();
        fallbackPool = new MultiplayerPuzzlePool();
        inventory = new DynamicPuzzleInventory(generator, persistencePort, fallbackPool);
    }

    @Test
    @DisplayName("Stage C Step 5: High-concurrency inventory depletion and fallback stress test")
    void testInventoryConcurrentStressAndFallback() throws Exception {
        // 1. Prime buffer with 10 puzzles
        inventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, 10);
        assertThat(inventory.getAvailableCount(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT)).isEqualTo(10);

        int totalRequests = 50;
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Long> latenciesMs = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger dynamicCount = new AtomicInteger(0);
        AtomicInteger fallbackCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> assignedPuzzleIds = Collections.synchronizedList(new ArrayList<>());
        Set<String> assignedFingerprints = Collections.synchronizedSet(new HashSet<>());

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < totalRequests; i++) {
            final int idx = i;
            tasks.add(() -> {
                long start = System.nanoTime();
                try {
                    String matchId = "stress_match_" + idx;
                    List<String> players = List.of("p_" + (idx * 2), "p_" + (idx * 2 + 1));
                    PuzzleAssignment puzzle = inventory.acquirePuzzleForMatch(
                            GameMode.QUICK_DUEL,
                            CompetitiveDifficultyTier.EXPERT,
                            matchId,
                            players,
                            null,
                            System.currentTimeMillis() + idx
                    );
                    long durationMs = (System.nanoTime() - start) / 1_000_000L;
                    latenciesMs.add(durationMs);

                    if (puzzle != null) {
                        assignedPuzzleIds.add(puzzle.puzzleId());
                        assignedFingerprints.add(puzzle.fingerprint());
                        if (puzzle.puzzleId().startsWith("mp_dyn_")) {
                            dynamicCount.incrementAndGet();
                        } else {
                            fallbackCount.incrementAndGet();
                        }
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        for (Future<Void> f : futures) {
            f.get();
        }
        executor.shutdown();

        // 2. Metrics calculation
        Collections.sort(latenciesMs);
        long p50 = latenciesMs.get((int) (latenciesMs.size() * 0.50));
        long p90 = latenciesMs.get((int) (latenciesMs.size() * 0.90));
        long p99 = latenciesMs.get((int) (latenciesMs.size() * 0.99));

        System.out.println("=== STAGE C STRESS BENCHMARK RESULTS ===");
        System.out.println("Total Match Requests: " + totalRequests);
        System.out.println("Failed Match Creations: " + failureCount.get());
        System.out.println("Dynamic Puzzles Served: " + dynamicCount.get());
        System.out.println("Fallback Puzzles Served: " + fallbackCount.get());
        System.out.println("Match Assignment Latency p50: " + p50 + " ms");
        System.out.println("Match Assignment Latency p90: " + p90 + " ms");
        System.out.println("Match Assignment Latency p99: " + p99 + " ms");
        System.out.println("Distinct Fingerprints in Run: " + assignedFingerprints.size());
        System.out.println("=========================================");

        assertThat(failureCount.get()).isEqualTo(0);
        assertThat(dynamicCount.get()).isGreaterThanOrEqualTo(10); // at least the initial 10 primed
        assertThat(fallbackCount.get()).isGreaterThan(0); // verified fallback triggered when depleted
        assertThat(p50).isLessThanOrEqualTo(5); // O(1) latency
    }
}
