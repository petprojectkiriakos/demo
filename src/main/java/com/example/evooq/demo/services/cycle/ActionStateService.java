package com.example.evooq.demo.services.cycle;

import com.example.evooq.demo.db.jpa.ActionRepository;
import com.example.evooq.demo.db.mapper.ActionEntityMapper;
import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.domain.action.Action;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manages the internal action state used by the service cycle. This service maintains a cache of
 * actions in memory for efficient access during the evaluation phase, while keeping it synchronized
 * with the database through the sync phase.
 */
@Service
public class ActionStateService {
  private static final Logger logger = LoggerFactory.getLogger(ActionStateService.class);

  private final ActionRepository actionRepository;
  private final ActionEntityMapper actionEntityMapper;

  // Internal cache of actions by ID for efficient access during evaluation
  private final Map<Long, Action> actionCache = new ConcurrentHashMap<>();

  public ActionStateService(
      ActionRepository actionRepository, ActionEntityMapper actionEntityMapper) {
    this.actionRepository = actionRepository;
    this.actionEntityMapper = actionEntityMapper;
  }

  /**
   * Initializes the internal state by loading all actions from the database. This is typically
   * called at application startup or when resetting the state.
   */
  public void initializeState() {
    logger.info("Initializing action state from database");
    try {
      List<ActionEntity> allActions = actionRepository.findAll();
      actionCache.clear();

      for (ActionEntity entity : allActions) {
        Action action = actionEntityMapper.toDomain(entity);
        actionCache.put(entity.getId(), action);
      }

      logger.info("Initialized action state with {} actions", actionCache.size());
    } catch (Exception e) {
      logger.error("Failed to initialize action state", e);
      throw new RuntimeException("Action state initialization failed", e);
    }
  }

  /**
   * Adds or updates an action in the internal state by fetching it from the database. Used during
   * the sync phase when processing ACTION_ADDED or ACTION_UPDATED events.
   */
  public void syncAction(Long actionId) {
    logger.debug("Syncing action with ID: {}", actionId);
    try {
      Optional<ActionEntity> entityOpt = actionRepository.findById(actionId);
      if (entityOpt.isPresent()) {
        Action action = actionEntityMapper.toDomain(entityOpt.get());
        actionCache.put(actionId, action);
        logger.debug("Successfully synced action {}", actionId);
      } else {
        logger.warn("Action {} not found in database, cannot sync", actionId);
      }
    } catch (Exception e) {
      logger.error("Failed to sync action {}", actionId, e);
      throw new RuntimeException("Action sync failed for ID: " + actionId, e);
    }
  }

  /**
   * Removes an action from the internal state. Used during the sync phase when processing
   * ACTION_DELETED events.
   */
  public void removeAction(Long actionId) {
    logger.debug("Removing action with ID: {}", actionId);
    Action removed = actionCache.remove(actionId);
    if (removed != null) {
      logger.debug("Successfully removed action {}", actionId);
    } else {
      logger.warn("Action {} was not present in cache", actionId);
    }
  }

  /**
   * Returns all actions currently in the internal state. Used during the evaluation phase to check
   * conditions for all actions.
   */
  public List<Action> getAllActions() {
    return List.copyOf(actionCache.values());
  }

  /** Returns the current size of the action cache for monitoring purposes. */
  public int getActionCount() {
    return actionCache.size();
  }
}
