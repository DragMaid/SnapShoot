package com.snapshoot.gateway.repositories.queue;

import com.snapshoot.gateway.domain.enums.WorkerType;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Tracks FREE workers.
 */
@Repository
public class IdleWorkerRepository {

    private final Set<String> idleVisionWorkers = ConcurrentHashMap.newKeySet();
    private final Set<String> idleRoutingWorkers = ConcurrentHashMap.newKeySet();

    /**
     * Add the FREE worker to the idle workers waitlist.
     */
    public void addToIdleWorkers(String workerId, WorkerType workerType) {
        switch (workerType) {
            case VISION:
                idleVisionWorkers.add(workerId);
                break;
            case ROUTING:
                idleRoutingWorkers.add(workerId);
                break;
        }
    }

    /**
     * Take any worker based on the WorkerType (VISION/ROUTING).
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
     * Remove a specific idle worker by its workerId.
     */
    public void clearIdlWorker(String workerId, WorkerType workerType) {
        switch (workerType) {
           	case VISION:
          		idleVisionWorkers.remove(workerId);
          		break;
            case ROUTING:
                idleRoutingWorkers.remove(workerId);
                break;
        }
    }
}
