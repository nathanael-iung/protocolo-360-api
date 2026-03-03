package com.protocolo360.api.modules.biometrics.dto;

public record TdeeResultResponse(
    int bmr,
    int maintenance,
    WeightTarget fatLoss,
    WeightTarget hypertrophy) {}
