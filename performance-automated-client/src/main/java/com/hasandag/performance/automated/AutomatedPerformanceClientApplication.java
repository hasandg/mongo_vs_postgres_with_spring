package com.hasandag.performance.automated;

import com.hasandag.performance.automated.service.AutomatedTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class AutomatedPerformanceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutomatedPerformanceClientApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner exportMetricsOnStartup(AutomatedTestService automatedTestService) {
        return args -> {
            // Check if there's a command line argument to export metrics
            for (String arg : args) {
                if (arg.equals("--export-metrics")) {
                    log.info("Exporting metrics on startup based on command line argument");
                    automatedTestService.exportAllMetrics();
                    // Don't exit the application as other features might still be needed
                }
            }
        };
    }
} 