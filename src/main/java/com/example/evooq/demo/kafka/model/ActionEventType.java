package com.example.evooq.demo.kafka.model;

/**
 * Defines the types of action events that can be received from Kafka. These events trigger updates
 * to the internal action state during the sync phase.
 */
public enum ActionEventType {
  /** A new action has been added and should be fetched and stored internally */
  ACTION_ADDED,

  /** An existing action has been updated and should be refreshed from the data source */
  ACTION_UPDATED,

  /** An action has been deleted and should be removed from internal state */
  ACTION_DELETED
}
