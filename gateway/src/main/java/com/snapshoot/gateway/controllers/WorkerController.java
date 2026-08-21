package com.snapshoot.gateway.controllers;

import com.snapshoot.gateway.common.security.JwtService;
import com.snapshoot.gateway.dto.worker.WorkerRegisterRequest;
import com.snapshoot.gateway.dto.worker.WorkerResponse;
import com.snapshoot.gateway.services.WorkerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Register Vision/Routing workers and issue their auth tokens.
 */
 @Tag(
     name = "Workers",
     description = "Create workers for vision/routing/attribution services."
 )
@RestController
@RequestMapping("/register-worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<WorkerResponse> registerWorker(@Valid @RequestBody WorkerRegisterRequest request) {
        String token = workerService.registerWorker(request.workerType(), request.password());
        String workerId = jwtService.extractWorkerId(token);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new WorkerResponse(token, workerId));
    }
}
