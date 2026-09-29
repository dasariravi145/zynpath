package com.zynpath.backend.social.websocket;

import com.zynpath.backend.multiplayer.websocket.MultiplayerWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

/**
 * Spring WebSocket configuration registering presence and multiplayer handlers with environment-aware origin controls.
 *
 * Implements Prompt 19 Section 31, Prompt 20 Section 27, and Prompt 44 Section 41:
 * - /ws/presence: Online presence heartbeats and lifecycle.
 * - /ws/multiplayer: Real-time match signaling, countdown, progress, and claims.
 * - Explicit allowed origins configured via environment properties in production.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PresenceWebSocketHandler presenceWebSocketHandler;
    private final MultiplayerWebSocketHandler multiplayerWebSocketHandler;

    @Value("${zynpath.websocket.allowed-origins:*}")
    private String allowedOrigins;

    public WebSocketConfig(
            PresenceWebSocketHandler presenceWebSocketHandler,
            MultiplayerWebSocketHandler multiplayerWebSocketHandler
    ) {
        this.presenceWebSocketHandler = presenceWebSocketHandler;
        this.multiplayerWebSocketHandler = multiplayerWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (origins.length == 0) {
            origins = new String[]{"*"};
        }

        registry.addHandler(presenceWebSocketHandler, "/ws/presence")
                .setAllowedOrigins(origins);
        registry.addHandler(multiplayerWebSocketHandler, "/ws/multiplayer")
                .setAllowedOrigins(origins);
    }
}
