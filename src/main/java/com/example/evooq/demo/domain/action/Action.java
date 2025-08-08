package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.domain.context.ContextRegistry;

public abstract class Action {
  protected String userId;
  protected String description;
  protected ContextRegistry contextRegistry;
  protected String contextId;

  public Action(
      String userId, String description, ContextRegistry contextRegistry, String contextId) {
    this.userId = userId;
    this.description = description;
    this.contextRegistry = contextRegistry;
    this.contextId = contextId;
  }

  public String getUserId() {
    return userId;
  }

  public String getDescription() {
    return description;
  }

  public ContextRegistry getContextRegistry() {
    return contextRegistry;
  }

  public String getContextId() {
    return contextId;
  }

  // Abstract method to execute the action
  public abstract boolean shouldTake(ActionContext context);
}
