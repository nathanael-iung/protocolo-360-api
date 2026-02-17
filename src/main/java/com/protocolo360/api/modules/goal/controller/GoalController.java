package com.protocolo360.api.modules.goal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protocolo360.api.modules.goal.dto.GoalsResponse;
import com.protocolo360.api.modules.goal.service.GoalService;
import com.protocolo360.api.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/goal")
@RequiredArgsConstructor
public class GoalController {

  private final GoalService goalService;

  @GetMapping("/goals")
  public ResponseEntity<ApiResponse<GoalsResponse>> listGoals() {

    GoalsResponse goals = goalService.listGoals();

    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.success(goals, "Goals retrieved successfully", 200));
  }

}
