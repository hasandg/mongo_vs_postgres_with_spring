# MongoDB vs PostgreSQL Performance Analysis

## Overview
This document presents the results of performance testing between MongoDB and PostgreSQL databases across different operations and data volumes.

## Data Export
We successfully exported the metrics data from the H2 database containing 15 records with detailed information about the performance tests, including:

1. Test phases (Phase 1 through Phase 4) with increasing record counts (100, 500, 1000, 5000)
2. Operation types (generate-data, read, update, delete)
3. Performance times for MongoDB and PostgreSQL for each operation
4. Total execution times, average execution times, and success rates
5. Timestamps and status information

## Performance Comparison

### Data Generation
| Records | MongoDB (ms) | PostgreSQL (ms) | Difference |
|---------|--------------|-----------------|------------|
| 100     | 17           | 82              | PostgreSQL ~5x slower |
| 500     | 45           | 286             | PostgreSQL ~6x slower |
| 1,000   | 93           | 502             | PostgreSQL ~5x slower |
| 5,000   | 251          | 2,643           | PostgreSQL ~10x slower |

### Reading Data
| Records | MongoDB (ms) | PostgreSQL (ms) | Difference |
|---------|--------------|-----------------|------------|
| 100     | 946          | 103             | MongoDB ~9x slower |
| 500     | 668          | 102             | MongoDB ~6.5x slower |
| 1,000   | 615          | 81              | MongoDB ~7.5x slower |
| 5,000   | 784          | 108             | MongoDB ~7x slower |

### Updating Data
| Records | MongoDB (ms) | PostgreSQL (ms) | Difference |
|---------|--------------|-----------------|------------|
| 100     | 74           | 1,550           | PostgreSQL ~21x slower |
| 500     | 357          | 8,312           | PostgreSQL ~23x slower |
| 1,000   | 738          | 16,962          | PostgreSQL ~23x slower |
| 5,000   | 3,449        | 97,886          | PostgreSQL ~28x slower |

### Deleting Data
| Records | MongoDB (ms) | PostgreSQL (ms) | Difference |
|---------|--------------|-----------------|------------|
| 100     | 69           | 1,638           | PostgreSQL ~24x slower |
| 500     | 268          | 7,485           | PostgreSQL ~28x slower |
| 1,000   | 632          | 16,359          | PostgreSQL ~26x slower |
| 5,000   | N/A          | N/A             | Data not available |

## Conclusion
The performance metrics indicate:

1. **MongoDB excels at:**
   - Data generation (inserts)
   - Updates
   - Deletes
   - Operations involving large datasets

2. **PostgreSQL excels at:**
   - Read operations across all data volumes
   - Structured queries (not directly measured in this test)

MongoDB shows significant performance advantages for most write operations (insert, update, delete) across all data volumes, while PostgreSQL is consistently faster for read operations. The performance gap between the databases widens as the dataset size increases, especially for update operations. 