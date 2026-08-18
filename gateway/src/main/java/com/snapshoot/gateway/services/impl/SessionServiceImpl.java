package com.snapshoot.gateway.services.impl;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.common.exception.BadRequestException;
import com.snapshoot.gateway.common.exception.NotFoundException;
import com.snapshoot.gateway.config.GameConfig;
import com.snapshoot.gateway.domain.cache.Session;
import com.snapshoot.gateway.repositories.cache.SessionRepository;
import com.snapshoot.gateway.services.SessionService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final GameConfig gameConfig;

    @Override
    public Set<Session> retrieveSessions() {
        return sessionRepository.getAllSessions();
    }

    @Override
    public String createGameSession(Integer maxPlayers, Integer durationMinutes) {
        if (maxPlayers > gameConfig.maxPlayers()) {
            throw new BadRequestException(
                "maxPlayers must not exceed " + gameConfig.maxPlayers()
            );
        }
        if (durationMinutes > gameConfig.maxGameSessionDurationMinutes()) {
            throw new BadRequestException(
                "durationMinutes must not exceed " + gameConfig.maxGameSessionDurationMinutes()
            );
        }
        String sessionId = UUID.randomUUID().toString();
        sessionRepository.save(new Session(
            sessionId,
            maxPlayers,
            durationMinutes,
            Instant.now(),
            ConcurrentHashMap.newKeySet()
        ));
        return sessionId;
    }

    @Override
    public void addPlayerToSession(String sessionId, String playerId) {
        if (!sessionRepository.exists(sessionId)) {
            throw new NotFoundException("Unknown sessionId: " + sessionId);
        }
        Set<String> players = sessionRepository.getPlayers(sessionId);
        players.add(playerId);
    }
}
