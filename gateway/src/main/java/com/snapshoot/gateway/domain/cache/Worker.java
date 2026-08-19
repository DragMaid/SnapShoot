package com.snapshoot.gateway.domain.cache;

import java.time.Instant;

public record Worker(
    String id,
    Instant connectedAt
) {
}
