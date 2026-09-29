package com.zynpath.backend.analytics.controller;

import com.zynpath.backend.analytics.model.PersonalAnalyticsDto.PersonalAnalyticsReportDto;
import com.zynpath.backend.analytics.service.PersonalAnalyticsService;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for server-authoritative personal analytics.
 * Implements Prompt 30 Section 56 and Prompt 36 Sections 18, 50.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequireAccess(EndpointAccessTier.AUTHENTICATED)
public class PersonalAnalyticsController {

    private final PersonalAnalyticsService personalAnalyticsService;
    private final SessionSecurityService sessionSecurityService;

    public PersonalAnalyticsController(
            PersonalAnalyticsService personalAnalyticsService,
            SessionSecurityService sessionSecurityService
    ) {
        this.personalAnalyticsService = personalAnalyticsService;
        this.sessionSecurityService = sessionSecurityService;
    }

    @GetMapping("/personal")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PersonalAnalyticsReportDto> getPersonalAnalytics(
            @RequestHeader("Authorization") String authHeader
    ) {
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        PersonalAnalyticsReportDto report = personalAnalyticsService.getPersonalAnalytics(session.playerId());
        return ResponseEntity.ok(report);
    }
}
