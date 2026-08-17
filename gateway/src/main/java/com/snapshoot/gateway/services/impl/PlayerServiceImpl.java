package com.snapshoot.gateway.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.common.exception.NotFoundException;
import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.domain.cache.Player;
import com.snapshoot.gateway.domain.cache.Session;
import com.snapshoot.gateway.repositories.cache.PlayerRepository;
import com.snapshoot.gateway.repositories.cache.SessionRepository;
import com.snapshoot.gateway.services.PlayerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final SessionRepository sessionRepository;
    private final JwtService jwtService;

    /**
     * Create a player for a game session, then return the Jwt for identification and verification.
     */
    @Override
    public String createPlayer(String username, String sessionId) {
        Session session = sessionRepository.getSession(sessionId)
            .orElseThrow(() -> new NotFoundException("Session not found"));

        String playerId = UUID.randomUUID().toString();
        playerRepository.save(new Player(playerId, username, session.durationMinutes()));
        return jwtService.generateGameJwtToken(playerId, sessionId, session.durationMinutes());
    }

    @Override
    public Player getPlayer(String playerId) {
        return playerRepository.getPlayer(playerId);
    }
}
