package com.zynpath.backend.puzzle.service;

import com.zynpath.backend.puzzle.model.CompetitiveValidationClaim;
import com.zynpath.backend.puzzle.model.ValidationOutcome;

/**
 * Authoritative boundary service for re-verifying full cell coverage, orthogonal steps,
 * and checkpoint sequence for multiplayer competition.
 */
public interface AuthoritativePuzzleValidator {
    ValidationOutcome validateCompetitiveSolution(CompetitiveValidationClaim claim);
}
