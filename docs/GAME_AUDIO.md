# Zynpath Game Audio System Architecture

## 1. Overview & Core Principles

The Zynpath Game Audio System delivers crisp, tactile, and non-intrusive auditory feedback across all game modes (Solo, Interactive Tutorial, Daily Challenge, Quick Duel, Friend Duel, and UI Navigation).

Audio is strictly **presentation-layer feedback**:
- It never modifies puzzle state, path geometry, checkpoint progression, timer countdowns, match outcomes, or leaderboard eligibility.
- It operates completely offline with zero network or remote asset dependencies.
- It respects system audio focus, device volume, and user preferences (`isSfxEnabled`, `isMusicEnabled`).

---

## 2. Audio Architecture

The audio architecture is organized into distinct responsibilities:

```
                          ┌──────────────────────────┐
                          │   ViewModel / Screen     │
                          └─────────────┬────────────┘
                                        │ emits ZynpathAudioEvent
                                        ▼
                          ┌──────────────────────────┐
                          │    ZynpathAudioManager   │
                          │   (Contract Interface)   │
                          └─────────────┬────────────┘
                                        │
             ┌──────────────────────────┴──────────────────────────┐
             ▼                                                     ▼
┌───────────────────────────┐                         ┌───────────────────────────┐
│        SoundPool          │                         │        MediaPlayer        │
│   (Low-Latency Gameplay)  │                         │ (Optional Ambient Music)  │
│ - Max 6 streams           │                         │ - Looping ambient stem    │
│ - Preloaded WAV assets    │                         │ - Audio focus aware       │
│ - Event throttling        │                         │ - Disabled by default     │
└─────────────┬─────────────┘                         └─────────────┬─────────────┘
              │                                                     │
              ▼                                                     ▼
┌───────────────────────────┐                         ┌───────────────────────────┐
│  SyntheticSoundGenerator  │                         │    Audio Focus Manager    │
│ (Procedural 16-bit PCM)   │                         │  (Duck / Pause / Resume)  │
└───────────────────────────┘                         └───────────────────────────┘
```

### Components:
1. **`ZynpathAudioEvent` (`com.zynpath.game.core.audio.model.ZynpathAudioEvent`)**:
   Enumeration of all 13 supported audio events:
   - `PATH_START`: Light ascending chime when beginning a valid path at Checkpoint 1.
   - `VALID_MOVE`: Crisp, wooden tick upon valid orthogonal cell entry.
   - `CHECKPOINT_REACHED`: Resonant harmonic bell when entering the required sequential checkpoint.
   - `INVALID_MOVE`: Muted low-frequency thud when crossing a wall or revisiting a cell.
   - `UNDO`: Subtle soft reverse click on single-step backtrack.
   - `RESET`: Distinct descending double sweep on board clear.
   - `HINT_USED`: Gentle shimmer upon rendering a solver recommendation.
   - `PUZZLE_COMPLETED`: Triumphant 3-note major fanfare upon verified full grid coverage.
   - `PUZZLE_FAILED`: Somber low tone when forfeit occurs or time expires.
   - `BUTTON_TAP`: Clean tactile micro-click on UI interaction.
   - `MATCH_READY`: Crisp alerting double-tone for multiplayer countdown.
   - `MATCH_COMPLETED`: Decisive final chord on authoritative multiplayer conclusion.
   - `INVITATION_RECEIVED`: Friendly rising chime when a friend challenge arrives.

2. **`SyntheticSoundGenerator` (`com.zynpath.game.core.audio.sound.SyntheticSoundGenerator`)**:
   Generates standard RIFF WAV files (16-bit PCM, 44.1 kHz, mono) using mathematical waveform synthesis (sine waves, harmonic overtones, and exponential ADSR envelopes). Stored in the app's private cache directory (`context.cacheDir/zynpath_sounds/`). Guarantees 100% offline availability without external binary bloat or copyright licensing issues.

3. **`ZynpathAudioManagerImpl` (`com.zynpath.game.core.audio.ZynpathAudioManagerImpl`)**:
   Singleton managing `SoundPool` initialization, stream preloading, audio focus acquisition/ducking, background music playback, and `DefaultLifecycleObserver` hooks.

4. **`NoOpAudioManager` (`com.zynpath.game.core.audio.NoOpAudioManager`)**:
   Lightweight fake for headless unit testing and preview environments.

---

## 3. High-Frequency Throttling & Deduplication

To prevent auditory clutter during fast dragging gestures:
- `VALID_MOVE` events are throttled to a minimum interval of **40 milliseconds**.
- `INVALID_MOVE` events are throttled to a minimum interval of **200 milliseconds**, preventing continuous buzzing when a finger lingers on an invalid cell or blocked edge.
- Recompositions in Jetpack Compose never trigger audio playback; sounds are triggered exclusively from ViewModel action handlers or one-time event channels.

---

## 4. Audio Focus & App Lifecycle Management

- **Lifecycle Awareness**: Implements `DefaultLifecycleObserver`.
  - `onStart`: Restores audio focus and resumes ambient music (if enabled).
  - `onStop`: Pauses background music and halts any sustained sound streams.
  - `onDestroy`: Releases audio resources, unloads `SoundPool`, and cleans up media players.
- **Audio Focus (`AudioManager.OnAudioFocusChangeListener`)**:
  - `AUDIOFOCUS_LOSS`: Releases audio focus and pauses playback.
  - `AUDIOFOCUS_LOSS_TRANSIENT`: Pauses background music during incoming phone calls or alarms.
  - `AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK`: Ducks music volume to 20% while another app plays brief audio (e.g., GPS navigation prompt).
  - `AUDIOFOCUS_GAIN`: Restores volume to 100% and resumes playback.

---

## 5. Background Music Foundation

- Disabled by default (`isMusicEnabled = false` in `UserPreferences`).
- Plays a gentle, looping ambient synthetic stem when enabled.
- Independent volume and master toggle in Player Settings.
- Never plays during initial app startup unless explicitly enabled by the user.

---

## 6. Competitive Fairness & Security

- Audio feedback never reveals hidden solution paths or opponent cell selections in Quick Duel or Friend Duel.
- Completion audio for competitive multiplayer matches is triggered only after the authoritative server response confirms the result (`ClientMatchState.COMPLETED`).
- Local completion sounds during duels are decoupled from server claims.

---

## 7. Performance & Resource Efficiency (Prompt 37)

- **SoundPool Resource Sharing**: Reuses a single shared `SoundPool` instance with a bounded stream limit (max 6 concurrent streams), eliminating the overhead of creating individual `MediaPlayer` instances per effect.
- **Pre-Decoded Audio Buffers**: Common sound samples (`VALID_MOVE`, `CHECKPOINT_REACHED`, `INVALID_MOVE`) are preloaded into memory on startup and kept in PCM buffers, preventing disk decoding latency during fast gameplay gestures.
- **Audio Lifecycle Cleanup**: Audio streams immediately pause or terminate when the application moves to the background (`onStop`), freeing hardware audio output channels and saving battery.
- **Rate-Throttled Playback**: High-frequency gameplay events (such as continuous dragging) enforce a minimum 40ms interval between `VALID_MOVE` sound triggers and 200ms between `INVALID_MOVE` triggers, preventing audio channel saturation and clipping.

---

## 8. Audio Accessibility & Hardware Fallbacks (Prompt 39)
- **Zero Auditory Dependency**: All puzzle events (path creation, checkpoints reached, move rejections, completions) are paired with visual cues and TalkBack semantics. Gameplay is 100% playable with audio muted.
- **Hardware Fallbacks**: If device audio services or `SoundPool` fail to initialize, the audio system falls back gracefully to `NoOpAudioManager` without crashing or interrupting gameplay.


