package com.hasandag.performance.automated.service;

import com.hasandag.performance.automated.model.AutomatedTestResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetricsExportService {
    private static final String EXPORT_DIR = "metrics-export";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final ObjectMapper objectMapper;
    
    public MetricsExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        createExportDirectory();
    }
    
    private void createExportDirectory() {
        try {
            Path exportDir = Paths.get(EXPORT_DIR);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
                log.info("Created metrics export directory: {}", exportDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create metrics export directory: {}", e.getMessage());
        }
    }
    
    public void exportTestResult(AutomatedTestResult result) {
        try {
            String filename = generateFilename(result);
            File file = new File(EXPORT_DIR, filename);
            objectMapper.writeValue(file, result);
            log.info("Exported test result to: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to export test result: {}", e.getMessage());
        }
    }
    
    public void exportTestResults(List<AutomatedTestResult> results) {
        try {
            String filename = "batch_export_" + LocalDateTime.now().format(DATE_FORMATTER) + ".json";
            File file = new File(EXPORT_DIR, filename);
            objectMapper.writeValue(file, results);
            log.info("Exported {} test results to: {}", results.size(), file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to export test results: {}", e.getMessage());
        }
    }
    
    public void exportSummaryReport(List<AutomatedTestResult> results) {
        try {
            String filename = "summary_report_" + LocalDateTime.now().format(DATE_FORMATTER) + ".csv";
            File file = new File(EXPORT_DIR, filename);
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write CSV header
                writer.write("Operation,RecordCount,MongoDBTime(ms),PostgresTime(ms),Difference(ms),FasterDB,Timestamp\n");
                
                // Write data rows
                for (AutomatedTestResult result : results) {
                    String fasterDb = result.getMongoDbTime() < result.getPostgresTime() ? "MongoDB" : "PostgreSQL";
                    long difference = Math.abs(result.getMongoDbTime() - result.getPostgresTime());
                    
                    writer.write(String.format("%s,%d,%d,%d,%d,%s,%s\n",
                            result.getOperation(),
                            result.getRecordCount(),
                            result.getMongoDbTime(),
                            result.getPostgresTime(),
                            difference,
                            fasterDb,
                            result.getTimestamp()));
                }
            }
            
            log.info("Exported summary report to: {}", file.getAbsolutePath());
            
            // Also create a comparative JSON report
            exportComparativeReport(results);
        } catch (IOException e) {
            log.error("Failed to export summary report: {}", e.getMessage());
        }
    }
    
    private void exportComparativeReport(List<AutomatedTestResult> results) throws IOException {
        String filename = "comparative_report_" + LocalDateTime.now().format(DATE_FORMATTER) + ".json";
        File file = new File(EXPORT_DIR, filename);
        
        Map<String, Map<Integer, Map<String, Object>>> report = new HashMap<>();
        
        // Group by operation and record count
        for (AutomatedTestResult result : results) {
            String operation = result.getOperation();
            int recordCount = result.getRecordCount();
            
            report.computeIfAbsent(operation, k -> new HashMap<>())
                  .computeIfAbsent(recordCount, k -> new HashMap<>());
            
            Map<String, Object> metrics = report.get(operation).get(recordCount);
            metrics.put("mongoDbTime", result.getMongoDbTime());
            metrics.put("postgresTime", result.getPostgresTime());
            metrics.put("difference", Math.abs(result.getMongoDbTime() - result.getPostgresTime()));
            metrics.put("fasterDb", result.getMongoDbTime() < result.getPostgresTime() ? "MongoDB" : "PostgreSQL");
            metrics.put("percentageDifference", calculatePercentageDifference(result.getMongoDbTime(), result.getPostgresTime()));
            metrics.put("timestamp", result.getTimestamp());
        }
        
        objectMapper.writeValue(file, report);
        log.info("Exported comparative report to: {}", file.getAbsolutePath());
    }
    
    private String generateFilename(AutomatedTestResult result) {
        return String.format("%s_%d_records_%s.json",
                result.getOperation(),
                result.getRecordCount(),
                LocalDateTime.now().format(DATE_FORMATTER));
    }
    
    private double calculatePercentageDifference(long time1, long time2) {
        if (time1 == 0 || time2 == 0) return 0;
        return Math.abs((double)(time1 - time2) / Math.max(time1, time2) * 100);
    }
} 