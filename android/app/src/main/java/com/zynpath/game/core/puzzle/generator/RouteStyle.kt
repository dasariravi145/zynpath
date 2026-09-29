package com.zynpath.game.core.puzzle.generator

/**
 * Geometric route layout preference for initial Hamiltonian path construction.
 *
 * Adheres to Prompt 9 Sections 7, 9, and 10:
 * - [MIXED]: Balanced combination of turns, straights, and seeded variations.
 * - [SERPENTINE]: Standard alternating sweep pattern (horizontal or vertical sweeps).
 * - [ZIGZAG]: High turn-frequency alternating pattern.
 * - [SPIRAL_LIKE]: Concentric inward/outward perimeter-hugging traversal where topology permits.
 * - [RANDOM_WALK]: Warnsdorff-heuristic self-avoiding random walk with deterministic seeded branch exploration.
 */
enum class RouteStyle {
    /**
     * Balanced blend of straight segments and directional turns.
     */
    MIXED,

    /**
     * Alternating orthogonal sweeps across the grid rows or columns.
     */
    SERPENTINE,

    /**
     * High-frequency directional alternating zigzag pattern.
     */
    ZIGZAG,

    /**
     * Concentric perimeter or spiral traversal towards the grid interior where valid.
     */
    SPIRAL_LIKE,

    /**
     * Pure Warnsdorff-guided self-avoiding random walk for maximum organic route variation.
     */
    RANDOM_WALK
}
