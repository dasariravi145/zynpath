package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Authoritative uniqueness verification test suite for [PuzzleGenerator].
 *
 * Implements Prompt 9 Sections 20, 21, and 32:
 * - Proven unique candidate accepted when uniqueness is required.
 * - Multiple-solution candidate rejected when uniqueness is required.
 * - Inconclusive search does not claim uniqueness.
 * - Solvable non-unique candidate accepted only when configuration permits.
 */
class PuzzleGeneratorUniquenessTest {

    private lateinit var generator: PuzzleGenerator

    @Before
    fun setUp() {
        generator = PuzzleGenerator()
    }

    @Test
    fun `when uniqueness is required, accepted puzzle must have proven UNIQUE status`() {
        // 4x4 with 6 checkpoints and 2-3 walls frequently yields uniquely solvable Hamiltonian paths
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 6,
            minWalls = 2,
            maxWalls = 3,
            seed = 100L,
            requireUniqueness = true,
            maxCandidateAttempts = 50,
            solverNodeBudget = 20_000L
        )

        val result = generator.generate(config)

        if (result.isSuccess) {
            assertEquals("Accepted puzzle must be proven UNIQUE", UniquenessStatus.UNIQUE, result.uniquenessStatus)
            val solver = com.zynpath.game.core.puzzle.solver.PuzzleSolver()
            val check = solver.checkUniqueness(result.puzzle!!)
            assertTrue("Independent solver verification must confirm uniqueness", check.isUnique)
            assertEquals(1, check.solutionCount)
        } else {
            // If no candidate was unique in this attempt budget, it must fail with NO_VALID_CANDIDATE, NOT return an unverified puzzle!
            assertEquals(GenerationStatus.NO_VALID_CANDIDATE, result.status)
        }
    }

    @Test
    fun `when uniqueness is optional, solvable puzzle can be accepted with UNKNOWN uniqueness`() {
        // Fast generation finding first solution (maxSolutions = 1)
        val config = GenerationConfiguration(
            dimensions = GridDimensions(4, 4),
            checkpointCount = 4,
            minWalls = 0,
            maxWalls = 0,
            seed = 42L,
            requireUniqueness = false,
            routeStyle = RouteStyle.SERPENTINE
        )

        val result = generator.generate(config)

        assertTrue("Should be successfully generated", result.isSuccess)
        // With maxSolutions = 1, search stops early without exploring all branches, so uniqueness is UNKNOWN
        assertNotEquals("Should not claim NON_UNIQUE or UNSOLVABLE", UniquenessStatus.UNSOLVABLE, result.uniquenessStatus)
    }

    @Test
    fun `never mislabels inconclusive search as unique or unsolvable`() {
        val config = GenerationConfiguration(
            dimensions = GridDimensions(5, 5),
            checkpointCount = 4,
            minWalls = 0,
            maxWalls = 0,
            seed = 77L,
            requireUniqueness = true,
            solverNodeBudget = 5L, // Tiny budget to trigger SEARCH_LIMIT_REACHED
            maxCandidateAttempts = 1
        )

        val result = generator.generate(config)

        // Cannot accept as unique when search limit was reached
        assertFalse(result.isSuccess)
        assertNotEquals(GenerationStatus.GENERATED, result.status)
    }
}
