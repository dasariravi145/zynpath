package com.zynpath.backend.multiplayer.puzzle;

import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Authoritative server-side path and solution validator for multiplayer competition.
 *
 * Implements Prompt 20 Sections 6 & 36:
 * - Complete dual-win condition verification:
 *   1. Starts at Checkpoint 1.
 *   2. Strict orthogonal adjacency.
 *   3. Zero blocked-edge crossing.
 *   4. Zero cell revisitation (no self-intersection).
 *   5. Full 100% required cell coverage.
 *   6. Checkpoints encountered in strictly ascending sequence (1 -> 2 -> ... -> N).
 *   7. Ends at the highest-numbered checkpoint.
 */
@Component
public class ServerPuzzleValidator {

    private static final Logger log = LoggerFactory.getLogger(ServerPuzzleValidator.class);

    public ValidationOutcome validateSolution(PuzzleAssignment puzzle, List<String> pathCoordinates) {
        if (puzzle == null) {
            return ValidationOutcome.failure("PUZZLE_NULL: Authoritative puzzle assignment is missing");
        }

        if (pathCoordinates == null || pathCoordinates.isEmpty()) {
            return ValidationOutcome.failure("EMPTY_PATH: Submitted path is empty");
        }

        // 1. Full cell coverage check
        int requiredCount = puzzle.requiredCells().size();
        if (pathCoordinates.size() != requiredCount) {
            return ValidationOutcome.failure("INCOMPLETE_COVERAGE: Path covers " + pathCoordinates.size() +
                    " cells, but puzzle requires " + requiredCount + " cells");
        }

        // Map checkpoints: "r,c" -> checkpointNumber
        Map<String, Integer> checkpointMap = new HashMap<>();
        int maxCheckpointNumber = 0;
        PuzzleAssignment.CheckpointSpec checkpoint1 = null;

        for (PuzzleAssignment.CheckpointSpec cp : puzzle.checkpoints()) {
            String coord = cp.row() + "," + cp.col();
            checkpointMap.put(coord, cp.number());
            if (cp.number() > maxCheckpointNumber) {
                maxCheckpointNumber = cp.number();
            }
            if (cp.number() == 1) {
                checkpoint1 = cp;
            }
        }

        if (checkpoint1 == null) {
            return ValidationOutcome.failure("INVALID_PUZZLE: Checkpoint 1 is missing in puzzle definition");
        }

        // 2. Start cell must be Checkpoint 1
        String firstCoord = pathCoordinates.get(0);
        String expectedStart = checkpoint1.row() + "," + checkpoint1.col();
        if (!firstCoord.equals(expectedStart)) {
            return ValidationOutcome.failure("INVALID_START: Path must start at Checkpoint 1 (" +
                    expectedStart + "), but started at (" + firstCoord + ")");
        }

        Set<String> visited = new HashSet<>();
        Set<String> requiredSet = new HashSet<>(puzzle.requiredCells());

        int expectedNextCheckpoint = 2; // Checkpoint 1 is at index 0
        int prevRow = checkpoint1.row();
        int prevCol = checkpoint1.col();
        visited.add(firstCoord);

        for (int i = 1; i < pathCoordinates.size(); i++) {
            String currCoord = pathCoordinates.get(i);

            // Parse coordinate "row,col"
            String[] parts = currCoord.split(",");
            if (parts.length != 2) {
                return ValidationOutcome.failure("MALFORMED_COORDINATE: Invalid coordinate syntax: " + currCoord);
            }

            int currRow;
            int currCol;
            try {
                currRow = Integer.parseInt(parts[0].trim());
                currCol = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                return ValidationOutcome.failure("MALFORMED_COORDINATE: Non-integer coordinates in: " + currCoord);
            }

            // Boundary and required cell check
            if (!requiredSet.contains(currCoord)) {
                return ValidationOutcome.failure("OUT_OF_BOUNDS: Coordinate (" + currCoord + ") is outside required grid");
            }

            // 3. Orthogonal step check
            int dRow = Math.abs(currRow - prevRow);
            int dCol = Math.abs(currCol - prevCol);
            if (dRow + dCol != 1) {
                return ValidationOutcome.failure("NON_ORTHOGONAL_STEP: Step from (" + prevRow + "," + prevCol +
                        ") to (" + currRow + "," + currCol + ") is not strictly orthogonal");
            }

            // 4. Blocked edge (wall) collision check
            for (PuzzleAssignment.BlockedEdgeSpec wall : puzzle.blockedEdges()) {
                if (wall.matches(prevRow, prevCol, currRow, currCol)) {
                    return ValidationOutcome.failure("WALL_COLLISION: Step from (" + prevRow + "," + prevCol +
                            ") to (" + currRow + "," + currCol + ") crosses a blocked edge");
                }
            }

            // 5. No revisiting (self-intersection)
            if (!visited.add(currCoord)) {
                return ValidationOutcome.failure("SELF_INTERSECTION: Cell (" + currCoord + ") revisited at step " + i);
            }

            // 6. Checkpoint order validation
            Integer cpNumber = checkpointMap.get(currCoord);
            if (cpNumber != null) {
                if (cpNumber != expectedNextCheckpoint) {
                    return ValidationOutcome.failure("CHECKPOINT_ORDER_VIOLATION: Expected Checkpoint #" +
                            expectedNextCheckpoint + ", but encountered Checkpoint #" + cpNumber + " at (" + currCoord + ")");
                }
                expectedNextCheckpoint++;
            }

            prevRow = currRow;
            prevCol = currCol;
        }

        // 7. Verify all checkpoints reached
        if (expectedNextCheckpoint <= maxCheckpointNumber) {
            return ValidationOutcome.failure("SKIPPED_CHECKPOINTS: Only visited checkpoints up to #" +
                    (expectedNextCheckpoint - 1) + " of " + maxCheckpointNumber);
        }

        // 8. End cell must be the maximum numbered checkpoint
        Integer lastCellCp = checkpointMap.get(pathCoordinates.get(pathCoordinates.size() - 1));
        if (lastCellCp == null || lastCellCp != maxCheckpointNumber) {
            return ValidationOutcome.failure("INVALID_FINISH: Path must terminate at final Checkpoint #" + maxCheckpointNumber);
        }

        log.info("Authoritative solution successfully verified for puzzleId={}", puzzle.puzzleId());
        return ValidationOutcome.success();
    }
}
