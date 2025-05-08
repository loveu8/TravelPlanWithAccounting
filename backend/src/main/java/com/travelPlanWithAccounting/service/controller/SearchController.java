package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Search", description = "搜尋引擎")
@RequestMapping("/api/search")
public class SearchController {

  @Autowired private SearchService searchService;
}
