package com.snapshoot.gateway.repositories.cache;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.snapshoot.gateway.domain.cache.Session;

/**
 * An in-memory datastore for game sessions, backed by a self-expiring cache
 * so each session is evicted durationMinutes after creation.
 */
@Repository
public class SessionRepository {

    private static final Expiry<String, Session> SESSION_EXPIRY = new Expiry<>() {
        @Override
        public long expireAfterCreate(String sessionId, Session session, long currentTime) {
            return TimeUnit.MINUTES.toNanos(session.durationMinutes());
        }

        @Override
        public long expireAfterUpdate(String sessionId, Session session, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String sessionId, Session session, long currentTime, long currentDuration) {
            return currentDuration;
        }
    };

    private final Cache<String, Session> sessionCache = Caffeine.newBuilder()
        .expireAfter(SESSION_EXPIRY)
        .build();

    public void save(Session session) {
        sessionCache.put(session.id(), session);
    }

    public Set<Session> getAllSessions() {
        return Set.copyOf(sessionCache.asMap().values());
    }

    public Optional<Session> getSession(String sessionId) {
        return Optional.ofNullable(
            sessionCache.getIfPresent(sessionId)
        );
    }

    public boolean exists(String sessionId) {
        return sessionCache.getIfPresent(sessionId) != null;
    }

    public Set<String> getPlayers(String sessionId) {
        Session session = sessionCache.getIfPresent(sessionId);
        return session != null ? session.playerIds() : null;
    }
}
