package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test", description = "測試用")
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello World";
  }

  @GetMapping("/exception/{messageCode}")
  public String throwException(@PathVariable SystemMessageCode messageCode) {
    throw new ApiException(SystemMessageCode.SERVICE_UNAVAILABLE);
  }
}
