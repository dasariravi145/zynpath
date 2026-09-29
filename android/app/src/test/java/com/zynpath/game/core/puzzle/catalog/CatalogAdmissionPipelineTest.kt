package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CatalogAdmissionPipeline].
 *
 * Implements Prompt 11 Section 13 and Section 33:
 * Verifies all 10 admission gates for published puzzles and confirms robust rejection
 * of invalid, unsolvable, or rule-violating candidates.
 */
class CatalogAdmissionPipelineTest {

    @Test
    fun `test all packaged puzzles pass complete 10-step admission pipeline`() {
        for ((levelId, definition) in PackagedPuzzles.ALL_PACKAGED) {
            val world = WorldDefinition.forLevel(levelId)
            val result = CatalogAdmissionPipeline.admit(
                levelId = levelId,
                worldId = world.worldId,
                candidate = definition,
                expectedUniqueness = UniquenessStatus.UNIQUE
            )

            assertTrue(
                "Level $levelId must pass admission pipeline, but was rejected: ${(result as? AdmissionResult.Rejected)?.reason}",
                result is AdmissionResult.Admitted
            )

            val admitted = result as AdmissionResult.Admitted
            assertEquals(levelId, admitted.levelId)
            assertEquals(world.worldId, admitted.worldId)
            assertEquals(UniquenessStatus.UNIQUE, admitted.uniquenessStatus)
            assertTrue(admitted.json.isNotBlank())
            assertTrue(admitted.fingerprint.isNotBlank())
        }
    }

    @Test
    fun `test admission pipeline rejects candidate with wall in World 1`() {
        val invalidCandidate = PackagedPuzzles.LEVEL_1.copy(
            blockedEdges = setOf(
                BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1))
            )
        )
        val result = CatalogAdmissionPipeline.admit(
            levelId = 1,
            worldId = 1,
            candidate = invalidCandidate
        )

        assertTrue(result is AdmissionResult.Rejected)
        val rejected = result as AdmissionResult.Rejected
        assertTrue(rejected.reason.contains("Wall count"))
    }

    @Test
    fun `test admission pipeline rejects candidate with insufficient checkpoints`() {
        // World 1 requires 4..6 checkpoints, provide 2
        val invalidCandidate = PackagedPuzzles.LEVEL_1.copy(
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 0))
            )
        )
        val result = CatalogAdmissionPipeline.admit(
            levelId = 1,
            worldId = 1,
            candidate = invalidCandidate
        )

        assertTrue(result is AdmissionResult.Rejected)
        val rejected = result as AdmissionResult.Rejected
        assertTrue(rejected.reason.contains("Checkpoint count"))
    }

    @Test
    fun `test admission pipeline rejects candidate with wrong grid size`() {
        // World 1 requires 4x4, provide 5x5
        val result = CatalogAdmissionPipeline.admit(
            levelId = 1,
            worldId = 1,
            candidate = PackagedPuzzles.LEVEL_21 // 5x5
        )

        assertTrue(result is AdmissionResult.Rejected)
        val rejected = result as AdmissionResult.Rejected
        assertTrue(rejected.reason.contains("Grid dimension mismatch"))
    }
}
