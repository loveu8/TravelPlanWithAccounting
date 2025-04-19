package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.entity.TestUser;
import com.travelPlanWithAccounting.service.service.TestUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Test", description = "測試用")
@RequiredArgsConstructor
public class TestUserController {

  private final TestUserService testUserService;

  @GetMapping
  @Operation(summary = "獲取所有用戶")
  public List<TestUser> getAllUsers() {
    return testUserService.getAllUsers();
  }
}
