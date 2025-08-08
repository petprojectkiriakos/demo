package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.domain.context.ContextRegistry;

public abstract class AutomaticAction extends Action {
  public AutomaticAction(
      String userId, String description, ContextRegistry contextRegistry, String contextId) {
    super(userId, description, contextRegistry, contextId);
  }

  @Override
  public abstract boolean shouldTake(ActionContext context);
}
