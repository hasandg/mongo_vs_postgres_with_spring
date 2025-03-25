# Automated Performance Testing Client

A Spring Boot application that automatically performs comprehensive performance testing between MongoDB and PostgreSQL databases.

## Overview

This automated client application provides a hands-off approach to performance testing by automatically running tests with different data sizes and operations. It stores test results in an H2 database and provides detailed metrics for analysis.

## Features

- Automatic test execution with configurable intervals
- Progressive test sizes (100, 500, 1000, 5000, 10000 records)
- All operations tested (generate-data, read, update, delete)
- Persistent storage of test results
- Detailed logging and metrics
- Error handling and recovery

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Main application running on port 8081
- MongoDB and PostgreSQL instances

## Project Structure

```
performance-automated-client/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── performance/
│       │               └── automated/
│       │                   ├── AutomatedPerformanceClientApplication.java
│       │                   ├── config/
│       │                   │   └── ClientConfig.java
│       │                   ├── model/
│       │                   │   └── TestResult.java
│       │                   ├── repository/
│       │                   │   └── TestResultRepository.java
│       │                   └── service/
│       │                       ├── AutomatedTestService.java
│       │                       └── TestSchedulerService.java
│       └── resources/
│           └── application.yml
└── pom.xml
```

## Configuration

The application is configured to run on port 8083 and connect to the main application on port 8081. Configuration can be modified in `application.yml`:

```yaml
server:
  port: 8083

spring:
  application:
    name: performance-automated-client
  datasource:
    url: jdbc:h2:file:./test-results
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    com.example.performance.automated: DEBUG
    org.springframework.web: INFO
```

## Test Phases

The automated client runs tests in phases, with each phase increasing the number of records:

1. Phase 1: 100 records
2. Phase 2: 500 records
3. Phase 3: 1000 records
4. Phase 4: 5000 records
5. Phase 5: 10000 records

For each phase, the following operations are tested:
- Generate test data
- Read performance
- Update performance
- Delete performance

## Test Execution

Tests are automatically executed every 5 seconds (configurable in `TestSchedulerService`). The client:
1. Runs one test at a time
2. Stores results in H2 database
3. Moves to the next operation
4. After completing all operations, moves to the next phase
5. Logs all activities and results

## Building and Running

1. Build the application:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

## Dependencies

- Spring Boot Web
- Spring Boot WebFlux
- Spring Boot Data JPA
- H2 Database
- Lombok
- Spring Boot Test
- Micrometer Prometheus
- Jackson Databind

## Test Results

Test results are stored in the H2 database and include:
- Operation type
- Record count
- MongoDB execution time
- PostgreSQL execution time
- Timestamp
- Status
- Error message (if any)
- Test phase
- Performance ratio

## Monitoring

The application uses Micrometer for metrics collection and can be integrated with Prometheus for monitoring.

## Error Handling

The application includes comprehensive error handling:
- Failed tests are logged with detailed error messages
- Test results include status and error information
- Asynchronous execution prevents test failures from affecting other tests
- Automatic retry mechanism for failed tests

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 