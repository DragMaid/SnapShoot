package com.snapshoot.gateway.common.security;

import com.snapshoot.gateway.config.JwtConfig;
import com.snapshoot.gateway.domain.enums.WebSocketPeerType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(
            jwtConfig.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Mints a signed JWT identifying players.
     */
    public String generatePhoneJwtToken(
        String playerId,
        String sessionId,
        long gameSessionDurationMinutes
    ) {
        Instant now = Instant.now();
        Instant expiry = now.plus(
            gameSessionDurationMinutes,
            ChronoUnit.MINUTES
        );
        return Jwts.builder()
            .setSubject(playerId)
            .claim("session_id", sessionId)
            .claim("type", WebSocketPeerType.PHONE.toString())
            .setIssuer(jwtConfig.issuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(secretKey)
            .compact();
    }

    /**
     * Mints a signed JWT identifying a Vision/Routing worker.
     */
    public String generateWorkerJwtToken(String workerId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(workerId)
            .claim("type", WebSocketPeerType.WORKER.toString())
            .setIssuer(jwtConfig.issuer())
            .setIssuedAt(Date.from(now))
            .signWith(secretKey)
            .compact();
    }

    /**
     * Mints a signed JWT identifying different game servers.
     */
    public String generateGameServerJwtToken(String gameServerId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(gameServerId)
            .claim("type", WebSocketPeerType.GAME_SERVER.toString())
            .setIssuer(jwtConfig.issuer())
            .setIssuedAt(Date.from(now))
            .signWith(secretKey)
            .compact();
    }

    public boolean isTokenValid(String token, WebSocketPeerType tokenType) {
        try {
            Claims claims = parseClaim(token);
            String type = claims.get("type", String.class);

            return tokenType.name().equals(type);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractPlayerId(String token) {
        return parseClaim(token).getSubject();
    }

    public String extractSessionId(String token) {
        return parseClaim(token).get("session_id", String.class);
    }

    public String extractWorkerId(String token) {
        return parseClaim(token).getSubject();
    }

    public String extractGameServerId(String token) {
        return parseClaim(token).getSubject();
    }

    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
