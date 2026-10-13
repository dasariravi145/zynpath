package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.PuzzleAssignment;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes multi-dimensional cognitive difficulty metrics and canonical SHA-256 fingerprint
 * for multiplayer competitive puzzles.
 *
 * Implements Prompt Section 3:
 * - Branching decisions vs forced moves
 * - Checkpoint placement & dependencies (unguided horizon)
 * - Wall and blocked-edge topology
 * - Deceptive checkpoint pairs (Manhattan <= 2, route distance >= 6)
 * - Trap and region-isolation risk
 * - Naive heuristic resistance (Greedy Manhattan & Row Snake)
 * - Deterministic canonical fingerprint
 */
public class CognitiveDifficultyEvaluator {

    public record CognitiveEvaluationResult(
            double cognitiveScore,
            CompetitiveDifficultyTier assignedTier,
            double branchingRatio,
            double forcedRatio,
            double avgCheckpointSpacing,
            double avgManhattanDistance,
            int deceptivePairs,
            double deceptionRatio,
            int wallCount,
            int checkpointCount,
            double heuristicResistance,
            long solverNodesExplored,
            String canonicalFingerprint
    ) {}

    public static String computeCanonicalFingerprint(
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges
    ) {
        String dims = rows + "x" + cols;

        // Sort cells canonically by row, then col
        List<String> sortedCells = new ArrayList<>(requiredCells);
        sortedCells.sort((a, b) -> {
            String[] pA = a.split(",");
            String[] pB = b.split(",");
            int rA = Integer.parseInt(pA[0].trim());
            int cA = Integer.parseInt(pA[1].trim());
            int rB = Integer.parseInt(pB[0].trim());
            int cB = Integer.parseInt(pB[1].trim());
            return (rA != rB) ? Integer.compare(rA, rB) : Integer.compare(cA, cB);
        });
        String cellsStr = String.join(";", sortedCells);

        // Sort checkpoints by number
        List<PuzzleAssignment.CheckpointSpec> sortedCps = new ArrayList<>(checkpoints);
        sortedCps.sort(Comparator.comparingInt(PuzzleAssignment.CheckpointSpec::number));
        StringBuilder cpsSb = new StringBuilder();
        for (int i = 0; i < sortedCps.size(); i++) {
            if (i > 0) cpsSb.append(";");
            PuzzleAssignment.CheckpointSpec cp = sortedCps.get(i);
            cpsSb.append(cp.number()).append("@").append(cp.row()).append(",").append(cp.col());
        }

        // Normalize and sort walls
        record NormWall(int r1, int c1, int r2, int c2) {}
        List<NormWall> normWalls = new ArrayList<>(blockedEdges.size());
        for (PuzzleAssignment.BlockedEdgeSpec w : blockedEdges) {
            int cmp = (w.row1() != w.row2()) ? Integer.compare(w.row1(), w.row2()) : Integer.compare(w.col1(), w.col2());
            if (cmp <= 0) {
                normWalls.add(new NormWall(w.row1(), w.col1(), w.row2(), w.col2()));
            } else {
                normWalls.add(new NormWall(w.row2(), w.col2(), w.row1(), w.col1()));
            }
        }
        normWalls.sort((a, b) -> {
            if (a.r1 != b.r1) return Integer.compare(a.r1, b.r1);
            if (a.c1 != b.c1) return Integer.compare(a.c1, b.c1);
            if (a.r2 != b.r2) return Integer.compare(a.r2, b.r2);
            return Integer.compare(a.c2, b.c2);
        });

        StringBuilder wallsSb = new StringBuilder();
        for (int i = 0; i < normWalls.size(); i++) {
            if (i > 0) wallsSb.append(";");
            NormWall nw = normWalls.get(i);
            wallsSb.append(nw.r1).append(",").append(nw.c1).append("-").append(nw.r2).append(",").append(nw.c2);
        }

        String canonical = "DIM:" + dims + "|CELLS:" + cellsStr + "|CP:" + cpsSb + "|WALLS:" + wallsSb;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 digest unavailable", e);
        }
    }

    public CognitiveEvaluationResult evaluate(
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
            List<String> solutionPath,
            AuthoritativePuzzleSolver.SolverResult solverResult,
            SimpleHeuristicEvaluator.SimpleEvaluationResult simpleResult
    ) {
        int totalCells = requiredCells.size();
        int cpCount = checkpoints.size();
        int wallCount = blockedEdges.size();

        // 1. Branching & Forced Moves
        Set<String> reqSet = new HashSet<>(requiredCells);
        record EdgeKey(int r1, int c1, int r2, int c2) {}
        Set<EdgeKey> wallSet = new HashSet<>(wallCount * 2);
        for (PuzzleAssignment.BlockedEdgeSpec w : blockedEdges) {
            int minR = Math.min(w.row1(), w.row2());
            int minC = (w.row1() == w.row2()) ? Math.min(w.col1(), w.col2()) : (w.row1() < w.row2() ? w.col1() : w.col2());
            int maxR = Math.max(w.row1(), w.row2());
            int maxC = (w.row1() == w.row2()) ? Math.max(w.col1(), w.col2()) : (w.row1() < w.row2() ? w.col2() : w.col1());
            wallSet.add(new EdgeKey(minR, minC, maxR, maxC));
        }

        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int branchCells = 0;
        int forcedCells = 0;

        for (String cell : requiredCells) {
            String[] parts = cell.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            int deg = 0;
            for (int[] d : deltas) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (reqSet.contains(nr + "," + nc)) {
                    int minR = Math.min(r, nr);
                    int minC = (r == nr) ? Math.min(c, nc) : (r < nr ? c : nc);
                    int maxR = Math.max(r, nr);
                    int maxC = (r == nr) ? Math.max(c, nc) : (r < nr ? nc : c);
                    if (!wallSet.contains(new EdgeKey(minR, minC, maxR, maxC))) {
                        deg++;
                    }
                }
            }
            if (deg >= 3) {
                branchCells++;
            } else if (deg <= 2) {
                forcedCells++;
            }
        }

        double branchingRatio = totalCells > 0 ? (double) branchCells / totalCells : 0.0;
        double forcedRatio = totalCells > 0 ? (double) forcedCells / totalCells : 0.0;

        // 2. Unguided Step Horizon (H)
        double avgSpacing = (cpCount > 1) ? (double) (totalCells - 1) / (cpCount - 1) : totalCells;
        double horizonScore = Math.min(1.0, Math.max(0.0, (avgSpacing - 3.0) / 12.0));

        // 3. Wall Constraint & Choke Depth (W)
        int targetWalls = Math.max(1, (int) (Math.sqrt(totalCells) * 1.5));
        double wallScore = Math.min(1.0, (double) wallCount / targetWalls);

        // 4. Deception & Cell Reservation (D)
        Map<String, Integer> pathToIdx = new HashMap<>(solutionPath.size() * 2);
        for (int i = 0; i < solutionPath.size(); i++) {
            pathToIdx.put(solutionPath.get(i), i);
        }

        List<PuzzleAssignment.CheckpointSpec> sortedCps = new ArrayList<>(checkpoints);
        sortedCps.sort(Comparator.comparingInt(PuzzleAssignment.CheckpointSpec::number));

        int deceptivePairs = 0;
        double sumManhattan = 0.0;
        int pairCount = sortedCps.size() - 1;

        for (int i = 0; i < pairCount; i++) {
            PuzzleAssignment.CheckpointSpec c1 = sortedCps.get(i);
            PuzzleAssignment.CheckpointSpec c2 = sortedCps.get(i + 1);
            int md = Math.abs(c1.row() - c2.row()) + Math.abs(c1.col() - c2.col());
            sumManhattan += md;

            Integer idx1 = pathToIdx.get(c1.row() + "," + c1.col());
            Integer idx2 = pathToIdx.get(c2.row() + "," + c2.col());
            if (idx1 != null && idx2 != null) {
                int routeGap = Math.abs(idx2 - idx1);
                if (md <= 2 && routeGap >= 6) {
                    deceptivePairs++;
                }
            }
        }

        double deceptionRatio = (pairCount > 0) ? (double) deceptivePairs / pairCount : 0.0;
        double avgManhattan = (pairCount > 0) ? sumManhattan / pairCount : 0.0;

        // 5. Trap / Region Isolation Risk (T)
        double trapRisk = branchingRatio * (1.0 - forcedRatio);

        // 6. Simple-Heuristic Resistance (R)
        double resistanceMultiplier = 1.00;
        if (simpleResult.solvedByAnySimple()) {
            if (simpleResult.greedyBacktracks() <= 1 || simpleResult.rowSnakeBacktracks() <= 1) {
                resistanceMultiplier = 0.50;
            } else {
                resistanceMultiplier = 0.70;
            }
        }

        // Composite Cognitive Score (0.0 to 1.0)
        double baseScore = (
                0.20 * ((double) totalCells / 144.0) +
                0.25 * horizonScore +
                0.20 * wallScore +
                0.20 * deceptionRatio +
                0.15 * trapRisk
        );

        double cognitiveScore = Math.max(0.05, Math.min(0.99, baseScore * resistanceMultiplier));
        cognitiveScore = Math.round(cognitiveScore * 10000.0) / 10000.0;

        // Tier Assignment
        CompetitiveDifficultyTier assignedTier;
        if (cognitiveScore >= 0.68) {
            assignedTier = CompetitiveDifficultyTier.MASTER;
        } else if (cognitiveScore >= 0.58) {
            assignedTier = CompetitiveDifficultyTier.EXPERT;
        } else {
            assignedTier = CompetitiveDifficultyTier.VERY_HARD;
        }

        String fingerprint = computeCanonicalFingerprint(rows, cols, requiredCells, checkpoints, blockedEdges);

        return new CognitiveEvaluationResult(
                cognitiveScore,
                assignedTier,
                Math.round(branchingRatio * 1000.0) / 1000.0,
                Math.round(forcedRatio * 1000.0) / 1000.0,
                Math.round(avgSpacing * 100.0) / 100.0,
                Math.round(avgManhattan * 100.0) / 100.0,
                deceptivePairs,
                Math.round(deceptionRatio * 1000.0) / 1000.0,
                wallCount,
                cpCount,
                resistanceMultiplier,
                solverResult.nodesExplored(),
                fingerprint
        );
    }
}
