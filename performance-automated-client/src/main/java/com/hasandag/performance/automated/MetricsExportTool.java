package com.hasandag.performance.automated;

import com.hasandag.performance.automated.model.AutomatedTestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.hasandag.performance.automated.service.MetricsExportService;
import com.hasandag.performance.automated.repository.TestResultRepository;

import java.util.List;

@Slf4j
@SpringBootApplication
@Profile("export-tool")
public class MetricsExportTool implements CommandLineRunner {

    @Autowired
    private MetricsExportService metricsExportService;
    
    @Autowired
    private TestResultRepository testResultRepository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(MetricsExportTool.class)
            .web(WebApplicationType.NONE) // Don't start a web server
            .profiles("export-tool")
            .run(args);
    }

    @Override
    public void run(String... args) {
        log.info("Starting metrics export tool...");
        List<AutomatedTestResult> results = testResultRepository.findAll();
        
        if (results.isEmpty()) {
            log.info("No test results found in the database.");
            return;
        }
        
        log.info("Found {} test results. Exporting...", results.size());
        
        // Export the metrics
        metricsExportService.exportTestResults(results);
        metricsExportService.exportSummaryReport(results);
        
        log.info("Metrics export completed successfully. Files saved to the metrics-export directory.");
    }
} 