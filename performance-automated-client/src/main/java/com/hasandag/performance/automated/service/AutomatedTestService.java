package com.hasandag.performance.automated.service;

import com.hasandag.performance.automated.model.AutomatedTestResult;
import com.hasandag.performance.automated.repository.TestResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AutomatedTestService {
    private final WebClient webClient;
    private final TestResultRepository testResultRepository;
    private final MetricsExportService metricsExportService;
    private final ExecutorService executorService;
    private final List<Integer> testSizes = List.of(100, 500, 1000, 5000, 10000);
    private int currentTestSizeIndex = 0;
    private int currentOperationIndex = 0;
    private final List<String> operations = List.of("generate-data", "read", "update", "delete");
    private boolean isTestRunning = false;

    public AutomatedTestService(WebClient webClient, TestResultRepository testResultRepository, 
                               MetricsExportService metricsExportService) {
        this.webClient = webClient;
        this.testResultRepository = testResultRepository;
        this.metricsExportService = metricsExportService;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void runNextTest() {
        if (isTestRunning) {
            log.info("Test is already running, skipping...");
            return;
        }

        if (currentTestSizeIndex >= testSizes.size()) {
            log.info("All test sizes completed");
            return;
        }

        int currentTestSize = testSizes.get(currentTestSizeIndex);
        String currentOperation = operations.get(currentOperationIndex);

        isTestRunning = true;
        log.info("Starting test: {} with {} records", currentOperation, currentTestSize);

        CompletableFuture<AutomatedTestResult> future = CompletableFuture.supplyAsync(() -> {
            AutomatedTestResult result = new AutomatedTestResult();
            result.setOperation(currentOperation);
            result.setRecordCount(currentTestSize);
            result.setTestPhase("Phase " + (currentTestSizeIndex + 1));
            result.setExecutionOrder(currentOperationIndex + 1);

            try {
                Mono<Map<String, Long>> responseMono;
                String uri = "/api/performance/" + currentOperation + "/" + currentTestSize;
                
                switch (currentOperation) {
                    case "generate-data":
                        responseMono = webClient.post()
                                .uri(uri)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {});
                        break;
                    case "read":
                        responseMono = webClient.get()
                                .uri(uri)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {});
                        break;
                    case "update":
                        responseMono = webClient.put()
                                .uri(uri)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {});
                        break;
                    case "delete":
                        responseMono = webClient.delete()
                                .uri(uri)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {});
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operation: " + currentOperation);
                }

                long startTime = System.currentTimeMillis();
                Map<String, Long> response = responseMono.block();
                long endTime = System.currentTimeMillis();

                if (response != null) {
                    result.setMongoDbTime(response.get("mongodb"));
                    result.setPostgresTime(response.get("postgres"));
                    result.setStatus("SUCCESS");
                    result.updateMetrics(endTime - startTime);
                    
                    // Log detailed results to console
                    log.info("╔══════════════════════════════════════════════════════════");
                    log.info("║ TEST RESULTS: {} - {} records", currentOperation.toUpperCase(), currentTestSize);
                    log.info("╠══════════════════════════════════════════════════════════");
                    log.info("║ MongoDB Time: {}ms", result.getMongoDbTime());
                    log.info("║ PostgreSQL Time: {}ms", result.getPostgresTime());
                    log.info("║ Difference: {}ms", Math.abs(result.getMongoDbTime() - result.getPostgresTime()));
                    log.info("║ Faster DB: {}", result.getMongoDbTime() < result.getPostgresTime() ? "MongoDB" : "PostgreSQL");
                    log.info("║ Total Execution Time: {}ms", endTime - startTime);
                    log.info("╚══════════════════════════════════════════════════════════");
                }
            } catch (Exception e) {
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                result.incrementRetryCount();
                log.error("Error running {} test: {}", currentOperation, e.getMessage());
                
                // Log detailed error to console
                log.error("╔══════════════════════════════════════════════════════════");
                log.error("║ TEST FAILED: {} - {} records", currentOperation.toUpperCase(), currentTestSize);
                log.error("╠══════════════════════════════════════════════════════════");
                log.error("║ Error: {}", e.getMessage());
                log.error("║ Retry Count: {}", result.getRetryCount());
                log.error("╚══════════════════════════════════════════════════════════");
            }

            return result;
        }, executorService);

        future.thenAccept(result -> {
            testResultRepository.save(result);
            
            // Export the test result to a file
            metricsExportService.exportTestResult(result);
            
            log.info("Test completed: {} with {} records", currentOperation, currentTestSize);
            
            // Move to next operation or test size
            currentOperationIndex++;
            if (currentOperationIndex >= operations.size()) {
                currentOperationIndex = 0;
                currentTestSizeIndex++;
                
                // Export summary report after completing all operations for a test size
                exportLatestSummaryReport();
            }
            
            isTestRunning = false;
        });
    }

    public List<AutomatedTestResult> getTestResults() {
        return testResultRepository.findAll();
    }

    public List<AutomatedTestResult> getTestResultsByPhase(String phase) {
        return testResultRepository.findByTestPhaseOrderByTimestampAsc(phase);
    }

    public List<AutomatedTestResult> getTestResultsByOperation(String operation) {
        return testResultRepository.findByOperationOrderByTimestampAsc(operation);
    }
    
    public void exportAllMetrics() {
        List<AutomatedTestResult> allResults = testResultRepository.findAll();
        if (allResults.isEmpty()) {
            log.info("No test results to export");
            return;
        }
        
        // Export all results
        metricsExportService.exportTestResults(allResults);
        
        // Export summary report
        metricsExportService.exportSummaryReport(allResults);
        
        log.info("Exported all metrics to the metrics-export directory");
    }
    
    private void exportLatestSummaryReport() {
        String phase = "Phase " + currentTestSizeIndex;
        List<AutomatedTestResult> phaseResults = testResultRepository.findByTestPhaseOrderByTimestampAsc(phase);
        if (!phaseResults.isEmpty()) {
            metricsExportService.exportSummaryReport(phaseResults);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 