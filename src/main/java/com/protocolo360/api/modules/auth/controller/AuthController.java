package com.protocolo360.api.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.auth.dto.RegisterRequest;
import com.protocolo360.api.modules.auth.model.User;
import com.protocolo360.api.modules.auth.service.AuthService;
import com.protocolo360.api.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.registerUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(savedUser, "User registered successfully", 201)
        );
    }
}