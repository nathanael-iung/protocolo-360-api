package com.protocolo360.api.modules.auth.dto;

public record TokenMetadata(String token, long issuedAt, long expiresAt) {}
