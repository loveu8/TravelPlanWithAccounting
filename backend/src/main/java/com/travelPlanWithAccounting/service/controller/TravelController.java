package com.travelPlanWithAccounting.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Travel", description = "旅遊行程")
@RequestMapping("/api/travel")
public class TravelController {

}
