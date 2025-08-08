package com.example.evooq.demo.resource.controller;

import com.example.evooq.demo.kafka.consumer.ActionMessageConsumer;
import com.example.evooq.demo.kafka.model.ActionEventType;
import com.example.evooq.demo.kafka.model.ActionMessage;
import com.example.evooq.demo.services.cycle.ActionCycleScheduler;
import com.example.evooq.demo.services.cycle.ActionStateService;
import com.example.evooq.demo.services.cycle.ServiceCycleResult;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing and monitoring the action service cycle. Provides endpoints for
 * manual triggering, monitoring, and testing.
 */
@RestController
@RequestMapping("/api/v1/service-cycle")
public class ServiceCycleController {

  private final ActionCycleScheduler scheduler;
  private final ActionStateService stateService;
  private final ActionMessageConsumer messageConsumer;

  public ServiceCycleController(
      ActionCycleScheduler scheduler,
      ActionStateService stateService,
      ActionMessageConsumer messageConsumer) {
    this.scheduler = scheduler;
    this.stateService = stateService;
    this.messageConsumer = messageConsumer;
  }

  /**
   * Manually trigger a service cycle execution. Useful for testing or immediate processing needs.
   */
  @PostMapping("/execute")
  public ResponseEntity<ServiceCycleResult> executeServiceCycle() {
    ServiceCycleResult result = scheduler.executeNow();
    return ResponseEntity.ok(result);
  }

  /** Get current status and statistics of the service cycle system. */
  @GetMapping("/status")
  public ResponseEntity<Map<String, Object>> getStatus() {
    Map<String, Object> status =
        Map.of(
            "actionCount", stateService.getActionCount(),
            "pendingMessages", messageConsumer.getQueueSize(),
            "lastProcessedTime", messageConsumer.getLastProcessedTime(),
            "systemTime", LocalDateTime.now());
    return ResponseEntity.ok(status);
  }

  /**
   * Initialize or reset the action state from the database. Use with caution as this clears the
   * current in-memory state.
   */
  @PostMapping("/initialize-state")
  public ResponseEntity<Map<String, String>> initializeState() {
    try {
      stateService.initializeState();
      return ResponseEntity.ok(
          Map.of(
              "status", "success",
              "message", "Action state initialized successfully",
              "actionCount", String.valueOf(stateService.getActionCount())));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(
              Map.of(
                  "status", "error", "message", "Failed to initialize state: " + e.getMessage()));
    }
  }

  /**
   * Add a mock Kafka message for testing purposes. In production, messages would come from actual
   * Kafka topics.
   */
  @PostMapping("/test/add-message")
  public ResponseEntity<Map<String, String>> addTestMessage(
      @RequestParam ActionEventType eventType,
      @RequestParam Long actionId,
      @RequestParam(defaultValue = "testUser") String userId) {

    ActionMessage message = new ActionMessage(eventType, actionId, userId, LocalDateTime.now());
    messageConsumer.addMockMessage(message);

    return ResponseEntity.ok(
        Map.of("status", "success", "message", "Test message added: " + message.toString()));
  }
}
