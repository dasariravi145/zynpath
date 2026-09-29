package com.zynpath.game.core.designsystem.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Lightweight, zero-network audio feedback manager for Zynpath solo gameplay.
 *
 * Implements Prompt 15 Section 13:
 * - Native offline audio feedback for checkpoint reach, invalid movement, and victory.
 * - Respects device settings and user sound preferences.
 * - Does not require network audio or external media assets.
 * - Safe error handling ensuring audio initialization never crashes gameplay.
 */
class SoundFeedbackManager(
    private val context: Context,
    var isSfxEnabled: Boolean = true
) {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (e: Exception) {
            Log.w("SoundFeedbackManager", "Audio tone generator initialization skipped: ${e.message}")
        }
    }

    /**
     * Plays subtle positive feedback tone when an in-order checkpoint is reached.
     */
    fun playCheckpointReached() {
        if (!isSfxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (_: Exception) {}
    }

    /**
     * Plays gentle error tone when an illegal move or wall crossing is rejected.
     */
    fun playInvalidMove() {
        if (!isSfxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 70)
        } catch (_: Exception) {}
    }

    /**
     * Plays celebratory chime when the puzzle is validated and completed.
     */
    fun playPuzzleCompleted() {
        if (!isSfxEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}

/**
 * Remembers a lifecycle-aware [SoundFeedbackManager] scoped to the composable tree.
 */
@Composable
fun rememberSoundFeedbackManager(isSfxEnabled: Boolean = true): SoundFeedbackManager {
    val context = LocalContext.current.applicationContext
    val manager = remember { SoundFeedbackManager(context, isSfxEnabled) }

    DisposableEffect(isSfxEnabled) {
        manager.isSfxEnabled = isSfxEnabled
        onDispose {}
    }

    DisposableEffect(Unit) {
        onDispose {
            manager.release()
        }
    }

    return manager
}
