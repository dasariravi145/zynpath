# Zynpath Audio Asset Provenance & Licensing

## 1. Provenance Summary

All audio sound effects and ambient tracks utilized in Zynpath: Number Path Puzzle are **100% procedurally synthesized** at runtime using Kotlin mathematical sound wave synthesis (`com.zynpath.game.core.audio.sound.SyntheticSoundGenerator`).

No third-party binary audio samples, copyrighted sound libraries, or scraped game assets are bundled in the application APK.

---

## 2. Asset Manifest & Specifications

| Asset Name | Generation Method | Frequency / Waveform | ADSR / Duration | Provenance | License |
|---|---|---|---|---|---|
| `zynpath_sfx_path_start.wav` | Synthetic PCM 16-bit | Harmonic C5 (523 Hz) Sine | 120 ms exponential decay | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_valid_move.wav` | Synthetic PCM 16-bit | E5 (659 Hz) Sine + Overtones | 45 ms crisp wooden click | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_checkpoint_reached.wav` | Synthetic PCM 16-bit | Dual Harmonic G5 (784 Hz) + C6 (1046 Hz) | 220 ms ringing chime | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_invalid_move.wav` | Synthetic PCM 16-bit | Low D3 (146 Hz) Square/Sine blend | 100 ms damped thud | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_undo.wav` | Synthetic PCM 16-bit | A4 (440 Hz) Sine | 50 ms soft click | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_reset.wav` | Synthetic PCM 16-bit | Descending dual pitch G4->C4 | 140 ms sweep | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_hint_used.wav` | Synthetic PCM 16-bit | Shimmer C6 (1046 Hz) + E6 (1318 Hz) | 250 ms soft shimmer | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_puzzle_completed.wav` | Synthetic PCM 16-bit | 3-note major triad C5-E5-G5 | 450 ms celebration fanfare | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_puzzle_failed.wav` | Synthetic PCM 16-bit | Minor low descent Eb3->C3 | 280 ms somber tone | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_button_tap.wav` | Synthetic PCM 16-bit | Clean click 800 Hz | 20 ms micro-pulse | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_match_ready.wav` | Synthetic PCM 16-bit | Rising double tone A5->D6 | 160 ms alert chime | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_match_completed.wav` | Synthetic PCM 16-bit | Resolved major chord C5-G5-C6 | 400 ms decisive finish | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_sfx_invitation_received.wav` | Synthetic PCM 16-bit | Ascending arpeggio F5->A5->C6 | 220 ms bell | Original algorithmic code | MIT / Public Domain (Zynpath Project) |
| `zynpath_ambient_music.wav` | Synthetic PCM 16-bit | Ambient peaceful progression | 4000 ms seamless loop | Original algorithmic code | MIT / Public Domain (Zynpath Project) |

---

## 3. Commercial & Distribution Rights

- **Copyright**: Copyright (c) 2026 Zynpath Open Source Project.
- **License**: MIT License.
- **Redistribution**: Royalty-free, irrevocable, unencumbered by third-party digital rights or patent claims.
