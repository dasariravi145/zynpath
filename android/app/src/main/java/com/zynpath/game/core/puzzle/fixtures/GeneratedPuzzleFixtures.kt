package com.zynpath.game.core.puzzle.fixtures

import com.zynpath.game.core.puzzle.generator.GenerationConfiguration
import com.zynpath.game.core.puzzle.generator.GenerationResult
import com.zynpath.game.core.puzzle.generator.PuzzleGenerator
import com.zynpath.game.core.puzzle.generator.RouteStyle
import com.zynpath.game.core.puzzle.model.GridDimensions

/**
 * Authoritative deterministic generated fixtures representing Worlds 1 through 6.
 *
 * Implements Prompt 9 Section 29:
 * Provides deterministic generated fixtures with independently validated complete solutions for:
 * - 4x4 without walls
 * - 5x5 without walls
 * - 5x5 with walls
 * - 6x6 with walls
 * - 7x7 with walls
 * - 8x8 with walls
 */
object GeneratedPuzzleFixtures {

    private val generator = PuzzleGenerator()

    // 1. 4x4 without walls (World 1)
    val config4x4Clean: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(4, 4),
        checkpointCount = 4,
        minWalls = 0,
        maxWalls = 0,
        seed = 1001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 1,
        levelId = 1
    )
    val fixture4x4Clean: GenerationResult by lazy {
        generator.generate(config4x4Clean)
    }

    // 2. 5x5 without walls (World 2)
    val config5x5Clean: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(5, 5),
        checkpointCount = 5,
        minWalls = 0,
        maxWalls = 0,
        seed = 2001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 2,
        levelId = 25
    )
    val fixture5x5Clean: GenerationResult by lazy {
        generator.generate(config5x5Clean)
    }

    // 3. 5x5 with walls (World 3)
    val config5x5WithWalls: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(5, 5),
        checkpointCount = 5,
        minWalls = 3,
        maxWalls = 3,
        seed = 3001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 3,
        levelId = 75
    )
    val fixture5x5WithWalls: GenerationResult by lazy {
        generator.generate(config5x5WithWalls)
    }

    // 4. 6x6 with walls (World 4)
    val config6x6WithWalls: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(6, 6),
        checkpointCount = 6,
        minWalls = 4,
        maxWalls = 4,
        seed = 4001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 4,
        levelId = 125
    )
    val fixture6x6WithWalls: GenerationResult by lazy {
        generator.generate(config6x6WithWalls)
    }

    // 5. 7x7 with walls (World 5)
    val config7x7WithWalls: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(7, 7),
        checkpointCount = 7,
        minWalls = 6,
        maxWalls = 6,
        seed = 5001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 5,
        levelId = 175
    )
    val fixture7x7WithWalls: GenerationResult by lazy {
        generator.generate(config7x7WithWalls)
    }

    // 6. 8x8 with walls (World 6)
    val config8x8WithWalls: GenerationConfiguration = GenerationConfiguration(
        dimensions = GridDimensions(8, 8),
        checkpointCount = 8,
        minWalls = 8,
        maxWalls = 8,
        seed = 6001L,
        routeStyle = RouteStyle.SERPENTINE,
        worldId = 6,
        levelId = 250
    )
    val fixture8x8WithWalls: GenerationResult by lazy {
        generator.generate(config8x8WithWalls)
    }
}
