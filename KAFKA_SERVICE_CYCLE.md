# Kafka Service Cycle Implementation

## Overview

This implementation provides a comprehensive service cycle that processes Kafka messages related to actions (add, update, delete) before evaluating if actions can be taken. The architecture ensures consistency and correctness through a well-defined three-phase approach.

## Architecture

### Core Components

1. **ActionServiceCycle** - Main orchestrator that executes the three-phase cycle
2. **ActionMessageConsumer** - Kafka consumer service (mock implementation for demo)
3. **ActionStateService** - Internal state management with in-memory cache
4. **ContextService** - Provides market context data for action evaluation

### Three-Phase Cycle

#### Phase 1: Message Gathering
```
Phase 1: Starting message gathering
Gathering Kafka messages since last cycle
Gathered X messages (Added: Y, Updated: Z, Deleted: W)
```
- Collects all Kafka messages received since the last cycle
- Filters messages by timestamp to avoid reprocessing
- Provides statistics on message types for observability
- Handles connection errors gracefully

#### Phase 2: Sync Phase  
```
Phase 2: Starting state synchronization with X messages
Synced Y actions, Z failed
```
- Updates internal action state based on received messages
- **ACTION_ADDED/ACTION_UPDATED**: Fetches action from database and updates cache
- **ACTION_DELETED**: Removes action from internal cache
- Continues processing even if individual sync operations fail
- Logs detailed information about each sync operation

#### Phase 3: Evaluation Phase
```
Phase 3: Starting action evaluation  
Evaluated X actions, Y ready for execution, Z errors
```
- Evaluates all actions in current state using market context
- Uses action's `shouldTake(context)` method for decision making
- Provides statistics on evaluation results
- Handles evaluation errors without failing the entire cycle

## Benefits of Cycle-Based Approach

### Consistency
- All actions are evaluated against the same state snapshot
- No partial state updates during evaluation
- Atomic view of the system at each cycle

### Correctness  
- State changes are processed before evaluation begins
- Ensures actions see the most recent state
- Prevents race conditions between updates and evaluations

### Atomicity
- Each cycle represents a complete unit of work
- Clear boundaries between processing phases
- Easy to reason about system behavior

### Observability
- Clear logging for each phase and outcome
- Detailed metrics on performance and errors
- Easy monitoring and debugging capabilities

## Implementation Details

### Service Scheduling
```java
@Scheduled(fixedRate = 30000) // 30 seconds
public void executeServiceCycle() {
    ServiceCycleResult result = actionServiceCycle.executeCycle();
    // Process results...
}
```

### Error Handling
- **Message Processing**: Continues on individual message failures
- **Data Fetching**: Logs warnings for missing actions but continues
- **Evaluation Errors**: Records errors but processes other actions
- **System Failures**: Returns failure result with error details

### Logging Strategy
```
INFO  - High-level cycle execution and results
DEBUG - Detailed phase execution and individual operations
WARN  - Non-critical failures (missing actions, etc.)
ERROR - Critical failures that require attention
```

### Mock Implementation Notes
The current implementation uses mock/placeholder components that simulate real Kafka infrastructure:

- **ActionMessageConsumer**: Uses in-memory queue instead of real Kafka consumer
- **ContextService**: Generates mock market data instead of fetching real prices
- **Database Operations**: Uses standard JPA but with test database for demo

## Production Adaptation

To adapt for production use:

### Kafka Integration
```java
@KafkaListener(topics = "action-events")
public void handleActionMessage(ActionMessage message) {
    // Add to processing queue
}
```

### Real Context Data
```java
public ActionContext getCurrentContext() {
    // Fetch from market data APIs
    // Get from Redis/cache
    // Query real-time databases
}
```

### Distributed Coordination
```java
@SchedulerLock(name = "action-service-cycle")
@Scheduled(fixedRate = 30000)
public void executeServiceCycle() {
    // Distributed locking for multiple instances
}
```

## API Endpoints

### Manual Execution
```http
POST /api/v1/service-cycle/execute
```

### System Status
```http
GET /api/v1/service-cycle/status
```

### State Management  
```http
POST /api/v1/service-cycle/initialize-state
```

### Testing Support
```http
POST /api/v1/service-cycle/test/add-message?eventType=ACTION_ADDED&actionId=123
```

## Testing

The implementation includes comprehensive tests:

```bash
./gradlew test --tests "*ActionServiceCycleIntegrationTest*"
```

Tests validate:
- Complete service cycle execution
- Message gathering functionality
- Error handling and resilience
- State management operations

## Configuration

### Test Environment
```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.com.example.evooq.demo=DEBUG
```

### Production Environment
```properties
# Kafka configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=action-service-group

# Database configuration  
spring.datasource.url=jdbc:postgresql://db:5432/actiondb
```

## Monitoring

Key metrics to monitor:
- Cycle execution time and frequency
- Message processing throughput
- Sync operation success/failure rates
- Action evaluation results
- System resource usage

## Conclusion

This implementation provides a robust, scalable foundation for processing action events in a consistent and reliable manner. The three-phase cycle ensures data integrity while the comprehensive error handling and logging make it production-ready with proper infrastructure configuration.