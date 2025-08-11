package com.example.evooq.demo.services;

import com.example.evooq.demo.db.jpa.UserRepository;
import com.example.evooq.demo.db.model.UserEntity;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserEntity login(String name, String email) {
    return userRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              UserEntity user = new UserEntity();
              user.setName(name);
              user.setEmail(email);
              user.setBalance(0.0);
              return userRepository.save(user);
            });
  }

  @Transactional
  public void updateBalance(Long userId, BigDecimal delta) {
    UserEntity user =
        userRepository
            .findByIdForUpdate(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    user.setBalance(user.getBalance() + delta.doubleValue());
    // Hibernate automatically updates at transaction commit
  }
}
