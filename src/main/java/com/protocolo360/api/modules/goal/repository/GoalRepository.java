package com.protocolo360.api.modules.goal.repository;

import com.protocolo360.api.modules.goal.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {
    // String is used here because the @Id in your Goal entity is a String
}