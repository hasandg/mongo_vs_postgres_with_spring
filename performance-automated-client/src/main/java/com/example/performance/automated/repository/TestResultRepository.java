package com.example.performance.automated.repository;

import com.example.performance.automated.model.AutomatedTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<AutomatedTestResult, Long> {
    List<AutomatedTestResult> findByTestPhaseOrderByTimestampAsc(String testPhase);
    List<AutomatedTestResult> findByOperationOrderByTimestampAsc(String operation);
} 