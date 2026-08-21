package com.snapshoot.gateway.controllers;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.domain.cache.Session;
import com.snapshoot.gateway.dto.session.SessionCreateRequest;
import com.snapshoot.gateway.dto.session.SessionResponse;
import com.snapshoot.gateway.services.PlayerService;
import com.snapshoot.gateway.services.SessionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Manage different game sessions
 */
 @Tag(
     name = "Sessions",
     description = "Operations for creating, listing, and joining game sessions."
 )
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final PlayerService playerService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<Set<Session>> getSessions() {
        Set<Session> sessions = sessionService.retrieveSessions();
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request
    ) {
        String sessionId = sessionService.createGameSession(
            request.maxPlayers(),
            request.durationMinutes()
        );
        String token = playerService.createPlayer(request.roomCreatorName(), sessionId);
        String playerId = jwtService.extractPlayerId(token);

        sessionService.addPlayerToSession(sessionId, playerId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new SessionResponse(token, sessionId));
    }

    @PostMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> joinSession(
        @PathVariable String sessionId,
        @RequestParam String username
    ) {
        String token = playerService.createPlayer(username, sessionId);
        String playerId = jwtService.extractPlayerId(token);

        sessionService.addPlayerToSession(sessionId, playerId);

        return ResponseEntity.ok(new SessionResponse(token, sessionId));
    }
}
