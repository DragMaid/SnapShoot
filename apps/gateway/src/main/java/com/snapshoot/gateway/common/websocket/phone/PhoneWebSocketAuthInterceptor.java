package com.snapshoot.gateway.common.websocket.phone;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.common.security.TokenType;
import com.snapshoot.gateway.services.SessionService;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket authentication by the player's JWT before establishing the connection
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneWebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final SessionService sessionService;

    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        String token = extractToken(request);

        // Check if token is not expired
        if (token == null || !jwtService.isTokenValid(token, TokenType.PHONE)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String sessionId = jwtService.extractSessionId(token);

        // Check if the game session exists
        if (!sessionService.sessionExists(sessionId)) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return false;
        }

        String playerId = jwtService.extractPlayerId(token);
        attributes.put("playerId", playerId);
        attributes.put("sessionId", sessionId);

        return true;
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        @Nullable Exception exception
    ) {
        if (exception != null) {
            log.warn("Phone WebSocket handshake failed", exception);
        } else {
            log.info("Phone WebSocket handshake successful");
        }
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }

        String header = authHeaders.getFirst();
        return header.startsWith(BEARER_PREFIX) ? header.substring(BEARER_PREFIX.length()) : null;
    }
}
