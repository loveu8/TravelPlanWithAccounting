package com.travelPlanWithAccounting.service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test", description = "測試用")
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello World";
  }
}
