package com.zynpath.backend.multiplayer;

import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServerPuzzleValidatorTest {

    private ServerPuzzleValidator validator;

    private List<String> standardCells4x4;
    private PuzzleAssignment standardPuzzle4x4;
    private List<String> canonicalSolution4x4;

    @BeforeEach
    void setUp() {
        validator = new ServerPuzzleValidator();

        standardCells4x4 = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                standardCells4x4.add(r + "," + c);
            }
        }

        // 4x4 S-Curve Serpentine puzzle:
        // Row 0: (0,0)[CP1] -> (0,1) -> (0,2) -> (0,3)[CP2]
        // Row 1: (1,3) -> (1,2) -> (1,1) -> (1,0)[CP3]
        // Row 2: (2,0) -> (2,1) -> (2,2) -> (2,3)[CP4]
        // Row 3: (3,3) -> (3,2) -> (3,1) -> (3,0)[CP5]
        standardPuzzle4x4 = new PuzzleAssignment(
                "test_puzzle_4x4",
                1,
                "fingerprint_test_4x4",
                4, 4,
                standardCells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0)
                ),
                List.of()
        );

        canonicalSolution4x4 = List.of(
                "0,0", "0,1", "0,2", "0,3",
                "1,3", "1,2", "1,1", "1,0",
                "2,0", "2,1", "2,2", "2,3",
                "3,3", "3,2", "3,1", "3,0"
        );
    }

    @Test
    @DisplayName("Canonical solution validates successfully")
    void validateSolution_canonicalSolution_returnsSuccess() {
        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, canonicalSolution4x4);
        assertThat(outcome.isValid()).isTrue();
        assertThat(outcome.rejectionReason()).isNull();
    }

    @Test
    @DisplayName("Null puzzle returns failure")
    void validateSolution_nullPuzzle_fails() {
        ValidationOutcome outcome = validator.validateSolution(null, canonicalSolution4x4);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("PUZZLE_NULL");
    }

    @Test
    @DisplayName("Empty path returns failure")
    void validateSolution_emptyPath_fails() {
        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, List.of());
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("EMPTY_PATH");
    }

    @Test
    @DisplayName("Incomplete coverage returns INCOMPLETE_COVERAGE")
    void validateSolution_incompleteCoverage_fails() {
        List<String> partial = canonicalSolution4x4.subList(0, 10);
        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, partial);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("INCOMPLETE_COVERAGE");
    }

    @Test
    @DisplayName("Path starting at wrong cell returns INVALID_START")
    void validateSolution_wrongStart_fails() {
        // Create 16 cells starting at 0,1 instead of 0,0
        List<String> badStart = new ArrayList<>(canonicalSolution4x4);
        badStart.set(0, "0,1");
        badStart.set(1, "0,0");

        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, badStart);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("INVALID_START");
    }

    @Test
    @DisplayName("Non-orthogonal diagonal step returns NON_ORTHOGONAL_STEP")
    void validateSolution_diagonalStep_fails() {
        List<String> diagonalPath = new ArrayList<>(canonicalSolution4x4);
        // (0,0) -> (1,1) is diagonal!
        diagonalPath.set(1, "1,1");

        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, diagonalPath);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("NON_ORTHOGONAL_STEP");
    }

    @Test
    @DisplayName("Revisiting a cell (self-intersection) returns SELF_INTERSECTION")
    void validateSolution_revisitCell_fails() {
        // Path with duplicate cell
        List<String> loopPath = List.of(
                "0,0", "0,1", "0,2", "0,3",
                "1,3", "1,2", "0,2", "1,1", // 0,2 revisited
                "2,0", "2,1", "2,2", "2,3",
                "3,3", "3,2", "3,1", "3,0"
        );

        ValidationOutcome outcome = validator.validateSolution(standardPuzzle4x4, loopPath);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("SELF_INTERSECTION");
    }

    @Test
    @DisplayName("Crossing a wall/blocked edge returns WALL_COLLISION")
    void validateSolution_blockedEdge_fails() {
        // Add wall between (0,1) and (0,2)
        PuzzleAssignment puzzleWithWall = new PuzzleAssignment(
                "puzzle_wall",
                1,
                "fp_wall",
                4, 4,
                standardCells4x4,
                standardPuzzle4x4.checkpoints(),
                List.of(new PuzzleAssignment.BlockedEdgeSpec(0, 1, 0, 2))
        );

        ValidationOutcome outcome = validator.validateSolution(puzzleWithWall, canonicalSolution4x4);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("WALL_COLLISION");
    }

    @Test
    @DisplayName("Out-of-order checkpoints returns CHECKPOINT_ORDER_VIOLATION")
    void validateSolution_outOfOrderCheckpoints_fails() {
        // Swap CP 2 and CP 3 in puzzle definition so visiting order is violated
        PuzzleAssignment badOrderPuzzle = new PuzzleAssignment(
                "puzzle_bad_order",
                1,
                "fp_bad_order",
                4, 4,
                standardCells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(3, 0, 3), // reached before CP2!
                        new PuzzleAssignment.CheckpointSpec(2, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0)
                ),
                List.of()
        );

        ValidationOutcome outcome = validator.validateSolution(badOrderPuzzle, canonicalSolution4x4);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("CHECKPOINT_ORDER_VIOLATION");
    }

    @Test
    @DisplayName("Path not ending at max checkpoint returns INVALID_FINISH")
    void validateSolution_wrongFinish_fails() {
        // Define CP5 at (3,1) instead of (3,0)
        PuzzleAssignment wrongEndPuzzle = new PuzzleAssignment(
                "puzzle_wrong_end",
                1,
                "fp_wrong_end",
                4, 4,
                standardCells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 1) // Path ends at 3,0!
                ),
                List.of()
        );

        ValidationOutcome outcome = validator.validateSolution(wrongEndPuzzle, canonicalSolution4x4);
        assertThat(outcome.isValid()).isFalse();
        assertThat(outcome.rejectionReason()).contains("INVALID_FINISH");
    }
}
