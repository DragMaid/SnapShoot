package com.snapshoot.gateway.services;

import com.snapshoot.gateway.dto.game_server.GameServerResponse;

public interface GameServerService {
    GameServerResponse registerGameServer(String password);
}
