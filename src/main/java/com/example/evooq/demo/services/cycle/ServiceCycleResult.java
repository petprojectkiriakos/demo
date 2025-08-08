package com.example.evooq.demo.services.cycle;

import java.util.List;

/**
 * Represents the complete result of a service cycle execution. Contains statistics and results from
 * all three phases of the cycle.
 */
public class ServiceCycleResult {
  private final int messagesProcessed;
  private final List<ActionEvaluationResult> evaluationResults;
  private final long durationMs;
  private final boolean successful;
  private final String errorMessage;

  public ServiceCycleResult(
      int messagesProcessed,
      List<ActionEvaluationResult> evaluationResults,
      long durationMs,
      boolean successful,
      String errorMessage) {
    this.messagesProcessed = messagesProcessed;
    this.evaluationResults = evaluationResults;
    this.durationMs = durationMs;
    this.successful = successful;
    this.errorMessage = errorMessage;
  }

  public int getMessagesProcessed() {
    return messagesProcessed;
  }

  public List<ActionEvaluationResult> getEvaluationResults() {
    return evaluationResults;
  }

  public long getDurationMs() {
    return durationMs;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getActionsEvaluated() {
    return evaluationResults.size();
  }

  public int getActionsReadyForExecution() {
    return (int) evaluationResults.stream().filter(ActionEvaluationResult::shouldExecute).count();
  }

  public int getEvaluationErrors() {
    return (int) evaluationResults.stream().filter(ActionEvaluationResult::hasError).count();
  }

  @Override
  public String toString() {
    return "ServiceCycleResult{"
        + "messagesProcessed="
        + messagesProcessed
        + ", actionsEvaluated="
        + getActionsEvaluated()
        + ", actionsReadyForExecution="
        + getActionsReadyForExecution()
        + ", durationMs="
        + durationMs
        + ", successful="
        + successful
        + ", errorMessage='"
        + errorMessage
        + '\''
        + '}';
  }
}
