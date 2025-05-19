package com.travelPlanWithAccounting.service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenApiHeader {
  ACCEPT_LANGUAGE("accept-language", "Accept Language", false),
  ;

  String headerName;
  String description;
  Boolean required;
}
