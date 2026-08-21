package com.snapshoot.gateway.repositories.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.snapshoot.gateway.domain.cache.Player;

/**
 * An in-memory datastore for temporary players, backed by a self-expiring
 * cache so each player is evicted durationMinutes after creation, matching
 * the lifetime of the game session they belong to.
 */
@Repository
public class PlayerRepository {

    private static final Expiry<String, Player> PLAYER_EXPIRY = new Expiry<>() {
        @Override
        public long expireAfterCreate(String playerId, Player player, long currentTime) {
            return TimeUnit.MINUTES.toNanos(player.durationMinutes());
        }

        @Override
        public long expireAfterUpdate(String playerId, Player player, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String playerId, Player player, long currentTime, long currentDuration) {
            return currentDuration;
        }
    };

    private final Cache<String, Player> playerCache = Caffeine.newBuilder()
        .expireAfter(PLAYER_EXPIRY)
        .build();

    public void save(Player player) {
        playerCache.put(player.id(), player);
    }

    public Player getPlayer(String playerId) {
        return playerCache.getIfPresent(playerId);
    }

    public boolean exists(String playerId) {
        return playerCache.getIfPresent(playerId) != null;
    }
}
