# Zynpath Cosmetic Customization System

## 1. Overview & Core Product Principles

The Cosmetic Customization System in **Zynpath: Number Path Puzzle** enables visual personalization across themes, path drawing effects, and avatar frames while maintaining strict competitive integrity.

### 1.1 Inviolable Fair-Play Principles
Per Prompt 28 Section 5 & 25:
- **No Gameplay Advantage**: Cosmetics never alter puzzle rules, grid dimensions, cell connectivity, wall collisions, checkpoint ordering, or win conditions.
- **No Solution Assistance**: Path effects and themes never reveal the next legal move, upcoming solution path, or hidden checkpoint order.
- **No Competitive Skew**: Matchmaking, rating algorithms, timers, and scoring in Quick Duel, Friend Duel, Mini League, and Daily Challenge remain strictly identical regardless of cosmetic tier.
- **Complete Free Experience**: Free players have access to accessible, polished defaults (Classic Midnight, Pure Dark OLED themes, Solid Glow path effect, Slate Ring and Silver Crest avatar frames).

---

## 2. Cosmetic Categories

The architecture categorizes cosmetics into three primary categories using stable identifiers:

| Category | Display Name | Storage Key | Description |
|---|---|---|---|
| `THEME` | Themes | `equipped_theme_id` | Overall visual palette (surfaces, board canvas, checkpoints, walls, text, buttons) |
| `PATH_EFFECT` | Path Effects | `equipped_path_effect_id` | Styling of the continuous connected path trail on the puzzle board |
| `AVATAR_FRAME` | Avatar Frames | `equipped_avatar_frame_id` | Border and ornamentation surrounding the player's profile avatar |

---

## 3. Catalog Architecture

All cosmetics are catalog-driven (`CosmeticCatalog.kt` on Android and `CosmeticService.java` on Backend).

### 3.1 Catalog Item Schema
Each item defines:
- `id`: Stable string identifier (e.g. `theme_cyber_neon`).
- `category`: `THEME`, `PATH_EFFECT`, or `AVATAR_FRAME`.
- `name`: Human-readable localized title.
- `description`: Visual description and styling hints.
- `accessStatus`: Availability tier (`FREE`, `PREMIUM`, `COMING_SOON`, `UNAVAILABLE`).
- `requiredFeatureKey`: Authoritative entitlement key (`PREMIUM_THEMES`, `PREMIUM_PATH_EFFECTS`, `PREMIUM_AVATAR_FRAMES`).
- `isDefault`: Boolean indicating the base fallback item.
- `version`: Monotonically increasing catalog version.

### 3.2 Access Decisions
Authoritative evaluation combines catalog item metadata with `SubscriptionEntitlement`:
- `AVAILABLE`: Item is free or user possesses active premium entitlement. Can be equipped.
- `LOCKED`: Item is premium and user lacks active subscription. Can be previewed, cannot be equipped.
- `UNAVAILABLE` / `COMING_SOON`: Item is not yet released or unsupported. Cannot be equipped.

---

## 4. Preview vs. Equip Paradigm

Per Prompt 28 Section 12:
- **Preview Isolation**: Users can preview any cosmetic item (free or premium) directly on the `CosmeticsScreen`.
- **Ephemeral State**: Previewing an item updates the live preview board and avatar frame in memory, but does **not** persist to `DataStore` or backend.
- **Authoritative Gating**: Only items evaluated as `AVAILABLE` can be committed via "Equip".
- **Locked Items**: Clicking a locked preview presents an "Unlock with Premium" action navigating directly to `PremiumScreen`.

---

## 5. Persistence, Account Isolation & Offline Access

### 5.1 Storage Architecture
- Local preferences persisted in Jetpack `DataStore` (`PreferencesRepository`).
- Authoritative entitlement managed by `SubscriptionEntitlementRepository`.
- Server synchronization through `CosmeticsApiService` (`/api/v1/cosmetics/equipped`).

### 5.2 Subscription Expiration Fallback
When a subscription expires:
1. User's saved preference in `DataStore` is **preserved** (never deleted).
2. The rendering pipeline (`resolveEffectiveCosmetic`) safely falls back to the free default (`theme_classic_midnight`, `path_solid_glow`, `frame_default_slate`).
3. Upon resubscription, the existing preference is re-authorized and restored automatically.

### 5.3 Account Isolation
When an account switches:
1. `SubscriptionEntitlementRepository.onAccountSwitched` clears the cached entitlement.
2. The new account's entitlement is loaded or reset to `free()`.
3. Premium access is isolated; unauthorized accounts revert to free defaults without leaking access.

---

## 6. Settings Integration & Accessibility Overrides (Prompt 32)
- **Appearance Entrypoint**: The unified Settings screen includes an explicit "Cosmetics & Themes" row routing to `Screen.Cosmetics`.
- **Accessibility Priority**: When High Contrast mode (`isHighContrast = true`) or Reduced Motion (`isReducedMotion = true`) is enabled in Settings, the rendering pipeline enforces high-contrast borders and suppresses animated path pulses, ensuring cosmetic selections never compromise essential accessibility.

---

## 7. Audio, Haptics & Cosmetic Parity (Prompt 34)
- **Strict Auditory & Tactile Parity**: Equipping premium themes, path effects, or avatar frames does not alter the timing, frequency, or duration of gameplay sounds or haptics.
- **Zero Cosmetic Advantage**: All players receive the exact same 40ms audio throttle, 50ms haptic throttle, and 200ms rejection throttling regardless of cosmetic customizations or subscription status.

---

## 8. Cosmetic Rendering Performance & Graceful Degradation (Prompt 37)
- **Draw-Phase Cosmetic Shader & Dash Effects**: Animated path shaders and dashed strokes are applied strictly within Canvas draw scope, avoiding composable recompositions.
- **Graceful Lower-End Device Scaling**: When `isReducedMotion == true` or when operating on memory-constrained devices, complex cosmetic particles and multi-layered glow effects gracefully simplify to clean solid strokes. Checkpoint numerals and blocked-edge walls remain 100% visible and readable under all cosmetic configurations.
- **Bounded Preview Caching**: Cosmetic catalog artwork and theme previews load vector assets lazily, preventing large raster memory allocations in background screens.

---

## 9. Accessibility Guarantees & Color-Blind Safety (Prompt 39)
- **Non-Color Discrimination**: Themes must maintain legible contrast on checkpoints ($\ge 4.5:1$ against cell background). Checkpoints always feature high-contrast numerals $1..N$; walls are thick distinctive boundaries ($5\text{ dp}$); path state is never conveyed purely by color hue.
- **TalkBack Catalog Descriptions**: Every theme, path effect, and avatar frame in the catalog provides detailed `contentDescription` detailing name, category, tier, and equipped state.



