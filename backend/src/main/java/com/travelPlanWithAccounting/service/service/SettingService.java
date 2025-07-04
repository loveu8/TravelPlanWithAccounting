package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.setting.SettingResponse;
import com.travelPlanWithAccounting.service.entity.Setting;
import com.travelPlanWithAccounting.service.repository.SettingRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

  @Autowired private SettingRepository settingRepository;

  /**
   * 根據類別查詢設定
   *
   * @param category 設定類別
   * @return List<SettingResponse>
   */
  public List<SettingResponse> getSettingsByCategory(String category) {
    // 暫時限制只能查詢 LANG_TYPE
    if (!"LANG_TYPE".equals(category)) {
      throw new RuntimeException("目前只支援查詢 LANG_TYPE 類別的設定");
    }

    List<Setting> settings = settingRepository.findByCategory(category);
    return settings.stream().map(this::convertToResponse).collect(Collectors.toList());
  }

  /**
   * 查詢所有語言類型設定
   *
   * @return List<SettingResponse>
   */
  public List<SettingResponse> getAllLanguageTypes() {
    List<Setting> settings = settingRepository.findAllLanguageTypes();
    return settings.stream().map(this::convertToResponse).collect(Collectors.toList());
  }

  /**
   * 將 Setting 實體轉換為 SettingResponse DTO
   *
   * @param setting Setting 實體
   * @return SettingResponse
   */
  private SettingResponse convertToResponse(Setting setting) {
    SettingResponse response = new SettingResponse();
    response.setCategory(setting.getCategory());
    response.setName(setting.getName());
    response.setCodeName(setting.getCodeName());
    response.setCodeDesc(setting.getCodeDesc());
    return response;
  }
}
