package com.snapshoot.gateway.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.snapshoot.gateway.common.exception.UnauthorizedException;
import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.config.WorkerConfig;
import com.snapshoot.gateway.domain.cache.Worker;
import com.snapshoot.gateway.repositories.cache.WorkerRepository;
import com.snapshoot.gateway.services.WorkerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final JwtService jwtService;
    private final WorkerConfig workerConfig;

    /**
     * Registers a new Vision/Routing worker, then returns the Jwt for
     * identification and verification.
     */
    @Override
    public String registerWorker(String password) {
        if (!password.equals(workerConfig.workerPassword())) {
            throw new UnauthorizedException("Wrong worker password");
        }

        String workerId = UUID.randomUUID().toString();

        workerRepository.save(new Worker(workerId, Instant.now()));
        return jwtService.generateWorkerJwtToken(workerId);
    }

    @Override
    public void deleteWorker(String workerId) {
        workerRepository.delete(workerId);
    }
}
