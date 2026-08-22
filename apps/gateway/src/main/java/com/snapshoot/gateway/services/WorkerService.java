package com.snapshoot.gateway.services;

import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.dto.worker.WorkerResponse;

public interface WorkerService {
    WorkerResponse registerWorker(WorkerType workerType, String password);

    void deleteWorker(String workerId);

    boolean workerExists(String workerId);

    WorkerType getWorkerType(String workerId);
}
