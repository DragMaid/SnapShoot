package com.snapshoot.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Checking the health of the Gateway Service
 */
@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "Gateway service is healthy!";
    }
}
