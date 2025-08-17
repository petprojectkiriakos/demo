package com.example.evooq.demo.services;

import com.example.evooq.demo.db.jpa.ActionRepository;
import com.example.evooq.demo.db.mapper.ActionEntityMapper;
import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.db.specification.ActionSpecifications;
import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.resource.model.ActionQueryCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ActionService {
  private final ActionRepository actionRepository;
  private final ActionEntityMapper actionEntityMapper;

  public ActionService(ActionRepository actionRepository, ActionEntityMapper actionEntityMapper) {
    this.actionEntityMapper = actionEntityMapper;
    this.actionRepository = actionRepository;
  }

  public Action create(Action action) {
    ActionEntity entity = actionEntityMapper.toEntity(action);
    entity = actionRepository.save(entity);
    return actionEntityMapper.toDomain(entity);
  }

  public Action edit(Long id, Action updatedAction) {
    Optional<ActionEntity> existing = actionRepository.findById(id);
    if (existing.isEmpty()) throw new RuntimeException("Action not found");
    ActionEntity updated = actionEntityMapper.toEntity(updatedAction);
    ActionEntity existingEntity = existing.get();
    existingEntity.patch(updated);
    actionRepository.save(existingEntity); // Update fields as needed
    return actionEntityMapper.toDomain(existingEntity);
  }

  public void delete(Long id) {
    actionRepository.deleteById(id);
  }

  public Page<ActionEntity> queryActions(ActionQueryCriteria criteria) {
    Specification<ActionEntity> spec = ActionSpecifications.fromCriteria(criteria);
    Pageable pageable = createPageable(criteria);
    return actionRepository.findAll(spec, pageable);
  }

  private Pageable createPageable(ActionQueryCriteria criteria) {
    List<String> sortFields = criteria.getSortByOrDefault();
    String direction = criteria.getSortDirectionOrDefault();

    Sort sort =
        Sort.by(
            direction.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
            sortFields.toArray(new String[0]));

    return PageRequest.of(criteria.getPageOrDefault(), criteria.getSizeOrDefault(), sort);
  }
}
