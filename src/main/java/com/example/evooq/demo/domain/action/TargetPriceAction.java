package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ContextRegistry;

public abstract class TargetPriceAction extends AutomaticAction {
  protected Float targetPrice;
  protected Float divergenceTolerance;
  protected Boolean priceIsLessThanTarget;

  public TargetPriceAction(
      String userId,
      String description,
      ContextRegistry contextRegistry,
      String contextId,
      Float targetPrice,
      Float divergenceTolerance,
      Boolean priceIsLessThanTarget) {
    super(userId, description, contextRegistry, contextId);
    this.targetPrice = targetPrice;
    this.divergenceTolerance = divergenceTolerance;
    this.priceIsLessThanTarget = priceIsLessThanTarget;
  }

  // Getters and setters as needed
}
