package com.example.evooq.demo.services.cycle;

import static org.junit.jupiter.api.Assertions.*;

import com.example.evooq.demo.kafka.consumer.ActionMessageConsumer;
import com.example.evooq.demo.kafka.model.ActionEventType;
import com.example.evooq.demo.kafka.model.ActionMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for the complete service cycle functionality. Tests the message gathering, sync,
 * and evaluation phases working together.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActionServiceCycleIntegrationTest {

  @Autowired private ActionServiceCycle actionServiceCycle;

  @Autowired private ActionMessageConsumer messageConsumer;

  @Autowired private ActionStateService actionStateService;

  @Test
  void testCompleteServiceCycle() {
    // Given: Initialize the action state (will be empty in test database)
    actionStateService.initializeState();
    int initialActionCount = actionStateService.getActionCount();

    // And: Add some mock Kafka messages (these will fail to sync as actions don't exist, but that's
    // expected)
    messageConsumer.addMockMessage(
        new ActionMessage(ActionEventType.ACTION_ADDED, 999L, "user123", LocalDateTime.now()));
    messageConsumer.addMockMessage(
        new ActionMessage(ActionEventType.ACTION_UPDATED, 1L, "user456", LocalDateTime.now()));

    // When: Execute a complete service cycle
    ServiceCycleResult result = actionServiceCycle.executeCycle();

    // Then: The cycle should complete successfully even if sync operations fail
    assertTrue(result.isSuccessful(), "Service cycle should complete successfully");
    assertNull(result.getErrorMessage(), "Should not have error message on success");
    assertTrue(result.getDurationMs() > 0, "Duration should be positive");

    // And: Messages should be processed
    assertEquals(2, result.getMessagesProcessed(), "Should process 2 messages");

    // And: Actions should be evaluated (will be 0 in empty test database)
    assertEquals(
        initialActionCount, result.getActionsEvaluated(), "Should evaluate the initial actions");
  }

  @Test
  void testServiceCycleWithNoMessages() {
    // Given: No new messages in queue

    // When: Execute service cycle
    ServiceCycleResult result = actionServiceCycle.executeCycle();

    // Then: Cycle should still complete successfully
    assertTrue(result.isSuccessful());
    assertEquals(0, result.getMessagesProcessed());
    assertTrue(result.getDurationMs() > 0);
  }

  @Test
  void testMessageConsumerGathering() {
    // Given: Some mock messages
    LocalDateTime before = LocalDateTime.now();
    messageConsumer.addMockMessage(
        new ActionMessage(ActionEventType.ACTION_ADDED, 100L, "testUser", LocalDateTime.now()));

    // When: Gathering messages
    List<ActionMessage> messages = messageConsumer.gatherMessagesSinceLastCycle();

    // Then: Should retrieve the messages
    assertEquals(1, messages.size());
    ActionMessage message = messages.get(0);
    assertEquals(ActionEventType.ACTION_ADDED, message.getEventType());
    assertEquals(100L, message.getActionId());
    assertEquals("testUser", message.getUserId());
    assertTrue(message.getTimestamp().isAfter(before));
  }
}
