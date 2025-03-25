package com.hasandag.performance.automated.controller;

import com.hasandag.performance.automated.model.AutomatedTestResult;
import com.hasandag.performance.automated.service.AutomatedTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class AutomatedTestController {
    private final AutomatedTestService automatedTestService;

    public AutomatedTestController(AutomatedTestService automatedTestService) {
        this.automatedTestService = automatedTestService;
    }

    @PostMapping("/run-test")
    public ResponseEntity<String> runTest() {
        log.info("Manual test execution triggered");
        automatedTestService.runNextTest();
        return ResponseEntity.ok("Test execution triggered");
    }

    @GetMapping("/results")
    public ResponseEntity<List<AutomatedTestResult>> getResults() {
        return ResponseEntity.ok(automatedTestService.getTestResults());
    }

    @GetMapping("/results/phase/{phase}")
    public ResponseEntity<List<AutomatedTestResult>> getResultsByPhase(@PathVariable String phase) {
        return ResponseEntity.ok(automatedTestService.getTestResultsByPhase(phase));
    }

    @GetMapping("/results/operation/{operation}")
    public ResponseEntity<List<AutomatedTestResult>> getResultsByOperation(@PathVariable String operation) {
        return ResponseEntity.ok(automatedTestService.getTestResultsByOperation(operation));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        List<AutomatedTestResult> results = automatedTestService.getTestResults();
        
        long mongoDbTotalTime = results.stream().mapToLong(AutomatedTestResult::getMongoDbTime).sum();
        long postgresTotalTime = results.stream().mapToLong(AutomatedTestResult::getPostgresTime).sum();
        double mongoDbAvgTime = results.stream().mapToLong(AutomatedTestResult::getMongoDbTime).average().orElse(0);
        double postgresAvgTime = results.stream().mapToLong(AutomatedTestResult::getPostgresTime).average().orElse(0);
        
        Map<String, Object> summary = Map.of(
            "totalTests", results.size(),
            "mongoDbTotalTime", mongoDbTotalTime,
            "postgresTotalTime", postgresTotalTime,
            "mongoDbAverageTime", mongoDbAvgTime,
            "postgresAverageTime", postgresAvgTime,
            "fasterDatabase", mongoDbTotalTime < postgresTotalTime ? "MongoDB" : "PostgreSQL",
            "percentageDifference", calculatePercentageDifference(mongoDbTotalTime, postgresTotalTime)
        );
        
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/export-metrics")
    public ResponseEntity<String> exportAllMetrics() {
        log.info("Exporting all metrics to files...");
        automatedTestService.exportAllMetrics();
        return ResponseEntity.ok("Metrics exported successfully to the 'metrics-export' directory");
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Application is running");
    }
    
    private double calculatePercentageDifference(long time1, long time2) {
        if (time1 == 0 || time2 == 0) return 0;
        return Math.abs((double)(time1 - time2) / Math.max(time1, time2) * 100);
    }
} 