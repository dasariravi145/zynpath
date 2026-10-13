package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Production procedural puzzle generator for Zynpath competitive multiplayer.
 *
 * Implements Prompt Sections 1, 3, 4, 6:
 * - Deterministic seed-based procedural generation
 * - Hierarchical regional decomposition with 1-cell choke doorways for 10x10, 11x11, 12x12 grids
 * - Ergodic subgrid Hamiltonian paths with bipartite parity enforcement & starvation pruning
 * - Strategic checkpoint placement with deceptive pair induction
 * - Macro boundary wall assembly and counter-example guided refinement (CEGAR)
 * - Authoritative solver single-solution uniqueness verification
 * - Multi-dimensional cognitive difficulty model (VERY_HARD, EXPERT, MASTER)
 * - Canonical SHA-256 fingerprinting and repeat protection
 * - Bounded CPU and search budget limits
 */
@Component
public class ProceduralPuzzleGenerator {

    private static final Logger log = LoggerFactory.getLogger(ProceduralPuzzleGenerator.class);

    public static final int GENERATOR_VERSION = 1;
    public static final int DEFAULT_MAX_CANDIDATE_ATTEMPTS = 20;
    public static final int DEFAULT_MAX_REFINEMENT_STEPS = 16;
    public static final int SOLVER_NODE_BUDGET = 100_000;
    public static final int SUB_SOLVER_NODE_BUDGET = 35_000;

    private final AuthoritativePuzzleSolver solver;
    private final AuthoritativePuzzleSolver subSolver;
    private final SimpleHeuristicEvaluator heuristicEvaluator;
    private final CognitiveDifficultyEvaluator difficultyEvaluator;

    public ProceduralPuzzleGenerator() {
        this.solver = new AuthoritativePuzzleSolver(SOLVER_NODE_BUDGET);
        this.subSolver = new AuthoritativePuzzleSolver(SUB_SOLVER_NODE_BUDGET);
        this.heuristicEvaluator = new SimpleHeuristicEvaluator();
        this.difficultyEvaluator = new CognitiveDifficultyEvaluator();
    }

    public ProceduralPuzzleGenerator(
            AuthoritativePuzzleSolver solver,
            AuthoritativePuzzleSolver subSolver,
            SimpleHeuristicEvaluator heuristicEvaluator,
            CognitiveDifficultyEvaluator difficultyEvaluator
    ) {
        this.solver = solver;
        this.subSolver = subSolver;
        this.heuristicEvaluator = heuristicEvaluator;
        this.difficultyEvaluator = difficultyEvaluator;
    }

    public record CandidateOutcome(
            boolean accepted,
            String rejectionReason,
            GeneratedPuzzle puzzle,
            long durationMs,
            long nodesExplored,
            boolean timedOut,
            int refinementSteps
    ) {}

    /**
     * Generates a verified competitive puzzle for the given game mode and difficulty tier.
     * Retries deterministically up to maxCandidateAttempts.
     */
    public GeneratedPuzzle generatePuzzle(GameMode mode, CompetitiveDifficultyTier tier, long baseSeed) {
        long startTime = System.currentTimeMillis();

        for (int attempt = 0; attempt < DEFAULT_MAX_CANDIDATE_ATTEMPTS; attempt++) {
            long attemptSeed = baseSeed + (long) attempt * 7919L;
            CandidateOutcome outcome = generateSingleCandidate(mode, tier, attemptSeed, attempt);
            if (outcome.accepted() && outcome.puzzle() != null) {
                log.info("Successfully generated puzzle id={}, tier={}, mode={}, attempts={}, durationMs={}",
                        outcome.puzzle().puzzleId(), tier, mode, attempt + 1, System.currentTimeMillis() - startTime);
                return outcome.puzzle();
            }
        }

        throw new IllegalStateException("Failed to generate solver-verified puzzle for tier " + tier +
                " and mode " + mode + " within " + DEFAULT_MAX_CANDIDATE_ATTEMPTS + " attempts");
    }

    /**
     * Generates a single candidate attempt. Useful for benchmark profiling.
     */
    public CandidateOutcome generateSingleCandidate(
            GameMode mode,
            CompetitiveDifficultyTier tier,
            long seed,
            int attemptIndex
    ) {
        long startNanos = System.nanoTime();
        Random rng = new Random(seed);

        // 1. Determine grid dimensions and quadrant splits
        int rows;
        int cols;
        int rTop;
        int cLeft;

        switch (tier) {
            case VERY_HARD -> {
                boolean is10 = rng.nextBoolean();
                if (is10) {
                    rows = 10;
                    cols = 10;
                    rTop = 5;
                    cLeft = 5;
                } else {
                    rows = 11;
                    cols = 11;
                    rTop = 6;
                    cLeft = 6;
                }
            }
            case EXPERT -> {
                boolean is11 = rng.nextBoolean();
                if (is11) {
                    rows = 11;
                    cols = 11;
                    rTop = 6;
                    cLeft = 6;
                } else {
                    rows = 12;
                    cols = 12;
                    rTop = 6;
                    cLeft = 6;
                }
            }
            case MASTER -> {
                rows = 12;
                cols = 12;
                rTop = 6;
                cLeft = 6;
            }
            default -> {
                rows = 11;
                cols = 11;
                rTop = 6;
                cLeft = 6;
            }
        }

        int totalCells = rows * cols;
        List<String> requiredCells = new ArrayList<>(totalCells);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                requiredCells.add(r + "," + c);
            }
        }

        // 2. Select quadrant doorway endpoints and build subgrid paths
        Doorways doorways = null;
        List<Point> p1 = null, p2 = null, p3 = null, p4 = null;
        SubgridResult r1 = null, r2 = null, r3 = null, r4 = null;

        for (int doorwayTry = 0; doorwayTry < 12; doorwayTry++) {
            doorways = selectDoorways(rows, cols, rTop, cLeft, rng);
            if (doorways == null) continue;

            p1 = findHamiltonianInSubgrid(0, rTop, 0, cLeft, doorways.s1, doorways.e1, rng);
            if (p1 == null) continue;
            p2 = findHamiltonianInSubgrid(0, rTop, cLeft, cols, doorways.s2, doorways.e2, rng);
            if (p2 == null) continue;
            p3 = findHamiltonianInSubgrid(rTop, rows, cLeft, cols, doorways.s3, doorways.e3, rng);
            if (p3 == null) continue;
            p4 = findHamiltonianInSubgrid(rTop, rows, 0, cLeft, doorways.s4, doorways.e4, rng);
            if (p4 == null) continue;

            r1 = refineSubgridUniqueness(p1, rng);
            r2 = refineSubgridUniqueness(p2, rng);
            r3 = refineSubgridUniqueness(p3, rng);
            r4 = refineSubgridUniqueness(p4, rng);

            if (r1.success && r2.success && r3.success && r4.success) {
                break;
            }
        }

        if (p1 == null || p2 == null || p3 == null || p4 == null ||
            r1 == null || r2 == null || r3 == null || r4 == null ||
            !r1.success || !r2.success || !r3.success || !r4.success) {
            return new CandidateOutcome(false, "SUBGRID_DECOMPOSITION_FAILED", null,
                    (System.nanoTime() - startNanos) / 1_000_000L, 0, false, 0);
        }

        // Assemble full path
        List<Point> fullPath = new ArrayList<>(totalCells);
        fullPath.addAll(p1);
        fullPath.addAll(p2);
        fullPath.addAll(p3);
        fullPath.addAll(p4);

        List<String> pathCoords = new ArrayList<>(totalCells);
        for (Point p : fullPath) {
            pathCoords.add(p.r + "," + p.c);
        }

        Set<Edge> truePathEdges = new HashSet<>(totalCells * 2);
        for (int i = 0; i < totalCells - 1; i++) {
            truePathEdges.add(Edge.normalized(fullPath.get(i), fullPath.get(i + 1)));
        }

        // Assemble initial walls
        List<PuzzleAssignment.BlockedEdgeSpec> walls = new ArrayList<>();
        Set<Edge> wallSet = new HashSet<>();

        // Add subgrid internal walls
        for (SubgridResult sr : List.of(r1, r2, r3, r4)) {
            for (PuzzleAssignment.BlockedEdgeSpec w : sr.walls) {
                Edge e = Edge.normalized(new Point(w.row1(), w.col1()), new Point(w.row2(), w.col2()));
                if (!truePathEdges.contains(e) && wallSet.add(e)) {
                    walls.add(w);
                }
            }
        }

        // Macro boundary walls
        // Q1 <-> Q2 boundary
        for (int r = 0; r < rTop; r++) {
            if (r != doorways.e1.r) {
                addWallIfValid(r, cLeft - 1, r, cLeft, truePathEdges, wallSet, walls);
            }
        }

        // Q2 <-> Q3 boundary
        for (int c = cLeft; c < cols; c++) {
            if (c != doorways.e2.c) {
                addWallIfValid(rTop - 1, c, rTop, c, truePathEdges, wallSet, walls);
            }
        }

        // Q3 <-> Q4 boundary
        for (int r = rTop; r < rows; r++) {
            if (r != doorways.e3.r) {
                addWallIfValid(r, cLeft - 1, r, cLeft, truePathEdges, wallSet, walls);
            }
        }

        // Q4 <-> Q1 boundary (fully blocked)
        for (int c = 0; c < cLeft; c++) {
            addWallIfValid(rTop - 1, c, rTop, c, truePathEdges, wallSet, walls);
        }

        // 3. Strategic Checkpoint Selection
        int targetCheckpoints = switch (tier) {
            case VERY_HARD -> 16 + rng.nextInt(5);
            case EXPERT -> 14 + rng.nextInt(5);
            case MASTER -> 12 + rng.nextInt(4);
        };
        int targetDeceptions = switch (tier) {
            case VERY_HARD -> 1 + rng.nextInt(2);
            case EXPERT -> 2 + rng.nextInt(2);
            case MASTER -> 3 + rng.nextInt(2);
        };

        List<PuzzleAssignment.CheckpointSpec> checkpoints = selectCheckpoints(
                fullPath,
                targetCheckpoints,
                targetDeceptions,
                rng
        );

        // 4. Global Counter-Example Guided Refinement (CEGAR)
        int refinementStep = 0;
        boolean uniqueFound = false;
        long totalNodesExplored = 0;
        boolean solverTimedOut = false;
        AuthoritativePuzzleSolver.SolverResult lastSolverResult = null;

        for (refinementStep = 0; refinementStep < DEFAULT_MAX_REFINEMENT_STEPS; refinementStep++) {
            AuthoritativePuzzleSolver.SolverResult solveRes = solver.solve(
                    rows,
                    cols,
                    requiredCells,
                    checkpoints,
                    walls,
                    2
            );
            lastSolverResult = solveRes;
            totalNodesExplored += solveRes.nodesExplored();

            if (solveRes.timedOut()) {
                solverTimedOut = true;
                break;
            }

            if (solveRes.uniqueness() == AuthoritativePuzzleSolver.Uniqueness.UNIQUE) {
                uniqueFound = true;
                break;
            }

            if (solveRes.solutions().size() < 2) {
                break;
            }

            List<String> altSol = solveRes.solutions().get(1);
            if (altSol.equals(pathCoords)) {
                altSol = solveRes.solutions().get(0);
            }

            boolean wallAdded = false;
            for (int i = 0; i < altSol.size() - 1; i++) {
                Point pA = Point.parse(altSol.get(i));
                Point pB = Point.parse(altSol.get(i + 1));
                Edge e = Edge.normalized(pA, pB);
                if (!truePathEdges.contains(e) && !wallSet.contains(e)) {
                    walls.add(new PuzzleAssignment.BlockedEdgeSpec(pA.r, pA.c, pB.r, pB.c));
                    wallSet.add(e);
                    wallAdded = true;
                    break;
                }
            }

            if (!wallAdded) {
                boolean cpAdded = false;
                for (int i = 0; i < totalCells; i++) {
                    if (!altSol.get(i).equals(pathCoords.get(i))) {
                        Point divPoint = Point.parse(pathCoords.get(i));
                        boolean alreadyCp = checkpoints.stream()
                                .anyMatch(c -> c.row() == divPoint.r && c.col() == divPoint.c);
                        if (!alreadyCp) {
                            checkpoints = insertCheckpointAt(checkpoints, divPoint, fullPath);
                            cpAdded = true;
                            break;
                        }
                    }
                }
                if (!cpAdded) {
                    break;
                }
            }
        }

        long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

        if (solverTimedOut) {
            return new CandidateOutcome(false, "SOLVER_TIMEOUT", null, durationMs, totalNodesExplored, true, refinementStep);
        }

        if (!uniqueFound || lastSolverResult == null || lastSolverResult.uniqueness() != AuthoritativePuzzleSolver.Uniqueness.UNIQUE) {
            return new CandidateOutcome(false, "NON_UNIQUE_OR_UNSOLVED", null, durationMs, totalNodesExplored, false, refinementStep);
        }

        // 5. Evaluate Cognitive Difficulty & Heuristics
        SimpleHeuristicEvaluator.SimpleEvaluationResult simpleRes = heuristicEvaluator.evaluate(
                rows,
                cols,
                requiredCells,
                checkpoints,
                walls
        );

        CognitiveDifficultyEvaluator.CognitiveEvaluationResult cogRes = difficultyEvaluator.evaluate(
                rows,
                cols,
                requiredCells,
                checkpoints,
                walls,
                pathCoords,
                lastSolverResult,
                simpleRes
        );

        // Verify tier qualification
        if (cogRes.cognitiveScore() < tier.getMinCognitiveScore()) {
            return new CandidateOutcome(
                    false,
                    "INSUFFICIENT_DIFFICULTY: Score " + cogRes.cognitiveScore() + " < min " + tier.getMinCognitiveScore(),
                    null,
                    durationMs,
                    totalNodesExplored,
                    false,
                    refinementStep
            );
        }

        String modeTag = (mode == GameMode.QUICK_DUEL) ? "qd" : "fa";
        String tierTag = tier.name().toLowerCase();
        String puzzleId = String.format("mp_dyn_%dx%d_%s_%s_%04d", rows, cols, modeTag, tierTag, Math.abs(seed % 10000));

        GeneratedPuzzle puzzle = new GeneratedPuzzle(
                puzzleId,
                GENERATOR_VERSION,
                cogRes.canonicalFingerprint(),
                tier,
                mode,
                rows,
                cols,
                requiredCells,
                checkpoints,
                walls,
                pathCoords,
                cogRes.cognitiveScore(),
                cogRes,
                durationMs,
                seed
        );

        return new CandidateOutcome(true, null, puzzle, durationMs, totalNodesExplored, false, refinementStep);
    }

    private void addWallIfValid(int r1, int c1, int r2, int c2, Set<Edge> pathEdges, Set<Edge> wallSet, List<PuzzleAssignment.BlockedEdgeSpec> walls) {
        Edge e = Edge.normalized(new Point(r1, c1), new Point(r2, c2));
        if (!pathEdges.contains(e) && wallSet.add(e)) {
            walls.add(new PuzzleAssignment.BlockedEdgeSpec(r1, c1, r2, c2));
        }
    }

    record Doorways(Point s1, Point e1, Point s2, Point e2, Point s3, Point e3, Point s4, Point e4) {}

    private Doorways selectDoorways(int rows, int cols, int rTop, int cLeft, Random rng) {
        if (rows == 10 && cols == 10) {
            int rExitQ1 = 2 * rng.nextInt(3); // 0, 2, 4
            Point e1 = new Point(rExitQ1, 4);
            List<Point> q1Starts = new ArrayList<>();
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    if ((r + c) % 2 == 0 && (r != e1.r || c != e1.c)) {
                        q1Starts.add(new Point(r, c));
                    }
                }
            }
            Point s1 = q1Starts.get(rng.nextInt(q1Starts.size()));
            Point s2 = new Point(rExitQ1, 5);

            int cExitQ2 = 5 + 2 * rng.nextInt(3); // 5, 7, 9
            Point e2 = new Point(4, cExitQ2);
            Point s3 = new Point(5, cExitQ2);

            int rExitQ3 = 5 + 2 * rng.nextInt(3); // 5, 7, 9
            Point e3 = new Point(rExitQ3, 5);
            Point s4 = new Point(rExitQ3, 4);

            List<Point> q4Ends = new ArrayList<>();
            for (int r = 5; r < 10; r++) {
                for (int c = 0; c < 5; c++) {
                    if ((r + c) % 2 == 1 && (r != s4.r || c != s4.c)) {
                        q4Ends.add(new Point(r, c));
                    }
                }
            }
            Point e4 = q4Ends.get(rng.nextInt(q4Ends.size()));
            return new Doorways(s1, e1, s2, e2, s3, e3, s4, e4);
        } else if (rows == 11 && cols == 11) {
            int rExitQ1 = 2 + 2 * rng.nextInt(2); // 2, 4
            Point e1 = new Point(rExitQ1, 5);
            List<Point> q1Starts = new ArrayList<>();
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    if ((r + c) % 2 == 0 && (r != e1.r || c != e1.c)) {
                        q1Starts.add(new Point(r, c));
                    }
                }
            }
            Point s1 = q1Starts.get(rng.nextInt(q1Starts.size()));
            Point s2 = new Point(rExitQ1, 6);

            int cExitQ2 = 6 + 2 * rng.nextInt(3); // 6, 8, 10
            Point e2 = new Point(5, cExitQ2);
            Point s3 = new Point(6, cExitQ2);

            int rExitQ3 = 6 + 2 * rng.nextInt(3); // 6, 8, 10
            Point e3 = new Point(rExitQ3, 6);
            Point s4 = new Point(rExitQ3, 5);

            List<Point> q4Ends = new ArrayList<>();
            for (int r = 6; r < 11; r++) {
                for (int c = 0; c < 6; c++) {
                    if ((r + c) % 2 == 0 && (r != s4.r || c != s4.c)) {
                        q4Ends.add(new Point(r, c));
                    }
                }
            }
            Point e4 = q4Ends.get(rng.nextInt(q4Ends.size()));
            return new Doorways(s1, e1, s2, e2, s3, e3, s4, e4);
        } else if (rows == 12 && cols == 12) {
            int rExitQ1 = 2 * rng.nextInt(3); // 0, 2, 4
            Point e1 = new Point(rExitQ1, 5);
            List<Point> q1Starts = new ArrayList<>();
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    if ((r + c) % 2 == 0 && (r != e1.r || c != e1.c)) {
                        q1Starts.add(new Point(r, c));
                    }
                }
            }
            Point s1 = q1Starts.get(rng.nextInt(q1Starts.size()));
            Point s2 = new Point(rExitQ1, 6);

            int cExitQ2 = 6 + 2 * rng.nextInt(3); // 6, 8, 10
            Point e2 = new Point(5, cExitQ2);
            Point s3 = new Point(6, cExitQ2);

            int rExitQ3 = 7 + 2 * rng.nextInt(3); // 7, 9, 11
            Point e3 = new Point(rExitQ3, 6);
            Point s4 = new Point(rExitQ3, 5);

            List<Point> q4Ends = new ArrayList<>();
            for (int r = 6; r < 12; r++) {
                for (int c = 0; c < 6; c++) {
                    if ((r + c) % 2 == 1 && (r != s4.r || c != s4.c)) {
                        q4Ends.add(new Point(r, c));
                    }
                }
            }
            Point e4 = q4Ends.get(rng.nextInt(q4Ends.size()));
            return new Doorways(s1, e1, s2, e2, s3, e3, s4, e4);
        }
        return null;
    }

    private List<Point> findHamiltonianInSubgrid(
            int rMin, int rMax, int cMin, int cMax, Point start, Point end, Random rng
    ) {
        int rCount = rMax - rMin;
        int cCount = cMax - cMin;
        int total = rCount * cCount;

        // Bipartite parity check
        if (total % 2 == 0) {
            if ((start.r + start.c) % 2 == (end.r + end.c) % 2) {
                return null;
            }
        } else {
            int evenCount = 0;
            for (int r = rMin; r < rMax; r++) {
                for (int c = cMin; c < cMax; c++) {
                    if ((r + c) % 2 == 0) evenCount++;
                }
            }
            int majParity = (evenCount > total - evenCount) ? 0 : 1;
            if ((start.r + start.c) % 2 != majParity || (end.r + end.c) % 2 != majParity) {
                return null;
            }
        }

        Set<Point> cellSet = new HashSet<>(total * 2);
        for (int r = rMin; r < rMax; r++) {
            for (int c = cMin; c < cMax; c++) {
                cellSet.add(new Point(r, c));
            }
        }

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        Set<Point> visited = new HashSet<>(total * 2);
        List<Point> path = new ArrayList<>(total);
        int[] nodes = new int[1];

        visited.add(start);
        path.add(start);

        if (dfsSubgrid(start, end, total, cellSet, visited, path, dirs, nodes, rng)) {
            return path;
        }
        return null;
    }

    private boolean dfsSubgrid(
            Point curr, Point end, int total, Set<Point> cellSet, Set<Point> visited,
            List<Point> path, int[][] dirs, int[] nodes, Random rng
    ) {
        nodes[0]++;
        if (nodes[0] > 15000) return false;
        if (path.size() == total) {
            return curr.equals(end);
        }

        List<Point> valid = new ArrayList<>(4);
        for (int[] d : dirs) {
            Point p = new Point(curr.r + d[0], curr.c + d[1]);
            if (cellSet.contains(p) && !visited.contains(p)) {
                if (path.size() < total - 1 && p.equals(end)) {
                    continue;
                }
                valid.add(p);
            }
        }

        if (valid.isEmpty()) return false;
        Collections.shuffle(valid, rng);

        // Warnsdorff ordering on subgrid
        valid.sort(Comparator.comparingInt(p -> {
            int deg = 0;
            for (int[] d : dirs) {
                Point np = new Point(p.r + d[0], p.c + d[1]);
                if (cellSet.contains(np) && !visited.contains(np)) deg++;
            }
            return deg;
        }));

        for (Point nxt : valid) {
            // Starvation check for neighbor cells
            boolean starved = false;
            for (int[] d : dirs) {
                Point neighbor = new Point(curr.r + d[0], curr.c + d[1]);
                if (cellSet.contains(neighbor) && !visited.contains(neighbor) && !neighbor.equals(nxt)) {
                    int deg = 0;
                    for (int[] nd : dirs) {
                        Point nn = new Point(neighbor.r + nd[0], neighbor.c + nd[1]);
                        if (cellSet.contains(nn) && (!visited.contains(nn) || nn.equals(nxt))) {
                            deg++;
                        }
                    }
                    int req = neighbor.equals(end) ? 1 : 2;
                    if (deg < req) {
                        starved = true;
                        break;
                    }
                }
            }

            if (starved) continue;

            visited.add(nxt);
            path.add(nxt);
            if (dfsSubgrid(nxt, end, total, cellSet, visited, path, dirs, nodes, rng)) {
                return true;
            }
            path.remove(path.size() - 1);
            visited.remove(nxt);
        }
        return false;
    }

    record SubgridResult(boolean success, List<PuzzleAssignment.BlockedEdgeSpec> walls) {}

    private SubgridResult refineSubgridUniqueness(List<Point> subPath, Random rng) {
        int total = subPath.size();
        Set<Edge> pathEdges = new HashSet<>(total * 2);
        for (int i = 0; i < total - 1; i++) {
            pathEdges.add(Edge.normalized(subPath.get(i), subPath.get(i + 1)));
        }

        List<String> subPathCoords = new ArrayList<>(total);
        for (Point p : subPath) subPathCoords.add(p.r + "," + p.c);

        List<PuzzleAssignment.CheckpointSpec> cps = new ArrayList<>();
        cps.add(new PuzzleAssignment.CheckpointSpec(1, subPath.get(0).r, subPath.get(0).c));
        cps.add(new PuzzleAssignment.CheckpointSpec(2, subPath.get(total / 2).r, subPath.get(total / 2).c));
        cps.add(new PuzzleAssignment.CheckpointSpec(3, subPath.get(total - 1).r, subPath.get(total - 1).c));

        List<PuzzleAssignment.BlockedEdgeSpec> walls = new ArrayList<>();
        Set<Edge> wallSet = new HashSet<>();

        for (int it = 0; it < 12; it++) {
            AuthoritativePuzzleSolver.SolverResult res = subSolver.solve(
                    30, 30, subPathCoords, cps, walls, 2
            );

            if (res.uniqueness() == AuthoritativePuzzleSolver.Uniqueness.UNIQUE) {
                return new SubgridResult(true, walls);
            }

            if (res.timedOut() || res.solutions().isEmpty()) {
                if (cps.size() < 5) {
                    for (Point p : subPath) {
                        boolean exists = cps.stream().anyMatch(c -> c.row() == p.r && c.col() == p.c);
                        if (!exists) {
                            cps = insertCheckpointAt(cps, p, subPath);
                            break;
                        }
                    }
                    continue;
                }
                return new SubgridResult(false, walls);
            }

            List<String> sol2 = res.solutions().get(1);
            if (sol2.equals(subPathCoords)) {
                sol2 = res.solutions().get(0);
            }

            boolean added = false;
            for (int i = 0; i < sol2.size() - 1; i++) {
                Point p1 = Point.parse(sol2.get(i));
                Point p2 = Point.parse(sol2.get(i + 1));
                Edge e = Edge.normalized(p1, p2);
                if (!pathEdges.contains(e) && wallSet.add(e)) {
                    walls.add(new PuzzleAssignment.BlockedEdgeSpec(p1.r, p1.c, p2.r, p2.c));
                    added = true;
                    break;
                }
            }

            if (!added && cps.size() < 5) {
                for (int i = 0; i < total; i++) {
                    if (!sol2.get(i).equals(subPathCoords.get(i))) {
                        Point div = subPath.get(i);
                        boolean exists = cps.stream().anyMatch(c -> c.row() == div.r && c.col() == div.c);
                        if (!exists) {
                            cps = insertCheckpointAt(cps, div, subPath);
                            break;
                        }
                    }
                }
            }
        }

        return new SubgridResult(false, walls);
    }

    private List<PuzzleAssignment.CheckpointSpec> selectCheckpoints(
            List<Point> path,
            int targetCount,
            int targetDeceptions,
            Random rng
    ) {
        int total = path.size();
        Set<Integer> chosenIndices = new HashSet<>();
        chosenIndices.add(0);
        chosenIndices.add(total - 1);

        record CandPair(int i, int j) {}
        List<CandPair> deceptiveCandidates = new ArrayList<>();
        for (int i = 1; i < total - 2; i++) {
            Point p1 = path.get(i);
            int maxJ = Math.min(total - 1, i + 35);
            for (int j = i + 6; j < maxJ; j++) {
                Point p2 = path.get(j);
                int md = Math.abs(p1.r - p2.r) + Math.abs(p1.c - p2.c);
                if (md <= 2) {
                    deceptiveCandidates.add(new CandPair(i, j));
                }
            }
        }

        Collections.shuffle(deceptiveCandidates, rng);
        int addedDeceptions = 0;
        for (CandPair cp : deceptiveCandidates) {
            if (addedDeceptions >= targetDeceptions) break;
            boolean iOk = chosenIndices.stream().allMatch(idx -> Math.abs(idx - cp.i) >= 3);
            boolean jOk = chosenIndices.stream().allMatch(idx -> Math.abs(idx - cp.j) >= 3);
            if (iOk && jOk && chosenIndices.size() + 2 <= targetCount) {
                chosenIndices.add(cp.i);
                chosenIndices.add(cp.j);
                addedDeceptions++;
            }
        }

        if (chosenIndices.size() < targetCount) {
            double step = (double) (total - 1) / (targetCount - 1);
            for (int k = 1; k < targetCount - 1; k++) {
                if (chosenIndices.size() >= targetCount) break;
                int ideal = (int) Math.round(k * step);
                int jitter = rng.nextInt(3) - 1;
                int cand = Math.max(1, Math.min(total - 2, ideal + jitter));
                boolean spaced = chosenIndices.stream().allMatch(idx -> Math.abs(idx - cand) >= 2);
                if (spaced) {
                    chosenIndices.add(cand);
                }
            }
        }

        List<Integer> sortedIndices = new ArrayList<>(chosenIndices);
        Collections.sort(sortedIndices);

        List<PuzzleAssignment.CheckpointSpec> result = new ArrayList<>(sortedIndices.size());
        for (int i = 0; i < sortedIndices.size(); i++) {
            Point p = path.get(sortedIndices.get(i));
            result.add(new PuzzleAssignment.CheckpointSpec(i + 1, p.r, p.c));
        }

        return result;
    }

    private List<PuzzleAssignment.CheckpointSpec> insertCheckpointAt(
            List<PuzzleAssignment.CheckpointSpec> current,
            Point newPoint,
            List<Point> fullPath
    ) {
        Map<Point, Integer> pathToIdx = new HashMap<>();
        for (int i = 0; i < fullPath.size(); i++) {
            pathToIdx.put(fullPath.get(i), i);
        }

        List<Point> allPoints = new ArrayList<>();
        for (PuzzleAssignment.CheckpointSpec cp : current) {
            allPoints.add(new Point(cp.row(), cp.col()));
        }
        allPoints.add(newPoint);

        allPoints.sort(Comparator.comparingInt(p -> pathToIdx.getOrDefault(p, 0)));

        List<PuzzleAssignment.CheckpointSpec> updated = new ArrayList<>(allPoints.size());
        for (int i = 0; i < allPoints.size(); i++) {
            Point p = allPoints.get(i);
            updated.add(new PuzzleAssignment.CheckpointSpec(i + 1, p.r, p.c));
        }

        return updated;
    }

    record Point(int r, int c) {
        static Point parse(String s) {
            String[] parts = s.split(",");
            return new Point(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        }
    }

    record Edge(Point p1, Point p2) {
        static Edge normalized(Point a, Point b) {
            int cmp = (a.r != b.r) ? Integer.compare(a.r, b.r) : Integer.compare(a.c, b.c);
            return (cmp <= 0) ? new Edge(a, b) : new Edge(b, a);
        }
    }
}
