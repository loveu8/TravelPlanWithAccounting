package com.travelPlanWithAccounting.service.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.travelPlan.CreateTravelMainRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainResponse;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.service.TravelService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Travel", description = "旅遊行程")
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    @Autowired
    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping("/createMain")
    public ResponseEntity<TravelMainResponse> createTravelMain(@RequestBody CreateTravelMainRequest request) {
        TravelMain savedTravelMain = travelService.createTravelMain(request);
        System.err.println("savedTravelMain =" + savedTravelMain.toString());
        // 將 entity 轉換為 response DTO
        TravelMainResponse response = new TravelMainResponse();
        response.setId(savedTravelMain.getId());
        response.setMemberId(savedTravelMain.getMemberId());
        response.setIsPrivate(savedTravelMain.getIsPrivate());
        response.setStartDate(savedTravelMain.getStartDate());
        response.setEndDate(savedTravelMain.getEndDate());
        response.setTitle(savedTravelMain.getTitle());
        response.setNotes(savedTravelMain.getNotes());
        response.setVisitPlace(savedTravelMain.getVisitPlace());
        response.setCreatedAt(savedTravelMain.getCreatedAt());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 根據ID獲取旅行主行程。
     * GET /api/travels/{id}
     * @param id 旅行主行程的 UUID
     * @return 找到的行程資訊及 HTTP 200 OK 狀態碼，如果未找到則返回 HTTP 404 Not Found。
     */
    @GetMapping("/{id}")
    public ResponseEntity<TravelMainResponse> getTravelMainById(@PathVariable UUID id) {
        Optional<TravelMain> travelMain = travelService.getTravelMainById(id);
        return travelMain.map(main -> {
            TravelMainResponse response = new TravelMainResponse();
            response.setId(main.getId());
            response.setMemberId(main.getMemberId());
            response.setIsPrivate(main.getIsPrivate());
            response.setStartDate(main.getStartDate());
            response.setEndDate(main.getEndDate());
            response.setTitle(main.getTitle());
            response.setNotes(main.getNotes());
            response.setVisitPlace(main.getVisitPlace());
            response.setCreatedAt(main.getCreatedAt());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 獲取所有旅行主行程。
     * GET /api/travels
     * @return 所有行程的列表及 HTTP 200 OK 狀態碼。
     */
    @GetMapping
    public ResponseEntity<List<TravelMainResponse>> getAllTravelMains() {
        List<TravelMain> travelMains = travelService.getAllTravelMains();
        List<TravelMainResponse> responses = travelMains.stream()
                .map(main -> {
                    TravelMainResponse response = new TravelMainResponse();
                    response.setId(main.getId());
                    response.setMemberId(main.getMemberId());
                    response.setIsPrivate(main.getIsPrivate());
                    response.setStartDate(main.getStartDate());
                    response.setEndDate(main.getEndDate());
                    response.setTitle(main.getTitle());
                    response.setNotes(main.getNotes());
                    response.setVisitPlace(main.getVisitPlace());
                    response.setCreatedAt(main.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
/* 
    @PostMapping("/{travelMainId}/day-plans")
    public ResponseEntity<TravelMain> addDayPlanDetails(@PathVariable UUID travelMainId,
                                                        @RequestBody TravelDayPlanCreateDTO request) {
        try {
            // 假設 createdBy 來自身份驗證上下文。為簡單起見，這裡使用一個模擬的 UUID
            // 實際應用中，應替換為來自安全上下文的實際用戶 ID
            UUID createdBy = UUID.randomUUID(); 
            TravelMain updatedTravelMain = travelService.addDayPlanDetails(travelMainId, request, createdBy);
            return new ResponseEntity<>(updatedTravelMain, HttpStatus.OK); // 使用 OK 因為是更新操作
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("添加每日行程詳細資訊時發生錯誤。", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
        */
}
