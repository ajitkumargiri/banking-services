package com.ajit.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class HandleFailureController {
    @GetMapping("/serviceNotAvailable")
    public Mono<ResponseEntity<Error>> serviceNotAvailable() {
        Error error = new Error(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        new Date(),
                        "Service Unavailable", "Service not available , Please try after sometime.");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error));
    }
}
