package com.snapshoot.gateway.services;

import com.snapshoot.gateway.domain.cache.Player;

public interface PlayerService {
    String createPlayer(String username, String sessionId);

    Player getPlayer(String playerId);
}
