package com.zynpath.backend.security.context;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerSession;

/**
 * ThreadLocal security context providing authenticated principal identity and request metadata.
 *
 * Implements Prompt 36 Section 5, 16, 17:
 * - Eliminates reliance on client-supplied player ID parameters.
 * - Guarantees the server derives identity strictly from the verified session token.
 * - ThreadLocal is safely cleared after request completion.
 */
public final class SecurityContext {

    private static final ThreadLocal<SecurityContext> CURRENT = new ThreadLocal<>();

    private final PlayerSession session;
    private final String clientIp;
    private final String userAgent;

    public SecurityContext(PlayerSession session, String clientIp, String userAgent) {
        this.session = session;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }

    public static void set(SecurityContext context) {
        CURRENT.set(context);
    }

    public static SecurityContext get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static boolean isAuthenticated() {
        SecurityContext ctx = CURRENT.get();
        return ctx != null && ctx.session != null && !ctx.session.isExpired();
    }

    public static String requirePlayerId() {
        SecurityContext ctx = CURRENT.get();
        if (ctx == null || ctx.session == null) {
            throw AuthException.unauthenticated("Authentication required for this operation");
        }
        if (ctx.session.isExpired()) {
            throw AuthException.sessionExpired("Session has expired");
        }
        return ctx.session.playerId();
    }

    public static String getOptionalPlayerId() {
        SecurityContext ctx = CURRENT.get();
        return (ctx != null && ctx.session != null) ? ctx.session.playerId() : null;
    }

    public static PlayerSession getSession() {
        SecurityContext ctx = CURRENT.get();
        return ctx != null ? ctx.session : null;
    }

    public static String getClientIp() {
        SecurityContext ctx = CURRENT.get();
        return ctx != null ? ctx.clientIp : "unknown";
    }

    public static String getUserAgent() {
        SecurityContext ctx = CURRENT.get();
        return ctx != null ? ctx.userAgent : "unknown";
    }
}
