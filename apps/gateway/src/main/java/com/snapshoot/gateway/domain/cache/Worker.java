package com.snapshoot.gateway.domain.cache;

import java.time.Instant;

import com.snapshoot.gateway.domain.enums.WorkerType;

public record Worker(
    String id,
    WorkerType workerType,
    Instant connectedAt
) {
}
