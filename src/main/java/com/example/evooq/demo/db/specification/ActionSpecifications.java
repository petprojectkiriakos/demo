package com.example.evooq.demo.db.specification;

import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.db.model.ActionType;
import com.example.evooq.demo.resource.model.ActionQueryCriteria;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ActionSpecifications {

  public static Specification<ActionEntity> fromCriteria(ActionQueryCriteria criteria) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (criteria.userId() != null && !criteria.userId().trim().isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("userId"), criteria.userId()));
      }

      if (criteria.type() != null) {
        ActionType entityType = mapToEntityType(criteria.type());
        predicates.add(criteriaBuilder.equal(root.get("actionType"), entityType));
      }

      if (criteria.createdFrom() != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.createdFrom()));
      }

      if (criteria.createdTo() != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.createdTo()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static ActionType mapToEntityType(
      com.example.evooq.demo.resource.model.ActionType resourceType) {
    return switch (resourceType) {
      case BUY_AUTOMATIC -> ActionType.BUY_AUTOMATIC;
      case SELL_AUTOMATIC -> ActionType.SELL_AUTOMATIC;
      case SET_STOP_LOSS -> ActionType.SET_STOP_LOSS;
    };
  }
}
