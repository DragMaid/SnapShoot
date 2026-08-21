package com.snapshoot.gateway.services;

import com.snapshoot.gateway.domain.enums.WorkerType;

public interface WorkerService {
    String registerWorker(WorkerType workerType, String password);

    void deleteWorker(String workerId);

    boolean workerExists(String workerId);

    WorkerType getWorkerType(String workerId);
}
