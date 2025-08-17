package com.example.evooq.demo.resource.mapper;

import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.resource.model.ActionResource;
import com.example.evooq.demo.resource.model.ActionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionQueryMapper {

  @Mapping(source = "actionType", target = "type")
  @Mapping(source = "targetValue", target = "targetPrice")
  @Mapping(source = "stopLossValue", target = "divergenceTolerance")
  @Mapping(target = "priceIsLessThanTarget", constant = "false")
  ActionResource toActionResource(ActionEntity entity);

  default ActionType mapActionType(com.example.evooq.demo.db.model.ActionType entityType) {
    if (entityType == null) return null;
    return switch (entityType) {
      case BUY_AUTOMATIC -> ActionType.BUY_AUTOMATIC;
      case SELL_AUTOMATIC -> ActionType.SELL_AUTOMATIC;
      case SET_STOP_LOSS -> ActionType.SET_STOP_LOSS;
    };
  }
}
