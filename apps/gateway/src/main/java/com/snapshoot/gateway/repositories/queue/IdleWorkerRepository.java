package com.snapshoot.gateway.repositories.queue;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.snapshoot.gateway.domain.enums.WorkerType;

/**
 * Tracks FREE workers.
 */
@Repository
public class IdleWorkerRepository {

    private final Set<String> idleVisionWorkers = ConcurrentHashMap.newKeySet();
    private final Set<String> idleRoutingWorkers = ConcurrentHashMap.newKeySet();

    /**
     * Park a vision worker as idle, unless it's already parked.
     */
    public void addToIdleVisionWorkers(String workerId) {
        idleVisionWorkers.add(workerId);
    }

    /**
     * Park a routing worker as idle, unless it's already parked.
     */
    public void addToIdleRoutingWorkers(String workerId) {
        idleRoutingWorkers.add(workerId);
    }

    /**
     * Take any worker based on the WorkerType (VISION/ROUTING)
     */
    public Optional<String> takeIdleWorker(WorkerType workerType) {
        Set<String> idleWorkers = switch (workerType) {
            case VISION -> idleVisionWorkers;
            case ROUTING -> idleRoutingWorkers;
        };

        if (idleWorkers.isEmpty()) {
            return Optional.empty();
        }

        Iterator<String> iterator = idleWorkers.iterator();
        String workerId = iterator.next();
        iterator.remove();

        return Optional.of(workerId);
    }

    /**
     * Remove a vision worker from the idle set, e.g. on disconnect, so a
     * dead connection is never matched to a future task.
     */
    public void clearIdleVisionWorker(String workerId) {
        idleVisionWorkers.remove(workerId);
    }

    /**
     * Remove a routing worker from the idle set, e.g. on disconnect, so a
     * dead connection is never matched to a future task.
     */
    public void clearIdleRoutingWorker(String workerId) {
        idleRoutingWorkers.remove(workerId);
    }
}
