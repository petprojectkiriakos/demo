package com.example.evooq.demo.kafka.consumer;

import com.example.evooq.demo.kafka.model.ActionMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock Kafka consumer service that simulates gathering messages from Kafka. In a production
 * environment, this would be replaced with actual Kafka consumer using @KafkaListener annotations
 * or Kafka Consumer API.
 */
@Service
public class ActionMessageConsumer {
  private static final Logger logger = LoggerFactory.getLogger(ActionMessageConsumer.class);

  // Mock queue to simulate Kafka messages for demo purposes
  private final ConcurrentLinkedQueue<ActionMessage> messageQueue = new ConcurrentLinkedQueue<>();
  private LocalDateTime lastProcessedTime = LocalDateTime.now().minusHours(1);

  /**
   * Gathers all action messages received since the last cycle. This method simulates polling Kafka
   * for messages within a time range.
   *
   * <p>In production, this would: - Connect to Kafka cluster - Poll for messages from action topics
   * - Filter messages by timestamp - Handle deserialization errors - Manage consumer offsets
   */
  public List<ActionMessage> gatherMessagesSinceLastCycle() {
    logger.info("Gathering Kafka messages since last cycle ({})", lastProcessedTime);

    List<ActionMessage> gatheredMessages = new ArrayList<>();
    LocalDateTime currentTime = LocalDateTime.now();

    try {
      // Simulate gathering messages from Kafka
      while (!messageQueue.isEmpty()) {
        ActionMessage message = messageQueue.poll();
        if (message != null && message.getTimestamp().isAfter(lastProcessedTime)) {
          gatheredMessages.add(message);
        }
      }

      logger.info("Gathered {} messages from Kafka", gatheredMessages.size());
      lastProcessedTime = currentTime;

      return gatheredMessages;
    } catch (Exception e) {
      logger.error("Error gathering messages from Kafka", e);
      throw new RuntimeException("Message gathering failed", e);
    }
  }

  /**
   * Adds a mock message to the queue for testing purposes. In production, this method wouldn't
   * exist as messages would come from actual Kafka.
   */
  public void addMockMessage(ActionMessage message) {
    if (message.getTimestamp() == null) {
      message.setTimestamp(LocalDateTime.now());
    }
    messageQueue.offer(message);
    logger.debug("Added mock message: {}", message);
  }

  /** Returns the timestamp of the last processed cycle for monitoring. */
  public LocalDateTime getLastProcessedTime() {
    return lastProcessedTime;
  }

  /**
   * Gets the current queue size for monitoring purposes. In production, this would return metrics
   * about consumer lag.
   */
  public int getQueueSize() {
    return messageQueue.size();
  }
}
