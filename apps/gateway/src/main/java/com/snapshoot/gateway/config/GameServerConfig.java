package com.snapshoot.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.game-server")
public record GameServerConfig(
    String gameServerPassword
) {
}
