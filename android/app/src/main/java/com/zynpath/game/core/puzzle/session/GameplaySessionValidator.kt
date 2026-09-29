package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Authoritative validator for stored gameplay session snapshots.
 *
 * Implements Prompt 13 Sections 13–16:
 * 1. Checks level ID matching.
 * 2. Checks puzzle ID matching.
 * 3. Checks puzzle version matching.
 * 4. Checks snapshot schema version support.
 * 5. Checks resumable status.
 * 6. Replays the exact path through the authoritative [PuzzleEngine].
 * 7. Reconstructs coverage and checkpoint order without trusting stored flags.
 * 8. Rejects any illegal move, wall crossing, cycle, or premature completion.
 */
object GameplaySessionValidator {

    const val CURRENT_SCHEMA_VERSION = 1

    fun validateAndReplay(
        snapshot: GameplaySessionSnapshot,
        definition: PuzzleDefinition,
        expectedLevelId: Int,
        expectedOwnerId: String? = null
    ): SessionRestorationResult {
        // 0. Confirm session ownership (Prompt 38 Section 15 & 32)
        if (expectedOwnerId != null && snapshot.ownerIdentity != null && snapshot.ownerIdentity != expectedOwnerId) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.SESSION_OWNERSHIP_MISMATCH,
                detailMessage = "Session owner (${snapshot.ownerIdentity}) does not match active player ($expectedOwnerId)"
            )
        }

        // 1. Confirm level ID
        if (snapshot.levelId != expectedLevelId) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.LEVEL_ID_MISMATCH,
                detailMessage = "Snapshot levelId (${snapshot.levelId}) does not match expected levelId ($expectedLevelId)"
            )
        }

        // 2. Confirm puzzle ID
        if (snapshot.puzzleId != definition.puzzleId) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.PUZZLE_ID_MISMATCH,
                detailMessage = "Snapshot puzzleId '${snapshot.puzzleId}' does not match catalog puzzleId '${definition.puzzleId}'"
            )
        }

        // 3. Confirm puzzle version
        if (snapshot.puzzleVersion != definition.puzzleVersion) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.PUZZLE_VERSION_MISMATCH,
                detailMessage = "Snapshot puzzleVersion (${snapshot.puzzleVersion}) does not match definition version (${definition.puzzleVersion})"
            )
        }

        // 4. Confirm supported schema version
        if (snapshot.snapshotSchemaVersion != CURRENT_SCHEMA_VERSION) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.UNSUPPORTED_SCHEMA_VERSION,
                detailMessage = "Unsupported snapshot schema version (${snapshot.snapshotSchemaVersion}), current is $CURRENT_SCHEMA_VERSION"
            )
        }

        // 5. Confirm resumable status
        if (!snapshot.isResumable) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.SESSION_NOT_RESUMABLE,
                detailMessage = "Session status '${snapshot.status}' is not resumable"
            )
        }

        // 6. Handle empty path
        if (snapshot.path.isEmpty()) {
            val engine = PuzzleEngine(definition)
            return SessionRestorationResult.Success(
                snapshot = snapshot,
                restoredGameState = engine.currentState,
                restoredEngine = engine
            )
        }

        // 7. Authoritative replay through PuzzleEngine
        val engine = PuzzleEngine(definition)

        // Step 1: Start at checkpoint #1
        val startPos = snapshot.path.first()
        val startResult = engine.process(PuzzleAction.StartPath(startPos), snapshot.elapsedActiveTimeMs)
        if (!startResult.isAccepted) {
            val reason = (startResult as? PuzzleEngineResult.Rejected)?.reason
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.START_NOT_CHECKPOINT_ONE,
                detailMessage = "Path start $startPos rejected by engine: $reason"
            )
        }

        // Step 2: Replay consecutive moves
        for (i in 1 until snapshot.path.size) {
            val nextPos = snapshot.path[i]
            val moveResult = engine.process(PuzzleAction.ExtendPath(nextPos), snapshot.elapsedActiveTimeMs)
            if (!moveResult.isAccepted) {
                val reason = (moveResult as? PuzzleEngineResult.Rejected)?.reason
                return SessionRestorationResult.Invalid(
                    snapshot = snapshot,
                    reason = RestorationFailureReason.ILLEGAL_MOVE_REPLAY,
                    detailMessage = "Replay transition from ${snapshot.path[i - 1]} to $nextPos rejected by engine: $reason"
                )
            }
        }

        // Step 3: Verify reconstructed path exactly equals stored ordered path
        if (engine.currentState.currentPath.positions != snapshot.path) {
            return SessionRestorationResult.Invalid(
                snapshot = snapshot,
                reason = RestorationFailureReason.PATH_MISMATCH_AFTER_REPLAY,
                detailMessage = "Reconstructed path does not match snapshot path"
            )
        }

        // Step 4: If session was paused, ensure engine state reflects paused
        if (snapshot.status == SessionStatus.PAUSED) {
            engine.process(PuzzleAction.PauseGame, snapshot.elapsedActiveTimeMs)
        }

        return SessionRestorationResult.Success(
            snapshot = snapshot,
            restoredGameState = engine.currentState,
            restoredEngine = engine
        )
    }
}
