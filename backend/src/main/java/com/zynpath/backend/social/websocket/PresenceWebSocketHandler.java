package com.zynpath.backend.social.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.social.model.PlayerPresenceState;
import com.zynpath.backend.social.service.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler managing authenticated real-time player presence lifecycle.
 *
 * Implements Prompt 19 Sections 30-35:
 * - Server-observed presence authority tied to verified session tokens.
 * - Handles connection established, lost, and heartbeats.
 * - Marks players ONLINE upon connect, OFFLINE upon disconnect/error.
 * - Prevents multiple duplicate stale sessions for the same player.
 */
@Component
public class PresenceWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(PresenceWebSocketHandler.class);

    private final PresenceService presenceService;
    private final SessionSecurityService sessionSecurityService;
    private final ObjectMapper objectMapper;
    private final com.zynpath.backend.security.audit.SecurityAuditLogger auditLogger;

    private static final int MAX_WS_MESSAGES_PER_SECOND = 20;
    private final Map<String, java.util.concurrent.atomic.AtomicInteger> socketMessageCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> socketWindowStarts = new ConcurrentHashMap<>();

    // Track active sessions: webSocketSessionId -> playerId
    private final Map<String, String> sessionPlayerMap = new ConcurrentHashMap<>();
    // Track reverse: playerId -> webSocketSession
    private final Map<String, WebSocketSession> playerActiveSocketMap = new ConcurrentHashMap<>();

    public PresenceWebSocketHandler(
            PresenceService presenceService,
            SessionSecurityService sessionSecurityService,
            ObjectMapper objectMapper,
            com.zynpath.backend.security.audit.SecurityAuditLogger auditLogger
    ) {
        this.presenceService = presenceService;
        this.sessionSecurityService = sessionSecurityService;
        this.objectMapper = objectMapper;
        this.auditLogger = auditLogger;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection initiated: id={}", session.getId());

        // Extract token from query param ?token=... if provided
        String token = extractTokenFromUri(session.getUri());
        if (token != null && !token.isBlank()) {
            authenticateSocket(session, token);
        } else {
            // Awaiting AUTH message
            sendJson(session, Map.of(
                    "type", "AUTH_REQUIRED",
                    "message", "Send AUTH message with valid session token"
            ));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        long now = System.currentTimeMillis();
        long windowStart = socketWindowStarts.computeIfAbsent(session.getId(), k -> now);
        if (now - windowStart > 1000L) {
            socketWindowStarts.put(session.getId(), now);
            socketMessageCounts.put(session.getId(), new java.util.concurrent.atomic.AtomicInteger(1));
        } else {
            java.util.concurrent.atomic.AtomicInteger count = socketMessageCounts.computeIfAbsent(session.getId(), k -> new java.util.concurrent.atomic.AtomicInteger(0));
            if (count.incrementAndGet() > MAX_WS_MESSAGES_PER_SECOND) {
                sendJson(session, Map.of("type", "RATE_LIMITED", "message", "Too many presence messages sent"));
                auditLogger.recordEvent(
                        com.zynpath.backend.security.audit.SecurityEventType.RATE_LIMIT_EXCEEDED,
                        sessionPlayerMap.get(session.getId()),
                        session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
                        "websocket:presence",
                        "THROTTLED",
                        Map.of("messageRateLimit", MAX_WS_MESSAGES_PER_SECOND)
                );
                return;
            }
        }

        try {
            JsonNode root = objectMapper.readTree(message.getPayload());
            String type = root.has("type") ? root.get("type").asText() : "";

            switch (type) {
                case "AUTH" -> {
                    String token = root.has("token") ? root.get("token").asText() : "";
                    authenticateSocket(session, token);
                }
                case "HEARTBEAT" -> {
                    String playerId = sessionPlayerMap.get(session.getId());
                    if (playerId != null) {
                        String stateStr = root.has("state") ? root.get("state").asText() : "ONLINE";
                        PlayerPresenceState state = "AWAY".equalsIgnoreCase(stateStr)
                                ? PlayerPresenceState.AWAY
                                : PlayerPresenceState.ONLINE;
                        presenceService.recordHeartbeat(playerId, state);
                        sendJson(session, Map.of("type", "HEARTBEAT_ACK", "state", state.name()));
                    } else {
                        sendJson(session, Map.of("type", "ERROR", "message", "Unauthenticated session"));
                    }
                }
                default -> {
                    log.debug("Unknown websocket message type: {}", type);
                }
            }
        } catch (Exception e) {
            log.warn("Error processing websocket message: {}", e.getMessage());
            sendJson(session, Map.of("type", "ERROR", "message", "Invalid message payload"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        socketMessageCounts.remove(session.getId());
        socketWindowStarts.remove(session.getId());
        String playerId = sessionPlayerMap.remove(session.getId());
        if (playerId != null) {
            playerActiveSocketMap.remove(playerId);
            presenceService.markOffline(playerId);
            log.info("WebSocket closed for playerId={}: status={}", playerId, status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
        String playerId = sessionPlayerMap.remove(session.getId());
        if (playerId != null) {
            playerActiveSocketMap.remove(playerId);
            presenceService.markOffline(playerId);
        }
    }

    private void authenticateSocket(WebSocketSession session, String token) {
        try {
            PlayerSession playerSession = sessionSecurityService.validateSession("Bearer " + token);
            String playerId = playerSession.playerId();

            // Close existing duplicate connection if present
            WebSocketSession existing = playerActiveSocketMap.put(playerId, session);
            if (existing != null && existing.isOpen() && !existing.getId().equals(session.getId())) {
                try {
                    existing.close(CloseStatus.POLICY_VIOLATION.withReason("Superseded by new session"));
                } catch (IOException ignored) {}
            }

            sessionPlayerMap.put(session.getId(), playerId);
            presenceService.recordHeartbeat(playerId, PlayerPresenceState.ONLINE);

            sendJson(session, Map.of(
                    "type", "AUTH_SUCCESS",
                    "playerId", playerId,
                    "presence", PlayerPresenceState.ONLINE.name()
            ));
            log.info("WebSocket authenticated successfully: playerId={}, sessionId={}", playerId, session.getId());
        } catch (Exception e) {
            log.warn("WebSocket authentication failed: {}", e.getMessage());
            try {
                sendJson(session, Map.of("type", "AUTH_FAILED", "message", "Invalid or expired session"));
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Authentication failed"));
            } catch (IOException ignored) {}
        }
    }

    private String extractTokenFromUri(URI uri) {
        if (uri == null || uri.getQuery() == null) return null;
        for (String param : uri.getQuery().split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "token".equalsIgnoreCase(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }

    private void sendJson(WebSocketSession session, Object payload) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            } catch (IOException e) {
                log.warn("Failed to send websocket message: {}", e.getMessage());
            }
        }
    }
}
