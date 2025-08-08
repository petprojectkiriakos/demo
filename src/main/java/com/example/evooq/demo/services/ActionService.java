package com.example.evooq.demo.services;

import com.example.evooq.demo.db.jpa.ActionRepository;
import com.example.evooq.demo.db.mapper.ActionEntityMapper;
import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.domain.action.Action;
import java.util.Optional;
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
}
