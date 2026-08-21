package com.snapshoot.gateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Checking the health of the Gateway Service
 */
 @Tag(
     name = "Health check",
     description = "Checking if the Spring Boot Gateway is alive."
 )
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String root() {
        return "Gateway service is healthy!";
    }
}
