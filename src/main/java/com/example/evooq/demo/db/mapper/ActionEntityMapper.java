package com.example.evooq.demo.db.mapper;

import static com.example.evooq.demo.db.model.ActionType.*;

import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.domain.action.BuyAutomaticAction;
import com.example.evooq.demo.domain.action.SellAutomaticAction;
import com.example.evooq.demo.domain.action.SetStopLossAction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActionEntityMapper {

  // Entity to domain
  default Action toDomain(ActionEntity entity) {
    if (entity == null) return null;
    return switch (entity.getActionType()) {
      case SELL_AUTOMATIC -> toDomainSellAutomatic(entity);
      case BUY_AUTOMATIC -> toDomainBuyAutomatic(entity);
      case SET_STOP_LOSS -> toDomainSetStopLoss(entity);
      default ->
          throw new IllegalArgumentException("Unsupported action type: " + entity.getActionType());
    };
  }

  // Domain to entity
  default ActionEntity toEntity(Action action) {
    return switch (action) {
      case null -> null;
      case SetStopLossAction stopLoss -> toEntitySetStopLoss(stopLoss);
      case SellAutomaticAction sell -> toEntitySellAutomatic(sell);
      case BuyAutomaticAction buy -> toEntityBuyAutomatic(buy);
      default ->
          throw new IllegalArgumentException("Unsupported action type: " + action.getClass());
    };
  }

  SellAutomaticAction toDomainSellAutomatic(ActionEntity entity);

  BuyAutomaticAction toDomainBuyAutomatic(ActionEntity entity);

  SetStopLossAction toDomainSetStopLoss(ActionEntity entity);

  ActionEntity toEntitySellAutomatic(SellAutomaticAction action);

  ActionEntity toEntityBuyAutomatic(BuyAutomaticAction action);

  ActionEntity toEntitySetStopLoss(SetStopLossAction action);
}
