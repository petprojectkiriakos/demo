package com.example.evooq.demo.services.cycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduler service that triggers the action service cycle at regular intervals. This service
 * coordinates when cycles should run and handles initialization.
 *
 * <p>In a production environment, the scheduling could be: - More frequent (every few seconds) for
 * real-time trading - Triggered by external events (market open/close, significant price movements)
 * - Coordinated across multiple instances using distributed locking
 */
@Service
@EnableScheduling
@EnableAsync
public class ActionCycleScheduler {
  private static final Logger logger = LoggerFactory.getLogger(ActionCycleScheduler.class);

  private final ActionServiceCycle actionServiceCycle;
  private final ActionStateService actionStateService;

  public ActionCycleScheduler(
      ActionServiceCycle actionServiceCycle, ActionStateService actionStateService) {
    this.actionServiceCycle = actionServiceCycle;
    this.actionStateService = actionStateService;
  }

  /**
   * Initializes the action state when the application starts. This ensures we have a consistent
   * starting point for the service cycle.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    logger.info("Application ready, initializing action state");
    try {
      actionStateService.initializeState();
      logger.info("Action state initialization completed successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize action state", e);
      // Application can still start, but cycles will have issues until state is fixed
    }
  }

  /**
   * Executes the service cycle every 30 seconds. This frequency can be adjusted based on
   * requirements: - Higher frequency for real-time trading scenarios - Lower frequency for
   * batch-oriented processing
   */
  @Scheduled(fixedRate = 30000) // 30 seconds
  @Async
  public void executeServiceCycle() {
    logger.debug("Scheduled service cycle execution starting");

    try {
      ServiceCycleResult result = actionServiceCycle.executeCycle();

      if (result.isSuccessful()) {
        logger.info("Scheduled cycle completed: {}", result);
      } else {
        logger.error("Scheduled cycle failed: {}", result.getErrorMessage());
      }

    } catch (Exception e) {
      logger.error("Unexpected error during scheduled service cycle", e);
    }
  }

  /**
   * Manually triggers a service cycle execution. Useful for testing or forcing immediate
   * processing.
   */
  public ServiceCycleResult executeNow() {
    logger.info("Manual service cycle execution requested");
    return actionServiceCycle.executeCycle();
  }
}
