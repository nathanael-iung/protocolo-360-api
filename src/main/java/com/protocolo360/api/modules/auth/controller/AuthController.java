package com.protocolo360.api.modules.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.auth.dto.LoginRequest;
import com.protocolo360.api.modules.auth.dto.LoginResponse;
import com.protocolo360.api.modules.auth.dto.RegisterRequest;
import com.protocolo360.api.modules.auth.model.User;
import com.protocolo360.api.modules.auth.service.AuthService;
import com.protocolo360.api.shared.dto.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/login")
public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody LoginRequest request, 
        HttpServletResponse response) {
    
    LoginResponse loginData = authService.authenticate(request);
    
    // Create the Cookie
    ResponseCookie cookie = ResponseCookie.from("protocolo360_auth", loginData.token())
            .httpOnly(true)       // Prevents JS access (No XSS)
            .secure(false)       // Set to 'true' in production (requires HTTPS)
            .path("/")           // Available for all routes
            .maxAge(24 * 60 * 60) // 24 hours
            .sameSite("Strict")  // Prevents CSRF
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok(ApiResponse.success(null, "Login successful", 200));
}
}