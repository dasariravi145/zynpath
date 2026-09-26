# Zynpath Android Modules & Package Architecture

**Document Status:** Authoritative  
**Target:** Native Android Project Structure  
**Package / Application ID:** `com.zynpath.game`  

---

## 1. Project Organization

The Android codebase is structured for scalability without excessive module fragmentation. The core architecture is established within the `app` module using strict internal package boundaries:

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/zynpath/game/
│   │   │   │   ├── ZynpathApp.kt                    # Application class (@HiltAndroidApp)
│   │   │   │   ├── MainActivity.kt                  # Single Activity with Compose
│   │   │   │   ├── core/
│   │   │   │   │   ├── puzzle/
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   ├── GridCoordinate.kt    # (row, col) coordinates & orthogonal checks
│   │   │   │   │   │   │   ├── WallEdge.kt          # Canonical blocked connection model
│   │   │   │   │   │   │   ├── PuzzleBoardState.kt  # Grid state, checkpoints & authoritative solver
│   │   │   │   │   │   │   └── SamplePuzzles.kt     # Verified 3x3 and 4x4 preview & tutorial boards
│   │   │   │   │   │   └── ui/
│   │   │   │   │   │       └── PuzzleBoard.kt       # High-performance Canvas puzzle board renderer
│   │   │   │   │   ├── designsystem/
│   │   │   │   │   │   ├── theme/
│   │   │   │   │   │   │   ├── Color.kt             # Dark Navy, Forest Mint, accents & alerts
│   │   │   │   │   │   │   ├── Type.kt              # Material 3 typography
│   │   │   │   │   │   │   ├── Spacing.kt           # Spacing and min touch target (48dp)
│   │   │   │   │   │   │   ├── Shapes.kt            # Corner shape tokens
│   │   │   │   │   │   │   ├── Elevation.kt         # Elevation levels
│   │   │   │   │   │   │   ├── AnimationTokens.kt   # Animation durations and easing
│   │   │   │   │   │   │   └── Theme.kt             # ZynpathTheme wrapper
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── ZynpathButton.kt     # Primary & Secondary styled buttons
│   │   │   │   │   │       ├── ScreenHeader.kt      # Unified back navigation header
│   │   │   │   │   │       ├── ZynpathTopBar.kt     # Material 3 TopAppBar with dark theme
│   │   │   │   │   │       ├── ZynpathBottomNavigation.kt # Bottom navigation bar
│   │   │   │   │   │       ├── ZynpathModeCard.kt   # Game mode selection cards
│   │   │   │   │   │       ├── ZynpathLevelCard.kt  # Level grid card with 0-3 stars & lock state
│   │   │   │   │   │       ├── ZynpathWorldCard.kt  # 6-world selection card with progress bar
│   │   │   │   │   │       ├── PlayerAvatarBadge.kt # Guest player badge & avatar circle
│   │   │   │   │   │       ├── FeatureCard.kt       # Interactive game mode cards
│   │   │   │   │   │       ├── StatusBadge.kt       # Status indicators & pill chips
│   │   │   │   │   │       ├── ZynpathFeedbackState.kt # Loading, Error, Empty states
│   │   │   │   │   │       ├── ZynpathConfirmationDialog.kt # Accessible confirmation dialog
│   │   │   │   │   │       └── ZynpathReactionPicker.kt # Preset reaction picker (7 phrases/emojis)
│   │   │   │   │   ├── datastore/
│   │   │   │   │   │   ├── UserPreferences.kt       # Preference model data class
│   │   │   │   │   │   └── PreferencesRepository.kt # DataStore repository interface & impl
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   │   ├── LevelProgressEntity.kt
│   │   │   │   │   │   │   ├── PlayerStatsEntity.kt
│   │   │   │   │   │   │   └── DailyChallengeEntity.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── LevelProgressDao.kt
│   │   │   │   │   │   │   ├── PlayerStatsDao.kt
│   │   │   │   │   │   │   └── DailyChallengeDao.kt
│   │   │   │   │   │   └── ZynpathDatabase.kt       # Room Database definition
│   │   │   │   │   └── di/
│   │   │   │   │       ├── AppModule.kt             # DataStore & app dispatchers
│   │   │   │   │       └── DatabaseModule.kt        # Room DB & DAO providers
│   │   │   │   └── feature/
│   │   │   │       ├── navigation/
│   │   │   │       │   ├── Screen.kt                # Centralized route hierarchy with parameters
│   │   │   │       │   └── NavGraph.kt              # Central NavHost routing (15 destinations)
│   │   │   │       ├── splash/
│   │   │   │       │   ├── SplashScreen.kt          # Animated brand intro
│   │   │   │       │   └── SplashViewModel.kt       # Startup routing logic
│   │   │   │       ├── onboarding/
│   │   │   │       │   ├── OnboardingScreen.kt      # 5-step tutorial slides
│   │   │   │       │   └── OnboardingViewModel.kt   # Slide coordinator & persistence
│   │   │   │       ├── tutorial/
│   │   │   │       │   └── TutorialScreen.kt        # 6-step interactive visual board tutorial
│   │   │   │       ├── home/
│   │   │   │       │   ├── HomeScreen.kt            # Home dashboard & Solo Play primary CTA
│   │   │   │       │   └── HomeViewModel.kt         # Guest state & stats
│   │   │   │       ├── world/
│   │   │   │       │   ├── WorldSelectionScreen.kt  # 6-world selection grid
│   │   │   │       │   └── WorldSelectionViewModel.kt # World unlocks & progress aggregation
│   │   │   │       ├── level/
│   │   │   │       │   ├── LevelSelectionScreen.kt  # Data-driven level grid
│   │   │   │       │   └── LevelSelectionViewModel.kt # Level status, stars & unlock logic
│   │   │   │       ├── gameplay/
│   │   │   │       │   └── GameplayShellScreen.kt   # Gameplay shell with stats & PuzzleBoard
│   │   │   │       ├── settings/
│   │   │   │       │   ├── SettingsScreen.kt        # Audio, haptic & theme toggles
│   │   │   │       │   └── SettingsViewModel.kt     # Preferences StateFlow
│   │   │   │       ├── premium/
│   │   │   │       │   └── PremiumScreen.kt         # Premium presentation (₹99/mo, ₹499/6mo)
│   │   │   │       └── placeholder/
│   │   │   │           └── DevStateScreen.kt        # Honest roadmap phase indicators
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   ├── colors.xml
│   │   │       │   └── themes.xml
│   │   │       ├── xml/
│   │   │       │   └── network_security_config.xml
│   │   │       └── drawable/
│   │   │           ├── ic_launcher_foreground.xml
│   │   │           └── ic_launcher_background.xml
│   │   └── test/
│   │       └── java/com/zynpath/game/
│   │           ├── fake/FakePreferencesRepository.kt
│   │           ├── SplashViewModelTest.kt
│   │           ├── OnboardingViewModelTest.kt
│   │           ├── SettingsViewModelTest.kt
│   │           ├── RoomEntityTest.kt
│   │           ├── PuzzleBoardModelTest.kt
│   │           ├── SamplePuzzleValidityTest.kt
│   │           ├── WorldSelectionTest.kt
│   │           └── NavigationDestinationsTest.kt
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 2. Dependency Matrix

| Library / Tool | Target Version |
|---|---|
| **Android Gradle Plugin (AGP)** | `9.2.1` |
| **Gradle** | `9.4.1` |
| **Kotlin** | `2.2.10` |
| **Kotlin KSP** | `2.2.10-2.0.2` |
| **Compose Compiler Plugin** | Built into Kotlin 2.2.10 |
| **Compose BOM** | `2026.02.01` |
| **Material 3** | `1.3.1` (via BOM) |
| **Hilt (Dagger)** | `2.59.2` |
| **Room Database** | `2.8.4` |
| **Preferences DataStore** | `1.1.2` |
| **Navigation Compose** | `2.9.7` |
| **Lifecycle Runtime** | `2.9.4` |
| **Coroutines** | `1.10.1` |

---

## 3. Build Configuration

- **Minimum SDK:** 24 (Android 7.0 Nougat — covering >98% of active devices worldwide)
- **Compile SDK:** 36 (Android 16 preview platform)
- **Target SDK:** 36
- **Java Compatibility:** Java 17 LTS (`JavaVersion.VERSION_17`)
- **JVM Toolchain:** Java 17 (`jvmToolchain(17)`)
