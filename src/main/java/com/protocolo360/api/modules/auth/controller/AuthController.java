package com.protocolo360.api.modules.auth.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.auth.dto.AuthResult;
import com.protocolo360.api.modules.auth.dto.LoginRequest;
import com.protocolo360.api.modules.auth.dto.LoginResponse;
import com.protocolo360.api.modules.auth.dto.RegisterRequest;
import com.protocolo360.api.modules.auth.dto.TokenMetadata;
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResult authResult = authService.authenticate(request);

        addAuthCookie(response, authResult.token(), 24 * 60 * 60);

        return ResponseEntity.ok(ApiResponse.success(authResult.response(), "User Logged In", 200));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        addAuthCookie(response, "", 0);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully", 200));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(HttpServletRequest request,
            HttpServletResponse response) {
        String token = jwtService.recoverToken(request); // Reuse your recovery logic

        if (token == null) {
            throw new BusinessException("No session cookie found", HttpStatus.UNAUTHORIZED);
        }

        TokenMetadata newTokenMetadata = jwtService.refreshToken(token);

        if (newTokenMetadata == null) {
            throw new BusinessException("Session expired, please login again", HttpStatus.UNAUTHORIZED);
        }

        addAuthCookie(response, newTokenMetadata.token(), 24 * 60 * 60); // 1 day expiry

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        LoginResponse loginResponse = new LoginResponse(
                email,
                roles,
                newTokenMetadata.issuedAt(),
                newTokenMetadata.expiresAt());

        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Token refreshed", 200));
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