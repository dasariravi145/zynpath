package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PartialPathValidationResult
import com.zynpath.game.core.puzzle.validator.PartialPathValidator
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * Authoritative, exact continuous-path Hamiltonian solver for Zynpath logic puzzles.
 *
 * Implements deterministic depth-first search with backtracking, minimum-onward-degree
 * (Warnsdorff's heuristic) and checkpoint proximity candidate ordering, and sound mathematical pruning rules:
 * - Checkpoint-constrained sequence order ($1 \to 2 \dots \to N$).
 * - Wall and planar graph boundary compliance.
 * - Single continuous path visiting every required cell exactly once ($100\%$ full coverage).
 * - Premature final-checkpoint entry prevention.
 * - Remaining unvisited cell connectivity / flood-fill pruning.
 * - Intermediate cell degree consistency pruning.
 * - Checkpoint reachability pruning (cannot traverse higher checkpoints out of order).
 *
 * Supports solving from the beginning or continuing from an arbitrary verified [PuzzlePath]
 * prefix adhering to Prompt 14 Section 11.
 *
 * All discovered solutions are independently verified against [SolutionValidator].
 */
class PuzzleSolver(
    private val defaultConfiguration: SolverConfiguration = SolverConfiguration.DEFAULT
) {

    /**
     * Solves the given [PuzzleDefinition] from checkpoint #1 using the default solver configuration.
     */
    fun solve(definition: PuzzleDefinition): SolverResult = solve(definition, defaultConfiguration)

    /**
     * Solves the given [PuzzleDefinition] from checkpoint #1 with an explicit [SolverConfiguration].
     */
    fun solve(definition: PuzzleDefinition, config: SolverConfiguration): SolverResult {
        val structuralCheck = PuzzleDefinitionValidator.validate(definition)
        if (structuralCheck is DefinitionValidationResult.Invalid) {
            return SolverResult.invalid(
                "Puzzle definition is structurally invalid: ${structuralCheck.errorSummary}"
            )
        }
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }
            ?: return SolverResult.invalid("MISSING_START_CHECKPOINT: Puzzle definition missing start checkpoint #1")
        return solveFromPartialPath(definition, PuzzlePath.of(startCheckpoint.position), config)
    }

    /**
     * Solves the given [PuzzleDefinition] continuing from an existing [partialPath].
     *
     * Implements Prompt 14 Section 11:
     * - Respects visited cells in [partialPath].
     * - Preserves current endpoint as starting point for forward DFS continuation.
     * - Reconstructs next required checkpoint from checkpoints already visited in [partialPath].
     * - Strictly prevents cycles, revisits, wall crossings, and premature final checkpoint entry.
     * - Validates complete continuation with [SolutionValidator].
     */
    fun solveFromPartialPath(
        definition: PuzzleDefinition,
        partialPath: PuzzlePath,
        config: SolverConfiguration = defaultConfiguration
    ): SolverResult {
        // 1. Structural integrity check
        val structuralCheck = PuzzleDefinitionValidator.validate(definition)
        if (structuralCheck is DefinitionValidationResult.Invalid) {
            return SolverResult.invalid(
                "Puzzle definition is structurally invalid: ${structuralCheck.errorSummary}"
            )
        }

        // 2. Identify start (#1) and final (maxCheckpointNumber) checkpoints
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }
        val finalCheckpoint = definition.checkpoints.maxByOrNull { it.number }
        if (startCheckpoint == null || finalCheckpoint == null) {
            return SolverResult.invalid("Puzzle definition missing start or final checkpoint")
        }

        val startPos = startCheckpoint.position
        val finalPos = finalCheckpoint.position
        val totalCells = definition.totalRequiredCells
        val rowCount = definition.gridDimensions.rows
        val colCount = definition.gridDimensions.columns

        // Trivial edge case: 1-cell puzzle
        if (totalCells == 1) {
            val singlePath = PuzzlePath.of(startPos)
            val validation = SolutionValidator.validate(definition, singlePath)
            return if (validation is SolutionValidationResult.Valid) {
                SolverResult(
                    status = SolverStatus.SOLVED,
                    solutions = listOf(singlePath),
                    isExhaustive = true,
                    statistics = SolverStatistics(1, 0, 0, 0),
                    diagnosticMessage = "Trivial 1-cell puzzle solved"
                )
            } else {
                SolverResult(
                    status = SolverStatus.UNSOLVABLE,
                    solutions = emptyList(),
                    isExhaustive = true,
                    statistics = SolverStatistics(1, 0, 0, 0),
                    diagnosticMessage = "Trivial 1-cell puzzle failed validation"
                )
            }
        }

        // 3. Partial path validation
        val effectivePath = if (partialPath.isEmpty) PuzzlePath.of(startPos) else partialPath
        val partialValidation = PartialPathValidator.validate(definition, effectivePath)
        if (partialValidation is PartialPathValidationResult.Invalid) {
            return SolverResult.invalid("Invalid partial path: ${partialValidation.reason}")
        }
        val validState = partialValidation as PartialPathValidationResult.Valid

        if (validState.isPrematureFinalCheckpoint) {
            return SolverResult(
                status = SolverStatus.UNSOLVABLE,
                solutions = emptyList(),
                isExhaustive = true,
                statistics = SolverStatistics(1, 0, 0, 0),
                diagnosticMessage = "Premature final checkpoint reached in partial path"
            )
        }

        if (validState.remainingRequiredCells == 0) {
            // Path already covers all cells
            if (effectivePath.currentHead == finalPos) {
                val solutionValidation = SolutionValidator.validate(definition, effectivePath)
                return if (solutionValidation is SolutionValidationResult.Valid) {
                    SolverResult(
                        status = SolverStatus.SOLVED,
                        solutions = listOf(effectivePath),
                        isExhaustive = true,
                        statistics = SolverStatistics(1, 0, 0, 0),
                        diagnosticMessage = "Partial path is already a valid full solution"
                    )
                } else {
                    SolverResult(
                        status = SolverStatus.UNSOLVABLE,
                        solutions = emptyList(),
                        isExhaustive = true,
                        statistics = SolverStatistics(1, 0, 0, 0),
                        diagnosticMessage = "Partial path has full length but failed solution validation"
                    )
                }
            } else {
                return SolverResult(
                    status = SolverStatus.UNSOLVABLE,
                    solutions = emptyList(),
                    isExhaustive = true,
                    statistics = SolverStatistics(1, 0, 0, 0),
                    diagnosticMessage = "Partial path covers all cells without ending at final checkpoint"
                )
            }
        }

        // 4. Precompute checkpoint positions by number for quick reachability lookups
        val checkpointPositions = definition.checkpoints.associate { it.number to it.position }

        // 5. Search state initialization
        if (config.cancellationSignal()) {
            return SolverResult(
                status = SolverStatus.CANCELLED,
                solutions = emptyList(),
                isExhaustive = false,
                statistics = SolverStatistics.EMPTY,
                diagnosticMessage = "Search cancelled by cooperative signal"
            )
        }

        val state = SolverSearchState(rowCount, colCount, totalCells)
        val solutions = ArrayList<PuzzlePath>()

        // Populate search state with the existing partial path
        for (pos in effectivePath.positions) {
            state.push(pos)
        }
        state.nodesExplored = effectivePath.length.toLong()

        val startNanos = System.nanoTime()
        val deadlineNanos = if (config.timeBudgetMs >= Long.MAX_VALUE / 1_000_000L) {
            Long.MAX_VALUE
        } else {
            startNanos + config.timeBudgetMs * 1_000_000L
        }

        // Helper: count available unvisited neighbors for candidate ordering (Warnsdorff)
        fun countUnvisitedNeighbors(pos: GridPosition): Int {
            var count = 0
            val neighbors = definition.graph.getTraversableNeighbors(pos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                if (!state.isVisited(nbr)) {
                    count++
                }
            }
            return count
        }

        // Recursive DFS search
        fun dfs(currentHead: GridPosition, nextRequiredCheckpoint: Int, remainingRequired: Int) {
            // Check cancellation and time budget periodically
            if (state.nodesExplored % 32L == 0L) {
                if (config.cancellationSignal()) {
                    state.cancelled = true
                    return
                }
                if (System.nanoTime() >= deadlineNanos) {
                    state.searchLimitReached = true
                    return
                }
            }

            val rawNeighbors = definition.graph.getTraversableNeighbors(currentHead)
            val legalCandidates = ArrayList<GridPosition>(4)

            for (i in rawNeighbors.indices) {
                val cand = rawNeighbors[i]

                // Must be unvisited and part of required cells
                if (state.isVisited(cand)) continue

                val cpNumber = definition.getCheckpointAt(cand)

                // Rule 2: Checkpoint sequence check
                if (!SolverPruningRules.isCheckpointSequenceValid(cpNumber, nextRequiredCheckpoint)) continue

                // Rule 1: Premature final checkpoint entry check
                if (SolverPruningRules.isPrematureFinalCheckpoint(cand, finalPos, remainingRequired)) continue

                legalCandidates.add(cand)
            }

            if (legalCandidates.isEmpty()) {
                state.backtracks++
                return
            }

            // Candidate ordering: Warnsdorff's heuristic + Checkpoint Proximity + Deterministic coordinate tie-breaking
            val targetPos = checkpointPositions[nextRequiredCheckpoint] ?: finalPos
            if (legalCandidates.size > 1) {
                legalCandidates.sortWith(Comparator { a, b ->
                    val degA = countUnvisitedNeighbors(a)
                    val degB = countUnvisitedNeighbors(b)
                    if (degA != degB) {
                        degA.compareTo(degB)
                    } else {
                        val distA = a.manhattanDistance(targetPos)
                        val distB = b.manhattanDistance(targetPos)
                        if (distA != distB) {
                            distA.compareTo(distB)
                        } else {
                            val rowComp = a.row.compareTo(b.row)
                            if (rowComp != 0) rowComp else a.column.compareTo(b.column)
                        }
                    }
                })
            }

            for (cIndex in legalCandidates.indices) {
                val cand = legalCandidates[cIndex]
                state.nodesExplored++

                if (state.nodesExplored >= config.nodeLimit) {
                    state.searchLimitReached = true
                    return
                }

                // Sound mathematical pruning
                if (config.enablePruning && remainingRequired > 1) {
                    // Check Rule 3: Connectivity of unvisited required cells
                    if (!SolverPruningRules.isConnectivityPreserved(
                            definition, cand, finalPos, remainingRequired, state
                        )
                    ) {
                        state.prunedBranches++
                        continue
                    }

                    // Check Rule 4: Intermediate cell degrees
                    if (!SolverPruningRules.hasSufficientDegrees(
                            definition, cand, finalPos, remainingRequired, state
                        )
                    ) {
                        state.prunedBranches++
                        continue
                    }

                    // Check Rule 5: Checkpoint reachability
                    val targetCpPos = checkpointPositions[nextRequiredCheckpoint]
                    if (!SolverPruningRules.isNextCheckpointReachable(
                            definition, cand, nextRequiredCheckpoint, targetCpPos, state
                        )
                    ) {
                        state.prunedBranches++
                        continue
                    }
                }

                // Step forward into candidate
                state.push(cand)

                val cpNumber = definition.getCheckpointAt(cand)
                val newNextCp = if (cpNumber == nextRequiredCheckpoint) nextRequiredCheckpoint + 1 else nextRequiredCheckpoint

                if (remainingRequired == 1) {
                    // Terminal step: must be at final checkpoint
                    if (cand == finalPos) {
                        val candidatePath = state.toPuzzlePath()
                        // Authoritative solution validation & engine defect detection
                        SolutionValidator.assertValid(definition, candidatePath)
                        if (!solutions.contains(candidatePath)) {
                            solutions.add(candidatePath)
                        }
                        if (solutions.size >= config.maxSolutions) {
                            state.pop()
                            return
                        }
                    }
                } else {
                    dfs(cand, newNextCp, remainingRequired - 1)
                    if (solutions.size >= config.maxSolutions || state.searchLimitReached || state.cancelled) {
                        state.pop()
                        return
                    }
                }

                // Step back (backtrack)
                state.pop()
                state.backtracks++
            }
        }

        // Execute search starting at the head of the partial path
        dfs(
            currentHead = effectivePath.currentHead ?: startPos,
            nextRequiredCheckpoint = validState.nextRequiredCheckpoint,
            remainingRequired = validState.remainingRequiredCells
        )

        val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L
        val statistics = state.toStatistics(elapsedMs)

        // Determine final exhaustive status and result outcome
        val isExhaustive = !state.cancelled && !state.searchLimitReached && solutions.size < config.maxSolutions

        val status = when {
            state.cancelled -> SolverStatus.CANCELLED
            state.searchLimitReached -> SolverStatus.SEARCH_LIMIT_REACHED
            solutions.isEmpty() -> SolverStatus.UNSOLVABLE
            solutions.size >= 2 -> SolverStatus.MULTIPLE_SOLUTIONS
            solutions.size == 1 -> SolverStatus.SOLVED
            else -> SolverStatus.SOLVED
        }

        val diagnostic = when (status) {
            SolverStatus.SOLVED -> if (isExhaustive) "Exhaustive search proved unique solution continuation" else "Found valid solution continuation"
            SolverStatus.MULTIPLE_SOLUTIONS -> "Discovered ${solutions.size} distinct solutions from partial path"
            SolverStatus.UNSOLVABLE -> "Exhaustive search proved no valid continuation exists from current path"
            SolverStatus.SEARCH_LIMIT_REACHED -> "Search limit reached (${state.nodesExplored} nodes, ${elapsedMs}ms)"
            SolverStatus.CANCELLED -> "Search cancelled by cooperative signal"
            SolverStatus.INVALID_PUZZLE -> "Invalid puzzle definition or partial path"
        }

        return SolverResult(
            status = status,
            solutions = solutions,
            isExhaustive = isExhaustive,
            statistics = statistics,
            diagnosticMessage = diagnostic
        )
    }

    /**
     * Solves and terminates immediately upon discovering the first valid full-coverage path.
     */
    fun findFirstSolution(definition: PuzzleDefinition): SolverResult =
        solve(definition, SolverConfiguration.FIRST_SOLUTION)

    /**
     * Searches for up to 2 distinct solutions to deterministically confirm or disprove uniqueness.
     */
    fun checkUniqueness(definition: PuzzleDefinition): SolverResult =
        solve(definition, SolverConfiguration.CHECK_UNIQUENESS)

    /**
     * Discovers up to [limit] distinct solutions.
     */
    fun countSolutions(definition: PuzzleDefinition, limit: Int = 10): SolverResult =
        solve(definition, SolverConfiguration(maxSolutions = limit))

    /**
     * Asynchronous solving interface adhering to Section 33.
     * Executes the search on [dispatcher] (defaults to [Dispatchers.Default]) off the main thread,
     * with automatic cooperative coroutine cancellation polling.
     */
    suspend fun solveAsync(
        definition: PuzzleDefinition,
        config: SolverConfiguration = defaultConfiguration,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): SolverResult = withContext(dispatcher) {
        val coroutineConfig = config.copy(
            cancellationSignal = {
                !coroutineContext.isActive || config.cancellationSignal()
            }
        )
        solve(definition, coroutineConfig)
    }

    /**
     * Asynchronous partial path solving interface adhering to Prompt 14.
     */
    suspend fun solveFromPartialPathAsync(
        definition: PuzzleDefinition,
        partialPath: PuzzlePath,
        config: SolverConfiguration = defaultConfiguration,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): SolverResult = withContext(dispatcher) {
        val coroutineConfig = config.copy(
            cancellationSignal = {
                !coroutineContext.isActive || config.cancellationSignal()
            }
        )
        solveFromPartialPath(definition, partialPath, coroutineConfig)
    }
}
