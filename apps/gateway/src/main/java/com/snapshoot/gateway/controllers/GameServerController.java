package com.snapshoot.gateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshoot.gateway.dto.game_server.GameServerRegisterRequest;
import com.snapshoot.gateway.dto.game_server.GameServerResponse;
import com.snapshoot.gateway.services.GameServerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/register-game-server")
@RequiredArgsConstructor
public class GameServerController {

    private final GameServerService gameServerService;

    @PostMapping
    public ResponseEntity<GameServerResponse> registerGameServer(@Valid @RequestBody GameServerRegisterRequest request) {
        GameServerResponse gameServerResponse = gameServerService.registerGameServer(request.password());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(gameServerResponse);
    }

}
