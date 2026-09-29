package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import java.util.Random
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * Authoritative, solver-validated continuous-path puzzle generation engine for Zynpath.
 *
 * Implements Prompt 9 Sections 5, 6, 19, 20, 22, 23, 27, and 35:
 * 1. Validates generation configuration.
 * 2. Constructs a full-coverage legal Hamiltonian route.
 * 3. Distributes numbered checkpoints along the verified route in strictly ascending order.
 * 4. Places direction-independent blocked edges (walls) without blocking the intended route.
 * 5. Builds candidate [PuzzleDefinition] and validates structural integrity.
 * 6. Solves candidate using authoritative [PuzzleSolver] to prove solvability and uniqueness.
 * 7. Independently validates discovered solution against [CompletionValidator].
 * 8. Returns structured [GenerationResult] with comprehensive telemetry.
 */
class PuzzleGenerator(
    private val routeConstructor: RouteConstructor = RouteConstructor(),
    private val checkpointPlacementStrategy: CheckpointPlacementStrategy = CheckpointPlacementStrategy(),
    private val wallPlacementStrategy: WallPlacementStrategy = WallPlacementStrategy(),
    private val candidateValidator: CandidateValidator = CandidateValidator(),
    private val solver: PuzzleSolver = PuzzleSolver()
) {

    /**
     * Synchronously generates a solver-verified puzzle matching [config].
     *
     * @param config Generation parameters and constraints.
     * @param cancellationSignal Cooperative cancellation predicate.
     * @param seenFingerprints Optional registry of canonical fingerprints to reject duplicates.
     * @return [GenerationResult] containing the verified puzzle or structured failure reason.
     */
    fun generate(
        config: GenerationConfiguration,
        cancellationSignal: () -> Boolean = { false },
        seenFingerprints: MutableSet<String>? = null
    ): GenerationResult {
        val startNanos = System.nanoTime()

        // 1. Precondition checks
        val validationError = config.validate()
        if (validationError != null) {
            return GenerationResult.invalidConfiguration(
                seed = config.seed,
                reason = validationError,
                generatorVersion = config.generatorVersion
            )
        }

        val random = Random(config.seed)
        val rejectionReasons = LinkedHashMap<String, Int>()

        fun recordRejection(reason: String) {
            rejectionReasons[reason] = (rejectionReasons[reason] ?: 0) + 1
        }

        var totalSolverNodes = 0L
        var totalSolverTimeMs = 0L
        var attempts = 0

        val maxAllowedTimeMs = maxOf(10_000L, config.solverTimeBudgetMs * 5)
        val deadlineNanos = startNanos + maxAllowedTimeMs * 1_000_000L

        // 2. Candidate generation loop
        while (attempts < config.maxCandidateAttempts) {
            attempts++

            // Cooperative cancellation
            if (cancellationSignal()) {
                val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
                val stats = GenerationStatistics(
                    totalAttempts = attempts,
                    rejectedCandidates = attempts,
                    rejectionReasons = rejectionReasons,
                    solverNodesExplored = totalSolverNodes,
                    solverElapsedMs = totalSolverTimeMs,
                    generationElapsedMs = elapsedMs
                )
                return GenerationResult.cancelled(config.seed, stats, config.generatorVersion)
            }

            // Overall time limit
            if (System.nanoTime() >= deadlineNanos) {
                val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
                val stats = GenerationStatistics(
                    totalAttempts = attempts,
                    rejectedCandidates = attempts,
                    rejectionReasons = rejectionReasons,
                    solverNodesExplored = totalSolverNodes,
                    solverElapsedMs = totalSolverTimeMs,
                    generationElapsedMs = elapsedMs
                )
                return GenerationResult.resourceLimitReached(
                    seed = config.seed,
                    statistics = stats,
                    reason = "Cumulative generation time limit reached ($elapsedMs ms)",
                    generatorVersion = config.generatorVersion
                )
            }

            // Step A: Construct complete legal route
            val route = routeConstructor.constructRoute(
                dimensions = config.dimensions,
                requiredCells = config.effectiveRequiredCells,
                style = config.routeStyle,
                random = random
            )
            if (route == null) {
                recordRejection("ROUTE_CONSTRUCTION_FAILED")
                continue
            }

            // Step B: Place numbered checkpoints along route
            val checkpoints = try {
                checkpointPlacementStrategy.placeCheckpoints(
                    route = route,
                    count = config.checkpointCount,
                    random = random
                )
            } catch (e: Exception) {
                recordRejection("CHECKPOINT_PLACEMENT_EXCEPTION: ${e.message}")
                continue
            }

            if (!checkpointPlacementStrategy.validateCheckpoints(checkpoints, route, config.checkpointCount)) {
                recordRejection("CHECKPOINT_PLACEMENT_INVALID")
                continue
            }

            // Step C: Place blocked edges (walls)
            val walls = wallPlacementStrategy.placeWalls(
                dimensions = config.dimensions,
                requiredCells = config.effectiveRequiredCells,
                route = route,
                minWalls = config.minWalls,
                maxWalls = config.maxWalls,
                random = random
            )
            if (walls == null) {
                recordRejection("WALL_PLACEMENT_FAILED")
                continue
            }

            // Step D: Build candidate PuzzleDefinition
            val candidateId = "${config.puzzleIdPrefix}_att${attempts}_s${config.seed}"
            val candidate = PuzzleDefinition(
                puzzleId = candidateId,
                puzzleVersion = config.puzzleVersion,
                gridDimensions = config.dimensions,
                requiredCells = config.effectiveRequiredCells,
                checkpoints = checkpoints,
                blockedEdges = walls,
                difficultyMetadata = "World_${config.worldId ?: 0}_Lvl_${config.levelId ?: 0}",
                seed = config.seed
            )

            // Step E: Structural candidate validation
            when (val candidateCheck = candidateValidator.validateCandidate(candidate, config)) {
                is CandidateValidationResult.Invalid -> {
                    recordRejection("CANDIDATE_STRUCTURAL_INVALID: ${candidateCheck.reason}")
                    continue
                }
                is CandidateValidationResult.Valid -> { /* Passed */ }
            }

            // Step F: Duplicate detection
            val fingerprint = PuzzleFingerprint.canonicalRepresentation(candidate)
            if (seenFingerprints != null && seenFingerprints.contains(fingerprint)) {
                recordRejection("DUPLICATE_CANDIDATE")
                continue
            }

            // Step G: Solver verification
            val solverConfig = SolverConfiguration(
                maxSolutions = if (config.requireUniqueness) 2 else 1,
                nodeLimit = config.solverNodeBudget,
                timeBudgetMs = config.solverTimeBudgetMs,
                enablePruning = true,
                cancellationSignal = cancellationSignal
            )

            val solverResult = solver.solve(candidate, solverConfig)
            totalSolverNodes += solverResult.statistics.nodesExplored
            totalSolverTimeMs += solverResult.statistics.elapsedMs

            if (solverResult.status == SolverStatus.CANCELLED) {
                val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
                val stats = GenerationStatistics(
                    totalAttempts = attempts,
                    rejectedCandidates = attempts,
                    rejectionReasons = rejectionReasons,
                    solverNodesExplored = totalSolverNodes,
                    solverElapsedMs = totalSolverTimeMs,
                    generationElapsedMs = elapsedMs
                )
                return GenerationResult.cancelled(config.seed, stats, config.generatorVersion)
            }

            if (config.requireUniqueness) {
                if (solverResult.status == SolverStatus.SEARCH_LIMIT_REACHED) {
                    recordRejection("SOLVER_SEARCH_LIMIT")
                    continue
                }
                if (solverResult.status == SolverStatus.MULTIPLE_SOLUTIONS || solverResult.solutions.size >= 2) {
                    recordRejection("MULTIPLE_SOLUTIONS")
                    continue
                }
                if (!solverResult.isUnique) {
                    recordRejection("NOT_PROVEN_UNIQUE")
                    continue
                }
            } else {
                if (!solverResult.isSolved) {
                    recordRejection("UNSOLVABLE")
                    continue
                }
            }

            // Step H: Independent CompletionValidator verification
            val verifiedPath = solverResult.firstSolution
            if (verifiedPath == null) {
                recordRejection("MISSING_SOLUTION")
                continue
            }

            val completionCheck = CompletionValidator.validate(candidate, verifiedPath)
            if (completionCheck !is CompletionCheckResult.Success) {
                recordRejection("COMPLETION_VALIDATION_FAILED")
                continue
            }

            // Step I: Candidate ACCEPTED!
            seenFingerprints?.add(fingerprint)
            val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
            val routeTurns = routeConstructor.countTurns(verifiedPath.positions)
            val sha256 = PuzzleFingerprint.computeSha256(candidate)

            val stats = GenerationStatistics(
                totalAttempts = attempts,
                acceptedCandidates = 1,
                rejectedCandidates = attempts - 1,
                rejectionReasons = rejectionReasons,
                solverNodesExplored = totalSolverNodes,
                solverElapsedMs = totalSolverTimeMs,
                generationElapsedMs = elapsedMs,
                routeTurns = routeTurns,
                wallCount = candidate.blockedEdges.size,
                checkpointCount = candidate.checkpoints.size,
                requiredCellCount = candidate.totalRequiredCells
            )

            return GenerationResult(
                status = GenerationStatus.GENERATED,
                puzzle = candidate,
                verifiedSolution = verifiedPath,
                seed = config.seed,
                generatorVersion = config.generatorVersion,
                uniquenessStatus = solverResult.uniqueness,
                statistics = stats,
                diagnosticMessage = "Puzzle successfully generated and solver-verified in $attempts attempt(s)",
                canonicalFingerprint = sha256
            )
        }

        // Attempt budget exhausted
        val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
        val finalStats = GenerationStatistics(
            totalAttempts = attempts,
            acceptedCandidates = 0,
            rejectedCandidates = attempts,
            rejectionReasons = rejectionReasons,
            solverNodesExplored = totalSolverNodes,
            solverElapsedMs = totalSolverTimeMs,
            generationElapsedMs = elapsedMs
        )

        return GenerationResult.noValidCandidate(
            seed = config.seed,
            statistics = finalStats,
            reason = "Failed to discover a valid candidate within $attempts attempts. Reasons: $rejectionReasons",
            generatorVersion = config.generatorVersion
        )
    }

    /**
     * Asynchronously generates a puzzle adhering to Section 35 off the main thread.
     */
    suspend fun generateAsync(
        config: GenerationConfiguration,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        seenFingerprints: MutableSet<String>? = null
    ): GenerationResult = withContext(dispatcher) {
        val coroutineCancellation = {
            !coroutineContext.isActive
        }
        generate(
            config = config,
            cancellationSignal = coroutineCancellation,
            seenFingerprints = seenFingerprints
        )
    }
}
