package com.example.evooq.demo.resource.mapper;

import static com.example.evooq.demo.resource.model.ActionType.SELL_AUTOMATIC;

import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.domain.action.BuyAutomaticAction;
import com.example.evooq.demo.domain.action.SellAutomaticAction;
import com.example.evooq.demo.domain.action.SetStopLossAction;
import com.example.evooq.demo.resource.model.ActionResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActionResourceMapper {

  default Action mapToDomainAction(ActionResource actionResource) {
    if (actionResource == null) return null;
    return switch (actionResource.type()) {
      case SELL_AUTOMATIC -> mapToDomainSellAutomaticAction(actionResource);
      case BUY_AUTOMATIC -> mapToDomainBuyAutomaticAction(actionResource);
      case SET_STOP_LOSS -> mapToDomainSetStopLossAction(actionResource);
      default ->
          throw new IllegalArgumentException("Unsupported action type: " + actionResource.type());
    };
  }

  default ActionResource mapToResource(Action action) {
    return switch (action) {
      case null -> null;
      case SetStopLossAction stopLossAction -> mapToSetStopLossResource(stopLossAction);
      case SellAutomaticAction sellAction -> mapToSellAutomaticResource(sellAction);
      case BuyAutomaticAction buyAction -> mapToBuyAutomaticResource(buyAction);
      default ->
          throw new IllegalArgumentException("Unsupported action type: " + action.getClass());
    };
  }

  SellAutomaticAction mapToDomainSellAutomaticAction(ActionResource actionResource);

  BuyAutomaticAction mapToDomainBuyAutomaticAction(ActionResource actionResource);

  SetStopLossAction mapToDomainSetStopLossAction(ActionResource actionResource);

  ActionResource mapToSellAutomaticResource(SellAutomaticAction action);

  ActionResource mapToBuyAutomaticResource(BuyAutomaticAction action);

  ActionResource mapToSetStopLossResource(SetStopLossAction action);
}
