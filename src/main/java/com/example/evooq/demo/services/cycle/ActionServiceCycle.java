package com.example.evooq.demo.services.cycle;

import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.kafka.consumer.ActionMessageConsumer;
import com.example.evooq.demo.kafka.model.ActionEventType;
import com.example.evooq.demo.kafka.model.ActionMessage;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the complete service cycle for processing Kafka messages and evaluating actions.
 *
 * <p>The cycle follows a three-phase approach: 1. Message Gathering: Collect all Kafka messages
 * since the last cycle 2. Sync Phase: Update internal action state based on received messages 3.
 * Evaluation Phase: Check all actions to determine if they should be executed
 *
 * <p>This cycle-based approach provides several benefits: - Consistency: All actions are evaluated
 * against the same state snapshot - Correctness: State changes are processed before evaluation
 * begins - Atomicity: Each cycle represents a complete unit of work - Observability: Clear
 * separation makes monitoring and debugging easier
 */
@Service
public class ActionServiceCycle {
  private static final Logger logger = LoggerFactory.getLogger(ActionServiceCycle.class);

  private final ActionMessageConsumer messageConsumer;
  private final ActionStateService actionStateService;
  private final ContextService contextService;

  public ActionServiceCycle(
      ActionMessageConsumer messageConsumer,
      ActionStateService actionStateService,
      ContextService contextService) {
    this.messageConsumer = messageConsumer;
    this.actionStateService = actionStateService;
    this.contextService = contextService;
  }

  /**
   * Executes a complete service cycle: gather messages, sync state, and evaluate actions. This
   * method should be called periodically (e.g., every few seconds) to process all pending changes
   * and determine which actions should be executed.
   */
  public ServiceCycleResult executeCycle() {
    logger.info("Starting service cycle execution");
    long startTime = System.currentTimeMillis();

    try {
      // Phase 1: Message Gathering
      List<ActionMessage> messages = gatherMessages();

      // Phase 2: Sync Phase
      syncInternalState(messages);

      // Phase 3: Evaluation Phase
      List<ActionEvaluationResult> evaluationResults = evaluateActions();

      long duration = System.currentTimeMillis() - startTime;
      ServiceCycleResult result =
          new ServiceCycleResult(messages.size(), evaluationResults, duration, true, null);

      logger.info(
          "Service cycle completed successfully in {}ms. Processed {} messages, evaluated {} actions, {} actions ready for execution",
          duration,
          messages.size(),
          evaluationResults.size(),
          evaluationResults.stream().mapToInt(r -> r.shouldExecute() ? 1 : 0).sum());

      return result;

    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      logger.error("Service cycle failed after {}ms", duration, e);
      return new ServiceCycleResult(0, List.of(), duration, false, e.getMessage());
    }
  }

  /**
   * Phase 1: Message Gathering Collects all Kafka messages received since the last cycle. Handles
   * errors gracefully and logs message statistics.
   */
  private List<ActionMessage> gatherMessages() {
    logger.debug("Phase 1: Starting message gathering");

    try {
      List<ActionMessage> messages = messageConsumer.gatherMessagesSinceLastCycle();

      // Log message statistics for observability
      long addedCount =
          messages.stream().filter(m -> m.getEventType() == ActionEventType.ACTION_ADDED).count();
      long updatedCount =
          messages.stream().filter(m -> m.getEventType() == ActionEventType.ACTION_UPDATED).count();
      long deletedCount =
          messages.stream().filter(m -> m.getEventType() == ActionEventType.ACTION_DELETED).count();

      logger.debug(
          "Phase 1 completed: Gathered {} messages (Added: {}, Updated: {}, Deleted: {})",
          messages.size(),
          addedCount,
          updatedCount,
          deletedCount);

      return messages;

    } catch (Exception e) {
      logger.error("Phase 1 failed: Message gathering error", e);
      throw new RuntimeException("Message gathering phase failed", e);
    }
  }

  /**
   * Phase 2: Sync Phase Updates the internal action state based on the messages received. Processes
   * each message type appropriately and handles errors robustly.
   */
  private void syncInternalState(List<ActionMessage> messages) {
    logger.debug("Phase 2: Starting state synchronization with {} messages", messages.size());

    int syncedActions = 0;
    int failedSyncs = 0;

    for (ActionMessage message : messages) {
      try {
        switch (message.getEventType()) {
          case ACTION_ADDED, ACTION_UPDATED -> {
            actionStateService.syncAction(message.getActionId());
            syncedActions++;
            logger.debug(
                "Synced action {} for event {}", message.getActionId(), message.getEventType());
          }
          case ACTION_DELETED -> {
            actionStateService.removeAction(message.getActionId());
            syncedActions++;
            logger.debug("Removed action {} from state", message.getActionId());
          }
          default -> {
            logger.warn(
                "Unknown event type {} for action {}",
                message.getEventType(),
                message.getActionId());
          }
        }
      } catch (Exception e) {
        failedSyncs++;
        logger.error(
            "Failed to sync action {} for event {}",
            message.getActionId(),
            message.getEventType(),
            e);
        // Continue processing other messages rather than failing the entire cycle
      }
    }

    logger.debug("Phase 2 completed: Synced {} actions, {} failed", syncedActions, failedSyncs);

    if (failedSyncs > 0) {
      logger.warn(
          "Phase 2 had {} failed sync operations out of {} total messages",
          failedSyncs,
          messages.size());
    }
  }

  /**
   * Phase 3: Evaluation Phase Evaluates all actions in the current state to determine which ones
   * should be executed. Uses the action's shouldTake method with current context to make decisions.
   */
  private List<ActionEvaluationResult> evaluateActions() {
    logger.debug("Phase 3: Starting action evaluation");

    List<Action> allActions = actionStateService.getAllActions();
    List<ActionEvaluationResult> evaluationResults = new ArrayList<>();

    int actionsEvaluated = 0;
    int actionsReadyForExecution = 0;
    int evaluationErrors = 0;

    for (Action action : allActions) {
      try {
        // Get the current context for this action
        ActionContext context = contextService.getCurrentContext();

        // Evaluate if the action should be taken
        boolean shouldExecute = action.shouldTake(context);
        actionsEvaluated++;

        if (shouldExecute) {
          actionsReadyForExecution++;
          logger.debug("Action {} is ready for execution", action.getDescription());
        }

        evaluationResults.add(new ActionEvaluationResult(action, shouldExecute, null));

      } catch (Exception e) {
        evaluationErrors++;
        logger.error("Failed to evaluate action: {}", action.getDescription(), e);
        evaluationResults.add(
            new ActionEvaluationResult(action, false, "Evaluation error: " + e.getMessage()));
      }
    }

    logger.debug(
        "Phase 3 completed: Evaluated {} actions, {} ready for execution, {} errors",
        actionsEvaluated,
        actionsReadyForExecution,
        evaluationErrors);

    if (evaluationErrors > 0) {
      logger.warn(
          "Phase 3 had {} evaluation errors out of {} total actions",
          evaluationErrors,
          allActions.size());
    }

    return evaluationResults;
  }
}
