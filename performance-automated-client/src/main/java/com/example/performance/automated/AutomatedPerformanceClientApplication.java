package com.example.performance.automated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutomatedPerformanceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutomatedPerformanceClientApplication.class, args);
    }
} 