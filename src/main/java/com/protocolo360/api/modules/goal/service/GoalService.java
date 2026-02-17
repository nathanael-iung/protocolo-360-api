package com.protocolo360.api.modules.goal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.protocolo360.api.modules.goal.dto.GoalData;
import com.protocolo360.api.modules.goal.dto.GoalsResponse;
import com.protocolo360.api.modules.goal.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalService {

  private final GoalRepository goalRepository;

  public GoalsResponse listGoals() {
        List<GoalData> goalDtos = goalRepository.findAll().stream()
            .map(goal -> new GoalData(goal.getId(), goal.getFullName(), goal.getDescription()))
            .toList();
        return new GoalsResponse(goalDtos);
    }
}
