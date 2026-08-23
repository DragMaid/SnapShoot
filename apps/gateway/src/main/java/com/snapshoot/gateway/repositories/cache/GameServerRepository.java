package com.snapshoot.gateway.repositories.cache;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snapshoot.gateway.domain.cache.GameServer;

/**
 * An in-memory datastore for registered game servers.
 * Each game server is identified by its id.
 */
@Repository
public class GameServerRepository {

    private final Cache<String, GameServer> gameServerCache = Caffeine.newBuilder().build();

    public void save(GameServer gameServer) {
        gameServerCache.put(gameServer.id(), gameServer);
    }

    public Optional<GameServer> get(String gameServerId) {
        return Optional.ofNullable(gameServerCache.getIfPresent(gameServerId));
    }

    public boolean exists(String gameServerId) {
        return gameServerCache.getIfPresent(gameServerId) != null;
    }

    public void delete(String gameServerId) {
        gameServerCache.invalidate(gameServerId);
    }

}
