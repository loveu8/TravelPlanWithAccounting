package com.travelPlanWithAccounting.service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.dto.travelPlan.TravelCopyRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailSortRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainResponse;
import com.travelPlanWithAccounting.service.entity.TransI18n;
import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.repository.TransI18nRepository;
import com.travelPlanWithAccounting.service.repository.TravelDateRepository;
import com.travelPlanWithAccounting.service.repository.TravelDetailRepository;
import com.travelPlanWithAccounting.service.repository.TravelMainRepository;

import jakarta.transaction.Transactional;

@Service
public class TravelService {

  private final TravelMainRepository travelMainRepository;
  private final TravelDateRepository travelDateRepository;
  private final TravelDetailRepository travelDetailRepository;
  private final MemberRepository memberRepository; // 注入 MemberRepository
  private final TransI18nRepository transI18nRepository;

  @Autowired
  public TravelService(
      TravelMainRepository travelMainRepository,
      TravelDateRepository travelDateRepository,
      TravelDetailRepository travelDetailRepository,
      MemberRepository memberRepository,
      TransI18nRepository transI18nRepository) { // 在建構子中注入
    this.travelMainRepository = travelMainRepository;
    this.travelDateRepository = travelDateRepository;
    this.travelDetailRepository = travelDetailRepository;
    this.memberRepository = memberRepository; // 賦值
    this.transI18nRepository = transI18nRepository;
  }

  /**
   * 根據請求創建新的旅行主行程。 在創建 TravelMain 時，自動生成對應的 TravelDate 列表和 TravelPermissions 項目。 不直接創建
   * TravelDetail。
   *
   * @param request 包含新旅行主行程資訊的 DTO
   * @return 創建成功的 TravelMain 實體
   */
  @Transactional // 確保操作的原子性
  public TravelMainResponse createTravelMain(TravelMainRequest request) {
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

    travelMain = travelMainRepository.save(travelMain);

    // 2. 根據 start_date 和 end_date 建立 travel_date 列表
    List<TravelDate> travelDates = new ArrayList<>();
    LocalDate currentDate = request.getStartDate();
    while (!currentDate.isAfter(request.getEndDate())) {
      TravelDate travelDate = new TravelDate();
      travelDate.setTravelMainId(travelMain.getId()); // 關聯 TravelMain
      travelDate.setTravelDate(currentDate);
      travelDate.setCreatedBy(request.getCreatedBy());
      travelDates.add(travelDate);
      currentDate = currentDate.plus(1, ChronoUnit.DAYS);
    }
    travelDateRepository.saveAll(travelDates); // 批量儲存所有旅行日期

    return new TravelMainResponse(travelMain, travelDates);
  }

  public TravelMain getTravelMainById(UUID id) {
    return travelMainRepository.findById(id).orElse(null);
  }

  public List<TravelMain> getTravelMainsByMemberId(UUID memberId) {
    return travelMainRepository.findByMemberId(memberId);
  }

  /**
   * 更新 TravelMain 記錄，並在日期範圍改變時，相應地更新 travel_date 和 travel_detail。
   *
   * @param request 包含要更新的 TravelMain ID 及新資訊的請求 DTO。
   * @return 更新後的 TravelMain 實體。
   * @throws NoSuchElementException 如果找不到指定 ID 的 TravelMain。
   */
  @Transactional
  public TravelMain updateTravelMain(TravelMainRequest request) {
    // 確保 request 中有 ID
    if (request.getId() == null) {
      throw new IllegalArgumentException("更新 TravelMain 時，ID 不能為空。");
    }

    // 找出現有的 TravelMain
    TravelMain existingTravelMain =
        travelMainRepository
            .findById(request.getId())
            .orElseThrow(
                () -> new NoSuchElementException("找不到 ID 為 " + request.getId() + " 的行程主表。"));

    // 記錄舊的日期範圍
    LocalDate oldStartDate = existingTravelMain.getStartDate();
    LocalDate oldEndDate = existingTravelMain.getEndDate();

    // 更新 TravelMain 的屬性
    existingTravelMain.setIsPrivate(request.getIsPrivate());
    existingTravelMain.setStartDate(request.getStartDate());
    existingTravelMain.setEndDate(request.getEndDate());
    existingTravelMain.setTitle(request.getTitle());
    existingTravelMain.setNotes(request.getNotes());
    existingTravelMain.setVisitPlace(request.getVisitPlace());
    existingTravelMain.setUpdatedBy(request.getCreatedBy()); // 假設 request.createdBy 是更新人

    // 保存更新後的 TravelMain
    TravelMain updatedTravelMain = travelMainRepository.save(existingTravelMain);

    // 處理 travel_date 的增減 (當日期範圍改變時)
    // 只有當開始日期或結束日期有變動時才執行此邏輯
    if (!oldStartDate.equals(request.getStartDate()) || !oldEndDate.equals(request.getEndDate())) {
      handleTravelDatesUpdate(
          request.getId(),
          oldStartDate,
          oldEndDate,
          request.getStartDate(),
          request.getEndDate(),
          request.getCreatedBy());
    }

    return updatedTravelMain;
  }

  @Transactional
  public TravelDate addTravelDate(UUID travelMainId, LocalDate baseDate, UUID createdBy) {
    LocalDate nextDay = baseDate.plusDays(1);
    TravelDate travelDate = new TravelDate();
    // UUID id will be automatically generated by @PrePersist or @GeneratedValue
    travelDate.setTravelMainId(travelMainId);
    travelDate.setTravelDate(nextDay);
    travelDate.setCreatedBy(createdBy);
    // createdAt and updatedAt are handled by the database with DEFAULT CURRENT_TIMESTAMP
    TravelDate savedTravelDate = travelDateRepository.save(travelDate);

    TravelMain travelMain =
        travelMainRepository
            .findById(travelMainId)
            .orElseThrow(
                () -> new NoSuchElementException("找不到行程主表 ID 為 " + travelMainId + " 的記錄。"));

    if (null != travelMain.getEndDate()
        && savedTravelDate.getTravelDate().isAfter(travelMain.getEndDate())) {
      travelMain.setEndDate(savedTravelDate.getTravelDate());
      travelMain.setUpdatedBy(createdBy);
      travelMainRepository.save(travelMain);
    }
    return travelDateRepository.save(travelDate);
  }

  @Transactional
  public void deleteTravelDate(UUID travelDateId) {
    Optional<TravelDate> travelDateOptional = travelDateRepository.findById(travelDateId);
    if (travelDateOptional.isEmpty()) {
      throw new NoSuchElementException("找不到 ID 為 " + travelDateId + " 的行程日期，無法刪除。");
    }
    TravelDate travelDateToDelete = travelDateOptional.get();

    UUID travelMainId = travelDateToDelete.getTravelMainId();
    List<TravelDate> remainingTravelDatesBeforeDeletion =
        travelDateRepository.findByTravelMainId(travelMainId);

    // 防呆：檢查是否為最後一個 travel_date，如果是，則不允許刪除
    if (remainingTravelDatesBeforeDeletion.size() == 1
        && remainingTravelDatesBeforeDeletion.get(0).getId().equals(travelDateId)) {
      throw new IllegalStateException("無法刪除最後一個行程日期，行程主表至少需要保留一天。");
    }

    // 1. First, delete all associated TravelDetail records for this travelDateId
    travelDetailRepository.deleteByTravelDateId(travelDateToDelete.getId());

    // 2. Then, delete the TravelDate record itself
    travelDateRepository.deleteById(travelDateToDelete.getId());

    // 3. Update TravelMain's start_date and end_date based on remaining travel_dates
    TravelMain travelMain =
        travelMainRepository
            .findById(travelMainId)
            .orElseThrow(
                () -> new NoSuchElementException("找不到行程主表 ID 為 " + travelMainId + " 的記錄。"));

    List<TravelDate> remainingTravelDatesAfterDeletion =
        travelDateRepository.findByTravelMainId(travelMainId);

    // 嚴格連續：重新排序並更新所有剩餘 TravelDate 的日期
    // 找到剩餘日期中的最早日期作為新的起始點
    LocalDate newStartDate =
        remainingTravelDatesAfterDeletion.stream()
            .map(TravelDate::getTravelDate)
            .min(Comparator.naturalOrder())
            .orElseThrow(() -> new IllegalStateException("剩餘行程日期不應為空，但無法找到最小日期。")); // 這行現在理論上不會被觸發

    // 根據日期對剩餘的 TravelDate 實體進行排序
    Collections.sort(
        remainingTravelDatesAfterDeletion, Comparator.comparing(TravelDate::getTravelDate));

    LocalDate currentExpectedDate = newStartDate;
    for (TravelDate td : remainingTravelDatesAfterDeletion) {
      // 只有當當前 TravelDate 的日期與期望日期不同時才更新
      if (!td.getTravelDate().equals(currentExpectedDate)) {
        td.setTravelDate(currentExpectedDate);
        travelDateRepository.save(td); // 保存更新後的 TravelDate
      }
      currentExpectedDate = currentExpectedDate.plusDays(1); // 移到下一天
    }

    // 更新 TravelMain 的 start_date 和 end_date
    travelMain.setStartDate(newStartDate);
    // 重新計算 end_date，因為日期可能已經被推移
    travelMain.setEndDate(newStartDate.plusDays(remainingTravelDatesAfterDeletion.size() - 1));
    travelMainRepository.save(travelMain); // 保存更新後的 TravelMain
  }

  public TravelDate getTravelDateById(UUID id) {
    return travelDateRepository.findById(id).orElse(null);
  }

  // You might want to retrieve all travel dates for a specific travel main
  public List<TravelDate> getTravelDatesByTravelMainId(UUID travelMainId) {
    return travelDateRepository.findByTravelMainId(travelMainId);
  }

  /**
   * 處理行程日期範圍變化時的 travel_date 增減及相關 travel_detail 的級聯刪除。
   *
   * @param travelMainId 所屬的 TravelMain ID。
   * @param oldStartDate 舊的開始日期。
   * @param oldEndDate 舊的結束日期。
   * @param newStartDate 新的開始日期。
   * @param newEndDate 新的結束日期。
   * @param updatedBy 更新人 ID。
   */
  private void handleTravelDatesUpdate(
      UUID travelMainId,
      LocalDate oldStartDate,
      LocalDate oldEndDate,
      LocalDate newStartDate,
      LocalDate newEndDate,
      UUID updatedBy) {
    // 獲取所有現有的 travel_date 記錄
    List<TravelDate> existingTravelDates = travelDateRepository.findByTravelMainId(travelMainId);
    Set<LocalDate> existingDatesSet =
        existingTravelDates.stream().map(TravelDate::getTravelDate).collect(Collectors.toSet());
    // 將現有日期映射到其ID，以便於刪除時找到對應的ID
    java.util.Map<LocalDate, UUID> existingDateIdMap =
        existingTravelDates.stream()
            .collect(Collectors.toMap(TravelDate::getTravelDate, TravelDate::getId));

    // 生成新的日期範圍的集合
    Set<LocalDate> newDatesSet = new java.util.HashSet<>();
    LocalDate tempDate = newStartDate;
    while (!tempDate.isAfter(newEndDate)) {
      newDatesSet.add(tempDate);
      tempDate = tempDate.plusDays(1);
    }

    // 找出需要刪除的日期 (在現有記錄中但不在新範圍內的日期)
    // 並將這些日期的 TravelDate ID 傳遞給 deleteTravelDate
    existingDatesSet.stream()
        .filter(date -> !newDatesSet.contains(date))
        .forEach(
            dateToDelete -> {
              UUID idToDelete = existingDateIdMap.get(dateToDelete);
              if (idToDelete != null) {
                // 透過 TravelDateService 刪除 travel_date，它會級聯刪除旗下的 travel_detail
                deleteTravelDate(idToDelete);
              }
            });

    // 找出需要新增的日期 (在新範圍內但不在現有記錄中的日期)
    List<TravelDate> datesToAdd = new ArrayList<>();
    newDatesSet.stream()
        .filter(date -> !existingDatesSet.contains(date))
        .forEach(
            dateToAdd -> {
              TravelDate newTravelDate = new TravelDate();
              newTravelDate.setTravelMainId(travelMainId);
              newTravelDate.setTravelDate(dateToAdd);
              newTravelDate.setCreatedBy(updatedBy); // 使用更新人 ID
              datesToAdd.add(newTravelDate);
            });

    // 執行新增操作
    if (!datesToAdd.isEmpty()) {
      travelDateRepository.saveAll(datesToAdd);
    }
  }

  @Transactional
  public TravelDetail createTravelDetail(TravelDetailRequest request) {
    validateTime(request.getStartTime(), request.getEndTime());
    TravelDetail travelDetail = new TravelDetail();
    // UUID id will be automatically generated by @PrePersist or @GeneratedValue
    travelDetail.setTravelMainId(request.getTravelMainId());
    travelDetail.setTravelDateId(request.getTravelDateId());
    travelDetail.setPoiId(request.getPoiId());
    int nextSort =
        Optional.ofNullable(
                travelDetailRepository.findMaxSortByTravelDateId(request.getTravelDateId()))
            .orElse(0)
            + 1;
    travelDetail.setSort(nextSort);
    travelDetail.setStartTime(request.getStartTime());
    travelDetail.setEndTime(request.getEndTime());
    // TODO extend_id
    travelDetail.setNotes(request.getNotes());
    travelDetail.setCreatedBy(request.getCreatedBy());
    // createdAt and updatedAt are handled by the database with DEFAULT CURRENT_TIMESTAMP

    TravelDetail saved = travelDetailRepository.save(travelDetail);
    checkTimeConflict(saved.getTravelDateId());
    return travelDetailRepository.findById(saved.getId()).orElse(saved);
  }

  public TravelDetail getTravelDetailById(UUID id) {
    return travelDetailRepository.findById(id).orElse(null);
  }

  public List<TravelDetail> getTravelDetailsByTravelDateId(UUID travelDateId) {
    return travelDetailRepository.findByTravelDateId(travelDateId);
  }

  @Transactional
  public void deleteTravelDetailById(UUID id) {
    TravelDetail detail =
        travelDetailRepository
            .findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("找不到 ID 為 " + id + " 的行程詳情，無法刪除。"));
    transI18nRepository.deleteByStartDetailIdOrEndDetailId(id, id);
    travelDetailRepository.deleteById(id);
    resortTravelDetails(detail.getTravelDateId());
  }

  @Transactional
  public void reorderTravelDetails(List<TravelDetailSortRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return;
    }
    List<UUID> ids = requests.stream().map(TravelDetailSortRequest::getId).collect(Collectors.toList());
    List<TravelDetail> details = travelDetailRepository.findAllById(ids);
    if (details.size() != requests.size()) {
      throw new NoSuchElementException("部分行程詳情不存在");
    }
    UUID travelDateId = details.get(0).getTravelDateId();
    for (TravelDetail d : details) {
      if (!d.getTravelDateId().equals(travelDateId)) {
        throw new IllegalArgumentException("所有行程詳情必須屬於同一天");
      }
    }
    requests.sort(Comparator.comparing(TravelDetailSortRequest::getSort));
    int index = 1;
    for (TravelDetailSortRequest req : requests) {
      TravelDetail detail = details.stream().filter(d -> d.getId().equals(req.getId())).findFirst().get();
      detail.setSort(index++);
      travelDetailRepository.save(detail);
    }
  }

  private void resortTravelDetails(UUID travelDateId) {
    List<TravelDetail> details = travelDetailRepository.findByTravelDateIdOrderBySort(travelDateId);
    int index = 1;
    for (TravelDetail detail : details) {
      if (!detail.getSort().equals(index)) {
        detail.setSort(index);
        travelDetailRepository.save(detail);
      }
      index++;
    }
  }

  /**
   * 更新 TravelDetail 記錄。
   *
   * @param request 包含要更新的 TravelDetail ID 及新資訊的請求 DTO。
   * @return 更新後的 TravelDetail 實體。
   * @throws NoSuchElementException 如果找不到指定 ID 的 TravelDetail。
   */
  @Transactional
  public TravelDetail updateTravelDetail(TravelDetailRequest request) {
    // 確保 request 中有 ID
    if (request.getId() == null) {
      throw new IllegalArgumentException("更新 TravelDetail 時，ID 不能為空。");
    }

    // 找出現有的 TravelDetail
    TravelDetail existingTravelDetail =
        travelDetailRepository
            .findById(request.getId())
            .orElseThrow(
                () -> new NoSuchElementException("找不到 ID 為 " + request.getId() + " 的行程詳情。"));

    validateTime(request.getStartTime(), request.getEndTime());
    // 更新 TravelDetail 的屬性
    existingTravelDetail.setStartTime(request.getStartTime());
    existingTravelDetail.setEndTime(request.getEndTime());
    // TODO extend_id
    if (request.getPoiId() != null && !request.getPoiId().equals(existingTravelDetail.getPoiId())) {
      existingTravelDetail.setPoiId(request.getPoiId());
      transI18nRepository.deleteByStartDetailIdOrEndDetailId(existingTravelDetail.getId(), existingTravelDetail.getId());
    }
    
    if (request.getSort() != null && !request.getSort().equals(existingTravelDetail.getSort())) {
      existingTravelDetail.setSort(request.getSort());
      resortTravelDetails(existingTravelDetail.getTravelDateId());
    }
    existingTravelDetail.setUpdatedBy(request.getCreatedBy()); // 假設 request.createdBy 是更新人

    TravelDetail saved = travelDetailRepository.save(existingTravelDetail);
    checkTimeConflict(saved.getTravelDateId());
    return travelDetailRepository.findById(saved.getId()).orElse(saved);
  }

  private void validateTime(LocalTime start, LocalTime end) {
    if (start == null || end == null || !start.isBefore(end)) {
      throw new IllegalArgumentException("開始時間必須早於結束時間");
    }
  }

  private void checkTimeConflict(UUID travelDateId) {
    List<TravelDetail> details = travelDetailRepository.findByTravelDateId(travelDateId);
    if (details == null || details.isEmpty()) {
      return;
    }
    details.sort(Comparator.comparing(TravelDetail::getStartTime));
    LocalTime lastEnd = null;
    for (TravelDetail detail : details) {
      boolean conflict = lastEnd != null && detail.getStartTime().isBefore(lastEnd);
      detail.setTimeConflict(conflict);
      if (lastEnd == null || detail.getEndTime().isAfter(lastEnd)) {
        lastEnd = detail.getEndTime();
      }
    }
    travelDetailRepository.saveAll(details);
  }

  @Transactional
  public TravelMainResponse copyTravelPlan(TravelCopyRequest request) {
    if (request.getId() == null || request.getMemberId() == null) {
      throw new IllegalArgumentException("行程主表ID與會員ID不能為空。");
    }

    if (!memberRepository.existsById(request.getMemberId())) {
      throw new NoSuchElementException("找不到ID為 " + request.getMemberId() + " 的會員。");
    }

    TravelMain sourceTravelMain =
        travelMainRepository
            .findById(request.getId())
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "找不到ID為 " + request.getId() + " 的行程主表。"));

    TravelMain newTravelMain = new TravelMain();
    newTravelMain.setMemberId(request.getMemberId());
    newTravelMain.setIsPrivate(sourceTravelMain.getIsPrivate());
    newTravelMain.setStartDate(sourceTravelMain.getStartDate());
    newTravelMain.setEndDate(sourceTravelMain.getEndDate());
    newTravelMain.setTitle(sourceTravelMain.getTitle());
    newTravelMain.setNotes(sourceTravelMain.getNotes());
    newTravelMain.setVisitPlace(sourceTravelMain.getVisitPlace());
    newTravelMain.setCreatedBy(request.getCreatedBy());
    newTravelMain = travelMainRepository.save(newTravelMain);

    List<TravelDate> sourceDates = travelDateRepository.findByTravelMainId(sourceTravelMain.getId());
    Map<UUID, UUID> dateIdMap = new HashMap<>();
    List<TravelDate> newDates = new ArrayList<>();
    for (TravelDate date : sourceDates) {
      TravelDate newDate = new TravelDate();
      newDate.setTravelMainId(newTravelMain.getId());
      newDate.setTravelDate(date.getTravelDate());
      newDate.setCreatedBy(request.getCreatedBy());
      newDate = travelDateRepository.save(newDate);
      dateIdMap.put(date.getId(), newDate.getId());
      newDates.add(newDate);
    }

    Map<UUID, UUID> detailIdMap = new HashMap<>();
    for (TravelDate date : sourceDates) {
      List<TravelDetail> details = travelDetailRepository.findByTravelDateId(date.getId());
      UUID newDateId = dateIdMap.get(date.getId());
      for (TravelDetail detail : details) {
        TravelDetail newDetail = new TravelDetail();
        newDetail.setTravelMainId(newTravelMain.getId());
        newDetail.setTravelDateId(newDateId);
        newDetail.setPoiId(detail.getPoiId());
        newDetail.setSort(detail.getSort());
        newDetail.setStartTime(detail.getStartTime());
        newDetail.setEndTime(detail.getEndTime());
        newDetail.setNotes(detail.getNotes());
        newDetail.setCreatedBy(request.getCreatedBy());
        newDetail = travelDetailRepository.save(newDetail);
        detailIdMap.put(detail.getId(), newDetail.getId());
      }
    }

    if (!detailIdMap.isEmpty()) {
      List<UUID> detailIds = new ArrayList<>(detailIdMap.keySet());
      List<TransI18n> transList =
          transI18nRepository.findByStartDetailIdInOrEndDetailIdIn(detailIds, detailIds);
      for (TransI18n trans : transList) {
        TransI18n newTrans = new TransI18n();
        newTrans.setLangType(trans.getLangType());
        newTrans.setStartDetailId(detailIdMap.get(trans.getStartDetailId()));
        newTrans.setEndDetailId(detailIdMap.get(trans.getEndDetailId()));
        newTrans.setInfosRaw(trans.getInfosRaw());
        newTrans.setTransType(trans.getTransType());
        newTrans.setTransTime(trans.getTransTime());
        newTrans.setSummary(trans.getSummary());
        newTrans.setNotes(trans.getNotes());
        newTrans.setCreatedBy(request.getCreatedBy());
        transI18nRepository.save(newTrans);
      }
    }

    return new TravelMainResponse(newTravelMain, newDates);
  }
}
