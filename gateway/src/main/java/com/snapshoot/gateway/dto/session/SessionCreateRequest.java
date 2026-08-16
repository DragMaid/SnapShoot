package com.snapshoot.gateway.dto.session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SessionCreateRequest(
    @NotNull(message = "maxPlayers is required")
    @Positive(message = "maxPlayers must be positive")
    Integer maxPlayers,

    @NotNull(message = "durationMinutes is required")
    @Positive(message = "durationMinutes must be positive")
    Integer durationMinutes
) {}
