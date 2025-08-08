package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.domain.context.ContextRegistry;

public class AskPermissionAction extends AutomaticAction {

  public AskPermissionAction(
      String userId, String description, ContextRegistry contextRegistry, String contextId) {
    super(userId, description, contextRegistry, contextId);
  }

  @Override
  public boolean shouldTake(ActionContext context) {
    // Implement permission logic here
    return false;
  }
}
