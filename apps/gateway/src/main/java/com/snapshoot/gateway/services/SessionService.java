package com.snapshoot.gateway.services;

import java.util.Set;

import com.snapshoot.gateway.domain.cache.Session;

public interface SessionService {
    Set<Session> retrieveSessions();

    String createGameSession(Integer maxPlayers, Integer durationMinutes);

    void addPlayerToSession(String sessionId, String playerId);

    boolean sessionExists(String sessionId);
}
