package com.snapshoot.gateway.common.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.secret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Mints a signed JWT identifying {@code playerId}, valid for
     * {@link JwtConfig#maxGameSessionDurationMinutes()}.
     */
    public String generateGameJwtToken(String playerId, String sessionId, long gameSessionDurationMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plus(gameSessionDurationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
            .setSubject(playerId)
            .claim("session_id", sessionId)
            .setIssuer(jwtConfig.issuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(secretKey)
            .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            parseClaim(token);
            return true;
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

    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
