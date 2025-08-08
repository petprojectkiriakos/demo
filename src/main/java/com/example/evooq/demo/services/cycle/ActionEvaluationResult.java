package com.example.evooq.demo.services.cycle;

import com.example.evooq.demo.domain.action.Action;

/**
 * Represents the result of evaluating a single action during the evaluation phase. Contains the
 * action, whether it should be executed, and any error message.
 */
public class ActionEvaluationResult {
  private final Action action;
  private final boolean shouldExecute;
  private final String errorMessage;

  public ActionEvaluationResult(Action action, boolean shouldExecute, String errorMessage) {
    this.action = action;
    this.shouldExecute = shouldExecute;
    this.errorMessage = errorMessage;
  }

  public Action getAction() {
    return action;
  }

  public boolean shouldExecute() {
    return shouldExecute;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean hasError() {
    return errorMessage != null;
  }

  @Override
  public String toString() {
    return "ActionEvaluationResult{"
        + "action="
        + action.getDescription()
        + ", shouldExecute="
        + shouldExecute
        + ", errorMessage='"
        + errorMessage
        + '\''
        + '}';
  }
}
