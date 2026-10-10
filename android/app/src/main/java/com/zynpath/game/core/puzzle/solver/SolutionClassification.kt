package com.zynpath.game.core.puzzle.solver

/**
 * Authoritative classification of solution count for a puzzle definition.
 *
 * Implements Master Redesign Section 11:
 * - [NONE]: Exhaustive search proved zero valid solutions exist.
 * - [UNIQUE]: Exhaustive search proved exactly one valid full-coverage solution exists.
 * - [MULTIPLE]: Search proved two or more distinct valid solutions exist.
 * - [UNKNOWN_LIMIT_REACHED]: Search terminated due to node/time limit or was non-exhaustive before uniqueness could be proved.
 *
 * Rule: Never claim UNIQUE without exhaustive proof.
 */
enum class SolutionClassification {
    NONE,
    UNIQUE,
    MULTIPLE,
    UNKNOWN_LIMIT_REACHED
}
