package com.zynpath.game.core.puzzle.curation

/**
 * Explicit, structured rejection reasons for candidate puzzle evaluation during generation and curation.
 *
 * Implements Prompt 2A Section 32:
 * Replaces generic "failed" messages with precise, diagnostic causes:
 * - [NO_SOLUTION]: Puzzle has no valid full-coverage Hamiltonian solution.
 * - [MULTIPLE_WHEN_UNIQUE_REQUIRED]: Puzzle has multiple valid solutions when unique solution was required.
 * - [SEARCH_LIMIT]: Solver or generator search limit reached before uniqueness could be proved.
 * - [TOO_TRIVIAL]: Puzzle is easily solved by naive heuristics or has insufficient lookahead depth.
 * - [TOO_FORCED]: Puzzle has excessive forced moves with insufficient meaningful decision points.
 * - [LOW_DEPENDENCY]: Future-cell dependency score or distance is below the required world threshold.
 * - [LOW_RESERVATION]: Cell or corridor reservation requirements are absent or below threshold.
 * - [LOW_DECISION_DEPTH]: Chained decision depth or meaningful decision points below target threshold.
 * - [REPETITIVE]: Candidate is a topological duplicate or has excessive similarity to existing levels.
 * - [INVALID_CHECKPOINTS]: Checkpoint placement is out of range, out of order, or violates world constraints.
 * - [INVALID_TOPOLOGY]: Planar graph topology, boundary, or wall constraints are violated.
 */
enum class GenerationRejectionReason(val description: String) {
    NO_SOLUTION("No valid full-coverage Hamiltonian solution exists"),
    MULTIPLE_WHEN_UNIQUE_REQUIRED("Multiple valid solutions exist when unique solution was required"),
    SEARCH_LIMIT("Solver or generator search limit reached before uniqueness could be proved"),
    TOO_TRIVIAL("Puzzle is easily solved by naive heuristics or has insufficient lookahead depth"),
    TOO_FORCED("Puzzle has excessive forced moves with insufficient meaningful decision points"),
    LOW_DEPENDENCY("Future-cell dependency score or distance is below the required world threshold"),
    LOW_RESERVATION("Cell or corridor reservation requirements are absent or below threshold"),
    LOW_DECISION_DEPTH("Chained decision depth or meaningful decision points below target threshold"),
    REPETITIVE("Candidate is a topological duplicate or has excessive similarity to existing levels"),
    INVALID_CHECKPOINTS("Checkpoint placement is out of range, out of order, or violates world constraints"),
    INVALID_TOPOLOGY("Planar graph topology, boundary, or wall constraints are violated")
}
