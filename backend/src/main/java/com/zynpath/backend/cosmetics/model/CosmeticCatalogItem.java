package com.zynpath.backend.cosmetics.model;

/**
 * Immutable catalog representation of a cosmetic item.
 * Implements Prompt 28 Section 7.
 */
public record CosmeticCatalogItem(
    String id,
    CosmeticCategory category,
    String name,
    String description,
    CosmeticAccessStatus accessStatus,
    String requiredFeatureKey,
    boolean isDefault,
    int version
) {}
