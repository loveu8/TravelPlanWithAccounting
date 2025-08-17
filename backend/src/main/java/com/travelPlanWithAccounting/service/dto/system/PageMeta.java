package com.travelPlanWithAccounting.service.dto.system;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {@code PageMeta} 是用來表示分頁資訊的 DTO。<br>
 * {@code PageMeta} is a DTO representing pagination information.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMeta implements Serializable {
  private static final long serialVersionUID = 1L;

  private int page;
  private int size;
  private int totalPages;
  private long totalElements;
  private boolean hasNext;
  private boolean hasPrev;
}
