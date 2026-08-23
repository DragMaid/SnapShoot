package com.snapshoot.gateway.common.websocket.shared;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

/**
 * Handles different WebSocket sessions, each one identified by a UUID string.
 */
public abstract class AbstractWebSocketRegistry {

    private final Map<String, WebSocketSession> connections = new ConcurrentHashMap<>();

    public void add(String id, WebSocketSession session) {
        connections.put(id, session);
    }

    public void remove(String id) {
        connections.remove(id);
    }

    public Optional<WebSocketSession> get(String id) {
        return Optional.ofNullable(connections.get(id));
    }
}
