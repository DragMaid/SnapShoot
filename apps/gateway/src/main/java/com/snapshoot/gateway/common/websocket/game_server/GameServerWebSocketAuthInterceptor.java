package com.snapshoot.gateway.common.websocket.game_server;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.common.security.TokenType;

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
 * WebSocket authentication by the game server's JWT before establishing the connection
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameServerWebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        String token = extractToken(request);

        // Check if token is not expired
        if (token == null || !jwtService.isTokenValid(token, TokenType.GAME_SERVER)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // TODO: Check if the game server exists (Binh An)

        String gameServerId = jwtService.extractGameServerId(token);

        attributes.put("gameServerId", gameServerId);

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
            log.warn("Game server WebSocket handshake failed", exception);
        } else {
            log.info("Game server WebSocket handshake successful");
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
