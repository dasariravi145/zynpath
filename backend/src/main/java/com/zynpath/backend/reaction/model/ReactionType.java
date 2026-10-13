package com.zynpath.backend.reaction.model;

/**
 * Predefined, polite in-match reactions.
 * Arbitrary user-generated text is strictly disallowed; reactions are held exclusively in memory.
 */
public enum ReactionType {
    // 4 Emojis
    THUMBS_UP("Well played!"),
    LIGHTNING("Speed run!"),
    FIRE("On fire!"),
    MIND_BLOWN("Incredible!"),

    // 8 Predefined Common Words
    HI("Hi!"),
    GOOD_LUCK("Good luck!"),
    NICE_MOVE("Nice move!"),
    WELL_PLAYED("Well played!"),
    GG("GG!"),
    WOW("Wow!"),
    THANKS("Thanks!"),
    REMATCH_Q("Rematch?"),

    // Legacy aliases
    NICE("Nice!"),
    AMAZING("Amazing!"),
    REMATCH("Rematch!");

    private final String displayText;

    ReactionType(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static boolean isValidToken(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            ReactionType.valueOf(token.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
