package com.snapshoot.gateway.services;

public interface SessionService {
    String createGameSession(Integer maxPlayers, Integer durationMinutes);

    void addPlayerToSession(String sessionId, String playerId);
}
