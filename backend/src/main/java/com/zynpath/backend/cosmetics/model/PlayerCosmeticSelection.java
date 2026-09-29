package com.zynpath.backend.cosmetics.model;

/**
 * Persisted player cosmetic configuration.
 * Implements Prompt 28 Section 35 & 52.
 */
public record PlayerCosmeticSelection(
    String playerId,
    String themeId,
    String pathEffectId,
    String avatarFrameId,
    long updatedAtMs
) {
    public static final String DEFAULT_THEME_ID = "theme_classic_midnight";
    public static final String DEFAULT_PATH_EFFECT_ID = "path_solid_glow";
    public static final String DEFAULT_AVATAR_FRAME_ID = "frame_default_slate";

    public static PlayerCosmeticSelection defaultFor(String playerId) {
        return new PlayerCosmeticSelection(
            playerId,
            DEFAULT_THEME_ID,
            DEFAULT_PATH_EFFECT_ID,
            DEFAULT_AVATAR_FRAME_ID,
            System.currentTimeMillis()
        );
    }
}
