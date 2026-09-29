# Zynpath Player Settings Architecture

## 1. Overview
The Zynpath Player Settings experience unifies all player configuration into an accessible, structured hierarchy while strictly maintaining the game's **guest-first, offline-first** design. 

Preferences are strictly segregated between **device-local preferences** (stored locally in encrypted/unencrypted Android Jetpack DataStore) and **account-synchronized preferences** (stored on the Spring Boot backend and mirrored locally).

---

## 2. Setting Classification & Ownership

| Setting Key | Category | Scope / Storage | Default | Description |
|---|---|---|---|---|
| `isSfxEnabled` | Sound & Haptics | Device-Local (DataStore) | `true` | Sound effects for moves, connections, chimes |
| `isMusicEnabled` | Sound & Haptics | Device-Local (DataStore) | `true` | Ambient background music track |
| `isHapticsEnabled` | Sound & Haptics | Device-Local (DataStore) | `true` | Tactile pulses on move progression & undo |
| `isTapInputMode` | Gameplay | Device-Local (DataStore) | `false` | Tap step-by-step cells vs drag drawing |
| `touchSensitivity`| Gameplay | Device-Local (DataStore) | `1.0f` | Drag gesture sensitivity multiplier (0.5x - 2.0x) |
| `isReducedMotion`| Accessibility | Device-Local (DataStore) | `false` | Suppresses particle bursts & pulse animations |
| `isHighContrast` | Accessibility | Device-Local (DataStore) | `false` | High-contrast borders & path highlight colors |
| `equippedThemeId`| Appearance | Local + Account Sync | `theme_classic_midnight` | Board theme |
| `equippedPathEffectId` | Appearance | Local + Account Sync | `path_solid_glow` | Path trace effect |
| `equippedAvatarFrameId` | Appearance | Local + Account Sync | `frame_default_slate` | Player profile frame |
| `isFriendAlertsEnabled` | Notifications | Local + Account Sync | `true` | Push & in-app alerts for friend requests |
| `isMultiplayerAlertsEnabled` | Notifications | Local + Account Sync | `true` | Alerts for duel invites & room starts |
| `isDailyReminderEnabled` | Notifications | Device-Local (DataStore) | `false` | Explicit opt-in local daily reminder |
| `profileVisibility` | Privacy | Account Synchronized | `PUBLIC` | `PUBLIC`, `FRIENDS_ONLY`, `PRIVATE` |
| `allowZynpathIdSearch` | Privacy | Account Synchronized | `true` | Searchable by public Zynpath ID |
| `allowFriendRequests` | Privacy | Account Synchronized | `true` | Accepts incoming requests & duel invites |
| `tutorialStage` | Onboarding | Device-Local (DataStore) | `1` | Last active interactive tutorial stage (1..7) |
| `isTutorialSkipped` | Onboarding | Device-Local (DataStore) | `false` | True if player skipped onboarding tutorial |
| `seenFeatureTips` | Discovery | Device-Local (DataStore) | `emptySet()` | Contextual feature IDs and world celebration IDs (`world_celebrated_{worldId}`) |

---

## 3. Unified Sections

1. **GAMEPLAY**: Input mode (Continuous Drag vs Discrete Tap-to-Move), tutorial guidance toggle, "How to Play" interactive tutorial replay entry, touch sensitivity adjustment (0.5x - 2.0x), language selection.
2. **APPEARANCE & ACCESSIBILITY**: Theme selection, reduced-motion toggle (`isReducedMotion`), high-contrast visibility mode (`isHighContrast`), TalkBack virtual cell grid support, hardware keyboard/D-pad navigation support, adaptive responsive layouts (landscape side-by-side, tablet constraints).
3. **SOUND & HAPTICS**: Master SFX toggle (`BUTTON_TAP`, `VALID_MOVE`, `CHECKPOINT_REACHED`, `PUZZLE_COMPLETED`), ambient background music toggle (disabled by default, lifecycle-managed), tactile haptic feedback toggle (tiered Android vibration, throttled moves, non-punitive invalid move pulses). Safe hardware fallbacks on devices lacking vibrator motors or audio outputs.
4. **NOTIFICATIONS & REMINDERS**: Category alerts (Friends, Multiplayer, Daily Challenge), local reminder time picker, Notification Center entry.
5. **PRIVACY & DISCOVERY**: Visibility tier indicator, discovery toggles, blocked player management link.
6. **ACCOUNT & SECURITY**: Guest identity vs authenticated session (Google / Facebook), linked providers list, unlinking safety, secure sign-out.
7. **PREMIUM PASS**: Subscription entitlement indicator, deep-link to Google Play Subscription Center, purchase restoration.
8. **DATA & STORAGE**: Disposable cache cleaner, portable JSON data export foundation ("Request My Data"), self-service account deletion (with Google Play subscription reminder).
9. **ABOUT ZYNPATH**: Version, engine specifications, privacy policy status, terms of service status.

---

## 4. Guest Experience Preservation
No authentication is required to modify gameplay, appearance, sound, or local accessibility settings. A guest player can enjoy the complete offline puzzle experience without signing in.
