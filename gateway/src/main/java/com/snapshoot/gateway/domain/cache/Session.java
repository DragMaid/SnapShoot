package com.snapshoot.gateway.domain.cache;

import java.time.Instant;
import java.util.Set;

public record Session(
    String id,
    Integer maxPlayers,
    Integer durationMinutes,
    Instant createdAt,
    Set<String> playerIds
) {
}
