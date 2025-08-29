package com.travelPlanWithAccounting.service.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelCopyRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDateRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailSortRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainResponse;
import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.service.TravelService;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Travel", description = "旅遊行程")
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping("/createTravelMain")
    public ResponseEntity<?> createTravelMain(@RequestBody TravelMainRequest request) {
        try {
            TravelMainResponse newTravelMainResponse = travelService.createTravelMain(request);
            return new ResponseEntity<>(newTravelMainResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建行程主表時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/updateTravelMain") // 更新行程主表 (POST 請求，ID在 Request Body 中)
    public ResponseEntity<?> updateTravelMain(@RequestBody TravelMainRequest request) {
        try {
            TravelMain updatedTravelMain = travelService.updateTravelMain(request);
            return ResponseEntity.ok(updatedTravelMain);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新行程主表時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/getTravelMain") // 根據ID獲取行程主表 (POST 請求，ID在 Request Body 中)
    public ResponseEntity<?> getTravelMain(@RequestBody UuidRequest request) {
        try {  
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程主表ID不能為空。");
            }  
            TravelMain travelMain = travelService.getTravelMainById(request.getId());
            if (travelMain != null) {
                return ResponseEntity.ok(travelMain);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到ID為 " + request.getId() + " 的行程主表。");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("獲取行程主表時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/getTravelMainsByMemberId")
    public ResponseEntity<?> getTravelMainsByMemberId(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("會員ID不能為空。");
            }
            List<TravelMain> travelMains = travelService.getTravelMainsByMemberId(request.getId());
            if (!travelMains.isEmpty()) {
                return ResponseEntity.ok(travelMains);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到會員ID為 " + request.getId() + " 的行程主表。");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("根據會員ID獲取行程主表時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/copyTravelPlan")
    public ResponseEntity<?> copyTravelPlan(@RequestBody TravelCopyRequest request) {
        try {
            TravelMainResponse response = travelService.copyTravelPlan(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("複製行程時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/addTravelDate")
    public ResponseEntity<?> addTravelDate(@RequestBody TravelDateRequest request) {
        try {    
            if (request.getTravelMainId() == null || request.getTravelDate() == null) {
                return ResponseEntity.badRequest().body("行程主表ID和基準日期不能為空。");
            }
            TravelDate nextTravelDate = travelService.addTravelDate(
                    request.getTravelMainId(),
                    request.getTravelDate(),
                    request.getCreatedBy()
            );
            return new ResponseEntity<>(nextTravelDate, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 例如找不到相關的 TravelMain
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建隔一天行程日期時發生未知錯誤: " + e.getMessage());
        }    
    }

    @PostMapping("/deleteTravelDate") // 刪除行程日期 (POST 請求，ID在 Request Body 中)
    public ResponseEntity<?> deleteTravelDate(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程日期ID不能為空。");
            }
            travelService.deleteTravelDate(request.getId());
            return ResponseEntity.ok().body("日期已成功刪除。");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除行程日期時發生未知錯誤: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    @PostMapping("/getTravelDate")
    public ResponseEntity<?> getTravelDate(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程日期ID不能為空。");
            }
            TravelDate travelDate = travelService.getTravelDateById(request.getId());
            if (travelDate != null) {
                return ResponseEntity.ok(travelDate);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到ID為 " + request.getId() + " 的行程日期。");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("獲取行程日期時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/getTravelDatesByTravelMainId")
    public ResponseEntity<?> getTravelDatesByTravelMainId(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程主表ID不能為空。");
            }    
            List<TravelDate> travelDates = travelService.getTravelDatesByTravelMainId(request.getId());
            if (!travelDates.isEmpty()) {
                return ResponseEntity.ok(travelDates);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到行程主表ID為 " + request.getId() + " 的行程日期。");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("根據行程主表ID獲取行程日期時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/createTravelDetail")
    public ResponseEntity<?> createTravelDetail(@RequestBody TravelDetailRequest request) {
        try {
            if (request.getTravelMainId() == null || request.getTravelDateId() == null) {
                return ResponseEntity.badRequest().body("行程主表ID、行程日期ID不能為空。");
            }
            TravelDetail newTravelDetail = travelService.createTravelDetail(request);
            return new ResponseEntity<>(newTravelDetail, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建行程詳情時發生未知錯誤: " + e.getMessage());
        }    
    }

    @PostMapping("/updateTravelDetail") // 更新行程詳情 (POST 請求，ID在 Request Body 中)
    public ResponseEntity<?> updateTravelDetail(@RequestBody TravelDetailRequest request) {
        try {
            TravelDetail updatedTravelDetail = travelService.updateTravelDetail(request);
            return ResponseEntity.ok(updatedTravelDetail);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // ID 為空時的處理
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新行程詳情時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/reorderTravelDetail")
    public ResponseEntity<?> reorderTravelDetail(@RequestBody List<TravelDetailSortRequest> requests) {
        try {
        travelService.reorderTravelDetails(requests);
        return ResponseEntity.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("重新排序行程詳情時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/getTravelDetail")
    public ResponseEntity<?> getTravelDetail(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程詳情ID不能為空。");
            }    
            TravelDetail travelDetail = travelService.getTravelDetailById(request.getId());
            if (travelDetail != null) {
                return ResponseEntity.ok(travelDetail);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到ID為 " + request.getId() + " 的行程詳情。");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("獲取行程詳情時發生未知錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/getTravelDetailsByTravelDateId")
    public ResponseEntity<?> getTravelDetailsByTravelDateId(@RequestBody UuidRequest request) {
        List<TravelDetail> travelDetails = travelService.getTravelDetailsByTravelDateId(request.getId());
        if (!travelDetails.isEmpty()) {
            return ResponseEntity.ok(travelDetails);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到ID為 " + request.getId() + " 的行程詳情。");
    }

    @PostMapping("/checkTimeConflict")
    public RestResponse<Object, Object> checkTimeConflict(@RequestBody TravelDetailRequest request) {
        return RestResponseUtils.success(travelService.checkTimeConflict(request));
    }

    @PostMapping("/deleteTravelDetail") // 刪除行程詳情 (POST 請求，ID在 Request Body 中)
    public ResponseEntity<?> deleteTravelDetailById(@RequestBody UuidRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body("行程詳情ID不能為空。");
            }
            travelService.deleteTravelDetailById(request.getId());
            return ResponseEntity.ok().body("明細已成功刪除。");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 如果找不到該 ID 的 TravelDetail
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除行程詳情時發生未知錯誤: " + e.getMessage());
        }
    }
}
