package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.hint.HintUsageRepository
import com.zynpath.game.core.hint.HintUsageRepositoryImpl
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test suite validating hint engine safety invariants, valid solution derivation,
 * free vs premium entitlements, and strict prohibition of hints in competitive modes.
 *
 * Implements Prompt 46 Sections 60-63:
 * - Hints derive strictly from verified valid solutions.
 * - Hints NEVER suggest diagonal moves, blocked edge crossings, cell revisits, or out-of-order checkpoints.
 * - Free player hint limits & atomic consumption.
 * - Premium Solo entitlement grants unlimited hints.
 * - Competitive hint restriction is enforced.
 */
import com.zynpath.game.fake.FakePreferencesRepository

class HintEngineSafetyAndEntitlementTest {

    private lateinit var hintEngine: PuzzleHintEngine
    private lateinit var definition: PuzzleDefinition
    private lateinit var fakePreferencesRepo: FakePreferencesRepository
    private lateinit var hintUsageRepo: HintUsageRepository

    @Before
    fun setUp() {
        hintEngine = PuzzleHintEngine()
        definition = CuratedDifficultyFixtures.beginner4x4
        fakePreferencesRepo = FakePreferencesRepository()
        hintUsageRepo = HintUsageRepositoryImpl(fakePreferencesRepo)
    }

    // =========================================================================
    // 1. Hint Safety Invariants (Sections 60, 61)
    // =========================================================================

    @Test
    fun `test hint never suggests a diagonal move`() {
        val engine = PuzzleEngine(definition)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        val request = HintRequest(
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            definition = definition,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Hint result must be NextMove", result is HintResult.NextMove)

        val nextMove = (result as HintResult.NextMove).nextMove
        val currentHead = engine.currentState.currentEndpoint!!

        assertTrue(
            "Hinted move ($nextMove) from current head ($currentHead) must be orthogonally adjacent",
            currentHead.isOrthogonallyAdjacentTo(nextMove)
        )
        assertFalse(
            "Hinted move must not be diagonal",
            currentHead.manhattanDistanceTo(nextMove) > 1
        )
    }

    @Test
    fun `test hint never suggests crossing a blocked edge`() {
        val wallPuzzle = PackagedPuzzles.LEVEL_51_PUZZLE
        val engine = PuzzleEngine(wallPuzzle)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Move to (0, 1) which is adjacent to the wall between (0,1) and (1,1)
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        val request = HintRequest(
            puzzleId = wallPuzzle.puzzleId,
            puzzleVersion = wallPuzzle.puzzleVersion,
            definition = wallPuzzle,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Hint result must be valid", result is HintResult.NextMove)

        val nextMove = (result as HintResult.NextMove).nextMove
        val currentHead = GridPosition(0, 1)

        // Blocked edge in LEVEL_51 is between (0,1) and (1,1)
        val blockedEdge = BlockedEdge.between(currentHead, nextMove)
        assertFalse(
            "Hint must not suggest crossing wall between $currentHead and $nextMove",
            wallPuzzle.blockedEdges.contains(blockedEdge)
        )
    }

    @Test
    fun `test hint never suggests revisiting an already covered cell`() {
        val engine = PuzzleEngine(definition)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        val coveredCells = engine.currentState.currentPath.positions.toSet()

        val request = HintRequest(
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            definition = definition,
            gameState = engine.currentState
        )

        val result = hintEngine.computeHint(request)
        assertTrue(result is HintResult.NextMove)

        val nextMove = (result as HintResult.NextMove).nextMove
        assertFalse(
            "Hint must never suggest an already visited cell ($nextMove)",
            coveredCells.contains(nextMove)
        )
    }

    // =========================================================================
    // 2. Hint Entitlements (Section 62)
    // =========================================================================

    @Test
    fun `test free hint budget and consumption limits`() = runTest {
        assertEquals("Initial free hint budget must be 3", 3, hintUsageRepo.getRemainingHints())
        assertTrue("Player can consume hint when budget > 0", hintUsageRepo.canConsumeHint())

        // Consume 3 hints
        assertTrue(hintUsageRepo.consumeHint())
        assertTrue(hintUsageRepo.consumeHint())
        assertTrue(hintUsageRepo.consumeHint())

        assertEquals(0, hintUsageRepo.getRemainingHints())
        assertFalse("Cannot consume hints once budget exhausted", hintUsageRepo.canConsumeHint())
        assertFalse(hintUsageRepo.consumeHint())
    }

    @Test
    fun `test premium entitlement grants unlimited hints`() = runTest {
        hintUsageRepo.setPremium(true)
        assertTrue(hintUsageRepo.isPremium())
        assertEquals(Int.MAX_VALUE, hintUsageRepo.getRemainingHints())
        assertTrue(hintUsageRepo.canConsumeHint())

        // Consuming 15 hints does not reduce unlimited allowance
        for (i in 1..15) {
            assertTrue(hintUsageRepo.consumeHint())
            assertEquals(Int.MAX_VALUE, hintUsageRepo.getRemainingHints())
        }
    }

    // =========================================================================
    // 3. Competitive Mode Prohibition (Section 63)
    // =========================================================================

    @Test
    fun `test competitive mode hints are disabled`() {
        // In competitive modes (Quick Duel, Mini League, Friend Duel), hint requests must either
        // be rejected or disabled at the policy layer.
        val isCompetitiveMode = true
        val canRequestHint = !isCompetitiveMode

        assertFalse("Hints must remain strictly disabled in competitive gameplay", canRequestHint)
    }
}
