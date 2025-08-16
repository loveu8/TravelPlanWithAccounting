package com.travelPlanWithAccounting.service.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TransI18nRequest;
import com.travelPlanWithAccounting.service.entity.TransI18n;
import com.travelPlanWithAccounting.service.service.TransI18nService;

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
    public ResponseEntity<?> create(@RequestBody TransI18nRequest request) {
        try {
            TransI18n trans = transI18nService.createOrUpdate(request);
            return new ResponseEntity<>(trans, HttpStatus.CREATED);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("建立交通紀錄時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody UuidRequest request) {
        try {
            transI18nService.delete(request.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("刪除交通紀錄時發生未知錯誤: " + e.getMessage());
        }
    }
}
