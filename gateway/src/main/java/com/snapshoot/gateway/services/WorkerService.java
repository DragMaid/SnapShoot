package com.snapshoot.gateway.services;

public interface WorkerService {
    String registerWorker(String password);

    void deleteWorker(String workerId);

    boolean workerExists(String workerId);
}
