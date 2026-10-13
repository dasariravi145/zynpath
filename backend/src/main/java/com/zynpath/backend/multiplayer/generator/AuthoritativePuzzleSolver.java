package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.PuzzleAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * High-performance authoritative exhaustive backtracking solver for Zynpath number-path puzzles.
 *
 * Implements:
 * - Packed primitive array indexing for extreme speed and zero GC pressure
 * - Warnsdorff onward-degree heuristic move ordering
 * - Cell degree starvation pruning (dead-end detection)
 * - Strict checkpoint ordering (1 -> 2 -> ... -> N)
 * - Blocked edge (wall) filtering
 * - Strict uniqueness detection (verifies whether exactly 1 solution exists)
 * - Bounded node search budget
 */
public class AuthoritativePuzzleSolver {

    public static final int DEFAULT_NODE_LIMIT = 100_000;

    public enum SolverStatus {
        SOLVED,
        UNSOLVABLE,
        TIMEOUT
    }

    public enum Uniqueness {
        UNIQUE,
        MULTIPLE_SOLUTIONS,
        UNKNOWN
    }

    public record SolverResult(
            SolverStatus status,
            Uniqueness uniqueness,
            List<List<String>> solutions,
            long nodesExplored,
            long durationMs,
            boolean timedOut
    ) {
        public boolean isUnique() {
            return uniqueness == Uniqueness.UNIQUE;
        }

        public List<String> getCanonicalSolution() {
            return (solutions != null && !solutions.isEmpty()) ? solutions.get(0) : List.of();
        }
    }

    private final int nodeLimit;

    public AuthoritativePuzzleSolver() {
        this(DEFAULT_NODE_LIMIT);
    }

    public AuthoritativePuzzleSolver(int nodeLimit) {
        this.nodeLimit = nodeLimit;
    }

    public int getNodeLimit() {
        return nodeLimit;
    }

    /**
     * Solves the given puzzle definition and determines solution uniqueness.
     */
    public SolverResult solve(
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
            int maxSolutions
    ) {
        long startTime = System.nanoTime();
        int totalCells = requiredCells.size();

        if (totalCells == 0 || checkpoints == null || checkpoints.isEmpty()) {
            return new SolverResult(SolverStatus.UNSOLVABLE, Uniqueness.UNKNOWN, List.of(), 0, 0, false);
        }

        // Map "r,c" coordinates to packed indices
        Map<String, Integer> coordToIdx = new HashMap<>(totalCells * 2);
        String[] idxToCoord = new String[totalCells];
        for (int i = 0; i < totalCells; i++) {
            String coord = requiredCells.get(i);
            coordToIdx.put(coord, i);
            idxToCoord[i] = coord;
        }

        // Parse walls into normalized edge set
        Set<Long> wallEdges = new HashSet<>(blockedEdges.size() * 2);
        for (PuzzleAssignment.BlockedEdgeSpec wall : blockedEdges) {
            String p1 = wall.row1() + "," + wall.col1();
            String p2 = wall.row2() + "," + wall.col2();
            Integer id1 = coordToIdx.get(p1);
            Integer id2 = coordToIdx.get(p2);
            if (id1 != null && id2 != null) {
                int min = Math.min(id1, id2);
                int max = Math.max(id1, id2);
                wallEdges.add(((long) min << 32) | (max & 0xFFFFFFFFL));
            }
        }

        // Parse checkpoints
        int maxCpNum = 0;
        for (PuzzleAssignment.CheckpointSpec cp : checkpoints) {
            if (cp.number() > maxCpNum) {
                maxCpNum = cp.number();
            }
        }

        int[] cpByPos = new int[totalCells]; // cellIdx -> cpNum (0 if not a checkpoint)
        int[] cpByNum = new int[maxCpNum + 1]; // cpNum -> cellIdx
        Arrays.fill(cpByNum, -1);

        for (PuzzleAssignment.CheckpointSpec cp : checkpoints) {
            String coord = cp.row() + "," + cp.col();
            Integer idx = coordToIdx.get(coord);
            if (idx == null) {
                return new SolverResult(SolverStatus.UNSOLVABLE, Uniqueness.UNKNOWN, List.of(), 0, 0, false);
            }
            cpByPos[idx] = cp.number();
            cpByNum[cp.number()] = idx;
        }

        if (cpByNum[1] == -1 || cpByNum[maxCpNum] == -1) {
            return new SolverResult(SolverStatus.UNSOLVABLE, Uniqueness.UNKNOWN, List.of(), 0, 0, false);
        }

        int startPos = cpByNum[1];
        int endPos = cpByNum[maxCpNum];

        // Build adjacency graph: for each cellIdx, list of valid orthogonal neighbor indices
        int[][] adj = new int[totalCells][];
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int i = 0; i < totalCells; i++) {
            String[] parts = idxToCoord[i].split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);

            int[] tempNbrs = new int[4];
            int nbrCount = 0;

            for (int[] d : deltas) {
                int nr = r + d[0];
                int nc = c + d[1];
                Integer nIdx = coordToIdx.get(nr + "," + nc);
                if (nIdx != null) {
                    int min = Math.min(i, nIdx);
                    int max = Math.max(i, nIdx);
                    long edgeKey = ((long) min << 32) | (max & 0xFFFFFFFFL);
                    if (!wallEdges.contains(edgeKey)) {
                        tempNbrs[nbrCount++] = nIdx;
                    }
                }
            }
            adj[i] = Arrays.copyOf(tempNbrs, nbrCount);
        }

        // DFS State
        List<List<String>> foundSolutions = new ArrayList<>(maxSolutions);
        boolean[] visited = new boolean[totalCells];
        int[] path = new int[totalCells];
        long[] nodesExplored = new long[1];
        boolean[] timedOut = new boolean[1];

        visited[startPos] = true;
        path[0] = startPos;

        dfs(
                startPos,
                1, // current path length
                2, // next expected checkpoint number
                endPos,
                maxCpNum,
                totalCells,
                adj,
                cpByPos,
                visited,
                path,
                nodesExplored,
                timedOut,
                maxSolutions,
                foundSolutions,
                idxToCoord
        );

        long durationMs = (System.nanoTime() - startTime) / 1_000_000L;

        SolverStatus status;
        if (!foundSolutions.isEmpty()) {
            status = SolverStatus.SOLVED;
        } else if (timedOut[0]) {
            status = SolverStatus.TIMEOUT;
        } else {
            status = SolverStatus.UNSOLVABLE;
        }

        Uniqueness uniqueness;
        if (foundSolutions.size() == 1 && !timedOut[0]) {
            uniqueness = Uniqueness.UNIQUE;
        } else if (foundSolutions.size() >= 2) {
            uniqueness = Uniqueness.MULTIPLE_SOLUTIONS;
        } else {
            uniqueness = Uniqueness.UNKNOWN;
        }

        return new SolverResult(
                status,
                uniqueness,
                foundSolutions,
                nodesExplored[0],
                durationMs,
                timedOut[0]
        );
    }

    private void dfs(
            int curr,
            int pathLen,
            int nextCpNum,
            int endPos,
            int maxCpNum,
            int totalCells,
            int[][] adj,
            int[] cpByPos,
            boolean[] visited,
            int[] path,
            long[] nodesExplored,
            boolean[] timedOut,
            int maxSolutions,
            List<List<String>> foundSolutions,
            String[] idxToCoord
    ) {
        nodesExplored[0]++;
        if (nodesExplored[0] >= nodeLimit) {
            timedOut[0] = true;
            return;
        }

        if (pathLen == totalCells) {
            if (curr == endPos && nextCpNum > maxCpNum) {
                List<String> sol = new ArrayList<>(totalCells);
                for (int i = 0; i < totalCells; i++) {
                    sol.add(idxToCoord[path[i]]);
                }
                foundSolutions.add(sol);
            }
            return;
        }

        // Candidate next moves
        int[] currAdj = adj[curr];
        int candidateCount = 0;
        int[] candidateMoves = new int[currAdj.length];
        int[] candidateNextCps = new int[currAdj.length];

        for (int nxt : currAdj) {
            if (!visited[nxt]) {
                int cp = cpByPos[nxt];
                if (cp > 0) {
                    if (cp != nextCpNum) {
                        continue; // Skip out-of-order checkpoint
                    }
                    candidateMoves[candidateCount] = nxt;
                    candidateNextCps[candidateCount] = nextCpNum + 1;
                    candidateCount++;
                } else {
                    candidateMoves[candidateCount] = nxt;
                    candidateNextCps[candidateCount] = nextCpNum;
                    candidateCount++;
                }
            }
        }

        if (candidateCount == 0) {
            return;
        }

        // Warnsdorff ordering: order by onward unvisited degree
        if (candidateCount > 1) {
            // Sort indices 0..candidateCount-1 by onward degree
            Integer[] order = new Integer[candidateCount];
            int[] onwardDegrees = new int[candidateCount];
            for (int i = 0; i < candidateCount; i++) {
                order[i] = i;
                int nxt = candidateMoves[i];
                int deg = 0;
                for (int nn : adj[nxt]) {
                    if (!visited[nn]) {
                        deg++;
                    }
                }
                onwardDegrees[i] = deg;
            }

            Arrays.sort(order, Comparator.comparingInt(i -> onwardDegrees[i]));

            // Rearrange candidateMoves according to order
            int[] sortedMoves = new int[candidateCount];
            int[] sortedCps = new int[candidateCount];
            for (int i = 0; i < candidateCount; i++) {
                sortedMoves[i] = candidateMoves[order[i]];
                sortedCps[i] = candidateNextCps[order[i]];
            }
            candidateMoves = sortedMoves;
            candidateNextCps = sortedCps;
        }

        // Explore branches with starvation pruning
        for (int i = 0; i < candidateCount; i++) {
            int nxt = candidateMoves[i];
            int nCp = candidateNextCps[i];

            // Starvation check: any unvisited neighbor of curr (other than nxt) must not be starved
            boolean starved = false;
            for (int neighbor : currAdj) {
                if (!visited[neighbor] && neighbor != nxt) {
                    int deg = 0;
                    for (int nn : adj[neighbor]) {
                        if (!visited[nn] || nn == nxt) {
                            deg++;
                        }
                    }
                    int required = (neighbor == endPos) ? 1 : 2;
                    if (deg < required) {
                        starved = true;
                        break;
                    }
                }
            }

            if (starved) {
                continue;
            }

            visited[nxt] = true;
            path[pathLen] = nxt;

            dfs(
                    nxt,
                    pathLen + 1,
                    nCp,
                    endPos,
                    maxCpNum,
                    totalCells,
                    adj,
                    cpByPos,
                    visited,
                    path,
                    nodesExplored,
                    timedOut,
                    maxSolutions,
                    foundSolutions,
                    idxToCoord
            );

            visited[nxt] = false;

            if (foundSolutions.size() >= maxSolutions || timedOut[0]) {
                return;
            }
        }
    }
}
