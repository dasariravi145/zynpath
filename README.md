# Zynpath: Number Path Puzzle

> **One path. Every number.**

[![Platform](https://img.shields.io/badge/Platform-Native%20Android-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin%20%7C%20Java%2017%2B-blue.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%7C%20Material%203-purple.svg)](https://developer.android.com/jetpack/compose)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%20%7C%20WebSocket-brightgreen.svg)](https://spring.io)

---

## 1. Project Overview

**Zynpath: Number Path Puzzle** is a native Android continuous number-path logic puzzle game. The objective is to draw one uninterrupted orthogonal path through a rectangular grid, starting at numbered checkpoint 1 and visiting all checkpoints in strictly ascending order, while covering **every single required cell** on the board without self-intersection or crossing walls.

### Key Highlights
- **100% Deterministic Orthogonal Path Logic**: Pure logic puzzle mechanic without RNG refills, matching, or gravity.
- **Guest-First Experience**: Play immediately after install with zero sign-in walls. Complete offline solo mode with Room and DataStore persistence.
- **Competitive Multiplayer**: Quick 1v1 Duels, Friend Duels, and 2–5 player Mini Leagues with real-time WebSocket match sync and authoritative server-side completion validation.
- **Cost-Conscious Architecture**: Solo play is 100% on-device; touch coordinates are never transmitted over the wire; ephemeral in-memory multiplayer match states eliminate redundant database writes.
- **Temporary Reaction System**: Expressive, polite communication using curated preset emojis and phrases without text chat storage overhead or moderation liabilities.
- **Fair Monetization**: Non-intrusive Google AdMob integration with Google Play Billing Premium subscriptions (₹99/month, ₹499/6-months). Zero ads during active gameplay; competitive hints are disabled for all players to guarantee competitive integrity.

---

## 2. Gameplay Summary

```
+---+---+---+---+
| 1 | · | · | 2 |
+---+---+---+---+
| · | · | · | · |
+---+---+---+---+
| 4 | · | · | 3 |
+---+---+---+---+
```

### The Non-Negotiable Rules
1. **Start at Checkpoint 1**: Every puzzle starts at checkpoint `1`.
2. **Ascending Checkpoint Order**: Checkpoints must be connected in strictly ascending order ($1 \to 2 \to 3 \to \dots \to N$).
3. **Strictly Orthogonal**: Movement is strictly horizontal or vertical between adjacent cells (no diagonals).
4. **Single Continuous Path**: The path cannot branch, split, or jump cells.
5. **Full Grid Coverage**: **Every required cell** on the grid must be covered exactly once.
6. **No Revisiting**: Cells cannot be visited more than once.
7. **No Crossing**: Paths cannot intersect or cross over themselves.
8. **No Wall Crossing**: Cells separated by an explicit wall obstacle cannot be directly connected.
9. **No Checkpoint Skipping**: All checkpoints must be included in the route.
10. **Dual Win Condition**: A level is solved **ONLY IF**:
    - All checkpoints are connected in strictly ascending order **AND**
    - **100% of required grid cells are covered**.
    *(Visiting all checkpoints without covering all cells is NOT a win. Covering all cells out of order is NOT a win).*

---

## 3. Technology Stack

### Mobile Client (Android)
- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVI/MVVM (Presentation, Domain, Data)
- **Local Persistence**: Room Database (level progress, stats) + Jetpack DataStore (settings, preferences)
- **Dependency Injection**: Hilt / Kotlin Inject
- **Networking**: OkHttp, Retrofit, Kotlinx Serialization, WebSocket Client
- **Authentication**: Firebase Authentication (Anonymous Guest $\to$ Google Sign-In / Facebook Login account linking)
- **Monetization**: Google Play Billing SDK + Google Mobile Ads (AdMob)

### Backend Services
- **Language & Runtime**: Java 17+ LTS
- **Framework**: Spring Boot 3.x
- **Real-Time Communication**: Spring WebSocket / STOMP
- **Data Persistence**: Spring Data JPA / Hibernate (PostgreSQL) or Cloud Firestore abstraction via Repository pattern
- **Security**: Spring Security + Firebase Admin SDK JWT token verification

---

## 4. Repository Structure

```
Zynpath/
├── android/                  # Native Android Kotlin application
│   ├── app/                  # Main Android application module
│   ├── core/                 # Core utilities, theme, and common components
│   │   ├── domain/           # Pure Kotlin puzzle engine & domain models
│   │   ├── data/             # Local database, preferences, repositories
│   │   └── ui/               # Design system, components, animations
│   └── features/             # Feature modules (solo, duel, league, profile)
├── backend/                  # Spring Boot 3 multiplayer service
│   ├── src/main/java/        # Game room, matchmaking, reaction relay, validation
│   └── src/test/java/        # Multiplayer integration & validation tests
├── docs/                     # Authoritative engineering documentation
│   ├── ARCHITECTURE.md       # High-level system architecture
│   ├── GAME_RULES.md         # Authoritative gameplay specification
│   ├── PUZZLE_ENGINE.md      # Pure Kotlin puzzle engine specification
│   ├── LEVEL_PROGRESSION.md  # World 1 to 6 level progression design
│   ├── ANDROID_ARCHITECTURE.md # Presentation, domain, data layer details
│   ├── MULTIPLAYER_ARCHITECTURE.md # WebSockets, rooms, leagues
│   ├── AUTHENTICATION.md     # Guest-first auth and account linking
│   ├── REACTION_SYSTEM.md    # Ephemeral in-memory reaction relays
│   ├── MONETIZATION.md       # AdMob & Google Play Billing model
│   ├── COST_OPTIMIZATION.md  # Cloud cost reduction & resource discipline
│   ├── LOCAL_MOBILE_TESTING.md # ADB, device deployment & testing guide
│   ├── DATABASE_DESIGN.md    # Room & backend entity schemas
│   ├── IMPLEMENTATION_PLAN.md # 50-Prompt master engineering plan
│   ├── TEST_PLAN.md          # Unit, integration & UI test strategies
│   └── PROGRESS.md           # Engineering progress & milestone tracker
├── infrastructure/           # Local Docker, environment templates, deployment configs
├── .gitignore                # Root gitignore for Android, Java, and IDEs
└── README.md                 # Project README
```

---

## 5. Development Roadmap Summary (50-Prompt Plan)

| Prompts | Phase | Focus Area | Status |
|---|---|---|---|
| **01** | Phase 1 | Project Foundation, Workspace Setup, Authoritative Specifications | **COMPLETED** |
| **02** | Phase 1 | Native Android Kotlin + Jetpack Compose Setup, Testing & Debug APK | **COMPLETED** |
| **03** | Phase 1 | Design System, Navigation Refinement, and Application Structure | **COMPLETED** |
| **04** | Phase 1 | Local Data Architecture, Settings, and Offline Progress Foundation | Pending |
| **05** | Phase 1 | Continuous Integration, Automated Linting, Git Pre-Commit Hooks | Pending |
| **06–12** | Phase 2 | Continuous-Path Puzzle Engine (Pure Kotlin, Solvers, Generators, Validators) | Pending |
| **13–18** | Phase 3 | Game UI, Custom Canvas Touch Controls, Path Rendering, Micro-Animations | Pending |
| **19–23** | Phase 4 | Level Progression, Worlds 1–6, Daily Challenges, Procedural Level Packs | Pending |
| **24–27** | Phase 5 | Guest-First Auth, Account Linking (Google/Facebook), Player Profiles | Pending |
| **28–32** | Phase 6 | Friends System, Invite Links, In-Memory Preset Reactions | Pending |
| **33–40** | Phase 7 | Real-Time Multiplayer: Quick Duel, Friend Duel, 2–5 Player Mini Leagues | Pending |
| **41–44** | Phase 8 | Monetization: AdMob Banners/Interstitials/Rewarded, Play Billing Premium | Pending |
| **45–48** | Phase 9 | Security Hardening, Input Validation, Performance Profiling, QA Matrix | Pending |
| **49–50** | Phase 10 | Release Preparation, Play Store Assets, CI/CD, Production Deployment | Pending |

---

## 6. Quick Start (Local Mobile Development)

1. **Verify Prerequisites**:
   - Java 17+ LTS installed (`java -version`).
   - Android SDK installed (`android-36` compile SDK).
2. **Build the Android Application**:
   ```powershell
   cd d:\Zynpath\android
   .\gradlew.bat testDebugUnitTest
   .\gradlew.bat assembleDebug
   ```
   *Generated Debug APK:* `android/app/build/outputs/apk/debug/app-debug.apk`
3. **Install on Physical Android Device via ADB**:
   ```powershell
   & "C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r d:\Zynpath\android\app\build\outputs\apk\debug\app-debug.apk
   ```
4. **Documentation Links**:
   - [docs/ANDROID_MODULES.md](file:///d:/Zynpath/docs/ANDROID_MODULES.md): Android package layout and dependency matrix.
   - [docs/DESIGN_SYSTEM.md](file:///d:/Zynpath/docs/DESIGN_SYSTEM.md): Design tokens, theme, and reusable components.
   - [docs/LOCAL_STORAGE.md](file:///d:/Zynpath/docs/LOCAL_STORAGE.md): Room entities, DAOs, and DataStore schema.
   - [docs/LOCAL_MOBILE_TESTING.md](file:///d:/Zynpath/docs/LOCAL_MOBILE_TESTING.md): Detailed device testing guide.
   - [docs/PROGRESS.md](file:///d:/Zynpath/docs/PROGRESS.md): Current engineering progress tracker.

---

## 7. License & Attribution

Copyright &copy; 2026 Zynpath. All rights reserved. Original logic puzzle game design and implementation.
