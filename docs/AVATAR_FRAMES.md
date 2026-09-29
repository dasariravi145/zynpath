# Zynpath Avatar Frames Architecture & Presentation

## 1. Overview & Identity Boundaries

Avatar frames in **Zynpath** provide decorative borders surrounding player avatars across profiles, multiplayer lobbies, friend lists, and match results.

### 1.1 Non-Competitive Representation
Per Prompt 28 Section 29:
- Avatar frames represent aesthetic personalization and are never conflated with competitive rating or achievement ranks (unless explicitly designated as rank badges in future league seasons).
- The underlying player avatar (`AvatarOption`) remains fully visible within all frames.

---

## 2. Avatar Frames Catalog

### 2.1 Free Frames
#### 1. Slate Ring (`frame_default_slate`) — Default
- **Design**: Minimal circular slate outline (`#64748B`, stroke: 2.5dp).
- **Available**: Unconditionally free for all guest and registered players.

#### 2. Silver Crest (`frame_silver_crest`)
- **Design**: Concentric silver border (`#CBD5E1` & `#94A3B8`) accented with four cardinal directional markers.
- **Available**: Free for all players.

### 2.2 Premium Frames
#### 3. Royal Gold Crown (`frame_gold_accent`)
- **Design**: Dual-ring polished gold border (`#FFB703` & `#FFE082`) crowned with a royal diamond pin at the apex.
- **Requirement**: `PREMIUM_AVATAR_FRAMES` entitlement.

#### 4. Cyber Ring (`frame_neon_ring`)
- **Design**: Dual concentric neon lines featuring electric magenta (`#FF007F`) and neon cyan (`#00F5D4`) with subtle outer aura.
- **Requirement**: `PREMIUM_AVATAR_FRAMES` entitlement.

#### 5. Emerald Facet (`frame_emerald_geometric`)
- **Design**: Faceted geometric border crafted with luminous jade (`#2EC4B6`), spring green (`#80ED99`), and four corner amber gem markers.
- **Requirement**: `PREMIUM_AVATAR_FRAMES` entitlement.

#### 6. Cosmic Aurora (`frame_cosmic_aurora`) — Coming Soon
- **Status**: Cataloged as `COMING_SOON`.

---

## 3. Placement & Component Integration

Implemented via `AvatarWithFrame` (`com.zynpath.game.core.cosmetics.ui.AvatarWithFrame`):
- Scales from large profile headers (88–92dp), customization previews (110dp), down to list icons (40dp).
- Supports click listeners for avatar and cosmetic picker navigation.
- Preserves user privacy: public profiles transmit solely the frame ID (`PublicPlayerCosmeticsDto`) with zero billing data.
