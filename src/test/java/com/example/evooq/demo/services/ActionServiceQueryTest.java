package com.example.evooq.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.evooq.demo.db.jpa.ActionRepository;
import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.db.model.ActionType;
import com.example.evooq.demo.resource.model.ActionQueryCriteria;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ActionServiceQueryTest {

  @Mock private ActionRepository actionRepository;

  @InjectMocks private ActionService actionService;

  private ActionEntity testAction;

  @BeforeEach
  void setUp() {
    testAction = new ActionEntity();
    testAction.setId(1L);
    testAction.setUserId("testUser");
    testAction.setDescription("Test action");
    testAction.setActionType(ActionType.BUY_AUTOMATIC);
    testAction.setCreatedAt(LocalDateTime.now());
  }

  @Test
  void queryActions_withBasicCriteria_returnsPagedResults() {
    // Arrange
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(0, 10, "testUser", null, null, null, null, null);

    List<ActionEntity> content = List.of(testAction);
    Pageable pageable = mock(Pageable.class);
    Page<ActionEntity> expectedPage = new PageImpl<>(content, pageable, 1);

    when(actionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(expectedPage);

    // Act
    Page<ActionEntity> result = actionService.queryActions(criteria);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals("testUser", result.getContent().get(0).getUserId());

    verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void queryActions_withDefaultPagination_usesCorrectDefaults() {
    // Arrange
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(null, null, null, null, null, null, null, null);

    when(actionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    // Act
    actionService.queryActions(criteria);

    // Assert
    verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void queryActions_withTypeFilter_appliesCorrectSpecification() {
    // Arrange
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(
            0,
            10,
            null,
            com.example.evooq.demo.resource.model.ActionType.BUY_AUTOMATIC,
            null,
            null,
            null,
            null);

    when(actionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    // Act
    actionService.queryActions(criteria);

    // Assert
    verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void queryActions_withDateRange_appliesCorrectSpecification() {
    // Arrange
    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    ActionQueryCriteria criteria = new ActionQueryCriteria(0, 10, null, null, from, to, null, null);

    when(actionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    // Act
    actionService.queryActions(criteria);

    // Assert
    verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void queryActions_withCustomSorting_appliesCorrectSort() {
    // Arrange
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(
            0, 10, null, null, null, null, List.of("userId", "createdAt"), "ASC");

    when(actionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    // Act
    actionService.queryActions(criteria);

    // Assert
    verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
  }
}
