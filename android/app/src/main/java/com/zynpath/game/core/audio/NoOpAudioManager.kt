package com.zynpath.game.core.audio

import com.zynpath.game.core.audio.model.ZynpathAudioEvent

/**
 * No-op implementation of [ZynpathAudioManager] for unit testing and headless environments.
 */
class NoOpAudioManager : ZynpathAudioManager {
    val recordedEvents = mutableListOf<ZynpathAudioEvent>()

    override fun playEvent(event: ZynpathAudioEvent, volumeMultiplier: Float) {
        recordedEvents.add(event)
    }

    override fun startMusic() {}
    override fun pauseMusic() {}
    override fun stopMusic() {}
    override fun release() {}
}
