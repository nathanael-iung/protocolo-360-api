package com.protocolo360.api.modules.biometrics.dto;

import com.protocolo360.api.modules.biometrics.enums.ActivityLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CalculateTdeeRequest(
    @NotNull double weight,
    @NotNull double height, 
    @NotNull int age,
    @NotBlank String gender,
    @NotNull ActivityLevel activityLevel) {}
