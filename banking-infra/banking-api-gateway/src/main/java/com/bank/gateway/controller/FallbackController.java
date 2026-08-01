package com.bank.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "SERVICE_UNAVAILABLE",
                "message", "Auth server temporarily unavailable",
                "retryAfter", 30
            )));
    }

    @RequestMapping("/fallback/resource")
    public Mono<ResponseEntity<Map<String, Object>>> resourceFallback() {
        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "SERVICE_UNAVAILABLE",
                "message", "Resource server temporarily unavailable",
                "retryAfter", 10
            )));
    }
}