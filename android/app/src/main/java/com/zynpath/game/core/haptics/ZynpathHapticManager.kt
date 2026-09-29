package com.zynpath.game.core.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized haptic feedback manager contract.
 *
 * Implements Prompt 34 Sections 31-37, 45, 50:
 * - Centralizes all tactile vibration effects.
 * - Respects user haptic preferences and device hardware capabilities.
 * - Enforces move throttling to prevent continuous motor hum.
 * - Safely handles older API levels and missing vibrator hardware without crashing.
 */
interface ZynpathHapticManager {
    /**
     * Executes tactile feedback for [event].
     * Fails safely without penalty if hardware is unavailable or user disabled haptics.
     */
    fun performHaptic(event: ZynpathHapticEvent)

    /**
     * Alias for [performHaptic] for ergonomic triggering.
     */
    fun triggerHaptic(event: ZynpathHapticEvent) = performHaptic(event)
}

@Singleton
class ZynpathHapticManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ZynpathHapticManager {

    private val tag = "ZynpathHapticManager"
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val isHapticsEnabled = AtomicBoolean(true)
    private val lastValidMoveTime = AtomicLong(0L)
    private val lastInvalidMoveTime = AtomicLong(0L)

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Exception) {
        Log.w(tag, "Failed to resolve Vibrator service: ${e.message}")
        null
    }

    init {
        observePreferences()
    }

    private fun observePreferences() {
        scope.launch {
            preferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                isHapticsEnabled.set(prefs.isHapticsEnabled)
            }
        }
    }

    override fun performHaptic(event: ZynpathHapticEvent) {
        if (!isHapticsEnabled.get()) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        // High-frequency throttling to prevent motor exhaustion
        val now = System.currentTimeMillis()
        when (event) {
            ZynpathHapticEvent.VALID_MOVE -> {
                val last = lastValidMoveTime.get()
                if (now - last < 50L) return
                lastValidMoveTime.set(now)
            }
            ZynpathHapticEvent.INVALID_MOVE -> {
                val last = lastInvalidMoveTime.get()
                if (now - last < 200L) return
                lastInvalidMoveTime.set(now)
            }
            else -> {}
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                executeModernHaptic(vib, event)
            } else {
                executeLegacyHaptic(vib, event)
            }
        } catch (e: Throwable) {
            // Hardware vibration exceptions must never compromise gameplay flow
            Log.w(tag, "Haptic execution failed for $event: ${e.message}")
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun executeModernHaptic(vib: Vibrator, event: ZynpathHapticEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (event) {
                ZynpathHapticEvent.PATH_START -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    return
                }
                ZynpathHapticEvent.VALID_MOVE -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                    return
                }
                ZynpathHapticEvent.CHECKPOINT_REACHED -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                    return
                }
                ZynpathHapticEvent.INVALID_MOVE -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                    return
                }
                ZynpathHapticEvent.UNDO -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                    return
                }
                ZynpathHapticEvent.BUTTON_CONFIRM -> {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    return
                }
                else -> {
                    // Fall through to custom waveforms below
                }
            }
        }

        val effect = when (event) {
            ZynpathHapticEvent.PATH_START -> VibrationEffect.createOneShot(15, 120)
            ZynpathHapticEvent.VALID_MOVE -> VibrationEffect.createOneShot(8, 80)
            ZynpathHapticEvent.CHECKPOINT_REACHED -> VibrationEffect.createOneShot(25, 200)
            ZynpathHapticEvent.INVALID_MOVE -> VibrationEffect.createWaveform(
                longArrayOf(0, 20, 30, 20),
                intArrayOf(0, 180, 0, 180),
                -1
            )
            ZynpathHapticEvent.UNDO -> VibrationEffect.createOneShot(12, 100)
            ZynpathHapticEvent.RESET -> VibrationEffect.createOneShot(35, 180)
            ZynpathHapticEvent.COMPLETION -> VibrationEffect.createWaveform(
                longArrayOf(0, 25, 40, 30, 40, 45),
                intArrayOf(0, 150, 0, 200, 0, 255),
                -1
            )
            ZynpathHapticEvent.BUTTON_CONFIRM -> VibrationEffect.createOneShot(10, 130)
            ZynpathHapticEvent.SCREEN_OPEN -> VibrationEffect.createOneShot(8, 70)
            ZynpathHapticEvent.REWARD_REVEALED -> VibrationEffect.createWaveform(
                longArrayOf(0, 20, 40, 25),
                intArrayOf(0, 160, 0, 220),
                -1
            )
            ZynpathHapticEvent.PLAYER_JOINED -> VibrationEffect.createOneShot(15, 120)
            ZynpathHapticEvent.MATCH_STARTED -> VibrationEffect.createOneShot(30, 200)
            ZynpathHapticEvent.VICTORY -> VibrationEffect.createWaveform(
                longArrayOf(0, 25, 40, 30, 40, 50),
                intArrayOf(0, 150, 0, 200, 0, 255),
                -1
            )
            ZynpathHapticEvent.DEFEAT -> VibrationEffect.createOneShot(35, 90)
            ZynpathHapticEvent.ERROR -> VibrationEffect.createWaveform(
                longArrayOf(0, 20, 30, 20),
                intArrayOf(0, 160, 0, 160),
                -1
            )
        }
        vib.vibrate(effect)
    }

    @Suppress("DEPRECATION")
    private fun executeLegacyHaptic(vib: Vibrator, event: ZynpathHapticEvent) {
        when (event) {
            ZynpathHapticEvent.PATH_START -> vib.vibrate(15)
            ZynpathHapticEvent.VALID_MOVE -> vib.vibrate(8)
            ZynpathHapticEvent.CHECKPOINT_REACHED -> vib.vibrate(25)
            ZynpathHapticEvent.INVALID_MOVE -> vib.vibrate(longArrayOf(0, 20, 30, 20), -1)
            ZynpathHapticEvent.UNDO -> vib.vibrate(12)
            ZynpathHapticEvent.RESET -> vib.vibrate(35)
            ZynpathHapticEvent.COMPLETION -> vib.vibrate(longArrayOf(0, 25, 40, 30, 40, 45), -1)
            ZynpathHapticEvent.BUTTON_CONFIRM -> vib.vibrate(10)
            ZynpathHapticEvent.SCREEN_OPEN -> vib.vibrate(8)
            ZynpathHapticEvent.REWARD_REVEALED -> vib.vibrate(longArrayOf(0, 20, 40, 25), -1)
            ZynpathHapticEvent.PLAYER_JOINED -> vib.vibrate(15)
            ZynpathHapticEvent.MATCH_STARTED -> vib.vibrate(30)
            ZynpathHapticEvent.VICTORY -> vib.vibrate(longArrayOf(0, 25, 40, 30, 40, 50), -1)
            ZynpathHapticEvent.DEFEAT -> vib.vibrate(35)
            ZynpathHapticEvent.ERROR -> vib.vibrate(longArrayOf(0, 20, 30, 20), -1)
        }
    }
}
