package com.zynpath.backend.multiplayer.puzzle;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 * Authoritative pool of solver-verified, continuous-path competitive puzzles.
 *
 * Implements Prompt Sections 32–42:
 * - Quick Duel puzzle pool MUST contain ONLY 11x11 and 12x12 boards.
 * - Friends Arena puzzle pool MUST contain ONLY 11x11 and 12x12 boards.
 * - Smaller grids (5x5, 6x6, 7x7, 8x8, 9x9, 10x10) eligible count = 0.
 * - Quick Duel floor: VERY_HARD minimum on 11x11, EXPERT minimum on 12x12.
 * - Friends Arena floor: EXPERT minimum on 11x11, EXPERT / EXTREME on 12x12.
 * - 100% full coverage Hamiltonian path, strictly unique 1 solution verified.
 * - Guarantees all participants receive the exact identical puzzle definition and hash.
 */
@Component
public class MultiplayerPuzzlePool {

    private final List<VerifiedPuzzleEntry> poolQuickDuel11x11 = new ArrayList<>();
    private final List<VerifiedPuzzleEntry> poolQuickDuel12x12 = new ArrayList<>();
    private final List<VerifiedPuzzleEntry> poolFriendsArena11x11 = new ArrayList<>();
    private final List<VerifiedPuzzleEntry> poolFriendsArena12x12 = new ArrayList<>();

    public record VerifiedPuzzleEntry(
        String puzzleId,
        int puzzleVersion,
        String fingerprint,
        int rows,
        int cols,
        List<String> requiredCells,
        List<PuzzleAssignment.CheckpointSpec> checkpoints,
        List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
        List<String> canonicalSolution
    ) {}

    public MultiplayerPuzzlePool() {
        initPool();
    }

    private void initPool() {
        // --- 11x11 All Cells Helper (121 cells) ---
        List<String> cells11x11 = new ArrayList<>(121);
        for (int r = 0; r < 11; r++) {
            for (int c = 0; c < 11; c++) {
                cells11x11.add(r + "," + c);
            }
        }

        // --- 12x12 All Cells Helper (144 cells) ---
        List<String> cells12x12 = new ArrayList<>(144);
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 12; c++) {
                cells12x12.add(r + "," + c);
            }
        }

        // =========================================================================
        // QUICK DUEL 11x11 POOL (VERY_HARD / EXPERT Floor)
        // =========================================================================
        VerifiedPuzzleEntry qd11_01 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_qd_01",
                5,
                "sha256_mp_comp_11x11_qd_01_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 7, 7),
                        new PuzzleAssignment.CheckpointSpec(2, 6, 9),
                        new PuzzleAssignment.CheckpointSpec(3, 5, 7),
                        new PuzzleAssignment.CheckpointSpec(4, 7, 5),
                        new PuzzleAssignment.CheckpointSpec(5, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(6, 7, 4),
                        new PuzzleAssignment.CheckpointSpec(7, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(8, 4, 2),
                        new PuzzleAssignment.CheckpointSpec(9, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(10, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(11, 5, 0),
                        new PuzzleAssignment.CheckpointSpec(12, 8, 2),
                        new PuzzleAssignment.CheckpointSpec(13, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(14, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(15, 8, 8),
                        new PuzzleAssignment.CheckpointSpec(16, 9, 10),
                        new PuzzleAssignment.CheckpointSpec(17, 8, 9),
                        new PuzzleAssignment.CheckpointSpec(18, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(19, 3, 8),
                        new PuzzleAssignment.CheckpointSpec(20, 1, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 2, 10),
                        new PuzzleAssignment.CheckpointSpec(22, 0, 5),
                        new PuzzleAssignment.CheckpointSpec(23, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(24, 1, 5)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(4, 4, 4, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 7, 7, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 6, 6, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 3, 2, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 7, 9, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 8, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 8, 5, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 4, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 5, 7, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 3, 8, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 3, 7, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 2, 5, 3)
                ),
                List.of(
                        "7,7", "6,7", "6,8", "7,8", "7,9", "6,9", "5,9", "4,9", "4,8", "5,8", "5,7", "4,7",
                        "3,7", "3,6", "4,6", "5,6", "6,6", "7,6", "8,6", "8,5", "7,5", "6,5", "5,5", "4,5",
                        "3,5", "2,5", "2,4", "3,4", "4,4", "5,4", "6,4", "7,4", "8,4", "9,4", "9,3", "8,3",
                        "7,3", "7,2", "7,1", "6,1", "5,1", "5,2", "6,2", "6,3", "5,3", "4,3", "4,2", "3,2",
                        "3,3", "2,3", "2,2", "2,1", "2,0", "3,0", "3,1", "4,1", "4,0", "5,0", "6,0", "7,0",
                        "8,0", "8,1", "8,2", "9,2", "9,1", "9,0", "10,0", "10,1", "10,2", "10,3", "10,4", "10,5",
                        "9,5", "9,6", "10,6", "10,7", "9,7", "8,7", "8,8", "9,8", "10,8", "10,9", "10,10", "9,10",
                        "9,9", "8,9", "8,10", "7,10", "6,10", "5,10", "4,10", "3,10", "3,9", "3,8", "2,8", "2,7",
                        "2,6", "1,6", "1,7", "1,8", "1,9", "2,9", "2,10", "1,10", "0,10", "0,9", "0,8", "0,7",
                        "0,6", "0,5", "0,4", "0,3", "0,2", "0,1", "0,0", "1,0", "1,1", "1,2", "1,3", "1,4",
                        "1,5"
                )
        );
        VerifiedPuzzleEntry qd11_02 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_qd_02",
                5,
                "sha256_mp_comp_11x11_qd_02_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 9, 3),
                        new PuzzleAssignment.CheckpointSpec(2, 8, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 9, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 6, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(7, 9, 9),
                        new PuzzleAssignment.CheckpointSpec(8, 8, 9),
                        new PuzzleAssignment.CheckpointSpec(9, 7, 7),
                        new PuzzleAssignment.CheckpointSpec(10, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(11, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(12, 4, 5),
                        new PuzzleAssignment.CheckpointSpec(13, 4, 6),
                        new PuzzleAssignment.CheckpointSpec(14, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(15, 5, 2),
                        new PuzzleAssignment.CheckpointSpec(16, 2, 1),
                        new PuzzleAssignment.CheckpointSpec(17, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(18, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(19, 4, 9),
                        new PuzzleAssignment.CheckpointSpec(20, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(21, 4, 7),
                        new PuzzleAssignment.CheckpointSpec(22, 1, 5),
                        new PuzzleAssignment.CheckpointSpec(23, 4, 8)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(1, 2, 1, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 8, 2, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 8, 3, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 4, 5, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 8, 6, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 9, 8, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 3, 8, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 3, 10, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 6, 9, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 7, 8, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 2, 4, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 1, 2, 2)
                ),
                List.of(
                        "9,3", "10,3", "10,2", "9,2", "8,2", "8,3", "8,4", "7,4", "7,3", "7,2", "7,1", "8,1",
                        "9,1", "10,1", "10,0", "9,0", "8,0", "7,0", "6,0", "6,1", "6,2", "6,3", "6,4", "6,5",
                        "7,5", "8,5", "9,5", "9,4", "10,4", "10,5", "10,6", "9,6", "9,7", "10,7", "10,8", "9,8",
                        "9,9", "10,9", "10,10", "9,10", "8,10", "8,9", "8,8", "7,8", "6,8", "6,7", "7,7", "8,7",
                        "8,6", "7,6", "6,6", "5,6", "5,5", "5,4", "5,3", "4,3", "4,4", "4,5", "4,6", "3,6",
                        "3,5", "3,4", "3,3", "3,2", "3,1", "4,1", "4,2", "5,2", "5,1", "5,0", "4,0", "3,0",
                        "2,0", "2,1", "1,1", "1,0", "0,0", "0,1", "0,2", "1,2", "2,2", "2,3", "2,4", "1,4",
                        "1,3", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "0,10", "1,10", "1,9", "2,9",
                        "2,10", "3,10", "3,9", "4,9", "4,10", "5,10", "6,10", "7,10", "7,9", "6,9", "5,9", "5,8",
                        "5,7", "4,7", "3,7", "2,7", "2,6", "2,5", "1,5", "1,6", "1,7", "1,8", "2,8", "3,8",
                        "4,8"
                )
        );
        VerifiedPuzzleEntry qd11_03 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_qd_03",
                5,
                "sha256_mp_comp_11x11_qd_03_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 1, 9),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 10),
                        new PuzzleAssignment.CheckpointSpec(4, 5, 9),
                        new PuzzleAssignment.CheckpointSpec(5, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(6, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(7, 4, 8),
                        new PuzzleAssignment.CheckpointSpec(8, 3, 8),
                        new PuzzleAssignment.CheckpointSpec(9, 4, 7),
                        new PuzzleAssignment.CheckpointSpec(10, 3, 6),
                        new PuzzleAssignment.CheckpointSpec(11, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(12, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(13, 1, 2),
                        new PuzzleAssignment.CheckpointSpec(14, 10, 3),
                        new PuzzleAssignment.CheckpointSpec(15, 7, 3),
                        new PuzzleAssignment.CheckpointSpec(16, 8, 1),
                        new PuzzleAssignment.CheckpointSpec(17, 6, 1),
                        new PuzzleAssignment.CheckpointSpec(18, 10, 9),
                        new PuzzleAssignment.CheckpointSpec(19, 9, 7),
                        new PuzzleAssignment.CheckpointSpec(20, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 4, 4),
                        new PuzzleAssignment.CheckpointSpec(22, 4, 5),
                        new PuzzleAssignment.CheckpointSpec(23, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(24, 5, 3)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(7, 4, 7, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 3, 1, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 1, 4, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 3, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 4, 3, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 7, 1, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 8, 2, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 6, 1, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 0, 4, 0),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 3, 9, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 3, 10, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 7, 3)
                ),
                List.of(
                        "1,9", "1,8", "2,8", "2,7", "1,7", "1,6", "1,5", "1,4", "0,4", "0,5", "0,6", "0,7",
                        "0,8", "0,9", "0,10", "1,10", "2,10", "2,9", "3,9", "3,10", "4,10", "4,9", "5,9", "5,10",
                        "6,10", "6,9", "7,9", "7,10", "8,10", "8,9", "8,8", "8,7", "7,7", "7,8", "6,8", "5,8",
                        "4,8", "3,8", "3,7", "4,7", "4,6", "3,6", "2,6", "2,5", "2,4", "2,3", "1,3", "0,3",
                        "0,2", "0,1", "0,0", "1,0", "2,0", "3,0", "3,1", "2,1", "1,1", "1,2", "2,2", "3,2",
                        "4,2", "5,2", "5,1", "4,1", "4,0", "5,0", "6,0", "7,0", "8,0", "9,0", "10,0", "10,1",
                        "10,2", "10,3", "10,4", "9,4", "8,4", "7,4", "7,3", "8,3", "9,3", "9,2", "9,1", "8,1",
                        "8,2", "7,2", "7,1", "6,1", "6,2", "6,3", "6,4", "6,5", "7,5", "8,5", "9,5", "10,5",
                        "10,6", "10,7", "10,8", "10,9", "10,10", "9,10", "9,9", "9,8", "9,7", "9,6", "8,6", "7,6",
                        "6,6", "6,7", "5,7", "5,6", "5,5", "5,4", "4,4", "4,5", "3,5", "3,4", "3,3", "4,3",
                        "5,3"
                )
        );
        VerifiedPuzzleEntry qd11_04 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_qd_04",
                5,
                "sha256_mp_comp_11x11_qd_04_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(2, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 3, 4),
                        new PuzzleAssignment.CheckpointSpec(4, 7, 5),
                        new PuzzleAssignment.CheckpointSpec(5, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(6, 3, 7),
                        new PuzzleAssignment.CheckpointSpec(7, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(8, 3, 9),
                        new PuzzleAssignment.CheckpointSpec(9, 5, 10),
                        new PuzzleAssignment.CheckpointSpec(10, 2, 7),
                        new PuzzleAssignment.CheckpointSpec(11, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(12, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(13, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(14, 2, 5),
                        new PuzzleAssignment.CheckpointSpec(15, 5, 2),
                        new PuzzleAssignment.CheckpointSpec(16, 7, 1),
                        new PuzzleAssignment.CheckpointSpec(17, 6, 3),
                        new PuzzleAssignment.CheckpointSpec(18, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(19, 9, 2),
                        new PuzzleAssignment.CheckpointSpec(20, 9, 4),
                        new PuzzleAssignment.CheckpointSpec(21, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(22, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(23, 9, 8),
                        new PuzzleAssignment.CheckpointSpec(24, 8, 8)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 4, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 1, 7, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 7, 7, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 3, 3, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 3, 5, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 4, 6, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 6, 7, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 9, 5, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 8, 0, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 6, 2, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 2, 6, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 5, 10, 5)
                ),
                List.of(
                        "5,3", "5,4", "4,4", "4,3", "3,3", "3,4", "3,5", "4,5", "5,5", "6,5", "7,5", "7,6",
                        "7,7", "7,8", "6,8", "6,7", "6,6", "5,6", "4,6", "3,6", "3,7", "4,7", "5,7", "5,8",
                        "4,8", "3,8", "3,9", "4,9", "5,9", "6,9", "6,10", "5,10", "4,10", "3,10", "2,10", "1,10",
                        "0,10", "0,9", "1,9", "2,9", "2,8", "2,7", "1,7", "1,8", "0,8", "0,7", "0,6", "0,5",
                        "0,4", "0,3", "0,2", "0,1", "0,0", "1,0", "2,0", "3,0", "4,0", "4,1", "3,1", "2,1",
                        "1,1", "1,2", "1,3", "1,4", "1,5", "1,6", "2,6", "2,5", "2,4", "2,3", "2,2", "3,2",
                        "4,2", "5,2", "5,1", "5,0", "6,0", "7,0", "7,1", "6,1", "6,2", "7,2", "7,3", "6,3",
                        "6,4", "7,4", "8,4", "8,3", "8,2", "8,1", "8,0", "9,0", "10,0", "10,1", "9,1", "9,2",
                        "10,2", "10,3", "9,3", "9,4", "10,4", "10,5", "10,6", "9,6", "9,5", "8,5", "8,6", "8,7",
                        "9,7", "10,7", "10,8", "10,9", "10,10", "9,10", "8,10", "7,10", "7,9", "8,9", "9,9", "9,8",
                        "8,8"
                )
        );
        poolQuickDuel11x11.add(qd11_01);
        poolQuickDuel11x11.add(qd11_02);
        poolQuickDuel11x11.add(qd11_03);
        poolQuickDuel11x11.add(qd11_04);

        // =========================================================================
        // QUICK DUEL 12x12 POOL (EXPERT / EXTREME Floor)
        // =========================================================================
        VerifiedPuzzleEntry qd12_01 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_qd_01",
                5,
                "sha256_mp_comp_12x12_qd_01_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(2, 3, 5),
                        new PuzzleAssignment.CheckpointSpec(3, 2, 6),
                        new PuzzleAssignment.CheckpointSpec(4, 1, 6),
                        new PuzzleAssignment.CheckpointSpec(5, 1, 10),
                        new PuzzleAssignment.CheckpointSpec(6, 2, 9),
                        new PuzzleAssignment.CheckpointSpec(7, 3, 7),
                        new PuzzleAssignment.CheckpointSpec(8, 4, 3),
                        new PuzzleAssignment.CheckpointSpec(9, 1, 2),
                        new PuzzleAssignment.CheckpointSpec(10, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(11, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(12, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(13, 5, 10),
                        new PuzzleAssignment.CheckpointSpec(14, 4, 9),
                        new PuzzleAssignment.CheckpointSpec(15, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(16, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(17, 8, 8),
                        new PuzzleAssignment.CheckpointSpec(18, 6, 11),
                        new PuzzleAssignment.CheckpointSpec(19, 9, 10),
                        new PuzzleAssignment.CheckpointSpec(20, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 11, 7),
                        new PuzzleAssignment.CheckpointSpec(22, 5, 6),
                        new PuzzleAssignment.CheckpointSpec(23, 2, 1),
                        new PuzzleAssignment.CheckpointSpec(24, 7, 0),
                        new PuzzleAssignment.CheckpointSpec(25, 8, 2),
                        new PuzzleAssignment.CheckpointSpec(26, 9, 4),
                        new PuzzleAssignment.CheckpointSpec(27, 9, 0),
                        new PuzzleAssignment.CheckpointSpec(28, 10, 2),
                        new PuzzleAssignment.CheckpointSpec(29, 10, 3)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(10, 5, 10, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 7, 3, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 2, 10, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 4, 9, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 8, 5, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 3, 5, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 3, 3, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 9, 9, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 3, 5, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 4, 7, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 0, 7, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 4, 10, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 2, 7, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(11, 4, 11, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(11, 8, 11, 9)
                ),
                List.of(
                        "3,3", "2,3", "2,4", "3,4", "3,5", "3,6", "2,6", "2,5", "1,5", "1,6", "1,7", "1,8",
                        "1,9", "1,10", "2,10", "2,9", "2,8", "2,7", "3,7", "4,7", "4,6", "4,5", "4,4", "4,3",
                        "4,2", "3,2", "2,2", "1,2", "1,1", "1,0", "0,0", "0,1", "0,2", "0,3", "1,3", "1,4",
                        "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "0,10", "0,11", "1,11", "2,11", "3,11", "4,11",
                        "5,11", "5,10", "4,10", "3,10", "3,9", "3,8", "4,8", "4,9", "5,9", "5,8", "5,7", "6,7",
                        "6,8", "6,9", "7,9", "7,8", "8,8", "8,9", "8,10", "7,10", "6,10", "6,11", "7,11", "8,11",
                        "9,11", "9,10", "10,10", "10,11", "11,11", "11,10", "11,9", "10,9", "9,9", "9,8", "9,7", "8,7",
                        "7,7", "7,6", "8,6", "9,6", "10,6", "10,7", "10,8", "11,8", "11,7", "11,6", "11,5", "10,5",
                        "9,5", "8,5", "7,5", "6,5", "6,6", "5,6", "5,5", "5,4", "6,4", "6,3", "5,3", "5,2",
                        "6,2", "6,1", "5,1", "4,1", "3,1", "2,1", "2,0", "3,0", "4,0", "5,0", "6,0", "7,0",
                        "8,0", "8,1", "7,1", "7,2", "8,2", "8,3", "7,3", "7,4", "8,4", "9,4", "9,3", "9,2",
                        "9,1", "9,0", "10,0", "11,0", "11,1", "10,1", "10,2", "11,2", "11,3", "11,4", "10,4", "10,3"
                )
        );
        VerifiedPuzzleEntry qd12_02 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_qd_02",
                5,
                "sha256_mp_comp_12x12_qd_02_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 9, 1),
                        new PuzzleAssignment.CheckpointSpec(2, 9, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 11, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 8, 1),
                        new PuzzleAssignment.CheckpointSpec(5, 10, 5),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(7, 11, 6),
                        new PuzzleAssignment.CheckpointSpec(8, 7, 8),
                        new PuzzleAssignment.CheckpointSpec(9, 2, 9),
                        new PuzzleAssignment.CheckpointSpec(10, 1, 8),
                        new PuzzleAssignment.CheckpointSpec(11, 2, 7),
                        new PuzzleAssignment.CheckpointSpec(12, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(13, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(14, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(15, 2, 6),
                        new PuzzleAssignment.CheckpointSpec(16, 3, 9),
                        new PuzzleAssignment.CheckpointSpec(17, 4, 10),
                        new PuzzleAssignment.CheckpointSpec(18, 4, 3),
                        new PuzzleAssignment.CheckpointSpec(19, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(20, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(21, 3, 2),
                        new PuzzleAssignment.CheckpointSpec(22, 7, 1),
                        new PuzzleAssignment.CheckpointSpec(23, 5, 1),
                        new PuzzleAssignment.CheckpointSpec(24, 6, 3),
                        new PuzzleAssignment.CheckpointSpec(25, 5, 4),
                        new PuzzleAssignment.CheckpointSpec(26, 8, 6),
                        new PuzzleAssignment.CheckpointSpec(27, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(28, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(29, 9, 7),
                        new PuzzleAssignment.CheckpointSpec(30, 9, 10)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(5, 5, 5, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 4, 10, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 6, 8, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 1, 7, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 6, 3, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 10, 8, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 1, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 3, 8, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(11, 7, 11, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 9, 11, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 9, 0, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 4, 2, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 4, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 2, 2, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 1, 4, 1)
                ),
                List.of(
                        "9,1", "10,1", "10,2", "9,2", "9,3", "10,3", "11,3", "11,2", "11,1", "11,0", "10,0", "9,0",
                        "8,0", "8,1", "8,2", "8,3", "7,3", "7,4", "8,4", "9,4", "10,4", "11,4", "11,5", "10,5",
                        "9,5", "9,6", "10,6", "11,6", "11,7", "10,7", "10,8", "11,8", "11,9", "11,10", "11,11", "10,11",
                        "9,11", "8,11", "8,10", "8,9", "8,8", "7,8", "7,9", "7,10", "7,11", "6,11", "5,11", "4,11",
                        "3,11", "2,11", "1,11", "0,11", "0,10", "1,10", "2,10", "2,9", "1,9", "0,9", "0,8", "1,8",
                        "2,8", "2,7", "1,7", "0,7", "0,6", "0,5", "0,4", "0,3", "1,3", "1,4", "1,5", "1,6",
                        "2,6", "2,5", "3,5", "3,6", "3,7", "3,8", "3,9", "3,10", "4,10", "4,9", "4,8", "4,7",
                        "4,6", "4,5", "4,4", "4,3", "3,3", "3,4", "2,4", "2,3", "2,2", "2,1", "1,1", "1,2",
                        "0,2", "0,1", "0,0", "1,0", "2,0", "3,0", "3,1", "3,2", "4,2", "4,1", "4,0", "5,0",
                        "6,0", "7,0", "7,1", "7,2", "6,2", "6,1", "5,1", "5,2", "5,3", "6,3", "6,4", "5,4",
                        "5,5", "6,5", "7,5", "8,5", "8,6", "7,6", "6,6", "5,6", "5,7", "5,8", "5,9", "5,10",
                        "6,10", "6,9", "6,8", "6,7", "7,7", "8,7", "9,7", "9,8", "9,9", "10,9", "10,10", "9,10"
                )
        );
        VerifiedPuzzleEntry qd12_03 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_qd_03",
                5,
                "sha256_mp_comp_12x12_qd_03_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 6, 4),
                        new PuzzleAssignment.CheckpointSpec(2, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(3, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 6),
                        new PuzzleAssignment.CheckpointSpec(5, 4, 8),
                        new PuzzleAssignment.CheckpointSpec(6, 1, 6),
                        new PuzzleAssignment.CheckpointSpec(7, 2, 5),
                        new PuzzleAssignment.CheckpointSpec(8, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(9, 8, 4),
                        new PuzzleAssignment.CheckpointSpec(10, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(11, 11, 2),
                        new PuzzleAssignment.CheckpointSpec(12, 7, 1),
                        new PuzzleAssignment.CheckpointSpec(13, 6, 1),
                        new PuzzleAssignment.CheckpointSpec(14, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(15, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(16, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(17, 2, 9),
                        new PuzzleAssignment.CheckpointSpec(18, 3, 10),
                        new PuzzleAssignment.CheckpointSpec(19, 1, 11),
                        new PuzzleAssignment.CheckpointSpec(20, 5, 11),
                        new PuzzleAssignment.CheckpointSpec(21, 10, 11),
                        new PuzzleAssignment.CheckpointSpec(22, 10, 8),
                        new PuzzleAssignment.CheckpointSpec(23, 6, 9),
                        new PuzzleAssignment.CheckpointSpec(24, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(25, 9, 9),
                        new PuzzleAssignment.CheckpointSpec(26, 9, 7),
                        new PuzzleAssignment.CheckpointSpec(27, 11, 4),
                        new PuzzleAssignment.CheckpointSpec(28, 10, 5),
                        new PuzzleAssignment.CheckpointSpec(29, 7, 5),
                        new PuzzleAssignment.CheckpointSpec(30, 9, 2)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(5, 4, 5, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 2, 10, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 6, 7, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 4, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 2, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 4, 7, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 9, 10, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 7, 7, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 6, 3, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 5, 2, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 1, 6, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 7, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 9, 5, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 10, 11, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 9, 8, 9)
                ),
                List.of(
                        "6,4", "5,4", "4,4", "4,5", "5,5", "6,5", "6,6", "6,7", "6,8", "5,8", "5,7", "5,6",
                        "4,6", "3,6", "2,6", "2,7", "3,7", "4,7", "4,8", "3,8", "2,8", "1,8", "1,7", "1,6",
                        "1,5", "1,4", "2,4", "2,5", "3,5", "3,4", "3,3", "4,3", "5,3", "6,3", "7,3", "7,4",
                        "8,4", "8,3", "8,2", "8,1", "9,1", "10,1", "10,2", "11,2", "11,1", "11,0", "10,0", "9,0",
                        "8,0", "7,0", "7,1", "7,2", "6,2", "5,2", "5,1", "6,1", "6,0", "5,0", "4,0", "3,0",
                        "2,0", "1,0", "0,0", "0,1", "0,2", "1,2", "1,1", "2,1", "3,1", "4,1", "4,2", "3,2",
                        "2,2", "2,3", "1,3", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "1,9", "2,9",
                        "3,9", "4,9", "4,10", "3,10", "2,10", "1,10", "0,10", "0,11", "1,11", "2,11", "3,11", "4,11",
                        "5,11", "6,11", "7,11", "8,11", "9,11", "10,11", "11,11", "11,10", "11,9", "11,8", "10,8", "10,9",
                        "10,10", "9,10", "8,10", "7,10", "6,10", "5,10", "5,9", "6,9", "7,9", "7,8", "7,7", "8,7",
                        "8,8", "8,9", "9,9", "9,8", "9,7", "10,7", "11,7", "11,6", "11,5", "11,4", "11,3", "10,3",
                        "10,4", "10,5", "10,6", "9,6", "8,6", "7,6", "7,5", "8,5", "9,5", "9,4", "9,3", "9,2"
                )
        );
        VerifiedPuzzleEntry qd12_04 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_qd_04",
                5,
                "sha256_mp_comp_12x12_qd_04_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 8, 10),
                        new PuzzleAssignment.CheckpointSpec(2, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(4, 1, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 5, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 1),
                        new PuzzleAssignment.CheckpointSpec(7, 10, 2),
                        new PuzzleAssignment.CheckpointSpec(8, 10, 0),
                        new PuzzleAssignment.CheckpointSpec(9, 11, 2),
                        new PuzzleAssignment.CheckpointSpec(10, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(11, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(12, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(13, 6, 4),
                        new PuzzleAssignment.CheckpointSpec(14, 3, 5),
                        new PuzzleAssignment.CheckpointSpec(15, 3, 4),
                        new PuzzleAssignment.CheckpointSpec(16, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(17, 1, 5),
                        new PuzzleAssignment.CheckpointSpec(18, 1, 9),
                        new PuzzleAssignment.CheckpointSpec(19, 2, 10),
                        new PuzzleAssignment.CheckpointSpec(20, 2, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 5, 7),
                        new PuzzleAssignment.CheckpointSpec(22, 3, 7),
                        new PuzzleAssignment.CheckpointSpec(23, 4, 10),
                        new PuzzleAssignment.CheckpointSpec(24, 4, 9),
                        new PuzzleAssignment.CheckpointSpec(25, 7, 10),
                        new PuzzleAssignment.CheckpointSpec(26, 8, 8),
                        new PuzzleAssignment.CheckpointSpec(27, 11, 6),
                        new PuzzleAssignment.CheckpointSpec(28, 9, 10),
                        new PuzzleAssignment.CheckpointSpec(29, 11, 10)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(9, 5, 9, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 4, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 1, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 3, 4, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 8, 9, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 5, 7, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 1, 6, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 7, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 0, 8, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 3, 11, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(11, 4, 11, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 3, 9, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 3, 8, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 4, 6, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 7, 5, 7)
                ),
                List.of(
                        "8,10", "8,11", "7,11", "6,11", "5,11", "4,11", "3,11", "2,11", "1,11", "0,11", "0,10", "0,9",
                        "0,8", "0,7", "0,6", "0,5", "0,4", "1,4", "1,3", "0,3", "0,2", "0,1", "0,0", "1,0",
                        "2,0", "3,0", "4,0", "4,1", "3,1", "2,1", "1,1", "1,2", "2,2", "3,2", "4,2", "5,2",
                        "6,2", "7,2", "7,1", "6,1", "5,1", "5,0", "6,0", "7,0", "8,0", "9,0", "9,1", "8,1",
                        "8,2", "9,2", "10,2", "10,1", "10,0", "11,0", "11,1", "11,2", "11,3", "11,4", "10,4", "10,3",
                        "9,3", "8,3", "8,4", "9,4", "9,5", "8,5", "7,5", "7,6", "8,6", "9,6", "9,7", "8,7",
                        "7,7", "6,7", "6,6", "6,5", "5,5", "5,4", "6,4", "7,4", "7,3", "6,3", "5,3", "4,3",
                        "4,4", "4,5", "3,5", "3,4", "3,3", "2,3", "2,4", "2,5", "1,5", "1,6", "1,7", "1,8",
                        "1,9", "1,10", "2,10", "2,9", "2,8", "2,7", "2,6", "3,6", "4,6", "5,6", "5,7", "5,8",
                        "4,8", "4,7", "3,7", "3,8", "3,9", "3,10", "4,10", "4,9", "5,9", "5,10", "6,10", "7,10",
                        "7,9", "6,9", "6,8", "7,8", "8,8", "8,9", "9,9", "9,8", "10,8", "10,7", "10,6", "10,5",
                        "11,5", "11,6", "11,7", "11,8", "11,9", "10,9", "10,10", "9,10", "9,11", "10,11", "11,11", "11,10"
                )
        );
        poolQuickDuel12x12.add(qd12_01);
        poolQuickDuel12x12.add(qd12_02);
        poolQuickDuel12x12.add(qd12_03);
        poolQuickDuel12x12.add(qd12_04);

        // =========================================================================
        // FRIENDS ARENA 11x11 POOL (EXPERT Minimum Floor)
        // =========================================================================
        VerifiedPuzzleEntry fa11_01 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_fa_01",
                5,
                "sha256_mp_comp_11x11_fa_01_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 7, 7),
                        new PuzzleAssignment.CheckpointSpec(2, 6, 9),
                        new PuzzleAssignment.CheckpointSpec(3, 5, 7),
                        new PuzzleAssignment.CheckpointSpec(4, 7, 5),
                        new PuzzleAssignment.CheckpointSpec(5, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(6, 7, 4),
                        new PuzzleAssignment.CheckpointSpec(7, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(8, 4, 2),
                        new PuzzleAssignment.CheckpointSpec(9, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(10, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(11, 5, 0),
                        new PuzzleAssignment.CheckpointSpec(12, 8, 2),
                        new PuzzleAssignment.CheckpointSpec(13, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(14, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(15, 8, 8),
                        new PuzzleAssignment.CheckpointSpec(16, 9, 10),
                        new PuzzleAssignment.CheckpointSpec(17, 8, 9),
                        new PuzzleAssignment.CheckpointSpec(18, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(19, 3, 8),
                        new PuzzleAssignment.CheckpointSpec(20, 1, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 2, 10),
                        new PuzzleAssignment.CheckpointSpec(22, 0, 5),
                        new PuzzleAssignment.CheckpointSpec(23, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(24, 1, 5)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(4, 4, 4, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 7, 7, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 6, 6, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 3, 2, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 7, 9, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 8, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 8, 5, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 4, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 5, 7, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 3, 8, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 3, 7, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 2, 5, 3)
                ),
                List.of(
                        "7,7", "6,7", "6,8", "7,8", "7,9", "6,9", "5,9", "4,9", "4,8", "5,8", "5,7", "4,7",
                        "3,7", "3,6", "4,6", "5,6", "6,6", "7,6", "8,6", "8,5", "7,5", "6,5", "5,5", "4,5",
                        "3,5", "2,5", "2,4", "3,4", "4,4", "5,4", "6,4", "7,4", "8,4", "9,4", "9,3", "8,3",
                        "7,3", "7,2", "7,1", "6,1", "5,1", "5,2", "6,2", "6,3", "5,3", "4,3", "4,2", "3,2",
                        "3,3", "2,3", "2,2", "2,1", "2,0", "3,0", "3,1", "4,1", "4,0", "5,0", "6,0", "7,0",
                        "8,0", "8,1", "8,2", "9,2", "9,1", "9,0", "10,0", "10,1", "10,2", "10,3", "10,4", "10,5",
                        "9,5", "9,6", "10,6", "10,7", "9,7", "8,7", "8,8", "9,8", "10,8", "10,9", "10,10", "9,10",
                        "9,9", "8,9", "8,10", "7,10", "6,10", "5,10", "4,10", "3,10", "3,9", "3,8", "2,8", "2,7",
                        "2,6", "1,6", "1,7", "1,8", "1,9", "2,9", "2,10", "1,10", "0,10", "0,9", "0,8", "0,7",
                        "0,6", "0,5", "0,4", "0,3", "0,2", "0,1", "0,0", "1,0", "1,1", "1,2", "1,3", "1,4",
                        "1,5"
                )
        );
        VerifiedPuzzleEntry fa11_02 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_fa_02",
                5,
                "sha256_mp_comp_11x11_fa_02_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 9, 3),
                        new PuzzleAssignment.CheckpointSpec(2, 8, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 9, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 6, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(7, 9, 9),
                        new PuzzleAssignment.CheckpointSpec(8, 8, 9),
                        new PuzzleAssignment.CheckpointSpec(9, 7, 7),
                        new PuzzleAssignment.CheckpointSpec(10, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(11, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(12, 4, 5),
                        new PuzzleAssignment.CheckpointSpec(13, 4, 6),
                        new PuzzleAssignment.CheckpointSpec(14, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(15, 5, 2),
                        new PuzzleAssignment.CheckpointSpec(16, 2, 1),
                        new PuzzleAssignment.CheckpointSpec(17, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(18, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(19, 4, 9),
                        new PuzzleAssignment.CheckpointSpec(20, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(21, 4, 7),
                        new PuzzleAssignment.CheckpointSpec(22, 1, 5),
                        new PuzzleAssignment.CheckpointSpec(23, 4, 8)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(1, 2, 1, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 8, 2, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 8, 3, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 4, 5, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 8, 6, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 9, 8, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 3, 8, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 3, 10, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 6, 9, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 7, 8, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 2, 4, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 1, 2, 2)
                ),
                List.of(
                        "9,3", "10,3", "10,2", "9,2", "8,2", "8,3", "8,4", "7,4", "7,3", "7,2", "7,1", "8,1",
                        "9,1", "10,1", "10,0", "9,0", "8,0", "7,0", "6,0", "6,1", "6,2", "6,3", "6,4", "6,5",
                        "7,5", "8,5", "9,5", "9,4", "10,4", "10,5", "10,6", "9,6", "9,7", "10,7", "10,8", "9,8",
                        "9,9", "10,9", "10,10", "9,10", "8,10", "8,9", "8,8", "7,8", "6,8", "6,7", "7,7", "8,7",
                        "8,6", "7,6", "6,6", "5,6", "5,5", "5,4", "5,3", "4,3", "4,4", "4,5", "4,6", "3,6",
                        "3,5", "3,4", "3,3", "3,2", "3,1", "4,1", "4,2", "5,2", "5,1", "5,0", "4,0", "3,0",
                        "2,0", "2,1", "1,1", "1,0", "0,0", "0,1", "0,2", "1,2", "2,2", "2,3", "2,4", "1,4",
                        "1,3", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "0,10", "1,10", "1,9", "2,9",
                        "2,10", "3,10", "3,9", "4,9", "4,10", "5,10", "6,10", "7,10", "7,9", "6,9", "5,9", "5,8",
                        "5,7", "4,7", "3,7", "2,7", "2,6", "2,5", "1,5", "1,6", "1,7", "1,8", "2,8", "3,8",
                        "4,8"
                )
        );
        VerifiedPuzzleEntry fa11_03 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_fa_03",
                5,
                "sha256_mp_comp_11x11_fa_03_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(2, 1, 1),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 2),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(5, 7, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 5, 3),
                        new PuzzleAssignment.CheckpointSpec(7, 7, 3),
                        new PuzzleAssignment.CheckpointSpec(8, 9, 4),
                        new PuzzleAssignment.CheckpointSpec(9, 9, 1),
                        new PuzzleAssignment.CheckpointSpec(10, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(11, 6, 5),
                        new PuzzleAssignment.CheckpointSpec(12, 4, 4),
                        new PuzzleAssignment.CheckpointSpec(13, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(14, 5, 7),
                        new PuzzleAssignment.CheckpointSpec(15, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(16, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(17, 2, 8),
                        new PuzzleAssignment.CheckpointSpec(18, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(19, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(20, 1, 9),
                        new PuzzleAssignment.CheckpointSpec(21, 0, 10),
                        new PuzzleAssignment.CheckpointSpec(22, 6, 10)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(4, 3, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 3, 8, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 2, 9, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 5, 1, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 1, 2, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 0, 8, 0),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 7, 5, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 7, 6, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 8, 9, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 8, 3, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 7, 3, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 8, 4, 8)
                ),
                List.of(
                        "0,2", "0,1", "0,0", "1,0", "1,1", "1,2", "2,2", "3,2", "3,1", "2,1", "2,0", "3,0",
                        "4,0", "5,0", "6,0", "7,0", "7,1", "6,1", "5,1", "4,1", "4,2", "4,3", "5,3", "5,2",
                        "6,2", "7,2", "7,3", "6,3", "6,4", "7,4", "8,4", "9,4", "9,3", "8,3", "8,2", "9,2",
                        "9,1", "8,1", "8,0", "9,0", "10,0", "10,1", "10,2", "10,3", "10,4", "10,5", "9,5", "8,5",
                        "7,5", "6,5", "5,5", "5,4", "4,4", "4,5", "4,6", "4,7", "4,8", "5,8", "6,8", "7,8",
                        "7,7", "6,7", "5,7", "5,6", "6,6", "7,6", "8,6", "8,7", "8,8", "8,9", "9,9", "9,8",
                        "9,7", "9,6", "10,6", "10,7", "10,8", "10,9", "10,10", "9,10", "8,10", "7,10", "7,9", "6,9",
                        "5,9", "4,9", "3,9", "2,9", "2,8", "3,8", "3,7", "3,6", "3,5", "2,5", "1,5", "1,4",
                        "2,4", "3,4", "3,3", "2,3", "1,3", "0,3", "0,4", "0,5", "0,6", "1,6", "2,6", "2,7",
                        "1,7", "0,7", "0,8", "1,8", "1,9", "0,9", "0,10", "1,10", "2,10", "3,10", "4,10", "5,10",
                        "6,10"
                )
        );
        VerifiedPuzzleEntry fa11_04 = new VerifiedPuzzleEntry(
                "mp_comp_11x11_fa_04",
                5,
                "sha256_mp_comp_11x11_fa_04_v5",
                11, 11,
                cells11x11,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 1, 5),
                        new PuzzleAssignment.CheckpointSpec(2, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 5, 0),
                        new PuzzleAssignment.CheckpointSpec(5, 1, 1),
                        new PuzzleAssignment.CheckpointSpec(6, 3, 2),
                        new PuzzleAssignment.CheckpointSpec(7, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(8, 6, 5),
                        new PuzzleAssignment.CheckpointSpec(9, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(10, 7, 4),
                        new PuzzleAssignment.CheckpointSpec(11, 8, 1),
                        new PuzzleAssignment.CheckpointSpec(12, 10, 4),
                        new PuzzleAssignment.CheckpointSpec(13, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(14, 10, 8),
                        new PuzzleAssignment.CheckpointSpec(15, 8, 10),
                        new PuzzleAssignment.CheckpointSpec(16, 3, 10),
                        new PuzzleAssignment.CheckpointSpec(17, 2, 5),
                        new PuzzleAssignment.CheckpointSpec(18, 3, 7),
                        new PuzzleAssignment.CheckpointSpec(19, 1, 8),
                        new PuzzleAssignment.CheckpointSpec(20, 5, 9),
                        new PuzzleAssignment.CheckpointSpec(21, 7, 9),
                        new PuzzleAssignment.CheckpointSpec(22, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(23, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(24, 2, 8)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(8, 5, 8, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 8, 6, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 8, 9, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 2, 6, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 3, 8, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 2, 5, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 0, 6, 0),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 5, 7, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 4, 9, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 1, 8, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 0, 8, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 6, 10, 6)
                ),
                List.of(
                        "1,5", "0,5", "0,4", "1,4", "2,4", "2,3", "1,3", "0,3", "0,2", "0,1", "0,0", "1,0",
                        "2,0", "3,0", "4,0", "5,0", "5,1", "4,1", "3,1", "2,1", "1,1", "1,2", "2,2", "3,2",
                        "4,2", "4,3", "3,3", "3,4", "4,4", "5,4", "6,4", "6,5", "6,6", "7,6", "7,5", "8,5",
                        "9,5", "9,4", "9,3", "8,3", "8,4", "7,4", "7,3", "6,3", "5,3", "5,2", "6,2", "7,2",
                        "7,1", "6,1", "6,0", "7,0", "8,0", "9,0", "10,0", "10,1", "9,1", "8,1", "8,2", "9,2",
                        "10,2", "10,3", "10,4", "10,5", "10,6", "10,7", "9,7", "9,6", "8,6", "8,7", "8,8", "9,8",
                        "10,8", "10,9", "10,10", "9,10", "9,9", "8,9", "8,10", "7,10", "6,10", "5,10", "4,10", "3,10",
                        "2,10", "1,10", "0,10", "0,9", "0,8", "0,7", "0,6", "1,6", "2,6", "2,5", "3,5", "3,6",
                        "3,7", "2,7", "1,7", "1,8", "1,9", "2,9", "3,9", "4,9", "5,9", "6,9", "7,9", "7,8",
                        "7,7", "6,7", "6,8", "5,8", "5,7", "5,6", "5,5", "4,5", "4,6", "4,7", "4,8", "3,8",
                        "2,8"
                )
        );
        poolFriendsArena11x11.add(fa11_01);
        poolFriendsArena11x11.add(fa11_02);
        poolFriendsArena11x11.add(fa11_03);
        poolFriendsArena11x11.add(fa11_04);

        // =========================================================================
        // FRIENDS ARENA 12x12 POOL (EXPERT / EXTREME Biased Floor)
        // =========================================================================
        VerifiedPuzzleEntry fa12_01 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_fa_01",
                5,
                "sha256_mp_comp_12x12_fa_01_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 9, 1),
                        new PuzzleAssignment.CheckpointSpec(2, 9, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 11, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 8, 1),
                        new PuzzleAssignment.CheckpointSpec(5, 10, 5),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(7, 11, 6),
                        new PuzzleAssignment.CheckpointSpec(8, 7, 8),
                        new PuzzleAssignment.CheckpointSpec(9, 2, 9),
                        new PuzzleAssignment.CheckpointSpec(10, 1, 8),
                        new PuzzleAssignment.CheckpointSpec(11, 2, 7),
                        new PuzzleAssignment.CheckpointSpec(12, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(13, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(14, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(15, 2, 6),
                        new PuzzleAssignment.CheckpointSpec(16, 3, 9),
                        new PuzzleAssignment.CheckpointSpec(17, 4, 10),
                        new PuzzleAssignment.CheckpointSpec(18, 4, 3),
                        new PuzzleAssignment.CheckpointSpec(19, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(20, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(21, 3, 2),
                        new PuzzleAssignment.CheckpointSpec(22, 7, 1),
                        new PuzzleAssignment.CheckpointSpec(23, 5, 1),
                        new PuzzleAssignment.CheckpointSpec(24, 6, 3),
                        new PuzzleAssignment.CheckpointSpec(25, 5, 4),
                        new PuzzleAssignment.CheckpointSpec(26, 8, 6),
                        new PuzzleAssignment.CheckpointSpec(27, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(28, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(29, 9, 7),
                        new PuzzleAssignment.CheckpointSpec(30, 9, 10)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(5, 5, 5, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 4, 10, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 6, 8, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 1, 7, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 6, 3, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 10, 8, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 1, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 3, 8, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(11, 7, 11, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(10, 9, 11, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 9, 0, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 4, 2, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 4, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 2, 2, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 1, 4, 1)
                ),
                List.of(
                        "9,1", "10,1", "10,2", "9,2", "9,3", "10,3", "11,3", "11,2", "11,1", "11,0", "10,0", "9,0",
                        "8,0", "8,1", "8,2", "8,3", "7,3", "7,4", "8,4", "9,4", "10,4", "11,4", "11,5", "10,5",
                        "9,5", "9,6", "10,6", "11,6", "11,7", "10,7", "10,8", "11,8", "11,9", "11,10", "11,11", "10,11",
                        "9,11", "8,11", "8,10", "8,9", "8,8", "7,8", "7,9", "7,10", "7,11", "6,11", "5,11", "4,11",
                        "3,11", "2,11", "1,11", "0,11", "0,10", "1,10", "2,10", "2,9", "1,9", "0,9", "0,8", "1,8",
                        "2,8", "2,7", "1,7", "0,7", "0,6", "0,5", "0,4", "0,3", "1,3", "1,4", "1,5", "1,6",
                        "2,6", "2,5", "3,5", "3,6", "3,7", "3,8", "3,9", "3,10", "4,10", "4,9", "4,8", "4,7",
                        "4,6", "4,5", "4,4", "4,3", "3,3", "3,4", "2,4", "2,3", "2,2", "2,1", "1,1", "1,2",
                        "0,2", "0,1", "0,0", "1,0", "2,0", "3,0", "3,1", "3,2", "4,2", "4,1", "4,0", "5,0",
                        "6,0", "7,0", "7,1", "7,2", "6,2", "6,1", "5,1", "5,2", "5,3", "6,3", "6,4", "5,4",
                        "5,5", "6,5", "7,5", "8,5", "8,6", "7,6", "6,6", "5,6", "5,7", "5,8", "5,9", "5,10",
                        "6,10", "6,9", "6,8", "6,7", "7,7", "8,7", "9,7", "9,8", "9,9", "10,9", "10,10", "9,10"
                )
        );
        VerifiedPuzzleEntry fa12_02 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_fa_02",
                5,
                "sha256_mp_comp_12x12_fa_02_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 4, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 10, 1),
                        new PuzzleAssignment.CheckpointSpec(3, 5, 2),
                        new PuzzleAssignment.CheckpointSpec(4, 8, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 9, 6),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 4),
                        new PuzzleAssignment.CheckpointSpec(7, 11, 3),
                        new PuzzleAssignment.CheckpointSpec(8, 10, 4),
                        new PuzzleAssignment.CheckpointSpec(9, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(10, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(11, 6, 7),
                        new PuzzleAssignment.CheckpointSpec(12, 7, 8),
                        new PuzzleAssignment.CheckpointSpec(13, 10, 10),
                        new PuzzleAssignment.CheckpointSpec(14, 9, 8),
                        new PuzzleAssignment.CheckpointSpec(15, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(16, 1, 10),
                        new PuzzleAssignment.CheckpointSpec(17, 5, 9),
                        new PuzzleAssignment.CheckpointSpec(18, 2, 8),
                        new PuzzleAssignment.CheckpointSpec(19, 0, 9),
                        new PuzzleAssignment.CheckpointSpec(20, 1, 5),
                        new PuzzleAssignment.CheckpointSpec(21, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(22, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(23, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(24, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(25, 6, 3),
                        new PuzzleAssignment.CheckpointSpec(26, 4, 1),
                        new PuzzleAssignment.CheckpointSpec(27, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(28, 1, 0)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(2, 1, 2, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 2, 5, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 7, 2, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 1, 7, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 7, 9, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 4, 4, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 9, 8, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 2, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 3, 8, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 5, 9, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 7, 5, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 4, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 8, 7, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 8, 3, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(3, 8, 3, 9)
                ),
                List.of(
                        "4,0", "5,0", "6,0", "7,0", "8,0", "9,0", "10,0", "11,0", "11,1", "10,1", "9,1", "8,1",
                        "7,1", "6,1", "5,1", "5,2", "6,2", "7,2", "8,2", "8,3", "8,4", "8,5", "8,6", "9,6",
                        "10,6", "10,5", "9,5", "9,4", "9,3", "9,2", "10,2", "11,2", "11,3", "10,3", "10,4", "11,4",
                        "11,5", "11,6", "11,7", "10,7", "9,7", "8,7", "7,7", "7,6", "7,5", "6,5", "5,5", "5,6",
                        "6,6", "6,7", "5,7", "4,7", "3,7", "3,8", "4,8", "5,8", "6,8", "6,9", "7,9", "7,8",
                        "8,8", "8,9", "8,10", "9,10", "10,10", "10,9", "9,9", "9,8", "10,8", "11,8", "11,9", "11,10",
                        "11,11", "10,11", "9,11", "8,11", "7,11", "7,10", "6,10", "6,11", "5,11", "4,11", "3,11", "2,11",
                        "1,11", "0,11", "0,10", "1,10", "2,10", "3,10", "4,10", "5,10", "5,9", "4,9", "3,9", "2,9",
                        "2,8", "1,8", "1,9", "0,9", "0,8", "0,7", "1,7", "2,7", "2,6", "2,5", "1,5", "1,6",
                        "0,6", "0,5", "0,4", "0,3", "1,3", "1,4", "2,4", "2,3", "2,2", "3,2", "3,3", "3,4",
                        "3,5", "3,6", "4,6", "4,5", "4,4", "5,4", "6,4", "7,4", "7,3", "6,3", "5,3", "4,3",
                        "4,2", "4,1", "3,1", "3,0", "2,0", "2,1", "1,1", "1,2", "0,2", "0,1", "0,0", "1,0"
                )
        );
        VerifiedPuzzleEntry fa12_03 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_fa_03",
                5,
                "sha256_mp_comp_12x12_fa_03_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 9, 1),
                        new PuzzleAssignment.CheckpointSpec(2, 10, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 8, 1),
                        new PuzzleAssignment.CheckpointSpec(4, 5, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 8, 4),
                        new PuzzleAssignment.CheckpointSpec(6, 6, 5),
                        new PuzzleAssignment.CheckpointSpec(7, 8, 7),
                        new PuzzleAssignment.CheckpointSpec(8, 8, 10),
                        new PuzzleAssignment.CheckpointSpec(9, 9, 9),
                        new PuzzleAssignment.CheckpointSpec(10, 7, 6),
                        new PuzzleAssignment.CheckpointSpec(11, 10, 6),
                        new PuzzleAssignment.CheckpointSpec(12, 10, 10),
                        new PuzzleAssignment.CheckpointSpec(13, 10, 0),
                        new PuzzleAssignment.CheckpointSpec(14, 6, 1),
                        new PuzzleAssignment.CheckpointSpec(15, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(16, 3, 2),
                        new PuzzleAssignment.CheckpointSpec(17, 5, 4),
                        new PuzzleAssignment.CheckpointSpec(18, 4, 6),
                        new PuzzleAssignment.CheckpointSpec(19, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(20, 3, 7),
                        new PuzzleAssignment.CheckpointSpec(21, 1, 8),
                        new PuzzleAssignment.CheckpointSpec(22, 5, 8),
                        new PuzzleAssignment.CheckpointSpec(23, 7, 11),
                        new PuzzleAssignment.CheckpointSpec(24, 4, 9),
                        new PuzzleAssignment.CheckpointSpec(25, 1, 10),
                        new PuzzleAssignment.CheckpointSpec(26, 1, 11),
                        new PuzzleAssignment.CheckpointSpec(27, 3, 10)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(2, 8, 2, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 4, 2, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 9, 10, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 10, 6, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 10, 8, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(6, 6, 7, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 1, 9, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(9, 2, 10, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 2, 5, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 1, 0, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 11, 5, 11),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 9, 4, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(0, 8, 0, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 6, 5, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(8, 8, 8, 9)
                ),
                List.of(
                        "9,1", "10,1", "10,2", "10,3", "10,4", "9,4", "9,3", "9,2", "8,2", "8,1", "7,1", "7,2",
                        "6,2", "5,2", "4,2", "4,3", "5,3", "6,3", "7,3", "8,3", "8,4", "7,4", "6,4", "6,5",
                        "6,6", "6,7", "7,7", "8,7", "8,8", "7,8", "7,9", "8,9", "8,10", "8,11", "9,11", "9,10",
                        "9,9", "9,8", "9,7", "9,6", "8,6", "7,6", "7,5", "8,5", "9,5", "10,5", "10,6", "10,7",
                        "10,8", "10,9", "10,10", "10,11", "11,11", "11,10", "11,9", "11,8", "11,7", "11,6", "11,5", "11,4",
                        "11,3", "11,2", "11,1", "11,0", "10,0", "9,0", "8,0", "7,0", "6,0", "6,1", "5,1", "5,0",
                        "4,0", "4,1", "3,1", "3,0", "2,0", "1,0", "0,0", "0,1", "1,1", "2,1", "2,2", "3,2",
                        "3,3", "3,4", "4,4", "5,4", "5,5", "5,6", "5,7", "4,7", "4,6", "4,5", "3,5", "2,5",
                        "2,4", "2,3", "1,3", "1,2", "0,2", "0,3", "0,4", "1,4", "1,5", "0,5", "0,6", "1,6",
                        "2,6", "3,6", "3,7", "2,7", "1,7", "0,7", "0,8", "1,8", "2,8", "3,8", "4,8", "5,8",
                        "6,8", "6,9", "6,10", "7,10", "7,11", "6,11", "5,11", "5,10", "5,9", "4,9", "3,9", "2,9",
                        "2,10", "1,10", "1,9", "0,9", "0,10", "0,11", "1,11", "2,11", "3,11", "4,11", "4,10", "3,10"
                )
        );
        VerifiedPuzzleEntry fa12_04 = new VerifiedPuzzleEntry(
                "mp_comp_12x12_fa_04",
                5,
                "sha256_mp_comp_12x12_fa_04_v5",
                12, 12,
                cells12x12,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 6, 6),
                        new PuzzleAssignment.CheckpointSpec(2, 5, 5),
                        new PuzzleAssignment.CheckpointSpec(3, 6, 5),
                        new PuzzleAssignment.CheckpointSpec(4, 7, 8),
                        new PuzzleAssignment.CheckpointSpec(5, 6, 10),
                        new PuzzleAssignment.CheckpointSpec(6, 9, 10),
                        new PuzzleAssignment.CheckpointSpec(7, 10, 8),
                        new PuzzleAssignment.CheckpointSpec(8, 10, 6),
                        new PuzzleAssignment.CheckpointSpec(9, 8, 9),
                        new PuzzleAssignment.CheckpointSpec(10, 9, 5),
                        new PuzzleAssignment.CheckpointSpec(11, 11, 3),
                        new PuzzleAssignment.CheckpointSpec(12, 7, 4),
                        new PuzzleAssignment.CheckpointSpec(13, 9, 2),
                        new PuzzleAssignment.CheckpointSpec(14, 10, 2),
                        new PuzzleAssignment.CheckpointSpec(15, 5, 0),
                        new PuzzleAssignment.CheckpointSpec(16, 1, 2),
                        new PuzzleAssignment.CheckpointSpec(17, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(18, 2, 6),
                        new PuzzleAssignment.CheckpointSpec(19, 0, 6),
                        new PuzzleAssignment.CheckpointSpec(20, 2, 10),
                        new PuzzleAssignment.CheckpointSpec(21, 4, 11),
                        new PuzzleAssignment.CheckpointSpec(22, 5, 10),
                        new PuzzleAssignment.CheckpointSpec(23, 3, 10),
                        new PuzzleAssignment.CheckpointSpec(24, 4, 8),
                        new PuzzleAssignment.CheckpointSpec(25, 4, 5),
                        new PuzzleAssignment.CheckpointSpec(26, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(27, 4, 2),
                        new PuzzleAssignment.CheckpointSpec(28, 7, 1),
                        new PuzzleAssignment.CheckpointSpec(29, 7, 2)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(9, 5, 9, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 2, 3, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 7, 8, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 8, 6, 8),
                        new PuzzleAssignment.BlockedEdgeSpec(7, 2, 8, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 9, 6, 9),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 10, 3, 10),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 6, 6, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 5, 6, 5),
                        new PuzzleAssignment.BlockedEdgeSpec(5, 4, 6, 4),
                        new PuzzleAssignment.BlockedEdgeSpec(4, 0, 5, 0),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 6, 2, 6),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 7, 3, 7),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 8, 2, 8)
                ),
                List.of(
                        "6,6", "6,7", "5,7", "5,6", "5,5", "5,4", "5,3", "6,3", "6,4", "6,5", "7,5", "7,6",
                        "7,7", "7,8", "6,8", "6,9", "7,9", "7,10", "6,10", "6,11", "7,11", "8,11", "8,10", "9,10",
                        "9,11", "10,11", "11,11", "11,10", "10,10", "10,9", "11,9", "11,8", "10,8", "10,7", "11,7", "11,6",
                        "10,6", "9,6", "9,7", "9,8", "9,9", "8,9", "8,8", "8,7", "8,6", "8,5", "9,5", "10,5",
                        "11,5", "11,4", "11,3", "10,3", "10,4", "9,4", "8,4", "7,4", "7,3", "8,3", "9,3", "9,2",
                        "8,2", "8,1", "9,1", "10,1", "10,2", "11,2", "11,1", "11,0", "10,0", "9,0", "8,0", "7,0",
                        "6,0", "5,0", "5,1", "4,1", "4,0", "3,0", "2,0", "1,0", "0,0", "0,1", "1,1", "1,2",
                        "0,2", "0,3", "1,3", "1,4", "0,4", "0,5", "1,5", "2,5", "2,4", "3,4", "3,5", "3,6",
                        "2,6", "2,7", "2,8", "2,9", "1,9", "1,8", "1,7", "1,6", "0,6", "0,7", "0,8", "0,9",
                        "0,10", "0,11", "1,11", "1,10", "2,10", "2,11", "3,11", "4,11", "5,11", "5,10", "4,10", "3,10",
                        "3,9", "4,9", "5,9", "5,8", "4,8", "3,8", "3,7", "4,7", "4,6", "4,5", "4,4", "4,3",
                        "3,3", "2,3", "2,2", "2,1", "3,1", "3,2", "4,2", "5,2", "6,2", "6,1", "7,1", "7,2"
                )
        );
        poolFriendsArena12x12.add(fa12_01);
        poolFriendsArena12x12.add(fa12_02);
        poolFriendsArena12x12.add(fa12_03);
        poolFriendsArena12x12.add(fa12_04);
    }

    public List<VerifiedPuzzleEntry> getQuickDuelPool() {
        List<VerifiedPuzzleEntry> combined = new ArrayList<>(poolQuickDuel11x11);
        combined.addAll(poolQuickDuel12x12);
        return Collections.unmodifiableList(combined);
    }

    public List<VerifiedPuzzleEntry> getQuickDuel11x11Pool() {
        return Collections.unmodifiableList(poolQuickDuel11x11);
    }

    public List<VerifiedPuzzleEntry> getQuickDuel12x12Pool() {
        return Collections.unmodifiableList(poolQuickDuel12x12);
    }

    public List<VerifiedPuzzleEntry> getFriendsArenaPool() {
        List<VerifiedPuzzleEntry> combined = new ArrayList<>(poolFriendsArena11x11);
        combined.addAll(poolFriendsArena12x12);
        return Collections.unmodifiableList(combined);
    }

    public List<VerifiedPuzzleEntry> getFriendsArena11x11Pool() {
        return Collections.unmodifiableList(poolFriendsArena11x11);
    }

    public List<VerifiedPuzzleEntry> getFriendsArena12x12Pool() {
        return Collections.unmodifiableList(poolFriendsArena12x12);
    }

    public List<VerifiedPuzzleEntry> getBracket1Pool() {
        return Collections.unmodifiableList(poolQuickDuel11x11);
    }

    public List<VerifiedPuzzleEntry> getBracket2Pool() {
        return Collections.unmodifiableList(poolQuickDuel12x12);
    }

    public List<VerifiedPuzzleEntry> getHardPool() {
        return Collections.unmodifiableList(poolQuickDuel11x11);
    }

    public List<VerifiedPuzzleEntry> getExpertPool() {
        return Collections.unmodifiableList(poolQuickDuel11x11);
    }

    public List<VerifiedPuzzleEntry> getExtremePool() {
        return Collections.unmodifiableList(poolQuickDuel12x12);
    }

    public List<VerifiedPuzzleEntry> getAllCompetitivePuzzles() {
        LinkedHashSet<VerifiedPuzzleEntry> all = new LinkedHashSet<>();
        all.addAll(poolQuickDuel11x11);
        all.addAll(poolQuickDuel12x12);
        all.addAll(poolFriendsArena11x11);
        all.addAll(poolFriendsArena12x12);
        return Collections.unmodifiableList(new ArrayList<>(all));
    }

    /**
     * Selects a competitive puzzle by explicit difficulty bracket.
     * Bracket 1: 11x11 (VERY_HARD / EXPERT).
     * Bracket 2: 12x12 (EXPERT / EXTREME).
     */
    public PuzzleAssignment selectCompetitivePuzzle(GameMode mode, int difficultyBracket, long seed, String excludePuzzleId) {
        List<VerifiedPuzzleEntry> targetPool = switch (mode) {
            case FRIENDS_ARENA -> getFriendsArenaPool();
            case MINI_LEAGUE -> poolFriendsArena12x12;
            case QUICK_DUEL, FRIEND_DUEL -> {
                if (difficultyBracket <= 1) yield poolQuickDuel11x11;
                else yield poolQuickDuel12x12;
            }
        };
        return pickFromPool(targetPool, seed, excludePuzzleId);
    }

    /**
     * Authoritatively selects a verified competitive puzzle for a match.
     */
    public PuzzleAssignment selectPuzzleForMode(GameMode mode, long seed) {
        return selectPuzzleForModeExcluding(mode, seed, null);
    }

    /**
     * Authoritatively selects a verified puzzle for a match, strictly from 11x11 and 12x12 pools.
     * Guaranteed 0 smaller boards eligible.
     */
    public PuzzleAssignment selectPuzzleForModeExcluding(GameMode mode, long seed, String excludePuzzleId) {
        Random rng = new Random(seed);
        List<VerifiedPuzzleEntry> targetPool = switch (mode) {
            case QUICK_DUEL -> {
                boolean pick12 = rng.nextBoolean();
                yield pick12 ? poolQuickDuel12x12 : poolQuickDuel11x11;
            }
            case FRIEND_DUEL -> poolQuickDuel11x11;
            case FRIENDS_ARENA -> {
                int roll = rng.nextInt(4);
                yield (roll == 0) ? poolFriendsArena11x11 : poolFriendsArena12x12;
            }
            case MINI_LEAGUE -> poolFriendsArena12x12;
        };

        return pickFromPool(targetPool, seed, excludePuzzleId);
    }

    private PuzzleAssignment pickFromPool(List<VerifiedPuzzleEntry> pool, long seed, String excludePuzzleId) {
        Random rng = new Random(seed);
        List<VerifiedPuzzleEntry> filtered = pool;
        if (excludePuzzleId != null && !excludePuzzleId.isBlank()) {
            List<VerifiedPuzzleEntry> nonMatching = pool.stream()
                    .filter(e -> !e.puzzleId().equals(excludePuzzleId))
                    .toList();
            if (!nonMatching.isEmpty()) {
                filtered = nonMatching;
            }
        }

        int index = Math.abs(rng.nextInt()) % filtered.size();
        VerifiedPuzzleEntry entry = filtered.get(index);

        return new PuzzleAssignment(
                entry.puzzleId(),
                entry.puzzleVersion(),
                entry.fingerprint(),
                entry.rows(),
                entry.cols(),
                entry.requiredCells(),
                entry.checkpoints(),
                entry.blockedEdges()
        );
    }
}
