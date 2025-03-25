# MongoDB vs PostgreSQL Performance Comparison

This Spring Boot project compares the performance of MongoDB and PostgreSQL databases using Docker containers.

## Prerequisites

- Java 17 or later
- Maven
- Docker and Docker Compose

## Setup

1. Clone the repository
2. Start the Docker containers:
   ```bash
   docker-compose up -d
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

The application provides the following endpoints to test database performance:

1. Generate Test Data:
   ```
   POST /api/performance/generate-data/{count}
   ```
   Generates the specified number of test records in both databases.

2. Test Read Performance:
   ```
   GET /api/performance/read/{count}
   ```
   Tests read performance for both databases.

3. Test Update Performance:
   ```
   PUT /api/performance/update/{count}
   ```
   Tests update performance for both databases.

4. Test Delete Performance:
   ```
   DELETE /api/performance/delete/{count}
   ```
   Tests delete performance for both databases.

## Example Usage

1. Generate 1000 test records:
   ```bash
   curl -X POST http://localhost:8080/api/performance/generate-data/1000
   ```

2. Test read performance:
   ```bash
   curl -X GET http://localhost:8080/api/performance/read/1000
   ```

3. Test update performance:
   ```bash
   curl -X PUT http://localhost:8080/api/performance/update/1000
   ```

4. Test delete performance:
   ```bash
   curl -X DELETE http://localhost:8080/api/performance/delete/1000
   ```

## Database Credentials

### MongoDB
- Host: localhost
- Port: 27017
- Database: performance_db
- Username: admin
- Password: admin123

### PostgreSQL
- Host: localhost
- Port: 5432
- Database: performance_db
- Username: postgres
- Password: postgres

## Performance Metrics

The application will output the execution time in milliseconds for each operation in both databases. This allows you to compare:
- Insert performance
- Read performance
- Update performance
- Delete performance

## Cleanup

To stop the Docker containers:
```bash
docker-compose down
``` 

