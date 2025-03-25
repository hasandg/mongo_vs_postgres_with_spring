package com.hasandag.performance.client.controller;

import com.hasandag.performance.client.model.TestResult;
import com.hasandag.performance.client.service.PerformanceTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/client/api/performance")
public class PerformanceTestController {
    private final PerformanceTestService performanceTestService;

    public PerformanceTestController(PerformanceTestService performanceTestService) {
        this.performanceTestService = performanceTestService;
    }

    @PostMapping("/run/{operation}/{recordCount}")
    public ResponseEntity<TestResult> runTest(
            @PathVariable String operation,
            @PathVariable int recordCount) throws ExecutionException, InterruptedException {
        CompletableFuture<TestResult> future = performanceTestService.runTest(operation, recordCount);
        TestResult result = future.get();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        return ResponseEntity.ok(performanceTestService.getTestResults());
    }
} 