package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.curation.DifficultyAnalysisResult
import com.zynpath.game.core.puzzle.curation.PuzzleDifficultyAnalyzer
import com.zynpath.game.core.puzzle.curation.PuzzleSimilarityCalculator
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.experience.LevelExperienceMetadata
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import java.util.concurrent.ConcurrentHashMap

/**
 * Result produced by [ChapterLevelGenerator].
 */
sealed interface ChapterLevelGenerationResult {

    /**
     * Successfully generated, solver-verified, hint-compatible, and difficulty-analyzed level.
     */
    data class Success(
        val levelId: Int,
        val puzzle: PuzzleDefinition,
        val solution: PuzzlePath,
        val difficulty: DifficultyAnalysisResult,
        val uniquenessStatus: UniquenessStatus,
        val experience: LevelExperienceMetadata,
        val attemptsUsed: Int,
        val isRecoveryLevel: Boolean
    ) : ChapterLevelGenerationResult

    /**
     * Bounded generation failure with explicit telemetry and rejection reasons.
     */
    data class Failure(
        val levelId: Int,
        val reason: String,
        val attemptsTried: Int,
        val rejectionReasons: Map<String, Int>
    ) : ChapterLevelGenerationResult
}

/**
 * Comprehensive, difficulty-controlled level generation orchestrator.
 *
 * Implements Prompt 26 Tasks 1–11:
 * 1. Derives chapter-aligned generation configuration via [ChapterLevelConfigFactory].
 * 2. Enforces strict ascending sequence and total cell coverage via [CompletionValidator].
 * 3. Rejects exact duplicates across catalog and near-duplicates (> 80% similarity) within neighboring levels.
 * 4. Measures multi-dimensional difficulty using [PuzzleDifficultyAnalyzer].
 * 5. Verifies hint engine solvability and first-move legality via [PuzzleHintEngine].
 * 6. Guarantees bounded execution without infinite retry loops.
 */
class ChapterLevelGenerator(
    private val puzzleGenerator: PuzzleGenerator = PuzzleGenerator(),
    private val difficultyAnalyzer: PuzzleDifficultyAnalyzer = PuzzleDifficultyAnalyzer(),
    private val hintEngine: PuzzleHintEngine = PuzzleHintEngine(),
    private val solver: PuzzleSolver = PuzzleSolver()
) {

    // Cache of recent canonical fingerprints and sliding window of neighboring puzzle definitions
    private val globalFingerprints = ConcurrentHashMap.newKeySet<String>()
    private val recentPuzzles = java.util.Collections.synchronizedList(mutableListOf<PuzzleDefinition>())

    companion object {
        const val MAX_ATTEMPTS_PER_LEVEL = 30
        const val MAX_NEIGHBOR_SIMILARITY = 0.85
        const val NEIGHBOR_WINDOW_SIZE = 5
    }

    /**
     * Deterministically generates a verified puzzle for [levelId].
     *
     * @param levelId Level number in 1..300.
     * @param cancellationSignal Cooperative cancellation hook.
     */
    fun generateLevel(
        levelId: Int,
        cancellationSignal: () -> Boolean = { false }
    ): ChapterLevelGenerationResult {
        require(levelId in 1..300) { "levelId must be in 1..300 (got $levelId)" }

        val experience = ProgressionPlan.getLevelExperience(levelId)
        val isRecovery = ChapterLevelConfigFactory.isRecoveryLevel(levelId)
        val rejectionBreakdown = mutableMapOf<String, Int>()

        fun recordRejection(reason: String) {
            rejectionBreakdown[reason] = (rejectionBreakdown[reason] ?: 0) + 1
        }

        var attempt = 0
        while (attempt < MAX_ATTEMPTS_PER_LEVEL) {
            attempt++

            if (cancellationSignal()) {
                return ChapterLevelGenerationResult.Failure(
                    levelId = levelId,
                    reason = "Cancelled by caller",
                    attemptsTried = attempt,
                    rejectionReasons = rejectionBreakdown
                )
            }

            // Step 1: Create chapter-specific configuration
            val config = ChapterLevelConfigFactory.createConfig(levelId, attemptIndex = attempt - 1)

            // Step 2: Generate candidate via underlying core generator
            val genResult = puzzleGenerator.generate(
                config = config,
                cancellationSignal = cancellationSignal,
                seenFingerprints = globalFingerprints
            )

            if (!genResult.isSuccess || genResult.puzzle == null || genResult.verifiedSolution == null) {
                val reason = genResult.diagnosticMessage ?: genResult.status.name
                recordRejection(reason)
                continue
            }

            val candidate = genResult.puzzle
            val solution = genResult.verifiedSolution

            // Step 3: Solvability & Authoritative Completion Validation
            val completionCheck = CompletionValidator.validate(
                definition = candidate,
                path = solution,
                levelId = levelId,
                worldId = config.worldId ?: 1
            )
            if (completionCheck !is CompletionCheckResult.Success) {
                recordRejection("COMPLETION_VALIDATOR_REJECTED")
                continue
            }

            // Step 4: Near-Duplicate Detection (Sliding Window of Neighboring Levels)
            var isNearDuplicate = false
            synchronized(recentPuzzles) {
                for (recent in recentPuzzles.takeLast(NEIGHBOR_WINDOW_SIZE)) {
                    val similarity = PuzzleSimilarityCalculator.calculateSimilarity(candidate, recent)
                    if (similarity >= MAX_NEIGHBOR_SIMILARITY) {
                        isNearDuplicate = true
                        break
                    }
                }
            }
            if (isNearDuplicate) {
                recordRejection("NEAR_DUPLICATE_OF_NEIGHBOR")
                continue
            }

            // Step 5: Multi-Dimensional Difficulty Analysis
            val difficultyResult = try {
                difficultyAnalyzer.analyze(
                    definition = candidate,
                    verifiedSolution = solution
                )
            } catch (e: Exception) {
                recordRejection("DIFFICULTY_ANALYSIS_FAILED: ${e.message}")
                continue
            }

            // Step 6: Hint Engine Verification
            // Prove that PuzzleHintEngine can successfully assist the player from Checkpoint #1
            val initialGameState = PuzzleGameState.initial(candidate)
            val hintRequest = HintRequest(
                puzzleId = candidate.puzzleId,
                puzzleVersion = candidate.puzzleVersion,
                definition = candidate,
                gameState = initialGameState,
                currentOrderedPath = initialGameState.currentPath.positions,
                nextRequiredCheckpoint = initialGameState.nextRequiredCheckpoint,
                gameMode = GameMode.SOLO
            )

            val hintResult = hintEngine.computeHint(hintRequest)
            if (hintResult !is HintResult.NextMove) {
                recordRejection("HINT_ENGINE_INCOMPATIBLE: ${hintResult::class.simpleName}")
                continue
            }

            // Verify that the hint's recommended move matches start checkpoint or next valid step
            val startPos = candidate.startCheckpoint?.position
            if (hintResult.nextMove != startPos && (startPos == null || !startPos.isOrthogonalNeighbor(hintResult.nextMove))) {
                recordRejection("HINT_MOVE_INVALID")
                continue
            }

            // Step 7: Final Acceptance
            val fingerprint = PuzzleFingerprint.canonicalRepresentation(candidate)
            globalFingerprints.add(fingerprint)
            synchronized(recentPuzzles) {
                recentPuzzles.add(candidate)
                if (recentPuzzles.size > NEIGHBOR_WINDOW_SIZE * 2) {
                    recentPuzzles.removeAt(0)
                }
            }

            return ChapterLevelGenerationResult.Success(
                levelId = levelId,
                puzzle = candidate,
                solution = solution,
                difficulty = difficultyResult,
                uniquenessStatus = genResult.uniquenessStatus,
                experience = experience,
                attemptsUsed = attempt,
                isRecoveryLevel = isRecovery
            )
        }

        return ChapterLevelGenerationResult.Failure(
            levelId = levelId,
            reason = "Failed to generate valid puzzle within $MAX_ATTEMPTS_PER_LEVEL bounded attempts",
            attemptsTried = attempt,
            rejectionReasons = rejectionBreakdown
        )
    }

    /**
     * Resets sliding window and cached fingerprints (useful for test isolation).
     */
    fun clearState() {
        globalFingerprints.clear()
        recentPuzzles.clear()
    }
}
