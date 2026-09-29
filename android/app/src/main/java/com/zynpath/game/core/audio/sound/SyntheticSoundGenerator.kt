package com.zynpath.game.core.audio.sound

import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural audio synthesizer generating clean, high-fidelity, non-intrusive sound effects.
 *
 * Implements Prompt 34 Sections 8, 9, 10 & 53:
 * - Generates standard 16-bit PCM mono 44.1kHz WAV audio in-memory.
 * - Uses soft musical harmonics and exponential ADSR envelopes to prevent harsh clicks.
 * - Guarantees 100% offline, zero-external-dependency, fully-licensed sound on any Android device.
 */
object SyntheticSoundGenerator {

    private const val SAMPLE_RATE = 44100

    /**
     * Synthesizes and caches a WAV file for the specified [event] into [cacheDir].
     * Returns the cached [File] ready for [android.media.SoundPool.load].
     */
    fun createOrGetSoundFile(cacheDir: File, event: ZynpathAudioEvent): File {
        val soundDir = File(cacheDir, "zynpath_sounds").apply { mkdirs() }
        val soundFile = File(soundDir, "${event.name.lowercase()}.wav")
        if (soundFile.exists() && soundFile.length() > 44) {
            return soundFile
        }

        val wavBytes = generateWavBytes(event)
        FileOutputStream(soundFile).use { it.write(wavBytes) }
        return soundFile
    }

    /**
     * Synthesizes raw WAV byte stream for [event].
     */
    fun generateWavBytes(event: ZynpathAudioEvent): ByteArray {
        val samples = when (event) {
            ZynpathAudioEvent.PATH_START -> generateRisingChime(startFreq = 523.25, endFreq = 659.25, durationMs = 120)
            ZynpathAudioEvent.VALID_MOVE -> generateOrganicTick(freq = 880.0, durationMs = 35)
            ZynpathAudioEvent.CHECKPOINT_REACHED -> generateHarmonicBell(fundamentalFreq = 783.99, durationMs = 180)
            ZynpathAudioEvent.INVALID_MOVE -> generateMutedThud(freq = 160.0, durationMs = 85)
            ZynpathAudioEvent.UNDO -> generateDescendingTick(startFreq = 440.0, endFreq = 293.66, durationMs = 50)
            ZynpathAudioEvent.RESET -> generateBrushSweep(startFreq = 587.33, endFreq = 220.0, durationMs = 140)
            ZynpathAudioEvent.HINT_USED -> generateShimmer(startFreq = 880.0, endFreq = 1318.51, durationMs = 220)
            ZynpathAudioEvent.PUZZLE_COMPLETED -> generateVictoryArpeggio()
            ZynpathAudioEvent.PUZZLE_FAILED -> generateDescendingTone(startFreq = 349.23, endFreq = 220.0, durationMs = 260)
            ZynpathAudioEvent.BUTTON_TAP -> generateClick(freq = 1200.0, durationMs = 20)
            ZynpathAudioEvent.MATCH_READY -> generateDualGong(freq1 = 440.0, freq2 = 880.0, durationMs = 320)
            ZynpathAudioEvent.MATCH_COMPLETED -> generateFanfareChime()
            ZynpathAudioEvent.INVITATION_RECEIVED -> generateDoorbellChime()
            ZynpathAudioEvent.SCREEN_OPEN -> generateRisingChime(startFreq = 440.0, endFreq = 554.37, durationMs = 80)
            ZynpathAudioEvent.REWARD_REVEALED -> generateHarmonicBell(fundamentalFreq = 1046.50, durationMs = 240)
            ZynpathAudioEvent.PLAYER_JOINED -> generateDoorbellChime()
            ZynpathAudioEvent.PLAYER_LEFT -> generateDescendingTick(startFreq = 440.0, endFreq = 329.63, durationMs = 90)
            ZynpathAudioEvent.MATCH_STARTED -> generateDualGong(freq1 = 523.25, freq2 = 659.25, durationMs = 260)
            ZynpathAudioEvent.VICTORY -> generateVictoryArpeggio()
            ZynpathAudioEvent.DEFEAT -> generateDescendingTone(startFreq = 392.0, endFreq = 246.94, durationMs = 300)
            ZynpathAudioEvent.ERROR -> generateMutedThud(freq = 140.0, durationMs = 90)
        }

        return pcmToWav(samples)
    }

    // --- Synthetic Audio Waveform Builders ---

    private fun generateOrganicTick(freq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val envelope = exp(-progress * 8.0) // Steep exponential decay
            val wave = sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 12000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateRisingChime(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = if (progress < 0.1) progress / 0.1 else exp(-(progress - 0.1) * 3.5)
            val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 14000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateHarmonicBell(fundamentalFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val attack = if (progress < 0.05) progress / 0.05 else 1.0
            val decay = exp(-progress * 4.0)
            val envelope = attack * decay
            // Fundamental + 2nd harmonic + 3rd harmonic
            val wave = sin(2.0 * PI * fundamentalFreq * t) +
                0.4 * sin(2.0 * PI * (fundamentalFreq * 2.0) * t) +
                0.15 * sin(2.0 * PI * (fundamentalFreq * 3.0) * t)
            buffer[i] = (wave * envelope * 15000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateMutedThud(freq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val envelope = exp(-progress * 10.0) // very fast damping
            val wave = sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 0.5) * t)
            buffer[i] = (wave * envelope * 11000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateDescendingTick(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = exp(-progress * 7.0)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 11000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateBrushSweep(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * (progress * progress)
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = sin(PI * progress) // smooth bell envelope
            val wave = sin(2.0 * PI * freq * t) + 0.2 * sin(4.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 12000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateShimmer(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val t = i.toDouble() / SAMPLE_RATE
            val freq = startFreq + (endFreq - startFreq) * progress
            val envelope = if (progress < 0.15) progress / 0.15 else exp(-(progress - 0.15) * 3.5)
            val wave = sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 1.5) * t)
            buffer[i] = (wave * envelope * 13000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateVictoryArpeggio(): ShortArray {
        // Triumphant 4-note ascending chord: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
        val noteDurMs = 120
        val totalDurMs = 520
        val totalSamples = (SAMPLE_RATE * (totalDurMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        for (i in 0 until totalSamples) {
            val timeMs = (i.toDouble() / SAMPLE_RATE) * 1000.0
            var mixedSample = 0.0
            for ((noteIndex, freq) in notes.withIndex()) {
                val noteStartMs = noteIndex * 90.0
                if (timeMs >= noteStartMs) {
                    val noteElapsed = (timeMs - noteStartMs) / 1000.0
                    val decay = exp(-noteElapsed * 4.5)
                    val wave = sin(2.0 * PI * freq * noteElapsed) + 0.25 * sin(4.0 * PI * freq * noteElapsed)
                    mixedSample += wave * decay * 8000.0
                }
            }
            buffer[i] = mixedSample.toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateDescendingTone(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = exp(-progress * 4.0)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 12000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateClick(freq: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = exp(-progress * 12.0)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * envelope * 12000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateDualGong(freq1: Double, freq2: Double, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val envelope = exp(-progress * 3.5)
            val wave = 0.6 * sin(2.0 * PI * freq1 * t) + 0.4 * sin(2.0 * PI * freq2 * t)
            buffer[i] = (wave * envelope * 14000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateFanfareChime(): ShortArray {
        val notes = doubleArrayOf(587.33, 739.99, 880.0) // D5, F#5, A5
        val totalDurMs = 400
        val totalSamples = (SAMPLE_RATE * (totalDurMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val timeMs = (i.toDouble() / SAMPLE_RATE) * 1000.0
            var mixedSample = 0.0
            for ((noteIndex, freq) in notes.withIndex()) {
                val noteStartMs = noteIndex * 70.0
                if (timeMs >= noteStartMs) {
                    val noteElapsed = (timeMs - noteStartMs) / 1000.0
                    val decay = exp(-noteElapsed * 5.0)
                    val wave = sin(2.0 * PI * freq * noteElapsed)
                    mixedSample += wave * decay * 9000.0
                }
            }
            buffer[i] = mixedSample.toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateDoorbellChime(): ShortArray {
        val totalDurMs = 350
        val totalSamples = (SAMPLE_RATE * (totalDurMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val timeMs = (i.toDouble() / SAMPLE_RATE) * 1000.0
            val t1 = (timeMs) / 1000.0
            val t2 = (timeMs - 120.0) / 1000.0

            val sample1 = if (timeMs < 120.0) sin(2.0 * PI * 659.25 * t1) * exp(-t1 * 6.0) else 0.0
            val sample2 = if (timeMs >= 120.0) sin(2.0 * PI * 880.0 * t2) * exp(-t2 * 4.5) else 0.0

            buffer[i] = ((sample1 + sample2) * 13000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    // --- Standard PCM to 16-bit Mono RIFF WAV Conversion ---

    private fun pcmToWav(pcmSamples: ShortArray): ByteArray {
        val byteDataSize = pcmSamples.size * 2
        val totalFileSize = byteDataSize + 36

        val out = ByteArrayOutputStream(totalFileSize + 8)

        // 1. RIFF Header
        out.write("RIFF".toByteArray())
        writeInt(out, totalFileSize)
        out.write("WAVE".toByteArray())

        // 2. fmt Sub-chunk
        out.write("fmt ".toByteArray())
        writeInt(out, 16) // SubChunk1Size (16 for PCM)
        writeShort(out, 1) // AudioFormat (1 for PCM)
        writeShort(out, 1) // NumChannels (1 for Mono)
        writeInt(out, SAMPLE_RATE) // SampleRate
        writeInt(out, SAMPLE_RATE * 2) // ByteRate (SampleRate * NumChannels * BitsPerSample/8)
        writeShort(out, 2) // BlockAlign (NumChannels * BitsPerSample/8)
        writeShort(out, 16) // BitsPerSample (16 bits)

        // 3. data Sub-chunk
        out.write("data".toByteArray())
        writeInt(out, byteDataSize)

        for (sample in pcmSamples) {
            writeShort(out, sample.toInt())
        }

        return out.toByteArray()
    }

    private fun writeInt(out: ByteArrayOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
        out.write((value shr 16) and 0xFF)
        out.write((value shr 24) and 0xFF)
    }

    private fun writeShort(out: ByteArrayOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }
}
