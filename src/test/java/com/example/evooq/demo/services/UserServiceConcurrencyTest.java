// src/test/java/com/example/evooq/demo/services/UserServiceConcurrencyTest.java
package com.example.evooq.demo.services;

import static org.junit.jupiter.api.Assertions.*;

import com.example.evooq.demo.db.jpa.UserRepository;
import com.example.evooq.demo.db.model.UserEntity;
import java.math.BigDecimal;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceConcurrencyTest {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  private UserEntity user;

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
    user = new UserEntity();
    user.setName("Test");
    user.setEmail("test@example.com");
    user.setBalance(0.0);
    user = userRepository.save(user);
  }

  @Test
  void concurrentBalanceUpdates_shouldNotLoseUpdates() throws Exception {
    int threads = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch latch = new CountDownLatch(threads);
    BigDecimal delta = BigDecimal.valueOf(10);

    for (int i = 0; i < threads; i++) {
      executor.submit(
          () -> {
            try {
              userService.updateBalance(user.getId(), delta);
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    UserEntity updated = userRepository.findById(user.getId()).orElseThrow();
    assertEquals(threads * delta.doubleValue(), updated.getBalance());
  }
}
