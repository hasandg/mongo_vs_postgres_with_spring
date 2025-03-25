package com.example.performance.automated.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.*;

import java.time.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "automated_test_results")
public class AutomatedTestResult  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(name = "test_phase")
    private String testPhase;

    @Column(name = "execution_order")
    private int executionOrder;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "operation")
    private String operation;

    @Column(name = "record_count")
    private int recordCount;

    @Column(name = "mongodb_time")
    private long mongoDbTime;

    @Column(name = "postgres_time")
    private long postgresTime;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "last_retry_time")
    private LocalDateTime lastRetryTime;

    @Column(name = "total_execution_time")
    private long totalExecutionTime;

    @Column(name = "average_execution_time")
    private double averageExecutionTime;

    @Column(name = "success_rate")
    private double successRate;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (lastRetryTime == null) {
            lastRetryTime = LocalDateTime.now();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetryTime = LocalDateTime.now();
    }

    public void updateMetrics(long executionTime) {
        this.totalExecutionTime += executionTime;
        this.averageExecutionTime = (double) totalExecutionTime / (retryCount + 1);
        this.successRate = "SUCCESS".equals(getStatus()) ? 1.0 : 0.0;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AutomatedTestResult that = (AutomatedTestResult) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}