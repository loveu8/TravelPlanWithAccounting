package com.travelPlanWithAccounting.service.dto.search.request;

// 定義請求物件
public class SearchRequest {
  private String countryCode;
  private String langType;

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getLangType() {
    return langType;
  }

  public void setLangType(String langType) {
    this.langType = langType;
  }
}
