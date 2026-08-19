package com.snapshoot.gateway.repositories.queue;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

/**
 * Tracks Vision/Routing workers that asked for a task ({@code READY}) while
 * none was available, so the next task to arrive can be matched to one of
 * them directly instead of waiting for another poll.
 */
@Repository
public class IdleWorkerRepository {

    private final Set<String> idleVisionWorkers = ConcurrentHashMap.newKeySet();
    private final Set<String> idleRoutingWorkers = ConcurrentHashMap.newKeySet();

    /**
     * Park a vision worker as idle, unless it's already parked.
     */
    public void markIdleVision(String workerId) {
        idleVisionWorkers.add(workerId);
    }

    /**
     * Park a routing worker as idle, unless it's already parked.
     */
    public void markIdleRouting(String workerId) {
        idleRoutingWorkers.add(workerId);
    }

    /**
     * Take an idle vision worker, if any.
     */
    public Optional<String> takeIdleVisionWorker() {
        return takeIdle(idleVisionWorkers);
    }

    /**
     * Take an idle routing worker, if any.
     */
    public Optional<String> takeIdleRoutingWorker() {
        return takeIdle(idleRoutingWorkers);
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

    /**
     * Pull one worker out of the idle set.
     */
     private Optional<String> takeIdle(Set<String> idleWorkers) {
         if (idleWorkers.isEmpty()) {
             return Optional.empty();
         }

         Iterator<String> iterator = idleWorkers.iterator();
         String workerId = iterator.next();
         iterator.remove();

         return Optional.of(workerId);
     }
}
