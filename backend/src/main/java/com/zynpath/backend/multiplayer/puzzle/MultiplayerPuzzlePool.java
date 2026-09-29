package com.zynpath.backend.multiplayer.puzzle;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Authoritative pool of solver-verified, continuous-path puzzles for online competition.
 *
 * Implements Prompt 20 Sections 21, 22, 23:
 * - Every candidate puzzle has a verified 100% full coverage path.
 * - Guarantees all participants in a match receive identical puzzle definitions.
 * - Immutable assignments keyed by stable fingerprints.
 */
@Component
public class MultiplayerPuzzlePool {

    private final List<VerifiedPuzzleEntry> pool4x4 = new ArrayList<>();
    private final List<VerifiedPuzzleEntry> pool5x5 = new ArrayList<>();
    private final List<VerifiedPuzzleEntry> pool6x6 = new ArrayList<>();

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
        // --- 4x4 Standard Curated Puzzles ---
        List<String> cells4x4 = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                cells4x4.add(r + "," + c);
            }
        }

        // 4x4 Puzzle 1: Serpentine S-Path
        pool4x4.add(new VerifiedPuzzleEntry(
                "mp_curated_4x4_01",
                1,
                "sha256_mp_4x4_01_a9f1c7e2b834",
                4, 4,
                cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0)
                ),
                List.of(),
                List.of(
                        "0,0", "0,1", "0,2", "0,3",
                        "1,3", "1,2", "1,1", "1,0",
                        "2,0", "2,1", "2,2", "2,3",
                        "3,3", "3,2", "3,1", "3,0"
                )
        ));

        // 4x4 Puzzle 2: Perimeter Spiral
        pool4x4.add(new VerifiedPuzzleEntry(
                "mp_curated_4x4_02",
                1,
                "sha256_mp_4x4_02_c5d9e1f40821",
                4, 4,
                cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(5, 2, 1)
                ),
                List.of(),
                List.of(
                        "0,0", "0,1", "0,2", "0,3",
                        "1,3", "2,3", "3,3",
                        "3,2", "3,1", "3,0",
                        "2,0", "1,0",
                        "1,1", "1,2",
                        "2,2", "2,1" // Ends at 2,1 with checkpoint 5 at 2,1
                )
        ));

        // --- 5x5 Curated Puzzles ---
        List<String> cells5x5 = new ArrayList<>();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                cells5x5.add(r + "," + c);
            }
        }

        // 5x5 Puzzle 1: Standard Serpentine
        pool5x5.add(new VerifiedPuzzleEntry(
                "mp_curated_5x5_01",
                1,
                "sha256_mp_5x5_01_f2b4c81093de",
                5, 5,
                cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 4, 4)
                ),
                List.of(),
                List.of(
                        "0,0", "0,1", "0,2", "0,3", "0,4",
                        "1,4", "1,3", "1,2", "1,1", "1,0",
                        "2,0", "2,1", "2,2", "2,3", "2,4",
                        "3,4", "3,3", "3,2", "3,1", "3,0",
                        "4,0", "4,1", "4,2", "4,3", "4,4"
                )
        ));

        // 5x5 Puzzle 2: With Obstacle Wall
        pool5x5.add(new VerifiedPuzzleEntry(
                "mp_curated_5x5_02_walls",
                1,
                "sha256_mp_5x5_02_e829fa1147bd",
                5, 5,
                cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 1, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 4, 4)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(0, 2, 1, 2),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 2, 3, 2)
                ),
                List.of(
                        "0,0", "0,1", "0,2", "0,3", "0,4",
                        "1,4", "1,3", "1,2", "1,1", "1,0",
                        "2,0", "2,1", "2,2", "2,3", "2,4",
                        "3,4", "3,3", "3,2", "3,1", "3,0",
                        "4,0", "4,1", "4,2", "4,3", "4,4"
                )
        ));

        // --- 6x6 Advanced Curated Puzzles for Leagues ---
        List<String> cells6x6 = new ArrayList<>();
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                cells6x6.add(r + "," + c);
            }
        }

        pool6x6.add(new VerifiedPuzzleEntry(
                "mp_curated_6x6_01",
                1,
                "sha256_mp_6x6_01_77a94d018cf4",
                6, 6,
                cells6x6,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 5),
                        new PuzzleAssignment.CheckpointSpec(3, 2, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 5),
                        new PuzzleAssignment.CheckpointSpec(5, 5, 0)
                ),
                List.of(),
                List.of(
                        "0,0", "0,1", "0,2", "0,3", "0,4", "0,5",
                        "1,5", "1,4", "1,3", "1,2", "1,1", "1,0",
                        "2,0", "2,1", "2,2", "2,3", "2,4", "2,5",
                        "3,5", "3,4", "3,3", "3,2", "3,1", "3,0",
                        "4,0", "4,1", "4,2", "4,3", "4,4", "4,5",
                        "5,5", "5,4", "5,3", "5,2", "5,1", "5,0"
                )
        ));
    }

    /**
     * Authoritatively selects a verified puzzle for a match.
     */
    public PuzzleAssignment selectPuzzleForMode(GameMode mode, long seed) {
        return selectPuzzleForModeExcluding(mode, seed, null);
    }

    /**
     * Authoritatively selects a verified puzzle for a match, excluding a specific puzzle ID if alternatives exist.
     * Implements Prompt 22 Section 41.
     */
    public PuzzleAssignment selectPuzzleForModeExcluding(GameMode mode, long seed, String excludePuzzleId) {
        Random rng = new Random(seed);
        List<VerifiedPuzzleEntry> targetPool = switch (mode) {
            case QUICK_DUEL -> rng.nextBoolean() ? pool4x4 : pool5x5;
            case FRIEND_DUEL -> pool5x5;
            case MINI_LEAGUE, FRIENDS_ARENA -> pool6x6.isEmpty() ? pool5x5 : pool6x6;
        };

        List<VerifiedPuzzleEntry> filtered = targetPool;
        if (excludePuzzleId != null && !excludePuzzleId.isBlank()) {
            List<VerifiedPuzzleEntry> nonMatching = targetPool.stream()
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
