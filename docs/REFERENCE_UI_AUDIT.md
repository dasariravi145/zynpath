# Zynpath Reference UI Audit & Master Design Specification

**Document Version:** 1.0.0  
**Source Master Reference:** `D:\Zynpath\assets\reference_composite.png` (1536 × 1024 px)  
**Status:** Approved Master Baseline  
**Scope:** 12 Screen Panels across Solo, Social, Multiplayer, and Economy Systems  

---

## 1. Executive Summary

This document establishes the authoritative visual and UX audit for **Zynpath**, derived directly from the approved 1536 × 1024 master composite reference (`assets/reference_composite.png`). All 12 phone panels have been cropped with pixel precision into `assets/reference_panels/` without overlap or label bleed:

- **Row 1 (Solo & Core Game Loop):**
  - Panel 01: `01_splash_screen.png` — Splash & Brand Loading Screen
  - Panel 02: `02_login_screen.png` — Guest-First & Facebook Onboarding / Sign-In
  - Panel 03: `03_home_screen.png` — Primary Dashboard & Mode Hub
  - Panel 04: `04_level_selection.png` — World & Level Progression Grid
  - Panel 05: `05_gameplay_screen.png` — Active Solo Puzzle Canvas & Action Controls
  - Panel 07: `07_rewards_screen.png` — Daily Streak Rewards, Missions & Treasure Chest
- **Row 2 (Social, Lobby & Real-Time Multiplayer):**
  - Panel 08: `08_friends_screen.png` — Facebook Friends Arena & Invitations
  - Panel 09: `09_create_room.png` — Room Creation & Game Mode Configuration (1–5 Players)
  - Panel 10: `10_invite_friends.png` — Facebook Friend Selection & Batch Invite
  - Panel 11: `11_waiting_room.png` — Real-Time Waiting Lobby & Ready Status
  - Panel 12: `12_multiplayer_gameplay.png` — Live Multi-Player Race & Opponent Progress Bar
  - Panel 13: `13_multiplayer_result.png` — Olympic Winner Podium & Results Presentation

---

## 2. Design System Tokens & Color Palette

### 2.1 Core Palette Tokens
The master reference uses a luminous, deep fantasy theme with rich saturated gradients, glowing neon paths, and glossy arcade-style buttons.

| Token Name | Hex Code | Visual Role / Usage |
| :--- | :--- | :--- |
| `ZynpathNavyDark` | `#050D1A` | Deepest background base, system bars, modal scrims |
| `ZynpathNavySurface` | `#0A1931` | Cards, panels, unvisited level tiles, bottom nav bar |
| `ZynpathNavyElevated`| `#0F264A` | Secondary buttons, inactive tabs, unselected player count |
| `ZynpathNavyBorder`  | `#1A3B6E` | Tile outlines, input borders, divider strokes |
| `ZynpathCyanGlow`    | `#00E5FF` | Active path ribbon, covered number tile fill, player glow |
| `ZynpathCyanNeon`    | `#00B0FF` | Selected player count pill, active checkboxes |
| `ZynpathGoldPrimary` | `#FFB300` | Primary CTA Play button, Create Room, Send Invite button |
| `ZynpathGoldGradient`| `#FFC107` to `#FF8F00` | Glossy pill button highlights, reward claim button gradient |
| `ZynpathStarGold`    | `#FFD54F` | Star counter, earned rating stars, champion crowns |
| `ZynpathCoinGold`    | `#FFA000` | Coin currency counter, treasure chest gold |
| `ZynpathGreenClaim`  | `#00C853` | "Claim" button, player "Ready" badge, winner podium line |
| `ZynpathFacebookBlue`| `#1877F2` | "Continue with Facebook", Facebook friend cards |
| `ZynpathSilverPill`  | `#E2E8F0` | "Play as Guest" pill surface |
| `ZynpathRedBadge`    | `#FF3D00` | Hint / Shuffle / Undo remaining quantity counter badge |
| `ZynpathWhite`       | `#FFFFFF` | Primary headers, number glyphs, button text |
| `ZynpathTextMuted`   | `#8E9EB5` | Subtitles, helper text, status disclaimers |

### 2.2 Typography Hierarchy
- **Game Brand Title**: Bold Rounded Display / Sans-Serif, 3D golden bevel (`#FFE066` with `#E65100` drop shadow and navy outline).
- **Screen Headers**: Centered, Bold Semi-rounded, 18–20sp, Pure White (`#FFFFFF`).
- **Primary CTA**: Uppercase Bold Sans-Serif, 18–20sp, Dark Navy/Black text (`#1A0C00` or `#050D1A`) over Golden Yellow.
- **Section Headers**: 14–16sp, Semi-Bold, `#FFFFFF`.
- **Number Glyphs**: 18–24sp, Extra Bold Rounded, `#FFFFFF` on active cyan and inactive dark tiles.
- **Counters & Badges**: 12–14sp, Bold, `#FFFFFF`.

### 2.3 Shapes & Geometry
- **Primary CTA Buttons**: Fully rounded capsules / pill buttons (height 52–56dp, corner radius 28dp) with subtle top highlight shine.
- **Game Tiles**: Rounded squares (corner radius 12–14dp) with inset depth border and glowing neon inner shadow when active.
- **Modal Panels**: Rounded surfaces (corner radius 20–24dp) with 1.5dp cyan/navy border.
- **Avatars**: Circular with 2dp glowing status rings (Cyan for You, Orange/Gold for Rahul, Pink for Priya, Orange for Amit).

---

## 3. Screen-by-Screen Comprehensive Audit

### Panel 01: Splash Screen (`01_splash_screen.png`)
- **Android Route**: `Screen.Splash` (`feature/splash/SplashScreen.kt`)
- **Visual Composition**:
  - Full-bleed scenic fantasy artwork: Soaring mountain peak illuminated by solar ring halo, lush emerald green valley with winding stepping stone path.
  - Three luminous stepping stones floating above forest: numbers "3", "2", "3" connected by glowing cyan beam, flanked by rustic torches with warm golden flames.
  - Title Logo: "ZYNPATH" in 3D golden-yellow typography with cyan drop shadow, subtitle "NUMBER PATH PUZZLE" in white, tagline "Connect the Numbers \n Conquer the Path" in warm gold (`#FFE066`).
  - Bottom Loading Indicator: Dark navy capsule bar with electric cyan progress fill (~40%) and bold white "Loading..." text below.

### Panel 02: Login Screen (`02_login_screen.png`)
- **Android Route**: `Screen.SignIn` (`feature/auth/SignInScreen.kt`)
- **Visual Composition**:
  - Full-bleed artwork: 3D cartoon boy explorer (blue shirt, backpack) and girl explorer (pink shirt, backpack) on grassy hilltop with neon stepping stones ("7", "2") between them, scenic mountain vista at sunrise.
  - Top Logo: "ZYNPATH NUMBER PATH PUZZLE".
  - Action Buttons:
    1. Primary Social Button: "Continue with Facebook" — Solid Facebook blue (`#1877F2`), white Facebook 'f' icon, white bold text.
    2. Primary Guest Button: "Play as Guest" — Light silver-white pill (`#E2E8F0` to `#FFFFFF`), dark navy user icon, dark navy bold text (`#0D1B2A`).
  - Footer Text: "Play with friends • Save progress \n Compete • Win rewards" in soft slate blue (`#8E9EB5`).
  - Zero forced login: Guest allows instantaneous offline play without network barrier.

### Panel 03: Home Screen (`03_home_screen.png`)
- **Android Route**: `Screen.Home` (`feature/home/HomeScreen.kt`)
- **Visual Composition**:
  - Header Bar:
    - Left: Player avatar in blue circular ring, label "Player_123", pill badge "Level 8".
    - Center-Right: Coin counter (`2,450` with gold coin icon), Star counter (`12` with gold star icon) in navy pill capsules.
    - Right: Settings gear icon button.
  - Hero Artwork & CTA:
    - Floating mountain island with golden trophy atop mountain peak, glowing blue numbered stepping stones winding up (1, 2, 4, 5).
    - Big Golden Yellow pill button: "▶ PLAY" with black text, subtle bevel/shadow (triggers immediate continuation of highest unlocked level or solo world).
  - Secondary Game Modes (3 equal rounded square cards):
    1. "QUICK DUEL": Crossed swords icon, navy card with cyan border, title "QUICK \n DUEL".
    2. "FRIENDS ARENA": 3-player silhouette icon, navy card, title "FRIENDS \n ARENA".
    3. "DAILY CHALLENGE": Calendar icon with "28", navy card, title "DAILY \n CHALLENGE".
  - Bottom Navigation Bar (5 tabs):
    - Dark navy docked bar with 5 items: Home (active yellow house + text), Game (crossed swords/controller), Rewards (gift), Friends (people), Profile (avatar).

### Panel 04: Level Selection Screen (`04_level_selection.png`)
- **Android Route**: `Screen.LevelSelection` (`feature/level/LevelSelectionScreen.kt`)
- **Visual Composition**:
  - Header: Back arrow `<` in dark disc, title "Select Level", forward arrow `>` in dark disc.
  - World Tabs: "World 1" (active bright gold pill with dark text), "World 2" (dark blue pill), "World 3" (dark blue pill).
  - Level Grid (4 columns):
    - Completed Levels (1, 2, 3, 4, 5): Vibrant cyan glowing rounded square cards, bold white level number, gold stars below (`★★☆` or `★★★`).
    - Unlocked / Current Levels (6, 7, 8): Dark navy cards with cyan border, white numbers, 1 star or empty star outline.
    - Locked Levels (9, 10, 11, 12): Dark navy cards, white numbers, silver padlock icon below.
  - Bottom Hero Artwork: Fantasy valley landscape with floating temple island and glowing star on peak.

### Panel 05: Gameplay Screen (`05_gameplay_screen.png`)
- **Android Route**: `Screen.Gameplay` (`feature/gameplay/GameplayShellScreen.kt`)
- **Visual Composition**:
  - Top Bar: Pause circular button `||` on left, "Level 8" title centered with 3-star bar below (2 gold stars filled), restart/shuffle icon on right.
  - Timer Capsule: Dark navy pill with clock icon, "00:28".
  - Interactive Puzzle Grid:
    - 4 columns × 5 rows (20 cells).
    - Active path (1 - 2 - 3 - 4 - 8): Connected by continuous luminous cyan path ribbon. Active cells have bright cyan gradient fill with white numbers.
    - Inactive cells: Dark navy rounded squares with crisp white numbers.
    - Background: Translucent overlay showcasing scenic jungle foliage and distant mountain river.
  - Bottom Action Toolbar (3 rounded cards with red notification badges):
    1. "Hint": Yellow glowing bulb icon, red circle badge "3", label "Hint".
    2. "Shuffle": Cyan crossing arrows icon, red circle badge "2", label "Shuffle".
    3. "Undo": Cyan curved return arrow icon, red circle badge "2", label "Undo".

### Panel 07: Rewards Screen (`07_rewards_screen.png`)
- **Android Route**: `Screen.DailyChallenge` / `DailyRewards` (`feature/daily/DailyChallengeScreen.kt`)
- **Visual Composition**:
  - Top Bar: Currency pill (`2,450`), Star pill (`12`), Settings gear icon.
  - Title: "REWARDS".
  - Segmented Navigation: 3 pill tabs: "Daily" (active gold pill), "Missions" (dark navy), "Achievements" (dark navy).
  - Daily Rewards Banner: Blue gift box with gold ribbon on left, "Daily Rewards \n Play daily and earn amazing rewards!" on right.
  - 5-Day Streak Progress Row:
    - Day 1: "Day 1", green checkmark badge, "100" coins.
    - Day 2: "Day 2", gold highlighted card, coin icon, "200" coins in dark pill.
    - Day 3: "Day 3", star icon, "1" star.
    - Day 4: "Day 4", coin icon, "300" coins.
    - Day 5: "Day 5", red gift box icon, "1" mystery gift.
  - Hero Reward Presentation: Glowing gold treasure chest spilling gold coins on stone tiles with starburst lighting, "DAY 2 REWARD \n 200 Coins".
  - Primary CTA: Big bright green pill button: "Claim".

### Panel 08: Friends Screen (`08_friends_screen.png`)
- **Android Route**: `Screen.Friends` (`feature/friends/FriendsScreen.kt`)
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "FRIENDS" centered.
  - Tabs: "Facebook Friends" (active gold pill), "Invitations" (dark navy pill).
  - Social Connect Card: Blue circular Facebook logo, text "Connect with Facebook \n Play with your friends \n in Friends Arena", blue button with Facebook 'f' icon + "Connect Facebook".
  - Suggested Friends Section:
    - Title: "Suggested Friends".
    - 4 friend cards: Rahul Sharma ("Play together!"), Priya Verma ("Challenge now!"), Amit Kumar ("Join my game!"), Neha Reddy ("Let's play!"). Each with circular avatar and blue "Invite" pill button.

### Panel 09: Create Room (`09_create_room.png`)
- **Android Route**: `Screen.FriendsArena` / `FriendsArenaEntryScreen.kt`
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "Create Game Room" centered.
  - Player Count Selector:
    - Label: "Select number of players \n (Max 5)".
    - 5 square buttons: "1", "2", "3", "4", "5". Slot "3" is active with bright cyan fill; others are dark navy with cyan outline.
  - Invite Friends Section: Dark blue card with square "+" button, "Add from Facebook \n Select your Facebook friends".
  - Room Settings Section:
    - Row 1: Dice/Map icon, "Level Selection", value "Random Level >".
    - Row 2: Crown/Game icon, "Game Mode", value "Standard >".
  - Primary CTA: Golden yellow pill button: "Create Room".

### Panel 10: Invite Friends (`10_invite_friends.png`)
- **Android Route**: `Screen.FriendsArenaFacebook` / `FriendsArenaFacebookScreen.kt`
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "Invite Friends" centered.
  - Search Input: Dark navy rounded capsule with magnifying glass icon, placeholder "Search Facebook Friends...".
  - Selectable Friend List:
    - Items with circular avatars and selection circles:
      - Rahul Sharma: Checked (cyan circular checkmark badge).
      - Priya Verma: Checked (cyan circular checkmark badge).
      - Amit Kumar: Unchecked (empty dark circular ring).
      - Neha Reddy: Unchecked (empty dark circular ring).
      - Vikram Singh: Unchecked (empty dark circular ring).
  - Primary CTA: Golden yellow pill button: "Send Invitation (2)".

### Panel 11: Waiting Room (`11_waiting_room.png`)
- **Android Route**: `Screen.FriendsArenaRoom` / `FriendsArenaRoomScreen.kt`
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "Game Room" centered, Room Code badge on top right: "Room Code \n ZP4587" with copy icon.
  - Player Lobby Grid:
    - Slot 1: "You (Host)" — Boy avatar with cyan glow aura, "You \n (Host)".
    - Slot 2: "Rahul" — Boy avatar with orange aura, "Rahul", "Ready" in bright green.
    - Slot 3: "Priya" — Girl avatar with pink aura, "Priya", "Ready" in bright green.
    - Slot 4: Empty slot — Dark blue circle with "+" icon, "Invite Friend \n (4/5)".
    - Slot 5: Empty slot — Dark blue circle with "+" icon, "Invite Friend \n (5/5)".
  - Status Text: "Waiting for players..." centered.
  - Primary Action Button: Large pill button "Start Game" (disabled or ready based on minimum player threshold).

### Panel 12: Multiplayer Gameplay (`12_multiplayer_gameplay.png`)
- **Android Route**: `Screen.FriendsArenaGameplay` / `FriendsArenaGameplayScreen.kt`
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "Multiplayer - Level 5", small icon on right.
  - Timer: Dark navy pill with clock icon, "00:25".
  - Real-Time Puzzle Grid:
    - 4x5 grid with numbers 1 to 20.
    - Active path (1 - 2 - 3 - 5): Cyan glowing connected path ribbon.
  - Live Opponent Score Bar at Bottom (4 player cards in a row):
    - You: Avatar in cyan ring, "You", score "28" in green, green active progress bar line underneath.
    - Rahul: Avatar in cyan ring, "Rahul", score "24" in cyan.
    - Priya: Avatar in pink ring, "Priya", score "26" in yellow.
    - Amit: Avatar in orange ring, "Amit", score "22" in orange, orange progress bar line.

### Panel 13: Multiplayer Result (`13_multiplayer_result.png`)
- **Android Route**: `Screen.FriendsArenaResults` / `FriendsArenaResultsScreen.kt`
- **Visual Composition**:
  - Header: Back arrow `<` on left, title "Game Result" centered, Share icon on right.
  - Celebration: Colorful confetti particles falling in background.
  - 3D Olympic Winner Podium:
    - Rank 1 (Center, Highest Gold block): Golden crown above "You" avatar, "1 \n You \n Score: 28", embossed golden "1" on pedestal.
    - Rank 2 (Left, Silver block): "Priya" avatar, "2 \n Priya \n Score: 26".
    - Rank 3 (Right, Bronze block): "Rahul" avatar, "3 \n Rahul \n Score: 24".
  - Remaining Players Rank List:
    - Rank 4: Dark rounded card, "4", Amit avatar, name "Amit", right-aligned "Score: 22".
    - Rank 5: Dark rounded card, "5", Neha avatar, name "Neha", right-aligned "Score: 18".
  - Action Buttons:
    - Top Button: Big bright golden yellow pill button: "Play Again".
    - Bottom Button: Blue pill button: "Back to Home".

---

## 4. Preservation & Technical Integrity Guarantee

1. **Gameplay & Puzzle Mechanics**:
   - The authoritative 300 levels (Worlds 1–6) across variable grid sizes (3×3 up to 8×8), checkpoints, walls, and continuous Hamiltonian path logic are preserved.
2. **Local Persistence**:
   - Guest identity UUID, Room Database (`LevelProgressEntity`, `PlayerStatsEntity`, `DailyChallengeEntity`), and DataStore user preferences are strictly preserved.
   - **No app uninstall or data clearance** will be performed.
3. **Multiplayer Architecture**:
   - Real-time WebSocket matchmaking, WebSocket protocol schemas, room codes, reaction relay, and authoritative result verification remain fully integrated with the visual overhaul.
4. **Monetization & Ads**:
   - Rewarded ad hint grants, AdMob placement constraints, and Google Play Billing dynamic pricing (₹99/mo, ₹499/6mo) remain fully intact.

---

## 5. Implementation Roadmap (Phase B & C)

1. **Design System & Theme Updates**:
   - Update `Color.kt` and `ZynpathColorPalette.kt` with master reference hex tokens.
   - Implement `ZynpathMasterComponents.kt` for exact matching pill buttons, currency capsules, level tiles with glowing stars, and bottom navigation.
   - Integrate scenic high-res background art and character avatars into Android drawable resources.
2. **Screen-by-Screen Visual Alignment**:
   - Update `SplashScreen`, `SignInScreen`, `HomeScreen`, `LevelSelectionScreen`, `GameplayShellScreen`, `DailyChallengeScreen`, `FriendsScreen`, and `FriendsArena` screens.
3. **Visual Verification**:
   - Deploy to Android emulator `Pixel_7`, capture live device screenshots, and compare pixel-by-pixel with the reference master panels.
