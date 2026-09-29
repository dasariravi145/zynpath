package com.zynpath.backend.cosmetics.model;

/**
 * Version metadata for cosmetic catalog cache invalidation.
 * Implements Prompt 28 Section 43 & 52.
 */
public record CosmeticCatalogVersion(
    int version,
    long publishedAtMs,
    int totalItems
) {}
