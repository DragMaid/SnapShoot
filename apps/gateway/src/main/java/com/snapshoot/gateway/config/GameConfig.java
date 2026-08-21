package com.snapshoot.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "app.game")
public record GameConfig(
    int maxPlayers,
    int minPlayers,
    @Min(5) long maxGameSessionDurationMinutes
) {
}
