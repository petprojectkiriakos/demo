package com.example.evooq.demo.kafka.model;

import java.time.LocalDateTime;

/**
 * Represents a Kafka message about an action event. This message is used in the message gathering
 * phase to identify which actions need to be synced during the service cycle.
 */
public class ActionMessage {
  private ActionEventType eventType;
  private Long actionId;
  private String userId;
  private LocalDateTime timestamp;

  public ActionMessage() {}

  public ActionMessage(
      ActionEventType eventType, Long actionId, String userId, LocalDateTime timestamp) {
    this.eventType = eventType;
    this.actionId = actionId;
    this.userId = userId;
    this.timestamp = timestamp;
  }

  public ActionEventType getEventType() {
    return eventType;
  }

  public void setEventType(ActionEventType eventType) {
    this.eventType = eventType;
  }

  public Long getActionId() {
    return actionId;
  }

  public void setActionId(Long actionId) {
    this.actionId = actionId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "ActionMessage{"
        + "eventType="
        + eventType
        + ", actionId="
        + actionId
        + ", userId='"
        + userId
        + '\''
        + ", timestamp="
        + timestamp
        + '}';
  }
}
