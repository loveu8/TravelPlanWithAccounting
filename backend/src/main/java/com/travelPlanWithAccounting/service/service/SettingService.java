package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import com.travelPlanWithAccounting.service.dto.setting.SettingResponse;
import com.travelPlanWithAccounting.service.repository.SettingRepository;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import com.travelPlanWithAccounting.service.validator.Validator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

  @Autowired private SettingRepository settingRepository;
  @Autowired private Validator coommonValidator;
  @Autowired private LangTypeMapper langTypeMapper;

  /**
   * 根據類別查詢設定，並依據語系回傳對應資料。
   *
   * @param category 設定類別
   * @param lang Accept-Language 標頭
   * @return List<SettingResponse>
   */
  @Cacheable(
      value = CacheConstants.SETTING_CACHE,
      key = "#category + '::' + (#lang == null || #lang.isEmpty() ? 'zh-TW' : #lang)")
  public List<SettingResponse> getSettingsByCategory(String category, String lang) {
    coommonValidator.validateCategory(category);

    String language = (lang == null || lang.isEmpty()) ? "zh-TW" : lang;
    coommonValidator.validate(language);
    String langCode = langTypeMapper.toCode(language);

    return settingRepository.findByCategoryWithLang(category, langCode, "001");
  }
}

