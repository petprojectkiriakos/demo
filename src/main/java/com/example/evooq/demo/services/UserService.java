package com.example.evooq.demo.services;

import com.example.evooq.demo.db.jpa.UserRepository;
import com.example.evooq.demo.db.model.UserEntity;
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
}
