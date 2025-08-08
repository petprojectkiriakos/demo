package com.example.evooq.demo.resource.contoller;

import com.example.evooq.demo.db.model.UserEntity;
import com.example.evooq.demo.resource.model.UserResource;
import com.example.evooq.demo.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  public UserResource login(@RequestParam String name, @RequestParam String email) {
    UserEntity user = userService.login(name, email);
    return new UserResource(user.getId(), user.getName(), user.getEmail(), user.getBalance());
  }
}
