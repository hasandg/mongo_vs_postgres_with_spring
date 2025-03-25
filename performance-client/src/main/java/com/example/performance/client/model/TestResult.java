package com.example.performance.client.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TestResult {
    private String operation;
    private int recordCount;
    private long mongoDbTime;
    private long postgresTime;
    private LocalDateTime timestamp;
    private String status;
    private String errorMessage;

    public TestResult(String operation, int recordCount) {
        this.operation = operation;
        this.recordCount = recordCount;
        this.timestamp = LocalDateTime.now();
    }

} 