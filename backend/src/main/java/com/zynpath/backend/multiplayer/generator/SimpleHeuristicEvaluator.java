package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.PuzzleAssignment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates puzzle resistance against naive human heuristics:
 * 1. Greedy Manhattan: Steps directly towards the next checkpoint.
 * 2. Row Snake: Sweeps rows horizontally in a snake pattern.
 *
 * If naive heuristics can solve the puzzle with minimal backtracking (<= 5 backtracks),
 * the puzzle lacks genuine cognitive depth and its difficulty is penalized.
 */
public class SimpleHeuristicEvaluator {

    public static final int DEFAULT_MAX_BACKTRACKS = 5;

    public record SimpleEvaluationResult(
            boolean solvedByAnySimple,
            String winningStrategy,
            boolean greedySolved,
            int greedyBacktracks,
            boolean rowSnakeSolved,
            int rowSnakeBacktracks
    ) {}

    private final int maxBacktracks;

    public SimpleHeuristicEvaluator() {
        this(DEFAULT_MAX_BACKTRACKS);
    }

    public SimpleHeuristicEvaluator(int maxBacktracks) {
        this.maxBacktracks = maxBacktracks;
    }

    public SimpleEvaluationResult evaluate(
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges
    ) {
        int totalCells = requiredCells.size();
        if (totalCells == 0 || checkpoints == null || checkpoints.isEmpty()) {
            return new SimpleEvaluationResult(false, null, false, 0, false, 0);
        }

        Map<String, Integer> coordToIdx = new HashMap<>(totalCells * 2);
        int[] cellRows = new int[totalCells];
        int[] cellCols = new int[totalCells];
        for (int i = 0; i < totalCells; i++) {
            String coord = requiredCells.get(i);
            coordToIdx.put(coord, i);
            String[] parts = coord.split(",");
            cellRows[i] = Integer.parseInt(parts[0]);
            cellCols[i] = Integer.parseInt(parts[1]);
        }

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

        int maxCpNum = 0;
        for (PuzzleAssignment.CheckpointSpec cp : checkpoints) {
            if (cp.number() > maxCpNum) {
                maxCpNum = cp.number();
            }
        }

        int[] cpByPos = new int[totalCells];
        int[] cpByNum = new int[maxCpNum + 1];
        Arrays.fill(cpByNum, -1);

        for (PuzzleAssignment.CheckpointSpec cp : checkpoints) {
            String coord = cp.row() + "," + cp.col();
            Integer idx = coordToIdx.get(coord);
            if (idx != null) {
                cpByPos[idx] = cp.number();
                cpByNum[cp.number()] = idx;
            }
        }

        if (cpByNum[1] == -1 || cpByNum[maxCpNum] == -1) {
            return new SimpleEvaluationResult(false, null, false, 0, false, 0);
        }

        int startPos = cpByNum[1];
        int endPos = cpByNum[maxCpNum];

        int[][] adj = new int[totalCells][];
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int i = 0; i < totalCells; i++) {
            int r = cellRows[i];
            int c = cellCols[i];
            int[] tempNbrs = new int[4];
            int nbrCount = 0;
            for (int[] d : deltas) {
                Integer nIdx = coordToIdx.get((r + d[0]) + "," + (c + d[1]));
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

        // 1. Run Greedy Manhattan
        int[] greedyBts = new int[1];
        boolean greedySolved = runGreedy(startPos, endPos, maxCpNum, totalCells, adj, cpByPos, cpByNum, cellRows, cellCols, greedyBts);

        // 2. Run Row Snake
        int[] snakeBts = new int[1];
        boolean snakeSolved = runRowSnake(startPos, endPos, maxCpNum, totalCells, adj, cpByPos, cellRows, cellCols, snakeBts);

        boolean solvedAny = greedySolved || snakeSolved;
        String winner = greedySolved ? "GREEDY_MANHATTAN" : (snakeSolved ? "ROW_SNAKE" : null);

        return new SimpleEvaluationResult(
                solvedAny,
                winner,
                greedySolved,
                greedyBts[0],
                snakeSolved,
                snakeBts[0]
        );
    }

    private boolean runGreedy(
            int startPos,
            int endPos,
            int maxCpNum,
            int totalCells,
            int[][] adj,
            int[] cpByPos,
            int[] cpByNum,
            int[] cellRows,
            int[] cellCols,
            int[] backtracks
    ) {
        boolean[] visited = new boolean[totalCells];
        visited[startPos] = true;
        return dfsGreedy(startPos, 1, 2, endPos, maxCpNum, totalCells, adj, cpByPos, cpByNum, cellRows, cellCols, visited, backtracks);
    }

    private boolean dfsGreedy(
            int curr,
            int pathLen,
            int nextCpNum,
            int endPos,
            int maxCpNum,
            int totalCells,
            int[][] adj,
            int[] cpByPos,
            int[] cpByNum,
            int[] cellRows,
            int[] cellCols,
            boolean[] visited,
            int[] backtracks
    ) {
        if (pathLen == totalCells) {
            return curr == endPos && nextCpNum > maxCpNum;
        }

        int targetCell = (nextCpNum <= maxCpNum && cpByNum[nextCpNum] != -1) ? cpByNum[nextCpNum] : endPos;
        int targetR = cellRows[targetCell];
        int targetC = cellCols[targetCell];

        // Filter valid neighbors
        int[] validNbrs = new int[adj[curr].length];
        int validCount = 0;
        for (int nxt : adj[curr]) {
            if (!visited[nxt]) {
                int cp = cpByPos[nxt];
                if (cp == 0 || cp == nextCpNum) {
                    validNbrs[validCount++] = nxt;
                }
            }
        }

        if (validCount == 0) {
            return false;
        }

        // Sort by Manhattan distance to target
        Integer[] order = new Integer[validCount];
        for (int i = 0; i < validCount; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> {
            int nxt = validNbrs[i];
            return Math.abs(cellRows[nxt] - targetR) + Math.abs(cellCols[nxt] - targetC);
        }));

        for (int i = 0; i < validCount; i++) {
            int nxt = validNbrs[order[i]];
            int cp = cpByPos[nxt];
            int nCp = (cp == nextCpNum) ? nextCpNum + 1 : nextCpNum;

            visited[nxt] = true;
            if (dfsGreedy(nxt, pathLen + 1, nCp, endPos, maxCpNum, totalCells, adj, cpByPos, cpByNum, cellRows, cellCols, visited, backtracks)) {
                return true;
            }
            visited[nxt] = false;
            backtracks[0]++;
            if (backtracks[0] > maxBacktracks) {
                return false;
            }
        }

        return false;
    }

    private boolean runRowSnake(
            int startPos,
            int endPos,
            int maxCpNum,
            int totalCells,
            int[][] adj,
            int[] cpByPos,
            int[] cellRows,
            int[] cellCols,
            int[] backtracks
    ) {
        boolean[] visited = new boolean[totalCells];
        visited[startPos] = true;
        return dfsRowSnake(startPos, 1, 2, endPos, maxCpNum, totalCells, adj, cpByPos, cellRows, cellCols, visited, backtracks);
    }

    private boolean dfsRowSnake(
            int curr,
            int pathLen,
            int nextCpNum,
            int endPos,
            int maxCpNum,
            int totalCells,
            int[][] adj,
            int[] cpByPos,
            int[] cellRows,
            int[] cellCols,
            boolean[] visited,
            int[] backtracks
    ) {
        if (pathLen == totalCells) {
            return curr == endPos && nextCpNum > maxCpNum;
        }

        int[] validNbrs = new int[adj[curr].length];
        int validCount = 0;
        for (int nxt : adj[curr]) {
            if (!visited[nxt]) {
                int cp = cpByPos[nxt];
                if (cp == 0 || cp == nextCpNum) {
                    validNbrs[validCount++] = nxt;
                }
            }
        }

        if (validCount == 0) {
            return false;
        }

        // Sort by row snake order: primary row, alternating column
        Integer[] order = new Integer[validCount];
        for (int i = 0; i < validCount; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> {
            int nxt = validNbrs[i];
            int r = cellRows[nxt];
            int c = cellCols[nxt];
            int colKey = (r % 2 == 0) ? c : (1000 - c);
            return r * 2000 + colKey;
        }));

        for (int i = 0; i < validCount; i++) {
            int nxt = validNbrs[order[i]];
            int cp = cpByPos[nxt];
            int nCp = (cp == nextCpNum) ? nextCpNum + 1 : nextCpNum;

            visited[nxt] = true;
            if (dfsRowSnake(nxt, pathLen + 1, nCp, endPos, maxCpNum, totalCells, adj, cpByPos, cellRows, cellCols, visited, backtracks)) {
                return true;
            }
            visited[nxt] = false;
            backtracks[0]++;
            if (backtracks[0] > maxBacktracks) {
                return false;
            }
        }

        return false;
    }
}
