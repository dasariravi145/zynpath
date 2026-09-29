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
- **Framework**: Spring Boot 3.x (Modular Monolith)
- **Real-Time Communication**: Spring WebSocket (`/ws/multiplayer`) with authenticated sessions
- **Data Persistence**: Spring Data JPA / Hibernate on PostgreSQL with versioned Flyway migrations (`V1`–`V6`)
- **Security & Config**: Spring Security, fail-fast production startup validation, non-wildcard CORS, token-scrubbed operational logging
- **Containerization & Observability**: Temurin 17 JRE multi-stage Docker container (non-root), Actuator liveness/readiness probes, native Prometheus metrics

---

## 4. Repository Structure

```
Zynpath/
├── .github/                  # GitHub Actions CI/CD workflows
│   └── workflows/
│       └── android-ci.yml    # Continuous integration and release bundle workflow
├── android/                  # Native Android Kotlin application
│   ├── app/                  # Main Android application module
│   ├── core/                 # Core utilities, theme, and common components
│   │   ├── domain/           # Pure Kotlin puzzle engine & domain models
│   │   ├── data/             # Local database, preferences, repositories
│   │   └── ui/               # Design system, components, animations
│   ├── keystore.properties.example # Release signing configuration template
│   └── features/             # Feature modules (solo, duel, league, profile)
├── backend/                  # Spring Boot 3 multiplayer service
│   ├── src/main/java/        # Game room, matchmaking, reaction relay, validation
│   ├── src/main/resources/   # Application profiles (prod, staging, test) & Flyway V1–V6
│   ├── Dockerfile            # Minimal production container (non-root user 10001)
│   ├── docker-compose.prod.yml # Production orchestration reference
│   └── src/test/java/        # Multiplayer integration & validation tests
├── docs/                     # Authoritative engineering documentation
│   ├── ARCHITECTURE.md       # High-level system architecture
│   ├── GAME_RULES.md         # Authoritative gameplay specification
│   ├── PUZZLE_ENGINE.md      # Pure Kotlin puzzle engine specification
│   ├── LEVEL_PROGRESSION.md  # World 1 to 6 level progression design
│   ├── ANDROID_ARCHITECTURE.md # Presentation, domain, data layer details
│   ├── ANDROID_RELEASE_CONFIGURATION.md # Application ID, versioning, build variants
│   ├── RELEASE_SIGNING.md    # Upload keystore generation, env injection, zero-debug
│   ├── GOOGLE_PLAY_APP_SIGNING.md # Play App Signing vs Upload key, PEPK, OAuth fingerprints
│   ├── CI_CD_PIPELINE.md     # GitHub Actions workflow, secret isolation, manual gate
│   ├── RELEASE_ARTIFACTS.md  # AAB vs APK, build output paths, mapping files
│   ├── BUILD_REPRODUCIBILITY.md # Toolchain pinning (JDK 17, Gradle 9.4.1, AGP 9.2.1)
│   ├── RELEASE_NOTES_TEMPLATE.md # Play Store <en-US> notes & testing templates
│   ├── ANDROID_RELEASE_CHECKLIST.md # Pre-release 18-point verification checklist
│   ├── PLAY_CONSOLE_UPLOAD.md # Manual upload procedure, track progression
│   ├── RELEASE_ROLLBACK.md   # Android version code monotonicity & forward-fix policy
│   ├── PRODUCTION_CONFIGURATION.md # Profiles, HikariCP, startup validation, shutdown
│   ├── ENVIRONMENT_VARIABLES.md # Master environment inventory & safe template
│   ├── DATABASE_MIGRATIONS.md # Flyway V1-V6 migration catalog & preservation rules
│   ├── BACKEND_HEALTH_CHECKS.md # Actuator liveness and readiness probe architecture
│   ├── OPERATIONAL_LOGGING.md # Structured logging, token scrubbing & metrics
│   ├── WEBSOCKET_DEPLOYMENT.md # Reverse proxy upgrade, origin security, single-instance
│   ├── DEPLOYMENT_RUNBOOK.md  # 9-step production deployment sequence
│   ├── ROLLBACK_RUNBOOK.md    # Application vs database rollback procedures
│   ├── BACKUP_AND_RESTORE.md  # PostgreSQL backup schedules, WAL archiving, restore drills
│   ├── PRODUCTION_READINESS.md # Master pre-launch release checklist & status matrix
│   ├── INFRASTRUCTURE_COSTS.md # Low-cost production tier analysis & cost control
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
| **04** | Phase 1 | Local Data Architecture, Settings, and Offline Progress Foundation | **COMPLETED** |
| **05** | Phase 1 | Development Environment, Local Backend Foundation, and Integration Readiness | **COMPLETED** |
| **06–11** | Phase 2 | Continuous-Path Puzzle Engine (Pure Kotlin, Solvers, Generators, Validators) | **COMPLETED** |
| **12** | Phase 3 | Gameplay Screen Integration, Touch Drawing and Real-Time Path Rendering | **COMPLETED** |
| **13** | Phase 3 | Gameplay Session Management, Timer, Pause, Resume and Safe Local Restoration | **COMPLETED** |
| **14** | Phase 3 | Gameplay Hint Engine, Solution-Aware Guidance and Fair Usage Controls | **COMPLETED** |
| **15** | Phase 3 | Solo Gameplay Polish, Completion Experience and Accessibility | **COMPLETED** |
| **16** | Phase 4 | Daily Challenge Foundation, Deterministic Puzzles and Offline Participation | **COMPLETED** |
| **17** | Phase 4 | Player Profile, Guest Identity and Local Achievement Foundation | **COMPLETED** |
| **18** | Phase 4 | Authentication Foundation, Google/Facebook Sign-In and Guest Account Linking | **COMPLETED** |
| **19** | Phase 5 | Online Player Presence, Friend Discovery and Invitation Foundation | **COMPLETED** |
| **20** | Phase 5 | Online Matchmaking Foundation, Game Session Lifecycle and Real-Time Transport | **COMPLETED** |
| **21** | Phase 5 | Quick Duel 1v1 Matchmaking, Live Gameplay and Result Experience | **COMPLETED** |
| **22** | Phase 5 | Friend Duel Invitations, Private 1v1 Matches and Rematch Experience | **COMPLETED** |
| **23** | Phase 5 | Mini League Private Rooms, 2–5 Player Lobbies and Multiplayer Gameplay | **COMPLETED** |
| **24** | Phase 5 | Competitive Match History, Player Statistics and Leaderboard Foundation | **COMPLETED** |
| **25** | Phase 5 | Daily Challenge Online Verification, Shared Daily Puzzles and Daily Leaderboard | **COMPLETED** |
| **26** | Phase 6 | Premium Subscription Foundation, Google Play Billing and Entitlement Management | **COMPLETED** |
| **27** | Phase 6 | Premium Solo Puzzle Packs, Content Entitlements and Offline Access | **COMPLETED** |
| **28** | Phase 6 | Premium Themes, Path Effects, Avatar Frames and Cosmetic Customization | **COMPLETED** |
| **29** | Phase 6 | Optional Rewarded Ads, Free Solo Hint Rewards and Ad-Free Premium Policy | **COMPLETED** |
| **30** | Phase 6 | Advanced Personal Statistics, Progression Insights and Premium Analytics | **COMPLETED** |
| **31** | Phase 7 | Notifications, Friend Invitation Alerts and Daily Challenge Reminders | **COMPLETED** |
| **32** | Phase 7 | Player Settings, Privacy Controls and Account Management | **COMPLETED** |
| **33** | Phase 7 | Player Onboarding, Interactive Tutorial and First-Time User Experience | **COMPLETED** |
| **34** | Phase 7 | Game Audio, Haptic Feedback and Interaction Polish | **COMPLETED** |
| **35** | Phase 8 | Offline-First Synchronization, Conflict Resolution and Data Recovery | **COMPLETED** |
| **36** | Phase 8 | Backend Security Hardening, API Authorization and Abuse Prevention | **COMPLETED** |
| **37** | Phase 8 | Performance Optimization, Memory Management and Battery Efficiency | **COMPLETED** |
| **39** | Phase 8 | Accessibility, Responsive Layouts and Device Compatibility Hardening | **COMPLETED** |
| **40** | Phase 9 | Home Screen, World Map and Game Mode Discovery Polish | **COMPLETED** |
| **41** | Phase 9 | Player Retention, Achievement Presentation and Engagement Polish | **COMPLETED** |
| **42** | Phase 9 | App Branding, Icons, Splash Screen and Store Listing Assets | **COMPLETED** |
| **43** | Phase 9 | Google Play Compliance, Privacy Disclosures and Release Policy Preparation | **COMPLETED** |
| **44** | Phase 10 | Production Backend Configuration, Database Migrations and Deployment Readiness | **COMPLETED** |
| **45** | Phase 10 | Android Release Build, Signing Configuration and CI/CD Pipeline Preparation | **COMPLETED** |
| **46** | Phase 11 | Comprehensive Unit Testing, Puzzle Solver Validation and Progression Integrity | **COMPLETED** |
| **47** | Phase 11 | Backend Integration Testing, Authentication, Multiplayer & Security Verification | **COMPLETED** |
| **48** | Phase 11 | Android UI, Accessibility, Device Compatibility and End-to-End Gameplay Testing | **COMPLETED** |
| **49** | Phase 12 | Final Release Validation, Security Audit and Google Play Pre-Launch Readiness | **COMPLETED** |
| **50** | Phase 12 | Final Release Handoff, Google Play Testing Track and Production Launch Checklist | **COMPLETED** |

---

## 6. Quick Start (Development & Local Testing)

### A. Local Spring Boot Backend
1. **Prerequisites**: Java 17+ LTS (`java -version`), Apache Maven 3.9+ (`mvn -version`).
2. **Build and Test Backend**:
   ```powershell
   cd d:\Zynpath\backend
   mvn clean test
   ```
3. **Run Backend Locally (port 8080)**:
   ```powershell
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
4. **Verify Health Endpoint**:
   ```powershell
   curl http://localhost:8080/api/v1/health
   ```

### B. Mobile Client (Android)
1. **Prerequisites**: Android SDK (`android-36` compile SDK), Gradle Wrapper.
2. **Build and Test Android App**:
   ```powershell
   cd d:\Zynpath\android
   .\gradlew.bat testDebugUnitTest
   .\gradlew.bat assembleDebug
   ```
   *Generated Debug APK:* `android/app/build/outputs/apk/debug/app-debug.apk`
3. **Local Testing Workflows**:
   - **Android Emulator**: Automatically routes to `http://10.0.2.2:8080/api/v1`.
   - **Physical Device via USB**: Run `adb reverse tcp:8080 tcp:8080`.
   - **Diagnostics**: Open Zynpath $\to$ Settings $\to$ "BACKEND CONNECTIVITY (DEBUG)" to test connectivity.

- [docs/NOTIFICATIONS.md](file:///d:/Zynpath/docs/NOTIFICATIONS.md): In-app notification center architecture, event categories, Room persistence, and offline behavior.
- [docs/PUSH_NOTIFICATIONS.md](file:///d:/Zynpath/docs/PUSH_NOTIFICATIONS.md): Firebase Cloud Messaging push integration, token lifecycle, account isolation, and honest configuration reporting.
- [docs/NOTIFICATION_PREFERENCES.md](file:///d:/Zynpath/docs/NOTIFICATION_PREFERENCES.md): Granular user controls for social, multiplayer, and daily reminders, with Android runtime permission handling.
- [docs/DAILY_REMINDERS.md](file:///d:/Zynpath/docs/DAILY_REMINDERS.md): Local Daily Challenge reminder scheduling via AlarmManager, local timezone alignment, and redundancy suppression.
- [docs/NOTIFICATION_SECURITY.md](file:///d:/Zynpath/docs/NOTIFICATION_SECURITY.md): Deep-link revalidation, server-authoritative block enforcement, rate limiting, and lock-screen privacy.
- [docs/PERSONAL_ANALYTICS.md](file:///d:/Zynpath/docs/PERSONAL_ANALYTICS.md): Personal analytics and progression insights architecture, offline-first computation, and zero fabrication policy.
- [docs/ANALYTICS_METRICS.md](file:///d:/Zynpath/docs/ANALYTICS_METRICS.md): Metric formulas, same-puzzle time improvement rules, decisive win-rate denominators, and sample sizes ($N$).
- [docs/ANALYTICS_DATA_SOURCES.md](file:///d:/Zynpath/docs/ANALYTICS_DATA_SOURCES.md): Source-of-truth classifications (`LOCAL_SOLO`, `LOCAL_DAILY`, `SERVER_VERIFIED_DAILY`, `SERVER_COMPETITIVE`).
- [docs/PREMIUM_ANALYTICS.md](file:///d:/Zynpath/docs/PREMIUM_ANALYTICS.md): Premium advanced analytics specification, subscription expiration behavior, and free preview standards.
- [docs/COSMETIC_SYSTEM.md](file:///d:/Zynpath/docs/COSMETIC_SYSTEM.md): Cosmetic customization architecture, catalog model, preview vs. equip, account persistence, and competitive fairness.
- [docs/THEMES.md](file:///d:/Zynpath/docs/THEMES.md): Visual themes catalog, free and premium color palettes, WCAG contrast verification, and game board token mapping.
- [docs/PATH_EFFECTS.md](file:///d:/Zynpath/docs/PATH_EFFECTS.md): Path drawing visual effects, glow, pulse, particle accents, performance boundaries, and reduced-motion static fallback.
- [docs/AVATAR_FRAMES.md](file:///d:/Zynpath/docs/AVATAR_FRAMES.md): Public profile avatar frames, slate, silver, gold, and neon rings, with zero billing token leakage.
- [docs/COSMETIC_ENTITLEMENTS.md](file:///d:/Zynpath/docs/COSMETIC_ENTITLEMENTS.md): Authoritative cosmetic entitlement verification, bounded offline caching, expiration fallback, and resubscription restoration.
- [docs/PREMIUM.md](file:///d:/Zynpath/docs/PREMIUM.md): Premium subscription architecture, Google Play Billing integration, and entitlement management.
- [docs/SUBSCRIPTION_ENTITLEMENTS.md](file:///d:/Zynpath/docs/SUBSCRIPTION_ENTITLEMENTS.md): Multi-tier entitlement models, verification pipeline, and state machines.
- [docs/PREMIUM_FEATURE_POLICY.md](file:///d:/Zynpath/docs/PREMIUM_FEATURE_POLICY.md): Policy on competitive fairness, ad-free experience, and cosmetic vs gameplay separation.
- [docs/PREMIUM_PUZZLE_PACKS.md](file:///d:/Zynpath/docs/PREMIUM_PUZZLE_PACKS.md): Premium puzzle pack catalog, offline access, and progression tracking.
- [docs/MINI_LEAGUE.md](file:///d:/Zynpath/docs/MINI_LEAGUE.md): Mini League 2–5 player private rooms, lobby lifecycle, and live multiplayer racing.
- [docs/MINI_LEAGUE_ROOMS.md](file:///d:/Zynpath/docs/MINI_LEAGUE_ROOMS.md): Mini League room model, capacity invariants, state machine, and host transfer protocol.
- [docs/MINI_LEAGUE_INVITATIONS.md](file:///d:/Zynpath/docs/MINI_LEAGUE_INVITATIONS.md): Mini League friend invitations, deep links, and Android Sharesheet integration.
- [docs/MINI_LEAGUE_RESULT_POLICY.md](file:///d:/Zynpath/docs/MINI_LEAGUE_RESULT_POLICY.md): Finishing order determination, 45s finishing window, authoritative standings, and tie policy.
- [docs/FRIEND_DUEL.md](file:///d:/Zynpath/docs/FRIEND_DUEL.md): Friend Duel 1v1 technical specification, social authorization, and match lifecycle.
- [docs/FRIEND_DUEL_INVITATIONS.md](file:///d:/Zynpath/docs/FRIEND_DUEL_INVITATIONS.md): Friend Duel invitation protocol, race-condition handling, and lifecycle states.
- [docs/FRIEND_DUEL_REMATCH.md](file:///d:/Zynpath/docs/FRIEND_DUEL_REMATCH.md): Friend Duel rematch system, mutual consent, and alternative puzzle assignment.
- [docs/QUICK_DUEL.md](file:///d:/Zynpath/docs/QUICK_DUEL.md): Quick Duel 1v1 technical specification, queue lifecycle, ready confirmation, live dual-win racing, and verification.
- [docs/QUICK_DUEL_UI.md](file:///d:/Zynpath/docs/QUICK_DUEL_UI.md): Quick Duel 1v1 UI/UX flow, entry point, searching radar, versus lobby, live dual progress HUD, and authoritative result screen.
- [docs/QUICK_DUEL_RESULT_POLICY.md](file:///d:/Zynpath/docs/QUICK_DUEL_RESULT_POLICY.md): Dual-win 8-point rule verification, server timing policy, idempotency, and forfeit/abandonment rules.
- [docs/MULTIPLAYER_ARCHITECTURE.md](file:///d:/Zynpath/docs/MULTIPLAYER_ARCHITECTURE.md): Common multiplayer infrastructure, authoritative matchmaking, and dual-win validation.
- [docs/MATCHMAKING.md](file:///d:/Zynpath/docs/MATCHMAKING.md): Quick Duel dedicated FIFO queue, 45s bounded timeout, and block policy.
- [docs/MATCH_SESSION_LIFECYCLE.md](file:///d:/Zynpath/docs/MATCH_SESSION_LIFECYCLE.md): Authoritative FSM, state transitions, and reconnect snapshot reconciliation.
- [docs/MULTIPLAYER_WEBSOCKET.md](file:///d:/Zynpath/docs/MULTIPLAYER_WEBSOCKET.md): /ws/multiplayer transport, event envelopes, sequence numbers, and progress throttling.
- [docs/MULTIPLAYER_PUZZLE_ASSIGNMENT.md](file:///d:/Zynpath/docs/MULTIPLAYER_PUZZLE_ASSIGNMENT.md): Solver-verified identical puzzle pools and SHA-256 fingerprints.
- [docs/MULTIPLAYER_RESULT_VALIDATION.md](file:///d:/Zynpath/docs/MULTIPLAYER_RESULT_VALIDATION.md): Server-side dual-win path validation, authoritative timing, and idempotent results.
- [docs/FRIENDS_SYSTEM.md](file:///d:/Zynpath/docs/FRIENDS_SYSTEM.md): Social architecture, mutual friendship lifecycle, and privacy constraints.
- [docs/FRIEND_REQUESTS.md](file:///d:/Zynpath/docs/FRIEND_REQUESTS.md): Authoritative backend friend request state machine and safety rules.
- [docs/PLAYER_PRESENCE.md](file:///d:/Zynpath/docs/PLAYER_PRESENCE.md): Ephemeral in-memory presence leases, server authority, and WebSocket lifecycle.
- [docs/INVITATION_LINKS.md](file:///d:/Zynpath/docs/INVITATION_LINKS.md): Versioned invitation URLs, Android deep links, and Sharesheet integration.
- [docs/SOCIAL_API.md](file:///d:/Zynpath/docs/SOCIAL_API.md): Social REST and WebSocket API specification.
- [docs/AUTHENTICATION.md](file:///d:/Zynpath/docs/AUTHENTICATION.md): Authentication architecture, Google/Facebook sign-in, guest account linking, and state machine.
- [docs/AUTHENTICATION_API.md](file:///d:/Zynpath/docs/AUTHENTICATION_API.md): Authentication REST API endpoints (/exchange, /link, /me, /signout) and error codes.
- [docs/PROVIDER_CONFIGURATION.md](file:///d:/Zynpath/docs/PROVIDER_CONFIGURATION.md): Google & Facebook environment configuration and honest status reporting.
- [docs/SESSION_SECURITY.md](file:///d:/Zynpath/docs/SESSION_SECURITY.md): Android KeyStore AES-GCM credential storage and server-side authorization.
- [docs/PLAYER_IDENTITY.md](file:///d:/Zynpath/docs/PLAYER_IDENTITY.md): Internal UUID vs. Public Zynpath ID (`ZYN-XXXX-YYYY`) and account lifecycle.
- [docs/PLAYER_PROFILE.md](file:///d:/Zynpath/docs/PLAYER_PROFILE.md): Profile models, display name rules, built-in vector avatars, and account linking dialog.
- [docs/ACCOUNT_LINKING.md](file:///d:/Zynpath/docs/ACCOUNT_LINKING.md): Guest progress preservation rules and conflict mitigation policy.
- [docs/ACHIEVEMENTS.md](file:///d:/Zynpath/docs/ACHIEVEMENTS.md): Local achievement system, idempotent unlock tracking, and registry.
- [docs/DAILY_CHALLENGE.md](file:///d:/Zynpath/docs/DAILY_CHALLENGE.md): Daily Challenge architecture, UTC date policy, deterministic scheduler, verified puzzle pool, and local streak engine.
- [docs/DAILY_CHALLENGE_SCHEDULE.md](file:///d:/Zynpath/docs/DAILY_CHALLENGE_SCHEDULE.md): Schedule specification, SHA-256 hash modulo mapping, and curated 14-puzzle index table.
- [docs/GAMEPLAY_ACCESSIBILITY.md](file:///d:/Zynpath/docs/GAMEPLAY_ACCESSIBILITY.md): Accessibility principles, non-color cues, tap-to-move input, and screen reader semantics.
- [docs/COMPLETION_EXPERIENCE.md](file:///d:/Zynpath/docs/COMPLETION_EXPERIENCE.md): Validated victory criteria, personal best tracking, celebratory animations, next-level catalog gating, and replay behavior.
- [docs/HINT_ENGINE.md](file:///d:/Zynpath/docs/HINT_ENGINE.md): Solution-aware gameplay hint engine, dead-end detection, and recovery guidance.
- [docs/HINT_USAGE_POLICY.md](file:///d:/Zynpath/docs/HINT_USAGE_POLICY.md): Solo free hint allowances, premium entitlements, and competitive fairness.
- [docs/GAMEPLAY_SESSIONS.md](file:///d:/Zynpath/docs/GAMEPLAY_SESSIONS.md): Session lifecycle, state snapshots, and Room persistence.
- [docs/GAMEPLAY_TIMER.md](file:///d:/Zynpath/docs/GAMEPLAY_TIMER.md): Monotonic active-play gameplay timer.
- [docs/GAMEPLAY_UI.md](file:///d:/Zynpath/docs/GAMEPLAY_UI.md): Native Jetpack Compose gameplay screen, canvas rendering, and state flow.
- [docs/TOUCH_INPUT.md](file:///d:/Zynpath/docs/TOUCH_INPUT.md): Touch coordinate mapping, drag gestures, fast-movement interpolation, discrete tap input, and backtracking.
- [docs/LEVEL_CATALOG.md](file:///d:/Zynpath/docs/LEVEL_CATALOG.md): Verified offline level catalog, world progression, and admission pipeline.
- [docs/PUZZLE_ASSET_FORMAT.md](file:///d:/Zynpath/docs/PUZZLE_ASSET_FORMAT.md): JSON puzzle asset schema, normalization rules, and SHA-256 fingerprints.
- [docs/CATALOG_VERSIONING.md](file:///d:/Zynpath/docs/CATALOG_VERSIONING.md): Catalog, manifest, and asset schema versioning and migration policies.
- [docs/LEVEL_CURATION.md](file:///d:/Zynpath/docs/LEVEL_CURATION.md): Difficulty analysis, quality signals, and level curation pipeline.
- [docs/PUZZLE_SOLVER.md](file:///d:/Zynpath/docs/PUZZLE_SOLVER.md): Exhaustive backtracker, graph pruning, and uniqueness proofs.
- [docs/PUZZLE_GENERATOR.md](file:///d:/Zynpath/docs/PUZZLE_GENERATOR.md): Deterministic candidate generation and solver-validated admission.
- [docs/BACKEND_ARCHITECTURE.md](file:///d:/Zynpath/docs/BACKEND_ARCHITECTURE.md): Spring Boot 3 modular monolith, boundaries, and validation.
- [docs/LOCAL_BACKEND_SETUP.md](file:///d:/Zynpath/docs/LOCAL_BACKEND_SETUP.md): Step-by-step local backend build, run, and test guide.
- [docs/API_CONTRACTS.md](file:///d:/Zynpath/docs/API_CONTRACTS.md): REST health, error payloads, and competitive validation contracts.
- [docs/ENVIRONMENT_CONFIGURATION.md](file:///d:/Zynpath/docs/ENVIRONMENT_CONFIGURATION.md): Debug vs. release configurations, emulator routing, and ADB reverse.
- [docs/LOCAL_MOBILE_TESTING.md](file:///d:/Zynpath/docs/LOCAL_MOBILE_TESTING.md): Detailed physical device and emulator testing guide.
- [docs/NOTIFICATIONS.md](file:///d:/Zynpath/docs/NOTIFICATIONS.md): In-app notification center, tabs, category models, and Room persistence.
- [docs/PUSH_NOTIFICATIONS.md](file:///d:/Zynpath/docs/PUSH_NOTIFICATIONS.md): FCM push integration, device token lifecycle, and configuration boundary.
- [docs/NOTIFICATION_PREFERENCES.md](file:///d:/Zynpath/docs/NOTIFICATION_PREFERENCES.md): Player notification controls, DataStore persistence, and Android 13+ permission handling.
- [docs/DAILY_REMINDERS.md](file:///d:/Zynpath/docs/DAILY_REMINDERS.md): Daily challenge local reminders, AlarmManager scheduling, and completion suppression.
- [docs/PLAYER_SETTINGS.md](file:///d:/Zynpath/docs/PLAYER_SETTINGS.md): Unified settings architecture, local vs. synced preferences, and section breakdown.
- [docs/PRIVACY_CONTROLS.md](file:///d:/Zynpath/docs/PRIVACY_CONTROLS.md): Profile visibility tiers, social discovery policies, and server-enforced block management.
- [docs/ACCOUNT_MANAGEMENT.md](file:///d:/Zynpath/docs/ACCOUNT_MANAGEMENT.md): Account states, provider unlinking safety, session lifecycle, and sign-out isolation.
- [docs/ACCOUNT_LINKING_POLICY.md](file:///d:/Zynpath/docs/ACCOUNT_LINKING_POLICY.md): Deterministic identity resolution, collision handling, and guest progress preservation.
- [docs/DATA_EXPORT.md](file:///d:/Zynpath/docs/DATA_EXPORT.md): "Request My Data" foundation, Schema Version 1 JSON specification, and configuration boundaries.
- [docs/ACCOUNT_DELETION.md](file:///d:/Zynpath/docs/ACCOUNT_DELETION.md): Self-service account deletion workflow, Google Play subscription notices, and opponent match integrity.
- [docs/DATA_RETENTION.md](file:///d:/Zynpath/docs/DATA_RETENTION.md): Data minimization principles, retention schedules, and financial audit exceptions.
- [docs/INTERACTIVE_TUTORIAL.md](file:///d:/Zynpath/docs/INTERACTIVE_TUTORIAL.md): 7-stage interactive gameplay tutorial, move rejection guidance, and accessible text mode.
- [docs/GAME_AUDIO.md](file:///d:/Zynpath/docs/GAME_AUDIO.md): Game audio architecture, 13-event audio taxonomy, SoundPool management, and synthetic generation.
- [docs/HAPTIC_FEEDBACK.md](file:///d:/Zynpath/docs/HAPTIC_FEEDBACK.md): Haptic feedback architecture, vibration waveforms, throttling, and safe hardware fallback.
- [docs/INTERACTION_POLISH.md](file:///d:/Zynpath/docs/INTERACTION_POLISH.md): Micro-interactions, button spring physics, board rejection shake, and reduced-motion compliance.
- [docs/AUDIO_ASSET_LICENSES.md](file:///d:/Zynpath/docs/AUDIO_ASSET_LICENSES.md): Algorithmic sound provenance, mathematical synthesis, and MIT / Public Domain rights.
- [docs/OFFLINE_SYNCHRONIZATION.md](file:///d:/Zynpath/docs/OFFLINE_SYNCHRONIZATION.md): Offline-first synchronization architecture, WorkManager background sync, and retry backoff.
- [docs/CONFLICT_RESOLUTION.md](file:///d:/Zynpath/docs/CONFLICT_RESOLUTION.md): Pure domain conflict resolution policies, personal best preservation, and clock skew tolerance.
- [docs/DATA_RECOVERY.md](file:///d:/Zynpath/docs/DATA_RECOVERY.md): Comprehensive data recovery behavior across process kill, token expiration, and schema migrations.
- [docs/SYNC_OPERATION_MODEL.md](file:///d:/Zynpath/docs/SYNC_OPERATION_MODEL.md): Durable Room operation queue schema, states, payloads, and pruning policies.
- [docs/SYNC_AUTHORITY_MATRIX.md](file:///d:/Zynpath/docs/SYNC_AUTHORITY_MATRIX.md): Comprehensive data authority matrix mapping local vs. backend cloud ownership.
- [docs/RESPONSIVE_LAYOUTS.md](file:///d:/Zynpath/docs/RESPONSIVE_LAYOUTS.md): Responsive layout architecture, window classification, landscape, tablet, and compact-phone layouts.
- [docs/ACCESSIBLE_PUZZLE_INPUT.md](file:///d:/Zynpath/docs/ACCESSIBLE_PUZZLE_INPUT.md): Accessible puzzle board interaction, TalkBack virtual cell grid, keyboard/D-pad navigation, and non-color feedback.
- [docs/DEVICE_COMPATIBILITY.md](file:///d:/Zynpath/docs/DEVICE_COMPATIBILITY.md): Device compatibility matrix, OS targets, system navigation, and capability fallbacks.
- [docs/ACCESSIBILITY_VERIFICATION_MATRIX.md](file:///d:/Zynpath/docs/ACCESSIBILITY_VERIFICATION_MATRIX.md): WCAG 2.1 Level AA compliance matrix and assistive technology verification status.
- [docs/PERFORMANCE_OPTIMIZATION.md](file:///d:/Zynpath/docs/PERFORMANCE_OPTIMIZATION.md): Overview of Android & backend performance optimizations, state ownership, and resource reuse.
- [docs/MEMORY_MANAGEMENT.md](file:///d:/Zynpath/docs/MEMORY_MANAGEMENT.md): Memory management principles, allocation elimination in Compose canvas, and lifecycle reclamation.
- [docs/BATTERY_EFFICIENCY.md](file:///d:/Zynpath/docs/BATTERY_EFFICIENCY.md): Battery efficiency guidelines, WorkManager constraints, sensor minimization, and background idling.
- [docs/HOME_SCREEN.md](file:///d:/Zynpath/docs/HOME_SCREEN.md): Home screen architecture, visual hierarchy, primary play action, and mode discovery.
- [docs/WORLD_MAP_UX.md](file:///d:/Zynpath/docs/WORLD_MAP_UX.md): Interactive 6-world progression map, canonical level ranges, and unlock states.
- [docs/LEVEL_SELECTION_UX.md](file:///d:/Zynpath/docs/LEVEL_SELECTION_UX.md): Level selection grid, locked/completed states, personal best times, and replayability.
- [docs/GAME_MODE_DISCOVERY.md](file:///d:/Zynpath/docs/GAME_MODE_DISCOVERY.md): Comprehensive game mode discovery across Solo, Daily, Quick Duel, Friend Duel, and Mini League.
- [docs/PRIMARY_NAVIGATION.md](file:///d:/Zynpath/docs/PRIMARY_NAVIGATION.md): Primary navigation graph, routes, deep link fallbacks, and back-stack integrity.
- [docs/PLAYER_ENGAGEMENT.md](file:///d:/Zynpath/docs/PLAYER_ENGAGEMENT.md): Player retention, respectful engagement, quiet hours, and privacy safeguards.
- [docs/ACHIEVEMENT_PRESENTATION.md](file:///d:/Zynpath/docs/ACHIEVEMENT_PRESENTATION.md): Achievement presentation, categories, definitions, and accessible detail dialog.
- [docs/MILESTONE_CELEBRATIONS.md](file:///d:/Zynpath/docs/MILESTONE_CELEBRATIONS.md): Milestone celebrations, canonical world boundaries, deduplication, and personal bests.
- [docs/RETENTION_EXPERIENCE.md](file:///d:/Zynpath/docs/RETENTION_EXPERIENCE.md): Return-to-play continuity, Home milestone preview, next goal discovery, and UTC streaks.
- [docs/BRAND_GUIDELINES.md](file:///d:/Zynpath/docs/BRAND_GUIDELINES.md): Canonical brand colors, logo mark, wordmark, and typography rules.
- [docs/APP_ICON_SPECIFICATION.md](file:///d:/Zynpath/docs/APP_ICON_SPECIFICATION.md): Adaptive icon layers, safe zone geometry, and monochrome dynamic theming.
- [docs/SPLASH_SCREEN.md](file:///d:/Zynpath/docs/SPLASH_SCREEN.md): Native Android splash screen configuration, theme switching, and startup performance.
- [docs/STORE_LISTING.md](file:///d:/Zynpath/docs/STORE_LISTING.md): Google Play store title, short description, and full description metadata.
- [docs/STORE_ASSET_INVENTORY.md](file:///d:/Zynpath/docs/STORE_ASSET_INVENTORY.md): Comprehensive inventory of store icons, feature graphic, and promotional artwork.
- [docs/STORE_SCREENSHOT_PLAN.md](file:///d:/Zynpath/docs/STORE_SCREENSHOT_PLAN.md): 8-screenshot sequence, dimensions, headlines, and capture guidelines.
- [docs/STORE_COMPLIANCE_CHECKLIST.md](file:///d:/Zynpath/docs/STORE_COMPLIANCE_CHECKLIST.md): Google Play Developer Program policy compliance verification.
- [docs/POLICY_SOURCE_REGISTER.md](file:///d:/Zynpath/docs/POLICY_SOURCE_REGISTER.md): Official Google Play policy sources, effective dates, and implementation evidence register.
- [docs/GOOGLE_PLAY_COMPLIANCE.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_COMPLIANCE.md): Master compliance inventory, permission audit, target SDK 36 readiness, and reviewer access guide.
- [docs/PRIVACY_POLICY_DRAFT.md](file:///d:/Zynpath/docs/PRIVACY_POLICY_DRAFT.md): Complete, evidence-backed Privacy Policy draft reflecting real application and SDK data handling.
- [docs/DATA_SAFETY_MATRIX.md](file:///d:/Zynpath/docs/DATA_SAFETY_MATRIX.md): Detailed Play Console Data Safety questionnaire answers and data classification matrix.
- [docs/DATA_FLOW_INVENTORY.md](file:///d:/Zynpath/docs/DATA_FLOW_INVENTORY.md): End-to-end data flow, local vs. cloud persistence, and data retention mapping.
- [docs/THIRD_PARTY_SDK_INVENTORY.md](file:///d:/Zynpath/docs/THIRD_PARTY_SDK_INVENTORY.md): Audit of Google Play Billing, Google Mobile Ads, OkHttp, and confirmed absence of invasive trackers.
- [docs/ACCOUNT_DELETION_COMPLIANCE.md](file:///d:/Zynpath/docs/ACCOUNT_DELETION_COMPLIANCE.md): In-app and external web account deletion compliance guide and subscription notices.
- [docs/ADS_AND_BILLING_DISCLOSURES.md](file:///d:/Zynpath/docs/ADS_AND_BILLING_DISCLOSURES.md): Monetization disclosures, subscription terms, restore purchases, and optional rewarded ad caps.
- [docs/TARGET_AUDIENCE_REVIEW.md](file:///d:/Zynpath/docs/TARGET_AUDIENCE_REVIEW.md): Target audience decision worksheet establishing Ages 13+ (General Audience) classification.
- [docs/CONTENT_RATING_PREPARATION.md](file:///d:/Zynpath/docs/CONTENT_RATING_PREPARATION.md): Evidence-backed answers for the official IARC content rating questionnaire.
- [docs/PLAY_CONSOLE_ACTIONS.md](file:///d:/Zynpath/docs/PLAY_CONSOLE_ACTIONS.md): Step-by-step checklist of manual Play Console store presence, policy, and monetization tasks.
- [docs/ANDROID_RELEASE_CONFIGURATION.md](file:///d:/Zynpath/docs/ANDROID_RELEASE_CONFIGURATION.md): Android build variants, versioning, signing architecture, and R8 configuration.
- [docs/RELEASE_SIGNING.md](file:///d:/Zynpath/docs/RELEASE_SIGNING.md): Secure signing configuration, keystore storage, and credential injection.
- [docs/GOOGLE_PLAY_APP_SIGNING.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_APP_SIGNING.md): Dual-key Google Play App Signing model, upload keys, and OAuth fingerprints.
- [docs/CI_CD_PIPELINE.md](file:///d:/Zynpath/docs/CI_CD_PIPELINE.md): GitHub Actions automation, least-privilege security boundaries, and bundle generation.
- [docs/TEST_STRATEGY.md](file:///d:/Zynpath/docs/TEST_STRATEGY.md): Comprehensive test strategy, test pyramid, canonical rule invariants, and QA architecture.
- [docs/PUZZLE_ENGINE_TEST_REPORT.md](file:///d:/Zynpath/docs/PUZZLE_ENGINE_TEST_REPORT.md): Core puzzle engine rule verification, state transitions, and boundary properties.
- [docs/SOLVER_VALIDATION_REPORT.md](file:///d:/Zynpath/docs/SOLVER_VALIDATION_REPORT.md): Puzzle solver correctness, negative cases, determinism, and performance benchmarks.
- [docs/LEVEL_CATALOG_AUDIT.md](file:///d:/Zynpath/docs/LEVEL_CATALOG_AUDIT.md): Complete 300-level catalog manifest audit and shipped puzzle solvability results.
- [docs/PROGRESSION_TEST_REPORT.md](file:///d:/Zynpath/docs/PROGRESSION_TEST_REPORT.md): World progression, sequential level unlocks, replay invariance, and personal best times.
- [docs/OFFLINE_PERSISTENCE_TEST_REPORT.md](file:///d:/Zynpath/docs/OFFLINE_PERSISTENCE_TEST_REPORT.md): Room DAOs, DataStore preferences, migration integrity, and offline autonomy.
- [docs/BACKEND_INTEGRATION_TEST_REPORT.md](file:///d:/Zynpath/docs/BACKEND_INTEGRATION_TEST_REPORT.md): Spring Boot backend integration test results across all 16 suites (73 tests, 100% pass rate).
- [docs/AUTHENTICATION_TEST_REPORT.md](file:///d:/Zynpath/docs/AUTHENTICATION_TEST_REPORT.md): Authentication, Google/Facebook exchange, guest account-linking, and session invalidation verification.
- [docs/MULTIPLAYER_TEST_REPORT.md](file:///d:/Zynpath/docs/MULTIPLAYER_TEST_REPORT.md): Quick Duel matchmaking, Friend Duel private rooms, Mini League lobbies, and 8-point dual-win validation.
- [docs/WEBSOCKET_TEST_REPORT.md](file:///d:/Zynpath/docs/WEBSOCKET_TEST_REPORT.md): Real-time WebSocket transport, token handshake, match event subscriptions, and presence.
- [docs/DAILY_CHALLENGE_TEST_REPORT.md](file:///d:/Zynpath/docs/DAILY_CHALLENGE_TEST_REPORT.md): UTC deterministic scheduling, official competitive attempt tracking, and offline provisional sync isolation.
- [docs/SYNCHRONIZATION_TEST_REPORT.md](file:///d:/Zynpath/docs/SYNCHRONIZATION_TEST_REPORT.md): Client batch progress synchronization, non-destructive merging, and operation ID deduplication.
- [docs/BILLING_TEST_REPORT.md](file:///d:/Zynpath/docs/BILLING_TEST_REPORT.md): Google Play Billing purchase verification, entitlement lifecycle, and competitive fairness enforcement.
- [docs/REWARDED_AD_TEST_REPORT.md](file:///d:/Zynpath/docs/REWARDED_AD_TEST_REPORT.md): Rewarded ad verification, anti-replay fraud guard, 5/24h caps, and AdMob SSV webhooks.
- [docs/SECURITY_REGRESSION_REPORT.md](file:///d:/Zynpath/docs/SECURITY_REGRESSION_REPORT.md): BOLA/IDOR protection, authentication requirements, token privacy, and security audit logging.
- [docs/ANDROID_UI_TEST_REPORT.md](file:///d:/Zynpath/docs/ANDROID_UI_TEST_REPORT.md): Comprehensive Android UI, navigation, gesture, and gameplay test report (Prompt 48).
- [docs/DEVICE_COMPATIBILITY_MATRIX.md](file:///d:/Zynpath/docs/DEVICE_COMPATIBILITY_MATRIX.md): Comprehensive device matrix, window size classes, and API version compatibility.
- [docs/ACCESSIBILITY_AUDIT.md](file:///d:/Zynpath/docs/ACCESSIBILITY_AUDIT.md): WCAG 2.1 Level AA accessibility audit, TalkBack semantics, and contrast analysis.
- [docs/END_TO_END_GAMEPLAY_REPORT.md](file:///d:/Zynpath/docs/END_TO_END_GAMEPLAY_REPORT.md): End-to-end player journeys, gesture hit precision, and multiplayer UI flows.
- [docs/LIFECYCLE_TEST_REPORT.md](file:///d:/Zynpath/docs/LIFECYCLE_TEST_REPORT.md): Activity lifecycle, session continuity, process death recovery, and rotation tests.
- [docs/MOBILE_PERFORMANCE_REPORT.md](file:///d:/Zynpath/docs/MOBILE_PERFORMANCE_REPORT.md): Startup latency, 120 FPS path rendering, memory profile, and battery efficiency.
- [docs/RELEASE_CANDIDATE_REPORT.md](file:///d:/Zynpath/docs/RELEASE_CANDIDATE_REPORT.md): Comprehensive release candidate evaluation and go/no-go readiness audit (Prompt 49).
- [docs/RELEASE_DEFECT_REGISTER.md](file:///d:/Zynpath/docs/RELEASE_DEFECT_REGISTER.md): Authoritative defect register covering Prompts 1–48 (12 defects, 100% resolved and verified).
- [docs/FINAL_RELEASE_BLOCKERS.md](file:///d:/Zynpath/docs/FINAL_RELEASE_BLOCKERS.md): Explicit breakdown of internal testing readiness vs. operational production release blockers.
- [docs/SECURITY_AUDIT_REPORT.md](file:///d:/Zynpath/docs/SECURITY_AUDIT_REPORT.md): Full-stack OWASP Mobile & API security audit, KeyStore encryption, and BOLA protection.
- [docs/DEPENDENCY_AUDIT_REPORT.md](file:///d:/Zynpath/docs/DEPENDENCY_AUDIT_REPORT.md): Supply-chain dependency audit covering Android libs.versions.toml and backend pom.xml.
- [docs/SECRET_AUDIT_REPORT.md](file:///d:/Zynpath/docs/SECRET_AUDIT_REPORT.md): Repository-wide secret scan verifying zero committed private keys, keystores, or database credentials.
- [docs/RELEASE_ARTIFACT_VERIFICATION.md](file:///d:/Zynpath/docs/RELEASE_ARTIFACT_VERIFICATION.md): Verification of app-release.aab, target SDK 36, R8 optimization, and mapping file handling.
- [docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md): Google Play policy verification (Title, Description, Target SDK 36, 13+ Target Audience, Reviewer Access).
- [docs/FINAL_PROJECT_HANDOFF.md](file:///d:/Zynpath/docs/FINAL_PROJECT_HANDOFF.md): Master technical handoff report concluding the 50-prompt implementation roadmap (Prompt 50).
- [docs/FINAL_RELEASE_READINESS_MATRIX.md](file:///d:/Zynpath/docs/FINAL_RELEASE_READINESS_MATRIX.md): Detailed 22-subsystem release evaluation matrix.
- [docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md): 10-step Google Play Internal Testing track execution runbook.
- [docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md): Production promotion checklist, staged rollout schedule, and halt thresholds.
- [docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md](file:///d:/Zynpath/docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md): Step-by-step production backend deployment and PostgreSQL migration runbook.
- [docs/INCIDENT_RESPONSE_RUNBOOK.md](file:///d:/Zynpath/docs/INCIDENT_RESPONSE_RUNBOOK.md): Operational SOPs for SEV-1 to SEV-4 outages and emergency playbooks.
- [docs/POST_LAUNCH_MONITORING.md](file:///d:/Zynpath/docs/POST_LAUNCH_MONITORING.md): Observability SLOs, Android Vitals monitoring, and daily health checklists.
- [docs/DEVELOPER_ONBOARDING.md](file:///d:/Zynpath/docs/DEVELOPER_ONBOARDING.md): Comprehensive developer onboarding and workstation quick-start guide.
- [docs/THIRD_PARTY_CONFIGURATION.md](file:///d:/Zynpath/docs/THIRD_PARTY_CONFIGURATION.md): Master inventory of external service integrations, credentials, and fallbacks.
- [docs/RELEASE_ARTIFACT_MANIFEST.md](file:///d:/Zynpath/docs/RELEASE_ARTIFACT_MANIFEST.md): Release candidate artifact identities, versioning, and build toolchains.
- [docs/FIRST_RELEASE_CHANGELOG.md](file:///d:/Zynpath/docs/FIRST_RELEASE_CHANGELOG.md): Version 1.0.0 user-facing release notes and technical changelog.
- [docs/KNOWN_LIMITATIONS.md](file:///d:/Zynpath/docs/KNOWN_LIMITATIONS.md): Transparent register of technical trade-offs and operational boundaries.
- [docs/MANUAL_ACTION_CHECKLIST.md](file:///d:/Zynpath/docs/MANUAL_ACTION_CHECKLIST.md): Master checklist of actions requiring manual operator authorization.
- [docs/DOCUMENTATION_INDEX.md](file:///d:/Zynpath/docs/DOCUMENTATION_INDEX.md): Master index categorizing all project documentation across 9 operational domains.
- [docs/PROGRESS.md](file:///d:/Zynpath/docs/PROGRESS.md): Detailed progress tracker concluding the original 50-prompt roadmap (50/50, 100% completed).

---

## 8. License & Attribution

Copyright &copy; 2026 Zynpath. All rights reserved. Original logic puzzle game design and implementation.
# zynpath
