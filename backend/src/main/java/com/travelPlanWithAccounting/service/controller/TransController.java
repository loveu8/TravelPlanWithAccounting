package com.travelPlanWithAccounting.service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TransI18nRequest;
import com.travelPlanWithAccounting.service.entity.TransI18n;
import com.travelPlanWithAccounting.service.security.AccessTokenRequired;
import com.travelPlanWithAccounting.service.service.TransI18nService;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Trans", description = "景點間交通紀錄")
@RequestMapping("/api/trans")
public class TransController {

    private final TransI18nService transI18nService;
    private final AuthContext authContext;

    public TransController(TransI18nService transI18nService, AuthContext authContext) {
        this.transI18nService = transI18nService;
        this.authContext = authContext;
    }

    @PostMapping("/create")
    @AccessTokenRequired
    public RestResponse<Object, Object> create(@RequestBody TransI18nRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        TransI18n trans = transI18nService.createOrUpdate(request);
        return RestResponseUtils.success(trans);
    }

    @PostMapping("/delete")
    @AccessTokenRequired
    public RestResponse<Object, Object> delete(@RequestBody UuidRequest request) {
        transI18nService.delete(request.getId());
        return RestResponseUtils.success(null);
    }
}
