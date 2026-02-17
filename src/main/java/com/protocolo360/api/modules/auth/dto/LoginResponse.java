package com.protocolo360.api.modules.auth.dto;

import java.util.Set;

public record LoginResponse(
    String email,
    Set<String> roles,
    long issuedAt,
    long expiresAt) {
}