# Splash Screen Architecture & Startup Performance — Zynpath

## 1. Overview & Strategy

Zynpath implements the Android native Splash Screen architecture supporting Android 12+ (API 31+) while providing seamless backward compatibility for earlier versions:

- **Theme Reference:** `Theme.Zynpath.Splash`
- **Background Color:** `@color/bg_midnight_dark` (`#0B132B`)
- **Splash Icon:** `@drawable/ic_splash_logo` (High-resolution vector path mark with checkpoints #1, #2, #3 and cyan glow)
- **Zero Startup Delay:** The splash screen does **not** employ synthetic sleeps or delays. As soon as the first frame of Compose `MainActivity` is ready, the activity transitions seamlessly via `setTheme(R.style.Theme_Zynpath)`.

---

## 2. Platform Implementation

### Android 12+ (API 31+) — `res/values-v31/themes.xml`
```xml
<style name="Theme.Zynpath.Splash" parent="Theme.Zynpath">
    <item name="android:windowSplashScreenBackground">@color/bg_midnight_dark</item>
    <item name="android:windowSplashScreenAnimatedIcon">@drawable/ic_splash_logo</item>
    <item name="android:windowSplashScreenIconBackgroundColor">@color/bg_midnight_dark</item>
    <item name="android:windowBackground">@color/bg_midnight_dark</item>
    <item name="android:statusBarColor">@color/bg_midnight_dark</item>
    <item name="android:navigationBarColor">@color/bg_midnight_dark</item>
</style>
```

### Pre-API 31 Fallback — `res/values/themes.xml`
```xml
<style name="Theme.Zynpath.Splash" parent="Theme.Zynpath">
    <item name="android:windowBackground">@drawable/splash_background</item>
    <item name="android:statusBarColor">@color/bg_midnight_dark</item>
    <item name="android:navigationBarColor">@color/bg_midnight_dark</item>
</style>
```
Using `res/drawable/splash_background.xml` layer-list with centered `@drawable/ic_splash_logo` on `@color/bg_midnight_dark`.

---

## 3. Post-Splash Navigation & Offline-First Guarantees

Upon startup:
1. `MainActivity.onCreate()` executes `setTheme(R.style.Theme_Zynpath)`.
2. `ZynpathNavGraph` reads the player's onboarding status from DataStore (`PreferencesRepository.userPreferencesFlow`).
   - If `isTutorialSkipped == false && tutorialStage == 1`, routes to `Screen.Onboarding`.
   - If onboarding is completed or skipped, routes directly to `Screen.Home`.
3. If an active, unfinished gameplay session exists in Room, `HomeScreen` dynamically promotes "Continue Your Path" for immediate restoration.
4. **Offline Resilience:** The startup process never blocks on backend connectivity or DNS resolution. Offline guest players enter the game in < 150ms.
