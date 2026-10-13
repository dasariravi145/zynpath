package com.zynpath.backend.multiplayer.generator;

/**
 * Competitive difficulty tiers for Zynpath online multiplayer (Quick Duel and Friends Arena).
 *
 * Requirements:
 * - VERY_HARD: 10x10 to 11x11, target human solving time 5–10 minutes, cognitive score >= 0.48
 * - EXPERT: 11x11 to 12x12, target human solving time 10–15 minutes, cognitive score >= 0.58
 * - MASTER: 12x12 (13x13/14x14 evaluated and capped at 12x12 due to mobile touch target/ergonomics limits),
 *           target human solving time 15–25 minutes, cognitive score >= 0.68
 */
public enum CompetitiveDifficultyTier {
    VERY_HARD("VERY_HARD", 10, 11, 0.48, 0.65, 5, 10),
    EXPERT("EXPERT", 11, 12, 0.58, 0.75, 10, 15),
    MASTER("MASTER", 12, 12, 0.68, 1.00, 15, 25);

    private final String id;
    private final int minGridDimension;
    private final int maxGridDimension;
    private final double minCognitiveScore;
    private final double maxCognitiveScore;
    private final int targetMinMinutes;
    private final int targetMaxMinutes;

    CompetitiveDifficultyTier(
            String id,
            int minGridDimension,
            int maxGridDimension,
            double minCognitiveScore,
            double maxCognitiveScore,
            int targetMinMinutes,
            int targetMaxMinutes
    ) {
        this.id = id;
        this.minGridDimension = minGridDimension;
        this.maxGridDimension = maxGridDimension;
        this.minCognitiveScore = minCognitiveScore;
        this.maxCognitiveScore = maxCognitiveScore;
        this.targetMinMinutes = targetMinMinutes;
        this.targetMaxMinutes = targetMaxMinutes;
    }

    public String getId() {
        return id;
    }

    public int getMinGridDimension() {
        return minGridDimension;
    }

    public int getMaxGridDimension() {
        return maxGridDimension;
    }

    public double getMinCognitiveScore() {
        return minCognitiveScore;
    }

    public double getMaxCognitiveScore() {
        return maxCognitiveScore;
    }

    public int getTargetMinMinutes() {
        return targetMinMinutes;
    }

    public int getTargetMaxMinutes() {
        return targetMaxMinutes;
    }

    public static CompetitiveDifficultyTier fromString(String value) {
        if (value == null || value.isBlank()) {
            return VERY_HARD;
        }
        for (CompetitiveDifficultyTier tier : values()) {
            if (tier.name().equalsIgnoreCase(value) || tier.id.equalsIgnoreCase(value)) {
                return tier;
            }
        }
        return VERY_HARD;
    }
}
