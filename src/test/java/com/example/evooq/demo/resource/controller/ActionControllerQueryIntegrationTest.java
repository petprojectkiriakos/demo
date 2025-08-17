package com.example.evooq.demo.resource.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.evooq.demo.db.jpa.ActionRepository;
import com.example.evooq.demo.db.model.ActionEntity;
import com.example.evooq.demo.db.model.ActionType;
import com.example.evooq.demo.resource.model.ActionQueryCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActionControllerQueryIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ActionRepository actionRepository;

  @Autowired private ObjectMapper objectMapper;

  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass");

  static {
    postgres.start();
  }

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setUp() {
    actionRepository.deleteAll();

    // Create test data
    ActionEntity action1 = new ActionEntity();
    action1.setUserId("user1");
    action1.setDescription("Buy action 1");
    action1.setActionType(ActionType.BUY_AUTOMATIC);
    action1.setCreatedAt(LocalDateTime.now().minusDays(2));
    actionRepository.save(action1);

    ActionEntity action2 = new ActionEntity();
    action2.setUserId("user1");
    action2.setDescription("Sell action 1");
    action2.setActionType(ActionType.SELL_AUTOMATIC);
    action2.setCreatedAt(LocalDateTime.now().minusDays(1));
    actionRepository.save(action2);

    ActionEntity action3 = new ActionEntity();
    action3.setUserId("user2");
    action3.setDescription("Buy action 2");
    action3.setActionType(ActionType.BUY_AUTOMATIC);
    action3.setCreatedAt(LocalDateTime.now());
    actionRepository.save(action3);
  }

  @Test
  void queryActions_withoutFilters_returnsAllActions() throws Exception {
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(0, 10, null, null, null, null, null, null);

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.first").value(true));
  }

  @Test
  void queryActions_withUserFilter_returnsFilteredActions() throws Exception {
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(0, 10, "user1", null, null, null, null, null);

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].userId").value("user1"))
        .andExpect(jsonPath("$.content[1].userId").value("user1"));
  }

  @Test
  void queryActions_withTypeFilter_returnsFilteredActions() throws Exception {
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

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void queryActions_withPagination_returnsCorrectPage() throws Exception {
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(0, 2, null, null, null, null, null, null);

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(false));
  }

  @Test
  void queryActions_withSorting_returnsSortedActions() throws Exception {
    ActionQueryCriteria criteria =
        new ActionQueryCriteria(0, 10, null, null, null, null, List.of("createdAt"), "ASC");

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void queryActions_withDateRange_returnsFilteredActions() throws Exception {
    LocalDateTime from = LocalDateTime.now().minusDays(1).minusHours(1);
    LocalDateTime to = LocalDateTime.now().plusHours(1);

    ActionQueryCriteria criteria = new ActionQueryCriteria(0, 10, null, null, from, to, null, null);

    mockMvc
        .perform(
            post("/actions/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(
            jsonPath("$.totalElements").value(2)); // Should return actions from yesterday and today
  }
}
