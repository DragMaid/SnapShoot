package com.snapshoot.gateway.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtConfig(
        @NotBlank String issuer,
        @NotBlank String secret
) {
}
