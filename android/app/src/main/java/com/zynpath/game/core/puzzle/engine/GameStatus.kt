package com.zynpath.game.core.puzzle.engine

/**
 * Lifecycle status of an active puzzle gameplay session in the engine.
 */
enum class GameStatus {
    /** Puzzle has been loaded, but the player has not yet touched/started at checkpoint 1. */
    NOT_STARTED,

    /** Active path drawing is in progress. Forward movement and backtracking are permitted. */
    IN_PROGRESS,

    /** Puzzle has been authoritatively completed: 100% cells covered and all checkpoints in order. */
    COMPLETED,

    /** Gameplay is temporarily paused. Input is suspended until resumed. */
    PAUSED;

    companion object {
        val SOLVED: GameStatus get() = COMPLETED
    }
}
