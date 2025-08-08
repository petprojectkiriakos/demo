package com.example.evooq.demo.domain.action;

import com.example.evooq.demo.domain.context.ContextRegistry;

public class SetStopLossAction extends SellAutomaticAction {

  private final Float purchasePrice;
  private final Float stopLossPercent;

  public SetStopLossAction(
      String userId,
      String description,
      ContextRegistry contextRegistry,
      String contextId,
      float purchasePrice,
      float stopLossPercent) {
    super(
        userId,
        description,
        contextRegistry,
        contextId,
        purchasePrice * (1 - stopLossPercent), // targetPrice
        0f, // divergenceTolerance
        true // below
        );
    this.purchasePrice = purchasePrice;
    this.stopLossPercent = stopLossPercent;
  }

  public Float getPurchasePrice() {
    return purchasePrice;
  }

  public Float getStopLossPercent() {
    return stopLossPercent;
  }
}
