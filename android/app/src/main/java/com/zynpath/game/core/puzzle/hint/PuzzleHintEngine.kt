package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.validator.PartialPathValidationResult
import com.zynpath.game.core.puzzle.validator.PartialPathValidator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * Authoritative, mathematically sound offline gameplay hint engine for Zynpath.
 *
 * Implements Prompt 14:
 * - Compatible with player's actual current path (Section 10).
 * - Partial-path solver integration respecting visited cells, endpoints, walls, and checkpoints (Section 11).
 * - Proves full-coverage completion before returning a next-move hint (Section 13).
 * - Exhaustive dead-end detection and solver-validated recovery guidance (Sections 14–16).
 * - In-process LRU verified solution caching (Section 21).
 * - Competitive mode fairness guard (Section 24).
 * - Asynchronous execution off the Android main thread (Section 19).
 */
class PuzzleHintEngine(
    private val solver: PuzzleSolver = PuzzleSolver(),
    private val cache: HintCache = HintCache()
) {

    /**
     * Synchronously computes the authoritative hint for the given [HintRequest].
     */
    fun computeHint(request: HintRequest): HintResult {
        // 1. Cooperative cancellation check
        if (request.configuration.cancellationSignal()) {
            return HintResult.Cancelled()
        }

        // 2. Competitive fairness check (Section 24)
        if (!request.gameMode.allowsHints) {
            return HintResult.HintNotAvailable(
                "Hints are disabled in competitive modes to ensure fair play."
            )
        }

        // 3. Request identity validation (Section 7)
        if (request.definition.puzzleId != request.puzzleId ||
            request.definition.puzzleVersion != request.puzzleVersion
        ) {
            return HintResult.InvalidState(
                "Puzzle definition mismatch: expected ${request.puzzleId} v${request.puzzleVersion}, " +
                        "got ${request.definition.puzzleId} v${request.definition.puzzleVersion}"
            )
        }

        // 4. Authoritative engine state vs supplied path alignment
        if (request.gameState.currentPath.positions != request.currentOrderedPath) {
            return HintResult.InvalidState(
                "Supplied path does not match authoritative engine game state"
            )
        }

        // 5. Check if puzzle is already completed
        if (request.gameState.isCompleted) {
            return HintResult.AlreadyCompleted()
        }

        val startCheckpoint = request.definition.checkpoints.find { it.number == 1 }
            ?: return HintResult.InvalidState("Puzzle definition missing start checkpoint #1")
        val startPos = startCheckpoint.position

        // 6. If path is empty, next legal move is starting at Checkpoint #1
        if (request.currentOrderedPath.isEmpty()) {
            val solverConfig = SolverConfiguration(
                maxSolutions = 1,
                nodeLimit = request.configuration.nodeLimit,
                timeBudgetMs = request.configuration.timeBudgetMs,
                cancellationSignal = request.configuration.cancellationSignal
            )
            val solveResult = solver.solve(request.definition, solverConfig)
            return if (solveResult.isSolved) {
                val fullSolution = solveResult.solutions.first()
                if (request.configuration.enableCache) {
                    cache.put(request.puzzleId, request.puzzleVersion, fullSolution)
                }
                HintResult.NextMove(
                    nextMove = startPos,
                    fullSolution = fullSolution,
                    totalSolutionLength = fullSolution.length,
                    isCached = false
                )
            } else if (solveResult.status == SolverStatus.SEARCH_LIMIT_REACHED) {
                HintResult.SearchInconclusive(
                    message = "Search budget exhausted without proving initial solvability",
                    nodesExplored = solveResult.statistics.nodesExplored,
                    elapsedMs = solveResult.statistics.elapsedMs
                )
            } else if (solveResult.status == SolverStatus.CANCELLED) {
                HintResult.Cancelled()
            } else {
                HintResult.InvalidState("Puzzle definition has no legal solutions")
            }
        }

        // 7. Validate non-empty partial path (Section 12)
        val validationResult = PartialPathValidator.validate(
            request.definition,
            request.gameState.currentPath
        )
        if (validationResult is PartialPathValidationResult.Invalid) {
            return HintResult.InvalidState(validationResult.reason)
        }
        val validState = validationResult as PartialPathValidationResult.Valid

        // Premature final checkpoint entry: dead end reached
        if (validState.isPrematureFinalCheckpoint) {
            return findRecoveryGuidance(request)
        }

        // Entire board covered check
        if (validState.remainingRequiredCells == 0) {
            val finalCheckpoint = request.definition.checkpoints.maxByOrNull { it.number }
            if (request.currentOrderedPath.last() == finalCheckpoint?.position) {
                return HintResult.AlreadyCompleted()
            } else {
                return findRecoveryGuidance(request)
            }
        }

        val currentHead = request.currentOrderedPath.last()

        // 8. Check verified solution cache (Section 21)
        if (request.configuration.enableCache) {
            val cachedSol = cache.findCompatibleSolution(
                puzzleId = request.puzzleId,
                puzzleVersion = request.puzzleVersion,
                currentPath = request.currentOrderedPath
            )
            if (cachedSol != null && cachedSol.length > request.currentOrderedPath.size) {
                val nextCandidate = cachedSol.positions[request.currentOrderedPath.size]
                if (isLegalStep(request.definition, currentHead, nextCandidate)) {
                    return HintResult.NextMove(
                        nextMove = nextCandidate,
                        fullSolution = cachedSol,
                        totalSolutionLength = cachedSol.length,
                        isCached = true
                    )
                }
            }
        }

        // 9. Run solver from player's actual partial path (Section 11)
        val solverConfig = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = request.configuration.nodeLimit,
            timeBudgetMs = request.configuration.timeBudgetMs,
            cancellationSignal = request.configuration.cancellationSignal
        )

        val solverResult = solver.solveFromPartialPath(
            definition = request.definition,
            partialPath = request.gameState.currentPath,
            config = solverConfig
        )

        return when {
            solverResult.isSolved -> {
                val fullSolution = solverResult.solutions.first()
                // Authoritative completion proof (Section 13)
                CompletionValidator.validate(request.definition, fullSolution)
                if (request.configuration.enableCache) {
                    cache.put(request.puzzleId, request.puzzleVersion, fullSolution)
                }

                val nextMove = fullSolution.positions[request.currentOrderedPath.size]
                HintResult.NextMove(
                    nextMove = nextMove,
                    fullSolution = fullSolution,
                    totalSolutionLength = fullSolution.length,
                    isCached = false
                )
            }

            solverResult.status == SolverStatus.SEARCH_LIMIT_REACHED -> {
                HintResult.SearchInconclusive(
                    message = "Search time limit reached without proving continuation",
                    nodesExplored = solverResult.statistics.nodesExplored,
                    elapsedMs = solverResult.statistics.elapsedMs
                )
            }

            solverResult.status == SolverStatus.CANCELLED -> {
                HintResult.Cancelled()
            }

            solverResult.status == SolverStatus.UNSOLVABLE -> {
                // Exhaustive search proved zero continuations: Dead End Detected! (Section 14)
                findRecoveryGuidance(request)
            }

            else -> {
                HintResult.InvalidState(solverResult.diagnosticMessage ?: "Solver execution failed")
            }
        }
    }

    /**
     * Executes bounded solver-backed prefix analysis to discover the longest earlier path prefix
     * from which a complete legal solution exists (Sections 15 & 16).
     */
    private fun findRecoveryGuidance(request: HintRequest): HintResult {
        val path = request.currentOrderedPath
        val pathLength = path.size

        if (pathLength <= 1) {
            return HintResult.InvalidState("Puzzle start position has no valid solution")
        }

        val maxPrefixes = minOf(request.configuration.maxPrefixesToAnalyze, pathLength - 1)
        val recoveryConfig = SolverConfiguration(
            maxSolutions = 1,
            nodeLimit = request.configuration.recoveryNodeLimit,
            timeBudgetMs = request.configuration.recoveryTimeBudgetMs,
            cancellationSignal = request.configuration.cancellationSignal
        )

        // Evaluate earlier prefixes in descending length (closest backtrack first)
        for (prefixLen in (pathLength - 1) downTo (pathLength - maxPrefixes)) {
            if (request.configuration.cancellationSignal()) {
                return HintResult.Cancelled()
            }

            val prefixPositions = path.take(prefixLen)
            val prefixPath = PuzzlePath(prefixPositions)

            // 1. Check cache first
            if (request.configuration.enableCache) {
                val cached = cache.findCompatibleSolution(
                    puzzleId = request.puzzleId,
                    puzzleVersion = request.puzzleVersion,
                    currentPath = prefixPositions
                )
                if (cached != null) {
                    val steps = pathLength - prefixLen
                    return HintResult.RecoveryRequired(
                        recommendedRollbackIndex = prefixLen - 1,
                        recommendedRollbackPosition = prefixPositions.last(),
                        stepsToRetract = steps,
                        explanation = "Current path reached a dead end. Undo $steps ${if (steps == 1) "step" else "steps"} to resume a solvable path.",
                        verifiedPrefix = prefixPositions
                    )
                }
            }

            // 2. Solve prefix
            val solveResult = solver.solveFromPartialPath(
                definition = request.definition,
                partialPath = prefixPath,
                config = recoveryConfig
            )

            if (solveResult.isSolved) {
                val solution = solveResult.solutions.first()
                if (request.configuration.enableCache) {
                    cache.put(request.puzzleId, request.puzzleVersion, solution)
                }
                val steps = pathLength - prefixLen
                return HintResult.RecoveryRequired(
                    recommendedRollbackIndex = prefixLen - 1,
                    recommendedRollbackPosition = prefixPositions.last(),
                    stepsToRetract = steps,
                    explanation = "Current path reached a dead end. Undo $steps ${if (steps == 1) "step" else "steps"} to resume a solvable path.",
                    verifiedPrefix = prefixPositions
                )
            }
        }

        // 3. Fallback: Check if rolling back to start position (checkpoint #1) has a known solution
        val startCheckpoint = request.definition.checkpoints.find { it.number == 1 }
        if (startCheckpoint != null) {
            val startPath = PuzzlePath.of(startCheckpoint.position)
            val startSolveResult = solver.solveFromPartialPath(
                definition = request.definition,
                partialPath = startPath,
                config = recoveryConfig
            )
            if (startSolveResult.isSolved) {
                val steps = pathLength - 1
                return HintResult.RecoveryRequired(
                    recommendedRollbackIndex = 0,
                    recommendedRollbackPosition = startCheckpoint.position,
                    stepsToRetract = steps,
                    explanation = "Current path reached a dead end. Reset to start checkpoint #1 to resume a solvable path.",
                    verifiedPrefix = listOf(startCheckpoint.position)
                )
            }
        }

        return HintResult.SearchInconclusive(
            message = "Recovery analysis was inconclusive within search limits",
            nodesExplored = 0L,
            elapsedMs = 0L
        )
    }

    private fun isLegalStep(definition: PuzzleDefinition, from: GridPosition, to: GridPosition): Boolean {
        return definition.graph.canTraverse(from, to)
    }

    /**
     * Executes hint computation asynchronously on [dispatcher] (defaults to [Dispatchers.Default])
     * off the Android main thread, polling coroutine cancellation (Section 19).
     */
    suspend fun computeHintAsync(
        request: HintRequest,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): HintResult = withContext(dispatcher) {
        val coroutineConfig = request.configuration.copy(
            cancellationSignal = {
                !coroutineContext.isActive || request.configuration.cancellationSignal()
            }
        )
        computeHint(request.copy(configuration = coroutineConfig))
    }
}
