package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * 會員基本資料修改請求。
 */
@Data
public class MemberProfileUpdateRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  @Schema(description = "名字")
  private String givenName;

  @Schema(description = "姓氏")
  private String familyName;

  @Schema(description = "暱稱")
  private String nickName;

  @Schema(description = "生日")
  private LocalDate birthday;

  @Schema(description = "是否訂閱")
  private Boolean subscribe;

  @Schema(description = "語系類型", example = "zh-TW")
  private String langType;
}

