package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ActionContext;
import com.example.evooq.demo.domain.context.ContextRegistry;
import java.util.HashMap;

public class SellAutomaticAction extends TargetPriceAction {

  public SellAutomaticAction(
      String userId,
      String description,
      ContextRegistry contextRegistry,
      String contextId,
      Float targetPrice,
      Float divergenceTolerance,
      Boolean priceIsLessThanTarget) {
    super(
        userId,
        description,
        contextRegistry,
        contextId,
        targetPrice,
        divergenceTolerance,
        priceIsLessThanTarget);
  }

  @Override
  public boolean shouldTake(ActionContext context) {
    if (priceIsLessThanTarget) {
      Float currentPrice =
          (Float) context.get(contextRegistry.name(), HashMap.class).get(contextId);
      return currentPrice != null && Math.abs(currentPrice - targetPrice) > divergenceTolerance;
    }
    return false;
  }
}
