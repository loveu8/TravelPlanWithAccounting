package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;
import java.io.Serializable;
import org.springframework.data.domain.Page;

public class PageBodyWriteHandler implements BodyWriteHandler {

  @Override
  public boolean supports(Object body) {
    return body instanceof Page<?>;
  }

  @Override
  public Object handle(Object body, ObjectMapper objectMapper) {
    Page<?> page = (Page<?>) body;
    if (!page.getContent().isEmpty() && !(page.getContent().get(0) instanceof Serializable)) {
      throw new IllegalArgumentException("Page content must be Serializable");
    }
    @SuppressWarnings("unchecked")
    Page<? extends Serializable> safePage = (Page<? extends Serializable>) page;
    return RestResponseUtils.successWithPage(safePage);
  }
}
