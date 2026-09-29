package com.zynpath.game.core.haptics

import com.zynpath.game.core.haptics.model.ZynpathHapticEvent

/**
 * No-op implementation of [ZynpathHapticManager] for unit testing and headless environments.
 */
class NoOpHapticManager : ZynpathHapticManager {
    val recordedEvents = mutableListOf<ZynpathHapticEvent>()

    override fun performHaptic(event: ZynpathHapticEvent) {
        recordedEvents.add(event)
    }
}
