package com.snapshoot.gateway.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.common.exception.UnauthorizedException;
import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.config.GameServerConfig;
import com.snapshoot.gateway.domain.cache.GameServer;
import com.snapshoot.gateway.dto.game_server.GameServerResponse;
import com.snapshoot.gateway.repositories.cache.GameServerRepository;
import com.snapshoot.gateway.services.GameServerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameServerServiceImpl implements GameServerService {

    private final GameServerRepository gameServerRepository;
    private final GameServerConfig gameServerConfig;
    private final JwtService jwtService;

    @Override
    public GameServerResponse registerGameServer(String password) {
        if (!password.equals(gameServerConfig.gameServerPassword())) {
            throw new UnauthorizedException("Wrong game server password");
        }

        String gameServerId = UUID.randomUUID().toString();
        gameServerRepository.save(new GameServer(gameServerId));
        String token = jwtService.generateGameServerJwtToken(gameServerId);

        return new GameServerResponse(gameServerId, token);
    }

}
