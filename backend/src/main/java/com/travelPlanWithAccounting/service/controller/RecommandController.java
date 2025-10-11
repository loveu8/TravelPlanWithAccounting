package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.recommand.LocationRecommand;
import com.travelPlanWithAccounting.service.service.RecommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommands")
@Tag(name = "Recommand", description = "推薦景點")
@RequiredArgsConstructor
public class RecommandController {

  private final RecommandService recommandService;

  @GetMapping("/{country}")
  @Operation(summary = "取得指定國家的推薦景點")
  public List<LocationRecommand> getRecommendations(
      @PathVariable String country, @RequestHeader("Accept-Language") String acceptLanguage) {
    return recommandService.getRecommendations(country, acceptLanguage);
  }
}
