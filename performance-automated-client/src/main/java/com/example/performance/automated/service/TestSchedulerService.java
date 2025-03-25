package com.example.performance.automated.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestSchedulerService {
    private final AutomatedTestService automatedTestService;
    private int totalTestsRun = 0;
    private static final int TOTAL_TESTS = 20; // 4 operations x 5 sizes
    private boolean isInitialized = false;

    public TestSchedulerService(AutomatedTestService automatedTestService) {
        this.automatedTestService = automatedTestService;
    }

    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void runScheduledTest() {
        if (!isInitialized) {
            log.info("Initializing automated performance tests...");
            isInitialized = true;
        }
        
        if (totalTestsRun >= TOTAL_TESTS) {
            log.info("┌─────────────────────────────────────────────────────┐");
            log.info("│ All tests completed! Total tests run: {}/{}", totalTestsRun, TOTAL_TESTS);
            log.info("└─────────────────────────────────────────────────────┘");
            return;
        }
        
        log.info("┌─────────────────────────────────────────────────────┐");
        log.info("│ Running test {}/{}", totalTestsRun + 1, TOTAL_TESTS);
        log.info("│ Progress: {}%", Math.round((totalTestsRun * 100.0) / TOTAL_TESTS));
        log.info("└─────────────────────────────────────────────────────┘");
        
        automatedTestService.runNextTest();
        totalTestsRun++;
    }
} 