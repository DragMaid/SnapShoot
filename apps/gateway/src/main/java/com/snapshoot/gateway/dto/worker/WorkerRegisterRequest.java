package com.snapshoot.gateway.dto.worker;

import com.snapshoot.gateway.domain.enums.WorkerType;

public record WorkerRegisterRequest(
    WorkerType workerType,
    String password
) {
}
