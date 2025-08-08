package com.example.evooq.demo.resource.contoller;

import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.resource.mapper.ActionResourceMapper;
import com.example.evooq.demo.resource.model.ActionResource;
import com.example.evooq.demo.services.ActionService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actions")
public class ActionController {

  private final ActionService actionService;
  private final ActionResourceMapper actionResourceMapper;

  public ActionController(ActionService actionService, ActionResourceMapper actionResourceMapper) {
    this.actionService = actionService;
    this.actionResourceMapper = actionResourceMapper;
  }

  @PostMapping("/{userId}")
  public ResponseEntity<ActionResource> create(
      @PathVariable String userId, @RequestBody ActionResource actionResource) {
    Action action = actionResourceMapper.mapToDomainAction(actionResource);
    Action created = actionService.create(action);
    return ResponseEntity.created(URI.create("actions/" + userId))
        .body(actionResourceMapper.mapToResource(created));
  }

  @PutMapping("/{userId}/{id}")
  public ResponseEntity<ActionResource> edit(
      @PathVariable String userId,
      @PathVariable Long id,
      @RequestBody ActionResource actionResource) {
    Action action = actionResourceMapper.mapToDomainAction(actionResource);
    Action updated = actionService.edit(id, action);
    return ResponseEntity.ok(actionResourceMapper.mapToResource(updated));
  }

  @DeleteMapping("/{userId}/{id}")
  public ResponseEntity<Void> delete(@PathVariable String userId, @PathVariable Long id) {
    actionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
