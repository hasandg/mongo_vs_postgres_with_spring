# Performance Testing Client

A Spring Boot application designed to perform comprehensive performance testing between MongoDB and PostgreSQL databases.

## Overview

This client application provides a robust framework for running performance tests against a main application that uses both MongoDB and PostgreSQL databases. It supports asynchronous test execution, concurrent test running, and detailed result analysis.

## Features

- Asynchronous test execution
- Concurrent test running
- Result storage and analysis
- Error handling and logging
- Performance ratio calculation
- Clean separation of concerns

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Main application running on port 8081
- MongoDB and PostgreSQL instances

## Project Structure

```
performance-client/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── performance/
│       │               └── client/
│       │                   ├── PerformanceClientApplication.java
│       │                   ├── config/
│       │                   │   └── ClientConfig.java
│       │                   ├── controller/
│       │                   │   └── PerformanceTestController.java
│       │                   ├── model/
│       │                   │   └── TestResult.java
│       │                   └── service/
│       │                       └── PerformanceTestService.java
│       └── resources/
│           └── application.yml
└── pom.xml
```

## Configuration

The application is configured to run on port 8082 and connect to the main application on port 8081. Configuration can be modified in `application.yml`:

```yaml
server:
  port: 8082

spring:
  application:
    name: performance-client

logging:
  level:
    com.example.performance.client: DEBUG
    org.springframework.web: INFO
```

## API Endpoints

### Run Performance Test
```http
POST /api/performance-test/run/{operation}/{count}
```

Parameters:
- `operation`: Type of operation (generate-data, read, update, delete)
- `count`: Number of records to process

Example:
```bash
curl -X POST http://localhost:8082/api/performance-test/run/generate-data/1000
```

### Get Test Results
```http
GET /api/performance-test/results
```

Returns all test results with performance metrics.

Example:
```bash
curl -X GET http://localhost:8082/api/performance-test/results
```

### Clear Test Results
```http
DELETE /api/performance-test/results
```

Clears all stored test results.

Example:
```bash
curl -X DELETE http://localhost:8082/api/performance-test/results
```

## Test Result Model

Each test result contains:
- Operation type
- Record count
- MongoDB execution time
- PostgreSQL execution time
- Timestamp
- Status
- Error message (if any)
- Performance ratio (PostgreSQL time / MongoDB time)

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
- Lombok
- Spring Boot Test
- Micrometer Prometheus
- Jackson Databind

## Performance Testing Workflow

1. Start the main application (port 8081)
2. Start the performance client (port 8082)
3. Run tests using the API endpoints
4. Analyze results using the GET endpoint
5. Clear results when needed

## Example Test Sequence

```bash
# Generate test data
curl -X POST http://localhost:8082/api/performance-test/run/generate-data/1000

# Test read performance
curl -X POST http://localhost:8082/api/performance-test/run/read/1000

# Test update performance
curl -X POST http://localhost:8082/api/performance-test/run/update/1000

# Test delete performance
curl -X POST http://localhost:8082/api/performance-test/run/delete/1000

# Get results
curl -X GET http://localhost:8082/api/performance-test/results
```

## Error Handling

The application includes comprehensive error handling:
- Failed tests are logged with detailed error messages
- Test results include status and error information
- Asynchronous execution prevents test failures from affecting other tests

## Monitoring

The application uses Micrometer for metrics collection and can be integrated with Prometheus for monitoring.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 