package com.snapshoot.gateway.controllers;

import com.snapshoot.gateway.dto.session.SessionCreateRequest;
import com.snapshoot.gateway.dto.session.SessionResponse;
import com.snapshoot.gateway.services.PlayerService;
import com.snapshoot.gateway.services.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Manage different game sessions
 */
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request
    ) {
        String sessionId = sessionService.createGameSession(
            request.maxPlayers(),
            request.durationMinutes()
        );

        String token = playerService.createPlayer();
        sessionService.addPlayerToSession(sessionId, token);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new SessionResponse(token, sessionId));
    }

    @PostMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> joinSession(
        @PathVariable String sessionId
    ) {
        String token = playerService.createPlayer();
        sessionService.addPlayerToSession(sessionId, token);
        return ResponseEntity.ok(new SessionResponse(token, sessionId));
    }
}
