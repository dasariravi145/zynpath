package com.zynpath.backend.puzzle.model;

/**
 * Outcome of authoritative server-side puzzle solution verification.
 */
public record ValidationOutcome(
    boolean valid,
    String rejectionReason,
    long verifiedAt
) {
    public static ValidationOutcome success() {
        return new ValidationOutcome(true, null, System.currentTimeMillis());
    }

    public static ValidationOutcome failure(String reason) {
        return new ValidationOutcome(false, reason, System.currentTimeMillis());
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return rejectionReason;
    }
}
