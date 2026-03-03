package com.protocolo360.api.modules.biometrics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.biometrics.dto.CalculateTdeeRequest;
import com.protocolo360.api.modules.biometrics.dto.TdeeResultResponse;
import com.protocolo360.api.modules.biometrics.service.BiometricsService;
import com.protocolo360.api.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/biometrics")
@RequiredArgsConstructor
public class BiometricsController {

  private final BiometricsService biometricsService;

  @PostMapping("/calculate")
  public ResponseEntity<ApiResponse<TdeeResultResponse>> calculateTdee(@Valid @RequestBody CalculateTdeeRequest request) {

    TdeeResultResponse tdeeResult = biometricsService.calculateTargets(request);

    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.success(tdeeResult, "Metabolic profile calculated successfully", 200));
  }

}
