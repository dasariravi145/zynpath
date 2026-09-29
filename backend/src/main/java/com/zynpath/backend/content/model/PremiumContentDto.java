package com.zynpath.backend.content.model;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Premium Solo puzzle pack catalog, manifests, and secure content delivery.
 *
 * Implements Prompt 27 Sections 9, 10, 11, 16, 20 & 45.
 */
public class PremiumContentDto {

    public record PremiumPuzzleRefDto(
            String puzzleId,
            int puzzleVersion,
            String fingerprint,
            int levelIndex,
            String gridSize,
            int checkpointCount
    ) {}

    public record PremiumPackManifestDto(
            String packId,
            int packVersion,
            String displayName,
            String description,
            String difficulty,
            int puzzleCount,
            String requiredEntitlement,
            String checksum,
            List<PremiumPuzzleRefDto> puzzles
    ) {}

    public record PremiumPackDownloadResponse(
            PremiumPackManifestDto manifest,
            List<Map<String, Object>> puzzles
    ) {}
}
