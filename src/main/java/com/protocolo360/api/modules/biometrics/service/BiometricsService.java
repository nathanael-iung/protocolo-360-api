package com.protocolo360.api.modules.biometrics.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.protocolo360.api.modules.biometrics.dto.CalculateTdeeRequest;
import com.protocolo360.api.modules.biometrics.dto.TdeeResultResponse;
import com.protocolo360.api.modules.biometrics.dto.WeightTarget;
import com.protocolo360.api.modules.biometrics.enums.ActivityLevel;
import com.protocolo360.api.shared.exception.BusinessException;

@Service
public class BiometricsService {

  public double calculateTDEE(double weight, double height, int age, String gender, ActivityLevel activityLevel) {
    // 1. Calculate BMR (Basal Metabolic Rate)
    double bmr = switch (gender.toUpperCase()) {
      case "M" -> 66.47 + (13.75 * weight) + (5.003 * height) - (6.755 * age);
      case "F" -> 655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age);
      default -> throw new BusinessException("Invalid gender provided", HttpStatus.BAD_REQUEST);
    };

    // 2. Apply Activity Factor
    return bmr * activityLevel.getMultiplier();
  }

  public TdeeResultResponse calculateTargets(CalculateTdeeRequest request) {
    double bmrValue = switch (request.gender().toUpperCase()) {
        case "M" -> 66.47 + (13.75 * request.weight()) + (5.003 * request.height()) - (6.755 * request.age());
        case "F" -> 655.1 + (9.563 * request.weight()) + (1.850 * request.height()) - (4.676 * request.age());
        default -> throw new BusinessException("Invalid gender", HttpStatus.BAD_REQUEST);
    };

    int bmr = (int) Math.round(bmrValue);
    int maintenance = (int) Math.round(bmrValue * request.activityLevel().getMultiplier());

    WeightTarget fatLoss = new WeightTarget(
        maintenance - 275, // Conservative (-0.25kg)
        maintenance - 550 // Aggressive (-0.50kg)
    );

    WeightTarget hypertrophy = new WeightTarget(
        maintenance + 275, // Conservative (+0.25kg)
        maintenance + 550 // Aggressive (+0.50kg)
    );

    return new TdeeResultResponse(bmr, maintenance, fatLoss, hypertrophy);
  }

}
