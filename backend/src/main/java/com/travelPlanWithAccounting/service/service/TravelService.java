package com.travelPlanWithAccounting.service.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.dto.travelPlan.CreateTravelMainRequest;
import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.entity.TravelPermissions;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.repository.TravelDateRepository;
import com.travelPlanWithAccounting.service.repository.TravelDetailRepository;
import com.travelPlanWithAccounting.service.repository.TravelMainRepository;
import com.travelPlanWithAccounting.service.repository.TravelPermissionsRepository;

import jakarta.transaction.Transactional;

@Service
public class TravelService {

    private final TravelMainRepository travelMainRepository;
    private final TravelDateRepository travelDateRepository;
    private final TravelDetailRepository travelDetailRepository;
    private final TravelPermissionsRepository travelPermissionsRepository;
    private final MemberRepository memberRepository; // 注入 MemberRepository

    @Autowired
    public TravelService(TravelMainRepository travelMainRepository,
                         TravelDateRepository travelDateRepository,
                         TravelDetailRepository travelDetailRepository,
                         TravelPermissionsRepository travelPermissionsRepository,
                         MemberRepository memberRepository) { // 在建構子中注入
        this.travelMainRepository = travelMainRepository;
        this.travelDateRepository = travelDateRepository;
        this.travelDetailRepository = travelDetailRepository;
        this.travelPermissionsRepository = travelPermissionsRepository;
        this.memberRepository = memberRepository; // 賦值
    }
    
    /**
     * 根據請求創建新的旅行主行程。
     * 在創建 TravelMain 時，自動生成對應的 TravelDate 列表和 TravelPermissions 項目。
     * 不直接創建 TravelDetail。
     *
     * @param request 包含新旅行主行程資訊的 DTO
     * @return 創建成功的 TravelMain 實體
     */
    @Transactional // 確保操作的原子性
    public TravelMain createTravelMain(CreateTravelMainRequest request) {
        // 1. 創建 TravelMain 實體並儲存
        TravelMain travelMain = new TravelMain();
        travelMain.setMemberId(request.getMemberId());
        travelMain.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false);
        travelMain.setStartDate(request.getStartDate());
        travelMain.setEndDate(request.getEndDate());
        travelMain.setTitle(request.getTitle());
        travelMain.setNotes(request.getNotes());
        travelMain.setVisitPlace(request.getVisitPlace());
        travelMain.setCreatedBy(request.getCreatedBy());
        // createdAt 和 updatedAt 由 JPA @CreationTimestamp 和 @UpdateTimestamp 自動處理
        // id 由 JPA @GeneratedValue 自動處理

        TravelMain savedTravelMain = travelMainRepository.save(travelMain);

        // 2. 根據 start_date 和 end_date 建立 travel_date 列表
        List<TravelDate> travelDates = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            TravelDate travelDate = new TravelDate();
            travelDate.setTravelMainId(savedTravelMain.getId()); // 關聯 TravelMain
            travelDate.setTravelDate(currentDate);
            travelDate.setCreatedBy(request.getCreatedBy());
            travelDates.add(travelDate);
            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
        }
        travelDateRepository.saveAll(travelDates); // 批量儲存所有旅行日期

        // 3. 建立 travel_permission (為行程的創建者建立一個 "OWN" 權限)
        TravelPermissions travelPermission = new TravelPermissions();
        travelPermission.setTravelId(savedTravelMain.getId()); // 關聯 TravelMain
        travelPermission.setMemberId(request.getMemberId()); // 行程創建者
        travelPermission.setEmail("creator@example.com"); // 預設或從 member 資訊獲取
        travelPermission.setType("OWN"); // OWNER 權限
        travelPermission.setPermissions(true); // 擁有權限
        travelPermission.setCreatedBy(request.getCreatedBy());
        travelPermissionsRepository.save(travelPermission);

        return savedTravelMain;
    }

    /**
     * 根據ID獲取單一 TravelMain 實體。
     * @param id TravelMain 的 UUID
     * @return Optional<TravelMain>
     */
    public Optional<TravelMain> getTravelMainById(UUID id) {
        return travelMainRepository.findById(id);
    }

    /**
     * 獲取所有 TravelMain 實體。
     * @return List<TravelMain>
     */
    public List<TravelMain> getAllTravelMains() {
        return travelMainRepository.findAll();
    }

/*
    @Transactional
    public TravelMain addDayPlanDetails(UUID travelMainId, TravelDayPlanCreateDTO dayPlanDTO, UUID createdBy) {
        // 尋找 TravelMain
        TravelMain travelMain = travelMainRepository.findById(travelMainId)
                .orElseThrow(() -> new IllegalArgumentException("未找到 ID 為: " + travelMainId + " 的 TravelMain。"));

        LocalDate planDate = dayPlanDTO.getTravelDate();

        // 尋找與此 TravelMain 相關的對應 TravelDate 實體
        TravelDate correspondingTravelDate = travelMain.getTravelDates().stream()
                .filter(td -> td.getTravelDate().equals(planDate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TravelMain ID: " + travelMainId + " 未找到日期為 " + planDate + " 的 TravelDate。"));

        if (dayPlanDTO.getDetails() != null) {
            for (TravelDetailCreateDTO detailDTO : dayPlanDTO.getDetails()) {
                TravelDetail detail = new TravelDetail();
                detail.setTravelMain(travelMain); // 設定關係
                detail.setTravelDate(correspondingTravelDate); // 設定關係
                detail.setType(detailDTO.getType());
                detail.setStartTime(detailDTO.getStartTime());
                detail.setEndTime(detailDTO.getEndTime());
                detail.setGoogleMapInfo(detailDTO.getGoogleMapInfo());
                detail.setNotes(detailDTO.getNotes());
                detail.setCreatedBy(createdBy); // 使用提供的 createdBy
                detail.setUpdatedBy(createdBy); // 使用提供的 createdBy

                correspondingTravelDate.addTravelDetail(detail); // 添加到日期實體的集合中以管理級聯操作
                travelDetailRepository.save(detail); // 明確儲存
            }
        }
        return travelMain; // 返回更新後的主實體
    }
         */
}
