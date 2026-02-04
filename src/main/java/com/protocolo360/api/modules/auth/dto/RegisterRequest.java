package com.protocolo360.api.modules.auth.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record RegisterRequest(
    @NotBlank String fullName,
    @Email @NotBlank String email,
    @Size(min = 8) @NotBlank String password,
    LocalDate birthDate,
    String phone,
    String gender,
    List<String> goals
) {}
