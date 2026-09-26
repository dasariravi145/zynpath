# Zynpath 50-Prompt Master Implementation Roadmap

**Status:** Authoritative  
**Domain:** End-to-End Engineering Execution Schedule  

---

## 1. Roadmap Architecture Overview

The development of **Zynpath: Number Path Puzzle** is partitioned into ten sequential, milestone-driven phases across 50 discrete prompts:

```
[Phase 1: Foundation (1-5)] ──> [Phase 2: Puzzle Engine (6-12)] ──> [Phase 3: Compose UI & Canvas (13-18)]
                                                                                │
                                                                                ▼
[Phase 6: Friends & Reactions (28-32)] <── [Phase 5: Auth & Profiles (24-27)] <── [Phase 4: Level Curriculum (19-23)]
      │
      ▼
[Phase 7: Real-Time Multiplayer (33-40)] ──> [Phase 8: Monetization (41-44)] ──> [Phase 9: Security & QA (45-48)]
                                                                                               │
                                                                                               ▼
                                                                                   [Phase 10: Launch & Play (49-50)]
```

---

## 2. Phase-by-Phase Execution Schedule

### Phase 1: Project Foundation & Architecture (Prompts 1–5)
- **Prompt 01 (CURRENT)**: Repository Inspection, Finalized Game Specification, and Architecture Initialization.
- **Prompt 02**: Native Android Kotlin + Jetpack Compose Project Setup and Local Device Testing Configuration.
- **Prompt 03**: Java 17+ Spring Boot 3 Backend Foundation, WebSocket Infrastructure, and Build Setup.
- **Prompt 04**: Design System Tokenization, Material 3 Dark Navy & Forest Palette, Typography, and Iconography.
- **Prompt 05**: Continuous Integration, Automated Linting, Git Pre-Commit Hooks, and Baseline Build Verification.

### Phase 2: Continuous-Path Puzzle Engine (Prompts 6–12)
- **Prompt 06**: Pure Kotlin Domain Data Models (`Coordinate`, `Wall`, `NumberedCheckpoint`, `PuzzleGrid`).
- **Prompt 07**: Orthogonal Movement & Bounds Validator (`MovementValidator`).
- **Prompt 08**: Ascending Checkpoint Sequence Validator (`CheckpointValidator`).
- **Prompt 09**: Full Grid Coverage & Dual Win Condition Validator (`CoverageValidator`, `CompletionValidator`).
- **Prompt 10**: Algorithmic Hamiltonian Path Generator & Placement Pipeline.
- **Prompt 11**: Constraint-Satisfaction Puzzle Solver (`PuzzleSolver`) & Solution Uniqueness Verifier.
- **Prompt 12**: Multi-Dimensional Difficulty Engine (`DifficultyEngine`), Hint & Undo Subsystems (`UndoManager`).

### Phase 3: Game UI, Touch Controls & Micro-Animations (Prompts 13–18)
- **Prompt 13**: High-Performance Hardware-Accelerated Compose Canvas Grid Renderer.
- **Prompt 14**: Precision PointerInput Drag Gesture Handler with Center Snapping & Reverse Drag Undo.
- **Prompt 15**: Fluid Polyline Path Renderer with Glow Effects, Corner Fillets, and Directional Arrows.
- **Prompt 16**: Dynamic Numbered Checkpoint Animations, Pulse States, and Visited Transitions.
- **Prompt 17**: In-Game HUD (Level Title, Timer, Star Rating, Moves Counter, Undo/Reset/Hint Buttons).
- **Prompt 18**: Victory Celebration Animation, Star Burst FX, and Level Completion Dialog.

### Phase 4: Levels, Progression & Daily Challenges (Prompts 19–23)
- **Prompt 19**: World 1 (Levels 1–20, 4×4 Basics) & World 2 (Levels 21–50, 5×5 Longer Routes) Level Asset Bundling.
- **Prompt 20**: World 3 (Levels 51–100, 5×5 Wall Challenges) & World 4 (Levels 101–150, 6×6 Complex Routes).
- **Prompt 21**: World 5 (Levels 151–200, 7×7 Advanced) & World 6 (Levels 201–300, 8×8 Expert Path).
- **Prompt 22**: World Navigation Carousel, Level Select Grid, Star Accumulation, and World Unlock Gates.
- **Prompt 23**: Procedural Daily Challenge System (Deterministic Date Seed, Global Clock, Offline Local Solve).

### Phase 5: Guest-First Identity, Auth & Profiles (Prompts 24–27)
- **Prompt 24**: Anonymous Guest Session Architecture, Local UUID Generation, and Zero-Friction First Run.
- **Prompt 25**: Firebase Authentication Integration (Google Sign-In & Facebook Login SDKs).
- **Prompt 26**: Seamless Account Linking & Progress Merging (`linkWithCredential`, Room $\to$ Cloud Upsert).
- **Prompt 27**: Player Profile Screen, Zynpath Tag Display, Avatar Selector, and Lifetime Statistics Card.

### Phase 6: Friends System & Temporary Reactions (Prompts 28–32)
- **Prompt 28**: Zynpath Tag Friend Search, Friend Requests, and Pending Relationships.
- **Prompt 29**: Deep-Linking & Cryptographic Room Invite URL Generation (Android App Links).
- **Prompt 30**: Ephemeral Reaction Wheel UI (Floating Radial Menu with Emojis & Curated Phrases).
- **Prompt 31**: WebSocket Reaction Relay Service (Spring Boot In-Memory Dispatcher & Rate Limiter).
- **Prompt 32**: In-Match Reaction Popups, Smooth Fadeout Animation, and One-Tap Opponent Muting.

### Phase 7: Real-Time Multiplayer Modes (Prompts 33–40)
- **Prompt 33**: WebSocket Client Infrastructure on Android (OkHttp, Reconnection Exponential Backoff).
- **Prompt 34**: Ephemeral Game Room State Manager (Spring Boot `ConcurrentHashMap` Registry).
- **Prompt 35**: Quick Duel 1v1 Matchmaking Queue and Synchronized Match Start Countdown.
- **Prompt 36**: Live Coarse Progress Broadcasting (Opponent Progress Bar & Checkpoint Tracking).
- **Prompt 37**: Friend Duel 1v1 Private Room Creation, Code Joining, and Rematch Loop.
- **Prompt 38**: Mini League (2–5 Players) Tournament Room Lifecycle & Round Coordinator.
- **Prompt 39**: Authoritative Headless Server Solution Verification (`PuzzleVerificationService`).
- **Prompt 40**: Match Results Screen, ELO Rating Updates, Tournament Podiums, and Dissolution.

### Phase 8: Monetization & Store Infrastructure (Prompts 41–44)
- **Prompt 41**: Google AdMob Banner Ads (World Select / Level Complete Anchors) & Strict Pacing Engine.
- **Prompt 42**: Rewarded Video Ads for Solo Hints (Daily Free 3 Hint Cap + Opt-In Watch).
- **Prompt 43**: Google Play Billing SDK v7 Integration (Monthly ₹99 & 6-Month ₹499 Subscription Products).
- **Prompt 44**: Backend Receipt Verification & Local Premium Entitlement Enforcement (Ad Removal, Themes).

### Phase 9: Security, Validation, Profiling & QA (Prompts 45–48)
- **Prompt 45**: Anti-Cheat & Input Sanitization (Strict Orthogonal Validation, Packet Throttling).
- **Prompt 46**: Performance Profiling (Android GPU Profiler, 120 FPS Target, Memory Leak Auditing).
- **Prompt 47**: Battery & Thermal Optimization (Minimizing Recompositions & WakeLocks).
- **Prompt 48**: Comprehensive Automated Test Suite (Unit Tests, Room DAOs, Spring WebSocket Integration).

### Phase 10: Release Preparation & Google Play Launch (Prompts 49–50)
- **Prompt 49**: Production Build Optimization (R8 ProGuard Rules, Keystore Signing, App Bundle Generation).
- **Prompt 50**: Google Play Console Asset Preparation, Store Listing, Privacy Policy, and Production Audit.
