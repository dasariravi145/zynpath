package com.zynpath.backend.cosmetics.model;

import java.util.List;

/**
 * Data transfer objects for cosmetic catalog and player equipment endpoints.
 *
 * Implements Prompt 28 Sections 31, 32, 51 & 54:
 * - Public cosmetic metadata exposes strictly ZERO billing or subscription tokens.
 * - Granular error messages for invalid or unauthorized equipment.
 */
public final class CosmeticDto {

    private CosmeticDto() {}

    public record CosmeticCatalogResponse(
        CosmeticCatalogVersion version,
        List<CosmeticCatalogItem> items
    ) {}

    public record EquippedCosmeticsDto(
        String themeId,
        String pathEffectId,
        String avatarFrameId,
        long updatedAtMs
    ) {}

    public record UpdateEquippedCosmeticsRequest(
        String themeId,
        String pathEffectId,
        String avatarFrameId
    ) {}

    public record UpdateEquippedCosmeticsResponse(
        boolean success,
        String errorCode,
        String message,
        EquippedCosmeticsDto equipped
    ) {}

    /**
     * Public metadata for opponent and friend profiles.
     * Section 31: strictly NO purchase tokens, billing accounts, or private entitlement data.
     */
    public record PublicPlayerCosmeticsDto(
        String playerId,
        String themeId,
        String pathEffectId,
        String avatarFrameId
    ) {}
}
