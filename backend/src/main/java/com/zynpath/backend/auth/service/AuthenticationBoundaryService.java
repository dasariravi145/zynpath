package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthClaims;

/**
 * Boundary contract for authenticating player credentials for online competitive features.
 */
public interface AuthenticationBoundaryService {
    AuthClaims verifyBearerToken(String token);
}
