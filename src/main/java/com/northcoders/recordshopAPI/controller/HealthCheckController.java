package com.northcoders.recordshopAPI.controller;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health-check")
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    public HealthCheckController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("{\"status\":\"OK\"}", HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<String> check() {
        String health = healthEndpoint.health().getStatus().getCode();
        return new ResponseEntity<>("{\"status\":\"" + health + "\"}", HttpStatus.OK);
    }

}
