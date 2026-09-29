package com.zynpath.game.core.feedback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified semantic feedback events across Zynpath gameplay and user interface (Prompt 21 Task 2).
 */
enum class FeedbackEvent {
    BUTTON_PRESSED,
    SCREEN_OPENED,
    PATH_STARTED,
    VALID_MOVE,
    CHECKPOINT_REACHED,
    INVALID_MOVE,
    UNDO,
    RESET,
    PUZZLE_COMPLETED,
    REWARD_REVEALED,
    PLAYER_JOINED,
    PLAYER_LEFT,
    MATCH_STARTED,
    MATCH_COMPLETED,
    VICTORY,
    DEFEAT,
    ERROR
}

/**
 * Centralized, decoupled feedback coordinator governing audio and haptic responses.
 *
 * Implements Prompt 21:
 * - Decouples audio, haptic, and animation responsibilities.
 * - Enforces event-driven execution preventing uncontrolled Compose recomposition spam.
 * - Respects player's sound and haptic preferences stored in DataStore.
 * - Provides safe fallbacks when hardware or audio focus is unavailable.
 */
interface ZynpathFeedbackCoordinator {
    fun trigger(event: FeedbackEvent)
    fun onButtonPressed() = trigger(FeedbackEvent.BUTTON_PRESSED)
    fun onScreenOpened() = trigger(FeedbackEvent.SCREEN_OPENED)
    fun onPathStarted() = trigger(FeedbackEvent.PATH_STARTED)
    fun onValidMove() = trigger(FeedbackEvent.VALID_MOVE)
    fun onCheckpointReached() = trigger(FeedbackEvent.CHECKPOINT_REACHED)
    fun onInvalidMove() = trigger(FeedbackEvent.INVALID_MOVE)
    fun onUndo() = trigger(FeedbackEvent.UNDO)
    fun onReset() = trigger(FeedbackEvent.RESET)
    fun onPuzzleCompleted() = trigger(FeedbackEvent.PUZZLE_COMPLETED)
    fun onRewardRevealed() = trigger(FeedbackEvent.REWARD_REVEALED)
    fun onPlayerJoined() = trigger(FeedbackEvent.PLAYER_JOINED)
    fun onPlayerLeft() = trigger(FeedbackEvent.PLAYER_LEFT)
    fun onMatchStarted() = trigger(FeedbackEvent.MATCH_STARTED)
    fun onMatchCompleted() = trigger(FeedbackEvent.MATCH_COMPLETED)
    fun onVictory() = trigger(FeedbackEvent.VICTORY)
    fun onDefeat() = trigger(FeedbackEvent.DEFEAT)
    fun onError() = trigger(FeedbackEvent.ERROR)
}

@Singleton
class ZynpathFeedbackCoordinatorImpl @Inject constructor(
    private val audioManager: ZynpathAudioManager,
    private val hapticManager: ZynpathHapticManager
) : ZynpathFeedbackCoordinator {

    override fun trigger(event: FeedbackEvent) {
        when (event) {
            FeedbackEvent.BUTTON_PRESSED -> {
                audioManager.playEvent(ZynpathAudioEvent.BUTTON_TAP)
                hapticManager.performHaptic(ZynpathHapticEvent.BUTTON_CONFIRM)
            }
            FeedbackEvent.SCREEN_OPENED -> {
                audioManager.playEvent(ZynpathAudioEvent.SCREEN_OPEN, volumeMultiplier = 0.5f)
                hapticManager.performHaptic(ZynpathHapticEvent.SCREEN_OPEN)
            }
            FeedbackEvent.PATH_STARTED -> {
                audioManager.playEvent(ZynpathAudioEvent.PATH_START)
                hapticManager.performHaptic(ZynpathHapticEvent.PATH_START)
            }
            FeedbackEvent.VALID_MOVE -> {
                audioManager.playEvent(ZynpathAudioEvent.VALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.VALID_MOVE)
            }
            FeedbackEvent.CHECKPOINT_REACHED -> {
                audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
                hapticManager.performHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
            }
            FeedbackEvent.INVALID_MOVE -> {
                audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.INVALID_MOVE)
            }
            FeedbackEvent.UNDO -> {
                audioManager.playEvent(ZynpathAudioEvent.UNDO)
                hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            }
            FeedbackEvent.RESET -> {
                audioManager.playEvent(ZynpathAudioEvent.RESET)
                hapticManager.performHaptic(ZynpathHapticEvent.RESET)
            }
            FeedbackEvent.PUZZLE_COMPLETED -> {
                audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                hapticManager.performHaptic(ZynpathHapticEvent.COMPLETION)
            }
            FeedbackEvent.REWARD_REVEALED -> {
                audioManager.playEvent(ZynpathAudioEvent.REWARD_REVEALED)
                hapticManager.performHaptic(ZynpathHapticEvent.REWARD_REVEALED)
            }
            FeedbackEvent.PLAYER_JOINED -> {
                audioManager.playEvent(ZynpathAudioEvent.PLAYER_JOINED)
                hapticManager.performHaptic(ZynpathHapticEvent.PLAYER_JOINED)
            }
            FeedbackEvent.PLAYER_LEFT -> {
                audioManager.playEvent(ZynpathAudioEvent.PLAYER_LEFT)
                hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            }
            FeedbackEvent.MATCH_STARTED -> {
                audioManager.playEvent(ZynpathAudioEvent.MATCH_STARTED)
                hapticManager.performHaptic(ZynpathHapticEvent.MATCH_STARTED)
            }
            FeedbackEvent.MATCH_COMPLETED -> {
                audioManager.playEvent(ZynpathAudioEvent.MATCH_COMPLETED)
                hapticManager.performHaptic(ZynpathHapticEvent.COMPLETION)
            }
            FeedbackEvent.VICTORY -> {
                audioManager.playEvent(ZynpathAudioEvent.VICTORY)
                hapticManager.performHaptic(ZynpathHapticEvent.VICTORY)
            }
            FeedbackEvent.DEFEAT -> {
                audioManager.playEvent(ZynpathAudioEvent.DEFEAT)
                hapticManager.performHaptic(ZynpathHapticEvent.DEFEAT)
            }
            FeedbackEvent.ERROR -> {
                audioManager.playEvent(ZynpathAudioEvent.ERROR)
                hapticManager.performHaptic(ZynpathHapticEvent.ERROR)
            }
        }
    }
}

/**
 * Entry point allowing composables without direct ViewModel injection to obtain the singleton feedback coordinator.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FeedbackCoordinatorEntryPoint {
    fun feedbackCoordinator(): ZynpathFeedbackCoordinator
}

val LocalFeedbackCoordinator = compositionLocalOf<ZynpathFeedbackCoordinator?> { null }

/**
 * Resolves the lifecycle-aware singleton [ZynpathFeedbackCoordinator] from the Compose hierarchy or Hilt EntryPoint.
 */
@Composable
fun rememberZynpathFeedbackCoordinator(): ZynpathFeedbackCoordinator {
    LocalFeedbackCoordinator.current?.let { return it }
    val context = LocalContext.current.applicationContext
    return remember(context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            FeedbackCoordinatorEntryPoint::class.java
        )
        entryPoint.feedbackCoordinator()
    }
}
