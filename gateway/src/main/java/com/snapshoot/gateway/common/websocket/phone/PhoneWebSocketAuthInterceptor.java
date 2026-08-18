package com.snapshoot.gateway.common.websocket.phone;

import com.snapshoot.gateway.common.exception.NotFoundException;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.common.security.TokenType;
import com.snapshoot.gateway.services.SessionService;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * WebSocket authentication by the player's JWT before establishing the connection
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneWebSocketAuthInterceptor implements HandshakeInterceptor {

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

        if (!sessionService.sessionExists(sessionId)) {
            throw new NotFoundException("Sesssion not found");
        }

        String playerId = jwtService.extractPlayerId(token);
        attributes.put("playerId", playerId);

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
            log.warn("WebSocket handshake failed", exception);
        } else {
            log.info("WebSocket handshake successful");
        }
    }

    // TODO: Should be an auth header rather than a query param! For now I am still unclear about how a phone browser works.
    private String extractToken(ServerHttpRequest request) {
        URI uri = request.getURI();

        return UriComponentsBuilder.fromUri(uri)
            .build()
            .getQueryParams()
            .getFirst("token");
    }
}
