package com.zynpath.backend.cosmetics.service;

import com.zynpath.backend.cosmetics.model.CosmeticAccessStatus;
import com.zynpath.backend.cosmetics.model.CosmeticCatalogItem;
import com.zynpath.backend.cosmetics.model.CosmeticCatalogVersion;
import com.zynpath.backend.cosmetics.model.CosmeticCategory;
import com.zynpath.backend.cosmetics.model.CosmeticDto.CosmeticCatalogResponse;
import com.zynpath.backend.cosmetics.model.CosmeticDto.EquippedCosmeticsDto;
import com.zynpath.backend.cosmetics.model.CosmeticDto.PublicPlayerCosmeticsDto;
import com.zynpath.backend.cosmetics.model.CosmeticDto.UpdateEquippedCosmeticsRequest;
import com.zynpath.backend.cosmetics.model.CosmeticDto.UpdateEquippedCosmeticsResponse;
import com.zynpath.backend.cosmetics.model.PlayerCosmeticSelection;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service governing cosmetic catalog queries, server-authoritative equipment validation,
 * and subscription expiration fallbacks.
 *
 * Implements Prompt 28 Sections 7, 8, 10, 11, 31, 32, 40, 51 & 52.
 */
@Service
public class CosmeticService {

    private static final Logger log = LoggerFactory.getLogger(CosmeticService.class);

    private final SubscriptionService subscriptionService;
    private final Map<String, PlayerCosmeticSelection> playerSelections = new ConcurrentHashMap<>();

    private final List<CosmeticCatalogItem> catalogItems;
    private final CosmeticCatalogVersion catalogVersion;

    public CosmeticService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;

        // Authoritative catalog matching client catalog items
        this.catalogItems = List.of(
            // Themes
            new CosmeticCatalogItem("theme_classic_midnight", CosmeticCategory.THEME, "Classic Midnight", "Signature navy blue palette with soothing mint accents.", CosmeticAccessStatus.FREE, null, true, 1),
            new CosmeticCatalogItem("theme_pure_dark", CosmeticCategory.THEME, "Pure Dark OLED", "Deep OLED true black background for maximum contrast.", CosmeticAccessStatus.FREE, null, false, 1),
            new CosmeticCatalogItem("theme_cyber_neon", CosmeticCategory.THEME, "Cyber Neon", "Vibrant synthwave aesthetic with magenta and neon cyan.", CosmeticAccessStatus.PREMIUM, "PREMIUM_THEMES", false, 1),
            new CosmeticCatalogItem("theme_emerald_forest", CosmeticCategory.THEME, "Emerald Forest", "Deep botanical greens with jade checkpoints.", CosmeticAccessStatus.PREMIUM, "PREMIUM_THEMES", false, 1),
            new CosmeticCatalogItem("theme_solar_amber", CosmeticCategory.THEME, "Solar Amber", "Obsidian charcoal canvas paired with radiant sun-gold paths.", CosmeticAccessStatus.PREMIUM, "PREMIUM_THEMES", false, 1),
            new CosmeticCatalogItem("theme_arctic_frost", CosmeticCategory.THEME, "Arctic Frost", "Glacial blue tones and polar shimmer.", CosmeticAccessStatus.COMING_SOON, "PREMIUM_THEMES", false, 1),

            // Path Effects
            new CosmeticCatalogItem("path_solid_glow", CosmeticCategory.PATH_EFFECT, "Solid Glow", "Crisp dual-layer solid path with ambient glow.", CosmeticAccessStatus.FREE, null, true, 1),
            new CosmeticCatalogItem("path_cyan_pulse", CosmeticCategory.PATH_EFFECT, "Cyan Energy Pulse", "Harmonic pulse of electrical energy along connected path.", CosmeticAccessStatus.PREMIUM, "PREMIUM_PATH_EFFECTS", false, 1),
            new CosmeticCatalogItem("path_gold_shimmer", CosmeticCategory.PATH_EFFECT, "Golden Shimmer", "Prestigious gold dual-gradient path with starlight spine.", CosmeticAccessStatus.PREMIUM, "PREMIUM_PATH_EFFECTS", false, 1),
            new CosmeticCatalogItem("path_ember_trail", CosmeticCategory.PATH_EFFECT, "Ember Trail", "Warm blazing gradient radiating heat along the trail.", CosmeticAccessStatus.PREMIUM, "PREMIUM_PATH_EFFECTS", false, 1),
            new CosmeticCatalogItem("path_starlight", CosmeticCategory.PATH_EFFECT, "Starlight Stream", "Subtle cosmic particles drifting along the path.", CosmeticAccessStatus.COMING_SOON, "PREMIUM_PATH_EFFECTS", false, 1),

            // Avatar Frames
            new CosmeticCatalogItem("frame_default_slate", CosmeticCategory.AVATAR_FRAME, "Slate Ring", "Clean, understated circular border.", CosmeticAccessStatus.FREE, null, true, 1),
            new CosmeticCatalogItem("frame_silver_crest", CosmeticCategory.AVATAR_FRAME, "Silver Crest", "Polished silver border with four cardinal directional markers.", CosmeticAccessStatus.FREE, null, false, 1),
            new CosmeticCatalogItem("frame_gold_accent", CosmeticCategory.AVATAR_FRAME, "Royal Gold Crown", "Prestigious gold double-ring border with diamond crown.", CosmeticAccessStatus.PREMIUM, "PREMIUM_AVATAR_FRAMES", false, 1),
            new CosmeticCatalogItem("frame_neon_ring", CosmeticCategory.AVATAR_FRAME, "Cyber Ring", "Glowing cyberpunk frame with dual concentric neon lines.", CosmeticAccessStatus.PREMIUM, "PREMIUM_AVATAR_FRAMES", false, 1),
            new CosmeticCatalogItem("frame_emerald_geometric", CosmeticCategory.AVATAR_FRAME, "Emerald Facet", "Faceted geometric border with luminous emerald nodes.", CosmeticAccessStatus.PREMIUM, "PREMIUM_AVATAR_FRAMES", false, 1),
            new CosmeticCatalogItem("frame_cosmic_aurora", CosmeticCategory.AVATAR_FRAME, "Cosmic Aurora", "Iridescent celestial border.", CosmeticAccessStatus.COMING_SOON, "PREMIUM_AVATAR_FRAMES", false, 1)
        );

        this.catalogVersion = new CosmeticCatalogVersion(1, System.currentTimeMillis(), catalogItems.size());
    }

    public CosmeticCatalogResponse getCatalog() {
        return new CosmeticCatalogResponse(catalogVersion, Collections.unmodifiableList(catalogItems));
    }

    public EquippedCosmeticsDto getEquippedCosmetics(String playerId) {
        PlayerCosmeticSelection selection = playerSelections.getOrDefault(playerId, PlayerCosmeticSelection.defaultFor(playerId));
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isEntitled = entitlement != null && entitlement.isActive();

        // Enforce server-side expiration fallback
        String effectiveTheme = resolveEffective(selection.themeId(), CosmeticCategory.THEME, isEntitled);
        String effectivePath = resolveEffective(selection.pathEffectId(), CosmeticCategory.PATH_EFFECT, isEntitled);
        String effectiveFrame = resolveEffective(selection.avatarFrameId(), CosmeticCategory.AVATAR_FRAME, isEntitled);

        return new EquippedCosmeticsDto(effectiveTheme, effectivePath, effectiveFrame, selection.updatedAtMs());
    }

    public UpdateEquippedCosmeticsResponse updateEquippedCosmetics(String playerId, UpdateEquippedCosmeticsRequest request) {
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isEntitled = entitlement != null && entitlement.isActive();

        // Validate theme
        String newTheme = request.themeId() != null ? request.themeId() : PlayerCosmeticSelection.DEFAULT_THEME_ID;
        CosmeticCatalogItem themeItem = findItem(newTheme, CosmeticCategory.THEME);
        if (themeItem == null || themeItem.accessStatus() == CosmeticAccessStatus.UNAVAILABLE || themeItem.accessStatus() == CosmeticAccessStatus.COMING_SOON) {
            return new UpdateEquippedCosmeticsResponse(false, "COSMETIC_NOT_FOUND", "Requested theme item is not available in catalog", getEquippedCosmetics(playerId));
        }
        if (themeItem.accessStatus() == CosmeticAccessStatus.PREMIUM && !isEntitled) {
            return new UpdateEquippedCosmeticsResponse(false, "ENTITLEMENT_REQUIRED", "Theme requires active Premium subscription", getEquippedCosmetics(playerId));
        }

        // Validate path effect
        String newPath = request.pathEffectId() != null ? request.pathEffectId() : PlayerCosmeticSelection.DEFAULT_PATH_EFFECT_ID;
        CosmeticCatalogItem pathItem = findItem(newPath, CosmeticCategory.PATH_EFFECT);
        if (pathItem == null || pathItem.accessStatus() == CosmeticAccessStatus.UNAVAILABLE || pathItem.accessStatus() == CosmeticAccessStatus.COMING_SOON) {
            return new UpdateEquippedCosmeticsResponse(false, "COSMETIC_NOT_FOUND", "Requested path effect is not available in catalog", getEquippedCosmetics(playerId));
        }
        if (pathItem.accessStatus() == CosmeticAccessStatus.PREMIUM && !isEntitled) {
            return new UpdateEquippedCosmeticsResponse(false, "ENTITLEMENT_REQUIRED", "Path effect requires active Premium subscription", getEquippedCosmetics(playerId));
        }

        // Validate avatar frame
        String newFrame = request.avatarFrameId() != null ? request.avatarFrameId() : PlayerCosmeticSelection.DEFAULT_AVATAR_FRAME_ID;
        CosmeticCatalogItem frameItem = findItem(newFrame, CosmeticCategory.AVATAR_FRAME);
        if (frameItem == null || frameItem.accessStatus() == CosmeticAccessStatus.UNAVAILABLE || frameItem.accessStatus() == CosmeticAccessStatus.COMING_SOON) {
            return new UpdateEquippedCosmeticsResponse(false, "COSMETIC_NOT_FOUND", "Requested avatar frame is not available in catalog", getEquippedCosmetics(playerId));
        }
        if (frameItem.accessStatus() == CosmeticAccessStatus.PREMIUM && !isEntitled) {
            return new UpdateEquippedCosmeticsResponse(false, "ENTITLEMENT_REQUIRED", "Avatar frame requires active Premium subscription", getEquippedCosmetics(playerId));
        }

        long now = System.currentTimeMillis();
        PlayerCosmeticSelection updated = new PlayerCosmeticSelection(playerId, newTheme, newPath, newFrame, now);
        playerSelections.put(playerId, updated);
        log.info("Player {} updated equipped cosmetics: theme={}, path={}, frame={}", playerId, newTheme, newPath, newFrame);

        return new UpdateEquippedCosmeticsResponse(true, null, "Cosmetics equipped successfully",
            new EquippedCosmeticsDto(newTheme, newPath, newFrame, now));
    }

    /**
     * Public cosmetic metadata for multiplayer matches, lobbies, and friends.
     * Section 31: Exposes strictly zero billing information or purchase tokens.
     */
    public PublicPlayerCosmeticsDto getPublicPlayerCosmetics(String playerId) {
        EquippedCosmeticsDto equipped = getEquippedCosmetics(playerId);
        return new PublicPlayerCosmeticsDto(
            playerId,
            equipped.themeId(),
            equipped.pathEffectId(),
            equipped.avatarFrameId()
        );
    }

    private CosmeticCatalogItem findItem(String id, CosmeticCategory category) {
        return catalogItems.stream()
            .filter(item -> item.id().equals(id) && item.category() == category)
            .findFirst()
            .orElse(null);
    }

    private String resolveEffective(String id, CosmeticCategory category, boolean isEntitled) {
        CosmeticCatalogItem item = findItem(id, category);
        if (item == null) {
            return fallbackFor(category);
        }
        if (item.accessStatus() == CosmeticAccessStatus.FREE) {
            return item.id();
        }
        if (item.accessStatus() == CosmeticAccessStatus.PREMIUM && isEntitled) {
            return item.id();
        }
        return fallbackFor(category);
    }

    private String fallbackFor(CosmeticCategory category) {
        return switch (category) {
            case THEME -> PlayerCosmeticSelection.DEFAULT_THEME_ID;
            case PATH_EFFECT -> PlayerCosmeticSelection.DEFAULT_PATH_EFFECT_ID;
            case AVATAR_FRAME -> PlayerCosmeticSelection.DEFAULT_AVATAR_FRAME_ID;
        };
    }
}
