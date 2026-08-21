package com.snapshoot.gateway.repositories.cache;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snapshoot.gateway.domain.cache.Worker;

/**
 * An in-memory datastore for registered Vision/Routing workers.
 */
@Repository
public class WorkerRepository {

    private final Cache<String, Worker> workerCache = Caffeine.newBuilder().build();

    public void save(Worker worker) {
        workerCache.put(worker.id(), worker);
    }

    public Optional<Worker> get(String workerId) {
        return Optional.ofNullable(workerCache.getIfPresent(workerId));
    }

    public boolean exists(String workerId) {
        return workerCache.getIfPresent(workerId) != null;
    }

    public void delete(String workerId) {
        workerCache.invalidate(workerId);
    }
}
