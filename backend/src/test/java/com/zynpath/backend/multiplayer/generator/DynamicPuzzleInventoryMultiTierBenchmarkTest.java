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

class DynamicPuzzleInventoryMultiTierBenchmarkTest {

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

    private BenchmarkResult runTierBenchmark(int totalRequests, int threadCount) throws Exception {
        // Reset and prime buffer with 10 dynamic puzzles
        persistencePort = new InMemoryMultiplayerPuzzlePersistenceAdapter();
        inventory = new DynamicPuzzleInventory(generator, persistencePort, fallbackPool);
        inventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, 10);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Long> latenciesMs = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger dynamicCount = new AtomicInteger(0);
        AtomicInteger fallbackCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        Set<String> assignedFingerprints = Collections.synchronizedSet(new HashSet<>());

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < totalRequests; i++) {
            final int idx = i;
            tasks.add(() -> {
                long start = System.nanoTime();
                try {
                    String matchId = "tier_bench_" + totalRequests + "_" + idx;
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

        Collections.sort(latenciesMs);
        long p50 = latenciesMs.isEmpty() ? 0 : latenciesMs.get((int) (latenciesMs.size() * 0.50));
        long p90 = latenciesMs.isEmpty() ? 0 : latenciesMs.get((int) (latenciesMs.size() * 0.90));
        long p99 = latenciesMs.isEmpty() ? 0 : latenciesMs.get((int) (latenciesMs.size() * 0.99));

        double dynamicPct = (totalRequests > 0) ? (dynamicCount.get() * 100.0 / totalRequests) : 0.0;
        double fallbackPct = (totalRequests > 0) ? (fallbackCount.get() * 100.0 / totalRequests) : 0.0;

        return new BenchmarkResult(
                totalRequests,
                dynamicCount.get(),
                fallbackCount.get(),
                failureCount.get(),
                dynamicPct,
                fallbackPct,
                assignedFingerprints.size(),
                p50,
                p90,
                p99
        );
    }

    record BenchmarkResult(
            int totalRequests,
            int dynamicCount,
            int fallbackCount,
            int failureCount,
            double dynamicPct,
            double fallbackPct,
            int distinctFingerprints,
            long p50Ms,
            long p90Ms,
            long p99Ms
    ) {}

    @Test
    @DisplayName("Stage D Step 7: Focused Load Tests for 5, 10, 25, 50 concurrent requests")
    void testMultiTierConcurrencyLoad() throws Exception {
        int[] tiers = {5, 10, 25, 50};
        int[] threads = {5, 10, 10, 10};

        System.out.println("==========================================================================");
        System.out.println("STAGE D STEP 7 — DYNAMIC INVENTORY MULTI-TIER CONCURRENCY BENCHMARK");
        System.out.println("==========================================================================");
        System.out.printf("%-10s | %-10s | %-12s | %-12s | %-8s | %-8s | %-8s | %-8s%n",
                "Requests", "Dynamic %", "Fallback %", "Unique FP", "Failures", "p50(ms)", "p90(ms)", "p99(ms)");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < tiers.length; i++) {
            BenchmarkResult res = runTierBenchmark(tiers[i], threads[i]);
            System.out.printf("%-10d | %9.1f%% | %11.1f%% | %-12d | %-8d | %-8d | %-8d | %-8d%n",
                    res.totalRequests(),
                    res.dynamicPct(),
                    res.fallbackPct(),
                    res.distinctFingerprints(),
                    res.failureCount(),
                    res.p50Ms(),
                    res.p90Ms(),
                    res.p99Ms());

            assertThat(res.failureCount()).isEqualTo(0);
            if (tiers[i] <= 10) {
                assertThat(res.dynamicPct()).isEqualTo(100.0);
                assertThat(res.fallbackPct()).isEqualTo(0.0);
            }
        }
        System.out.println("==========================================================================");
    }
}
