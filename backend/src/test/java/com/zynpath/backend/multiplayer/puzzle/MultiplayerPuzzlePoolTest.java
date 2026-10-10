package com.zynpath.backend.multiplayer.puzzle;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MultiplayerPuzzlePoolTest {

    private MultiplayerPuzzlePool pool;

    @BeforeEach
    void setUp() {
        pool = new MultiplayerPuzzlePool();
    }

    @Test
    @DisplayName("Competitive pool contains ONLY 11x11 and 12x12 boards; 0 smaller boards eligible")
    void testCompetitivePoolHasStrictly11x11And12x12Boards() {
        for (MultiplayerPuzzlePool.VerifiedPuzzleEntry entry : pool.getAllCompetitivePuzzles()) {
            assertThat(entry.rows()).isIn(11, 12);
            assertThat(entry.cols()).isIn(11, 12);
            assertThat(entry.requiredCells()).hasSize(entry.rows() * entry.cols());
            assertThat(entry.canonicalSolution()).hasSize(entry.rows() * entry.cols());
        }

        // Section 32 & 33 & 50: Verify Quick Duel smaller grids eligible = 0
        long qdSmaller = pool.getQuickDuelPool().stream().filter(e -> e.rows() <= 10).count();
        assertThat(qdSmaller).as("Quick Duel smaller grids (<=10x10) eligible count must be 0").isEqualTo(0);

        // Verify Friends Arena smaller grids eligible = 0
        long faSmaller = pool.getFriendsArenaPool().stream().filter(e -> e.rows() <= 10).count();
        assertThat(faSmaller).as("Friends Arena smaller grids (<=10x10) eligible count must be 0").isEqualTo(0);
    }

    @Test
    @DisplayName("Every competitive puzzle solution visits all cells continuously and checkpoints in order")
    void testEverySolutionIsContinuousAndVisitsCheckpointsInOrder() {
        for (MultiplayerPuzzlePool.VerifiedPuzzleEntry entry : pool.getAllCompetitivePuzzles()) {
            List<String> sol = entry.canonicalSolution();
            assertThat(new HashSet<>(sol)).hasSize(entry.rows() * entry.cols());

            // Check continuous adjacency
            for (int i = 0; i < sol.size() - 1; i++) {
                String[] p1 = sol.get(i).split(",");
                String[] p2 = sol.get(i + 1).split(",");
                int r1 = Integer.parseInt(p1[0]);
                int c1 = Integer.parseInt(p1[1]);
                int r2 = Integer.parseInt(p2[0]);
                int c2 = Integer.parseInt(p2[1]);
                int dist = Math.abs(r1 - r2) + Math.abs(c1 - c2);
                assertThat(dist).as("Consecutive steps must be orthogonally adjacent").isEqualTo(1);
            }

            // Checkpoints in order
            int lastCpIndex = -1;
            for (PuzzleAssignment.CheckpointSpec cp : entry.checkpoints()) {
                String cpPos = cp.row() + "," + cp.col();
                int idx = sol.indexOf(cpPos);
                assertThat(idx).as("Checkpoint " + cp.number() + " must be in solution").isGreaterThan(-1);
                assertThat(idx).as("Checkpoints must appear in ascending order").isGreaterThan(lastCpIndex);
                lastCpIndex = idx;
            }
        }
    }

    @Test
    @DisplayName("Opponents receiving same seed and mode receive IDENTICAL puzzle assignment")
    void testOpponentsReceiveIdenticalPuzzle() {
        long seed = 987654321L;
        PuzzleAssignment p1 = pool.selectPuzzleForMode(GameMode.QUICK_DUEL, seed);
        PuzzleAssignment p2 = pool.selectPuzzleForMode(GameMode.QUICK_DUEL, seed);

        assertThat(p1.puzzleId()).isEqualTo(p2.puzzleId());
        assertThat(p1.fingerprint()).isEqualTo(p2.fingerprint());
        assertThat(p1.gridRows()).isIn(11, 12);
        assertThat(p1.gridCols()).isIn(11, 12);
        assertThat(p1.checkpoints()).isEqualTo(p2.checkpoints());
        assertThat(p1.blockedEdges()).isEqualTo(p2.blockedEdges());
    }

    @Test
    @DisplayName("Friends Arena participants receive identical puzzle from dedicated advanced pool (11x11 or 12x12)")
    void testFriendsArenaReceivesIdenticalAdvancedPuzzle() {
        long seed = 123456789L;
        PuzzleAssignment fa1 = pool.selectPuzzleForMode(GameMode.FRIENDS_ARENA, seed);
        PuzzleAssignment fa2 = pool.selectPuzzleForMode(GameMode.FRIENDS_ARENA, seed);

        assertThat(fa1.puzzleId()).isEqualTo(fa2.puzzleId());
        assertThat(fa1.gridRows()).isIn(11, 12);

        // Verify it belongs to Friends Arena pool
        List<String> faIds = pool.getFriendsArenaPool().stream()
                .map(MultiplayerPuzzlePool.VerifiedPuzzleEntry::puzzleId)
                .toList();
        assertThat(faIds).contains(fa1.puzzleId());
    }

    @Test
    @DisplayName("Difficulty brackets route between 11x11 (Bracket 1) and 12x12 (Bracket 2)")
    void testDifficultyBrackets() {
        long seed = 42L;
        PuzzleAssignment b1 = pool.selectCompetitivePuzzle(GameMode.QUICK_DUEL, 1, seed, null);
        List<String> b1Ids = pool.getBracket1Pool().stream()
                .map(MultiplayerPuzzlePool.VerifiedPuzzleEntry::puzzleId)
                .toList();
        assertThat(b1Ids).contains(b1.puzzleId());
        assertThat(b1.gridRows()).isEqualTo(11);

        PuzzleAssignment b2 = pool.selectCompetitivePuzzle(GameMode.QUICK_DUEL, 2, seed, null);
        List<String> b2Ids = pool.getBracket2Pool().stream()
                .map(MultiplayerPuzzlePool.VerifiedPuzzleEntry::puzzleId)
                .toList();
        assertThat(b2Ids).contains(b2.puzzleId());
        assertThat(b2.gridRows()).isEqualTo(12);
    }

    @Test
    @DisplayName("Excluding previous puzzle ID ensures rematch selects an alternate puzzle")
    void testExclusionAvoidsRepetition() {
        long seed = 100L;
        PuzzleAssignment initial = pool.selectPuzzleForMode(GameMode.QUICK_DUEL, seed);
        PuzzleAssignment rematch = pool.selectPuzzleForModeExcluding(GameMode.QUICK_DUEL, seed, initial.puzzleId());

        assertThat(rematch.puzzleId()).isNotEqualTo(initial.puzzleId());
        assertThat(rematch.gridRows()).isIn(11, 12);
    }
}
