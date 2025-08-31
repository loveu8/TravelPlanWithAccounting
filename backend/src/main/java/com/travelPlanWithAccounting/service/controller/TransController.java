package com.travelPlanWithAccounting.service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TransI18nRequest;
import com.travelPlanWithAccounting.service.entity.TransI18n;
import com.travelPlanWithAccounting.service.service.TransI18nService;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Trans", description = "景點間交通紀錄")
@RequestMapping("/api/trans")
public class TransController {

    private final TransI18nService transI18nService;

    public TransController(TransI18nService transI18nService) {
        this.transI18nService = transI18nService;
    }

    @PostMapping("/create")
    public RestResponse<Object, Object> create(@RequestBody TransI18nRequest request) {
        TransI18n trans = transI18nService.createOrUpdate(request);
        return RestResponseUtils.success(trans);
    }

    @PostMapping("/delete")
    public RestResponse<Object, Object> delete(@RequestBody UuidRequest request) {
        transI18nService.delete(request.getId());
        return RestResponseUtils.success(null);
    }
}
