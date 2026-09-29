package com.zynpath.backend.reaction.model;

/**
 * Predefined, polite in-match reactions.
 * Arbitrary user-generated text is strictly disallowed; reactions are held exclusively in memory.
 */
public enum ReactionType {
    WOW("Wow!"),
    NICE("Nice!"),
    GG("GG!"),
    WELL_PLAYED("Well played!"),
    GOOD_LUCK("Good luck!"),
    AMAZING("Amazing!"),
    REMATCH("Rematch!");

    private final String displayText;

    ReactionType(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
