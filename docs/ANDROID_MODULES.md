# Zynpath Android Modules & Package Architecture

**Document Status:** Authoritative  
**Target:** Native Android Project Structure  
**Package / Application ID:** `com.zynpath.game`  

---

## 1. Project Organization

The Android codebase is structured for scalability without excessive module fragmentation. In Phase 1, the core architecture is established within the `app` module using strict internal package boundaries:

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/zynpath/game/
│   │   │   │   ├── ZynpathApp.kt                    # Application class (@HiltAndroidApp)
│   │   │   │   ├── MainActivity.kt                  # Single Activity with Compose
│   │   │   │   ├── core/
│   │   │   │   │   ├── designsystem/
│   │   │   │   │   │   ├── theme/
│   │   │   │   │   │   │   ├── Color.kt             # Dark Navy & Forest Mint color tokens
│   │   │   │   │   │   │   ├── Type.kt              # Material 3 typography
│   │   │   │   │   │   │   └── Theme.kt             # ZynpathTheme wrapper
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── ZynpathButton.kt     # Primary & Secondary styled buttons
│   │   │   │   │   │       ├── ScreenHeader.kt      # Unified back navigation header
│   │   │   │   │   │       ├── PlayerAvatarBadge.kt # Guest player badge
│   │   │   │   │   │       ├── FeatureCard.kt       # Interactive game mode cards
│   │   │   │   │   │       └── StatusBadge.kt       # Offline / status indicators
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
│   │   │   │       │   ├── Screen.kt                # Type-safe navigation routes
│   │   │   │       │   └── NavGraph.kt              # Central NavHost routing
│   │   │   │       ├── splash/
│   │   │   │       │   ├── SplashScreen.kt          # Animated brand intro
│   │   │   │       │   └── SplashViewModel.kt       # Startup routing logic
│   │   │   │       ├── onboarding/
│   │   │   │       │   ├── OnboardingScreen.kt      # 5-step interactive tutorial
│   │   │   │       │   └── OnboardingViewModel.kt   # Slide coordinator & persistence
│   │   │   │       ├── home/
│   │   │   │       │   ├── HomeScreen.kt            # Home dashboard & Solo CTA
│   │   │   │       │   └── HomeViewModel.kt         # Guest state & stats
│   │   │   │       ├── settings/
│   │   │   │       │   ├── SettingsScreen.kt        # Functional audio & haptic toggles
│   │   │   │       │   └── SettingsViewModel.kt     # Preferences StateFlow
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
│   │           └── RoomEntityTest.kt
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
| **Compose Compiler Plugin** | Built into Kotlin 2.x |
| **Compose BOM** | `2026.02.01` |
| **Material 3** | `1.3.1` (via BOM) |
| **Hilt (Dagger)** | `2.59.2` |
| **Room Database** | `2.8.4` |
| **Preferences DataStore** | `1.1.2` |
| **Navigation Compose** | `2.8.8` |
| **Lifecycle Runtime** | `2.10.0` |
| **Coroutines** | `1.10.1` |

---

## 3. Build Configuration

- **Minimum SDK:** 24 (Android 7.0 Nougat — covering >98% of active devices worldwide)
- **Compile SDK:** 35
- **Target SDK:** 35 (Android 15)
- **Java Compatibility:** Java 17 LTS (`JavaVersion.VERSION_17`)
- **JVM Toolchain:** Java 17 (`jvmToolchain(17)`)
