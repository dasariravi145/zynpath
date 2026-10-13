package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProceduralPuzzleGeneratorBenchmarkTest {

    private ProceduralPuzzleGenerator generator;
    private ServerPuzzleValidator serverValidator;

    @BeforeEach
    void setUp() {
        generator = new ProceduralPuzzleGenerator();
        serverValidator = new ServerPuzzleValidator();
    }

    @Test
    @DisplayName("Deterministic reproducibility: Identical seed produces identical puzzle and fingerprint")
    void testDeterministicReproducibility() {
        long seed = 123456789L;
        GeneratedPuzzle p1 = generator.generatePuzzle(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, seed);
        GeneratedPuzzle p2 = generator.generatePuzzle(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, seed);

        assertThat(p1.fingerprint()).isEqualTo(p2.fingerprint());
        assertThat(p1.puzzleId()).isEqualTo(p2.puzzleId());
        assertThat(p1.gridRows()).isEqualTo(p2.gridRows());
        assertThat(p1.gridCols()).isEqualTo(p2.gridCols());
        assertThat(p1.checkpoints()).isEqualTo(p2.checkpoints());
        assertThat(p1.blockedEdges()).isEqualTo(p2.blockedEdges());
        assertThat(p1.canonicalSolution()).isEqualTo(p2.canonicalSolution());
        assertThat(p1.cognitiveScore()).isEqualTo(p2.cognitiveScore());
    }

    @Test
    @DisplayName("ServerPuzzleValidator independence: Generated puzzle solution passes authoritative server validation")
    void testServerPuzzleValidatorIndependence() {
        for (CompetitiveDifficultyTier tier : CompetitiveDifficultyTier.values()) {
            GeneratedPuzzle p = generator.generatePuzzle(GameMode.QUICK_DUEL, tier, 987654321L);
            PuzzleAssignment assignment = p.toPuzzleAssignment();
            ValidationOutcome outcome = serverValidator.validateSolution(assignment, p.canonicalSolution());

            assertThat(outcome.isValid())
                    .as("Validation failed for tier %s: %s", tier, outcome.getErrorMessage())
                    .isTrue();
        }
    }

    @Test
    @DisplayName("Stage A 300-Candidate Quality Gate Benchmark across 3 tiers and 2 modes")
    void testBenchmark300CandidatesAcrossTiersAndModes() throws IOException {
        int candidatesPerBatch = 50;
        GameMode[] modes = {GameMode.QUICK_DUEL, GameMode.FRIENDS_ARENA};
        CompetitiveDifficultyTier[] tiers = {
                CompetitiveDifficultyTier.VERY_HARD,
                CompetitiveDifficultyTier.EXPERT,
                CompetitiveDifficultyTier.MASTER
        };

        int totalAttempted = 0;
        int totalAccepted = 0;
        int totalRejected = 0;
        int totalTimeouts = 0;
        Map<String, Integer> rejectionBreakdown = new HashMap<>();
        Set<String> acceptedFingerprints = new HashSet<>();
        int duplicateFingerprints = 0;

        List<Long> latenciesMs = new ArrayList<>();
        List<Long> solverNodes = new ArrayList<>();
        Map<CompetitiveDifficultyTier, List<Double>> tierCognitiveScores = new HashMap<>();
        for (CompetitiveDifficultyTier t : tiers) {
            tierCognitiveScores.put(t, new ArrayList<>());
        }

        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long initialHeap = memBean.getHeapMemoryUsage().getUsed();
        long peakHeap = initialHeap;

        long benchmarkStartTime = System.currentTimeMillis();

        long baseSeed = 54321000L;
        int sampleIdx = 0;

        for (GameMode mode : modes) {
            for (CompetitiveDifficultyTier tier : tiers) {
                for (int i = 0; i < candidatesPerBatch; i++) {
                    sampleIdx++;
                    totalAttempted++;
                    long candidateSeed = baseSeed + sampleIdx * 1337L;

                    ProceduralPuzzleGenerator.CandidateOutcome outcome =
                            generator.generateSingleCandidate(mode, tier, candidateSeed, 0);

                    latenciesMs.add(outcome.durationMs());
                    solverNodes.add(outcome.nodesExplored());

                    long currentHeap = memBean.getHeapMemoryUsage().getUsed();
                    if (currentHeap > peakHeap) {
                        peakHeap = currentHeap;
                    }

                    if (outcome.timedOut()) {
                        totalTimeouts++;
                    }

                    if (outcome.accepted() && outcome.puzzle() != null) {
                        totalAccepted++;
                        GeneratedPuzzle p = outcome.puzzle();
                        tierCognitiveScores.get(tier).add(p.cognitiveScore());

                        if (!acceptedFingerprints.add(p.fingerprint())) {
                            duplicateFingerprints++;
                        }

                        // Independent server verification
                        ValidationOutcome val = serverValidator.validateSolution(p.toPuzzleAssignment(), p.canonicalSolution());
                        assertThat(val.isValid())
                                .as("Authoritative server validation failed for %s", p.puzzleId())
                                .isTrue();
                    } else {
                        totalRejected++;
                        String reason = outcome.rejectionReason();
                        if (reason == null) reason = "UNKNOWN";
                        else if (reason.startsWith("INSUFFICIENT_DIFFICULTY")) reason = "INSUFFICIENT_DIFFICULTY";
                        rejectionBreakdown.put(reason, rejectionBreakdown.getOrDefault(reason, 0) + 1);
                    }
                }
            }
        }

        long totalDurationMs = System.currentTimeMillis() - benchmarkStartTime;

        // Calculate statistics
        Collections.sort(latenciesMs);
        long p50Latency = latenciesMs.get((int) (latenciesMs.size() * 0.50));
        long p90Latency = latenciesMs.get((int) (latenciesMs.size() * 0.90));
        long p99Latency = latenciesMs.get((int) (latenciesMs.size() * 0.99));
        long maxLatency = latenciesMs.get(latenciesMs.size() - 1);

        Collections.sort(solverNodes);
        long p50Nodes = solverNodes.get((int) (solverNodes.size() * 0.50));
        long p90Nodes = solverNodes.get((int) (solverNodes.size() * 0.90));
        long maxNodes = solverNodes.get(solverNodes.size() - 1);

        double acceptRate = (totalAttempted > 0) ? (double) totalAccepted / totalAttempted * 100.0 : 0.0;

        System.out.println("=================================================================");
        System.out.println("STAGE A GENERATOR BENCHMARK REPORT (300 CANDIDATES)");
        System.out.println("=================================================================");
        System.out.printf("Total Attempted: %d\n", totalAttempted);
        System.out.printf("Total Accepted:  %d (%.2f%%)\n", totalAccepted, acceptRate);
        System.out.printf("Total Rejected:  %d\n", totalRejected);
        System.out.printf("Solver Timeouts: %d\n", totalTimeouts);
        System.out.printf("Duplicate Fingerprints: %d\n", duplicateFingerprints);
        System.out.printf("Rejection Breakdown: %s\n", rejectionBreakdown);
        System.out.printf("Latency (ms): p50=%d, p90=%d, p99=%d, max=%d\n", p50Latency, p90Latency, p99Latency, maxLatency);
        System.out.printf("Solver Nodes: p50=%d, p90=%d, max=%d\n", p50Nodes, p90Nodes, maxNodes);
        System.out.printf("Total Benchmark Time: %d ms (avg %.2f ms/candidate)\n", totalDurationMs, (double) totalDurationMs / totalAttempted);
        System.out.printf("Peak Heap Memory: %.2f MB\n", (double) peakHeap / (1024 * 1024));

        for (CompetitiveDifficultyTier t : tiers) {
            List<Double> scores = tierCognitiveScores.get(t);
            if (!scores.isEmpty()) {
                double minScore = Collections.min(scores);
                double maxScore = Collections.max(scores);
                double avgScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                System.out.printf("Tier %s: count=%d, min=%.4f, max=%.4f, avg=%.4f\n", t, scores.size(), minScore, maxScore, avgScore);
            }
        }
        System.out.println("=================================================================");

        // Quality Gate Assertions
        assertThat(totalAttempted).isEqualTo(300);
        assertThat(totalAccepted).isGreaterThan(180); // At least 60% candidate acceptance rate
        assertThat(duplicateFingerprints).isEqualTo(0); // 0 duplicate fingerprints
        assertThat(p90Latency).isLessThan(5000); // 90th percentile under 5 seconds
    }
}
