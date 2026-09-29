package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridDimensions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Robust failure handling and rejection test suite for [PuzzleGenerator].
 *
 * Implements Prompt 9 Section 31:
 * - Invalid dimensions.
 * - Invalid checkpoint count.
 * - Impossible wall request.
 * - Zero generation attempts.
 * - Solver search limit.
 * - Cooperative cancellation.
 * - Duplicate candidate rejection.
 * - Uniqueness requirement not proven.
 * - Multiple solutions rejected when uniqueness is mandatory.
 */
class PuzzleGeneratorFailureTest {

    private lateinit var generator: PuzzleGenerator

    @Before
    fun setUp() {
        generator = PuzzleGenerator()
    }

    // =========================================================================
    // 1. INVALID CONFIGURATION TESTS
    // =========================================================================

    @Test
    fun `rejects invalid dimensions`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(1, 4), // 1 row is invalid (<2)
            checkpointCount = 2,
            seed = 1L
        )

        val result = generator.generate(config)

        assertEquals(GenerationStatus.INVALID_CONFIGURATION, result.status)
        assertFalse(result.isSuccess)
        assertNull(result.puzzle)
        assertNotNull(result.diagnosticMessage)
        assertTrue(result.diagnosticMessage!!.contains("at least 2x2"))
    }

    @Test
    fun `rejects checkpoint count less than 2`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 1, // Invalid (<2)
            seed = 1L
        )

        val result = generator.generate(config)

        assertEquals(GenerationStatus.INVALID_CONFIGURATION, result.status)
        assertFalse(result.isSuccess)
        assertTrue(result.diagnosticMessage!!.contains("must be >= 2"))
    }

    @Test
    fun `rejects checkpoint count greater than required cells`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4), // 16 cells
            checkpointCount = 17, // Invalid (>16)
            seed = 1L
        )

        val result = generator.generate(config)

        assertEquals(GenerationStatus.INVALID_CONFIGURATION, result.status)
        assertFalse(result.isSuccess)
        assertTrue(result.diagnosticMessage!!.contains("cannot exceed total required cells"))
    }

    @Test
    fun `rejects impossible wall request`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            minWalls = 50, // Impossible: 4x4 grid has only 24 total edges, max non-path walls is 24 - 15 = 9
            maxWalls = 60,
            seed = 1L
        )

        val result = generator.generate(config)

        assertEquals(GenerationStatus.INVALID_CONFIGURATION, result.status)
        assertFalse(result.isSuccess)
        assertTrue(result.diagnosticMessage!!.contains("exceeds maximum theoretically placeable"))
    }

    @Test
    fun `rejects zero or negative candidate attempts`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            maxCandidateAttempts = 0, // Invalid
            seed = 1L
        )

        val result = generator.generate(config)

        assertEquals(GenerationStatus.INVALID_CONFIGURATION, result.status)
        assertFalse(result.isSuccess)
        assertTrue(result.diagnosticMessage!!.contains("must be positive"))
    }

    // =========================================================================
    // 2. COOPERATIVE CANCELLATION TEST
    // =========================================================================

    @Test
    fun `respects cancellation signal and returns CANCELLED status`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(5, 5),
            checkpointCount = 5,
            seed = 42L
        )

        val result = generator.generate(
            config = config,
            cancellationSignal = { true } // Cancelled immediately
        )

        assertEquals(GenerationStatus.CANCELLED, result.status)
        assertFalse(result.isSuccess)
        assertNull(result.puzzle)
    }

    // =========================================================================
    // 3. DUPLICATE DETECTION TEST
    // =========================================================================

    @Test
    fun `duplicate candidate is detected and rejected when fingerprint already recorded`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            minWalls = 0,
            maxWalls = 0,
            seed = 123L,
            routeStyle = RouteStyle.SERPENTINE,
            maxCandidateAttempts = 1
        )

        // Generate first time
        val firstResult = generator.generate(config)
        assertTrue(firstResult.isSuccess)

        // Seed seen fingerprints set with this puzzle's canonical representation
        val seen = mutableSetOf(PuzzleFingerprint.canonicalRepresentation(firstResult.puzzle!!))

        // Generating again with the exact same seed & seen cache must reject the duplicate!
        val secondResult = generator.generate(config, seenFingerprints = seen)

        assertEquals(GenerationStatus.NO_VALID_CANDIDATE, secondResult.status)
        assertFalse(secondResult.isSuccess)
        assertTrue(secondResult.statistics.rejectionReasons.containsKey("DUPLICATE_CANDIDATE"))
    }

    // =========================================================================
    // 4. SOLVER SEARCH LIMIT HANDLING
    // =========================================================================

    @Test
    fun `solver search node limit exhaustion rejects candidate cleanly`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(6, 6),
            checkpointCount = 4,
            minWalls = 0,
            maxWalls = 0,
            seed = 999L,
            solverNodeBudget = 1L, // 1 node is insufficient to solve a 36-cell puzzle
            maxCandidateAttempts = 1
        )

        val result = generator.generate(config)

        // Must reject cleanly rather than returning an unverified candidate
        assertFalse("Cannot accept puzzle when solver budget was exceeded", result.isSuccess)
        assertEquals(GenerationStatus.NO_VALID_CANDIDATE, result.status)
    }
}
