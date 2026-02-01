package com.protocolo360.api.modules.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.health.dto.AppStatusResponse;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    @GetMapping
    public AppStatusResponse getStatus() {
        return new AppStatusResponse(
            true, 
            "Protocolo 360 API is healthy and connected to Neon!", 
            "development"
        );
    }
}
