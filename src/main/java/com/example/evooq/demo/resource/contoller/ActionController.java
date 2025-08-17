package com.example.evooq.demo.resource.contoller;

import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.domain.action.Action;
import com.example.evooq.demo.resource.mapper.ActionQueryMapper;
import com.example.evooq.demo.resource.mapper.ActionResourceMapper;
import com.example.evooq.demo.resource.model.ActionQueryCriteria;
import com.example.evooq.demo.resource.model.ActionQueryResult;
import com.example.evooq.demo.resource.model.ActionResource;
import com.example.evooq.demo.services.ActionService;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actions")
public class ActionController {

  private final ActionService actionService;
  private final ActionResourceMapper actionResourceMapper;
  private final ActionQueryMapper actionQueryMapper;

  public ActionController(
      ActionService actionService,
      ActionResourceMapper actionResourceMapper,
      ActionQueryMapper actionQueryMapper) {
    this.actionService = actionService;
    this.actionResourceMapper = actionResourceMapper;
    this.actionQueryMapper = actionQueryMapper;
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

  @PostMapping("/query")
  public ResponseEntity<ActionQueryResult> query(@RequestBody ActionQueryCriteria criteria) {
    Page<ActionEntity> page = actionService.queryActions(criteria);

    List<ActionResource> content =
        page.getContent().stream().map(actionQueryMapper::toActionResource).toList();

    ActionQueryResult result =
        ActionQueryResult.of(content, page.getNumber(), page.getSize(), page.getTotalElements());

    return ResponseEntity.ok(result);
  }
}
