# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Temporary files
- **temporary-files** - use for temporary work files that can be deleted later

## Project Overview

This is a bank transaction verification system built for a competition/hackathon. The application:
- Fetches unverified bank transactions from an external API
- Validates transactions against multiple fraud detection rules
- Verifies or rejects transactions via API callbacks
- Persists transaction records to a PostgreSQL database

## Key Technologies

- **Java 21** with Spring Boot 3.4.4
- **Gradle** build system
- **PostgreSQL 16** for data persistence
- **Flyway** for database migrations
- **Retrofit 2** for external API communication
- **Lombok** for boilerplate reduction
- **JPA/JDBC** for database access

## Common Commands

### Database
Start the PostgreSQL database in Docker:
```bash
docker compose up -d
```

Database connection details:
- Host: localhost:5439
- Database: detector
- User: postgres
- Password: GVA0wPDuA9S5f24x

### Running the Application

In IDE (preferred method):
- Run `DetectorApplication.java` main class

Via Gradle:
```bash
./gradlew bootRun --args='--detector.token=ajryQmxU5XwN7jXWWSl6i4Lvk3TVG98m'
```

### Stopping the Application

Kill all running instances:
```bash
pkill -f DetectorApplication
```

### Testing
Run tests:
```bash
./gradlew test
```

### Building
Build the project:
```bash
./gradlew build
```

## Architecture

### Core Processing Flow

1. **Processor** (`process/Processor.java`): Main scheduled job that runs every 1 second
   - Fetches batches of 10 unverified transactions
   - Validates each transaction
   - Sends verify/reject decision to API
   - Persists transaction to database

2. **Domain-Driven Structure**: Code is organized by domain entities:
   - `domain/transaction/`: Transaction validation and processing
   - `domain/person/`: Person validation (warrant, contract, blacklist checks)
   - `domain/account/`: Account validation logic
   - `domain/device/`: Device validation logic

Each domain follows a consistent structure:
- `common/`: Entity, repository, mapper, specification classes
- `external/`: External API integration (requester, API config, models)
- `feature/`: Business feature implementations (use case patterns)
- `*Validator.java`: Domain-specific validation rules

### Transaction Validation Rules

The `TransactionValidator` (line 25) applies multiple fraud detection checks:
- Person validation: no warrant, has contract, not blacklisted (both sender and recipient)
- Device validation: device MAC address checks
- Account validation: sender has sufficient funds, accounts belong to correct persons
- Burst detection: max 10 transactions per sender in 30 seconds
- Multi-device detection: max 2 different devices per sender in 10 seconds
- History validation: sender's recent transactions (1 minute) must all be legitimate

### External API Integration

All external APIs use Retrofit with a custom `RetrofitRequestExecutor` wrapper from `ee.bitweb.core` library:
- `TransactionsApi`: Fetch unverified transactions, verify/reject transactions
- `PersonApi`: Fetch person details
- `AccountApi`: Fetch account details
- `DeviceApi`: Fetch device details

API configuration uses Spring properties:
- Base URL: `https://devday.sandbox.bitw3b.eu`
- Token: Configured in `application.properties` as `detector.token`

### Configuration

The application requires a valid API token to run. Register via:
```bash
curl -X POST 'https://devday.sandbox.bitw3b.eu/detectors' \
  -H 'Content-Type: application/json' \
  --data-raw '{"name": "YourTeamName"}'
```

Place the returned token in `src/main/resources/application.properties`:
```
detector.token=your_token_here
```

### Service Limitations

- Max 50 concurrent requests per API token
- Max 10000 pending transactions per API token

## Monitoring and Performance Metrics

### Grafana Dashboard

The competition provides a Grafana dashboard to monitor all teams' performance metrics:
- **Dashboard URL**: https://dash.sandbox.bitw3b.eu/d/cejuv890k9wcgc/digit-2025-dashboard
- **Credentials**: admin/admin
- **Refresh Rate**: 5 seconds

The dashboard tracks three key metrics per team:
1. **Correctly Verified Transactions Rate** - Successfully processed transactions per minute
2. **Incorrectly Verified Transactions Rate** - Bugs in validation logic (false positives/negatives)
3. **Late Transactions Rate** - Transactions processed too slowly (performance issues)

### Fetching Team-Specific Metrics

To programmatically fetch metrics for a specific team (e.g., "Plepic"), use the Prometheus API through Grafana:

#### 1. Get Incorrectly Verified Transactions (Bugs)
```bash
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_miss_total{detector="Plepic"}[1m]) * 60' -s | jq .
```

#### 2. Get Correctly Verified Transactions (Success Rate)
```bash
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_hit_total{detector="Plepic"}[1m]) * 60' -s | jq .
```

#### 3. Get Late Transactions (Performance Issues)
```bash
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_late_total{detector="Plepic"}[1m]) * 60' -s | jq .
```

#### 4. Get All Teams' Metrics (for comparison)
```bash
# All teams - miss rate
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_miss_total[1m]) * 60' -s | jq .

# All teams - hit rate
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_hit_total[1m]) * 60' -s | jq .

# All teams - late rate
curl -u admin:admin -G 'https://dash.sandbox.bitw3b.eu/api/datasources/proxy/uid/cemc2ioh2i51cb/api/v1/query' \
  --data-urlencode 'query=rate(transactions_late_total[1m]) * 60' -s | jq .
```

#### Understanding the Results

The API returns JSON with the current rate per minute. Example response:
```json
{
  "status": "success",
  "data": {
    "resultType": "vector",
    "result": [
      {
        "metric": {
          "detector": "Plepic",
          "application": "transactions"
        },
        "value": [1761312440.023, "52.88135593220339"]
      }
    ]
  }
}
```

The second value in the `value` array is the rate (52.88 transactions/minute in this example).

#### Key Metrics to Monitor

- **Miss Rate**: Should be **0** - any value indicates bugs in validation logic
- **Hit Rate**: Higher is better - indicates throughput and accuracy
- **Late Rate**: Should be **0** - indicates performance bottlenecks if non-zero
- **Success Ratio**: `hit_rate / (hit_rate + miss_rate)` - should be over 99%

### Performance Optimization Tips

If your team shows:
- **High miss rate**: Review validation logic in `TransactionValidator`, `PersonValidator`, etc.
- **High late rate**: Optimize API calls, add caching, increase concurrent requests
- **Low hit rate**: Check for errors in logs, ensure all validation rules are implemented correctly

## Important Implementation Notes

- The application uses async configuration (`AsyncConfig.java`) and scheduling (`SchedulingConfig.java`)
- Spring Boot scans both `ee.digit25` and `ee.bitweb.core` packages
- Database migrations are in `src/main/resources/db/migration/`
- Custom object mapper configuration is enabled via `ee.bitweb.core.object-mapper.auto-configuration=true`
- The `ee.bitweb` core library provides Retrofit and ObjectMapper autoconfiguration
