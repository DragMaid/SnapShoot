package com.snapshoot.gateway.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.services.PlayerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final JwtService jwtService;

    @Override
    public String createPlayer() {
        String playerId = UUID.randomUUID().toString();
        String token = jwtService.generateJwtToken(playerId);
        return token;
    }
}
