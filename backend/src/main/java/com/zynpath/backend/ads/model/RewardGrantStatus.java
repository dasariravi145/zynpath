package com.zynpath.backend.ads.model;

/**
 * Status outcomes for atomic reward grants.
 */
public enum RewardGrantStatus {
    SUCCESS,
    DUPLICATE_TRANSACTION,
    DUPLICATE_CLAIM_ALREADY_VERIFIED,
    OWNERSHIP_MISMATCH,
    CLAIM_PREVIOUSLY_REJECTED,
    DAILY_LIMIT_EXCEEDED,
    CREDIT_CAP_REACHED,
    AD_UNIT_MISMATCH,
    REWARD_TYPE_MISMATCH,
    DATABASE_ERROR
}
