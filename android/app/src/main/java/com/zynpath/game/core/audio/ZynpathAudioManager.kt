package com.zynpath.game.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.audio.sound.SyntheticSoundGenerator
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized game audio manager contract.
 *
 * Implements Prompt 34 Sections 6-11, 24-30:
 * - Decouples audio events from UI recomposition.
 * - Manages low-latency SoundPool for short gameplay effects.
 * - Handles optional background music, audio focus, and app lifecycle state.
 */
interface ZynpathAudioManager : DefaultLifecycleObserver {
    /**
     * Triggers sound playback for the given [event].
     * Respects user SFX preference, high-frequency throttling, and audio focus.
     */
    fun playEvent(event: ZynpathAudioEvent, volumeMultiplier: Float = 1.0f)

    /**
     * Explicitly starts or resumes optional ambient background music if enabled in settings.
     */
    fun startMusic()

    /**
     * Explicitly pauses background music (e.g. app backgrounded or modal opened).
     */
    fun pauseMusic()

    /**
     * Explicitly stops background music.
     */
    fun stopMusic()

    /**
     * Cleans up all audio resources (SoundPool, MediaPlayer, audio focus).
     */
    fun release()
}

@Singleton
class ZynpathAudioManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ZynpathAudioManager {

    private val tag = "ZynpathAudioManager"
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    // User preferences cache
    private val isSfxEnabled = AtomicBoolean(true)
    private val isMusicEnabled = AtomicBoolean(false)

    // SoundPool resources
    private var soundPool: SoundPool? = null
    private val soundIdMap = ConcurrentHashMap<ZynpathAudioEvent, Int>()
    private val loadedSoundIds = ConcurrentHashMap.newKeySet<Int>()

    // Event throttling timestamps
    private val lastValidMoveTime = AtomicLong(0L)
    private val lastInvalidMoveTime = AtomicLong(0L)

    // Audio Focus
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private val hasAudioFocus = AtomicBoolean(false)

    // Optional Background Music
    private var mediaPlayer: MediaPlayer? = null
    private val isMusicPlaying = AtomicBoolean(false)
    private val musicVolume = 0.4f

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d(tag, "Audio focus permanently lost")
                hasAudioFocus.set(false)
                pauseMusicInternal()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(tag, "Audio focus transiently lost")
                hasAudioFocus.set(false)
                pauseMusicInternal()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d(tag, "Audio focus ducked")
                mediaPlayer?.setVolume(musicVolume * 0.25f, musicVolume * 0.25f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(tag, "Audio focus gained")
                hasAudioFocus.set(true)
                mediaPlayer?.setVolume(musicVolume, musicVolume)
                if (isMusicEnabled.get()) {
                    resumeMusicInternal()
                }
            }
        }
    }

    init {
        initializeSoundPool()
        observePreferences()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
            } else {
                Log.w(tag, "Failed to load sample ID: $sampleId, status=$status")
            }
        }

        // Preload all 13 sound effects asynchronously on background thread
        scope.launch {
            preloadSoundEffects()
        }
    }

    private fun observePreferences() {
        scope.launch {
            preferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                val sfxChanged = isSfxEnabled.getAndSet(prefs.isSfxEnabled) != prefs.isSfxEnabled
                val musicChanged = isMusicEnabled.getAndSet(prefs.isMusicEnabled) != prefs.isMusicEnabled

                if (musicChanged) {
                    if (prefs.isMusicEnabled) {
                        startMusic()
                    } else {
                        stopMusic()
                    }
                }
            }
        }
    }

    private fun preloadSoundEffects() {
        try {
            val sp = soundPool ?: return
            val cacheDir = context.cacheDir

            for (event in ZynpathAudioEvent.values()) {
                val soundFile = SyntheticSoundGenerator.createOrGetSoundFile(cacheDir, event)
                val soundId = sp.load(soundFile.absolutePath, 1)
                soundIdMap[event] = soundId
            }
            Log.d(tag, "Preloaded ${soundIdMap.size} sound effects successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to preload sound effects: ${e.message}", e)
        }
    }

    override fun playEvent(event: ZynpathAudioEvent, volumeMultiplier: Float) {
        if (!isSfxEnabled.get()) return

        // High-frequency move throttling to avoid acoustic saturation
        val now = System.currentTimeMillis()
        when (event) {
            ZynpathAudioEvent.VALID_MOVE -> {
                val last = lastValidMoveTime.get()
                if (now - last < 40L) return
                lastValidMoveTime.set(now)
            }
            ZynpathAudioEvent.INVALID_MOVE -> {
                val last = lastInvalidMoveTime.get()
                if (now - last < 200L) return
                lastInvalidMoveTime.set(now)
            }
            else -> {}
        }

        val soundId = soundIdMap[event] ?: return
        if (!loadedSoundIds.contains(soundId)) {
            // Sample still loading into SoundPool, graceful skip
            return
        }

        try {
            val volume = (1.0f * volumeMultiplier).coerceIn(0.0f, 1.0f)
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        } catch (e: Exception) {
            Log.w(tag, "Failed to play audio event $event: ${e.message}")
        }
    }

    // --- Background Music Management ---

    override fun startMusic() {
        if (!isMusicEnabled.get()) return
        if (isMusicPlaying.get()) return

        requestAudioFocus()
        resumeMusicInternal()
    }

    override fun pauseMusic() {
        pauseMusicInternal()
    }

    override fun stopMusic() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isMusicPlaying.set(false)
            abandonAudioFocus()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping music: ${e.message}")
        }
    }

    private fun resumeMusicInternal() {
        try {
            if (mediaPlayer == null) {
                // Synthesize or prepare ambient track
                val musicFile = SyntheticSoundGenerator.createOrGetSoundFile(context.cacheDir, ZynpathAudioEvent.MATCH_COMPLETED)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(musicFile.absolutePath)
                    isLooping = true
                    setVolume(musicVolume, musicVolume)
                    prepare()
                }
            }
            mediaPlayer?.start()
            isMusicPlaying.set(true)
        } catch (e: Exception) {
            Log.w(tag, "Failed to start background music: ${e.message}")
        }
    }

    private fun pauseMusicInternal() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
            isMusicPlaying.set(false)
        } catch (e: Exception) {
            Log.w(tag, "Error pausing music: ${e.message}")
        }
    }

    private fun requestAudioFocus() {
        val am = audioManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioFocusRequest = request
            val res = am.requestAudioFocus(request)
            hasAudioFocus.set(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        } else {
            @Suppress("DEPRECATION")
            val res = am.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
            hasAudioFocus.set(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        }
    }

    private fun abandonAudioFocus() {
        val am = audioManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { am.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            am.abandonAudioFocus(audioFocusChangeListener)
        }
        hasAudioFocus.set(false)
    }

    // --- Lifecycle Management ---

    override fun onStart(owner: LifecycleOwner) {
        if (isMusicEnabled.get()) {
            startMusic()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        pauseMusicInternal()
        soundPool?.autoPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    override fun release() {
        stopMusic()
        try {
            soundPool?.release()
            soundPool = null
            soundIdMap.clear()
            loadedSoundIds.clear()
        } catch (e: Exception) {
            Log.w(tag, "Error releasing SoundPool: ${e.message}")
        }
    }
}
