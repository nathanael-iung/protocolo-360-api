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
import com.protocolo360.api.modules.auth.service.JwtService;
import com.protocolo360.api.shared.dto.ApiResponse;
import com.protocolo360.api.shared.exception.BusinessException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(savedUser, "User registered successfully", 201));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginData = authService.authenticate(request);

        addAuthCookie(response, loginData.token(), 24 * 60 * 60); // 1 day expiry
        
        return ResponseEntity.ok(ApiResponse.success(null, "Login successful", 200));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        addAuthCookie(response, "", 0);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully", 200));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtService.recoverToken(request); // Reuse your recovery logic

        if (token == null) {
            throw new BusinessException("No session cookie found", HttpStatus.UNAUTHORIZED);
        }

        String newToken = jwtService.refreshToken(token);

        if (newToken == null) {
            throw new BusinessException("Session expired, please login again", HttpStatus.UNAUTHORIZED);
        }
        addAuthCookie(response, newToken, 24 * 60 * 60); // 1 day expiry
        return ResponseEntity.ok(ApiResponse.success(null, "Token refreshed", 200));
    }

    private void addAuthCookie(HttpServletResponse response, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from("protocolo360_auth", token)
                .httpOnly(true)
                .secure(false) // Remember to use 'true' in production
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}